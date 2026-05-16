CREATE TABLE token_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    encrypted_value VARCHAR(512) NOT NULL,
    key_version INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_token_records_encrypted_value_key_version
    ON token_records (encrypted_value, key_version);
