package com.europay.hub.features.audit.application;

import com.europay.hub.features.payment.domain.event.PaymentDomainEvent;
import com.europay.hub.shared.event.AuditEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Turns events into audit records. Runs synchronously inside the publisher's transaction, so an
 * action and its audit entry commit together. Payment lifecycle is audited from the payment
 * event; everything else publishes a generic {@link AuditEvent}.
 */
@Component
public class AuditEventListener {

    private final AuditService auditService;

    public AuditEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void on(AuditEvent event) {
        auditService.record(event.merchantId(), event.actor(), event.action(),
                event.entityType(), event.entityId(), event.metadata());
    }

    @EventListener
    public void on(PaymentDomainEvent event) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("status", event.status());
        metadata.put("method", event.method());
        metadata.put("amountMinor", event.amountMinor());
        String action = "PAYMENT_" + event.status();
        auditService.record(event.merchantId(), "system", action, "PAYMENT", event.paymentId(), metadata);
    }
}
