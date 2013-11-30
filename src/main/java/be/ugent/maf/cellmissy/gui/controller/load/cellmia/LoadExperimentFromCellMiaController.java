/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.cellmia;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.load.cellmia.LoadFromCellMiaPanel;
import be.ugent.maf.cellmissy.gui.experiment.load.cellmia.LoadFromCellMiaPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * Parent Controller: CellMissy Controller (main controller) Child Controllers:
 * Imaged Plate Controller, Experiment Metadata Controller
 *
 * @author Paola Masuzzo
 */
@Controller("loadExperimentFromCellMiaController")
public class LoadExperimentFromCellMiaController {

    private static final Logger LOG = Logger.getLogger(LoadExperimentFromCellMiaController.class);
    //model
    private Experiment experiment;
    private boolean dataLoadingHasBeenSaved;
    //view
    private LoadFromCellMiaPanel loadFromCellMiaPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private CellMiaImagedPlateController cellMiaImagedPlateController;
    @Autowired
    private CellMiaExperimentDataController cellMiaExperimentDataController;
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ObsepFileParser obsepFileParser;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init main view
        loadFromCellMiaPanel = new LoadFromCellMiaPanel();
        dataLoadingHasBeenSaved = false;
        //init child controllers
        cellMiaImagedPlateController.init();
        cellMiaExperimentDataController.init();
        //init main view
        initMainPanel();
    }

    /*
     * getters and setters
     */
    public LoadFromCellMiaPanel getLoadFromCellMiaPanel() {
        return loadFromCellMiaPanel;
    }

    public LoadFromCellMiaPlatePanel getLoadFromCellMiaPlatePanel() {
        return cellMiaImagedPlateController.getLoadFromCellMiaPlatePanel();
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public void updateInfoLabel(JLabel label, String message) {
        cellMissyController.updateInfoLabel(label, message);
    }

    public void setCursor(Cursor cursor) {
        cellMissyController.setCursor(cursor);
    }

    public void showMessage(String message, String title, Integer messageType) {
        cellMissyController.showMessage(message, title, messageType);
    }

    public void handleUnexpectedError(Exception ex) {
        cellMissyController.handleUnexpectedError(ex);
    }

    public int showConfirmDialog(String message, String title, Integer optionType) {
        return JOptionPane.showConfirmDialog(cellMissyController.getCellMissyFrame(), message, title, optionType);
    }

    public ImagedPlatePanel getImagedPlatePanel() {
        return cellMiaImagedPlateController.getImagedPlatePanel();
    }

    public User getCurrentUser() {
        return cellMissyController.getCurrentUser();
    }
    
    public void setExpListRenderer(User currentUser) {
        cellMiaExperimentDataController.setExpListRenderer(currentUser);
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
     * Called in the main controller, reset views and models if another view has
     * being shown
     */
    public void resetAfterCardSwitch() {
        experiment = null;
        dataLoadingHasBeenSaved = false;
        // clear selection on project list
        cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel().getProjectsList().clearSelection();
        if (cellMiaExperimentDataController.getExperimentBindingList() != null) {
            cellMiaExperimentDataController.getExperimentBindingList().clear();
        }
        cellMiaExperimentDataController.resetAfterUserInteraction();
        loadFromCellMiaPanel.getInfolabel().setForeground(Color.black);
        // swap views
        GuiUtils.switchChildPanels(loadFromCellMiaPanel.getTopPanel(), cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel(), cellMiaImagedPlateController.getLoadFromCellMiaPlatePanel());
        cellMissyController.updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Select a project and then an experiment in progress to load CELLMIA data.");
    }

    /*
     * private methods and classes
     */
    /**
     * Initializes the loading data panel
     */
    private void initMainPanel() {
        cellMiaImagedPlateController.getLoadFromCellMiaPlatePanel().getPurposeTextArea().setLineWrap(true);
        cellMiaImagedPlateController.getLoadFromCellMiaPlatePanel().getPurposeTextArea().setWrapStyleWord(true);
        //disable buttons
        loadFromCellMiaPanel.getFinishButton().setEnabled(false);
        loadFromCellMiaPanel.getExpDataButton().setEnabled(false);
        loadFromCellMiaPanel.getForwardButton().setEnabled(false);
        loadFromCellMiaPanel.getCancelButton().setEnabled(false);
        loadFromCellMiaPanel.getStartButton().setEnabled(false);
        //hide progress bar
        loadFromCellMiaPanel.getSaveDataProgressBar().setVisible(false);

        //update info message
        cellMissyController.updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Select a project and then an experiment in progress to load CELLMIA data.");

        /**
         * add action listeners
         */
        /**
         * Parse .obsep file from microscope directory
         */
        loadFromCellMiaPanel.getExpDataButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if obsep file was not automatically detectable, let the user choose a file to parse
                if (experiment.getObsepFile() != null) {
                    File obsepFile = experiment.getObsepFile();
                    setExperimentMetadata(obsepFile);
                    loadFromCellMiaPanel.getExpDataButton().setEnabled(false);
                    // enable start button: swap views
                    loadFromCellMiaPanel.getStartButton().setEnabled(true);
                    cellMissyController.updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Click on Start to start with the loading.");
                } else {
                    cellMissyController.showMessage("No valid microscope file was found or different files were found.\nPlease select a file.", ".obsep file not valid", JOptionPane.WARNING_MESSAGE);
                    //choose file to parse form microscope folder
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
                            if (extension.equals("obsep")) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        @Override
                        public String getDescription() {
                            return ("obsep (Olympus CellR/APL) files only");
                        }
                    });
                    // Removing "All Files" option from FileType
                    fileChooser.setAcceptAllFileFilterUsed(false);

                    // in response to the button click, show open dialog
                    int returnVal = fileChooser.showOpenDialog(cellMissyController.getCellMissyFrame());
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File obsepFile = fileChooser.getSelectedFile();
                        // set file for current experiment
                        experiment.setObsepFile(obsepFile);
                        // set experiment metadata
                        setExperimentMetadata(obsepFile);
                        loadFromCellMiaPanel.getExpDataButton().setEnabled(false);
                        // enable start button: swap views
                        loadFromCellMiaPanel.getStartButton().setEnabled(true);
                        cellMissyController.updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Click on Start to start with the loading.");
                    } else {
                        cellMissyController.showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        /**
         * Start the loading: Swap views and enable the Forward button
         */
        loadFromCellMiaPanel.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellMissyController.updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Click on Forward to process imaging data for the experiment.");
                LoadFromCellMiaPlatePanel loadFromCellMiaPlatePanel = cellMiaImagedPlateController.getLoadFromCellMiaPlatePanel(); // update labels with experiment metadata
                loadFromCellMiaPlatePanel.getProjNumberLabel().setText(experiment.getProject().toString());
                loadFromCellMiaPlatePanel.getExpNumberLabel().setText(experiment.toString());
                loadFromCellMiaPlatePanel.getPurposeTextArea().setText(experiment.getPurpose());
                GuiUtils.switchChildPanels(loadFromCellMiaPanel.getTopPanel(), loadFromCellMiaPlatePanel, cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel());
                loadFromCellMiaPanel.getTopPanel().repaint();
                loadFromCellMiaPanel.getForwardButton().setEnabled(true);
                loadFromCellMiaPanel.getStartButton().setEnabled(false);
            }
        });

        /**
         * Cancel the selection: reset Plate View
         */
        loadFromCellMiaPanel.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (WellGui wellGui : cellMiaImagedPlateController.getImagedPlatePanel().getWellGuiList()) {

                    //empty the collection of WellHasImagingType (so color is set to default)
                    wellGui.getWell().getWellHasImagingTypeList().clear();

                    //remove smaller ellipsi
                    List<Ellipse2D> ellipse2DList = new ArrayList<>();
                    for (Ellipse2D ellipse2D : wellGui.getEllipsi()) {
                        if (wellGui.getEllipsi().indexOf(ellipse2D) > 0) {
                            ellipse2DList.add(ellipse2D);
                        }
                    }
                    wellGui.getEllipsi().removeAll(ellipse2DList);
                    cellMiaImagedPlateController.getImagedPlatePanel().repaint();
                }
                //update info message (the user needs to click again on forward)
                updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Click again on Forward to process imaging data.");
                //set boolean isFirtTime to false
                cellMiaImagedPlateController.setIsFirtTime(false);
                //disable and enable buttons
                loadFromCellMiaPanel.getFinishButton().setEnabled(false);
                loadFromCellMiaPanel.getForwardButton().setEnabled(true);
                loadFromCellMiaPanel.getCancelButton().setEnabled(false);
            }
        });

        /**
         * Once data have been loaded, save the experiment with a swing worker
         */
        loadFromCellMiaPanel.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //set CellMia Data
                setCellMiaData();
                //set experiment status to "performed" and update it to DB
                experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                //launch a swing worker to update the experiment in the background thread
                SaveExpSwingWorker saveExpSwingWorker = new SaveExpSwingWorker();
                saveExpSwingWorker.execute();
            }
        });
        // add main view to top panel
        loadFromCellMiaPanel.getTopPanel().add(cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel(), gridBagConstraints);
        cellMissyController.getCellMissyFrame().getLoadFromCellMiaParentPanel().add(loadFromCellMiaPanel, gridBagConstraints);
    }

    /**
     * Set experiment metadata parsing the obsep file from microscope
     *
     * @param obsepFile: this is loaded from the experiment or it is rather
     * chosen by the user
     */
    private void setExperimentMetadata(File obsepFile) {
        // parse .obsep file
        obsepFileParser.parseObsepFile(obsepFile);
        // get experiment metadata
        List<Double> experimentMetadata = obsepFileParser.getExperimentMetadata();
        // set JtextFields
        cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel().getTimeFramesTextField().setText(experimentMetadata.get(0).toString());
        cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel().getIntervalTextField().setText(experimentMetadata.get(1).toString());
        cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel().getIntervalUnitComboBox().setSelectedItem(obsepFileParser.getUnit());
        cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel().getDurationTextField().setText(experimentMetadata.get(2).toString());
        // set experiment fields
        experiment.setTimeFrames(experimentMetadata.get(0).intValue());
        experiment.setExperimentInterval(experimentMetadata.get(1));
        experiment.setDuration(experimentMetadata.get(2));
        cellMiaExperimentDataController.getLoadFromCellMiaMetadataPanel().getIntervalUnitComboBox().setSelectedItem(obsepFileParser.getUnit());
    }

    /**
     * This method sets CellMIA data of wells, before the experiment is saved to
     * DB
     */
    private void setCellMiaData() {
        // iterate through conditions
        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            for (WellGui wellGui : cellMiaImagedPlateController.getImagedPlatePanel().getWellGuiList()) {

                //if the wellGui has a well with an empty collection of wellHasImagingTypes, the well has not been imaged: in this case, an empty collection is passed
                //if the wellGui has a rectangle, the well belongs to a certain condition
                if (wellGui.getRectangle() != null) {
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
                    }
                }
            }
        }
    }

    /**
     * Swing Worker to update the Experiment
     */
    private class SaveExpSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            //disable the Finish and the Cancel buttons
            loadFromCellMiaPanel.getFinishButton().setEnabled(false);
            loadFromCellMiaPanel.getCancelButton().setEnabled(false);
            //show progress bar (indeterminate)
            loadFromCellMiaPanel.getSaveDataProgressBar().setVisible(true);
            loadFromCellMiaPanel.getSaveDataProgressBar().setIndeterminate(true);
            // update message
            updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Please wait, data is being saved.");
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
                loadFromCellMiaPanel.getSaveDataProgressBar().setVisible(false);
                LOG.debug("Experiment was saved.");
                //update info for the user
                showMessage("Experiment was successfully saved to DB.\nPlease choose what you want to do next.", "Experiment saved", JOptionPane.INFORMATION_MESSAGE);
                updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Experiment was successfully saved to DB.");
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                handleUnexpectedError(ex);
            }
        }
    }
}
