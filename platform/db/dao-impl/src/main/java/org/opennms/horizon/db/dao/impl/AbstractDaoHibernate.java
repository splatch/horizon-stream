package org.opennms.horizon.db.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import org.opennms.horizon.db.dao.api.OnmsDao;

public abstract class AbstractDaoHibernate<T, K extends Serializable> implements OnmsDao<T, K> {

    @PersistenceContext(unitName = "dao-hibernate")
    private EntityManager entityManager;

    private final Class<T> entityClass;

    public AbstractDaoHibernate(Class<T> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass);
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entity);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Override
    public void delete(K key) {
        T entity = get(key);
        entityManager.remove(entity);
    }

    @SuppressWarnings("unchecked")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public List<T> findAll() {
        return entityManager.createQuery("Select a from " + entityClass.getSimpleName() + " a").getResultList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public T get(K id) {
        return entityManager.find(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public K save(T entity) {
        entityManager.persist(entity);
        return (K)(entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void update(T entity) {
        entityManager.persist(entity);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public void saveOrUpdate(T entity) {
        // FIXME: OOPS: Need more here
        entityManager.persist(entity);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public void flush() {
        entityManager.flush();
    }

    @SuppressWarnings("unchecked")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public List<T> findMatching(CriteriaQuery<?> query) {
        return (List<T>)entityManager.createQuery(query).getResultList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public long countAll() {
        return (Long)entityManager.createQuery("SELECT COUNT(x) from " + entityClass.getSimpleName() + " x").getSingleResult();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
