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
     * @param conversionFactor
     * @param timeLapse
     */
    public void generateTrackDataHolders(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition, double conversionFactor, Double timeLapse);

    /**
     * Generate the data structure: this will put together the well of the
     * track, the track number and its time index.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * This will perform all the operations required on the step-centric and
     * cell-centric level.
     *
     * @param singleCellPreProcessingResults
     */
    public void operateOnStepsAndCells(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the track coordinates matrix.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateRawTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the shifted track coordinates matrix.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateShiftedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate raw coordinates ranges.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateRawCoordinatesRanges(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate shifted coordinates ranges.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateShiftedCoordinatesRanges(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the instantaneous displacement Vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateInstantaneousDisplacementsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the directionality ratios vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateDirectionalityRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

    /**
     * Generate the median directionality ratios vector.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateMedianDirectionalityRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

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
    public void generateEndPointDirectionalityRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults);

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

    /**
     * Generate vector with the median direction autocorrelations.
     *
     * @param singleCellPreProcessingResults
     */
    public void generateMedianDirectionAutocorrelationsVector(SingleCellPreProcessingResults singleCellPreProcessingResults);
}
