/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.AnalysisPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.util.Map;
import javax.swing.ButtonGroup;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * A controller for the actual analysis.
 *
 * @author Paola
 */
@Controller("singleCellAnalysisController")
public class SingleCellAnalysisController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCellAnalysisController.class);
    // model
    // view
    private AnalysisPanel analysisPanel;
    private ChartPanel msdChartPanel;
    // parent controller
    @Autowired
    private SingleCellMainController singleCellMainController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initAnalysisPanel();
    }

    /**
     * The analysis panel view to be passed on parent controller.
     *
     * @return
     */
    public AnalysisPanel getAnalysisPanel() {
        return analysisPanel;
    }

    /**
     * Initialize main view.
     */
    private void initAnalysisPanel() {
        // make a new panel
        analysisPanel = new AnalysisPanel();

        msdChartPanel = new ChartPanel(null);
        msdChartPanel.setOpaque(false);

        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();

        /**
         * Add action listeners
         */
        // add view to parent panel
        singleCellMainController.getSingleCellAnalysisPanel().getAnalysisParentPanel().add(analysisPanel, gridBagConstraints);
    }

    /**
     * Show the MSD analysis for all the conditions of the experiment.
     */
    public void showMSD() {
        // create a new chart
        JFreeChart msdChart = ChartFactory.createXYLineChart("Experiment - MSD", "time-lag", "MSD (µm²)",
                createMsdCollection(), PlotOrientation.VERTICAL, true, true, false);
        JFreeChartUtils.setupConditionsChart(msdChart, singleCellMainController.getPlateConditionList(), true, true);
        msdChartPanel.setChart(msdChart);
    }

    /**
     * Private classes and methods.
     *
     */
    /**
     * Generate x-y collection for MSD values.
     *
     * @return the collection.
     */
    private XYSeriesCollection createMsdCollection() {
        XYSeriesCollection collection = new XYSeriesCollection();
        Map<PlateCondition, SingleCellConditionDataHolder> preProcessingMap = singleCellMainController.getPreProcessingMap();
        preProcessingMap.values().stream().map((cellConditionDataHolder) -> {
            double[][] msdArray = cellConditionDataHolder.getMsdArray();
            XYSeries xYSeries = JFreeChartUtils.generateXYSeries(msdArray);
            xYSeries.setKey(cellConditionDataHolder.getPlateCondition().toString());
            return xYSeries;
        }).forEach((xYSeries) -> {
            collection.addSeries(xYSeries);
        });
        return collection;
    }
}
