package be.ugent.maf.cellmissy.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.*;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: niels
 * Date: 17/02/12
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringTestXMLConfig.xml")
public class ObsepFileParserTest {

    @Autowired
    private ObsepFileParser obsepFileParser;

    @Test
    public void testObsepFileParser(){
        File obsepFile = new File(ObsepFileParserTest.class.getClassLoader().getResource("gffp.obsep").getPath());

        obsepFileParser.parseObsepFile(obsepFile);
        List<Double> info = obsepFileParser.getExperimentInfo();

        assertTrue(!info.isEmpty());
    }

}
