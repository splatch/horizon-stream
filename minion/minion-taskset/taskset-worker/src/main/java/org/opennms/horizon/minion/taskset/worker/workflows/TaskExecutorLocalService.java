package org.opennms.horizon.minion.ignite.worker.workflows;

public interface TaskExecutorLocalService {
    void start() throws Exception;
    void cancel();
}
