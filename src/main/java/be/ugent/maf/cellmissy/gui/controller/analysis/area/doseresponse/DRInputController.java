/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.ChooseTreatmentDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private NonEditableTableModel sharedTableModel;
    //view
    private DRInputPanel dRInputPanel;
    private ChooseTreatmentDialog chooseTreatmentDialog;
    // parent controller
    @Autowired
    private DoseResponseController doseResponseController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        plateConditionsList = new ArrayList<>();
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        sharedTableModel = new NonEditableTableModel();
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

    public ChooseTreatmentDialog getChooseTreatmentDialog() {
        return chooseTreatmentDialog;
    }

    public NonEditableTableModel getTableModel() {
        return sharedTableModel;
    }

    private void setTableModel(NonEditableTableModel tableModel) {
        this.sharedTableModel = tableModel;
    }

    /**
     * Initalize data, called on switch from linear regression to dose-response
     */
    public void initDRInputData() {
        //get conditions processed in area analysis
        List<PlateCondition> processedConditions = doseResponseController.getProcessedConditions();
        //number of replicates per condition will be added to list as information
        List<Integer> numberOfReplicates = doseResponseController.getNumberOfReplicates();
        //create and set the table model for the top panel table
        setTableModel(createTableModel(processedConditions));
        // put conditions in selectable list
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, dRInputPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        dRInputPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, numberOfReplicates));

    }

    /**
     * Initialize view
     */
    private void initDRInputPanel() {
        dRInputPanel = new DRInputPanel();
        plateConditionsList = new ArrayList<>();
        areaAnalysisResultsList = new ArrayList<>();
        // control opaque property of bottom table
        dRInputPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);
        JTable slopesTable = dRInputPanel.getSlopesTable();
        slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        slopesTable.getTableHeader().setReorderingAllowed(false);
        slopesTable.setFillsViewportHeight(true);

        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup experimentTypeRadioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        experimentTypeRadioButtonGroup.add(dRInputPanel.getStimulationRadioButton());
        experimentTypeRadioButtonGroup.add(dRInputPanel.getInhibitionRadioButton());
        //select as default first button (Stimulation)
        dRInputPanel.getStimulationRadioButton().setSelected(true);

        //initialize treatment choice dialog
        chooseTreatmentDialog = new ChooseTreatmentDialog(doseResponseController.getCellMissyFrame(), true);


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

        /**
         * When button is pressed, selection of combo box gets taken into
         * account as treatment to analyse and dialog closes.
         */
        chooseTreatmentDialog.getSelectTreatmentButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setTreatment(chooseTreatmentDialog.getTreatmentComboBox().getSelectedItem().toString());
                chooseTreatmentDialog.setVisible(false);
                doseResponseController.initFirstFitting();
            }
        });

        //add view to parent panel
        //doseResponseController.getDRPanel().getGraphicsDRParentPanel().add(dRInputPanel, gridBagConstraints);
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
        doseResponseController.setdRAnalysisGroup(new DoseResponseAnalysisGroup(plateConditionsList, areaAnalysisResultsList));
        // check treatments, dialog pops up if necessary
        checkTreatments(doseResponseController.getdRAnalysisGroup(), chooseTreatmentDialog);
        // populate bottom table with the analysis group
        dRInputPanel.getSlopesTable().setModel(createTableModel(doseResponseController.getdRAnalysisGroup()));
        dRInputPanel.getSlopesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));

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
        doseResponseController.setdRAnalysisGroup(new DoseResponseAnalysisGroup(plateConditionsList, areaAnalysisResultsList));
        //check treatments, dialog pops up if necessary
        checkTreatments(doseResponseController.getdRAnalysisGroup(), chooseTreatmentDialog);
        // populate bottom table with the analysis group
        dRInputPanel.getSlopesTable().setModel(createTableModel(doseResponseController.getdRAnalysisGroup()));
        dRInputPanel.getSlopesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));

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
            PlateCondition selectedCondition = doseResponseController.getProcessedConditions().get(selectedIndex);
            selectedConditions.add(selectedCondition);
        }
        return selectedConditions;
    }

    /**
     * Create and return table model for top panel table. Table contains
     * condition number, treatment name, concentration and concentration unit
     * and replicate slopes.
     *
     * @param processedConditions
     * @return
     */
    private NonEditableTableModel createTableModel(List<PlateCondition> processedConditions) {
        List<Integer> conditionNumberList = new ArrayList();
        List<String> treatmentNameList = new ArrayList();
        List<Double> concentrationList = new ArrayList();
        List<String> concentrationUnitList = new ArrayList();
        List<Double[]> slopesList = new ArrayList();
        Integer i = 0;
        for (PlateCondition condition : processedConditions) {
            //1 platecondition might have multiple treatments
            List<Treatment> treatmentList = condition.getTreatmentList();

            for (Treatment treatment : treatmentList) {
                //for dose-response treatment category needs to be 1
                if (treatment.getTreatmentType().getTreatmentCategory() == 1) {
                    conditionNumberList.add(i);
                    treatmentNameList.add(treatment.getTreatmentType().getName());
                    concentrationList.add(treatment.getConcentration());
                    concentrationUnitList.add(treatment.getConcentrationUnit());
                    slopesList.add(doseResponseController.getLinearResultsAnalysisMap().get(condition).getSlopes());
                }
            }
            i++;
        }
        int maximumNumberOfReplicates = AnalysisUtils.getMaximumNumberOfReplicates(processedConditions);
        Object[][] data = new Object[conditionNumberList.size()][maximumNumberOfReplicates + 4];
        for (int rowIndex = 0; rowIndex < conditionNumberList.size(); rowIndex++) {
            for (int columnIndex = 4; columnIndex < slopesList.get(rowIndex).length + 4; columnIndex++) {
                Double slope = slopesList.get(rowIndex)[columnIndex - 4];
                if (slope != null && !slope.isNaN()) {
                    // round to three decimals slopes and coefficients
                    slope = AnalysisUtils.roundThreeDecimals(slopesList.get(rowIndex)[columnIndex - 4]);
                    // show in table slope + (coefficient)
                    data[rowIndex][columnIndex] = slope;
                } else if (slope == null) {
                    data[rowIndex][columnIndex] = "excluded";
                } else if (slope.isNaN()) {
                    data[rowIndex][columnIndex] = "NaN";
                }
            }
            // first column contains condition numbers
            data[rowIndex][0] = conditionNumberList.get(rowIndex);
            // second to fourth will contain treatment information
            data[rowIndex][1] = treatmentNameList.get(rowIndex);
            data[rowIndex][2] = concentrationList.get(rowIndex);
            data[rowIndex][3] = concentrationUnitList.get(rowIndex);
        }
        // array of column names for table model
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "Condition number";
        columnNames[1] = "Treatment";
        columnNames[2] = "Concentration";
        columnNames[3] = "Unit";
        for (int x = 4; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + (x - 3);
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

    /**
     * Table model for bottom table: starts from analysis group.
     *
     * @param analysisGroup
     * @return
     */
    private NonEditableTableModel createTableModel(DoseResponseAnalysisGroup analysisGroup) {
        LinkedHashMap<Double, String> concentrationsMap = analysisGroup.getConcentrationsMap().get(analysisGroup.getTreatmentToAnalyse());
        LinkedHashMap<PlateCondition, List<Double>> velocitiesMap = analysisGroup.getVelocitiesMap();

        List<List<Double>> allVelocities = new ArrayList<List<Double>>();

        Object[][] data = new Object[concentrationsMap.size()][velocitiesMap.entrySet().iterator().next().getValue().size() + 2];
        int i = 0;
        int controlIndex = 0;
        for (PlateCondition plateCondition : velocitiesMap.keySet()) {
            List<Double> replicateVelocities = analysisGroup.getVelocitiesMap().get(plateCondition);
            allVelocities.add(replicateVelocities);

            //check if this platecondition is the control, save index for table
            for (Treatment treatment : plateCondition.getTreatmentList()) {
                if (treatment.getTreatmentType().getName().equalsIgnoreCase("control")) {
                    controlIndex = i;
                }
            }
            i++;
        }
        data[controlIndex][0] = 0.0;
        data[controlIndex][1] = "--";

        int rowIndex = 0;

        for (Map.Entry<Double, String> entry : concentrationsMap.entrySet()) {
            if (rowIndex >= controlIndex) {
                data[rowIndex + 1][0] = entry.getKey();
                data[rowIndex + 1][1] = entry.getValue();
            } else {
                data[rowIndex][0] = entry.getKey();
                data[rowIndex][1] = entry.getValue();
            }

            for (int columnIndex = 2; columnIndex < allVelocities.get(0).size() + 2; columnIndex++) {
                Double slope = allVelocities.get(rowIndex).get(columnIndex - 2);
                if (rowIndex < controlIndex) {
                    if (slope != null && !slope.isNaN()) {
                        // round to three decimals slopes and coefficients
                        slope = AnalysisUtils.roundThreeDecimals(allVelocities.get(rowIndex).get(columnIndex - 2));
                        // show in table slope + (coefficient)
                        data[rowIndex][columnIndex] = slope;
                    } else if (slope == null) {
                        data[rowIndex][columnIndex] = "excluded";
                    } else if (slope.isNaN()) {
                        data[rowIndex][columnIndex] = "NaN";
                    }
                } else {
                    if (slope != null && !slope.isNaN()) {
                        // round to three decimals slopes and coefficients
                        slope = AnalysisUtils.roundThreeDecimals(allVelocities.get(rowIndex).get(columnIndex - 2));
                        // show in table slope + (coefficient)
                        data[rowIndex + 1][columnIndex] = slope;
                    } else if (slope == null) {
                        data[rowIndex + 1][columnIndex] = "excluded";
                    } else if (slope.isNaN()) {
                        data[rowIndex + 1][columnIndex] = "NaN";
                    }
                }
            }
            rowIndex++;
        }
        // array of column names for table model
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "Conc of " + analysisGroup.getTreatmentToAnalyse().getTreatmentType().getName();
        columnNames[1] = "Unit";
        for (int x = 2; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + (x - 1);
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;

    }

    /**
     * Checks analysis group treatments. If there is more than one (excluding
     * control), a dialog must pop up where the user must choose which treatment
     * to analyze.
     */
    private void checkTreatments(DoseResponseAnalysisGroup analysisGroup, ChooseTreatmentDialog dialog) {
        Set<Treatment> treatmentSet = analysisGroup.getConcentrationsMap().keySet();
        //remove all so that items are not duplicated, because this method can be called several times
        dialog.getTreatmentComboBox().removeAllItems();
        if (treatmentSet.size() > 2) {
            //Strings are needed for display
            for (Treatment treatment : treatmentSet) {
                String treatmentName = treatment.getTreatmentType().getName();
                if (!treatmentName.equalsIgnoreCase("control")) {
                    dialog.getTreatmentComboBox().addItem(treatmentName);
                }
            }
            dialog.pack();
            // center the dialog on the main frame
            GuiUtils.centerDialogOnFrame(doseResponseController.getCellMissyFrame(), dialog);
            // show the dialog
            dialog.setVisible(true);

        } else {
            for (Treatment treatment : treatmentSet) {
                if (!treatment.getTreatmentType().getName().equalsIgnoreCase("control")) {
                    analysisGroup.setTreatmentToAnalyse(treatment);
                }
            }
            doseResponseController.initFirstFitting();
        }
    }

    /**
     * Sets the treatment to analyse in the analysis group to the treatment with
     * this name.
     *
     * @param treatmentName The string selected in the dialog combobox
     */
    private void setTreatment(String treatmentName) {
        Set<Treatment> allTreatments = doseResponseController.getdRAnalysisGroup().getConcentrationsMap().keySet();
        for (Treatment treatment : allTreatments) {
            if (treatment.getTreatmentType().getName().equals(treatmentName)) {
                doseResponseController.getdRAnalysisGroup().setTreatmentToAnalyse(treatment);
            }
        }
    }
}
