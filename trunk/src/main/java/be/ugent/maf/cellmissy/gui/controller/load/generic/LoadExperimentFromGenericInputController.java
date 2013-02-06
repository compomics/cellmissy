/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputPanel;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.ExperimentService;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("loadExperimentFromGenericInputController")
public class LoadExperimentFromGenericInputController {

    //model
    private Experiment experiment;
    //view
    private LoadFromGenericInputPanel loadFromGenericInputPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private GenericImagedPlateController genericImagedPlateController;
    @Autowired
    private GenericExperimentDataController genericExperimentDataController;
    // services
    @Autowired
    private ExperimentService experimentService;

    //services
    /**
     * Initialize controller
     */
    public void init() {
        // init main view
        loadFromGenericInputPanel = new LoadFromGenericInputPanel();
        initMainPanel();
        //init child controllers
        genericExperimentDataController.init();
        genericImagedPlateController.init();
    }

    /**
     * getters and setters
     * @return 
     */
    public LoadFromGenericInputPanel getLoadFromGenericInputPanel() {
        return loadFromGenericInputPanel;
    }

    public void showMessage(String message, Integer messageType) {
        cellMissyController.showMessage(message, messageType);
    }

    public void updateInfoLabel(JLabel label, String message) {
        cellMissyController.updateInfoLabel(label, message);
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public ImagedPlatePanel getImagedPlatePanel() {
        return genericImagedPlateController.getImagedPlatePanel();
    }

    public void initPlatePanel(PlateFormat plateFormat, Dimension dimension) {
        genericImagedPlateController.getImagedPlatePanel().initPanel(plateFormat, dimension);
    }

    public CellMissyFrame getCellMissyFrame() {
        return cellMissyController.getCellMissyFrame();
    }

    /**
     * Enable buttons once one experiment is loaded
     */
    public void enableButtons() {
        loadFromGenericInputPanel.getRemoveButton().setEnabled(true);
        loadFromGenericInputPanel.getAddDatasetButton().setEnabled(true);
        loadFromGenericInputPanel.getAddImagingButton().setEnabled(true);
    }

    /**
     * 
     */
    private void initMainPanel() {
        //update info message
        cellMissyController.updateInfoLabel(loadFromGenericInputPanel.getInfolabel(), "Select a project and then an experiment in progress to load motility data.");
        // disable buttons
        loadFromGenericInputPanel.getResetButton().setEnabled(false);
        loadFromGenericInputPanel.getFinishButton().setEnabled(false);
        loadFromGenericInputPanel.getRemoveButton().setEnabled(false);
        loadFromGenericInputPanel.getAddDatasetButton().setEnabled(false);
        loadFromGenericInputPanel.getAddImagingButton().setEnabled(false);

        // hide progress bar
        loadFromGenericInputPanel.getjProgressBar1().setVisible(false);
        // allow only one node to be selected
        loadFromGenericInputPanel.getDataTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // listen to tree selection (imaging type)
        loadFromGenericInputPanel.getDataTree().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) loadFromGenericInputPanel.getDataTree().getLastSelectedPathComponent();
                if (selectedNode == null) {
                    // nothing is selected, return
                    return;
                }
                // look for imaging type selected
                for (int i = 0; i < genericImagedPlateController.getImagingTypesBindingList().size(); i++) {
                    if (genericImagedPlateController.getImagingTypesBindingList().get(i).getName().equals(selectedNode.toString())) {
                        // imaging type that was selected
                        ImagingType selectedImagingType = genericImagedPlateController.getImagingTypesBindingList().get(i);
                        genericImagedPlateController.setCurrentImagingType(selectedImagingType);
                        // look for associated dataset
                        Algorithm associatedDataset = findDataset(selectedNode);
                        genericImagedPlateController.setCurrentAlgorithm(associatedDataset);
                        // if mouse listener was still not enabled, enable it, together with main panel buttons
                        if (!genericImagedPlateController.isMouseListenerEnabled()) {
                            genericImagedPlateController.setMouseListenerEnabled(true);
                            loadFromGenericInputPanel.getResetButton().setEnabled(true);
                            loadFromGenericInputPanel.getFinishButton().setEnabled(true);
                        }
                    }
                }
            }
        });


        // add action listeners
        /**
         * Reset view on plate (all raw data is deleted)
         */
        loadFromGenericInputPanel.getResetButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // warn the user that data was already loaded for the selected combination of well/dataset/imaging type
                Object[] options = {"Continue", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(null, "Do you really want to reset everything?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                switch (showOptionDialog) {
                    case 0:
                        // reset
                        genericImagedPlateController.reset();
                        break;
                    case 1:
                        // cancel: do nothing
                        return;
                }
            }
        });

        /**
         * Save experiment
         */
        loadFromGenericInputPanel.getFinishButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // validate experiment
                // if everything is valid, save the experiment, else show a message
                List<String> messages = genericExperimentDataController.setExperimentMetadata();
                if (messages.isEmpty()) {
                    // check if data loading is valid
                    if (genericImagedPlateController.validateDataLoading()) {
                        // if data loading is valid, set motility data and save the experiment
                        //set motility Data
                        setMotilityData();
                        //set experiment status to "performed" and save it to DB
                        experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                        //launch a swing worker to save the experiment in the background thread
                        SaveExperimentWorker worker = new SaveExperimentWorker();
                        worker.execute();
                    } else {
                        // if data loading is not valid, ask the user if he wants to procede with storing
                        Object[] options = {"Continue", "Cancel"};
                        int showOptionDialog = JOptionPane.showOptionDialog(null, "Some wells still do not have any data.\nDo you want to procede with storage?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                        switch (showOptionDialog) {
                            case 0: // set motility data and procede with storage
                                //set motility Data
                                setMotilityData();
                                //set experiment status to "performed" and save it to DB
                                experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                                //launch a swing worker to save the experiment in the background thread
                                SaveExperimentWorker worker = new SaveExperimentWorker();
                                worker.execute();
                                break;
                            case 1:
                        }
                    }
                } else {
                    String message = "";
                    for (String string : messages) {
                        message += string + "\n";
                    }
                    showMessage(message, JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        /**
         * If error occurred, remove dataset 
         */
        loadFromGenericInputPanel.getRemoveButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // jtree structure
                JTree dataTree = loadFromGenericInputPanel.getDataTree();
                // root (Data) node
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) dataTree.getModel().getRoot();
                // last selected node
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
                // dataset and imaging type to remove
                Algorithm algoToRemove = null;
                ImagingType imagingToRemove = null;

                // check that a node is actually selected
                if (selectedNode != null) {
                    // iterate through the algorithms 
                    for (int i = 0; i < genericImagedPlateController.getAlgorithmsBindingList().size(); i++) {
                        if (selectedNode.toString().equals(genericImagedPlateController.getAlgorithmsBindingList().get(i).getAlgorithmName())) {
                            algoToRemove = genericImagedPlateController.getAlgorithmsBindingList().get(i);
                            genericImagedPlateController.getAlgorithmsBindingList().remove(algoToRemove);
                        }
                    }
                    // iterate through the imaging types
                    for (int j = 0; j < genericImagedPlateController.getImagingTypesBindingList().size(); j++) {
                        if (selectedNode.toString().equals(genericImagedPlateController.getImagingTypesBindingList().get(j).getName())) {
                            imagingToRemove = genericImagedPlateController.getImagingTypesBindingList().get(j);
                            genericImagedPlateController.getImagingTypesBindingList().remove(imagingToRemove);
                        }
                    }

                    // remove selected node from tree & update model
                    selectedNode.removeFromParent();
                    // if an imaging node is deleted in one dataset, it has to be deleted as well in the other(s)
                    for (int n = 0; n < dataTree.getModel().getChildCount(rootNode); n++) {
                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) dataTree.getModel().getChild(rootNode, n);
                        for (int m = 0; m < childNode.getChildCount(); m++) {
                            DefaultMutableTreeNode imagingNode = (DefaultMutableTreeNode) childNode.getChildAt(m);
                            if (selectedNode.toString().equals(imagingNode.toString())) {
                                imagingNode.removeFromParent();
                            }
                        }
                    }
                    DefaultTreeModel model = (DefaultTreeModel) dataTree.getModel();
                    model.reload();
                } else {
                    showMessage("Select a dataset / imaging type you want to remove!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        /**
         * Add a dataset to DATA
         */
        loadFromGenericInputPanel.getAddDatasetButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String datasetName = loadFromGenericInputPanel.getDatasetNameTextField().getText();
                if (!datasetName.isEmpty()) {
                    Algorithm newAlgorithm = new Algorithm();
                    newAlgorithm.setAlgorithmName(datasetName);
                    newAlgorithm.setWellHasImagingTypeCollection(new ArrayList<WellHasImagingType>());
                    // add algo to list and to data tree
                    addDataset(newAlgorithm);
                    loadFromGenericInputPanel.getDatasetNameTextField().setText("");
                } else {
                    showMessage("Please insert a name for the dataset.", JOptionPane.INFORMATION_MESSAGE);
                    loadFromGenericInputPanel.getDatasetNameTextField().requestFocusInWindow();
                }
            }
        });

        /**
         * Add an imaging type to DATA
         */
        loadFromGenericInputPanel.getAddImagingButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String imagingName = loadFromGenericInputPanel.getImagingNameTextField().getText();
                // check that at least one dataset is present
                if (!genericImagedPlateController.getAlgorithmsBindingList().isEmpty()) {
                    if (!imagingName.isEmpty()) {
                        ImagingType newImagingType = new ImagingType();
                        newImagingType.setName(imagingName);
                        newImagingType.setWellHasImagingTypeCollection(new ArrayList<WellHasImagingType>());
                        // exposure time and light intensity are not set
                        // add imaging type to list and to data tree
                        addImagingType(newImagingType);
                        loadFromGenericInputPanel.getImagingNameTextField().setText("");
                    } else {
                        showMessage("Please insert a name for the imaging type.", JOptionPane.INFORMATION_MESSAGE);
                        loadFromGenericInputPanel.getImagingNameTextField().requestFocusInWindow();
                    }
                } else {
                    showMessage("Please insert first a dataset.", JOptionPane.INFORMATION_MESSAGE);
                    loadFromGenericInputPanel.getDatasetNameTextField().requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Add a new dataset to list and to data tree
     * @param datasetToAdd 
     */
    private void addDataset(Algorithm datasetToAdd) {
        if (!genericImagedPlateController.getAlgorithmsBindingList().contains(datasetToAdd)) {
            // add dataset to list
            genericImagedPlateController.getAlgorithmsBindingList().add(datasetToAdd);
            // add dataset node to data tree
            DefaultMutableTreeNode rootNote = (DefaultMutableTreeNode) loadFromGenericInputPanel.getDataTree().getModel().getRoot();
            DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode(datasetToAdd.getAlgorithmName());
            rootNote.add(datasetNode);
            // add also imaging types node if present
            if (!genericImagedPlateController.getImagingTypesBindingList().isEmpty()) {
                for (ImagingType imagingType : genericImagedPlateController.getImagingTypesBindingList()) {
                    DefaultMutableTreeNode imagingNode = new DefaultMutableTreeNode(imagingType.getName());
                    datasetNode.add(imagingNode);
                }
            }
            // reload the model
            DefaultTreeModel model = (DefaultTreeModel) loadFromGenericInputPanel.getDataTree().getModel();
            model.reload();
            loadFromGenericInputPanel.getDataTree().scrollPathToVisible(new TreePath(datasetNode.getPath()));
        } else {
            showMessage("This dataset was already added!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Add a new imaging type to list and to data tree
     * @param imagingToAdd 
     */
    private void addImagingType(ImagingType imagingToAdd) {
        if (!genericImagedPlateController.getImagingTypesBindingList().contains(imagingToAdd)) {
            // add imaging type to list
            genericImagedPlateController.getImagingTypesBindingList().add(imagingToAdd);
            // add imaging type node to data tree
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) loadFromGenericInputPanel.getDataTree().getModel().getRoot();
            // imaging type node is added for each dataset node
            for (int i = 0; i < genericImagedPlateController.getAlgorithmsBindingList().size(); i++) {
                DefaultMutableTreeNode datasetNode = (DefaultMutableTreeNode) loadFromGenericInputPanel.getDataTree().getModel().getChild(rootNode, i);
                DefaultMutableTreeNode imagingNode = new DefaultMutableTreeNode(imagingToAdd.getName());
                datasetNode.add(imagingNode);
                // reload the model
                DefaultTreeModel model = (DefaultTreeModel) loadFromGenericInputPanel.getDataTree().getModel();
                model.reload();
                loadFromGenericInputPanel.getDataTree().scrollPathToVisible(new TreePath(imagingNode.getPath()));
            }
        } else {
            showMessage("This imaging type was already added!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Given an imaging node, find the upper dataset
     * @param imagingNode
     * @return 
     */
    private Algorithm findDataset(DefaultMutableTreeNode imagingNode) {
        Algorithm foundDataset = null;
        DefaultMutableTreeNode datasetNode = (DefaultMutableTreeNode) imagingNode.getParent();
        for (Algorithm algorithm : genericImagedPlateController.getAlgorithmsBindingList()) {
            if (algorithm.getAlgorithmName().equals(datasetNode.toString())) {
                foundDataset = algorithm;
            }
        }
        return foundDataset;
    }

    /**
     * Swing Worker to save the Experiment
     */
    private class SaveExperimentWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            //disable the Finish button and show a waiting cursor
            loadFromGenericInputPanel.getFinishButton().setEnabled(false);
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //show a progress bar (indeterminate)
            loadFromGenericInputPanel.getjProgressBar1().setVisible(true);
            loadFromGenericInputPanel.getjProgressBar1().setIndeterminate(true);

            //INSERT experiment to DB
            experimentService.save(experiment);
            return null;
        }

        @Override
        protected void done() {
            //show back default cursor and hide the progress bar
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            loadFromGenericInputPanel.getjProgressBar1().setVisible(false);
            //update info for the user
            showMessage("Experiment was successfully saved to DB.", JOptionPane.INFORMATION_MESSAGE);
            updateInfoLabel(loadFromGenericInputPanel.getInfolabel(), "Experiment was successfully saved to DB.");
        }
    }

    /**
     * this method sets Migration data of wells, before the experiment is saved to DB
     */
    private void setMotilityData() {

        for (PlateCondition plateCondition : experiment.getPlateConditionCollection()) {
            for (WellGui wellGui : genericImagedPlateController.getImagedPlatePanel().getWellGuiList()) {

                //if the wellGui has a well with a NOT empty collection of wellHasImagingTypes, the well has been imaged
                //if the wellGui has a rectangle, the well belongs to a certain condition
                //only if these two conditions are true, motility data must be set and stored to DB
                if (!wellGui.getWell().getWellHasImagingTypeCollection().isEmpty() && wellGui.getRectangle() != null) {

                    for (Well well : plateCondition.getWellCollection()) {
                        //check for coordinates
                        if (well.getColumnNumber() == wellGui.getColumnNumber() && well.getRowNumber() == wellGui.getRowNumber()) {
                            //set collection of wellHasImagingType to the well of the plateCondition
                            well.setWellHasImagingTypeCollection(wellGui.getWell().getWellHasImagingTypeCollection());

                            //the other way around: set the well for each wellHasImagingType
                            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeCollection()) {
                                wellHasImagingType.setWell(well);
                            }
                        }

                    }
                }
            }
        }
    }
}
