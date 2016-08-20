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

    // standard error of bottom
    private double stdErrBottomInitial;

    // standard error of top
    private double stdErrTopInitial;

    //standard error of the logEC50
    private double stdErrLogEC50Initial;

    // standard error of the hillslope
    private double stdErrHillslopeInitial;

    //array with lower and upper values of 95% confidence interval of bottom
    private double[] cIBottomInitial;

    //array with lower and upper values of 95% confidence interval of top
    private double[] cITopInitial;

    //array with lower and upper values of 95% confidence interval of logEC50
    private double[] cILogEC50Initial;

    //lower and upper values of 95% confidence interval of EC50
    private double[] cIEC50Initial;

    //lower and upper values of 95% confidence interval of hillslope (if fitted)
    private double[] cIHillslopeInitial;

    //same variables as above, only this time from fitting the normalized data
    private double stdErrBottomNormalized;
    private double stdErrTopNormalized;
    private double stdErrLogEC50Normalized;
    private double stdErrHillslopeNormalized;
    private double[] cIBottomNormalized;
    private double[] cITopNormalized;
    private double[] cILogEC50Normalized;
    private double[] cIEC50Normalized;
    private double[] cIHillslopeNormalized;

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

    public double getStdErrBottomInitial() {
        return stdErrBottomInitial;
    }

    public void setStdErrBottomInitial(double stdErrBottomInitial) {
        this.stdErrBottomInitial = stdErrBottomInitial;
    }

    public double getStdErrTopInitial() {
        return stdErrTopInitial;
    }

    public void setStdErrTopInitial(double stdErrTopInitial) {
        this.stdErrTopInitial = stdErrTopInitial;
    }

    public double getStdErrLogEC50Initial() {
        return stdErrLogEC50Initial;
    }

    public void setStdErrLogEC50Initial(double stdErrLogEC50Initial) {
        this.stdErrLogEC50Initial = stdErrLogEC50Initial;
    }

    public double getStdErrHillslopeInitial() {
        return stdErrHillslopeInitial;
    }

    public void setStdErrHillslopeInitial(double stdErrHillslopeInitial) {
        this.stdErrHillslopeInitial = stdErrHillslopeInitial;
    }

    public double[] getcIBottomInitial() {
        return cIBottomInitial;
    }

    public void setcIBottomInitial(double[] cIBottomInitial) {
        this.cIBottomInitial = cIBottomInitial;
    }

    public double[] getcITopInitial() {
        return cITopInitial;
    }

    public void setcITopInitial(double[] cITopInitial) {
        this.cITopInitial = cITopInitial;
    }

    public double[] getcILogEC50Initial() {
        return cILogEC50Initial;
    }

    public void setcILogEC50Initial(double[] cILogEC50Initial) {
        this.cILogEC50Initial = cILogEC50Initial;
    }

    public double[] getcIEC50Initial() {
        return cIEC50Initial;
    }

    public void setcIEC50Initial(double[] cIEC50Initial) {
        this.cIEC50Initial = cIEC50Initial;
    }

    public double[] getcIHillslopeInitial() {
        return cIHillslopeInitial;
    }

    public void setcIHillslopeInitial(double[] cIHillslopeInitial) {
        this.cIHillslopeInitial = cIHillslopeInitial;
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

    public double getStdErrBottomNormalized() {
        return stdErrBottomNormalized;
    }

    public void setStdErrBottomNormalized(double stdErrBottomNormalized) {
        this.stdErrBottomNormalized = stdErrBottomNormalized;
    }

    public double getStdErrTopNormalized() {
        return stdErrTopNormalized;
    }

    public void setStdErrTopNormalized(double stdErrTopNormalized) {
        this.stdErrTopNormalized = stdErrTopNormalized;
    }

    public double getStdErrLogEC50Normalized() {
        return stdErrLogEC50Normalized;
    }

    public void setStdErrLogEC50Normalized(double stdErrLogEC50Normalized) {
        this.stdErrLogEC50Normalized = stdErrLogEC50Normalized;
    }

    public double getStdErrHillslopeNormalized() {
        return stdErrHillslopeNormalized;
    }

    public void setStdErrHillslopeNormalized(double stdErrHillslopeNormalized) {
        this.stdErrHillslopeNormalized = stdErrHillslopeNormalized;
    }

    public double[] getcIBottomNormalized() {
        return cIBottomNormalized;
    }

    public void setcIBottomNormalized(double[] cIBottomNormalized) {
        this.cIBottomNormalized = cIBottomNormalized;
    }

    public double[] getcITopNormalized() {
        return cITopNormalized;
    }

    public void setcITopNormalized(double[] cITopNormalized) {
        this.cITopNormalized = cITopNormalized;
    }

    public double[] getcILogEC50Normalized() {
        return cILogEC50Normalized;
    }

    public void setcILogEC50Normalized(double[] cILogEC50Normalized) {
        this.cILogEC50Normalized = cILogEC50Normalized;
    }

    public double[] getcIEC50Normalized() {
        return cIEC50Normalized;
    }

    public void setcIEC50Normalized(double[] cIEC50Normalized) {
        this.cIEC50Normalized = cIEC50Normalized;
    }

    public double[] getcIHillslopeNormalized() {
        return cIHillslopeNormalized;
    }

    public void setcIHillslopeNormalized(double[] cIHillslopeNormalized) {
        this.cIHillslopeNormalized = cIHillslopeNormalized;
    }

}
