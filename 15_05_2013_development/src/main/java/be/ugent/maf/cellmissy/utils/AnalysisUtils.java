/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * Utility class for Analysis -- basic math and statistics methods
 *
 * @author Paola Masuzzo
 */
public class AnalysisUtils {

    /**
     * Exclude null values from an array of Double
     *
     * @param data
     * @return another Double array with no longer null values
     */
    public static Double[] excludeNullValues(Double[] data) {
        List<Double> list = new ArrayList<>();
        for (Double value : data) {
            if (value != null) {
                list.add(value);
            }
        }
        Double[] newArray = list.toArray(new Double[list.size()]);
        return newArray;
    }

    /**
     * Transpose a 2D array of double
     *
     * @param data
     * @return the same 2D array but transposed
     */
    public static Double[][] transpose2DArray(Double[][] data) {
        Double[][] transposed = new Double[data[0].length][data.length];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < data[0].length; columnIndex++) {
                if (data[rowIndex][columnIndex] != null) {
                    transposed[columnIndex][rowIndex] = data[rowIndex][columnIndex];
                }
            }
        }
        return transposed;
    }

    /**
     * Transpose a 2D array of boolean
     *
     * @param matrix
     * @return
     */
    public static boolean[][] transposeBooleanMatrix(boolean[][] matrix) {
        boolean[][] transposed = new boolean[matrix.length][matrix[0].length];
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
                if (matrix[rowIndex][columnIndex]) {
                    transposed[columnIndex][rowIndex] = true;
                }
            }
        }
        return transposed;
    }

    /**
     * Formatting a symmetric matrix: make the matrix diagonal, so that symmetric (identical) values are not shown anymore, i.e. they are set to null. With a customized renderer, these null values can
     * be shown as a dash (-), as we do for example in the p values matrix.
     *
     * @param matrix
     * @return
     */
    public static Double[][] formatSymmetricMatrix(Double[][] matrix) {

        Double[][] formattedMatrix = new Double[matrix.length][matrix[0].length];
        // copy content to new matrix
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            System.arraycopy(matrix[rowIndex], 0, formattedMatrix[rowIndex], 0, matrix[0].length);
        }
        // iterate through the raows and columns
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++) {
                // get current value in the matrix
                Double value = formattedMatrix[rowIndex][columnIndex];
                // get the summetric value in the matrix
                Double symmValue = formattedMatrix[columnIndex][rowIndex];
                if (value != null && symmValue != null) {
                    if (value.equals(symmValue)) {
                        formattedMatrix[rowIndex][columnIndex] = null;
                    }
                }
            }
        }
        return formattedMatrix;
    }

    /**
     * Round up to two decimals
     *
     * @param d
     * @return
     */
    public static Double roundThreeDecimals(Double d) {
        DecimalFormat twoDForm = new DecimalFormat("###.###");
        return Double.valueOf(twoDForm.format(d));
    }

    /**
     * Compute mean value of an array of double
     *
     * @param data
     * @return mean
     */
    public static double computeMean(double[] data) {
        // sum of all the elements
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum / data.length;
    }

    /**
     * Compute median value of an array of double
     *
     * @param data
     * @return median
     */
    public static double computeMedian(double[] data) {
        // sort the input data, i.e. arrange the data points in ascending order
        Arrays.sort(data);
        //make a distinction between odd and even dataset sizes
        // odd size: return the data point in the middle position
        if (data.length % 2 == 1) {
            return data[(data.length + 1) / 2 - 1];
        } else {
            // even size
            double lower = data[(data.length / 2 - 1)];
            double upper = data[(data.length / 2)];
            return (lower + upper) / 2;
        }
    }

    /**
     * Compute Standard Deviation of an array of double
     *
     * @param data
     * @return sd
     */
    public static double computeStandardDeviation(double[] data) {
        double sum = 0;
        double mean = computeMean(data);
        for (int i = 0; i < data.length; i++) {
            double diff = data[i] - mean;
            sum += diff * diff;
        }
        return Math.sqrt(sum / data.length);
    }

    /**
     * Compute Standard Error of the Mean (SEM) of a given array of double
     *
     * @param data
     * @return SEM
     */
    public static double computeSEM(double[] data) {
        return (computeStandardDeviation(data) / Math.sqrt(data.length));
    }

    /**
     * Compute Median Absolute Deviation (MAD) of an array of double
     *
     * @param data
     * @return MAD
     */
    public static double computeMAD(double[] data) {
        double[] deviations = new double[data.length];
        double median = computeMedian(data);
        for (int i = 0; i < data.length; i++) {
            deviations[i] = Math.abs(data[i] - median);
        }
        return computeMedian(deviations);
    }

    /**
     * Scale MAD in order to use it as a consistent estimator for the estimation of the standard deviation
     *
     * @param data
     * @return sd (related to MAD)
     */
    public static double scaleMAD(double[] data) {
        //scale factor for asymptotically normal consistency
        final double constant = 1.4826;
        return constant * computeMAD(data);
    }

    /**
     * This method is using the Descriptive Statistics Class from org.apache.commons.math to estimate sample quantiles Cfr algorithm type 6 in R, EXCEL, Minitab and SPSS. Continuous sample quantiles
     *
     * @param data
     * @param p
     * @return
     */
    public static double computeQuantile(double[] data, double p) {
        DescriptiveStatistics dataStatistics = new DescriptiveStatistics();
        for (int i = 0; i < data.length; i++) {
            dataStatistics.addValue(data[i]);
        }
        // get an estimate for the pth percentile of the data
        return dataStatistics.getPercentile(p);
    }

    /**
     * This method is estimating quantiles making use of algorithm type 7 in R. This is used by S as well. This implementation is more sensitive, especially with small datasets (less than 15 data
     * points)
     *
     * @param data -- array of double (distribution of data points)
     * @param p -- percentile
     * @return a double
     */
    public static double estimateQuantile(double[] data, double p) {
        double estimation = 0;
        //get order statistics
        Arrays.sort(data);
        int dataSize = data.length;
        // criterium to estimate the quantile: 1+p(N-1)
        double criterium = 1 + (p / 100) * (dataSize - 1);
        // get the int part of this criterium
        int k = (int) criterium;
        // get the double part of this criterium
        double d = criterium - k;
        //
        if (k > 0 && k < dataSize) {
            estimation = data[k - 1] + d * (data[k] - data[k - 1]);
        } else if (k == 0) {
            estimation = data[0];
        } else if (k == dataSize) {
            estimation = data[dataSize - 1];
        }

        return estimation;
    }

    /**
     * Given two vectors A and B, this method is computing the Euclidean Distance between them
     *
     * @param firstVector
     * @param secondVector
     * @return a double value for the distance
     */
    public static double computeEuclideanDistance(Double[] firstVector, Double[] secondVector) {
        double distance = 0;
        for (int i = 0; i < firstVector.length; i++) {
            if (firstVector[i] != null && secondVector[i] != null) {
                double temp = Math.pow((firstVector[i] - secondVector[i]), 2);
                distance += temp;
            }
        }
        return Math.sqrt(distance);
    }

    /**
     * Get the maximum double of a list of array of doubles
     *
     * @param list
     * @return maximum double
     */
    public static double getMaxOfAList(List<Double[]> list) {
        double max = 0;
        for (Double[] doubles : list) {
            Arrays.sort(doubles);
            double tempMax = doubles[doubles.length - 1];
            if (tempMax > max) {
                max = tempMax;
            }
        }
        return max;
    }

    /**
     * Compute maximum number of Replicates overall the experiment
     *
     * @param plateConditions
     * @return
     */
    public static int getMaximumNumberOfReplicates(List<PlateCondition> plateConditions) {
        int max = 0;
        for (PlateCondition plateCondition : plateConditions) {
            int numberOfSamplesPerCondition = getNumberOfSamplesPerCondition(plateCondition);
            if (numberOfSamplesPerCondition > max) {
                max = numberOfSamplesPerCondition;
            }
        }
        return max;
    }

    /**
     * Get number of sample points for each condition
     *
     * @param plateCondition
     * @return
     */
    public static int getNumberOfSamplesPerCondition(PlateCondition plateCondition) {
        int numberOfSamples = 0;
        List<Well> processedWells = plateCondition.getAreaAnalyzedWells();
        for (Well well : processedWells) {
            numberOfSamples += getNumberOfSamplesPerWell(well);
        }
        return numberOfSamples;
    }

    /**
     * Get number of sample points per each well
     *
     * @param well
     * @return
     */
    public static int getNumberOfSamplesPerWell(Well well) {
        int numberOfSamplesPerWell = 0;
        for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
            if (!wellHasImagingType.getTimeStepList().isEmpty()) {
                numberOfSamplesPerWell++;
            }
        }
        return numberOfSamplesPerWell;
    }
}
