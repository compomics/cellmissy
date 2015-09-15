/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 * Interface for statistics calculations.
 *
 * @author Paola Masuzzo
 */
public interface StatisticsCalculator {

    /**
     * Execute a statistical test.
     *
     * @param x
     * @param y
     * @return p-value associated with the test
     */
    double executeStatisticalTest(double[] x, double[] y);

    /**
     * Given data and alpha level, detect significance.
     *
     * @param data
     * @param alpha
     * @return a 2D array of boolean
     */
    boolean[][] detectSignificance(Double[][] data, double alpha);

    /**
     * Get Statistical summary for a distribution of doubles x.
     *
     * @param x
     * @return
     */
    StatisticalSummary getSummaryStatistics(double[] x);
}
