/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing;

import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;

/**
 * An interface to perform track interpolation.
 *
 * @author Paola
 */
public interface TrackInterpolator {

    /**
     * Interpolate a track with different interpolation techniques.
     *
     * @param time: the time points
     * @param x: the x coordinates
     * @param y: the y coordinates
     * @param interpolationPoints: how many points shall I use for the
     * interpolation?
     * @return: an interpolated track object
     */
    InterpolatedTrack interpolateTrack(double[] time, double[] x, double[] y);
}
