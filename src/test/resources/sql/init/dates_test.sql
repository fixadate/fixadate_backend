CREATE TABLE teams (
       team_id BIGINT NOT NULL AUTO_INCREMENT,
       name VARCHAR(255) NOT NULL,
       description VARCHAR(255),
       updated_by VARCHAR(255),
       create_date           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
       last_modified_date    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       PRIMARY KEY (team_id)
);
