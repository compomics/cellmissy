package be.ugent.maf.cellmissy.entity.result.singlecell;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 * This class holds the results of applying an interpolation to a single cell track.
 * <p>
 * Created by Paola on 2/1/2016.
 */
public class InterpolatedTrack {

    // the interpolant time
    private double[] interpolantTime;
    // the interpolated x coordinates
    private double[] interpolatedX;
    // the interpolated y coordinates
    private double[] interpolatedY;
    // the polynomial function for the x interpolation
    private PolynomialFunction polynomialFunctionX;
    // the polynomial function for the y interpolation
    private PolynomialFunction polynomialFunctionY;

    /**
     * Empty constructor.
     */
    public InterpolatedTrack() {
    }

    /**
     * Constructor.
     *
     * @param interpolantTime
     * @param interpolatedX
     * @param interpolatedY
     * @param polynomialFunctionX
     * @param polynomialFunctionY
     */
    public InterpolatedTrack(double[] interpolantTime, double[] interpolatedX, double[] interpolatedY, PolynomialFunction polynomialFunctionX, PolynomialFunction polynomialFunctionY) {
        this.interpolantTime = interpolantTime;
        this.interpolatedX = interpolatedX;
        this.interpolatedY = interpolatedY;
        this.polynomialFunctionX = polynomialFunctionX;
        this.polynomialFunctionY = polynomialFunctionY;
    }

    public double[] getInterpolantTime() {
        return interpolantTime;
    }

    public void setInterpolantTime(double[] interpolantTime) {
        this.interpolantTime = interpolantTime;
    }

    public double[] getInterpolatedX() {
        return interpolatedX;
    }

    public void setInterpolatedX(double[] interpolatedX) {
        this.interpolatedX = interpolatedX;
    }

    public double[] getInterpolatedY() {
        return interpolatedY;
    }

    public void setInterpolatedY(double[] interpolatedY) {
        this.interpolatedY = interpolatedY;
    }

    public PolynomialFunction getPolynomialFunctionX() {
        return polynomialFunctionX;
    }

    public void setPolynomialFunctionX(PolynomialFunction polynomialFunctionX) {
        this.polynomialFunctionX = polynomialFunctionX;
    }

    public PolynomialFunction getPolynomialFunctionY() {
        return polynomialFunctionY;
    }

    public void setPolynomialFunctionY(PolynomialFunction polynomialFunctionY) {
        this.polynomialFunctionY = polynomialFunctionY;
    }
}
