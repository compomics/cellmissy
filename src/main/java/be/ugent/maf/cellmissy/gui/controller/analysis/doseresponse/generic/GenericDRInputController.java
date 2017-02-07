/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.entity.result.doseresponse.GenericDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInputController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.ButtonGroup;
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
    private List<Double> conditionsList;      //contains the conditions (concentrations) that are currently in the analysis group
    //view: in super class
    //parent controller
    @Autowired
    private GenericDoseResponseController doseResponseController;

    @Override
    public void initDRInputData() {
        // create and set the table model for the top panel table using the loaded data
        setTableModel(createTableModel(doseResponseController.getImportedDRDataHolder().getDoseResponseData()));
        //number of replicates per condition will be added to a list as information
        List<Integer> numberOfReplicates = getNumberOfReplicates(doseResponseController.getImportedDRDataHolder().getDoseResponseData());
        // put conditions in selectable list
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, dRInputPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        dRInputPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, numberOfReplicates));
        dRInputPanel.getConditionsList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        doseResponseController.getDRPanel().getGraphicsDRParentPanel().add(dRInputPanel);
        doseResponseController.getDRPanel().revalidate();
        doseResponseController.getDRPanel().repaint();
    }

    @Override
    protected void initDRInputPanel() {
        dRInputPanel = new DRInputPanel();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeFromDRAnalysis() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Get List of selected conditions from RectIcon List
     *
     * @return List of ??? to be added to the DR analysis group
     */
//    private List<Object> getSelectedConditions() {
//        // get selected indices from rect icon list
//        int[] selectedIndices = dRInputPanel.getConditionsList().getSelectedIndices();
//        List<> selectedConditions = new ArrayList<>();
//        for (int selectedIndex : selectedIndices) {
//             selectedCondition = doseResponseController.getSOMETHING().get(selectedIndex);
//            selectedConditions.add(selectedCondition);
//        }
//        return selectedConditions;
//    }
    
    /**
     * Create model for overview table (top one)
     * @return 
     */
    private NonEditableTableModel createTableModel(LinkedHashMap<Double, List<Double>> importedData) {
        List<Integer> conditionNumberList = new ArrayList();
        List<String> treatmentNameList = new ArrayList();
        List<Double> concentrationList = new ArrayList();
        List<String> concentrationUnitList = new ArrayList();
        List<Double[]> slopesList = new ArrayList();
        //start counting from 1, easier for user
        Integer i = 1;
        for (PlateCondition condition : processedConditions) {
            //1 platecondition might have multiple treatments
            List<Treatment> treatmentList = condition.getTreatmentList();

            for (Treatment treatment : treatmentList) {

                conditionNumberList.add(i);
                treatmentNameList.add(treatment.getTreatmentType().getName());
                concentrationList.add(treatment.getConcentration());
                concentrationUnitList.add(treatment.getConcentrationUnit());
                slopesList.add(doseResponseController.getLinearResultsAnalysisMap().get(condition).getSlopes());

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
     * Create model for analysis group table (bottom one)
     * @return 
     */
    private NonEditableTableModel createTableModel(GenericDoseResponseAnalysisGroup analysisGroup) {
        LinkedHashMap<Double, List<Double>> dataToShow = analysisGroup.getDoseResponseData();
        //when removing all conditions
        if (dataToShow.isEmpty()) {
            return new NonEditableTableModel();
        }
        //the number of columns is dependent on the maximum number of replicates of the dataset
        int maxReplicates = 0;
        for (List<Double> value : dataToShow.values()) {
            int replicates = value.size();
            if (replicates > maxReplicates) {
                maxReplicates = replicates;
            }
        }
        
        //THE 2 EXTRA COLUMNS ARE FOR CONCENTRATION AND UNIT, IS UNIT NECESSARY OR EVEN DEFINED???
        //HOW WILL THE CONTROL BE DEFINED IN THE LOADED DATA?
        Object[][] data = new Object[dataToShow.size()][maxReplicates + 2];
        int i = 0;
        int controlIndex = 100;
        int rowIndex = 0;

//        for (Map.Entry<PlateCondition, List<Double>> entry : velocitiesMap.entrySet()) {
//            //check if this platecondition is the control, save index for table
//            for (Treatment treatment : entry.getKey().getTreatmentList()) {
//                if (treatment.getTreatmentType().getName().contains("ontrol")) {
//                    controlIndex = i;
//                }
//            }
//            i++;
//
//            int columnIndex = 2;
//            for (Double velocity : entry.getValue()) {
//
//                if (velocity != null && !velocity.isNaN()) {
//                    // round to three decimals slopes and coefficients
//                    Double slope = AnalysisUtils.roundThreeDecimals(velocity);
//                    // show in table slope + (coefficient)
//                    data[rowIndex][columnIndex] = slope;
//                } else if (velocity == null) {
//                    data[rowIndex][columnIndex] = "excluded";
//                } else if (velocity.isNaN()) {
//                    data[rowIndex][columnIndex] = "NaN";
//                }
//
//                columnIndex++;
//            }
//            rowIndex++;
//        }
//
//        if (controlIndex != 100) {
//            data[controlIndex][0] = 0.0;
//            data[controlIndex][1] = "--";
//        }
//        rowIndex = 0;
//        //if user only selects control, the concentrationsmap is null
//        if (concentrationsMap != null) {
//            for (Map.Entry<Double, String> entry : concentrationsMap.entrySet()) {
//                if (rowIndex >= controlIndex) {
//                    data[rowIndex + 1][0] = entry.getKey();
//                    data[rowIndex + 1][1] = entry.getValue();
//                } else {
//                    data[rowIndex][0] = entry.getKey();
//                    data[rowIndex][1] = entry.getValue();
//                }
//
//                rowIndex++;
//            }
//        }
        // array of column names for table model
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "Conc of " + analysisGroup.getTreatmentToAnalyse();
        columnNames[1] = "Unit";
        for (int x = 2; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + (x - 1);
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

    private List<Integer> getNumberOfReplicates(LinkedHashMap<Double, List<Double>> allConditions) {
        List<Integer> result = new ArrayList<>();
        //we can iterate over the map like this because the order is saved (-->"LINKED")
        for (List<Double> value :allConditions.values()) {
            result.add(value.size());
        }
        return result;
    }

}
