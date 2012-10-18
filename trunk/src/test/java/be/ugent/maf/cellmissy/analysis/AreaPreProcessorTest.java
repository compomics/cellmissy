/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.*;

/**
 *
 * @author Paola Masuzzo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class AreaPreProcessorTest {

    @Autowired
    private KernelDensityEstimator kernelDensityEstimator;
    @Autowired
    private OutliersHandler outliersHandler;
    private static double[] data;

    /**
     * set up data for test
     */
    @BeforeClass
    public static void setUpData() {
        data = new double[100];
        for (int i = 0; i < data.length; i++) {
            data[i] = i + 1;
        }
    }

    /**
     * Test Kernel Density Estimator class
     */
    @Test
    public void testKernelDensityEstimation() {

        List<double[]> estimateDensityFunction = kernelDensityEstimator.estimateDensityFunction(data);
        double[] randomSamples = estimateDensityFunction.get(0);
        double[] estimatedValues = estimateDensityFunction.get(1);
        assertTrue(randomSamples.length == 512);
        assertTrue(estimatedValues.length == 512);

        for (int i = 0; i < estimatedValues.length; i++) {
            System.out.println("random sample: " + randomSamples[i]);
            System.out.println("density value: " + estimatedValues[i]);
        }

        System.out.println("mean random sample: " + AnalysisUtils.computeMean(randomSamples));
        System.out.println("mean density function: " + AnalysisUtils.computeMean(estimatedValues));
    }

    /**
     * Test Outliers Detection
     */
    @Test
    public void testOutlierDetection() {
        List<double[]> handleOutliers = outliersHandler.handleOutliers(data);
        double[] ouliers = handleOutliers.get(0);
        assertNotNull(ouliers);
        if (ouliers.length == 0) {
            assertEquals(data.length, handleOutliers.get(1).length);
        }
    }
}
