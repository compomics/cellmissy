/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 * Interface for the distance metric between two vectors.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface DistanceMetricOperator {

    /**
     * Compute the distance metric between two vectors of data points; these are
     * double.
     *
     * @param firstVector
     * @param secondVector
     * @return
     */
    public double computeDistanceMetric(Double[] firstVector, Double[] secondVector);
}
