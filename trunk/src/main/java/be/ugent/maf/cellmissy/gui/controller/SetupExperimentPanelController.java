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
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
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
        setupPlatePanelController = new SetupPlatePanelController(this);
        conditionsPanelController = new ConditionsPanelController(this);

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

    public void onNewConditionAdded(PlateCondition newPlateCondition) {
        setupPlatePanelController.addNewRectangleEntry(newPlateCondition);
    }

    public void onConditionToRemove(PlateCondition conditionToRemove) {
        //set plate condition of wells again to null
        for (WellGui wellGui : setupPlatePanelController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            for (Rectangle rectangle : setupPlatePanelController.getSetupPlatePanel().getRectangles().get(conditionToRemove)) {
                if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                    wellGui.getWell().setPlateCondition(null);
                }
            }
        }
        setupPlatePanelController.removeRectangleEntry(conditionToRemove);
        setupPlatePanelController.getSetupPlatePanel().repaint();
    }

    public PlateCondition getCurrentCondition() {
        return conditionsPanelController.getCurrentCondition();
    }

    public SetupPlatePanel getSetupPlatePanel() {
        return setupPlatePanelController.getSetupPlatePanel();
    }

    public void updateWellCollection(PlateCondition plateCondition) {
        Collection<Well> wellCollection = plateCondition.getWellCollection();
        for (WellGui wellGui : setupPlatePanelController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            for (Rectangle rectangle : setupPlatePanelController.getSetupPlatePanel().getRectangles().get(plateCondition)) {

                if (rectangle != null && rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                    //check if the collection already contains that well
                    if (!wellCollection.contains(wellGui.getWell())) {
                        //check if the well already has a condition
                        if (!hasCondition(wellGui)) {
                            wellCollection.add(wellGui.getWell());
                            wellGui.getWell().setPlateCondition(plateCondition);
                        } else {
                            System.out.println("watch out!!!");
                        }
                    }
                }
            }
        }
    }

    private boolean hasCondition(WellGui wellGui) {
        boolean hasCondition = false;
        Ellipse2D ellipse = wellGui.getEllipsi().get(0);
        for (List<Rectangle> list : setupPlatePanelController.getSetupPlatePanel().getRectangles().values()) {
            for (Rectangle rectangle : list) {
                if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight()) && wellGui.getWell().getPlateCondition() != null) {
                    hasCondition = true;
                }
            }
        }
        return hasCondition;
    }
}
