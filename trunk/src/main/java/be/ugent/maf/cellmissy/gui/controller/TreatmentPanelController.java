/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.TreatmentDualList;
import be.ugent.maf.cellmissy.service.TreatmentService;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class TreatmentPanelController {

    //model
    //binding list for drugs
    private ObservableList<TreatmentType> drugBindingList;
    //binding list for general treatments
    private ObservableList<TreatmentType> generalTreatmentBindingList;
    //binding list for actual treatments
    private ObservableList<TreatmentType> actualTreatmentList;
    private BindingGroup bindingGroup;
    //view
    private TreatmentDualList treatmentDualList;
    //parent controller
    private ConditionsPanelController conditionsPanelController;
    //services
    private TreatmentService treatmentService;
    private GridBagConstraints gridBagConstraints;

    public TreatmentPanelController(ConditionsPanelController conditionsPanelController) {

        this.conditionsPanelController = conditionsPanelController;

        //init services
        treatmentService = (TreatmentService) conditionsPanelController.getSetupExperimentPanelController().getCellMissyController().getBeanByName("treatmentService");
        bindingGroup = new BindingGroup();

        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        treatmentDualList = new TreatmentDualList();
        initTreatmentSetupPanel();
    }

    public ObservableList<TreatmentType> getDrugBindingList() {
        return drugBindingList;
    }

//    public void updateTreatmentConditionFields(PlateCondition plateCondition) {
//        plateCondition.getTreatment().setType(((TreatmentCategory) (conditionsPanelController.getSetupConditionsPanel().getTreatmentTypeComboBox().getSelectedItem())).getDatabaseValue());
//        switch (plateCondition.getTreatment().getType()) {
//            case 1:
//                //if the type is a drug, bind to drug comboboxgetName
//                plateCondition.getTreatment().setName(((Treatment) (conditionsPanelController.getSetupConditionsPanel().getDrugComboBox().getSelectedItem())).getName());
//                break;
//            case 2:
//                //if it's a general treatment, bind to the other combobox
//                plateCondition.getTreatment().setName(((Treatment) (conditionsPanelController.getSetupConditionsPanel().getGeneralTreatmentComboBox().getSelectedItem())).getName());
//                break;
//        }
//    }
//
//    public void updateTreatmentInputFields(PlateCondition plateCondition) {
//        conditionsPanelController.getSetupConditionsPanel().getTreatmentTypeComboBox().setSelectedIndex(plateCondition.getTreatment().getType() - 1);
//        switch (plateCondition.getTreatment().getType()) {
//            case 1:
//                conditionsPanelController.getSetupConditionsPanel().getDrugComboBox().setSelectedIndex(drugBindingList.indexOf(plateCondition.getTreatment()));
//                break;
//            case 2:
//                conditionsPanelController.getSetupConditionsPanel().getGeneralTreatmentComboBox().setSelectedIndex(generalTreatmentBindingList.indexOf(plateCondition.getTreatment()));
//        }
//    }
    private void initTreatmentSetupPanel() {

        //init drug and general treatment binding lists
        drugBindingList = ObservableCollections.observableList(treatmentService.findByCategory(1));
        generalTreatmentBindingList = ObservableCollections.observableList(treatmentService.findByCategory(2));
        actualTreatmentList = ObservableCollections.observableList(new ArrayList<TreatmentType>());

        //init drug JList binding
        JListBinding drugListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, drugBindingList, treatmentDualList.getSourceList1());
        bindingGroup.addBinding(drugListBinding);

        //init general treatment JList binding
        JListBinding generalTreatmentListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, generalTreatmentBindingList, treatmentDualList.getSourceList2());
        bindingGroup.addBinding(generalTreatmentListBinding);

        //init actual treatment JList binding
        JListBinding actualTreatmentListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, actualTreatmentList, treatmentDualList.getDestinationList());
        bindingGroup.addBinding(actualTreatmentListBinding);
        bindingGroup.bind();

        //add action listeners
        treatmentDualList.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treatmentDualList.getSourceList1().getSelectedValue() != null) {
                    //remove it from the source List and add it to the destination List
                    actualTreatmentList.add((TreatmentType) treatmentDualList.getSourceList1().getSelectedValue());
                    drugBindingList.remove((TreatmentType) treatmentDualList.getSourceList1().getSelectedValue());
                }

                if (treatmentDualList.getSourceList2().getSelectedValue() != null) {
                    //remove it from the source List and add it to the destination List
                    actualTreatmentList.add((TreatmentType) treatmentDualList.getSourceList2().getSelectedValue());
                    generalTreatmentBindingList.remove((TreatmentType) treatmentDualList.getSourceList2().getSelectedValue());
                }
            }
        });

        treatmentDualList.getRemoveButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //remove it from the destination List and add it to the "right" source List
                if (treatmentDualList.getDestinationList().getSelectedValue() != null) {
                    switch (((TreatmentType) treatmentDualList.getDestinationList().getSelectedValue()).getTreatmentCategory()) {
                        case 1:
                            drugBindingList.add((TreatmentType) treatmentDualList.getDestinationList().getSelectedValue());
                            break;
                        case 2:
                            generalTreatmentBindingList.add((TreatmentType) treatmentDualList.getDestinationList().getSelectedValue());
                    }
                    actualTreatmentList.remove((TreatmentType) treatmentDualList.getDestinationList().getSelectedValue());
                }
            }
        });

        //add mouse listeners
        treatmentDualList.getSourceList1().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                treatmentDualList.getSourceList2().clearSelection();
            }
        });

        treatmentDualList.getSourceList2().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                treatmentDualList.getSourceList1().clearSelection();
            }
        });

        conditionsPanelController.getSetupConditionsPanel().getTreatmentDualListParent().add(treatmentDualList, gridBagConstraints);
    }
}
