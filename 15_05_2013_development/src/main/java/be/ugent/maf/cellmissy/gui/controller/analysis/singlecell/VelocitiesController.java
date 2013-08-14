/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.VelocitiesPanel;
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
@Component("velocitiesController")
public class VelocitiesController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VelocitiesController.class);
    // model
    private JTable velocitiesTable;
    // view
    private VelocitiesPanel velocitiesPanel;
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
        initVelocitiesPanel();
    }

    /**
     * getters
     */
    public VelocitiesPanel getVelocitiesPanel() {
        return velocitiesPanel;
    }

    /**
     * Show the raw data velocities for each time step for the plate condition.
     *
     * @param plateCondition
     */
    public void showInstantaneousVelocitiesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[] instantaneousVelocitiesVector = singleCellPreProcessingResults.getInstantaneousVelocitiesVector();
            velocitiesTable.setModel(new VelocitiesTableModel(dataStructure, instantaneousVelocitiesVector));
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < velocitiesTable.getColumnModel().getColumnCount(); i++) {
                velocitiesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            velocitiesTable.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
            velocitiesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        velocitiesPanel.getTableInfoLabel().setText("Instantaneous Single Cell Velocities (for each time step)");
    }

    /**
     * Show the track velocities: a track velocity comes fro the median of
     * instantaneous velocities for a single track.
     *
     * @param plateCondition
     */
    public void showTrackVelocitesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Double[] trackVelocitiesVector = singleCellPreProcessingResults.getTrackVelocitiesVector();
            String[] columnNames = {"well", "track", "track velocity (µm)"};
            TrackDataTableModel trackDataTableModel = new TrackDataTableModel(columnNames, singleCellPreProcessingResults, trackVelocitiesVector);
            velocitiesTable.setModel(trackDataTableModel);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < velocitiesTable.getColumnModel().getColumnCount(); i++) {
                velocitiesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            velocitiesTable.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
            velocitiesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        velocitiesPanel.getTableInfoLabel().setText("Track velocities (median of instantaneous velocities)");
    }

    /**
     * Initialise main panel
     */
    private void initVelocitiesPanel() {
        // create main view
        velocitiesPanel = new VelocitiesPanel();
        //init dataTable
        velocitiesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(velocitiesTable);
        //the table will take all the viewport height available
        velocitiesTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        velocitiesTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        velocitiesTable.setColumnSelectionAllowed(true);
        velocitiesTable.setRowSelectionAllowed(false);
        velocitiesPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(velocitiesPanel.getInstantaneousVelocitiesRadioButton());
        radioButtonGroup.add(velocitiesPanel.getTrackVelocitiesRadioButton());
        //select as default first button (raw data track coordinates Computation)
        velocitiesPanel.getInstantaneousVelocitiesRadioButton().setSelected(true);

        /**
         * Add action listeners
         */
        // show raw data velocities
        velocitiesPanel.getInstantaneousVelocitiesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showInstantaneousVelocitiesInTable(currentCondition);
                }
            }
        });

        // show track velocities
        velocitiesPanel.getTrackVelocitiesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showTrackVelocitesInTable(currentCondition);
                }
            }
        });

        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesParentPanel().add(velocitiesPanel, gridBagConstraints);
    }
}
