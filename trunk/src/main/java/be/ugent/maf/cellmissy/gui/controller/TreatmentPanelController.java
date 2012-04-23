/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.service.TreatmentService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class TreatmentPanelController {

    //model
    //binding list for drugs
    private ObservableList<Treatment> drugBindingList;
    //binding list for general treatments
    private ObservableList<Treatment> generalTreatmentBindingList;
    private BindingGroup bindingGroup;
    //view
    //parent controller
    private ConditionsPanelController conditionsPanelController;
    //services
    private TreatmentService treatmentService;

    public TreatmentPanelController(ConditionsPanelController conditionsPanelController) {

        this.conditionsPanelController = conditionsPanelController;

        //init services
        treatmentService = (TreatmentService) conditionsPanelController.getExperimentSetupPanelController().getCellMissyController().getBeanByName("treatmentService");
        bindingGroup = new BindingGroup();

        //init views
        initTreatmentTypePanel();
        initTreatmentSetupPanel();
    }

    public ObservableList<Treatment> getDrugBindingList() {
        return drugBindingList;
    }

    public void updateTreatmentConditionFields(PlateCondition plateCondition) {
        plateCondition.getTreatment().setType(((TreatmentType) (conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().getSelectedItem())).getDatabaseValue());
        switch (plateCondition.getTreatment().getType()) {
            case 1:
                //if the type is a drug, bind to drug comboboxgetName
                plateCondition.getTreatment().setName(((Treatment) (conditionsPanelController.getConditionsSetupPanel().getDrugComboBox().getSelectedItem())).getName());
                break;
            case 2:
                //if it's a general treatment, bind to the other combobox
                plateCondition.getTreatment().setName(((Treatment) (conditionsPanelController.getConditionsSetupPanel().getGeneralTreatmentComboBox().getSelectedItem())).getName());
                break;
        }
    }

    public void updateTreatmentInputFields(PlateCondition plateCondition) {
        conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().setSelectedIndex(plateCondition.getTreatment().getType() - 1);
        switch (plateCondition.getTreatment().getType()) {
            case 1:
                conditionsPanelController.getConditionsSetupPanel().getDrugComboBox().setSelectedIndex(drugBindingList.indexOf(plateCondition.getTreatment()));
                break;
            case 2:
                conditionsPanelController.getConditionsSetupPanel().getGeneralTreatmentComboBox().setSelectedIndex(generalTreatmentBindingList.indexOf(plateCondition.getTreatment()));
        }
    }

    private void initTreatmentTypePanel() {

        //fill in tratment type ComboBox 
        for (TreatmentType treatmentType : TreatmentType.values()) {
            conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().addItem(treatmentType);
        }
    }

    private void initTreatmentSetupPanel() {

        //init drug and general treatment binding lists
        drugBindingList = ObservableCollections.observableList(treatmentService.findByType(1));
        generalTreatmentBindingList = ObservableCollections.observableList(treatmentService.findByType(2));

        //init drug combobox binding
        JComboBoxBinding drugComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, drugBindingList, conditionsPanelController.getConditionsSetupPanel().getDrugComboBox());
        bindingGroup.addBinding(drugComboBoxBinding);

        //init general treatment combobox binding
        JComboBoxBinding generalTreatmentComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, generalTreatmentBindingList, conditionsPanelController.getConditionsSetupPanel().getGeneralTreatmentComboBox());
        bindingGroup.addBinding(generalTreatmentComboBoxBinding);
        bindingGroup.bind();

        //add action listener
        conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                switch (((TreatmentType) conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().getSelectedItem())) {
                    case DRUG:
                        conditionsPanelController.getConditionsSetupPanel().getGeneralTreatmentComboBox().setVisible(false);
                        conditionsPanelController.getConditionsSetupPanel().getDrugComboBox().setVisible(true);
                        break;
                    case GENERAL_TREATMENT:
                        conditionsPanelController.getConditionsSetupPanel().getDrugComboBox().setVisible(false);
                        conditionsPanelController.getConditionsSetupPanel().getGeneralTreatmentComboBox().setVisible(true);
                        break;
                }
            }
        });

        conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().setSelectedIndex(0);
        conditionsPanelController.getConditionsSetupPanel().getGeneralTreatmentComboBox().setSelectedIndex(0);
        conditionsPanelController.getConditionsSetupPanel().getDrugComboBox().setSelectedIndex(0);
    }
}
