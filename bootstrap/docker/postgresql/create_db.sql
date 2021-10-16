CREATE TABLE IF NOT EXISTS residential_proxy
(
    id uuid PRIMARY KEY,
    key VARCHAR(50) NOT NULL,
    port INT NOT NULL,
    platform VARCHAR(50) NOT NULL,
    registered BOOLEAN NOT NULL DEFAULT false,
    ip_address VARCHAR(50),
    last_heartbeat TIMESTAMP,
    created TIMESTAMP NOT NULL
);
