CREATE DATABASE IF NOT EXISTS hexagonal_order_service;

CREATE USER IF NOT EXISTS 'myuser'@'172.17.0.1' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON hexagonal_order_service.* TO 'myuser'@'172.17.0.1';
CREATE USER IF NOT EXISTS 'myuser'@'%' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON hexagonal_order_service.* TO 'myuser'@'%';
FLUSH PRIVILEGES;
