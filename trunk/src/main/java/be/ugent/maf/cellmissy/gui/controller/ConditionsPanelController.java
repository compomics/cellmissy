/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.ValidationUtils;
import be.ugent.maf.cellmissy.gui.experiment.ConditionsPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupConditionsPanel;
import be.ugent.maf.cellmissy.service.CellLineService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
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
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola
 */
public class ConditionsPanelController {

    //model
    private ObservableList<CellLineType> cellLineTypeBindingList;
    private ObservableList<PlateCondition> plateConditionBindingList;
    private ObservableList<String> mediumBindingList;
    private ObservableList<String> serumBindingList;
    private BindingGroup bindingGroup;
    //view
    private ConditionsPanel conditionsPanel;
    private SetupConditionsPanel setupConditionsPanel;
    //parent controller
    private SetupExperimentPanelController setupExperimentPanelController;
    //child controllers
    private AssayEcmPanelController assayEcmPanelController;
    private TreatmentPanelController treatmentPanelController;
    //services
    private CellLineService cellLineService;
    private ApplicationContext context;
    private GridBagConstraints gridBagConstraints;
    private Integer conditionIndex;
    private Integer previousConditionIndex;

    /**
     * constructor
     * @param setupExperimentPanelController 
     */
    public ConditionsPanelController(SetupExperimentPanelController setupExperimentPanelController) {

        this.setupExperimentPanelController = setupExperimentPanelController;

        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        cellLineService = (CellLineService) context.getBean("cellLineService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        conditionsPanel = new ConditionsPanel();
        setupConditionsPanel = new SetupConditionsPanel();

        //init child controllers
        assayEcmPanelController = new AssayEcmPanelController(this);
        treatmentPanelController = new TreatmentPanelController(this);

        initCellLinePanel();
        initConditionsPanel();
        initPanel();
    }

    /**
     * setters and getters
     * 
     */
    public SetupExperimentPanelController getSetupExperimentPanelController() {
        return setupExperimentPanelController;
    }

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

    /**
     * public  methods
     * 
     */
    /**
     * get the current plate condition
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
     * show a message through the main frame
     */
    public void showMessage(String message, Integer messageType) {
        setupExperimentPanelController.showMessage(message, messageType);
    }

    /**
     * this method updates fields of a condition (assay/ECM and treatments fields)
     * @param conditionIndex 
     */
    public void updateCondition(Integer conditionIndex) {
        assayEcmPanelController.updateAssayEcmConditionFields(plateConditionBindingList.get(conditionIndex));
        treatmentPanelController.updateTreatmentCollection(plateConditionBindingList.get(conditionIndex));
    }

    /**
     * validate a Plate Condition
     * @param plateCondition
     * @return 
     */
    public List<String> validateCondition(PlateCondition plateCondition) {
        List<String> messages = new ArrayList<>();
        //validate cell line
        if (!validateCellLine(plateCondition.getCellLine()).isEmpty()) {
            messages.addAll(validateCellLine(plateCondition.getCellLine()));
        }
        //validate ECM (2D and 3D) input
        if (!assayEcmPanelController.validate2DEcm().isEmpty()) {
            messages.addAll(assayEcmPanelController.validate2DEcm());
        }
        if (assayEcmPanelController.validate3DEcm().isEmpty()) {
            messages.addAll(assayEcmPanelController.validate3DEcm());
        }
        //validate treatments

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
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, serumBindingList, treatmentPanelController.getTreatmentPanel().getSerumComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init assay medium JCombo (it's actually in the treatment panel, but ca not be bind before since the mediumBindingList would still be null)
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, mediumBindingList, treatmentPanelController.getTreatmentPanel().getAssayMediumComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        /**
         * add action listeners
         */
        /**
         * insert a new cell line Type in the DB if it's not present yet
         */
        setupConditionsPanel.getInsertCellLineButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!setupConditionsPanel.getCellLineNameTextField().getText().isEmpty()) {
                    CellLineType newCellLineType = new CellLineType();
                    newCellLineType.setName(setupConditionsPanel.getCellLineNameTextField().getText());
                    //insert cell line to DB
                    cellLineService.saveCellLineType(newCellLineType);
                    //add the new cell line to the list
                    cellLineTypeBindingList.add(newCellLineType);
                }
            }
        });
    }

    /**
     * initialize ConditionsPanel components
     */
    private void initConditionsPanel() {

        //set selected matrix dimension to "2D"
        setupConditionsPanel.getEcmDimensionComboBox().setSelectedIndex(0);
        setupConditionsPanel.getjTabbedPane1().setEnabled(false);
        //set current and previous conditions indexes
        conditionIndex = 0;
        previousConditionIndex = -1;

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
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.serum"), setupConditionsPanel.getSerumComboBox(), BeanProperty.create("selectedItem"), "serumbinding");
        bindingGroup.addBinding(binding);
        //autobind serum concentration
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.serumConcentration"), setupConditionsPanel.getSerumConcentrationTextField(), BeanProperty.create("text"), "serumconcentrationbinding");
        bindingGroup.addBinding(binding);
        //autobind cell line type
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, conditionsPanel.getConditionsJList(), BeanProperty.create("selectedElement.cellLine.cellLineType"), setupConditionsPanel.getCellLineComboBox(), BeanProperty.create("selectedItem"), "celllinetypebinding");
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
        PlateCondition firstCondition = new PlateCondition();
        initFirstCondition(firstCondition);
        //add Condition 1 to the list
        plateConditionBindingList.add(firstCondition);

        //set cell renderer for conditionJList
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsRenderer());

        /**
         * add mouse listeners
         */
        //if Condition validation is OK, update previous condition and user input fields
        conditionsPanel.getConditionsJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int locationToIndex = conditionsPanel.getConditionsJList().locationToIndex(e.getPoint());
                //add mouse listener and enable tabbed pane on the right (only once, for Condition 1)
                if (locationToIndex == 0) {
                    setupExperimentPanelController.addMouseListener();
                    setupConditionsPanel.getjTabbedPane1().setEnabled(true);
                }
                if (previousConditionIndex < plateConditionBindingList.size() && previousConditionIndex != -1) {
                    //check if validation of condition is OK
                    if (setupExperimentPanelController.validateCondition(plateConditionBindingList.get(previousConditionIndex))) {
                        //update fields of previous condition
                        updateCondition(previousConditionIndex);
                        //update and reset fields for the assay-ecm panel
                        assayEcmPanelController.updateAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                        //assayEcmPanelController.resetAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                        //empty the treatments list and fill it in with other objects
                        treatmentPanelController.initTreatmentList(plateConditionBindingList.get(previousConditionIndex));
                        //keep source and destination lists sync: show actual treatment collection
                        treatmentPanelController.updateTreatmentLists(plateConditionBindingList.get(locationToIndex));
                    }
                }
                previousConditionIndex = locationToIndex;
            }
        });

        //add an empty list of rectangles for Condition 1
        setupExperimentPanelController.onNewConditionAdded(firstCondition);
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
                setupExperimentPanelController.onNewConditionAdded(newCondition);

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
                    setupExperimentPanelController.onConditionToRemove((PlateCondition) (conditionsPanel.getConditionsJList().getSelectedValue()));
                    //remove the Condition from the list
                    plateConditionBindingList.remove(conditionsPanel.getConditionsJList().getSelectedIndex());
                    //select first condition after removing
                    conditionsPanel.getConditionsJList().setSelectedIndex(0);
                    //if there's only one condition left, disable again the remove button
                    if (plateConditionBindingList.size() == 1) {
                        conditionsPanel.getRemoveButton().setEnabled(false);
                    }
                }
            }
        });
    }

    /**
     * this method assigns default fields to the first Condition created and added to the List
     * @param firstCondition 
     */
    private void initFirstCondition(PlateCondition firstCondition) {
        //set the name
        firstCondition.setName("Condition " + ++conditionIndex);

        //set the cell line
        CellLine cellLine = new CellLine();
        cellLine.setCellLineType(cellLineTypeBindingList.get(0));
        cellLine.setSeedingDensity(50000);
        cellLine.setSeedingTime("24 hours");
        cellLine.setGrowthMedium(mediumBindingList.get(0));
        cellLine.setSerum(serumBindingList.get(0));
        cellLine.setSerumConcentration("10");
        firstCondition.setCellLine(cellLine);
        cellLine.setPlateCondition(firstCondition);

        //set matrix dimension: 2D
        firstCondition.setMatrixDimension(assayEcmPanelController.getMatrixDimensionBindingList().get(0));

        //set the migration assay: Oris platform
        firstCondition.setAssay(assayEcmPanelController.getAssay2DBindingList().get(0));

        //create a new ECM object and set its class members
        Ecm ecm = new Ecm();
        ecm.setEcmComposition(assayEcmPanelController.getEcm2DCompositionBindingList().get(0));
        ecm.setEcmCoating(assayEcmPanelController.getEcmCoatingBindingList().get(0));
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
     * this method assigns values for each new condition, from the previously created 8and set-up) one
     * @param newCondition 
     */
    private void initNewCondition(PlateCondition newCondition) {
        //set the name
        newCondition.setName("Condition " + ++conditionIndex);
        //set the cell line
        CellLine cellLine = plateConditionBindingList.get(previousConditionIndex).getCellLine();
        CellLine newCellLine = new CellLine(cellLine.getSeedingTime(), cellLine.getSeedingDensity(), cellLine.getGrowthMedium(), cellLine.getSerumConcentration(), cellLine.getCellLineType(), cellLine.getSerum());
        newCondition.setCellLine(newCellLine);
        newCellLine.setPlateCondition(newCondition);
        //set matrix dimension
        newCondition.setMatrixDimension(plateConditionBindingList.get(previousConditionIndex).getMatrixDimension());
        //set assay and ecm (still default values)
        Ecm ecm = new Ecm();
        if (newCondition.getMatrixDimension().getMatrixDimension().equals("2D")) {
            newCondition.setAssay(assayEcmPanelController.getAssay2DBindingList().get(0));
            //set ecm 2D fields
            ecm.setEcmComposition(assayEcmPanelController.getEcm2DCompositionBindingList().get(0));
            ecm.setEcmCoating(assayEcmPanelController.getEcmCoatingBindingList().get(0));
            ecm.setConcentration(0.04);
            ecm.setVolume(100.0);
            ecm.setCoatingTime("60");
            ecm.setCoatingTemperature("RT");
            ecm.setVolumeUnit("\u00B5" + "l");
            ecm.setConcentrationUnit("mg/ml");
        } else {
            newCondition.setAssay(assayEcmPanelController.getAssay3DBindingList().get(0));
            //set ecm 3D fields
            ecm.setEcmComposition(assayEcmPanelController.getEcm3DCompositionBindingList().get(0));
            ecm.setEcmDensity((EcmDensity) assayEcmPanelController.getAssayEcm3DPanel().getDensityComboBox().getItemAt(1));
            ecm.setVolume(40.0);
            ecm.setPolymerisationTime("30");
            ecm.setPolymerisationTemperature("37 C");
        }
        newCondition.setEcm(ecm);

        //set an empty collection of treatments
        List<Treatment> treatmentList = new ArrayList<>();
        newCondition.setTreatmentCollection(treatmentList);

        //set an empty collection of wells (wells are not recalled from previous condition)
        List<Well> wellList = new ArrayList<>();
        newCondition.setWellCollection(wellList);
    }

    private List<String> validateCellLine(CellLine cellLine) {
        return ValidationUtils.validateObject(cellLine);
    }

    /**
     * renderer for the Conditions JList
     */
    private class ConditionsRenderer extends DefaultListCellRenderer {

        /*
         *constructor
         */
        public ConditionsRenderer() {
            setOpaque(true);
            setIconTextGap(10);
        }

        //Overrides method from the DefaultListCellRenderer
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            PlateCondition newCondition = (PlateCondition) value;
            setText(newCondition.getName());
            setIcon(new rectIcon(GuiUtils.getAvailableColors()[((PlateCondition) value).getConditionIndex()]));
            if (isSelected) {
                setBackground(Color.lightGray);
                setBorder(BorderFactory.createLineBorder(Color.black, 2));
                setFont(new Font("Roman", Font.BOLD, 14));
            } else {
                setFont(new Font("Roman", Font.BOLD, 12));
            }
            return this;
        }
    }

    /**
     * rectangular icon for the Condition list
     */
    private class rectIcon implements Icon {

        private final Integer rectSize = 10;
        private Color color;

        /**
         * constructor
         * @param color 
         */
        public rectIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            setupExperimentPanelController.getSetupPlatePanel().setGraphics(g2d);
            g2d.setColor(color);
            g2d.drawRect(x, y, rectSize, rectSize);
        }

        @Override
        public int getIconWidth() {
            return rectSize;
        }

        @Override
        public int getIconHeight() {
            return rectSize;
        }
    }

    /**
     * add the Condition Panel and the Setup Condition Panel to their parent panels
     */
    private void initPanel() {
        setupExperimentPanelController.getSetupPanel().getConditionsParentPanel().add(conditionsPanel, gridBagConstraints);
        setupExperimentPanelController.getSetupPanel().getSetupConditionsParentPanel().add(setupConditionsPanel, gridBagConstraints);
    }
}
