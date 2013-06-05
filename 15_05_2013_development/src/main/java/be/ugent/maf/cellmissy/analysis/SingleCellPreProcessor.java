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
    public void generateTimeIndexes(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     *
     * @param singleCellPreProcessingResults
     * @param conversionFactor
     */
    public void generateRawTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults, double conversionFactor);

    /**
     *
     * @param singleCellPreProcessingResults
     */
    public void computeCoordinatesRanges(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     *
     * @param singleCellPreProcessingResults
     */
    public void generateShiftedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate a vector holding the information True or False: T if the step is
     * motile, F if the step is not considered as motile (according to the
     * motile criterium)
     *
     * @param singleCellPreProcessingResults
     * @param motileCriterium
     */
    public void generateOutliersVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     *
     * @param singleCellPreProcessingResults
     */
    public void generateInstantaneousVelocitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate a vector with mean velocities per track.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTrackVelocitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Compute cumulative distances for each track.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateCumulativeDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Compute Euclidean distances for each track.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateEuclideanDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Compute directionality for each track.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateDirectionalitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTurningAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);
    
    /**
     * 
     * @param singleCellPreProcessingResults 
     */
    public void generateTrackAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);
}
