package com.europay.hub.features.audit.infrastructure.persistence;

import com.europay.hub.features.audit.domain.AuditLog;
import com.europay.hub.features.audit.domain.AuditLogRepository;
import com.europay.hub.shared.domain.PageResult;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpa;

    public AuditLogRepositoryAdapter(AuditLogJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        return toDomain(jpa.save(toEntity(auditLog)));
    }

    @Override
    public PageResult<AuditLog> findByMerchantId(UUID merchantId, int page, int size) {
        Page<AuditLogEntity> result = jpa.findByMerchantId(merchantId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return new PageResult<>(
                result.getContent().stream().map(AuditLogRepositoryAdapter::toDomain).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private static AuditLogEntity toEntity(AuditLog a) {
        AuditLogEntity e = new AuditLogEntity();
        e.setId(a.id());
        e.setMerchantId(a.merchantId());
        e.setActor(a.actor());
        e.setAction(a.action());
        e.setEntityType(a.entityType());
        e.setEntityId(a.entityId());
        e.setMetadata(a.metadata());
        e.setCreatedAt(a.createdAt());
        return e;
    }

    private static AuditLog toDomain(AuditLogEntity e) {
        return new AuditLog(e.getId(), e.getMerchantId(), e.getActor(), e.getAction(),
                e.getEntityType(), e.getEntityId(), e.getMetadata(), e.getCreatedAt());
    }
}
