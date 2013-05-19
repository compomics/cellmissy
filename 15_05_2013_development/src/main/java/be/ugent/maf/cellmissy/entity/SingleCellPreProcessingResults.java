/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

/**
 * This class is holding the results from the pre processing of single cell
 * analysis.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class SingleCellPreProcessingResults {

    // data structure containing wells, tracks numbers and time indexes 
    private Object[][] fixedDataStructure;
    // track coordinates normalized to position (0, 0)
    private Double[][] normalizedTrackCoordinates;
    // directional movements in x and y
    private Double[][] directionalMovements;

    public Object[][] getFixedDataStructure() {
        return fixedDataStructure;
    }

    public void setFixedDataStructure(Object[][] fixedDataStructure) {
        this.fixedDataStructure = fixedDataStructure;
    }

    public Double[][] getNormalizedTrackCoordinates() {
        return normalizedTrackCoordinates;
    }

    public void setNormalizedTrackCoordinates(Double[][] normalizedTrackCoordinates) {
        this.normalizedTrackCoordinates = normalizedTrackCoordinates;
    }

    public Double[][] getDirectionalMovements() {
        return directionalMovements;
    }

    public void setDirectionalMovements(Double[][] directionalMovements) {
        this.directionalMovements = directionalMovements;
    }
}
