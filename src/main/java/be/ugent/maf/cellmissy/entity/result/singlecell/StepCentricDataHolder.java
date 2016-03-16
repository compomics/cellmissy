/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.analysis.kdtree.KDTree;
import be.ugent.maf.cellmissy.analysis.singlecell.InterpolationMethod;
import be.ugent.maf.cellmissy.entity.Track;
import java.awt.geom.Point2D;

import java.util.List;
import java.util.Map;

/**
 * This class keeps the instantaneous measurements about a certain track, i.e.
 * step-centric measurements.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class StepCentricDataHolder {

    // the track on which the computations are to be done
    private Track track;
    // time indexes
    // the temporal indexes in which the cell has been detected and tracked by the imaging sofware
    private double[] timeIndexes;
    // matrix for track coordinates (x, y)
    // each row is a track point and contains couples of coordinates (x, y)
    private Double[][] coordinatesMatrix;
    // matrix for shifted track coordinates
    // same as the coordinatesMatrix, but the origins of migration are superimposed at (0, 0)
    private Double[][] shiftedCoordinatesMatrix;
    // matrix for delta movements in (x, y) direction
    // differences between location (x[n], y[n]) and location (x[n-1], y[n-1])
    private Double[][] deltaMovements;
    // array for  the instantaneous cell displacements
    // for a track, the minimal instantaneous speeds are derived from the displacement of the cell centroid between
    // adjacent time points
    private Double[] instantaneousDisplacements;
    // array for turning angles
    // a turning angle is the observed turning angle of a cell between sequential time points (it is an instantaneous
    // angle)
    private Double[] turningAngles;
    // array for directionality ratios
    // this is the consistency of motion: the ratio between the net displacement and the cumulative distance at each
    // travelled time point
    // the math concept is the same as for the end point directionality vector, but this is computed in time
    private Double[] directionalityRatios;
    // array for the MSD values
    private Double[] meanSquareDisplacements;
    // list of arrays for the direction autocorrelations
    // a direction autocorrelation is defined as the cosine of the difference between turning angles at successive
    // time points
    private List<Double[]> directionAutocorrelations;
    // a 2D tree of points (spatially)
    private KDTree<Point2D> xY2DTree;
    // the 2D trees of points (for the time series: x(t) and y(t))
    private KDTree<Point2D> xT2DTree;
    private KDTree<Point2D> yT2DTree;
    // a list of lists of eclosing balls 
    // a list of enclosing balls is computed for each radius value
    private List<List<EnclosingBall>> xYEnclosingBalls;
    // same as the spatial enclosing balls, but for the x(t) time series
    private List<List<EnclosingBall>> xTEnclosingBalls;
    // same as the spatial enclosing balls, but for the y(t) time series
    private List<List<EnclosingBall>> yTEnclosingBalls;
    // an array for the mean values of the direction autocorrelations
    // per each time point (overlapping time interval), a mean DA is computed
    private Double[] medianDirectionAutocorrelations;
    // a map containing the interpolation method and the correspondent interpolated track objects
    private Map<InterpolationMethod, InterpolatedTrack> interpolationMap;

    /**
     * Empty Constructor
     */
    public StepCentricDataHolder() {
    }

    /**
     * Constructor takes a track
     *
     * @param track
     */
    public StepCentricDataHolder(Track track) {
        this.track = track;
    }

    /**
     * Getters and setters.
     *
     * @return
     */
    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
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

    public Double[][] getShiftedCoordinatesMatrix() {
        return shiftedCoordinatesMatrix;
    }

    public void setShiftedCoordinatesMatrix(Double[][] shiftedCoordinatesMatrix) {
        this.shiftedCoordinatesMatrix = shiftedCoordinatesMatrix;
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

    public List<Double[]> getDirectionAutocorrelations() {
        return directionAutocorrelations;
    }

    public void setDirectionAutocorrelations(List<Double[]> directionAutocorrelations) {
        this.directionAutocorrelations = directionAutocorrelations;
    }

    public Double[] getMedianDirectionAutocorrelations() {
        return medianDirectionAutocorrelations;
    }

    public void setMedianDirectionAutocorrelations(Double[] medianDirectionAutocorrelations) {
        this.medianDirectionAutocorrelations = medianDirectionAutocorrelations;
    }

    public KDTree<Point2D> getxY2DTree() {
        return xY2DTree;
    }

    public void setxY2DTree(KDTree<Point2D> xY2DTree) {
        this.xY2DTree = xY2DTree;
    }

    public KDTree<Point2D> getxT2DTree() {
        return xT2DTree;
    }

    public void setxT2DTree(KDTree<Point2D> xT2DTree) {
        this.xT2DTree = xT2DTree;
    }

    public KDTree<Point2D> getyT2DTree() {
        return yT2DTree;
    }

    public void setyT2DTree(KDTree<Point2D> yT2DTree) {
        this.yT2DTree = yT2DTree;
    }

    public List<List<EnclosingBall>> getxYEnclosingBalls() {
        return xYEnclosingBalls;
    }

    public void setxYEnclosingBalls(List<List<EnclosingBall>> xYEnclosingBalls) {
        this.xYEnclosingBalls = xYEnclosingBalls;
    }

    public List<List<EnclosingBall>> getxTEnclosingBalls() {
        return xTEnclosingBalls;
    }

    public void setxTEnclosingBalls(List<List<EnclosingBall>> xTEnclosingBalls) {
        this.xTEnclosingBalls = xTEnclosingBalls;
    }

    public List<List<EnclosingBall>> getyTEnclosingBalls() {
        return yTEnclosingBalls;
    }

    public void setyTEnclosingBalls(List<List<EnclosingBall>> yTEnclosingBalls) {
        this.yTEnclosingBalls = yTEnclosingBalls;
    }

    public Map<InterpolationMethod, InterpolatedTrack> getInterpolationMap() {
        return interpolationMap;
    }

    public void setInterpolationMap(Map<InterpolationMethod, InterpolatedTrack> interpolationMap) {
        this.interpolationMap = interpolationMap;
    }
}
