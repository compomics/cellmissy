/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import java.util.List;

/**
 * An interface to preprocess data on a well level. It uses a
 * SingleCellWellDataHolder entity to keep all the data.
 *
 * @author Paola
 */
public interface SingleCellWellPreProcessor {

    /**
     * Put all together the track data holders for a certain plate condition.
     *
     * @param singleCellWellDataHolder
     */
    void generateTrackDataHolders(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the data structure: this will put together the well of the
     * track, the track number and its time index.
     *
     * @param singleCellWellDataHolder
     */
    void generateDataStructure(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * This will perform all the operations required on the step-centric and
     * cell-centric level.
     *
     * @param singleCellWellDataHolder
     * @param conversionFactor: required for coordinates matrix computation
     * @param timeLapse: required for track duration computations
     */
    void preProcessStepsAndCells(SingleCellWellDataHolder singleCellWellDataHolder,
              double conversionFactor, double timeLapse);

    /**
     * Generate the track coordinates matrix.
     *
     * @param singleCellWellDataHolder
     */
    void generateRawTrackCoordinatesMatrix(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the shifted track coordinates matrix.
     *
     * @param singleCellWellDataHolder
     */
    void generateShiftedTrackCoordinatesMatrix(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate raw coordinates ranges.
     *
     * @param singleCellWellDataHolder
     */
    void generateRawCoordinatesRanges(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate shifted coordinates ranges.
     *
     * @param singleCellWellDataHolder
     */
    void generateShiftedCoordinatesRanges(SingleCellWellDataHolder singleCellWellDataHolder);

}
