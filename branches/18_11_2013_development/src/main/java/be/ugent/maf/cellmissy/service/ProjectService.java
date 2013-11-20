/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Project;
import java.io.File;

/**
 *
 * @author Paola
 */
public interface ProjectService extends GenericService<Project, Long> {

    /**
     * this method set up a new project
     *
     * @param projectNumber
     * @param description
     * @param projectDirectory
     * @return
     */
    public Project setupProject(int projectNumber, String description, File projectDirectory);

    /**
     * 
     * @param entity
     */
    public void saveProjectUsers(Project entity);
}
