# Orchestrator Scheduler Services

## Purpose

The orchestrator executes provisioning operations asynchronously. HTTP requests create `PENDING` operations in the database; scheduler services claim eligible operations and submit them to worker pools.

Scheduling is split into two services so new provisioning work and provider-status polling cannot block each other.

## Scheduler Flow

```text
Create VM API
     |
     v
PENDING operation in database
     |
     v
PendingOperationSchedulerService
     |
     v
provisioningExecutor -> OrchestratorApplicationService.execute(...)
                            |
                            | provider VM is still building
                            v
                       WAITING operation
                            |
                            v
WaitingOperationPollingSchedulerService
                            |
                            v
pollingExecutor -> OrchestratorApplicationService.execute(...)
```

## Components

### PendingOperationSchedulerService

File: `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/application/service/PendingOperationSchedulerService.java`

- Runs on `app.scheduler.pending.fixed-delay-ms`.
- Atomically claims up to `app.scheduler.pending.batch-size` eligible `PENDING` operations.
- Submits each operation to `provisioningExecutor`.
- Handles new operations and retryable failed steps whose retry time has arrived.

### WaitingOperationPollingSchedulerService

File: `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/application/service/WaitingOperationPollingSchedulerService.java`

- Runs on `app.scheduler.polling.fixed-delay-ms`.
- Atomically claims up to `app.scheduler.polling.batch-size` eligible `WAITING` operations.
- Submits each operation to `pollingExecutor`.
- Resumes operations when the next provider-status poll is due.

### OperationExecutorConfiguration

File: `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/config/OperationExecutorConfiguration.java`

Defines two bounded worker pools:

- `provisioningExecutor` for normal provisioning work.
- `pollingExecutor` for status polling.

Keep these executors separate. Provider polling may be frequent or slow and must not exhaust the workers used to start new operations.

### OperationRepositoryPort

File: `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/application/port/out/spi/OperationRepositoryPort.java`

The scheduler uses these atomic ownership methods:

- `claimNextPending(int limit)`
- `claimWaitingReadyForPoll(int limit)`

The JPA adapter obtains pessimistic database locks, changes claimed records to `RUNNING`, flushes the changes, and then returns them. This prevents two scheduler ticks or two orchestrator instances from executing the same operation concurrently.

## Configuration

```yaml
app:
  scheduler:
    pending:
      fixed-delay-ms: ${PENDING_OPERATION_SCHEDULER_DELAY_MS:5000}
      batch-size: ${PENDING_OPERATION_BATCH_SIZE:10}
      worker-threads: ${PROVISIONING_WORKER_THREADS:10}
    polling:
      fixed-delay-ms: ${WAITING_OPERATION_SCHEDULER_DELAY_MS:2000}
      batch-size: ${WAITING_OPERATION_BATCH_SIZE:50}
      worker-threads: ${POLLING_WORKER_THREADS:20}
```

Scheduling is enabled by `@EnableScheduling` on `OrchestratorServiceApplication`.

## State Rules

- `PENDING`: eligible for the pending scheduler unless a step has a future `nextRetryAt`.
- `RUNNING`: already claimed; schedulers must not claim it again.
- `WAITING`: VM creation was submitted and the operation is waiting for a provider-status poll.
- `SUCCEEDED`, `FAILED`, `ROLLED_BACK`, or `ROLLBACK_FAILED`: terminal for normal scheduler selection.

The scheduler only dispatches work. Business workflow, retries, polling decisions, rollback, and final state changes remain in `OrchestratorApplicationService` and the domain model.

## Adding Another Scheduled Operation Type

Prefer reusing the existing schedulers if the new operation follows the same `PENDING -> RUNNING -> WAITING/terminal` lifecycle. Provider-specific code belongs in provider strategies/workflows, not in a scheduler.

Add another scheduler only when the work has a genuinely different selection rule or resource-isolation requirement. If one is required:

1. Add an atomic claim method to `OperationRepositoryPort`.
2. Implement it with database locking in the persistence adapter.
3. Add a dedicated bounded executor if the workload can starve existing workers.
4. Create a small scheduler service that only claims and dispatches.
5. Add configuration for delay, batch size, and worker count.
6. Test eligibility, concurrent claiming, and failure behavior.

Never perform provider API calls while holding the database claim transaction open.

## Verification

Run:

```powershell
mvn -pl orchestrator-service -am test
```

Before changing claim queries, also verify behavior with the production database engine because locking and pagination behavior can vary between databases.
