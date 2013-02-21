/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.cellmia;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.load.cellmia.LoadFromCellMiaPanel;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.service.ExperimentService;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
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
 * Parent Controller: CellMissy Controller (main controller) Child Controllers: Imaged Plate Controller, Experiment Metadata Controller
 *
 * @author Paola Masuzzo
 */
@Controller("loadExperimentFromCellMiaController")
public class LoadExperimentFromCellMiaController {

    private static final Logger LOG = Logger.getLogger(LoadExperimentFromCellMiaController.class);
    //model
    private Experiment experiment;
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

    /**
     * initialize controller
     */
    public void init() {
        // init main view
        loadFromCellMiaPanel = new LoadFromCellMiaPanel();
        //init main view
        initMainPanel();
        //init child controllers
        cellMiaImagedPlateController.init();
        cellMiaExperimentDataController.init();
    }

    /*
     * getters and setters
     */
    public LoadFromCellMiaPanel getLoadFromCellMiaPanel() {
        return loadFromCellMiaPanel;
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

    public void showMessage(String message, Integer messageType) {
        cellMissyController.showMessage(message, messageType);
    }

    public int showConfirmDialog(String message, String title, Integer optionType) {
        return JOptionPane.showConfirmDialog(cellMissyController.getCellMissyFrame(), message, title, optionType);
    }

    public ImagedPlatePanel getImagedPlatePanel() {
        return cellMiaImagedPlateController.getImagedPlatePanel();
    }

    /*
     * private methods and classes
     */
    /**
     * initializes the loading data panel
     */
    private void initMainPanel() {

        //disable buttons
        loadFromCellMiaPanel.getFinishButton().setEnabled(false);
        loadFromCellMiaPanel.getExpDataButton().setEnabled(false);
        loadFromCellMiaPanel.getForwardButton().setEnabled(false);
        loadFromCellMiaPanel.getCancelButton().setEnabled(false);
        //hide progress bar
        loadFromCellMiaPanel.getSaveDataProgressBar().setVisible(false);

        //update info message
        cellMissyController.updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Select a project and then an experiment in progress to load CELLMIA data.");

        /**
         * add action listeners
         */
        //parse obsep file from the microscope
        loadFromCellMiaPanel.getExpDataButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (experiment.getObsepFile() != null) {
                    File obsepFile = experiment.getObsepFile();
                    setExperimentMetadata(obsepFile);
                } else {
                    cellMissyController.showMessage("No valid microscope file was found or different files were found.\nPlease select a file.", JOptionPane.WARNING_MESSAGE);
                    //choose file to parse form microscope folder
                    JFileChooser chooseObsepFile = new JFileChooser();
                    chooseObsepFile.setFileFilter(new FileFilter() {
                        // to select only (.obsep) files
                        @Override
                        public boolean accept(File f) {
                            return f.getName().toLowerCase(Locale.ENGLISH).endsWith(".obsep");
                        }

                        @Override
                        public String getDescription() {
                            return ("(.obsep)");
                        }
                    });

                    // in response to the button click, show open dialog
                    int returnVal = chooseObsepFile.showOpenDialog(cellMissyController.getCellMissyFrame());
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File obsepFile = chooseObsepFile.getSelectedFile();
                        // set file for current experiment
                        experiment.setObsepFile(obsepFile);
                        // set experiment metadata
                        setExperimentMetadata(obsepFile);
                    } else {
                        cellMissyController.showMessage("Open command cancelled by user", 1);
                    }
                }
                cellMissyController.updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Click <<Forward>> to process imaging data for the experiment.");
                loadFromCellMiaPanel.getForwardButton().setEnabled(true);
                loadFromCellMiaPanel.getExpDataButton().setEnabled(false);
            }
        });

        //cancel the selection: reset Plate View
        loadFromCellMiaPanel.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (WellGui wellGui : cellMiaImagedPlateController.getImagedPlatePanel().getWellGuiList()) {

                    //empty the collection of WellHasImagingType (so color is set to default)
                    wellGui.getWell().getWellHasImagingTypeCollection().clear();

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
                updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Click again <<Forward>> to process imaging data.");
                //set boolean isFirtTime to false
                cellMiaImagedPlateController.setIsFirtTime(false);
                //disable and enable buttons
                loadFromCellMiaPanel.getFinishButton().setEnabled(false);
                loadFromCellMiaPanel.getForwardButton().setEnabled(true);
                loadFromCellMiaPanel.getCancelButton().setEnabled(false);
            }
        });

        //save the experiment once all data have been loaded
        loadFromCellMiaPanel.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //set CellMia Data
                setCellMiaData();
                //set experiment status to "performed" and update it to DB
                experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                //launch a swing worker to update the experiment in the background thread
                SaveExperimentWorker worker = new SaveExperimentWorker();
                worker.execute();
            }
        });
    }

    /**
     * Set experiment metadata parsing the obsep file from microscope
     *
     * @param obsepFile: this is loaded from the experiment or it is rather chosen by the user
     */
    private void setExperimentMetadata(File obsepFile) {
        // parse .obsep file 
        obsepFileParser.parseObsepFile(obsepFile);
        // get experiment metadata
        List<Double> experimentMetadata = obsepFileParser.getExperimentMetadata();
        // set JtextFields
        cellMiaExperimentDataController.getExperimentMetadataPanel().getTimeFramesTextField().setText(experimentMetadata.get(0).toString());
        cellMiaExperimentDataController.getExperimentMetadataPanel().getIntervalTextField().setText(experimentMetadata.get(1).toString());
        cellMiaExperimentDataController.getExperimentMetadataPanel().getIntervalUnitComboBox().setSelectedItem(obsepFileParser.getUnit());
        cellMiaExperimentDataController.getExperimentMetadataPanel().getDurationTextField().setText(experimentMetadata.get(2).toString());
        // set experiment fields
        experiment.setTimeFrames(experimentMetadata.get(0).intValue());
        experiment.setExperimentInterval(experimentMetadata.get(1));
        experiment.setDuration(experimentMetadata.get(2));
        cellMiaExperimentDataController.getExperimentMetadataPanel().getIntervalUnitComboBox().setSelectedItem(obsepFileParser.getUnit());
    }

    /**
     * This method sets CellMIA data of wells, before the experiment is saved to DB
     */
    private void setCellMiaData() {
        // iterate through conditions
        for (PlateCondition plateCondition : experiment.getPlateConditionCollection()) {
            for (WellGui wellGui : cellMiaImagedPlateController.getImagedPlatePanel().getWellGuiList()) {

                //if the wellGui has a well with an empty collection of wellHasImagingTypes, the well has not been imaged: in this case, an empty collection is passed
                //if the wellGui has a rectangle, the well belongs to a certain condition
                if (wellGui.getRectangle() != null) {
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

    /**
     * Swing Worker to update the Experiment
     */
    private class SaveExperimentWorker extends SwingWorker<Void, Void> {

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
            experimentService.saveMotilityDataForExperiment(experiment);
            // update experiment
            experiment = experimentService.update(experiment);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                //show back default cursor and hide the progress bar
                cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                loadFromCellMiaPanel.getSaveDataProgressBar().setVisible(false);
                LOG.debug("Experiment was saved.");
                //update info for the user
                showMessage("Experiment was successfully saved to DB.", JOptionPane.INFORMATION_MESSAGE);
                updateInfoLabel(loadFromCellMiaPanel.getInfolabel(), "Experiment was successfully saved to DB.");
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ExecutionException ex) {
                showMessage("An expected error occured: " + ex.getMessage() + ", please try to restart the application.", JOptionPane.ERROR_MESSAGE);
            } catch (CancellationException ex) {
                LOG.info("Loading data was cancelled.");
            }
        }
    }
}
