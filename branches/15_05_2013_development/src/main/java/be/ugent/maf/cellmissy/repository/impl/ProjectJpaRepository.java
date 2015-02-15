/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.repository.ProjectRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("projectRepository")
class ProjectJpaRepository extends GenericJpaRepository<Project, Long> implements ProjectRepository {
}
