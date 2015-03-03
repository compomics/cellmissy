/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.Comparator;

/**
 * GeometricPoint object. This is a point with both Cartesian and Polar
 * representation.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class GeometricPoint implements Comparable<GeometricPoint> {

    // x coordinate
    private final double x;
    // y coordinate
    private final double y;
    // a polar comparator for the point: compare three points according to the angle they make
    public final Comparator<GeometricPoint> POLAR_COMPARATOR = new PolarComparator();

    /**
     * Public constructor: takes x and y coordinates.
     *
     * @param x
     * @param y
     */
    public GeometricPoint(double x, double y) {
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
     * Get the angle theta in polar coordinates.
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
    public double euclideanDistanceTo(GeometricPoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.hypot(dx, dy);
    }

    /**
     * Given 3 points on the plane, is q-r-s a counterclockwise turn? This is
     * determined using the signed area the three points form.
     *
     * @param q
     * @param r
     * @param s
     * @return { -1, 0, +1 } if q-r-s is a { clockwise, collinear,
     * counterclockwise } turn.
     */
    public static int counterClockWise(GeometricPoint q, GeometricPoint r, GeometricPoint s) {
        double area = computeSignedArea(q, r, s);
        if (area < 0) {
            return -1; // the segment q-s is counterclockwise with respect to q-r (at r we make a left turn)
        } else if (area > 0) {
            return +1; // the segment q-s is clockwise with respect to q-r (at r we make a right turn)
        } else {
            return 0; // the 3 points are collinear
        }
    }

    /**
     * Returns twice the signed area of the triangle whose vertices are q, r, s.
     * This is also the area of the parallelogram whose vertices are q, r, s,
     * q+r+s. This is also the determinant of the matrix containing the
     * coordinates of the 3 points, i.e. the cross product q x r x s.
     *
     * @param q first point
     * @param r second point
     * @param s third point
     * @return twice the signed area of the triangle q-r-s.
     */
    private static double computeSignedArea(GeometricPoint q, GeometricPoint r, GeometricPoint s) {
        return (r.getX() - q.getX()) * (s.getY() - q.getY()) - (r.getY() - q.getY()) * (s.getX() - q.getX());
    }

    /**
     * Compares this point to another point by y-coordinate, breaking ties by
     * x-coordinate.
     *
     * @param o the other point
     * @return {-1, 0, +1} if this point is {less than, equal to, greater than}
     * that point
     */
    @Override
    public int compareTo(GeometricPoint o) {
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

    /**
     * Round to three decimals in the toString method
     *
     * @return
     */
    @Override
    public String toString() {
        Double roundedX = AnalysisUtils.roundThreeDecimals(x);
        Double roundedY = AnalysisUtils.roundThreeDecimals(y);
        return "(" + roundedX + ", " + roundedY + ")";
    }

    /**
     * Equals method.
     *
     * @param
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        GeometricPoint other = (GeometricPoint) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    /**
     * Compare other points (q, r) relative to the polar angle (between 0 and 2pi) they
     * make with this point.
     */
    private class PolarComparator implements Comparator<GeometricPoint> {

        @Override
        public int compare(GeometricPoint q, GeometricPoint r) {
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
                return -counterClockWise(GeometricPoint.this, q, r);
            }
        }
    }
}
