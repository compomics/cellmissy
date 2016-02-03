/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackInterpolator;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.MathArrays;

/**
 * This class implements a linear function for interpolation of a track.
 *
 * @author Paola
 */
public class TrackLinearInterpolator implements TrackInterpolator {

    @Override
    public InterpolatedTrack interpolateTrack(double[] time, double[] x, double[] y) {
        // create a new linear interpolator
        LinearInterpolator linearInterpolator = new LinearInterpolator();
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
        PolynomialSplineFunction functionX = linearInterpolator.interpolate(time, x);
        PolynomialSplineFunction functionY = linearInterpolator.interpolate(time, y);

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
