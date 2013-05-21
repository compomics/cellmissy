/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;

/**
 * Interface for single cell pre-processing.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface SingleCellPreProcessor {

    /**
     *
     * @param singleCellPreProcessingResults
     * @param plateCondition
     */
    public void generateTrackResultsList(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition);

    /**
     *
     * @param singleCellPreProcessingResults
     * @param plateCondition
     */
    public void generateDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     *
     * @param singleCellPreProcessingResults
     */
    public void generateNormalizedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     *
     * @param singleCellPreProcessingResults
     */
    public void generateVelocitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);
}
