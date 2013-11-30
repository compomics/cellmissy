/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.repository.ProjectRepository;
import be.ugent.maf.cellmissy.repository.UserRepository;
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
 * Unit Test for the Project Service
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testDeleteUsersFromProject() {
        // get the project with id = 1
        Project project = projectRepository.findById(1L);
        // get the user with id = 1
        User user1 = userRepository.findById(1L);
        List<User> usersToDelete = new ArrayList<>();
        usersToDelete.add(user1);
        // we delete here only one user from the project
        projectService.deleteUsersFromProject(usersToDelete, project);
        // the project id must still be not null
        Assert.assertNotNull(project.getProjectid());
        // now the project must have only one user
        List<ProjectHasUser> projectHasUserList = project.getProjectHasUserList();
        Assert.assertEquals(1, projectHasUserList.size());
    }

    @Test
    public void testAddUsersToProject() {
        // get the project with id = 1
        Project project = projectRepository.findById(1L);
        // create 2 new users, one ADMIN and one STANDARD
        User user3 = new User("user3", "user3", Role.ADMIN_USER, "password3", "user3@email.com");
        User user4 = new User("user4", "user4", Role.STANDARD_USER, "password4", "user4@email.com");
        List<User> usersToAdd = new ArrayList<>();
        usersToAdd.add(user3);
        usersToAdd.add(user4);
        projectService.addUsersToProject(usersToAdd, project);
        // now the project has 4 users
        List<ProjectHasUser> projectHasUserList = project.getProjectHasUserList();
        Assert.assertEquals(4, projectHasUserList.size());
    }
}
