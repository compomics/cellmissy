/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class keeps information of the fitting to a dose-response model.
 *
 * @author Gwendolien
 */
public class SigmoidFittingResultsHolder {

    //minimum response, best-fit value or constrained by user
    private double bottom;

    //maximum response, best-fit or constrained
    private double top;

    //best-fit value or standard hillslope of curve
    private double hillslope;

    //log of 50% effective concentration
    private double logEC50;

    //matrix of parameter covariances
    private double[][] covariances;

    //list of constrained parameter names
    private List<String> constrainedParameters;

    /**
     * Getters and setters
     *
     * @return
     */
    public double getBottom() {
        return bottom;
    }

    public void setBottom(double bottom) {
        this.bottom = bottom;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getHillslope() {
        return hillslope;
    }

    public void setHillslope(double hillslope) {
        this.hillslope = hillslope;
    }

    public double getLogEC50() {
        return logEC50;
    }

    public void setLogEC50(double logEC50) {
        this.logEC50 = logEC50;
    }

    public double[][] getCovariances() {
        return covariances;
    }

    public void setCovariances(double[][] covariances) {
        this.covariances = covariances;
    }

    public List<String> getConstrainedParameters() {
        return constrainedParameters;
    }

    public void setConstrainedParameters(List<String> constrainedParameters) {
        this.constrainedParameters = constrainedParameters;
    }

}
