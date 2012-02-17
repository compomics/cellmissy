/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.service;

import be.ugent.maf.limsdesktop.entity.Project;
import java.io.File;

/**
 *
 * @author Paola
 */
public interface ProjectService extends GenericService<Project, Long> {
    
    Project setupProject(String projectNumber, File projectDirectory);
    
}
