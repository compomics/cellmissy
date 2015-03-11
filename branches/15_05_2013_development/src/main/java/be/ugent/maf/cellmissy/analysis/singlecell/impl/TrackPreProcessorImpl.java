package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.CellCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.StepCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.TrackPreProcessor;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An implementation for the TrackPreProcessor interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("trackPreProcessor")
public class TrackPreProcessorImpl implements TrackPreProcessor {

    @Autowired
    private StepCentricOperator stepCentricOperator;
    @Autowired
    private CellCentricOperator cellCentricOperator;

    @Override
    public void preProcessSteps(TrackDataHolder trackDataHolder, double conversionFactor) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        stepCentricOperator.generateTimeIndexes(stepCentricDataHolder);
        stepCentricOperator.generateCoordinatesMatrix(stepCentricDataHolder, conversionFactor);
        stepCentricOperator.computeShiftedCoordinatesMatrix(stepCentricDataHolder);
    }

    @Override
    public void preProcessCells(TrackDataHolder trackDataHolder, double timeLapse) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
        cellCentricOperator.computeTrackDuration(stepCentricDataHolder, cellCentricDataHolder, timeLapse);
        cellCentricOperator.computeCoordinatesRange(stepCentricDataHolder, cellCentricDataHolder);
    }
}
