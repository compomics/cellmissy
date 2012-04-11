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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private PlateCondition newPlateCondition;
    private PlateCondition selectedPlateCondition;
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
        initCellLinePanel();
        initConditionsPanel();

        //init child controllers
        assayEcmPanelController = new AssayEcmPanelController(this);

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

        //init conditionJList (create new empty list) (conditions are NOT retrieved from DB)
        plateConditionBindingList = ObservableCollections.observableList(new ArrayList<PlateCondition>());
        //create a new (default) first condition and add it to the list
        newPlateCondition = new PlateCondition();
        conditionIndex = 1;
        newPlateCondition.setName("Condition " + conditionIndex);
        plateConditionBindingList.add(newPlateCondition);

        //set cell renderer for conditionJList (to show condition name instead of toString-method return)
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsRenderer());

        //init conditionListBinding
        JListBinding conditionListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsPanel.getConditionsJList());
        bindingGroup.addBinding(conditionListBinding);
        //bind
        bindingGroup.bind();

        //select as default the first condition (to init the binding)
        conditionsPanel.getConditionsJList().setSelectedIndex(0);

        //add action listeners
        //add a new condition to the List
        conditionsPanel.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //create a new condition and set the name
                newPlateCondition = new PlateCondition();
                newPlateCondition.setName("Condition " + ++conditionIndex);
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

        conditionsPanel.getConditionsJList().addMouseListener(new MouseAdapter() {
        
            
        });
        
                           
    }

    private void initPlateCondition() {

        //bind cell line
        Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, conditionsSetupPanel.getCellLineComboBox(), BeanProperty.create("selectedItem"), conditionsPanel.getConditionsJList().getSelectedValue(), BeanProperty.create("cellLine"), "celllinebinding");
        bindingGroup.addBinding(binding);
        //bind ecm dimension
        binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, conditionsSetupPanel.getEcmDimensionComboBox(), BeanProperty.create("selectedItem"), conditionsPanel.getConditionsJList().getSelectedValue(), BeanProperty.create("matrixDimension"), "matrixdimensionbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();
        //bind assay, depending on 2D/3D ecm
        if ((selectedPlateCondition.getMatrixDimension().getMatrixDimension().equals("2D"))) {
            binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, assayEcmPanelController.getAssayEcm2DPanel().getAssayComboBox(), BeanProperty.create("selectedItem"), conditionsPanel.getConditionsJList().getSelectedValue(), BeanProperty.create("assay"), "assaybinding");
        } else {
            binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, assayEcmPanelController.getAssayEcm3DPanel().getAssayComboBox(), BeanProperty.create("selectedItem"), conditionsPanel.getConditionsJList().getSelectedValue(), BeanProperty.create("assay"), "assaybinding");
        }
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

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
        //initPlateCondition();
    }
}
