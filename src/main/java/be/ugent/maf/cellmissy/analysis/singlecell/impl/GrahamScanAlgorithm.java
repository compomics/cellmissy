/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.ConvexHullCalculator;
import be.ugent.maf.cellmissy.entity.result.singlecell.Point;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.springframework.stereotype.Component;

/**
 * This class implements the convex hull calculator, using the Graham Scan
 * algorithm.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("grahamScanAlgorithm")
public class GrahamScanAlgorithm implements ConvexHullCalculator {

    @Override
    public void computeHull(Track track, ConvexHull convexHull) {
        GrahamScan grahamScan = new GrahamScan(track);
        Iterable<Point> hull = grahamScan.computeHull();
        convexHull.setHull(hull);
    }

    @Override
    public void computeFarthestPoints(Track track, ConvexHull convexHull) {
        FarthestPointsPair farthestPointsPair = new FarthestPointsPair(track, convexHull);
        List<Point> points = new ArrayList<>();
        points.add(farthestPointsPair.firstPoint);
        points.add(farthestPointsPair.secondPoint);
        convexHull.setFarthestPointsPair(points);
    }

    /**
     * Graham Scan
     */
    private class GrahamScan {

        // the hull, in a form of a Stack
        private Stack<Point> hull = new Stack<>();

        /**
         * Private Constructor
         *
         * @param track: the track to apply the graham algorithm on
         */
        private GrahamScan(Track track) {
            // create a list of points from the track points
            List<TrackPoint> trackPointList = track.getTrackPointList();
            List<Point> pointList = new ArrayList<>();
            for (TrackPoint trackPoint : trackPointList) {
                pointList.add(trackPoint.getPoint());
            }
            int n = pointList.size();
            Point[] points = pointList.toArray(new Point[n]);
            // given that a vertex (extreme point) of the convex hull is a point with first a minimum (lowest) y coordinate, and second a maximum (rightmost) x coordinate,
            // we preprocess the points so that P0 (points[0]) has the lowest y-coordinate and we break ties by the x-coordinate
            Arrays.sort(points); // sort by y coord
            // sort by polar angle with respect to base point P0
            // breaking ties by distance to P0: if there is a tie and two points have the same angle, discard the one that is closest to P0
            Arrays.sort(points, 1, n, points[0].POLAR_COMPARATOR);
            hull.push(points[0]); // P0 is the first extreme point
            // we next loop through the points of the set one-by-one testing for convex hull vertices
            // find index k1 of first point not equal to P0, this is P1
            int k1;
            for (k1 = 1; k1 < n; k1++) {
                if (!points[0].equals(points[k1])) {
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
                if (Point.counterClockWise(points[0], points[k1], points[k2]) != 0) {
                    break;
                }
            }
            hull.push(points[k2 - 1]);    // points[k2-1] is the second extreme point
            // Graham scan algorithm
            for (int i = k2; i < n; i++) {
                // get the top point of the stack and remove it
                Point top = hull.pop();
                // look at the turn between the point at the top of the stack, the top that we just removed and the current i-point
                // while this turn is NOT counterclockwise, remove the point at the top of the stack
                while (Point.counterClockWise(hull.peek(), top, points[i]) <= 0) {
                    top = hull.pop();
                }
                // put back the top into the stack
                hull.push(top);
                // add the i-point to the stack
                hull.push(points[i]);
            }
            assert isConvex();
        }

        /**
         * Return extreme points on convex hull in counterclockwise order as an
         * Iterable.
         *
         * @return
         */
        private Iterable<Point> computeHull() {
            Stack<Point> s = new Stack<>();
            for (Point point : hull) {
                s.push(point);
            }
            return s;
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
            Point[] points = new Point[n];
            int j = 0;
            Iterable<Point> computeHull = computeHull();
            for (Point p : computeHull) {
                points[j++] = p;
            }
            for (int i = 0; i < j; i++) {
                if (Point.counterClockWise(points[i], points[(i + 1) % j], points[(i + 2) % j]) <= 0) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     *
     */
    private class FarthestPointsPair {

        // first and second points of track, along with distance between them
        private Point firstPoint;
        private Point secondPoint;
        private double maxSpan;

        /**
         * Constructor
         *
         * @param track: the track on which the farthest points pair is computed
         * @param convexHull : the convex hull of the track
         */
        public FarthestPointsPair(Track track, ConvexHull convexHull) {
            computePoints(track, convexHull);
        }

        /**
         * Given a track, compute the two most distant track points. Compute the
         * convex hull of the track and then look for the two farthest points
         * among the vertices of the hull.
         *
         * @param track
         */
        private void computePoints(Track track, ConvexHull convexHull) {
            List<TrackPoint> trackPointList = track.getTrackPointList();
            // only one point, return
            if (trackPointList.size() <= 1) {
                return;
            }
            // number of points on the hull
            int M = 0;
            for (Point point : convexHull.getHull()) {
                M++;
            }
            // the hull, in counterclockwise order
            Point[] hull = new Point[M];
            int m = 0;
            for (Point point : convexHull.getHull()) {
                hull[m++] = point;
            }
            // all points are equal
            if (M == 1) {
                return;
            }
            // points are collinear
            if (M == 2) {
                firstPoint = hull[0];
                secondPoint = hull[1];
                maxSpan = firstPoint.euclideanDistanceTo(secondPoint);
                return;
            }

            // check for the greatest distance
            for (Point point : hull) {
                for (int i = 0; i < hull.length; i++) {
                    Point other = hull[i];
                    if (!point.equals(other)) {
                        double euclideanDistance = point.euclideanDistanceTo(other);
                        if (euclideanDistance > maxSpan) {
                            firstPoint = point;
                            secondPoint = other;
                            maxSpan = euclideanDistance;
                        }
                    }
                }
            }
        }
    }
}
