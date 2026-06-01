CREATE TABLE user_public_keys (
    key_id UUID NOT NULL,
    user_id UUID NOT NULL,
    device_id VARCHAR(140) NOT NULL,
    public_key_pem TEXT NOT NULL,
    fingerprint VARCHAR(128) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP(6) WITH TIME ZONE NULL,

    CONSTRAINT pk_user_public_keys PRIMARY KEY (key_id),

    CONSTRAINT uk_public_keys_fingerprint
        UNIQUE (fingerprint),

    CONSTRAINT fk_public_keys_user
        FOREIGN KEY (user_id)
        REFERENCES user_accounts(user_id)
);

CREATE INDEX idx_public_keys_user_id
ON user_public_keys(user_id);

CREATE INDEX idx_public_keys_device_id
ON user_public_keys(device_id);

CREATE INDEX idx_public_keys_status
ON user_public_keys(status);

CREATE INDEX idx_public_keys_user_status
ON user_public_keys(user_id, status);

CREATE INDEX idx_public_keys_user_device_status
ON user_public_keys(user_id, device_id, status);