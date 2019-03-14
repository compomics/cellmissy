/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree.distance;

/**
 *
 * @author ninad
 */
public class AccumulatedDistance {
    
    // a moet som van alle delta z's worden
    
    public double distance(double[] a) {
        double dist = 0;
        for (int i = 0; i < a.length; i++){
            dist += a[i];
        }
        return dist;
    } 
}
