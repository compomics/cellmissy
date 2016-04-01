/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.InterpolationMethod;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellWellOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.TrackOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.interpolation.InterpolatedTrackOperator;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An implementation for the single cell well operator.
 *
 * @author Paola
 */
@Component("singleCellWellOperator")
public class SingleCellWellOperatorImpl implements SingleCellWellOperator {

    @Autowired
    private TrackOperator trackOperator; // the operator for the track
    @Autowired
    private InterpolatedTrackOperator interpolatedTrackOperator; // the operator for the interpolated track

    @Override
    public void operateOnStepsAndCells(SingleCellWellDataHolder singleCellWellDataHolder) {
        singleCellWellDataHolder.getTrackDataHolders().stream().map((trackDataHolder) -> {
            trackOperator.operateOnSteps(trackDataHolder);
            return trackDataHolder;
        }).forEach((trackDataHolder) -> {
            trackOperator.operateOnCells(trackDataHolder);
        });
    }

    @Override
    public void generateInstantaneousDisplacementsVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] instantaneousDisplacementsVector = new Double[singleCellWellDataHolder.getDataStructure().length];
        int counter = 0;
        for (Iterator<TrackDataHolder> it = singleCellWellDataHolder.getTrackDataHolders().iterator(); it.hasNext();) {
            TrackDataHolder trackDataHolder = it.next();
            Double[] instantaneousDisplacements = trackDataHolder.getStepCentricDataHolder()
                    .getInstantaneousDisplacements();
            for (Double instantaneousDisplacement : instantaneousDisplacements) {
                instantaneousDisplacementsVector[counter] = instantaneousDisplacement;
                counter++;
            }
        }
        singleCellWellDataHolder.setInstantaneousDisplacementsVector(instantaneousDisplacementsVector);
    }

    @Override
    public void generateDirectionalityRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] directionalityRatiosVector = new Double[singleCellWellDataHolder.getDataStructure().length];
        int counter = 0;
        for (Iterator<TrackDataHolder> it = singleCellWellDataHolder.getTrackDataHolders().iterator(); it.hasNext();) {
            TrackDataHolder trackDataHolder = it.next();
            Double[] directionalityRatios = trackDataHolder.getStepCentricDataHolder().getDirectionalityRatios();
            for (Double directionalityRatio : directionalityRatios) {
                directionalityRatiosVector[counter] = directionalityRatio;
                counter++;
            }
        }
        singleCellWellDataHolder.setDirectionalityRatiosVector(directionalityRatiosVector);
    }

    @Override
    public void generateMedianDirectionalityRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] medianDirectionalityRatiosVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < medianDirectionalityRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double trackAngle = trackDataHolder.getCellCentricDataHolder().getMedianDirectionalityRatio();
            medianDirectionalityRatiosVector[i] = trackAngle;
        }
        singleCellWellDataHolder.setMedianDirectionalityRatiosVector(medianDirectionalityRatiosVector);
    }

    @Override
    public void generateTrackDisplacementsVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] trackDisplacementsVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double trackMeanDisplacement = trackDataHolder.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        singleCellWellDataHolder.setTrackDisplacementsVector(trackDisplacementsVector);
    }

    @Override
    public void generateCumulativeDistancesVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] cumulativeDistancesVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < cumulativeDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double cumulativeDistance = trackDataHolder.getCellCentricDataHolder().getCumulativeDistance();
            cumulativeDistancesVector[i] = cumulativeDistance;
        }
        singleCellWellDataHolder.setCumulativeDistancesVector(cumulativeDistancesVector);
    }

    @Override
    public void generateEuclideanDistancesVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] euclideanDistancesVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < euclideanDistancesVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double euclideanDistance = trackDataHolder.getCellCentricDataHolder().getEuclideanDistance();
            euclideanDistancesVector[i] = euclideanDistance;
        }
        singleCellWellDataHolder.setEuclideanDistancesVector(euclideanDistancesVector);
    }

    @Override
    public void generateTrackSpeedsVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] trackSpeedsVector = new Double[singleCellWellDataHolder.getTrackDisplacementsVector().length];
        for (int i = 0; i < singleCellWellDataHolder.getTrackDisplacementsVector().length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double trackMeanSpeed = trackDataHolder.getCellCentricDataHolder().getMedianSpeed();
            trackSpeedsVector[i] = trackMeanSpeed;
        }
        singleCellWellDataHolder.setTrackSpeedsVector(trackSpeedsVector);
    }

    @Override
    public void generateMSDArray(SingleCellWellDataHolder singleCellWellDataHolder) {
        int max = 0;
        List<TrackDataHolder> trackDataHolders = singleCellWellDataHolder.getTrackDataHolders();
        for (TrackDataHolder trackDataHolder : trackDataHolders) {
            double[][] msd = trackDataHolder.getStepCentricDataHolder().getMSD();
            if (msd.length > max) {
                max = msd.length;
            }
        }

        double[][] msdArray = new double[max][2];
        msdArray[0][0] = 0; // always zero at this cell

        double[] dt_n = new double[max];

        for (int j = 1; j < max; j++) {
            msdArray[j][0] = j;
            for (TrackDataHolder trackDataHolder : trackDataHolders) {
                double[][] msd = trackDataHolder.getStepCentricDataHolder().getMSD();
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
        
        singleCellWellDataHolder.setMsdArray(msdArray);
    }

    @Override
    public void generateEndPointDirectionalityRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] endPointDirectionalityRatios = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < endPointDirectionalityRatios.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double endPointDirectionalityRatio = trackDataHolder.getCellCentricDataHolder()
                    .getEndPointDirectionalityRatio();
            endPointDirectionalityRatios[i] = endPointDirectionalityRatio;
        }
        singleCellWellDataHolder.setEndPointDirectionalityRatios(endPointDirectionalityRatios);
    }

    @Override
    public void generateConvexHullsVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        ConvexHull[] convexHullsVector = new ConvexHull[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < convexHullsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            ConvexHull convexHull = trackDataHolder.getCellCentricDataHolder().getConvexHull();
            convexHullsVector[i] = convexHull;
        }
        singleCellWellDataHolder.setConvexHullsVector(convexHullsVector);
    }

    @Override
    public void generateDisplacementRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] displacementRatiosVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < displacementRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double displacementRatio = trackDataHolder.getCellCentricDataHolder().getDisplacementRatio();
            displacementRatiosVector[i] = displacementRatio;
        }
        singleCellWellDataHolder.setDisplacementRatiosVector(displacementRatiosVector);
    }

    @Override
    public void generateOutreachRatiosVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] outreachRatiosVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < outreachRatiosVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double outreachRatio = trackDataHolder.getCellCentricDataHolder().getOutreachRatio();
            outreachRatiosVector[i] = outreachRatio;
        }
        singleCellWellDataHolder.setOutreachRatiosVector(outreachRatiosVector);
    }

    @Override
    public void generateTurningAnglesVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] turningAnglesVector = new Double[singleCellWellDataHolder.getDataStructure().length];
        int counter = 0;
        for (TrackDataHolder trackDataHolder : singleCellWellDataHolder.getTrackDataHolders()) {
            Double[] turningAngles = trackDataHolder.getStepCentricDataHolder().getTurningAngles();
            for (Double turningAngle : turningAngles) {
                turningAnglesVector[counter] = turningAngle;
                counter++;
            }
        }
        singleCellWellDataHolder.setTurningAnglesVector(turningAnglesVector);
    }

    @Override
    public void generateMedianTurningAnglesVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] medianTurningAnglesVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < medianTurningAnglesVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double medianTurningAngle = trackDataHolder.getCellCentricDataHolder().getMedianTurningAngle();
            medianTurningAnglesVector[i] = medianTurningAngle;
        }
        singleCellWellDataHolder.setMedianTurningAnglesVector(medianTurningAnglesVector);
    }

    @Override
    public void generateMedianDirectionAutocorrelationsVector(SingleCellWellDataHolder singleCellWellDataHolder) {
        Double[] medianDirectionAutocorrelationsVector = new Double[singleCellWellDataHolder.getTrackDataHolders().size()];
        for (int i = 0; i < medianDirectionAutocorrelationsVector.length; i++) {
            TrackDataHolder trackDataHolder = singleCellWellDataHolder.getTrackDataHolders().get(i);
            double medianDirectionAutocorrelation = trackDataHolder.getCellCentricDataHolder()
                    .getMedianDirectionAutocorrelation();
            medianDirectionAutocorrelationsVector[i] = medianDirectionAutocorrelation;
        }
        singleCellWellDataHolder.setMedianDirectionAutocorrelationsVector(medianDirectionAutocorrelationsVector);
    }

    @Override
    public void operateOnInterpolatedTracks(SingleCellWellDataHolder singleCellWellDataHolder) {
        // iterate through the track data holders and get the interpolation map
        singleCellWellDataHolder.getTrackDataHolders().stream().forEach((trackDataHolder) -> {
            Map<InterpolationMethod, InterpolatedTrack> interpolationMap = trackDataHolder.getStepCentricDataHolder().getInterpolationMap();
            // for each method, get the interpolated track
            interpolationMap.keySet().stream().forEach((method) -> {
                InterpolatedTrack interpolatedTrack = interpolationMap.get(method);
                // now do the computations
                interpolatedTrackOperator.computeCoordinatesMatrix(interpolatedTrack);
                interpolatedTrackOperator.computeDeltaMovements(interpolatedTrack);
                interpolatedTrackOperator.computeTurningAngles(interpolatedTrack);
            });
        });
    }
}
