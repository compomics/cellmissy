/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInitialController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInitialPlotPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author CompOmics Gwen
 */
@Controller("genericDRInitialController")
public class GenericDRInitialController extends DRInitialController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericDRInitialController.class);

    //model
    //the data to fit, essentially the data that was loaded at start
    private List<DoseResponsePair> dataToFit;
    //view
    //parent controller
    @Autowired
    private GenericDoseResponseController doseResponseController;

    @Override
    public void initDRInitialData() {
        //Log transform concentrations if needed, keeping responses the same
        dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup().getDoseResponseData());
        //create and set the table model for the top panel table
        setTableModel(doseResponseController.updateTableModel(createTableModel(dataToFit)));
        //Fit data according to initial parameters (standard hillslope, no constraints)
        doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(false), null, null);
    }

    /**
     * Initialize view.
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

    private List<DoseResponsePair> prepareFittingData(List<DoseResponsePair> doseResponseData) {
        if (doseResponseController.getLogTransform()) {
            //since input file is only allowed to contain numbers, we can assume the user will assign the value 0.0 to the control dose
            //to find a log value for control, go through all doses, find the lowest one
            //control dose will be log-transformed value of this dose minus 1.0
            List<DoseResponsePair> result = new ArrayList<>();
            Double lowestDose = 10000000.00;
            for (DoseResponsePair row : doseResponseData) {
                if (row.getDose() < lowestDose && !row.getDose().equals(0.0)) {
                    lowestDose = row.getDose();
                }
            }
            //iterate through DRPairs 
            for (DoseResponsePair row : doseResponseData) {
                Double dose;
                //transform the concentration if needed 
                if (row.getDose().equals(0.0)) {
                    dose = (AnalysisUtils.logTransform(lowestDose, "M")) - 1.0;
                } else {
                    dose = AnalysisUtils.logTransform(row.getDose(), "M");
                }
                //add new DRPair to list
                result.add(new DoseResponsePair(dose, row.getResponses()));
            }
            return result;

        } else {
            return doseResponseData;
        }
    }
}
