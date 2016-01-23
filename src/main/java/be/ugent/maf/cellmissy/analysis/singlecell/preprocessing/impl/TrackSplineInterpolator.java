/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackInterpolator;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.MathArrays;
import org.springframework.stereotype.Component;

/**
 *
 * The x values passed to the interpolator must be ordered in ascending order.
 * It is not valid to evaluate the function for values outside the range x0..xN
 * -- will throw an OutOfRangeException.
 *
 * @author Paola
 */
@Component("trackSplineInterpolator")
public class TrackSplineInterpolator implements TrackInterpolator {

    @Override
    public void interpolateTrack(CellCentricDataHolder cellCentricDataHolder, StepCentricDataHolder stepCentricDataHolder, int interpolationPoints) {
        // make sure we interpolate in the range of the x value
        double xMin = cellCentricDataHolder.getxMin();
        double xMax = cellCentricDataHolder.getxMax();
        double[] interpolationX = new double[interpolationPoints];
        double[] interpolatedY = new double[interpolationPoints];
        double interpolationStep = (xMax - xMin) / interpolationPoints;
        // create a new spline interpolator
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        // get the x and the y coordinates
        double[] xCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[0]));
        double[] yCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[1]));
        // [x] needs to be strictly monotonic
        MathArrays.sortInPlace(xCoord, yCoord);
        double[] XMonotonic = makeXStrictlyMonotonic(xCoord);
        // call the interpolator
        PolynomialSplineFunction function = splineInterpolator.interpolate(XMonotonic, yCoord);
        for (int i = 0; i < interpolationPoints; i++) {
            interpolationX[i] = xMin + (i * interpolationStep);
            interpolatedY[i] = function.value(interpolationX[i]);
        }
        cellCentricDataHolder.setInterpolationX(interpolationX);
        cellCentricDataHolder.setInterpolatedY(interpolatedY);
    }

    /**
     *
     * @param xArray
     * @return
     */
    private double[] makeXStrictlyMonotonic(double[] xArray) {
        double previous = xArray[0];
        final int max = xArray.length;
        double[] monotonic = new double[max];
        for (int i = 1; i < max; i++) {
            if (previous < xArray[i] | previous > xArray[i]) {
                monotonic[i - 1] = previous;
            } else if (previous == xArray[i]) {
                monotonic[i] = xArray[i] + .1;
            }
            previous = xArray[i];
        }
        monotonic[max - 1] = previous; // needs to be changed!
        return monotonic;
    }
}
