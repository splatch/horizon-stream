package org.opennms.netmgt.provision.persistence;

public interface ProvisionRepository{
    String save(String s);
    String read(String id);
    void delete(String id);
}
