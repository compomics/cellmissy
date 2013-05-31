/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 * This interface is taking care of outliers for the area analysis
 *
 * @author Paola Masuzzo
 */
public interface OutliersHandler {

    /**
     * Detect outliers
     *
     * @param data
     * @return a boolean matrix (true if data point is outlier, false if not)
     */
    boolean[][] detectOutliers(Double[][] data);

    /**
     * Making use of the boolean matrix, correct data set for outliers
     *
     * @param data
     * @return
     */
    Double[][] correctForOutliers(Double[][] data);

    /**
     *
     * @param data
     * @return
     */
    boolean[] detectOutliers(Double[] data);

    /**
     * 
     * @param data
     * @return 
     */
    Double[] correctForOutliers(Double[] data);
}
