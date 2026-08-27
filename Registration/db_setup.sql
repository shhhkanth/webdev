CREATE DATABASE IF NOT EXISTS registration_db;

USE registration_db;

CREATE TABLE IF NOT EXISTS students (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    regno       VARCHAR(50)   NOT NULL UNIQUE,
    email       VARCHAR(100)  NOT NULL,
    phone       CHAR(10)      NOT NULL,
    dob         DATE          NOT NULL,
    gender      ENUM('Male','Female','Other') NOT NULL,
    course      VARCHAR(100)  NOT NULL,
    department  VARCHAR(100)  NOT NULL,
    semester    VARCHAR(20)   NOT NULL,
    address     TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
