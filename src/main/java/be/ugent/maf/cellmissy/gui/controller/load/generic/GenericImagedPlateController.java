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
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.DataTreeCellRenderer;
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
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
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

    private static final Logger LOG = Logger.getLogger(GenericImagedPlateController.class);
    // format to show data
    private static final String DATA_FORMAT = PropertiesConfigurationHolder.getInstance().getString("dataFormat");
    //model
    private ObservableList<Algorithm> algorithmsBindingList;
    private ObservableList<ImagingType> imagingTypesBindingList;
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
        // init views
        initLoadDataPlatePanel();
        initAlgoImagingPanel();
    }

    /**
     * Getters and setters
     *
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
     * This is checking that each well on the plate that has a condition, contains a collection of WellhasImagingType not empty, i.e. with some data loaded
     *
     * @return
     */
    public boolean validateDataLoading() {
        boolean isDataLoadingValid = true;
        for (WellGui wellGui : imagedPlatePanel.getWellGuiList()) {
            // wellGui with a condition on the plate
            if (wellGui.getRectangle() != null) {
                if (wellGui.getWell().getWellHasImagingTypeCollection().isEmpty()) {
                    isDataLoadingValid = false;
                    break;
                }
            }
        }
        return isDataLoadingValid;
    }

    /**
     * Reset data loading and plate view (e.g. if user was importing data from a wrong folder and so on..) The user is warned first, this should be used carefully!!
     */
    public void reset() {
        // empty timesteps list
        timeStepsBindingList.clear();
        // reset view on the plate
        // empty wellhasimagingtype collection of each well
        List<WellGui> wellGuiList = imagedPlatePanel.getWellGuiList();
        for (WellGui wellGui : wellGuiList) {
            // clear the wellhasimagingtype collection
            wellGui.getWell().getWellHasImagingTypeCollection().clear();
            List<Ellipse2D> ellipsi = wellGui.getEllipsi();
            Iterator<Ellipse2D> iterator = ellipsi.iterator();
            while (iterator.hasNext()) {
                // only the default, bigger ellipse needs to stay in the repaint
                if (!iterator.next().equals(ellipsi.get(0))) {
                    iterator.remove();
                }
            }
        }
        // repaint the plate view
        imagedPlatePanel.repaint();
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
                            // new wellHasImagingType (for selected well and current imaging type/algorithm)
                            WellHasImagingType newWellHasImagingType = new WellHasImagingType(selectedWellGui.getWell(), currentImagingType, currentAlgorithm);
                            // get the collection of WellHasImagingType for the selected well
                            Collection<WellHasImagingType> wellHasImagingTypeCollection = selectedWellGui.getWell().getWellHasImagingTypeCollection();
                            // 
                            reloadData(selectedWellGui);
                            // check if the wellHasImagingType was already processed
                            // this is comparing objects with column, row numbers, and algorithm,imaging types
                            if (!wellHasImagingTypeCollection.contains(newWellHasImagingType)) {
                                // if it was not already processed, choose a file to parse
                                File bulkCellFile = chooseData();
                                if (bulkCellFile != null) {
                                    // load data
                                    loadData(bulkCellFile, newWellHasImagingType, selectedWellGui);
                                    // update relation with algorithm and imaging type
                                    currentAlgorithm.getWellHasImagingTypeCollection().add(newWellHasImagingType);
                                    currentImagingType.getWellHasImagingTypeCollection().add(newWellHasImagingType);
                                    // highlight imaged well
                                    highlightImagedWell(selectedWellGui);
                                    // check if table still has to be initialized
                                    if (timeStepsTableBinding == null) {
                                        showRawDataInTable();
                                    }
                                }
                            } else {
                                // warn the user that data was already loaded for the selected combination of well/dataset/imaging type
                                Object[] options = {"Overwrite", "Clear data", "Add location on same well", "Cancel"};
                                int showOptionDialog = JOptionPane.showOptionDialog(null, "Data already loaded for this well / dataset / imaging type.\nWhat do you want to do now?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[3]);
                                switch (showOptionDialog) {
                                    case 0: // overwrite loaded data:      
                                        // choose another file to parse (another dataset to load)
                                        File newFile = chooseData();
                                        if (newFile != null) {
                                            // remove from the list the old data
                                            removeOldDataFromList(newWellHasImagingType);
                                            // remove the data from the well
                                            selectedWellGui.getWell().getWellHasImagingTypeCollection().remove(newWellHasImagingType);
                                            // update relation with algorithm and imaging type
                                            currentAlgorithm.getWellHasImagingTypeCollection().remove(newWellHasImagingType);
                                            currentImagingType.getWellHasImagingTypeCollection().remove(newWellHasImagingType);
                                            // load new data
                                            loadData(newFile, newWellHasImagingType, selectedWellGui);
                                        }
                                        break;
                                    case 1: // clear data for current algorithm/imaging type
                                        if (!imagingTypeIsNotLast(selectedWellGui)) {
                                            List<WellHasImagingType> oldSamples = removeOldDataFromList(newWellHasImagingType);
                                            // remove the data from the well
                                            selectedWellGui.getWell().getWellHasImagingTypeCollection().removeAll(oldSamples);
                                            // update relation with algorithm and imaging type
                                            currentAlgorithm.getWellHasImagingTypeCollection().remove(newWellHasImagingType);
                                            currentImagingType.getWellHasImagingTypeCollection().remove(newWellHasImagingType);
                                            onCancel(selectedWellGui);
                                        } else {
                                            List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(selectedWellGui.getWell().getWellHasImagingTypeCollection());
                                            ImagingType lastImagingType = uniqueImagingTypes.get(uniqueImagingTypes.size() - 1);
                                            loadExperimentFromGenericInputController.showMessage("Please remove first last added imaging type " + "(" + lastImagingType.getName() + ")", "", JOptionPane.WARNING_MESSAGE);
                                            List<Algorithm> uniqueAlgorithms = getUniqueAlgorithms(wellHasImagingTypeCollection);
                                            Algorithm lastAlgorithm = uniqueAlgorithms.get(uniqueAlgorithms.size() - 1);
                                            loadExperimentFromGenericInputController.selectImagingTypeOnTree(lastImagingType, lastAlgorithm);
                                        }
                                        break;
                                    case 2: // select another file to parse, adding location on the same well
                                        // choose another file to parse (another dataset to load)
                                        newFile = chooseData();
                                        if (newFile != null) {
                                            // load new data
                                            loadData(newFile, newWellHasImagingType, selectedWellGui);
                                        }
                                        break;
                                    // cancel: do nothing
                                }
                            }
                        } else {
                            //show a warning message
                            String message = "The well you selected does not belong to a condition.\nPlease select another well.";
                            loadExperimentFromGenericInputController.showMessage(message, "Well's selection error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    /**
     * Get unique algorithms from a collection of samples
     *
     * @param wellHasImagingTypes
     * @return
     */
    private List<Algorithm> getUniqueAlgorithms(Collection<WellHasImagingType> wellHasImagingTypes) {
        List<Algorithm> algorithms = new ArrayList<>();

        for (WellHasImagingType wellHasImagingType : wellHasImagingTypes) {
            if (!algorithms.contains(wellHasImagingType.getAlgorithm())) {
                algorithms.add(wellHasImagingType.getAlgorithm());
            }
        }
        return algorithms;
    }

    /**
     * This is parsing a certain file, for a certain selected well and a wellHasImagingType (i.e. dataset and imaging type are chosen)
     *
     * @param bulkCellFile
     * @param newWellHasImagingType
     * @param selectedWellGui
     */
    private void loadData(File bulkCellFile, WellHasImagingType newWellHasImagingType, WellGui selectedWellGui) {
        Collection<WellHasImagingType> wellHasImagingTypeCollection = selectedWellGui.getWell().getWellHasImagingTypeCollection();
        // parse raw data for selected well
        try {
            List<TimeStep> timeSteps = genericInputFileParser.parseBulkCellFile(bulkCellFile);
            // set the timeStepCollection and add the wellHasImagingType to the collection
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
        } catch (FileParserException ex) {
            LOG.error(ex.getMessage());
            loadExperimentFromGenericInputController.showMessage(ex.getMessage(), "Generic input file error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opening a dialog to select file to parse and load data parsing the selected file
     *
     * @param rawDataChooser
     * @param newWellHasImagingType
     * @param selectedWellGui
     */
    private File chooseData() {
        File bulkCellFile = null;
        JFileChooser fileChooser = new JFileChooser();
        // to select only txt files
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                int index = f.getName().lastIndexOf(".");
                String extension = f.getName().substring(index + 1);
                if (extension.equals("txt")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return ("text files only");
            }
        });
        // Removing "All Files" option from FileType 
        fileChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = fileChooser.showOpenDialog(loadExperimentFromGenericInputController.getCellMissyFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // load selected data
            // file to parse 
            bulkCellFile = fileChooser.getSelectedFile();
        } else {
            loadExperimentFromGenericInputController.showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
        }
        return bulkCellFile;
    }

    /**
     * Reload data already parsed for a selected well
     *
     * @param selectedWellGui
     */
    private void reloadData(WellGui selectedWellGui) {
        // empty the list
        timeStepsBindingList.clear();
        Collection<WellHasImagingType> wellHasImagingTypeCollection = selectedWellGui.getWell().getWellHasImagingTypeCollection();
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypeCollection) {
            for (TimeStep timeStep : wellHasImagingType.getTimeStepCollection()) {
                timeStepsBindingList.add(timeStep);
            }
        }
    }

    /**
     * remove timeSteps from List: this is called when the user wants to overwrite data or to clear data.
     *
     * @param wellHasImagingTypeToOverwrite
     */
    private List<WellHasImagingType> removeOldDataFromList(WellHasImagingType wellHasImagingTypeToOverwrite) {
        List<WellHasImagingType> list = new ArrayList<>();
        Iterator<TimeStep> iterator = timeStepsBindingList.iterator();
        while (iterator.hasNext()) {
            WellHasImagingType wellHasImagingType = iterator.next().getWellHasImagingType();
            if (wellHasImagingType.equals(wellHasImagingTypeToOverwrite)) {
                list.add(wellHasImagingType);
                iterator.remove();
            }
        }
        return list;
    }

    /**
     * Check if the TimeStepsList contains the selected well
     *
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
     *
     * @param selectedWellGui
     */
    private void highlightImagedWell(WellGui selectedWellGui) {
        List<Ellipse2D> ellipsi = selectedWellGui.getEllipsi();
        Collection<WellHasImagingType> wellHasImagingTypeCollection = selectedWellGui.getWell().getWellHasImagingTypeCollection();
        List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(wellHasImagingTypeCollection);
        // if size is one, only one imaging type was processed: do not add eny ellipsi
        if (uniqueImagingTypes.size() != 1) {
            if (ellipsi.size() < uniqueImagingTypes.size()) {
                int lastIndex = uniqueImagingTypes.size() - 2;
                Ellipse2D lastEllipse = ellipsi.get(lastIndex);
                // calculate factors for new ellipse
                double size = lastEllipse.getHeight();
                double newSize = (size / uniqueImagingTypes.size());
                double newTopLeftX = lastEllipse.getCenterX() - (newSize / 2);
                double newTopLeftY = lastEllipse.getCenterY() - (newSize / 2);
                if (newSize != size) {
                    Ellipse2D ellipseToAdd = new Ellipse2D.Double(newTopLeftX, newTopLeftY, newSize, newSize);
                    // add the new Ellipse2D to the ellipsi List
                    ellipsi.add(ellipseToAdd);
                }
            }
        }
        imagedPlatePanel.repaint();
    }

    /**
     * On cancel: delete data and, if necessary, repaint the plate view
     *
     * @param wellGui
     */
    private void onCancel(WellGui wellGui) {
        Collection<WellHasImagingType> wellHasImagingTypeCollection = wellGui.getWell().getWellHasImagingTypeCollection();
        Iterator<WellHasImagingType> iterator = wellHasImagingTypeCollection.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getImagingType().equals(imagedPlatePanel.getCurrentImagingType())) {
                iterator.remove();
            }
        }
        // get unique imaging types from the collection
        List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(wellHasImagingTypeCollection);
        // index of last added ellipse
        int lastIndex = uniqueImagingTypes.size();
        // if the ellipse is the first one, do not remove it, neither if it still contains the current imaging type
        if (lastIndex != 0 && !containsImagingType(wellHasImagingTypeCollection, currentImagingType)) {
            // ellipse to remove
            Ellipse2D ellipseToRemove = wellGui.getEllipsi().get(lastIndex);
            wellGui.getEllipsi().remove(ellipseToRemove);
        }
        imagedPlatePanel.repaint();
    }

    /**
     * Check if the imaging type the user is attempting to delete is the first one
     *
     * @return
     */
    private boolean imagingTypeIsNotLast(WellGui wellGui) {
        Collection<WellHasImagingType> wellHasImagingTypeCollection = wellGui.getWell().getWellHasImagingTypeCollection();
        List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(wellHasImagingTypeCollection);
        boolean isNotLast = false;
        int numberOfImagingTypes = uniqueImagingTypes.size();
        int currentImagingIndex = uniqueImagingTypes.indexOf(currentImagingType);
        if (numberOfImagingTypes != 1 && currentImagingIndex < uniqueImagingTypes.size() - 1) {
            isNotLast = true;
        }
        return isNotLast;
    }

    /**
     * Check if a collection of wellhasImagingTypes contains a certain imaging type
     *
     * @param wellHasImagingTypes
     * @param imagingType
     * @return
     */
    private boolean containsImagingType(Collection<WellHasImagingType> wellHasImagingTypes, ImagingType imagingType) {
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypes) {
            if (wellHasImagingType.getImagingType().equals(imagingType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialize panel with datasets and imaging types information (user interaction is here required)
     */
    private void initAlgoImagingPanel() {
        //init timeStepsBindingList
        timeStepsBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
        // init algo list
        algorithmsBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        //init imaging type list
        imagingTypesBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        // set imaging type list for plate
        imagedPlatePanel.setImagingTypeList(imagingTypesBindingList);
        // set cell renderer for the JTree
        loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getDataTree().setCellRenderer(new DataTreeCellRenderer(imagingTypesBindingList));
    }

    /**
     * This method validates the selection of a well on the plate
     *
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
}
