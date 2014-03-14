/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * A Graham Scan class. For a list of geometric points on the plane, it
 * calculates the hull of the set associated to the list.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class GrahamScan {

    // the hull, in a form of a Stack
    private Stack<GeometricPoint> hull = new Stack<>();

    /**
     * Get the constructed hull
     */
    public Stack<GeometricPoint> getHull() {
        return hull;
    }

    /**
     * Constructor
     *
     * @param track
     */
    public GrahamScan(List<GeometricPoint> geometricPointList) {
        constructHull(geometricPointList);
    }

    /**
     * Construct the Hull
     *
     * @param geometricPointList
     */
    private void constructHull(List<GeometricPoint> geometricPointList) {
        int n = geometricPointList.size();
        GeometricPoint[] geometricPoints = geometricPointList.toArray(new GeometricPoint[n]);
        // given that a vertex (extreme point) of the convex hull is a point with first a minimum (lowest) y coordinate, and second a maximum (rightmost) x coordinate,
        // we preprocess the points so that P0 (points[0]) has the lowest y-coordinate and we break ties by the x-coordinate
        Arrays.sort(geometricPoints); // sort by y coord
        // sort by polar angle with respect to base point P0
        // breaking ties by distance to P0: if there is a tie and two points have the same angle, discard the one that is closest to P0
        Arrays.sort(geometricPoints, 1, n, geometricPoints[0].POLAR_COMPARATOR);
        hull.push(geometricPoints[0]); // P0 is the first extreme point
        // we next loop through the points of the set one-by-one testing for convex hull vertices
        // find index k1 of first point not equal to P0, this is P1
        int k1;
        for (k1 = 1; k1 < n; k1++) {
            if (!geometricPoints[0].equals(geometricPoints[k1])) {
                break;
            }
        }
        if (k1 == n) {
            // all points are equal, return
            return;
        }
        // find index k2 of first point not collinear with P0 and P1, this is P2
        int k2;
        for (k2 = k1 + 1; k2 < n; k2++) {
            if (GeometricPoint.counterClockWise(geometricPoints[0], geometricPoints[k1], geometricPoints[k2]) != 0) {
                break;
            } else {
                System.out.println("points: " + geometricPoints[0] + ", " + geometricPoints[k1] + ", " + geometricPoints[k2] + " are collinear on the plane.");
            }
        }
        hull.push(geometricPoints[k2 - 1]);    // points[k2-1] is the second extreme point
        // Graham scan algorithm
        for (int i = k2; i < n; i++) {
            // get the top point of the stack and remove it
            GeometricPoint topPoint = hull.pop();
            // look at the turn between the point at the top of the stack, the top that we just removed and the current i-point
            // while this turn is NOT counterclockwise, remove the point at the top of the stack
            while (GeometricPoint.counterClockWise(hull.peek(), topPoint, geometricPoints[i]) <= 0) {
                topPoint = hull.pop();
            }
            // put back the top into the stack
            hull.push(topPoint);
            // add the i-point to the stack
            hull.push(geometricPoints[i]);
        }
        assert isConvex();
    }

    /**
     * Check that boundary of hull is strictly convex. We check for possible
     * clockwise turns: if there is any, the polygon is not convex!
     *
     * @return
     */
    private boolean isConvex() {
        int n = hull.size();
        if (n <= 2) {
            return true;
        }
        GeometricPoint[] points = new GeometricPoint[n];
        int j = 0;
        for (GeometricPoint geometricPoint : hull) {
            points[j++] = geometricPoint;
        }
        for (int i = 0; i < j; i++) {
            if (GeometricPoint.counterClockWise(points[i], points[(i + 1) % j], points[(i + 2) % j]) <= 0) {
                return false;
            }
        }
        return true;
    }
}
