/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.SingleCellDataTableModel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
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
    private JTable dataTable;
    private Map<PlateCondition, SingleCellPreProcessingResults> preProcessingMap;
    // view
    private SingleCellAnalysisPanel singleCellAnalysisPanel;
    // parent controller
    @Autowired
    private SingleCellMainController singleCellMainController;
    // child controllers
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
    }

    public SingleCellAnalysisPanel getSingleCellAnalysisPanel() {
        return singleCellAnalysisPanel;
    }

    public ObservableList<Track> getTracksBindingList() {
        return tracksBindingList;
    }

    public ObservableList<TrackPoint> getTrackPointsBindingList() {
        return trackPointsBindingList;
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
            singleCellPreProcessor.generateNormalizedTrackCoordinatesMatrix(singleCellPreProcessingResults);
            singleCellPreProcessor.generateVelocitiesVector(singleCellPreProcessingResults);

            // fill in map
            preProcessingMap.put(plateCondition, singleCellPreProcessingResults);
        }
    }

    /**
     * Show table with Tracks results from CellMIA analysis (Tracks fetched from
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
     *
     * @param plateCondition
     */
    public void showNormalizedTrackCoordinatesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = preProcessingMap.get(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] fixedDataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] normalizedTrackCoordinatesMatrix = singleCellPreProcessingResults.getNormalizedTrackCoordinatesMatrix();
            String[] columnNames = {"well", "track", "time index", "x", "y"};
            dataTable.setModel(new SingleCellDataTableModel(fixedDataStructure, normalizedTrackCoordinatesMatrix, columnNames));
            FormatRenderer formatRenderer = new FormatRenderer(singleCellMainController.getFormat());
            for (int i = 3; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
//            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        }
        singleCellAnalysisPanel.getTableInfoLabel().setText("Tracks Coordinates normalized to 0");
    }

    /**
     *
     * @param plateCondition
     */
    public void showVelocitiesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = preProcessingMap.get(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] fixedDataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[] velocitiesVector = singleCellPreProcessingResults.getVelocitiesVector();
            String[] columnNames = {"well", "track", "time index", "velocity"};
            dataTable.setModel(new SingleCellDataTableModel(fixedDataStructure, velocitiesVector, columnNames));
            FormatRenderer formatRenderer = new FormatRenderer(singleCellMainController.getFormat());
            for (int i = 3; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
//            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        }
        singleCellAnalysisPanel.getTableInfoLabel().setText("Velocities");
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

        //init dataTable
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        singleCellAnalysisPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(singleCellAnalysisPanel.getNormalizedTrackCoordinatesRadioButton());
        radioButtonGroup.add(singleCellAnalysisPanel.getVelocityRadioButton());
        //select as default first button (Normalized track coordinates Computation)
        singleCellAnalysisPanel.getNormalizedTrackCoordinatesRadioButton().setSelected(true);
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

        /**
         * add action listeners
         */
        singleCellAnalysisPanel.getNormalizedTrackCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (singleCellMainController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    showNormalizedTrackCoordinatesInTable(singleCellMainController.getCurrentCondition());
                }
            }
        });

        singleCellAnalysisPanel.getVelocityRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (singleCellMainController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    showVelocitiesInTable(singleCellMainController.getCurrentCondition());
                }
            }
        });

        // add view to parent panel
        singleCellMainController.getDataAnalysisPanel().getAnalysisParentPanel().add(singleCellAnalysisPanel, gridBagConstraints);
    }

    /**
     * Show track points in the table
     */
    private void showTrackPointsInTable() {
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
        columnBinding.setColumnName("x");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(singleCellMainController.getFormat()));

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellCol}"));
        columnBinding.setColumnName("y");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(singleCellMainController.getFormat()));

        bindingGroup.addBinding(trackPointsTableBinding);
        bindingGroup.bind();
    }
}
