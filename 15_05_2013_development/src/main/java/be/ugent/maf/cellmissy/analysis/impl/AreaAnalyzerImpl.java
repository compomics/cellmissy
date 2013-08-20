/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import be.ugent.maf.cellmissy.analysis.MeasuredAreaType;
import be.ugent.maf.cellmissy.entity.result.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements the AreaAnalyzer interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("areaAnalyzer")
public class AreaAnalyzerImpl implements AreaAnalyzer {

    @Autowired
    private LinearRegressor linearRegressor;

    /**
     * The class makes use of the LinearRegressor interface to estimate a linear
     * model in this method; having a certain areaAnalysisResults, this method
     * will set the slopes, the R² coefficients, the mean slope and the mad
     * slope of this object. Computation will depend as well on the type of the
     * measured area (open area or cell-covered area) and will be different if
     * corrected data or non corrected data need to be taken into consideration.
     *
     * @param areaPreProcessingResults
     * @param areaAnalysisResults
     * @param useCorrectedData
     * @param measuredAreaTypes
     * @param timeFrames
     */
    @Override
    public void estimateLinearModel(AreaPreProcessingResults areaPreProcessingResults, AreaAnalysisResults areaAnalysisResults, boolean useCorrectedData, MeasuredAreaType measuredAreaTypes, double[] timeFrames) {
        Double[][] dataToUse = null;
        switch (measuredAreaTypes) {
            case CELL_COVERED_AREA:
                if (useCorrectedData) {
                    dataToUse = areaPreProcessingResults.getNormalizedCorrectedArea();
                } else {
                    dataToUse = areaPreProcessingResults.getNormalizedArea();
                }
                break;
            case OPEN_AREA:
                if (useCorrectedData) {
                    dataToUse = areaPreProcessingResults.getNormalizedCorrectedArea();
                } else {
                    dataToUse = areaPreProcessingResults.getTransformedData();
                }
                break;
        }
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(dataToUse);
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
                for (int i = 0; i < timeFrames.length; i++) {
                    if (data[i] != null) {
                        double[] temp = new double[2];
                        temp[0] = timeFrames[i];
                        temp[1] = data[i];
                        tempList.add(temp);
                    }
                }
                double[][] tempArray = tempList.toArray(new double[tempList.size()][]);
                double slope = computeSlope(tempArray);
                double coefficient = 0;
                if (slope != 0) {
                    coefficient = computeRCoefficient(tempArray);
                }
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
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(slopes);
        if (excludeNullValues.length != 0) {
            // set mean slope
            areaAnalysisResults.setMeanSlope(AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNullValues)));
            // set MAD for mean slope
            areaAnalysisResults.setMadSlope(AnalysisUtils.scaleMAD(ArrayUtils.toPrimitive(excludeNullValues)));
        }
    }

    /**
     * Given 2D array of double compute Slope through a Linear Regression
     *
     * @param data
     * @return
     */
    private double computeSlope(double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(0);
    }

    /**
     * Get R2 Coefficient out of the Linear Regression Model
     *
     * @param data
     * @return
     */
    private double computeRCoefficient(double[][] data) {
        return linearRegressor.estimateLinearModel(data).get(1);
    }
}
