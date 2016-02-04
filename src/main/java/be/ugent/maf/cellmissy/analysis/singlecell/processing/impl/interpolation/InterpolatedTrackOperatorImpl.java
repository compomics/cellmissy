/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl.interpolation;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.interpolation.InterpolatedTrackOperator;
import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
import org.springframework.stereotype.Component;

/**
 * This class implements the InterpolatedTrackOperator. It performs operations
 * for an interpolated cell trajectory. The class is Spring-annotated as a
 * Component.
 *
 * @author Paola
 */
@Component("interpolatedTrackOperator")
public class InterpolatedTrackOperatorImpl implements InterpolatedTrackOperator {

    @Override
    public void computeCoordinatesMatrix(InterpolatedTrack interpolatedTrack) {
        // get the x and the y coordinates of the interpolated track
        double[] interpolatedX = interpolatedTrack.getInterpolatedX();
        double[] interpolatedY = interpolatedTrack.getInterpolatedY();
        double[][] coordinatesMatrix = new double[interpolatedX.length][2];
        for (int i = 0; i < coordinatesMatrix.length; i++) {
            coordinatesMatrix[i] = new double[]{interpolatedX[i], interpolatedY[i]};
        }
        interpolatedTrack.setCoordinatesMatrix(coordinatesMatrix);
    }

    @Override
    public void computeDeltaMovements(InterpolatedTrack interpolatedTrack) {
        // get the (already computed) coordinates matrix of the interpolated track
        double[][] coordinatesMatrix = interpolatedTrack.getCoordinatesMatrix();
        double[][] deltaMovements = new double[coordinatesMatrix.length - 1][coordinatesMatrix[0].length];
        for (int row = 1; row < coordinatesMatrix.length; row++) {
            double deltaX = coordinatesMatrix[row][0] - coordinatesMatrix[row - 1][0];
            double deltaY = coordinatesMatrix[row][1] - coordinatesMatrix[row - 1][1];
            deltaMovements[row - 1] = new double[]{deltaX, deltaY};
        }
        interpolatedTrack.setDeltaMovements(deltaMovements);
    }

    @Override
    public void computeTurningAngles(InterpolatedTrack interpolatedTrack) {
        // get the (already computed) delta movements of the interpolated track
        double[][] deltaMovements = interpolatedTrack.getDeltaMovements();
        double[] turningAngles = new double[deltaMovements.length];
        for (int row = 0; row < turningAngles.length; row++) {
            // angle = degrees(atan(deltaY/deltaX))
            double angleRadians = Math.atan2(deltaMovements[row][0], deltaMovements[row][1]);
            // go from radians to degrees
            turningAngles[row] = Math.toDegrees(angleRadians);
        }
        interpolatedTrack.setTurningAngles(turningAngles);
    }
}
