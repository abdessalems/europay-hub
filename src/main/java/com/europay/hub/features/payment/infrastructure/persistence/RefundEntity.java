package com.europay.hub.features.payment.infrastructure.persistence;

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
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
public class RefundEntity {

    @Id
    private UUID id;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "amount_minor")
    private long amountMinor;

    private String currency;

    private String reason;

    @Column(name = "created_at")
    private Instant createdAt;
}
