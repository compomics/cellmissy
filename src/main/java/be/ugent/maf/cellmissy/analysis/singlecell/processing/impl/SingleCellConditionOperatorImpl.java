/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellConditionOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellWellOperator;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An implementation for the single cell condition operator.
 *
 * @author Paola
 */
@Component("singleCellConditionOperator")
public class SingleCellConditionOperatorImpl implements SingleCellConditionOperator {

    @Autowired
    private SingleCellWellOperator singleCellWellOperator;

    @Override
    public void operateOnStepsAndCells(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.operateOnStepsAndCells(singleCellWellDataHolder);
        }
    }

    @Override
    public void interpolateTracks(SingleCellConditionDataHolder singleCellConditionDataHolder, int interpolationPoints, String interpolatorBeanName) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.interpolateTracks(singleCellWellDataHolder, interpolationPoints, interpolatorBeanName);
        }
    }

    @Override
    public void generateInstantaneousDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateInstantaneousDisplacementsVector(singleCellWellDataHolder);
        }
        Double[] instantaneousDisplacementsVector = new Double[singleCellConditionDataHolder.getDataStructure().length];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            Double[] instantaneousDisplacements = trackDataHolder.getStepCentricDataHolder()
                      .getInstantaneousDisplacements();
            for (Double instantaneousDisplacement : instantaneousDisplacements) {
                instantaneousDisplacementsVector[counter] = instantaneousDisplacement;
                counter++;
            }
        }
        singleCellConditionDataHolder.setInstantaneousDisplacementsVector(instantaneousDisplacementsVector);
    }

    @Override
    public void generateDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateDirectionalityRatiosVector(singleCellWellDataHolder);
        }
        Double[] directionalityRatiosVector = new Double[singleCellConditionDataHolder.getDataStructure().length];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            Double[] directionalityRatios = trackDataHolder.getStepCentricDataHolder().getDirectionalityRatios();
            for (Double directionalityRatio : directionalityRatios) {
                directionalityRatiosVector[counter] = directionalityRatio;
                counter++;
            }
        }
        singleCellConditionDataHolder.setDirectionalityRatiosVector(directionalityRatiosVector);
    }

    @Override
    public void generateMedianDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateMedianDirectionalityRatiosVector(singleCellWellDataHolder);
        }
        Double[] medianDirectionalityRatiosVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < medianDirectionalityRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double trackAngle = trackDataHolder.getCellCentricDataHolder().getMedianDirectionalityRatio();
            medianDirectionalityRatiosVector[i] = trackAngle;
        }
        singleCellConditionDataHolder.setMedianDirectionalityRatiosVector(medianDirectionalityRatiosVector);
    }

    @Override
    public void generateTrackDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateTrackDisplacementsVector(singleCellWellDataHolder);
        }
        Double[] trackDisplacementsVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double trackMeanDisplacement = trackDataHolder.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        singleCellConditionDataHolder.setTrackDisplacementsVector(trackDisplacementsVector);
    }

    @Override
    public void generateCumulativeDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateCumulativeDistancesVector(singleCellWellDataHolder);
        }
        Double[] cumulativeDistancesVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < cumulativeDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double cumulativeDistance = trackDataHolder.getCellCentricDataHolder().getCumulativeDistance();
            cumulativeDistancesVector[i] = cumulativeDistance;
        }
        singleCellConditionDataHolder.setCumulativeDistancesVector(cumulativeDistancesVector);
    }

    @Override
    public void generateEuclideanDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateEuclideanDistancesVector(singleCellWellDataHolder);
        }
        Double[] euclideanDistancesVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < euclideanDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double euclideanDistance = trackDataHolder.getCellCentricDataHolder().getEuclideanDistance();
            euclideanDistancesVector[i] = euclideanDistance;
        }
        singleCellConditionDataHolder.setEuclideanDistancesVector(euclideanDistancesVector);
    }

    @Override
    public void generateTrackSpeedsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateTrackSpeedsVector(singleCellWellDataHolder);
        }
        Double[] trackSpeedsVector = new Double[singleCellConditionDataHolder.getTrackDisplacementsVector().length];
        for (int i = 0; i < singleCellConditionDataHolder.getTrackDisplacementsVector().length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double trackMeanSpeed = trackDataHolder.getCellCentricDataHolder().getMedianSpeed();
            trackSpeedsVector[i] = trackMeanSpeed;
        }
        singleCellConditionDataHolder.setTrackSpeedsVector(trackSpeedsVector);
    }

    @Override
    public void generateEndPointDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateEndPointDirectionalityRatiosVector(singleCellWellDataHolder);
        }
        Double[] endPointDirectionalityRatios = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < endPointDirectionalityRatios.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double endPointDirectionalityRatio = trackDataHolder.getCellCentricDataHolder()
                      .getEndPointDirectionalityRatio();
            endPointDirectionalityRatios[i] = endPointDirectionalityRatio;
        }
        singleCellConditionDataHolder.setEndPointDirectionalityRatios(endPointDirectionalityRatios);
    }

    @Override
    public void generateConvexHullsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateConvexHullsVector(singleCellWellDataHolder);
        }
        ConvexHull[] convexHullsVector = new ConvexHull[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < convexHullsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            ConvexHull convexHull = trackDataHolder.getCellCentricDataHolder().getConvexHull();
            convexHullsVector[i] = convexHull;
        }
        singleCellConditionDataHolder.setConvexHullsVector(convexHullsVector);
    }

    @Override
    public void generateDisplacementRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateDisplacementRatiosVector(singleCellWellDataHolder);
        }
        Double[] displacementRatiosVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < displacementRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double displacementRatio = trackDataHolder.getCellCentricDataHolder().getDisplacementRatio();
            displacementRatiosVector[i] = displacementRatio;
        }
        singleCellConditionDataHolder.setDisplacementRatiosVector(displacementRatiosVector);
    }

    @Override
    public void generateOutreachRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateOutreachRatiosVector(singleCellWellDataHolder);
        }
        Double[] outreachRatiosVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < outreachRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double outreachRatio = trackDataHolder.getCellCentricDataHolder().getOutreachRatio();
            outreachRatiosVector[i] = outreachRatio;
        }
        singleCellConditionDataHolder.setOutreachRatiosVector(outreachRatiosVector);
    }

    @Override
    public void generateTurningAnglesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateTurningAnglesVector(singleCellWellDataHolder);
        }
        Double[] turningAnglesVector = new Double[singleCellConditionDataHolder.getDataStructure().length];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            Double[] turningAngles = trackDataHolder.getStepCentricDataHolder().getTurningAngles();
            for (Double turningAngle : turningAngles) {
                turningAnglesVector[counter] = turningAngle;
                counter++;
            }
        }
        singleCellConditionDataHolder.setTurningAnglesVector(turningAnglesVector);
    }

    @Override
    public void generateMedianTurningAnglesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateMedianTurningAnglesVector(singleCellWellDataHolder);
        }
        Double[] medianTurningAnglesVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < medianTurningAnglesVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double medianTurningAngle = trackDataHolder.getCellCentricDataHolder().getMedianTurningAngle();
            medianTurningAnglesVector[i] = medianTurningAngle;
        }
        singleCellConditionDataHolder.setMedianTurningAnglesVector(medianTurningAnglesVector);
    }

    @Override
    public void generateMedianDirectionAutocorrelationsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            singleCellWellOperator.generateMedianDirectionAutocorrelationsVector(singleCellWellDataHolder);
        }
        Double[] medianDirectionAutocorrelationsVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < medianDirectionAutocorrelationsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double medianDirectionAutocorrelation = trackDataHolder.getCellCentricDataHolder()
                      .getMedianDirectionAutocorrelation();
            medianDirectionAutocorrelationsVector[i] = medianDirectionAutocorrelation;
        }
        singleCellConditionDataHolder.setMedianDirectionAutocorrelationsVector(medianDirectionAutocorrelationsVector);
    }
}
