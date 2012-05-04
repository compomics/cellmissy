/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.ExperimentInfoPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupExperimentPanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class SetupExperimentPanelController {

    //model
    private ObservableList<Project> projectBindingList;
    private BindingGroup bindingGroup;
    //view
    private SetupExperimentPanel setupExperimentPanel;
    private ExperimentInfoPanel experimentInfoPanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controller
    private ConditionsPanelController conditionsPanelController;
    private SetupPlatePanelController setupPlatePanelController;
    //services
    private ProjectService projectService;
    private GridBagConstraints gridBagConstraints;

    public SetupExperimentPanelController(CellMissyController cellMissyController) {
        this.cellMissyController = cellMissyController;

        setupExperimentPanel = new SetupExperimentPanel();
        experimentInfoPanel = new ExperimentInfoPanel();

        //init child controllers
        conditionsPanelController = new ConditionsPanelController(this);
        setupPlatePanelController = new SetupPlatePanelController(this);

        //init services
        projectService = (ProjectService) cellMissyController.getBeanByName("projectService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        initExperimentInfoPanel();
    }

    public SetupExperimentPanel getSetupExperimentPanel() {
        return setupExperimentPanel;
    }

    public CellMissyController getCellMissyController() {
        return cellMissyController;
    }

    public ConditionsPanelController getConditionsPanelController() {
        return conditionsPanelController;
    }

    private void initExperimentInfoPanel() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentInfoPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        setupExperimentPanel.getExperimentInfoParentPanel().add(experimentInfoPanel, gridBagConstraints);
    }
    
    public void onNewConditionAdded(Integer conditionIndex){
        setupPlatePanelController.addNewRectangleEntry(conditionIndex);
    }
    
    public Integer getCurrentConditionIndex(){
        return conditionsPanelController.getCurrentConditionIndex();
    }

//    public void updateWellsCollection(PlateCondition plateCondition) {
//        Collection<Well> wellCollection = plateCondition.getWellCollection();
//        List<Well> selectedWellsList = setupPlatePanelController.getSelectedWellsList();
//        for (Well well : selectedWellsList) {
//            wellCollection.add(well);
//        }
//        plateCondition.setWellCollection(wellCollection);
//        setupPlatePanelController.getSelectedWellsList().clear();
//    }
//
//    public void updateConditionOfSelectedWells(PlateCondition plateCondition) {
//        for (WellGui wellGui : setupPlatePanelController.getSetupPlatePanel().getWellGuiList()) {
//            //get only the bigger default ellipse2D
//            Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
//            for (Rectangle rectangle : setupPlatePanelController.getSetupPlatePanel().getRectanglesToDrawList()) {
//                if (rectangle.contains(defaultWell.getX(), defaultWell.getY(), defaultWell.getWidth(), defaultWell.getHeight())) {
//                    wellGui.getWell().setPlateCondition(plateCondition);
//                    setupPlatePanelController.getSelectedWellsList().add(wellGui.getWell());
//                }
//            }
//        }
        //setupPlatePanelController.getSetupPlatePanel().repaint(setupPlatePanelController.getRectangle());
//    }
}
