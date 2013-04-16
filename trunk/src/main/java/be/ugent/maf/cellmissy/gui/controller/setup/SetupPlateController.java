/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.PlateService;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Setup Plate Panel Controller: set up plate view during experiment design Parent Controller: Setup Experiment Panel Controller
 *
 * @author Paola
 */
@Controller("setupPlateController")
public class SetupPlateController {

    //model
    private ObservableList<PlateFormat> plateFormatBindingList;
    private Rectangle rectangle;
    private BindingGroup bindingGroup;
    private boolean configurationIsRandom;
    //view
    private SetupPlatePanel setupPlatePanel;
    //parent controller
    @Autowired
    private SetupExperimentController setupExperimentController;
    //services
    @Autowired
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        configurationIsRandom = false;
        //create new panel
        //init views
        initSetupPlatePanel();
    }

    /**
     * setters and getters
     *
     * @return
     */
    public SetupPlatePanel getSetupPlatePanel() {
        return setupPlatePanel;
    }

    /**
     * public methods
     *
     */
    /**
     * add to the map a new entry: new condition-empty list of rectangles
     *
     * @param conditionToAdd added to the list
     */
    public void addNewRectangleEntry(PlateCondition conditionToAdd) {
        setupPlatePanel.getRectangles().put(conditionToAdd, new ArrayList<Rectangle>());
    }

    /**
     * remove from the map the list of rectangles of a condition that the user wants to delete
     *
     * @param conditionToRemove from the list
     */
    public void removeRectangleEntry(PlateCondition conditionToRemove) {
        setupPlatePanel.getRectangles().remove(conditionToRemove);
    }

    /**
     * remove from the map the list of rectangles of all conditions
     */
    public void removeAllRectangleEntries() {
        for (PlateCondition plateCondition : setupExperimentController.getPlateConditionBindingList()) {
            removeRectangleEntry(plateCondition);
        }
    }

    /**
     * Check that every well selected has a plate condition assigned!!
     *
     * @return
     */
    public boolean validateWells() {
        boolean isValid = true;
        for (WellGui wellGui : setupPlatePanel.getWellGuiList()) {
            if (wellGui.getRectangle() != null) {
                Well well = wellGui.getWell();
                if (well.getPlateCondition() == null) {
                    isValid = false;
                    break;
                }
            }

        }
        return isValid;
    }

    /**
     * add mouse listener
     */
    public void addMouseListener() {
        SetupPlateListener setupPlateListener = new SetupPlateListener();
        // a mouse listener listens to a mouse pressing/releasing
        setupPlatePanel.addMouseListener(setupPlateListener);
        // a mouse motion listener listens also to a mouse moving/dragging
        setupPlatePanel.addMouseMotionListener(setupPlateListener);
    }

    /**
     * On clear the entire plate selections
     */
    public void onClearPlate() {
        //reset to null the conditions of all the wells
        setupExperimentController.resetAllWellsCondition();
        //remove all the rectangles from the map and call the repaint
        for (List<Rectangle> rectangleList : setupPlatePanel.getRectangles().values()) {
            rectangleList.clear();
        }
        setupPlatePanel.repaint();
        // set to null the rectangles of the wellguis
        for (WellGui wellGui : setupPlatePanel.getWellGuiList()) {
            wellGui.setRectangle(null);
        }
        for (PlateCondition plateCondition : setupExperimentController.getPlateConditionBindingList()) {
            plateCondition.getWellCollection().clear();
        }
    }

    /**
     * Clear ONE selection only
     *
     * @param plateCondition
     */
    public void onClearSelection(PlateCondition plateCondition) {
        //reset to null the condition of the selected wells
        setupExperimentController.resetWellsCondition(plateCondition);
        //remove the rectangles from the map and call the repaint
        setupPlatePanel.getRectangles().get(plateCondition).clear();
        setupPlatePanel.repaint();
        Collection<Well> wellCollection = plateCondition.getWellCollection();
        // set to null the rectangles of the wellguis of this condition
        for (WellGui wellGui : setupPlatePanel.getWellGuiList()) {
            for (Well well : wellCollection) {
                if (wellGui.getWell().equals(well)) {
                    wellGui.setRectangle(null);
                }
            }
        }
        wellCollection.clear();
    }

    /**
     * private methods and classes
     */
    /**
     * initialize view: set-up plate panel
     */
    private void initSetupPlatePanel() {
        //init set up plate panel and add it to the bottom panel of the plate panel gui
        setupPlatePanel = new SetupPlatePanel();
        setupExperimentController.getSetupPanel().getBottomPanel().add(setupPlatePanel, gridBagConstraints);
        //init plateFormatJcombo
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, plateFormatBindingList, setupExperimentController.getSetupPanel().getPlateFormatComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();


        /**
         * add action listeners
         */
        //plate format combo box
        setupExperimentController.getSetupPanel().getPlateFormatComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the selected plate format
                PlateFormat selectedPlateFormat = (PlateFormat) (setupExperimentController.getSetupPanel().getPlateFormatComboBox().getSelectedItem());
                // if selection has not started yet, simply show the plate format
                if (!selectionStarted()) {
                    Dimension parentDimension = setupExperimentController.getSetupPanel().getBottomPanel().getSize();
                    setupPlatePanel.initPanel(selectedPlateFormat, parentDimension);
                    setupPlatePanel.repaint();
                    // set plate format of experiment
                    if (setupExperimentController.getExperiment() != null) {
                        setupExperimentController.getExperiment().setPlateFormat(selectedPlateFormat);
                    }
                    // otherwise, check if a DIFFERENT plate format is selected
                } else if (!selectedPlateFormat.equals(setupExperimentController.getExperiment().getPlateFormat())) {
                    //if selections were made on the plate, reset everything: clear the map and repaint the panel
                    // ask first the user
                    Object[] options = {"Yes", "No", "Cancel"};
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "Current set-up will not be saved. Continue with another format?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                    // if YES, reset the plate and move to another format
                    if (showOptionDialog == 0) {
                        onClearPlate();
                        Dimension parentDimension = setupExperimentController.getSetupPanel().getBottomPanel().getSize();
                        setupPlatePanel.initPanel(selectedPlateFormat, parentDimension);
                        setupPlatePanel.repaint();
                        setupExperimentController.getExperiment().setPlateFormat(selectedPlateFormat);
                        configurationIsRandom = false;
                    } else {
                        //otherwise, stay on the same format
                        PlateFormat plateFormat = setupExperimentController.getExperiment().getPlateFormat();
                        setupExperimentController.getSetupPanel().getPlateFormatComboBox().setSelectedItem(plateFormat);
                    }
                }

            }
        });

        //clear last selection: clear rectangles of the last condition (condition is not removed)
        setupExperimentController.getSetupPanel().getClearLastButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectionStarted()) {
                    // on clear last selection only
                    onClearSelection(setupExperimentController.getCurrentCondition());
                }
            }
        });

        //clear all selections: clear rectangles of all conditions (conditions are not removed)
        setupExperimentController.getSetupPanel().getClearAllButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectionStarted()) {
                    // on clear the entire plate
                    onClearPlate();
                }
            }
        });

        // randomize wells coordinates on the plate
        setupExperimentController.getSetupPanel().getRandomButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // before randomize the wells coordinates on the plate, ask the user confirmation
                // same confirmation is asked when the user click on a different plate format
                // check if there are already rectangles, i.e. selections were made on the plate
                if (selectionStarted()) {
                    if (!configurationIsRandom) {
                        Object[] options = {"Yes", "No", "Cancel"};
                        int showOptionDialog = JOptionPane.showOptionDialog(null, "Current set-up will not be saved. Continue?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                        // if Yes, continue with randomization
                        if (showOptionDialog == 0) {
                            onRandomWells();
                        }
                    } else {
                        onRandomWells();
                    }
                }
            }
        });

        //show 96 plate format as default
        setupExperimentController.getSetupPanel().getPlateFormatComboBox().setSelectedIndex(0);
    }

    /**
     * Check if some rectangles already where selected on the plate view
     *
     * @return
     */
    private boolean selectionStarted() {
        boolean started = false;
        Collection<List<Rectangle>> values = setupPlatePanel.getRectangles().values();
        for (List<Rectangle> list : values) {
            if (!list.isEmpty()) {
                started = true;
                break;
            }
        }
        return started;
    }

    /**
     * Randomize wells coordinates on the plate panel
     */
    private void onRandomWells() {
        // get list of conditions
        ObservableList<PlateCondition> plateConditionBindingList = setupExperimentController.getPlateConditionBindingList();
        List<WellGui> wellGuiList = setupPlatePanel.getWellGuiList();
        int numberOfWells = wellGuiList.size();
        // before cleaning the plate panel, put all sizes of well collections in a list
        List<Integer> list = new ArrayList<>();
        for (PlateCondition plateCondition : plateConditionBindingList) {
            list.add(plateCondition.getWellCollection().size());
        }
        // reset the entire plate
        onClearPlate();
        for (int i = 0; i < plateConditionBindingList.size(); i++) {
            // make a list to keep the new random wells
            List<Well> randomWells = new ArrayList<>();
            Integer numberOfWellsForCurrentCondition = list.get(i);
            for (int j = 0; j < numberOfWellsForCurrentCondition; j++) {
                // create a random number and take its integer part
                Double random = Math.random() * numberOfWells;
                int intValue = random.intValue();
                // get the random well Gui and thus the random well
                WellGui randomWellGui = wellGuiList.get(intValue);
                Well randomWell = randomWellGui.getWell();
                // check that the random well was not already assigned to another condition
                // if not, set condition of the randow well with the current condition, add the randow well to the list and put the relative rectangle inside the rectangles map
                if (randomWell.getPlateCondition() == null) {
                    randomWell.setPlateCondition(plateConditionBindingList.get(i));
                    randomWells.add(randomWell);
                    Ellipse2D ellipse = randomWellGui.getEllipsi().get(0);
                    Rectangle randomRectangle = ellipse.getBounds();
                    setupPlatePanel.getRectangles().get(plateConditionBindingList.get(i)).add(randomRectangle);
                } else {
                    // if the randow well already had a condition, decrement the index and start again
                    j--;
                }
            }
            // take the random wells list and assign to the condition
            plateConditionBindingList.get(i).setWellCollection(randomWells);
        }
        //repaint the setup plate panel
        setupPlatePanel.repaint();
        configurationIsRandom = true;
    }

    /**
     * MouseInputAdapter for the Setup Plate
     */
    private class SetupPlateListener extends MouseInputAdapter {

        private int xMin;
        private int xMax;
        private int yMin;
        private int yMax;

        // mouse has been pressed
        @Override
        public void mousePressed(MouseEvent e) {
            setupPlatePanel.setStartPoint(e.getPoint());
            setupPlatePanel.setEndPoint(setupPlatePanel.getStartPoint());
            xMin = setupPlatePanel.getStartPoint().x;
            xMax = setupPlatePanel.getStartPoint().x;
            yMin = setupPlatePanel.getStartPoint().y;
            yMax = setupPlatePanel.getStartPoint().y;
        }

        // mouse is being dragged
        @Override
        public void mouseDragged(MouseEvent e) {

            setupPlatePanel.setEndPoint(e.getPoint());
            xMin = Math.min(xMin, setupPlatePanel.getEndPoint().x);
            xMax = Math.max(xMax, setupPlatePanel.getEndPoint().x);
            yMin = Math.min(yMin, setupPlatePanel.getEndPoint().y);
            yMax = Math.max(yMax, setupPlatePanel.getEndPoint().y);
            setupPlatePanel.setCurrentCondition(setupExperimentController.getCurrentCondition());
            setupPlatePanel.repaint(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1);
        }

        // mouse has been released
        @Override
        public void mouseReleased(MouseEvent e) {

            if (setupPlatePanel.getStartPoint() != null && setupPlatePanel.getEndPoint() != null) {
                int x = Math.min(setupPlatePanel.getStartPoint().x, setupPlatePanel.getEndPoint().x);
                int y = Math.min(setupPlatePanel.getStartPoint().y, setupPlatePanel.getEndPoint().y);
                int width = Math.abs(setupPlatePanel.getStartPoint().x - setupPlatePanel.getEndPoint().x);
                int height = Math.abs(setupPlatePanel.getStartPoint().y - setupPlatePanel.getEndPoint().y);
                rectangle = new Rectangle(x, y, width, height);
                if (rectangle.width != 0 || rectangle.height != 0) {
                    //if the selection of wells is valid (wells do not already have a condition set), add the rectangle to the map
                    if (setupExperimentController.updateWellCollection(setupExperimentController.getCurrentCondition(), rectangle)) {
                        setupPlatePanel.getRectangles().get(setupExperimentController.getCurrentCondition()).add(rectangle);
                        for (Well well : setupExperimentController.getCurrentCondition().getWellCollection()) {
                            if (well.getPlateCondition() == null) {
                                well.setPlateCondition(setupExperimentController.getCurrentCondition());
                            }
                        }
                    }
                }
                setupPlatePanel.setStartPoint(null);
                setupPlatePanel.repaint();
            }
        }
    }
}