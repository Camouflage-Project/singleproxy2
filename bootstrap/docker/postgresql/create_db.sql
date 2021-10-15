CREATE TABLE IF NOT EXISTS residential_proxy
(
    id uuid PRIMARY KEY,
    key VARCHAR(50) NOT NULL,
    port INT NOT NULL,
    platform VARCHAR(50) NOT NULL,
    created TIMESTAMP NOT NULL
);
