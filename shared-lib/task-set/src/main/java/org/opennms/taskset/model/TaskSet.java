package org.opennms.taskset.model;

import lombok.Data;

import java.util.Collection;

@Data
public class TaskSet {
    private Collection<TaskDefinition> taskDefinitionList;
}
