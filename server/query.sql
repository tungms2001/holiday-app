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

CREATE TABLE conversation(
    id INT AUTO_INCREMENT,
    title VARCHAR(100) CHARSET utf8,
    creator VARCHAR(100),
    PRIMARY KEY (id)
);

CREATE TABLE deleted_conversation(
    conversation_id INT,
    user VARCHAR(100),
    deleted_at DATETIME,
    PRIMARY KEY (conversation_id, user)
);

CREATE TABLE message(
    id INT AUTO_INCREMENT,
    conversation_id INT,
    sender VARCHAR(100),
    content VARCHAR(1000) CHARSET utf8,
    created_at DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE deleted_message(
    user VARCHAR(100),
    message_id INT,
    deleted_at DATETIME,
    PRIMARY KEY (user, message_id)
);

CREATE TABLE participant(
    conversation_id INT,
    user VARCHAR(100),
    PRIMARY KEY (conversation_id, user)
);

CREATE TABLE tour(
    id INT AUTO_INCREMENT,
    tour_name VARCHAR(200) CHARSET utf8,
    creator VARCHAR(100),
    status VARCHAR(50),
    cost FLOAT,
    image VARCHAR(500),
    note VARCHAR(200) CHARSET utf8,
    rate FLOAT,
    created_at DATETIME,
    updated_at DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE stop_point(
    id INT AUTO_INCREMENT,
    tour_id INT,
    stop_name VARCHAR(200) CHARSET utf8,
    beginning DATETIME,
    end DATETIME,
    description VARCHAR(200) CHARSET utf8,
    image VARCHAR(500),
    status VARCHAR(50),
    rate FLOAT,
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
    image VARCHAR(500),
    rate INT,
    active BOOL,
    created_at DATETIME,
    updated_at DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE stop_point_comment(
    id INT AUTO_INCREMENT,
    user VARCHAR(100),
    stop_point_id INT,
    image VARCHAR(500),
    rate INT,
    active BOOL,
    created_at DATETIME,
    updated_at DATETIME,
    PRIMARY KEY (id)
);

ALTER TABLE user
ADD CONSTRAINT CHK_ROLE
CHECK (role IN('admin','general'));

ALTER TABLE tour
ADD CONSTRAINT CHK_TOUR_STATUS
CHECK (status IN ('open', 'closed', 'in-progress', 'prepared', 'done', 'delayed'));

ALTER TABLE stop_point
ADD CONSTRAINT CHK_STOP_POINT_STATUS
CHECK(status IN ('not visited', 'visited', 'skipped'));

ALTER TABLE conversation
ADD CONSTRAINT FK_USER_AS_CREATOR
FOREIGN KEY (creator) REFERENCES user(username);

ALTER TABLE participant
ADD CONSTRAINT FK_USER_AS_PARTICIPANT
FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE participant
ADD CONSTRAINT FK_PARTICIPANT_IN_CONSERVATION
FOREIGN KEY (conversation_id) REFERENCES conversation(id);

ALTER TABLE message
ADD CONSTRAINT FK_USER_AS_SENDER
FOREIGN KEY (sender) REFERENCES user(username);

ALTER TABLE message
ADD CONSTRAINT FK_MESSAGE_IN_CONVERSATION
FOREIGN KEY (conversation_id) REFERENCES conversation(id);

ALTER TABLE deleted_conversation
ADD CONSTRAINT FK_USER_DELETED_CONVERSATION
FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE deleted_conversation
ADD CONSTRAINT FK_LINK_CONVERSATION
FOREIGN KEY (conversation_id) REFERENCES conversation(id);

ALTER TABLE deleted_message
ADD CONSTRAINT FK_USER_DELETE_MESSAGE
FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE deleted_message
ADD CONSTRAINT FK_LINK_MESSAGE
FOREIGN KEY (message_id) REFERENCES message(id);

ALTER TABLE tour
ADD CONSTRAINT FK_USER_AS_TOUR_CREATOR
FOREIGN KEY (creator) REFERENCES user(username);

ALTER TABLE member
ADD CONSTRAINT FK_TOUR_MEMBER
FOREIGN KEY (tour_id) REFERENCES tour(id);

ALTER TABLE member
ADD CONSTRAINT FK_USER_AS_MEMBER
FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE stop_point
ADD CONSTRAINT FK_STOP_POINT_IN_TOUR
FOREIGN KEY (tour_id) REFERENCES tour(id);

ALTER TABLE tour_comment
ADD CONSTRAINT FK_USER_AS_TOUR_COMMENTER
FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE tour_comment
ADD CONSTRAINT FK_COMMENT_IN_TOUR
FOREIGN KEY (tour_id) REFERENCES tour(id);

ALTER TABLE stop_point_comment
ADD CONSTRAINT FK_USER_AS_STOP_POINT_COMMENTER
FOREIGN KEY (user) REFERENCES user(username);

ALTER TABLE stop_point_comment
ADD CONSTRAINT FK_COMMENT_IN_STOP_POINT
FOREIGN KEY (stop_point_id) REFERENCES stop_point(id);