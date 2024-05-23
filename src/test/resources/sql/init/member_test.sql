CREATE TABLE push_key
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255),
    push_key  VARCHAR(255) UNIQUE
);

CREATE TABLE google_credentials
(
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    channel_id         VARCHAR(255),
    access_token       VARCHAR(255),
    refresh_token      VARCHAR(255),
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
    id                    VARCHAR(255) NOT NULL,
    oauth_id              VARCHAR(255) NOT NULL,
    oauth_platform        ENUM ('APPLE', 'GOOGLE', 'KAKAO'),
    name                  VARCHAR(255) NOT NULL,
    profile_img           VARCHAR(255),
    nickname              VARCHAR(255) NOT NULL,
    birth                 INT,
    gender                VARCHAR(255),
    profession            VARCHAR(255),
    email                 VARCHAR(255),
    role                  VARCHAR(255),
    signature_color       VARCHAR(255) NOT NULL,
    push_key_id           BIGINT,
    google_credentials_id BIGINT,
    create_date           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY oauth_id (oauth_id),
    FOREIGN KEY (google_credentials_id) REFERENCES google_credentials (id),
    FOREIGN KEY (push_key_id) REFERENCES push_key (id)
);



CREATE TABLE color_type
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    color      VARCHAR(255),
    name       VARCHAR(255),
    is_default BOOLEAN,
    member_id  VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);


CREATE TABLE adate
(
    id                 BIGINT   NOT NULL AUTO_INCREMENT,
    title              VARCHAR(255),
    notes              VARCHAR(500),
    location           VARCHAR(300),
    alert_when         DATETIME,
    repeat_freq        DATETIME,
    color              VARCHAR(255),
    adate_name         VARCHAR(255),
    if_all_day         TINYINT(1),
    starts_when        DATETIME,
    ends_when          DATETIME,
    calendar_id        VARCHAR(255) UNIQUE,
    etag               VARCHAR(255),
    reminders          TINYINT(1),
    version            DATETIME,
    status             VARCHAR(255),
    member_id          VARCHAR(255),
    color_type_id      BIGINT,
    create_date        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (color_type_id) REFERENCES color_type (id)
);



