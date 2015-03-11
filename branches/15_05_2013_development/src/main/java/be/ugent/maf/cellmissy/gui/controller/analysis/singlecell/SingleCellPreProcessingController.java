/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.TrackCoordinatesUnitOfMeasurement;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DisplacementsPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.text.Format;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for single cell pre processing
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("singleCellPreProcessingController")
class SingleCellPreProcessingController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger
            (SingleCellPreProcessingController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<Track> tracksBindingList;
    private ObservableList<TrackPoint> trackPointsBindingList;
    private Map<PlateCondition, SingleCellConditionDataHolder> preProcessingMap;
    private Double[][] experimentRawCoordinatesRanges;
    private Double[][] experimentShiftedCoordinatesRanges;
    // view
    private SingleCellAnalysisPanel singleCellAnalysisPanel;
    // parent controller
    @Autowired
    private SingleCellMainController singleCellMainController;
    // child controllers
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    @Autowired
    private DisplacementsController displacementsController;
    //services
    @Autowired
    private SingleCellPreProcessor singleCellPreProcessor;
    @Autowired
    private SingleCellOperator singleCellOperator;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        initSingleCellAnalysisPanel();
        // init child controllers
        trackCoordinatesController.init();
        displacementsController.init();
    }

    /**
     * getters
     */
    public SingleCellAnalysisPanel getSingleCellAnalysisPanel() {
        return singleCellAnalysisPanel;
    }

    public TrackCoordinatesPanel getTrackCoordinatesPanel() {
        return trackCoordinatesController.getTrackCoordinatesPanel();
    }

    public DisplacementsPanel getSpeedsPanel() {
        return displacementsController.getSpeedsPanel();
    }

    public ObservableList<Track> getTracksBindingList() {
        return tracksBindingList;
    }

    public ObservableList<TrackPoint> getTrackPointsBindingList() {
        return trackPointsBindingList;
    }

    public PlateCondition getCurrentCondition() {
        return singleCellMainController.getCurrentCondition();
    }

    public void setCurrentCondition(PlateCondition currentCondition) {
        singleCellMainController.setCurrentCondition(currentCondition);
    }

    public AnalysisPlatePanel getAnalysisPlatePanel() {
        return singleCellMainController.getAnalysisPlatePanel();
    }

    public PlateCondition getSelectedCondition() {
        return singleCellMainController.getSelectedCondition();
    }

    public Format getFormat() {
        return singleCellMainController.getFormat();
    }

    public SingleCellConditionDataHolder getConditionDataHolder(PlateCondition plateCondition) {
        return preProcessingMap.get(plateCondition);
    }

    public List<PlateCondition> getPlateConditionList() {
        return new ArrayList<>(preProcessingMap.keySet());
    }

    public void showRawTrackCoordinatesInTable(PlateCondition plateCondition) {
        trackCoordinatesController.showRawTrackCoordinatesInTable(plateCondition);
    }

    public void updateTracksNumberInfo(PlateCondition plateCondition) {
        trackCoordinatesController.updateTracksNumberInfo(plateCondition);
    }

    public void updateWellBindingList(PlateCondition plateCondition) {
        trackCoordinatesController.updateWellBindingList(plateCondition);
    }

    public void showShiftedTrackCoordinatesInTable(PlateCondition plateCondition) {
        trackCoordinatesController.showShiftedTrackCoordinatesInTable(plateCondition);
    }

    public void plotRandomTrackCoordinates(PlateCondition plateCondition, boolean useRawData) {
        trackCoordinatesController.plotRandomTrackCoordinates(plateCondition, useRawData);
    }

    public void showPlottedTracksInTable() {
        trackCoordinatesController.showPlottedTracksInTable();
    }

    public void showInstantaneousSpeedsInTable(PlateCondition plateCondition) {
        displacementsController.showInstantaneousDisplInTable(plateCondition);
    }

    public void showTrackDisplInTable(PlateCondition plateCondition) {
        displacementsController.showTrackDisplInTable(plateCondition);
    }

    public void showTrackSpeedsInTable(PlateCondition plateCondition) {
        displacementsController.showTrackSpeedsInTable(plateCondition);
    }

    CellMissyFrame getMainFrame() {
        return singleCellMainController.getCellMissyFrame();
    }

    public ObservableList<TrackDataHolder> getTrackDataHolderBindingList() {
        return trackCoordinatesController.getTrackDataHolderBindingList();
    }

    public Double[][] getExperimentRawCoordinatesRanges() {
        return experimentRawCoordinatesRanges;
    }

    public Double[][] getExperimentShiftedCoordinatesRanges() {
        return experimentShiftedCoordinatesRanges;
    }

    public void fetchTracks(PlateCondition plateCondition) {
        singleCellMainController.fetchTracks(plateCondition);
    }

    /**
     * Get the category to plot for the tracks: normally 0, the plateCondition
     * category; if the well radio button is selected, set the category to plot
     * to 1.
     *
     * @return
     */
    public int getCategoryToPlot() {
        return trackCoordinatesController.getCategoryToPlot();
    }

    /**
     * Handle Unexpected errors through the main controller
     *
     * @param exception
     */
    public void handleUnexpectedError(Exception exception) {
        singleCellMainController.handleUnexpectedError(exception);
    }

    public void controlGuiComponents(boolean enabled) {
        singleCellMainController.controlGuiComponents(enabled);
    }

    public JList getConditionsList() {
        return singleCellMainController.getDataAnalysisPanel().getConditionsList();
    }

    /**
     * Show message through the main controller
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        singleCellMainController.showMessage(message, title, messageType);
    }

    /**
     * Initialize map with plate conditions as keys and null objects as values
     */
    public void initMapWithConditions() {
        // if the map is not empty, clear it first!
        if (!preProcessingMap.keySet().isEmpty()) {
            preProcessingMap.clear();
        }
        for (PlateCondition plateCondition : singleCellMainController.getPlateConditionList()) {
            // each plateCondition is not loaded at the beginning
            plateCondition.setLoaded(false);
            preProcessingMap.put(plateCondition, null);
        }
    }

    /**
     * Operate on a specific plateCondition
     *
     * @param plateCondition
     */
    public void operateOnCondition(PlateCondition plateCondition) {
        ConditionOperatorSwingWorker conditionOperatorSwingWorker = new ConditionOperatorSwingWorker(plateCondition);
        conditionOperatorSwingWorker.execute();
    }

    /**
     * Set cursor from main controller
     */
    public void setCursor(Cursor cursor) {
        singleCellMainController.setCursor(cursor);
    }

    /**
     * Given a certain category (selected index in a tabbed pane) generate the
     * random track data holders.
     *
     * @param category:       can be 0 or 1
     * @param plateCondition: the plateCondition to generate the tracks from
     */
    public void generateRandomTrackDataHolders(int category, PlateCondition plateCondition) {
        trackCoordinatesController.generateRandomTrackDataHolders(category, plateCondition);
    }

    /**
     * This method pre-processes the entire experiment: data are retrieved from
     * DB for all the conditions, and basic computations are performed.
     */
    public void preProcessExperiment(Experiment experiment) {
        PreProcessExperimentSwingWorker preProcessExperimentSwingWorker = new PreProcessExperimentSwingWorker
                (experiment);
        preProcessExperimentSwingWorker.execute();
    }

    /**
     * When a plateCondition is selected pre processing results are computed and
     * plateCondition is put into the map together with its results holder object
     *
     * @param plateCondition
     */
    public void updateMapWithCondition(PlateCondition plateCondition) {
        // fetch the track points from DB
        singleCellMainController.fetchTrackPoints(plateCondition);
        if (preProcessingMap.get(plateCondition) == null) {
            // create a new object to hold pre-processing results
            SingleCellConditionDataHolder singleCellConditionDataHolder = new SingleCellConditionDataHolder();
            // do some pre-processing
            appendInfo("generating track data holders...");
            singleCellPreProcessor.generateTrackDataHolders(singleCellConditionDataHolder, plateCondition);
            appendInfo("--> current total number of cell tracks: " + singleCellConditionDataHolder
                    .getTrackDataHolders().size());
            // it can very well be that a plateCondition and/or a sample have been imaged, but there are no tracks in it
            // if this is not the case, go for the computation
            if (!singleCellConditionDataHolder.getTrackDataHolders().isEmpty()) {
                singleCellPreProcessor.generateDataStructure(singleCellConditionDataHolder);
                appendInfo("pre-process step-centric and cell-centric data...");
                singleCellPreProcessor.preProcessStepsAndCells(singleCellConditionDataHolder, singleCellMainController
                        .getConversionFactor(), singleCellMainController.getExperiment().getExperimentInterval());
                appendInfo("generating raw coordinates matrix...");
                singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellConditionDataHolder);
                appendInfo("computing shifted-to-zero coordinates matrix...");
                singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellConditionDataHolder);
                appendInfo("computing raw coordinates ranges...");
                singleCellPreProcessor.generateRawCoordinatesRanges(singleCellConditionDataHolder);
                appendInfo("computing shifted coordinates ranges...");
                singleCellPreProcessor.generateShiftedCoordinatesRanges(singleCellConditionDataHolder);
                // fill in the map
                preProcessingMap.put(plateCondition, singleCellConditionDataHolder);
                appendInfo("**************************");
                appendInfo("Plate plateCondition processed!");
            } else {
                // remove the plateCondition from the map and inform the user
                preProcessingMap.remove(plateCondition);
                appendInfo("No tracks recorded for plateCondition: " + plateCondition + "; computations skipped!");
            }
        }
    }

    /**
     * Show table with Tracking results from image analysis (Tracks fetched from
     * DB) this is populating the JTable in the ResultsImporter Panel
     */
    public void showTracksInTable() {
        // table binding
        JTableBinding tracksTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ,
                tracksBindingList, singleCellAnalysisPanel.getTracksTable());
        // add column bindings
        ColumnBinding columnBinding = tracksTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType"
                + ".well.columnNumber}"));
        columnBinding.setColumnName("Column");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = tracksTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.rowNumber}"));
        columnBinding.setColumnName("Row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = tracksTableBinding.addColumnBinding(ELProperty.create("${trackLength}"));
        columnBinding.setColumnName("Track Length");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = tracksTableBinding.addColumnBinding(ELProperty.create("${trackNumber}"));
        columnBinding.setColumnName("Track Number");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        bindingGroup.addBinding(tracksTableBinding);
        bindingGroup.bind();
    }

    /**
     * Append the info in the log text area.
     *
     * @param info
     */
    public void appendInfo(String info) {
        String newLine = JFreeChartUtils.getNewLine();
        singleCellAnalysisPanel.getLogTextArea().append(info + newLine);
        singleCellAnalysisPanel.getLogTextArea().setCaretPosition(singleCellAnalysisPanel.getLogTextArea()
                .getDocument().getLength());
    }

    /**
     * Initialize main panel
     */
    private void initSingleCellAnalysisPanel() {
        // make a new panel
        singleCellAnalysisPanel = new SingleCellAnalysisPanel();
        // init binding lists
        tracksBindingList = ObservableCollections.observableList(new ArrayList<Track>());
        trackPointsBindingList = ObservableCollections.observableList(new ArrayList<TrackPoint>());
        // set background of tables scroll panes to white
        singleCellAnalysisPanel.getTracksScrollPane().getViewport().setBackground(Color.white);
        singleCellAnalysisPanel.getTrackPointsScrollPane().getViewport().setBackground(Color.white);
        JTable tracksTable = singleCellAnalysisPanel.getTracksTable();
        JTable trackPointsTable = singleCellAnalysisPanel.getTrackPointsTable();
        tracksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // row and column selection
        trackPointsTable.setColumnSelectionAllowed(true);
        trackPointsTable.setRowSelectionAllowed(false);

        // get the tables headers
        JTableHeader tracksTableHeader = tracksTable.getTableHeader();
        JTableHeader trackPointsTableHeader = trackPointsTable.getTableHeader();
        tracksTableHeader.setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        trackPointsTableHeader.setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        tracksTableHeader.setReorderingAllowed(false);
        trackPointsTableHeader.setReorderingAllowed(false);

        preProcessingMap = new LinkedHashMap<>();

        // if you click on a row, the relative track points are fetched from Db and shown in another table
        singleCellAnalysisPanel.getTracksTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = singleCellAnalysisPanel.getTracksTable().getSelectedRow();
                    PlateCondition selectedCondition = singleCellMainController.getSelectedCondition();
                    if (selectedRow != -1) {
                        Track selectedTrack = tracksBindingList.get(selectedRow);
                        singleCellMainController.updateTrackPointsList(selectedCondition, selectedTrack);
                        showTrackPointsInTable();
                    }
                }
            }
        });

        // add view to parent panel
        singleCellMainController.getDataAnalysisPanel().getAreaAnalysisParentPanel().add(singleCellAnalysisPanel,
                gridBagConstraints);
    }

    /**
     * Show track points in the table: this is raw data
     */
    private void showTrackPointsInTable() {
        // get the tracking coordinates unit of measurement
        TrackCoordinatesUnitOfMeasurement coordinatesUnitOfMeasurement = singleCellMainController
                .getCoordinatesUnitOfMeasurement();
        String unitOfMeasurementString = coordinatesUnitOfMeasurement.getUnitOfMeasurementString();

        // table binding
        JTableBinding trackPointsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ,
                trackPointsBindingList, singleCellAnalysisPanel.getTrackPointsTable());

        ColumnBinding columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track"
                + ".trackNumber}"));
        columnBinding.setColumnName("Track");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${timeIndex}"));
        columnBinding.setColumnName("Time Index");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellRow}"));
        columnBinding.setColumnName("x" + " (" + unitOfMeasurementString + ")");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(singleCellMainController.getFormat(), SwingConstants.RIGHT));

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellCol}"));
        columnBinding.setColumnName("y" + " (" + unitOfMeasurementString + ")");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(singleCellMainController.getFormat(), SwingConstants.RIGHT));

        bindingGroup.addBinding(trackPointsTableBinding);
        bindingGroup.bind();
    }

    /**
     * Compute both raw and shifted experiment coordinates ranges.
     */
    private void computeExperimentCoordinatesRanges() {
        experimentRawCoordinatesRanges = new Double[2][2];
        experimentShiftedCoordinatesRanges = new Double[2][2];
        List<Double> xRawMinList = new ArrayList<>();
        List<Double> xRawMaxList = new ArrayList<>();
        List<Double> yRawMinList = new ArrayList<>();
        List<Double> yRawMaxList = new ArrayList<>();

        List<Double> xShiftMinList = new ArrayList<>();
        List<Double> xShiftMaxList = new ArrayList<>();
        List<Double> yShiftMinList = new ArrayList<>();
        List<Double> yShiftMaxList = new ArrayList<>();

        for (PlateCondition plateCondition : preProcessingMap.keySet()) {
            // now get back the coordinates and compute the ranges
            SingleCellConditionDataHolder singleCellConditionDataHolder = getConditionDataHolder(plateCondition);
            Double[][] rawCoordinatesRanges = singleCellConditionDataHolder.getRawCoordinatesRanges();
            Double[][] shiftedCoordinatesRanges = singleCellConditionDataHolder.getShiftedCoordinatesRanges();

            xRawMinList.add(rawCoordinatesRanges[0][0]);
            xRawMaxList.add(rawCoordinatesRanges[0][1]);
            yRawMinList.add(rawCoordinatesRanges[1][0]);
            yRawMaxList.add(rawCoordinatesRanges[1][1]);

            xShiftMinList.add(shiftedCoordinatesRanges[0][0]);
            xShiftMaxList.add(shiftedCoordinatesRanges[0][1]);
            yShiftMinList.add(shiftedCoordinatesRanges[1][0]);
            yShiftMaxList.add(shiftedCoordinatesRanges[1][1]);
        }
        Double xRawMin = Collections.min(xRawMinList);
        Double xRawMax = Collections.max(xRawMaxList);
        Double yRawMin = Collections.min(yRawMinList);
        Double yRawMax = Collections.max(yRawMaxList);
        experimentRawCoordinatesRanges[0] = new Double[]{xRawMin, xRawMax};
        experimentRawCoordinatesRanges[1] = new Double[]{yRawMin, yRawMax};

        Double xShiftMin = Collections.min(xShiftMinList);
        Double xShiftMax = Collections.max(xShiftMaxList);
        Double yShiftMin = Collections.min(yShiftMinList);
        Double yShiftMax = Collections.max(yShiftMaxList);
        experimentShiftedCoordinatesRanges[0] = new Double[]{xShiftMin, xShiftMax};
        experimentShiftedCoordinatesRanges[1] = new Double[]{yShiftMin, yShiftMax};
        appendInfo("raw range x: (" + xRawMin + ", " + xRawMax + ")" + "; y: (" + yRawMin + ", " + yRawMax + ")");
        appendInfo("shifted range x: (" + xShiftMin + ", " + xShiftMax + ")" + "; y: (" + yShiftMin + ", " + yShiftMax +
                ")");
    }

    /**
     * A swing worker to pre-process the entire experiment (i.e. all its
     * conditions at once).
     */
    private class PreProcessExperimentSwingWorker extends SwingWorker<Void, Void> {

        private final Experiment experiment;
        private final WaitingDialog waitingDialog = new WaitingDialog(getMainFrame(), false);

        public PreProcessExperimentSwingWorker(Experiment experiment) {
            this.experiment = experiment;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // show a waiting cursor, disable GUI components
            waitingDialog.setTitle("Please wait, retrieving tracks and computing...");
            GuiUtils.centerDialogOnFrame(getMainFrame(), waitingDialog);
            waitingDialog.setVisible(true);
            singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            singleCellMainController.controlGuiComponents(false);
            // for each plateCondition, get the data from database and do computations
            for (PlateCondition condition : experiment.getPlateConditionList()) {
                fetchTracks(condition);
                updateMapWithCondition(condition);
                singleCellMainController.showNotImagedWells(condition);
            }
            // finally, compute the experiment ranges
            computeExperimentCoordinatesRanges();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                waitingDialog.setVisible(false);
                // when done, enable back the list, but keep buttons disabled!
                singleCellMainController.showMessage("Tracks retrieved!\nSelect a condition to start with the" +
                        "analysis.", "tracks retrieved", JOptionPane.INFORMATION_MESSAGE);
                singleCellMainController.getDataAnalysisPanel().getConditionsList().setEnabled(true);
                singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                singleCellMainController.showInfoMessage("Tracks retrieved from DB. Select a condition to " +
                        "start the analysis.");
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellMainController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * A class extending a swing worker to operate (i.e. perform some basic computations) on a specific plateCondition.
     */
    private class ConditionOperatorSwingWorker extends SwingWorker<Void, Void> {

        private PlateCondition plateCondition;
        private final WaitingDialog waitingDialog = new WaitingDialog(getMainFrame(), false);

        /**
         * Constructor: takes the plate plateCondition to perform the operations on.
         *
         * @param plateCondition
         */
        public ConditionOperatorSwingWorker(PlateCondition plateCondition) {
            this.plateCondition = plateCondition;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // show a waiting dialog
            waitingDialog.setTitle("Computing for: " + plateCondition);
            GuiUtils.centerDialogOnFrame(getMainFrame(), waitingDialog);
            waitingDialog.setVisible(true);
            // show a waiting cursor, disable GUI components
            singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            singleCellMainController.controlGuiComponents(false);
            // actually do the computations on the plateCondition
            SingleCellConditionDataHolder singleCellConditionDataHolder = preProcessingMap.get(plateCondition);
            appendInfo("Operating now on plateCondition: " + plateCondition);
            appendInfo("operating on steps and cells...");
            singleCellOperator.operateOnStepsAndCells(singleCellConditionDataHolder);
            appendInfo("generating instantaneous displacements...");
            singleCellOperator.generateInstantaneousDisplacementsVector(singleCellConditionDataHolder);
            appendInfo("generating directionality ratios...");
            singleCellOperator.generateDirectionalityRatiosVector(singleCellConditionDataHolder);
            appendInfo("generating track displacements...");
            singleCellOperator.generateMedianDirectionalityRatiosVector(singleCellConditionDataHolder);
            appendInfo("generating median directionality ratios...");
            singleCellOperator.generateTrackDisplacementsVector(singleCellConditionDataHolder);
            appendInfo("generating cumulative distances...");
            singleCellOperator.generateCumulativeDistancesVector(singleCellConditionDataHolder);
            appendInfo("generating euclidean distances...");
            singleCellOperator.generateEuclideanDistancesVector(singleCellConditionDataHolder);
            appendInfo("generating track speeds...");
            singleCellOperator.generateTrackSpeedsVector(singleCellConditionDataHolder);
            appendInfo("generating track end-point directionality ratios...");
            singleCellOperator.generateEndPointDirectionalityRatiosVector(singleCellConditionDataHolder);
            appendInfo("generating convex hulls...");
            singleCellOperator.generateConvexHullsVector(singleCellConditionDataHolder);
            appendInfo("generating track displacements...");
            singleCellOperator.generateDisplacementRatiosVector(singleCellConditionDataHolder);
            appendInfo("generating outreach ratios...");
            singleCellOperator.generateOutreachRatiosVector(singleCellConditionDataHolder);
            appendInfo("generating turning angles...");
            singleCellOperator.generateTurningAnglesVector(singleCellConditionDataHolder);
            appendInfo("generating median turning angles...");
            singleCellOperator.generateMedianTurningAnglesVector(singleCellConditionDataHolder);
            plateCondition.setComputed(true);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                waitingDialog.setVisible(false);
                // when done, enable back the list and put back cursor to default
                singleCellMainController.getDataAnalysisPanel().getConditionsList().setEnabled(true);
                singleCellMainController.controlGuiComponents(true);
                singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellMainController.handleUnexpectedError(ex);
            }
        }
    }
}
