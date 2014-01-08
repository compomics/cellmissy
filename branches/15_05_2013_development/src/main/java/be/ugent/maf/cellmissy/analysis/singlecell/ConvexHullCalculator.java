/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;

/**
 * An interface for a Convex Hull Calculator. The Convex Hull for a set of
 * Points is the smallest convex set (polygon) that contains the set of points.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface ConvexHullCalculator {

    public void computeHull(Track track, ConvexHull convexHull);

    public void findMostDistantPoints(Track track, ConvexHull convexHull);
}