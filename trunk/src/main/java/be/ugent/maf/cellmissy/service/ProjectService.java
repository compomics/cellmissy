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
     * @param projectNumber
     * @param projectDirectory
     * @return 
     */
    Project setupProject(int projectNumber, File projectDirectory);
}
