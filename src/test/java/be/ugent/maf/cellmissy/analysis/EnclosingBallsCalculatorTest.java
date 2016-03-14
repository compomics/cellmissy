/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.EnclosingBallCalculator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.EnclosingBall;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class EnclosingBallsCalculatorTest {

    @Autowired
    private StepCentricOperator stepCentricOperator;
    @Autowired
    private EnclosingBallCalculator enclosingBallsCalculator;
    // the data holder
    private static final StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder();

    @BeforeClass
    public static void createTrack() {
        List<TrackPoint> trackPoints = new ArrayList<>();
        TrackPoint tpq = new TrackPoint(0.5, 0.5);
        TrackPoint tpr = new TrackPoint(0.7, 0.5);
        TrackPoint tps = new TrackPoint(1.5, 0.5);
        TrackPoint tpt = new TrackPoint(3, 3);
        TrackPoint tpu = new TrackPoint(3.3, 2);
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
    }

    @Test
    public void testEnclosingBalls() {
        stepCentricOperator.generateCoordinatesMatrix(stepCentricDataHolder, 1.0);
        double[] timeIndexes = new double[]{2, 3, 5, 6, 7, 10}; // time indexes of the track
        stepCentricDataHolder.setTimeIndexes(timeIndexes);
        stepCentricOperator.init2Dtrees(stepCentricDataHolder);

        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        double[] xCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[0]));
        double[] yCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[1]));
        List<EnclosingBall> enclosingBalls = enclosingBallsCalculator.findEnclosingBalls(xCoord, yCoord, stepCentricDataHolder.getxY2DTree(), 0.1);
        Assert.assertEquals(6, enclosingBalls.size());

        enclosingBalls = enclosingBallsCalculator.findEnclosingBalls(xCoord, yCoord, stepCentricDataHolder.getxY2DTree(), 0.5);
        Assert.assertEquals(5, enclosingBalls.size());

        enclosingBalls = enclosingBallsCalculator.findEnclosingBalls(xCoord, yCoord, stepCentricDataHolder.getxY2DTree(), 1.1);
        Assert.assertEquals(3, enclosingBalls.size());
        Assert.assertEquals(3, enclosingBalls.get(0).getEnclosingPoints().size());
        Assert.assertEquals(2, enclosingBalls.get(1).getEnclosingPoints().size());
        Assert.assertEquals(1, enclosingBalls.get(2).getEnclosingPoints().size());

        enclosingBalls = enclosingBallsCalculator.findEnclosingBalls(timeIndexes, xCoord, stepCentricDataHolder.getxT2DTree(), 0.5);
        Assert.assertEquals(6, enclosingBalls.size());
        enclosingBalls = enclosingBallsCalculator.findEnclosingBalls(timeIndexes, xCoord, stepCentricDataHolder.getxT2DTree(), 1.0);
        Assert.assertEquals(6, enclosingBalls.size());
        enclosingBalls = enclosingBallsCalculator.findEnclosingBalls(timeIndexes, xCoord, stepCentricDataHolder.getxT2DTree(), 1.5);
        Assert.assertEquals(4, enclosingBalls.size());
    }
}
