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
    public void generateRawTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults);

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

    /**
     * Generate a vector holding the information True or False: T if the step is
     * motile, F if the step is not considered as motile (according to the
     * motile criterium)
     *
     * @param singleCellPreProcessingResults
     * @param motileCriterium
     */
    public void generateMotileStepsVector(SingleCellPreProcessingResults singleCellPreProcessingResults, double motileCriterium);

    /**
     * Generate a vector with mean velocities per track.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateMeanVelocitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);
}
