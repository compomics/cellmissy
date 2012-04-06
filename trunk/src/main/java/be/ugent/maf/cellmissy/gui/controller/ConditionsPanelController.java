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
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
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
    private PlateCondition newPlateCondition;
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
    //services
    private CellLineService cellLineService;
    private GridBagConstraints gridBagConstraints;

    public ConditionsPanelController(ExperimentSetupPanelController experimentSetupPanelController) {

        this.experimentSetupPanelController = experimentSetupPanelController;

        //init services
        cellLineService = (CellLineService) experimentSetupPanelController.getCellMissyController().getBeanByName("cellLineService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        conditionsPanel = new ConditionsPanel();
        conditionsSetupPanel = new ConditionsSetupPanel();
        initCellLinePanel();
        initConditionsPanel();
        initPanel();

        //init child controllers
        assayEcmPanelController = new AssayEcmPanelController(this);
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
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, cellLineBindingList, conditionsSetupPanel.getCellLineComboBox());
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

        //init conditionJList (empty list)
        plateConditionBindingList = ObservableCollections.observableList(new ArrayList<PlateCondition>());

        //set cell renderer for conditionJList (to show name of condition instead of toString method)
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsRenderer());

        JListBinding conditionListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsPanel.getConditionsJList());
        bindingGroup.addBinding(conditionListBinding);

//        //init condition binding
//        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, conditionsPanel.getConditionNameTextField(), ELProperty.create("${text}"), newPlateCondition, BeanProperty.create("name"), "namebinding");
//        bindingGroup.addBinding(binding);

        //bind
        bindingGroup.bind();

        //add action listeners
        //add condition action
        conditionsPanel.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //create a new condition and set the name
                newPlateCondition = new PlateCondition();
                newPlateCondition.setName("Condition " + (plateConditionBindingList.size() + 1));
                //add the new condition to the list
                plateConditionBindingList.add(newPlateCondition);
            }
        });

        //remove condition action
        conditionsPanel.getRemoveButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (conditionsPanel.getConditionsJList().getSelectedValue() != null) {
                    plateConditionBindingList.remove((PlateCondition) conditionsPanel.getConditionsJList().getSelectedValue());
                }
            }
        });

    }

    private class ConditionsRenderer extends DefaultListCellRenderer {

        public ConditionsRenderer() {
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            newPlateCondition = (PlateCondition) value;
            setText(newPlateCondition.getName());
            return this;
        }
    }

    private void initPanel() {
        experimentSetupPanelController.getExperimentSetupPanel().getConditionsParentPanel().add(conditionsPanel, gridBagConstraints);
        experimentSetupPanelController.getExperimentSetupPanel().getConditionsSetupParentPanel().add(conditionsSetupPanel, gridBagConstraints);
    }
}
