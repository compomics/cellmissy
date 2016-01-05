/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.AngleDirectPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.InstantaneousDataTableModel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for turning angle and directionality. Parent controller is single
 * cell preprocessing controller.
 *
 * @author Paola
 */
@Controller("angleDirectController")
public class AngleDirectController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AngleDirectController.class);
    // model
    private JTable dataTable;
    // view
    // the main view
    private AngleDirectPanel angleDirectPanel;
    private ChartPanel chartPanel;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        chartPanel = new ChartPanel(null);
        // initialize main view
        initAngleDirectPanel();
    }

    /**
     * Getters
     *
     * @return
     */
    public AngleDirectPanel getAngleDirectPanel() {
        return angleDirectPanel;
    }

    /**
     * Reset on cancel.
     */
    public void resetOnCancel() {
        dataTable.setModel(new DefaultTableModel());
    }

    /**
     * Show the instantaneous turning angles for each
     *
     * @param plateCondition
     */
    public void showInstAngleInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            dataTable.setModel(new InstantaneousDataTableModel(singleCellConditionDataHolder.getDataStructure(),
                      singleCellConditionDataHolder.getTurningAnglesVector(), "inst turn angle"));
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < dataTable.getColumnModel().getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            dataTable.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        angleDirectPanel.getTableInfoLabel().setText("Instantaneous Single Cell Turning Angles (for each time step)");
    }

    /**
     * Plot the angle and the directionality data for a certain plate condition.
     *
     * @param plateCondition
     */
    public void plotAngleAndDirectData(PlateCondition plateCondition) {
        plotInstTurnAngles(plateCondition);
    }

    /**
     * Initialize the main view.
     */
    private void initAngleDirectPanel() {
        // initialize the main view
        angleDirectPanel = new AngleDirectPanel();
        // initialize the dataTable
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        angleDirectPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(angleDirectPanel.getInstTurnAngleRadioButton());
        radioButtonGroup.add(angleDirectPanel.getTrackTurnAngleRadioButton());
        radioButtonGroup.add(angleDirectPanel.getDynamicDirectRatioRadioButton());
        radioButtonGroup.add(angleDirectPanel.getEndPointDirectRatioRadioButton());

        /**
         * Add action listeners
         */
        // show instantaneous turning angles
        angleDirectPanel.getInstTurnAngleRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showInstAngleInTable(currentCondition);
                    plotAngleAndDirectData(currentCondition);
                }
            }
        });

        // show dynamic directionality ratios
        angleDirectPanel.getDynamicDirectRatioRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {

                }
            }
        });

        // show end-point directionality ratios
        angleDirectPanel.getEndPointDirectRatioRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {

                }
            }
        });

        //select as default first button 
        angleDirectPanel.getInstTurnAngleRadioButton().setSelected(true);

        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getAngleDirectParentPanel().add(angleDirectPanel, gridBagConstraints);
    }

    /**
     * Plot the instantaneous turning angles for a certain condition.
     *
     * @param plateCondition
     */
    private void plotInstTurnAngles(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            CategoryDataset instAngleDataset = getInstAngleDataset(singleCellConditionDataHolder);
            JFreeChart chart = ChartFactory.createBarChart("Bar Plot inst turn angle", "well", "inst turning angle", instAngleDataset, PlotOrientation.VERTICAL, true, true, false);
            chartPanel.setChart(chart);
            angleDirectPanel.getLeftPlotParentPanel().add(chartPanel, gridBagConstraints);
            angleDirectPanel.getLeftPlotParentPanel().revalidate();
            angleDirectPanel.getLeftPlotParentPanel().repaint();
        }
    }

    /**
     * Create a CategoryDataset for a single cell condition data holders. We
     * only have Column Keys (i.e. categories), which are the technical
     * replicates (i.e. the wells).
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private CategoryDataset getInstAngleDataset(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            for (Double angle : singleCellWellDataHolder.getTurningAnglesVector()) {
                if (angle != null && !angle.isNaN()) {
                    dataset.addValue(angle.doubleValue(), singleCellWellDataHolder.getWell().toString(), "turning angle");
                }
            }
        }
        return dataset;
    }
}
