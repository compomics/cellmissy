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
    
    Project setupProject(String projectNumber, File projectDirectory);
    
}
