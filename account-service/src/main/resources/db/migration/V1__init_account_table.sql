CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    owner_name VARCHAR(255) NOT NULL,
    balance NUMERIC(38, 2) NOT NULL,
    version BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);