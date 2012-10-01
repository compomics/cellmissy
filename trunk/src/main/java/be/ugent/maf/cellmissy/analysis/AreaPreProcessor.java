/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import java.util.List;

/**
 * Interface for area data pre-processing: Normalize Area, Identify and Correct for Outliers
 * @author Paola Masuzzo
 */
public interface AreaPreProcessor {

    /**
     * set time frames number (for one experiment this number changes, so this method needs to be called only once)
     * @param timeFramesNumber 
     */
    public void setTimeFramesNumber(int timeFramesNumber);

    /**
     * set time steps list (for each condition time steps list changes)
     * @param timeStepsList 
     */
    public void setTimeStepsList(List<TimeStep> timeStepsList);

    /**
     * Compute time frames, knowing the interval of the experiment
     * @param experimentInterval
     * @return an array of double with time frames
     */
    public double[] computeTimeFrames(Double experimentInterval);

    /**
     * Normalize Area values (Area @ Time Frame zero is zero)
     * @param data (area values for one condition)
     * @return a 2D array of Double with normalized values
     */
    public Double[][] computeNormalizedArea(Double[][] data);

    /**
     * Compute increments of Area from one time frame to the following one (Delta Area Values)
     * @param data (area values for one condition)
     * @return a 2D array of Double with delta values
     */
    public Double[][] computeDeltaArea(Double[][] data);

    /**
     * Compute %area increase for a certain condition
     * @param data
     * @param plateCondition
     * @return 
     */
    public Double[][] computeAreaIncrease(Double[][] data, PlateCondition plateCondition);

    /**
     * Compute Normalized Corrected Area values for a certain Condition
     * @param data
     * @param plateCondition
     * @return 
     */
    public Double[][] normalizeCorrectedArea(Double[][] data, PlateCondition plateCondition);

    /**
     * Using the Outlier Interface, compute OUTLIERS values for a certain dataset
     * @param data
     * @return 
     */
    public double[] computeOutliers(double[] data);

    /**
     * Using the outlier interface, compute Corrected Values (Outliers are being deleted from dataset)
     * @param data
     * @return 
     */
    public double[] computeCorrectedArea(double[] data);
}
