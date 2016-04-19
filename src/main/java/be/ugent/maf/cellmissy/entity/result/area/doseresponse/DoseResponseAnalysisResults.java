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

    //results of the fitting for the initial data
    private SigmoidFittingResultsHolder initialFittingResults;

    //results of the fitting for the normalized data
    private SigmoidFittingResultsHolder normalizedFittingResults;

    //50% effective concentration
    private double ec50Initial;
    private double ec50Normalized;

    //goodness of fit
    private double goodnessOfFitInitial;
    private double goodnessOfFitNormalized;

//    //standard error of the logEC50
//    private double standardErrorLogEC50Initial;
//
//    //array with lower and upper values of 95% confidence interval of logEC50
//    private Double[] confidenceIntervalLogEC50Initial;
//
//    //lower and upper values of 95% confidence interval of EC50
//    private Double[] confidenceIntervalEC50Initial;
//
//    //lower and upper values of 95% confidence interval of hillslope (if fitted)
//    private Double[] confidenceIntervalHillslopeInitial;
//
//    //same variables as above, only this time from fitting the normalized data
//    private double standardErrorLogEC50Normalized;
//
//    private Double[] confidenceIntervalLogEC50Normalized;
//
//    private Double[] confidenceIntervalEC50Normalized;
//
//    private Double[] confidenceIntervalHillslopeNormalized;
    public DoseResponseAnalysisResults() {
        this.initialFittingResults = new SigmoidFittingResultsHolder();
        this.normalizedFittingResults = new SigmoidFittingResultsHolder();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public SigmoidFittingResultsHolder getInitialFittingResults() {
        return initialFittingResults;
    }

    public SigmoidFittingResultsHolder getNormalizedFittingResults() {
        return normalizedFittingResults;
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

//    public double getStandardErrorLogEC50Initial() {
//        return standardErrorLogEC50Initial;
//    }
//
//    public void setStandardErrorLogEC50Initial(double standardErrorLogEC50Initial) {
//        this.standardErrorLogEC50Initial = standardErrorLogEC50Initial;
//    }
//
//    public Double[] getConfidenceIntervalLogEC50Initial() {
//        return confidenceIntervalLogEC50Initial;
//    }
//
//    public void setConfidenceIntervalLogEC50Initial(Double[] confidenceIntervalLogEC50Initial) {
//        this.confidenceIntervalLogEC50Initial = confidenceIntervalLogEC50Initial;
//    }
//
//    public Double[] getConfidenceIntervalEC50Initial() {
//        return confidenceIntervalEC50Initial;
//    }
//
//    public void setConfidenceIntervalEC50Initial(Double[] confidenceIntervalEC50Initial) {
//        this.confidenceIntervalEC50Initial = confidenceIntervalEC50Initial;
//    }
//
//    public Double[] getConfidenceIntervalHillslopeInitial() {
//        return confidenceIntervalHillslopeInitial;
//    }
//
//    public void setConfidenceIntervalHillslopeInitial(Double[] confidenceIntervalHillslopeInitial) {
//        this.confidenceIntervalHillslopeInitial = confidenceIntervalHillslopeInitial;
//    }

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

//    public double getStandardErrorLogEC50Normalized() {
//        return standardErrorLogEC50Normalized;
//    }
//
//    public void setStandardErrorLogEC50Normalized(double standardErrorLogEC50Normalized) {
//        this.standardErrorLogEC50Normalized = standardErrorLogEC50Normalized;
//    }
//
//    public Double[] getConfidenceIntervalLogEC50Normalized() {
//        return confidenceIntervalLogEC50Normalized;
//    }
//
//    public void setConfidenceIntervalLogEC50Normalized(Double[] confidenceIntervalLogEC50Normalized) {
//        this.confidenceIntervalLogEC50Normalized = confidenceIntervalLogEC50Normalized;
//    }
//
//    public Double[] getConfidenceIntervalEC50Normalized() {
//        return confidenceIntervalEC50Normalized;
//    }
//
//    public void setConfidenceIntervalEC50Normalized(Double[] confidenceIntervalEC50Normalized) {
//        this.confidenceIntervalEC50Normalized = confidenceIntervalEC50Normalized;
//    }
//
//    public Double[] getConfidenceIntervalHillslopeNormalized() {
//        return confidenceIntervalHillslopeNormalized;
//    }
//
//    public void setConfidenceIntervalHillslopeNormalized(Double[] confidenceIntervalHillslopeNormalized) {
//        this.confidenceIntervalHillslopeNormalized = confidenceIntervalHillslopeNormalized;
//    }

}
