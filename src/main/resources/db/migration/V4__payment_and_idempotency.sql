-- =====================================================================
-- V4 — Payment & Idempotency
-- Phase 3: payments (with a state machine) and idempotency-key dedup.
-- =====================================================================

CREATE TABLE payment (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id        UUID         NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    order_id           UUID         NOT NULL REFERENCES orders (id),
    payment_method     VARCHAR(20)  NOT NULL,
    amount_minor       BIGINT       NOT NULL,
    currency           VARCHAR(3)   NOT NULL DEFAULT 'EUR',
    status             VARCHAR(20)  NOT NULL DEFAULT 'CREATED',
    provider_reference VARCHAR(100),
    failure_reason     VARCHAR(255),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_payment_amount_positive CHECK (amount_minor > 0)
);

CREATE INDEX idx_payment_merchant ON payment (merchant_id);
CREATE INDEX idx_payment_order    ON payment (order_id);

CREATE TABLE idempotency_key (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id     UUID         NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    idempotency_key VARCHAR(200) NOT NULL,
    request_hash    VARCHAR(100) NOT NULL,
    payment_id      UUID         NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_idempotency_merchant_key UNIQUE (merchant_id, idempotency_key)
);
