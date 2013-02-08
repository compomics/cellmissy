/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.repository.GenericRepository;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;

/**
 *
 * @param <T>
 * @param <ID>
 * @author niels
 */
public class GenericJpaRepository<T, ID extends Serializable> implements GenericRepository<T, ID> {

    private final Class<T> persistentClass;
    private EntityManager entityManager;

    public GenericJpaRepository() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public GenericJpaRepository(final Class<T> persistentClass) {
        super();
        this.persistentClass = persistentClass;
    }

    @Override
    public long countAll() {
        return countByCriteria();
    }

    @Override
    public int countByExample(final T exampleInstance) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria crit = session.createCriteria(getEntityClass());
        crit.setProjection(Projections.rowCount());
        crit.add(Example.create(exampleInstance));

        return (Integer) crit.list().get(0);
    }

    @Override
    public List<T> findAll() {
        return findByCriteria();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByExample(final T exampleInstance) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria crit = session.createCriteria(getEntityClass());
        final List<T> result = crit.list();
        return result;
    }

    @Override
    public T findById(final ID id) {
        final T result = getEntityManager().find(persistentClass, id);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByNamedQuery(final String name, Object... params) {
        javax.persistence.Query query = getEntityManager().createNamedQuery(name);

        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }

        final List<T> result = (List<T>) query.getResultList();
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByNamedQueryAndNamedParams(final String name,
            final Map<String, ? extends Object> params) {
        javax.persistence.Query query = getEntityManager().createNamedQuery(
                name);

        for (final Map.Entry<String, ? extends Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }

        final List<T> result = (List<T>) query.getResultList();
        return result;
    }

    @Override
    public Class<T> getEntityClass() {
        return persistentClass;
    }

    /**
     * set the JPA entity manager to use.
     *
     * @param entityManager
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Use this inside subclasses as a convenience method.
     *
     * @param criterion
     * @return
     */
    protected List<T> findByCriteria(final Criterion... criterion) {
        return findByCriteria(-1, -1, criterion);
    }

    /**
     * Use this inside subclasses as a convenience method.
     *
     * @param firstResult
     * @param maxResults
     * @param criterion
     * @return  
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(final int firstResult,
            final int maxResults, final Criterion... criterion) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria crit = session.createCriteria(getEntityClass());

        for (final Criterion c : criterion) {
            crit.add(c);
        }

        if (firstResult > 0) {
            crit.setFirstResult(firstResult);
        }

        if (maxResults > 0) {
            crit.setMaxResults(maxResults);
        }

        final List<T> result = crit.list();
        return result;
    }

    protected long countByCriteria(Criterion... criterion) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria crit = session.createCriteria(getEntityClass());
        crit.setProjection(Projections.rowCount());

        for (final Criterion c : criterion) {
            crit.add(c);
        }

        return (Long) crit.list().get(0);
    }

    @Override
    public void delete(T entity) {
        getEntityManager().remove(entity);
    }

    @Override
    public T update(T entity) {
        final T mergedEntity = getEntityManager().merge(entity);
        return mergedEntity;
    }

    @Override
    public void save(T entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }
}
