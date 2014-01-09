/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.ConvexHullCalculator;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.GrahamScan;
import be.ugent.maf.cellmissy.entity.result.singlecell.MostDistantPointsPair;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * This class implements the convex hull calculator, using the Graham Scan
 * algorithm (and the Graham Scan class of CellMissy).
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("grahamScanAlgorithm")
public class GrahamScanAlgorithm implements ConvexHullCalculator {

    @Override
    public void computeHull(Track track, ConvexHull convexHull) {
        // create a list of geometric points from the track points
        List<TrackPoint> trackPointList = track.getTrackPointList();
        List<GeometricPoint> geometricPoints = new ArrayList<>();
        for (TrackPoint trackPoint : trackPointList) {
            geometricPoints.add(trackPoint.getPoint());
        }
        // create a new Graham Scan object for these points
        GrahamScan grahamScan = new GrahamScan(geometricPoints);
        // get the hull from this object and pass it to the convex hull object
        Iterable<GeometricPoint> hull = grahamScan.getHull();
        convexHull.setHull(hull);
    }

    @Override
    public void findMostDistantPoints(Track track, ConvexHull convexHull) {
        // create a new Most Distant Points Pair object and set it to the convex hull
        MostDistantPointsPair mostDistantPointsPair = new MostDistantPointsPair(track, convexHull);
        convexHull.setMostDistantPointsPair(mostDistantPointsPair);
    }

    @Override
    public void computeArea(ConvexHull convexHull) {
        double area = 0;
        Iterable<GeometricPoint> hull = convexHull.getHull();
        // get the size of the hull: number of vertices of polygon
        int n = 0;
        for (GeometricPoint geometricPoint : hull) {
            n++;
        }
        // we take n + 1
        GeometricPoint[] geometricPoints = new GeometricPoint[n + 1];
        int j = 0;
        for (GeometricPoint geometricPoint : hull) {
            geometricPoints[j++] = geometricPoint;
        }
        // close the polygon: last point of polygon has to be equal to first point
        geometricPoints[n] = geometricPoints[0];
        // sum the cross products around each vertex of the hull
        for (int i = 0; i < n; i++) {
            double xUp = geometricPoints[i].getX();
            double yDown = geometricPoints[i + 1].getY();
            double yUp = geometricPoints[i].getY();
            double xDown = geometricPoints[i + 1].getX();
            area += (xUp * yDown) - (yUp * xDown);
        }
        convexHull.setArea(area / 2);
    }
}
