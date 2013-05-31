/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

/**
 * Interface for handling outliers in a distribution of data points
 *
 * @author Paola Masuzzo
 */
@Component("outliersHandler")
public class OutliersHandlerImpl implements OutliersHandler {

    final double k = 1.5;

    @Override
    public boolean[][] detectOutliers(Double[][] data) {
        Double[][] transposedData = AnalysisUtils.transpose2DArray(data);
        boolean[][] booleanMatrix = new boolean[data.length][data[0].length];
        for (int rowIndex = 0; rowIndex < transposedData.length; rowIndex++) {
            Double[] row = transposedData[rowIndex];
            for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                booleanMatrix[columnIndex][rowIndex] = isOutlier(row[columnIndex], row);
            }
        }
        return booleanMatrix;
    }

    @Override
    public Double[][] correctForOutliers(Double[][] data) {
        Double[][] correctedData = new Double[data.length][data[0].length];
        boolean[][] outliers = detectOutliers(data);
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < data[0].length; columnIndex++) {
                //here data points that are outliers are being simply kicked out from the data set
                if (!outliers[rowIndex][columnIndex]) {
                    correctedData[rowIndex][columnIndex] = data[rowIndex][columnIndex];
                }
            }
        }
        return correctedData;
    }

    @Override
    public boolean[] detectOutliers(Double[] data) {
        boolean[] outliers = new boolean[data.length];
        for (int i = 0; i < data.length; i++) {
            outliers[i] = isOutlier(data[i], data);
        }
        return outliers;
    }

    @Override
    public Double[] correctForOutliers(Double[] data) {
        Double[] correctedData = new Double[data.length];
        boolean[] outliers = detectOutliers(data);
        for (int i = 0; i < data.length; i++) {
            if (!outliers[i]) {
                correctedData[i] = data[i];
            }
        }
        return correctedData;
    }

    /**
     *
     * @param dataPoint
     * @param dataset
     * @return
     */
    private boolean isOutlier(Double dataPoint, Double[] dataset) {
        boolean isOutlier = false;
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(dataset);
        //quantiles are estimated on primitive values, and null values need to be excluded
        double firstQuartile = AnalysisUtils.estimateQuantile(ArrayUtils.toPrimitive(excludeNullValues), 25);
        double thirdQuartile = AnalysisUtils.estimateQuantile(ArrayUtils.toPrimitive(excludeNullValues), 75);
        double IQR = thirdQuartile - firstQuartile;
        if (dataPoint != null) {
            if (dataPoint < (firstQuartile - k * IQR) | dataPoint > (thirdQuartile + k * IQR)) {
                isOutlier = true;
            }
        }
        return isOutlier;
    }
}
