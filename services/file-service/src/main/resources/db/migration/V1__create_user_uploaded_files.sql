CREATE TABLE user_uploaded_files (
    id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,

    file_url VARCHAR(1000) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    storage_path VARCHAR(1000) NOT NULL,
    content_type VARCHAR(120) NOT NULL,
    file_size_bytes BIGINT NOT NULL,

    status VARCHAR(30) NOT NULL,
    rejection_reason VARCHAR(500) NULL,

    uploaded_at TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP NULL,
    reviewed_by_admin_user_id CHAR(36) NULL,

    PRIMARY KEY (id),
    INDEX idx_user_uploaded_files_user_id (user_id),
    INDEX idx_user_uploaded_files_status (status),
    INDEX idx_user_uploaded_files_uploaded_at (uploaded_at)
);