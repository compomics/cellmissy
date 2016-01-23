/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl.TrackSplineInterpolator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
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
 *
 * @author Paola
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class TrackInterpolatorTest {

    @Autowired
    private TrackSplineInterpolator trackSplineInterpolator;
    @Autowired
    private StepCentricOperator stepCentricOperator;

    private static StepCentricDataHolder stepCentricDataHolder;
    private static CellCentricDataHolder cellCentricDataHolder;
    private static final int INTERPOLATION_POINTS = 100;

    @BeforeClass
    public static void createHolders() {
        List<TrackPoint> trackPoints = new ArrayList<>();
        TrackPoint tpq = new TrackPoint(2, 3);
        TrackPoint tpr = new TrackPoint(10,6);
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
        stepCentricDataHolder = new StepCentricDataHolder(track);
        cellCentricDataHolder = new CellCentricDataHolder();
    }

    @Test
    public void testInterpolation() {
        stepCentricOperator.generateCoordinatesMatrix(stepCentricDataHolder, 1.0);
        trackSplineInterpolator.interpolateTrack(cellCentricDataHolder, stepCentricDataHolder, INTERPOLATION_POINTS);
        Assert.assertEquals(INTERPOLATION_POINTS, cellCentricDataHolder.getInterpolationX().length);
        Assert.assertEquals(INTERPOLATION_POINTS, cellCentricDataHolder.getInterpolatedY().length);
    }
}
