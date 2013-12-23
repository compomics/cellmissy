/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import java.util.List;

/**
 * This object is a pair of farthest points in a track. It contains first and
 * second point, together with their distance.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class FarthestPointsPair {

    // the convex hull of the track, as an iterable of points
    private Iterable<Point> convexHull;
    // first and second points of track, along with distance between them
    private Point firstPoint;
    private Point secondPoint;
    private double greatestDistance;

    /**
     * Constructor
     *
     * @param track: the track on which the farthest points pair is computed
     * @param convexHull : the convex hull of the track
     */
    public FarthestPointsPair(Track track, Iterable<Point> convexHull) {
        this.convexHull = convexHull;
        compute(track);
    }

    /**
     * Getters
     */
    public Point getFirstPoint() {
        return firstPoint;
    }

    public Point getSecondPoint() {
        return secondPoint;
    }

    public double getGreatestDistance() {
        return greatestDistance;
    }

    /**
     * Given a track, compute the two most distant track points. Compute the
     * convex hull of the track and then look for the two farthest points among
     * the vertices of the hull.
     *
     * @param track
     */
    private void compute(Track track) {
        List<TrackPoint> trackPointList = track.getTrackPointList();
        // only one point, return
        if (trackPointList.size() <= 1) {
            return;
        }
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
            greatestDistance = firstPoint.euclideanDistanceTo(secondPoint);
            return;
        }

        // check for the greatest distance
        for (Point point : convexHull) {
            for (int i = 0; i < hull.length; i++) {
                Point other = hull[i];
                if (!point.equals(other)) {
                    double euclideanDistance = point.euclideanDistanceTo(other);
                    if (euclideanDistance > greatestDistance) {
                        firstPoint = point;
                        secondPoint = other;
                        greatestDistance = euclideanDistance;
                    }
                }
            }
        }
    }
}
