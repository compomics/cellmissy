/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl;

import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.analysis.factory.KernelDensityEstimatorFactory;
import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.SingleCellWellPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackPreProcessor;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An implementation for the single cell well preprocessor. Uses an operator one
 * level down (TrackPreProcessor).
 *
 * @author Paola
 */
@Component("singleCellWellPreProcessor")
public class SingleCellWellPreProcessorImpl implements SingleCellWellPreProcessor {

    @Autowired
    private TrackPreProcessor trackPreProcessor;

    @Override
    public void generateTrackDataHolders(SingleCellWellDataHolder singleCellWellDataHolder) {
        List<TrackDataHolder> trackDataHolders = new ArrayList<>();
        for (WellHasImagingType wellHasImagingType : singleCellWellDataHolder.getWell().getWellHasImagingTypeList()) {
            List<Track> trackList = wellHasImagingType.getTrackList();
            for (Track track : trackList) {
                StepCentricDataHolder stepCentricDataHolder = new StepCentricDataHolder(track);
                CellCentricDataHolder cellCentricDataHolder = new CellCentricDataHolder();
                // generate a new track data holder for this track and add it to the list
                trackDataHolders.add(new TrackDataHolder(track, stepCentricDataHolder,
                          cellCentricDataHolder));
            }
        }
        singleCellWellDataHolder.setTrackDataHolders(trackDataHolders);
    }

    @Override
    public void generateDataStructure(SingleCellWellDataHolder singleCellWellDataHolder) {
        int trackPointsNumber = computeTotalTrackPointsNumber(singleCellWellDataHolder);
        Object[][] dataStructure = new Object[trackPointsNumber][3];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellWellDataHolder.getTrackDataHolders()) {
            for (TrackPoint trackPoint : trackDataHolder.getTrack().getTrackPointList()) {
                dataStructure[counter][0] = singleCellWellDataHolder.getWell().toString();
                dataStructure[counter][1] = trackPoint.getTrack().getTrackNumber();
                dataStructure[counter][2] = trackPoint.getTimeIndex();
                counter++;
            }
        }
        singleCellWellDataHolder.setDataStructure(dataStructure);
    }

    @Override
    public void preProcessStepsAndCells(SingleCellWellDataHolder singleCellWellDataHolder, double conversionFactor, double timeLapse) {
        for (TrackDataHolder trackDataHolder : singleCellWellDataHolder.getTrackDataHolders()) {
            trackPreProcessor.preProcessSteps(trackDataHolder, conversionFactor);
            trackPreProcessor.preProcessCells(trackDataHolder, timeLapse);
        }
    }

    @Override
    public void generateRawTrackCoordinatesMatrix(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[][] rawTrackCoordinatesMatrix = new Double[singleCellWellDataHolder.getDataStructure().length][2];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellWellDataHolder.getTrackDataHolders()) {
            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
            Double[][] trackCoordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
            for (Double[] aTrackCoordinatesMatrix : trackCoordinatesMatrix) {
                rawTrackCoordinatesMatrix[counter] = aTrackCoordinatesMatrix;
                counter++;
            }
        }
        singleCellWellDataHolder.setRawTrackCoordinatesMatrix(rawTrackCoordinatesMatrix);
    }

    @Override
    public void generateShiftedTrackCoordinatesMatrix(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[][] shiftedTrackCoordinatesMatrix = new Double[singleCellWellDataHolder.getDataStructure().length][2];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellWellDataHolder.getTrackDataHolders()) {
            Double[][] shiftedTrackCoordinates = trackDataHolder.getStepCentricDataHolder()
                      .getShiftedCoordinatesMatrix();
            for (Double[] shiftedTrackCoordinate : shiftedTrackCoordinates) {
                shiftedTrackCoordinatesMatrix[counter] = shiftedTrackCoordinate;
                counter++;
            }
        }
        singleCellWellDataHolder.setShiftedTrackCoordinatesMatrix(shiftedTrackCoordinatesMatrix);
    }

    @Override
    public void generateRawCoordinatesRanges(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(singleCellWellDataHolder.getRawTrackCoordinatesMatrix());
        // compute the min and the max coordinates
        Double xMin = Collections.min(Arrays.asList(transposedMatrix[0]));
        Double xMax = Collections.max(Arrays.asList(transposedMatrix[0]));
        Double yMin = Collections.min(Arrays.asList(transposedMatrix[1]));
        Double yMax = Collections.max(Arrays.asList(transposedMatrix[1]));
        Double[][] rawCoordinatesRanges = new Double[2][2];
        rawCoordinatesRanges[0] = new Double[]{xMin, xMax};
        rawCoordinatesRanges[1] = new Double[]{yMin, yMax};
        singleCellWellDataHolder.setRawCoordinatesRanges(rawCoordinatesRanges);
    }

    @Override
    public void generateShiftedCoordinatesRanges(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(singleCellWellDataHolder.getShiftedTrackCoordinatesMatrix());
        // compute the min and the max coordinates
        Double xMin = Collections.min(Arrays.asList(transposedMatrix[0]));
        Double xMax = Collections.max(Arrays.asList(transposedMatrix[0]));
        Double yMin = Collections.min(Arrays.asList(transposedMatrix[1]));
        Double yMax = Collections.max(Arrays.asList(transposedMatrix[1]));
        Double[][] shiftedCoordinatesRanges = new Double[2][2];
        shiftedCoordinatesRanges[0] = new Double[]{xMin, xMax};
        shiftedCoordinatesRanges[1] = new Double[]{yMin, yMax};
        singleCellWellDataHolder.setShiftedCoordinatesRanges(shiftedCoordinatesRanges);
    }

   

    /**
     * Calculate the total number of track points.
     *
     * @param singleCellConditionDataHolder
     * @return an integer
     */
    private int computeTotalTrackPointsNumber(SingleCellWellDataHolder singleCellWellDataHolder) {
        int trackPointsNumber = 0;
        for (TrackDataHolder trackDataHolder : singleCellWellDataHolder.getTrackDataHolders()) {
            trackPointsNumber += trackDataHolder.getTrack().getTrackPointList().size();
        }
        return trackPointsNumber;
    }

}
