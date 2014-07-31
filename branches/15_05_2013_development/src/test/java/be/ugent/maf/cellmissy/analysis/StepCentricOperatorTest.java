/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
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
    // the data holder
    private static StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder();

    @BeforeClass
    public static void createTrack() {
        List<TrackPoint> trackPoints = new ArrayList<>();
        TrackPoint tpq = new TrackPoint(2, 3);
        TrackPoint tpr = new TrackPoint(10, 6);
        TrackPoint tps = new TrackPoint(10, 9);
        TrackPoint tpt = new TrackPoint(8, 10);
        TrackPoint tpu = new TrackPoint(-2, 5);
        TrackPoint tpv = new TrackPoint(7, -4);
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
    public void testComputations() {
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
        Assert.assertEquals(0.669, AnalysisUtils.roundThreeDecimals(thirdDR));
        // test instantaneous turning angles
        stepCentricOperator.computeTurningAngles(stepCentricDataHolder);
        Double[] turningAngles = stepCentricDataHolder.getTurningAngles();
        double firstTA = turningAngles[0];
        double secondTA = turningAngles[1];
        double thirdTA = turningAngles[2];
        double fourthTA = turningAngles[3];
        double lastTurningAngle = turningAngles[4];
        Assert.assertEquals(20.556, AnalysisUtils.roundThreeDecimals(firstTA));
        Assert.assertEquals(90.0, secondTA);
        Assert.assertEquals(333.435, AnalysisUtils.roundThreeDecimals(thirdTA));
        Assert.assertEquals(26.565, AnalysisUtils.roundThreeDecimals(fourthTA));
        Assert.assertEquals(315.0, AnalysisUtils.roundThreeDecimals(lastTurningAngle));
    }
}
