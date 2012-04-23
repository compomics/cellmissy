/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.ExperimentInfoPanel;
import be.ugent.maf.cellmissy.gui.experiment.ExperimentSetupPanel;
import be.ugent.maf.cellmissy.gui.experiment.PlateSetupPanel;
import be.ugent.maf.cellmissy.gui.plate.PlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class ExperimentSetupPanelController {

    //model
    private ObservableList<Project> projectBindingList;
    private BindingGroup bindingGroup;
    //view
    private ExperimentSetupPanel experimentSetupPanel;
    private ExperimentInfoPanel experimentInfoPanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controller
    private ConditionsPanelController conditionsPanelController;
    private PlateSetupPanelController plateSetupPanelController;
    //services
    private ProjectService projectService;
    private GridBagConstraints gridBagConstraints;

    public ExperimentSetupPanelController(CellMissyController cellMissyController) {
        this.cellMissyController = cellMissyController;

        experimentSetupPanel = new ExperimentSetupPanel();
        experimentInfoPanel = new ExperimentInfoPanel();

        //init child controllers
        conditionsPanelController = new ConditionsPanelController(this);
        plateSetupPanelController = new PlateSetupPanelController(this);
        
        //init services
        projectService = (ProjectService) cellMissyController.getBeanByName("projectService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        initExperimentInfoPanel();
    }

    public ExperimentSetupPanel getExperimentSetupPanel() {
        return experimentSetupPanel;
    }

    public CellMissyController getCellMissyController() {
        return cellMissyController;
    }

    private void initExperimentInfoPanel() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentInfoPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        experimentSetupPanel.getExperimentInfoParentPanel().add(experimentInfoPanel, gridBagConstraints);
    }
}
