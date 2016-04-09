/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCacheSingleCell.DataCategory;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCacheSingleCell;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DisplSpeedPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.ExtendedBoxAndWhiskerRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.InstantaneousDataTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.MsdWellTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for single cell displacement and speed logic. Parent controller is
 * single cell preprocessing controller.
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
    private ChartPanel msdChartPanel;
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
        msdChartPanel = new ChartPanel(null);
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
        msdChartPanel.setChart(null);
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
            dataTable.setModel(new InstantaneousDataTableModel(singleCellConditionDataHolder.getDataStructure(),
                    singleCellConditionDataHolder.getInstantaneousDisplacementsVector(), "inst displ"));
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
     * Plot Density Functions for both raw and corrected area data. A Swing
     * Worker is used, and a cache to hold density functions values.
     *
     * @param plateCondition
     */
    public void plotDisplAndSpeedData(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            PlotDataSwingWorker plotDataSwingWorker = new PlotDataSwingWorker(singleCellConditionDataHolder);
            plotDataSwingWorker.execute();
        }
    }

    /**
     * Plot MSD data for a given condition.
     *
     * @param plateCondition
     */
    public void plotMsdData(PlateCondition plateCondition) {
        JFreeChart msdChart = ChartFactory.createXYLineChart(plateCondition + " - MSD", "time-lag", "MSD (µm²)",
                generateMSDCollection(singleCellPreProcessingController.getConditionDataHolder(plateCondition)),
                PlotOrientation.VERTICAL, true, true, false);

        JFreeChartUtils.setupReplicatesChart(msdChart, plateCondition.getWellList(), true, true);

        displSpeedPanel.getPlotPanel().removeAll();
        displSpeedPanel.getPlotPanel().revalidate();
        displSpeedPanel.getPlotPanel().repaint();
        gridBagConstraints.gridx = 0;
        msdChartPanel.setChart(msdChart);
        displSpeedPanel.getPlotPanel().add(msdChartPanel, gridBagConstraints);
        displSpeedPanel.getPlotPanel().revalidate();
        displSpeedPanel.getPlotPanel().repaint();
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
            singleCellPreProcessingController.showTrackDataInTable(plateCondition, dataTable, columnNames, trackDisplacementsVector);
        }
        displSpeedPanel.getTableInfoLabel().setText("Track Displacements (mean of instantaneous displacements)");
    }

    /**
     * Show the track speed values in a table.
     *
     * @param plateCondition
     */
    public void showTrackSpeedsInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Double[] trackSpeedsVector = singleCellConditionDataHolder.getTrackSpeedsVector();
            String[] columnNames = {"well", "track", "track speed (µm/min)"};
            singleCellPreProcessingController.showTrackDataInTable(plateCondition, dataTable, columnNames, trackSpeedsVector);
        }
        displSpeedPanel.getTableInfoLabel().setText("Track Speeds (mean of instantaneous speeds)");
    }

    /**
     * Show the mean-squared displacements in a table.
     *
     * @param plateCondition
     */
    public void showMsdInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            MsdWellTableModel model = new MsdWellTableModel(singleCellConditionDataHolder);
            dataTable.setModel(model);
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
     * Private methods and classes.
     */
    /**
     * Initialize main panel
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
        radioButtonGroup.add(displSpeedPanel.getMsdRadioButton());

        /**
         * Add action listeners
         */
        // show instantaneous displacements
        displSpeedPanel.getInstantaneousDisplRadioButton().addActionListener((ActionEvent e) -> {
            //check that a condition is selected
            if (singleCellPreProcessingController.getCurrentCondition() != null) {
                showInstantaneousDisplInTable(singleCellPreProcessingController.getCurrentCondition()); // show the data
                plotDisplAndSpeedData(singleCellPreProcessingController.getCurrentCondition()); // plot the data
            }
        });

        // show track displacements
        displSpeedPanel.getTrackDisplRadioButton().addActionListener((ActionEvent e) -> {
            //check that a condition is selected
            if (singleCellPreProcessingController.getCurrentCondition() != null) {
                showTrackDisplInTable(singleCellPreProcessingController.getCurrentCondition()); // show the data
                plotDisplAndSpeedData(singleCellPreProcessingController.getCurrentCondition()); // plot the data
            }
        });

        // show track speeds
        displSpeedPanel.getTrackSpeedsRadioButton().addActionListener((ActionEvent e) -> {
            //check that a condition is selected
            if (singleCellPreProcessingController.getCurrentCondition() != null) {
                showTrackSpeedsInTable(singleCellPreProcessingController.getCurrentCondition()); // show the data
                plotDisplAndSpeedData(singleCellPreProcessingController.getCurrentCondition()); // plot the data
            }
        });

        // show MSD mean-squared-displacement
        displSpeedPanel.getMsdRadioButton().addActionListener((ActionEvent e) -> {
            //check that a condition is selected
            if (singleCellPreProcessingController.getCurrentCondition() != null) {
                showMsdInTable(singleCellPreProcessingController.getCurrentCondition()); // show the data
                plotMsdData(singleCellPreProcessingController.getCurrentCondition()); // plot the data
            }
        });

        //select as default first button 
        displSpeedPanel.getInstantaneousDisplRadioButton().setSelected(true);
        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getDisplSpeedParentPanel().add(displSpeedPanel, gridBagConstraints);
    }

    /**
     * Generate the dataset for the box plot for a single plate condition data
     * holder.
     *
     * @param singleCellConditionDataHolder
     * @return a DefaultBoxAndWhiskerCategoryDataset
     */
    private DefaultBoxAndWhiskerCategoryDataset getInstDisplBoxPlotDataset(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            dataset.add(Arrays.asList(singleCellWellDataHolder.getInstantaneousDisplacementsVector()), singleCellWellDataHolder.getWell().toString(), "");
        });
        return dataset;
    }

    /**
     * Generate the dataset for the box plot for a single plate condition data
     * holder.
     *
     * @param singleCellConditionDataHolder
     * @return a DefaultBoxAndWhiskerCategoryDataset
     */
    private DefaultBoxAndWhiskerCategoryDataset getTrackDisplBoxPlotDataset(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            dataset.add(Arrays.asList(singleCellWellDataHolder.getTrackDisplacementsVector()), singleCellWellDataHolder.getWell().toString(), "");
        });
        return dataset;
    }

    /**
     * Generate the dataset for the box plot for a single plate condition data
     * holder.
     *
     * @param singleCellConditionDataHolder
     * @return a DefaultBoxAndWhiskerCategoryDataset
     */
    private DefaultBoxAndWhiskerCategoryDataset getTrackSpeedBoxPlotDataset(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            dataset.add(Arrays.asList(singleCellWellDataHolder.getTrackSpeedsVector()), singleCellWellDataHolder.getWell().toString(), "");
        });
        return dataset;
    }

    /**
     * This is the only method that makes use of the kernel density estimator
     * interface. Given a SingleCellConditionDataHolder, this is estimating the
     * density function for
     *
     * @param singleCellConditionDataHolder
     * @return a map of DataCategory and a list of list of double arrays: each
     * list of array of double has two components: x values and y values.
     */
    private Map<DataCategory, List<List<double[]>>> estimateDensityFunctions(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        String kernelDensityEstimatorBeanName = singleCellPreProcessingController.getKernelDensityEstimatorBeanName();
        Map<DataCategory, List<List<double[]>>> densityFunctions = new HashMap<>();
        List<List<double[]>> instDisplDensityFunctions = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().map((singleCellWellDataHolder) -> singleCellPreProcessingController.estimateDensityFunction(singleCellWellDataHolder.getInstantaneousDisplacementsVector(),
                kernelDensityEstimatorBeanName)).forEach((oneReplicateInstDisplDensityFunction) -> {
                    instDisplDensityFunctions.add(oneReplicateInstDisplDensityFunction);
                });
        densityFunctions.put(DensityFunctionHolderCacheSingleCell.DataCategory.INST_DISPL, instDisplDensityFunctions);

        List<List<double[]>> trackDisplDensityFunctions = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().map((singleCellWellDataHolder) -> singleCellPreProcessingController.estimateDensityFunction(singleCellWellDataHolder.getTrackDisplacementsVector(),
                kernelDensityEstimatorBeanName)).forEach((oneReplicateTrackDisplDensityFunction) -> {
                    trackDisplDensityFunctions.add(oneReplicateTrackDisplDensityFunction);
                });
        densityFunctions.put(DensityFunctionHolderCacheSingleCell.DataCategory.TRACK_DISPL, trackDisplDensityFunctions);

        List<List<double[]>> trackSpeedDensityFunctions = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().map((singleCellWellDataHolder) -> singleCellPreProcessingController.estimateDensityFunction(singleCellWellDataHolder.getTrackDisplacementsVector(),
                kernelDensityEstimatorBeanName)).forEach((oneReplicateTrackSpeedDensityFunction) -> {
                    trackSpeedDensityFunctions.add(oneReplicateTrackSpeedDensityFunction);
                });
        densityFunctions.put(DensityFunctionHolderCacheSingleCell.DataCategory.TRACK_SPEED, trackSpeedDensityFunctions);

        // put the estimated functions into the map
        return densityFunctions;
    }

    /**
     * Swing Worker for plotting both displacement and speed data (the data
     * concerned with this controller).
     */
    private class PlotDataSwingWorker extends SwingWorker<Void, Void> {

        private final SingleCellConditionDataHolder singleCellConditionDataHolder;
        private XYSeriesCollection instDisplCollection;
        private XYSeriesCollection trackDisplCollection;
        private XYSeriesCollection trackSpeedCollection;
        private DefaultBoxAndWhiskerCategoryDataset instDisplDataset;
        private DefaultBoxAndWhiskerCategoryDataset trackDisplDataset;
        private DefaultBoxAndWhiskerCategoryDataset trackSpeedDataset;

        private Map<DataCategory, List<List<double[]>>> densityFunctionsMap;

        public PlotDataSwingWorker(SingleCellConditionDataHolder singleCellConditionDataHolder) {
            this.singleCellConditionDataHolder = singleCellConditionDataHolder;
        }

        @Override
        protected Void doInBackground() throws Exception {
            singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            singleCellPreProcessingController.controlGuiComponents(false);
            // check if density functions have already been computed: in this case, they are stored in the cache
            if (densityFunctionHolderCacheSingleCell.containsKey(singleCellConditionDataHolder)) {
                // if results are in cache, get them from cache
                densityFunctionsMap = densityFunctionHolderCacheSingleCell.getFromCache(singleCellConditionDataHolder);
            } else {
                singleCellPreProcessingController.showWaitingDialog("Please wait... density functions are being estimated");
                // else estimate results and put them in cache
                densityFunctionsMap = estimateDensityFunctions(singleCellConditionDataHolder);
                densityFunctionHolderCacheSingleCell.putInCache(singleCellConditionDataHolder, densityFunctionsMap);
            }
            // set xyseriesCollections
            instDisplCollection = generateDensityFunction(singleCellConditionDataHolder, densityFunctionsMap, DataCategory.INST_DISPL);
            trackDisplCollection = generateDensityFunction(singleCellConditionDataHolder, densityFunctionsMap, DataCategory.TRACK_DISPL);
            trackSpeedCollection = generateDensityFunction(singleCellConditionDataHolder, densityFunctionsMap, DataCategory.TRACK_SPEED);
            instDisplDataset = getInstDisplBoxPlotDataset(singleCellConditionDataHolder);
            trackDisplDataset = getTrackDisplBoxPlotDataset(singleCellConditionDataHolder);
            trackSpeedDataset = getTrackSpeedBoxPlotDataset(singleCellConditionDataHolder);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // once xySeriesCollections are generated, generate also Charts and show results
                JFreeChart densityChart;
                JFreeChart boxPlotChart;
                if (displSpeedPanel.getInstantaneousDisplRadioButton().isSelected()) {
                    densityChart = JFreeChartUtils.generateDensityFunctionChart(singleCellConditionDataHolder,
                            instDisplCollection, "KDE inst displ", "inst displ", true);
                    boxPlotChart = generateBoxPlotChart(singleCellConditionDataHolder, instDisplDataset,
                            "inst displ");
                } else if (displSpeedPanel.getTrackDisplRadioButton().isSelected()) {
                    densityChart = JFreeChartUtils.generateDensityFunctionChart(singleCellConditionDataHolder,
                            trackDisplCollection, "KDE track displ", "track displ", true);
                    boxPlotChart = generateBoxPlotChart(singleCellConditionDataHolder, trackDisplDataset,
                            "track displ");
                } else {
                    densityChart = JFreeChartUtils.generateDensityFunctionChart(singleCellConditionDataHolder,
                            trackSpeedCollection, "KDE track spped", "track speed", true);
                    boxPlotChart = generateBoxPlotChart(singleCellConditionDataHolder, trackSpeedDataset,
                            "track speed");
                }
                plotDensityChart(densityChart);
                plotBoxPlotChart(boxPlotChart);
                singleCellPreProcessingController.hideWaitingDialog();
                singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                singleCellPreProcessingController.controlGuiComponents(true);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellPreProcessingController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Given a chart for the data density function, show it.
     *
     * @param densityChart
     */
    private void plotDensityChart(JFreeChart densityChart) {
        displSpeedPanel.getPlotPanel().removeAll();
        displSpeedPanel.getPlotPanel().revalidate();
        displSpeedPanel.getPlotPanel().repaint();
        densityPlotChartPanel.setChart(densityChart);
        gridBagConstraints.gridx = 0;
        displSpeedPanel.getPlotPanel().add(densityPlotChartPanel, gridBagConstraints);
        displSpeedPanel.getPlotPanel().revalidate();
        displSpeedPanel.getPlotPanel().repaint();
    }

    /**
     * Given a chart for the box plot, show it.
     *
     * @param boxPlotChart
     */
    private void plotBoxPlotChart(JFreeChart boxPlotChart) {
        boxPlotChartPanel.setChart(boxPlotChart);
        gridBagConstraints.gridx = 1;
        displSpeedPanel.getPlotPanel().add(boxPlotChartPanel, gridBagConstraints);
        displSpeedPanel.getPlotPanel().revalidate();
        displSpeedPanel.getPlotPanel().repaint();
    }

    /**
     * Generate a box plot chart given the singleCellConditionDataHolder
     *
     * @param singleCellConditionDataHolder
     * @param dataset
     * @param title
     * @param yLabel
     * @return
     */
    private JFreeChart generateBoxPlotChart(SingleCellConditionDataHolder singleCellConditionDataHolder, DefaultBoxAndWhiskerCategoryDataset dataset, String yLabel) {
        CategoryAxis xAxis = new CategoryAxis("well");
        NumberAxis yAxis = new NumberAxis(yLabel);
        yAxis.setAutoRangeIncludesZero(false);
        CategoryPlot boxPlot = new CategoryPlot(dataset, xAxis, yAxis, new ExtendedBoxAndWhiskerRenderer());
        JFreeChart boxPlotChart = new JFreeChart("Box-and-Whisker - " + singleCellConditionDataHolder.getPlateCondition() + " (replicates)", boxPlot);
        JFreeChartUtils.setupBoxPlotChart(boxPlotChart);
        return boxPlotChart;
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

    /**
     * Generate data for the mean-squared-displacements.
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private XYSeriesCollection generateMSDCollection(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().map((singleCellWellDataHolder) -> {
            double[][] msdArray = singleCellWellDataHolder.getMsdArray();
            XYSeries xySeries = JFreeChartUtils.generateXYSeries(msdArray);
            xySeries.setKey(singleCellWellDataHolder.getWell().toString());
            return xySeries;
        }).forEach((xySeries) -> {
            xySeriesCollection.addSeries(xySeries);
        });
        return xySeriesCollection;
    }
}
