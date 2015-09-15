/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.exception.GenericImportDirectoryException;
import be.ugent.maf.cellmissy.gui.controller.load.generic.area.GenericAreaImagedPlateController;
import be.ugent.maf.cellmissy.gui.controller.load.generic.singlecell.GenericSingleCellImagedPlateController;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.GenericImportInfoDialog;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.tree.LoadDataTreeCellRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A controller to take care of loading data from a generic input, both area and
 * single cell data.
 *
 * @author Paola Masuzzo
 */
@Component("genericImagedPlateController")
public class GenericImagedPlateController {

    private static final Logger LOG = Logger.getLogger(GenericImagedPlateController.class);
    //model
    private ObservableList<Algorithm> algorithmsBindingList;
    private ObservableList<ImagingType> imagingTypesBindingList;
    private Format format;
    private ImagingType currentImagingType;
    private Algorithm currentAlgorithm;
    private boolean isDirectoryLoaded;
    private BindingGroup bindingGroup;
    private File directory;
    //view
    private ImagedPlatePanel imagedPlatePanel;
    private LoadFromGenericInputPlatePanel loadFromGenericInputPlatePanel;
    private GenericImportInfoDialog genericImportInfoDialog;
    // parent controller
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    // child controllers
    @Autowired
    private GenericAreaImagedPlateController genericAreaImagedPlateController;
    @Autowired
    private GenericSingleCellImagedPlateController genericSingleCellImagedPlateController;
    @Autowired
    private DragAndDropController dragAndDropController;
    //services
    @Autowired
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;
    private boolean mouseListenerEnabled;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        imagedPlatePanel = new ImagedPlatePanel();
        genericImportInfoDialog = new GenericImportInfoDialog(loadExperimentFromGenericInputController.getCellMissyFrame());
        loadFromGenericInputPlatePanel = new LoadFromGenericInputPlatePanel();
        //disable mouse Listener
        mouseListenerEnabled = false;
        format = new DecimalFormat(PropertiesConfigurationHolder.getInstance().getString("dataFormat"));
        // init child controllers
        genericAreaImagedPlateController.init();
        genericSingleCellImagedPlateController.init();
        dragAndDropController.init();
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

    public Format getFormat() {
        return format;
    }

    public void showMessage(String message, String title, Integer messageType) {
        loadExperimentFromGenericInputController.showMessage(message, title, messageType);
    }

    public ObservableList<ImagingType> getImagingTypesBindingList() {
        return imagingTypesBindingList;
    }

    public ObservableList<Algorithm> getAlgorithmsBindingList() {
        return algorithmsBindingList;
    }

    public File getDirectory() {
        return directory;
    }

    /**
     * Load the data.
     *
     * @param dataFile
     * @param newWellHasImagingType
     * @param selectedWellGui
     */
    public void loadData(File dataFile, WellHasImagingType newWellHasImagingType, WellGui selectedWellGui) {
        // if it's area that we need to load...
        if (loadExperimentFromGenericInputController.isGenericArea()) {
            genericAreaImagedPlateController.loadData(dataFile, newWellHasImagingType, selectedWellGui);
            // check if table still has to be initialized
            if (genericAreaImagedPlateController.getTimeStepsTableBinding() == null) {
                genericAreaImagedPlateController.showRawDataInTable();
            }
        } else {
            // call the other controller
            genericSingleCellImagedPlateController.loadData(dataFile, newWellHasImagingType, selectedWellGui);
            // check if table still has to be initialized
            if (genericSingleCellImagedPlateController.getTrackPointsTableBinding() == null) {
                genericSingleCellImagedPlateController.showRawDataInTable();
            }
        }
    }

    /**
     * Overwrite data for a specific well.
     *
     * @param newFile: the new file to parse to load the new data.
     * @param selectedWellGui: the well to overwrite the data for.
     * @param newWellHasImagingType: the new sample created to load the new
     * data.
     */
    public void overwriteDataForWell(File newFile, WellGui selectedWellGui, WellHasImagingType newWellHasImagingType) {
        if (loadExperimentFromGenericInputController.isGenericArea()) {
            genericAreaImagedPlateController.removeOldDataFromList(newWellHasImagingType);
        } else {
            // call the other controller
            genericSingleCellImagedPlateController.removeOldDataFromList(newWellHasImagingType);
        }
        // remove the data from the well
        selectedWellGui.getWell().getWellHasImagingTypeList().remove(newWellHasImagingType);
        // update relation with algorithm and imaging type
        dragAndDropController.getCurrentAlgorithm().getWellHasImagingTypeList().remove(newWellHasImagingType);
        dragAndDropController.getCurrentImagingType().getWellHasImagingTypeList().remove(newWellHasImagingType);

        if (loadExperimentFromGenericInputController.isGenericArea()) {
            genericAreaImagedPlateController.loadData(newFile, newWellHasImagingType, selectedWellGui);
        } else {
            // call the other controller
            genericSingleCellImagedPlateController.loadData(newFile, newWellHasImagingType, selectedWellGui);
        }
    }

    /**
     * Given a certain wellGui (and thus a well), just clear all the data loaded
     * for it (reset the well).
     *
     * @param selectedWellGui
     */
    public void clearDataForWell(WellGui selectedWellGui) {
        List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {

            if (loadExperimentFromGenericInputController.isGenericArea()) {
                genericAreaImagedPlateController.removeOldDataFromList(wellHasImagingType);
            } else {
                genericSingleCellImagedPlateController.removeOldDataFromList(wellHasImagingType);
            }
            dragAndDropController.getCurrentAlgorithm().getWellHasImagingTypeList().remove(wellHasImagingType);
            dragAndDropController.getCurrentImagingType().getWellHasImagingTypeList().remove(wellHasImagingType);
        }

        resetWellGui(selectedWellGui);
        imagedPlatePanel.repaint();
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
        if (loadExperimentFromGenericInputController.isGenericArea()) {
            genericAreaImagedPlateController.getTimeStepsBindingList().clear();
        } else {
            genericSingleCellImagedPlateController.getTrackPointsBindingList().clear();
        }

        // resetData view on the plate
        // empty wellhasimagingtype collection of each well
        List<WellGui> wellGuiList = imagedPlatePanel.getWellGuiList();
        for (WellGui wellGui : wellGuiList) {
            resetWellGui(wellGui);
        }
        // repaint the plate view
        imagedPlatePanel.repaint();
        for (ImagingType imagingType : imagingTypesBindingList) {
            imagingType.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
        }
        for (Algorithm algorithm : algorithmsBindingList) {
            algorithm.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
        }
    }

    /**
     * Given a wellGui, reset both its data and its layout (just retain the
     * biggest ellipse).
     *
     * @param wellGui
     */
    private void resetWellGui(WellGui wellGui) {
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
    }

    /**
     * Initialize panel with datasets and imaging types information (user
     * interaction is here required)
     */
    private void initAlgoImagingPanel() {
        // init algo list
        algorithmsBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        //init imaging type list
        imagingTypesBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        // set imaging type list for plate
        imagedPlatePanel.setImagingTypeList(imagingTypesBindingList);
        // set cell renderer for the JrTree
        loadFromGenericInputPlatePanel.getDirectoryTree().setCellRenderer(new LoadDataTreeCellRenderer(imagingTypesBindingList));
        loadFromGenericInputPlatePanel.getDirectoryTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // set icon for the question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        loadFromGenericInputPlatePanel.getQuestionButton().setIcon(GuiUtils.getScaledIcon(questionIcon));
        // bind algorithms and imaging types to the Jlists
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, algorithmsBindingList, loadFromGenericInputPlatePanel.getAlgorithmList());
        bindingGroup.addBinding(jListBinding);
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, imagingTypesBindingList, loadFromGenericInputPlatePanel.getImagingTypeList());
        bindingGroup.addBinding(jListBinding);

        bindingGroup.bind();

        /**
         * Action Listeners.
         */
        /**
         * Load the DATA directory into the JTree.
         */
        loadFromGenericInputPlatePanel.getLoadDirectoryButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // first, we let the user choose the directory to load
                // we check if the directory was already loaded before
                if (!isDirectoryLoaded) {
                    chooseDirectoryAndLoadData();
                } else {
                    // otherwise we ask the user if they want to reload the directory
                    Object[] options = {"Load a different directory", "Cancel"};
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "It seems a directory was already loaded.\nLoading a different directory will RESET the plate!\nWhat do you want to do?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    switch (showOptionDialog) {
                        case 0: // load a different directory:
                            // reset data
                            resetData();
                            // clear algo and imaging type lists
                            algorithmsBindingList.clear();
                            imagingTypesBindingList.clear();
                            // reset the model of the directory tree
                            DefaultTreeModel model = (DefaultTreeModel) loadFromGenericInputPlatePanel.getDirectoryTree().getModel();
                            DefaultMutableTreeNode rootNote = (DefaultMutableTreeNode) model.getRoot();
                            rootNote.removeAllChildren();
                            model.reload();
                            chooseDirectoryAndLoadData();
                            break;  // cancel: do nothing
                    }
                }
                // we can now enable the reset and finish buttons
                loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getResetButton().setEnabled(true);
                loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getFinishButton().setEnabled(true);
            }
        });

        /**
         * Show some info on clicking on the question button.
         */
        loadFromGenericInputPlatePanel.getQuestionButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // pack and show info dialog
                GuiUtils.centerDialogOnFrame(loadExperimentFromGenericInputController.getCellMissyFrame(), genericImportInfoDialog);
                genericImportInfoDialog.setVisible(true);
            }
        });

        loadFromGenericInputPlatePanel.getRawDataTable().getTableHeader().setReorderingAllowed(false);
        loadFromGenericInputPlatePanel.getRawDataTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
    }

    /**
     * Choose and return the directory to load into the JTree.
     *
     * @return
     */
    private void chooseDirectoryAndLoadData() {
        JFileChooser fileChooser = new JFileChooser();
        String chooserTitle = "Please select a root folder";
        fileChooser.setDialogTitle(chooserTitle);
        // allow for directories only
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // removing "All Files" option from FileType
        fileChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = fileChooser.showOpenDialog(loadExperimentFromGenericInputController.getCellMissyFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // the directory for the data
            directory = fileChooser.getSelectedFile();
            try {
                loadDataIntoTree();
            } catch (GenericImportDirectoryException ex) {
                LOG.error(ex.getMessage());
                showMessage(ex.getMessage(), "wrong directory structure error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            loadExperimentFromGenericInputController.showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Given the chosen directory, read all its folders/files and load the data
     * into the JTree
     *
     * @param directory
     */
    private void loadDataIntoTree() throws GenericImportDirectoryException {
        DefaultTreeModel model = (DefaultTreeModel) loadFromGenericInputPlatePanel.getDirectoryTree().getModel();
        DefaultMutableTreeNode rootNote = (DefaultMutableTreeNode) model.getRoot();
        // change name (user object) of root node
        rootNote.setUserObject(directory.getName());
        // list the files in the directory 
        // first level of information is the algorithm level
        File[] algoFolders = directory.listFiles();
        // check that the directory is not empty
        if (algoFolders.length != 0) {
            // iterate through the files and check if they are directories or files
            for (File algoFolder : algoFolders) {
                if (algoFolder.isDirectory()) {
                    // algo folder --> imaging folder(s)
                    File[] imagingFolders = algoFolder.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.isDirectory();
                        }
                    });
                    if (imagingFolders.length != 0) {
                        for (File imagingFolder : imagingFolders) {

                            // imaging folder --> text files containing the data to actually load
                            File[] dataFiles = imagingFolder.listFiles(new FileFilter() {
                                @Override
                                public boolean accept(File f) {
                                    int index = f.getName().lastIndexOf(".");
                                    String extension = f.getName().substring(index + 1);
                                    return extension.equals("txt");
                                }
                            });
                            if (dataFiles.length != 0) {
                                // create the node and add it to the root
                                DefaultMutableTreeNode algoNode = new DefaultMutableTreeNode(algoFolder.getName(), Boolean.TRUE);
                                rootNote.add(algoNode);
                                // also, add the algo to the list
                                Algorithm algorithm = new Algorithm();
                                algorithm.setAlgorithmName(algoFolder.getName());
                                algorithm.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
                                algorithmsBindingList.add(algorithm);
                                // create the node and add it to the root
                                DefaultMutableTreeNode imagingNode = new DefaultMutableTreeNode(imagingFolder.getName(), Boolean.TRUE);
                                algoNode.add(imagingNode);
                                // also, add the imaging type to the list
                                ImagingType imagingType = new ImagingType();
                                imagingType.setName(imagingFolder.getName());
                                imagingType.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
                                if (!imagingTypesBindingList.contains(imagingType)) {
                                    imagingTypesBindingList.add(imagingType);
                                }
                                for (File dataFile : dataFiles) {
                                    DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(dataFile.getName(), Boolean.FALSE);
                                    imagingNode.add(dataNode);
                                }
                            } else {
                                throw new GenericImportDirectoryException("Sorry, this directory structure doesn't seem quite correct!");
                            }
                        }
                    } else {
                        throw new GenericImportDirectoryException("Sorry, this directory structure doesn't seem quite correct!");
                    }
                } else {
                    throw new GenericImportDirectoryException("Sorry, this directory structure doesn't seem quite correct!");
                }
            }
        } else {
            throw new GenericImportDirectoryException("This directory seems to be empty!\nPlease load something else!");
        }
        model.reload();
        isDirectoryLoaded = true;
        for (int row = 0; row < loadFromGenericInputPlatePanel.getDirectoryTree().getRowCount(); row++) {
            loadFromGenericInputPlatePanel.getDirectoryTree().expandRow(row);
        }
        loadFromGenericInputPlatePanel.getDirectoryTextArea().setText(directory.getAbsolutePath());
        loadExperimentFromGenericInputController.showMessage("Directory successful loaded!\nYou can start dragging and dropping files into your plate!", "", JOptionPane.INFORMATION_MESSAGE);
    }
}
