package org.opennms.horizon.minion.ignite.worker.workflows.impl;

import java.util.concurrent.TimeUnit;
import org.opennms.horizon.minion.ignite.worker.workflows.RetriableExecutor;
import org.opennms.horizon.minion.ignite.worker.workflows.TaskExecutionResultProcessor;
import org.opennms.horizon.minion.ignite.worker.workflows.TaskExecutorLocalService;
import org.opennms.horizon.minion.scheduler.OpennmsScheduler;
import org.opennms.taskset.model.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local implementation of the service to execute a workflow that implements retry handling.  This class runs "locally"
 *  only, so it is never serialized / deserialized; this enables the "ignite" service to be a thin implementation,
 *  reducing the chances of problems due to serialization/deserialization.
 */
public class TaskCommonRetryExecutor implements TaskExecutorLocalService {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(TaskCommonRetryExecutor.class);

    private Logger log = DEFAULT_LOGGER;

    private OpennmsScheduler opennmsScheduler;

    private TaskDefinition taskDefinition;
    private TaskExecutionResultProcessor resultProcessor;
    private RetriableExecutor retriableExecutor;
    private int numRepeatFailures = 0;

    public TaskCommonRetryExecutor(
            OpennmsScheduler opennmsScheduler,
            TaskDefinition taskDefinition,
            TaskExecutionResultProcessor resultProcessor,
            RetriableExecutor retriableExecutor) {

        this.opennmsScheduler = opennmsScheduler;
        this.taskDefinition = taskDefinition;
        this.resultProcessor = resultProcessor;
        this.retriableExecutor = retriableExecutor;
    }

//========================================
// API
//----------------------------------------

    @Override
    public void start() {
        try {
            retriableExecutor.init(this::handleDisconnect);

            attemptConnect();
        } catch (RuntimeException rtExc) {
            throw rtExc;
        }
    }

    @Override
    public void cancel() {
        opennmsScheduler.cancelTask(taskDefinition.getId());
        retriableExecutor.cancel();
    }

//========================================
// Connection Handling
//----------------------------------------

    private void handleDisconnect() {
        scheduleConnectionAttempt();
    }

    private void attemptConnect() {
        try {
            log.info("Attempting to connect: workflow-uuid={}", taskDefinition.getId());
            retriableExecutor.attempt();
            numRepeatFailures = 0;
        } catch (Exception exc) {
            numRepeatFailures++;

            log.info("Failed to connect: workflow-uuid={}", taskDefinition.getId(), exc);

            scheduleConnectionAttempt();
        }
    }

    private void scheduleConnectionAttempt() {
        int delay = calculateFallbackDelay();

        log.info("Scheduling next connection attempt: workflow-uuid={}; repeated-failures={}; retry-delay={}",
                taskDefinition.getId(),
                numRepeatFailures,
                delay);

        opennmsScheduler.scheduleOnce(taskDefinition.getId(), delay, TimeUnit.MILLISECONDS, this::attemptConnect);
    }

    /**
     * Determine the fallback delay before the next connection attempt, which is calculated solely based on the number
     *  of repeated connection failures.
     *
     * Fallback schedule:
     *  0.250 s
     *  1s
     *  5s
     *  10s
     *  30s ...
     *
     * @return
     */
    private int calculateFallbackDelay() {
        switch (numRepeatFailures) {
            case 0: // shouldn't happen
            case 1:
                return 250;

            case 2:
                return 1_000;

            case 3:
                return 5_000;

            case 4:
                return 10_000;

            default:
                return 30_000;
        }
    }
}
