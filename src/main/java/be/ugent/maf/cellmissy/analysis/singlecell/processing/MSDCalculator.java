/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;

/**
 * An interface to perform mean-squared displacement computation on a cell
 * trajectory.
 *
 * @author Paola
 */
public interface MSDCalculator {

    /**
     * For a step centric data holder, compute (thus set the mean-squared
     * displacements).
     *
     * @param stepCentricDataHolder
     * @return a 2D array containing both lag times (col:0) and msd values
     * (col:1).
     */
    double[][] computeMSD(StepCentricDataHolder stepCentricDataHolder);
}
