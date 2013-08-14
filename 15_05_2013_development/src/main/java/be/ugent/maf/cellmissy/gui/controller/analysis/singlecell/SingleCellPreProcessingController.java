/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.analysis.TrackCoordinatesUnitOfMeasurement;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.VelocitiesPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Format;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
    private VelocitiesController velocitiesController;
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
        velocitiesController.init();
    }

    /**
     * getters
     *
     * @return
     */
    public SingleCellAnalysisPanel getSingleCellAnalysisPanel() {
        return singleCellAnalysisPanel;
    }

    public TrackCoordinatesPanel getTrackCoordinatesPanel() {
        return trackCoordinatesController.getTrackCoordinatesPanel();
    }

    public VelocitiesPanel getVelocitiesPanel() {
        return velocitiesController.getVelocitiesPanel();
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

    public void updateTrackNumberLabel() {
        trackCoordinatesController.updateTrackNumberLabel();
    }

    public void updateWellBindingList(PlateCondition plateCondition) {
        trackCoordinatesController.updateWellBindingList(plateCondition);
    }

    public void showShiftedTrackCoordinatesInTable(PlateCondition plateCondition) {
        trackCoordinatesController.showShiftedTrackCoordinatesInTable(plateCondition);
    }

    public void plotRandomTrackCoordinates(PlateCondition plateCondition, boolean useRawData, boolean plotLines, boolean plotPoints) {
        trackCoordinatesController.plotRandomTrackCoordinates(plateCondition, useRawData, plotLines, plotPoints);
    }

    public void resetRandomTracks() {
        trackCoordinatesController.resetTracksList();
    }

    public void showInstantaneousVelocitiesInTable(PlateCondition plateCondition) {
        velocitiesController.showInstantaneousVelocitiesInTable(plateCondition);
    }

    public void showTrackVelocitesInTable(PlateCondition plateCondition) {
        velocitiesController.showTrackVelocitesInTable(plateCondition);
    }

    public CellMissyFrame getMainFrame() {
        return singleCellMainController.getCellMissyFrame();
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
     * Set cursor from main controller
     *
     * @param type
     */
    public void setCursor(Cursor cursor) {
        singleCellMainController.setCursor(cursor);
    }

    /**
     * When a condition is selected pre processing results are computed and
     * condition is put into the map together with its results holder object
     *
     * @param plateCondition
     */
    public void updateMapWithCondition(PlateCondition plateCondition) {
        if (preProcessingMap.get(plateCondition) == null) {
            // create a new object to hold pre-processing results
            SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
            // do computations
            singleCellMainController.fetchTrackPoints(plateCondition);
            singleCellPreProcessor.generateTrackResultsList(singleCellPreProcessingResults, plateCondition);
            singleCellPreProcessor.generateDataStructure(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTimeIndexes(singleCellPreProcessingResults);
            singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellPreProcessingResults, computeConversionFactor());
            singleCellPreProcessor.computeCoordinatesRanges(singleCellPreProcessingResults);
            singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellPreProcessingResults);
            singleCellPreProcessor.generateInstantaneousVelocitiesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackVelocitiesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateCumulativeDistancesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateEuclideanDistancesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateDirectionalitiesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTurningAnglesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackAnglesVector(singleCellPreProcessingResults);
//            singleCellPreProcessor.generateOutliersVector(singleCellPreProcessingResults);
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
        singleCellAnalysisPanel.getTracksTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = singleCellAnalysisPanel.getTracksTable().getSelectedRow();
                Track selectedTrack = tracksBindingList.get(selectedRow);
                singleCellMainController.updateTrackPointsList(singleCellMainController.getCurrentCondition(), selectedTrack);
                showTrackPointsInTable();
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
            // actual conversion factor = instrument conversion factor x magnifiction / 10
            Magnification magnification = currentExperiment.getMagnification();
            double instrumentConversionFactor = currentExperiment.getInstrument().getConversionFactor();
            double magnificationValue = magnification.getMagnificationValue();
            conversionFactor = instrumentConversionFactor * magnificationValue / 10;
        }
        return conversionFactor;
    }
}
