package org.opennms.taskset.model;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TaskDefinition implements Serializable {
    /**
     * Identifier for this definition.  Must be unique within a full task set.  Also important to remain unchanged
     *   unless the task definition changes in a way that requires a restart on the Minion.
     */
    private String id;

    private String description;
    private TaskType type;
    private String pluginName;
    private Map<String,String> parameters = new LinkedHashMap<>();
    private String schedule;
}
