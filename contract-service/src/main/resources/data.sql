INSERT INTO default_contract_configs
    (config_key, contract_type, duration_days, product_id, product_name, quantity, unit_amount, currency, signup_credit_amount)
VALUES
    ('TRIAL', 'PAY_AS_YOU_GO', 30, UUID_TO_BIN('00000000-0000-0000-0000-000000000001'), 'Trial VM', 2, 0.00, 'USD', 10.00)
ON DUPLICATE KEY UPDATE
    contract_type = VALUES(contract_type),
    duration_days = VALUES(duration_days),
    product_id = VALUES(product_id),
    product_name = VALUES(product_name),
    quantity = VALUES(quantity),
    unit_amount = VALUES(unit_amount),
    currency = VALUES(currency),
    signup_credit_amount = VALUES(signup_credit_amount);
INSERT INTO products (id, name, description, status)
VALUES (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'), 'Trial VM', 'Default VM product used by signup trial quota', 'ACTIVE')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description), status = VALUES(status);

INSERT INTO product_price_history (product_id, price_id, amount, currency, effective_from, effective_to)
VALUES (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'), UUID_TO_BIN('00000000-0000-0000-0000-000000000101'), 1.00, 'USD', '2026-01-01', NULL)
ON DUPLICATE KEY UPDATE amount = VALUES(amount), currency = VALUES(currency), effective_from = VALUES(effective_from), effective_to = VALUES(effective_to);
