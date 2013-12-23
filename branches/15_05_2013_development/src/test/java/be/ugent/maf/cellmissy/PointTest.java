/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy;

import be.ugent.maf.cellmissy.entity.Point;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A unit test for Point objects.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class PointTest {

    // 3 points ont the plane
    private static Point q;
    private static Point r;
    private static Point s;

    @BeforeClass
    public static void createPoints() {
        q = new Point(2.5, 3.5);
        r = new Point(7.7, 6.8);
        s = new Point(2.3, 5.56);
    }

    @Test
    public void testEuclideanDistance() {
        double euclideanDistanceTo = q.euclideanDistanceTo(r);
        Assert.assertEquals(6.158733636065128, euclideanDistanceTo);
        double zeroDistance = q.euclideanDistanceTo(q);
        Assert.assertEquals(0.0, zeroDistance);
    }

    @Test
    public void testSignedArea() {
        double area = Point.computeSignedArea(q, r, s);
        Assert.assertEquals(5.685999999999999 * 2, area);
    }

    @Test
    public void testCounterClockWise() {
        int counterClockWise = Point.counterClockWise(q, r, s);
        Assert.assertEquals(1, counterClockWise);
    }
}
