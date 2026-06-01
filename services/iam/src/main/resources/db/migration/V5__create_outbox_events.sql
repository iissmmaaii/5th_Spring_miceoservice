CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,

    event_id VARCHAR(100) NOT NULL UNIQUE,

    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,

    event_type VARCHAR(150) NOT NULL,
    event_version VARCHAR(20) NOT NULL,

    topic VARCHAR(255) NOT NULL,
    kafka_key VARCHAR(255) NOT NULL,

    payload TEXT NOT NULL,

    status VARCHAR(30) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,

    next_attempt_at TIMESTAMPTZ NOT NULL,
    locked_at TIMESTAMPTZ NULL,
    published_at TIMESTAMPTZ NULL,

    last_error TEXT NULL,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX idx_outbox_event_id
ON outbox_events(event_id);

CREATE INDEX idx_outbox_status_next_attempt_created
ON outbox_events(status, next_attempt_at, created_at);

CREATE INDEX idx_outbox_publishing_locked_at
ON outbox_events(status, locked_at);