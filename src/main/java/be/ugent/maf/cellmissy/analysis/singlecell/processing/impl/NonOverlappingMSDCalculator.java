/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.MSDCalculator;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * An implementation of the MSD calculator -- computes mean-squared displacement
 * with non-overlapping lag times.
 *
 * @author Paola
 */
@Component("nonOverlappingMSDCalculator")
public class NonOverlappingMSDCalculator implements MSDCalculator {

    @Override
    public double[][] computeMSD(StepCentricDataHolder stepCentricDataHolder) {
        // get the track points
        List<TrackPoint> points = stepCentricDataHolder.getTrack().getTrackPointList();
        // the number of MSD values
        int nMSD = points.size();

        double[] dt_n = new double[nMSD];
        // this 2D array contains both the time lags (col:0)
        // and the correspective msd value (col:1)
        double[][] MSD = new double[nMSD][2];

        for (int tau = 1; tau < nMSD; tau++) {
            MSD[tau][0] = tau * 1;

            for (int j = 0; j + tau < nMSD; j += tau) {
                MSD[tau][1] += points.get(j).getGeometricPoint().euclideanDistanceTo(points.get(j + tau).getGeometricPoint());

                dt_n[tau]++;
            }

        }
        // divide the sum by the sample size -- to average across time
        for (int dt = 1; dt < nMSD; dt++) {
            MSD[dt][1] = (dt_n[dt] != 0) ? MSD[dt][1] / dt_n[dt] : 0;
        }
        return MSD;
    }

}
