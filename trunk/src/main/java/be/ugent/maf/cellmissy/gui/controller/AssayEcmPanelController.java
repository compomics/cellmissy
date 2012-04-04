/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.gui.experiment.AssayEcm2DPanel;
import be.ugent.maf.cellmissy.gui.experiment.AssayEcm3DPanel;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.EcmService;
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

    //model
    private ObservableList<Assay> assay2DBindingList;
    private ObservableList<Assay> assay3DBindingList;
    private BindingGroup bindingGroup;
    //view
    private AssayEcm2DPanel assayEcm2DPanel;
    private AssayEcm3DPanel assayEcm3DPanel;
    //parent controller
    private ConditionsSetupPanelController conditionsSetupPanelController;
    //services
    private AssayService assayService;
    private EcmService ecmService;

    public AssayEcmPanelController(ConditionsSetupPanelController conditionsSetupPanelController) {
        this.conditionsSetupPanelController = conditionsSetupPanelController;

        //init services
        assayService = (AssayService) conditionsSetupPanelController.getExperimentSetupPanelController().getCellMissyController().getBeanByName("assayService");
        ecmService = (EcmService) conditionsSetupPanelController.getExperimentSetupPanelController().getCellMissyController().getBeanByName("ecmService");

        bindingGroup = new BindingGroup();
        
        //init views
        assayEcm2DPanel = new AssayEcm2DPanel();
        assayEcm3DPanel = new AssayEcm3DPanel();
        initAssayEcm2DPanel();
        initAssayEcm3DPanel();
    }

    private void initAssayEcm2DPanel() {

        //init assayJCombo (3D)
        assay2DBindingList = ObservableCollections.observableList(assayService.findByMatrixDimension(ecmService.findByDimension("2D")));
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, assay2DBindingList, assayEcm2DPanel.getAssayComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

    }

    private void initAssayEcm3DPanel() {

        //init assayJCombo (2D)
        assay3DBindingList = ObservableCollections.observableList(assayService.findByMatrixDimension(ecmService.findByDimension("3D")));
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, assay3DBindingList, assayEcm3DPanel.getAssayComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
    }

    public AssayEcm2DPanel getAssayEcm2DPanel() {
        return assayEcm2DPanel;
    }

    public AssayEcm3DPanel getAssayEcm3DPanel() {
        return assayEcm3DPanel;
    }
}
