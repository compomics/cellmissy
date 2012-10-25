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
            List<double[]> tempList = new ArrayList<>(); 
            for (int i = 0; i < data.length; i++) {
                if (data[i] != null) {
                    double[] temp = new double[2];
                    temp[0] = timeFrames[i];
                    temp[1] = data[i];
                    tempList.add(temp);
                }
            }
            double[][] tempArray = tempList.toArray(new double[tempList.size()][]);
            double slope = computeSlope(tempArray);
            double coefficient = computeRCoefficient(tempArray);
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
    private double computeSlope(double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(0);
    }

    /**
     * Get R2 Coefficient out of the Linear Regression Model
     * @param data
     * @return 
     */
    private double computeRCoefficient(double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(1);
    }
}
