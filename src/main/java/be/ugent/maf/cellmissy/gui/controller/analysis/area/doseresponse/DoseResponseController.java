/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.analysis.area.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.analysis.area.AreaMainController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;

import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for area dose-response analysis
 *
 * @author Gwendolien
 */
@Controller("doseResponseController")
public class DoseResponseController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DoseResponseController.class);
    //model
    private JTable dataTable;
    private int standardHillslope;
    private DoseResponseAnalysisGroup dRAnalysisGroup;
    //view
    private DRPanel dRPanel;
    // parent controller
    @Autowired
    private AreaMainController areaMainController;
    // child controller
    private DRInputController dRInputController;
    private DRInitialController dRInitialController;
    private DRNormalizedController dRNormalizedController;
    private DRResultsController dRResultsController;
    // services
    @Autowired
    private SigmoidFitter sigmoidFitter;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init view
        initMainView();
        //init child controllers
        dRInputController.init();
        dRInitialController.init();
        dRNormalizedController.init();
        dRResultsController.init();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public DRPanel getDRPanel() {
        return dRPanel;
    }

    public int getStandardHillslope() {
        return standardHillslope;
    }

    public void setStandardHillslope(int standardHillslope) {
        this.standardHillslope = standardHillslope;
    }

    public void setdRAnalysisGroup(DoseResponseAnalysisGroup dRAnalysisGroup) {
        this.dRAnalysisGroup = dRAnalysisGroup;
    }

    public DoseResponseAnalysisGroup getdRAnalysisGroup() {
        return dRAnalysisGroup;
    }

    public Map<PlateCondition, AreaAnalysisResults> getLinearResultsAnalysisMap() {
        return areaMainController.getLinearResultsAnalysisMap();
    }

    public List<PlateCondition> getPlateConditionList() {
        return areaMainController.getPlateConditionList();
    }

    public List<Integer> getNumberOfReplicates() {
        return areaMainController.getNumberOfReplicates();
    }

    public CellMissyFrame getCellMissyFrame() {
        return areaMainController.getCellMissyFrame();
    }

    /**
     * Called by parent controller, show dose-response panels
     */
    public void onDoseResponse() {

    }

    /**
     * update information message above table. Message will be different for
     * each subview
     *
     * @param messageToShow
     */
    public void updateTableInfoMessage(String messageToShow) {
        dRPanel.getTableInfoLabel().setText(messageToShow);
    }

    /**
     * Populate the table. Each subview uses this method to show different data.
     */
    public void populateTable() {

    }

    /**
     * Plots the fitted data.
     */
    public void plotDoseResponse() {

    }
    
    /**
     * Perform fitting according to user specifications. This method will check
     * how many parameters have been constrained and pick the right fitter class.
     * 
     * @param dataToFit The data (log-transformed concentration - velocity)
     * @param resultsHolder The class that will contain the results from fitting
     * @param bottomConstrained Double if user constrains, otherwise null
     * @param topConstrained Double if user constrains, otherwise null
     * @param standardHillcurve If true, will use standardHillSlope field to constrain
     */
    public void performFitting(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrained, Double topConstrained, boolean standardHillcurve) {
        if (bottomConstrained != null) {
            
            if (topConstrained != null) {
                if (standardHillcurve) {
                    sigmoidFitter.fitBotTopHillConstrain(dataToFit, resultsHolder, bottomConstrained, topConstrained, getStandardHillslope());
                } else {
                    sigmoidFitter.fitBotTopConstrain(dataToFit, resultsHolder, bottomConstrained, topConstrained);
                }
            } else if (standardHillcurve) {
                sigmoidFitter.fitBotHillConstrain(dataToFit, resultsHolder, bottomConstrained, getStandardHillslope());
            } else {
                sigmoidFitter.fitBotConstrain(dataToFit, resultsHolder, bottomConstrained);
            }
        } else if (topConstrained != null) {
            if (standardHillcurve) {
                sigmoidFitter.fitTopHillConstrain(dataToFit, resultsHolder, topConstrained, getStandardHillslope());
            } else {
                sigmoidFitter.fitTopConstrain(dataToFit, resultsHolder, topConstrained);
            }
        } else if (standardHillcurve) {
            sigmoidFitter.fitHillConstrain(dataToFit, resultsHolder, getStandardHillslope());
        } else {
            sigmoidFitter.fitNoConstrain(dataToFit, resultsHolder);
        }
    }

    /**
     * Gets conditions that were processed in previous area analysis steps. This
     * is nessesary to populate table when on input panel.
     *
     * @return list of all processed conditions viable for dose-response
     * analysis
     */
    public List<PlateCondition> getProcessedConditions() {
        List<PlateCondition> processedConditions = areaMainController.getProcessedConditions();
        return processedConditions;
    }

    /**
     * Reset views on cancel
     */
    public void resetOnCancel() {

    }
    
    /**
     * Log-transform a concentration according to its concentration unit.
     * @param concentration Set by user in experimental setup screen
     * @param unit The concentration unit (µM, nM...)
     * @return The log-transformed value of the concentration (eg. 1 µm becomes -6)
     */
    public Double logTransform(Double concentration, String unit) {
        Double value = concentration;
        if (unit.equals("mM") ){
            value *= Math.pow(10, -3);
        } else if (unit.equals("µM")) {
            value *= Math.pow(10,-6);
        } else if (unit.equals("nM")) {
            value *= Math.pow(10, -9);
    }
        return Math.log10(value);
    }
    
    /**
     * private methods
     */
    /**
     * Initialize main view
     */
    private void initMainView() {
        dRPanel = new DRPanel();
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup mainDRRadioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        mainDRRadioButtonGroup.add(dRPanel.getInputDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getInitialPlotDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getNormalizedPlotDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getResultsDRButton());
        //select as default first button
        dRPanel.getInputDRButton().setSelected(true);
        //init dataTable
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row and column selection must be false
        //dataTable.setColumnSelectionAllowed(false);
        //dataTable.setRowSelectionAllowed(false);
        dRPanel.getDatatableDRPanel().add(scrollPane);

        /**
         * When button is selected, switch view to corresponding subview
         */
        dRPanel.getInputDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        dRPanel.getInitialPlotDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        dRPanel.getNormalizedPlotDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        dRPanel.getResultsDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        //add view to parent panel
        areaMainController.getAreaAnalysisPanel().getDoseResponseParentPanel().add(dRPanel, gridBagConstraints);
    }

}
