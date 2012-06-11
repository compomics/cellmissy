/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.ValidationUtils;
import be.ugent.maf.cellmissy.gui.experiment.ConditionsPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupConditionsPanel;
import be.ugent.maf.cellmissy.service.CellLineService;
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

/**
 *
 * @author Paola
 */
public class ConditionsPanelController {

    //model
    private ObservableList<CellLineType> cellLineTypeBindingList;
    private ObservableList<PlateCondition> plateConditionBindingList;
    private ObservableList<String> mediumBindingList;
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
        cellLineService = (CellLineService) setupExperimentPanelController.getCellMissyController().getBeanByName("cellLineService");

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

        //if validation was OK, validate the condition
        if (messages.isEmpty()) {
            messages.addAll(ValidationUtils.validateObject(plateCondition));
        }

        return messages;
    }

    /**
     * private methods and classes
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

        //init assay medium JCombo (it's actually in the treatment panel, but ca not be bind before since the mediumBindingList would be still null)
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, mediumBindingList, treatmentPanelController.getTreatmentPanel().getAssayMediumComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

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

    private void initConditionsPanel() {

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
        initCondition(firstCondition);
        //add Condition 1 to the list
        plateConditionBindingList.add(firstCondition);

        //set cell renderer for conditionJList
        conditionsPanel.getConditionsJList().setCellRenderer(new ConditionsRenderer());

        /**
         * add mouse listeners
         */
        conditionsPanel.getConditionsJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                setupConditionsPanel.getjTabbedPane1().setEnabled(true);
                int locationToIndex = conditionsPanel.getConditionsJList().locationToIndex(e.getPoint());
                if (previousConditionIndex < plateConditionBindingList.size() && previousConditionIndex != -1) {
                    if (setupExperimentPanelController.validateCondition(plateConditionBindingList.get(previousConditionIndex))) {
                        //update fields of previous condition
                        updateCondition(previousConditionIndex);
                        //update and reset fields for the assay-ecm panel
                        assayEcmPanelController.updateAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                        assayEcmPanelController.resetAssayEcmInputFields(plateConditionBindingList.get(locationToIndex));
                        //empty the treatments list and fill it in with other objects
                        treatmentPanelController.initTreatmentList(plateConditionBindingList.get(previousConditionIndex));
                        //keep source and destination lists sync: show actual treatment collection
                        treatmentPanelController.updateTreatmentLists(plateConditionBindingList.get(locationToIndex));
                    }
                }
                previousConditionIndex = locationToIndex;
            }
        });

        //select Condition 1 as default
        conditionsPanel.getConditionsJList().setSelectedIndex(-1);
        //add an empty list of rectangles for Condition 1
        setupExperimentPanelController.onNewConditionAdded(firstCondition);
        //disable the Remove Button
        conditionsPanel.getRemoveButton().setEnabled(false);

        /** 
         * add action listeners
         */
        //add a new Condition to the List
        conditionsPanel.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //create and init a new Condition
                PlateCondition newCondition = new PlateCondition();
                initCondition(newCondition);
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
     * this method assigns default fields to each new condition created
     * @param plateCondition 
     */
    private void initCondition(PlateCondition plateCondition) {

        //set the name
        plateCondition.setName("Condition " + ++conditionIndex);

        //set the cell line
        CellLine cellLine = new CellLine();
        cellLine.setCellLineType(cellLineTypeBindingList.get(0));
        cellLine.setSeedingDensity(50000);
        cellLine.setSeedingTime("24 hours");
        cellLine.setGrowthMedium(mediumBindingList.get(0));
        plateCondition.setCellLine(cellLine);
        cellLine.setPlateCondition(plateCondition);

        //set matrix dimension: 2D
        plateCondition.setMatrixDimension(assayEcmPanelController.getMatrixDimensionBindingList().get(0));

        //set the migration assay: Oris platform
        plateCondition.setAssay(assayEcmPanelController.getAssay2DBindingList().get(0));

        //create a new ECM object and set its class members
        Ecm ecm = new Ecm();
        ecm.setEcmComposition(assayEcmPanelController.getEcm2DCompositionBindingList().get(0));
        ecm.setEcmCoating(assayEcmPanelController.getEcmCoatingBindingList().get(0));
        ecm.setCoatingTemperature("37C");
        ecm.setCoatingTime("12h");
        ecm.setConcentration(0.5);
        ecm.setVolume(0.5);
        plateCondition.setEcm(ecm);

        //set an empty collection of treatments
        List<Treatment> treatmentList = new ArrayList<>();
        plateCondition.setTreatmentCollection(treatmentList);

        //set an empty collection of wells
        List<Well> wellList = new ArrayList<>();
        plateCondition.setWellCollection(wellList);

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
