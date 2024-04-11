CREATE TABLE google_credentials
(
    channel_id         VARCHAR(255) NOT NULL,
    access_token       VARCHAR(255),
    resource_id        VARCHAR(255),
    resource_uri       VARCHAR(255),
    resource_state     VARCHAR(255),
    channel_expiration BIGINT,
    channel_token      VARCHAR(255),
    user_id            VARCHAR(255),
    PRIMARY KEY (channel_id)
);