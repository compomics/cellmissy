/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;

/**
 * Interface for single cell pre-processing: uses the track pre-processor interface to bring together all the
 * pre-processing to a plate condition level.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface SingleCellPreProcessor {

    /**
     * Put all together the track data holders for a certain plate condition.
     *
     * @param singleCellConditionDataHolder
     * @param plateCondition:               we need here a plate condition because this can be done only on the
     *                                      single cell analysed wells. We'll get these wells having the condition.
     */
    public void generateTrackDataHolders(SingleCellConditionDataHolder singleCellConditionDataHolder,
                                         PlateCondition plateCondition);

    /**
     * Generate the data structure: this will put together the well of the
     * track, the track number and its time index.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateDataStructure(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * This will perform all the operations required on the step-centric and
     * cell-centric level.
     *
     * @param singleCellConditionDataHolder
     * @param conversionFactor:             required for coordinates matrix computation
     * @param timeLapse:                    required for track duration computations
     */
    public void preProcessStepsAndCells(SingleCellConditionDataHolder singleCellConditionDataHolder,
                                        double conversionFactor, double timeLapse);

    /**
     * Generate the track coordinates matrix.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateRawTrackCoordinatesMatrix(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the shifted track coordinates matrix.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateShiftedTrackCoordinatesMatrix(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate raw coordinates ranges.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateRawCoordinatesRanges(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate shifted coordinates ranges.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateShiftedCoordinatesRanges(SingleCellConditionDataHolder singleCellConditionDataHolder);


}
