/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackPreProcessor;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
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
    private TrackPreProcessor trackPreProcessor;

    @Override
    public void generateTrackDataHolders(SingleCellConditionDataHolder singleCellConditionDataHolder,
                                         PlateCondition plateCondition) {
        List<TrackDataHolder> trackDataHolders = new ArrayList<>();
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        for (Well singleCellAnalyzedWell : singleCellAnalyzedWells) {
            List<WellHasImagingType> wellHasImagingTypeList = singleCellAnalyzedWell.getWellHasImagingTypeList();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                List<Track> trackList = wellHasImagingType.getTrackList();
                for (Track track : trackList) {
                    StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder(track);
                    CellCentricDataHolder cellCentricDataHolder = new CellCentricDataHolder();
                    // generate a new track data holder for this track and add it to the list
                    TrackDataHolder trackDataHolder = new TrackDataHolder(track, stepCentricDataHolder,
                            cellCentricDataHolder);
                    trackDataHolders.add(trackDataHolder);
                }
            }
        }
        singleCellConditionDataHolder.setTrackDataHolders(trackDataHolders);
    }

    @Override
    public void generateDataStructure(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        // for a single condition, compute first the total number of track points
        int trackPointsNumber = computeTotalTrackPointsNumber(singleCellConditionDataHolder);
        Object[][] dataStructure = new Object[trackPointsNumber][3];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            List<TrackPoint> trackPointList = trackDataHolder.getTrack().getTrackPointList();
            for (TrackPoint trackPoint : trackPointList) {
                dataStructure[counter][0] = trackDataHolder.getTrack().getWellHasImagingType().getWell().toString();
                dataStructure[counter][1] = trackPoint.getTrack().getTrackNumber();
                dataStructure[counter][2] = trackPoint.getTimeIndex();
                counter++;
            }
        }
        singleCellConditionDataHolder.setDataStructure(dataStructure);
    }

    @Override
    public void preProcessStepsAndCells(SingleCellConditionDataHolder singleCellConditionDataHolder, double
            conversionFactor, double timeLapse) {
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            trackPreProcessor.preProcessSteps(trackDataHolder, conversionFactor);
            trackPreProcessor.preProcessCells(trackDataHolder, timeLapse);
        }
    }

    @Override
    public void generateRawTrackCoordinatesMatrix(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        Object[][] dataStructure = singleCellConditionDataHolder.getDataStructure();
        Double[][] rawTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
            Double[][] trackCoordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
            for (Double[] aTrackCoordinatesMatrix : trackCoordinatesMatrix) {
                rawTrackCoordinatesMatrix[counter] = aTrackCoordinatesMatrix;
                counter++;
            }
        }
        singleCellConditionDataHolder.setRawTrackCoordinatesMatrix(rawTrackCoordinatesMatrix);
    }

    @Override
    public void generateShiftedTrackCoordinatesMatrix(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        Object[][] dataStructure = singleCellConditionDataHolder.getDataStructure();
        Double[][] shiftedTrackCoordinatesMatrix = new Double[dataStructure.length][2];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            Double[][] shiftedTrackCoordinates = trackDataHolder.getStepCentricDataHolder()
                    .getShiftedCoordinatesMatrix();
            for (Double[] shiftedTrackCoordinate : shiftedTrackCoordinates) {
                shiftedTrackCoordinatesMatrix[counter] = shiftedTrackCoordinate;
                counter++;
            }
        }
        singleCellConditionDataHolder.setShiftedTrackCoordinatesMatrix(shiftedTrackCoordinatesMatrix);
    }

    @Override
    public void generateRawCoordinatesRanges(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        // get the raw tracks coordinates
        Double[][] rawTrackCoordinatesMatrix = singleCellConditionDataHolder.getRawTrackCoordinatesMatrix();
        // get the x and the y values, first needs to be transposed
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(rawTrackCoordinatesMatrix);
        // compute the min and the max coordinates
        Double xMin = Collections.min(Arrays.asList(transposedMatrix[0]));
        Double xMax = Collections.max(Arrays.asList(transposedMatrix[0]));
        Double yMin = Collections.min(Arrays.asList(transposedMatrix[1]));
        Double yMax = Collections.max(Arrays.asList(transposedMatrix[1]));
        Double[][] rawCoordinatesRanges = new Double[2][2];
        rawCoordinatesRanges[0] = new Double[]{xMin, xMax};
        rawCoordinatesRanges[1] = new Double[]{yMin, yMax};
        singleCellConditionDataHolder.setRawCoordinatesRanges(rawCoordinatesRanges);
    }

    @Override
    public void generateShiftedCoordinatesRanges(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        // get the raw tracks coordinates
        Double[][] shiftedTrackCoordinatesMatrix = singleCellConditionDataHolder.getShiftedTrackCoordinatesMatrix();
        // get the x and the y values, first needs to be transposed
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(shiftedTrackCoordinatesMatrix);
        // compute the min and the max coordinates
        Double xMin = Collections.min(Arrays.asList(transposedMatrix[0]));
        Double xMax = Collections.max(Arrays.asList(transposedMatrix[0]));
        Double yMin = Collections.min(Arrays.asList(transposedMatrix[1]));
        Double yMax = Collections.max(Arrays.asList(transposedMatrix[1]));
        Double[][] shiftedCoordinatesRanges = new Double[2][2];
        shiftedCoordinatesRanges[0] = new Double[]{xMin, xMax};
        shiftedCoordinatesRanges[1] = new Double[]{yMin, yMax};
        singleCellConditionDataHolder.setShiftedCoordinatesRanges(shiftedCoordinatesRanges);
    }

    /**
     * Calculate the total number of track points.
     *
     * @param singleCellConditionDataHolder
     * @return an integer
     */
    private int computeTotalTrackPointsNumber(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        int trackPointsNumber = 0;
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            trackPointsNumber += trackDataHolder.getTrack().getTrackPointList().size();
        }
        return trackPointsNumber;
    }
}
