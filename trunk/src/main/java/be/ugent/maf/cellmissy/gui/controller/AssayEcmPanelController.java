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
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
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
public class AssayEcmPanelController {

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
    private ConditionsPanelController conditionsPanelController;
    // services
    private AssayService assayService;
    private EcmService ecmService;
    private GridBagConstraints gridBagConstraints;

    public AssayEcmPanelController(ConditionsPanelController conditionsPanelController) {
        this.conditionsPanelController = conditionsPanelController;

        //init services
        assayService = (AssayService) conditionsPanelController.getExperimentSetupPanelController().getCellMissyController().getBeanByName("assayService");
        ecmService = (EcmService) conditionsPanelController.getExperimentSetupPanelController().getCellMissyController().getBeanByName("ecmService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        initEcmPanel();
    }

    public ObservableList<MatrixDimension> getMatrixDimensionBindingList() {
        return matrixDimensionBindingList;
    }

    public ObservableList<Assay> getAssay2DBindingList() {
        return assay2DBindingList;
    }

    public ObservableList<EcmComposition> getEcm2DCompositionBindingList() {
        return ecm2DCompositionBindingList;
    }

    public ObservableList<EcmCoating> getEcmCoatingBindingList() {
        return ecmCoatingBindingList;
    }

    private void initEcmPanel() {
        //init matrixDimensionJCombo
        matrixDimensionBindingList = ObservableCollections.observableList(ecmService.findAllMatrixDimension());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, matrixDimensionBindingList, conditionsPanelController.getConditionsSetupPanel().getEcmDimensionComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        //init sub views
        assayEcm2DPanel = new AssayEcm2DPanel();
        assayEcm3DPanel = new AssayEcm3DPanel();
        initAssayEcm2DPanel();
        initAssayEcm3DPanel();

        //add action listener
        //show different assay-ecm, 2D-3D panels
        conditionsPanelController.getConditionsSetupPanel().getEcmDimensionComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                switch (((MatrixDimension) (conditionsPanelController.getConditionsSetupPanel().getEcmDimensionComboBox().getSelectedItem())).getMatrixDimension()) {
                    case "2D":
                        switchChildPanels(assayEcm2DPanel, assayEcm3DPanel);
                        break;
                    case "3D":
                        switchChildPanels(assayEcm3DPanel, assayEcm2DPanel);
                        break;
                }
                conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel().revalidate();
                conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel().repaint();
            }
        });

        conditionsPanelController.getConditionsSetupPanel().getEcmDimensionComboBox().setSelectedIndex(0);

    }

    public void updateAssayEcmConditionFields(PlateCondition plateCondition) {
        if (plateCondition.getMatrixDimension().getMatrixDimension().equals("2D")) {
            //set assay
            plateCondition.setAssay(assay2DBindingList.get(assayEcm2DPanel.getAssayComboBox().getSelectedIndex()));
            //ecm composition
            plateCondition.getEcm().setEcmComposition(ecm2DCompositionBindingList.get(assayEcm2DPanel.getCompositionComboBox().getSelectedIndex()));
            //ecm coating type
            plateCondition.getEcm().setEcmCoating(ecmCoatingBindingList.get(assayEcm2DPanel.getCoatingComboBox().getSelectedIndex()));
            //ecm concentration
            plateCondition.getEcm().setConcentration(Integer.parseInt(assayEcm2DPanel.getConcentrationTextField().getText()));
            //ecm volume
            plateCondition.getEcm().setVolume(Integer.parseInt(assayEcm2DPanel.getVolumeTextField().getText()));
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
            plateCondition.getEcm().setVolume(Integer.parseInt(assayEcm3DPanel.getVolumeTextField().getText()));
            //ecm polymerization time
            plateCondition.getEcm().setPolymerisationTime(assayEcm3DPanel.getPolymerizationTimeTextField().getText());
            //ecm polymerization temperature
            plateCondition.getEcm().setPolymerisationTemperature(assayEcm3DPanel.getPolymerizationTemperatureTextField().getText());
        }


    }

    public void updateAssayEcmInputFields(PlateCondition plateCondition) {
        if (plateCondition.getMatrixDimension().getMatrixDimension().equals("2D")) {
            assayEcm2DPanel.getAssayComboBox().setSelectedIndex(assay2DBindingList.indexOf(plateCondition.getAssay()));
            assayEcm2DPanel.getCompositionComboBox().setSelectedIndex(ecm2DCompositionBindingList.indexOf(plateCondition.getEcm().getEcmComposition()));
            assayEcm2DPanel.getCoatingComboBox().setSelectedIndex(ecmCoatingBindingList.indexOf(plateCondition.getEcm().getEcmCoating()));
            assayEcm2DPanel.getConcentrationTextField().setText("" + plateCondition.getEcm().getConcentration());
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

        //do the binding
        bindingGroup.bind();
    }

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

        //do the binding
        bindingGroup.bind();
    }

    private void switchChildPanels(JPanel panelToAdd, JPanel panelToRemove) {
        if (!GuiUtils.containsComponent(conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel(), panelToAdd)) {
            conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel().add(panelToAdd, gridBagConstraints);
        }
        if (GuiUtils.containsComponent(conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel(), panelToRemove)) {
            conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel().remove(panelToRemove);
        }
    }
}