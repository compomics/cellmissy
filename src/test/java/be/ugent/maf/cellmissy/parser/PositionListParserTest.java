/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class PositionListParserTest {

    @Autowired
    private PositionListParser positionListParser;
    @Autowired
    private ObsepFileParser obsepFileParser;

    @Test
    public void testPositionListParser() throws IOException {

        Resource obsepResource = new ClassPathResource("gffp.obsep");
        obsepFileParser.parseObsepFile(obsepResource.getFile());
        Map<ImagingType, String> imagingTypePositionListMap = obsepFileParser.mapImagingTypetoPositionList();

        File microscopeFolder = new File(ObsepFileParserTest.class.getClassLoader().getResource("position_list_files").getPath());
        Map<ImagingType, List<WellHasImagingType>> imagingTypeListOfWellHasImagingTypeMap = positionListParser.parsePositionList(imagingTypePositionListMap, microscopeFolder);

        assertTrue(!imagingTypeListOfWellHasImagingTypeMap.isEmpty());
    }
}
