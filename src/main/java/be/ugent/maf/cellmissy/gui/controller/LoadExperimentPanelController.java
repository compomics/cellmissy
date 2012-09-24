/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.experiment.LoadExperimentPanel;
import be.ugent.maf.cellmissy.gui.plate.LoadDataPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.service.ExperimentService;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("loadExperimentPanelController")
public class LoadExperimentPanelController {

    //model
    private Experiment experiment;
    //view
    private LoadExperimentPanel loadExperimentPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private LoadDataPlatePanelController loadDataPlatePanelController;
    @Autowired
    private LoadExperimentInfoPanelController loadExperimentInfoPanelController;
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ObsepFileParser obsepFileParser;

    /**
     * initialize controller 
     */
    public void init() {
        //create main panel
        loadExperimentPanel = new LoadExperimentPanel();
        //init main view
        initMainPanel();
        //init child controller
        loadDataPlatePanelController.init();
        loadExperimentInfoPanelController.init();
    }

    /*
     * getters and setters
     */
    public LoadExperimentPanel getLoadExperimentPanel() {
        return loadExperimentPanel;
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
        return JOptionPane.showConfirmDialog(cellMissyController.cellMissyFrame, message, title, optionType);
    }

    public void initPlatePanel(PlateFormat plateFormat, Dimension dimension) {
        loadDataPlatePanelController.getLoadDataPlatePanel().initPanel(plateFormat, dimension);
    }

    public LoadDataPlatePanel getLoadDataPlatePanel() {
        return loadDataPlatePanelController.getLoadDataPlatePanel();
    }

    /*
     * private methods and classes
     */
    /**
     * initializes the loading data panel
     */
    private void initMainPanel() {

        //disable buttons
        loadExperimentPanel.getFinishButton().setEnabled(false);
        loadExperimentPanel.getExpDataButton().setEnabled(false);
        loadExperimentPanel.getForwardButton().setEnabled(false);
        loadExperimentPanel.getCancelButton().setEnabled(false);
        //hide progress bar
        loadExperimentPanel.getjProgressBar1().setVisible(false);

        //update info message
        cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Select a project and then an experiment in progress to load CELLMIA data.");

        /**
         * add action listeners
         */
        //parse obseo file from the microscope
        loadExperimentPanel.getExpDataButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (experiment.getObsepFile() != null) {
                    File obsepFile = experiment.getObsepFile();
                    setExperimentData(obsepFile);
                } else {
                    cellMissyController.showMessage("No valid microscope file was found or different files were found.\nPlease select a file.", 0);
                    //choose file to parse form microscope folder
                    JFileChooser chooseObsepFile = new JFileChooser();
                    chooseObsepFile.setFileFilter(new FileFilter() {

                        // to select only (.obsep) files
                        @Override
                        public boolean accept(File f) {
                            return f.getName().toLowerCase().endsWith(".obsep");
                        }

                        @Override
                        public String getDescription() {
                            return ("(.obsep)");
                        }
                    });

                    // in response to the button click, show open dialog
                    int returnVal = chooseObsepFile.showOpenDialog(cellMissyController.cellMissyFrame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File obsepFile = chooseObsepFile.getSelectedFile();
                        setExperimentData(obsepFile);
                    } else {
                        cellMissyController.showMessage("Open command cancelled by user", 1);
                    }
                }
                cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Click <<Forward>> to process imaging data for the experiment.");
                loadExperimentPanel.getForwardButton().setEnabled(true);
                loadExperimentPanel.getExpDataButton().setEnabled(false);
            }
        });

        //cancel the selection: reset Plate View
        loadExperimentPanel.getCancelButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (WellGui wellGui : loadDataPlatePanelController.getLoadDataPlatePanel().getWellGuiList()) {

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
                    loadDataPlatePanelController.getLoadDataPlatePanel().repaint();
                }
                //update info message (the user needs to click again on forward)
                updateInfoLabel(loadExperimentPanel.getInfolabel(), "Click again <<Forward>> to process imaging data.");
                //set boolean isFirtTime to false
                loadDataPlatePanelController.setIsFirtTime(false);
                //disable and enable buttons
                loadExperimentPanel.getFinishButton().setEnabled(false);
                loadExperimentPanel.getForwardButton().setEnabled(true);
                loadExperimentPanel.getCancelButton().setEnabled(false);
            }
        });

        //save the experiment once all data have been loaded
        loadExperimentPanel.getFinishButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //set CellMia Data
                setCellMiaData();
                //set experiment status to "performed" and save it to DB
                experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                //launch a swing worker to save the experiment in the background thread
                SaveExperimentWorker worker = new SaveExperimentWorker();
                worker.execute();
            }
        });
    }

    /**
     * set experiment data parsing the obsep file from microscope
     * @param obsepFile: this is loaded from the experiment or it is rather chosen by the user
     */
    private void setExperimentData(File obsepFile) {
        obsepFileParser.parseObsepFile(obsepFile);
        List<Double> experimentInfo = obsepFileParser.getExperimentInfo();
        loadExperimentInfoPanelController.getLoadExperimentInfoPanel().getTimeFramesTextField().setText(experimentInfo.get(0).toString());
        loadExperimentInfoPanelController.getLoadExperimentInfoPanel().getIntervalTextField().setText(experimentInfo.get(1).toString());
        loadExperimentInfoPanelController.getLoadExperimentInfoPanel().getUnitLabel().setText(obsepFileParser.getUnit().name().toLowerCase());
        loadExperimentInfoPanelController.getLoadExperimentInfoPanel().getDurationTextField().setText(experimentInfo.get(2).toString());
    }

    /**
     * this method sets Migration data of wells, before the experiment is saved to DB
     */
    private void setCellMiaData() {

        for (PlateCondition plateCondition : experiment.getPlateConditionCollection()) {
            for (WellGui wellGui : loadDataPlatePanelController.getLoadDataPlatePanel().getWellGuiList()) {

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

    /**
     * Swing Worker to save the Experiment
     */
    private class SaveExperimentWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            //disable the Finish button and show a waiting cursor
            loadExperimentPanel.getFinishButton().setEnabled(false);
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //show a progress bar (indeterminate)
            loadExperimentPanel.getjProgressBar1().setVisible(true);
            loadExperimentPanel.getjProgressBar1().setIndeterminate(true);

            //INSERT experiment to DB
            experimentService.save(experiment);
            return null;
        }

        @Override
        protected void done() {

            //show back default cursor and hide the progress bar
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            loadExperimentPanel.getjProgressBar1().setVisible(false);
            //update info for the user
            showMessage("Experiment was successfully saved to DB.", 1);
            updateInfoLabel(loadExperimentPanel.getInfolabel(), "Experiment was successfully saved to DB.");
        }
    }
}
