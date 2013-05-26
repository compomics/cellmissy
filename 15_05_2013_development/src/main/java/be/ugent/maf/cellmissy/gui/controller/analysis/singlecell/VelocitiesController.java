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
     *
     * @param plateCondition
     */
    public void showRawVelocitiesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[] velocitiesVector = singleCellPreProcessingResults.getVelocitiesVector();
            velocitiesTable.setModel(new VelocitiesTableModel(dataStructure, velocitiesVector));
            FormatRenderer formatRenderer = new FormatRenderer(SwingConstants.CENTER, singleCellPreProcessingController.getFormat());
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                velocitiesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            velocitiesTable.getColumnModel().getColumn(velocitiesTable.getColumnCount() - 1).setCellRenderer(formatRenderer);
            velocitiesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        velocitiesPanel.getTableInfoLabel().setText("Raw Velocities");
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
        radioButtonGroup.add(velocitiesPanel.getRawVelocitiesRadioButton());
        //select as default first button (raw data track coordinates Computation)
        velocitiesPanel.getRawVelocitiesRadioButton().setSelected(true);

        /**
         * Add action listeners
         */
        velocitiesPanel.getRawVelocitiesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showRawVelocitiesInTable(currentCondition);
                }
            }
        });

        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesParentPanel().add(velocitiesPanel, gridBagConstraints);
    }
}
