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
    public List<Double> estimateLinearModel(double[][] data) {

        List<Double> linearModelResults = new ArrayList<>();
        //make a Simple Regression from input Data
        SimpleRegression simpleRegression = new SimpleRegression();
        simpleRegression.addData(data);
        //get Slopes and R2 coefficients fromt the Model
        linearModelResults.add(simpleRegression.getSlope());
        linearModelResults.add(simpleRegression.getRSquare());

        return linearModelResults;
    }
}
