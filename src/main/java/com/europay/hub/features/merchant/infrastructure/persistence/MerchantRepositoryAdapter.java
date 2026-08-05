package com.europay.hub.features.merchant.infrastructure.persistence;

import com.europay.hub.features.merchant.domain.Merchant;
import com.europay.hub.features.merchant.domain.MerchantRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class MerchantRepositoryAdapter implements MerchantRepository {

    private final MerchantJpaRepository jpa;

    public MerchantRepositoryAdapter(MerchantJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Merchant save(Merchant merchant) {
        return toDomain(jpa.save(toEntity(merchant)));
    }

    @Override
    public Optional<Merchant> findById(UUID id) {
        return jpa.findById(id).map(MerchantRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email.trim().toLowerCase());
    }

    private static MerchantEntity toEntity(Merchant merchant) {
        MerchantEntity e = new MerchantEntity();
        e.setId(merchant.id());
        e.setLegalName(merchant.legalName());
        e.setEmail(merchant.email());
        e.setStatus(merchant.status());
        e.setCreatedAt(merchant.createdAt());
        e.setUpdatedAt(Instant.now());
        return e;
    }

    private static Merchant toDomain(MerchantEntity e) {
        return new Merchant(e.getId(), e.getLegalName(), e.getEmail(), e.getStatus(), e.getCreatedAt());
    }
}
