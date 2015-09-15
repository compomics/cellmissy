/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;

/**
 * An interface for a Convex Hull Calculator. The interface simply computes the
 * hull, and find the most distant points on it for a given track.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface ConvexHullCalculator {

    /**
     * Compute the hull, the real convex polygon associated to the track.
     *
     * @param track: the track we need to get the geometric points from
     * @param convexHull: the convex hull object for which the hull is being
     * calculated
     */
    void computeHull(Track track, ConvexHull convexHull);

    /**
     * Compute the size of the convex hull, i.e. the number of vertices.
     *
     * @param convexHull
     */
    void computeHullSize(ConvexHull convexHull);

    /**
     * Find the pair of geometric points that are most distant from each other
     * on a track. The distance between the two points will be the diameter (max
     * span) of the convex hull object.
     *
     * @param track
     * @param convexHull
     */
    void findMostDistantPoints(Track track, ConvexHull convexHull);
}