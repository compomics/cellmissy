/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import org.junit.Assert;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Paola Masuzzo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class AnalysisUtilsTest {

    @Test
    public void testMedianForOddDatasets() {
        double[] data = {4, 5, 7, 2, 1};
        double median = AnalysisUtils.computeMedian(data);
        Assert.assertEquals(4.0, median, 0);
    }

    @Test
    public void testMedianForEvenDatasets() {
        double[] data = {4, 5, 7, 2, 1, 8};
        double median = AnalysisUtils.computeMedian(data);
        Assert.assertEquals(4.5, median, 0);
    }

    @Test
    public void testStandardDeviation() {
        double[] data = {13, 23, 12, 44, 55};
        double standardDeviation = AnalysisUtils.computeStandardDeviation(data);
        Double rounded = AnalysisUtils.roundThreeDecimals(standardDeviation);
        Assert.assertEquals(17.212, rounded, 0);
    }

    @Test
    public void testQuantilesEstimation() {
        double[] data = {13, 23, 12, 44, 55};
        double lowerQuartile = AnalysisUtils.estimateQuantile(data, 25);
        Assert.assertEquals(13.0, lowerQuartile, 0);
        double upperQuartile = AnalysisUtils.estimateQuantile(data, 75);
        Assert.assertEquals(44.0, upperQuartile, 0);
    }

    @Test
    public void testGetMaxOfAList() {
        List<Double[]> data = new ArrayList<>();
        data.add(new Double[]{15.0, 12.54});
        data.add(new Double[]{80.0, 1.23});
        data.add(new Double[]{66.0, 80.15});
        Double max = AnalysisUtils.getMaxOfAList(data);
        Assert.assertEquals(80.15, max, 0);
    }

    @Test
    public void testLogTransform() {
        Double logConc = AnalysisUtils.logTransform(1.0, "µM");
        Assert.assertEquals(-6.0, logConc, 0);
        logConc = AnalysisUtils.logTransform(1.0, "mM");
        Assert.assertEquals(-3.0, logConc, 0);
        logConc = AnalysisUtils.logTransform(10.0, "nM");
        Assert.assertEquals(-8.0, logConc, 0);
    }
    
    @Test
    public void testGetDiagonalCovariances() {
        double[][] matrix = new double[4][4];
        for (int i = 0; i < matrix.length; i++) {
            double[] column = new double[]{1,2,3,4};
            matrix[i] = column;
        }
        double[] covariances = AnalysisUtils.getDiagonalCovariances(matrix);
        Assert.assertEquals(1.0, covariances[0], 0);
        Assert.assertEquals(2.0, covariances[1], 0);
        Assert.assertEquals(3.0, covariances[2], 0);
        Assert.assertEquals(4.0, covariances[3], 0);
    }
}
