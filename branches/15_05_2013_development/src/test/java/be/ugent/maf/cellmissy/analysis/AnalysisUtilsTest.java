/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import static junit.framework.Assert.assertEquals;
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
        assertEquals(4.0, median);
    }

    @Test
    public void testMedianForEvenDatasets() {
        double[] data = {4, 5, 7, 2, 1, 8};
        double median = AnalysisUtils.computeMedian(data);
        assertEquals(4.5, median);
    }

    @Test
    public void testStandardDeviation() {
        double[] data = {13, 23, 12, 44, 55};
        double standardDeviation = AnalysisUtils.computeStandardDeviation(data);
        Double rounded = AnalysisUtils.roundThreeDecimals(standardDeviation);
        assertEquals(17.212, rounded);
    }

    @Test
    public void testQuantilesEstimation() {
        double[] data = {13, 23, 12, 44, 55};
        double lowerQuartile = AnalysisUtils.estimateQuantile(data, 25);
        assertEquals(13.0, lowerQuartile);
        double upperQuartile = AnalysisUtils.estimateQuantile(data, 75);
        assertEquals(44.0, upperQuartile);
    }
}
