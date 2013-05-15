/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Project;
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
@ContextConfiguration("classpath:myTestSpringXMLConfig.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProjectRepositoryTest {

    @Autowired
    ProjectRepository projectRepository;

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
//        String projectDescription = projects.get(1).getProjectDescription();
//        Assert.assertTrue(projectDescription.equals("test2"));

        // use generic repository
        Project project = new Project();
        project.setProjectNumber(4);
        project.setProjectDescription("This is a test");
        projectRepository.save(project);
        Assert.assertNotNull(project.getProjectid());
    }
}
