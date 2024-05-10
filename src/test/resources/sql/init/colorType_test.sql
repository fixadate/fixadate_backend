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
    push_key_id         BIGINT,
    google_credentials_id         BIGINT,
    signature_color    VARCHAR(255) NOT NULL,
    create_date        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY oauth_id (oauth_id)
);


CREATE TABLE color_type
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    color     VARCHAR(255),
    name      VARCHAR(255),
    member_id VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);
