/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DisplSpeedPanel;
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
import java.util.Arrays;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for displacements logic. Parent controller is single cell
 * preprocessing controller.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("displSpeedController")
class DisplSpeedController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisplSpeedController.class);
    // model
    private JTable displacementsTable;
    // view
    private DisplSpeedPanel displSpeedPanel;
    private ChartPanel boxPlotChartPanel;
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
        boxPlotChartPanel = new ChartPanel(null);
        // init views
        initDisplSpeedPanel();
    }

    /**
     * Getters
     */
    public DisplSpeedPanel getDisplSpeedPanel() {
        return displSpeedPanel;
    }

    /**
     * Show the instantaneous displacements for each time step for the plate
     * condition.
     *
     * @param plateCondition
     */
    public void showInstantaneousDisplInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Object[][] dataStructure = singleCellConditionDataHolder.getDataStructure();
            Double[] instantaneousDisplacementsVector = singleCellConditionDataHolder.getInstantaneousDisplacementsVector();
            displacementsTable.setModel(new DisplacementsTableModel(dataStructure, instantaneousDisplacementsVector));
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < displacementsTable.getColumnModel().getColumnCount(); i++) {
                displacementsTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            displacementsTable.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
            displacementsTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        displSpeedPanel.getTableInfoLabel().setText("Instantaneous Single Cell Displacements (for each time step)");
    }

    /**
     *
     * @param plateCondition
     */
    public void showBoxPlot(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            DefaultBoxAndWhiskerCategoryDataset boxPlotDataset = getBoxPlotDataset(singleCellConditionDataHolder);
            CategoryAxis xAxis = new CategoryAxis("Well");
            NumberAxis yAxis = new NumberAxis("Inst. Displ.");
            yAxis.setAutoRangeIncludesZero(false);
            BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
            renderer.setFillBox(true);
            CategoryPlot boxPlot = new CategoryPlot(boxPlotDataset, xAxis, yAxis, renderer);
            JFreeChart boxPlotChart = new JFreeChart("Box-and-Whisker Inst. Displ", boxPlot);
            boxPlotChartPanel.setChart(boxPlotChart);
            displSpeedPanel.getGraphicsParentPanel().add(boxPlotChartPanel, gridBagConstraints);
        }
    }

    /**
     * Show the track displacements: a track displacement is the mean of
     * instantaneous displacements for a single track.
     *
     * @param plateCondition
     */
    public void showTrackDisplInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Double[] trackDisplacementsVector = singleCellConditionDataHolder.getTrackDisplacementsVector();
            String[] columnNames = {"well", "track", "track displacement (µm)"};
            showTrackDataInTable(plateCondition, columnNames, trackDisplacementsVector);
        }
        displSpeedPanel.getTableInfoLabel().setText("Track Displacements (mean of instantaneous displacements)");
    }

    /**
     * @param plateCondition
     */
    public void showTrackSpeedsInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Double[] trackSpeedsVector = singleCellConditionDataHolder.getTrackSpeedsVector();
            String[] columnNames = {"well", "track", "track speed (µm/min)"};
            showTrackDataInTable(plateCondition, columnNames, trackSpeedsVector);
        }
        displSpeedPanel.getTableInfoLabel().setText("Track Speeds (mean of instantaneous speeds)");
    }

    /**
     * Initialise main panel
     */
    private void initDisplSpeedPanel() {
        // create main view
        displSpeedPanel = new DisplSpeedPanel();
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
        displSpeedPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(displSpeedPanel.getInstantaneousDisplRadioButton());
        radioButtonGroup.add(displSpeedPanel.getTrackDisplRadioButton());
        radioButtonGroup.add(displSpeedPanel.getTrackSpeedsRadioButton());
        //select as default first button (raw data track coordinates Computation)
        displSpeedPanel.getInstantaneousDisplRadioButton().setSelected(true);

        /**
         * Add action listeners
         */
        // show raw data speeds
        displSpeedPanel.getInstantaneousDisplRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showInstantaneousDisplInTable(currentCondition);
                    showBoxPlot(currentCondition);
                }
            }
        });

        // show track displacements
        displSpeedPanel.getTrackDisplRadioButton().addActionListener(new ActionListener() {
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
        displSpeedPanel.getTrackSpeedsRadioButton().addActionListener(new ActionListener() {
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
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getDisplSpeedParentPanel().add(displSpeedPanel, gridBagConstraints);
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
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            TrackDataTableModel trackDataTableModel = new TrackDataTableModel(columnNames, singleCellConditionDataHolder, dataToShow);
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

    /**
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private DefaultBoxAndWhiskerCategoryDataset getBoxPlotDataset(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            dataset.add(Arrays.asList(singleCellWellDataHolder.getInstantaneousDisplacementsVector()), singleCellWellDataHolder.getWell().toString(), "");
        }
        return dataset;
    }
}
