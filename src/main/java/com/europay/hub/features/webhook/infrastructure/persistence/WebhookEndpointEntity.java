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
@Table(name = "webhook_endpoint")
@Getter
@Setter
@NoArgsConstructor
public class WebhookEndpointEntity {

    @Id
    private UUID id;

    @Column(name = "merchant_id")
    private UUID merchantId;

    private String url;
    private String secret;
    private boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
