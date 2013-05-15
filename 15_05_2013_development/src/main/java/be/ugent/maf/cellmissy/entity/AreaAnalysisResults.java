/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

/**
 * This class is holding the results from the area-analysis step.
 * @author Paola Masuzzo
 */
public class AreaAnalysisResults {

    // slopes from Linear Model
    private Double[] slopes;
    // R2 coefficients from Linear Model
    private Double[] goodnessOfFit;
    // mean velocity
    private double meanSlope;
    // MAD of mean slope
    private double madSlope;

    public Double[] getGoodnessOfFit() {
        return goodnessOfFit;
    }

    public void setGoodnessOfFit(Double[] goodnessOfFit) {
        this.goodnessOfFit = goodnessOfFit;
    }

    public Double[] getSlopes() {
        return slopes;
    }

    public void setSlopes(Double[] slopes) {
        this.slopes = slopes;
    }

    public double getMadSlope() {
        return madSlope;
    }

    public void setMadSlope(double madSlope) {
        this.madSlope = madSlope;
    }

    public double getMeanSlope() {
        return meanSlope;
    }

    public void setMeanSlope(double meanSlope) {
        this.meanSlope = meanSlope;
    }
}
