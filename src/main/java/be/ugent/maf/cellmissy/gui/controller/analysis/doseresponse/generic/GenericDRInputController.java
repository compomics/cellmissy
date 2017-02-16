/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.entity.result.doseresponse.GenericDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInputController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.view.icon.RectIcon;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author CompOmics Gwen
 */
@Controller("genericDRInputController")
public class GenericDRInputController extends DRInputController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericDRInputController.class);

    //model
    private List<DoseResponsePair> conditionsList;      //contains the conditions that are currently in the analysis group
    //view: in super class
    //parent controller
    @Autowired
    private GenericDoseResponseController doseResponseController;

    @Override
    public void initDRInputData() {
        List<DoseResponsePair> doseResponseData = doseResponseController.getImportedDRDataHolder().getDoseResponseData();
        // create and set the table model for the top panel table using the loaded data
        setTableModel(createTableModel(doseResponseData));
        //number of replicates per condition will be added to a list as information
        List<Integer> numberOfReplicates = getNumberOfReplicates(doseResponseData);
        // put conditions in selectable list
        ObservableList<DoseResponsePair> doseResponsePairBindingList = ObservableCollections.observableList(doseResponseData);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, doseResponsePairBindingList, dRInputPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        dRInputPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(doseResponseData, numberOfReplicates));
        dRInputPanel.getConditionsList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        doseResponseController.getDRPanel().revalidate();
        doseResponseController.getDRPanel().repaint();
        doseResponseController.getDRPanel().getGraphicsDRParentPanel().add(dRInputPanel, gridBagConstraints);
    }

    public void reset() {
        conditionsList = new ArrayList<>();
    }

    @Override
    protected void initDRInputPanel() {
        dRInputPanel = new DRInputPanel();
        conditionsList = new ArrayList<>();
        // control opaque property of bottom table
        dRInputPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);
        slopesTable = dRInputPanel.getSlopesTable();
        slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        slopesTable.getTableHeader().setReorderingAllowed(false);
        slopesTable.setFillsViewportHeight(true);
        slopesTable.setModel(new NonEditableTableModel());

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

    }

    @Override
    protected void addToDRAnalysis() {
        List<DoseResponsePair> selectedConditions = getSelectedConditions();
        if (selectedConditions != null && !selectedConditions.equals(conditionsList)) {
            for (DoseResponsePair selectedCondition : selectedConditions) {
                //only add to list if list does not contain this condition already
                if (!conditionsList.contains(selectedCondition)) {
                    conditionsList.add(selectedCondition);
                }
            }
            // make a new analysis group
            // override variable if one existed already
            doseResponseController.setdRAnalysisGroup(new GenericDoseResponseAnalysisGroup(conditionsList));
            doseResponseController.setFirstFitting(true);

            // populate bottom table with the analysis group
            slopesTable.setModel(createTableModel(doseResponseController.getdRAnalysisGroup()));
            slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        }
    }

    @Override
    protected void removeFromDRAnalysis() {
        List<DoseResponsePair> selectedConditions = getSelectedConditions();
        for (DoseResponsePair selectedCondition : selectedConditions) {
            //only possible to remove if group contains selected condition
            if (conditionsList.contains(selectedCondition)) {
                conditionsList.remove(selectedCondition);
            }
        }
        //only make new analysis group if you have not removed all
        if (!conditionsList.isEmpty()) {
            doseResponseController.setdRAnalysisGroup(new GenericDoseResponseAnalysisGroup(conditionsList));
            doseResponseController.setFirstFitting(true);
            // populate bottom table with the analysis group
            slopesTable.setModel(createTableModel(doseResponseController.getdRAnalysisGroup()));
            slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        } else {
            //otherwise show new empty table
            slopesTable.setModel(new NonEditableTableModel());
        }
    }

    /**
     * Get List of selected conditions from RectIcon List.
     *
     * @return List of conditions to be added to the analysis group, null if the
     * array is empty and no conditions are selected.
     */
    private List<DoseResponsePair> getSelectedConditions() {
        // get selected indices from rect icon list
        int[] selectedIndices = dRInputPanel.getConditionsList().getSelectedIndices();
        //check if array is not empty
        if (selectedIndices.length > 1) {
            List<DoseResponsePair> selectedConditions = new ArrayList<>();
            for (int selectedIndex : selectedIndices) {
                DoseResponsePair selectedCondition = doseResponseController.getImportedDRDataHolder().getDoseResponseData().get(selectedIndex);
                selectedConditions.add(selectedCondition);
            }
            return selectedConditions;
        } else {
            return null;
        }
    }

    /**
     * Create model for overview table (top one). Contains all imported data.
     *
     * @return
     */
    private NonEditableTableModel createTableModel(List<DoseResponsePair> importedData) {
        int maxReplicates = Collections.max(getNumberOfReplicates(importedData));
        Object[][] data = new Object[importedData.size()][maxReplicates + 2];

        for (int rowIndex = 0; rowIndex < importedData.size(); rowIndex++) {
            data[rowIndex][0] = rowIndex + 1;
            data[rowIndex][1] = importedData.get(rowIndex).getDose();
            //not all row have the maximum number of columns
            for (int columnIndex = 2; columnIndex < maxReplicates + 2; columnIndex++) {
                try {
                    data[rowIndex][columnIndex] = importedData.get(rowIndex).getResponses().get(columnIndex - 2);
                } catch (IndexOutOfBoundsException e) {
                    data[rowIndex][columnIndex] = "";
                }
            }
        }

        // array of column names for table model
        String[] columnNames = new String[maxReplicates + 2];
        columnNames[0] = "Condition number";
        columnNames[1] = "Dose";
        for (int x = 2; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + (x - 1);
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

    /**
     * Create model for analysis group table (bottom one). Contains only the
     * data the user has chosen to analyze.
     *
     * @return
     */
    private NonEditableTableModel createTableModel(GenericDoseResponseAnalysisGroup analysisGroup) {
        List<DoseResponsePair> dataToShow = analysisGroup.getDoseResponseData();
        //when removing all conditions
        if (dataToShow.isEmpty()) {
            return new NonEditableTableModel();
        }
        //the number of columns is dependent on the maximum number of replicates of the dataset
        int maxReplicates = Collections.max(getNumberOfReplicates(dataToShow));
        Object[][] data = new Object[dataToShow.size()][maxReplicates + 1];

        for (int rowIndex = 0; rowIndex < dataToShow.size(); rowIndex++) {
            data[rowIndex][0] = dataToShow.get(rowIndex).getDose();
            for (int columnIndex = 1; columnIndex < maxReplicates + 1; columnIndex++) {
                try {
                    data[rowIndex][columnIndex] = dataToShow.get(rowIndex).getResponses().get(columnIndex - 1);
                } catch (IndexOutOfBoundsException e) {
                    data[rowIndex][columnIndex] = "";
                }
            }
        }

        // array of column names for table model
        String[] columnNames = new String[maxReplicates + 1];
        columnNames[0] = "Dose";
        for (int x = 1; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + x;
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

    private List<Integer> getNumberOfReplicates(List<DoseResponsePair> allConditions) {
        List<Integer> result = new ArrayList<>();
        //we can iterate over the map like this because the order is saved in the list
        for (DoseResponsePair row : allConditions) {
            result.add(row.getResponses().size());
        }
        return result;
    }

    /**
     * Private class for list items renderer
     */
    public class RectIconListRenderer extends DefaultListCellRenderer {

        private final List<DoseResponsePair> doseResponsePairList;
        private final List<Integer> numberOfReplicates;

        /**
         * Constructor, needs a list of plate conditions, together with number
         * of replicates for each condition.
         *
         * @param doseResponsePairList
         * @param numberOfReplicates
         */
        public RectIconListRenderer(List<DoseResponsePair> doseResponsePairList, List<Integer> numberOfReplicates) {
            this.doseResponsePairList = doseResponsePairList;
            this.numberOfReplicates = numberOfReplicates;
            setOpaque(true);
            setIconTextGap(10);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            int lenght = GuiUtils.getAvailableColors().length;
            int conditionIndex = doseResponsePairList.indexOf(value);
            int indexOfColor = conditionIndex % lenght;
            setIcon(new RectIcon(GuiUtils.getAvailableColors()[indexOfColor]));
            setText("N = " + numberOfReplicates.get(conditionIndex));
            return this;
        }
    }

}
