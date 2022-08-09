package org.opennms.horizon.minion.ignite.worker.workflows;

public interface WorkflowExecutorLocalService {
    void start() throws Exception;
    void cancel();
}
