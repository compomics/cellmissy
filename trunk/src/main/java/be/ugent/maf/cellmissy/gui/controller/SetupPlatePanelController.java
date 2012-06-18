/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.PlatePanelGui;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.MouseInputAdapter;
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
public class SetupPlatePanelController {

    //model
    private ObservableList<PlateFormat> plateFormatBindingList;
    private Rectangle rectangle;
    private BindingGroup bindingGroup;
    //view
    private PlatePanelGui platePanelGui;
    private SetupPlatePanel setupPlatePanel;
    //parent controller
    private SetupExperimentPanelController setupExperimentPanelController;
    //services
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    /**
     * constructor
     * @param setupExperimentPanelController 
     */
    public SetupPlatePanelController(SetupExperimentPanelController setupExperimentPanelController) {
        this.setupExperimentPanelController = setupExperimentPanelController;

        //init setup plate panel gui
        platePanelGui = new PlatePanelGui();
        //init services
        plateService = (PlateService) setupExperimentPanelController.getCellMissyController().getBeanByName("plateService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        initSetupPlatePanel();
    }

    /**
     * setters and getters
     * 
     */
    public SetupPlatePanel getSetupPlatePanel() {
        return setupPlatePanel;
    }

    public PlatePanelGui getSetupPlatePanelGui() {
        return platePanelGui;
    }

    /**
     * public methods
     *  
     */
    /**
     * add to the map a new entry: new condition-empty list of rectangles
     * @param newCondition added to the list
     */
    public void addNewRectangleEntry(PlateCondition newCondition) {
        setupPlatePanel.getRectangles().put(newCondition, new ArrayList<Rectangle>());
    }

    /**
     * remove from the map the list of rectangles of a condition that the user wants to delete
     * @param conditionToRemove 
     */
    public void removeRectangleEntry(PlateCondition conditionToRemove) {
        setupPlatePanel.getRectangles().remove(conditionToRemove);
    }

    /**
     * add mouse listener
     */
    public void addMouseListener() {
        SetupPlateListener setupPlateListener = new SetupPlateListener();
        setupPlatePanel.addMouseListener(setupPlateListener);
        setupPlatePanel.addMouseMotionListener(setupPlateListener);
    }

    /**
     * private methods and classes
     */
    private void initSetupPlatePanel() {

        //init set up plate panel and add it to the bottom panel of the set up plate panel gui
        setupPlatePanel = new SetupPlatePanel();
        platePanelGui.getBottomPanel().add(setupPlatePanel, gridBagConstraints);

        //init plateFormatJcombo
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, plateFormatBindingList, platePanelGui.getPlateFormatComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        /**
         * add action listeners
         */
        //plate format combo box
        platePanelGui.getPlateFormatComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateFormat selectedPlateFormat = plateFormatBindingList.get(platePanelGui.getPlateFormatComboBox().getSelectedIndex());
                Dimension parentDimension = platePanelGui.getBottomPanel().getSize();
                setupPlatePanel.initPanel(selectedPlateFormat, parentDimension);
                //if selections were made on the plate, reset everything: clear the map and repaint the panel
                for (List<Rectangle> rectangleList : setupPlatePanel.getRectangles().values()) {
                    rectangleList.clear();
                }
                setupPlatePanel.repaint();
            }
        });

        //clear last selection: clear rectangles of the last condition (condition is not removed)
        setupExperimentPanelController.getSetupPanel().getClearLastButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //reset to null the condition of the selected wells
                setupExperimentPanelController.resetWellsCondition(setupExperimentPanelController.getCurrentCondition());
                //remove the rectangles from the map and call the repaint
                setupPlatePanel.getRectangles().get(setupExperimentPanelController.getCurrentCondition()).clear();
                setupPlatePanel.repaint();
            }
        });

        //clear all selections: clear rectangles of all conditions (conditions are not removed)
        setupExperimentPanelController.getSetupPanel().getClearAllButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //reset to null the conditions of all the wells
                setupExperimentPanelController.resetAllWellsCondition();
                //remove all the rectangles from the map and call the repaint
                for (List<Rectangle> rectangleList : setupPlatePanel.getRectangles().values()) {
                    rectangleList.clear();
                }
                setupPlatePanel.repaint();
            }
        });

        //show 96 plate format as default
        platePanelGui.getPlateFormatComboBox().setSelectedIndex(0);

        setupExperimentPanelController.getSetupPanel().getSetupPlateParentPanel().add(platePanelGui, gridBagConstraints);
    }

    private class SetupPlateListener extends MouseInputAdapter {

        private int xMin;
        private int xMax;
        private int yMin;
        private int yMax;

        @Override
        public void mousePressed(MouseEvent e) {

            setupPlatePanel.setStartPoint(e.getPoint());
            setupPlatePanel.setEndPoint(setupPlatePanel.getStartPoint());
            xMin = setupPlatePanel.getStartPoint().x;
            xMax = setupPlatePanel.getStartPoint().x;
            yMin = setupPlatePanel.getStartPoint().y;
            yMax = setupPlatePanel.getStartPoint().y;
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            setupPlatePanel.setEndPoint(e.getPoint());
            xMin = Math.min(xMin, setupPlatePanel.getEndPoint().x);
            xMax = Math.max(xMax, setupPlatePanel.getEndPoint().x);
            yMin = Math.min(yMin, setupPlatePanel.getEndPoint().y);
            yMax = Math.max(yMax, setupPlatePanel.getEndPoint().y);
            setupPlatePanel.setCurrentCondition(setupExperimentPanelController.getCurrentCondition());
            setupPlatePanel.repaint(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            int x = Math.min(setupPlatePanel.getStartPoint().x, setupPlatePanel.getEndPoint().x);
            int y = Math.min(setupPlatePanel.getStartPoint().y, setupPlatePanel.getEndPoint().y);
            int width = Math.abs(setupPlatePanel.getStartPoint().x - setupPlatePanel.getEndPoint().x);
            int height = Math.abs(setupPlatePanel.getStartPoint().y - setupPlatePanel.getEndPoint().y);
            rectangle = new Rectangle(x, y, width, height);

            if (rectangle.width != 0 || rectangle.height != 0) {
                //if the selection of wells is valid (wells do not already have a condition set), add the rectangle to the map
                if (setupExperimentPanelController.updateWellCollection(setupExperimentPanelController.getCurrentCondition(), rectangle)) {
                    setupPlatePanel.getRectangles().get(setupExperimentPanelController.getCurrentCondition()).add(rectangle);
                }
            }

            setupPlatePanel.setStartPoint(null);
            setupPlatePanel.repaint();
        }
    }
}