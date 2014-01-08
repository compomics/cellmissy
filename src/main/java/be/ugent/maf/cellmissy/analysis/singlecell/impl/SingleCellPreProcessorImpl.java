/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.TrackOperator;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.MostDistantPointsPair;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of the single cell pre processor. It makes use of a track
 * operator to perform basic operations. This class will also bring together the
 * quantitative parameters of tracks on a plate condition level.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("singleCellPreProcessor")
public class SingleCellPreProcessorImpl implements SingleCellPreProcessor {

    @Autowired
    private TrackOperator trackOperator;

    @Override
    public void generateTrackDataHolders(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition) {
        List<TrackDataHolder> trackDataHolders = new ArrayList<>();
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        for (Well singleCellAnalyzedWell : singleCellAnalyzedWells) {
            List<WellHasImagingType> wellHasImagingTypeList = singleCellAnalyzedWell.getWellHasImagingTypeList();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                List<Track> trackList = wellHasImagingType.getTrackList();
                for (Track track : trackList) {
                    // generate a new track data holder for this track and add it to the list
                    TrackDataHolder trackDataHolder = new TrackDataHolder(track);
                    trackDataHolders.add(trackDataHolder);
                }
            }
        }
        singleCellPreProcessingResults.setTrackDataHolders(trackDataHolders);
    }

    @Override
    public void generateDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        // for a single condition, compute first the total number of track points
        int trackPointsNumber = computeTotalTrackPointsNumber(singleCellPreProcessingResults);
        Object[][] dataStructure = new Object[trackPointsNumber][3];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            List<TrackPoint> trackPointList = trackDataHolder.getTrack().getTrackPointList();
            for (TrackPoint trackPoint : trackPointList) {
                dataStructure[counter][0] = trackDataHolder.getTrack().getWellHasImagingType().getWell().toString();
                dataStructure[counter][1] = trackPoint.getTrack().getTrackNumber();
                dataStructure[counter][2] = trackPoint.getTimeIndex();
                counter++;
            }
        }
        singleCellPreProcessingResults.setDataStructure(dataStructure);
    }

    @Override
    public void generateTimeIndexes(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.generateTimeIndexes(trackDataHolder);
        }
    }

    @Override
    public void generateTrackDurations(Double timeLapse, SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeDuration(timeLapse, trackDataHolder);
        }
    }

    @Override
    public void generateRawTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults, double conversionFactor) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[][] rawTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        generateTrackCoordinatesMatrix(singleCellPreProcessingResults, conversionFactor);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[][] trackCoordinatesMatrix = trackDataHolder.getCoordinatesMatrix();
            for (int row = 0; row < trackCoordinatesMatrix.length; row++) {
                rawTrackCoordinatesMatrix[counter] = trackCoordinatesMatrix[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setRawTrackCoordinatesMatrix(rawTrackCoordinatesMatrix);
    }

    @Override
    public void computeCoordinatesRanges(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeCoordinatesRange(trackDataHolder);
        }
    }

    @Override
    public void generateShiftedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[][] normalizedTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        computeShiftedTrackCoordinates(singleCellPreProcessingResults);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[][] normalizedTrackCoordinates = trackDataHolder.getShiftedCooordinatesMatrix();
            for (int row = 0; row < normalizedTrackCoordinates.length; row++) {
                normalizedTrackCoordinatesMatrix[counter] = normalizedTrackCoordinates[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setShiftedTrackCoordinatesMatrix(normalizedTrackCoordinatesMatrix);
    }

    @Override
    public void generateInstantaneousDisplacementsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[] instantaneousDisplacementsVector = new Double[dataStructure.length];
        computeInstantaneousDisplacements(singleCellPreProcessingResults);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[] instantaneousDisplacements = trackDataHolder.getInstantaneousDisplacements();
            for (int i = 0; i < instantaneousDisplacements.length; i++) {
                instantaneousDisplacementsVector[counter] = instantaneousDisplacements[i];
                counter++;
            }
        }
        singleCellPreProcessingResults.setInstantaneousDisplacementsVector(instantaneousDisplacementsVector);
    }

    @Override
    public void generateTrackDisplacementsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] trackDisplacementsVector = new Double[trackDataHolders.size()];
        computeTrackMeanDisplacement(singleCellPreProcessingResults);
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackMeanDisplacement = trackDataHolder.getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        singleCellPreProcessingResults.setTrackDisplacementsVector(trackDisplacementsVector);
    }

    @Override
    public void generateCumulativeDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] cumulativeDistancesVector = new Double[trackDataHolders.size()];
        computeCumulativeDistances(singleCellPreProcessingResults);
        for (int i = 0; i < cumulativeDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double cumulativeDistance = trackDataHolder.getCumulativeDistance();
            cumulativeDistancesVector[i] = cumulativeDistance;
        }
        singleCellPreProcessingResults.setCumulativeDistancesVector(cumulativeDistancesVector);
    }

    @Override
    public void generateEuclideanDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] euclideanDistancesVector = new Double[trackDataHolders.size()];
        computeEuclideanDistances(singleCellPreProcessingResults);
        for (int i = 0; i < euclideanDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double euclideanDistance = trackDataHolder.getEuclideanDistance();
            euclideanDistancesVector[i] = euclideanDistance;
        }
        singleCellPreProcessingResults.setEuclideanDistancesVector(euclideanDistancesVector);
    }

    @Override
    public void generateTrackSpeedsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] trackDisplacementsVector = singleCellPreProcessingResults.getTrackDisplacementsVector();
        computeTrackMeanSpeed(singleCellPreProcessingResults);
        Double[] trackSpeedsVector = new Double[trackDisplacementsVector.length];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackMeanSpeed = trackDataHolder.getMedianSpeed();
            trackSpeedsVector[i] = trackMeanSpeed;
        }
        singleCellPreProcessingResults.setTrackSpeedsVector(trackSpeedsVector);
    }

    @Override
    public void generateDirectionalitiesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] directionalitiesVector = new Double[trackDataHolders.size()];
        computeDirectionalities(singleCellPreProcessingResults);
        for (int i = 0; i < directionalitiesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double directionality = trackDataHolder.getDirectionality();
            directionalitiesVector[i] = directionality;
        }
        singleCellPreProcessingResults.setDirectionalitiesVector(directionalitiesVector);
    }

    @Override
    public void generateFarthestPointsPairsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        GeometricPoint[][] farthestPointsPairsVector = new GeometricPoint[trackDataHolders.size()][2];
        computeFarthestPointsPairs(singleCellPreProcessingResults);
        for (int i = 0; i < farthestPointsPairsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            ConvexHull convexHull = trackDataHolder.getConvexHull();
            MostDistantPointsPair mostDistantPointsPair = convexHull.getMostDistantPointsPair();
            GeometricPoint firstPoint = mostDistantPointsPair.getFirstPoint();
            GeometricPoint secondPoint = mostDistantPointsPair.getSecondPoint();
            farthestPointsPairsVector[i] = new GeometricPoint[]{firstPoint, secondPoint};
        }
        singleCellPreProcessingResults.setFarthestPointsPairsVector(farthestPointsPairsVector);
    }

    @Override
    public void generateDisplacementRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] displacementRatiosVector = new Double[trackDataHolders.size()];
        computeDisplacementRatios(singleCellPreProcessingResults);
        for (int i = 0; i < displacementRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double displacementRatio = trackDataHolder.getDisplacementRatio();
            displacementRatiosVector[i] = displacementRatio;
        }
        singleCellPreProcessingResults.setDisplacementRatiosVector(displacementRatiosVector);
    }

    @Override
    public void generateOutreachRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] outreachRatiosVector = new Double[trackDataHolders.size()];
        computeOutreachRatios(singleCellPreProcessingResults);
        for (int i = 0; i < outreachRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double outreachRatio = trackDataHolder.getOutreachRatio();
            outreachRatiosVector[i] = outreachRatio;
        }
        singleCellPreProcessingResults.setOutreachRatiosVector(outreachRatiosVector);
    }

    @Override
    public void generateTurningAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[] turningAnglesVector = new Double[dataStructure.length];
        computeTurningAngles(singleCellPreProcessingResults);
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[] turningAngles = trackDataHolder.getTurningAngles();
            for (int i = 0; i < turningAngles.length; i++) {
                turningAnglesVector[counter] = turningAngles[i];
                counter++;
            }
        }
        singleCellPreProcessingResults.setTurningAnglesVector(turningAnglesVector);
    }

    @Override
    public void generateMedianTurningAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] medianTurningAnglesVector = new Double[trackDataHolders.size()];
        computeMedianTurningAngles(singleCellPreProcessingResults);
        for (int i = 0; i < medianTurningAnglesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackAngle = trackDataHolder.getMedianTurningAngle();
            medianTurningAnglesVector[i] = trackAngle;
        }
        singleCellPreProcessingResults.setMedianTurningAnglesVector(medianTurningAnglesVector);
    }

    /**
     * Generate matrix with raw data.
     *
     * @param singleCellPreProcessingResults
     */
    private void generateTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults, double conversionFactor) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.generateCoordinatesMatrix(trackDataHolder, conversionFactor);
        }
    }

    /**
     * Compute shifted track coordinates.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeShiftedTrackCoordinates(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeShiftedCoordinatesMatrix(trackDataHolder);
        }
    }

    /**
     * Compute instantaneous displacements.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeInstantaneousDisplacements(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            computeDeltaMovements(trackDataHolder);
            trackOperator.computeInstantaneousDisplacements(trackDataHolder);
        }
    }

    /**
     * Compute delta movements in x and y direction for the specified
     * trackDataHolder.
     *
     * @param trackDataHolder
     */
    private void computeDeltaMovements(TrackDataHolder trackDataHolder) {
        trackOperator.computeDeltaMovements(trackDataHolder);
    }

    /**
     * Calculate the total number of track points.
     *
     * @param singleCellPreProcessingResults
     * @return an integer
     */
    private int computeTotalTrackPointsNumber(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        int trackPointsNumber = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackPointsNumber += trackDataHolder.getTrack().getTrackPointList().size();
        }
        return trackPointsNumber;
    }

    /**
     * Compute the median displacement for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeTrackMeanDisplacement(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeMedianDisplacement(trackDataHolder);
        }
    }

    /**
     * Compute the median track speed.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeTrackMeanSpeed(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeMedianSpeed(trackDataHolder);
        }
    }

    /**
     * Compute the cumulative distances for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeCumulativeDistances(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeCumulativeDistance(trackDataHolder);
        }
    }

    /**
     * Compute the Euclidean distances for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeEuclideanDistances(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeEuclideanDistance(trackDataHolder);
        }
    }

    /**
     * Compute the directionality for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeDirectionalities(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeDirectionality(trackDataHolder);
        }
    }

    /**
     * Compute the maximal displacement for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeFarthestPointsPairs(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeConvexHull(trackDataHolder);
        }
    }

    /**
     * Compute the displacement ratios for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeDisplacementRatios(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeDisplacementRatio(trackDataHolder);
        }
    }

    /**
     * Compute the outreach ratios for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeOutreachRatios(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeOutreachRatio(trackDataHolder);
        }
    }

    /**
     * Compute the turning angles for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeTurningAngles(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeTurningAngles(trackDataHolder);
        }
    }

    /**
     * Compute the track angle for each track data holder.
     *
     * @param singleCellPreProcessingResults
     */
    private void computeMedianTurningAngles(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.computeMedianTurningAngle(trackDataHolder);
        }
    }
}
