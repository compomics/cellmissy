/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.analysis.SingleCellAnalysisPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
 *
 * @author Paola Masuzzo
 */
@Controller("singleCellAnalysisController")
public class SingleCellAnalysisController {

    // model
    private BindingGroup bindingGroup;
    private ObservableList<Track> tracksBindingList;
    private ObservableList<TrackPoint> trackPointsBindingList;
    private JTableBinding tracksTableBinding;
    private JTableBinding trackPointsTableBinding;
    // view
    private SingleCellAnalysisPanel singleCellAnalysisPanel;
    // parent controller
    @Autowired
    private DataAnalysisController dataAnalysisController;
    // child controllers
    //services
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init views
        initSingleCellAnalysisPanel();
    }

    public SingleCellAnalysisPanel getSingleCellAnalysisPanel() {
        return singleCellAnalysisPanel;
    }

    public ObservableList<TrackPoint> getTrackPointsBindingList() {
        return trackPointsBindingList;
    }

    public ObservableList<Track> getTracksBindingList() {
        return tracksBindingList;
    }

    /**
     * 
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
     * initialize main view
     */
    private void initSingleCellAnalysisPanel() {
        // init main view
        singleCellAnalysisPanel = new SingleCellAnalysisPanel();

        // init binding lists
        tracksBindingList = ObservableCollections.observableList(new ArrayList<Track>());
        trackPointsBindingList = ObservableCollections.observableList(new ArrayList<TrackPoint>());
        // set background of tables scroll panes to white 
        singleCellAnalysisPanel.getTracksTableScrollPane().getViewport().setBackground(Color.white);
        singleCellAnalysisPanel.getTrackPointsTableScrollPane().getViewport().setBackground(Color.white);

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
        dataAnalysisController.getDataAnalysisPanel().getSingleCellAnalysisParentPanel().add(singleCellAnalysisPanel, gridBagConstraints);
    }

    /**
     * 
     */
    private void showTrackPointsInTable() {
        // table binding
        trackPointsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, trackPointsBindingList, singleCellAnalysisPanel.getTrackPointsTable());

        ColumnBinding columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track.trackNumber}"));
        columnBinding.setColumnName("Track");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

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
        
        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellCol}"));
        columnBinding.setColumnName("Cell col");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        
        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellRow}"));
        columnBinding.setColumnName("Cell row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        
        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${velocityPixels}"));
        columnBinding.setColumnName("Velocity Pixels");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        bindingGroup.addBinding(trackPointsTableBinding);
        bindingGroup.bind();
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
            dataAnalysisController.setCursor(Cursor.WAIT_CURSOR);
            dataAnalysisController.fetchTrackPoints();
            dataAnalysisController.updateTrackPointsList(dataAnalysisController.getSelectedCondition(), track);
            return null;
        }

        @Override
        protected void done() {
            showTrackPointsInTable();
            dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }
}
