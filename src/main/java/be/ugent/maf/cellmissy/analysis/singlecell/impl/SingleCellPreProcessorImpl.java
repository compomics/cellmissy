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
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public void generateTrackDataHolders(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition, double conversionFactor, Double timeLapse) {
        List<TrackDataHolder> trackDataHolders = new ArrayList<>();
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        for (Well singleCellAnalyzedWell : singleCellAnalyzedWells) {
            List<WellHasImagingType> wellHasImagingTypeList = singleCellAnalyzedWell.getWellHasImagingTypeList();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                List<Track> trackList = wellHasImagingType.getTrackList();
                for (Track track : trackList) {
                    StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder(track, conversionFactor, timeLapse);
                    CellCentricDataHolder cellCentricDataHolder = new CellCentricDataHolder();
                    // generate a new track data holder for this track and add it to the list
                    TrackDataHolder trackDataHolder = new TrackDataHolder(track, stepCentricDataHolder, cellCentricDataHolder);
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
    public void operateOnStepsAndCells(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            trackOperator.operateOnSteps(trackDataHolder);
            trackOperator.operateOnCells(trackDataHolder);
        }
    }

    @Override
    public void generateRawTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[][] rawTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
            Double[][] trackCoordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
            for (int row = 0; row < trackCoordinatesMatrix.length; row++) {
                rawTrackCoordinatesMatrix[counter] = trackCoordinatesMatrix[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setRawTrackCoordinatesMatrix(rawTrackCoordinatesMatrix);
    }

    @Override
    public void generateShiftedTrackCoordinatesMatrix(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[][] shiftedTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[][] shiftedTrackCoordinates = trackDataHolder.getStepCentricDataHolder().getShiftedCooordinatesMatrix();
            for (int row = 0; row < shiftedTrackCoordinates.length; row++) {
                shiftedTrackCoordinatesMatrix[counter] = shiftedTrackCoordinates[row];
                counter++;
            }
        }
        singleCellPreProcessingResults.setShiftedTrackCoordinatesMatrix(shiftedTrackCoordinatesMatrix);
    }

    @Override
    public void generateRawCoordinatesRanges(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        // get the raw tracks coordinates
        Double[][] rawTrackCoordinatesMatrix = singleCellPreProcessingResults.getRawTrackCoordinatesMatrix();
        // get the x and the y values, first needs to be transposed
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(rawTrackCoordinatesMatrix);
        Double[] xCoord = transposedMatrix[0];
        Double[] yCoord = transposedMatrix[1];
        // compute the min and the max coordinates
        Double xMin = Collections.min(Arrays.asList(xCoord));
        Double xMax = Collections.max(Arrays.asList(xCoord));
        Double ymin = Collections.min(Arrays.asList(yCoord));
        Double yMax = Collections.max(Arrays.asList(yCoord));
        Double[][] rawCoordinatesRanges = new Double[2][2];
        rawCoordinatesRanges[0] = new Double[]{xMin, xMax};
        rawCoordinatesRanges[1] = new Double[]{ymin, yMax};
        singleCellPreProcessingResults.setRawCoordinatesRanges(rawCoordinatesRanges);
    }

    @Override
    public void generateShiftedCoordinatesRanges(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        // get the raw tracks coordinates
        Double[][] shiftedTrackCoordinatesMatrix = singleCellPreProcessingResults.getShiftedTrackCoordinatesMatrix();
        // get the x and the y values, first needs to be transposed
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(shiftedTrackCoordinatesMatrix);
        Double[] xCoord = transposedMatrix[0];
        Double[] yCoord = transposedMatrix[1];
        // compute the min and the max coordinates
        Double xMin = Collections.min(Arrays.asList(xCoord));
        Double xMax = Collections.max(Arrays.asList(xCoord));
        Double ymin = Collections.min(Arrays.asList(yCoord));
        Double yMax = Collections.max(Arrays.asList(yCoord));
        Double[][] shiftedCoordinatesRanges = new Double[2][2];
        shiftedCoordinatesRanges[0] = new Double[]{xMin, xMax};
        shiftedCoordinatesRanges[1] = new Double[]{ymin, yMax};
        singleCellPreProcessingResults.setShiftedCoordinatesRanges(shiftedCoordinatesRanges);
    }

    @Override
    public void generateInstantaneousDisplacementsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[] instantaneousDisplacementsVector = new Double[dataStructure.length];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[] instantaneousDisplacements = trackDataHolder.getStepCentricDataHolder().getInstantaneousDisplacements();
            for (int i = 0; i < instantaneousDisplacements.length; i++) {
                instantaneousDisplacementsVector[counter] = instantaneousDisplacements[i];
                counter++;
            }
        }
        singleCellPreProcessingResults.setInstantaneousDisplacementsVector(instantaneousDisplacementsVector);
    }

    @Override
    public void generateDirectionalityRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[] directionalityRatiosVector = new Double[dataStructure.length];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[] directionalityRatios = trackDataHolder.getStepCentricDataHolder().getDirectionalityRatios();
            for (int i = 0; i < directionalityRatios.length; i++) {
                directionalityRatiosVector[counter] = directionalityRatios[i];
                counter++;
            }
        }
        singleCellPreProcessingResults.setDirectionalityRatiosVector(directionalityRatiosVector);
    }

    @Override
    public void generateMedianDirectionalityRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] medianDirectionalityRatiosVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < medianDirectionalityRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackAngle = trackDataHolder.getCellCentricDataHolder().getMedianDirectionalityRatio();
            medianDirectionalityRatiosVector[i] = trackAngle;
        }
        singleCellPreProcessingResults.setMedianDirectionalityRatiosVector(medianDirectionalityRatiosVector);
    }

    @Override
    public void generateTrackDisplacementsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] trackDisplacementsVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackMeanDisplacement = trackDataHolder.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        singleCellPreProcessingResults.setTrackDisplacementsVector(trackDisplacementsVector);
    }

    @Override
    public void generateCumulativeDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] cumulativeDistancesVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < cumulativeDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double cumulativeDistance = trackDataHolder.getCellCentricDataHolder().getCumulativeDistance();
            cumulativeDistancesVector[i] = cumulativeDistance;
        }
        singleCellPreProcessingResults.setCumulativeDistancesVector(cumulativeDistancesVector);
    }

    @Override
    public void generateEuclideanDistancesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] euclideanDistancesVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < euclideanDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double euclideanDistance = trackDataHolder.getCellCentricDataHolder().getEuclideanDistance();
            euclideanDistancesVector[i] = euclideanDistance;
        }
        singleCellPreProcessingResults.setEuclideanDistancesVector(euclideanDistancesVector);
    }

    @Override
    public void generateTrackSpeedsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] trackDisplacementsVector = singleCellPreProcessingResults.getTrackDisplacementsVector();
        Double[] trackSpeedsVector = new Double[trackDisplacementsVector.length];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackMeanSpeed = trackDataHolder.getCellCentricDataHolder().getMedianSpeed();
            trackSpeedsVector[i] = trackMeanSpeed;
        }
        singleCellPreProcessingResults.setTrackSpeedsVector(trackSpeedsVector);
    }

    @Override
    public void generateEndPointDirectionalityRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] endPointDirectionalityRatios = new Double[trackDataHolders.size()];
        for (int i = 0; i < endPointDirectionalityRatios.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double endPointDirectionalityRatio = trackDataHolder.getCellCentricDataHolder().getEndPointDirectionalityRatio();
            endPointDirectionalityRatios[i] = endPointDirectionalityRatio;
        }
        singleCellPreProcessingResults.setEndPointDirectionalityRatios(endPointDirectionalityRatios);
    }

    @Override
    public void generateConvexHullsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        ConvexHull[] convexHullsVector = new ConvexHull[trackDataHolders.size()];
        for (int i = 0; i < convexHullsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            ConvexHull convexHull = trackDataHolder.getCellCentricDataHolder().getConvexHull();
            convexHullsVector[i] = convexHull;
        }
        singleCellPreProcessingResults.setConvexHullsVector(convexHullsVector);
    }

    @Override
    public void generateDisplacementRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] displacementRatiosVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < displacementRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double displacementRatio = trackDataHolder.getCellCentricDataHolder().getDisplacementRatio();
            displacementRatiosVector[i] = displacementRatio;
        }
        singleCellPreProcessingResults.setDisplacementRatiosVector(displacementRatiosVector);
    }

    @Override
    public void generateOutreachRatiosVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] outreachRatiosVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < outreachRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double outreachRatio = trackDataHolder.getCellCentricDataHolder().getOutreachRatio();
            outreachRatiosVector[i] = outreachRatio;
        }
        singleCellPreProcessingResults.setOutreachRatiosVector(outreachRatiosVector);
    }

    @Override
    public void generateTurningAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
        Double[] turningAnglesVector = new Double[dataStructure.length];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            Double[] turningAngles = trackDataHolder.getStepCentricDataHolder().getTurningAngles();
            for (Double turningAngle : turningAngles) {
                turningAnglesVector[counter] = turningAngle;
                counter++;
            }
        }
        singleCellPreProcessingResults.setTurningAnglesVector(turningAnglesVector);
    }

    @Override
    public void generateMedianTurningAnglesVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] medianTurningAnglesVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < medianTurningAnglesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double medianTurningAngle = trackDataHolder.getCellCentricDataHolder().getMedianTurningAngle();
            medianTurningAnglesVector[i] = medianTurningAngle;
        }
        singleCellPreProcessingResults.setMedianTurningAnglesVector(medianTurningAnglesVector);
    }

    @Override
    public void generateMedianDirectionAutocorrelationsVector(SingleCellPreProcessingResults singleCellPreProcessingResults) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        Double[] medianDirectionAutocorrelationsVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < medianDirectionAutocorrelationsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double medianDirectionAutocorrelation = trackDataHolder.getCellCentricDataHolder().getMedianDirectionAutocorrelation();
            medianDirectionAutocorrelationsVector[i] = medianDirectionAutocorrelation;
        }
        singleCellPreProcessingResults.setMedianDirectionAutocorrelationsVector(medianDirectionAutocorrelationsVector);
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

}
