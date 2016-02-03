/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackInterpolator;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;

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
    public InterpolatedTrack interpolateTrack(double[] time, double[] x, double[] y) {
        // create interpolators for X and Y
        HermiteInterpolator xHermite = new HermiteInterpolator();
        HermiteInterpolator yHermite = new HermiteInterpolator();
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

        double[] internalPointsDerivativeX = internalPointsDerivative(time, x);
        double[] internalPointsDerivativeY = internalPointsDerivative(time, y);

        double[] endPointsDerivativeX = endPointsDerivative(time, x);
        double[] endPointsDerivativeY = endPointsDerivative(time, y);

        // call the interpolator and add sample points to it
        // we do add only the values, and not their derivatives
        for (int i = 0; i < time.length; i++) {
            if (i == 0) {
                xHermite.addSamplePoint(time[i], new double[]{x[i]}, new double[]{endPointsDerivativeX[0]});
                yHermite.addSamplePoint(time[i], new double[]{y[i]}, new double[]{endPointsDerivativeY[0]});
            } else if (i == time.length - 1) {
                xHermite.addSamplePoint(time[i], new double[]{x[i]}, new double[]{endPointsDerivativeX[1]});
                yHermite.addSamplePoint(time[i], new double[]{y[i]}, new double[]{endPointsDerivativeY[1]});
            } else {
                xHermite.addSamplePoint(time[i], new double[]{x[i]}, new double[]{internalPointsDerivativeX[i - 1]});
                yHermite.addSamplePoint(time[i], new double[]{y[i]}, new double[]{internalPointsDerivativeY[i - 1]});
            }
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

    /**
     *
     * @param x
     * @param y
     * @return
     */
    private double[] internalPointsDerivative(double[] x, double[] y) {
        int L = y.length;
        double internalPointsDerivative[] = new double[L];
        for (int k = 1; k < L - 1; k++) {
            // compute the slopes at internal points x[k]
            double hk = x[k + 1] - x[k];
            double hk_previous = x[k] - x[k - 1];
            // the first divided difference is:
            double deltak = (y[k + 1] - y[k]) / hk;
            double deltak_previous = (y[k] - y[k - 1]) / hk_previous;
            if (Math.signum(deltak) != Math.signum(deltak_previous) | Math.signum(deltak) == 0 | Math.signum(deltak_previous) == 0) {
                internalPointsDerivative[k - 1] = 0;
            } else {
                // compute the weighted harmonic mean
                double w1 = 2 * hk + hk_previous;
                double w2 = hk + 2 * hk_previous;
                internalPointsDerivative[k - 1] = (deltak * deltak_previous) * (w1 + w2) / (w1 * deltak + w2 * deltak_previous);
            }
        }

        return internalPointsDerivative;
    }

    private double[] endPointsDerivative(double[] x, double[] y) {
        int n = x.length;

        double[] endPointsDerivative = new double[2];
        double h1 = x[1] - x[0];
        double h2 = x[2] - x[1];
        double del1 = (y[1] - y[0]) / h1;
        double del2 = (y[2] - y[1]) / h2;
        endPointsDerivative[0] = getSlope(h1, h2, del1, del2);

        h1 = x[n - 1] - x[n - 2];
        h2 = x[n - 2] - x[n - 3];
        del1 = (y[n - 1] - y[n - 2]) / h1;
        del2 = (y[n - 2] - y[n - 3]) / h2;
        endPointsDerivative[1] = getSlope(h1, h2, del1, del2);
        return endPointsDerivative;
    }

    private double getSlope(double h1, double h2, double del1, double del2) {
        // noncentered, shape-preserving, three-point formula
        double d = ((2 * h1 + h2) * del1 - h1 * del2) / (h1 + h2);
        if (Math.signum(d) != Math.signum(del1)) {
            d = 0;
        } else if ((Math.signum(del1) != Math.signum(del2)) & (Math.abs(d) > Math.abs(3 * del1))) {
            d = 3 * del1;
        }
        return d;
    }
}
