/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.singlecell.CellCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.StepCentricOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
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
 * A unit test for some pre-processing methods on single cell analysis, at the
 * cell-centric level.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class CellCentricOperatorTest {

    @Autowired
    private CellCentricOperator cellCentricOperator;
    @Autowired
    private StepCentricOperator stepCentricOperator;
    // the data holders
    private static StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder();
    private static CellCentricDataHolder cellCentricDataHolder = new CellCentricDataHolder();

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
        // test the euclidean distance
        cellCentricOperator.computeEuclideanDistance(stepCentricDataHolder, cellCentricDataHolder);
        double euclideanDistance = cellCentricDataHolder.getEuclideanDistance();
        Assert.assertEquals(8.602, AnalysisUtils.roundThreeDecimals(euclideanDistance));
        // test the cumulative distance
        cellCentricOperator.computeCumulativeDistance(stepCentricDataHolder, cellCentricDataHolder);
        double cumulativeDistance = cellCentricDataHolder.getCumulativeDistance();
        Assert.assertEquals(37.688, AnalysisUtils.roundThreeDecimals(cumulativeDistance));
        // test end point directionality ratio
        cellCentricOperator.computeEndPointDirectionalityRatio(cellCentricDataHolder);
        double endPointDirectionalityRatio = cellCentricDataHolder.getEndPointDirectionalityRatio();
        Assert.assertEquals(0.228, AnalysisUtils.roundThreeDecimals(endPointDirectionalityRatio));
    }
}
