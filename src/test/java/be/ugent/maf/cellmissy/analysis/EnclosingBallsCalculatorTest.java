/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.EnclosingBallsCalculator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import java.util.ArrayList;
import java.util.List;
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
    private EnclosingBallsCalculator enclosingBallsCalculator;
    // the data holder
    private static final StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder();

    @BeforeClass
    public static void createTrack() {
        List<TrackPoint> trackPoints = new ArrayList<>();
        TrackPoint tpq = new TrackPoint(1, 1);
        TrackPoint tpr = new TrackPoint(2, 2);
        TrackPoint tps = new TrackPoint(3.2, 3);
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
        enclosingBallsCalculator.computeEnclosingBalls(stepCentricDataHolder, 1.8);
    }
}
