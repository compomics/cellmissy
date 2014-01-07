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
    private Iterable<Point> hull;
    // the farthest points on the hull, as a list of two points
    private List<Point> farthestPointsPair;
    // the area of the hull
    private double area;
    // the perimeter of the hull
    private double perimeter;

    /**
     * Getters and setters
     */
    public Iterable<Point> getHull() {
        return hull;
    }

    public void setHull(Iterable<Point> hull) {
        this.hull = hull;
    }

    public List<Point> getFarthestPointsPair() {
        return farthestPointsPair;
    }

    public void setFarthestPointsPair(List<Point> farthestPointsPair) {
        this.farthestPointsPair = farthestPointsPair;
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
