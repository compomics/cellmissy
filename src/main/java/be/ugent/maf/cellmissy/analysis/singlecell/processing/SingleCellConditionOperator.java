/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;

/**
 * An interface to operate on a condition level.
 *
 * @author Paola
 */
public interface SingleCellConditionOperator {

    /**
     * This will perform all the operations required on the step-centric and
     * cell-centric level.
     *
     * @param singleCellConditionDataHolder
     */
    void operateOnStepsAndCells(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the instantaneous displacement vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateInstantaneousDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the directionality ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median directionality ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateMedianDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the track displacements vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateTrackDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);
    
    /**
     * Compute the median speed across replicates for a condition.
     *
     * @param singleCellConditionDataHolder
     */
    void computeMedianSpeed(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the cumulative distances vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateCumulativeDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate Euclidean distances vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateEuclideanDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median track speeds vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateTrackSpeedsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     *
     * @param singleCellConditionDataHolder
     */
    void generateMSDArray(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the directionality vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateEndPointDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the convex hulls vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateConvexHullsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the displacement ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateDisplacementRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the outreach ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateOutreachRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the turning angles vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateTurningAnglesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median turning angles vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateMedianTurningAnglesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median direction autocorrelations vector.
     *
     * @param singleCellConditionDataHolder
     */
    void generateMedianDirectionAutocorrelationsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Operate (i.e. perform computations) on interpolated tracks for a single
     * cell condition data holder.
     *
     * @param singleCellConditionDataHolder
     */
    void operateOnInterpolatedTracks(SingleCellConditionDataHolder singleCellConditionDataHolder);
}
