/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("areaAnalyzer")
public class AreaAnalyzerImpl implements AreaAnalyzer {

    @Autowired
    private LinearRegressor linearRegressor;

    @Override
    public List<double[]> computeSlopes(Double[][] areaData, double[] timeFrames) {
        List<double[]> resultsList = new ArrayList<>();
        double[] slopes = new double[areaData.length];
        double[] coefficients = new double[areaData.length];

        for (int columnIndex = 0; columnIndex < areaData.length; columnIndex++) {
            Double[] data = areaData[columnIndex];
            Double[][] temp = new Double[data.length][2];
            for (int i = 0; i < temp.length; i++) {
                temp[i][0] = timeFrames[i];
                temp[i][1] = data[i];
            }
            double slope = computeSlope(temp);
            double coefficient = computeRCoefficient(temp);
            slopes[columnIndex] = slope;
            coefficients[columnIndex] = coefficient;
        }
        
        resultsList.add(slopes);
        resultsList.add(coefficients);
        
        return resultsList;
    }

    /**
     * Given 2D array of double compute Slope through a Linear Regression
     * @param data
     * @return 
     */
    private double computeSlope(Double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(0);
    }

    /**
     * Get R2 Coefficient out of the Linear Regression Model
     * @param data
     * @return 
     */
    private double computeRCoefficient(Double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(1);
    }
}
