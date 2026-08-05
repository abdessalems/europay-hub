package com.europay.hub.features.merchant.infrastructure.persistence;

import com.europay.hub.features.merchant.domain.MerchantStatus;
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
@Table(name = "merchant")
@Getter
@Setter
@NoArgsConstructor
public class MerchantEntity {

    @Id
    private UUID id;

    @Column(name = "legal_name")
    private String legalName;

    private String email;

    @Enumerated(EnumType.STRING)
    private MerchantStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
