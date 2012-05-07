/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.SetupPlatePanelGui;
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
    private SetupPlatePanelGui setupPlatePanelGui;
    private SetupPlatePanel setupPlatePanel;
    //parent controller
    private SetupExperimentPanelController setupExperimentPanelController;
    //services
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    public SetupPlatePanelController(SetupExperimentPanelController setupExperimentPanelController) {
        this.setupExperimentPanelController = setupExperimentPanelController;

        //a new set up plate panel needs to be added to a new set up plate panel gui
        setupPlatePanelGui = new SetupPlatePanelGui();
        //init services
        plateService = (PlateService) setupExperimentPanelController.getCellMissyController().getBeanByName("plateService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        initSetupPlatePanel();
    }

    public SetupPlatePanel getSetupPlatePanel() {
        return setupPlatePanel;
    }

    public void addNewRectangleEntry(PlateCondition newPlateCondition) {
        setupPlatePanel.getRectangles().put(newPlateCondition, new ArrayList<Rectangle>());
    }

    public void removeRectangleEntry(PlateCondition plateConditionToRemove) {
        setupPlatePanel.getRectangles().remove(plateConditionToRemove);
    }

    private void initSetupPlatePanel() {
        //init set up plate panel and add it to the bottom panel of the set up plate panel gui
        setupPlatePanel = new SetupPlatePanel();
        setupPlatePanelGui.getBottomPanel().add(setupPlatePanel, gridBagConstraints);

        //init plateFormatJcombo
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, plateFormatBindingList, setupPlatePanelGui.getPlateFormatComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        //add mouse listener
        SetupPlateListener setupPlateListener = new SetupPlateListener();
        setupPlatePanel.addMouseListener(setupPlateListener);
        setupPlatePanel.addMouseMotionListener(setupPlateListener);

        // add action listener
        setupPlatePanelGui.getPlateFormatComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateFormat selectedPlateFormat = plateFormatBindingList.get(setupPlatePanelGui.getPlateFormatComboBox().getSelectedIndex());
                Dimension parentDimension = setupPlatePanelGui.getBottomPanel().getSize();
                setupPlatePanel.initPanel(selectedPlateFormat, parentDimension);
                for (List<Rectangle> rectangleList : setupPlatePanel.getRectangles().values()) {
                    rectangleList.clear();
                }
                setupPlatePanel.repaint();
            }
        });

        // show 96 plate format as default
        // after adding the action listener
        setupPlatePanelGui.getPlateFormatComboBox().setSelectedIndex(0);

        setupExperimentPanelController.getSetupExperimentPanel().getSetupPlateParentPanel().add(setupPlatePanelGui, gridBagConstraints);
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
                setupPlatePanel.getRectangles().get(setupExperimentPanelController.getCurrentCondition()).add(rectangle);
                setupExperimentPanelController.updateWellCollection(setupExperimentPanelController.getCurrentCondition());
            }

            setupPlatePanel.setStartPoint(null);
            setupPlatePanel.repaint();
        }
    }
}