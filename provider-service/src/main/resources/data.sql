INSERT INTO providers (id, name, type, status, admin_username, credential_secret_ref)
VALUES ('33333333-3333-3333-3333-333333333333', 'Local OpenStack Test Provider', 'OPENSTACK', 'ACTIVE', 'admin', 'secret/openstack/local');

INSERT INTO provider_endpoints (provider_id, service_name, url)
VALUES
    ('33333333-3333-3333-3333-333333333333', 'keystone', 'http://localhost:5000/v3'),
    ('33333333-3333-3333-3333-333333333333', 'nova', 'http://localhost:8774/v2.1'),
    ('33333333-3333-3333-3333-333333333333', 'neutron', 'http://localhost:9696'),
    ('33333333-3333-3333-3333-333333333333', 'horizon', 'http://localhost/dashboard');
