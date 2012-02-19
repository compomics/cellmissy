/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl;
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
public class PositionListParserTest {

    @Autowired
    private PositionListParser positionListParser;
    private ObsepFileParser obsepFileParser = new ObsepFileParserImpl();

    @Test
    public void testPositionListParser() {

        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());
        obsepFileParser.parseObsepFile(obsepFile);
        Map<ImagingType, String> map = obsepFileParser.mapImagingTypetoPosList();

        File microscopeFolder = new File("M:\\CM\\CM_P003_TES_Project_3\\CM_P003_E001\\CM_P003_E001_raw");
        Map<ImagingType, List<WellHasImagingType>> map2 = positionListParser.parsePositionLists(map, microscopeFolder);

        assertTrue(!map2.isEmpty());
    }
}
