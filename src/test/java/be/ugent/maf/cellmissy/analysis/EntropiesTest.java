/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.CellCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.EnclosingBallCalculator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
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
public class EntropiesTest {

    @Autowired
    private StepCentricOperator stepCentricOperator;
    @Autowired
    private CellCentricOperator cellCentricOperator;
    @Autowired
    private EnclosingBallCalculator enclosingBallsCalculator;
    // the data holder
    private static final StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder();
    private static final CellCentricDataHolder cellCentricDataHolder = new CellCentricDataHolder();

    @BeforeClass
    public static void createTrack() {
        List<TrackPoint> trackPoints = new ArrayList<>();
        TrackPoint tpq = new TrackPoint(1.0, 1.0);
        TrackPoint tpr = new TrackPoint(2.0, 2.0);
        TrackPoint tps = new TrackPoint(3.0, 3.0);
        TrackPoint tpt = new TrackPoint(4.0, 4.0);
        TrackPoint tpu = new TrackPoint(5.0, 5.0);
        TrackPoint tpv = new TrackPoint(6.0, 6.0);
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
    public void testEntropies() {
        stepCentricOperator.generateCoordinatesMatrix(stepCentricDataHolder, 1.0);
        double[] timeIndexes = new double[]{1, 2, 3, 4, 5, 6}; // time indexes of the track
        stepCentricDataHolder.setTimeIndexes(timeIndexes);
        stepCentricOperator.init2Dtrees(stepCentricDataHolder);

        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        double[] xCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[0]));
        double[] yCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[1]));
        List<EnclosingBall> enclosingBalls = enclosingBallsCalculator.findEnclosingBalls(xCoord, yCoord, stepCentricDataHolder.getxY2DTree(), 1.5);

        List<List<EnclosingBall>> list = new ArrayList<>();
        list.add(enclosingBalls);
        stepCentricDataHolder.setxYEnclosingBalls(list);
        cellCentricOperator.computeEntropies(stepCentricDataHolder, cellCentricDataHolder);
        Double entropy = cellCentricDataHolder.getEntropies().get(0);

//        stepCentricDataHolder.setxYEnclosingBalls(list);
//        cellCentricOperator.computeEntropies(stepCentricDataHolder, cellCentricDataHolder);
//        entropy = cellCentricDataHolder.getEntropies().get(0);
//        Assert.assertEquals(0.113, entropy, 0.1);
    }
}
