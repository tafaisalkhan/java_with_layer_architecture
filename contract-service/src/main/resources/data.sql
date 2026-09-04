INSERT INTO default_contract_configs
    (config_key, contract_type, duration_days, product_id, product_name, quantity, unit_amount, currency, signup_credit_amount)
VALUES
    ('TRIAL', 'PAY_AS_YOU_GO', 30, '00000000-0000-0000-0000-000000000001', 'Trial VM', 2, 0.00, 'USD', 10.00)
ON DUPLICATE KEY UPDATE
    contract_type = VALUES(contract_type),
    duration_days = VALUES(duration_days),
    product_id = VALUES(product_id),
    product_name = VALUES(product_name),
    quantity = VALUES(quantity),
    unit_amount = VALUES(unit_amount),
    currency = VALUES(currency),
    signup_credit_amount = VALUES(signup_credit_amount);
