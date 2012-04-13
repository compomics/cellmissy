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

    public AssayEcm2DPanel getAssayEcm2DPanel() {
        return assayEcm2DPanel;
    }

    public AssayEcm3DPanel getAssayEcm3DPanel() {
        return assayEcm3DPanel;
    }

    public ObservableList<MatrixDimension> getMatrixDimensionBindingList() {
        return matrixDimensionBindingList;
    }

    public ObservableList<Assay> getAssay2DBindingList() {
        return assay2DBindingList;
    }

    public ObservableList<Assay> getAssay3DBindingList() {
        return assay3DBindingList;
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
                if (((MatrixDimension) (conditionsPanelController.getConditionsSetupPanel().getEcmDimensionComboBox().getSelectedItem())).getMatrixDimension().equals("2D")) {
                    switchChildPanels(assayEcm2DPanel, assayEcm3DPanel);
                    conditionsPanelController.switchAssayBinding(assayEcm2DPanel.getAssayComboBox());
                } else if(((MatrixDimension) (conditionsPanelController.getConditionsSetupPanel().getEcmDimensionComboBox().getSelectedItem())).getMatrixDimension().equals("3D")){
                    switchChildPanels(assayEcm3DPanel, assayEcm2DPanel);
                    conditionsPanelController.switchAssayBinding(assayEcm3DPanel.getAssayComboBox());
                }
                conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel().revalidate();
                conditionsPanelController.getConditionsSetupPanel().getAssayEcmParentPanel().repaint();
            }
        });

        //conditionsPanelController.getConditionsSetupPanel().getEcmDimensionComboBox().setSelectedIndex(0);

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
