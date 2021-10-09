CREATE TABLE IF NOT EXISTS residential_proxy
(
    id uuid PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    port VARCHAR(12),
    created TIMESTAMP NOT NULL
);
