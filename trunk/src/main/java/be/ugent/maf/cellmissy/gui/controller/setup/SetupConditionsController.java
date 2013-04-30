/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.AssayMedium;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.ValidationUtils;
import be.ugent.maf.cellmissy.gui.experiment.setup.ConditionsPanel;
import be.ugent.maf.cellmissy.gui.experiment.setup.SetupConditionsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsSetupListRenderer;
import be.ugent.maf.cellmissy.service.CellLineService;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Set up Conditions Controller: set up conditions details during experiment
 * design Parent controller: Setup Experiment Controller Child controllers:
 * AssayEcm Controller, Treatment Controller
 *
 * @author Paola
 */
@Controller("setupConditionsController")
public class SetupConditionsController {

    private static final Logger LOG = Logger.getLogger(SetupConditionsController.class);
    //model
    private ObservableList<CellLineType> cellLineTypeBindingList;
    private ObservableList<PlateCondition> plateConditionBindingList;
    private ObservableList<String> mediumBindingList;
    private ObservableList<String> serumBindingList;
    private BindingGroup bindingGroup;
    private Integer conditionIndex;
    private Integer previousConditionIndex;
    //view
    private ConditionsPanel conditionsPanel;
    private SetupConditionsPanel setupConditionsPanel;
    //parent controller
    @Autowired
    private SetupExperimentController setupExperimentController;
    //child controllers
    @Autowired
    private AssayEcmController assayEcmController;
    @Autowired
    private TreatmentsController treatmentsController;
    //services
    @Autowired
    private CellLineService cellLineService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //create panels
        conditionsPanel = new ConditionsPanel();
        setupConditionsPanel = new SetupConditionsPanel();
        //init child controllers
        assayEcmController.init();
        treatmentsController.init();
        //init views
        initCellLinePanel();
        initConditionsPanel();
        initPanel();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public ConditionsPanel getConditionsPanel() {
        return conditionsPanel;
    }

    public SetupConditionsPanel getSetupConditionsPanel() {
        return setupConditionsPanel;
    }

    public ObservableList<PlateCondition> getPlateConditionBindingList() {
        return plateConditionBindingList;
    }

    public ObservableList<String> getMediumBindingList() {
        return mediumBindingList;
    }

    public Integer getPreviousConditionIndex() {
        return previousConditionIndex;
    }

    public ObservableList<Treatment> getTreatmentBindingList() {
        return treatmentsController.getTreatmentBindingList();
    }

    /**
     * Reset the indexes of condition (current and previous one)
     */
    public void resetConditionIndexes() {
        conditionIndex = 0;
        previousConditionIndex = -1;
    }

    /**
     * Get the main (CellMissy) frame through the parent controller
     *
     * @return
     */
    public CellMissyFrame getCellMissyFrame() {
        return setupExperimentController.getCellMissyFrame();
    }

    /**
     * public methods
     *
     */
    /**
     * get the current plate condition
     *
     * @return the selected value of the conditions List
     */
    public PlateCondition getCurrentCondition() {
        PlateCondition currentCondition = new PlateCondition();
        if (conditionsPanel.getConditionsJList().getSelectedValue() != null) {
            currentCondition = ((PlateCondition) (conditionsPanel.getConditionsJList().getSelectedValue()));
        }
        return currentCondition;
    }

    /**
     * show a message through the main frame (CellMissy frame)
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        setupExperimentController.showMessage(message, title, messageType);
    }

    /**
     * this method updates fields of a condition (assay/ECM and treatments
     * fields)
     *
     * @param conditionIndex
     */
    public void updateCondition(Integer conditionIndex) {
        assayEcmController.updateAssayEcmConditionFields(plateConditionBindingList.get(conditionIndex));
        treatmentsController.updateTreatmentCollection(plateConditionBindingList.get(conditionIndex));
    }

    /**
     * validate a Plate Condition
     *
     * @param plateCondition
     * @return a list of strings to be concatenated in order to show message to
     * the user
     */
    public List<String> validateCondition(PlateCondition plateCondition) {
        List<String> messages = new ArrayList<>();
        //validate cell line
        if (!validateCellLine(plateCondition.getCellLine()).isEmpty()) {
            messages.addAll(validateCellLine(plateCondition.getCellLine()));
        }
        //validate ECM (2D and 3D) input
        if (!assayEcmController.validate2DEcm().isEmpty()) {
            messages.addAll(assayEcmController.validate2DEcm());
        }
        if (!assayEcmController.validate3DEcm().isEmpty()) {
            messages.addAll(assayEcmController.validate3DEcm());
        }
        //if validation was OK, validate the condition: check for wells collection
        if (messages.isEmpty()) {
            if (plateCondition.getWellCollection().isEmpty()) {
                String message = "Conditions must have at least one well";
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * Create and initialize the first condition
     *
     * @return
     */
    public PlateCondition createFirstCondition() {
        PlateCondition firstCondition = new PlateCondition();
        initFirstCondition(firstCondition);
        return firstCondition;
    }

    /**
     * private methods and classes
     */
    /**
     * initialize cell Line panel
     */
    private void initCellLinePanel() {
        //init cellLineJCombo
        cellLineTypeBindingList = ObservableCollections.observableList(cellLineService.findAllCellLineTypes());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, cellLineTypeBindingList, setupConditionsPanel.getCellLineComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init growth medium JCombo
        mediumBindingList = ObservableCollections.observableList(cellLineService.findAllGrowthMedia());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, mediumBindingList, setupConditionsPanel.getGrowthMediumComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init serum JCombo
        serumBindingList = ObservableCollections.observableList(cellLineService.findAllSera());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, serumBindingList, setupConditionsPanel.getSerumComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init the other serum ComboBox
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, serumBindingList, treatmentsController.getTreatmentsPanel().getSerumComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init assay medium JCombo (it's actually in the treatment panel, but ca not be bind before since the mediumBindingList would still be null)
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, mediumBindingList, treatmentsController.getTreatmentsPanel().getAssayMediumComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        /**
         * add action listeners
         */
        /**
         * insert a new cell line Type in the DB if it's not present yet
         */
        setupConditionsPanel.getAddCellLineButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!setupConditionsPanel.getCellLineNameTextField().getText().isEmpty()) {
                    CellLineType newCellLineType = new CellLineType();
                    newCellLineType.setName(setupConditionsPanel.getCellLineNameTextField().getText());
                    try {
                        //insert cell line to DB
                        cellLineService.saveCellLineType(newCellLineType);
                        //add the new cell line to the list
                        cellLineTypeBindingList.add(newCellLineType);
                        setupConditionsPanel.getCellLineNameTextField().setText("");
                    } catch (PersistenceException exception) {
                        LOG.error(exception.getMessage());
                        showMessage("Cell Line already present in DB!", "", JOptionPane.WARNING_MESSAGE);
                        setupConditionsPanel.getCellLineNameTextField().setText("");
                        setupConditionsPanel.getCellLineNameTextField().requestFocusInWindow();
                    }
                } else {
                    showMessage("Please insert a name for the cell line!", "", JOptionPane.WARNING_MESSAGE);
                    setupConditionsPanel.getCellLineNameTextField().requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Initialize ConditionsPanel components
     */
    private void initConditionsPanel() {
        //set selected matrix dimension to "2D"
        setupConditionsPanel.getEcmDimensionComboBox().setSelectedIndex(0);
        setupConditionsPanel.getjTabbedPane1().setEnabled(false);
        //set current and previous conditions indexes
        conditionIndex = 0;
        previousConditionIndex = -1;

        conditionsPanel.getAddButton().setEnabled(false);
        //init conditionJList (create new empty list) (conditions are NOT retrieved from DB)
        plateConditionBindingList = ObservableCollections.observableList(new ArrayList<PlateCondition>());

        //autobind cell line
        //autobind seeding density
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.seedingDensity"), setupConditionsPanel.getSeedingDensityTextField(), BeanProperty.create("text"), "seedingdensitybinding");
        bindingGroup.addBinding(binding);
        //autobind seeding time
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.seedingTime"), setupConditionsPanel.getSeedingTimeTextField(), BeanProperty.create("text"), "seedingtimebinding");
        bindingGroup.addBinding(binding);
        //autobind growth medium
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.growthMedium"), setupConditionsPanel.getGrowthMediumComboBox(), BeanProperty.create("selectedItem"), "growthmediumbinding");
        bindingGroup.addBinding(binding);
        //autobind serum
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.serum"), setupConditionsPanel.getSerumComboBox(), BeanProperty.create("selectedItem"), "celllineserumbinding");
        bindingGroup.addBinding(binding);
        //autobind serum concentration
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.serumConcentration"), setupConditionsPanel.getSerumConcentrationTextField(), BeanProperty.create("text"), "celllineserumconcentrationbinding");
        bindingGroup.addBinding(binding);
        //autobind cell line type
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.cellLineType"), setupConditionsPanel.getCellLineComboBox(), BeanProperty.create("selectedItem"), "celllinetypebinding");
        bindingGroup.addBinding(binding);

        //autobind assay medium
        //autobind medium
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assayMedium.medium"), treatmentsController.getTreatmentsPanel().getAssayMediumComboBox(), BeanProperty.create("selectedItem"), "assaymediumbinding");
        bindingGroup.addBinding(binding);
        //autobind serum
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assayMedium.serum"), treatmentsController.getTreatmentsPanel().getSerumComboBox(), BeanProperty.create("selectedItem"), "assayserumbinding");
        bindingGroup.addBinding(binding);
        //autobind serum concentration
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assayMedium.serumConcentration"), treatmentsController.getTreatmentsPanel().getSerumConcentrationTextField(), BeanProperty.create("text"), "assayserumconcentrationbinding");
        bindingGroup.addBinding(binding);
        // autobind volume
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.assayMedium.volume"), treatmentsController.getTreatmentsPanel().getMediumVolumeTextField(), BeanProperty.create("text"), "assayvolumebinding");
        bindingGroup.addBinding(binding);

        //autobind matrix dimension
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.matrixDimension"), setupConditionsPanel.getEcmDimensionComboBox(), BeanProperty.create("selectedItem"), "matrixdimensionbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

        //init conditionListBinding
        JListBinding conditionListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsPanel.getConditionsJList());
        bindingGroup.addBinding(conditionListBinding);
        bindingGroup.bind();

        //create and init the first condition (Condition 1)
        PlateCondition firstCondition = createFirstCondition();
        //add Condition 1 to the list
        plateConditionBindingList.add(firstCondition);

        //set cell renderer for conditionJList
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsSetupListRenderer());

        /**
         * add mouse listeners
         */
        //if Condition validation is OK, update previous condition and user input fields
        conditionsPanel.getConditionsJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                conditionsPanel.getAddButton().setEnabled(true);
                int locationToIndex = conditionsPanel.getConditionsJList().locationToIndex(e.getPoint());
                //add mouse listener and enable tabbed pane on the right (only once, for Condition 1)
                if (locationToIndex == 0) {
                    setupExperimentController.addMouseListener();
                    setupConditionsPanel.getjTabbedPane1().setEnabled(true);
                }
                if (previousConditionIndex < plateConditionBindingList.size() && previousConditionIndex != -1) {
                    //check if validation of condition is OK
                    if (setupExperimentController.validateCondition(plateConditionBindingList.get(previousConditionIndex))) {
                        //update fields of previous condition
                        updateCondition(previousConditionIndex);
                        //update and reset fields for the assay-ecm panel
                        assayEcmController.updateAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                        //assayEcmPanelController.resetAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                        //keep source and destination lists sync: show actual treatment collection
                        treatmentsController.updateLists(plateConditionBindingList.get(locationToIndex));
                    }
                }
                previousConditionIndex = locationToIndex;
            }
        });

        //add an empty list of rectangles for Condition 1
        setupExperimentController.onNewConditionAdded(firstCondition);
        //disable the Remove Button
        conditionsPanel.getRemoveButton().setEnabled(false);

        /**
         * add action listeners
         */
        //add a new Condition to the List
        //each new Condition is init through values selected from the previously created one!
        conditionsPanel.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create and init a new Condition
                PlateCondition newCondition = new PlateCondition();
                initNewCondition(newCondition);
                //add the new Condition to the list
                plateConditionBindingList.add(newCondition);
                //add a new empty list of rectangles for the just added Condition
                setupExperimentController.onNewConditionAdded(newCondition);
                //after a new condition is added enable the remove button
                if (!conditionsPanel.getRemoveButton().isEnabled()) {
                    conditionsPanel.getRemoveButton().setEnabled(true);
                }
            }
        });

        //remove a Condition from the list
        conditionsPanel.getRemoveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (conditionsPanel.getConditionsJList().getSelectedValue() != null) {
                    //empty the list of rectangles for this Condition and reset to null the Condition of the associated wells
                    setupExperimentController.onConditionToRemove((PlateCondition) (conditionsPanel.getConditionsJList().getSelectedValue()));
                    //remove the Condition from the list
                    int selectedIndex = conditionsPanel.getConditionsJList().getSelectedIndex();
                    PlateCondition conditionToRemove = plateConditionBindingList.get(selectedIndex);
                    // if the condition to remove is the last one, decrease previous condition index
                    if (plateConditionBindingList.indexOf(conditionToRemove) == plateConditionBindingList.size() - 1) {
                        previousConditionIndex = plateConditionBindingList.size() - 2;
                    }
                    // remove condition from the list
                    plateConditionBindingList.remove(conditionToRemove);
                    // select first condition after removing
                    conditionsPanel.getConditionsJList().setSelectedIndex(0);
                    // if there's only one condition left, disable again the remove button
                    if (plateConditionBindingList.size() == 1) {
                        conditionsPanel.getRemoveButton().setEnabled(false);
                    }
                }
            }
        });
    }

    /**
     * this method assigns default fields to the first Condition created and
     * added to the List
     *
     * @param firstCondition
     */
    private void initFirstCondition(PlateCondition firstCondition) {
        //set the name
        firstCondition.setName("Condition " + ++conditionIndex);
        //set the cell line
        CellLine cellLine = new CellLine();
        cellLine.setCellLineType(cellLineTypeBindingList.get(0));
        cellLine.setSeedingDensity(50000);
        cellLine.setSeedingTime("day -1");
        cellLine.setGrowthMedium(mediumBindingList.get(0));
        cellLine.setSerum(serumBindingList.get(0));
        cellLine.setSerumConcentration(10.0);
        firstCondition.setCellLine(cellLine);
        cellLine.setPlateCondition(firstCondition);
        //set the migration assay: Oris platform; matrix dimension: 2D
        firstCondition.setAssay(assayEcmController.getAssay2DBindingList().get(0));
        //create a new AssayMedium object and set its class members
        AssayMedium assayMedium = new AssayMedium();
        assayMedium.setMedium(treatmentsController.getTreatmentsPanel().getAssayMediumComboBox().getItemAt(0).toString());
        assayMedium.setSerum(serumBindingList.get(0));
        assayMedium.setSerumConcentration(1.0);
        assayMedium.setVolume(10.0);
        firstCondition.setAssayMedium(assayMedium);
        assayMedium.setPlateCondition(firstCondition);
        //create a new ECM object and set its class members
        Ecm ecm = new Ecm();
        ecm.setEcmComposition(assayEcmController.getEcm2DCompositionBindingList().get(0));
        ecm.setCoatingTemperature("RT");
        ecm.setCoatingTime("60");
        ecm.setConcentration(0.04);
        ecm.setVolume(100.0);
        ecm.setVolumeUnit("\u00B5" + "l");
        ecm.setConcentrationUnit("mg/ml");
        firstCondition.setEcm(ecm);
        //set an empty collection of treatments
        List<Treatment> treatmentList = new ArrayList<>();
        firstCondition.setTreatmentCollection(treatmentList);
        //set an empty collection of wells
        List<Well> wellList = new ArrayList<>();
        firstCondition.setWellCollection(wellList);
    }

    /**
     * this method assigns values for each new condition, from the previously
     * created 8and set-up) one
     *
     * @param newCondition
     */
    private void initNewCondition(PlateCondition newCondition) {
        PlateCondition previousCondition = plateConditionBindingList.get(previousConditionIndex);
        //set the name
        newCondition.setName("Condition " + ++conditionIndex);
        //set the cell line (the same as the previous condition)
        CellLine cellLine = previousCondition.getCellLine();
        CellLine newCellLine = new CellLine(cellLine.getSeedingTime(), cellLine.getSeedingDensity(), cellLine.getGrowthMedium(), cellLine.getSerumConcentration(), cellLine.getCellLineType(), cellLine.getSerum());
        newCondition.setCellLine(newCellLine);
        newCellLine.setPlateCondition(newCondition);
        //set assay medium (another object, but with the same parameters as previous condition)
        String medium = mediumBindingList.get(treatmentsController.getTreatmentsPanel().getAssayMediumComboBox().getSelectedIndex());
        String serum = serumBindingList.get(treatmentsController.getTreatmentsPanel().getSerumComboBox().getSelectedIndex());
        Double serumConcentration = Double.parseDouble(treatmentsController.getTreatmentsPanel().getSerumConcentrationTextField().getText());
        Double volume = Double.parseDouble(treatmentsController.getTreatmentsPanel().getMediumVolumeTextField().getText());
        AssayMedium assayMedium = new AssayMedium(medium, serum, serumConcentration, volume);
        newCondition.setAssayMedium(assayMedium);
        assayMedium.setPlateCondition(newCondition);
        //set assay and ecm (get the values according to the last selected ones)
        Ecm ecm = new Ecm();
        Assay assay = null;
        BottomMatrix bottomMatrix = null;
        //need to set different values according to matrix dimension: 2D or 3D or 2.5D
        switch (assayEcmController.getMatrixDimensionBindingList().get(setupConditionsPanel.getEcmDimensionComboBox().getSelectedIndex()).getDimension()) {
            case "2D":
                //set assay
                assay = assayEcmController.getAssay2DBindingList().get(assayEcmController.getAssayEcm2DPanel().getAssayComboBox().getSelectedIndex());
                //2D matrix: set ecm 2D fields
                ecm.setEcmComposition(assayEcmController.getEcm2DCompositionBindingList().get(assayEcmController.getAssayEcm2DPanel().getCompositionComboBox().getSelectedIndex()));
                ecm.setConcentration(Double.parseDouble(assayEcmController.getAssayEcm2DPanel().getConcentrationTextField().getText()));
                ecm.setVolume(Double.parseDouble(assayEcmController.getAssayEcm2DPanel().getVolumeTextField().getText()));
                ecm.setCoatingTime(assayEcmController.getAssayEcm2DPanel().getCoatingTimeTextField().getText());
                ecm.setCoatingTemperature(assayEcmController.getAssayEcm2DPanel().getCoatingTemperatureTextField().getText());
                ecm.setVolumeUnit(assayEcmController.getAssayEcm2DPanel().getVolumeUnitLabel().getText());
                ecm.setConcentrationUnit(assayEcmController.getAssayEcm2DPanel().getConcentrationUnitOfMeasure().getSelectedItem().toString());
                break;
            case "3D":
                //set assay    
                assay = assayEcmController.getAssay3DBindingList().get(assayEcmController.getAssayEcm3DPanel().getAssayComboBox().getSelectedIndex());
                //3D matrix: set ecm 3D fields
                ecm.setEcmComposition(assayEcmController.getEcm3DCompositionBindingList().get(assayEcmController.getAssayEcm3DPanel().getCompositionComboBox().getSelectedIndex()));
                ecm.setEcmDensity(assayEcmController.getEcmDensityBindingList().get(assayEcmController.getAssayEcm3DPanel().getDensityComboBox().getSelectedIndex()));
                // bottom matrix
                bottomMatrix = assayEcmController.getBottomMatrixBindingList().get(assayEcmController.getAssayEcm3DPanel().getBottomMatrixTypeComboBox().getSelectedIndex());
                ecm.setBottomMatrix(bottomMatrix);
                switch (bottomMatrix.getType()) {
                    case "gel":
                        // both top and bottom matrix volumes
                        String text = assayEcmController.getAssayEcm3DPanel().getTopMatrixVolumeTextField().getText();
                        if (!text.isEmpty()) {
                            ecm.setTopMatrixVolume(Double.parseDouble(text));
                        } else {
                            ecm.setTopMatrixVolume(40.0);
                        }
                        ecm.setBottomMatrixVolume(Double.parseDouble(assayEcmController.getAssayEcm3DPanel().getBottomMatrixVolumeTextField().getText()));
                        break;
                    case "thin gel coating":
                        // top matrix but no bottom matrix volume
                        ecm.setTopMatrixVolume(Double.parseDouble(assayEcmController.getAssayEcm3DPanel().getTopMatrixVolumeTextField().getText()));
                        break;
                }
                ecm.setPolymerisationTime(assayEcmController.getAssayEcm3DPanel().getPolymerizationTimeTextField().getText());
                ecm.setPolymerisationTemperature(assayEcmController.getAssayEcm3DPanel().getPolymerizationTemperatureTextField().getText());
                ecm.setPolymerisationPh(assayEcmController.getPolymerisationPhBindingList().get(assayEcmController.getAssayEcm3DPanel().getPolymerizationPhComboBox().getSelectedIndex()));
                break;
            case "2.5D":
                // set assay
                assay = assayEcmController.getAssay25DBindingList().get(assayEcmController.getAssayEcm25DPanel().getAssayComboBox().getSelectedIndex());
                //3D matrix: set ecm 2.5D fields
                ecm.setEcmComposition(assayEcmController.getEcm25DCompositionBindingList().get(assayEcmController.getAssayEcm25DPanel().getCompositionComboBox().getSelectedIndex()));
                ecm.setEcmDensity(assayEcmController.getEcmDensityBindingList().get(assayEcmController.getAssayEcm25DPanel().getDensityComboBox().getSelectedIndex()));
                // bottom matrix
                bottomMatrix = assayEcmController.getBottomMatrixBindingList().get(assayEcmController.getAssayEcm25DPanel().getBottomMatrixTypeComboBox().getSelectedIndex());
                ecm.setBottomMatrix(bottomMatrix);
                switch (bottomMatrix.getType()) {
                    case "gel":
                        // bottom matrix volume
                        ecm.setBottomMatrixVolume(Double.parseDouble(assayEcmController.getAssayEcm25DPanel().getBottomMatrixVolumeTextField().getText()));
                        break;
                }
                ecm.setPolymerisationTime(assayEcmController.getAssayEcm25DPanel().getPolymerizationTimeTextField().getText());
                ecm.setPolymerisationTemperature(assayEcmController.getAssayEcm25DPanel().getPolymerizationTemperatureTextField().getText());
                ecm.setPolymerisationPh(assayEcmController.getPolymerisationPhBindingList().get(assayEcmController.getAssayEcm25DPanel().getPolymerizationPhComboBox().getSelectedIndex()));
                break;
        }
        newCondition.setAssay(assay);
        newCondition.setEcm(ecm);
        // create new treatment with same parameters as ones from previous condition
        List<Treatment> treatmentList = new ArrayList<>();
        ObservableList<Treatment> treatmentBindingList = treatmentsController.getTreatmentBindingList();
        for (Treatment treatment : treatmentBindingList) {
            Double concentration = treatment.getConcentration();
            String concentrationUnit = treatment.getConcentrationUnit();
            String timing = treatment.getTiming();
            String drugSolvent = treatment.getDrugSolvent();
            Double drugSolventConcentration = treatment.getDrugSolventConcentration();
            TreatmentType treatmentType = treatment.getTreatmentType();
            Treatment newTreatment = new Treatment(concentration, concentrationUnit, timing, drugSolvent, drugSolventConcentration, treatmentType);
            newTreatment.setPlateCondition(newCondition);
            treatmentList.add(newTreatment);
        }
        newCondition.setTreatmentCollection(treatmentList);
        //set an empty collection of wells (wells are not recalled from previous condition)
        List<Well> wellList = new ArrayList<>();
        newCondition.setWellCollection(wellList);
    }

    /**
     * validate Cell Line
     *
     * @param cellLine
     * @return
     */
    private List<String> validateCellLine(CellLine cellLine) {
        return ValidationUtils.validateObject(cellLine);
    }

    /**
     * add the Condition Panel and the Setup Condition Panel to their parent
     * panels
     */
    private void initPanel() {
        setupExperimentController.getSetupPanel().getConditionsParentPanel().add(conditionsPanel, gridBagConstraints);
        setupExperimentController.getSetupPanel().getSetupConditionsParentPanel().add(setupConditionsPanel, gridBagConstraints);
    }
}
