/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.Point;
import be.ugent.maf.cellmissy.entity.Track;

/**
 * An interface for a Convex hull Calculator.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface ConvexHullCalculator {

    /**
     *
     * @param track
     * @return
     */
    public Iterable<Point> computeConvexHull(Track track);
}
