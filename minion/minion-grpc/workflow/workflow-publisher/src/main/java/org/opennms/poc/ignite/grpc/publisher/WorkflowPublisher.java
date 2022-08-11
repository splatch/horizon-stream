package org.opennms.poc.ignite.grpc.publisher;

import java.io.IOException;
import org.opennms.horizon.minion.ignite.model.workflows.Workflows;

public interface WorkflowPublisher {

  void publish(Workflows twin) throws IOException;

}
