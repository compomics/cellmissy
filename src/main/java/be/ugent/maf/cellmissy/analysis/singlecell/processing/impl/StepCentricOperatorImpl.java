/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

/**
 * An implementation of the step centric operator interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("stepCentricOperator")
public class StepCentricOperatorImpl implements StepCentricOperator {

    @Override
    public void generateTimeIndexes(StepCentricDataHolder stepCentricDataHolder) {
        Track track = stepCentricDataHolder.getTrack();
        List<TrackPoint> trackPointList = track.getTrackPointList();
        double[] timeIndexes = new double[trackPointList.size()];
        for (int i = 0; i < timeIndexes.length; i++) {
            timeIndexes[i] = trackPointList.get(i).getTimeIndex();
        }
        stepCentricDataHolder.setTimeIndexes(timeIndexes);
    }

    @Override
    public void generateCoordinatesMatrix(StepCentricDataHolder stepCentricDataHolder, double conversionFactor) {
        Track track = stepCentricDataHolder.getTrack();
        List<TrackPoint> trackPointList = track.getTrackPointList();
        Double[][] coordinatesMatrix = new Double[trackPointList.size()][2];
        for (int i = 0; i < coordinatesMatrix.length; i++) {
            TrackPoint trackPoint = trackPointList.get(i);
            // put in each row (x, y) coordinates from trackPoint
            double x = trackPoint.getCellRow() / conversionFactor;
            double y = trackPoint.getCellCol() / conversionFactor;
            coordinatesMatrix[i] = new Double[]{x, y};
            // create a new GeometricPoint with these coordinates and set it to the TrackPoint
            GeometricPoint geometricPoint = new GeometricPoint(x, y);
            trackPoint.setGeometricPoint(geometricPoint);
        }
        stepCentricDataHolder.setCoordinatesMatrix(coordinatesMatrix);
    }

    @Override
    public void computeShiftedCoordinatesMatrix(StepCentricDataHolder stepCentricDataHolder) {
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
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
        stepCentricDataHolder.setShiftedCoordinatesMatrix(shiftedCoordinatesMatrix);
    }

    @Override
    public void computeDeltaMovements(StepCentricDataHolder stepCentricDataHolder) {
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        Double[][] deltaMovements = new Double[coordinatesMatrix.length - 1][coordinatesMatrix[0].length];
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
        stepCentricDataHolder.setDeltaMovements(deltaMovements);
    }

    @Override
    public void computeInstantaneousDisplacements(StepCentricDataHolder stepCentricDataHolder) {
        // get delta movements
        Double[][] deltaMovements = stepCentricDataHolder.getDeltaMovements();
        Double[] instantaneousDisplacements = new Double[deltaMovements.length];
        for (int row = 0; row < instantaneousDisplacements.length; row++) {
            Double deltaX = deltaMovements[row][0];
            Double deltaY = deltaMovements[row][1];
            // this is simply the Euclidean distance between two consecutive time points
            // deltaZ = sqrt(deltaX² + deltaY²)
            // dividing this quantity for the time frame will give the speed information
            Double instantaneousDisplacement = Math.hypot(deltaX, deltaY);
            instantaneousDisplacements[row] = instantaneousDisplacement;
        }
        stepCentricDataHolder.setInstantaneousDisplacements(instantaneousDisplacements);
    }

    @Override
    public void computeTurningAngles(StepCentricDataHolder stepCentricDataHolder) {
        Double[][] deltaMovements = stepCentricDataHolder.getDeltaMovements();
        Double[] turningAngles = new Double[deltaMovements.length];
        for (int row = 0; row < turningAngles.length; row++) {
            Double deltaX = deltaMovements[row][0];
            Double deltaY = deltaMovements[row][1];
            // angle = degrees(atan(deltaY/deltaX))
            // Returns the angle theta from the conversion of rectangular coordinates (x, y)
            // to polar coordinates (r, theta).
            // This method computes the phase theta by computing an arc tangent of y/x in the range of -pi to pi.
            double division = deltaY / deltaX; // if division is 0, the cell is going exactly back on its path!
            Double angleRadians = Math.atan(division) + 0; // need to add 0 to avoid signed 0 in Java
            // go from radians to degrees
            Double angleDegrees = Math.toDegrees(angleRadians);
            // if the angle is NaN (both deltaX and deltaY are zero), the cell stays exactly on place 
            if (angleDegrees < 0) {
                angleDegrees = angleDegrees + 360;
            }
            turningAngles[row] = angleDegrees;
        }
        stepCentricDataHolder.setTurningAngles(turningAngles);
    }

    @Override
    public void computeDirectionalityRatios(StepCentricDataHolder stepCentricDataHolder) {
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        Double[] instantaneousDisplacements = stepCentricDataHolder.getInstantaneousDisplacements();
        Double[] directionalityRatios = new Double[instantaneousDisplacements.length];
        // first coordinates (x, y)
        double firstX = coordinatesMatrix[0][0];
        double firstY = coordinatesMatrix[0][1];
        for (int row = 1; row < coordinatesMatrix.length; row++) {
            // get current coordinates
            double currentX = coordinatesMatrix[row][0];
            double currentY = coordinatesMatrix[row][1];
            // the delta movements between the current position and the start point
            double deltaX = currentX - firstX;
            double deltaY = currentY - firstY;
            // the distance between the start point of the track and the current position
            double euclDistToStartPoint = Math.hypot(deltaX, deltaY);
            double tempCumDist = 0;
            for (int i = 0; i < row; i++) {
                tempCumDist += instantaneousDisplacements[i];
            }
            double currentDirectionalityRatio = euclDistToStartPoint / tempCumDist;
            directionalityRatios[row - 1] = currentDirectionalityRatio;
        }
        stepCentricDataHolder.setDirectionalityRatios(directionalityRatios);
    }

    @Override
    public void computeMeanSquareDisplacements(StepCentricDataHolder stepCentricDataHolder) {
    }

    @Override
    public void computeDirectionAutocorrelations(StepCentricDataHolder stepCentricDataHolder) {
        Double[] turningAngles = stepCentricDataHolder.getTurningAngles();
        List<Double[]> directionAutocorrelationsList = new ArrayList<>();
        Double[] firstCoefficient = new Double[]{1.0}; // first coefficient is 1
        directionAutocorrelationsList.add(firstCoefficient);
        for (int counter = 0; counter < turningAngles.length - 1; counter++) {
            // create a new current vector for direction autocorrelations
            Double[] currentDAs = new Double[turningAngles.length - 1 - counter];
            for (int row = 0; row < currentDAs.length; row++) {
                double currentTA = turningAngles[row]; // current turning angle
                double successiveTA = turningAngles[row + counter + 1]; // successive turning angle
                double theta = currentTA - successiveTA; // the theta difference between them
                double currentDA = Math.cos(Math.toRadians(theta)); // current direction autocorrelation
                currentDAs[row] = currentDA; // put it in the vector
            }
            directionAutocorrelationsList.add(currentDAs); // add the vector to the total list
        }
        stepCentricDataHolder.setDirectionAutocorrelations(directionAutocorrelationsList);
    }

    @Override
    public void computeMeanDirectionAutocorrelations(StepCentricDataHolder stepCentricDataHolder) {
        List<Double[]> directionAutocorrelations = stepCentricDataHolder.getDirectionAutocorrelations();
        Double[] medianDirectionAutocorrelations = new Double[directionAutocorrelations.size()];
        for (int i = 0; i < directionAutocorrelations.size(); i++) {
            Double[] coefficients = directionAutocorrelations.get(i);
            Double medianDirectionAutocorrelation = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(coefficients));
            medianDirectionAutocorrelations[i] = medianDirectionAutocorrelation;
        }
        stepCentricDataHolder.setMedianDirectionAutocorrelations(medianDirectionAutocorrelations);
    }
}
