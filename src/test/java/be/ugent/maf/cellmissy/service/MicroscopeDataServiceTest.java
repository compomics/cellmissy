/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

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
@ContextConfiguration("classpath:mySpringTestXMLConfig.xml")
public class MicroscopeDataServiceTest {

    @Autowired
    private MicroscopeDataService microscopeDataService;

    @Test
    public void testMicroscopeDataService() {
        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());
        File microscopeFolder = new File(ObsepFileParserTest.class.getClassLoader().getResource("position_list_files").getPath());
       // microscopeDataService.init(microscopeFolder, obsepFile);
        Map<ImagingType, List<WellHasImagingType>> map = microscopeDataService.processMicroscopeData();
        
        assertTrue(!map.isEmpty());
    }
}
