/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputPanel;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author Paola Masuzzo
 */
@Controller("loadExperimentFromGenericInputController")
public class LoadExperimentFromGenericInputController {

    private static final Logger LOG = Logger.getLogger(LoadExperimentFromGenericInputController.class);
    //model
    private Experiment experiment;
    private boolean dataLoadingHasBeenSaved;
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
    private GridBagConstraints gridBagConstraints;

    //services

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        loadFromGenericInputPanel = new LoadFromGenericInputPanel();
        dataLoadingHasBeenSaved = false;
        //init child controllers
        genericExperimentDataController.init();
        genericImagedPlateController.init();
        // init main view
        initMainPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public LoadFromGenericInputPanel getLoadFromGenericInputPanel() {
        return loadFromGenericInputPanel;
    }

    public LoadFromGenericInputPlatePanel getLoadFromGenericInputPlatePanel() {
        return genericImagedPlateController.getLoadFromGenericInputPlatePanel();
    }

    public void showMessage(String message, String title, Integer messageType) {
        cellMissyController.showMessage(message, title, messageType);
    }

    void handleUnexpectedError(Exception ex) {
        cellMissyController.handleUnexpectedError(ex);
    }

    void updateInfoLabel(JLabel label, String message) {
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

    public CellMissyFrame getCellMissyFrame() {
        return cellMissyController.getCellMissyFrame();
    }

    // resetData everything
    public void resetData() {
        genericImagedPlateController.resetData();
    }

    public User getCurrentUser() {
        return cellMissyController.getCurrentUser();
    }

    /**
     * Check if current analysis has been saved before leaving the view
     *
     * @return
     */
    public boolean loadingWasSaved() {
        boolean saved = true;
        if (experiment != null && !dataLoadingHasBeenSaved) {
            saved = false;
        }
        return saved;
    }

    /**
     * Called in the main controller, resetData views and models if another view
     * has being shown
     */
    public void resetAfterCardSwitch() {
        experiment = null;
        dataLoadingHasBeenSaved = false;
        // clear selection on project list
        genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getProjectsList().clearSelection();
        // clear experiment list
        if (genericExperimentDataController.getExperimentBindingList() != null) {
            genericExperimentDataController.getExperimentBindingList().clear();
        }
        resetData();
        // reset text fields
        genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getDurationTextField().setText("");
        genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getIntervalTextField().setText("");
        genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getTimeFramesTextField().setText("");
        // set text area to empty field
        genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getProjectDescriptionTextArea().setText("");
        // swap views
        GuiUtils.switchChildPanels(loadFromGenericInputPanel.getTopPanel(), genericExperimentDataController.getLoadFromGenericInputMetadataPanel(), genericImagedPlateController.getLoadFromGenericInputPlatePanel());
        loadFromGenericInputPanel.getTopPanel().repaint();
        loadFromGenericInputPanel.getTopPanel().revalidate();
        loadFromGenericInputPanel.getStartButton().setEnabled(false);
    }

    /**
     * Initialize main panel
     */
    private void initMainPanel() {
        genericImagedPlateController.getLoadFromGenericInputPlatePanel().getPurposeTextArea().setLineWrap(true);
        genericImagedPlateController.getLoadFromGenericInputPlatePanel().getPurposeTextArea().setWrapStyleWord(true);
        //update info message
        cellMissyController.updateInfoLabel(loadFromGenericInputPanel.getInfolabel(), "Select project/experiment in progress to load data; provide experiment metadata to start with the import.");
        // disable buttons
        loadFromGenericInputPanel.getResetButton().setEnabled(false);
        loadFromGenericInputPanel.getFinishButton().setEnabled(false);
        loadFromGenericInputPanel.getStartButton().setEnabled(false);

        // hide progress bar
        loadFromGenericInputPanel.getSaveDataProgressBar().setVisible(false);

        // add document listener to experiment data text fields and start button
        ExperimentListener experimentListener = new ExperimentListener(loadFromGenericInputPanel.getStartButton());
        experimentListener.registerDoc(genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getDurationTextField().getDocument());
        experimentListener.registerDoc(genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getIntervalTextField().getDocument());
        experimentListener.registerDoc(genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getTimeFramesTextField().getDocument());


        // add action listeners
        /**
         * Start: swap views
         */
        loadFromGenericInputPanel.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // validate experiment
                // if everything is valid, update the experiment, else show a message
                List<String> messages = validateExperimentMetadata();
                if (messages.isEmpty()) {
                    cellMissyController.updateInfoLabel(loadFromGenericInputPanel.getInfolabel(), "Add datasets and imaging types to start the import. Click on each well to choose file(s) to import.");
                    LoadFromGenericInputPlatePanel loadFromGenericInputPlatePanel = genericImagedPlateController.getLoadFromGenericInputPlatePanel(); // update labels with experiment metadata
                    loadFromGenericInputPlatePanel.getProjNumberLabel().setText(experiment.getProject().toString());
                    loadFromGenericInputPlatePanel.getExpNumberLabel().setText(experiment.toString());
                    loadFromGenericInputPlatePanel.getPurposeTextArea().setText(experiment.getPurpose());
                    GuiUtils.switchChildPanels(loadFromGenericInputPanel.getTopPanel(), loadFromGenericInputPlatePanel, genericExperimentDataController.getLoadFromGenericInputMetadataPanel());
                    loadFromGenericInputPanel.getTopPanel().repaint();
                    loadFromGenericInputPanel.getTopPanel().revalidate();
                    loadFromGenericInputPanel.getStartButton().setEnabled(false);
                } else {
                    String message = "";
                    for (String string : messages) {
                        message += string + "\n";
                    }
                    showMessage(message, "Experiment validation problem", JOptionPane.WARNING_MESSAGE);
                    genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getTimeFramesTextField().requestFocusInWindow();
                }
            }
        });

        /**
         * Reset view on plate (all raw data is deleted)
         */
        loadFromGenericInputPanel.getResetButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // warn the user that data are going to be lost
                Object[] options = {"Continue", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(null, "Do you really want to reset everything?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                switch (showOptionDialog) {
                    case 0:
                        // keep on resetting the view
                        resetData();
                        break;
                    case 1:
                        break;
                }
            }
        });

        /**
         * Save experiment
         */
        loadFromGenericInputPanel.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if data loading is valid
                if (genericImagedPlateController.validateDataLoading()) {
                    // if data loading is valid, set motility data and update the experiment
                    //set motility Data
                    setMotilityData();
                    //set experiment status to "performed" and update it to DB
                    experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                    //launch a swing worker to update the experiment in the background thread
                    SaveExperimentWorker worker = new SaveExperimentWorker();
                    worker.execute();
                } else {
                    // if data loading is not valid, ask the user if he wants to proceed with storing
                    Object[] options = {"Continue", "Cancel"};
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "Some wells still do not have any data.\nDo you want to proceed with storage?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    switch (showOptionDialog) {
                        case 0: // set motility data and proceed with storage
                            //set motility Data
                            setMotilityData();
                            //set experiment status to "performed" and update it to DB
                            experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                            //launch a swing worker to update the experiment in the background thread
                            SaveExperimentWorker worker = new SaveExperimentWorker();
                            worker.execute();
                            break;
                        case 1:
                    }
                }
            }
        });
        // add main view on top panel
        loadFromGenericInputPanel.getTopPanel().add(genericExperimentDataController.getLoadFromGenericInputMetadataPanel(), gridBagConstraints);
        cellMissyController.getCellMissyFrame().getLoadFromGenericInputParentPanel().add(loadFromGenericInputPanel, gridBagConstraints);
    }

    /**
     * this method checks experiment Info
     *
     * @return
     */
    private List<String> validateExperimentMetadata() {
        List<String> messages = new ArrayList<>();
        try {
            // time frames
            experiment.setTimeFrames(Integer.parseInt(genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getTimeFramesTextField().getText()));
            // interval
            experiment.setExperimentInterval(Double.parseDouble(genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getIntervalTextField().getText()));
            // duration
            experiment.setDuration(Double.parseDouble(genericExperimentDataController.getLoadFromGenericInputMetadataPanel().getDurationTextField().getText()));
        } catch (NumberFormatException e) {
            messages.add("Please insert valid experiment metadata");
        }
        return messages;
    }

    /**
     * Swing Worker to update the Experiment
     */
    private class SaveExperimentWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            //disable the Finish button
            loadFromGenericInputPanel.getFinishButton().setEnabled(false);
            //show a progress bar (indeterminate)
            loadFromGenericInputPanel.getSaveDataProgressBar().setVisible(true);
            loadFromGenericInputPanel.getSaveDataProgressBar().setIndeterminate(true);
            // update message
            updateInfoLabel(loadFromGenericInputPanel.getInfolabel(), "Please wait, data is being saved!");
            // show a waiting cursor
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // save motility data
            experimentService.saveMigrationDataForExperiment(experiment);
            // update experiment
            experiment = experimentService.update(experiment);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                dataLoadingHasBeenSaved = true;
                //show back default cursor and hide the progress bar
                cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                loadFromGenericInputPanel.getSaveDataProgressBar().setVisible(false);
                //update info for the user
                showMessage("Experiment was successfully saved to DB.\nPlease choose what you want to do next.", "Experiment saved", JOptionPane.INFORMATION_MESSAGE);
                updateInfoLabel(loadFromGenericInputPanel.getInfolabel(), "Experiment was successfully saved to DB.");
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                handleUnexpectedError(ex);
            }
        }
    }

    /**
     * this method sets Migration data of wells, before the experiment is saved
     * to DB
     */
    private void setMotilityData() {

        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            for (WellGui wellGui : genericImagedPlateController.getImagedPlatePanel().getWellGuiList()) {

                //if the wellGui has a well with a NOT empty List of wellHasImagingTypes, the well has been imaged
                //if the wellGui has a rectangle, the well belongs to a certain condition
                //only if these two conditions are true, motility data must be set and stored to DB
                if (!wellGui.getWell().getWellHasImagingTypeList().isEmpty() && wellGui.getRectangle() != null) {

                    for (Well well : plateCondition.getWellList()) {
                        //check for coordinates
                        if (well.getColumnNumber() == wellGui.getColumnNumber() && well.getRowNumber() == wellGui.getRowNumber()) {
                            //set collection of wellHasImagingType to the well of the plateCondition
                            well.setWellHasImagingTypeList(wellGui.getWell().getWellHasImagingTypeList());

                            //the other way around: set the well for each wellHasImagingType
                            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                                wellHasImagingType.setWell(well);
                            }
                        }

                    } // what if the well was not imaged? data were not loaded for it...
                } else if (wellGui.getWell().getWellHasImagingTypeList().isEmpty() && wellGui.getRectangle() != null) {
                    for (Well well : plateCondition.getWellList()) {
                        //check for coordinates
                        if (well.getColumnNumber() == wellGui.getColumnNumber() && well.getRowNumber() == wellGui.getRowNumber()) {
                            //set List of wellHasImagingType to the well of the plateCondition
                            List<WellHasImagingType> list = new ArrayList<>();
                            well.setWellHasImagingTypeList(list);
                        }
                    }
                }
            }
        }
    }

    /**
     * this class extends a document listener on "Start" button
     */
    private static class ExperimentListener implements DocumentListener {

        private final List<Document> documentList = new ArrayList<>();
        private final JButton button;

        public ExperimentListener(JButton button) {
            this.button = button;
        }

        public void registerDoc(Document document) {
            documentList.add(document);
            document.addDocumentListener(this);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update();
        }

        //for each document check the lenght, when it's not zero enable the button
        private void update() {
            for (Document document : documentList) {
                if (document.getLength() == 0) {
                    button.setEnabled(false);
                    return;
                }
            }
            button.setEnabled(true);
        }
    }
}
