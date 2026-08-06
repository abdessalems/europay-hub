package com.europay.hub.features.webhook.infrastructure.persistence;

import com.europay.hub.features.webhook.domain.WebhookStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webhook_event")
@Getter
@Setter
@NoArgsConstructor
public class WebhookEventEntity {

    @Id
    private UUID id;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(columnDefinition = "text")
    private String payload;

    @Enumerated(EnumType.STRING)
    private WebhookStatus status;

    private int attempts;

    @Column(name = "max_attempts")
    private int maxAttempts;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Column(name = "last_status_code")
    private Integer lastStatusCode;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at")
    private Instant createdAt;
}
