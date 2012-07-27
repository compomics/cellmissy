/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.TreatmentPanel;
import be.ugent.maf.cellmissy.service.TreatmentService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
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
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;

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
    //binding list for actual treatment
    private ObservableList<Treatment> treatmentBindingList;
    private BindingGroup bindingGroup;
    //view
    private TreatmentPanel treatmentPanel;
    //parent controller
    private ConditionsPanelController conditionsPanelController;
    //services
    private ApplicationContext context;
    private TreatmentService treatmentService;
    private GridBagConstraints gridBagConstraints;

    public TreatmentPanelController(ConditionsPanelController conditionsPanelController) {

        this.conditionsPanelController = conditionsPanelController;

        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        treatmentService = (TreatmentService) context.getBean("treatmentService");
        bindingGroup = new BindingGroup();

        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        treatmentPanel = new TreatmentPanel();

        initTreatmentSetupPanel();
        initPanel();
    }

    public ObservableList<TreatmentType> getDrugBindingList() {
        return drugBindingList;
    }

    public TreatmentPanel getTreatmentPanel() {
        return treatmentPanel;
    }

    /**
     * update treatment collection for previously selected condition
     */
    public void updateTreatmentCollection(PlateCondition plateCondition) {
        for (Treatment treatment : treatmentBindingList) {
            //set plate condition of the treatment
            treatment.setPlateCondition(plateCondition);
            //update treatment collection of the plate condition
            if (!plateCondition.getTreatmentCollection().contains(treatment)) {
                plateCondition.getTreatmentCollection().add(treatment);
            }
        }
    }

    /**
     * this method is used inn the condition panel controller to actually show the current treatment list and sync the source lists according to the last one
     * @param plateCondition 
     */
    public void updateTreatmentLists(PlateCondition plateCondition) {
        //update source lists: drugBindingList and generalTreatmentBindingList
        updateSourceLists();
        //empty the treatment binding list to show the actual one
        treatmentBindingList.clear();
        //update the treatment list
        updateDestinationList(plateCondition);
    }

    public void initTreatmentList(PlateCondition plateCondition) {
        //empty the list and fill it with new treatments, copying all fields from previous treatment collection
        treatmentBindingList.clear();

        for (Treatment treatment : plateCondition.getTreatmentCollection()) {
            Treatment newTreatment = new Treatment(treatment.getDescription(), treatment.getConcentration(), treatment.getTiming(), treatment.getAssayMedium(), treatment.getTreatmentType());
            treatmentBindingList.add(newTreatment);
        }
    }

    /**
     * private methods and classes
     *  
     */
    private void initTreatmentSetupPanel() {

        //init drug and general treatment binding lists
        drugBindingList = ObservableCollections.observableList(treatmentService.findByCategory(1));
        generalTreatmentBindingList = ObservableCollections.observableList(treatmentService.findByCategory(2));
        treatmentBindingList = ObservableCollections.observableList(new ArrayList<Treatment>());

        //init drug JList binding
        JListBinding drugListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, drugBindingList, treatmentPanel.getSourceList1());
        bindingGroup.addBinding(drugListBinding);

        //init general treatment JList binding
        JListBinding generalTreatmentListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, generalTreatmentBindingList, treatmentPanel.getSourceList2());
        bindingGroup.addBinding(generalTreatmentListBinding);

        //init actual treatment JList binding
        JListBinding actualTreatmentListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, treatmentBindingList, treatmentPanel.getDestinationList());
        bindingGroup.addBinding(actualTreatmentListBinding);

        treatmentPanel.getDestinationList().setCellRenderer(new TreatmentsRenderer());
        //autobind treatment
        //treatment description
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentPanel.getDestinationList(), BeanProperty.create("selectedElement.description"), treatmentPanel.getAdditionalInfoTextArea(), BeanProperty.create("text"), "treatmentdescriptionbinding");
        bindingGroup.addBinding(binding);
        //treatment timing
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentPanel.getDestinationList(), BeanProperty.create("selectedElement.timing"), treatmentPanel.getTimingTextField(), BeanProperty.create("text"), "treatmenttimingbinding");
        bindingGroup.addBinding(binding);
        //treatment concentration
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentPanel.getDestinationList(), BeanProperty.create("selectedElement.concentration"), treatmentPanel.getConcentrationTextField(), BeanProperty.create("text"), "treatmentconcentrationbinding");
        bindingGroup.addBinding(binding);
        //treatment concentration unit of measure
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentPanel.getDestinationList(), BeanProperty.create("selectedElement.concentrationUnitOfMeasure"), treatmentPanel.getConcentrationUnitComboBox(), BeanProperty.create("selectedItem"), "treatmentconcentrationunitofmeasurebinding");
        bindingGroup.addBinding(binding);
        //treatment assay medium
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentPanel.getDestinationList(), BeanProperty.create("selectedElement.assayMedium"), treatmentPanel.getAssayMediumComboBox(), BeanProperty.create("selectedItem"), "treatmentassaymediumbinding");
        bindingGroup.addBinding(binding);
        //treatment serum concentration binding
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentPanel.getDestinationList(), BeanProperty.create("selectedElement.serumConcentration"), treatmentPanel.getSerumConcentrationTextField(), BeanProperty.create("text"), "treatmentserumconcentrationbinding");
        bindingGroup.addBinding(binding);
        //treatment (drug) solvent
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentPanel.getDestinationList(), BeanProperty.create("selectedElement.drugSolvent"), treatmentPanel.getDrugSolventComboBox(), BeanProperty.create("selectedItem"), "treatmentdrugsolventbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

        //unit of measure combobox
        treatmentPanel.getConcentrationUnitComboBox().addItem("\u00B5" + "M");
        treatmentPanel.getConcentrationUnitComboBox().addItem("\u00B5" + "g" + "\\" + "\u00B5" + "l");

        //set default to microM
        treatmentPanel.getConcentrationUnitComboBox().setSelectedIndex(0);

        //fill in drug solvent combo box
        treatmentPanel.getDrugSolventComboBox().addItem("");
        treatmentPanel.getDrugSolventComboBox().addItem("DMSO");
        treatmentPanel.getDrugSolventComboBox().addItem("PBS");

        //set default to null
        treatmentPanel.getDrugSolventComboBox().setSelectedIndex(0);

        //add action listeners
        //add a drug/treatment to the actual treatment list
        treatmentPanel.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treatmentPanel.getSourceList1().getSelectedValue() != null) {
                    //move a treatment from a source list to the destination list
                    addTreatmentFromASourceList(treatmentPanel.getSourceList1());
                    drugBindingList.remove((TreatmentType) treatmentPanel.getSourceList1().getSelectedValue());
                }

                if (treatmentPanel.getSourceList2().getSelectedValue() != null) {
                    //move a treatment from a source list to the destination list
                    addTreatmentFromASourceList(treatmentPanel.getSourceList2());
                    generalTreatmentBindingList.remove((TreatmentType) treatmentPanel.getSourceList2().getSelectedValue());
                }
            }
        });

        //remove a drug/treatment from the actual treatment list
        treatmentPanel.getRemoveButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //remove it from the destination List and add it to the "right" source List
                if (treatmentPanel.getDestinationList().getSelectedValue() != null) {
                    switch (((Treatment) treatmentPanel.getDestinationList().getSelectedValue()).getTreatmentType().getTreatmentCategory()) {
                        case 1:
                            drugBindingList.add(((Treatment) treatmentPanel.getDestinationList().getSelectedValue()).getTreatmentType());
                            break;
                        case 2:
                            generalTreatmentBindingList.add(((Treatment) treatmentPanel.getDestinationList().getSelectedValue()).getTreatmentType());
                    }
                    treatmentBindingList.remove((Treatment) treatmentPanel.getDestinationList().getSelectedValue());
                }
            }
        });

        //add a new drug to the DB
        treatmentPanel.getAddDrugButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!treatmentPanel.getDrugTextField().getText().isEmpty()) {
                    TreatmentType newDrug = new TreatmentType();
                    newDrug.setTreatmentCategory(1);
                    newDrug.setName(treatmentPanel.getDrugTextField().getText());
                    drugBindingList.add(newDrug);
                    treatmentService.saveTreatmentType(newDrug);
                    treatmentPanel.getDrugTextField().setText("");
                }
            }
        });

        //add a new treatment to the DB
        treatmentPanel.getAddTreatmentButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!treatmentPanel.getTreatmentTextField().getText().isEmpty()) {
                    TreatmentType newTreatment = new TreatmentType();
                    newTreatment.setTreatmentCategory(2);
                    newTreatment.setName(treatmentPanel.getTreatmentTextField().getText());
                    generalTreatmentBindingList.add(newTreatment);
                    treatmentService.saveTreatmentType(newTreatment);
                    treatmentPanel.getTreatmentTextField().setText("");
                }
            }
        });

        //add mouse listeners
        //select drug OR general treatment
        treatmentPanel.getSourceList1().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                treatmentPanel.getSourceList2().clearSelection();
            }
        });

        treatmentPanel.getSourceList2().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                treatmentPanel.getSourceList1().clearSelection();
            }
        });
    }

    /**
     * this method updates drug and general treatment lists according to actual list of treatment for current condition
     */
    private void updateSourceLists() {
        for (Treatment treatment : treatmentBindingList) {
            switch (treatment.getTreatmentType().getTreatmentCategory()) {
                //drug
                case 1:
                    if (!drugBindingList.contains(treatment.getTreatmentType())) {
                        drugBindingList.add(treatment.getTreatmentType());
                    }
                    break;
                //general treatment
                case 2:
                    if (!generalTreatmentBindingList.contains(treatment.getTreatmentType())) {
                        generalTreatmentBindingList.add(treatment.getTreatmentType());
                    }
                    break;
            }
        }
    }

    /**
     * this method updates the destination list (actual treatment list for current condition) and sync its changes with the two source lists
     * @param plateCondition 
     */
    private void updateDestinationList(PlateCondition plateCondition) {
        //fill in the treatment binding list with the acutually treatments for the current condition
        if (!plateCondition.getTreatmentCollection().isEmpty()) {
            for (Treatment treatment : plateCondition.getTreatmentCollection()) {
                if (!treatmentBindingList.contains(treatment)) {
                    treatmentBindingList.add(treatment);
                }
                switch (treatment.getTreatmentType().getTreatmentCategory()) {
                    //drug
                    case 1:
                        if (drugBindingList.contains(treatment.getTreatmentType())) {
                            drugBindingList.remove(treatment.getTreatmentType());
                        }
                        break;
                    //general treatment
                    case 2:
                        if (generalTreatmentBindingList.contains(treatment.getTreatmentType())) {
                            generalTreatmentBindingList.remove(treatment.getTreatmentType());
                        }
                        break;
                }
            }
        }
    }

    private void addTreatmentFromASourceList(JList sourceList) {
        Treatment treatment = new Treatment();
        treatment.setTreatmentType((TreatmentType) sourceList.getSelectedValue());
        initTreatment(treatment);
        treatmentBindingList.add(treatment);
        treatmentPanel.getDestinationList().setSelectedIndex(treatmentBindingList.indexOf(treatment));
    }

    /**
     * this method sets some default parameters for a treatment
     * @param treatment 
     */
    private void initTreatment(Treatment treatment) {
        treatment.setConcentration(0.5);
        treatment.setConcentrationUnit(treatmentPanel.getConcentrationUnitComboBox().getItemAt(0).toString());
        treatment.setDescription("Please add some information here");
        treatment.setTiming("10 hours");
        treatment.setAssayMedium(conditionsPanelController.getMediumBindingList().get(0));
        treatment.setSerumConcentration("10%");
    }

    private void initPanel() {
        conditionsPanelController.getSetupConditionsPanel().getTreatmentParentPanel().add(treatmentPanel, gridBagConstraints);
    }

    private class TreatmentsRenderer extends DefaultListCellRenderer {

        public TreatmentsRenderer() {
            setOpaque(true);
        }

        //Overrides method from the DefaultListCellRenderer
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Treatment treatment = (Treatment) value;
            setText(treatment.getTreatmentType().getName());
            return this;
        }
    }
}
