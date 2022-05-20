package org.opennms.netmgt.provision.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.netmgt.provision.persistence.dao.HibernateRequisitionEntity;
import org.opennms.horizon.db.dao.api.PersistenceContextHolder;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public class HibernateRequisitionRepositoryTest {

    public static final String BLAH_ID = "blahId";

    private RequisitionRepository requisitionRepository;
    private RequisitionDTO dto ;

    @Mock
    EntityManager entityManager;
    @Mock
    EntityManagerFactory entityManagerFactory;
    @Mock
    PersistenceUnitUtil persistenceUnitUtil;
    HibernateRequisitionEntity hibernateRequisitionEntity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        PersistenceContextHolder p = new DummyPersistenceContextHolder();
        requisitionRepository = new HibernateRequisitionRepository(p);
        when(entityManager.getEntityManagerFactory()).thenReturn(entityManagerFactory);
        when(entityManagerFactory.getPersistenceUnitUtil()).thenReturn(persistenceUnitUtil);

        dto = new RequisitionDTO(BLAH_ID);
        hibernateRequisitionEntity = new HibernateRequisitionEntity(dto.getId(), dto);
    }

    @Test
    public void save() {
        String id = requisitionRepository.save(dto);
        assertNotNull(id);
    }

    @Test
    public void read() {
        when(entityManager.find(eq(HibernateRequisitionEntity.class), anyString())).thenReturn(hibernateRequisitionEntity);
        RequisitionDTO readDto = requisitionRepository.read(BLAH_ID);
        assertNotNull(readDto);
    }

    //TODO: can we check for an exception here?
    @Test
    public void delete() {
        requisitionRepository.delete(BLAH_ID);
        assertTrue(true);
    }

    private class DummyPersistenceContextHolder implements PersistenceContextHolder {

        @Override
        public EntityManager getEntityManager() {
            return entityManager;
        }
    }
}