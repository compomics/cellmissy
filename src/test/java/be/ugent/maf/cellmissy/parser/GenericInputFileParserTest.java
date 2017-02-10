/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.exception.FileParserException;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A test unit for the generic input file parser.
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class GenericInputFileParserTest {

    @Autowired
    private GenericInputFileParser genericInputFileParser;

    @Test
    public void testParseBulkCellFile() {
        File genericBulkCellFile = new File(GenericInputFileParserTest.class.getClassLoader().getResource("generic_bulkcell.txt").getPath());
        try {
            List<TimeStep> timeStepList = genericInputFileParser.parseBulkCellFile(genericBulkCellFile);
            Assert.assertEquals(51, timeStepList.size());
        } catch (FileParserException ex) {
            Logger.getLogger(GenericInputFileParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testParseTrackFile() {
        File genericTrackingFile = new File(GenericInputFileParserTest.class.getClassLoader().getResource("generic_tracking.txt").getPath());
        try {
            List<Track> trackList = genericInputFileParser.parseTrackFile(genericTrackingFile);

            Assert.assertEquals(45, trackList.size());
            Track firstTrack = trackList.get(0);
            Assert.assertEquals(30, firstTrack.getTrackPointList().size());

            Track lastTrack = trackList.get(trackList.size() - 1);
            Assert.assertEquals(58, lastTrack.getTrackPointList().size());
        } catch (FileParserException ex) {
            Logger.getLogger(GenericInputFileParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testParseDoseResponseFile() {
        File genericDoseResponseFile = new File(GenericInputFileParserTest.class.getClassLoader().getResource("generic_doseresponse.csv").getPath());
        try {
            List<DoseResponsePair> doseResponseData = genericInputFileParser.parseDoseResponseFile(genericDoseResponseFile);
            assertEquals(4, doseResponseData.size());
            List<Double> controlResponses = doseResponseData.get(0).getResponses();
            List<Double> expected = new ArrayList<>();
            expected.add(1.2);
            expected.add(1.3);
            expected.add(1.0);
            assertEquals(expected, controlResponses);
        } catch (FileParserException ex) {
            Logger.getLogger(GenericInputFileParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
