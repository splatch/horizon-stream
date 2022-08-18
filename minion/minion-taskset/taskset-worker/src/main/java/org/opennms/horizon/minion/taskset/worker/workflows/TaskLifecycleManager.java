package org.opennms.horizon.minion.ignite.worker.workflows;

import org.opennms.taskset.model.TaskSet;

/**
 * Responsible for management of task lifecycle.
 * Ensure all elements from workflows/task set are processed.
 */
public interface TaskLifecycleManager {

  /**
   * Force deployment of a given task set.
   *
   * @param taskSet Task set.
   * @return Number of stopped tasks.
   */
  int deploy(TaskSet taskSet);

}
