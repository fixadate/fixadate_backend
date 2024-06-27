CREATE TABLE member
(
    id                    VARCHAR(255) NOT NULL,
    oauth_id              VARCHAR(255) NOT NULL,
    oauth_platform        ENUM ('APPLE', 'GOOGLE', 'KAKAO'),
    name                  VARCHAR(255) NOT NULL,
    profile_img           VARCHAR(255),
    nickname              VARCHAR(255) NOT NULL,
    birth                 VARCHAR(255),
    gender                VARCHAR(255),
    profession            VARCHAR(255),
    email                 VARCHAR(255),
    role                  VARCHAR(255),
    push_key_id           BIGINT,
    google_credentials_id BIGINT,
    signature_color       VARCHAR(255) NOT NULL,
    create_date           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY oauth_id (oauth_id)
);


CREATE TABLE tag
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
    if_all_day         TINYINT(1),
    starts_when        DATETIME,
    ends_when          DATETIME,
    calendar_id        VARCHAR(255) UNIQUE,
    etag               VARCHAR(255),
    reminders          TINYINT(1),
    member_id          VARCHAR(255),
    tag_id             BIGINT,
    create_date        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);