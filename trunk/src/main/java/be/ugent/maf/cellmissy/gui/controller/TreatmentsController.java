/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.AddDrugsTreatmentsPanel;
import be.ugent.maf.cellmissy.gui.experiment.TreatmentsPanel;
import be.ugent.maf.cellmissy.service.TreatmentService;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.persistence.PersistenceException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Treatment Panel Controller: setup treatments details for each condition during experiment design
 * Parent Controller: Conditions Panel Controller
 * @author Paola
 */
@Controller("treatmentsController")
public class TreatmentsController {

    //model
    //binding list for drugs
    private ObservableList<TreatmentType> drugBindingList;
    //binding list for general treatments
    private ObservableList<TreatmentType> generalTreatmentBindingList;
    //binding list for actual treatment
    private ObservableList<Treatment> treatmentBindingList;
    private ObservableList<String> drugSolventList;
    private BindingGroup bindingGroup;
    //view
    private TreatmentsPanel treatmentsPanel;
    //parent controller
    @Autowired
    private SetupConditionsController setupConditionsController;
    //services
    @Autowired
    private TreatmentService treatmentService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize constructor
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        
        //create panel
        treatmentsPanel = new TreatmentsPanel();
        
        //init views
        initTreatmentSetupPanel();
        initMainPanel();
    }

    /**
     * getters and setters
     */
    public ObservableList<TreatmentType> getDrugBindingList() {
        return drugBindingList;
    }

    public TreatmentsPanel getTreatmentsPanel() {
        return treatmentsPanel;
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
            Treatment newTreatment = new Treatment(treatment.getConcentration(), treatment.getTreatmentType());
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
        JListBinding drugListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, drugBindingList, treatmentsPanel.getSourceList1());
        bindingGroup.addBinding(drugListBinding);

        //init general treatment JList binding
        JListBinding generalTreatmentListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, generalTreatmentBindingList, treatmentsPanel.getSourceList2());
        bindingGroup.addBinding(generalTreatmentListBinding);

        //init actual treatment JList binding
        JListBinding actualTreatmentListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, treatmentBindingList, treatmentsPanel.getDestinationList());
        bindingGroup.addBinding(actualTreatmentListBinding);

        //init drug solvents JCombobox
        drugSolventList = ObservableCollections.observableList(treatmentService.findAllDrugSolvents());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, drugSolventList, treatmentsPanel.getDrugSolventComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //set cell renderer
        treatmentsPanel.getDestinationList().setCellRenderer(new TreatmentsRenderer());
        //autobind treatment
        //treatment timing (Time of Addition)
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentsPanel.getDestinationList(), BeanProperty.create("selectedElement.timing"), treatmentsPanel.getTimingTextField(), BeanProperty.create("text"), "treatmenttimingbinding");
        bindingGroup.addBinding(binding);
        //treatment concentration
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentsPanel.getDestinationList(), BeanProperty.create("selectedElement.concentration"), treatmentsPanel.getConcentrationTextField(), BeanProperty.create("text"), "treatmentconcentrationbinding");
        bindingGroup.addBinding(binding);
        //treatment concentration unit of measure
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentsPanel.getDestinationList(), BeanProperty.create("selectedElement.concentrationUnit"), treatmentsPanel.getConcentrationUnitComboBox(), BeanProperty.create("selectedItem"), "treatmentconcentrationunitbinding");
        bindingGroup.addBinding(binding);
        //treatment (drug) solvent
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentsPanel.getDestinationList(), BeanProperty.create("selectedElement.drugSolvent"), treatmentsPanel.getDrugSolventComboBox(), BeanProperty.create("selectedItem"), "treatmentsolventbinding");
        bindingGroup.addBinding(binding);
        //treatment (drug) solvent final concentration
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, treatmentsPanel.getDestinationList(), BeanProperty.create("selectedElement.drugSolventConcentration"), treatmentsPanel.getSolventConcentrationTextField(), BeanProperty.create("text"), "treatmentsolventconcentrationbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

        //unit of measure combobox
        treatmentsPanel.getConcentrationUnitComboBox().addItem("\u00B5" + "M");
        treatmentsPanel.getConcentrationUnitComboBox().addItem("\u00B5" + "g" + "\\" + "\u00B5" + "l");

        //add action listeners
        treatmentsPanel.getAddNewButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //show internal frame with options to add new drugs/treatments to the DB
                InternalFrame internalFrame = new InternalFrame("Add new Drugs/Treatments to the DB");
                internalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });


        //add a drug/treatment to the actual treatment list
        treatmentsPanel.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treatmentsPanel.getSourceList1().getSelectedValue() != null) {
                    //move a treatment from a source list to the destination list
                    addTreatmentFromASourceList(treatmentsPanel.getSourceList1());
                    drugBindingList.remove((TreatmentType) treatmentsPanel.getSourceList1().getSelectedValue());
                }

                if (treatmentsPanel.getSourceList2().getSelectedValue() != null) {
                    //move a treatment from a source list to the destination list
                    addTreatmentFromASourceList(treatmentsPanel.getSourceList2());
                    generalTreatmentBindingList.remove((TreatmentType) treatmentsPanel.getSourceList2().getSelectedValue());
                }
            }
        });

        //remove a drug/treatment from the actual treatment list
        treatmentsPanel.getRemoveButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //remove it from the destination List and add it to the "right" source List
                if (treatmentsPanel.getDestinationList().getSelectedValue() != null) {
                    switch (((Treatment) treatmentsPanel.getDestinationList().getSelectedValue()).getTreatmentType().getTreatmentCategory()) {
                        case 1:
                            drugBindingList.add(((Treatment) treatmentsPanel.getDestinationList().getSelectedValue()).getTreatmentType());
                            break;
                        case 2:
                            generalTreatmentBindingList.add(((Treatment) treatmentsPanel.getDestinationList().getSelectedValue()).getTreatmentType());
                    }
                    treatmentBindingList.remove((Treatment) treatmentsPanel.getDestinationList().getSelectedValue());
                }
            }
        });

        //add mouse listeners
        //select drug OR general treatment
        treatmentsPanel.getSourceList1().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                treatmentsPanel.getSourceList2().clearSelection();
            }
        });

        treatmentsPanel.getSourceList2().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                treatmentsPanel.getSourceList1().clearSelection();
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

    /**
     * this method adds treatments/drugs from a source list to the destination list
     * @param sourceList 
     */
    private void addTreatmentFromASourceList(JList sourceList) {
        Treatment treatment = new Treatment();
        treatment.setTreatmentType((TreatmentType) sourceList.getSelectedValue());
        initTreatment(treatment);
        treatmentBindingList.add(treatment);
        treatmentsPanel.getDestinationList().setSelectedIndex(treatmentBindingList.indexOf(treatment));
    }

    /**
     * this method sets some default parameters for a treatment
     * @param treatment 
     */
    private void initTreatment(Treatment treatment) {
        treatment.setConcentration(0.5);
        treatment.setConcentrationUnit(treatmentsPanel.getConcentrationUnitComboBox().getItemAt(0).toString());
        treatment.setTiming("0 h");
        treatment.setDrugSolvent(null);
        treatment.setDrugSolventConcentration(0.50);

    }

    /**
     * initialize view (treatment panel)
     */
    private void initMainPanel() {
        setupConditionsController.getSetupConditionsPanel().getTreatmentParentPanel().add(treatmentsPanel, gridBagConstraints);
    }

    /**
     * show internal frame to add new treatments/drugs to DB
     */
    private class InternalFrame extends JFrame {

        AddDrugsTreatmentsPanel addDrugsTreatmentsPanel;
        //constructor

        public InternalFrame(String title) {
            super(title);
            initFrame();
        }

        //create and show frame
        private void initFrame() {
            JFrame frame = new JFrame();
            frame.setVisible(true);
            frame.setBounds(100, 100, 350, 250);
            frame.setLayout(new GridBagLayout());
            frame.setAlwaysOnTop(true);
            addDrugsTreatmentsPanel = new AddDrugsTreatmentsPanel();

            //add action listeners
            //add new Drug to the DB
            addDrugsTreatmentsPanel.getAddDrugButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!addDrugsTreatmentsPanel.getDrugTextField().getText().isEmpty()) {
                        TreatmentType newDrug = new TreatmentType();
                        //category 1: drug
                        newDrug.setTreatmentCategory(1);
                        newDrug.setName(addDrugsTreatmentsPanel.getDrugTextField().getText());
                        try {
                            //add drug to the list
                            drugBindingList.add(newDrug);
                            //save drug to DB
                            treatmentService.saveTreatmentType(newDrug);
                            setupConditionsController.showMessage("Drug was inserted into DB.", 1);
                            addDrugsTreatmentsPanel.getDrugTextField().setText("");
                        } catch (PersistenceException exception) {
                            setupConditionsController.showMessage("Drug already present in DB.", 1);
                            addDrugsTreatmentsPanel.getDrugTextField().setText("");
                            addDrugsTreatmentsPanel.getDrugTextField().requestFocusInWindow();
                        }
                    }
                }
            });

            //add new Treatment to the DB
            addDrugsTreatmentsPanel.getAddTreatmentButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!addDrugsTreatmentsPanel.getTreatmentTextField().getText().isEmpty()) {
                        TreatmentType newTreatment = new TreatmentType();
                        //category 2: general treatment
                        newTreatment.setTreatmentCategory(2);
                        newTreatment.setName(addDrugsTreatmentsPanel.getTreatmentTextField().getText());
                        try {
                            //add treatment to the list
                            generalTreatmentBindingList.add(newTreatment);
                            //save treatment to DB
                            treatmentService.saveTreatmentType(newTreatment);
                            setupConditionsController.showMessage("Treatment was inserted into DB.", 1);
                            addDrugsTreatmentsPanel.getTreatmentTextField().setText("");
                        } catch (PersistenceException exception) {
                            setupConditionsController.showMessage("Treatment already present in DB.", 1);
                            addDrugsTreatmentsPanel.getTreatmentTextField().setText("");
                            addDrugsTreatmentsPanel.getTreatmentTextField().requestFocusInWindow();
                        }
                    }
                }
            });
            //add the panel to the frame
            frame.add(addDrugsTreatmentsPanel, gridBagConstraints);
            addDrugsTreatmentsPanel.getDrugTextField().requestFocusInWindow();
        }
    }

    /**
     * customized cell renderer for treatment list
     */
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
