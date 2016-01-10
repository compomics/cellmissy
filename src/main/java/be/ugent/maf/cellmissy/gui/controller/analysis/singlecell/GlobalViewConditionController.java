/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This class takes care of rendering a global view showing cell trajectories
 * for all wells of a condition.
 *
 * @author Paola
 */
@Controller("globalViewConditionController")
public class GlobalViewConditionController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlobalViewConditionController.class);
    // model
    // view
    private List<ChartPanel> coordinatesChartPanels;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // child controllers    
    // services

    /**
     * Initialize controller
     */
    public void init() {
        coordinatesChartPanels = new ArrayList<>();

    }

}
