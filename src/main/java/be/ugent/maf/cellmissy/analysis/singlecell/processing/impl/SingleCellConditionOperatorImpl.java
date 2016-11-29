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
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.operateOnStepsAndCells(singleCellWellDataHolder);
        });
    }

    @Override
    public void generateInstantaneousDisplacementsVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateInstantaneousDisplacementsVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateDirectionalityRatiosVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateMedianDirectionalityRatiosVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateTrackDisplacementsVector(singleCellWellDataHolder);
        });
        Double[] trackDisplacementsVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double trackMeanDisplacement = trackDataHolder.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        singleCellConditionDataHolder.setTrackDisplacementsVector(trackDisplacementsVector);
    }

    /**
     *
     * @param singleCellConditionDataHolder
     */
    @Override
    public void computeMedianSpeed(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            double medianSpeed = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellWellDataHolder.getTrackSpeedsVector())));
            singleCellConditionDataHolder.setMedianSpeed(medianSpeed);
        }
    }

    @Override
    public void generateCumulativeDistancesVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateCumulativeDistancesVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateEuclideanDistancesVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateTrackSpeedsVector(singleCellWellDataHolder);
        });
        Double[] trackSpeedsVector = new Double[singleCellConditionDataHolder.getTrackDisplacementsVector().length];
        for (int i = 0; i < singleCellConditionDataHolder.getTrackDisplacementsVector().length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double trackMeanSpeed = trackDataHolder.getCellCentricDataHolder().getMedianSpeed();
            trackSpeedsVector[i] = trackMeanSpeed;
        }
        singleCellConditionDataHolder.setTrackSpeedsVector(trackSpeedsVector);
    }

    @Override
    public void generateMSDArray(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateMSDArray(singleCellWellDataHolder);
        });

        List<SingleCellWellDataHolder> singleCellWellDataHolders = singleCellConditionDataHolder.getSingleCellWellDataHolders();
        int max = 0;
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellWellDataHolders) {
            double[][] msd = singleCellWellDataHolder.getMsdArray();
            if (msd.length > max) {
                max = msd.length;
            }
        }

        // the new array
        double[][] msdArray = new double[max][2];
        msdArray[0][0] = 0; // always zero at this cell

        double[] dt_n = new double[max];

        for (int j = 1; j < max; j++) {
            msdArray[j][0] = j;
            for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellWellDataHolders) {
                double[][] msd = singleCellWellDataHolder.getMsdArray();
                if (msd.length > j) {

                    msdArray[j][1] += msd[j][1];
                    dt_n[j]++;
                }
            }
        }

        // divide by the number of occurrences to get the average
        for (int dt = 1; dt < max; dt++) {
            msdArray[dt][1] = (dt_n[dt] != 0) ? msdArray[dt][1] / dt_n[dt] : 0;
        }

        singleCellConditionDataHolder.setMsdArray(msdArray);
    }

    @Override
    public void generateEndPointDirectionalityRatiosVector(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateEndPointDirectionalityRatiosVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateConvexHullsVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateDisplacementRatiosVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateOutreachRatiosVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateTurningAnglesVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateMedianTurningAnglesVector(singleCellWellDataHolder);
        });
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
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.generateMedianDirectionAutocorrelationsVector(singleCellWellDataHolder);
        });
        Double[] medianDirectionAutocorrelationsVector = new Double[singleCellConditionDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < medianDirectionAutocorrelationsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellConditionDataHolder.getTrackDataHolders().get(i);
            double medianDirectionAutocorrelation = trackDataHolder.getCellCentricDataHolder()
                    .getMedianDirectionAutocorrelation();
            medianDirectionAutocorrelationsVector[i] = medianDirectionAutocorrelation;
        }
        singleCellConditionDataHolder.setMedianDirectionAutocorrelationsVector(medianDirectionAutocorrelationsVector);
    }

    @Override
    public void operateOnInterpolatedTracks(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            singleCellWellOperator.operateOnInterpolatedTracks(singleCellWellDataHolder);
        });
    }
}
