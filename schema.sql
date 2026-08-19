-- Run once against the `scratch` database after `docker compose up -d`.
CREATE TABLE links (
    id         uuid         PRIMARY KEY,
    url        text         NOT NULL,
    code       varchar(16)  UNIQUE NOT NULL,
    created_at timestamptz  NOT NULL
);
