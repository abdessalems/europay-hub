package com.europay.hub.features.audit.application;

import com.europay.hub.features.audit.application.dto.AuditLogResponse;
import com.europay.hub.features.audit.domain.AuditLog;
import com.europay.hub.features.audit.domain.AuditLogRepository;
import com.europay.hub.shared.web.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void record(UUID merchantId, String actor, String action, String entityType,
                       UUID entityId, Map<String, Object> metadata) {
        repository.save(AuditLog.record(merchantId, actor, action, entityType, entityId, toJson(metadata)));
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> list(UUID merchantId, int page, int size) {
        return PageResponse.of(repository.findByMerchantId(merchantId, page, size).map(AuditLogResponse::from));
    }

    private String toJson(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            return null;
        }
    }
}
