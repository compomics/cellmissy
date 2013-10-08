/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import java.io.Serializable;
import java.util.List;

/**
 * Generic Service
 * @author Paola Masuzzo
 * @param <T>
 * @param <ID> 
 */
public interface GenericService<T, ID extends Serializable> {

    T findById(final ID id);

    List<T> findAll();

    T update(final T entity);

    void delete(final T entity);

    void save(final T entity);
}
