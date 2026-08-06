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
import com.europay.hub.features.payment.domain.port.PaymentProviderRegistry;
import com.europay.hub.features.payment.domain.port.ProviderResult;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import com.europay.hub.shared.web.PageResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment creation and queries. Creating a payment: validates the order, submits to the chosen
 * provider (Strategy), applies the resulting state transition, and honours the Idempotency-Key.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentProviderRegistry providerRegistry;
    private final IdempotencyStore idempotencyStore;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                          PaymentProviderRegistry providerRegistry, IdempotencyStore idempotencyStore) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.providerRegistry = providerRegistry;
        this.idempotencyStore = idempotencyStore;
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
        switch (result.outcome()) {
            case PENDING -> { /* stays PENDING, awaiting approval */ }
            case AUTHORIZED -> payment.authorize();
            case DECLINED -> payment.fail(result.declineReason());
        }

        Payment saved = paymentRepository.save(payment);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyStore.save(new IdempotencyRecord(merchantId, idempotencyKey, requestHash, saved.id()));
        }
        return PaymentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse get(UUID merchantId, UUID paymentId) {
        return PaymentResponse.from(paymentRepository.findById(paymentId)
                .filter(p -> p.merchantId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId)));
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> list(UUID merchantId, int page, int size) {
        return PageResponse.of(paymentRepository.findByMerchantId(merchantId, page, size)
                .map(PaymentResponse::from));
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
