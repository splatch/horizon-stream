package org.opennms.horizon.minion.ignite.worker.workflows;

import org.opennms.horizon.minion.ignite.model.workflows.Workflows;

/**
 * Responsible for management of task lifecycle.
 * Ensure all elements from workflows/task set are processed.
 */
public interface WorkflowLifecycleManager {

  /**
   * Force deployment of a given task set.
   *
   * @param workflows Task set.
   * @return Number of stopped tasks.
   */
  int deploy(Workflows workflows);

}
