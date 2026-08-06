package com.europay.hub.features.payment.infrastructure.scheduler;

import com.europay.hub.features.payment.application.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Periodically expires PENDING payments past their window (BR-023: expired can't be approved). */
@Component
public class PaymentExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(PaymentExpiryScheduler.class);

    private final PaymentService paymentService;

    public PaymentExpiryScheduler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Scheduled(fixedDelayString = "${europay.payment.expiry-scan-ms:60000}")
    public void expireStalePayments() {
        int expired = paymentService.expireStalePayments();
        if (expired > 0) {
            log.info("Expired {} stale payment(s)", expired);
        }
    }
}
