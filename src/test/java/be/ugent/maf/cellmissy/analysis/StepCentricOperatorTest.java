/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
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
 * A unit test for some preprocessing methods on single cells analysis, at the
 * step-centric level.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class StepCentricOperatorTest {

    @Autowired
    private StepCentricOperator stepCentricOperator;
    // 6 points ont the plane
    private static StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder();
    private static GeometricPoint q = new GeometricPoint(2, 3);
    private static GeometricPoint r = new GeometricPoint(10, 6);
    private static GeometricPoint s = new GeometricPoint(10, 9);
    private static GeometricPoint t = new GeometricPoint(8, 10);
    private static GeometricPoint u = new GeometricPoint(-2, 5);
    private static GeometricPoint v = new GeometricPoint(7, -4);

    @BeforeClass
    public static void createTrack() {
        List<TrackPoint> trackPoints = new ArrayList<>();
        TrackPoint tpq = new TrackPoint(q.getX(), q.getY());
        TrackPoint tpr = new TrackPoint(r.getX(), r.getY());
        TrackPoint tps = new TrackPoint(s.getX(), s.getY());
        TrackPoint tpt = new TrackPoint(t.getX(), s.getY());
        TrackPoint tpu = new TrackPoint(u.getX(), u.getY());
        TrackPoint tpv = new TrackPoint(v.getX(), v.getY());
        trackPoints.add(tpq);
        trackPoints.add(tpr);
        trackPoints.add(tps);
        trackPoints.add(tpt);
        trackPoints.add(tpu);
        trackPoints.add(tpv);
        Track track = new Track();
        track.setTrackPointList(trackPoints);
        stepCentricDataHolder.setTrack(track);
        stepCentricDataHolder.setConversionFactor(1.0);
    }

    @Test
    public void testDirectionalityRatios() {
        stepCentricOperator.generateCoordinatesMatrix(stepCentricDataHolder);
        stepCentricOperator.computeDeltaMovements(stepCentricDataHolder);
        stepCentricOperator.computeInstantaneousDisplacements(stepCentricDataHolder);
        stepCentricOperator.computeDirectionalityRatios(stepCentricDataHolder);
        Double[] directionalityRatios = stepCentricDataHolder.getDirectionalityRatios();
        double firstDR = directionalityRatios[0];
        double secondDR = directionalityRatios[1];
        double thirdDR = directionalityRatios[2];
        Assert.assertEquals(1.0, firstDR);
        Assert.assertEquals(0.866, AnalysisUtils.roundThreeDecimals(secondDR));
        Assert.assertEquals(4.119, AnalysisUtils.roundThreeDecimals(thirdDR));
    }
}
