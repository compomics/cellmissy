/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import java.util.List;

/**
 * Object Convex Hull: the convex hull or convex envelope of a set X of points
 * in the Euclidean plane or Euclidean space is the smallest convex set
 * (polygon) that contains X.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class ConvexHull {

    // the hull, as an iterable of points
    private Iterable<GeometricPoint> hull;
    // the most distant points on the hull
    private MostDistantPointsPair mostDistantPointsPair;
    // the area of the hull
    private double area;
    // the perimeter of the hull
    private double perimeter;

    /**
     * Getters and setters
     */
    public Iterable<GeometricPoint> getHull() {
        return hull;
    }

    public void setHull(Iterable<GeometricPoint> hull) {
        this.hull = hull;
    }

    public MostDistantPointsPair getMostDistantPointsPair() {
        return mostDistantPointsPair;
    }

    public void setMostDistantPointsPair(MostDistantPointsPair mostDistantPointsPair) {
        this.mostDistantPointsPair = mostDistantPointsPair;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(double perimeter) {
        this.perimeter = perimeter;
    }
}
