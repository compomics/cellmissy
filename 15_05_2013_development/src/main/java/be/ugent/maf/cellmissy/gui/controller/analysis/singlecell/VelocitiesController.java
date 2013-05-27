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
import be.ugent.maf.cellmissy.gui.view.renderer.MotileStepsRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.MotileStepsTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.VelocitiesTableModel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
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
    private double motileCriterium;
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
        motileCriterium = 1.41;
        // init views
        initVelocitiesPanel();
    }

    /**
     * getters
     */
    public VelocitiesPanel getVelocitiesPanel() {
        return velocitiesPanel;
    }

    public double getMotileCriterium() {
        return motileCriterium;
    }

    /**
     * Show the raw data velocities for each time step for the plate condition.
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
            for (int i = 0; i < velocitiesTable.getColumnModel().getColumnCount(); i++) {
                velocitiesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            velocitiesTable.getColumnModel().getColumn(velocitiesTable.getColumnCount() - 1).setCellRenderer(formatRenderer);
            velocitiesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        velocitiesPanel.getTableInfoLabel().setText("Raw Single Cell Velocities (for each time step)");
    }

    /**
     *
     * @param plateCondition
     */
    public void showMotileStepsInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[] velocitiesVector = singleCellPreProcessingResults.getVelocitiesVector();
            Object[] motileStepsVector = singleCellPreProcessingResults.getMotileStepsVector();
            velocitiesTable.setModel(new MotileStepsTableModel(dataStructure, velocitiesVector, motileStepsVector));
            FormatRenderer formatRenderer = new FormatRenderer(SwingConstants.CENTER, singleCellPreProcessingController.getFormat());
            MotileStepsRenderer motileStepsRenderer = new MotileStepsRenderer(motileStepsVector);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < velocitiesTable.getColumnModel().getColumnCount(); i++) {
                velocitiesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            velocitiesTable.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
            velocitiesTable.getColumnModel().getColumn(4).setCellRenderer(motileStepsRenderer);
            velocitiesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        velocitiesPanel.getTableInfoLabel().setText("Non motile steps are filtered according to current motile criterium");
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
        radioButtonGroup.add(velocitiesPanel.getMotileStepsRadioButton());
        //select as default first button (raw data track coordinates Computation)
        velocitiesPanel.getRawVelocitiesRadioButton().setSelected(true);
        velocitiesPanel.getMotileCriteriumTextField().setText("" + motileCriterium);

        /**
         * Add action listeners
         */
        // show raw data velocities
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

        // show motile steps
        velocitiesPanel.getMotileStepsRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showMotileStepsInTable(currentCondition);
                }
            }
        });

        // filter non motile steps with current motile criterium
        velocitiesPanel.getFilterNonMotileStepsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    // check the current motile criterium and refresh the table model according to it
                    SingleCellPreProcessingResults preProcessingResults = singleCellPreProcessingController.getPreProcessingResults(currentCondition);
                    String text = velocitiesPanel.getMotileCriteriumTextField().getText();
                    if (!text.isEmpty()) {
                        try {
                            double parseDouble = Double.parseDouble(text);
                            motileCriterium = parseDouble;
                            singleCellPreProcessingController.generateMotileStepsVector(preProcessingResults, motileCriterium);
                            showMotileStepsInTable(currentCondition);
                        } catch (NumberFormatException ex) {
                            singleCellPreProcessingController.showMessage("Insert a valid criterium to filter non motile steps!\nThis has to be a valid number!", "criterium cannot be empty", JOptionPane.WARNING_MESSAGE);
                            LOG.error("Invalid min motile step inserted - " + ex.getMessage());
                            // reset the motile criterium to 1.41 and refresh text info
                            velocitiesPanel.getMotileCriteriumTextField().setText("" + motileCriterium);
                        }
                    } else {
                        singleCellPreProcessingController.showMessage("Insert a min step to filter non motile steps!", "criterium cannot be empty", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });


        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesParentPanel().add(velocitiesPanel, gridBagConstraints);
    }
}
