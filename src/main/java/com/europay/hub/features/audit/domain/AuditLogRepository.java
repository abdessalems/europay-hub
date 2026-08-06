package com.europay.hub.features.audit.domain;

import com.europay.hub.shared.domain.PageResult;
import java.util.UUID;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);

    PageResult<AuditLog> findByMerchantId(UUID merchantId, int page, int size);
}
