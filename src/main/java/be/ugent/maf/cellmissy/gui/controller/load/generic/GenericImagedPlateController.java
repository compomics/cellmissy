/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.RightAlignmentRenderer;
import be.ugent.maf.cellmissy.parser.GenericInputFileParser;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("genericImagedPlatePanel")
public class GenericImagedPlateController {

    // format to show data
    private static final String DATA_FORMAT = PropertiesConfigurationHolder.getInstance().getString("dataFormat");
    //model
    private ObservableList<Algorithm> algorithmsBindingList;
    private ObservableList<ImagingType> imagingTypesBindingList;
    private Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> algoMap;
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepsBindingList;
    private JTableBinding timeStepsTableBinding;
    private Format format;
    private ImagingType currentImagingType;
    private Algorithm currentAlgorithm;
    //view
    private ImagedPlatePanel imagedPlatePanel;
    // parent controller
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    //services
    @Autowired
    private PlateService plateService;
    @Autowired
    private GenericInputFileParser genericInputFileParser;
    private GridBagConstraints gridBagConstraints;
    private boolean mouseListenerEnabled;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        bindingGroup = new BindingGroup();
        imagedPlatePanel = new ImagedPlatePanel();
        //disable mouse Listener
        mouseListenerEnabled = false;
        format = new DecimalFormat(DATA_FORMAT);
        initLoadDataPlatePanel();
        initAlgoImagingPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public ImagedPlatePanel getImagedPlatePanel() {
        return imagedPlatePanel;
    }

    public ObservableList<Algorithm> getAlgorithmsBindingList() {
        return algorithmsBindingList;
    }

    public ObservableList<ImagingType> getImagingTypesBindingList() {
        return imagingTypesBindingList;
    }

    public void setCurrentAlgorithm(Algorithm currentAlgorithm) {
        this.currentAlgorithm = currentAlgorithm;
    }

    public void setCurrentImagingType(ImagingType currentImagingType) {
        this.currentImagingType = currentImagingType;
    }

    public boolean isMouseListenerEnabled() {
        return mouseListenerEnabled;
    }

    public void setMouseListenerEnabled(boolean mouseListenerEnabled) {
        this.mouseListenerEnabled = mouseListenerEnabled;
    }

    /**
     * Initialize plate view
     */
    private void initLoadDataPlatePanel() {
        //show as default a 96 plate format
        Dimension parentDimension = loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getPlateViewParentPanel().getSize();
        imagedPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getPlateViewParentPanel().add(imagedPlatePanel, gridBagConstraints);
        loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getPlateViewParentPanel().repaint();
        List<WellGui> wellGuiList = imagedPlatePanel.getWellGuiList();
        // set empty list of wellhasimagingtype to the plate
        for (WellGui wellGui : wellGuiList) {
            wellGui.getWell().setWellHasImagingTypeCollection(new ArrayList<WellHasImagingType>());
        }
        /**
         * Mouse Listener for imaged plate panel
         */
        imagedPlatePanel.addMouseListener(new MouseAdapter() {

            // if the mouse has been pressed and released on a wellGui, show a dialog to choose file to parse
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mouseListenerEnabled == true) {
                    WellGui selectedWellGui = null;
                    for (WellGui wellGui : imagedPlatePanel.getWellGuiList()) {
                        List<Ellipse2D> ellipsi = wellGui.getEllipsi();
                        if ((e.getButton() == 1) && ellipsi.get(0).contains(e.getX(), e.getY())) {
                            selectedWellGui = wellGui;
                            break;
                        }
                    }
                    // check if the selected wellGui is actually not null
                    if (selectedWellGui != null) {
                        // check if well belongs to condition (otherwise selection is not valid)
                        if (validateSelection(selectedWellGui)) {
                            JFileChooser chooseRawDataFile = new JFileChooser();
                            chooseRawDataFile.setFileFilter(new FileFilter() {
                                // to select only txt files

                                @Override
                                public boolean accept(File f) {
                                    return f.getName().toLowerCase().endsWith(".txt");
                                }

                                @Override
                                public String getDescription() {
                                    return ("(.txt)");
                                }
                            });

                            // new wellHasImagingType (new sample for selected algorithm/imaging type)
                            WellHasImagingType newWellHasImagingType = new WellHasImagingType();
                            // set imaging type, algorithm and well (column, row numbers)
                            newWellHasImagingType.setImagingType(currentImagingType);
                            newWellHasImagingType.setAlgorithm(currentAlgorithm);
                            newWellHasImagingType.setWell(selectedWellGui.getWell());

                            // get the collection of WellHasImagingType for the selected well
                            Collection<WellHasImagingType> wellHasImagingTypeCollection = selectedWellGui.getWell().getWellHasImagingTypeCollection();
                            // check if the wellHasImagingType was already processed
                            // this is comparing objects with column and row numbers, and algorithm and imaging types
                            if (!wellHasImagingTypeCollection.contains(newWellHasImagingType)) {
                                int returnVal = chooseRawDataFile.showOpenDialog(loadExperimentFromGenericInputController.getCellMissyFrame());
                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    // file to parse 
                                    File bulkCellFile = chooseRawDataFile.getSelectedFile();
                                    // if not, parse raw data for selected well
                                    // set the timeStepCollection and add the wellHasImagingType to the collection
                                    List<TimeStep> timeSteps = genericInputFileParser.parseBulkCellFile(bulkCellFile);
                                    newWellHasImagingType.setTimeStepCollection(timeSteps);
                                    wellHasImagingTypeCollection.add(newWellHasImagingType);
                                    for (TimeStep timeStep : timeSteps) {
                                        timeStep.setWellHasImagingType(newWellHasImagingType);
                                    }
                                    // if the list is not empty and does not contain the selected well, clear it before adding the new timeSteps
                                    if (!timeStepsBindingList.isEmpty() && !containsWell(selectedWellGui)) {
                                        timeStepsBindingList.clear();
                                    }
                                    timeStepsBindingList.addAll(timeSteps);
                                    // show imaged well with a different color
                                    highlightImagedWell(selectedWellGui);
                                } else {
                                    Object[] options = {"Overwrite", "See loaded data", "Cancel"};
                                    int showOptionDialog = JOptionPane.showOptionDialog(null, "Data already loaded for this well.\nWhat do you want to do?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                                    switch (showOptionDialog) {
                                        case 0:

                                            break;
                                        case 1:
                                            // empty the list
                                            timeStepsBindingList.clear();
                                            // retrieve the already loaded data
                                            selectedWellGui.getWell().getWellHasImagingTypeCollection();
                                            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeCollection) {
                                                for (TimeStep timeStep : wellHasImagingType.getTimeStepCollection()) {
                                                    timeStepsBindingList.add(timeStep);
                                                }
                                            }
                                            break;
                                        case 2:
                                            return;

                                    }

                                }

                                // check if table still has to be initialized
                                if (timeStepsTableBinding == null) {
                                    showRawDataInTable();
                                }
                            } else {
                                loadExperimentFromGenericInputController.showMessage("Open command cancelled by user", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } else {
                        //show a warning message
                        String message = "The well you selected does not belong to a condition.\nPlease select another well.";
                        loadExperimentFromGenericInputController.showMessage(message, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     * Check if the TimeStepsList contains the selected well
     * @param selectedWellGui
     * @return 
     */
    private boolean containsWell(WellGui selectedWellGui) {
        boolean containsWell = false;
        Well selectedWell = selectedWellGui.getWell();
        for (TimeStep timeStep : timeStepsBindingList) {
            if (timeStep.getWellHasImagingType().getWell().equals(selectedWell)) {
                containsWell = true;
                break;
            }
        }
        return containsWell;
    }

    /**
     * Initialize panel with datasets and imaging types information (user interaction is here required)
     */
    private void initAlgoImagingPanel() {
        // init map
        algoMap = new HashMap<>();
        //init timeStepsBindingList
        timeStepsBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
        // init algo list
        algorithmsBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        //init imaging type list
        imagingTypesBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        // set imaging type list for plate
        imagedPlatePanel.setImagingTypeList(imagingTypesBindingList);
    }

    /**
     * This method validates the selection of a well on the plate
     * @return true if the selection is valid
     */
    private boolean validateSelection(WellGui selectedWellGui) {
        boolean isSelectionValid = true;
        //check if the imaged wellGui has a condition
        if (selectedWellGui.getRectangle() == null) {
            isSelectionValid = false;
        }
        return isSelectionValid;
    }

    /**
     * Show Area values in table
     */
    private void showRawDataInTable() {

        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepsBindingList, loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getRawDataTable());
        //add column bindings
        JTableBinding.ColumnBinding columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.columnNumber}"));
        columnBinding.setColumnName("Column");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.rowNumber}"));
        columnBinding.setColumnName("Row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.algorithm.algorithmName}"));
        columnBinding.setColumnName("Dataset");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(String.class);
        columnBinding.setRenderer(new RightAlignmentRenderer());

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.imagingType.name}"));
        columnBinding.setColumnName("Imaging type");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(String.class);
        columnBinding.setRenderer(new RightAlignmentRenderer());

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${timeStepSequence}"));
        columnBinding.setColumnName("Time sequence");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${area}"));
        columnBinding.setColumnName("Area");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(format));

        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();
    }

    /**
     * 
     * @param selectedWellGui 
     */
    private void highlightImagedWell(WellGui selectedWellGui) {
        List<Ellipse2D> ellipsi = selectedWellGui.getEllipsi();
        // get the bigger ellipsi and calculate factors for the new ones (concentric wells)
        Ellipse2D ellipse2D = ellipsi.get(0);
        double size = ellipse2D.getHeight();
        double newSize = (size / imagedPlatePanel.getUniqueImagingTypes(selectedWellGui.getWell().getWellHasImagingTypeCollection()).size());
        double newTopLeftX = ellipse2D.getCenterX() - (newSize / 2);
        double newTopLeftY = ellipse2D.getCenterY() - (newSize / 2);

        if (newSize != size) {
            Ellipse2D newEllipse2D = new Ellipse2D.Double(newTopLeftX, newTopLeftY, newSize, newSize);
            // add the new Ellipse2D to the ellipsi List
            ellipsi.add(newEllipse2D);
        }

        // this calls the paintComponent method
        imagedPlatePanel.repaint();
    }
}
