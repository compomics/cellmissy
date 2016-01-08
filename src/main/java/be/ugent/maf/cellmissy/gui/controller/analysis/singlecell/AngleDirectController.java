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
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
        plotPolarPlots(plateCondition);
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
        angleDirectPanel.getLeftPlotParentPanel().removeAll();
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        List<ChartPanel> histChartPanels = new ArrayList<>();
        if (singleCellConditionDataHolder != null) {
            List<HistogramDataset> histogramDatasets = getInstTurnAngleDatasets(singleCellConditionDataHolder,
                      getNumberOfBins(singleCellConditionDataHolder));
            for (int i = 0; i < histogramDatasets.size(); i++) {
                JFreeChart chart = ChartFactory.createHistogram("", "", "ITA - RF",
                          histogramDatasets.get(i), PlotOrientation.VERTICAL, true, true, false);
                JFreeChartUtils.setShadowVisible(chart, false);
                JFreeChartUtils.setUpHistogramChart(chart, i);
                ChartPanel histChartPanel = new ChartPanel(chart);
                histChartPanels.add(histChartPanel);
                // compute the constraints
                GridBagConstraints tempConstraints = getGridBagConstraints(histogramDatasets.size(), i);
                angleDirectPanel.getLeftPlotParentPanel().add(histChartPanel, tempConstraints);
                angleDirectPanel.getLeftPlotParentPanel().revalidate();
                angleDirectPanel.getLeftPlotParentPanel().repaint();
            }
        }
    }

    private int getNumberOfBins(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellConditionDataHolder.getTurningAnglesVector()));
        double range = Arrays.stream(toPrimitive).max().getAsDouble() - Arrays.stream(toPrimitive).min().getAsDouble();
        return (int) range / 10;
    }

    private void plotPolarPlots(PlateCondition plateCondition) {
        angleDirectPanel.getRightPlotParentPanel().removeAll();
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        List<ChartPanel> polarChartPanels = new ArrayList<>();
        if (singleCellConditionDataHolder != null) {
            List<XYSeriesCollection> datasets = getInstTurnAngleDatasets(singleCellConditionDataHolder);
            for (int i = 0; i < datasets.size(); i++) {
                JFreeChart chart = ChartFactory.createPolarChart("", datasets.get(i), true, true, false);
//                JFreeChartUtils.setUpHistogramChart(chart, i);
                ChartPanel polarChartPanel = new ChartPanel(chart);
                polarChartPanels.add(polarChartPanel);
                // compute the constraints
                GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
                angleDirectPanel.getRightPlotParentPanel().add(polarChartPanel, tempConstraints);
                angleDirectPanel.getRightPlotParentPanel().revalidate();
                angleDirectPanel.getRightPlotParentPanel().repaint();
            }
        }
    }

    /**
     * Create a list of datasets for the histograms.
     *
     * @param singleCellConditionDataHolder
     * @param bins
     * @return
     */
    private List<HistogramDataset> getInstTurnAngleDatasets(SingleCellConditionDataHolder singleCellConditionDataHolder, int bins) {
        List<HistogramDataset> datasets = new ArrayList<>();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            datasets.add(getHistogramDatasetForAWell(singleCellWellDataHolder.getWell().toString(),
                      singleCellWellDataHolder.getTurningAnglesVector(), bins));
        }
        return datasets;
    }

    private List<XYSeriesCollection> getInstTurnAngleDatasets(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<XYSeriesCollection> datasets = new ArrayList<>();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            datasets.add(getPolarDatasetForAWell(singleCellWellDataHolder.getTurningAnglesVector()));
        }
        return datasets;
    }

    /**
     * For a single well, create an histogram dataset.
     *
     * @param data
     * @param seriesKey
     * @return
     */
    private HistogramDataset getHistogramDatasetForAWell(String seriesKey, Double[] data, int bins) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);
        dataset.addSeries(seriesKey, ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(data)), bins);
        return dataset;
    }

    private XYSeriesCollection getPolarDatasetForAWell(Double[] data) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries series1 = createRandomData("Series 1", 75.0, 10.0);
        final XYSeries series2 = createRandomData("Series 2", 50.0, 5.0);
        final XYSeries series3 = createRandomData("Series 3", 25.0, 1.0);
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        double[] dataX = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(dataset));
//        double[] dataY = new double[dataX.length];
//        Arrays.fill(dataY, 1);
//
//        XYSeries series = JFreeChartUtils.generateXYSeries(dataX, dataY);
//        dataset.addSeries(series);
        return dataset;
    }

    private XYSeries createRandomData(final String name,
              final double baseRadius,
              final double thetaInc) {
        final XYSeries series = new XYSeries(name);
        for (double theta = 0.0; theta < 360.0; theta += thetaInc) {
            final double radius = baseRadius * (1.0 + Math.random());
            series.add(theta, radius);
        }
        return series;
    }

    /**
     * Compute temp constraints.
     *
     * @param nPlots
     * @param index
     * @return
     */
    private GridBagConstraints getGridBagConstraints(int nPlots, int index) {
        GridBagConstraints tempConstraints = new GridBagConstraints();
        int nRows = (int) Math.ceil(nPlots / 3);
        tempConstraints.fill = GridBagConstraints.BOTH;
        tempConstraints.weightx = 1.0 / 3;
        tempConstraints.weighty = 1.0 / nRows;
        tempConstraints.gridy = (int) Math.floor(index / 3);
        if (index < 3) {
            tempConstraints.gridx = index;
        } else {
            tempConstraints.gridx = index - ((index / 3) * 3);
        }
        return tempConstraints;
    }
}
