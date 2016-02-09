/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree.distance;

/**
 * An implementation of the distance metric implementing the Euclidean distance.
 *
 * @author Paola
 */
public class EuclideanDistance extends DistanceMetric {

    @Override
    public double distance(double[] a, double[] b) {
        return Math.sqrt(sqrdist(a, b));

    }

    public static double sqrdist(double[] a, double[] b) {
        double dist = 0;
        for (int i = 0; i < a.length; ++i) {
            double diff = (a[i] - b[i]);
            dist += diff * diff;
        }
        return dist;
    }
}
