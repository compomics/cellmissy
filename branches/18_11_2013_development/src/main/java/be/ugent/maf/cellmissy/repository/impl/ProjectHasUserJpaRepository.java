/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.repository.ProjectHasUserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Repository("projectHasUserRepository")
public class ProjectHasUserJpaRepository extends GenericJpaRepository<ProjectHasUser, Long> implements ProjectHasUserRepository {

    @Override
    public List<Project> findProjectsByUserid(Long userid) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("ProjectHasUser.findByUserid");
        byNameQuery.setParameter("userid", userid);
        List<ProjectHasUser> projectHasUsers = byNameQuery.getResultList();
        List<Project> resultList = new ArrayList<>();
        if (projectHasUsers != null) {
            if (!projectHasUsers.isEmpty()) {
                for (ProjectHasUser projectHasUser : projectHasUsers) {
                    Project project = projectHasUser.getProject();
                    resultList.add(project);
                }
            }
        }
        return resultList;
    }

    @Override
    public List<User> findUsersByProjectid(Long projectid) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("ProjectHasUser.findByProjectid");
        byNameQuery.setParameter("projectid", projectid);
        List<ProjectHasUser> projectHasUsers = byNameQuery.getResultList();
        List<User> resultList = new ArrayList<>();
        if (projectHasUsers != null) {
            if (!projectHasUsers.isEmpty()) {
                for (ProjectHasUser projectHasUser : projectHasUsers) {
                    User user = projectHasUser.getUser();
                    resultList.add(user);
                }
            }
        }
        return resultList;
    }
}
