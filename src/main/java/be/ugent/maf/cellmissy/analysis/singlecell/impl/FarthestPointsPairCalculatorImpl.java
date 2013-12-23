/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.FarthestPointsPairCalculator;
import be.ugent.maf.cellmissy.entity.Point;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("farthestPointsPairCalculator")
public class FarthestPointsPairCalculatorImpl implements FarthestPointsPairCalculator {

    @Autowired
    private GrahamScanAlgorithm grahamScanAlgorithm;
    private FarthestPair farthestPair;

    @Override
    public List<Point> findFarthestPoints(Track track) {
        List<Point> farthestPoints = new ArrayList<>();
        farthestPair = new FarthestPair(track);
        farthestPoints.add(farthestPair.firstPoint);
        farthestPoints.add(farthestPair.secondPoint);
        return farthestPoints;
    }

    /**
     *
     */
    private class FarthestPair {

        /**
         *
         * @param track
         */
        private FarthestPair(Track track) {
            compute(track);
        }
        private Point firstPoint;
        private Point secondPoint;
        private double farthestDistance;

        /**
         * Given a track, compute the two most distant track points. Use the
         * rotating calispers method to determine all the antipodal pairs of
         * points and vertices on the convex hull computed through the graham
         * scan algorithm. (Note that only antipodal pairs of points should be
         * checked, because the diameter is the highest distance between two
         * parallel lines of the hull).
         *
         * @param track
         */
        private void compute(Track track) {
            List<TrackPoint> trackPointList = track.getTrackPointList();
            // only one point, return
            if (trackPointList.size() <= 1) {
                return;
            }
            // compute convex hull containing vertices
            Iterable<Point> convexHull = grahamScanAlgorithm.computeConvexHull(track);
            // number of points on the hull
            int M = 0;
            for (Point point : convexHull) {
                M++;
            }
            // the hull, in counterclockwise order
            Point[] hull = new Point[M];
            int m = 0;
            for (Point point : convexHull) {
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
                farthestDistance = firstPoint.euclideanDistanceTo(secondPoint);
                return;
            }

            for (Point point : convexHull) {
                for (int i = 0; i < hull.length; i++) {
                    Point other = hull[i];
                    if (!point.equals(other)) {
                        double euclideanDistance = point.euclideanDistanceTo(other);
                        if (euclideanDistance > farthestDistance) {
                            firstPoint = point;
                            secondPoint = other;
                            farthestDistance = euclideanDistance;
                        }
                    }
                }
            }
        }
    }
}
