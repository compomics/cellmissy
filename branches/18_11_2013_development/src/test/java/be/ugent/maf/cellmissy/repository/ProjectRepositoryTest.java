/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.User;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola Masuzzo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectHasUserRepository projectHasUserRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRepository() {
        // use import sql file
        // test find all
        List<Project> projects = projectRepository.findAll();
        Assert.assertTrue(!projects.isEmpty());
        // test count all from generic repository
        // Assert.assertEquals(3, projectRepository.countAll());
        // test other methods from generic repository
        Long projectId = projects.get(0).getProjectid();
        Project found = projectRepository.findById(projectId);
        Assert.assertNotNull(found);
        User user1 = userRepository.findById(1L);
        User user2 = userRepository.findByFullName("user2", "user2");
        // use generic repository
        Project project = new Project();
        project.setProjectNumber(4);
        project.setProjectDescription("This is a test");
        // project has users list
        List<ProjectHasUser> projectHasUsers = new ArrayList<>();
        ProjectHasUser projectHasUser1 = new ProjectHasUser(project, user1);
        projectHasUsers.add(projectHasUser1);
        ProjectHasUser projectHasUser2 = new ProjectHasUser(project, user2);
        projectHasUsers.add(projectHasUser2);
        // set the other side of the relationship
        project.setProjectHasUserList(projectHasUsers);
        user1.setProjectHasUserList(projectHasUsers);
        user2.setProjectHasUserList(projectHasUsers);
        // save the project has users
        for (ProjectHasUser projectHasUser : projectHasUsers) {
            projectHasUserRepository.save(projectHasUser);
        }
        // finally save the project
        projectRepository.save(project);
        // check for the id of the entities
        Assert.assertNotNull(project.getProjectid());
        for (ProjectHasUser projectHasUser : projectHasUsers) {
            Assert.assertNotNull(projectHasUser.getProjectHasUserid());
        }
        // count back the entitites from DB
        Assert.assertEquals(2, projectHasUserRepository.countAll());
        List<ProjectHasUser> findAll = projectHasUserRepository.findAll();
        System.out.println("" + findAll.get(0) + "; " + findAll.get(1));
    }
}
