/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DisplacementsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.DisplacementsTableModel;
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
import org.springframework.stereotype.Controller;

/**
 * Controller for displacements logic. Parent controller is single cell
 * preprocessing controller.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("displacementsController")
public class DisplacementsController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisplacementsController.class);
    // model
    private JTable displacementsTable;
    // view
    private DisplacementsPanel displacementsPanel;
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
     * Getters
     */
    public DisplacementsPanel getSpeedsPanel() {
        return displacementsPanel;
    }

    /**
     * Show the instantaneous displacements for each time step for the plate
     * condition.
     *
     * @param plateCondition
     */
    public void showInstantaneousDisplInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[] instantaneousDisplacementsVector = singleCellPreProcessingResults.getInstantaneousDisplacementsVector();
            displacementsTable.setModel(new DisplacementsTableModel(dataStructure, instantaneousDisplacementsVector));
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < displacementsTable.getColumnModel().getColumnCount(); i++) {
                displacementsTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            displacementsTable.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
            displacementsTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        displacementsPanel.getTableInfoLabel().setText("Instantaneous Single Cell Displacements (for each time step)");
    }

    /**
     * Show the track displacements: a track displacement is the mean of
     * instantaneous displacements for a single track.
     *
     * @param plateCondition
     */
    public void showTrackDisplInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Double[] trackDisplacementsVector = singleCellPreProcessingResults.getTrackDisplacementsVector();
            String[] columnNames = {"well", "track", "track displacement (µm)"};
            showTrackDataInTable(plateCondition, columnNames, trackDisplacementsVector);
        }
        displacementsPanel.getTableInfoLabel().setText("Track Displacements (mean of instantaneous displacements)");
    }

    /**
     *
     * @param plateCondition
     */
    public void showTrackSpeedsInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Double[] trackSpeedsVector = singleCellPreProcessingResults.getTrackSpeedsVector();
            String[] columnNames = {"well", "track", "track speed (µm/min)"};
            showTrackDataInTable(plateCondition, columnNames, trackSpeedsVector);
        }
        displacementsPanel.getTableInfoLabel().setText("Track Speeds (mean of instantaneous speeds)");
    }

    /**
     * Initialise main panel
     */
    private void initSpeedsPanel() {
        // create main view
        displacementsPanel = new DisplacementsPanel();
        //init dataTable
        displacementsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(displacementsTable);
        //the table will take all the viewport height available
        displacementsTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        displacementsTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        displacementsTable.setColumnSelectionAllowed(true);
        displacementsTable.setRowSelectionAllowed(false);
        displacementsPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(displacementsPanel.getInstantaneousDisplRadioButton());
        radioButtonGroup.add(displacementsPanel.getTrackDisplRadioButton());
        radioButtonGroup.add(displacementsPanel.getTrackSpeedsRadioButton());
        //select as default first button (raw data track coordinates Computation)
        displacementsPanel.getInstantaneousDisplRadioButton().setSelected(true);

        /**
         * Add action listeners
         */
        // show raw data speeds
        displacementsPanel.getInstantaneousDisplRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showInstantaneousDisplInTable(currentCondition);
                }
            }
        });

        // show track displacements
        displacementsPanel.getTrackDisplRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showTrackDisplInTable(currentCondition);
                }
            }
        });

        // show track speeds
        displacementsPanel.getTrackSpeedsRadioButton().addActionListener(new ActionListener() {
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
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesParentPanel().add(displacementsPanel, gridBagConstraints);
    }

    /**
     * Show the track data in a table.
     *
     * @param plateCondition: the condition from which the data needs to be
     * shown.
     * @param columnNames: the names for the columns of the table model.
     * @param dataToShow: the data to be shown.
     */
    private void showTrackDataInTable(PlateCondition plateCondition, String columnNames[], Double[] dataToShow) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            TrackDataTableModel trackDataTableModel = new TrackDataTableModel(columnNames, singleCellPreProcessingResults, dataToShow);
            displacementsTable.setModel(trackDataTableModel);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < displacementsTable.getColumnModel().getColumnCount(); i++) {
                displacementsTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            displacementsTable.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
            displacementsTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
    }
}
