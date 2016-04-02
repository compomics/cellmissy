/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.singlecell.TrackCoordinatesUnitOfMeasurement;
import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.SingleCellConditionPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellConditionOperator;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.AngleDirectPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DisplSpeedPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataTableModel;
import be.ugent.maf.cellmissy.logging.LogTextAreaAppender;
import be.ugent.maf.cellmissy.utils.GuiUtils;
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

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.Format;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controller for single cell pre processing
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("singleCellPreProcessingController")
class SingleCellPreProcessingController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCellPreProcessingController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<Track> tracksBindingList;
    private ObservableList<TrackPoint> trackPointsBindingList;
    private Map<PlateCondition, SingleCellConditionDataHolder> preProcessingMap;
    private Double[][] experimentRawCoordinatesRanges;
    private Double[][] experimentShiftedCoordinatesRanges;
    private String kernelDensityEstimatorBeanName;
    private String outliersHandlerBeanName;
    // view
    private SingleCellAnalysisPanel singleCellAnalysisPanel;
    // parent controller
    @Autowired
    private SingleCellMainController singleCellMainController;
    // child controllers
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    @Autowired
    private DisplSpeedController displSpeedController;
    @Autowired
    private AngleDirectController angleDirectController;
    //services
    @Autowired
    private SingleCellConditionPreProcessor singleCellConditionPreProcessor;
    @Autowired
    private SingleCellConditionOperator singleCellConditionOperator;
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
        displSpeedController.init();
        angleDirectController.init();
        // get the GUI appender and set the text area for it
        LogTextAreaAppender appender = (LogTextAreaAppender) org.apache.log4j.Logger.getLogger("gui").getAppender("gui");
        appender.setTextArea(singleCellAnalysisPanel.getLogTextArea());
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

    public DisplSpeedPanel getDisplSpeedPanel() {
        return displSpeedController.getDisplSpeedPanel();
    }

    public AngleDirectPanel getAngleDirectPanel() {
        return angleDirectController.getAngleDirectPanel();
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

    public String getKernelDensityEstimatorBeanName() {
        return kernelDensityEstimatorBeanName;
    }

    public void setKernelDensityEstimatorBeanName(String kernelDensityEstimatorBeanName) {
        this.kernelDensityEstimatorBeanName = kernelDensityEstimatorBeanName;
    }

    public String getOutliersHandlerBeanName() {
        return outliersHandlerBeanName;
    }

    public void setOutliersHandlerBeanName(String outliersHandlerBeanName) {
        this.outliersHandlerBeanName = outliersHandlerBeanName;
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
        displSpeedController.showInstantaneousDisplInTable(plateCondition);
    }

    public void showInstAngleInTable(PlateCondition plateCondition) {
        angleDirectController.showInstAngleInTable(plateCondition);
    }

    public void showTrackAngleInTable(PlateCondition plateCondition) {
        angleDirectController.showTrackAngleInTable(plateCondition);
    }

    public void plotDisplAndSpeedData(PlateCondition plateCondition) {
        displSpeedController.plotDisplAndSpeedData(plateCondition);
    }

    public void plotMsdData(PlateCondition plateCondition) {
        displSpeedController.plotMsdData(plateCondition);
    }

    public void plotAngleAndDirectData(PlateCondition plateCondition) {
        angleDirectController.plotAngleAndDirectData(plateCondition);
    }

    public void showTrackDisplInTable(PlateCondition plateCondition) {
        displSpeedController.showTrackDisplInTable(plateCondition);
    }

    public void showTrackSpeedsInTable(PlateCondition plateCondition) {
        displSpeedController.showTrackSpeedsInTable(plateCondition);
    }

    public void showMsdInTable(PlateCondition plateCondition) {
        displSpeedController.showMsdInTable(plateCondition);
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

    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName) {
        return singleCellConditionPreProcessor.estimateDensityFunction(data, kernelDensityEstimatorBeanName);
    }

    public void showWaitingDialog(String title) {
        singleCellMainController.showWaitingDialog(title);
    }

    public void hideWaitingDialog() {
        singleCellMainController.hideWaitingDialog();
    }

    public void renderConditionGlobalView(PlateCondition plateCondition) {
        trackCoordinatesController.renderConditionGlobalView(plateCondition);
    }

    public Map<PlateCondition, SingleCellConditionDataHolder> getPreProcessingMap() {
        return preProcessingMap;
    }

    /**
     * Show track data in a table.
     *
     * @param plateCondition: the condition to show the data for
     * @param dataTable: the actual JTable
     * @param columnNames: the header for the table
     * @param dataToShow: the data to show in the table
     */
    public void showTrackDataInTable(PlateCondition plateCondition, JTable dataTable, String columnNames[], Double[] dataToShow) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            TrackDataTableModel trackDataTableModel = new TrackDataTableModel(columnNames, singleCellConditionDataHolder, dataToShow);
            dataTable.setModel(trackDataTableModel);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < dataTable.getColumnModel().getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            dataTable.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
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
        singleCellMainController.getPlateConditionList().stream().map((plateCondition) -> {
            // each plateCondition is not loaded at the beginning
            plateCondition.setLoaded(false);
            return plateCondition;
        }).forEach((plateCondition) -> {
            preProcessingMap.put(plateCondition, null);
        });
    }

    /**
     * Operate on a specific plate condition.
     *
     * @param plateCondition
     */
    public void operateOnCondition(PlateCondition plateCondition) {
        ConditionOperatorSwingWorker conditionOperatorSwingWorker = new ConditionOperatorSwingWorker(plateCondition);
        conditionOperatorSwingWorker.execute();
    }

    /**
     *
     */
    public void enableAnalysis() {
        ProceedToAnalysisSwingWorker proceedToAnalysisSwingWorker = new ProceedToAnalysisSwingWorker();
        proceedToAnalysisSwingWorker.execute();
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
     * @param category: can be 0 or 1
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
        PreProcessExperimentSwingWorker preProcessExperimentSwingWorker = new PreProcessExperimentSwingWorker(experiment);
        preProcessExperimentSwingWorker.execute();
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
     * Private methods and classes
     */
    /**
     * When a plateCondition is selected pre processing results are computed and
     * plateCondition is put into the map together with its results holder
     * object
     *
     * @param plateCondition
     */
    private void updateMapWithCondition(PlateCondition plateCondition) {
        // fetch the track points from DB
        singleCellMainController.fetchTrackPoints(plateCondition);
        if (preProcessingMap.get(plateCondition) == null) {
            // create a new object to hold pre-processing results
            SingleCellConditionDataHolder singleCellConditionDataHolder = new SingleCellConditionDataHolder(plateCondition);
            // do some pre-processing
            LOG.info("generating track data holders...");
            singleCellConditionPreProcessor.generateDataHolders(singleCellConditionDataHolder);
            LOG.info("--> current total number of cell tracks: " + singleCellConditionDataHolder
                    .getTrackDataHolders().size());
            // it can very well be that a plateCondition and/or a sample have been imaged, but there are no tracks in it
            // if this is not the case, go for the computation
            if (!singleCellConditionDataHolder.getTrackDataHolders().isEmpty()) {
                singleCellConditionPreProcessor.generateDataStructure(singleCellConditionDataHolder);
                LOG.info("pre-process step-centric and cell-centric data...");
                singleCellConditionPreProcessor.preProcessStepsAndCells(singleCellConditionDataHolder, singleCellMainController
                        .getConversionFactor(), singleCellMainController.getExperiment().getExperimentInterval());
                LOG.info("generating raw coordinates matrix...");
                singleCellConditionPreProcessor.generateRawTrackCoordinatesMatrix(singleCellConditionDataHolder);
                LOG.info("computing shifted-to-zero coordinates matrix...");
                singleCellConditionPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellConditionDataHolder);
                LOG.info("computing raw coordinates ranges...");
                singleCellConditionPreProcessor.generateRawCoordinatesRanges(singleCellConditionDataHolder);
                LOG.info("computing shifted coordinates ranges...");
                singleCellConditionPreProcessor.generateShiftedCoordinatesRanges(singleCellConditionDataHolder);
                // fill in the map
                preProcessingMap.put(plateCondition, singleCellConditionDataHolder);
                LOG.info("**************************");
                LOG.info("plate condition: " + plateCondition + " processed!");
            } else {
                // remove the plateCondition from the map and inform the user
                preProcessingMap.remove(plateCondition);
                LOG.info("No tracks recorded for condition: " + plateCondition + "; no coordinates to retrieve from"
                        + " DB!");
            }
        }
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
        singleCellAnalysisPanel.getTracksTable().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = singleCellAnalysisPanel.getTracksTable().getSelectedRow();
                PlateCondition selectedCondition = singleCellMainController.getSelectedCondition();
                if (selectedRow != -1) {
                    Track selectedTrack = tracksBindingList.get(selectedRow);
                    singleCellMainController.updateTrackPointsList(selectedCondition, selectedTrack);
                    showTrackPointsInTable();
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

        preProcessingMap.keySet().stream().map((plateCondition) -> getConditionDataHolder(plateCondition)).map((singleCellConditionDataHolder) -> {
            Double[][] rawCoordinatesRanges = singleCellConditionDataHolder.getRawCoordinatesRanges();
            Double[][] shiftedCoordinatesRanges = singleCellConditionDataHolder.getShiftedCoordinatesRanges();
            xRawMinList.add(rawCoordinatesRanges[0][0]);
            xRawMaxList.add(rawCoordinatesRanges[0][1]);
            yRawMinList.add(rawCoordinatesRanges[1][0]);
            yRawMaxList.add(rawCoordinatesRanges[1][1]);
            xShiftMinList.add(shiftedCoordinatesRanges[0][0]);
            return shiftedCoordinatesRanges;
        }).map((shiftedCoordinatesRanges) -> {
            xShiftMaxList.add(shiftedCoordinatesRanges[0][1]);
            return shiftedCoordinatesRanges;
        }).map((shiftedCoordinatesRanges) -> {
            yShiftMinList.add(shiftedCoordinatesRanges[1][0]);
            return shiftedCoordinatesRanges;
        }).forEach((shiftedCoordinatesRanges) -> {
            yShiftMaxList.add(shiftedCoordinatesRanges[1][1]);
        });
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
        LOG.info("raw range x: (" + xRawMin + ", " + xRawMax + ")" + "; y: (" + yRawMin + ", " + yRawMax + ")");
        LOG.info("shifted range x: (" + xShiftMin + ", " + xShiftMax + ")" + "; y: (" + yShiftMin + ", " + yShiftMax
                + ")");
    }

    /**
     * A class extending a swing worker to pre-process the entire experiment
     * (i.e. all its A class extending a swing worker to pre-process the entire
     * experiment (i.e. all its conditions at once).
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
            experiment.getPlateConditionList().stream().map((condition) -> {
                fetchTracks(condition);
                return condition;
            }).map((condition) -> {
                updateMapWithCondition(condition);
                return condition;
            }).forEach((condition) -> {
                singleCellMainController.showNotImagedWells(condition);
            });
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                waitingDialog.setVisible(false);
                // finally, compute the experiment ranges
                if (!preProcessingMap.isEmpty()) {
                    computeExperimentCoordinatesRanges();
                } else {
                    singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    showMessage("Sorry, I did not find any single cell tracks in the DB!\n"
                            + "Please make sure you select the right combination of algorithm-imaging type.", "error-nothing to analyze", JOptionPane.ERROR_MESSAGE);
                    // should get back here to the main window....!!!
                    return;
                }
                // when done, enable back the list, but keep buttons disabled!
                showMessage("Tracks retrieved!\nSelect a condition to start with the"
                        + " analysis.", "tracks retrieved", JOptionPane.INFORMATION_MESSAGE);
                singleCellMainController.getDataAnalysisPanel().getConditionsList().setEnabled(true);
                singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                singleCellMainController.showInfoMessage("Tracks retrieved from DB. Select a condition to "
                        + "start the analysis.");
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellMainController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     *
     * @param plateCondition
     */
    private void computeCondition(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = preProcessingMap.get(plateCondition);
        LOG.info("****************");
        LOG.info("Operating now on: " + singleCellConditionDataHolder);
        LOG.info("operating on steps and cells...");
        singleCellConditionOperator.operateOnStepsAndCells(singleCellConditionDataHolder);
        LOG.info("generating instantaneous displacements...");
        singleCellConditionOperator.generateInstantaneousDisplacementsVector(singleCellConditionDataHolder);
        LOG.info("generating directionality ratios...");
        singleCellConditionOperator.generateDirectionalityRatiosVector(singleCellConditionDataHolder);
        LOG.info("generating track displacements...");
        singleCellConditionOperator.generateMedianDirectionalityRatiosVector(singleCellConditionDataHolder);
        LOG.info("generating median directionality ratios...");
        singleCellConditionOperator.generateTrackDisplacementsVector(singleCellConditionDataHolder);
        LOG.info("generating cumulative distances...");
        singleCellConditionOperator.generateCumulativeDistancesVector(singleCellConditionDataHolder);
        LOG.info("generating euclidean distances...");
        singleCellConditionOperator.generateEuclideanDistancesVector(singleCellConditionDataHolder);
        LOG.info("generating track speeds...");
        singleCellConditionOperator.generateTrackSpeedsVector(singleCellConditionDataHolder);
        LOG.info("generating track end-point directionality ratios...");
        singleCellConditionOperator.generateEndPointDirectionalityRatiosVector(singleCellConditionDataHolder);
        LOG.info("generating mean-squared displacement...");
        singleCellConditionOperator.generateMSDArray(singleCellConditionDataHolder);
        LOG.info("generating convex hulls...");
        singleCellConditionOperator.generateConvexHullsVector(singleCellConditionDataHolder);
        LOG.info("generating track displacements...");
        singleCellConditionOperator.generateDisplacementRatiosVector(singleCellConditionDataHolder);
        LOG.info("generating outreach ratios...");
        singleCellConditionOperator.generateOutreachRatiosVector(singleCellConditionDataHolder);
        LOG.info("generating turning angles...");
        singleCellConditionOperator.generateTurningAnglesVector(singleCellConditionDataHolder);
        LOG.info("generating median turning angles...");
        singleCellConditionOperator.generateMedianTurningAnglesVector(singleCellConditionDataHolder);
        LOG.info("computing for interpolated tracks...");
        singleCellConditionOperator.operateOnInterpolatedTracks(singleCellConditionDataHolder);
        plateCondition.setComputed(true);
    }

    /**
     * A class extending a swing worker to operate (i.e. perform some basic
     * computations) on a specific plateCondition.
     */
    private class ConditionOperatorSwingWorker extends SwingWorker<Void, Void> {

        // the condition
        private final PlateCondition plateCondition;

        /**
         * Constructor: takes the plate plateCondition to perform the operations
         * on.
         *
         * @param plateCondition
         */
        public ConditionOperatorSwingWorker(PlateCondition plateCondition) {
            this.plateCondition = plateCondition;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting dialog
            singleCellMainController.showWaitingDialog("Computing for: " + plateCondition);
            // show a waiting cursor, disable GUI components
            singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            singleCellMainController.controlGuiComponents(false);
            // actually do the computations on the plateCondition
            SingleCellConditionDataHolder singleCellConditionDataHolder = preProcessingMap.get(plateCondition);
            // check if the condition is actually to be analyzed
            if (singleCellConditionDataHolder != null) {
                computeCondition(plateCondition);
            } else {
                // if not, just inform the user and skip the computation
                LOG.info("Apparently this condition was not imaged/analyzed!");
                singleCellMainController.showInfoMessage("Apparently this condition was not imaged/analyzed!\nNothing"
                        + " to compute!");
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                singleCellMainController.hideWaitingDialog();
                // update GUI according to current view on the Card Layout
                singleCellMainController.onCardSwitch();
                // the condition is loaded, and plate view is refreshed
                singleCellMainController.showNotImagedWells(plateCondition);
                singleCellMainController.showWellsForCurrentCondition(plateCondition);
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

    /**
     *
     */
    private class ProceedToAnalysisSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting dialog
            singleCellMainController.showWaitingDialog("Enabling analysis, processing the other conditions...");
            // show a waiting cursor, disable GUI components
            singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            singleCellMainController.controlGuiComponents(false);
            for (PlateCondition condition : singleCellMainController.getPlateConditionList()) {
                if (!condition.isComputed()) {
                    computeCondition(condition);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                singleCellMainController.hideWaitingDialog();
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
