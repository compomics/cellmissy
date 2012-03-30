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
    private ObservableList<PlateFormat> plateFormatBindingList;
    private ObservableList<Project> projectBindingList;
    private BindingGroup bindingGroup;
    //view
    ExperimentSetupPanel experimentSetupPanel;
    ExperimentInfoPanel experimentInfoPanel;
    PlateSetupPanel plateSetupPanel;
    PlatePanel platePanel;
    //parent controller
    private CellMissyController cellMissyController;
    //services
    private PlateService plateService;
    private ProjectService projectService;
    
    private GridBagConstraints gridBagConstraints;

    public ExperimentSetupPanelController(CellMissyController cellMissyController) {

        this.cellMissyController = cellMissyController;
        experimentSetupPanel = new ExperimentSetupPanel();
        experimentInfoPanel = new ExperimentInfoPanel();
        plateSetupPanel = new PlateSetupPanel();
        platePanel = new PlatePanel();

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        plateService = (PlateService) cellMissyController.getBeanByName("plateService");
        projectService = (ProjectService) cellMissyController.getBeanByName("projectService");

        initExperimentInfoPanel();
        initPlateSetupPanel();
        initPanel();
    }

    public ExperimentSetupPanel getExperimentSetupPanel() {
        return experimentSetupPanel;
    }

    private void initExperimentInfoPanel() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentInfoPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    private void initPlateSetupPanel() {
        //init plateFormatJcombo
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, plateFormatBindingList, plateSetupPanel.getPlateFormatComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        // show 96 plate format as default
        plateSetupPanel.getPlateFormatComboBox().setSelectedIndex(0);

        // add action listener
        plateSetupPanel.getPlateFormatComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateFormat selectedPlateFormat = plateFormatBindingList.get(plateSetupPanel.getPlateFormatComboBox().getSelectedIndex());
                platePanel.initPanel(selectedPlateFormat);
                plateSetupPanel.getBottomPanel().add(platePanel, gridBagConstraints);
                plateSetupPanel.repaint();
            }
        });
    }

    private void initPanel() {
        
        //add exp info panel and plate setup panel to main panel
        experimentSetupPanel.getExperimentInfoParentPanel().add(experimentInfoPanel, gridBagConstraints);
        experimentSetupPanel.getPlateSetupParentPanel().add(plateSetupPanel, gridBagConstraints);
    }
}
