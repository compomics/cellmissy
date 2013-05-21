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
        List<TrackDataHolder> trackPreProcessingResultsList = new ArrayList<>();
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        for (Well well : singleCellAnalyzedWells) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                for (Track track : wellHasImagingType.getTrackList()) {
                    TrackDataHolder trackDataHolder = new TrackDataHolder(track);
                    trackPreProcessingResultsList.add(trackDataHolder);
                }
            }
        }
        singleCellPreProcessingResults.setTrackDataHolders(trackPreProcessingResultsList);
    }

    @Override
    public void generateDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        int trackPointsNumber = computeTotalTrackPointsNumber(singleCellPreProcessingResults);
        Object[][] dataStructure = new Object[trackPointsNumber][3];
        int counter = 0;
        for (TrackDataHolder trackPreProcessingResults : singleCellPreProcessingResults.getTrackDataHolders()) {
            for (TrackPoint trackPoint : trackPreProcessingResults.getTrack().getTrackPointList()) {
                dataStructure[counter][0] = trackPreProcessingResults.getTrack().getWellHasImagingType().getWell().toString();
                dataStructure[counter][1] = trackPoint.getTrack().getTrackNumber();
                dataStructure[counter][2] = trackPoint.getTimeIndex();
                counter++;
            }
        }
        singleCellPreProcessingResults.setDataStructure(dataStructure);
    }

    @Override
    public void generateNormalizedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[][] normalizedTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        computeNormalizedTrackCoordinates(singleCellPreProcessingResults);
        List<TrackDataHolder> trackPreProcessingResultsList = singleCellPreProcessingResults.getTrackDataHolders();
        int counter = 0;
        for (TrackDataHolder trackPreProcessingResults : trackPreProcessingResultsList) {
            Double[][] normalizedTrackCoordinates = trackPreProcessingResults.getNormalizedTrackCoordinates();
            for (int row = 0; row < normalizedTrackCoordinates.length; row++) {
                normalizedTrackCoordinatesMatrix[counter] = normalizedTrackCoordinates[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setNormalizedTrackCoordinatesMatrix(normalizedTrackCoordinatesMatrix);
    }

    @Override
    public void generateVelocitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[] velocitiesVector = new Double[dataStructure.length];
        computeVelocities(singleCellPreProcessingResults);
        List<TrackDataHolder> trackPreProcessingResultsList = singleCellPreProcessingResults.getTrackDataHolders();
        int counter = 0;
        for (TrackDataHolder trackPreProcessingResults : trackPreProcessingResultsList) {
            Double[] velocities = trackPreProcessingResults.getVelocities();
            for (int row = 0; row < velocities.length; row++) {
                velocitiesVector[counter] = velocities[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setVelocitiesVector(velocitiesVector);
    }

    /**
     *
     * @param singleCellPreProcessingResults
     */
    private void computeNormalizedTrackCoordinates(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackPreProcessingResults : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.generateTrackCoordinatesMatrix(trackPreProcessingResults);
            trackOperator.computeNormalizedTrackCoordinates(trackPreProcessingResults);
        }
    }

    /**
     *
     * @param singleCellPreProcessingResults
     */
    private void computeVelocities(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackPreProcessingResults : singleCellPreProcessingResults.getTrackDataHolders()) {
            computeDeltaMovements(trackPreProcessingResults);
            trackOperator.computeVelocities(trackPreProcessingResults);
        }
    }

    /**
     *
     * @param singleCellPreProcessingResults
     */
    private void computeDeltaMovements(TrackDataHolder trackPreProcessingResults) {
        trackOperator.computeDeltaMovements(trackPreProcessingResults);
    }

    /**
     *
     * @param singleCellPreProcessingResults
     * @return
     */
    private int computeTotalTrackPointsNumber(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        int trackPointsNumber = 0;
        for (TrackDataHolder trackPreProcessingResults : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackPointsNumber += trackPreProcessingResults.getTrack().getTrackPointList().size();
        }
        return trackPointsNumber;
    }
}
