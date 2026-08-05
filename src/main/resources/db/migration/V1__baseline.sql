-- =====================================================================
-- V1 — Baseline
-- Phase 0: establish the schema baseline and required PostgreSQL extensions.
-- Domain tables (merchant, api_key, customer, orders, payment, refund,
-- webhook_event, webhook_delivery, audit_log, idempotency_key, outbox_event)
-- are introduced in later phases, each in its own versioned migration.
-- =====================================================================

-- gen_random_uuid() for UUID primary keys
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
