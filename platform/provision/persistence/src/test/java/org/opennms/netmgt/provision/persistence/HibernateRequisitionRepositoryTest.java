package org.opennms.netmgt.provision.persistence;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public class HibernateRequisitionRepositoryTest {

    RequisitionRepository requisitionRepository;

    @Before
    public void setUp() {
        requisitionRepository = new HibernateRequisitionRepository();
    }

    @Test
    public void save() {
        RequisitionDTO dto = new RequisitionDTO("blahId");
        String id = requisitionRepository.save(dto);
        assertNotNull(id);
    }

    @Test(expected = RuntimeException.class)
    public void read() {
        RequisitionDTO dto = requisitionRepository.read("blahId");
        assertNotNull(dto);
    }

    @Test(expected = RuntimeException.class)
    public void delete() {
        requisitionRepository.delete("blahId");
    }
}