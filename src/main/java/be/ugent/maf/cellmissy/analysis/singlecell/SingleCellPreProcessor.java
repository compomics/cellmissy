/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;

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
     * Generate time indexes.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTimeIndexes(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate track durations.
     *
     * @param timeLapse
     * @param singleCellPreProcessingResults
     */
    public void generateTrackDurations(Double timeLapse, SingleCellPreProcessingResults singleCellPreProcessingResults);

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
     * Generate the instantaneous displacement Vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateInstantaneousDisplacementsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate a vector with track displacements.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTrackDisplacementsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

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
     * Generate a vector with the median track speeds.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateTrackSpeedsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate directionality vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateDirectionalitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate vector with convex hulls.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateConvexHullsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate vector with displacement ratios.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateDisplacementRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate vector with outreach ratios.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateOutreachRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

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
    public void generateMedianTurningAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults);
}
