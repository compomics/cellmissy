/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("areaAnalyzer")
public class AreaAnalyzerImpl implements AreaAnalyzer {

    @Autowired
    private LinearRegressor linearRegressor;

    @Override
    public void estimateLinearModel(AreaPreProcessingResults areaPreProcessingResults, AreaAnalysisResults areaAnalysisResults, double[] timeFrames) {
        Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        // check if some replicates need to be excluded
        boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
        // Double arrays for slopes and R2 coefficients
        Double[] slopes = new Double[transposedArea.length];
        Double[] goodnessOfFit = new Double[transposedArea.length];
        for (int columnIndex = 0; columnIndex < transposedArea.length; columnIndex++) {
            //check if replicate needs to be excluded from computation
            if (!excludeReplicates[columnIndex]) {
                Double[] data = transposedArea[columnIndex];
                List<double[]> tempList = new ArrayList<>();
                for (int i = 0; i < data.length; i++) {
                    if (data[i] != null) {
                        double[] temp = new double[2];
                        temp[0] = timeFrames[i];
                        temp[1] = data[i];
                        tempList.add(temp);
                    }
                }
                double[][] tempArray = tempList.toArray(new double[tempList.size()][]);
                double slope = computeSlope(tempArray);
                double coefficient = computeRCoefficient(tempArray);
                slopes[columnIndex] = slope;
                goodnessOfFit[columnIndex] = coefficient;
            } else {
                // set results to null if replicate is not taken into account 
                slopes[columnIndex] = null;
                goodnessOfFit[columnIndex] = null;
            }
        }
        areaAnalysisResults.setSlopes(slopes);
        areaAnalysisResults.setGoodnessOfFit(goodnessOfFit);
        // set mean slope
        areaAnalysisResults.setMeanSlope(AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopes))));
        // set MAD for mean slope
        areaAnalysisResults.setMadSlope(AnalysisUtils.scaleMAD(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopes))));
    }

    /**
     * Given 2D array of double compute Slope through a Linear Regression
     * @param data
     * @return 
     */
    private double computeSlope(double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(0);
    }

    /**
     * Get R2 Coefficient out of the Linear Regression Model
     * @param data
     * @return 
     */
    private double computeRCoefficient(double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(1);
    }
}