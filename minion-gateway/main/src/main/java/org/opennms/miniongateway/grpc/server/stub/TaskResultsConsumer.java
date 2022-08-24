package org.opennms.miniongateway.grpc.server.stub;

import org.opennms.horizon.grpc.tasksets.contract.TaskSetResults;
import org.opennms.horizon.shared.ipc.sink.api.MessageConsumer;
import org.opennms.horizon.shared.ipc.sink.api.SinkModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskResultsConsumer implements MessageConsumer<TaskSetResults, TaskSetResults> {

    private final Logger logger = LoggerFactory.getLogger(TaskResultsConsumer.class);

    @Override
    public SinkModule<TaskSetResults, TaskSetResults> getModule() {
        return new TaskResultsModule();
    }

    @Override
    public void handleMessage(TaskSetResults messageLog) {
        logger.warn("Received results {}", messageLog);
    }

}
