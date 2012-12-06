/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.StatisticsCalculator;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.springframework.stereotype.Component;

/**
 * Implementation for Statistics Calculator
 * @author Paola Masuzzo
 */
@Component("statisticsCalculator")
public class StatisticsCalculatorImpl implements StatisticsCalculator {

    @Override
    public double executeMannWhitneyUTest(double[] x, double[] y) {

        MannWhitneyUTest mannWhitneyUTest = new MannWhitneyUTest();
        double pValue = mannWhitneyUTest.mannWhitneyUTest(x, y);
        return pValue;
    }

    @Override
    public StatisticalSummary getSummaryStatistics(double[] x) {
        SummaryStatistics summaryStatistics = new SummaryStatistics();
        for (int i = 0; i < x.length; i++) {
            summaryStatistics.addValue(x[i]);
        }
        StatisticalSummary summary = summaryStatistics.getSummary();
        return summary;
    }

    @Override
    public Double[][] correctForMultipleComparisons(Double[][] pValuesMatrix) {
        Double[][] adjustedPValues = new Double[pValuesMatrix.length][pValuesMatrix.length];
        int numberOfComparisons = (pValuesMatrix.length) * (pValuesMatrix.length - 1) / 2;
        for (int i = 0; i < pValuesMatrix.length; i++) {
            for (int j = 0; j < pValuesMatrix[0].length; j++) {
                if (pValuesMatrix[i][j] != null) {
                    adjustedPValues[i][j] = pValuesMatrix[i][j] * numberOfComparisons;
                }
            }
        }
        return adjustedPValues;
    }
}
