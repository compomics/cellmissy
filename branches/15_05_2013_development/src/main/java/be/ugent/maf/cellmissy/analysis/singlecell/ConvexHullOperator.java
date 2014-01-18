/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface ConvexHullOperator {

    /**
     * Compute the perimeter of the hull (in micro-meters). This is simply the
     * sum of the length of the edges of the hull.
     *
     * @param convexHull: the convex hull to compute the perimeter for
     */
    public void computePerimeter(ConvexHull convexHull);

    /**
     * Compute the Area of the hull (in micro-meters squared). Given the
     * coordinates listed in a counterclockwise order around the hull, the area
     * is simply computed using the determinant law.
     *
     * @param convexHull: the convex hull to compute the area for
     */
    public void computeArea(ConvexHull convexHull);

    /**
     * Compute the acircularity of the convex hull = (perimeter)^2 / 4*pi*area .
     *
     * @param convexHull
     */
    public void computeAcircularity(ConvexHull convexHull);

    /**
     * Compute dispersion for the convex hull: this is the diameter divided by
     * the area of the convex hull.
     *
     * @param convexHull
     */
    public void computeDirectionality(ConvexHull convexHull);
}
