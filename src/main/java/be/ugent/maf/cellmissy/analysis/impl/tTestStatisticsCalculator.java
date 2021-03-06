/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.StatisticsCalculator;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;

/**
 *
 * @author ninad
 */
public class tTestStatisticsCalculator implements StatisticsCalculator{

    //Calculate p-value
    @Override
    public double executeStatisticalTest(double[] x, double[] y) {
        TTest tTest = new TTest();
        return tTest.homoscedasticTTest(x, y);
    }
    
    //Get summary statistics (mean, min, max...) for the distribution of x
    @Override
    public StatisticalSummary getSummaryStatistics(double[] x) {
        SummaryStatistics summaryStatistics = new SummaryStatistics();
        for (double aX : x) {
            summaryStatistics.addValue(aX);
        }
        return summaryStatistics.getSummary();
    }

    //Determine significance
    @Override
    public boolean[][] detectSignificance(Double[][] data, double alpha) {
        boolean[][] significances = new boolean[data.length][data[0].length];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < data[0].length; columnIndex++) {
                Double pValue = data[rowIndex][columnIndex];
                // if p value is not null and it's less than alpha level, set boolean to true:
                // correspondent pairwise comparison is statistically significant
                if (pValue != null && pValue <= alpha) {
                    significances[rowIndex][columnIndex] = true;
                } 
            }
        }
        return significances;
    }


    
}
