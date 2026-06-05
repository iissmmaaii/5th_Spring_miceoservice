CREATE TABLE user_verification_status (
    user_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    source_file_id UUID NULL,
    rejection_reason VARCHAR(500) NULL,
    reviewed_at TIMESTAMP NULL,
    reviewed_by_admin_user_id UUID NULL,
    last_event_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    PRIMARY KEY (user_id)
);

CREATE TABLE processed_kafka_events (
    event_id UUID NOT NULL,
    topic VARCHAR(255) NOT NULL,
    kafka_key VARCHAR(255) NULL,
    processed_at TIMESTAMP NOT NULL,

    PRIMARY KEY (event_id)
);

CREATE TABLE account_transaction_history (
    id UUID NOT NULL,
    client_request_id VARCHAR(120) NOT NULL,
    sender_user_id UUID NOT NULL,
    receiver_user_id UUID NOT NULL,
    amount_minor BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(30) NOT NULL,
    fabric_tx_id VARCHAR(255) NULL,
    failure_reason VARCHAR(1000) NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_transaction_client_request_id UNIQUE (client_request_id)
);

CREATE INDEX idx_transaction_sender_user_id
ON account_transaction_history(sender_user_id);

CREATE INDEX idx_transaction_receiver_user_id
ON account_transaction_history(receiver_user_id);

CREATE INDEX idx_transaction_created_at
ON account_transaction_history(created_at);