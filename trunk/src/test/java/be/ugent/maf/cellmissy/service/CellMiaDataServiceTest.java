/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.exception.CellMiaDataLoadingException;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.exception.PositionListMismatchException;
import be.ugent.maf.cellmissy.parser.ObsepFileParserTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class CellMiaDataServiceTest {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CellMiaDataServiceTest.class);
    @Autowired
    private CellMiaDataService cellMiaDataService;

    /**
     * Test CellMiaDataService: results from CellMia are assigned to WellHasImagingType objects
     */
    @Test
    public void testCellMiaDataService() {

        //cellmia folder -- cell_missy.properties file
        File miaFolder = new File(PropertiesConfigurationHolder.getInstance().getString("cellMiaFolder"));
        //obsep file and setup folder -- test resources
        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());
        File setupFolder = new File(ObsepFileParserTest.class.getClassLoader().getResource("position_list_files").getPath());
        //experiment object 
        Experiment experiment = new Experiment();
        experiment.setObsepFile(obsepFile);
        experiment.setSetupFolder(setupFolder);
        experiment.setMiaFolder(miaFolder);

        // init the service: set the experiment
        cellMiaDataService.init(experiment);
        // init also the microscope data service
        cellMiaDataService.getMicroscopeDataService().init(experiment);

        // get the map
        Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> processCellMiaData = new HashMap<>();
        try {
            processCellMiaData = cellMiaDataService.processCellMiaData();
            // iterate through the algorithms found
            for (Algorithm algorithm : processCellMiaData.keySet()) {
                // get each map for each algorithm found
                Map<ImagingType, List<WellHasImagingType>> rawDataMap = processCellMiaData.get(algorithm);
                // iterate through the imaging types
//            for (ImagingType imagingType : rawDataMap.keySet()) {
//                List<WellHasImagingType> list = rawDataMap.get(imagingType);
//                for (WellHasImagingType wellHasImagingType : list) {
//                    assertTrue(!wellHasImagingType.getTrackCollection().isEmpty());
//                    System.out.println("size tracks: " + wellHasImagingType.getTrackCollection().size());
//                    assertTrue(!wellHasImagingType.getTimeStepCollection().isEmpty());
//                    System.out.println("size steps: " + wellHasImagingType.getTimeStepCollection().size());
//                }
//            }
                assertEquals(2, rawDataMap.size());
            }
        } catch (FileParserException | PositionListMismatchException | CellMiaDataLoadingException ex) {
            LOG.error(ex.getMessage());
        }

    }
}
