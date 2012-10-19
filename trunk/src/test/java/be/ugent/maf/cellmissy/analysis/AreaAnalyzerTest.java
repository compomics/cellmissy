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
public class AreaAnalyzerTest {

    @Autowired
    private LinearRegressor linearRegressor;
    private static Double[][] data;

    /**
     * SetUp data for test
     */
    @BeforeClass
    public static void setUpData() {
        data = new Double[100][100];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = Double.valueOf(i + 1);
            }
        }
    }

    /**
     * Test Linear Regressor Class
     */
    @Test
    public void testLinearRegressor() {
        List<Double> linearModel = linearRegressor.estimateLinearModel(data);
        assertNotNull(linearModel);
        assertTrue(linearModel.size() == 2);
        Double rSquared = linearModel.get(1);
        Double slope = linearModel.get(0);
        assertEquals(1.0, rSquared);
        assertEquals(1.0, slope);
    }
}
