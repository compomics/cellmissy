/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.*;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.project.TracksWriterDialog;
import be.ugent.maf.cellmissy.gui.view.MapDataTreeModel;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of writing tracks-derived data to file.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("tracksWriterController")
class TracksWriterController {

    private static final Logger LOG = Logger.getLogger(TracksWriterController.class);
    // model
    private LinkedHashMap<Project, List<Experiment>> projectMap;
    private File directory;
    private LinkedHashMap<Experiment, List<List<TrackDataHolder>>> computationsMap;
    // view
    private TracksWriterDialog tracksWriterDialog;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    // child controllers
    // services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private WellService wellService;
    @Autowired
    private SingleCellPreProcessor singleCellPreProcessor;

    /**
     * Initialize controller
     */
    public void init() {
        projectMap = new LinkedHashMap<>();
        computationsMap = new LinkedHashMap<>();
        // initialize main view component
        tracksWriterDialog = new TracksWriterDialog(cellMissyController.getCellMissyFrame(), false);
        initTracksWriterDialog();
    }

    /**
     * Show the dialog in the main controller.
     */
    public void showTracksWriterDialog() {
        tracksWriterDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), tracksWriterDialog);
        tracksWriterDialog.setVisible(true);
    }

    /**
     * Initialize dialog
     */
    private void initTracksWriterDialog() {
        // find and sort all projects
        List<Project> allProjects = projectService.findAll();
        Collections.sort(allProjects);
        // for each project, get the experiments performed, if any
        for (Project project : allProjects) {
            List<Experiment> experiments = experimentService.findExperimentsByProjectIdAndStatus(project.getProjectid(), ExperimentStatus.PERFORMED);
            projectMap.put(project, experiments);
        }
        // initialise the data tree (set some of its properties, etc.)
        initDataTree();

        /**
         * On selecting all the projects.
         */
        tracksWriterDialog.getSelectAllButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        /**
         * On deselecting all the projects.
         */
        tracksWriterDialog.getDeselectAllButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        /**
         * Choose directory to create data files.
         */
        tracksWriterDialog.getChooseDirectoryButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory();
            }
        });

        /**
         * Retrieve the selected experiments, and do the computations for them.
         */
        tracksWriterDialog.getComputeAndWriteButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                initComputationsMap();
                computeAndWriteToFile();
            }
        });
    }

    /**
     * Initialise the data tree (JTree).
     */
    private void initDataTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("cellmissy");
        JTree dataTree = tracksWriterDialog.getDataTree();
        MapDataTreeModel mapDataTreeModel = new MapDataTreeModel(root, projectMap);
        dataTree.setModel(mapDataTreeModel);
        dataTree.setEditable(true);
    }

    /**
     * Choose Directory
     */
    private void chooseDirectory() {
        // Open a JFile Chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory to save the files");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        // in response to the button click, show open dialog
        int returnVal = fileChooser.showOpenDialog(tracksWriterDialog);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            directory = fileChooser.getSelectedFile();
        }
        tracksWriterDialog.getDirectoryTextField().setText(directory.getAbsolutePath());
    }

    /**
     * Write data for a specific experiment.
     *
     * @param experiment
     */
    private void writeDataForExperiment(Experiment experiment) {
        directory = new File("E:\\data_14022015");
        // get the data out of the map
        List<List<TrackDataHolder>> list = computationsMap.get(experiment);
        // name for the file
        String fileName = "cellmissy" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + "_" + experiment + " [" + experiment.getProject() + "]" + ".csv";
        appendInfo("writing file: " + fileName + " to directory: " + directory);
        // use a BufferedWriter
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, fileName)))) {

            String header = "projid" + " expid" + " condid" + " wellid" + " trackid" + " length" + " xmin" + " xmax" + " ymin" + " ymax" + " xnd" + " ynd" + " cd" + " ed" + " diratio" + " medir" + " medspeed" + " medta" + " maxdisp" + " dr" + " or" + " perim" + " area" + " acirc" + " dir" + " v";
            bufferedWriter.append(header);
            bufferedWriter.newLine();
            for (List<TrackDataHolder> trackDataHolders : list) {

                // iterate through the data
                for (TrackDataHolder trackDataHolder : trackDataHolders) {
                    CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
                    Track track = trackDataHolder.getTrack();
                    Well well = track.getWellHasImagingType().getWell();
                    PlateCondition condition = well.getPlateCondition();

                    String data = experiment.getProject().getProjectid() + " " + experiment.getExperimentid() + " " + condition.getPlateConditionid() + " " + well.getWellid() + " " + track.getTrackid() + " " + track.getTrackPointList().size() + " " + cellCentricDataHolder.getxMin() + " " + cellCentricDataHolder.getxMax() + " " + cellCentricDataHolder.getyMin() + " " + cellCentricDataHolder.getyMax() + " " + cellCentricDataHolder.getxNetDisplacement() + " " + cellCentricDataHolder.getyNetDisplacement() + " " + cellCentricDataHolder.getCumulativeDistance() + " " + cellCentricDataHolder.getEuclideanDistance() + " " + cellCentricDataHolder.getEndPointDirectionalityRatio() + " " + cellCentricDataHolder.getMedianDirectionalityRatio() + " " + cellCentricDataHolder.getMedianSpeed() + " " + cellCentricDataHolder.getMedianTurningAngle() + " " + cellCentricDataHolder.getConvexHull().getMostDistantPointsPair().getMaxSpan() + " " + cellCentricDataHolder.getDisplacementRatio() + " " + cellCentricDataHolder.getOutreachRatio() + " " + cellCentricDataHolder.getConvexHull().getPerimeter() + " " + cellCentricDataHolder.getConvexHull().getArea() + " " + cellCentricDataHolder.getConvexHull().getAcircularity() + " " + cellCentricDataHolder.getConvexHull().getDirectionality() + " " + cellCentricDataHolder.getConvexHull().getHullSize();

                    bufferedWriter.append(data);
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            appendInfo("ERROR WRITING DATA TO FILE !!!");
        }
    }

    /**
     * Get the selected List of Experiments, to start the computations on.
     *
     * @return
     */
    private void initComputationsMap() {
        for (Project project : projectMap.keySet()) {
            if (project.getProjectid() == 1L) { // ************************
                List<Experiment> exps = projectMap.get(project);
                if (exps != null && !exps.isEmpty()) {
                    for (Experiment exp : exps) {
                        computationsMap.put(exp, new ArrayList());
                    }
                }
            }
        }
    }

    /**
     *
     */
    private class ComputationsSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            Iterator<Experiment> iterator = computationsMap.keySet().iterator();
            while (iterator.hasNext()) {
                List<List<TrackDataHolder>> list = new ArrayList();
                Experiment experiment = iterator.next();
                String info = "Starting querying data from EXPERIMENT: " + experiment + " [" + experiment.getProject() + "]";
                appendInfo(info);
                // compute the conversion factor
                double conversionFactor = experiment.getInstrument().getConversionFactor() * experiment.getMagnification().getMagnificationValue() / 10;
                // fetch the migration data
                for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                    info = "Starting querying data from CONDITION: " + plateCondition;
                    appendInfo(info);
                    List<Well> wells = new ArrayList<>();
                    for (Well well : plateCondition.getWellList()) {
                        Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
                        wells.add(fetchedWell);
                        info = "Starting querying data from WELL: " + fetchedWell;
                        appendInfo(info);
                    }
                    plateCondition.setWellList(wells);
                    info = "Data fully queried for CONDITION: " + plateCondition;
                    appendInfo(info);

                    info = "... COMPUTING stuff now ...";
                    appendInfo(info);
                    // now do the computations
                    int totTracks = 0;
                    info = "Starting computations for CONDITION: " + plateCondition;
                    appendInfo(info);
                    // create a new object to hold pre-processing results
                    SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
                    // do the computations
                    singleCellPreProcessor.generateTrackDataHolders(singleCellPreProcessingResults, plateCondition, conversionFactor, experiment.getExperimentInterval());
                    info = "track data holders generated...";
                    appendInfo(info);
                    List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
                    if (!trackDataHolders.isEmpty()) {
                        singleCellPreProcessor.generateDataStructure(singleCellPreProcessingResults);
                        info = "data structure generated ...\n*****";
                        appendInfo(info);
                        singleCellPreProcessor.operateOnStepsAndCells(singleCellPreProcessingResults);
                        info = "step-centric and cell-centric operations performed ...";
                        appendInfo(info);
                        singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellPreProcessingResults);
                        info = "tracks coordinates computed...";
                        appendInfo(info);
                        singleCellPreProcessor.generateInstantaneousDisplacementsVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateDirectionalityRatiosVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateMedianDirectionalityRatiosVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateTrackDisplacementsVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateCumulativeDistancesVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateEuclideanDistancesVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateTrackSpeedsVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateEndPointDirectionalityRatiosVector(singleCellPreProcessingResults);
                        info = "displacements, speeds and directionalities computed...";
                        appendInfo(info);
                        singleCellPreProcessor.generateConvexHullsVector(singleCellPreProcessingResults);
                        info = "convex hulls computed...";
                        appendInfo(info);
                        singleCellPreProcessor.generateDisplacementRatiosVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateOutreachRatiosVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateTurningAnglesVector(singleCellPreProcessingResults);
                        singleCellPreProcessor.generateMedianTurningAnglesVector(singleCellPreProcessingResults);
                        info = "angles data computed...";
                        appendInfo(info);
                        totTracks += trackDataHolders.size();
                        list.add(trackDataHolders);
                        appendInfo("$$$ nr. tracks for CONDITION: " + trackDataHolders.size());
                        appendInfo("*-*-* CONDITION " + plateCondition + " processed!");
                        appendInfo("TOTAL nr. of cell tracks: " + totTracks);
                    } else {
                        tracksWriterDialog.getLogTextArea().setForeground(Color.red);
                        info = "No Tracks... skipping computations, moving to next!";
                        appendInfo(info);
                        tracksWriterDialog.getLogTextArea().setForeground(Color.black);
                    }
                }
                computationsMap.get(experiment).addAll(list);
                appendInfo("WRITING data for: " + experiment);
                writeDataForExperiment(experiment);
                iterator.remove();
            }
            appendInfo("DONE !!!");
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Append the info in the log text area.
     *
     * @param info
     */
    private void appendInfo(String info) {
        String newLine = JFreeChartUtils.getNewLine();
        tracksWriterDialog.getLogTextArea().append(info + newLine);
        tracksWriterDialog.getLogTextArea().setCaretPosition(tracksWriterDialog.getLogTextArea().getDocument().getLength());
    }

    /**
     * For a specific list of selected experiments, compute the track data. This
     * basically calls a SwingWorker and execute it. For each experiment, write
     * the data to a single file in the chosen directory.
     */
    private void computeAndWriteToFile() {
        ComputationsSwingWorker computationsSwingWorker = new ComputationsSwingWorker();
        computationsSwingWorker.execute();
    }
}
