/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.SingleCutOffPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of the logic for a single cut-off filtering on
 * single cell trajectories.
 *
 * @author Paola
 */
@Controller("singleCutOffFilteringController")
public class SingleCutOffFilteringController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCutOffFilteringController.class);
    // model
    private double cutOff;
    // view
    private SingleCutOffPanel singleCutOffPanel;
    private ChartPanel rawKdeChartPanel;
    private ChartPanel filteredKdeChartPanel;
    // parent controller
    @Autowired
    private FilteringController filteringController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize the controller.
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initMainView();
         // initialize the other views
        initOtherViews();
    }

    public SingleCutOffPanel getSingleCutOffPanel() {
        return singleCutOffPanel;
    }

    /**
     * Plot the raw KDE for track displacements.
     *
     * @param plateCondition
     */
    public void plotRawKdeSingleCutOff(PlateCondition plateCondition) {
        SingleCellConditionDataHolder conditionDataHolder = filteringController.getConditionDataHolder(plateCondition);
        List<List<double[]>> estimateRawDensityFunction = filteringController.estimateRawDensityFunction(conditionDataHolder);
        XYSeriesCollection densityFunction = filteringController.generateDensityFunction(conditionDataHolder, estimateRawDensityFunction);
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(conditionDataHolder, densityFunction, "raw KDE track displ", "track displ", true);
        rawKdeChartPanel.setChart(densityChart);
    }

    /**
     * Initialize the main view.
     */
    private void initMainView() {
        // make a new view
        singleCutOffPanel = new SingleCutOffPanel();
        
        // add view to parent container
        filteringController.getFilteringPanel().getSingleCutOffParentPanel().add(singleCutOffPanel, gridBagConstraints);
    }
    
     // initialize the other views
    private void initOtherViews() {
        rawKdeChartPanel = new ChartPanel(null);
        rawKdeChartPanel.setOpaque(false);
        filteredKdeChartPanel = new ChartPanel(null);
        filteredKdeChartPanel.setOpaque(false);

        // add chart panels to parent containers
        singleCutOffPanel.getRawPlotParentPanel().add(rawKdeChartPanel, gridBagConstraints);
        singleCutOffPanel.getFilteredPlotParentPanel().add(filteredKdeChartPanel, gridBagConstraints);
    }

}
