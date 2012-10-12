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

    public double[] computeSlopesPerCondition(PlateCondition plateCondition, double[][] areaData, double[] timeFrames);
}
