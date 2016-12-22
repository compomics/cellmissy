/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInitialController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInitialPlotPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
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
    private LinkedHashMap<Double, List<Double>> dataToFit;
    //view
    //parent controller
    @Autowired
    private GenericDoseResponseController doseResponseController;

    @Override
    public void initDRInitialData() {
        //Log transform concentrations, keeping slopes the same
        dataToFit = doseResponseController.getdRAnalysisGroup().getDoseResponseData();
        //create and set the table model for the top panel table
        setTableModel(createTableModel(dataToFit));
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
}
