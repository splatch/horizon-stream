package org.opennms.horizon.minion.ignite.worker.workflows.impl;

import org.opennms.horizon.minion.ignite.model.workflows.Workflows;
import org.opennms.horizon.minion.ignite.worker.workflows.WorkflowLifecycleManager;
import org.opennms.horizon.minion.ipc.twin.api.TwinListener;

public class TwinToWorkflowLifecycleManagerAdapter implements TwinListener<Workflows> {

  private final WorkflowLifecycleManager workflowLifecycleManager;

  public TwinToWorkflowLifecycleManagerAdapter(WorkflowLifecycleManager workflowLifecycleManager) {
    this.workflowLifecycleManager = workflowLifecycleManager;
  }

  @Override
  public Class<Workflows> getType() {
    return Workflows.class;
  }

  @Override
  public void accept(Workflows workflows) {
    workflowLifecycleManager.deploy(workflows);
  }
}
