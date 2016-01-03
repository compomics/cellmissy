/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.ConvexHullOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.impl.GrahamScanAlgorithm;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.MostDistantPointsPair;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A unit test for the Graham Scan algorithm.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class GrahamScanTest {

    @Autowired
    private GrahamScanAlgorithm grahamScanAlgorithm;
    @Autowired
    private ConvexHullOperator convexHullOperator;
    // 6 points ont the plane
    private static final Track track = new Track();
    private static final GeometricPoint q = new GeometricPoint(2, 3);
    private static final GeometricPoint r = new GeometricPoint(10, 6);
    private static final GeometricPoint s = new GeometricPoint(10, 9);
    private static final GeometricPoint t = new GeometricPoint(8, 10);
    private static final GeometricPoint u = new GeometricPoint(-2, 5);
    private static final GeometricPoint v = new GeometricPoint(7, -4);
    // the convex hull to do the calculations on
    private final ConvexHull convexHull = new ConvexHull();

    @BeforeClass
    public static void createTrack() {
        List<TrackPoint> trackPoints = new ArrayList<>();
        TrackPoint tpq = new TrackPoint(q);
        TrackPoint tpr = new TrackPoint(r);
        TrackPoint tps = new TrackPoint(s);
        TrackPoint tpt = new TrackPoint(t);
        TrackPoint tpu = new TrackPoint(u);
        TrackPoint tpv = new TrackPoint(v);
        trackPoints.add(tpq);
        trackPoints.add(tpr);
        trackPoints.add(tps);
        trackPoints.add(tpt);
        trackPoints.add(tpu);
        trackPoints.add(tpv);
        track.setTrackPointList(trackPoints);
    }

    @Test
    public void testHull() {
        // compute the hull and its size, the number of vertices
        grahamScanAlgorithm.computeHull(track, convexHull);
        grahamScanAlgorithm.computeHullSize(convexHull);
        Iterable<GeometricPoint> hull = convexHull.getHull();
        int hullSize = convexHull.getHullSize();
        List<GeometricPoint> convexHullVertices = new ArrayList<>();
        for (GeometricPoint vertex : hull) {
            convexHullVertices.add(vertex);
        }
        // convex hull has 5 points
        Assert.assertEquals(5, hullSize);
        // first vertex is v
        Assert.assertEquals(v, convexHullVertices.get(0));
        // last vertex is u
        Assert.assertEquals(u, convexHullVertices.get(hullSize - 1));
        // find the two most distant points on the hull
        grahamScanAlgorithm.findMostDistantPoints(track, convexHull);
        MostDistantPointsPair mostDistantPointsPair = convexHull.getMostDistantPointsPair();
        GeometricPoint firstPoint = mostDistantPointsPair.getFirstPoint();
        GeometricPoint secondPoint = mostDistantPointsPair.getSecondPoint();
        Assert.assertNotNull(firstPoint);
        Assert.assertNotNull(secondPoint);
        Assert.assertEquals(v, firstPoint);
        Assert.assertEquals(t, secondPoint);
        //compute perimeter
        convexHullOperator.computePerimeter(convexHull);
        double perimeter = convexHull.getPerimeter();
        Assert.assertEquals(39.585, AnalysisUtils.roundThreeDecimals(perimeter));
        // compute area
        convexHullOperator.computeArea(convexHull);
        double area = convexHull.getArea();
        Assert.assertEquals(86.5, area);
    }
}
