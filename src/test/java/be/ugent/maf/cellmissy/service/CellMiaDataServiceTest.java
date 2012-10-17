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
import be.ugent.maf.cellmissy.parser.ObsepFileParserTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class CellMiaDataServiceTest {

    @Autowired
    private CellMiaDataService cellMiaDataService;

    /**
     * Test CellMiaDataService: results from CellMia are assigned to WellHasImagingType objects
     */
    @Test
    public void testCellMiaDataService() {

        File miaFolder = new File(PropertiesConfigurationHolder.getInstance().getString("cellMiaFolder"));

        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());
        File setupFolder = new File(ObsepFileParserTest.class.getClassLoader().getResource("position_list_files").getPath());
        Experiment experiment = new Experiment();
        experiment.setObsepFile(obsepFile);
        experiment.setSetupFolder(setupFolder);
        experiment.setMiaFolder(miaFolder);

        cellMiaDataService.init(experiment);
        cellMiaDataService.getMicroscopeDataService().init(experiment);

        Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> processCellMiaData = cellMiaDataService.processCellMiaData();

        for (Algorithm algorithm : processCellMiaData.keySet()) {
            Map<ImagingType, List<WellHasImagingType>> get = processCellMiaData.get(algorithm);
            for (ImagingType imagingType : get.keySet()) {
                List<WellHasImagingType> list = get.get(imagingType);
                for (WellHasImagingType wellHasImagingType : list) {
                    assertTrue(!wellHasImagingType.getTrackCollection().isEmpty());
                    assertTrue(!wellHasImagingType.getTimeStepCollection().isEmpty());
                }
            }
        }
    }
}
