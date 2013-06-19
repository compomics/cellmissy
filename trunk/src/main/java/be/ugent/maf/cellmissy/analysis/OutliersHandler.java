/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 * This interface is taking care of outliers. It has two methods, one for the
 * detection of them, and one for the correction of the data points.
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
}
