/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import be.ugent.maf.cellmissy.entity.PlateCondition;
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
    public double[] computeSlopesPerCondition(PlateCondition plateCondition, double[][] areaData, double[] timeFrames) {
        double[] slopes = new double[plateCondition.getWellCollection().size()];

        for (int columnIndex = 0; columnIndex < areaData.length; columnIndex++) {
            double[] data = areaData[columnIndex];
            double[][] temp = new double[data.length][2];

            for (int i = 0; i < temp.length; i++) {
                temp[i][0] = timeFrames[i];
                temp[i][1] = data[i];
            }

            double slope = computeSlope(temp);
            slopes[columnIndex] = slope;
        }
        return slopes;
    }

    private double computeSlope(double[][] data) {
        return AnalysisUtils.roundTwoDecimals(linearRegressor.computeSlope(data));
    }
}
