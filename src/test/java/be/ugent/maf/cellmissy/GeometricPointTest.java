/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy;

import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A unit test for GeometricPoint objects.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class GeometricPointTest {

    // 3 points ont the plane
    private static GeometricPoint q;
    private static GeometricPoint r;
    private static GeometricPoint s;

    @BeforeClass
    public static void createPoints() {
        q = new GeometricPoint(2.5, 3.5);
        r = new GeometricPoint(7.7, 6.8);
        s = new GeometricPoint(2.3, 5.56);
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
        double area = GeometricPoint.computeSignedArea(q, r, s);
        Assert.assertEquals(5.685999999999999 * 2, area);
    }

    @Test
    public void testCounterClockWise() {
        int counterClockWise = GeometricPoint.counterClockWise(q, r, s);
        Assert.assertEquals(1, counterClockWise);
    }
}
