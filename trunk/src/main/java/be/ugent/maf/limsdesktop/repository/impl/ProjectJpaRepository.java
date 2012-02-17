/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.repository.impl;

import be.ugent.maf.limsdesktop.entity.Project;
import be.ugent.maf.limsdesktop.repository.ProjectRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("projectRepository")
public class ProjectJpaRepository extends GenericJpaRepository<Project, Long> implements ProjectRepository {
    
}
