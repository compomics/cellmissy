/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TreatmentType;
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
    private ObservableList<PlateFormat> plateFormatBindingList;
    private ObservableList<Project> projectBindingList;
    private BindingGroup bindingGroup;
    //view
    private ExperimentSetupPanel experimentSetupPanel;
    private ExperimentInfoPanel experimentInfoPanel;
    private PlateSetupPanel plateSetupPanel;
    private PlatePanel platePanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controller
    private ConditionsPanelController conditionsSetupPanelController;
    //services
    private PlateService plateService;
    private ProjectService projectService;
    private GridBagConstraints gridBagConstraints;

    public ExperimentSetupPanelController(CellMissyController cellMissyController) {
        this.cellMissyController = cellMissyController;
        
        experimentSetupPanel = new ExperimentSetupPanel();
        experimentInfoPanel = new ExperimentInfoPanel();
        plateSetupPanel = new PlateSetupPanel();
        
        //init child controllers
        conditionsSetupPanelController = new ConditionsPanelController(this);
        
        //init services
        plateService = (PlateService) cellMissyController.getBeanByName("plateService");
        projectService = (ProjectService) cellMissyController.getBeanByName("projectService");
        
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        
        //init views
        initExperimentInfoPanel();
        initPlateSetupPanel();
        initPanel();
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
    }

    private void initPlateSetupPanel() {
        //init plate panel and add it to the bottom panel 
        platePanel = new PlatePanel();
        plateSetupPanel.getBottomPanel().add(platePanel, gridBagConstraints);

        //init plateFormatJcombo
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, plateFormatBindingList, plateSetupPanel.getPlateFormatComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        
        // add action listener
        plateSetupPanel.getPlateFormatComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateFormat selectedPlateFormat = plateFormatBindingList.get(plateSetupPanel.getPlateFormatComboBox().getSelectedIndex());
                Dimension parentDimension = plateSetupPanel.getBottomPanel().getSize();
                platePanel.initPanel(selectedPlateFormat, parentDimension);
                plateSetupPanel.getBottomPanel().repaint();
            }
        });

        // show 96 plate format as default
        // after adding the listener
        plateSetupPanel.getPlateFormatComboBox().setSelectedIndex(0);

    }

    private void initPanel() {
        //add exp info panel and plate setup panel to main panel
        experimentSetupPanel.getExperimentInfoParentPanel().add(experimentInfoPanel, gridBagConstraints);
        experimentSetupPanel.getPlateSetupParentPanel().add(plateSetupPanel, gridBagConstraints);
    }
}
