/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.parser.ObsepFileParserTest;
import java.io.File;
import java.util.List;
import java.util.Map;
import static junit.framework.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class MicroscopeDataServiceTest {

    @Autowired
    private MicroscopeDataService microscopeDataService;

    /**
     * Test MicroscopeDataService: given obsepFile and setupFolder, initialize the service and process Microscope Data (get a map of ImagingType and WellhasImagingType)
     */
    @Test
    public void testMicroscopeDataService() {

        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());
        File setupFolder = new File(ObsepFileParserTest.class.getClassLoader().getResource("position_list_files").getPath());
        Experiment experiment = new Experiment();
        experiment.setObsepFile(obsepFile);
        experiment.setSetupFolder(setupFolder);
        
        microscopeDataService.init(experiment);
        Map<ImagingType, List<WellHasImagingType>> imagingTypeMap = microscopeDataService.processMicroscopeData();

        //two position lists are parsed, map needs to contain 2 keys
        assertTrue(imagingTypeMap.size()==2);
    }
}
