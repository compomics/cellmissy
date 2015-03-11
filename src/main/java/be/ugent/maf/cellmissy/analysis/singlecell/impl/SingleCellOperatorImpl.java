package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.TrackOperator;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * An implementation for the SingleCellOperator interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("singleCellOperator")
public class SingleCellOperatorImpl implements SingleCellOperator {

    @Autowired
    private TrackOperator trackOperator;

    @Override
    public void operateOnStepsAndCells(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (TrackDataHolder trackDataHolder : singleCellConditionDataHolder.getTrackDataHolders()) {
            trackOperator.operateOnSteps(trackDataHolder);
            trackOperator.operateOnCells(trackDataHolder);
        }
    }

    @Override
    public void generateInstantaneousDisplacementsVector(SingleCellConditionDataHolder
                                                                     singleCellConditionDataHolder) {
        Object[][] dataStructure = singleCellConditionDataHolder.getDataStructure();
        Double[] instantaneousDisplacementsVector = new Double[dataStructure.length];
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
        Object[][] dataStructure = singleCellConditionDataHolder.getDataStructure();
        Double[] directionalityRatiosVector = new Double[dataStructure.length];
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
    public void generateMedianDirectionalityRatiosVector(SingleCellConditionDataHolder
                                                                     singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] medianDirectionalityRatiosVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < medianDirectionalityRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackAngle = trackDataHolder.getCellCentricDataHolder().getMedianDirectionalityRatio();
            medianDirectionalityRatiosVector[i] = trackAngle;
        }
        singleCellConditionDataHolder.setMedianDirectionalityRatiosVector(medianDirectionalityRatiosVector);
    }

    @Override
    public void generateTrackDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] trackDisplacementsVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackMeanDisplacement = trackDataHolder.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        singleCellConditionDataHolder.setTrackDisplacementsVector(trackDisplacementsVector);
    }

    @Override
    public void generateCumulativeDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] cumulativeDistancesVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < cumulativeDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double cumulativeDistance = trackDataHolder.getCellCentricDataHolder().getCumulativeDistance();
            cumulativeDistancesVector[i] = cumulativeDistance;
        }
        singleCellConditionDataHolder.setCumulativeDistancesVector(cumulativeDistancesVector);
    }

    @Override
    public void generateEuclideanDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] euclideanDistancesVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < euclideanDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double euclideanDistance = trackDataHolder.getCellCentricDataHolder().getEuclideanDistance();
            euclideanDistancesVector[i] = euclideanDistance;
        }
        singleCellConditionDataHolder.setEuclideanDistancesVector(euclideanDistancesVector);
    }

    @Override
    public void generateTrackSpeedsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] trackDisplacementsVector = singleCellConditionDataHolder.getTrackDisplacementsVector();
        Double[] trackSpeedsVector = new Double[trackDisplacementsVector.length];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double trackMeanSpeed = trackDataHolder.getCellCentricDataHolder().getMedianSpeed();
            trackSpeedsVector[i] = trackMeanSpeed;
        }
        singleCellConditionDataHolder.setTrackSpeedsVector(trackSpeedsVector);
    }

    @Override
    public void generateEndPointDirectionalityRatiosVector(SingleCellConditionDataHolder
                                                                       singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] endPointDirectionalityRatios = new Double[trackDataHolders.size()];
        for (int i = 0; i < endPointDirectionalityRatios.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double endPointDirectionalityRatio = trackDataHolder.getCellCentricDataHolder()
                    .getEndPointDirectionalityRatio();
            endPointDirectionalityRatios[i] = endPointDirectionalityRatio;
        }
        singleCellConditionDataHolder.setEndPointDirectionalityRatios(endPointDirectionalityRatios);
    }

    @Override
    public void generateConvexHullsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        ConvexHull[] convexHullsVector = new ConvexHull[trackDataHolders.size()];
        for (int i = 0; i < convexHullsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            ConvexHull convexHull = trackDataHolder.getCellCentricDataHolder().getConvexHull();
            convexHullsVector[i] = convexHull;
        }
        singleCellConditionDataHolder.setConvexHullsVector(convexHullsVector);
    }

    @Override
    public void generateDisplacementRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] displacementRatiosVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < displacementRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double displacementRatio = trackDataHolder.getCellCentricDataHolder().getDisplacementRatio();
            displacementRatiosVector[i] = displacementRatio;
        }
        singleCellConditionDataHolder.setDisplacementRatiosVector(displacementRatiosVector);
    }

    @Override
    public void generateOutreachRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] outreachRatiosVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < outreachRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double outreachRatio = trackDataHolder.getCellCentricDataHolder().getOutreachRatio();
            outreachRatiosVector[i] = outreachRatio;
        }
        singleCellConditionDataHolder.setOutreachRatiosVector(outreachRatiosVector);
    }

    @Override
    public void generateTurningAnglesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        Object[][] dataStructure = singleCellConditionDataHolder.getDataStructure();
        Double[] turningAnglesVector = new Double[dataStructure.length];
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
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] medianTurningAnglesVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < medianTurningAnglesVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double medianTurningAngle = trackDataHolder.getCellCentricDataHolder().getMedianTurningAngle();
            medianTurningAnglesVector[i] = medianTurningAngle;
        }
        singleCellConditionDataHolder.setMedianTurningAnglesVector(medianTurningAnglesVector);
    }

    @Override
    public void generateMedianDirectionAutocorrelationsVector(SingleCellConditionDataHolder
                                                                          singleCellConditionDataHolder) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        Double[] medianDirectionAutocorrelationsVector = new Double[trackDataHolders.size()];
        for (int i = 0; i < medianDirectionAutocorrelationsVector.length; i++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(i);
            double medianDirectionAutocorrelation = trackDataHolder.getCellCentricDataHolder()
                    .getMedianDirectionAutocorrelation();
            medianDirectionAutocorrelationsVector[i] = medianDirectionAutocorrelation;
        }
        singleCellConditionDataHolder.setMedianDirectionAutocorrelationsVector(medianDirectionAutocorrelationsVector);
    }
}
