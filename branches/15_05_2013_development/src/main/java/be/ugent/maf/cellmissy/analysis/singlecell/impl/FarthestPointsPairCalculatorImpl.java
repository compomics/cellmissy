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

        private void compute(Track track) {
            List<TrackPoint> trackPointList = track.getTrackPointList();
            Iterable<Point> convexHull = grahamScanAlgorithm.computeConvexHull(track);
            // single point
            if (trackPointList.size() <= 1) {
                return;
            }
            // number of points on the hull
            int hullPointsNumber = 0;
            for (Point point : convexHull) {
                hullPointsNumber++;
            }
            // the hull, in counterclockwise order
            Point[] hull = new Point[hullPointsNumber + 1];
            int n = 1;
            for (Point point : convexHull) {
                hull[n++] = point;
            }
            // all points are equal
            if (hullPointsNumber == 1) {
                return;
            }

            // points are collinear
            if (hullPointsNumber == 2) {
                firstPoint = hull[1];
                secondPoint = hull[2];
            }

            // k = farthest vertex from edge from hull[1] to hull[m]
            int k = 2;
            double a = Point.computeSignedArea(hull[hullPointsNumber], hull[k + 1], hull[1]);
            double b = Point.computeSignedArea(hull[hullPointsNumber], hull[k], hull[1]);
            while (a > b) {
                k++;
            }

            int j = k;
            for (int i = 1; i <= k; i++) {
                System.out.println(hull[i] + " and " + hull[j] + " are antipodal");
                if (hull[i].euclideanDistanceTo(hull[j]) > farthestDistance) {
                    firstPoint = hull[i];
                    secondPoint = hull[j];
                    farthestDistance = hull[i].euclideanDistanceTo(hull[j]);
                }
                double c = Point.computeSignedArea(hull[i], hull[j + 1], hull[i + 1]);
                double d = Point.computeSignedArea(hull[i], hull[j], hull[i + 1]);
                while ((j < hullPointsNumber) && c > d) {
                    j++;
                    System.out.println(hull[i] + " and " + hull[j] + " are antipodal");
                    double distance = hull[i].euclideanDistanceTo(hull[j]);
                    if (distance > farthestDistance) {
                        firstPoint = hull[i];
                        secondPoint = hull[j];
                        farthestDistance = hull[i].euclideanDistanceTo(hull[j]);
                    }
                }
            }
        }
    }
}
