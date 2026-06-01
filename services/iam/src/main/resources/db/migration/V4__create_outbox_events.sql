CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,

    event_type VARCHAR(120) NOT NULL,
    event_version VARCHAR(20) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,

    topic_name VARCHAR(180) NOT NULL,
    message_key VARCHAR(180) NOT NULL,

    payload TEXT NOT NULL,

    status VARCHAR(30) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,

    next_retry_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    sent_at TIMESTAMPTZ NULL,

    last_error TEXT NULL,

    kafka_partition INTEGER NULL,
    kafka_offset BIGINT NULL,

    publishing_started_at TIMESTAMPTZ NULL,

    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_outbox_status_retry
ON outbox_events (status, next_retry_at);

CREATE INDEX idx_outbox_publishing_started
ON outbox_events (status, publishing_started_at);