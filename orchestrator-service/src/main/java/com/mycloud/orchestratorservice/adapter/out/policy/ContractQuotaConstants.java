package com.mycloud.orchestratorservice.adapter.out.policy;

import java.util.UUID;

public final class ContractQuotaConstants {
    public static final String RESERVE_QUOTA_PATH = "/contracts/quota/reserve";
    public static final String COMMIT_QUOTA_PATH = "/contracts/quota/commit";
    public static final String RELEASE_QUOTA_PATH = "/contracts/quota/release";
    public static final UUID VM_PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private ContractQuotaConstants() {
    }
}
