/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;

/**
 *
 * @author Paola Masuzzo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class StatisticsCalculatorTest {    
    
    private static double[] xData;
    private static double[] yData;
    
    @Autowired
    private StatisticsCalculator mann_Whitney_Statistics; 
    

    /**
     * SetUp data for test
     */
    @BeforeClass
    public static void setUpData() {
        double[] data1 = {301.687, 310.946, 283.103, 235.029, 252.831, 213.888};
        double[] data2 = {271.138, 243.779, 259.783, 253.797, 262.709};
        xData = data1;
        yData = data2;
    }

    /**
     *
     */
    @Test
    public void testMannWhitney() {
        double pValue = mann_Whitney_Statistics.executeStatisticalTest(xData, yData);
        Assert.assertEquals(0.855, pValue, 0.001);        
    }
}
