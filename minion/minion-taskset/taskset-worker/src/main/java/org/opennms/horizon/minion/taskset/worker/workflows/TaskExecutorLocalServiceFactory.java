package org.opennms.horizon.minion.ignite.worker.workflows;

import org.opennms.taskset.model.TaskDefinition;

public interface TaskExecutorLocalServiceFactory {
    TaskExecutorLocalService create(TaskDefinition taskDefinition);
}
