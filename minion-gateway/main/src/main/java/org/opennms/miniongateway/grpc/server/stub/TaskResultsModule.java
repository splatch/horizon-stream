package org.opennms.miniongateway.grpc.server.stub;

import com.google.protobuf.InvalidProtocolBufferException;
import org.opennms.horizon.grpc.tasksets.contract.TaskSetResults;
import org.opennms.horizon.shared.ipc.sink.aggregation.IdentityAggregationPolicy;
import org.opennms.horizon.shared.ipc.sink.api.AggregationPolicy;
import org.opennms.horizon.shared.ipc.sink.api.AsyncPolicy;
import org.opennms.horizon.shared.ipc.sink.api.SinkModule;

public class TaskResultsModule implements SinkModule<TaskSetResults, TaskSetResults> {

    public static final String MODULE_ID = "task-set-result";

    @Override
    public String getId() {
        return MODULE_ID;
    }

    @Override
    public int getNumConsumerThreads() {
        return 1;
    }

    @Override
    public byte[] marshal(TaskSetResults message) {
        return message.toByteArray();
    }

    @Override
    public TaskSetResults unmarshal(byte[] message) {
        try {
            return TaskSetResults.parseFrom(message);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] marshalSingleMessage(TaskSetResults message) {
        return marshal(message);
    }

    @Override
    public TaskSetResults unmarshalSingleMessage(byte[] message) {
        return unmarshal(message);
    }

    @Override
    public AggregationPolicy<TaskSetResults, TaskSetResults, ?> getAggregationPolicy() {
        return new IdentityAggregationPolicy<>();
    }

    @Override
    public AsyncPolicy getAsyncPolicy() {
        return new AsyncPolicy() {
            @Override
            public int getQueueSize() {
                return 10;
            }

            @Override
            public int getNumThreads() {
                return 1;
            }

            @Override
            public boolean isBlockWhenFull() {
                return true;
            }
        };
    }
}
