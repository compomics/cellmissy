/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.result.doseresponse.AreaDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInitialController;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInputController;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRNormalizedController;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRResultsController;
import java.util.LinkedHashMap;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;

/**
 * Contains methods used in both the generic and area dose-response analysis.
 * (Having these in a controller does not work because of how the controller
 * hierarchy is organized. Duplicating the methods is bad programming practice.
 *
 * @author Gwendolien Sergeant
 */
public class SharedDoseResponse {

    //fields: interfaces of the subclasses, for correct delegation of methods
    private DRInputController inputController;
    private DRInitialController initialController;
    private DRNormalizedController normalizedController;
    private DRResultsController resultsController;
    //standard hillslope value
    private int standardHillslope;

    /**
     * Getters and setters
     */
    public void setStandardHillslope(int standardHillslope) {
        this.standardHillslope = standardHillslope;
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


    public JFreeChart createDoseResponseChart(LinkedHashMap<Double, List<Double>> dataToPlot, AreaDoseResponseAnalysisGroup analysisGroup, boolean normalized);
}
