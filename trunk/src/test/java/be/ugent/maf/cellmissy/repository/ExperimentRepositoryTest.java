/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.AssayMedium;
import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.parser.CellMiaFileParser;
import be.ugent.maf.cellmissy.parser.CellMiaFileParserTest;
import be.ugent.maf.cellmissy.service.EcmService;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
 * This class is testing the insertion into db of an experiment.
 * The sample experiment has 2 plate conditions, each one with 3 wells (technical replicates).
 * well(1,1) and well(3,3) were imaged twice. 
 * @author Paola Masuzzo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mySpringXMLConfig.xml", "classpath:myTestSpringXMLConfig.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ExperimentRepositoryTest {

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PlateFormatRepository plateFormatRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private MagnificationRepository magnificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CellLineRepository cellLineRepository;
    @Autowired
    private AssayRepository assayRepository;
    @Autowired
    private EcmService ecmService;
    @Autowired
    private CellMiaFileParser cellMiaFileParser;

    @Test
    public void testRepository() {
        // first parse the files to get collection of tracks and time steps
        File trackingFile = new File(CellMiaFileParserTest.class.getClassLoader().getResource("tracking.txt").getPath());
        List<Track> trackList = cellMiaFileParser.parseTrackingFile(trackingFile);
        File bulkCellFile = new File(CellMiaFileParserTest.class.getClassLoader().getResource("bulkcell.txt").getPath());
        List<TimeStep> timeStepList = cellMiaFileParser.parseBulkCellFile(bulkCellFile);
        // look for project
        List<Project> projects = projectRepository.findAll();
        Project project = projects.get(0);
        // create a new experiment istance and set its class members
        Experiment experiment = new Experiment();
        experiment.setProject(project);
        experiment.setExperimentNumber(001);
        experiment.setPurpose("This is to test insertion to DB");
        // Imaging types: 2, BF and PC
        ImagingType firstImagingType = new ImagingType();
        firstImagingType.setName("BF");

        // Algorithm: only 1
        Algorithm algorithm = new Algorithm();
        algorithm.setAlgorithmName("Test Algo");
        // plate format: 6
        experiment.setPlateFormat(plateFormatRepository.findByFormat(6));
        // 2 plate conditions, 3 wells for each condition
        PlateCondition firstPlateCondition = new PlateCondition();
        // first row of wells for condition 
        Well well1 = new Well(1, 1);
        well1.setPlateCondition(firstPlateCondition);
        Well well2 = new Well(2, 1);
        well2.setPlateCondition(firstPlateCondition);
        Well well3 = new Well(3, 1);
        well3.setPlateCondition(firstPlateCondition);
        List<Well> wells = new ArrayList<>();
        wells.add(well1);
        wells.add(well2);
        wells.add(well3);
        List<WellHasImagingType> wellHasImagingTypes;
        List<WellHasImagingType> globalWellHasImagingTypes = new ArrayList<>();

        for (Well well : wells) {
            wellHasImagingTypes = new ArrayList<>();
            WellHasImagingType wellHasImagingType = new WellHasImagingType(well, firstImagingType, algorithm);
            wellHasImagingType.setTimeStepCollection(timeStepList);
            for (TimeStep timeStep : timeStepList) {
                timeStep.setWellHasImagingType(wellHasImagingType);
            }
            wellHasImagingType.setTrackCollection(trackList);
            for (Track track : trackList) {
                track.setWellHasImagingType(wellHasImagingType);
            }
            wellHasImagingTypes.add(wellHasImagingType);
            globalWellHasImagingTypes.add(wellHasImagingType);
            well.setWellHasImagingTypeCollection(wellHasImagingTypes);
        }

        firstPlateCondition.setWellCollection(wells);
        // assay, assay medium, ecm, cell line
        firstPlateCondition.setAssay(assayRepository.findByMatrixDimensionName("2D").get(0));
        firstPlateCondition.setAssayMedium(new AssayMedium("medium1", "serum1", 1.0, 5.0));
        EcmComposition ecmComposition = new EcmComposition();
        ecmComposition.setMatrixDimension(firstPlateCondition.getAssay().getMatrixDimension());
        ecmComposition.setCompositionType("composition test");
        ecmService.saveEcmComposition(ecmComposition);

        Ecm ecm = new Ecm(1.0, 2.0, "12 h", "37 C", "", "", null, ecmComposition, null, "mg/ml", "");
        ecmService.save(ecm);
        firstPlateCondition.setEcm(ecm);
        firstPlateCondition.setCellLine(cellLineRepository.findAll().get(0));
        //firstPlateCondition.setMatrixDimension(firstPlateCondition.getAssay().getMatrixDimension());
        firstPlateCondition.setExperiment(experiment);

        PlateCondition secondPlateCondition = new PlateCondition();
        // second row of wells for second condition
        well1 = new Well(1, 2);
        well1.setPlateCondition(secondPlateCondition);
        well2 = new Well(2, 2);
        well2.setPlateCondition(secondPlateCondition);
        well3 = new Well(3, 2);
        well3.setPlateCondition(secondPlateCondition);
        wells = new ArrayList<>();
        wells.add(well1);
        wells.add(well2);
        wells.add(well3);

        for (Well well : wells) {
            wellHasImagingTypes = new ArrayList<>();
            WellHasImagingType wellHasImagingType = new WellHasImagingType(well, firstImagingType, algorithm);
            wellHasImagingType.setTimeStepCollection(timeStepList);
            for (TimeStep timeStep : timeStepList) {
                timeStep.setWellHasImagingType(wellHasImagingType);
            }
            wellHasImagingType.setTrackCollection(trackList);
            for (Track track : trackList) {
                track.setWellHasImagingType(wellHasImagingType);
            }
            wellHasImagingTypes.add(wellHasImagingType);
            globalWellHasImagingTypes.add(wellHasImagingType);
            well.setWellHasImagingTypeCollection(wellHasImagingTypes);
        }

        firstImagingType.setWellHasImagingTypeCollection(globalWellHasImagingTypes);
        algorithm.setWellHasImagingTypeCollection(globalWellHasImagingTypes);

        secondPlateCondition.setWellCollection(wells);
        // assay, assay medium, ecm, cell line
        secondPlateCondition.setAssay(assayRepository.findByMatrixDimensionName("3D").get(0));
        secondPlateCondition.setAssayMedium(new AssayMedium("medium2", "serum2", 2.0, 10.0));
        secondPlateCondition.setEcm(ecmService.findAll().get(0));
        secondPlateCondition.setCellLine(cellLineRepository.findAll().get(1));
        //secondPlateCondition.setMatrixDimension(firstPlateCondition.getAssay().getMatrixDimension());
        secondPlateCondition.setExperiment(experiment);

        List<PlateCondition> plateConditions = new ArrayList<>();
        plateConditions.add(firstPlateCondition);
        plateConditions.add(secondPlateCondition);
        experiment.setPlateConditionCollection(plateConditions);
        experiment.setExperimentStatus(ExperimentStatus.IN_PROGRESS);

        //instrument, magnification and user fields
        experiment.setInstrument(instrumentRepository.findAll().get(0));
        experiment.setMagnification(magnificationRepository.findAll().get(0));
        experiment.setUser(userRepository.findByFirstName("user1"));

        Experiment savedExperiment = experimentRepository.update(experiment);
        Assert.assertNotNull(savedExperiment.getExperimentid());
        Assert.assertEquals(2, savedExperiment.getPlateConditionCollection().size());
        Collection<PlateCondition> plateConditionCollection = savedExperiment.getPlateConditionCollection();
        for (PlateCondition plateCondition : plateConditionCollection) {
            Assert.assertEquals(3, plateCondition.getWellCollection().size());
            for (Well well : plateCondition.getWellCollection()) {
                Assert.assertEquals(1, well.getWellHasImagingTypeCollection().size());
            }
        }
        // test plate format
        Assert.assertEquals(plateFormatRepository.findByFormat(6), savedExperiment.getPlateFormat());
        // collection of the algorithm and the imaging type
        Assert.assertEquals(6, algorithm.getWellHasImagingTypeCollection().size());
        Assert.assertEquals(6, firstImagingType.getWellHasImagingTypeCollection().size());
    }
}
