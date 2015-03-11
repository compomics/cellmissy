/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

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
    public void generateTimeIndexes(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Generate Track Point Matrix for a track. This is already scaling
     * according to the conversion factor, going from pixels to micrometers. If
     * the conversion factor is equal to one, data was already in micrometers
     * and no actual conversion is taken up.
     *
     * @param stepCentricDataHolder
     */
    public void generateCoordinatesMatrix(StepCentricDataHolder stepCentricDataHolder, double conversionFactor);

    /**
     * Compute shifted track coordinates starting from the track coordinates
     * matrix; shifted means track coordinates are normalised to 0.
     *
     * @param stepCentricDataHolder
     */
    public void computeShiftedCoordinatesMatrix(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute delta movements matrix.
     *
     * @param stepCentricDataHolder
     */
    public void computeDeltaMovements(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute minimal instantaneous cell displacements.
     *
     * @param stepCentricDataHolder
     */
    public void computeInstantaneousDisplacements(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute turning angles.
     *
     * @param stepCentricDataHolder
     */
    public void computeTurningAngles(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute directionality ratios.
     *
     * @param stepCentricDataHolder
     */
    public void computeDirectionalityRatios(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute MSD values.
     *
     * @param stepCentricDataHolder
     */
    public void computeMeanSquareDisplacements(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute direction autocorrelation values.
     *
     * @param stepCentricDataHolder
     */
    public void computeDirectionAutocorrelations(StepCentricDataHolder stepCentricDataHolder);

    /**
     * Compute mean direction autocorrelations values.
     *
     * @param stepCentricDataHolder
     */
    public void computeMeanDirectionAutocorrelations(StepCentricDataHolder stepCentricDataHolder);
}
