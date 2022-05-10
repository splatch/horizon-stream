package org.opennms.netmgt.provision.persistence;

public class DefaultRevisionRepository implements ProvisionRepository {

    @Override
    public String save(String s) {
        throw new RuntimeException("not yet implemented");
        //return new id
    }

    @Override
    public String read(String id) {
        throw new RuntimeException("not yet implemented");
//        return null;
    }

    @Override
    public void delete(String id) {
        throw new RuntimeException("not yet implemented");
    }
}
