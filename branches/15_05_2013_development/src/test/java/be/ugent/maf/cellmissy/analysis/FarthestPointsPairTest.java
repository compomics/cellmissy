/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.FarthestPointsPairCalculator;
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
 * A unit test for the farthest points calculator.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class FarthestPointsPairTest {

    @Autowired
    private FarthestPointsPairCalculator farthestPointsPairCalculator;
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
    public void testFindFarthestPoints() {

//        System.out.println("q-r: " + q.euclideanDistanceTo(r));
//        System.out.println("q-s: " + q.euclideanDistanceTo(s));
//        System.out.println("q-t: " + q.euclideanDistanceTo(t));
//        System.out.println("q-u: " + q.euclideanDistanceTo(u));
//        System.out.println("q-v: " + q.euclideanDistanceTo(v));
//
//        System.out.println("r-s: " + r.euclideanDistanceTo(s));
//        System.out.println("r-t: " + r.euclideanDistanceTo(t));
//        System.out.println("r-u: " + r.euclideanDistanceTo(u));
//        System.out.println("r-v: " + r.euclideanDistanceTo(v));
//
//        System.out.println("s-t: " + s.euclideanDistanceTo(t));
//        System.out.println("s-u: " + s.euclideanDistanceTo(u));
//        System.out.println("s-v: " + s.euclideanDistanceTo(v));
//
//        System.out.println("t-u: " + t.euclideanDistanceTo(u));
//        System.out.println("t-v: " + t.euclideanDistanceTo(v));
//
//        System.out.println("u-v: " + u.euclideanDistanceTo(v));

        List<Point> farthestPoints = farthestPointsPairCalculator.findFarthestPoints(track);
        int n = farthestPoints.size();
        Assert.assertEquals(2, n);
        Point firstPoint = farthestPoints.get(0);
        Point secondPoint = farthestPoints.get(1);
        double farthestDistance = firstPoint.euclideanDistanceTo(secondPoint);
        System.out.println("distance from: " + firstPoint + " to: " + secondPoint + " is: d = " + farthestDistance);

        Assert.assertEquals(v, firstPoint);
        Assert.assertEquals(t, secondPoint);
    }
}
