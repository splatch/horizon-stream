package org.opennms.poc.ignite.worker.workflows;

import org.opennms.horizon.minion.ignite.model.workflows.Workflow;

public interface WorkflowExecutorLocalServiceFactory {
    WorkflowExecutorLocalService create(Workflow workflow);
}
