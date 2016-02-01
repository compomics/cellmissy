/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackInterpolator;
import java.util.ArrayList;
import java.util.List;

import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
import org.apache.commons.math3.analysis.interpolation.HermiteInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.util.MathArrays;

/**
 * An Hermite implementation for the track interpolation. This polynomial
 * interpolator uses both sample values and sample derivatives.There is one
 * polynomial for each component of the values vector. All polynomials have the
 * same degree. The degree of the polynomials depends on the number of points
 * and number of derivatives at each point. For example the interpolation
 * polynomials for n sample points without any derivatives all have degree n-1.
 * The interpolation polynomials for n sample points with the two extreme points
 * having value and first derivative and the remaining points having value only
 * all have degree n+1. The interpolation polynomial for n sample points with
 * value, first and second derivative for all points all have degree 3n-1. The
 * point abscissae for all calls must be different!
 *
 * @author Paola
 */
public class TrackHermiteInterpolator implements TrackInterpolator {

    @Override
    public InterpolatedTrack interpolateTrack(double[] time, double[] x, double[] y, int interpolationPoints) {
        // create interpolators for X and Y
        HermiteInterpolator xHermite = new HermiteInterpolator();
        HermiteInterpolator yHermite = new HermiteInterpolator();

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

        // call the interpolator and add sample points to it
        // we do add only the values, and not their derivatives
        for (int i = 0; i < time.length; i++) {
            xHermite.addSamplePoint(time[i], new double[]{x[i]});
            yHermite.addSamplePoint(time[i], new double[]{y[i]});
        }

        for (int i = 0; i < interpolationPoints; i++) {
            interpolantTime[i] = time[0] + (i * interpolationStep);
            interpolatedX[i] = xHermite.value(interpolantTime[i])[0];
            interpolatedY[i] = yHermite.value(interpolantTime[i])[0];
        }

        // get the polynomial functions in both directions
        PolynomialFunction polynomialFunctionX = xHermite.getPolynomials()[0];
        PolynomialFunction polynomialFunctionY = yHermite.getPolynomials()[0];

        return new InterpolatedTrack(interpolantTime, interpolatedX, interpolatedY, polynomialFunctionX, polynomialFunctionY);
    }
}
