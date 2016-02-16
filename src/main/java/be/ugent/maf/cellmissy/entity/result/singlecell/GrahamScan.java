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
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>s
 */
public class GrahamScan {

    // the hull, in a form of a Stack
    private final Stack<GeometricPoint> hull = new Stack<>();
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GrahamScan.class);

    /**
     * Get the constructed hull
     *
     * @return
     */
    public Stack<GeometricPoint> getHull() {
        return hull;
    }

    /**
     * Constructor, this will create the hull.
     *
     * @param geometricPointList
     */
    public GrahamScan(List<GeometricPoint> geometricPointList) {
        constructHull(geometricPointList);
    }

    /**
     * Construct the Hull, given a list of geometric points.
     *
     * @param geometricPointList
     */
    private void constructHull(List<GeometricPoint> geometricPointList) {
        // how many points do we have?
        int n = geometricPointList.size();
        GeometricPoint[] geometricPoints = geometricPointList.toArray(new GeometricPoint[n]);
        // given that a vertex (extreme point) of the convex hull is a point with first a minimum (lowest) y coordinate, and second a maximum (rightmost) x coordinate,
        // we preprocess the points so that P0 (points[0]) has the lowest y-coordinate and we break ties by the x-coordinate
        Arrays.sort(geometricPoints); // sort by y coord
        // sort the reamining points by polar angle with respect to base point P0
        // breaking ties by distance to P0: if there is a tie and two points have the same angle, discard the one that is closest to P0
        Arrays.sort(geometricPoints, 1, n, geometricPoints[0].POLAR_COMPARATOR);
        hull.push(geometricPoints[0]); // P0 is the first extreme point, push it to the stack
        // we next loop through the points of the set one-by-one testing for convex hull vertices
        // find index k1 of first point not equal to P0, this is P1
        int k1;
        for (k1 = 1; k1 < n; k1++) {
            if (!geometricPoints[0].equals(geometricPoints[k1])) {
                break;
            }
        }
        if (k1 == n) {
            // all points are equal, just return
            return;
        }
        // find index k2 of first point not collinear with P0 and P1, this is P2
        int k2;
        for (k2 = k1 + 1; k2 < n; k2++) {
            if (GeometricPoint.counterClockWise(geometricPoints[0], geometricPoints[k1], geometricPoints[k2]) != 0) {
                break;
            } else {
                String info = "points: " + geometricPoints[0] + ", " + geometricPoints[k1] + ", " + geometricPoints[k2] + " are collinear on the plane.";
                LOG.info(info);
            }
        }
        hull.push(geometricPoints[k2 - 1]);    // points[k2-1] is the second extreme point, push it to the stack
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
