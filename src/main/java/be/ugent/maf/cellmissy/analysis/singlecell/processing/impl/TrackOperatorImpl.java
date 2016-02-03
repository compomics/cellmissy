/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.CellCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.StepCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.TrackOperator;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation for the Track Operator interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("trackOperator")
public class TrackOperatorImpl implements TrackOperator {

    @Autowired
    private StepCentricOperator stepCentricOperator;
    @Autowired
    private CellCentricOperator cellCentricOperator;

    @Override
    public void operateOnSteps(TrackDataHolder trackDataHolder) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        stepCentricOperator.computeDeltaMovements(stepCentricDataHolder);
        stepCentricOperator.computeInstantaneousDisplacements(stepCentricDataHolder);
        stepCentricOperator.computeTurningAngles(stepCentricDataHolder);
        stepCentricOperator.computeDirectionalityRatios(stepCentricDataHolder);
        stepCentricOperator.computeMeanSquareDisplacements(stepCentricDataHolder);
        stepCentricOperator.computeDirectionAutocorrelations(stepCentricDataHolder);
        stepCentricOperator.computeDirAutocorrMatrix(stepCentricDataHolder);
        stepCentricOperator.computeDiffAngles(stepCentricDataHolder);
        stepCentricOperator.computeMeanDirectionAutocorrelations(stepCentricDataHolder);
        stepCentricOperator.interpolateTrack(stepCentricDataHolder);
    }

    @Override
    public void operateOnCells(TrackDataHolder trackDataHolder) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
        cellCentricOperator.computeMedianDisplacement(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeCumulativeDistance(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeEuclideanDistance(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeMedianSpeed(cellCentricDataHolder);
        cellCentricOperator.computeEndPointDirectionalityRatio(cellCentricDataHolder);
        cellCentricOperator.computeMedianDirectionalityRatio(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeConvexHull(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeDisplacementRatio(cellCentricDataHolder);
        cellCentricOperator.computeOutreachRatio(cellCentricDataHolder);
        cellCentricOperator.computeMedianTurningAngle(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeMedianDirectionAutocorrelation(stepCentricDataHolder, cellCentricDataHolder);
    }
}
