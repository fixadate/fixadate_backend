CREATE TABLE push_key
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255),
    push_key  VARCHAR(255) UNIQUE
);

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
    signature_color       VARCHAR(255) NOT NULL,
    push_key_id           BIGINT,
    google_credentials_id BIGINT,
    create_date           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY oauth_id (oauth_id),
    FOREIGN KEY (push_key_id) REFERENCES push_key (id)
);

