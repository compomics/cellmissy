/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import java.util.List;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class MostDistantPointsPair {

    // first and second points of track, along with distance between them
    private GeometricPoint firstPoint;
    private GeometricPoint secondPoint;
    private double maxSpan;

    /**
     * Constructor
     *
     * @param track
     * @param convexHull
     */
    public MostDistantPointsPair(Track track, ConvexHull convexHull) {
        compute(track, convexHull);
    }

    /**
     * Getters
     */
    public GeometricPoint getFirstPoint() {
        return firstPoint;
    }

    public GeometricPoint getSecondPoint() {
        return secondPoint;
    }

    public double getMaxSpan() {
        return maxSpan;
    }

    /**
     *
     * @param track
     * @param convexHull
     */
    private void compute(Track track, ConvexHull convexHull) {
        List<TrackPoint> trackPointList = track.getTrackPointList();
        // only one point, return
        if (trackPointList.size() <= 1) {
            return;
        }
        // number of points on the hull
        int M = 0;
        for (GeometricPoint geometricPoint : convexHull.getHull()) {
            M++;
        }
        // the hull, in counterclockwise order
        GeometricPoint[] hull = new GeometricPoint[M];
        int m = 0;
        for (GeometricPoint geometricPoint : convexHull.getHull()) {
            hull[m++] = geometricPoint;
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
        for (GeometricPoint geometricPoint : hull) {
            for (int i = 0; i < hull.length; i++) {
                GeometricPoint otherPoint = hull[i];
                if (!geometricPoint.equals(otherPoint)) {
                    double euclideanDistance = geometricPoint.euclideanDistanceTo(otherPoint);
                    if (euclideanDistance > maxSpan) {
                        firstPoint = geometricPoint;
                        secondPoint = otherPoint;
                        maxSpan = euclideanDistance;
                    }
                }
            }
        }
    }
}
