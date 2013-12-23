/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.util.Comparator;

/**
 * Point object. This is a point with both Cartesian and Polar representation.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class Point implements Comparable<Point> {

    // x coordinate
    private double x;
    // y coordinate
    private double y;
    //
    public final Comparator<Point> POLAR_COMPARATOR = new PolarComparator();

    /**
     * Public constructor: takes x and y coordinates.
     *
     * @param x
     * @param y
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getters
     */
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Get the radius in polar coordinates.
     *
     * @return sqrt(x*x + y*y)
     */
    public double getPolarRadius() {
        return Math.hypot(x, y);
    }

    /**
     * Returns the angle getTheta in polar coordinates.
     *
     * @return the angle in radians (between -pi/2 and pi/2)
     */
    public double getTheta() {
        return Math.atan2(y, x);
    }

    /**
     * Euclidean distance from one point to another one.
     *
     * @param other
     * @return
     */
    public double euclideanDistanceTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.hypot(dx, dy);
    }

    /**
     * Given three points on the plane, is q-r-s a counterclockwise turn?
     *
     * @param q
     * @param r
     * @param s
     * @return { -1, 0, +1 } if q-r-s is a { clockwise, collinear,
     * counterclockwise } turn.
     */
    public static int counterClockWise(Point q, Point r, Point s) {
        double area = computeSignedArea(q, r, s);
        if (area < 0) {
            return -1;
        } else if (area > 0) {
            return +1;
        } else {
            return 0;
        }
    }

    /**
     * Returns twice the signed area of the triangle q, r, s.
     *
     * @param a first point
     * @param b second point
     * @param c third point
     * @return twice the signed area of the triangle a-b-c
     */
    public static double computeSignedArea(Point q, Point r, Point s) {
        return (r.getX() - q.getX()) * (s.getY() - q.getY()) - (r.getY() - q.getY()) * (s.getX() - q.getX());
    }

    /**
     * Compares this point to another point by y-coordinate, breaking ties by
     * x-coordinate.
     *
     * @param o the other point
     * @return { a negative integer, zero, a positive integer } if this point is
     * { less than, equal to, greater than } that point
     */
    @Override
    public int compareTo(Point o) {
        if (this.y < o.y) {
            return -1;
        } else if (this.y > o.y) {
            return +1;
        } else if (this.x < o.x) {
            return -1;
        } else if (this.x > o.x) {
            return +1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Equals method.
     *
     * @param
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        Point that = (Point) other;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    /**
     * Compare other points relative to polar angle (between 0 and 2pi) they
     * make with this point.
     */
    private class PolarComparator implements Comparator<Point> {

        @Override
        public int compare(Point q, Point r) {
            double dx1 = q.getX() - x;
            double dy1 = q.getY() - y;
            double dx2 = r.getX() - x;
            double dy2 = r.getY() - y;
            if (dy1 >= 0 && dy2 < 0) { //q above and r below
                return -1;
            } else if (dy2 >= 0 && dy1 < 0) { //q below and r above
                return +1;
            } else if (dy1 == 0 && dy2 == 0) { //3-collinear and horizontal
                if (dx1 >= 0 && dx2 < 0) { //q right and r left
                    return -1;
                } else if (dx2 >= 0 && dx1 < 0) { //q left and r right
                    return +1;
                } else {
                    return 0;
                }
            } else {
                // both above or below
                return -counterClockWise(Point.this, q, r);
            }
        }
    }
}
