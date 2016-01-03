/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.entity.Well;
import java.util.List;

/**
 * A class to keep data on the well level.
 *
 * @author Paola
 */
public class SingleCellWellDataHolder {

    // the well
    private Well well;
    // list of track data holders
    private List<TrackDataHolder> trackDataHolders;
    // data structure containing tracks numbers and time indexes
    // this data structure is on the track point level
    private Object[][] dataStructure;
    // raw data track coordinates
    private Double[][] rawTrackCoordinatesMatrix;
    // track coordinates shifted to position (0, 0)
    private Double[][] shiftedTrackCoordinatesMatrix;
    // 2x2 matrix with raw coordinates ranges (xmin-xmax-y-min-ymax)
    private Double[][] rawCoordinatesRanges;
    // 2x2 matrix with shifted coordinates ranges (xmin-xmax-y-min-ymax)
    private Double[][] shiftedCoordinatesRanges;
    // array for instantaneous displacements
    private Double[] instantaneousDisplacementsVector;
    // array for instantaneous directionality ratios
    private Double[] directionalityRatiosVector;
    // array for the median directionality ratios (median in time)
    private Double[] medianDirectionalityRatiosVector;
    // array for track displacements
    private Double[] trackDisplacementsVector;
    // array for track speeds
    private Double[] trackSpeedsVector;
    // array for track cumulative distancse
    private Double[] cumulativeDistancesVector;
    // array for track Euclidean distancse
    private Double[] euclideanDistancesVector;
    // array for directionalities
    private Double[] endPointDirectionalityRatios;
    // array for convex hulls
    private ConvexHull[] convexHullsVector;
    // array for displacement ratios
    private Double[] displacementRatiosVector;
    // array for outreach ratios
    private Double[] outreachRatiosVector;
    // array for turning angles
    private Double[] turningAnglesVector;
    // array for track angles
    private Double[] medianTurningAnglesVector;
    // array with the median direction autocorrelations
    private Double[] medianDirectionAutocorrelationsVector;

    /**
     * Empty Constructor.
     */
    public SingleCellWellDataHolder() {
    }

    public SingleCellWellDataHolder(Well well) {
        this.well = well;
    }

    public Well getWell() {
        return well;
    }

    public void setWell(Well well) {
        this.well = well;
    }

    public List<TrackDataHolder> getTrackDataHolders() {
        return trackDataHolders;
    }

    public void setTrackDataHolders(List<TrackDataHolder> trackDataHolders) {
        this.trackDataHolders = trackDataHolders;
    }

    public Object[][] getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(Object[][] dataStructure) {
        this.dataStructure = dataStructure;
    }

    public Double[][] getRawTrackCoordinatesMatrix() {
        return rawTrackCoordinatesMatrix;
    }

    public void setRawTrackCoordinatesMatrix(Double[][] rawTrackCoordinatesMatrix) {
        this.rawTrackCoordinatesMatrix = rawTrackCoordinatesMatrix;
    }

    public Double[][] getShiftedTrackCoordinatesMatrix() {
        return shiftedTrackCoordinatesMatrix;
    }

    public void setShiftedTrackCoordinatesMatrix(Double[][] shiftedTrackCoordinatesMatrix) {
        this.shiftedTrackCoordinatesMatrix = shiftedTrackCoordinatesMatrix;
    }

    public Double[][] getRawCoordinatesRanges() {
        return rawCoordinatesRanges;
    }

    public void setRawCoordinatesRanges(Double[][] rawCoordinatesRanges) {
        this.rawCoordinatesRanges = rawCoordinatesRanges;
    }

    public Double[][] getShiftedCoordinatesRanges() {
        return shiftedCoordinatesRanges;
    }

    public void setShiftedCoordinatesRanges(Double[][] shiftedCoordinatesRanges) {
        this.shiftedCoordinatesRanges = shiftedCoordinatesRanges;
    }

    public Double[] getInstantaneousDisplacementsVector() {
        return instantaneousDisplacementsVector;
    }

    public void setInstantaneousDisplacementsVector(Double[] instantaneousDisplacementsVector) {
        this.instantaneousDisplacementsVector = instantaneousDisplacementsVector;
    }

    public Double[] getDirectionalityRatiosVector() {
        return directionalityRatiosVector;
    }

    public void setDirectionalityRatiosVector(Double[] directionalityRatiosVector) {
        this.directionalityRatiosVector = directionalityRatiosVector;
    }

    public Double[] getMedianDirectionalityRatiosVector() {
        return medianDirectionalityRatiosVector;
    }

    public void setMedianDirectionalityRatiosVector(Double[] medianDirectionalityRatiosVector) {
        this.medianDirectionalityRatiosVector = medianDirectionalityRatiosVector;
    }

    public Double[] getTrackDisplacementsVector() {
        return trackDisplacementsVector;
    }

    public void setTrackDisplacementsVector(Double[] trackDisplacementsVector) {
        this.trackDisplacementsVector = trackDisplacementsVector;
    }

    public Double[] getTrackSpeedsVector() {
        return trackSpeedsVector;
    }

    public void setTrackSpeedsVector(Double[] trackSpeedsVector) {
        this.trackSpeedsVector = trackSpeedsVector;
    }

    public Double[] getCumulativeDistancesVector() {
        return cumulativeDistancesVector;
    }

    public void setCumulativeDistancesVector(Double[] cumulativeDistancesVector) {
        this.cumulativeDistancesVector = cumulativeDistancesVector;
    }

    public Double[] getEuclideanDistancesVector() {
        return euclideanDistancesVector;
    }

    public void setEuclideanDistancesVector(Double[] euclideanDistancesVector) {
        this.euclideanDistancesVector = euclideanDistancesVector;
    }

    public Double[] getEndPointDirectionalityRatios() {
        return endPointDirectionalityRatios;
    }

    public void setEndPointDirectionalityRatios(Double[] endPointDirectionalityRatios) {
        this.endPointDirectionalityRatios = endPointDirectionalityRatios;
    }

    public ConvexHull[] getConvexHullsVector() {
        return convexHullsVector;
    }

    public void setConvexHullsVector(ConvexHull[] convexHullsVector) {
        this.convexHullsVector = convexHullsVector;
    }

    public Double[] getDisplacementRatiosVector() {
        return displacementRatiosVector;
    }

    public void setDisplacementRatiosVector(Double[] displacementRatiosVector) {
        this.displacementRatiosVector = displacementRatiosVector;
    }

    public Double[] getOutreachRatiosVector() {
        return outreachRatiosVector;
    }

    public void setOutreachRatiosVector(Double[] outreachRatiosVector) {
        this.outreachRatiosVector = outreachRatiosVector;
    }

    public Double[] getTurningAnglesVector() {
        return turningAnglesVector;
    }

    public void setTurningAnglesVector(Double[] turningAnglesVector) {
        this.turningAnglesVector = turningAnglesVector;
    }

    public Double[] getMedianTurningAnglesVector() {
        return medianTurningAnglesVector;
    }

    public void setMedianTurningAnglesVector(Double[] medianTurningAnglesVector) {
        this.medianTurningAnglesVector = medianTurningAnglesVector;
    }

    public Double[] getMedianDirectionAutocorrelationsVector() {
        return medianDirectionAutocorrelationsVector;
    }

    public void setMedianDirectionAutocorrelationsVector(Double[] medianDirectionAutocorrelationsVector) {
        this.medianDirectionAutocorrelationsVector = medianDirectionAutocorrelationsVector;
    }

}
