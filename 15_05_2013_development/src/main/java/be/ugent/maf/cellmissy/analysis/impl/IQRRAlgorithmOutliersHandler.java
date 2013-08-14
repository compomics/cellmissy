/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import org.apache.commons.lang.ArrayUtils;

/**
 * This is implementing the outliers detection following the interquartile range
 * criterium, with the R type algorithm.
 *
 * @author Paola Masuzzo
 */
public class IQRRAlgorithmOutliersHandler implements OutliersHandler {

    final double k = 1.5;

    @Override
    public boolean[][] detectOutliers(Double[][] data) {
        Double[][] transposedData = AnalysisUtils.transpose2DArray(data);
        boolean[][] booleanMatrix = new boolean[data.length][data[0].length];

        for (int rowIndex = 0; rowIndex < transposedData.length; rowIndex++) {
            Double[] row = transposedData[rowIndex];
            Double[] excludeNullValues = AnalysisUtils.excludeNullValues(row);

            //quantiles are estimated on primitive values, and null values need to be excluded
            double firstQuartile = AnalysisUtils.estimateQuantile(ArrayUtils.toPrimitive(excludeNullValues), 25);
            double thirdQuartile = AnalysisUtils.estimateQuantile(ArrayUtils.toPrimitive(excludeNullValues), 75);
            // compute interquartile range
            double IQR = thirdQuartile - firstQuartile;

            for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                //an outlier is here defined as a data point smaller than 1.5 times (first quartile - IQR)
                // greater than 1.5 * (third quartile + IQR)
                if (row[columnIndex] != null) {
                    if (row[columnIndex] < (firstQuartile - k * IQR) | row[columnIndex] > (thirdQuartile + k * IQR)) {
                        booleanMatrix[columnIndex][rowIndex] = true;
                    }
                }
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
}
