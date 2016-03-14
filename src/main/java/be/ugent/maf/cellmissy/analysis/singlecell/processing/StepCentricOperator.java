/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;

/**
 * An interface to operate on step-centric data. As such, works basically with a
 * step centric data holder entity.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface StepCentricOperator {

    /**
     * Generate array with time indexes of track.
     *
     * @param stepCentricDataHolder
     */
    void generateTimeIndexes(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Generate Track Point Matrix for a track. This is already scaling
     * according to the conversion factor, going from pixels to micrometers. If
     * the conversion factor is equal to one, data was already in micrometers
     * and no actual conversion is taken up.
     *
     * @param stepCentricDataHolder
     * @param conversionFactor
     */
    void generateCoordinatesMatrix(StepCentricDataHolder stepCentricDataHolder, double conversionFactor);

    /**
     * Compute shifted track coordinates starting from the track coordinates
     * matrix; shifted means track coordinates are normalized to 0.
     *
     * @param stepCentricDataHolder
     */
    void computeShiftedCoordinatesMatrix(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute delta movements matrix.
     *
     * @param stepCentricDataHolder
     */
    void computeDeltaMovements(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute minimal instantaneous cell displacements.
     *
     * @param stepCentricDataHolder
     */
    void computeInstantaneousDisplacements(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute turning angles.
     *
     * @param stepCentricDataHolder
     */
    void computeTurningAngles(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute directionality ratios.
     *
     * @param stepCentricDataHolder
     */
    void computeDirectionalityRatios(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute MSD values.
     *
     * @param stepCentricDataHolder
     */
    void computeMeanSquareDisplacements(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute direction autocorrelation values.
     *
     * @param stepCentricDataHolder
     */
    void computeDirectionAutocorrelations(StepCentricDataHolder stepCentricDataHolder);

    // these might not be needed in the end!!!
    void computeDiffAngles(StepCentricDataHolder stepCentricDataHolder);

    // these might not be needed in the end!!!
    void computeDirAutocorrMatrix(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute mean direction autocorrelations values.
     *
     * @param stepCentricDataHolder
     */
    void computeMeanDirectionAutocorrelations(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Initialize the 2D trees for the step centric data holder: one 2D tree for
     * the x-y coordinates, one for the x(t) time series, and one for the y(t)
     * time series.
     *
     * @param stepCentricDataHolder
     */
    void init2Dtrees(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute the spatial enclosing balls for the step centric data holder.
     *
     * @param stepCentricDataHolder
     */
    void computeXYEnclosingBalls(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute the balls enclosing the x(t) time series for the step centric
     * data holder.
     *
     * @param stepCentricDataHolder
     */
    void computeXTEnclosingBalls(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute the balls enclosing the y(t) time series for the step centric
     * data holder.
     *
     * @param stepCentricDataHolder
     */
    void computeYTEnclosingBalls(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Interpolate cell trajectory.
     *
     * @param stepCentricDataHolder
     */
    void interpolateTrack(StepCentricDataHolder stepCentricDataHolder);
}
