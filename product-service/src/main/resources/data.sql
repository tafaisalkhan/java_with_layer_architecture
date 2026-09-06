INSERT INTO products (id, name, description, status)
VALUES (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'), 'Trial VM', 'Default VM product used by signup trial quota', 'ACTIVE');

INSERT INTO product_price_history (product_id, price_id, amount, currency, effective_from, effective_to)
VALUES (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'), UUID_TO_BIN('00000000-0000-0000-0000-000000000101'), 1.00, 'USD', '2026-01-01', NULL);
