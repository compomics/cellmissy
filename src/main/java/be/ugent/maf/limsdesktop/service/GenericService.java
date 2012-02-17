/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.service;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author niels
 */
public interface GenericService<T, ID extends Serializable> {

    T findById(final ID id);

    List<T> findAll();

    T save(final T entity);

    void delete(final T entity);

}
