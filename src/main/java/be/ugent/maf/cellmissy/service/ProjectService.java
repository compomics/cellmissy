/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.User;
import java.io.File;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface ProjectService extends GenericService<Project, Long> {

    /**
     * Set up a new project
     *
     * @param projectNumber
     * @param description
     * @param projectDirectory
     * @return
     */
    Project setupProject(int projectNumber, String description, File projectDirectory);

    /**
     * Save the users for a certain project
     *
     * @param entity
     */
    void saveProjectUsers(Project entity);

    /**
     * Find projects by user id
     *
     * @param userid
     * @return a list of projects for those users
     */
    List<Project> findProjectsByUserid(Long userid);

    /**
     * Delete users from a project
     *
     * @param users
     * @param project
     */
    void deleteUsersFromProject(List<User> users, Project project);

    /**
     * Add users to a project
     *
     * @param users
     * @param project
     */
    void addUsersToProject(List<User> users, Project project);
}
