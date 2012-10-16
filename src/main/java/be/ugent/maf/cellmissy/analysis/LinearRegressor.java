/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import java.util.List;

/**
 *
 * @author Paola Masuzzo
 */
public interface LinearRegressor {

    /**
     * Given a 2D array of double, estimate linear model
     * @param data
     * @return a list of double containing: slopes and R2 coefficients
     */
    public List<Double> estimateLinearModel(double[][] data);
}
