/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;

/**
 * An operator to compute data on a well level.
 *
 * @author Paola
 */
public interface SingleCellWellOperator {

    /**
     * This will perform all the operations required on the step-centric and
     * cell-centric level.
     *
     * @param singleCellWellDataHolder
     */
    void operateOnStepsAndCells(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the instantaneous displacement vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateInstantaneousDisplacementsVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the directionality ratios vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateDirectionalityRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the median directionality ratios vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateMedianDirectionalityRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the track displacements vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateTrackDisplacementsVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the cumulative distances vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateCumulativeDistancesVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate Euclidean distances vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateEuclideanDistancesVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the median track speeds vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateTrackSpeedsVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * 
     * @param singleCellWellDataHolder 
     */
    void generateMSDArray(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the directionality vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateEndPointDirectionalityRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the convex hulls vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateConvexHullsVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the displacement ratios vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateDisplacementRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the outreach ratios vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateOutreachRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the turning angles vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateTurningAnglesVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the median turning angles vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateMedianTurningAnglesVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Generate the median direction autocorrelations vector.
     *
     * @param singleCellWellDataHolder
     */
    void generateMedianDirectionAutocorrelationsVector(SingleCellWellDataHolder singleCellWellDataHolder);

    /**
     * Operate (i.e. perform computations) on the interpolated tracks of a
     * single cell well data holder.
     *
     * @param singleCellWellDataHolder
     */
    void operateOnInterpolatedTracks(SingleCellWellDataHolder singleCellWellDataHolder);

}
