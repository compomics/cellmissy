/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.area;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.result.doseresponse.AreaDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRNormalizedController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRNormalizedPlotPanel;
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
 * Controller for normalized plot panel of dose-response analysis
 *
 * @author Gwendolien
 */
@Controller("areaDRNormalizedController")
public class AreaDRNormalizedController extends DRNormalizedController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AreaDRNormalizedController.class);

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
     * When changing view from input panel after creating new analysis group:
     * make dataset, do fitting and plot according to default parameters.
     */
    @Override
    public void initDRNormalizedData() {
        //set constrain combo boxes to standard setting: means
        dRNormalizedPlotPanel.getBottomComboBox().setSelectedIndex(0);
        dRNormalizedPlotPanel.getTopComboBox().setSelectedIndex(0);
        //set initial parameters
        dRNormalizedPlotPanel.getBottomTextField().setText(AnalysisUtils.roundTwoDecimals(Collections.min(computeMeans(doseResponseController.getdRAnalysisGroup().getVelocitiesMap().values()))).toString());
        dRNormalizedPlotPanel.getTopTextField().setText(AnalysisUtils.roundTwoDecimals(Collections.max(computeMeans(doseResponseController.getdRAnalysisGroup().getVelocitiesMap().values()))).toString());
        //LogTransform concentrations and perform initial normalization (mean values)
        dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup());
        //create and set the table model for the top panel table (dependent on normalization)
        setTableModel(createTableModel(dataToFit));
        //Perform initial curve fitting (standard hillslope, no constraints)
        doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(true), null, null);

    }

    /**
     * Initialize view
     */
    @Override
    protected void initDRNormalizedPanel() {
        dRNormalizedPlotPanel = new DRNormalizedPlotPanel();
        //init chart panel
        normalizedChartPanel = new ChartPanel(null);
        normalizedChartPanel.setOpaque(false);

        /**
         * Action listeners for buttons
         */
        /**
         * The combo box determines how the normalization is done. Bottom combo
         * box defines what the value for 0% response is.
         */
        dRNormalizedPlotPanel.getBottomComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = (String) dRNormalizedPlotPanel.getBottomComboBox().getSelectedItem();
                switch (value) {
                    case "Smallest Mean Value":
                        dRNormalizedPlotPanel.getBottomTextField().setEditable(false);
                        dRNormalizedPlotPanel.getBottomTextField().setText(AnalysisUtils.roundTwoDecimals(Collections.min(computeMeans(doseResponseController.getdRAnalysisGroup().getVelocitiesMap().values()))).toString());
                        break;
                    case "Smallest Median Value":
                        dRNormalizedPlotPanel.getBottomTextField().setEditable(false);
                        dRNormalizedPlotPanel.getBottomTextField().setText(AnalysisUtils.roundTwoDecimals(Collections.min(computeMedians(doseResponseController.getdRAnalysisGroup().getVelocitiesMap().values()))).toString());
                        break;
                    case "Other Value":
                        dRNormalizedPlotPanel.getBottomTextField().setText("");
                        dRNormalizedPlotPanel.getBottomTextField().setEditable(true);
                        break;
                }
            }
        });

        /**
         * The combo box determines how the normalization is done. Top combo box
         * defines what the value for 100% response is.
         */
        dRNormalizedPlotPanel.getTopComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String choice = (String) dRNormalizedPlotPanel.getTopComboBox().getSelectedItem();
                switch (choice) {
                    case "Largest Mean Value":
                        dRNormalizedPlotPanel.getTopTextField().setEditable(false);
                        dRNormalizedPlotPanel.getTopTextField().setText(AnalysisUtils.roundTwoDecimals(Collections.max(computeMeans(doseResponseController.getdRAnalysisGroup().getVelocitiesMap().values()))).toString());
                        break;
                    case "Largest Median Value":
                        dRNormalizedPlotPanel.getTopTextField().setEditable(false);
                        dRNormalizedPlotPanel.getTopTextField().setText(AnalysisUtils.roundTwoDecimals(Collections.max(computeMeans(doseResponseController.getdRAnalysisGroup().getVelocitiesMap().values()))).toString());
                        break;
                    case "Other Value":
                        dRNormalizedPlotPanel.getTopTextField().setText("");
                        dRNormalizedPlotPanel.getTopTextField().setEditable(true);
                        break;
                }
            }
        });

        /**
         * If selected, the curve fit 'bottom' parameter will be constrained to
         * zero. This zero is defined by the text field value during
         * normalization.
         *
         */
        dRNormalizedPlotPanel.getBottomConstrainCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    bottomConstrainValue = 0.0;
                } else {
                    bottomConstrainValue = null;
                }
            }
        });

        /**
         * If selected, the curve fit 'top' parameter will be constrained to
         * 100. This is defined by the text field value during normalization.
         *
         */
        dRNormalizedPlotPanel.getTopConstrainCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    topConstrainValue = 100.0;

                } else {
                    topConstrainValue = null;
                }
            }
        });

        /**
         * Re-normalize and plot new dose-response graph, taking into account
         * any choices made by the user.
         */
        dRNormalizedPlotPanel.getPlotGraphButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup());
                setTableModel(createTableModel(dataToFit));
                doseResponseController.updateModelInTable(tableModel);
                doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(true), bottomConstrainValue, topConstrainValue);
                doseResponseController.plotDoseResponse(normalizedChartPanel, dRNormalizedPlotPanel.getDoseResponseChartParentPanel(), dataToFit, doseResponseController.getdRAnalysisGroup(), true);
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
     * the normalized replicate velocites
     */
    private LinkedHashMap<Double, List<Double>> prepareFittingData(AreaDoseResponseAnalysisGroup dRAnalysisGroup) {
        LinkedHashMap<Double, List<Double>> result = new LinkedHashMap<>();

        //!! control concentrations (10 * lower than lowest treatment conc) also need to be added
        List<List<Double>> allVelocities = new ArrayList<>();
        List<Double> allLogConcentrations = new ArrayList<>();

        //put concentrations of treatment to analyze (control not included!) in list
        LinkedHashMap<Double, String> nestedMap = dRAnalysisGroup.getConcentrationsMap().get(dRAnalysisGroup.getTreatmentToAnalyse());
        for (Double concentration : nestedMap.keySet()) {
            String unit = nestedMap.get(concentration);

            Double logConcentration = AnalysisUtils.logTransform(concentration, unit);
            allLogConcentrations.add(logConcentration);
        }

        Double lowestLogConc = Collections.min(allLogConcentrations);
        //iterate through conditions
        int x = 0;
        for (PlateCondition plateCondition : dRAnalysisGroup.getVelocitiesMap().keySet()) {
            List<Double> replicateVelocities = dRAnalysisGroup.getVelocitiesMap().get(plateCondition);

            //normalize each value
            List<Double> normalizedVelocities = new ArrayList<>();
            for (Double value : replicateVelocities) {
                normalizedVelocities.add(normalize(value));
            }
            //check if this platecondition is the control
            for (Treatment treatment : plateCondition.getTreatmentList()) {
                if (treatment.getTreatmentType().getName().contains("ontrol")) {
                    allLogConcentrations.add(x, lowestLogConc - 1.0);
                }
            }

            allVelocities.add(normalizedVelocities);
            x++;
        }
        for (int i = 0; i < allLogConcentrations.size(); i++) {
            result.put(allLogConcentrations.get(i), allVelocities.get(i));
        }

        return result;
    }

}
