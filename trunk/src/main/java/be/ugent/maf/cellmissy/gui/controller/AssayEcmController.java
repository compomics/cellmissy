/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.EcmCoating;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.AssayEcm2DPanel;
import be.ugent.maf.cellmissy.gui.experiment.AssayEcm3DPanel;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.EcmService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * AssayEcm Controller: according to matrix dimension (2D/3D) set up assay/ECM details during experiment  design
 * Parent Controller: Setup Conditions Controller
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
    //binding list for ecm composition (2D and 3D)
    private ObservableList<EcmComposition> ecm2DCompositionBindingList;
    private ObservableList<EcmComposition> ecm3DCompositionBindingList;
    //binding list for ecm coating (only for 2D)
    private ObservableList<EcmCoating> ecmCoatingBindingList;
    //binding list for ecm density (only for 3D)
    private ObservableList<EcmDensity> ecmDensityBindingList;
    private BindingGroup bindingGroup;
    // view
    private AssayEcm2DPanel assayEcm2DPanel;
    private AssayEcm3DPanel assayEcm3DPanel;
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
     * public methods and classes
     */
    /**
     * getters and setters
     * 
     */
    public ObservableList<MatrixDimension> getMatrixDimensionBindingList() {
        return matrixDimensionBindingList;
    }

    public ObservableList<Assay> getAssay2DBindingList() {
        return assay2DBindingList;
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

    public ObservableList<EcmCoating> getEcmCoatingBindingList() {
        return ecmCoatingBindingList;
    }

    public AssayEcm3DPanel getAssayEcm3DPanel() {
        return assayEcm3DPanel;
    }

    /**
     * update assay/ECM fields for previous condition
     * @param plateCondition 
     */
    public void updateAssayEcmConditionFields(PlateCondition plateCondition) {
        if (plateCondition.getMatrixDimension().getMatrixDimension().equals("2D")) {
            //set assay
            plateCondition.setAssay(assay2DBindingList.get(assayEcm2DPanel.getAssayComboBox().getSelectedIndex()));
            //ecm composition
            plateCondition.getEcm().setEcmComposition(ecm2DCompositionBindingList.get(assayEcm2DPanel.getCompositionComboBox().getSelectedIndex()));
            //ecm coating type
            plateCondition.getEcm().setEcmCoating(ecmCoatingBindingList.get(assayEcm2DPanel.getCoatingComboBox().getSelectedIndex()));
            //ecm concentration
            //unit Of Measure
            plateCondition.getEcm().setConcentrationUnit(assayEcm2DPanel.getConcentrationUnitOfMeasure().getSelectedItem().toString());
            try {
                plateCondition.getEcm().setConcentration(Double.parseDouble(assayEcm2DPanel.getConcentrationTextField().getText()));
            } catch (NumberFormatException e) {
                String message = "Please insert a valid Concentration";
                setupConditionsController.showMessage(message, 2);
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
                setupConditionsController.showMessage(message, 2);
                setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
                assayEcm2DPanel.getVolumeTextField().requestFocusInWindow();
            }

            //ecm coating time
            plateCondition.getEcm().setCoatingTime(assayEcm2DPanel.getCoatingTimeTextField().getText());
            //ecm coating temperature
            plateCondition.getEcm().setCoatingTemperature(assayEcm2DPanel.getCoatingTemperatureTextField().getText());
        } else {
            //set assay
            plateCondition.setAssay(assay3DBindingList.get(assayEcm3DPanel.getAssayComboBox().getSelectedIndex()));
            //ecm composition
            plateCondition.getEcm().setEcmComposition(ecm3DCompositionBindingList.get(assayEcm3DPanel.getCompositionComboBox().getSelectedIndex()));
            //ecm density
            plateCondition.getEcm().setEcmDensity(ecmDensityBindingList.get(assayEcm3DPanel.getDensityComboBox().getSelectedIndex()));
            //ecm volume
            //unit Of Measure
            plateCondition.getEcm().setVolumeUnit(assayEcm3DPanel.getVolumeUnitLabel().getText());
            try {
                plateCondition.getEcm().setVolume(Double.parseDouble(assayEcm3DPanel.getVolumeTextField().getText()));
            } catch (NumberFormatException e) {
                String message = "Please insert a valid Volume";
                setupConditionsController.showMessage(message, 2);
                setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
                assayEcm3DPanel.getVolumeTextField().requestFocusInWindow();
            }
            //ecm polymerization time
            plateCondition.getEcm().setPolymerisationTime(assayEcm3DPanel.getPolymerizationTimeTextField().getText());
            //ecm polymerization temperature
            plateCondition.getEcm().setPolymerisationTemperature(assayEcm3DPanel.getPolymerizationTemperatureTextField().getText());
        }
    }

    /**
     * for a current selected condition, update input fields (components selected values and text fields)
     * @param plateCondition 
     */
    public void updateAssayEcmInputFields(PlateCondition plateCondition) {
        if (plateCondition.getMatrixDimension().getMatrixDimension().equals("2D")) {
            assayEcm2DPanel.getAssayComboBox().setSelectedIndex(assay2DBindingList.indexOf(plateCondition.getAssay()));
            assayEcm2DPanel.getCompositionComboBox().setSelectedIndex(ecm2DCompositionBindingList.indexOf(plateCondition.getEcm().getEcmComposition()));
            assayEcm2DPanel.getCoatingComboBox().setSelectedIndex(ecmCoatingBindingList.indexOf(plateCondition.getEcm().getEcmCoating()));
            assayEcm2DPanel.getConcentrationTextField().setText("" + plateCondition.getEcm().getConcentration());
            assayEcm2DPanel.getConcentrationUnitOfMeasure().setSelectedItem(plateCondition.getEcm().getConcentrationUnit());
            assayEcm2DPanel.getVolumeTextField().setText("" + plateCondition.getEcm().getVolume());
            assayEcm2DPanel.getCoatingTimeTextField().setText(plateCondition.getEcm().getCoatingTime());
            assayEcm2DPanel.getCoatingTemperatureTextField().setText(plateCondition.getEcm().getCoatingTemperature());
        } else {
            assayEcm3DPanel.getAssayComboBox().setSelectedIndex(assay3DBindingList.indexOf(plateCondition.getAssay()));
            assayEcm3DPanel.getCompositionComboBox().setSelectedIndex(ecm3DCompositionBindingList.indexOf(plateCondition.getEcm().getEcmComposition()));
            assayEcm3DPanel.getDensityComboBox().setSelectedIndex(ecmDensityBindingList.indexOf(plateCondition.getEcm().getEcmDensity()));
            assayEcm3DPanel.getVolumeTextField().setText("" + plateCondition.getEcm().getVolume());
            assayEcm3DPanel.getPolymerizationTimeTextField().setText(plateCondition.getEcm().getPolymerisationTime());
            assayEcm3DPanel.getPolymerizationTemperatureTextField().setText(plateCondition.getEcm().getPolymerisationTemperature());
        }
    }

    /**
     * 
     * @param plateCondition 
     */
    public void resetAssayEcmInputFields(PlateCondition plateCondition) {
        if (plateCondition.getMatrixDimension().getMatrixDimension().equals("2D")) {
            //reset input fields in 3D panel
            assayEcm3DPanel.getAssayComboBox().setSelectedItem(assay3DBindingList.get(0));
            assayEcm3DPanel.getCompositionComboBox().setSelectedItem(ecm3DCompositionBindingList.get(0));
            assayEcm3DPanel.getDensityComboBox().setSelectedItem(ecmDensityBindingList.get(0));
            assayEcm3DPanel.getVolumeTextField().setText("0");
            assayEcm3DPanel.getPolymerizationTimeTextField().setText("");
            assayEcm3DPanel.getPolymerizationTemperatureTextField().setText("");
        } else {
            //reset input fields in 2D panel
            assayEcm2DPanel.getAssayComboBox().setSelectedItem(assay2DBindingList.get(0));
            assayEcm2DPanel.getCompositionComboBox().setSelectedItem(ecm2DCompositionBindingList.get(0));
            assayEcm2DPanel.getCoatingComboBox().setSelectedItem(ecmCoatingBindingList.get(0));
            assayEcm2DPanel.getConcentrationTextField().setText("0");
            assayEcm2DPanel.getVolumeTextField().setText("0");
            assayEcm2DPanel.getCoatingTimeTextField().setText("");
            assayEcm2DPanel.getCoatingTemperatureTextField().setText("");
        }
    }

    /**
     * validate user input for the ECM 2D panel
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
     * @return a list of string (messages to show to the user)
     */
    public List<String> validate3DEcm() {
        List<String> messages = new ArrayList<>();
        if (assayEcm3DPanel.getVolumeTextField().getText().isEmpty()) {
            String message = "ECM Volume cannot be null";
            messages.add(message);
            assayEcm3DPanel.getVolumeTextField().requestFocusInWindow();
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
        initAssayEcm2DPanel();
        initAssayEcm3DPanel();

        /**
         * add action listeners
         */
        //show different assay-ecm, 2D-3D panels
        setupConditionsController.getSetupConditionsPanel().getEcmDimensionComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                switch (((MatrixDimension) (setupConditionsController.getSetupConditionsPanel().getEcmDimensionComboBox().getSelectedItem())).getMatrixDimension()) {
                    case "2D":
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm2DPanel, assayEcm3DPanel);
                        break;
                    case "3D":
                        GuiUtils.switchChildPanels(setupConditionsController.getSetupConditionsPanel().getAssayEcmParentPanel(), assayEcm3DPanel, assayEcm2DPanel);
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

        //init coatingBindingList (only for 2D)
        ecmCoatingBindingList = ObservableCollections.observableList(ecmService.findAllEcmCoating());
        //init coatingJCombo
        JComboBoxBinding ecmCoatingComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecmCoatingBindingList, assayEcm2DPanel.getCoatingComboBox());
        bindingGroup.addBinding(ecmCoatingComboBoxBinding);

        //add strings for Concentration Unit of Measure
        assayEcm2DPanel.getConcentrationUnitOfMeasure().addItem("mg/ml");
        assayEcm2DPanel.getConcentrationUnitOfMeasure().addItem("\u00B5" + "g/well");

        //set volume unit of measure
        assayEcm2DPanel.getVolumeUnitLabel().setText("\u00B5" + "l");

        //do the binding
        bindingGroup.bind();


        //set default fields
        assayEcm2DPanel.getCompositionComboBox().setSelectedIndex(0);
        assayEcm2DPanel.getCoatingComboBox().setSelectedIndex(0);
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
                }
            }
        });
    }

    /**
     * initialize assay/ECM Panel for 3D matrix dimension
     */
    private void initAssayEcm3DPanel() {
        //init assayBindingList
        assay3DBindingList = ObservableCollections.observableList(assayService.findByMatrixDimensionName("3D"));
        //init assayJCombo (3D)
        JComboBoxBinding assayComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, assay3DBindingList, assayEcm3DPanel.getAssayComboBox());
        bindingGroup.addBinding(assayComboBoxBinding);

        //init ecmCompositionBindingList
        ecm3DCompositionBindingList = ObservableCollections.observableList(ecmService.findEcmCompositionByMatrixDimensionName("3D"));
        //init ecmCompositionJCombo (3D)
        JComboBoxBinding ecmCompositionComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecm3DCompositionBindingList, assayEcm3DPanel.getCompositionComboBox());
        bindingGroup.addBinding(ecmCompositionComboBoxBinding);

        //init densityBindingList (only for 3D)
        ecmDensityBindingList = ObservableCollections.observableList(ecmService.findAllEcmDensity());
        //init densityJCombo
        JComboBoxBinding ecmDensityComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, ecmDensityBindingList, assayEcm3DPanel.getDensityComboBox());
        bindingGroup.addBinding(ecmDensityComboBoxBinding);

        //set volume unit of measure
        assayEcm3DPanel.getVolumeUnitLabel().setText("\u00B5" + "l");

        //do the binding
        bindingGroup.bind();

        //set default fields
        assayEcm3DPanel.getCompositionComboBox().setSelectedIndex(0);
        assayEcm3DPanel.getDensityComboBox().setSelectedIndex(1);
        assayEcm3DPanel.getVolumeTextField().setText("40");
        assayEcm3DPanel.getPolymerizationTimeTextField().setText("30");
        assayEcm3DPanel.getPolymerizationTemperatureTextField().setText("37 C");

        /*
         * add action listeners
         */
        //add new composition for 3D ecm
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
                }
            }
        });
    }
}
