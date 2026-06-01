CREATE TABLE user_accounts (
    user_id UUID NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_user_accounts PRIMARY KEY (user_id),
    CONSTRAINT uk_user_accounts_email UNIQUE (email)
);

CREATE INDEX idx_user_accounts_email
ON user_accounts(email);

CREATE INDEX idx_user_accounts_status
ON user_accounts(status);

CREATE INDEX idx_user_accounts_created_at
ON user_accounts(created_at);