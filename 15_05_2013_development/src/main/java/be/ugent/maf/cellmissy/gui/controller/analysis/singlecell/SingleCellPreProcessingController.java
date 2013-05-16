/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
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
            SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
            // do computations
            Double[][] tracksRawData = getTracksRawData(plateCondition);
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
        // tracksTable.setColumnSelectionAllowed(true);
        trackPointsTable.setColumnSelectionAllowed(true);
        //tracksTable.setRowSelectionAllowed(false);
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
                FetchTrackPointsSwingWorker fetchTrackPointsSwingWorker = new FetchTrackPointsSwingWorker(selectedTrack);
                fetchTrackPointsSwingWorker.execute();
            }
        });

        // add view to parent panel
        singleCellMainController.getDataAnalysisPanel().getAnalysisParentPanel().add(singleCellAnalysisPanel, gridBagConstraints);
    }

    /**
     * Swing Worker to fetch One track track points
     */
    private class FetchTrackPointsSwingWorker extends SwingWorker<Void, Void> {

        private Track track;

        public FetchTrackPointsSwingWorker(Track track) {
            this.track = track;
        }

        @Override
        protected Void doInBackground() throws Exception {
            singleCellMainController.setCursor(Cursor.WAIT_CURSOR);
            singleCellMainController.fetchTrackPoints();
            singleCellMainController.updateTrackPointsList(singleCellMainController.getCurrentCondition(), track);
            return null;
        }

        @Override
        protected void done() {
            showTrackPointsInTable();
            singleCellMainController.setCursor(Cursor.DEFAULT_CURSOR);
        }
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
        columnBinding.setColumnName("Cell row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellCol}"));
        columnBinding.setColumnName("Cell col");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${velocityPixels}"));
        columnBinding.setColumnName("Velocity Pixels");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${angle}"));
        columnBinding.setColumnName("Angle");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${angleDelta}"));
        columnBinding.setColumnName("Delta Angle");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${relativeAngle}"));
        columnBinding.setColumnName("Relative Angle");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        bindingGroup.addBinding(trackPointsTableBinding);
        bindingGroup.bind();
    }

    /**
     *
     * @param plateCondition
     * @return
     */
    private Double[][] getTracksRawData(PlateCondition plateCondition) {
        int totalNumberOfTrackPoints = getTotalNumberOfTrackPoints(plateCondition);
        // get number of samples 
        Double[][] tracksRawData = new Double[totalNumberOfTrackPoints][2];

        return tracksRawData;

    }

    /**
     * For a certain condition, get total number of track points
     *
     * @param plateCondition
     * @return
     */
    private int getTotalNumberOfTrackPoints(PlateCondition plateCondition) {
        int totalNumber = 0;
        singleCellMainController.fetchTrackPoints();
        List<Well> imagedWells = plateCondition.getImagedWells();
        for (Well well : imagedWells) {
            Collection<WellHasImagingType> wellHasImagingTypeCollection = well.getWellHasImagingTypeCollection();
            Iterator<WellHasImagingType> iterator = wellHasImagingTypeCollection.iterator();
            while (iterator.hasNext()) {
                WellHasImagingType wellHasImagingType = iterator.next();
                Collection<Track> trackCollection = wellHasImagingType.getTrackCollection();
                Iterator<Track> iterator1 = trackCollection.iterator();
                while (iterator1.hasNext()) {
                    Track track = iterator1.next();
                    int size = track.getTrackPointCollection().size();
                    totalNumber += size;
                }
            }
        }
        return totalNumber;
    }
}
