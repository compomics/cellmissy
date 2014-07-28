/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.TrackCoordinatesUnitOfMeasurement;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DisplacementsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.text.Format;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
public class SingleCellPreProcessingController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCellPreProcessingController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<Track> tracksBindingList;
    private ObservableList<TrackPoint> trackPointsBindingList;
    private JTableBinding tracksTableBinding;
    private JTableBinding trackPointsTableBinding;
    private Map<PlateCondition, SingleCellPreProcessingResults> preProcessingMap;
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
     *
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

    public Format getFormat() {
        return singleCellMainController.getFormat();
    }

    public SingleCellPreProcessingResults getPreProcessingResults(PlateCondition plateCondition) {
        return preProcessingMap.get(plateCondition);
    }

    public List<PlateCondition> getPlateConditionList() {
        return singleCellMainController.getPlateConditionList();
    }

    public void showRawTrackCoordinatesInTable(PlateCondition plateCondition) {
        trackCoordinatesController.showRawTrackCoordinatesInTable(plateCondition);
    }

    public void updateTracksNumberInfo() {
        trackCoordinatesController.updateTracksNumberInfo();
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

    public void resetRandomTracks() {
        trackCoordinatesController.resetTracksList();
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

    public CellMissyFrame getMainFrame() {
        return singleCellMainController.getCellMissyFrame();
    }

    public Experiment getExperiment() {
        return singleCellMainController.getExperiment();
    }

    public ObservableList<TrackDataHolder> getTrackDataHolderBindingList() {
        return trackCoordinatesController.getTrackDataHolderBindingList();
    }

    /**
     * Get the category to plot for the tracks: normally 0, the condition
     * category; if the well radio button is selected, set the category to plot
     * to 1.
     *
     * @return
     */
    public int getCategoryToplot() {
        return trackCoordinatesController.getCategoryToplot();
    }

    /**
     * Handle Unexpected errors through the main controller
     *
     * @param exception
     */
    public void handleUnexpectedError(Exception exception) {
        singleCellMainController.handleUnexpectedError(exception);
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
        for (PlateCondition plateCondition : singleCellMainController.getPlateConditionList()) {
            // each condition is not loaded at the beginning
            plateCondition.setLoaded(false);
            preProcessingMap.put(plateCondition, null);
        }
    }

    /**
     * Set cursor from main controller
     *
     * @param type
     */
    public void setCursor(Cursor cursor) {
        singleCellMainController.setCursor(cursor);
    }

    /**
     * Given a certain category (selected index in a tabbed pane) generate the
     * random track data holders.
     *
     * @param category: can be 0 or 1
     */
    public void generateRandomTrackDataHolders(int category) {
        trackCoordinatesController.generateRandomTrackDataHolders(category);
    }

    /**
     * When a condition is selected pre processing results are computed and
     * condition is put into the map together with its results holder object
     *
     * @param plateCondition
     */
    public void updateMapWithCondition(PlateCondition plateCondition) {
        // fetch the track points from DB
        singleCellMainController.fetchTrackPoints(plateCondition);
        if (preProcessingMap.get(plateCondition) == null) {
            // create a new object to hold pre-processing results
            SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
            // do computations
            double conversionFactor = computeConversionFactor();
            Double timeLapse = singleCellMainController.getExperiment().getExperimentInterval();
            singleCellPreProcessor.generateTrackDataHolders(singleCellPreProcessingResults, plateCondition, conversionFactor, timeLapse);
            singleCellPreProcessor.generateDataStructure(singleCellPreProcessingResults);
            singleCellPreProcessor.operateOnStepsAndCells(singleCellPreProcessingResults);
            singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellPreProcessingResults);
            singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellPreProcessingResults);
            singleCellPreProcessor.generateInstantaneousDisplacementsVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateDirectionalityRatiosVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackDisplacementsVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateCumulativeDistancesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateEuclideanDistancesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackSpeedsVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateEndPointDirectionalityRatiosVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateConvexHullsVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateDisplacementRatiosVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateOutreachRatiosVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTurningAnglesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateMedianTurningAnglesVector(singleCellPreProcessingResults);
            // fill in map
            preProcessingMap.put(plateCondition, singleCellPreProcessingResults);
        }
    }

    /**
     * Show table with Tracking results from image analysis (Tracks fetched from
     * DB) this is populating the JTable in the ResultsImporter Panel
     */
    public void showTracksInTable() {
        // table binding
        tracksTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, tracksBindingList, singleCellAnalysisPanel.getTracksTable());
        // add column bindings
        ColumnBinding columnBinding = tracksTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.columnNumber}"));
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

        // if you clisk on a row, the relative track points are fetched from Db and shown in another table
        singleCellAnalysisPanel.getTracksTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = singleCellAnalysisPanel.getTracksTable().getSelectedRow();
                    if (selectedRow != -1) {
                        Track selectedTrack = tracksBindingList.get(selectedRow);
                        singleCellMainController.updateTrackPointsList(singleCellMainController.getCurrentCondition(), selectedTrack);
                        showTrackPointsInTable();
                    }
                }
            }
        });

        // add view to parent panel
        singleCellMainController.getDataAnalysisPanel().getAreaAnalysisParentPanel().add(singleCellAnalysisPanel, gridBagConstraints);
    }

    /**
     * Show track points in the table: this is raw data
     */
    private void showTrackPointsInTable() {
        // get the tracking coordinates unit of measuremet
        TrackCoordinatesUnitOfMeasurement coordinatesUnitOfMeasurement = singleCellMainController.getCoordinatesUnitOfMeasurement();
        String unitOfMeasurementString = coordinatesUnitOfMeasurement.getUnitOfMeasurementString();

        // table binding
        trackPointsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, trackPointsBindingList, singleCellAnalysisPanel.getTrackPointsTable());

        ColumnBinding columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track.trackNumber}"));
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
     * Compute the conversion factor according to coordinates unit of
     * measurement and experiment magnification.
     *
     * @return
     */
    private double computeConversionFactor() {
        Experiment currentExperiment = singleCellMainController.getExperiment();
        // by default, conversion factor is equal to 1
        // this is the case of having imported micrometers results to the DB
        double conversionFactor = 1;
        // get the actual unit of measurement: if its pixels, override the conversion factor
        TrackCoordinatesUnitOfMeasurement coordinatesUnitOfMeasurement = singleCellMainController.getCoordinatesUnitOfMeasurement();
        if (coordinatesUnitOfMeasurement.equals(TrackCoordinatesUnitOfMeasurement.PIXELS)) {
            // conversion factor needs to be set according to conversion factor of instrument and magnification used
            // actual conversion factor = instrument conversion factor x magnification / 10
            Magnification magnification = currentExperiment.getMagnification();
            double instrumentConversionFactor = currentExperiment.getInstrument().getConversionFactor();
            double magnificationValue = magnification.getMagnificationValue();
            conversionFactor = instrumentConversionFactor * magnificationValue / 10;
        }
        return conversionFactor;
    }
}
