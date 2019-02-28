/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
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
        return list.toArray(new Double[list.size()]);
    }

    /**
     * Exclude NaN values from an array of Double
     *
     * @param data
     * @return another Double array with no longer NaN values
     */
    public static Double[] excludeNaNvalues(Double[] data) {
        List<Double> list = new ArrayList<>();
        for (Double value : data) {
            if (!Double.isNaN(value)) {
                list.add(value);
            }
        }
        return list.toArray(new Double[list.size()]);
    }

    /**
     * Transpose a 2D array of Double
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
     * Transpose a 2D array of double
     *
     * @param data
     * @return the same 2D array but transposed
     */
    public static double[][] transpose2DArray(double[][] data) {
        double[][] transposed = new double[data[0].length][data.length];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < data[0].length; columnIndex++) {
                transposed[columnIndex][rowIndex] = data[rowIndex][columnIndex];
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
     * Formatting a symmetric matrix: make the matrix diagonal, so that
     * symmetric (identical) values are not shown anymore, i.e. they are set to
     * null. With a customized renderer, these null values can be shown as a
     * dash (-), as we do for example in the p values matrix.
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
     * Round up to three decimals
     *
     * @param d
     * @return
     */
    public static Double roundThreeDecimals(Double d) {
        DecimalFormat threeForm = new DecimalFormat("###.###");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        threeForm.setDecimalFormatSymbols(dfs);
        return Double.valueOf(threeForm.format(d));
    }

    /**
     * Round up to two decimals
     *
     * @param d
     * @return
     */
    public static Double roundTwoDecimals(Double d) {
        DecimalFormat twoDForm = new DecimalFormat("###.##");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        twoDForm.setDecimalFormatSymbols(dfs);
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
        for (double aData : data) {
            sum += aData;
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
     * Compute median value of a list of double
     *
     * @param data
     * @return median
     */
    public static Double computeMedian(List<Double> data) {
        // sort the input data, i.e. arrange the data points in ascending order
        Collections.sort(data);
        //make a distinction between odd and even dataset sizes
        // odd size: return the data point in the middle position
        if (data.size() % 2 == 1) {
            return data.get((data.size() + 1) / 2 - 1);
        } else {
            // even size
            Double lower = data.get((data.size() / 2 - 1));
            Double upper = data.get((data.size() / 2));
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
        for (double aData : data) {
            double diff = aData - mean;
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
    private static double computeMAD(double[] data) {
        double[] deviations = new double[data.length];
        double median = computeMedian(data);
        for (int i = 0; i < data.length; i++) {
            deviations[i] = Math.abs(data[i] - median);
        }
        return computeMedian(deviations);
    }

    /**
     * Scale MAD in order to use it as a consistent estimator for the estimation
     * of the standard deviation
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
     * This method is using the Descriptive Statistics Class from
     * org.apache.commons.math to estimate sample quantiles Cfr algorithm type 6
     * in R, EXCEL, Minitab and SPSS. Continuous sample quantiles
     *
     * @param data
     * @param p
     * @return
     */
    public static double computeQuantile(double[] data, double p) {
        DescriptiveStatistics dataStatistics = new DescriptiveStatistics();
        for (double aData : data) {
            dataStatistics.addValue(aData);
        }
        // get an estimate for the pth percentile of the data
        return dataStatistics.getPercentile(p);
    }

    /**
     * This method is estimating quantiles making use of algorithm type 7 in R.
     * This is used by S as well. This implementation is more sensitive,
     * especially with small datasets (less than 15 data points)
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
        // check for the range in which k falls
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
     * Add the contents of the array to the list
     *
     * @param list
     * @param array
     * @return A copy of the original list, with the array values added to it.
     */
    public static List<Double> addArrayToList(List<Double> list, double[] array) {
        List<Double> copiedList = new ArrayList<>(list);
        //if parameter is constrained there is a null instead of an array
        if (array == null) {
            copiedList.add(null);
            copiedList.add(null);
        } else {
            for (int i = 0; i < array.length; i++) {
                copiedList.add(array[i]);
            }
        }
        return copiedList;
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
            int numberOfSamplesPerCondition = getNumberOfAreaAnalyzedSamples(plateCondition);
            if (numberOfSamplesPerCondition > max) {
                max = numberOfSamplesPerCondition;
            }
        }
        return max;
    }

    /**
     * Get number of samples that produced area results values.
     *
     * @param plateCondition
     * @return
     */
    public static int getNumberOfAreaAnalyzedSamples(PlateCondition plateCondition) {
        int areaAnalyzedSamples = 0;
        List<Well> areaAnalyzedWells = plateCondition.getAreaAnalyzedWells();
        areaAnalyzedSamples = areaAnalyzedWells.stream().map((well) -> getNumberOfAreaAnalyzedSamplesPerWell(well)).reduce(areaAnalyzedSamples, Integer::sum);
        return areaAnalyzedSamples;
    }

    /**
     * Get number of samples that produced single cell analysis results.
     *
     * @param plateCondition
     * @return
     */
    public static int getNumberOfSingleCellAnalyzedSamples(PlateCondition plateCondition) {
        int singleCellAnalyzedSamples = 0;
        List<Well> singleCellAnalyzedWells = plateCondition.getSingleCellAnalyzedWells();
        singleCellAnalyzedSamples = singleCellAnalyzedWells.stream().map((well) -> getNumberOfSingleCellAnalyzedSamplesPerWell(well)).reduce(singleCellAnalyzedSamples, Integer::sum);
        return singleCellAnalyzedSamples;
    }

    /**
     * Get number of sample points per each well
     *
     * @param well
     * @return
     */
    public static int getNumberOfAreaAnalyzedSamplesPerWell(Well well) {
        int numberOfSamplesPerWell = 0;
        numberOfSamplesPerWell = well.getWellHasImagingTypeList().stream().filter((wellHasImagingType) -> (!wellHasImagingType.getTimeStepList().isEmpty())).map((_item) -> 1).reduce(numberOfSamplesPerWell, Integer::sum);
        return numberOfSamplesPerWell;
    }

    /**
     *
     * @param well
     * @return
     */
    public static int getNumberOfSingleCellAnalyzedSamplesPerWell(Well well) {
        int numberOfSamplesPerWell = 0;
        numberOfSamplesPerWell = well.getWellHasImagingTypeList().stream().filter((wellHasImagingType)
                -> (!wellHasImagingType.getTrackList().isEmpty())).map((_item) -> 1).reduce(numberOfSamplesPerWell, Integer::sum);
        return numberOfSamplesPerWell;
    }

    /**
     *
     * @param well
     * @return
     */
    public static List<Integer> getNumbersOfTrackPoints(Well well) {
        List<Integer> list = new ArrayList<>();
        well.getWellHasImagingTypeList().stream().map((wellHasImagingType) -> wellHasImagingType.getTrackList()).forEach((trackList) -> {
            int number = 0;
            if (!trackList.isEmpty()) {
                number = trackList.stream().map((track) -> track.getTrackPointList().size()).reduce(number, Integer::sum);
                list.add(number);
            }
        });
        return list;
    }

    /**
     *
     * @param well
     * @return
     */
    public static List<Integer> getNumbersOfTracks(Well well) {
        List<Integer> list = new ArrayList<>();
        well.getWellHasImagingTypeList().stream().map((wellHasImagingType) -> wellHasImagingType.getTrackList()).filter((trackList) -> (!trackList.isEmpty())).forEach((trackList) -> {
            list.add(trackList.size());
        });
        return list;
    }

    /**
     * Generate an array of x values from a list of DoseResponsePairs.
     *
     * @param data The HashMap that maps one x value to replicate y values.
     * @return An array of x values duplicated to the according amount of
     * replicates in the original map.
     */
    public static double[] generateXValues(List<DoseResponsePair> data) {
        List<Double> xValues = new ArrayList<>();
        for (DoseResponsePair entry : data) {
            for (Double response : entry.getResponses()) {
                if (response != null) {
                    xValues.add(entry.getDose());
                }
            }
        }
        return ArrayUtils.toPrimitive(xValues.toArray(new Double[xValues.size()]));
    }

    /**
     * Generate an array of y values from a list of DoseResponsePairs.
     *
     * @param data The HashMap that maps one x value to replicate y values.
     * @return An array of all y values in the original map
     */
    public static double[] generateYValues(List<DoseResponsePair> data) {
        List<Double> yValues = new ArrayList<>();
        for (DoseResponsePair entry : data) {
            for (Double response : entry.getResponses()) {
                if (response != null) {
                    yValues.add(response);
                }
            }
        }
        return ArrayUtils.toPrimitive(yValues.toArray(new Double[yValues.size()]));
    }

    /**
     * Log-transform a concentration according to its concentration unit.
     *
     * @param concentration Set by user in experimental setup screen
     * @param unit The concentration unit (µM, nM...)
     * @return The log-transformed value of the concentration (eg. 1 µm becomes
     * -6)
     */
    public static Double logTransform(Double concentration, String unit) {
        Double value = concentration;
        if (unit.equals("mM")) {
            value *= Math.pow(10, -3);
        } else if (unit.equals("µM")) {
            value *= Math.pow(10, -6);
        } else if (unit.equals("nM")) {
            value *= Math.pow(10, -9);
        }
        return Math.log10(value);
    }

    /**
     * Compute the R² of a non-linear fitting.
     *
     * @param data Log transformed concentrations mapped to replicate velocities
     * (normalized or not)
     * @param resultsholder Contains the best-fit value parameters of the
     * initial or normalized fitting
     * @return
     */
    public static double computeRSquared(List<DoseResponsePair> data, SigmoidFittingResultsHolder resultsholder) {
        double ssRes = 0.0;
        double ssTot = 0.0;
        double[] experimentalYS = generateYValues(data);
        double[] experimentalXS = generateXValues(data);
        double mean = computeMean(experimentalYS);

        for (int i = 0; i < experimentalYS.length; i++) {
            ssTot += (experimentalYS[i] - mean) * (experimentalYS[i] - mean);
            ssRes += Math.pow(experimentalYS[i] - calculatePredictedValue(experimentalXS[i], resultsholder), 2);
        }

        return 1 - (ssRes / ssTot);
    }

    /**
     * Calculate the Y value of a non-linear fit predicted by the best-fit
     * parameters.
     *
     * @param xValue
     * @param resultsholder
     * @return
     */
    private static double calculatePredictedValue(double xValue, SigmoidFittingResultsHolder resultsholder) {
        return (resultsholder.getBottom()
                + (resultsholder.getTop() - resultsholder.getBottom())
                / (1 + Math.pow(10, (resultsholder.getLogEC50() - xValue) * resultsholder.getHillslope())));
    }

    /**
     * Calculate a confidence interval given an estimated value, it's standard
     * error and the quantile which determines the confidence level. A possible
     * extra param is the quantile of the normal distribution. This is 1.96 for
     * a standard 95% confidence interval
     *
     * @param value
     * @param standardError
     *
     * @return The lower and upper boundaries of the confidence interval.
     */
    public static double[] calculateConfidenceIntervalBoundaries(double value, double standardError) {
        double[] result = new double[2];
        result[0] = value - (standardError * 1.96);
        result[1] = value + (standardError * 1.96);
        return result;
    }

    /**
     * Acquire the diagonal covariances for a given data set. These can be used
     * for calculating standard errors.
     *
     * @param covarianceMatrix Contains covariances for the estimated parameters
     * @return Array containing the diagonal matrix values of the covariance
     * matrix.
     */
    public static double[] getDiagonalCovariances(double[][] covarianceMatrix) {
        double[] result = new double[covarianceMatrix.length];
        for (int i = 0; i < covarianceMatrix.length; i++) {
            result[i] = covarianceMatrix[i][i];
        }
        return result;
    }

    /**
     * Calculates the standard errors of the estimated best-fit parameter values
     * of a dose-response estimation.
     *
     * @param data Log transformed concentrations mapped to replicate velocities
     * (normalized or not)
     * @param resultsholder Contains the best-fit value parameters of the
     * initial or normalized fitting
     * @return
     */
    public static double[] calculateStandardErrors(List<DoseResponsePair> data, SigmoidFittingResultsHolder resultsholder) {
        //lenght of the result array is always 4, for the max amount of parameters possible to be estimated in a dose-response fit.
        double[] result = new double[4];
        List<String> constrainedParameters = resultsholder.getConstrainedParameters();
        //calculate residual sum of squares
        double ssRes = 0.0;
        double[] experimentalYS = generateYValues(data);
        double[] experimentalXS = generateXValues(data);
        for (int i = 0; i < experimentalYS.length; i++) {
            ssRes += Math.pow(experimentalYS[i] - calculatePredictedValue(experimentalXS[i], resultsholder), 2);
        }
        //get the diagonal values from the covariance matrix
        double[] covariances = getDiagonalCovariances(resultsholder.getCovariances());

        //the degrees of freedom is the amount of data points minus the number of parameters fit
        int degreesFreedom = experimentalXS.length - covariances.length;

        //second to last and last value of covariances array will always be of logEC50 and hillslope
        //size of distributions map is always equal to lenght of covariances array
        result[2] = Math.sqrt((ssRes / degreesFreedom) * covariances[covariances.length - 2]);
        result[3] = Math.sqrt((ssRes / degreesFreedom) * covariances[covariances.length - 1]);

        //if the parameter was constrained in the fitting, there is no distribution
        //the standard error will be set to 0, so that later it can be correctly displayed in the GUI
        if (constrainedParameters.contains("bottom")) {
            result[0] = 0;
            if (constrainedParameters.contains("top")) {
                result[1] = 0;
            } else {
                result[1] = Math.sqrt((ssRes / degreesFreedom) * covariances[0]);
            }
        } else {
            //calculate the standard error
            result[0] = Math.sqrt((ssRes / degreesFreedom) * covariances[0]);
            if (constrainedParameters.contains("top")) {
                result[1] = 0;
            } else {
                result[1] = Math.sqrt((ssRes / degreesFreedom) * covariances[1]);
            }
        }
        return result;
    }

    /**
     * Maps a string coordinate to its corresponding integer (A-1, B-2 etc.). If
     * the input is already an integer, return it.
     *
     * @param coordinate
     * @return
     */
    public static int checkRowCoordinate(String coordinate) {
        String t = "";
        String s = coordinate.toLowerCase();
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            int n = (int) ch - (int) 'a' + 1;
            t += String.valueOf(n);
        }
        return Integer.parseInt(t);
    }

    /**
     * Maps an integer coordinate to its corresponding string (1-A, 2-B etc.).
     * Unlike above, the input cannot be a string how the method is currently
     * used.
     *
     * @param coordinate
     * @return
     */
    public static String RowCoordinateToString(int coordinate) {
        return (char)(coordinate+'A'-1) + "";
    }
}
