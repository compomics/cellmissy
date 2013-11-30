/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.bean;

import be.ugent.maf.cellmissy.entity.User;
import org.springframework.stereotype.Component;

/**
 * Authentication Bean: this keeps the current user in the application.
 *
 * @author Paola Masuzzo
 */
@Component("authenticationBean")
public class AuthenticationBean {

    private User currentUser;

    /**
     * Constructor
     */
    public AuthenticationBean() {
    }

    /**
     * Get the current user
     *
     * @return
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set the current user
     *
     * @param currentUser
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
