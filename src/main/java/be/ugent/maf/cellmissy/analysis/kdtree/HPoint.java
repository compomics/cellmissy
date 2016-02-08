/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree;

import be.ugent.maf.cellmissy.analysis.kdtree.distance.EuclideanDistance;
import java.io.Serializable;

/**
 * This is a class representing an Hyper-Point to support the KDTree class.
 *
 * @author Paola
 */
public class HPoint implements Serializable {

    protected double[] coord;

    protected HPoint(int n) {
        coord = new double[n];
    }

    protected HPoint(double[] x) {
        coord = new double[x.length];
        for (int i = 0; i < x.length; ++i) {
            coord[i] = x[i];
        }
    }

    @Override
    protected Object clone() {
        return new HPoint(coord);
    }

    protected boolean equals(HPoint p) {
        for (int i = 0; i < coord.length; ++i) {
            if (coord[i] != p.coord[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * The square distance.
     *
     * @param x
     * @param y
     * @return
     */
    protected static double sqrdist(HPoint x, HPoint y) {
        return EuclideanDistance.sqrdist(x.coord, y.coord);
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < coord.length; ++i) {
            s = s + coord[i] + " ";
        }
        return s;
    }
}
