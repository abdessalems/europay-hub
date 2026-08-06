package com.europay.hub.features.webhook.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webhook_delivery")
@Getter
@Setter
@NoArgsConstructor
public class WebhookDeliveryEntity {

    @Id
    private UUID id;

    @Column(name = "webhook_event_id")
    private UUID webhookEventId;

    private int attempt;

    @Column(name = "status_code")
    private Integer statusCode;

    private boolean success;

    private String error;

    @Column(name = "created_at")
    private Instant createdAt;
}
