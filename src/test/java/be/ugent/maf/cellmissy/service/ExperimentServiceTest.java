/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Project;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Paola Masuzzo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class ExperimentServiceTest {

    @Autowired
    private ExperimentService experimentService;

    /**
     * Test Creation of Folder Structure for experiment Object
     */
    @Test
    public void testFolderStructure() {

        Experiment experiment = new Experiment();
        File mainDirectoryTest = new File(PropertiesConfigurationHolder.getInstance().getString("mainDirectory"));
        experimentService.init(mainDirectoryTest);
        Project newProject = new Project();
        newProject.setProjectDescription("Test");
        newProject.setProjectNumber(001);
        experiment.setProject(newProject);
        experiment.setExperimentNumber(1);
        
        // this is first checking if folders already exist, if not, create them
        experimentService.createFolderStructure(experiment);
    }
}
