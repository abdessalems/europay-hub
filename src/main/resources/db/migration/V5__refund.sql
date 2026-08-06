-- =====================================================================
-- V5 — Refund
-- Phase 4: refunds of successful payments.
-- =====================================================================

CREATE TABLE refund (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id   UUID         NOT NULL REFERENCES payment (id) ON DELETE CASCADE,
    merchant_id  UUID         NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    amount_minor BIGINT       NOT NULL,
    currency     VARCHAR(3)   NOT NULL DEFAULT 'EUR',
    reason       VARCHAR(255),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_refund_amount_positive CHECK (amount_minor > 0)
);

CREATE INDEX idx_refund_payment ON refund (payment_id);
