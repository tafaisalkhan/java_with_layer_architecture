package com.mycloud.orchestratorservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OperationPollingTest {
    @Test
    void waitingForProviderReleasesOperationUntilNextPoll() {
        Operation operation = Operation.createVm(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), OperationPriority.NORMAL,
            new ResourceRequest("vm-1", "image", "flavor", "network"),
            List.of(OperationStepName.WAIT_FOR_VM_ACTIVE)
        );
        Instant now = Instant.parse("2026-09-06T10:00:00Z");

        Operation waiting = operation.start()
            .startStep(OperationStepName.WAIT_FOR_VM_ACTIVE, now)
            .waitForStepPoll(OperationStepName.WAIT_FOR_VM_ACTIVE, now, now.plusSeconds(5), "BUILDING");

        assertThat(waiting.status()).isEqualTo(OperationStatus.WAITING);
        assertThat(waiting.steps().getFirst().status()).isEqualTo(OperationStepStatus.WAITING);
        assertThat(waiting.steps().getFirst().nextRetryAt()).isEqualTo(now.plusSeconds(5));
        assertThat(waiting.steps().getFirst().providerStatus()).isEqualTo("BUILDING");
    }
}
