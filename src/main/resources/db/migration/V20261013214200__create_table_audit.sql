CREATE TABLE users_audit(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id BINARY(16) NOT NULL,
    operation CHAR(1) NOT NULL,
    old_username VARCHAR(255),
    old_password VARCHAR(255),
    old_role ENUM('MANAGER', 'CUSTOMER', 'CASHIER', 'STOCKER'),
    username VARCHAR(255),
    password VARCHAR(255),
    role ENUM('MANAGER', 'CUSTOMER', 'CASHIER', 'STOCKER'),
    datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);