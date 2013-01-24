/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.parser.CellMiaFileParser;
import be.ugent.maf.cellmissy.parser.CellMiaFileParserTest;
import java.io.File;
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
@ContextConfiguration(locations = {"classpath:mySpringXMLConfig.xml", "classpath:myTestSpringXMLConfig.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class WellHasImagingTypeRepositoryTest {

    @Autowired
    private WellHasImagingTypeRepository wellHasImagingTypeRepository;
    @Autowired
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private ImagingTypeRepository imagingTypeRepository;
    @Autowired
    private WellRepository wellRepository;
    @Autowired
    private CellMiaFileParser cellMiaFileParser;

    @Test
    public void testRepository() {
        // first parse the files to get collection of tracks and time steps
        File trackingFile = new File(CellMiaFileParserTest.class.getClassLoader().getResource("tracking.txt").getPath());
        List<Track> trackList = cellMiaFileParser.parseTrackingFile(trackingFile);
        File bulkCellFile = new File(CellMiaFileParserTest.class.getClassLoader().getResource("bulkcell.txt").getPath());
        List<TimeStep> timeStepList = cellMiaFileParser.parseBulkCellFile(bulkCellFile);

        // create new wellhasImagingType object
        WellHasImagingType wellHasImagingType = new WellHasImagingType();
        List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
        // new algorithm
        Algorithm algorithm = new Algorithm();
        algorithm.setAlgorithmName("Algo to test");
        wellHasImagingType.setAlgorithm(algorithm);
        // new imaging type
        ImagingType imagingType = new ImagingType();
        imagingType.setName("imaging to test");
        wellHasImagingType.setImagingType(imagingType);
        // new well (column number, row number)
        Well well = new Well(2, 2);
        wellHasImagingType.setWell(well);
        wellHasImagingTypes.add(wellHasImagingType);
        // set relations
        well.setWellHasImagingTypeCollection(wellHasImagingTypes);
        algorithm.setWellHasImagingTypeCollection(wellHasImagingTypes);
        imagingType.setWellHasImagingTypeCollection(wellHasImagingTypes);
        // set relations
        for (TimeStep timeStep : timeStepList) {
            timeStep.setWellHasImagingType(wellHasImagingType);
        }

        for (Track track : trackList) {
            track.setWellHasImagingType(wellHasImagingType);
        }

        // set time steps and tracks
        wellHasImagingType.setTimeStepCollection(timeStepList);
        wellHasImagingType.setTrackCollection(trackList);
        // persist wellhasImagingType
        WellHasImagingType saved = wellHasImagingTypeRepository.save(wellHasImagingType);
        List<WellHasImagingType> findAll = wellHasImagingTypeRepository.findAll();
        Assert.assertTrue(!findAll.isEmpty());

        Assert.assertNotNull(saved.getWellHasImagingTypeid());
        // test methods of the generic repository for this entity
        Assert.assertNotNull(wellHasImagingTypeRepository.findById(saved.getWellHasImagingTypeid()));
        Assert.assertNotNull(wellHasImagingTypeRepository.findByExample(saved));
        Assert.assertEquals(1, wellHasImagingTypeRepository.countAll());
        // check id generation for algorithm, imaging type and well too
        List<Algorithm> allAlgos = algorithmRepository.findAll();
        Assert.assertNotNull(allAlgos.get(0).getAlgorithmid());
        List<ImagingType> allImaging = imagingTypeRepository.findAll();
        Assert.assertNotNull(allImaging.get(0).getImagingTypeid());
        List<Well> allWells = wellRepository.findAll();
        Assert.assertNotNull(allWells.get(0).getWellid());
        // test named query
        WellHasImagingType findByWellIdAlgoIdAndImagingTypeId = wellHasImagingTypeRepository.findByWellIdAlgoIdAndImagingTypeId(allWells.get(0).getWellid(), allAlgos.get(0).getAlgorithmid(), allImaging.get(0).getImagingTypeid());
        Assert.assertNotNull(findByWellIdAlgoIdAndImagingTypeId);
        // remove object from  db
        wellHasImagingTypeRepository.delete(saved);
        Assert.assertEquals(0, wellHasImagingTypeRepository.countAll());
    }
}
