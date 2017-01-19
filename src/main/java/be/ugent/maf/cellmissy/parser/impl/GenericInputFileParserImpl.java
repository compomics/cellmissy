/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser.impl;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.parser.GenericInputFileParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * This is parsing a single generic input file.
 *
 * @author Paola Masuzzo
 */
@Service("genericInputFileParser")
public class GenericInputFileParserImpl implements GenericInputFileParser {

    private static final Logger LOG = Logger.getLogger(GenericInputFileParser.class);

    @Override
    public List<TimeStep> parseBulkCellFile(File bulkCellFile) throws FileParserException {
        List<TimeStep> timeStepList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(bulkCellFile))) {
            String strRead;
            while ((strRead = bufferedReader.readLine()) != null) {
                //check if the line is the header
                if (strRead.startsWith("Time")) {
                    continue;
                }
                String[] splitarray = strRead.split("\t");
                // check for number of columns in generic file 
                if (splitarray.length == 2) {
                    //create new timestep object and set class members
                    TimeStep timeStep = new TimeStep();
                    try {
                        timeStep.setTimeStepSequence(Integer.parseInt(splitarray[0]));
                        timeStep.setArea(Double.parseDouble(splitarray[1]));
                        //add timestep to the list
                        timeStepList.add(timeStep);
                    } catch (NumberFormatException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new FileParserException("Please make sure each line of your import file contains numbers!");
                    }
                } else {
                    throw new FileParserException("Please make sure your import file has 2 columns!");
                }
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FileParserException(ex.getMessage());
        }
        return timeStepList;
    }

    @Override
    public List<Track> parseTrackFile(File trackFile) throws FileParserException {
        List<Track> trackList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(trackFile))) {
            // we keep a current track in memory
            Track currentTrack = null;
            List<TrackPoint> currentTrackPointList = new ArrayList<>();
            int currentNumber = -1;

            String strRead;
            while ((strRead = bufferedReader.readLine()) != null) {
                // check for the header of the file
                if (strRead.toLowerCase().startsWith("track")) {
                    continue;
                }
                String[] splitarray = strRead.split("\t");
                // check for number of columns in the file 
                if (splitarray.length == 4) {
                    try {
                        // take the current track number from the first column
                        int currentTrackNumber = Integer.parseInt(splitarray[0]);
                        //check if the currentTrackNumber differs from the currentRowNumber
                        if (currentTrackNumber != currentNumber) {

                            if (currentTrack != null) {
                                trackList.add(currentTrack); //add the currentTrack to the track list
                            }
                            // create new track object and set some class members
                            currentTrack = new Track();
                            currentTrack.setTrackNumber(Integer.parseInt(splitarray[0]));
                            currentNumber = currentTrackNumber;
                            currentTrackPointList = new ArrayList<>();
                        }

                        // create trackpoint object and set class members                      
                        TrackPoint trackPoint = new TrackPoint();
                        Double parseDouble = Double.parseDouble(splitarray[1]);
                        trackPoint.setTimeIndex(parseDouble.intValue());
                        trackPoint.setCellRow(Double.parseDouble(splitarray[2]));
                        trackPoint.setCellCol(Double.parseDouble(splitarray[3]));
                        trackPoint.setTrack(currentTrack);
                        //add current track point to currentTrackPointList
                        currentTrackPointList.add(trackPoint);
                        //set the currentTrack trackpoint List
                        currentTrack.setTrackPointList(currentTrackPointList);
                        // set the lenght of the track
                        currentTrack.setTrackLength(currentTrackPointList.size());
                    } catch (NumberFormatException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new FileParserException("Please make sure each line of your import file contains numbers!");
                    }
                } else {
                    throw new FileParserException("Please make sure your import file has 4 columns!");
                }
            }
            // when all the file is read, add the last track to the list
            trackList.add(currentTrack);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FileParserException(ex.getMessage());
        }
        return trackList;
    }

    @Override
    public datastructure parseDoseResponseFile(File doseResponseFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
