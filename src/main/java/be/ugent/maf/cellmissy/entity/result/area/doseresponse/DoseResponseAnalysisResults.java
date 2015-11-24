/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.area.doseresponse;

/**
 * This class holds the results of the dose-response analysis All these results
 * will be shown in a table on the results tab and this table will also be put
 * in the report.
 *
 * @author Gwendolien
 */
public class DoseResponseAnalysisResults {

    //minimum response, best-fit value or constrained by user
    private double bottomInitial;

    //maximum response, best-fit or constrained
    private double topInitial;

    //best-fit value or standard hillslope of curve
    private double hillslopeInitial;

    //log of 50% effective concentration
    private double logEC50Initial;

    //50% effective concentration
    private double ec50Initial;

    //goodness of fit
    private double goodnessOfFitInitial;

    //standard error of the logEC50
    private double standardErrorLogEC50Initial;

    //array with lower and upper values of 95% confidence interval of logEC50
    private Double[] confidenceIntervalLogEC50Initial;

    //lower and upper values of 95% confidence interval of EC50
    private Double[] confidenceIntervalEC50Initial;

    //lower and upper values of 95% confidence interval of hillslope (if fitted)
    private Double[] confidenceIntervalHillslopeInitial;

    //same variables as above, only this time from fitting the normalized data
    private double bottomNormalized;

    private double topNormalized;

    private double hillslopeNormalized;

    private double logEC50Normalized;

    private double ec50Normalized;

    private double goodnessOfFitNormalized;

    private double standardErrorLogEC50Normalized;

    private Double[] confidenceIntervalLogEC50Normalized;

    private Double[] confidenceIntervalEC50Normalized;

    private Double[] confidenceIntervalHillslopeNormalized;

    /**
     * Getters and setters
     *
     * @return
     */
    public double getBottomInitial() {
        return bottomInitial;
    }

    public void setBottomInitial(double bottomInitial) {
        this.bottomInitial = bottomInitial;
    }

    public double getTopInitial() {
        return topInitial;
    }

    public void setTopInitial(double topInitial) {
        this.topInitial = topInitial;
    }

    public double getHillslopeInitial() {
        return hillslopeInitial;
    }

    public void setHillslopeInitial(double hillslopeInitial) {
        this.hillslopeInitial = hillslopeInitial;
    }

    public double getLogEC50Initial() {
        return logEC50Initial;
    }

    public void setLogEC50Initial(double logEC50Initial) {
        this.logEC50Initial = logEC50Initial;
    }

    public double getEc50Initial() {
        return ec50Initial;
    }

    public void setEc50Initial(double ec50Initial) {
        this.ec50Initial = ec50Initial;
    }

    public double getGoodnessOfFitInitial() {
        return goodnessOfFitInitial;
    }

    public void setGoodnessOfFitInitial(double goodnessOfFitInitial) {
        this.goodnessOfFitInitial = goodnessOfFitInitial;
    }

    public double getStandardErrorLogEC50Initial() {
        return standardErrorLogEC50Initial;
    }

    public void setStandardErrorLogEC50Initial(double standardErrorLogEC50Initial) {
        this.standardErrorLogEC50Initial = standardErrorLogEC50Initial;
    }

    public Double[] getConfidenceIntervalLogEC50Initial() {
        return confidenceIntervalLogEC50Initial;
    }

    public void setConfidenceIntervalLogEC50Initial(Double[] confidenceIntervalLogEC50Initial) {
        this.confidenceIntervalLogEC50Initial = confidenceIntervalLogEC50Initial;
    }

    public Double[] getConfidenceIntervalEC50Initial() {
        return confidenceIntervalEC50Initial;
    }

    public void setConfidenceIntervalEC50Initial(Double[] confidenceIntervalEC50Initial) {
        this.confidenceIntervalEC50Initial = confidenceIntervalEC50Initial;
    }

    public Double[] getConfidenceIntervalHillslopeInitial() {
        return confidenceIntervalHillslopeInitial;
    }

    public void setConfidenceIntervalHillslopeInitial(Double[] confidenceIntervalHillslopeInitial) {
        this.confidenceIntervalHillslopeInitial = confidenceIntervalHillslopeInitial;
    }

    public double getBottomNormalized() {
        return bottomNormalized;
    }

    public void setBottomNormalized(double bottomNormalized) {
        this.bottomNormalized = bottomNormalized;
    }

    public double getTopNormalized() {
        return topNormalized;
    }

    public void setTopNormalized(double topNormalized) {
        this.topNormalized = topNormalized;
    }

    public double getHillslopeNormalized() {
        return hillslopeNormalized;
    }

    public void setHillslopeNormalized(double hillslopeNormalized) {
        this.hillslopeNormalized = hillslopeNormalized;
    }

    public double getLogEC50Normalized() {
        return logEC50Normalized;
    }

    public void setLogEC50Normalized(double logEC50Normalized) {
        this.logEC50Normalized = logEC50Normalized;
    }

    public double getEc50Normalized() {
        return ec50Normalized;
    }

    public void setEc50Normalized(double ec50Normalized) {
        this.ec50Normalized = ec50Normalized;
    }

    public double getGoodnessOfFitNormalized() {
        return goodnessOfFitNormalized;
    }

    public void setGoodnessOfFitNormalized(double goodnessOfFitNormalized) {
        this.goodnessOfFitNormalized = goodnessOfFitNormalized;
    }

    public double getStandardErrorLogEC50Normalized() {
        return standardErrorLogEC50Normalized;
    }

    public void setStandardErrorLogEC50Normalized(double standardErrorLogEC50Normalized) {
        this.standardErrorLogEC50Normalized = standardErrorLogEC50Normalized;
    }

    public Double[] getConfidenceIntervalLogEC50Normalized() {
        return confidenceIntervalLogEC50Normalized;
    }

    public void setConfidenceIntervalLogEC50Normalized(Double[] confidenceIntervalLogEC50Normalized) {
        this.confidenceIntervalLogEC50Normalized = confidenceIntervalLogEC50Normalized;
    }

    public Double[] getConfidenceIntervalEC50Normalized() {
        return confidenceIntervalEC50Normalized;
    }

    public void setConfidenceIntervalEC50Normalized(Double[] confidenceIntervalEC50Normalized) {
        this.confidenceIntervalEC50Normalized = confidenceIntervalEC50Normalized;
    }

    public Double[] getConfidenceIntervalHillslopeNormalized() {
        return confidenceIntervalHillslopeNormalized;
    }

    public void setConfidenceIntervalHillslopeNormalized(Double[] confidenceIntervalHillslopeNormalized) {
        this.confidenceIntervalHillslopeNormalized = confidenceIntervalHillslopeNormalized;
    }

}
