/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.User;

/**
 *
 * @author niels
 */
public interface UserService extends GenericService<User, Long> {

    /**
     * find an User by firstName + lastName
     * @param firstName
     * @param lastName
     * @return found User, if any
     */
    User findByFullName(String firstName, String lastName);

    /**
     * find an User by firstName
     * @param firstName
     * @return found User, if any
     */
    User findByFirstName(String firstName);

    /**
     * find an User by lastName
     * @param lastName
     * @return found User, if any
     */
    User findByLastName(String lastName);
}
