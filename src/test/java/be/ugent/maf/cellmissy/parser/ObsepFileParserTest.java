package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.ImagingType;
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
public class ObsepFileParserTest {
    
    @Autowired
    private ObsepFileParser obsepFileParser;

    /**
     * Test ObsepFileParser class: Getting experiment info together with Imaging Type, Position List Names - map
     */
    @Test
    public void testObsepFileParser() {
        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());
        
        obsepFileParser.parseObsepFile(obsepFile);
        List<Double> info = obsepFileParser.getExperimentInfo();
        Map<ImagingType, String> map = obsepFileParser.mapImagingTypetoPositionList();
        
        assertTrue(!map.isEmpty());

        //2 imaging types in sample file
        assertTrue(map.keySet().size() == 2);

        //time frames, interval and duration
        assertTrue(!info.isEmpty());
        assertTrue(info.size() == 3);
    }
}
