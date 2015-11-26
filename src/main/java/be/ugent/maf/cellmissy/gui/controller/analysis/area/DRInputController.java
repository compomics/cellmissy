/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for input panel of dose-response analysis Here the analysis group
 * is created according to the user's choice
 *
 * @author Gwendolien
 */
@Controller("dRInputController")
public class DRInputController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DRInputController.class);
    //model
    private BindingGroup bindingGroup;
    private List<PlateCondition> plateConditionsList;
    private List<AreaAnalysisResults> areaAnalysisResultsList;
    //view
    private DRInputPanel dRInputPanel;
    // parent controller
    @Autowired
    private DoseResponseController doseResponseController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init view
        initDRInputPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public DRInputPanel getdRInputPanel() {
        return dRInputPanel;
    }

    /**
     * Initialize view
     */
    private void initDRInputPanel() {
        dRInputPanel = new DRInputPanel();

        //get conditions processed in area analysis
        List<PlateCondition> processedConditions = doseResponseController.getProcessedConditions();
        //variable that constains selected indices in list:
        //these conditions will be added or removed from dose-response analysis on corresponding button press
        List<PlateCondition> selectedConditions = getSelectedConditions();
        //number of replicates per condition will be added to list as information
        List<Integer> numberOfReplicates = getNumberOfReplicates();

        //update table info label
        doseResponseController.updateTableInfoMessage("This table contains all conditions and their respective slopes");
        // control opaque property of table
        dRInputPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);
        JTable slopesTable = dRInputPanel.getSlopesTable();
        slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        slopesTable.getTableHeader().setReorderingAllowed(false);
        slopesTable.setFillsViewportHeight(true);

        // put conditions in selectable list
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, dRInputPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        dRInputPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, numberOfReplicates));

        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup experimentTypeRadioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        experimentTypeRadioButtonGroup.add(dRInputPanel.getStimulationRadioButton());
        experimentTypeRadioButtonGroup.add(dRInputPanel.getInhibitionRadioButton());
        //select as default first button (Stimulation)
        dRInputPanel.getStimulationRadioButton().setSelected(true);

        /*
         * Action listeners for buttons
         */
        dRInputPanel.getAddConditionButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //add selected condition to analysis
                addToDRAnalysis();
            }
        });

        dRInputPanel.getRemoveConditionButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // remove condition from analysis
                removeFromDRAnalysis();
            }
        });

        /**
         * Choosing stimulation or inhibition type experiment defines standard
         * hillslope parameter
         */
        dRInputPanel.getStimulationRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doseResponseController.setStandardHillslope(1);
            }
        });

        dRInputPanel.getInhibitionRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doseResponseController.setStandardHillslope(-1);
            }
        });

        //add view to parent panel
        doseResponseController.getDRPanel().getGraphicsDRParentPanel().add(dRInputPanel, gridBagConstraints);
    }

    /**
     * Get conditions according to selection in list and add to the
     * dose-response analysis group
     */
    private void addToDRAnalysis() {
        //selected conditions
        List<PlateCondition> selectedConditions = getSelectedConditions();
        for (PlateCondition selectedCondition : selectedConditions) {
            //only add to list if list does not contain this condition already
            if (!plateConditionsList.contains(selectedCondition)) {
                plateConditionsList.add(selectedCondition);
                AreaAnalysisResults areaAnalysisResults = doseResponseController.getLinearResultsAnalysisMap().get(selectedCondition);
                areaAnalysisResultsList.add(areaAnalysisResults);
            }
        }
        // make a new analysis group, with those conditions and those results
        // override variable if one existed already
        DoseResponseAnalysisGroup dRAnalysisGroup = new DoseResponseAnalysisGroup(plateConditionsList, areaAnalysisResultsList);
        // populate bottom table with the analysis group

    }

    /**
     * Remove selected condition from the dose-response analysis group
     */
    private void removeFromDRAnalysis() {
        //selected conditions
        List<PlateCondition> selectedConditions = getSelectedConditions();
        for (PlateCondition selectedCondition : selectedConditions) {
            //only possible to remove if group contains selected condition
            if (plateConditionsList.contains(selectedCondition)) {
                plateConditionsList.remove(selectedCondition);
                AreaAnalysisResults areaAnalysisResults = doseResponseController.getLinearResultsAnalysisMap().get(selectedCondition);
                areaAnalysisResultsList.remove(areaAnalysisResults);
            }
        }
        DoseResponseAnalysisGroup dRAnalysisGroup = new DoseResponseAnalysisGroup(plateConditionsList, areaAnalysisResultsList);
        // populate bottom table with the analysis group
    }

    /**
     * Get drug IDs for all conditions. It is necessary to show this in case of
     * an experiment where different drugs were used.
     */
    private List<String> getDrugIDs() {
        List<String> drugIDs = new ArrayList<>();

        return drugIDs;
    }

    /**
     * Get List of Selected conditions from RectIcon List
     *
     * @return List of Plate Conditions to be added to the DR analysis group
     */
    private List<PlateCondition> getSelectedConditions() {
        // get selected indices from rect icon list
        int[] selectedIndices = dRInputPanel.getConditionsList().getSelectedIndices();
        List<PlateCondition> selectedConditions = new ArrayList<>();
        for (int selectedIndex : selectedIndices) {
            PlateCondition selectedCondition = doseResponseController.getPlateConditionList().get(selectedIndex);
            selectedConditions.add(selectedCondition);
        }
        return selectedConditions;
    }
}
