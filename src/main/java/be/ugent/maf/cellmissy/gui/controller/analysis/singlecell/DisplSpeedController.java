/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache.DataCategory;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCacheSingleCell;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DisplSpeedPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.ExtendedBoxAndWhiskerRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.DisplacementsTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
    private JTable dataTable;
    // view
    private DisplSpeedPanel displSpeedPanel;
    private ChartPanel boxPlotChartPanel;
    private ChartPanel densityPlotChartPanel;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    // services
    @Autowired
    private DensityFunctionHolderCacheSingleCell densityFunctionHolderCacheSingleCell;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        boxPlotChartPanel = new ChartPanel(null);
        densityPlotChartPanel = new ChartPanel(null);
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
     * Reset on cancel.
     */
    public void resetOnCancel() {
        dataTable.setModel(new DefaultTableModel());
        boxPlotChartPanel.setChart(null);
        densityPlotChartPanel.setChart(null);
        densityFunctionHolderCacheSingleCell.clearCache();
    }

    /**
     * Show the instantaneous displacements for each time step for a given plate
     * condition.
     *
     * @param plateCondition
     */
    public void showInstantaneousDisplInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            dataTable.setModel(new DisplacementsTableModel(singleCellConditionDataHolder.getDataStructure(), singleCellConditionDataHolder.getInstantaneousDisplacementsVector()));
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < dataTable.getColumnModel().getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            dataTable.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        displSpeedPanel.getTableInfoLabel().setText("Instantaneous Single Cell Displacements (for each time step)");
    }

    /**
     * Show Box Plot of instantaneous displacements for a given plate condition.
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
            CategoryPlot boxPlot = new CategoryPlot(boxPlotDataset, xAxis, yAxis, new ExtendedBoxAndWhiskerRenderer());
            JFreeChart boxPlotChart = new JFreeChart("Box-and-Whisker Inst. Displ", boxPlot);
            JFreeChartUtils.setupBoxPlotChart(boxPlotChart);
            boxPlotChartPanel.setChart(boxPlotChart);
            displSpeedPanel.getLeftPlotParentPanel().add(boxPlotChartPanel, gridBagConstraints);
            displSpeedPanel.getLeftPlotParentPanel().revalidate();
            displSpeedPanel.getLeftPlotParentPanel().repaint();
        }
    }

    /**
     * Plot Density Functions for both raw and corrected area data. A Swing
     * Worker is used, and a cache to hold density functions values.
     *
     * @param plateCondition
     */
    public void plotInstDisplDensityFunctions(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            PlotDensityFunctionSwingWorker plotDensityFunctionSwingWorker = new PlotDensityFunctionSwingWorker(singleCellConditionDataHolder);
            plotDensityFunctionSwingWorker.execute();
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
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        displSpeedPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(displSpeedPanel.getInstantaneousDisplRadioButton());
        radioButtonGroup.add(displSpeedPanel.getTrackDisplRadioButton());
        radioButtonGroup.add(displSpeedPanel.getTrackSpeedsRadioButton());

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
                    plotInstDisplDensityFunctions(currentCondition);
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

        //select as default first button (raw data track coordinates Computation)
        displSpeedPanel.getInstantaneousDisplRadioButton().setSelected(true);
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
            dataTable.setModel(trackDataTableModel);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < dataTable.getColumnModel().getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            dataTable.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
    }

    /**
     * Generate the dataset for the bow plot for a single plate condition data
     * holder.
     *
     * @param singleCellConditionDataHolder
     * @return a DefaultBoxAndWhiskerCategoryDataset
     */
    private DefaultBoxAndWhiskerCategoryDataset getBoxPlotDataset(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            dataset.add(Arrays.asList(singleCellWellDataHolder.getInstantaneousDisplacementsVector()), singleCellWellDataHolder.getWell().toString(), "");
        }
        return dataset;
    }

    /**
     * This is the only method that makes use of the kernel density estimator
     * interface. Given a condition, this is estimating the density functions
     * for both raw and corrected data.
     *
     * @param singleCellConditionDataHolder
     * @return a map of DataCategory (enum of type: raw data or corrected data)
     * and a list of list of double arrays: each list of array of double has two
     * components: x values and y values.
     */
    private Map<DataCategory, List<List<double[]>>> estimateDensityFunctions(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        String outliersHandlerBeanName = singleCellPreProcessingController.getOutliersHandlerBeanName();
        String kernelDensityEstimatorBeanName = singleCellPreProcessingController.getKernelDensityEstimatorBeanName();

        Map<DataCategory, List<List<double[]>>> densityFunctions = new HashMap<>();
        List<List<double[]>> rawDataDensityFunctions = new ArrayList<>();
        List<List<double[]>> correctedDataDensityFunctions = new ArrayList<>();

        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            List<double[]> oneReplicateRawDataDensityFunction = singleCellPreProcessingController.estimateDensityFunction(singleCellWellDataHolder.getInstantaneousDisplacementsVector(),
                      kernelDensityEstimatorBeanName);
            rawDataDensityFunctions.add(oneReplicateRawDataDensityFunction);
        }

        // raw data density functions into map
        densityFunctions.put(DensityFunctionHolderCache.DataCategory.RAW_DATA, rawDataDensityFunctions);
        // corrected data density functions into map
        densityFunctions.put(DensityFunctionHolderCache.DataCategory.CORRECTED_DATA, correctedDataDensityFunctions);
        return densityFunctions;
    }

    /**
     * Swing Worker for Density Function(s) Plot
     */
    private class PlotDensityFunctionSwingWorker extends SwingWorker<Void, Void> {

        private final SingleCellConditionDataHolder singleCellConditionDataHolder;
        // xySeriesCollections needed for raw data and for corrected data
        private XYSeriesCollection rawDataXYSeriesCollection;
        private XYSeriesCollection correctedDataXYSeriesCollection;
        private Map<DataCategory, List<List<double[]>>> densityFunctionsMap;

        public PlotDensityFunctionSwingWorker(SingleCellConditionDataHolder singleCellConditionDataHolder) {
            this.singleCellConditionDataHolder = singleCellConditionDataHolder;
        }

        @Override
        protected Void doInBackground() throws Exception {
            singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // check if density functions have already been computed: in this case, they are stored in the cache
            if (densityFunctionHolderCacheSingleCell.containsKey(singleCellConditionDataHolder)) {
                // if results are in cache, get them from cache
                densityFunctionsMap = densityFunctionHolderCacheSingleCell.getFromCache(singleCellConditionDataHolder);
            } else {
                // else estimate results and put them in cache
                densityFunctionsMap = estimateDensityFunctions(singleCellConditionDataHolder);
                densityFunctionHolderCacheSingleCell.putInCache(singleCellConditionDataHolder, densityFunctionsMap);
            }
            // set xyseriesCollections calling the generateDensityFunctions method
            rawDataXYSeriesCollection = generateDensityFunction(singleCellConditionDataHolder, densityFunctionsMap, DataCategory.RAW_DATA);
//            correctedDataXYSeriesCollection = generateDensityFunction(singleCellConditionDataHolder, densityFunctionsMap, DataCategory.CORRECTED_DATA);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // once xySeriesCollections are generated, generate also Charts and show results
                int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(singleCellConditionDataHolder.getPlateCondition()) + 1;
                JFreeChart rawDensityChart = JFreeChartUtils.generateDensityFunctionChart(singleCellConditionDataHolder,
                          conditionIndex, rawDataXYSeriesCollection, "KDE raw data");
                plotRawDataDensityFunctions(rawDensityChart);
//                JFreeChart correctedDensityChart = JFreeChartUtils.generateDensityFunctionChart(singleCellConditionDataHolder.getPlateCondition(),
//                          conditionIndex, correctedDataXYSeriesCollection, "KDE corrected data");
//                plotCorrectedDataDensityFunctions(correctedDensityChart);
                singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellPreProcessingController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Given a chart for the raw data density function, show it
     *
     * @param densityChart
     */
    private void plotRawDataDensityFunctions(JFreeChart densityChart) {
        densityPlotChartPanel.setChart(densityChart);
        displSpeedPanel.getRightPlotParentPanel().add(densityPlotChartPanel, gridBagConstraints);
        displSpeedPanel.getRightPlotParentPanel().revalidate();
        displSpeedPanel.getRightPlotParentPanel().repaint();
    }

    /**
     * Given a map with density functions inside, create xySeriesCollection
     *
     * @param singleCellConditionDataHolder
     * @param dataCategory
     * @param densityFunctionsMap
     * @return
     */
    private XYSeriesCollection generateDensityFunction(SingleCellConditionDataHolder singleCellConditionDataHolder, Map<DataCategory, List<List<double[]>>> densityFunctionsMap, DataCategory dataCategory) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        List<List<double[]>> densityFunctions = densityFunctionsMap.get(dataCategory);
        int counter = 0;
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSingleCellAnalyzedSamplesPerWell(singleCellWellDataHolder.getWell());
            if (numberOfSamplesPerWell == 1) {
                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                    // x values
                    double[] xValues = densityFunctions.get(i).get(0);
                    // y values
                    double[] yValues = densityFunctions.get(i).get(1);
                    XYSeries series = new XYSeries("" + singleCellWellDataHolder.getWell(), false);
                    for (int j = 0; j < xValues.length; j++) {
                        double x = xValues[j];
                        double y = yValues[j];
                        series.add(x, y);
                    }
                    xySeriesCollection.addSeries(series);
                }
                counter += numberOfSamplesPerWell;
            } else {
                int label = 0;
                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                    // x values
                    double[] xValues = densityFunctions.get(i).get(0);
                    // y values
                    double[] yValues = densityFunctions.get(i).get(1);
                    XYSeries series = new XYSeries("" + (singleCellWellDataHolder.getWell()) + ", " + (label + 1), false);
                    for (int j = 0; j < xValues.length; j++) {
                        double x = xValues[j];
                        double y = yValues[j];
                        series.add(x, y);
                    }
                    xySeriesCollection.addSeries(series);
                    label++;
                }
                counter += numberOfSamplesPerWell;
            }

        }

        return xySeriesCollection;
    }

}
