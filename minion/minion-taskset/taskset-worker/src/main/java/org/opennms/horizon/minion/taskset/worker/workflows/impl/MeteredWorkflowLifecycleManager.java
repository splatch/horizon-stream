package org.opennms.horizon.minion.ignite.worker.workflows.impl;

import static com.codahale.metrics.MetricRegistry.name;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import java.util.Collections;
import java.util.Map;
import org.opennms.horizon.minion.ignite.worker.workflows.TaskLifecycleManager;
import org.opennms.horizon.shared.ipc.rpc.IpcIdentity;
import org.opennms.taskset.model.TaskSet;

public class MeteredWorkflowLifecycleManager implements TaskLifecycleManager, MetricSet {

  private final IpcIdentity identity;
  private final TaskLifecycleManager delegate;
  private final Counter counter = new Counter();

  public MeteredWorkflowLifecycleManager(IpcIdentity identity, TaskLifecycleManager delegate) {
    this.identity = identity;
    this.delegate = delegate;
  }

  @Override
  public int deploy(TaskSet taskSet) {
    int size = taskSet.getTaskDefinitionList().size();
    int deployed = delegate.deploy(taskSet);
    counter.inc(size - deployed);
    return deployed;
  }

  @Override
  public Map<String, Metric> getMetrics() {
    return Collections.singletonMap(name("minion", identity.getLocation(), identity.getId(), "taskset"), counter);
  }

}
