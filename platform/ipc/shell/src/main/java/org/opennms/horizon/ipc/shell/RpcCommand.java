package org.opennms.horizon.ipc.shell;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;

@Command(scope = "opennms", name = "ping", description = "ICMP Ping")
public class RpcCommand implements Action {
    @Override
    public Object execute() throws Exception {
        return null;
    }
}
