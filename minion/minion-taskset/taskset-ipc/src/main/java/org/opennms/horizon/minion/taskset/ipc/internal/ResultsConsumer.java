package org.opennms.horizon.minion.taskset.ipc.internal;

import com.google.protobuf.Any;
import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.opennms.horizon.grpc.tasksets.contract.TaskSetResults;
import org.opennms.horizon.grpc.tasksets.contract.TaskSetResults.Builder;
import org.opennms.horizon.shared.ipc.sink.api.MessageDispatcherFactory;
import org.opennms.horizon.shared.ipc.sink.api.SyncDispatcher;
import org.opennms.taskset.model.Result;
import org.opennms.taskset.model.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultsConsumer implements Consumer<Results> {

  private final Logger logger = LoggerFactory.getLogger(ResultsConsumer.class);
  private final SyncDispatcher<TaskSetResults> dispatcher;

  public ResultsConsumer(MessageDispatcherFactory messageDispatcherFactory) {
    dispatcher = messageDispatcherFactory.createSyncDispatcher(new TaskSetResultsSinkModule());
  }

  @Override
  public void accept(Results results) {
    Builder taskSetResultsBuilder = TaskSetResults.newBuilder();
    for (Result result : results.getResults()) {
      TaskSetResults.TaskResult.Builder resultBuilder = taskSetResultsBuilder.addResultsBuilder();

      if (result.getParameters() != null) {
        for (Map.Entry<String, Object> entry : result.getParameters().entrySet()) {
          Object value = entry.getValue();
          Value.Builder valueBuilder = Value.newBuilder();
          if (value instanceof Number) {
            valueBuilder.setNumberValue(((Number) value).doubleValue());
          } else if (value instanceof String) {
            valueBuilder.setStringValue((String) value);
          } else {
            logger.warn("Unsupported result property {} {}", entry.getKey(), entry.getValue());
            valueBuilder.setNullValue(NullValue.NULL_VALUE);
          }
          resultBuilder.putParameters(entry.getKey(), Any.pack(valueBuilder.build()));
        }
      }
      Optional.ofNullable(result.getUuid()).ifPresent(resultBuilder::setUuid);
      Optional.ofNullable(result.getStatus()).ifPresent(resultBuilder::setStatus);
      Optional.ofNullable(result.getReason()).ifPresent(resultBuilder::setReason);
      taskSetResultsBuilder.addResults(resultBuilder.build());
    }
    dispatcher.send(taskSetResultsBuilder.build());
  }

}
