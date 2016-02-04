/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl.interpolation;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.interpolation.TrackInterpolator;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;

import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.MathArrays;

/**
 *
 * This implementation for the track interpolator computes a natural (also known
 * as "free", "un-clamped") cubic spline interpolation for the data set. The x
 * values passed to the interpolator must be ordered in ascending order. It is
 * not valid to evaluate the function for values outside the range x0..xN --
 * will throw an OutOfRangeException.
 *
 * @author Paola
 */
public class TrackSplineInterpolator implements TrackInterpolator {

    @Override
    public InterpolatedTrack interpolateTrack(double[] time, double[] x, double[] y) {
        // create a new spline interpolator
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        int interpolationPoints = PropertiesConfigurationHolder.getInstance().getInt("numberOfInterpolationPoints");

        // create arrays to hold the interpolant time, the interpolated X and the interpolated Y
        double[] interpolantTime = new double[interpolationPoints];
        double[] interpolatedX = new double[interpolationPoints];
        double[] interpolatedY = new double[interpolationPoints];
        // the step used for the interpolation in both direction
        double interpolationStep = (time[time.length - 1] - time[0]) / interpolationPoints;

        // check for monotonicity
        boolean monotonic = MathArrays.isMonotonic(time, MathArrays.OrderDirection.INCREASING, false);
        // in case time is not monotonic, sort in place time, x and y coordinates
        if (!monotonic) {
            MathArrays.sortInPlace(time, x, y);
        }

        // call the interpolator, and actually do the interpolation
        PolynomialSplineFunction functionX = splineInterpolator.interpolate(time, x);
        PolynomialSplineFunction functionY = splineInterpolator.interpolate(time, y);

        // get the polynomial functions in both directions
        PolynomialFunction polynomialFunctionX = functionX.getPolynomials()[0];
        PolynomialFunction polynomialFunctionY = functionY.getPolynomials()[0];

        for (int i = 0; i < interpolationPoints; i++) {
            interpolantTime[i] = time[0] + (i * interpolationStep);
            interpolatedX[i] = functionX.value(interpolantTime[i]);
            interpolatedY[i] = functionY.value(interpolantTime[i]);
        }

        return new InterpolatedTrack(interpolantTime, interpolatedX, interpolatedY, polynomialFunctionX, polynomialFunctionY);
    }
}
