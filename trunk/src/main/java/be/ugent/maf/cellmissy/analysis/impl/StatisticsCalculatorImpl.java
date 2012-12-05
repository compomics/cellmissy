/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.StatisticsCalculator;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("statisticsCalculator")
public class StatisticsCalculatorImpl implements StatisticsCalculator{

    @Override
    public double executeWilcoxonTest(double[] x, double[] y) {
        
        WilcoxonSignedRankTest wilcoxonSignedRankTest = new WilcoxonSignedRankTest();
        double pValue = wilcoxonSignedRankTest.wilcoxonSignedRankTest(x, y, true);
        return pValue;
    }    
}
