-- =====================================================================
-- V6 — Webhooks
-- Phase 5: merchant webhook endpoint, transactional outbox, delivery logs.
-- =====================================================================

CREATE TABLE webhook_endpoint (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID         NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    secret      VARCHAR(100) NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_webhook_endpoint_merchant UNIQUE (merchant_id)
);

-- Transactional outbox: one row per event to deliver.
CREATE TABLE webhook_event (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id      UUID         NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    event_type       VARCHAR(50)  NOT NULL,
    payment_id       UUID,
    payload          TEXT         NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    attempts         INT          NOT NULL DEFAULT 0,
    max_attempts     INT          NOT NULL DEFAULT 3,
    next_attempt_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_status_code INT,
    last_error       VARCHAR(500),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_webhook_event_merchant ON webhook_event (merchant_id);
CREATE INDEX idx_webhook_event_dispatch ON webhook_event (status, next_attempt_at);

-- One row per delivery attempt (audit trail).
CREATE TABLE webhook_delivery (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    webhook_event_id  UUID        NOT NULL REFERENCES webhook_event (id) ON DELETE CASCADE,
    attempt           INT         NOT NULL,
    status_code       INT,
    success           BOOLEAN     NOT NULL,
    error             VARCHAR(500),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_webhook_delivery_event ON webhook_delivery (webhook_event_id);
