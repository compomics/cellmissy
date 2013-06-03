/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.analysis.TrackOperator;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.TrackDataHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("singleCellPreProcessor")
public class SingleCellPreProcessorImpl implements SingleCellPreProcessor {

    @Autowired
    private TrackOperator trackOperator;

    @Override
    public void generateTrackResultsList(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition) {
        List<TrackDataHolder> trackDataHolders = new ArrayList<>();
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        for (Well well : singleCellAnalyzedWells) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                for (Track track : wellHasImagingType.getTrackList()) {
                    TrackDataHolder trackDataHolder = new TrackDataHolder(track);
                    trackDataHolders.add(trackDataHolder);
                }
            }
        }
        singleCellPreProcessingResults.setTrackDataHolders(trackDataHolders);
    }

    @Override
    public void generateDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        int trackPointsNumber = computeTotalTrackPointsNumber(singleCellPreProcessingResults);
        Object[][] dataStructure = new Object[trackPointsNumber][3];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            for (TrackPoint trackPoint : trackDataHolder.getTrack().getTrackPointList()) {
                dataStructure[counter][0] = trackDataHolder.getTrack().getWellHasImagingType().getWell().toString();
                dataStructure[counter][1] = trackPoint.getTrack().getTrackNumber();
                dataStructure[counter][2] = trackPoint.getTimeIndex();
                counter++;
            }
        }
        singleCellPreProcessingResults.setDataStructure(dataStructure);
    }

    @Override
    public void generateRawTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults, double conversionFactor) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[][] rawTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        generateTrackCoordinatesMatrix(singleCellPreProcessingResults, conversionFactor);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
            for (int row = 0; row < trackCoordinatesMatrix.length; row++) {
                rawTrackCoordinatesMatrix[counter] = trackCoordinatesMatrix[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setRawTrackCoordinatesMatrix(rawTrackCoordinatesMatrix);
    }

    @Override
    public void generateNormalizedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[][] normalizedTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        computeNormalizedTrackCoordinates(singleCellPreProcessingResults);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[][] normalizedTrackCoordinates = trackDataHolder.getShiftedTrackCoordinates();
            for (int row = 0; row < normalizedTrackCoordinates.length; row++) {
                normalizedTrackCoordinatesMatrix[counter] = normalizedTrackCoordinates[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setNormalizedTrackCoordinatesMatrix(normalizedTrackCoordinatesMatrix);
    }

    @Override
    public void generateInstantaneousVelocitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[] velocitiesVector = new Double[dataStructure.length];
        computeVelocities(singleCellPreProcessingResults);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[] instantaneousVelocities = trackDataHolder.getInstantaneousVelocities();
            for (int i = 0; i < instantaneousVelocities.length; i++) {
                velocitiesVector[counter] = instantaneousVelocities[i];
                counter++;
            }
        }
        singleCellPreProcessingResults.setInstantaneousVelocitiesVector(velocitiesVector);
    }

    @Override
    public void generateOutliersVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        boolean[] outliersVector = new boolean[dataStructure.length];
        computeOutliers(singleCellPreProcessingResults);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            boolean[] outliers = trackDataHolder.getOutliers();
            for (int i = 0; i < outliers.length; i++) {
                outliersVector[counter] = outliersVector[i];
                counter++;
            }
        }
        singleCellPreProcessingResults.setOutliersVector(outliersVector);
    }

    @Override
    public void generateTrackVelocitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] trackVelocitiesVector = new Double[trackDataHolders.size()];
        generateMeanVelocities(singleCellPreProcessingResults);
        for (int i = 0; i < trackVelocitiesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackVelocity = trackDataHolder.getTrackVelocity();
            trackVelocitiesVector[i] = trackVelocity;
        }
        singleCellPreProcessingResults.setTrackVelocitiesVector(trackVelocitiesVector);
    }

    @Override
    public void generateTimeIndexes(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.generateTimeIndexes(trackDataHolder);
        }
    }

    @Override
    public void computeCumulativeDistances(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeCumulativeDistance(trackDataHolder);
        }
    }

    @Override
    public void computeEuclideanDistances(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeEuclideanDistance(trackDataHolder);
        }
    }

    /**
     * Generate matrix with raw data.
     *
     * @param singleCellPreProcessingResults
     */
    private void generateTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults, double conversionFactor) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.generateTrackCoordinatesMatrix(trackDataHolder, conversionFactor);
        }
    }

    /**
     * Compute normalized track coordinates.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeNormalizedTrackCoordinates(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeNormalizedTrackCoordinates(trackDataHolder);
        }
    }

    /**
     * Compute velocities.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeVelocities(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            computeDeltaMovements(trackDataHolder);
            trackOperator.computeVelocities(trackDataHolder);
        }
    }

    /**
     * Compute delta movements in x and y direction for the specified
     * trackDataHolder
     *
     * @param singleCellPreProcessingResults
     */
    private void computeDeltaMovements(TrackDataHolder trackDataHolder) {
        trackOperator.computeDeltaMovements(trackDataHolder);
    }

    /**
     * Calculate the total number of track points.
     *
     * @param singleCellPreProcessingResults
     * @return
     */
    private int computeTotalTrackPointsNumber(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        int trackPointsNumber = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackPointsNumber += trackDataHolder.getTrack().getTrackPointList().size();
        }
        return trackPointsNumber;
    }

    /**
     * Filter non motile steps, using the given double value.
     *
     * @param singleCellPreProcessingResults
     * @param motileCriterium
     */
    private void computeOutliers(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.filterNonMotileSteps(trackDataHolder);
        }
    }

    /**
     * Generate the filtered velocities vector: velocities are filtered against
     * non motile cells.
     *
     * @param singleCellPreProcessingResults
     */
    private void generateMeanVelocities(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeTrackVelocity(trackDataHolder);
        }
    }
}
