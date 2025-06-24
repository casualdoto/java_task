CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tasks (
    id BINARY(16) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    creation_date TIMESTAMP NOT NULL,
    target_date TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
    id BINARY(16) PRIMARY KEY,
    message VARCHAR(500) NOT NULL,
    `read` BOOLEAN NOT NULL DEFAULT FALSE,
    creation_date TIMESTAMP NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
); 