/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.kdtree.KDTree;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeyDuplicateException;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeySizeException;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.EnclosingBallsCalculator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.StepCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.impl.EnclosingBallsCalculatorImpl;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private EnclosingBallsCalculator enclosingBallsCalculator;
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
        final KDTree<GeometricPoint> tree = new KDTree(2);
        initKDTree(stepCentricDataHolder, tree);
        List<Ellipse2D> enclosingBalls = enclosingBallsCalculator.computeEnclosingBalls(stepCentricDataHolder, 0.1);
        Assert.assertEquals(6, enclosingBalls.size());

        enclosingBalls = enclosingBallsCalculator.computeEnclosingBalls(stepCentricDataHolder, 0.5);
        Assert.assertEquals(5, enclosingBalls.size());

        enclosingBalls = enclosingBallsCalculator.computeEnclosingBalls(stepCentricDataHolder, 1.1);
        Assert.assertEquals(3, enclosingBalls.size());

    }

    private void initKDTree(StepCentricDataHolder stepCentricDataHolder, KDTree tree) {
        stepCentricDataHolder.getTrack().getTrackPointList().stream().map((trackPoint) -> trackPoint.getGeometricPoint()).forEach((geometricPoint) -> {
            double[] key = new double[]{geometricPoint.getX(), geometricPoint.getY()};
            try {
                tree.insert(key, geometricPoint);
            } catch (KeySizeException | KeyDuplicateException ex) {
                Logger.getLogger(EnclosingBallsCalculatorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        stepCentricDataHolder.setkDTree(tree);
    }
}
