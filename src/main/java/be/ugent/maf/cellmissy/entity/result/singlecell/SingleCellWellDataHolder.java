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
    // data structure containing wells, tracks numbers and time indexes
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

}
