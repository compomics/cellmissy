/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

/**
 * Interface for handling outliers in a distribution of data points
 * @author Paola Masuzzo
 */
@Component("outliersHandler")
public class OutliersHandlerImpl implements OutliersHandler {

    @Override
    public boolean[][] detectOutliers(Double[][] data) {
        Double[][] transposedData = AnalysisUtils.transpose2DArray(data);
        boolean[][] booleanMatrix = new boolean[data.length][data[0].length];

        for (int rowIndex = 0; rowIndex < transposedData.length; rowIndex++) {
            Double[] row = transposedData[rowIndex];
            Double[] excludeNullValues = AnalysisUtils.excludeNullValues(row);
            final double k = 1.5;

            //quantiles are estimated on primitive values, and null values need to be excluded
            double firstQuartile = AnalysisUtils.estimateQuantile(ArrayUtils.toPrimitive(excludeNullValues), 25);
            double thirdQuartile = AnalysisUtils.estimateQuantile(ArrayUtils.toPrimitive(excludeNullValues), 75);
            double IQR = thirdQuartile - firstQuartile;

            for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                //an outlier is here defined as a data point greater than 1.5 * (upper quartile + interquile range)
                if (row[columnIndex] != null && row[columnIndex] > (thirdQuartile + k * IQR)) {
                    booleanMatrix[columnIndex][rowIndex] = true;
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
