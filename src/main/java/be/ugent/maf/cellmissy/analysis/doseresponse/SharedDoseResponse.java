/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.result.doseresponse.AreaDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInitialController;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInputController;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRNormalizedController;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRResultsController;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.util.LinkedHashMap;
import java.util.List;
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
public class SharedDoseResponse {

    //fields: abstracts of the subclasses, for correct delegation of methods
    private final DRInputController inputController;
    private final DRInitialController initialController;
    private final DRNormalizedController normalizedController;
    private final DRResultsController resultsController;
    //standard hillslope value
    private int standardHillslope;
    private DoseResponseAnalysisGroup doseResponseAnalysisGroup;

    /**
     * Getters and setters
     */
    public void setStandardHillslope(int standardHillslope) {
        this.standardHillslope = standardHillslope;
    }
    
    public SharedDoseResponse(DRInputController inputController, DRInitialController initialController, DRNormalizedController normalizedController, DRResultsController resultsController) {
        this.inputController = inputController;
        this.initialController = initialController;
        this.normalizedController = normalizedController;
        this.resultsController = resultsController;
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
    
    public XYSeries simulateData(boolean normalized) {
        SigmoidFittingResultsHolder resultsholder = dRAnalysisGroup.getDoseResponseAnalysisResults().getFittingResults(normalized);
        return JFreeChartUtils.createFittedDataset(resultsholder.getTop(), resultsholder.getBottom(), resultsholder.getHillslope(), resultsholder.getLogEC50());
    }


    public JFreeChart createDoseResponseChart(LinkedHashMap<Double, List<Double>> dataToPlot, AreaDoseResponseAnalysisGroup analysisGroup, boolean normalized) {
        //setup scatter data of experimental concentrations/slopes, renderer and axis
        XYSeriesCollection experimentalData = new XYSeriesCollection();
        XYSeries scatterXYSeries = JFreeChartUtils.generateXYSeries(AnalysisUtils.generateXValues(dataToPlot), AnalysisUtils.generateYValues(dataToPlot));
        scatterXYSeries.setKey("Experimental data");
        experimentalData.addSeries(scatterXYSeries);

        // Create the line data, renderer, and axis
        XYSeriesCollection fitting = new XYSeriesCollection();
        // create xy series of simulated data from the parameters from the fitting
        XYSeries fittingData = simulateData(normalized);
        fittingData.setKey("Fitting");
        fitting.addSeries(fittingData);

        XYPlot plot = JFreeChartUtils.setupDoseResponseDatasets(experimentalData, fitting, normalized);

        // show the r squared value
        SigmoidFittingResultsHolder resultsholder = doseResponseAnalysisGroup.getDoseResponseAnalysisResults().getFittingResults(normalized);
        plot.addAnnotation(new XYTextAnnotation("R2=" + AnalysisUtils.roundThreeDecimals(AnalysisUtils.computeRSquared(dataToPlot, resultsholder)), -4, 10.0));

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
}
