package org.opennms.horizon.minion.ignite.model.workflows;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class PluginMetadata {

    private String pluginName;
    private WorkflowType pluginType;
}
