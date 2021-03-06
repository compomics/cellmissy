/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.ConvexHullCalculator;
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
 * algorithm (and the Graham Scan class of CellMissy). The main idea here is as
 * simple as this: Sort points by angle from x0. Push x0 and x1. Set i=2. While
 * i less or equal to n do: If xi makes left turn with respect to the top 2
 * items on stack then f push xi and increment i++, else pop and discard.
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
        trackPointList.stream().forEach((trackPoint) -> {
            geometricPoints.add(trackPoint.getGeometricPoint());
        });
        // create a new Graham Scan object for these points
        GrahamScan grahamScan = new GrahamScan(geometricPoints);
        // get the hull from this object and pass it to the convex hull object
        Iterable<GeometricPoint> hull = grahamScan.getHull();
        convexHull.setHull(hull);
    }

    @Override
    public void computeHullSize(ConvexHull convexHull) {
        int n = 0;
        for (GeometricPoint geometricPoint : convexHull.getHull()) {
            n++;
        }
        convexHull.setHullSize(n);
    }

    @Override
    public void findMostDistantPoints(Track track, ConvexHull convexHull) {
        // create a new Most Distant Points Pair object and set it to the convex hull
        MostDistantPointsPair mostDistantPointsPair = new MostDistantPointsPair(track, convexHull);
        convexHull.setMostDistantPointsPair(mostDistantPointsPair);
    }
}
