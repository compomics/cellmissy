/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import java.util.List;

/**
 * Interface for Analysis of Area Data (after pre-processing step)
 * @author Paola Masuzzo
 */
public interface AreaAnalyzer {

    /**
     * Compute the slopes from Linear Regression Model
     * @param areaData
     * @param timeFrames
     * @param excludeReplicate 
     * @return an array with double values of slopes
     */
    public List<Double[]> computeSlopes(Double[][] areaData, double[] timeFrames, boolean[] excludeReplicate);
}
