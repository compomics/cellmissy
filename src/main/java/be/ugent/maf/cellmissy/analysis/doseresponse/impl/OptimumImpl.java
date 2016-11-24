/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.doseresponse.impl;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Gwendolien
 */
public class OptimumImpl implements Optimum {

    /**
     * abscissa and ordinate
     */
    private final Evaluation value;
    /**
     * number of evaluations to compute this optimum
     */
    private final int evaluations;
    /**
     * number of iterations to compute this optimum
     */
    private final int iterations;

    /**
     * Distribution of the estimated parameters
     */
    private HashMap<Integer, ArrayList<Double>> parameterDistr;

    /**
     * Construct an optimum from an evaluation and the values of the counters.
     *
     * @param value the function value
     * @param evaluations number of times the function was evaluated
     * @param iterations number of iterations of the algorithm
     */
    OptimumImpl(final Evaluation value, final int evaluations, final int iterations, HashMap<Integer, ArrayList<Double>> parameterDistr) {
        this.value = value;
        this.evaluations = evaluations;
        this.iterations = iterations;
        this.parameterDistr = parameterDistr;
    }

    @Override
    public int getEvaluations() {
        return evaluations;
    }

    @Override
    public int getIterations() {
        return iterations;
    }

    public HashMap<Integer, ArrayList<Double>> getParameterDistr() {
        return parameterDistr;
    }

    @Override
    public RealMatrix getCovariances(double d) {
        return value.getCovariances(d);
    }

    @Override
    public RealVector getSigma(double d) {
        return value.getSigma(d);
    }

    @Override
    public double getRMS() {
        return value.getRMS();
    }

    @Override
    public RealMatrix getJacobian() {
        return value.getJacobian();
    }

    @Override
    public double getCost() {
        return value.getCost();
    }

    @Override
    public RealVector getResiduals() {
        return value.getResiduals();
    }

    @Override
    public RealVector getPoint() {
        return value.getPoint();
    }

}
