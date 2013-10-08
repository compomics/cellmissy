/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.DistanceMetricOperator;

/**
 * This is one implementation for the distance metric operator: it's using the
 * Euclidean distance
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class EuclideanDistanceMetric implements DistanceMetricOperator {

    @Override
    public double computeDistanceMetric(Double[] firstVector, Double[] secondVector) {
        double distance = 0;
        for (int i = 0; i < firstVector.length; i++) {
            if (firstVector[i] != null && secondVector[i] != null) {
                double temp = Math.pow((firstVector[i] - secondVector[i]), 2);
                distance += temp;
            }
        }
        return Math.sqrt(distance);
    }
}
