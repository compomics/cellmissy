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
public interface OutliersHandler {

    /**
     * Handle the outliers
     * @param data
     * @return 
     */
    List<Double[]> handleOutliers(Double[] data);

    /**
     * 
     * @param data
     * @return 
     */
    boolean[][] detectOutliers(Double[][] data);
    
    /**
     * 
     * @param data
     * @return 
     */
    Double[][] correctForOutliers(Double[][] data);
    
}
