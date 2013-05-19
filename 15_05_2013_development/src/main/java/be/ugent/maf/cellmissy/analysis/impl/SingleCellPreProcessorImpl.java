/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("singleCellPreProcessor")
public class SingleCellPreProcessorImpl implements SingleCellPreProcessor {

    @Override
    public void generateFixedDataStructure(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition) {
        Double[][] normalizedTrackCoordinates = singleCellPreProcessingResults.getNormalizedTrackCoordinates();
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        Object[][] fixedDataStructure = new Object[normalizedTrackCoordinates.length][3];
        int counter = 0;
        for (int i = 0; i < singleCellAnalyzedWells.size(); i++) {
            Well well = singleCellAnalyzedWells.get(i);
            List<WellHasImagingType> wellHasImagingTypeList = well.getWellHasImagingTypeList();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                for (Track track : wellHasImagingType.getTrackList()) {
                    for (TrackPoint trackPoint : track.getTrackPointList()) {
                        fixedDataStructure[counter][0] = well.toString();
                        fixedDataStructure[counter][1] = trackPoint.getTrack().getTrackNumber();
                        fixedDataStructure[counter][2] = trackPoint.getTimeIndex();
                        counter++;
                    }
                }
            }
        }
        singleCellPreProcessingResults.setFixedDataStructure(fixedDataStructure);
    }

    @Override
    public void computeDirectionalMovements(SingleCellPreProcessingResults singleCellPreProcessingResults, PlateCondition plateCondition) {
        Double[][] normalizedTrackCoordinates = singleCellPreProcessingResults.getNormalizedTrackCoordinates();
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        Double[][] directionalMovements = new Double[normalizedTrackCoordinates.length][2];
        for (int i = 0; i < singleCellAnalyzedWells.size(); i++) {
            Well well = singleCellAnalyzedWells.get(i);
            List<WellHasImagingType> wellHasImagingTypeList = well.getWellHasImagingTypeList();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                for (Track track : wellHasImagingType.getTrackList()) {
                    double[][] trackPointMatrix = track.getTrackPointMatrix();
                    for (int j = 1; j < trackPointMatrix.length - 1; j++) {
                        double current = trackPointMatrix[j][0];
                        double previous = trackPointMatrix[j - 1][0];
                        directionalMovements[i] = new Double[]{current, previous};
                    }
                }
            }
        }
        singleCellPreProcessingResults.setDirectionalMovements(directionalMovements);
    }
}
