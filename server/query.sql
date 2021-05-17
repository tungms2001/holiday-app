CREATE DATABASE holidayapp;

USE holidayapp;

CREATE TABLE user(
     username VARCHAR(100),
     email VARCHAR(100),
     phone CHAR(10),
     password VARCHAR(100),
     fullname VARCHAR(200) CHARSET utf8,
     avatar VARCHAR(500),
     role VARCHAR(10),
     created_at DATETIME,
     updated_at DATETIME,
     PRIMARY KEY (username)
);

CREATE TABLE password_reset(
     id INT AUTO_INCREMENT,
     email VARCHAR(100),
     token CHAR(6),
     PRIMARY KEY (id)
);

CREATE TABLE tour(
     id INT AUTO_INCREMENT,
     tour_name VARCHAR(200) CHARSET utf8,
     type VARCHAR(200) CHARSET utf8,
     departure VARCHAR(200) CHARSET utf8,
     destination VARCHAR(200) CHARSET utf8,
     creator VARCHAR(100),
     status VARCHAR(50),
     during VARCHAR(30) CHARSET utf8,
     image VARCHAR(500),
     note VARCHAR(200) CHARSET utf8,
     created_at DATETIME,
     updated_at DATETIME,
     PRIMARY KEY (id)
);

CREATE TABLE member(
     tour_id INT,
     user VARCHAR(100),
     PRIMARY KEY (tour_id, user)
);

CREATE TABLE tour_comment(
     id INT AUTO_INCREMENT,
     user VARCHAR(100),
     tour_id INT,
     content VARCHAR(500) CHARSET utf8,
     rate INT,
     active BOOL,
     created_at DATETIME,
     deleted_at DATETIME,
     PRIMARY KEY (id)
);

CREATE TABLE notification(
     id INT AUTO_INCREMENT,
     tour_id INT,
     sender VARCHAR(100),
     receiver VARCHAR(100),
     type VARCHAR(20),
     status VARCHAR(20),
     PRIMARY KEY (id)
);

ALTER TABLE user
    ADD CONSTRAINT CHK_ROLE
    CHECK (role IN ('admin', 'general'));

ALTER TABLE user
    ALTER role SET DEFAULT 'general';

ALTER TABLE user
    ADD CONSTRAINT UNI_EMAIL
    UNIQUE (email);

ALTER TABLE password_reset
    ADD CONSTRAINT FK_EMAIL_UNI
    FOREIGN KEY (email) REFERENCES user(email);

ALTER TABLE tour
    ADD CONSTRAINT CHK_TOUR_STATUS
    CHECK (status IN ('Open', 'Closed', 'In-progress', 'Prepared', 'Done', 'Delayed'));

ALTER TABLE notification
    ADD CONSTRAINT CK_NOTIFICATION_TYPE
    CHECK (status IN ('invite', 'apply', 'comment', 'rate'));

ALTER TABLE notification
    ADD CONSTRAINT CHK_NOTIFICATION_STATUS
    CHECK (status IN ('Pending', 'Accept', 'Decline'));

ALTER TABLE tour
    ADD CONSTRAINT FK_USER_AS_TOUR_CREATOR
    FOREIGN KEY (creator) REFERENCES user(username);

ALTER TABLE member
    ADD CONSTRAINT FK_TOUR_MEMBER
    FOREIGN KEY (tour_id) REFERENCES tour(id);

ALTER TABLE member
    ADD CONSTRAINT FK_USER_AS_MEMBER
    FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE tour_comment
    ADD CONSTRAINT FK_USER_AS_TOUR_COMMENTER
    FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE tour_comment
    ADD CONSTRAINT FK_COMMENT_IN_TOUR
    FOREIGN KEY (tour_id) REFERENCES tour(id);

ALTER TABLE notification
    ADD CONSTRAINT FK_USER_AS_SENDER
    FOREIGN KEY (sender) REFERENCES user(username);

ALTER TABLE notification
    ADD CONSTRAINT FK_USER_AS_RECEIVER
    FOREIGN KEY (receiver) REFERENCES user(username);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_TOUR
    FOREIGN KEY (tour_id) REFERENCES tour(id);

INSERT INTO user(username, email, password, fullname, role, created_at, updated_at)
VALUES ('admin', 'admin@holidayapp.com', 'admin', 'Administrator', 'admin', NOW(), NOW());

INSERT INTO user(username, email, phone, password, fullname, role, created_at, updated_at)
VALUES ('tungms', 'tungms@holidayapp.com', '0123456789', 'maisontung', 'Mai Sơn Tùng', 'general', NOW(), NOW());