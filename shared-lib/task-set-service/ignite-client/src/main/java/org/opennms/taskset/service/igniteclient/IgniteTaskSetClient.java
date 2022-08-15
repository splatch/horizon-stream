package org.opennms.taskset.service.igniteclient;

import org.opennms.taskset.model.TaskSet;

public interface IgniteTaskSetClient {
    void publishTaskSet(String location, TaskSet taskSet);
}
