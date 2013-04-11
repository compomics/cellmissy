/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.experiment.setup.AssayEcm25DPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.setup.AssayEcm2DPanel;
import be.ugent.maf.cellmissy.gui.experiment.setup.AssayEcm3DPanel;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.EcmService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * AssayEcm Controller: according to matrix dimension (2D/3D) set up assay/ECM details during experiment design Parent Controller: Setup Conditions Controller
 *
 * @author Paola
 */
@Controller("assayEcmController")
public class AssayEcmController {

    // model
    //binding list for matrix dimensions
    private ObservableList<MatrixDimension> matrixDimensionBindingList;
    //binding list for assays (2D and 3D)
    private ObservableList<Assay> assay2DBindingList;
    private ObservableList<Assay> assay3DBindingList;
    private ObservableList<Assay> assay25DBindingList;
    private ObservableList<String> polymerisationPhBindingList;
    //binding list for ecm composition (2D and 3D)
    private ObservableList<EcmComposition> ecm2DCompositionBindingList;
    private ObservableList<EcmComposition> ecm3DCompositionBindingList;
    private ObservableList<EcmComposition> ecm25DCompositionBindingList;
    //binding list for ecm coating (only for 2D)
    private ObservableList<BottomMatrix> bottomMatrixBindingList;
    //binding list for ecm density (only for 3D)
    private ObservableList<EcmDensity> ecmDensityBindingList;
    private BindingGroup bindingGroup;
    // view
    private AssayEcm2DPanel assayEcm2DPanel;
    private AssayEcm3DPanel assayEcm3DPanel;
    private AssayEcm25DPanel assayEcm25DPanel;
    // parent controller
    @Autowired
    private SetupConditionsController setupConditionsController;
    // services
    @Autowired
    private AssayService assayService;
    @Autowired
    private EcmService ecmService;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //init views
        initEcmPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public ObservableList<MatrixDimension> getMatrixDimensionBindingList() {
        return matrixDimensionBindingList;
    }

    public ObservableList<Assay> getAssay2DBindingList() {
        return assay2DBindingList;
    }

    public ObservableList<Assay> getAssay25DBindingList() {
        return assay25DBindingList;
    }

    public ObservableList<Assay> getAssay3DBindingList() {
        return assay3DBindingList;
    }

    public ObservableList<EcmComposition> getEcm2DCompositionBindingList() {
        return ecm2DCompositionBindingList;
    }

    public ObservableList<EcmComposition> getEcm3DCompositionBindingList() {
        return ecm3DCompositionBindingList;
    }

    public ObservableList<EcmComposition> getEcm25DCompositionBindingList() {
        return ecm25DCompositionBindingList;
    }

    public ObservableList<BottomMatrix> getBottomMatrixBindingList() {
        return bottomMatrixBindingList;
    }

    public AssayEcm3DPanel getAssayEcm3DPanel() {
        return assayEcm3DPanel;
    }

    public AssayEcm25DPanel getAssayEcm25DPanel() {
        return assayEcm25DPanel;
    }

    public AssayEcm2DPanel getAssayEcm2DPanel() {
        return assayEcm2DPanel;
    }

    public ObservableList<String> getPolymerisationPhBindingList() {
        return polymerisationPhBindingList;
    }

    public ObservableList<EcmDensity> getEcmDensityBindingList() {
        return ecmDensityBindingList;
    }

    /**
     * public methods
     */
    /**
     * update assay/ECM fields for previous condition
     *
     * @param plateCondition
     */
    public void updateAssayEcmConditionFields(PlateCondition plateCondition) {
        MatrixDimension matrixDimension = (MatrixDimension) setupConditionsController.getSetupConditionsPanel().getEcmDimensionComboBox().getSelectedItem();
        String dimension = matrixDimension.getDimension();
        switch (dimension) {
            case "2D":
                // 2D situation
                //set assay
                plateCondition.setAssay(assay2DBindingList.get(assayEcm2DPanel.getAssayComboBox().getSelectedIndex()));
                //ecm composition
                plateCondition.getEcm().setEcmComposition(ecm2DCompositionBindingList.get(assayEcm2DPanel.getCompositionComboBox().getSelectedIndex()));
                //ecm coating type is not valid for 2D: always monomeric coating
                //ecm concentration
                //unit Of Measure
                plateCondition.getEcm().setConcentrationUnit(assayEcm2DPanel.getConcentrationUnitOfMeasure().getSelectedItem().toString());
                try {
                    plateCondition.getEcm().setConcentration(Double.parseDouble(assayEcm2DPanel.getConcentrationTextField().getText()));
                } catch (NumberFormatException e) {
                    String message = "Please insert a valid Concentration";
                    setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                    setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
                    assayEcm2DPanel.getConcentrationTextField().requestFocusInWindow();
                }
                //ecm volume
                //unit Of Measure
                plateCondition.getEcm().setVolumeUnit(assayEcm2DPanel.getVolumeUnitLabel().getText());
                try {
                    plateCondition.getEcm().setVolume(Double.parseDouble(assayEcm2DPanel.getVolumeTextField().getText()));
                } catch (NumberFormatException e) {
                    String message = "Please insert a valid Volume";
                    setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                    setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
                    assayEcm2DPanel.getVolumeTextField().requestFocusInWindow();
                }
                //ecm coating time
                plateCondition.getEcm().setCoatingTime(assayEcm2DPanel.getCoatingTimeTextField().getText());
                //ecm coating temperature
                plateCondition.getEcm().setCoatingTemperature(assayEcm2DPanel.getCoatingTemperatureTextField().getText());
                break;
            case "3D":
                // 3D situation
                //set assay
                plateCondition.setAssay(assay3DBindingList.get(assayEcm3DPanel.getAssayComboBox().getSelectedIndex()));
                //ecm composition
                plateCondition.getEcm().setEcmComposition(ecm3DCompositionBindingList.get(assayEcm3DPanel.getCompositionComboBox().getSelectedIndex()));
                //ecm density
                plateCondition.getEcm().setEcmDensity(ecmDensityBindingList.get(assayEcm3DPanel.getDensityComboBox().getSelectedIndex()));
                //ecm volume TOP
                //unit Of Measure
                plateCondition.getEcm().setVolumeUnit(assayEcm3DPanel.getTopVolumeUnitLabel().getText());
                try {
                    plateCondition.getEcm().setTopMatrixVolume(Double.parseDouble(assayEcm3DPanel.getTopMatrixVolumeTextField().getText()));
                } catch (NumberFormatException e) {
                    String message = "Please insert a valid Volume";
                    setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                    setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
                    assayEcm3DPanel.getTopMatrixVolumeTextField().requestFocusInWindow();
                }
                // bottom matrix
                plateCondition.getEcm().setBottomMatrix(bottomMatrixBindingList.get(assayEcm3DPanel.getBottomMatrixTypeComboBox().getSelectedIndex()));
                String type = plateCondition.getEcm().getBottomMatrix().getType();
                switch (type) {
                    case "thin gel coating":
                        plateCondition.getEcm().setBottomMatrixVolume(null);
                        break;
                    case "gel":
                        try {
                            plateCondition.getEcm().setBottomMatrixVolume(Double.parseDouble(assayEcm3DPanel.getBottomMatrixVolumeTextField().getText()));
                        } catch (NumberFormatException e) {
                            String message = "Please insert a valid Volume";
                            setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                            setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
                            assayEcm3DPanel.getBottomMatrixVolumeTextField().requestFocusInWindow();
                        }
                        break;
                }
                //ecm polymerization time
                plateCondition.getEcm().setPolymerisationTime(assayEcm3DPanel.getPolymerizationTimeTextField().getText());
                //ecm polymerization temperature
                plateCondition.getEcm().setPolymerisationTemperature(assayEcm3DPanel.getPolymerizationTemperatureTextField().getText());
                // ecm polymerization ph
                plateCondition.getEcm().setPolymerisationPh(polymerisationPhBindingList.get(assayEcm3DPanel.getPolymerizationPhComboBox().getSelectedIndex()));
                break;
            case "2.5D":
                // 2.5D situation
                //set assay
                plateCondition.setAssay(assay25DBindingList.get(assayEcm25DPanel.getAssayComboBox().getSelectedIndex()));
                //ecm composition
                plateCondition.getEcm().setEcmComposition(ecm25DCompositionBindingList.get(assayEcm25DPanel.getCompositionComboBox().getSelectedIndex()));
                //ecm density
                plateCondition.getEcm().setEcmDensity(ecmDensityBindingList.get(assayEcm25DPanel.getDensityComboBox().getSelectedIndex()));
                //ecm volume TOP
                //unit Of Measure
                plateCondition.getEcm().setVolumeUnit(assayEcm25DPanel.getBottomVolumeUnitlabel().getText());
                // bottom matrix
                plateCondition.getEcm().setBottomMatrix(bottomMatrixBindingList.get(assayEcm25DPanel.getBottomMatrixTypeComboBox().getSelectedIndex()));
                switch (plateCondition.getEcm().getBottomMatrix().getType()) {
                    case "thin gel coating":
                        plateCondition.getEcm().setBottomMatrixVolume(null);
                        break;
                    case "gel":
                        try {
                            plateCondition.getEcm().setBottomMatrixVolume(Double.parseDouble(assayEcm25DPanel.getBottomMatrixVolumeTextField().getText()));
                        } catch (NumberFormatException e) {
                            String message = "Please insert a valid Volume";
                            setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                            setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
                            assayEcm25DPanel.getBottomMatrixVolumeTextField().requestFocusInWindow();
                        }
                        break;
                }
                //ecm polymerization time
                plateCondition.getEcm().setPolymerisationTime(assayEcm25DPanel.getPolymerizationTimeTextField().getText());
                //ecm polymerization temperature
                plateCondition.getEcm().setPolymerisationTemperature(assayEcm25DPanel.getPolymerizationTemperatureTextField().getText());
                // ecm polymerization ph
                plateCondition.getEcm().setPolymerisationPh(polymerisationPhBindingList.get(assayEcm25DPanel.getPolymerizationPhComboBox().getSelectedIndex()));
                break;
        }
    }

    /**
     * for a current selected condition, update input fields (components selected values and text fields)
     *
     * @param plateCondition
     */
    public void updateAssayEcmInputFields(PlateCondition plateCondition) {
        MatrixDimension matrixDimension = plateCondition.getAssay().getMatrixDimension();
        setupConditionsController.getSetupConditionsPanel().getEcmDimensionComboBox().setSelectedIndex(matrixDimensionBindingList.indexOf(matrixDimension));
        String dimension = matrixDimension.getDimension();
        switch (dimension) {
            case "2D":
                assayEcm2DPanel.getAssayComboBox().setSelectedIndex(assay2DBindingList.indexOf(plateCondition.getAssay()));
                assayEcm2DPanel.getCompositionComboBox().setSelectedIndex(ecm2DCompositionBindingList.indexOf(plateCondition.getEcm().getEcmComposition()));
                assayEcm2DPanel.getConcentrationTextField().setText("" + plateCondition.getEcm().getConcentration());
                assayEcm2DPanel.getConcentrationUnitOfMeasure().setSelectedItem(plateCondition.getEcm().getConcentrationUnit());
                assayEcm2DPanel.getVolumeTextField().setText("" + plateCondition.getEcm().getVolume());
                assayEcm2DPanel.getCoatingTimeTextField().setText(plateCondition.getEcm().getCoatingTime());
                assayEcm2DPanel.getCoatingTemperatureTextField().setText(plateCondition.getEcm().getCoatingTemperature());
                break;
            case "3D":
                assayEcm3DPanel.getAssayComboBox().setSelectedIndex(assay3DBindingList.indexOf(plateCondition.getAssay()));
                assayEcm3DPanel.getCompositionComboBox().setSelectedIndex(ecm3DCompositionBindingList.indexOf(plateCondition.getEcm().getEcmComposition()));
                assayEcm3DPanel.getDensityComboBox().setSelectedIndex(ecmDensityBindingList.indexOf(plateCondition.getEcm().getEcmDensity()));
                assayEcm3DPanel.getBottomMatrixTypeComboBox().setSelectedIndex(bottomMatrixBindingList.indexOf(plateCondition.getEcm().getBottomMatrix()));
                assayEcm3DPanel.getBottomMatrixVolumeTextField().setText("" + plateCondition.getEcm().getBottomMatrixVolume());
                assayEcm3DPanel.getTopMatrixVolumeTextField().setText("" + plateCondition.getEcm().getTopMatrixVolume());
                assayEcm3DPanel.getPolymerizationTimeTextField().setText(plateCondition.getEcm().getPolymerisationTime());
                assayEcm3DPanel.getPolymerizationTemperatureTextField().setText(plateCondition.getEcm().getPolymerisationTemperature());
                break;
            case "2.5D":
                assayEcm25DPanel.getAssayComboBox().setSelectedIndex(assay25DBindingList.indexOf(plateCondition.getAssay()));
                assayEcm25DPanel.getCompositionComboBox().setSelectedIndex(ecm25DCompositionBindingList.indexOf(plateCondition.getEcm().getEcmComposition()));
                assayEcm25DPanel.getDensityComboBox().setSelectedIndex(ecmDensityBindingList.indexOf(plateCondition.getEcm().getEcmDensity()));
                assayEcm25DPanel.getBottomMatrixTypeComboBox().setSelectedIndex(bottomMatrixBindingList.indexOf(plateCondition.getEcm().getBottomMatrix()));
                assayEcm25DPanel.getBottomMatrixVolumeTextField().setText("" + plateCondition.getEcm().getBottomMatrixVolume());
                assayEcm25DPanel.getPolymerizationTimeTextField().setText(plateCondition.getEcm().getPolymerisationTime());
                assayEcm25DPanel.getPolymerizationTemperatureTextField().setText(plateCondition.getEcm().getPolymerisationTemperature());
                break;
        }
    }

    /**
     * Reset fields for Assay and ECM parameters
     *
     ********************************
     * @param plateCondition
     */
    public void resetAssayEcmInputFields(PlateCondition plateCondition) {
        switch (plateCondition.getAssay().getMatrixDimension().getDimension()) {
            case "2D":
                //reset input fields in 3D panel
                assayEcm3DPanel.getAssayComboBox().setSelectedItem(assay3DBindingList.get(0));
                assayEcm3DPanel.getCompositionComboBox().setSelectedItem(ecm3DCompositionBindingList.get(0));
                assayEcm3DPanel.getDensityComboBox().setSelectedItem(ecmDensityBindingList.get(0));
                assayEcm3DPanel.getBottomMatrixTypeComboBox().setSelectedItem(bottomMatrixBindingList.get(1));
                assayEcm3DPanel.getTopMatrixVolumeTextField().setText("0");
                assayEcm3DPanel.getPolymerizationTimeTextField().setText("");
                assayEcm3DPanel.getPolymerizationTemperatureTextField().setText("");
                break;
            case "3D":
                //reset input fields in 2D panel
                assayEcm2DPanel.getAssayComboBox().setSelectedItem(assay2DBindingList.get(0));
                assayEcm2DPanel.getCompositionComboBox().setSelectedItem(ecm2DCompositionBindingList.get(0));
                assayEcm2DPanel.getConcentrationTextField().setText("0");
                assayEcm2DPanel.getVolumeTextField().setText("0");
                assayEcm2DPanel.getCoatingTimeTextField().setText("");
                assayEcm2DPanel.getCoatingTemperatureTextField().setText("");
                break;
        }
    }

    /**
     * validate user input for the ECM 2D panel
     *
     * @return a list of string (messages to show to the user)
     */
    public List<String> validate2DEcm() {
        List<String> messages = new ArrayList<>();
        if (assayEcm2DPanel.getConcentrationTextField().getText().isEmpty()) {
            String message = "ECM Concentration cannot be null";
            messages.add(message);
            assayEcm2DPanel.getConcentrationTextField().requestFocusInWindow();
        }

        if (assayEcm2DPanel.getVolumeTextField().getText().isEmpty()) {
            String message = "ECM Volume cannot be null";
            messages.add(message);
            assayEcm2DPanel.getVolumeTextField().requestFocusInWindow();
        }
        return messages;

    }

    /**
     * validate user input for the ECM 3D panel
     *
     * @return a list of string (messages to show to the user)
     */
    public List<String> validate3DEcm() {
        List<String> messages = new ArrayList<>();
        if (assayEcm3DPanel.getTopMatrixVolumeTextField().getText().isEmpty()) {
            String message = "ECM Top Volume cannot be null";
            messages.add(message);
            assayEcm3DPanel.getTopMatrixVolumeTextField().requestFocusInWindow();
        }
        return messages;
    }

    /**
     * initialize ECM dimension Panel
     */
    private void initEcmPanel() {
        //init matrixDimensionJCombo
        matrixDimensionBindingList = ObservableCollections.observableList(ecmService.findAllMatrixDimension());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, matrixDimensionBindingList, setupConditionsController.getSetupConditionsPanel().getEcmDimensionComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        //init sub views
        assayEcm2DPanel = new AssayEcm2DPanel();
        assayEcm3DPanel = new AssayEcm3DPanel();
        assayEcm25DPanel = new AssayEcm25DPanel();
        initAssayEcm2DPanel();
        initAssayEcm3DPanel();
        initAssayEcm25DPanel();
        /**
         * add action listeners
         */
        //show different assay-ecm, 2D-3D panels
        setupConditionsController.getSetupConditionsPanel().getEcmDimensionComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MatrixDimension dimension = matrixDimensionBindingList.get(setupConditionsController.getSetupConditionsPanel().getEcmDimensionComboBox().getSelectedIndex());
                String dim = dimension.getDimension();
                switch (dim) {
                    case "2D":
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm2DPanel, assayEcm3DPanel);
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm2DPanel, assayEcm25DPanel);
                        break;
                    case "3D":
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm3DPanel, assayEcm2DPanel);
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm3DPanel, assayEcm25DPanel);
                        break;
                    case "2.5D":
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm25DPanel, assayEcm2DPanel);
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm25DPanel, assayEcm3DPanel);
                        break;
                }
                setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel().revalidate();
                setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel().repaint();
            }
        });

        //conditionsPanelController.getSetupConditionsPanel().getEcmDimensionComboBox().setSelectedIndex(0);
    }

    /**
     * initialize assay/ECM Panel for 2D matrix dimension
     */
    private void initAssayEcm2DPanel() {
        //init assayBindingList
        assay2DBindingList = ObservableCollections.observableList(assayService.findByMatrixDimensionName("2D"));
        //init assayJCombo (2D)
        JComboBoxBinding assayComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, assay2DBindingList, assayEcm2DPanel.getAssayComboBox());
        bindingGroup.addBinding(assayComboBoxBinding);
        //init ecmCompositionBindingList
        ecm2DCompositionBindingList = ObservableCollections.observableList(ecmService.findEcmCompositionByMatrixDimensionName("2D"));
        //init ecmCompositionJCombo (2D)
        JComboBoxBinding ecmCompositionComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecm2DCompositionBindingList, assayEcm2DPanel.getCompositionComboBox());
        bindingGroup.addBinding(ecmCompositionComboBoxBinding);
        //do the binding
        bindingGroup.bind();
        //add strings for Concentration Unit of Measure
        assayEcm2DPanel.getConcentrationUnitOfMeasure().addItem("mg/ml");
        assayEcm2DPanel.getConcentrationUnitOfMeasure().addItem("\u00B5" + "g/well");
        //set volume unit of measure
        assayEcm2DPanel.getVolumeUnitLabel().setText("\u00B5" + "l");
        //set default fields
        assayEcm2DPanel.getCompositionComboBox().setSelectedIndex(0);
        assayEcm2DPanel.getConcentrationTextField().setText("0.04");
        assayEcm2DPanel.getConcentrationUnitOfMeasure().setSelectedIndex(0);
        assayEcm2DPanel.getVolumeTextField().setText("100");
        assayEcm2DPanel.getCoatingTimeTextField().setText("60");
        assayEcm2DPanel.getCoatingTemperatureTextField().setText("RT");
        /**
         * add action listeners
         */
        //add new composition for 2D gel
        assayEcm2DPanel.getAddCompositionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!assayEcm2DPanel.getCompositionTextField().getText().isEmpty()) {
                    EcmComposition ecmComposition = new EcmComposition();
                    ecmComposition.setCompositionType(assayEcm2DPanel.getCompositionTextField().getText());
                    ecmComposition.setMatrixDimension(matrixDimensionBindingList.get(0));
                    ecm2DCompositionBindingList.add(ecmComposition);
                    ecmService.saveEcmComposition(ecmComposition);
                    assayEcm2DPanel.getCompositionTextField().setText("");
                } else {
                    String message = "Please insert a name for the new ECM composition!";
                    setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                    assayEcm3DPanel.getCompositionTextField().requestFocus();
                }
            }
        });
    }

    /**
     * Initialize assay/ECM Panel for 3D matrix dimension
     */
    private void initAssayEcm3DPanel() {
        // init list for bottom matrix type: only for 3D (and 2.5D)
        bottomMatrixBindingList = ObservableCollections.observableList(ecmService.findAllBottomMatrix());
        //init assayBindingList
        assay3DBindingList = ObservableCollections.observableList(assayService.findByMatrixDimensionName("3D"));
        //init assayJCombo (3D)
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, assay3DBindingList, assayEcm3DPanel.getAssayComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init ecmCompositionBindingList
        ecm3DCompositionBindingList = ObservableCollections.observableList(ecmService.findEcmCompositionByMatrixDimensionName("3D"));
        //init ecmCompositionJCombo (3D)
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecm3DCompositionBindingList, assayEcm3DPanel.getCompositionComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init densityBindingList (only for 3D)
        ecmDensityBindingList = ObservableCollections.observableList(ecmService.findAllEcmDensity());
        //init densityJCombo
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecmDensityBindingList, assayEcm3DPanel.getDensityComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init coatingJCombo
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, bottomMatrixBindingList, assayEcm3DPanel.getBottomMatrixTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //set volume unit of measure
        assayEcm3DPanel.getTopVolumeUnitLabel().setText("\u00B5" + "l");
        assayEcm3DPanel.getBottomVolumeUnitlabel().setText("\u00B5" + "l");
        //init polymerisation PH JCombo
        polymerisationPhBindingList = ObservableCollections.observableList(ecmService.findAllPolimerysationPh());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, polymerisationPhBindingList, assayEcm3DPanel.getPolymerizationPhComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();
        //set default fields
        assayEcm3DPanel.getCompositionComboBox().setSelectedIndex(0);
        assayEcm3DPanel.getDensityComboBox().setSelectedIndex(1);
        assayEcm3DPanel.getBottomMatrixTypeComboBox().setSelectedIndex(1);
        assayEcm3DPanel.getTopMatrixVolumeTextField().setText("40");
        assayEcm3DPanel.getBottomMatrixVolumeTextField().setText("40");
        assayEcm3DPanel.getPolymerizationTimeTextField().setText("30");
        assayEcm3DPanel.getPolymerizationTemperatureTextField().setText("37 C");
        assayEcm3DPanel.getPolymerizationPhComboBox().setSelectedIndex(0);
        /*
         * add action listeners
         */
        /**
         * Add a new composition for 3D ECM
         */
        assayEcm3DPanel.getAddCompositionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!assayEcm3DPanel.getCompositionTextField().getText().isEmpty()) {
                    EcmComposition ecmComposition = new EcmComposition();
                    ecmComposition.setCompositionType(assayEcm3DPanel.getCompositionTextField().getText());
                    ecmComposition.setMatrixDimension(matrixDimensionBindingList.get(1));
                    ecm3DCompositionBindingList.add(ecmComposition);
                    ecmService.saveEcmComposition(ecmComposition);
                    assayEcm3DPanel.getCompositionTextField().setText("");
                } else {
                    String message = "Please insert a name for the new ECM composition!";
                    setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                    assayEcm3DPanel.getCompositionTextField().requestFocus();
                }
            }
        });
        /**
         * If bottom matrix type is thin gel coating, the bottom volume must be left empty (and disabled) If bottom matrix type is gel, the bottom volume text field is enabled and set by default to 40
         */
        assayEcm3DPanel.getBottomMatrixTypeComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BottomMatrix bottomMatrix = bottomMatrixBindingList.get(assayEcm3DPanel.getBottomMatrixTypeComboBox().getSelectedIndex());
                switch (bottomMatrix.getType()) {
                    case "thin gel coating":
                        assayEcm3DPanel.getBottomMatrixVolumeTextField().setEnabled(false);
                        break;
                    case "gel":
                        assayEcm3DPanel.getBottomMatrixVolumeTextField().setEnabled(true);
                        break;
                }
            }
        });
    }

    /**
     * Initialize assay/ECM panel for 2.5D matrix dimension
     */
    private void initAssayEcm25DPanel() {
        //init assayBindingList
        assay25DBindingList = ObservableCollections.observableList(assayService.findByMatrixDimensionName("2.5D"));
        //init assayJCombo (2.5D)
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, assay25DBindingList, assayEcm25DPanel.getAssayComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init ecmCompositionBindingList
        ecm25DCompositionBindingList = ObservableCollections.observableList(ecmService.findEcmCompositionByMatrixDimensionName("2.5D"));
        //init ecmCompositionJCombo (3D)
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecm25DCompositionBindingList, assayEcm25DPanel.getCompositionComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init densityJCombo
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecmDensityBindingList, assayEcm25DPanel.getDensityComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init coatingJCombo
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, bottomMatrixBindingList, assayEcm25DPanel.getBottomMatrixTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //set volume unit of measure
        assayEcm25DPanel.getBottomVolumeUnitlabel().setText("\u00B5" + "l");
        //init polymerisation PH JCombo
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, polymerisationPhBindingList, assayEcm25DPanel.getPolymerizationPhComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();
        //set default fields
        assayEcm25DPanel.getCompositionComboBox().setSelectedIndex(0);
        assayEcm25DPanel.getDensityComboBox().setSelectedIndex(1);
        assayEcm25DPanel.getBottomMatrixTypeComboBox().setSelectedIndex(1);
        assayEcm25DPanel.getBottomMatrixVolumeTextField().setText("40");
        assayEcm25DPanel.getPolymerizationTimeTextField().setText("30");
        assayEcm25DPanel.getPolymerizationTemperatureTextField().setText("37 C");
        assayEcm25DPanel.getPolymerizationPhComboBox().setSelectedIndex(0);
        /*
         * add action listeners
         */
        /**
         * Add a new composition for 2.5D ECM
         */
        assayEcm25DPanel.getAddCompositionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!assayEcm25DPanel.getCompositionTextField().getText().isEmpty()) {
                    EcmComposition ecmComposition = new EcmComposition();
                    ecmComposition.setCompositionType(assayEcm25DPanel.getCompositionTextField().getText());
                    ecmComposition.setMatrixDimension(matrixDimensionBindingList.get(1));
                    ecm3DCompositionBindingList.add(ecmComposition);
                    ecmService.saveEcmComposition(ecmComposition);
                    assayEcm25DPanel.getCompositionTextField().setText("");
                } else {
                    String message = "Please insert a name for the new ECM composition!";
                    setupConditionsController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                    assayEcm25DPanel.getCompositionTextField().requestFocus();
                }
            }
        });
        /**
         * If bottom matrix type is thin gel coating, the bottom volume must be left empty (and disabled) If bottom matrix type is gel, the bottom volume text field is enabled and set by default to 40
         */
        assayEcm25DPanel.getBottomMatrixTypeComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BottomMatrix bottomMatrix = bottomMatrixBindingList.get(assayEcm25DPanel.getBottomMatrixTypeComboBox().getSelectedIndex());
                switch (bottomMatrix.getType()) {
                    case "thin gel coating":
                        assayEcm25DPanel.getBottomMatrixVolumeTextField().setEnabled(false);
                        break;
                    case "gel":
                        assayEcm25DPanel.getBottomMatrixVolumeTextField().setEnabled(true);
                        break;
                }
            }
        });
    }
}
