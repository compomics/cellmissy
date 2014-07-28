/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.entity.Track;

/**
 * This class keeps the instantaneous measurements about a certain track.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class StepCentricDataHolder {
    // the track on which the computations are to be done
    private Track track;
    // the conversion factor (from pixel to another unit of measuremnt)
    private double conversionFactor;
    // acquisition frame
    private Double timeLapse;
    // time indexes
    // the temporal indexes in which the cell has been detected and tracked by the imaging sofware
    private double[] timeIndexes;
    // matrix for track coordinates (x, y)
    // each row is a track point and contains couples of coordinates (x, y)
    private Double[][] coordinatesMatrix;
    // matrix for shifted track coordinates
    // same as the coordinatesMatrix, but the origins of migration are superimposed at (0, 0)
    private Double[][] shiftedCooordinatesMatrix;
    // matrix for delta movements in (x, y) direction
    // differences between location (x[n], y[n]) and location (x[n-1], y[n-1])
    private Double[][] deltaMovements;
    // array for  the instantaneous cell displacements
    // for a track, the minimal instantaneous speeds are derived from the displacement of the cell centroid between adjacent time points
    private Double[] instantaneousDisplacements;
    // array for turning angles
    // a turning angle is the observed turning angle of a cell between sequential time points (it is an instantaneous angle)
    private Double[] turningAngles;
    // array for directionality ratios
    // this is the consistency of motion: the ratio between the net displacement and the cumulative distance at each travelled time point
    private Double[] directionalityRatios;
    // array for the MSD values
    private Double[] meanSquareDisplacements;
    // array for the direction autocorrelation values
    private Double[] directionAutocorrelations;

    /**
     * Constructor
     */
    public StepCentricDataHolder() {
    }

    public StepCentricDataHolder(Track track) {
        this.track = track;
    }

    public StepCentricDataHolder(Track track, double conversionFactor) {
        this.track = track;
        this.conversionFactor = conversionFactor;
    }

    public StepCentricDataHolder(Track track, double conversionFactor, Double timeLapse) {
        this.track = track;
        this.conversionFactor = conversionFactor;
        this.timeLapse = timeLapse;
    }

    /**
     * Getters and setters.
     *
     */
    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public Double getTimeLapse() {
        return timeLapse;
    }

    public void setTimeLapse(Double timeLapse) {
        this.timeLapse = timeLapse;
    }

    public double[] getTimeIndexes() {
        return timeIndexes;
    }

    public void setTimeIndexes(double[] timeIndexes) {
        this.timeIndexes = timeIndexes;
    }

    public Double[][] getCoordinatesMatrix() {
        return coordinatesMatrix;
    }

    public void setCoordinatesMatrix(Double[][] coordinatesMatrix) {
        this.coordinatesMatrix = coordinatesMatrix;
    }

    public Double[][] getShiftedCooordinatesMatrix() {
        return shiftedCooordinatesMatrix;
    }

    public void setShiftedCooordinatesMatrix(Double[][] shiftedCooordinatesMatrix) {
        this.shiftedCooordinatesMatrix = shiftedCooordinatesMatrix;
    }

    public Double[][] getDeltaMovements() {
        return deltaMovements;
    }

    public void setDeltaMovements(Double[][] deltaMovements) {
        this.deltaMovements = deltaMovements;
    }

    public Double[] getInstantaneousDisplacements() {
        return instantaneousDisplacements;
    }

    public void setInstantaneousDisplacements(Double[] instantaneousDisplacements) {
        this.instantaneousDisplacements = instantaneousDisplacements;
    }

    public Double[] getTurningAngles() {
        return turningAngles;
    }

    public void setTurningAngles(Double[] turningAngles) {
        this.turningAngles = turningAngles;
    }

    public Double[] getDirectionalityRatios() {
        return directionalityRatios;
    }

    public void setDirectionalityRatios(Double[] directionalityRatios) {
        this.directionalityRatios = directionalityRatios;
    }

    public Double[] getMeanSquareDisplacements() {
        return meanSquareDisplacements;
    }

    public void setMeanSquareDisplacements(Double[] meanSquareDisplacements) {
        this.meanSquareDisplacements = meanSquareDisplacements;
    }

    public Double[] getDirectionAutocorrelations() {
        return directionAutocorrelations;
    }

    public void setDirectionAutocorrelations(Double[] directionAutocorrelations) {
        this.directionAutocorrelations = directionAutocorrelations;
    }
}
