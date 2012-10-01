/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("linearRegressor")
public class LinearRegressorImpl implements LinearRegressor{

    private SimpleRegression simpleRegression;
    
    @Override
    public double computeSlope(double[][] data) {
        
        simpleRegression = new SimpleRegression();
        
        simpleRegression.addData(data);
        return simpleRegression.getSlope();        
    }
    
}
