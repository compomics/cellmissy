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
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.DataTreeCellRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.RightAlignmentRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.parser.GenericInputFileParser;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
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
    private LoadFromGenericInputPlatePanel loadFromGenericInputPlatePanel;
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
        loadFromGenericInputPlatePanel = new LoadFromGenericInputPlatePanel();
        //disable mouse Listener
        mouseListenerEnabled = false;
        format = new DecimalFormat(PropertiesConfigurationHolder.getInstance().getString("dataFormat"));
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

    public LoadFromGenericInputPlatePanel getLoadFromGenericInputPlatePanel() {
        return loadFromGenericInputPlatePanel;
    }

    /**
     * This is checking that each well on the plate that has a condition,
     * contains a collection of WellhasImagingType not empty, i.e. with some
     * data loaded
     *
     * @return
     */
    public boolean validateDataLoading() {
        boolean isDataLoadingValid = true;
        for (WellGui wellGui : imagedPlatePanel.getWellGuiList()) {
            // wellGui with a condition on the plate
            if (wellGui.getRectangle() != null) {
                if (wellGui.getWell().getWellHasImagingTypeList().isEmpty()) {
                    isDataLoadingValid = false;
                    break;
                }
            }
        }
        return isDataLoadingValid;
    }

    /**
     * Reset data loading and plate view (e.g. if user was importing data from a
     * wrong folder and so on..) The user is warned first, this should be used
     * carefully!!
     */
    public void resetData() {
        // empty timesteps list
        timeStepsBindingList.clear();
        // resetData view on the plate
        // empty wellhasimagingtype collection of each well
        List<WellGui> wellGuiList = imagedPlatePanel.getWellGuiList();
        for (WellGui wellGui : wellGuiList) {
            // clear the wellhasimagingtype collection
            wellGui.getWell().getWellHasImagingTypeList().clear();
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
        JPanel plateParentPanel = loadFromGenericInputPlatePanel.getPlateParentPanel();
        //show as default a 96 plate format
        Dimension parentDimension = plateParentPanel.getSize();
        imagedPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        plateParentPanel.add(imagedPlatePanel, gridBagConstraints);
        plateParentPanel.repaint();
        List<WellGui> wellGuiList = imagedPlatePanel.getWellGuiList();
        // set empty list of wellhasimagingtype to the plate
        for (WellGui wellGui : wellGuiList) {
            wellGui.getWell().setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
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
                            // get the list of WellHasImagingType for the selected well
                            List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
                            //
                            reloadData(selectedWellGui);
                            // check if the wellHasImagingType was already processed
                            // this is comparing objects with column, row numbers, and algorithm,imaging types
                            if (!wellHasImagingTypeList.contains(newWellHasImagingType)) {
                                // if it was not already processed, choose a file to parse
                                File bulkCellFile = chooseData();
                                if (bulkCellFile != null) {
                                    // load data
                                    loadData(bulkCellFile, newWellHasImagingType, selectedWellGui);
                                    // update relation with algorithm and imaging type
                                    currentAlgorithm.getWellHasImagingTypeList().add(newWellHasImagingType);
                                    currentImagingType.getWellHasImagingTypeList().add(newWellHasImagingType);
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
                                            selectedWellGui.getWell().getWellHasImagingTypeList().remove(newWellHasImagingType);
                                            // update relation with algorithm and imaging type
                                            currentAlgorithm.getWellHasImagingTypeList().remove(newWellHasImagingType);
                                            currentImagingType.getWellHasImagingTypeList().remove(newWellHasImagingType);
                                            // load new data
                                            loadData(newFile, newWellHasImagingType, selectedWellGui);
                                        }
                                        break;
                                    case 1: // clear data for current algorithm/imaging type
                                        if (!imagingTypeIsNotLast(selectedWellGui)) {
                                            List<WellHasImagingType> oldSamples = removeOldDataFromList(newWellHasImagingType);
                                            // remove the data from the well
                                            selectedWellGui.getWell().getWellHasImagingTypeList().removeAll(oldSamples);
                                            // update relation with algorithm and imaging type
                                            currentAlgorithm.getWellHasImagingTypeList().remove(newWellHasImagingType);
                                            currentImagingType.getWellHasImagingTypeList().remove(newWellHasImagingType);
                                            onCancel(selectedWellGui);
                                        } else {
                                            List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(selectedWellGui.getWell().getWellHasImagingTypeList());
                                            ImagingType lastImagingType = uniqueImagingTypes.get(uniqueImagingTypes.size() - 1);
                                            loadExperimentFromGenericInputController.showMessage("Please remove first last added imaging type " + "(" + lastImagingType.getName() + ")", "", JOptionPane.WARNING_MESSAGE);
                                            List<Algorithm> uniqueAlgorithms = getUniqueAlgorithms(wellHasImagingTypeList);
                                            Algorithm lastAlgorithm = uniqueAlgorithms.get(uniqueAlgorithms.size() - 1);
                                            selectImagingTypeOnTree(lastImagingType, lastAlgorithm);
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
     * Get unique algorithms from a list of samples
     *
     * @param wellHasImagingTypes
     * @return
     */
    private List<Algorithm> getUniqueAlgorithms(List<WellHasImagingType> wellHasImagingTypes) {
        List<Algorithm> algorithms = new ArrayList<>();

        for (WellHasImagingType wellHasImagingType : wellHasImagingTypes) {
            if (!algorithms.contains(wellHasImagingType.getAlgorithm())) {
                algorithms.add(wellHasImagingType.getAlgorithm());
            }
        }
        return algorithms;
    }

    /**
     * Select a specific imaging type on the JTree, according to a certain
     * algorithm
     *
     * @param imagingType
     * @param algorithm
     */
    private void selectImagingTypeOnTree(ImagingType imagingType, Algorithm algorithm) {
        // jtree structure
        JTree dataTree = loadFromGenericInputPlatePanel.getDataTree();
        // model of JTree
        DefaultTreeModel model = (DefaultTreeModel) dataTree.getModel();
        // root (Data) node
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        // iterate through dataset nodes
        for (int n = 0; n < model.getChildCount(rootNode); n++) {
            DefaultMutableTreeNode datasetNode = (DefaultMutableTreeNode) model.getChild(rootNode, n);
            if (datasetNode.toString().equals(algorithm.toString())) {
                // iterate through imaging type node
                for (int m = 0; m < datasetNode.getChildCount(); m++) {
                    DefaultMutableTreeNode imagingNode = (DefaultMutableTreeNode) datasetNode.getChildAt(m);
                    if (imagingNode.toString().equals(imagingType.toString())) {
                        dataTree.setSelectionPath(new TreePath(imagingNode.getPath()));
                    }
                }
            }
        }
    }

    /**
     * This is parsing a certain file, for a certain selected well and a
     * wellHasImagingType (i.e. dataset and imaging type are chosen)
     *
     * @param bulkCellFile
     * @param newWellHasImagingType
     * @param selectedWellGui
     */
    private void loadData(File bulkCellFile, WellHasImagingType newWellHasImagingType, WellGui selectedWellGui) {
        List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
        // parse raw data for selected well
        try {
            List<TimeStep> timeSteps = genericInputFileParser.parseBulkCellFile(bulkCellFile);
            // set the timeStepList and add the wellHasImagingType to the list
            newWellHasImagingType.setTimeStepList(timeSteps);
            wellHasImagingTypeList.add(newWellHasImagingType);
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
     * Opening a dialog to select file to parse and load data parsing the
     * selected file
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
        List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
            for (TimeStep timeStep : wellHasImagingType.getTimeStepList()) {
                timeStepsBindingList.add(timeStep);
            }
        }
    }

    /**
     * remove timeSteps from List: this is called when the user wants to
     * overwrite data or to clear data.
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
        List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
        List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(wellHasImagingTypeList);
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
        List<WellHasImagingType> wellHasImagingTypeList = wellGui.getWell().getWellHasImagingTypeList();
        Iterator<WellHasImagingType> iterator = wellHasImagingTypeList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getImagingType().equals(imagedPlatePanel.getCurrentImagingType())) {
                iterator.remove();
            }
        }
        // get unique imaging types from the collection
        List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(wellHasImagingTypeList);
        // index of last added ellipse
        int lastIndex = uniqueImagingTypes.size();
        // if the ellipse is the first one, do not remove it, neither if it still contains the current imaging type
        if (lastIndex != 0 && !containsImagingType(wellHasImagingTypeList, currentImagingType)) {
            // ellipse to remove
            Ellipse2D ellipseToRemove = wellGui.getEllipsi().get(lastIndex);
            wellGui.getEllipsi().remove(ellipseToRemove);
        }
        imagedPlatePanel.repaint();
    }

    /**
     * Check if the imaging type the user is attempting to delete is the first
     * one
     *
     * @return
     */
    private boolean imagingTypeIsNotLast(WellGui wellGui) {
        List<WellHasImagingType> wellHasImagingTypeList = wellGui.getWell().getWellHasImagingTypeList();
        List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(wellHasImagingTypeList);
        boolean isNotLast = false;
        int numberOfImagingTypes = uniqueImagingTypes.size();
        int currentImagingIndex = uniqueImagingTypes.indexOf(currentImagingType);
        if (numberOfImagingTypes != 1 && currentImagingIndex < uniqueImagingTypes.size() - 1) {
            isNotLast = true;
        }
        return isNotLast;
    }

    /**
     * Check if a collection of wellhasImagingTypes contains a certain imaging
     * type
     *
     * @param wellHasImagingTypes
     * @param imagingType
     * @return
     */
    private boolean containsImagingType(List<WellHasImagingType> wellHasImagingTypes, ImagingType imagingType) {
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypes) {
            if (wellHasImagingType.getImagingType().equals(imagingType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialize panel with datasets and imaging types information (user
     * interaction is here required)
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
        loadFromGenericInputPlatePanel.getDataTree().setCellRenderer(new DataTreeCellRenderer(imagingTypesBindingList));

        // allow only one node to be selected
        loadFromGenericInputPlatePanel.getDataTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        loadFromGenericInputPlatePanel.getDataTree().setRootVisible(true);
        loadFromGenericInputPlatePanel.getDataTree().setShowsRootHandles(true);

        // listen to tree selection (imaging type)
        loadFromGenericInputPlatePanel.getDataTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) loadFromGenericInputPlatePanel.getDataTree().getLastSelectedPathComponent();
                if (selectedNode == null) {
                    // nothing is selected, return
                    return;
                }
                // look for imaging type selected
                for (int i = 0; i < imagingTypesBindingList.size(); i++) {
                    if (imagingTypesBindingList.get(i).getName().equals(selectedNode.toString())) {
                        // imaging type that was selected
                        ImagingType selectedImagingType = imagingTypesBindingList.get(i);
                        currentImagingType = selectedImagingType;
                        // look for associated dataset
                        Algorithm associatedDataset = findDataset(selectedNode);
                        currentAlgorithm = associatedDataset;
                        // if mouse listener was still not enabled, enable it, together with main panel buttons
                        if (!mouseListenerEnabled) {
                            mouseListenerEnabled = true;
                            loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getResetButton().setEnabled(true);
                            loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getFinishButton().setEnabled(true);
                        }
                    }
                }
            }
        });

        /**
         * If error occurred, remove dataset
         */
        loadFromGenericInputPlatePanel.getRemoveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // jtree structure
                JTree dataTree = loadFromGenericInputPlatePanel.getDataTree();
                // model of JTree
                DefaultTreeModel model = (DefaultTreeModel) dataTree.getModel();
                // root (Data) node
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
                // last selected node
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
                // dataset and imaging type to remove
                Algorithm algoToRemove = null;
                ImagingType imagingToRemove = null;

                // check that a node is actually selected
                if (selectedNode != null) {
                    // iterate through the algorithms
                    for (int i = 0; i < algorithmsBindingList.size(); i++) {
                        if (selectedNode.toString().equals(algorithmsBindingList.get(i).getAlgorithmName())) {
                            algoToRemove = algorithmsBindingList.get(i);
                            algorithmsBindingList.remove(algoToRemove);
                        }
                    }
                    // iterate through the imaging types
                    for (int j = 0; j < imagingTypesBindingList.size(); j++) {
                        if (selectedNode.toString().equals(imagingTypesBindingList.get(j).getName())) {
                            imagingToRemove = imagingTypesBindingList.get(j);
                            imagingTypesBindingList.remove(imagingToRemove);
                        }
                    }

                    // remove selected node from tree & update model
                    selectedNode.removeFromParent();
                    // if an imaging node is deleted in one dataset, it has to be deleted as well in the other(s)
                    for (int n = 0; n < model.getChildCount(rootNode); n++) {
                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) model.getChild(rootNode, n);
                        for (int m = 0; m < childNode.getChildCount(); m++) {
                            DefaultMutableTreeNode imagingNode = (DefaultMutableTreeNode) childNode.getChildAt(m);
                            if (selectedNode.toString().equals(imagingNode.toString())) {
                                imagingNode.removeFromParent();
                            }
                        }
                    }
                    model.reload();
                } else {
                    loadExperimentFromGenericInputController.showMessage("Select a dataset / imaging type you want to remove!", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        /**
         * Add a dataset to DATA
         */
        loadFromGenericInputPlatePanel.getAddDatasetButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String datasetName = loadFromGenericInputPlatePanel.getDatasetNameTextField().getText();
                if (!datasetName.isEmpty()) {
                    Algorithm newAlgorithm = new Algorithm();
                    newAlgorithm.setAlgorithmName(datasetName);
                    newAlgorithm.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
                    // add algo to list and to data tree
                    addDataset(newAlgorithm);
                    loadFromGenericInputPlatePanel.getDatasetNameTextField().setText("");
                } else {
                    loadExperimentFromGenericInputController.showMessage("Please insert a name for the dataset.", "", JOptionPane.INFORMATION_MESSAGE);
                    loadFromGenericInputPlatePanel.getDatasetNameTextField().requestFocusInWindow();
                }
            }
        });

        /**
         * Add an imaging type to DATA
         */
        loadFromGenericInputPlatePanel.getAddImagingButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String imagingName = loadFromGenericInputPlatePanel.getImagingNameTextField().getText();
                // check that at least one dataset is present
                if (!algorithmsBindingList.isEmpty()) {
                    if (!imagingName.isEmpty()) {
                        ImagingType newImagingType = new ImagingType();
                        newImagingType.setName(imagingName);
                        newImagingType.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
                        // exposure time and light intensity are not set
                        // add imaging type to list and to data tree
                        addImagingType(newImagingType);
                        loadFromGenericInputPlatePanel.getImagingNameTextField().setText("");
                    } else {
                        loadExperimentFromGenericInputController.showMessage("Please insert a name for the imaging type.", "", JOptionPane.INFORMATION_MESSAGE);
                        loadFromGenericInputPlatePanel.getImagingNameTextField().requestFocusInWindow();
                    }
                } else {
                    loadExperimentFromGenericInputController.showMessage("Please insert first a dataset.", "", JOptionPane.INFORMATION_MESSAGE);
                    loadFromGenericInputPlatePanel.getDatasetNameTextField().requestFocusInWindow();
                }
            }
        });
        loadFromGenericInputPlatePanel.getRawDataTable().getTableHeader().setReorderingAllowed(false);
        loadFromGenericInputPlatePanel.getRawDataTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
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
     * Add a new dataset to list and to data tree
     *
     * @param datasetToAdd
     */
    private void addDataset(Algorithm datasetToAdd) {
        if (!algorithmsBindingList.contains(datasetToAdd)) {
            // add dataset to list
            algorithmsBindingList.add(datasetToAdd);
            // model of JTree
            DefaultTreeModel model = (DefaultTreeModel) loadFromGenericInputPlatePanel.getDataTree().getModel();
            // add dataset node to data tree
            DefaultMutableTreeNode rootNote = (DefaultMutableTreeNode) model.getRoot();
            DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode(datasetToAdd.getAlgorithmName());
            rootNote.add(datasetNode);
            // add also imaging types node if present
            if (!imagingTypesBindingList.isEmpty()) {
                for (ImagingType imagingType : imagingTypesBindingList) {
                    DefaultMutableTreeNode imagingNode = new DefaultMutableTreeNode(imagingType.getName());
                    datasetNode.add(imagingNode);
                }
            }
            // reload the model
            model.reload();
            loadFromGenericInputPlatePanel.getDataTree().scrollPathToVisible(new TreePath(datasetNode.getPath()));
        } else {
            loadExperimentFromGenericInputController.showMessage("This dataset was already added!", "", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Add a new imaging type to list and to data tree
     *
     * @param imagingToAdd
     */
    private void addImagingType(ImagingType imagingToAdd) {
        if (!imagingTypesBindingList.contains(imagingToAdd)) {
            // add imaging type to list
            imagingTypesBindingList.add(imagingToAdd);
            // model of JTree
            DefaultTreeModel model = (DefaultTreeModel) loadFromGenericInputPlatePanel.getDataTree().getModel();
            // add imaging type node to data tree
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
            // imaging type node is added for each dataset node
            for (int i = 0; i < algorithmsBindingList.size(); i++) {
                DefaultMutableTreeNode datasetNode = (DefaultMutableTreeNode) model.getChild(rootNode, i);
                DefaultMutableTreeNode imagingNode = new DefaultMutableTreeNode(imagingToAdd.getName());
                datasetNode.add(imagingNode);
                // reload the model
                model.reload();
                loadFromGenericInputPlatePanel.getDataTree().scrollPathToVisible(new TreePath(imagingNode.getPath()));
            }
        } else {
            loadExperimentFromGenericInputController.showMessage("This imaging type was already added!", "", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Given an imaging node, find the upper dataset
     *
     * @param imagingNode
     * @return
     */
    private Algorithm findDataset(DefaultMutableTreeNode imagingNode) {
        Algorithm foundDataset = null;
        DefaultMutableTreeNode datasetNode = (DefaultMutableTreeNode) imagingNode.getParent();
        for (Algorithm algorithm : algorithmsBindingList) {
            if (algorithm.getAlgorithmName().equals(datasetNode.toString())) {
                foundDataset = algorithm;
            }
        }
        return foundDataset;
    }

    /**
     * Show Area values in table
     */
    private void showRawDataInTable() {
        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepsBindingList, loadFromGenericInputPlatePanel.getRawDataTable());
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
        columnBinding.setRenderer(new FormatRenderer(format, SwingConstants.RIGHT));

        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();
    }
}
