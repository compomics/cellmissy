/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.ConvexHullCalculator;
import be.ugent.maf.cellmissy.entity.Point;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
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
    public Iterable<Point> computeConvexHull(Track track) {
        GrahamScan grahamScan = new GrahamScan(track);
        return grahamScan.computeHull();
    }

    /**
     * Graham Scan
     */
    private class GrahamScan {

        private Stack<Point> hull = new Stack<>();

        /**
         * Constructor
         *
         * @param track
         */
        private GrahamScan(Track track) {
            List<TrackPoint> trackPointList = track.getTrackPointList();
            List<Point> pointList = new ArrayList<>();
            for (TrackPoint trackPoint : trackPointList) {
                pointList.add(trackPoint.getPoint());
            }
            int n = pointList.size();
            Point[] points = pointList.toArray(new Point[n]);
            // preprocess so that points[0] has lowest y-coordinate; break ties by x-coordinate
            // points[0] is an extreme point of the convex hull
            Arrays.sort(points);
            // sort by polar angle with respect to base point points[0],
            // breaking ties by distance to points[0]
            Arrays.sort(points, 1, n, points[0].POLAR_COMPARATOR);
            hull.push(points[0]); // points[0] is the first extreme point
            // find index k1 of first point not equal to points[0]
            int k1;
            for (k1 = 1; k1 < n; k1++) {
                if (!points[0].equals(points[k1])) {
                    break;
                }
            }
            if (k1 == n) {
                // all points equal
                return;
            }
            // find index k2 of first point not collinear with points[0] and points[k1]
            int k2;
            for (k2 = k1 + 1; k2 < n; k2++) {
                if (Point.counterClockWise(points[0], points[k1], points[k2]) != 0) {
                    break;
                }
            }
            hull.push(points[k2 - 1]);    // points[k2-1] is the second extreme point
            // Graham scan: note that points[n-1] is extreme point different from points[0]
            for (int i = k2; i < n; i++) {
                Point top = hull.pop();
                while (Point.counterClockWise(hull.peek(), top, points[i]) <= 0) {
                    top = hull.pop();
                }
                hull.push(top);
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
         * Check that boundary of hull is strictly convex.
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
            for (Point p : computeHull()) {
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
}
