/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

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
    // the size of the hull
    private int hullSize;
    // the most distant points on the hull
    private MostDistantPointsPair mostDistantPointsPair;
    // the perimeter of the hull
    private double perimeter;
    // the area of the hull
    private double area;
    // the acircularity
    private double acircularity;
    // the directionality
    private double directionality;

    /**
     * Getters and setters
     * @return
     */
    public Iterable<GeometricPoint> getHull() {
        return hull;
    }

    public void setHull(Iterable<GeometricPoint> hull) {
        this.hull = hull;
    }

    public int getHullSize() {
        return hullSize;
    }

    public void setHullSize(int hullSize) {
        this.hullSize = hullSize;
    }

    public MostDistantPointsPair getMostDistantPointsPair() {
        return mostDistantPointsPair;
    }

    public void setMostDistantPointsPair(MostDistantPointsPair mostDistantPointsPair) {
        this.mostDistantPointsPair = mostDistantPointsPair;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(double perimeter) {
        this.perimeter = perimeter;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getAcircularity() {
        return acircularity;
    }

    public void setAcircularity(double acircularity) {
        this.acircularity = acircularity;
    }

    public double getDirectionality() {
        return directionality;
    }

    public void setDirectionality(double directionality) {
        this.directionality = directionality;
    }
}
