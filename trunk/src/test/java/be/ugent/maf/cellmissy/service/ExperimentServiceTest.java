/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
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
    @Autowired
    private WellService wellService;

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
        experiment.setExperimentNumber(45);

        // this is first checking if folders already exist, if not, create them
        experimentService.createFolderStructure(experiment);
    }

    @Test
    public void testDataLoading() {
        File mainDirectoryTest = new File(PropertiesConfigurationHolder.getInstance().getString("mainDirectory"));
        // initialize service
        experimentService.init(mainDirectoryTest);
        List<Experiment> experimentsFound = experimentService.findExperimentsByProjectIdAndStatus(1L, ExperimentStatus.IN_PROGRESS);
        Assert.assertEquals(1, experimentsFound.size());
        // get the service
        Experiment experiment = experimentsFound.get(0);
        // load folders
        experimentService.loadFolderStructure(experiment);
        // get microscope file
        File obsepFile = experiment.getObsepFile();
        Assert.assertNotNull(obsepFile);
        // initialize service and get imaging types
        wellService.init(experiment);
        List<ImagingType> imagingTypes = wellService.getImagingTypes();
        Assert.assertEquals(1, imagingTypes.size());
        // get global map
        Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> map = wellService.getMap();
        Set<Map.Entry<Algorithm, Map<ImagingType, List<WellHasImagingType>>>> entrySet = map.entrySet();
        Assert.assertEquals(1, entrySet.size());
        // set motility data
        Iterator<Map.Entry<Algorithm, Map<ImagingType, List<WellHasImagingType>>>> iterator = entrySet.iterator();
        List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Algorithm, Map<ImagingType, List<WellHasImagingType>>> next = iterator.next();
            Map<ImagingType, List<WellHasImagingType>> submMap = next.getValue();
            for (ImagingType imagingType : submMap.keySet()) {
                List<WellHasImagingType> get = submMap.get(imagingType);
                wellHasImagingTypes.addAll(get);
            }
        }

        List<Well> wells = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionCollection()) {
            wells.addAll(plateCondition.getWellCollection());
        }

        for (int i = 0; i < wells.size(); i++) {
            List<WellHasImagingType> wellHasImagingTypesList = new ArrayList();
            Well well = wells.get(i);
            WellHasImagingType wellHasImagingType = wellHasImagingTypes.get(i);
            wellHasImagingType.setWell(well);
            wellHasImagingTypesList.add(wellHasImagingType);
            well.setWellHasImagingTypeCollection(wellHasImagingTypesList);
        }

        experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
        experimentService.saveMotilityDataForExperiment(experiment);
        Assert.assertEquals(ExperimentStatus.PERFORMED, experiment.getExperimentStatus());
        Assert.assertNotNull(experiment.getExperimentid());
    }
}
