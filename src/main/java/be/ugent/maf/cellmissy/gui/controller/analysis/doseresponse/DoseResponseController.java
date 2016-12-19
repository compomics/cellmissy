/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.analysis.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Contains methods used in both the generic and area dose-response analysis.
 * (Having these in a controller does not work because of how the controller
 * hierarchy is organized. Duplicating the methods is bad programming practice.
 *
 * @author Gwendolien Sergeant
 */
public abstract class DoseResponseController {

    //model
    protected JTable dataTable;
    protected boolean firstFitting;
    private int standardHillslope;
    //view
    protected DRPanel dRPanel;
    //services
    protected GridBagConstraints gridBagConstraints;

    /**
     * Getters and setters
     */
    public void setStandardHillslope(int standardHillslope) {
        this.standardHillslope = standardHillslope;
    }

    public boolean isFirstFitting() {
        return firstFitting;
    }

    /**
     * Set wheter the data needs to be fit for a first time. Is set to true when
     * creating a analysis group, set to false after performing first fit.
     *
     * @param firstFitting
     */
    public void setFirstFitting(boolean firstFitting) {
        this.firstFitting = firstFitting;
    }

    public DRPanel getDRPanel() {
        return dRPanel;
    }

    protected void resetOnCancel() {
        dataTable.setModel(new DefaultTableModel());
        dRPanel.getGraphicsDRParentPanel().removeAll();
    }

    /**
     * update information message above table. Message will be different for
     * each subview
     *
     * @param messageToShow
     */
    protected void updateTableInfoMessage(String messageToShow) {
        dRPanel.getTableInfoLabel().setText(messageToShow);
    }

    /**
     * When switching to a different subview, change the model for the main
     * table.
     */
    public void updateModelInTable(NonEditableTableModel tableModel) {
        dataTable.setModel(tableModel);
    }

    /**
     * Perform fitting according to user specifications. This method will check
     * how many parameters have been constrained and pick the right fitter
     * class.
     *
     * @param sigmoidFitter The type of fitter to be used.
     * @param dataToFit The data (log-transformed concentration - velocity)
     * @param resultsHolder The class that will contain the results from fitting
     * @param bottomConstrained Double if user constrains, otherwise null
     * @param topConstrained Double if user constrains, otherwise null
     *
     */
    public void performFitting(SigmoidFitter sigmoidFitter, LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrained, Double topConstrained) {
        if (topConstrained != null) {
            if (bottomConstrained != null) {
                sigmoidFitter.fitBotTopConstrain(dataToFit, resultsHolder, bottomConstrained, topConstrained, standardHillslope);
            } else {
                sigmoidFitter.fitTopConstrain(dataToFit, resultsHolder, topConstrained, standardHillslope);
            }
        } else if (bottomConstrained != null) {
            sigmoidFitter.fitBotConstrain(dataToFit, resultsHolder, bottomConstrained, standardHillslope);
        } else {
            sigmoidFitter.fitNoConstrain(dataToFit, resultsHolder, standardHillslope);
        }
    }

    protected XYSeries simulateData(SigmoidFittingResultsHolder resultsholder) {
        return JFreeChartUtils.createFittedDataset(resultsholder.getTop(), resultsholder.getBottom(), resultsholder.getHillslope(), resultsholder.getLogEC50());
    }

    public JFreeChart createDoseResponseChart(LinkedHashMap<Double, List<Double>> dataToPlot, DoseResponseAnalysisGroup analysisGroup, boolean normalized) {
        //setup scatter data of experimental concentrations/slopes, renderer and axis
        XYSeriesCollection experimentalData = new XYSeriesCollection();
        XYSeries scatterXYSeries = JFreeChartUtils.generateXYSeries(AnalysisUtils.generateXValues(dataToPlot), AnalysisUtils.generateYValues(dataToPlot));
        scatterXYSeries.setKey("Experimental data");
        experimentalData.addSeries(scatterXYSeries);

        // Create the line data, renderer, and axis
        XYSeriesCollection fitting = new XYSeriesCollection();
        // create xy series of simulated data from the parameters from the fitting
        SigmoidFittingResultsHolder resultsHolder = analysisGroup.getDoseResponseAnalysisResults().getFittingResults(normalized);
        XYSeries fittingData = simulateData(resultsHolder);
        fittingData.setKey("Fitting");
        fitting.addSeries(fittingData);

        XYPlot plot = JFreeChartUtils.setupDoseResponseDatasets(experimentalData, fitting, normalized);

        // show the r squared value
        plot.addAnnotation(new XYTextAnnotation("R2=" + AnalysisUtils.roundThreeDecimals(AnalysisUtils.computeRSquared(dataToPlot, resultsHolder)), -4, 10.0));

        // Create the chart with the plot and no legend
        JFreeChart chart = new JFreeChart("Title", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        String title = "";
        if (normalized) {
            title = "Normalized fitting";
        } else {
            title = "Initial fitting";
        }
        JFreeChartUtils.setupDoseResponseChart(chart, title);
        return chart;
    }

    /**
     * Plots the fitted data.
     */
    public void plotDoseResponse(ChartPanel chartPanel, JPanel subviewPanel, LinkedHashMap<Double, List<Double>> dataToPlot, DoseResponseAnalysisGroup analysisGroup, boolean normalized) {
        JFreeChart doseResponseChart = createDoseResponseChart(dataToPlot, normalized);
        chartPanel.setChart(doseResponseChart);
        //add chartpanel to graphics parent panel and repaint
        subviewPanel.add(chartPanel, gridBagConstraints);
        dRPanel.getGraphicsDRParentPanel().repaint();
        dRPanel.getGraphicsDRParentPanel().revalidate();
    }

    /**
     * Abstract methods
     */
    protected abstract void initMainView();

    protected abstract void initFirstFitting();

    protected abstract void calculateStatistics();

    protected abstract List<Double> getConstrainValues(boolean normalized);

    protected abstract JFreeChart createDoseResponseChart(LinkedHashMap<Double, List<Double>> dataToPlot, boolean normalized);

    public abstract LinkedHashMap<Double, List<Double>> getDataToFit(boolean normalized);
}
