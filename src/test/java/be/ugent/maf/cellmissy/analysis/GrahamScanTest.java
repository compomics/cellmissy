/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.impl.GrahamScanAlgorithm;
import be.ugent.maf.cellmissy.entity.Point;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
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
    // 6 points ont the plane
    private static Track track = new Track();
    private static Point q = new Point(2, 3);
    private static Point r = new Point(10, 6);
    private static Point s = new Point(10, 9);
    private static Point t = new Point(8, 10);
    private static Point u = new Point(-2, 5);
    private static Point v = new Point(7, -4);

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
    public void testGrahamScan() {
        Iterable<Point> convexHull = grahamScanAlgorithm.computeConvexHull(track);
        List<Point> convexHullVertices = new ArrayList<>();
        int verticesNumber = 0;
        for (Point vertex : convexHull) {
            verticesNumber++;
            convexHullVertices.add(vertex);
        }
        // convex hull has 4 points
        Assert.assertEquals(5, verticesNumber);
        // first vertex is q (2, 3)
        Assert.assertEquals(v, convexHullVertices.get(0));
        // last vertex is u
        Assert.assertEquals(u, convexHullVertices.get(verticesNumber - 1));
    }
}
