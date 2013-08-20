/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SpeedsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.VelocitiesTableModel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller for velocities logic
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("speedsController")
public class SpeedsController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SpeedsController.class);
    // model
    private JTable speedsTable;
    // view
    private SpeedsPanel speedsPanel;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialise controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        initSpeedsPanel();
    }

    /**
     * getters
     */
    public SpeedsPanel getSpeedsPanel() {
        return speedsPanel;
    }

    /**
     * Show the raw data velocities for each time step for the plate condition.
     *
     * @param plateCondition
     */
    public void showInstantaneousSpeedsInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[] instantaneousSpeedsVector = singleCellPreProcessingResults.getInstantaneousSpeedsVector();
            speedsTable.setModel(new VelocitiesTableModel(dataStructure, instantaneousSpeedsVector));
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < speedsTable.getColumnModel().getColumnCount(); i++) {
                speedsTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            speedsTable.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
            speedsTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        speedsPanel.getTableInfoLabel().setText("Instantaneous Single Cell Speeds (for each time step)");
    }

    /**
     * Show the track velocities: a track velocity comes fro the median of
     * instantaneous velocities for a single track.
     *
     * @param plateCondition
     */
    public void showTrackSpeedsInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Double[] trackSpeedsVector = singleCellPreProcessingResults.getTrackSpeedsVector();
            String[] columnNames = {"well", "track", "track velocity (µm)"};
            TrackDataTableModel trackDataTableModel = new TrackDataTableModel(columnNames, singleCellPreProcessingResults, trackSpeedsVector);
            speedsTable.setModel(trackDataTableModel);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < speedsTable.getColumnModel().getColumnCount(); i++) {
                speedsTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            speedsTable.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
            speedsTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        speedsPanel.getTableInfoLabel().setText("Track speeds (median of instantaneous speeds)");
    }

    /**
     * Initialise main panel
     */
    private void initSpeedsPanel() {
        // create main view
        speedsPanel = new SpeedsPanel();
        //init dataTable
        speedsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(speedsTable);
        //the table will take all the viewport height available
        speedsTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        speedsTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        speedsTable.setColumnSelectionAllowed(true);
        speedsTable.setRowSelectionAllowed(false);
        speedsPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(speedsPanel.getInstantaneousSpeedsRadioButton());
        radioButtonGroup.add(speedsPanel.getTrackSpeedsRadioButton());
        //select as default first button (raw data track coordinates Computation)
        speedsPanel.getInstantaneousSpeedsRadioButton().setSelected(true);

        /**
         * Add action listeners
         */
        // show raw data speeds
        speedsPanel.getInstantaneousSpeedsRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showInstantaneousSpeedsInTable(currentCondition);
                }
            }
        });

        // show track speeds
        speedsPanel.getTrackSpeedsRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showTrackSpeedsInTable(currentCondition);
                }
            }
        });

        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesParentPanel().add(speedsPanel, gridBagConstraints);
    }
}
