/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author niels
 */
public interface GenericRepository<T, ID extends Serializable> {

    /**
     * Get the Class of the entity.
     *
     * @return the class
     */
    Class<T> getEntityClass();

    /**
     * Find an entity by its primary key
     *
     * @param id the primary key
     * @return the entity
     */
    T findById(final ID id);

    /**
     * Load all entities.
     *
     * @return the list of entities
     */
    List<T> findAll();

    /**
     * Find entities based on an example.
     *
     * @param exampleInstance the example
     * @return the list of entities
     */
    List<T> findByExample(final T exampleInstance);

    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params    the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQuery(
            final String queryName,
            Object... params
    );

    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params    the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQueryAndNamedParams(
            final String queryName,
            final Map<String, ? extends Object> params
    );

    /**
     * Count all entities.
     *
     * @return the number of entities
     */
    long countAll();

    /**
     * Count entities based on an example.
     *
     * @param exampleInstance the search criteria
     * @return the number of entities
     */
    int countByExample(final T exampleInstance);


    /**
     * update an entity. This can be either a INSERT or UPDATE in the database.
     *
     * @param entity the entity to update
     * @return the saved entity
     */
    T update(final T entity);

    /**
     * delete an entity from the database.
     *
     * @param entity the entity to delete
     */
    void delete(final T entity);

    void save(final T entity);
    
    void flush();
    
    EntityManager getEntityManager();
}

