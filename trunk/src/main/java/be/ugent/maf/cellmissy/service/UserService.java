/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.User;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface UserService extends GenericService<User, Long> {

    /**
     * find an User by firstName + lastName
     *
     * @param firstName
     * @param lastName
     * @return found User, if any
     */
    public User findByFullName(String firstName, String lastName);

    /**
     * find an User by firstName
     *
     * @param firstName
     * @return found User, if any
     */
    public User findByFirstName(String firstName);

    /**
     * find an User by lastName
     *
     * @param lastName
     * @return found User, if any
     */
    public User findByLastName(String lastName);

    /**
     * Find the user by login credentials.
     *
     * @param userName the user name
     * @param password the user password
     * @return the found user
     */
    public User findByLoginCredentials(String userName, String password);

    /**
     * Find List of users busy with a certain project, given the project id.
     *
     * @param projectid
     * @return
     */
    public List<User> findUsersByProjectid(Long projectid);
}
