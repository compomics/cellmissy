/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.repository.ProjectHasUserRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Repository("projectHasUserRepository")
public class ProjectHasUserJpaRepository extends GenericJpaRepository<ProjectHasUser, Long> implements ProjectHasUserRepository {
}
