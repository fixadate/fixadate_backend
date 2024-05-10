CREATE TABLE google_credentials
(
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    channel_id         VARCHAR(255),
    access_token       VARCHAR(255),
    resource_id        VARCHAR(255),
    resource_uri       VARCHAR(255),
    resource_state     VARCHAR(255),
    channel_expiration BIGINT,
    channel_token      VARCHAR(255),
    user_id            VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE member
(
    id                 VARCHAR(255) NOT NULL,
    oauth_id           VARCHAR(255) NOT NULL,
    oauth_platform     ENUM ('APPLE', 'GOOGLE', 'KAKAO'),
    name               VARCHAR(255) NOT NULL,
    profile_img        VARCHAR(255),
    nickname           VARCHAR(255) NOT NULL,
    birth              INT,
    gender             VARCHAR(255),
    profession         VARCHAR(255),
    email              VARCHAR(255),
    role               VARCHAR(255),
    signature_color    VARCHAR(255) NOT NULL,
    push_key_id        BIGINT,
    google_credentials_id BIGINT,
    create_date        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY oauth_id (oauth_id),
    FOREIGN KEY (google_credentials_id) REFERENCES google_credentials (id)
);
