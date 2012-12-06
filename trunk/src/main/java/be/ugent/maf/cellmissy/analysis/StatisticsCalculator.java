/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 *
 * @author Paola Masuzzo
 */
public interface StatisticsCalculator {

    /**
     * Pairwise comparisons using Wilcoxon rank sum test 
     * This is a Mann-Whitney U Test for samples that also have not equal size
     * @param x
     * @param y
     * @return p-value associated with the test 
     */
    public double executeMannWhitneyUTest(double[] x, double[] y);

    /**
     * Get Statistical summary for a distribution of doubles x
     * @param x
     * @return 
     */
    public StatisticalSummary getSummaryStatistics(double[] x);
    
    /**
     * Correct for Multiple Comparisons
     * @param pValuesMatrix
     * @return 
     */
    public Double[][] correctForMultipleComparisons(Double[][] pValuesMatrix);    
}
