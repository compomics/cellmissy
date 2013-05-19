/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;

/**
 * Interface for single cell pre-processing.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface SingleCellPreProcessor {

    /**
     *
     * @param singleCellPreProcessingResults
     * @param plateCondition
     */
    public void generateFixedDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition);
    
    /**
     * Compute directional movements in x and y directions
     * @param singleCellPreProcessingResults
     * @param plateCondition 
     */
    public void computeDirectionalMovements(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition);
}
