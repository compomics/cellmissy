/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
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
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
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
    private Integer previousConditionIndex;

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
        previousConditionIndex = -1;

        //set cell renderer for conditionJList (to show condition name instead of to String)
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsRenderer());

        //init conditionJList (create new empty list) (conditions are NOT retrieved from DB)
        plateConditionBindingList = ObservableCollections.observableList(new ArrayList<PlateCondition>());

        //autobind cell line and matrix dimension
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine"), conditionsSetupPanel.getCellLineComboBox(), BeanProperty.create("selectedItem"), "celllinebinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.matrixDimension"), conditionsSetupPanel.getEcmDimensionComboBox(), BeanProperty.create("selectedItem"), "matrixdimensionbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

        //autobind treatment
        //treatment type and name are bound manually in the treatment panel controller

        //treatment description
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.treatment.description"), conditionsSetupPanel.getAdditionalInfoTextField(), BeanProperty.create("text"), "treatmentdescriptionbinding");
        bindingGroup.addBinding(binding);
        //treatment timing
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.treatment.timing"), conditionsSetupPanel.getTimingTextField(), BeanProperty.create("text"), "treatmenttimingbinding");
        bindingGroup.addBinding(binding);
        //treatment concentration
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.treatment.concentration"), conditionsSetupPanel.getConcentrationTextField(), BeanProperty.create("text"), "treatmentconcentrationbinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //init conditionListBinding
        JListBinding conditionListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsPanel.getConditionsJList());
        bindingGroup.addBinding(conditionListBinding);
        bindingGroup.bind();

        //add mouse listener
        conditionsPanel.getConditionsJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int locationToIndex = conditionsPanel.getConditionsJList().locationToIndex(e.getPoint());
                if (previousConditionIndex != -1) {
                    assayEcmPanelController.updateAssayEcmConditionFields(plateConditionBindingList.get(previousConditionIndex));
                    assayEcmPanelController.updateAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                    assayEcmPanelController.resetAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                    treatmentPanelController.updateTreatmentConditionFields(plateConditionBindingList.get(previousConditionIndex));
                    treatmentPanelController.updateTreatmentInputFields(plateConditionBindingList.get(locationToIndex));
                }
                previousConditionIndex = locationToIndex;
            }
        });

        //add action listeners
        //add a new condition to the List
        conditionsPanel.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //create and init a new condition
                PlateCondition newPlateCondition = new PlateCondition();
                initCondition(newPlateCondition);
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

    private void initCondition(PlateCondition plateCondition) {

        //assign defaults fields to a new condition
        plateCondition.setName("Condition " + ++conditionIndex);
        plateCondition.setCellLine(cellLineBindingList.get(0));
        plateCondition.setMatrixDimension(assayEcmPanelController.getMatrixDimensionBindingList().get(0));
        plateCondition.setAssay(assayEcmPanelController.getAssay2DBindingList().get(0));
        Ecm ecm = new Ecm();
        ecm.setEcmComposition(assayEcmPanelController.getEcm2DCompositionBindingList().get(0));
        ecm.setEcmCoating(assayEcmPanelController.getEcmCoatingBindingList().get(0));
        ecm.setCoatingTemperature("");
        ecm.setCoatingTime("");
        ecm.setConcentration(0);
        ecm.setVolume(0);
        plateCondition.setEcm(ecm);
        Treatment treatment = new Treatment();
        treatment.setType(TreatmentType.DRUG.getDatabaseValue());
        treatment.setName(treatmentPanelController.getDrugBindingList().get(0).getName());
        plateCondition.setTreatment(treatment);
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
