/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree.distance;

/**
 *
 * @author Paola
 */
public abstract class DistanceMetric {

    public abstract double distance(double[] a, double[] b);
}
