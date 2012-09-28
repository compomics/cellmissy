/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import java.util.List;

/**
 * Interface for computations with Area values
 * @author Paola Masuzzo
 */
public interface AreaCalculator {

    /**
     * Initialize the calculator with time frames number and list of time steps
     * @param timeFramesNumber
     * @param timeStepsList 
     */
    public void init(int timeFramesNumber, List<TimeStep> timeStepsList);

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
     * 
     * @param data
     * @param plateCondition
     * @return 
     */
    public Double[][] computeAreaIncrease(Double[][] data, PlateCondition plateCondition);

    /**
     * 
     * @param data
     * @param plateCondition
     * @return 
     */
    public Double[][] normalizeCorrectedArea(Double[][] data, PlateCondition plateCondition);
}
