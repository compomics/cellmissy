/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.interpolation;

import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;

/**
 * An interface to perform operations on an interpolated cell track.
 *
 * @author Paola
 */
public interface InterpolatedTrackOperator {

    /**
     * Compute the matrix holding the coordinates of the interpolated track.
     *
     * @param interpolatedTrack
     */
    void computeCoordinatesMatrix(InterpolatedTrack interpolatedTrack);

    /**
     * Compute the double array holding the delta movements in both x and y
     * directions for an interpolated track.
     *
     * @param interpolatedTrack
     */
    void computeDeltaMovements(InterpolatedTrack interpolatedTrack);

    /**
     * Compute the turning angles for an interpolated cell track.
     *
     * @param interpolatedTrack
     */
    void computeTurningAngles(InterpolatedTrack interpolatedTrack);

}
