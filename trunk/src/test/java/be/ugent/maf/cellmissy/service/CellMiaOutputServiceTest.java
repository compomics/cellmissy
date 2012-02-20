/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.parser.ObsepFileParserTest;
import be.ugent.maf.cellmissy.parser.PositionListParser;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl;
import be.ugent.maf.cellmissy.parser.impl.PositionListParserImpl;
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
@ContextConfiguration("classpath:mySpringTestXMLConfig.xml")
public class CellMiaOutputServiceTest {

    @Autowired
    private CellMiaOutputService cellMiaOutputService;
    private ObsepFileParser obsepFileParser = new ObsepFileParserImpl();
    private PositionListParser positionListParser = new PositionListParserImpl();

    @Test
    public void testCellMiaOutputService() {

        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());
        obsepFileParser.parseObsepFile(obsepFile);
        Map<ImagingType, String> mapImagingTypetoPosList = obsepFileParser.mapImagingTypetoPosList();

        File microscopeFolder = new File("M:\\CM\\CM_P003_TES_Project_3\\CM_P003_E001\\CM_P003_E001_raw");
        Map<ImagingType, List<WellHasImagingType>> parsePositionLists = positionListParser.parsePositionLists(mapImagingTypetoPosList, microscopeFolder);
        File cellMiaFolder = new File("M:\\CM\\CM_P003_TES_Project_3\\CM_P003_E001\\CM_P003_E001_MIA\\CM_P003_E001_MIA_algo-1\\18-11-11 algo 1\\batch--8T5H38DT_DocumentFiles");

        cellMiaOutputService.processCellMiaOutput(parsePositionLists, cellMiaFolder);
        
        assertTrue(!mapImagingTypetoPosList.isEmpty());
    }
}
