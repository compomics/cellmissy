/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("linearRegressor")
public class LinearRegressorImpl implements LinearRegressor {

    @Override
    public List<Double> estimateLinearModel(Double[][] data) {

        //make primitive data from Double
        double[][] doubleValues = new double[data.length][data[0].length];
        for(int columnIndex = 0; columnIndex < data.length; columnIndex ++){
            for(int rowIndex = 0; rowIndex < data[0].length; rowIndex ++){
                doubleValues[columnIndex][rowIndex] = data[columnIndex][rowIndex].doubleValue();
            }
        }
        List<Double> linearModelResults = new ArrayList<>();
        //make a Simple Regression from input Data
        SimpleRegression simpleRegression = new SimpleRegression();
        simpleRegression.addData(doubleValues);
        //get Slopes and R2 coefficients fromt the Model
        linearModelResults.add(simpleRegression.getSlope());
        linearModelResults.add(simpleRegression.getRSquare());

        return linearModelResults;
    }
}
