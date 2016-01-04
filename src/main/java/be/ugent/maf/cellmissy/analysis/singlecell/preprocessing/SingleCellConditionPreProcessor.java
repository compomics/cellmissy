/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import java.util.List;

/**
 * An interface to preprocess data at a condition level, using the well level
 * (one level below).
 *
 * @author Paola
 */
public interface SingleCellConditionPreProcessor {

    /**
     * Generate the data holders.
     *
     * @param singleCellConditionDataHolder
     */
    void generateDataHolders(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the data structure: this will put together the well of the
     * track, the track number and its time index.
     *
     * @param singleCellConditionDataHolder
     */
    void generateDataStructure(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * This will perform all the operations required on the step-centric and
     * cell-centric level.
     *
     * @param singleCellConditionDataHolder
     * @param conversionFactor: required for coordinates matrix computation
     * @param timeLapse: required for track duration computations
     */
    void preProcessStepsAndCells(SingleCellConditionDataHolder singleCellConditionDataHolder,
              double conversionFactor, double timeLapse);

    /**
     * Generate the track coordinates matrix.
     *
     * @param singleCellConditionDataHolder
     */
    void generateRawTrackCoordinatesMatrix(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the shifted track coordinates matrix.
     *
     * @param singleCellConditionDataHolder
     */
    void generateShiftedTrackCoordinatesMatrix(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate raw coordinates ranges.
     *
     * @param singleCellConditionDataHolder
     */
    void generateRawCoordinatesRanges(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate shifted coordinates ranges.
     *
     * @param singleCellConditionDataHolder
     */
    void generateShiftedCoordinatesRanges(SingleCellConditionDataHolder singleCellConditionDataHolder);
    
      /**
     * Estimate density function for a given array of data.
     *
     * @param data
     * @param kernelDensityEstimatorBeanName
     * @return
     */
    List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName);
}
