package org.opennms.netmgt.provision.service.scan.rpc.dummyimpl;

import java.net.InetAddress;
import org.opennms.netmgt.provision.service.scan.rpc.EntityScopeProvider;
import org.opennms.netmgt.provision.service.scan.rpc.Scope;

public class DummyEntityScopeProvider implements EntityScopeProvider {

    @Override
    public Scope getScopeForNode(Integer nodeId) {
        return null;
    }

    @Override
    public Scope getScopeForInterface(Integer nodeId, String ipAddress) {
        return null;
    }

    @Override
    public Scope getScopeForInterfaceByIfIndex(Integer nodeId, int ifIndex) {
        return null;
    }

    @Override
    public Scope getScopeForService(Integer nodeId, InetAddress ipAddress, String serviceName) {
        return null;
    }
}
