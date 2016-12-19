/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.area;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.result.doseresponse.AreaDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInitialController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInitialPlotPanel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for initial plot panel of dose-response analysis.
 *
 * @author Gwendolien
 */
@Controller("areaDRInitialController")
public class AreaDRInitialController extends DRInitialController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AreaDRInitialController.class);
    //model
    private LinkedHashMap<Double, List<Double>> dataToFit;
    //view: in super class
    // parent controller
    @Autowired
    private AreaDoseResponseController doseResponseController;

    /**
     * Getters and setters
     *
     * @return
     */
    public LinkedHashMap<Double, List<Double>> getDataToFit() {
        return dataToFit;
    }

    /**
     * When changing view from input panel: make dataset, do fitting and plot
     * according to starting parameters.
     */
    @Override
    public void initDRInitialData() {
        //Log transform concentrations, keeping slopes the same
        dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup());
        //create and set the table model for the top panel table
        setTableModel(createTableModel(dataToFit));
        //Fit data according to initial parameters (standard hillslope, no constraints)
        doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(false), null, null);

    }

    /**
     * Initialize view
     */
    @Override
    protected void initDRInitialPanel() {
        dRInitialPlotPanel = new DRInitialPlotPanel();

        //init chart panel
        initialChartPanel = new ChartPanel(null);
        initialChartPanel.setOpaque(false);
        /**
         * Action listeners for buttons
         */

        /**
         * If selected, text field to enter value for parameter constraining
         * will be taken into account on plotting.
         */
        dRInitialPlotPanel.getBottomCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    bottomConstrainValue = 0.0;
                } else {
                    bottomConstrainValue = null;
                }
            }
        });

        dRInitialPlotPanel.getTopCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    topConstrainValue = 0.0;
                } else {
                    topConstrainValue = null;
                }
            }
        });

        /**
         * Perform fitting and plot new dose-response graph, taking into account
         * any choices made by the user.
         */
        dRInitialPlotPanel.getPlotGraphButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (bottomConstrainValue != null) {
                    bottomConstrainValue = Double.parseDouble(dRInitialPlotPanel.getBottomTextField().getText());
                }
                if (topConstrainValue != null) {
                    topConstrainValue = Double.parseDouble(dRInitialPlotPanel.getTopTextField().getText());
                }
                doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(false), bottomConstrainValue, topConstrainValue);
                //Plot fitted data in dose-response curve, along with R² annotation
                doseResponseController.plotDoseResponse(initialChartPanel, dRInitialPlotPanel.getDoseResponseChartParentPanel(), dataToFit, doseResponseController.getdRAnalysisGroup(), false);
                //Calculate new statistics
                doseResponseController.calculateStatistics();
            }
        });
    }

    /**
     * Private methods
     */
    /**
     * Prepare data for fitting starting from the analysis group.
     *
     * @param dRAnalysisGroup
     * @return LinkedHashMap That maps the concentration (log-transformed!) to
     * the replicate velocities
     */
    private LinkedHashMap<Double, List<Double>> prepareFittingData(AreaDoseResponseAnalysisGroup dRAnalysisGroup) {
        LinkedHashMap<Double, List<Double>> result = new LinkedHashMap<>();

        List<List<Double>> allVelocities = new ArrayList<>();
        List<Double> allLogConcentrations = new ArrayList<>();

        //put concentrations of treatment to analyze (control not included!) in list
        LinkedHashMap<Double, String> nestedMap = dRAnalysisGroup.getConcentrationsMap().get(dRAnalysisGroup.getTreatmentToAnalyse());
        for (Double concentration : nestedMap.keySet()) {
            //key can only be linked with a single value, if one concentration is setup to have more than one associated concentration unit, only the last will remain
            String unit = nestedMap.get(concentration);

            Double logConcentration = AnalysisUtils.logTransform(concentration, unit);
            allLogConcentrations.add(logConcentration);
        }

        Double lowestLogConc = Collections.min(allLogConcentrations);
        //iterate through conditions
        int x = 0;
        for (PlateCondition plateCondition : dRAnalysisGroup.getVelocitiesMap().keySet()) {
            List<Double> replicateVelocities = dRAnalysisGroup.getVelocitiesMap().get(plateCondition);

            //check if this platecondition is the control
            for (Treatment treatment : plateCondition.getTreatmentList()) {
                if (treatment.getTreatmentType().getName().contains("ontrol")) {
                    allLogConcentrations.add(x, lowestLogConc - 1.0);
                }
            }

            allVelocities.add(replicateVelocities);
            x++;
        }

        for (int i = 0; i < allVelocities.size(); i++) {
            result.put(allLogConcentrations.get(i), allVelocities.get(i));
        }
        return result;
    }

}
