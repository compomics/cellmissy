/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.xml;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.parser.XMLParser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;
import org.junit.Assert;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class XmlFileParserTest {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XmlFileParserTest.class);
    @Autowired
    private XMLParser xMLParser;

    /**
     * Test a valid XML file
     */
    @Test
    public void testValidXml() {

        File xmlFile = new File(XmlFileParserTest.class.getClassLoader().getResource("valid_file.xml").getPath());
        Experiment experiment = null;
        try {
            experiment = xMLParser.unmarshal(Experiment.class, xmlFile);
            // get validation messages
            List<String> validationErrorMesage = xMLParser.getValidationErrorMesage();
            Assert.assertTrue(validationErrorMesage.isEmpty());
            Assert.assertTrue(experiment != null);
            List<PlateCondition> plateConditionList = experiment.getPlateConditionList();
            Assert.assertEquals(6, plateConditionList.size());
        } catch (JAXBException | SAXException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Test a not valid XML file
     */
    @Test
    public void testNotValidXml() {

        File xmlFile = new File(XmlFileParserTest.class.getClassLoader().getResource("not_valid_file.xml").getPath());
        try {
            Experiment experiment = xMLParser.unmarshal(Experiment.class, xmlFile);
            // get validation messages
            List<String> validationErrorMesage = xMLParser.getValidationErrorMesage();
            Assert.assertTrue(!validationErrorMesage.isEmpty());
        } catch (JAXBException | SAXException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
