package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;

/**
 * Interface for single cell operations: uses the track operator interface, and bring all the operations to the plate
 * condition level.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface SingleCellOperator {

    /**
     * This will perform all the operations required on the step-centric and
     * cell-centric level.
     *
     * @param singleCellConditionDataHolder
     */
    public void operateOnStepsAndCells(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the instantaneous displacement vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateInstantaneousDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the directionality ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median directionality ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateMedianDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the track displacements vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateTrackDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the cumulative distances vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateCumulativeDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate Euclidean distances vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateEuclideanDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median track speeds vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateTrackSpeedsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the directionality vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateEndPointDirectionalityRatiosVector(SingleCellConditionDataHolder
                                                                   singleCellConditionDataHolder);

    /**
     * Generate the convex hulls vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateConvexHullsVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the displacement ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateDisplacementRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the outreach ratios vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateOutreachRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the turning angles vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateTurningAnglesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median turning angles vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateMedianTurningAnglesVector(SingleCellConditionDataHolder singleCellConditionDataHolder);

    /**
     * Generate the median direction autocorrelations vector.
     *
     * @param singleCellConditionDataHolder
     */
    public void generateMedianDirectionAutocorrelationsVector(SingleCellConditionDataHolder
                                                                      singleCellConditionDataHolder);

}
