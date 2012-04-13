/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.ConditionsPanel;
import be.ugent.maf.cellmissy.gui.experiment.ConditionsSetupPanel;
import be.ugent.maf.cellmissy.service.CellLineService;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class ConditionsPanelController {

    //model
    private ObservableList<CellLine> cellLineBindingList;
    private ObservableList<PlateCondition> plateConditionBindingList;
    private BindingGroup bindingGroup;
    //view
    private ConditionsPanel conditionsPanel;
    private ConditionsSetupPanel conditionsSetupPanel;
    //parent controller
    private ExperimentSetupPanelController experimentSetupPanelController;
    //child controllers
    private AssayEcmPanelController assayEcmPanelController;
    private TreatmentPanelController treatmentPanelController;
    //services
    private CellLineService cellLineService;
    private GridBagConstraints gridBagConstraints;
    private Integer conditionIndex;
    
    public ConditionsPanelController(ExperimentSetupPanelController experimentSetupPanelController) {
        
        this.experimentSetupPanelController = experimentSetupPanelController;

        //init services
        cellLineService = (CellLineService) experimentSetupPanelController.getCellMissyController().getBeanByName("cellLineService");
        
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        conditionsPanel = new ConditionsPanel();
        conditionsSetupPanel = new ConditionsSetupPanel();

        //init child controllers
        assayEcmPanelController = new AssayEcmPanelController(this);
        treatmentPanelController = new TreatmentPanelController(this);
        
        initCellLinePanel();
        initConditionsPanel();
        initPanel();
    }
    
    public ExperimentSetupPanelController getExperimentSetupPanelController() {
        return experimentSetupPanelController;
    }
    
    public ConditionsSetupPanel getConditionsSetupPanel() {
        return conditionsSetupPanel;
    }
    
    private void initCellLinePanel() {

        //init cellLineJCombo
        cellLineBindingList = ObservableCollections.observableList(cellLineService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, cellLineBindingList, conditionsSetupPanel.getCellLineComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        //add action listeners
        conditionsSetupPanel.getInsertCellLineButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!conditionsSetupPanel.getCellLineNameTextField().getText().isEmpty()) {
                    CellLine newCellLine = new CellLine();
                    newCellLine.setName(conditionsSetupPanel.getCellLineNameTextField().getText());
                    //insert cell line to DB
                    cellLineService.save(newCellLine);
                    //add the new cell line to the list
                    cellLineBindingList.add(newCellLine);
                }
            }
        });
    }
    
    private void initConditionsPanel() {
        
        conditionIndex = 0;

        //set cell renderer for conditionJList (to show condition name instead of toString-method return)
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsRenderer());

        //init conditionJList (create new empty list) (conditions are NOT retrieved from DB)
        plateConditionBindingList = ObservableCollections.observableList(new ArrayList<PlateCondition>());

        //create a new (default) first condition, set its properties (to show something through the components) and add it to the list
        PlateCondition newPlateCondition = new PlateCondition();
        initPlateCondition(newPlateCondition);
        plateConditionBindingList.add(newPlateCondition);

        //init conditionListBinding
        JListBinding conditionListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsPanel.getConditionsJList());
        bindingGroup.addBinding(conditionListBinding);
        bindingGroup.bind();

        //select as default the first condition
        conditionsPanel.getConditionsJList().setSelectedIndex(0);

        //autobind plate conditions
        bindPlateConditions();

        //add action listeners
        //add a new condition to the List
        conditionsPanel.getAddButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                //create and init a new condition
                PlateCondition newPlateCondition = new PlateCondition();
                initPlateCondition(newPlateCondition);
                //add the new condition to the list
                plateConditionBindingList.add(newPlateCondition);
            }
        });

        //remove a condition from the list (if the user makes mistakes)
        conditionsPanel.getRemoveButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (conditionsPanel.getConditionsJList().getSelectedValue() != null) {
                    plateConditionBindingList.remove(conditionsPanel.getConditionsJList().getSelectedIndex());
                }
            }
        });
    }
    
    private void initPlateCondition(PlateCondition plateCondition) {

        //for each new condition, class members are set as default
        plateCondition.setName("Condition " + ++conditionIndex);
        plateCondition.setCellLine(cellLineBindingList.get(0));
        plateCondition.setMatrixDimension(assayEcmPanelController.getMatrixDimensionBindingList().get(0));
        plateCondition.setAssay(assayEcmPanelController.getAssay2DBindingList().get(0));
    }
    
    private void bindPlateConditions() {

        //bind cell line
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine"), conditionsSetupPanel.getCellLineComboBox(), BeanProperty.create("selectedItem"), "celllinebinding");
        bindingGroup.addBinding(binding);

        //bind ecm dimension
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.matrixDimension"), conditionsSetupPanel.getEcmDimensionComboBox(), BeanProperty.create("selectedItem"), "matrixdimensionbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

//        //bind assay, depending on 2D/3D ecm
//        PlateCondition selectedPlateCondition = plateConditionBindingList.get(conditionsPanel.getConditionsJList().getSelectedIndex());
//        if (selectedPlateCondition.getMatrixDimension().getMatrixDimension().equals("2D")) {
//            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assay"), assayEcmPanelController.getAssayEcm2DPanel().getAssayComboBox(), BeanProperty.create("selectedItem"), "assaybinding2D");
//        } else {
//            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assay"), assayEcmPanelController.getAssayEcm3DPanel().getAssayComboBox(), BeanProperty.create("selectedItem"), "assaybinding3D");
//        }
//        bindingGroup.addBinding(binding);
//        bindingGroup.bind();
    }
    
    public void switchAssayBinding(JComboBox jComboBox) {
        if (bindingGroup.getBinding("assaybinding") != null) {
            bindingGroup.removeBinding(bindingGroup.getBinding("assaybinding"));
            //bindingGroup.bind();
        }
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assay"), jComboBox, BeanProperty.create("selectedItem"), "assaybinding");
        bindingGroup.addBinding(binding);
        //bindingGroup.bind();
    }
    
    private class ConditionsRenderer extends DefaultListCellRenderer {
        
        public ConditionsRenderer() {
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            PlateCondition newPlateCondition = (PlateCondition) value;
            setText(newPlateCondition.getName());
            return this;
        }
    }
    
    private void initPanel() {
        experimentSetupPanelController.getExperimentSetupPanel().getConditionsParentPanel().add(conditionsPanel, gridBagConstraints);
        experimentSetupPanelController.getExperimentSetupPanel().getConditionsSetupParentPanel().add(conditionsSetupPanel, gridBagConstraints);
    }
}
