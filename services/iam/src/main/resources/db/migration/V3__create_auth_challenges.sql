CREATE TABLE auth_challenges (
    challenge_id UUID NOT NULL,
    user_id UUID NOT NULL,
    nonce VARCHAR(160) NOT NULL,
    purpose VARCHAR(40) NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP(6) WITH TIME ZONE NULL,

    CONSTRAINT pk_auth_challenges PRIMARY KEY (challenge_id),

    CONSTRAINT fk_auth_challenges_user
        FOREIGN KEY (user_id)
        REFERENCES user_accounts(user_id)
);

CREATE INDEX idx_challenges_user_id
ON auth_challenges(user_id);

CREATE INDEX idx_challenges_expires_at
ON auth_challenges(expires_at);

CREATE INDEX idx_challenges_used
ON auth_challenges(used);

CREATE INDEX idx_challenges_user_used
ON auth_challenges(user_id, used);

CREATE INDEX idx_challenges_user_purpose_used
ON auth_challenges(user_id, purpose, used);