/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * Utility class for Analysis -- basic math and statistics methods
 * @author Paola Masuzzo
 */
public class AnalysisUtils {

    /**
     * exclude null values from an array of Double
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
        Double[] toArray = list.toArray(new Double[list.size()]);
        return toArray;
    }

    /**
     * transpose a 2D array of double
     * @param data
     * @return the same 2D array but transposed
     */
    public static Double[][] transpose2DArray(Double[][] data) {
        Double[][] transposed = new Double[data[0].length][data.length];
        for (int i = 0; i < data[0].length; i++) {
            List<Double> tempList = new ArrayList<>();
            for (int j = 0; j < data.length; j++) {
                if (data[j][i] != null) {
                    tempList.add((double) data[j][i]);
                }
            }
            transposed[i] = tempList.toArray(new Double[tempList.size()]);
        }
        return transposed;
    }

    /**
     * Compute mean value of an array of double
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
     * @param data
     * @return median
     */
    public static double computeMedian(double[] data) {
        // sort the input data
        Arrays.sort(data);
        //make a distinction between odd and even data points
        if (data.length % 2 == 1) {
            return data[(data.length + 1) / 2 - 1];
        } else {
            double lower = data[(data.length / 2 - 1)];
            double upper = data[(data.length / 2)];

            return (lower + upper) / 2;
        }
    }

    /**
     * Compute Standard Deviation of an array of double
     * @param data
     * @return sd
     */
    public static double computeStandardDeviation(double[] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            final double v = data[i] - computeMean(data);
            sum += v * v;
        }
        return Math.sqrt(sum / data.length);
    }

    /**
     * Compute Standard Error of the Mean
     * @param data
     * @return SEM
     */
    public static double computeSEM(double[] data) {
        return (computeStandardDeviation(data) / Math.sqrt(data.length));
    }

    /**
     * Compute Median Absolute Deviation (MAD) of an array of double
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
     * Scale MAD in order to use it as a consistent estimator for the estimation of the sd
     * @param data
     * @return sd (related to MAD)
     */
    public static double scaleMAD(double[] data) {
        //scale factor for asymptotically normal consistency
        final double constant = 1.4826;
        return constant * computeMAD(data);
    }

    /**
     * Compute First Quartile
     * @param data
     * @return double
     */
    public static double computeFirstQuartile(double[] data) {
        DescriptiveStatistics dataStatistics = new DescriptiveStatistics();
        for (int i = 0; i < data.length; i++) {
            dataStatistics.addValue(data[i]);
        }
        return dataStatistics.getPercentile(25);
    }

    /**
     * Compute Third Quartile
     * @param data
     * @return double
     */
    public static double computeThirdQuartile(double[] data) {
        DescriptiveStatistics dataStatistics = new DescriptiveStatistics();
        for (int i = 0; i < data.length; i++) {
            dataStatistics.addValue(data[i]);
        }
        return dataStatistics.getPercentile(75);
    }

    /**
     * 
     * @param firstVector
     * @param secondVector
     * @return 
     */
    public static double computeEuclideanDistance(double[] firstVector, double[] secondVector) {
        double distance = 0;
        int minLength = Math.min(firstVector.length, secondVector.length);
        for (int i = 0; i < minLength; i++) {
            double temp = Math.pow((firstVector[i] - secondVector[i]), 2);
            distance += temp;
        }
        return Math.sqrt(distance);
    }
}
