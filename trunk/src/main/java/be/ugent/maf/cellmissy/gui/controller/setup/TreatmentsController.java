/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.gui.experiment.setup.AddDrugsTreatmentsDialog;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.setup.TreatmentsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.TreatmentsRenderer;
import be.ugent.maf.cellmissy.service.TreatmentService;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
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
 * Treatment Panel Controller: setup treatments details for each condition
 * during experiment design Parent Controller: Conditions Panel Controller
 *
 * @author Paola
 */
@Controller("treatmentsController")
public class TreatmentsController {

    private static final Logger LOG = Logger.getLogger(TreatmentsController.class);
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
    private AddDrugsTreatmentsDialog addDrugsTreatmentsDialog;
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
     *
     * @return
     */
    public ObservableList<TreatmentType> getDrugBindingList() {
        return drugBindingList;
    }

    public TreatmentsPanel getTreatmentsPanel() {
        return treatmentsPanel;
    }

    public ObservableList<Treatment> getTreatmentBindingList() {
        return treatmentBindingList;
    }

    /**
     * Taking a list of treatment type, we add them to the GUI-models; and we do
     * it according to them category: drug or more general treatment,
     *
     * @param treatmentTypes
     */
    public void addNewTreatmentTypes(List<TreatmentType> treatmentTypes) {
        for (TreatmentType treatmentType : treatmentTypes) {
            switch (treatmentType.getTreatmentCategory()) {
                case 1:
                    drugBindingList.add(treatmentType);
                    break;
                case 2:
                    generalTreatmentBindingList.add(treatmentType);
                    break;
            }
        }
    }

    /**
     * Given an experiment, we see for each condition if a new drug solvent is
     * associated to the treatment, and if new drug solvents are found, these
     * are added to the drug solvent binding list.
     *
     * @param experiment
     */
    public void addNewDrugSolvents(Experiment experiment) {
        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            List<Treatment> treatmentList = plateCondition.getTreatmentList();
            for (Treatment treatment : treatmentList) {
                String drugSolvent = treatment.getDrugSolvent();
                if (!drugSolventList.contains(drugSolvent)) {
                    drugSolventList.add(drugSolvent);
                }
            }
        }
    }

    /**
     * For an experiment, this method goes all over its conditions and check for
     * new treatment types, i.e. treatment types that are not yet in the current
     * DB.
     *
     * @param experiment
     * @return a list with this new treatment types, if any.
     */
    public List<TreatmentType> findNewTreatmentTypes(Experiment experiment) {
        List<TreatmentType> treatmentTypeList = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            List<Treatment> treatmentList = plateCondition.getTreatmentList();
            for (Treatment treatment : treatmentList) {
                TreatmentType treatmentType = treatment.getTreatmentType();
                TreatmentType findByName = treatmentService.findByName(treatmentType.getName());
                if (findByName == null) {
                    if (!treatmentTypeList.contains(treatmentType)) {
                        treatmentTypeList.add(treatmentType);
                    }
                }
            }

        }
        return treatmentTypeList;
    }

    /**
     * USing the treatment service, save an entity to DB.
     *
     * @param treatmentType
     */
    public void saveTreatmentType(TreatmentType treatmentType) {
        treatmentService.saveTreatmentType(treatmentType);
    }

    /**
     * update treatment collection for previously selected condition
     *
     * @param plateCondition
     */
    public void updateTreatmentCollection(PlateCondition plateCondition) {
        // add to the collection newly inserted treatments
        for (Treatment treatment : treatmentBindingList) {
            //set plate condition of the treatment
            treatment.setPlateCondition(plateCondition);
            //update treatment collection of the plate condition
            if (!plateCondition.getTreatmentList().contains(treatment)) {
                plateCondition.getTreatmentList().add(treatment);
            }
        }

        // remove form the collection treatments not present anymore
        Iterator<Treatment> iterator = plateCondition.getTreatmentList().iterator();
        while (iterator.hasNext()) {
            if (!treatmentBindingList.contains(iterator.next())) {
                iterator.remove();
            }
        }
    }

    /**
     * this method is used inn the condition panel controller to actually show
     * the current treatment list and sync the source lists according to the
     * last one
     *
     * @param plateCondition
     */
    public void updateLists(PlateCondition plateCondition) {
        //update source lists: drugBindingList and generalTreatmentBindingList
        updateSourceLists();
        //empty the treatment binding list to show the actual one
        treatmentBindingList.clear();
        //update the treatment list
        updateDestinationList(plateCondition);
    }

    /**
     * private methods and classes
     *
     */
    private void initTreatmentSetupPanel() {
        addDrugsTreatmentsDialog = new AddDrugsTreatmentsDialog(setupConditionsController.getCellMissyFrame(), true);

        //set volume unit of measure (of assay medium)
        treatmentsPanel.getVolumeUnitLabel().setText("\u00B5" + "l");
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
        treatmentsPanel.getConcentrationUnitComboBox().addItem("\u00B5" + "g");
        treatmentsPanel.getConcentrationUnitComboBox().addItem("\u00B5" + "g" + "/" + "\u00B5" + "l");

        //add action listeners
        treatmentsPanel.getAddNewButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show dialog on center of main frame
                addDrugsTreatmentsDialog.pack();
                GuiUtils.centerDialogOnFrame(setupConditionsController.getCellMissyFrame(), addDrugsTreatmentsDialog);
                addDrugsTreatmentsDialog.setVisible(true);
                addDrugsTreatmentsDialog.getDrugTextField().requestFocusInWindow();
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
                } else if (treatmentsPanel.getSourceList2().getSelectedValue() != null) {
                    //move a treatment from a source list to the destination list
                    addTreatmentFromASourceList(treatmentsPanel.getSourceList2());
                    generalTreatmentBindingList.remove((TreatmentType) treatmentsPanel.getSourceList2().getSelectedValue());
                } else {
                    setupConditionsController.showMessage("Select a drug or a treatment to add to current list!", "add drug/treatment error", JOptionPane.WARNING_MESSAGE);
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
                } else {
                    setupConditionsController.showMessage("Select a drug or a treatment to remove from current list!", "remove drug/treatment error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //add mouse listeners
        //select drug OR general treatment
        treatmentsPanel.getSourceList1().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (treatmentsPanel.getSourceList1().getSelectedIndex() != -1) {
                        treatmentsPanel.getSourceList2().clearSelection();
                    }
                }
            }
        });

        treatmentsPanel.getSourceList2().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (treatmentsPanel.getSourceList2().getSelectedIndex() != -1) {
                        treatmentsPanel.getSourceList1().clearSelection();
                    }
                }
            }
        });

        //add new Drug to the DB
        addDrugsTreatmentsDialog.getAddDrugButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message;
                String title;
                if (!addDrugsTreatmentsDialog.getDrugTextField().getText().isEmpty()) {
                    TreatmentType newDrug = new TreatmentType();
                    //category 1: drug
                    newDrug.setTreatmentCategory(1);
                    newDrug.setName(addDrugsTreatmentsDialog.getDrugTextField().getText());
                    try {
                        //add drug to the list
                        drugBindingList.add(newDrug);
                        //save drug to DB
                        treatmentService.saveTreatmentType(newDrug);
                        message = "Drug was inserted into DB!";
                        title = "drug added";
                        JOptionPane.showMessageDialog(addDrugsTreatmentsDialog, message, title, JOptionPane.INFORMATION_MESSAGE);
                        addDrugsTreatmentsDialog.getDrugTextField().setText("");
                        // close the dialog
                        addDrugsTreatmentsDialog.setVisible(false);
                    } catch (PersistenceException exception) {
                        LOG.error(exception.getMessage());
                        message = "Drug already present in DB!";
                        title = "error in adding drug";
                        JOptionPane.showMessageDialog(addDrugsTreatmentsDialog, message, title, JOptionPane.WARNING_MESSAGE);
                        addDrugsTreatmentsDialog.getDrugTextField().setText("");
                        addDrugsTreatmentsDialog.getDrugTextField().requestFocusInWindow();
                    }
                } else {
                    message = "Insert a name for the drug!";
                    title = "error in adding drug";
                    JOptionPane.showMessageDialog(addDrugsTreatmentsDialog, message, title, JOptionPane.WARNING_MESSAGE);
                    addDrugsTreatmentsDialog.getDrugTextField().requestFocusInWindow();
                }
            }
        });

        //add new Treatment to the DB
        addDrugsTreatmentsDialog.getAddTreatmentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message;
                String title;
                if (!addDrugsTreatmentsDialog.getTreatmentTextField().getText().isEmpty()) {
                    TreatmentType newTreatment = new TreatmentType();
                    //category 2: general treatment
                    newTreatment.setTreatmentCategory(2);
                    newTreatment.setName(addDrugsTreatmentsDialog.getTreatmentTextField().getText());
                    try {
                        //add treatment to the list
                        generalTreatmentBindingList.add(newTreatment);
                        //save treatment to DB
                        treatmentService.saveTreatmentType(newTreatment);
                        message = "Treatment was inserted into DB!";
                        title = "treatment added";
                        JOptionPane.showMessageDialog(addDrugsTreatmentsDialog, message, title, JOptionPane.INFORMATION_MESSAGE);
                        addDrugsTreatmentsDialog.getTreatmentTextField().setText("");
                        // close the dialog
                        addDrugsTreatmentsDialog.setVisible(false);
                    } catch (PersistenceException exception) {
                        LOG.error(exception.getMessage());
                        message = "Treatment already present in DB!";
                        title = "error in adding treatment";
                        JOptionPane.showMessageDialog(addDrugsTreatmentsDialog, message, title, JOptionPane.WARNING_MESSAGE);
                        addDrugsTreatmentsDialog.getTreatmentTextField().setText("");
                        addDrugsTreatmentsDialog.getTreatmentTextField().requestFocusInWindow();
                    }
                } else {
                    message = "Insert a name for the treatment!";
                    title = "error in adding treatment";
                    JOptionPane.showMessageDialog(addDrugsTreatmentsDialog, message, title, JOptionPane.WARNING_MESSAGE);
                    addDrugsTreatmentsDialog.getTreatmentTextField().requestFocusInWindow();
                }
            }
        });
    }

    /**
     * this method updates drug and general treatment lists according to actual
     * list of treatment for current condition
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
     * this method updates the destination list (actual treatment list for
     * current condition) and sync its changes with the two source lists
     *
     * @param plateCondition
     */
    private void updateDestinationList(PlateCondition plateCondition) {
        //fill in the treatment binding list with the acutually treatments for the current condition
        if (!plateCondition.getTreatmentList().isEmpty()) {
            for (Treatment treatment : plateCondition.getTreatmentList()) {
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
     * this method adds treatments/drugs from a source list to the destination
     * list
     *
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
     *
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
}
