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
import javax.swing.JList;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
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

        //init conditionJList (create new empty list) (conditions are NOT retrieved from DB)
        plateConditionBindingList = ObservableCollections.observableList(new ArrayList<PlateCondition>());
        //create a new (default) first condition and add it to the list
        PlateCondition newPlateCondition = new PlateCondition();
        conditionIndex = 1;
        newPlateCondition.setName("Condition " + conditionIndex);
        newPlateCondition.setCellLine(cellLineBindingList.get(2));
        plateConditionBindingList.add(newPlateCondition);

        PlateCondition secondPlateCondition = new PlateCondition();
        secondPlateCondition.setName("Condition " + ++conditionIndex);
        secondPlateCondition.setCellLine(cellLineBindingList.get(1));
        plateConditionBindingList.add(secondPlateCondition);

        //set cell renderer for conditionJList (to show condition name instead of toString-method return)
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsRenderer());

        //init conditionListBinding
        JListBinding conditionListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsPanel.getConditionsJList());
        bindingGroup.addBinding(conditionListBinding);
        bindingGroup.bind();

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine"), conditionsSetupPanel.getCellLineComboBox(), BeanProperty.create("selectedItem"), "celllinebinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.name"), conditionsSetupPanel.getCellLineNameTextField(), BeanProperty.create("text"), "celllinebinding2");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

        //select as default the first condition
        conditionsPanel.getConditionsJList().setSelectedIndex(0);

        //add action listeners
        //add a new condition to the List
        conditionsPanel.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //create a new condition and set the name
                PlateCondition newPlateCondition = new PlateCondition();
                newPlateCondition.setName("Condition " + ++conditionIndex);
                //add the new condition to the list
                plateConditionBindingList.add(newPlateCondition);
                //bindingGroup.bind();
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

//    private void initPlateCondition() {
//
//        //bind cell line
//        Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine"), conditionsSetupPanel.getCellLineComboBox(), BeanProperty.create("selectedItem"), "celllinebinding");
//        bindingGroup.addBinding(binding);
////        //bind ecm dimension
////        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.matrixDimension"), conditionsSetupPanel.getEcmDimensionComboBox(), BeanProperty.create("selectedItem"), "matrixdimensionbinding");
////        bindingGroup.addBinding(binding);
//        bindingGroup.bind();
//        
//        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.name"), conditionsSetupPanel.getCellLineNameTextField(), BeanProperty.create("text"), "celllinebinding2");
//        bindingGroup.addBinding(binding);
//        bindingGroup.bind();
//        //conditionsPanel.getConditionsJList().setSelectedIndex(1);
//
////        //bind assay, depending on 2D/3D ecm
////        if ((selectedPlateCondition.getMatrixDimension().getMatrixDimension().equals("2D"))) {
////            binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, assayEcmPanelController.getAssayEcm2DPanel().getAssayComboBox(), BeanProperty.create("selectedItem"), conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assay"), "assaybinding");
////        } else {
////            binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, assayEcmPanelController.getAssayEcm3DPanel().getAssayComboBox(), BeanProperty.create("selectedItem"), conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assay"), "assaybinding");
////        }
////        bindingGroup.addBinding(binding);
////        bindingGroup.bind();
//
//    }
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
