/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.CellCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.StepCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.TrackOperator;
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
        stepCentricOperator.generateTimeIndexes(stepCentricDataHolder);
        stepCentricOperator.generateCoordinatesMatrix(stepCentricDataHolder);
        stepCentricOperator.computeShiftedCoordinatesMatrix(stepCentricDataHolder);
        stepCentricOperator.computeDeltaMovements(stepCentricDataHolder);
        stepCentricOperator.computeInstantaneousDisplacements(stepCentricDataHolder);
        stepCentricOperator.computeTurningAngles(stepCentricDataHolder);
        stepCentricOperator.computeDirectionalityRatios(stepCentricDataHolder);
        stepCentricOperator.computeMeanSquareDisplacements(stepCentricDataHolder);
        stepCentricOperator.computeDirectionAutocorrelations(stepCentricDataHolder);
        stepCentricOperator.computeMeanDirectionAutocorrelations(stepCentricDataHolder);
    }

    @Override
    public void operateOnCells(TrackDataHolder trackDataHolder) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
        cellCentricOperator.computeTrackDuration(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeCoordinatesRange(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeMedianDisplacement(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeCumulativeDistance(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeEuclideanDistance(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeMedianSpeed(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeEndPointDirectionalityRatio(cellCentricDataHolder);
        cellCentricOperator.computeMedianDirectionalityRatio(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeConvexHull(stepCentricDataHolder, cellCentricDataHolder);
        cellCentricOperator.computeDisplacementRatio(cellCentricDataHolder);
        cellCentricOperator.computeOutreachRatio(cellCentricDataHolder);
        cellCentricOperator.computeMedianTurningAngle(stepCentricDataHolder, cellCentricDataHolder);
    }
}
