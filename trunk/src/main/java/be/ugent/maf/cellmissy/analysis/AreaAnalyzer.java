/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 *
 * @author Paola Masuzzo
 */
public interface AreaAnalyzer {

    /**
     * 
     * @param areaData
     * @param timeFrames
     * @return
     */
    public double[] computeSlopes(double[][] areaData, double[] timeFrames);
}