package org.opennms.netmgt.provision.persistence;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class DefaultRevisionRepositoryTest {

    ProvisionRepository provisionRepository;

    @Before
    public void setUp() {
        provisionRepository = new DefaultRevisionRepository();
    }

    @Test(expected = RuntimeException.class)
    public void save() {
        String id = provisionRepository.save("blah");
        assertNotNull(id);
    }

    @Test(expected = RuntimeException.class)
    public void read() {
        String xml = provisionRepository.read("blahId");
        assertNotNull(xml);
    }

    @Test(expected = RuntimeException.class)
    public void delete() {
        provisionRepository.delete("blahId");
    }
}