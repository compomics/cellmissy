/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.ExperimentInfoPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupPanel;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.List;
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
    private SetupPanel setupPanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controller
    private ConditionsPanelController conditionsPanelController;
    private SetupPlatePanelController setupPlatePanelController;
    //services
    private ProjectService projectService;
    private GridBagConstraints gridBagConstraints;

    /**
     * constructor
     * @param cellMissyController 
     */
    public SetupExperimentPanelController(CellMissyController cellMissyController) {
        this.cellMissyController = cellMissyController;

        setupExperimentPanel = new SetupExperimentPanel();
        experimentInfoPanel = new ExperimentInfoPanel();
        setupPanel = new SetupPanel();

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

    /**
     * setters and getters
     *  
     */
    public SetupExperimentPanel getSetupExperimentPanel() {
        return setupExperimentPanel;
    }

    public CellMissyController getCellMissyController() {
        return cellMissyController;
    }

    public ConditionsPanelController getConditionsPanelController() {
        return conditionsPanelController;
    }

    public SetupPanel getSetupPanel() {
        return setupPanel;
    }

    /**
     * public methods
     */
    /**
     * 
     * if the user adds a new condition, add a new entry to the map: new condition-empty list of rectangles
     * @param newCondition added to the list
     */
    public void onNewConditionAdded(PlateCondition newCondition) {
        setupPlatePanelController.addNewRectangleEntry(newCondition);
    }

    /**
     * if the user removes a condition from the list, wells conditions are set back to null, rectangles are removed from the map and repaint is called
     * @param conditionToRemove 
     */
    public void onConditionToRemove(PlateCondition conditionToRemove) {
        //set back to null the condition of the wells selected 
        resetWellsCondition(conditionToRemove);
        //remove the rectangles from the map
        setupPlatePanelController.removeRectangleEntry(conditionToRemove);
        //repaint
        setupPlatePanelController.getSetupPlatePanel().repaint();
    }

    /**
     * get the current condition from the child controller
     * @return the current condition
     */
    public PlateCondition getCurrentCondition() {
        return conditionsPanelController.getCurrentCondition();
    }

    /**
     * get the setup plate panel from the child controller
     * @return setup plate panel
     */
    public SetupPlatePanel getSetupPlatePanel() {
        return setupPlatePanelController.getSetupPlatePanel();
    }

    /**
     * when the mouse is released and the rectangle has been drawn, this method is called:
     * set well collection of the current condition and set the condition of the selected wells
     * @param plateCondition, the current condition
     * @param rectangle, the just drawn rectangle
     * @return true if the selection of wells is valid, else show a message
     */
    public boolean updateWellCollection(PlateCondition plateCondition, Rectangle rectangle) {
        boolean isSelectionValid = true;
        Collection<Well> wellCollection = plateCondition.getWellCollection();
        outerloop:
        for (WellGui wellGui : setupPlatePanelController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                //check if the collection already contains that well
                if (!wellCollection.contains(wellGui.getWell())) {
                    //the selection is valid if the wells do not have a condition yet
                    if (!hasCondition(wellGui)) {
                        //in this case, add the well to the collection and set the condition of the well
                        wellCollection.add(wellGui.getWell());
                        wellGui.getWell().setPlateCondition(plateCondition);
                    } else {
                        //if the wells do have a condition already, the selection is not valid
                        isSelectionValid = false;
                        //in this case, show a message through the main controller
                        cellMissyController.showMessage("Wells cannot have more than one condition\nPlease select again the wells", 1);
                        //exit from the outer loop
                        break outerloop;
                    }
                }
            }
        }
        return isSelectionValid;
    }

    /**
     * set back to null the condition of the wells selected (for a certain Condition)
     * @param plateCondition 
     */
    public void resetWellsCondition(PlateCondition plateCondition) {
        //set plate condition of wells again to null
        for (WellGui wellGui : setupPlatePanelController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            for (Rectangle rectangle : setupPlatePanelController.getSetupPlatePanel().getRectangles().get(plateCondition)) {
                if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                    wellGui.getWell().setPlateCondition(null);
                }
            }
        }
    }

    /**
     * set back to null the conditions of all wells selected (for all conditions)
     */
    public void resetAllWellsCondition() {
        //set plate condition of all wells selected again to null
        for (PlateCondition plateCondition : conditionsPanelController.getPlateConditionBindingList()) {
            resetWellsCondition(plateCondition);
        }
    }

    /**
     * initializes the experiment info panel
     */
    private void initExperimentInfoPanel() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentInfoPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        setupExperimentPanel.getTopPanel().add(experimentInfoPanel, gridBagConstraints);

        cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and fill in experiment data");

        //disable Next and Previous buttons
        setupExperimentPanel.getNextButton().setEnabled(false);
        setupExperimentPanel.getPreviousButton().setEnabled(false);

        /**
         * add action listener
         */
        setupExperimentPanel.getNextButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), setupPanel, experimentInfoPanel);
            }
        });

        experimentInfoPanel.getNumberTextField().requestFocus();
        experimentInfoPanel.getNumberTextField().addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("focus gained");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("focus lost");
            }
        });
    }

    /*
     * private methods and classes
     */
    /**
     * check if a well already has a condition
     * @param wellGui
     * @return true if a well already has a condition assigned
     */
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
