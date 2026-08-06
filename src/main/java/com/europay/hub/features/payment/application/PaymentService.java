package com.europay.hub.features.payment.application;

import com.europay.hub.features.order.domain.Order;
import com.europay.hub.features.order.domain.OrderRepository;
import com.europay.hub.features.order.domain.OrderStatus;
import com.europay.hub.features.payment.application.dto.CreatePaymentRequest;
import com.europay.hub.features.payment.application.dto.PaymentResponse;
import com.europay.hub.features.payment.application.port.IdempotencyRecord;
import com.europay.hub.features.payment.application.port.IdempotencyStore;
import com.europay.hub.features.payment.domain.Payment;
import com.europay.hub.features.payment.domain.PaymentRepository;
import com.europay.hub.features.payment.domain.PaymentStatus;
import com.europay.hub.features.payment.domain.Refund;
import com.europay.hub.features.payment.domain.RefundRepository;
import com.europay.hub.features.payment.domain.port.PaymentProviderRegistry;
import com.europay.hub.features.payment.domain.port.ProviderResult;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import com.europay.hub.shared.web.PageResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment use cases: creation (with provider Strategy + idempotency) and the lifecycle
 * transitions (approve, refund, cancel, retry, expire). When a payment succeeds, the order
 * is marked paid.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final PaymentProviderRegistry providerRegistry;
    private final IdempotencyStore idempotencyStore;
    private final long expiryMinutes;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                          RefundRepository refundRepository, PaymentProviderRegistry providerRegistry,
                          IdempotencyStore idempotencyStore,
                          @Value("${europay.payment.expiry-minutes}") long expiryMinutes) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.providerRegistry = providerRegistry;
        this.idempotencyStore = idempotencyStore;
        this.expiryMinutes = expiryMinutes;
    }

    @Transactional
    public PaymentResponse create(UUID merchantId, CreatePaymentRequest request, String idempotencyKey) {
        String requestHash = hash(request.orderId() + ":" + request.paymentMethod());

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<PaymentResponse> replay = replayIfPresent(merchantId, idempotencyKey, requestHash);
            if (replay.isPresent()) {
                return replay.get();
            }
        }

        Order order = orderRepository.findById(request.orderId())
                .filter(o -> o.merchantId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));
        if (order.status() != OrderStatus.CREATED) {
            throw new BusinessRuleViolationException(
                    "ORDER_NOT_PAYABLE", "Only a CREATED order can be paid (was " + order.status() + ")");
        }

        Payment payment = Payment.create(merchantId, order.id(), request.paymentMethod(), order.amount());
        ProviderResult result = providerRegistry.forMethod(request.paymentMethod()).submit(payment);
        payment.submit(result.providerReference());
        applyOutcome(payment, result);

        Payment saved = paymentRepository.save(payment);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyStore.save(new IdempotencyRecord(merchantId, idempotencyKey, requestHash, saved.id()));
        }
        return PaymentResponse.from(saved);
    }

    /** Customer/merchant confirms the payment → SUCCESS, and the order is marked paid. */
    @Transactional
    public PaymentResponse approve(UUID merchantId, UUID paymentId) {
        Payment payment = requireOwned(merchantId, paymentId);
        payment.markSucceeded();
        Payment saved = paymentRepository.save(payment);
        markOrderPaid(saved.orderId());
        return PaymentResponse.from(saved);
    }

    @Transactional
    public PaymentResponse cancel(UUID merchantId, UUID paymentId) {
        Payment payment = requireOwned(merchantId, paymentId);
        payment.cancel();
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse refund(UUID merchantId, UUID paymentId, String reason) {
        Payment payment = requireOwned(merchantId, paymentId);
        if (payment.status() != PaymentStatus.SUCCESS && payment.status() != PaymentStatus.SETTLED) {
            throw new BusinessRuleViolationException(
                    "REFUND_NOT_ALLOWED", "Refund is only allowed for SUCCESS/SETTLED payments (was " + payment.status() + ")");
        }
        payment.refund();
        refundRepository.save(Refund.create(payment.id(), merchantId, payment.amount(), reason));
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse retry(UUID merchantId, UUID paymentId) {
        Payment payment = requireOwned(merchantId, paymentId);
        if (payment.status() != PaymentStatus.FAILED) {
            throw new BusinessRuleViolationException(
                    "RETRY_NOT_ALLOWED", "Only a FAILED payment can be retried (was " + payment.status() + ")");
        }
        ProviderResult result = providerRegistry.forMethod(payment.method()).submit(payment);
        payment.retry(result.providerReference());
        applyOutcome(payment, result);
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    /** Expire PENDING payments older than the configured window. Driven by a scheduler. */
    @Transactional
    public int expireStalePayments() {
        Instant cutoff = Instant.now().minus(expiryMinutes, ChronoUnit.MINUTES);
        List<Payment> expirable = paymentRepository.findExpirable(cutoff);
        for (Payment payment : expirable) {
            payment.expire();
            paymentRepository.save(payment);
        }
        return expirable.size();
    }

    @Transactional(readOnly = true)
    public PaymentResponse get(UUID merchantId, UUID paymentId) {
        return PaymentResponse.from(requireOwned(merchantId, paymentId));
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> list(UUID merchantId, int page, int size) {
        return PageResponse.of(paymentRepository.findByMerchantId(merchantId, page, size)
                .map(PaymentResponse::from));
    }

    private Payment requireOwned(UUID merchantId, UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .filter(p -> p.merchantId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
    }

    private void markOrderPaid(UUID orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.status() == OrderStatus.CREATED) {
                order.markPaid();
                orderRepository.save(order);
            }
        });
    }

    private static void applyOutcome(Payment payment, ProviderResult result) {
        switch (result.outcome()) {
            case PENDING -> { /* awaiting approval */ }
            case AUTHORIZED -> payment.authorize();
            case DECLINED -> payment.fail(result.declineReason());
        }
    }

    private Optional<PaymentResponse> replayIfPresent(UUID merchantId, String key, String requestHash) {
        return idempotencyStore.find(merchantId, key).map(record -> {
            if (!record.requestHash().equals(requestHash)) {
                throw new BusinessRuleViolationException(
                        "IDEMPOTENCY_KEY_REUSED", "Idempotency-Key was reused with a different request");
            }
            return PaymentResponse.from(paymentRepository.findById(record.paymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", record.paymentId())));
        });
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
