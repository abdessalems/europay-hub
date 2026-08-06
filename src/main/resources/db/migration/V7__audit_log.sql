-- =====================================================================
-- V7 — Audit log
-- Phase 6: an append-only record of every important action.
-- =====================================================================

CREATE TABLE audit_log (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID,
    actor       VARCHAR(100) NOT NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id   UUID,
    metadata    TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_merchant ON audit_log (merchant_id, created_at DESC);
