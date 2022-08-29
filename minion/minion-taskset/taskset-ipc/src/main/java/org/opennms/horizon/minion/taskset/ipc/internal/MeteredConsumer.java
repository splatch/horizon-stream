package org.opennms.horizon.minion.taskset.ipc.internal;

import static com.codahale.metrics.MetricRegistry.name;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.opennms.horizon.minion.observability.metrics.MetricsProvider;
import org.opennms.horizon.shared.ipc.rpc.IpcIdentity;
import org.opennms.taskset.model.Results;

public class MeteredConsumer implements Consumer<Results>, MetricsProvider {

  private final MetricRegistry metrics = new MetricRegistry();
  private final IpcIdentity identity;
  private final Consumer<Results> delegate;

  public MeteredConsumer(IpcIdentity identity, Consumer<Results> delegate) {
    this.identity = identity;
    this.delegate = delegate;
  }

  @Override
  public void accept(Results results) {
    Counter counter = get(name("minion", identity.getLocation(), identity.getId(), "result.count"), Counter::new);
    counter.inc();
    delegate.accept(results);
  }

  @Override
  public MetricRegistry getMetrics() {
    return metrics;
  }

  private <T extends Metric> T get(String name, Supplier<T> creator) {
    if (!metrics.getMetrics().containsKey(name)) {
      metrics.register(name, creator.get());
    }
    return (T) metrics.getMetrics().get(name);
  }

}
