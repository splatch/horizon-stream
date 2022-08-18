package org.opennms.horizon.minion.ignite.worker.workflows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.opennms.taskset.model.TaskDefinition;
import org.opennms.taskset.model.TaskSet;

public class TaskRepository {

    public Collection<TaskDefinition> getTaskDefinitions() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("workflows.json");
            if (stream == null) {
                throw new FileNotFoundException("Test file does not exist.");
            }
            else {
                return objectMapper.readValue(stream, TaskSet.class).getTaskDefinitionList();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
