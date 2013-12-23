/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.result.singlecell.Point;
import be.ugent.maf.cellmissy.entity.Track;

/**
 * An interface for a Convex Hull Calculator. The Convex Hull for a set of
 * Points is the smallest convex set (polygon) that contains the set of points.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface ConvexHullCalculator {

    /**
     * Given a track, and thus a list of points (cell based points) that make up
     * this track, compute the convex hull for this set.
     *
     * @param track
     * @return
     */
    public Iterable<Point> computeConvexHull(Track track);
}
