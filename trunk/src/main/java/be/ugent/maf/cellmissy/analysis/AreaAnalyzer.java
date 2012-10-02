/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.PlateCondition;

/**
 *
 * @author Paola Masuzzo
 */
public interface AreaAnalyzer {

    public double[] computeSlopesPerCondition(double[][] areaData, PlateCondition plateCondition, double[] timeFrames);

    public double computeMean(double[] data);

    public double computeStandardDeviation(double[] data);
}
