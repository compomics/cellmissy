/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import java.util.List;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface ProjectHasUserRepository extends GenericRepository<ProjectHasUser, Long> {

    public List<Project> findProjectsByUserid(Long userid);
}
