/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.exception.FileParserException;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.*;
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
            assertEquals(51, timeStepList.size());
        } catch (FileParserException ex) {
            Logger.getLogger(GenericInputFileParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testParseTrackFile() {
        File genericTrackingFile = new File(GenericInputFileParserTest.class.getClassLoader().getResource("generic_tracking.txt").getPath());
        try {
            List<Track> trackList = genericInputFileParser.parseTrackFile(genericTrackingFile);

            assertEquals(45, trackList.size());
            Track firstTrack = trackList.get(0);
            assertEquals(30, firstTrack.getTrackPointList().size());

            Track lastTrack = trackList.get(trackList.size() - 1);
            assertEquals(58, lastTrack.getTrackPointList().size());
        } catch (FileParserException ex) {
            Logger.getLogger(GenericInputFileParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
