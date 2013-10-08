/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.TrackOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

/**
 * Implementation for the Track Operator interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("trackOperator")
public class TrackOperatorImpl implements TrackOperator {

    @Override
    public void generateTimeIndexes(TrackDataHolder trackDataHolder) {
        Track track = trackDataHolder.getTrack();
        List<TrackPoint> trackPointList = track.getTrackPointList();
        double[] timeIndexes = new double[trackPointList.size()];
        for (int i = 0; i < timeIndexes.length; i++) {
            timeIndexes[i] = trackPointList.get(i).getTimeIndex();
        }
        trackDataHolder.setTimeIndexes(timeIndexes);
    }

    @Override
    public void computeTrackDuration(Double timeLapse, TrackDataHolder trackDataHolder) {
        double[] timeIndexes = trackDataHolder.getTimeIndexes();
        int numberOfPoints = timeIndexes.length;
        double trackDuration = (numberOfPoints - 1) * timeLapse;
        trackDataHolder.setTrackDuration(trackDuration);
    }

    @Override
    public void generateTrackCoordinatesMatrix(TrackDataHolder trackDataHolder, double conversionFactor) {
        Track track = trackDataHolder.getTrack();
        List<TrackPoint> trackPointList = track.getTrackPointList();
        Double[][] trackCoordinatesMatrix = new Double[trackPointList.size()][2];
        for (int i = 0; i < trackCoordinatesMatrix.length; i++) {
            // put in each row (x, y) coordinates from trackPoint
            double cellRow = trackPointList.get(i).getCellRow();
            double x = cellRow / conversionFactor;
            double cellCol = trackPointList.get(i).getCellCol();
            double y = cellCol / conversionFactor;
            trackCoordinatesMatrix[i] = new Double[]{x, y};
        }
        trackDataHolder.setTrackCoordinatesMatrix(trackCoordinatesMatrix);
    }

    @Override
    public void computeCoordinatesRange(TrackDataHolder trackDataHolder) {
        Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
        Double[][] transpose2DArray = AnalysisUtils.transpose2DArray(trackCoordinatesMatrix);
        Double[] xCoordinates = transpose2DArray[0];
        Double[] yCoordinates = transpose2DArray[1];
        Double xMin = Collections.min(Arrays.asList(xCoordinates));
        Double xMax = Collections.max(Arrays.asList(xCoordinates));
        Double yMin = Collections.min(Arrays.asList(yCoordinates));
        Double yMax = Collections.max(Arrays.asList(yCoordinates));
        trackDataHolder.setxMin(xMin);
        trackDataHolder.setxMax(xMax);
        trackDataHolder.setyMin(yMin);
        trackDataHolder.setyMax(yMax);
    }

    @Override
    public void computeShiftedTrackCoordinates(TrackDataHolder trackDataHolder) {
        Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
        Double[][] shiftedTrackCoordinates = new Double[trackCoordinatesMatrix.length][trackCoordinatesMatrix[0].length];
        Double[] firstTrackCoordinates = trackCoordinatesMatrix[0];
        // get x0 and y0
        Double x0 = firstTrackCoordinates[0];
        Double y0 = firstTrackCoordinates[1];
        for (int row = 0; row < trackCoordinatesMatrix.length; row++) {
            // get current x and current y
            double currentX = trackCoordinatesMatrix[row][0];
            double currentY = trackCoordinatesMatrix[row][1];
            shiftedTrackCoordinates[row] = new Double[]{currentX - x0, currentY - y0};
        }
        trackDataHolder.setShiftedTrackCoordinates(shiftedTrackCoordinates);
    }

    @Override
    public void computeDeltaMovements(TrackDataHolder trackDataHolder) {
        Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
        Double[][] deltaMovements = new Double[trackCoordinatesMatrix.length][trackCoordinatesMatrix[0].length];
        // we need to start from the second row
        for (int row = 1; row < trackCoordinatesMatrix.length; row++) {
            // get current coordinates
            double currentX = trackCoordinatesMatrix[row][0];
            double currentY = trackCoordinatesMatrix[row][1];
            // get previous coordinates
            double previousX = trackCoordinatesMatrix[row - 1][0];
            double previousY = trackCoordinatesMatrix[row - 1][1];
            // compute delta movements
            double deltaX = currentX - previousX;
            double deltaY = currentY - previousY;
            deltaMovements[row - 1] = new Double[]{deltaX, deltaY};
        }
        trackDataHolder.setDeltaMovements(deltaMovements);
    }

    @Override
    public void computeInstantaneousDisplacements(TrackDataHolder trackDataHolder) {
        // get delta movements
        Double[][] deltaMovements = trackDataHolder.getDeltaMovements();
        Double[] instantaneousDisplacements = new Double[deltaMovements.length];
        for (int row = 0; row < instantaneousDisplacements.length; row++) {
            Double deltaX = deltaMovements[row][0];
            Double deltaY = deltaMovements[row][1];
            if (deltaX != null && deltaY != null) {
                // this is simply the Euclidean distance between two consecutive time points
                // deltaZ = sqrt(deltaX² + deltaY²)
                // dividing this quantity for the time frame will give the speed information
                Double instantaneousDisplacement = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
                instantaneousDisplacements[row] = instantaneousDisplacement;
            }
        }
        trackDataHolder.setInstantaneousDisplacements(instantaneousDisplacements);
    }

    @Override
    public void computeTrackMedianDisplacement(TrackDataHolder trackDataHolder) {
        Double[] instantaneousDisplacements = trackDataHolder.getInstantaneousDisplacements();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(instantaneousDisplacements);
        double trackMedianDisplacement = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNullValues));
        trackDataHolder.setTrackMedianDisplacement(trackMedianDisplacement);
    }

    @Override
    public void computeCumulativeDistance(TrackDataHolder trackDataHolder) {
        double cumulativeDistance = 0;
        Double[] instantaneousDisplacements = trackDataHolder.getInstantaneousDisplacements();
        for (int i = 0; i < instantaneousDisplacements.length; i++) {
            if (instantaneousDisplacements[i] != null) {
                cumulativeDistance += instantaneousDisplacements[i];
            }
        }
        trackDataHolder.setCumulativeDistance(cumulativeDistance);
    }

    @Override
    public void computeEuclideanDistance(TrackDataHolder trackDataHolder) {
        Double[][] deltaMovements = trackDataHolder.getDeltaMovements();
        // last delta movements
        Double deltaX = deltaMovements[deltaMovements.length - 2][0];
        Double deltaY = deltaMovements[deltaMovements.length - 2][1];
        double euclideanDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        trackDataHolder.setEuclideanDistance(euclideanDistance);
    }

    @Override
    public void computeTrackMeanSpeed(TrackDataHolder trackDataHolder) {
        double cumulativeDistance = trackDataHolder.getCumulativeDistance();
        double trackDuration = trackDataHolder.getTrackDuration();
        double trackMeanSpeed = cumulativeDistance / trackDuration;
        trackDataHolder.setTrackMeanSpeed(trackMeanSpeed);
    }

    @Override
    public void computeDirectionality(TrackDataHolder trackDataHolder) {
        // this is the Euclidean distance divided by the cumulative one
        double directionality = trackDataHolder.getEuclideanDistance() / trackDataHolder.getCumulativeDistance();
        trackDataHolder.setDirectionality(directionality);
    }

    @Override
    public void computeTurningAngles(TrackDataHolder trackDataHolder) {
        // get delta movements
        Double[][] deltaMovements = trackDataHolder.getDeltaMovements();
        Double[] turningAngles = new Double[deltaMovements.length];
        for (int row = 0; row < turningAngles.length; row++) {
            Double deltaX = deltaMovements[row][0];
            Double deltaY = deltaMovements[row][1];
            if (deltaX != null && deltaY != null) {
                // angle = degrees(atan(deltaY/deltaX))
                Double angleRadians = Math.atan2(deltaY, deltaX);
                // go from radians to degrees
                Double angleDegrees = angleRadians * 180 / Math.PI;
                turningAngles[row] = angleDegrees;
            }
        }
        trackDataHolder.setTurningAngles(turningAngles);
    }

    @Override
    public void computeTrackAngle(TrackDataHolder trackDataHolder) {
        Double[] turningAngles = trackDataHolder.getTurningAngles();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(turningAngles);
        // simply compute the median of the turning angles
        double trackAngle = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNullValues));
        trackDataHolder.setTrackAngle(trackAngle);
    }
}
