INSERT INTO operation_config
    (id, operation_type, version, enabled, rollback_mode, quota_rollback_enabled)
VALUES (1, 'CREATE_VM', 1, true, 'AUTO', true)
ON DUPLICATE KEY UPDATE enabled = VALUES(enabled), rollback_mode = VALUES(rollback_mode),
    quota_rollback_enabled = VALUES(quota_rollback_enabled);

INSERT INTO step_config
    (id, operation_config_id, step_name, sequence_number, retry_enabled,
     max_attempts, retry_delay_seconds, required_step, enabled,
     execution_type, poll_interval_seconds)
VALUES
    (101, 1, 'CHECK_CUSTOMER_QUOTA',        10, true,  3, 30, true,  true, 'ACTION', NULL),
    (102, 1, 'CHECK_RESOURCE_ELIGIBILITY',  10, true,  3, 30, true,  true, 'ACTION', NULL),
    (104, 1, 'LOAD_PROVIDER_CONFIGURATION', 30, true,  3, 30, true,  true, 'ACTION', NULL),
    (106, 1, 'PROVISION_RESOURCE',           50, true,  3, 30, true,  true, 'ACTION', NULL),
    (112, 1, 'WAIT_FOR_VM_ACTIVE',           60, false, 1,  0, true,  true, 'POLL',   5),
    (107, 1, 'ASSIGN_PUBLIC_IP',             70, true,  3, 30, true,  true, 'ACTION', NULL),
    (108, 1, 'COLLECT_RESOURCE_METADATA',    80, true,  3, 30, true,  true, 'ACTION', NULL),
    (109, 1, 'REGISTER_MONITORING',          90, false, 1,  0, false, true, 'ACTION', NULL),
    (110, 1, 'COMMIT_QUOTA',                100, true,  3, 30, true,  true, 'ACTION', NULL),
    (111, 1, 'REQUEST_BILLING',             110, true,  3, 30, false, true, 'ACTION', NULL)
ON DUPLICATE KEY UPDATE step_name = VALUES(step_name), sequence_number = VALUES(sequence_number), retry_enabled = VALUES(retry_enabled),
    max_attempts = VALUES(max_attempts), retry_delay_seconds = VALUES(retry_delay_seconds),
    required_step = VALUES(required_step), enabled = VALUES(enabled), execution_type = VALUES(execution_type),
    poll_interval_seconds = VALUES(poll_interval_seconds);

-- Authentication is runtime infrastructure behavior, not durable workflow state.
-- Disable legacy authentication steps if this database was initialized by an older version.
UPDATE step_config SET enabled = false WHERE id IN (103, 105);

-- Every dependency must succeed before its target step is ready.
-- Ready steps having the same dependency set and sequence can execute in parallel.
DELETE FROM step_dependency WHERE id IN (1010, 1011);

INSERT INTO step_dependency (id, step_config_id, depends_on_step_config_id)
VALUES
    (1001, 104, 101), (1002, 104, 102), (1003, 106, 104),
    (1004, 112, 106), (1005, 107, 112), (1006, 108, 107),
    (1007, 109, 108), (1008, 110, 109), (1009, 111, 110)
ON DUPLICATE KEY UPDATE step_config_id = VALUES(step_config_id),
    depends_on_step_config_id = VALUES(depends_on_step_config_id);
