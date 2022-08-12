package org.opennms.horizon.minion.ignite.worker.workflows.impl;

import static com.codahale.metrics.MetricRegistry.name;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import java.util.Collections;
import java.util.Map;
import org.opennms.horizon.minion.ignite.model.workflows.Workflows;
import org.opennms.horizon.minion.ignite.worker.workflows.WorkflowLifecycleManager;
import org.opennms.horizon.shared.ipc.rpc.IpcIdentity;

public class MeteredWorkflowLifecycleManager implements WorkflowLifecycleManager, MetricSet {

  private final IpcIdentity identity;
  private final WorkflowLifecycleManager delegate;
  private final Counter counter = new Counter();

  public MeteredWorkflowLifecycleManager(IpcIdentity identity, WorkflowLifecycleManager delegate) {
    this.identity = identity;
    this.delegate = delegate;
  }

  @Override
  public int deploy(Workflows workflows) {
    int size = workflows.getWorkflows().size();
    int deployed = delegate.deploy(workflows);
    counter.inc(size - deployed);
    return deployed;
  }

  @Override
  public Map<String, Metric> getMetrics() {
    return Collections.singletonMap(name("minion", identity.getLocation(), identity.getId(), "workflows"), counter);
  }

}
