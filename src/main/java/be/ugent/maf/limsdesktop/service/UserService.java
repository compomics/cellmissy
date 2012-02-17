/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.service;

import be.ugent.maf.limsdesktop.entity.User;

/**
 *
 * @author niels
 */
public interface UserService extends GenericService<User, Long> {

    User findByFullName(String firstName, String lastName);

    User findByFirstName(String firstName);

    User findByLastName(String lastName);
}
