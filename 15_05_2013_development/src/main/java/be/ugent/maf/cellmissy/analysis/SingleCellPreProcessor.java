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
     * Put all together the track data holders for a certain plate condition.
     *
     * @param singleCellPreProcessingResults
     * @param plateCondition: we need here a plate condition because this can be
     * done only on the single cell analysed wells. We'll get these wells having
     * the condition.
     */
    public void generateTrackDataHolders(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition);

    /**
     * Generate the data structure: this will put together the well of the
     * track, the track number and its time index.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTimeIndexes(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the track coordinates matrix.
     *
     * @param singleCellPreProcessingResults
     * @param conversionFactor
     */
    public void generateRawTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults, double conversionFactor);

    /**
     * Compute the coordinates range.
     *
     * @param singleCellPreProcessingResults
     */
    public void computeCoordinatesRanges(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the shifted track coordinates matrix.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateShiftedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the instantaneous Speeds Vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateInstantaneousSpeedsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate a vector with mean speeds per track.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTrackSpeedsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate cumulative distances vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateCumulativeDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate Euclidean distances vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateEuclideanDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate directionality vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateDirectionalitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate turning angles vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTurningAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate track angles vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTrackAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);
}
