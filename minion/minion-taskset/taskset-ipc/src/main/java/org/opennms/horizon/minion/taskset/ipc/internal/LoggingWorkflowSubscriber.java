package org.opennms.horizon.minion.taskset.ipc.internal;

import org.opennms.horizon.minion.ignite.model.workflows.Workflows;
import org.opennms.horizon.minion.ipc.twin.api.TwinListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingWorkflowSubscriber implements TwinListener<Workflows> {

  private final Logger logger = LoggerFactory.getLogger(LoggingWorkflowSubscriber.class);

  @Override
  public void accept(Workflows workflowTwin) {
    logger.info(">> Received workflow twin {}", workflowTwin);
  }

  @Override
  public Class<Workflows> getType() {
    return Workflows.class;
  }
}
