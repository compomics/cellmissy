/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

/**
 * Holds all statistical values associated with a fit (initial or normalized).
 * @author Gwendolien Sergeant
 */
public class DoseResponseStatisticsHolder {
    
    //50% effective concentration
    private double ec50;

    //goodness of fit
    private double goodnessOfFit;

    // standard error of bottom
    private double stdErrBottom;

    // standard error of top
    private double stdErrTop;

    //standard error of the logEC50
    private double stdErrLogEC50;

    // standard error of the hillslope
    private double stdErrHillslope;

    //array with lower and upper values of 95% confidence interval of bottom
    private double[] cIBottom;

    //array with lower and upper values of 95% confidence interval of top
    private double[] cITop;

    //array with lower and upper values of 95% confidence interval of logEC50
    private double[] cILogEC50;

    //lower and upper values of 95% confidence interval of EC50
    private double[] cIEC50;

    //lower and upper values of 95% confidence interval of hillslope (if fitted)
    private double[] cIHillslope;

    /**
     * Getters and setters
     * @return 
     */
    public double getEc50() {
        return ec50;
    }

    public void setEc50(double ec50) {
        this.ec50 = ec50;
    }

    public double getGoodnessOfFit() {
        return goodnessOfFit;
    }

    public void setGoodnessOfFit(double goodnessOfFit) {
        this.goodnessOfFit = goodnessOfFit;
    }

    public double getStdErrBottom() {
        return stdErrBottom;
    }

    public void setStdErrBottom(double stdErrBottom) {
        this.stdErrBottom = stdErrBottom;
    }

    public double getStdErrTop() {
        return stdErrTop;
    }

    public void setStdErrTop(double stdErrTop) {
        this.stdErrTop = stdErrTop;
    }

    public double getStdErrLogEC50() {
        return stdErrLogEC50;
    }

    public void setStdErrLogEC50(double stdErrLogEC50) {
        this.stdErrLogEC50 = stdErrLogEC50;
    }

    public double getStdErrHillslope() {
        return stdErrHillslope;
    }

    public void setStdErrHillslope(double stdErrHillslope) {
        this.stdErrHillslope = stdErrHillslope;
    }

    public double[] getcIBottom() {
        return cIBottom;
    }

    public void setcIBottom(double[] cIBottom) {
        this.cIBottom = cIBottom;
    }

    public double[] getcITop() {
        return cITop;
    }

    public void setcITop(double[] cITop) {
        this.cITop = cITop;
    }

    public double[] getcILogEC50() {
        return cILogEC50;
    }

    public void setcILogEC50(double[] cILogEC50) {
        this.cILogEC50 = cILogEC50;
    }

    public double[] getcIEC50() {
        return cIEC50;
    }

    public void setcIEC50(double[] cIEC50) {
        this.cIEC50 = cIEC50;
    }

    public double[] getcIHillslope() {
        return cIHillslope;
    }

    public void setcIHillslope(double[] cIHillslope) {
        this.cIHillslope = cIHillslope;
    }
    
    
    
}
