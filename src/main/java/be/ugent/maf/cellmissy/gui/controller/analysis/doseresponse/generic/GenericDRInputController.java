/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.doseresponse.GenericDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRInputController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.ChooseTreatmentDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    //view: in super class
    //parent controller
    @Autowired
    private GenericDoseResponseController doseResponseController;

    @Override
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
                doseResponseController.getdRAnalysisGroup().setTreatmentToAnalyse(chooseTreatmentDialog.getTreatmentComboBox().getSelectedItem().toString());
                chooseTreatmentDialog.setVisible(false);
                doseResponseController.setFirstFitting(true);
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
    private List<> getSelectedConditions() {
        
    }
    
    /**
     * Create model for overview table (top one)
     * @return 
     */
    private NonEditableTableModel createTableModel() {
        
    }
    
    /**
     * Create model for analysis group table (bottom one)
     * @return 
     */
    private NonEditableTableModel createTableModel(GenericDoseResponseAnalysisGroup analysisGroup) {
        
    }

}
