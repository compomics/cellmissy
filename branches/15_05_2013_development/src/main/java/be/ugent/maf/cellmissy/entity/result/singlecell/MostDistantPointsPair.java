/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import java.util.List;

/**
 * A pair of most distant geometric points on a track. It contains the couple of
 * points, together with the distance between them. Searching for the 2 points
 * is implemented using a convex hull
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
     * Do the actual computation
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
                GeometricPoint otherGeometricPoint = hull[i];
                if (!geometricPoint.equals(otherGeometricPoint)) {
                    double euclideanDistance = geometricPoint.euclideanDistanceTo(otherGeometricPoint);
                    if (euclideanDistance > maxSpan) {
                        firstPoint = geometricPoint;
                        secondPoint = otherGeometricPoint;
                        maxSpan = euclideanDistance;
                    }
                }
            }
        }
    }
}
