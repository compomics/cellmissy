/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser.impl;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.parser.CellMiaFileParser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Paola
 */
@Service("cellMiaFileParser")
public class CellMiaFileParserImpl implements CellMiaFileParser {

    private static final Logger LOG = Logger.getLogger(CellMiaFileParser.class);

    @Override
    public List<TimeStep> parseBulkCellFile(File bulkCellFile) {
        List<TimeStep> timeStepList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(bulkCellFile));

            String strRead;
            while ((strRead = bufferedReader.readLine()) != null) {
                //check if the line is the header
                if (strRead.startsWith("Time")) {
                    continue;
                }
                String[] splitarray = strRead.split("\t");

                //create new timestep object and set class members
                TimeStep timeStep = new TimeStep();
                timeStep.setTimeStepSequence(Integer.parseInt(splitarray[0]));
                timeStep.setArea(Double.parseDouble(splitarray[1]));
                timeStep.setCentroidX(Double.parseDouble(splitarray[2]));
                timeStep.setCentroidY(Double.parseDouble(splitarray[3]));
                timeStep.setEccentricity(Double.parseDouble(splitarray[4]));
                timeStep.setMajorAxis(Double.parseDouble(splitarray[5]));
                timeStep.setMinorAxis(Double.parseDouble(splitarray[6]));

                //add timestep to the list
                timeStepList.add(timeStep);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long currentTimeMillis1 = System.currentTimeMillis();
        LOG.debug("Time to parse a Tracking File: " + (currentTimeMillis1 - currentTimeMillis) + " ms");
        return timeStepList;
    }

    @Override
    public List<Track> parseTrackingFile(File trackingFile) {
        List<Track> trackList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(trackingFile));

            Track currentTrack = null;
            List<TrackPoint> currentTrackPointList = new ArrayList<>();
            int currentTrackID = 0;

            String strRead;
            while ((strRead = bufferedReader.readLine()) != null) {
                //check if the line is the header
                if (strRead.startsWith("ID")) {
                    continue;
                }

                String[] splitarray = strRead.split("\t");

                int currentPointID = Integer.parseInt(splitarray[0]);

                //check if the currentPointID differs from the currentTrackID
                if (currentPointID != currentTrackID) {

                    if (currentTrack != null) {
                        trackList.add(currentTrack); //add the currentTrack to the track list

                    }

                    currentTrack = new Track();
                    currentTrack.setTrackNumber(Integer.parseInt(splitarray[0]));
                    currentTrack.setTrackLength(Integer.parseInt(splitarray[8]));

                    currentTrackID = currentPointID;
                    currentTrackPointList = new ArrayList<>();
                }

                // create trackpoint object and set class members
                TrackPoint trackPoint = getTrackPoint(splitarray);

                //set the currentTrack trackpoint collection
                currentTrack.setTrackPointCollection(currentTrackPointList);

                trackPoint.setTrack(currentTrack);
                //add current track point to currentTrackPointList
                currentTrackPointList.add(trackPoint);


            }
            // when all the file is read, add the last track to the list
            trackList.add(currentTrack);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long currentTimeMillis1 = System.currentTimeMillis();
        LOG.debug("Time to parse a Bulk Cell File: " + (currentTimeMillis1 - currentTimeMillis) + " ms");

        return trackList;
    }

    /**
     * this method creates a TrackPoint object and set its class members
     * @param splitarray
     * @return a TrackPoint object
     */
    private TrackPoint getTrackPoint(String[] splitarray) {

        TrackPoint trackpoint = new TrackPoint();
        trackpoint.setTimeIndex(Integer.parseInt(splitarray[1]));
        trackpoint.setCellRow(Double.parseDouble(splitarray[2]));
        trackpoint.setCellCol(Double.parseDouble(splitarray[3]));

        // check if columns in the file are empty
        if (splitarray[4].length() > 0) {
            trackpoint.setVelocityPixels(Double.parseDouble(splitarray[4]));
        }

        if (splitarray[5].length() > 0) {
            trackpoint.setAngle(Double.parseDouble(splitarray[5]));
        }

        if (splitarray[6].length() > 0) {
            trackpoint.setAngleDelta(Double.parseDouble(splitarray[6]));
        }

        if (splitarray[7].length() > 0) {
            trackpoint.setRelativeAngle(Double.parseDouble(splitarray[7]));
        }

        if (splitarray[9].length() > 0) {
            trackpoint.setMotionConsistency(Double.parseDouble(splitarray[9]));
        }

        if (splitarray[10].length() > 0) {
            trackpoint.setCumulatedDistance(Double.parseDouble(splitarray[10]));
        }

        trackpoint.setDistance(Double.parseDouble(splitarray[11]));

        return trackpoint;
    }
}
