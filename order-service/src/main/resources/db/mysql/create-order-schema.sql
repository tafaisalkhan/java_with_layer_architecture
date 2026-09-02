CREATE DATABASE IF NOT EXISTS hexagonal_order_service;

USE hexagonal_order_service;

CREATE TABLE IF NOT EXISTS orders (
    id BINARY(16) NOT NULL PRIMARY KEY,
    customer_id BINARY(16) NOT NULL,
    payment_id BINARY(16) NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL
);
