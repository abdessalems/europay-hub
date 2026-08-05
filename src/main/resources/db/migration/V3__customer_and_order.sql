-- =====================================================================
-- V3 — Customer & Order
-- Phase 2: merchant customers and their orders.
-- =====================================================================

CREATE TABLE customer (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID         NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    email       VARCHAR(255) NOT NULL,
    full_name   VARCHAR(200) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_customer_merchant_email UNIQUE (merchant_id, email)
);

CREATE INDEX idx_customer_merchant ON customer (merchant_id);

CREATE TABLE orders (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id  UUID         NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    customer_id  UUID         NOT NULL REFERENCES customer (id),
    reference    VARCHAR(50)  NOT NULL,
    amount_minor BIGINT       NOT NULL,
    currency     VARCHAR(3)   NOT NULL DEFAULT 'EUR',
    status       VARCHAR(20)  NOT NULL DEFAULT 'CREATED',
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_order_merchant_reference UNIQUE (merchant_id, reference),
    CONSTRAINT chk_order_amount_positive CHECK (amount_minor > 0)
);

CREATE INDEX idx_order_merchant ON orders (merchant_id);
CREATE INDEX idx_order_customer ON orders (customer_id);
