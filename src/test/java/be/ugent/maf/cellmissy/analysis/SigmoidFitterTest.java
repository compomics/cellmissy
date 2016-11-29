/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Gwendolien
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mySpringXMLConfig.xml")
public class SigmoidFitterTest {

    @Autowired
    private SigmoidFitter sigmoidFitter;
    private final static LinkedHashMap<Double, List<Double>> dataToFit = new LinkedHashMap<>();
    private final SigmoidFittingResultsHolder resultsHolder = new SigmoidFittingResultsHolder();

    /**
     * SetUp data for test
     */
    @BeforeClass
    public static void setupData() {
        List<Double> list = new ArrayList<>();
        list.add(10.0);
        dataToFit.put(-8.0, list);
        list = new ArrayList<>();
        list.add(10.0);
        dataToFit.put(-7.0, list);
        list = new ArrayList<>();
        list.add(20.0);
        dataToFit.put(-6.0, list);
        list = new ArrayList<>();
        list.add(30.0);
        dataToFit.put(-5.0, list);
        list = new ArrayList<>();
        list.add(40.0);
        dataToFit.put(-4.0, list);
        list = new ArrayList<>();
        list.add(50.0);
        dataToFit.put(-3.0, list);
        list = new ArrayList<>();
        list.add(50.0);
        dataToFit.put(-2.0, list);
    }

    @Test
    public void testFitNoConstrain() {
        sigmoidFitter.fitNoConstrain(dataToFit, resultsHolder,1);
        double logEC50 = resultsHolder.getLogEC50();
        double hillslope = resultsHolder.getHillslope();
        double top = resultsHolder.getTop();
        double bottom = resultsHolder.getBottom();
        assertEquals(10, bottom, 5);
        assertEquals(50, top, 5);
        assertEquals(-5.0, logEC50, 0.1);
        assertEquals(0.5, hillslope, 0.15);
    }
}
