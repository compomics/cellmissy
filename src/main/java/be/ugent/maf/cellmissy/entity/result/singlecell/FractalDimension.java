/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

/**
 * An object keeping references to the results of a fractal analysis: x and y
 * values (preferably in log10) and the fractal dimension value as the slope of
 * the linear regression of the two.
 *
 * @author Paola
 */
public class FractalDimension {

    // the values for the x dimension
    private double[] xValues;
    // the values for the y dimension
    private double[] yValues;
    // the value of the fractal dimension
    private double FD;

    /**
     * Empty constructor
     */
    public FractalDimension() {
    }

    public FractalDimension(double[] xValues, double[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }

    public double[] getxValues() {
        return xValues;
    }

    public void setxValues(double[] xValues) {
        this.xValues = xValues;
    }

    public double[] getyValues() {
        return yValues;
    }

    public void setyValues(double[] yValues) {
        this.yValues = yValues;
    }

    public double getFD() {
        return FD;
    }

    public void setFD(double FD) {
        this.FD = FD;
    }

}
