INSERT INTO operation_step_execution_configs
    (step_name, retry_enabled, max_attempts, retry_delay_seconds, required_step, rollback_on_failure)
VALUES
    ('CHECK_CUSTOMER_QUOTA', true, 3, 30, true, false),
    ('CHECK_RESOURCE_ELIGIBILITY', true, 3, 30, true, false),
    ('CREATE_USER_TOKEN', true, 3, 30, true, false),
    ('LOAD_PROVIDER_CONFIGURATION', true, 3, 30, true, false),
    ('PROVIDER_LOGIN', true, 3, 30, true, false),
    ('PROVISION_RESOURCE', true, 3, 30, true, true),
    ('ASSIGN_PUBLIC_IP', true, 3, 30, true, true),
    ('COLLECT_RESOURCE_METADATA', true, 3, 30, true, true),
    ('REGISTER_MONITORING', false, 1, 0, false, false),
    ('COMMIT_QUOTA', true, 3, 30, true, false),
    ('REQUEST_BILLING', true, 3, 30, true, false),
    ('ROLLBACK_RESOURCE', true, 3, 30, true, false),
    ('RELEASE_QUOTA', true, 3, 30, true, false)
ON DUPLICATE KEY UPDATE
    retry_enabled = VALUES(retry_enabled),
    max_attempts = VALUES(max_attempts),
    retry_delay_seconds = VALUES(retry_delay_seconds),
    required_step = VALUES(required_step),
    rollback_on_failure = VALUES(rollback_on_failure);
