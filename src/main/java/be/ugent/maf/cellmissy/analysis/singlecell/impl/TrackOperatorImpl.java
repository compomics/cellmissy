/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.TrackOperator;
import be.ugent.maf.cellmissy.entity.result.singlecell.Point;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation for the Track Operator interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("trackOperator")
public class TrackOperatorImpl implements TrackOperator {

    @Autowired
    private GrahamScanAlgorithm grahamScanAlgorithm;

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
    public void computeDuration(Double timeLapse, TrackDataHolder trackDataHolder) {
        double[] timeIndexes = trackDataHolder.getTimeIndexes();
        int numberOfPoints = timeIndexes.length;
        double duration = (numberOfPoints - 1) * timeLapse;
        trackDataHolder.setDuration(duration);
    }

    @Override
    public void generateCoordinatesMatrix(TrackDataHolder trackDataHolder, double conversionFactor) {
        Track track = trackDataHolder.getTrack();
        List<TrackPoint> trackPointList = track.getTrackPointList();
        Double[][] coordinatesMatrix = new Double[trackPointList.size()][2];
        for (int i = 0; i < coordinatesMatrix.length; i++) {
            TrackPoint trackPoint = trackPointList.get(i);
            // put in each row (x, y) coordinates from trackPoint
            double x = trackPoint.getCellRow() / conversionFactor;
            double y = trackPoint.getCellCol() / conversionFactor;
            coordinatesMatrix[i] = new Double[]{x, y};
            // create a new Point with these coordinates and set it to the TrackPoint
            Point point = new Point(x, y);
            trackPoint.setPoint(point);
        }
        trackDataHolder.setCoordinatesMatrix(coordinatesMatrix);
    }

    @Override
    public void computeCoordinatesRange(TrackDataHolder trackDataHolder) {
        Double[][] coordinatesMatrix = trackDataHolder.getCoordinatesMatrix();
        Double[][] transposedCoordinatesMatrix = AnalysisUtils.transpose2DArray(coordinatesMatrix);
        Double[] xCoordinates = transposedCoordinatesMatrix[0];
        Double[] yCoordinates = transposedCoordinatesMatrix[1];
        List<Double> xCoordAsList = Arrays.asList(xCoordinates);
        List<Double> yCoordAsList = Arrays.asList(yCoordinates);
        trackDataHolder.setxMin(Collections.min(xCoordAsList));
        trackDataHolder.setxMax(Collections.max(xCoordAsList));
        trackDataHolder.setyMin(Collections.min(yCoordAsList));
        trackDataHolder.setyMax(Collections.max(yCoordAsList));
    }

    @Override
    public void computeShiftedCoordinatesMatrix(TrackDataHolder trackDataHolder) {
        Double[][] coordinatesMatrix = trackDataHolder.getCoordinatesMatrix();
        Double[][] shiftedCoordinatesMatrix = new Double[coordinatesMatrix.length][coordinatesMatrix[0].length];
        Double[] firstCoordinates = coordinatesMatrix[0];
        // get x0 and y0
        Double x0 = firstCoordinates[0];
        Double y0 = firstCoordinates[1];
        for (int row = 0; row < coordinatesMatrix.length; row++) {
            // get current x and current y
            double currentX = coordinatesMatrix[row][0];
            double currentY = coordinatesMatrix[row][1];
            shiftedCoordinatesMatrix[row] = new Double[]{currentX - x0, currentY - y0};
        }
        trackDataHolder.setShiftedCooordinatesMatrix(shiftedCoordinatesMatrix);
    }

    @Override
    public void computeDeltaMovements(TrackDataHolder trackDataHolder) {
        Double[][] coordinatesMatrix = trackDataHolder.getCoordinatesMatrix();
        Double[][] deltaMovements = new Double[coordinatesMatrix.length][coordinatesMatrix[0].length];
        // we need to start from the second row
        for (int row = 1; row < coordinatesMatrix.length; row++) {
            // get current coordinates
            double currentX = coordinatesMatrix[row][0];
            double currentY = coordinatesMatrix[row][1];
            // get previous coordinates
            double previousX = coordinatesMatrix[row - 1][0];
            double previousY = coordinatesMatrix[row - 1][1];
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
                Double instantaneousDisplacement = Math.hypot(deltaX, deltaY);
                instantaneousDisplacements[row] = instantaneousDisplacement;
            }
        }
        trackDataHolder.setInstantaneousDisplacements(instantaneousDisplacements);
    }

    @Override
    public void computeMedianDisplacement(TrackDataHolder trackDataHolder) {
        Double[] instantaneousDisplacements = trackDataHolder.getInstantaneousDisplacements();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(instantaneousDisplacements);
        double medianDisplacement = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNullValues));
        trackDataHolder.setMedianDisplacement(medianDisplacement);
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
        // Math.hypot: sqrt(x2 +y2) without intermediate overflow or underflow
        double euclideanDistance = Math.hypot(deltaX, deltaY);
        trackDataHolder.setEuclideanDistance(euclideanDistance);
    }

    @Override
    public void computeMedianSpeed(TrackDataHolder trackDataHolder) {
        double medianDisplacement = trackDataHolder.getMedianDisplacement();
        double duration = trackDataHolder.getDuration();
        double medianSpeed = medianDisplacement / duration;
        trackDataHolder.setMedianSpeed(medianSpeed);
    }

    @Override
    public void computeDirectionality(TrackDataHolder trackDataHolder) {
        double directionality = trackDataHolder.getEuclideanDistance() / trackDataHolder.getCumulativeDistance();
        trackDataHolder.setDirectionality(directionality);
    }

    @Override
    public void computeConvexHull(TrackDataHolder trackDataHolder) {
        Track track = trackDataHolder.getTrack();
        ConvexHull convexHull = new ConvexHull();
        grahamScanAlgorithm.computeHull(track, convexHull);
        grahamScanAlgorithm.computeFarthestPoints(track, convexHull);
        trackDataHolder.setConvexHull(convexHull);
    }

    @Override
    public void computeDisplacementRatio(TrackDataHolder trackDataHolder) {
        ConvexHull convexHull = trackDataHolder.getConvexHull();
        List<Point> farthestPointsPair = convexHull.getFarthestPointsPair();
        double maxSpan = farthestPointsPair.get(0).euclideanDistanceTo(farthestPointsPair.get(1));
        double displacementRatio = trackDataHolder.getEuclideanDistance() / maxSpan;
        trackDataHolder.setDisplacementRatio(displacementRatio);
    }

    @Override
    public void computeOutreachRatio(TrackDataHolder trackDataHolder) {
        ConvexHull convexHull = trackDataHolder.getConvexHull();
        List<Point> farthestPointsPair = convexHull.getFarthestPointsPair();
        double maxSpan = farthestPointsPair.get(0).euclideanDistanceTo(farthestPointsPair.get(1));
        double outreachRatio = maxSpan / trackDataHolder.getCumulativeDistance();
        trackDataHolder.setOutreachRatio(outreachRatio);
    }

    @Override
    public void computeTurningAngles(TrackDataHolder trackDataHolder) {
        Double[][] deltaMovements = trackDataHolder.getDeltaMovements();
        Double[] turningAngles = new Double[deltaMovements.length];
        for (int row = 0; row < turningAngles.length; row++) {
            Double deltaX = deltaMovements[row][0];
            Double deltaY = deltaMovements[row][1];
            if (deltaX != null && deltaY != null) {
                // angle = degrees(atan(deltaY/deltaX))
                // Returns the angle theta from the conversion of rectangular coordinates (x, y)
                // to polar coordinates (r, theta).
                // This method computes the phase theta by computing an arc tangent of y/x in the range of -pi to pi.
                Double angleRadians = Math.atan2(deltaY, deltaX);
                // go from radians to degrees
                Double angleDegrees = angleRadians * 180 / Math.PI;
                turningAngles[row] = angleDegrees;
            }
        }
        trackDataHolder.setTurningAngles(turningAngles);
    }

    @Override
    public void computeMedianTurningAngle(TrackDataHolder trackDataHolder) {
        Double[] turningAngles = trackDataHolder.getTurningAngles();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(turningAngles);
        // simply compute the median of the turning angles
        double medianTurningAngle = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNullValues));
        trackDataHolder.setMedianTurningAngle(medianTurningAngle);
    }
}
