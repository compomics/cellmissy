/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.AnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotOptionsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.AngularHistogramRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.ExtendedBoxAndWhiskerRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.SingleCellConditionDataTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import smile.plot.QQPlot;


/**
 * A controller for the actual analysis.
 *
 * @author Paola
 */
@Controller("singleCellAnalysisController")
public class SingleCellAnalysisController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCellAnalysisController.class);
    // model
    private Boolean filteredData;
    private List<XYSeriesCollection> cellTracksData;
    private List<List<TrackDataHolder>> trackDataHolderList;
    // view
    private AnalysisPanel analysisPanel;
    private List<ChartPanel> cellTracksChartPanels;
    private ChartPanel speedBoxPlotChartPanel;
    private ChartPanel speedKDEChartPanel;
    private ChartPanel directPlotChartPanel;
    private List<ChartPanel> rosePlotChartPanels;
    private PlotOptionsPanel plotOptionsPanel;
    // parent controller
    @Autowired
    private SingleCellMainController singleCellMainController;
    // child controllers
    @Autowired
    private SingleCellStatisticsController singleCellStatisticsController;
    @Autowired
    private SingleCellNormalityTestController singleCellNormalityTestController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        cellTracksChartPanels = new ArrayList<>();
        rosePlotChartPanels = new ArrayList<>();
        cellTracksData = new ArrayList<>();
        filteredData = Boolean.FALSE;
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initAnalysisPanel();
        initPlotOptionsPanel();
        // initialize other views
        initOtherViews();
        // init child controllers
        singleCellStatisticsController.init();
        singleCellNormalityTestController.init();
    }

    /**
     * The analysis panel view to be passed on parent controller.
     *
     * @return
     */
    public AnalysisPanel getAnalysisPanel() {
        return analysisPanel;
    }

    public Map<PlateCondition, SingleCellConditionDataHolder> getPreProcessingMap() {
        return singleCellMainController.getPreProcessingMap();
    }

    public Map<SingleCellConditionDataHolder, List<TrackDataHolder>> getFilteringMap() {
        return singleCellMainController.getFilteringMap();
    }

    public SingleCellNormalityTestController getSingleCellNormalityTestController() {
        return singleCellNormalityTestController;
    }
    

    /**
     * Called in the parent controller: update data and graphics.
     */
    public void plotData() {
        updateDataTable();
        plotSpeedBoxPlot();
        plotSpeedKDE();
        plotDirectBoxPlot();
        plotRosePlots();
    }

    /**
     * Set filtered data: yes or no?
     *
     * @param filteredData
     */
    public void setFilteredData(Boolean filteredData) {
        this.filteredData = filteredData;
    }

    public Boolean isFilteredData() {
        return filteredData;
    }

    /**
     * Plot the cell trajectories.
     */
    public void plotCellTracks() {
        if (cellTracksData.isEmpty()) {
            PlotCellTracksSwingWorker plotCellTracksSwingWorker = new PlotCellTracksSwingWorker();
            plotCellTracksSwingWorker.execute();
        }
    }

    public void showMessage(String message, String title, Integer messageType) {
        singleCellMainController.showMessage(message, title, messageType);
    }

    /**
     * Reset everything when cancelling analysis. Called by parent controller.
     */
    protected void resetOnCancel() {
        cellTracksChartPanels = new ArrayList<>();
        rosePlotChartPanels = new ArrayList<>();
        cellTracksData = new ArrayList<>();
        filteredData = Boolean.FALSE;
        speedBoxPlotChartPanel = new ChartPanel(null);
        speedBoxPlotChartPanel.setOpaque(false);
        directPlotChartPanel = new ChartPanel(null);
        directPlotChartPanel.setOpaque(false);
        speedKDEChartPanel = new ChartPanel(null);
        speedKDEChartPanel.setOpaque(false);
        //also reset child controller
        singleCellStatisticsController.resetOnCancel();
        analysisPanel.getCellTracksRadioButton().setSelected(true);
    }

    /**
     * Initialize main view.
     */
    private void initAnalysisPanel() {
        // make a new panel
        analysisPanel = new AnalysisPanel();
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(analysisPanel.getCellTracksRadioButton());
        radioButtonGroup.add(analysisPanel.getCellSpeedRadioButton());
        radioButtonGroup.add(analysisPanel.getStatisticsRadioButton());
        radioButtonGroup.add(analysisPanel.getNormalityTestsRadioButton());
        
        /**
         * Add action listeners
         */
        // plot the cell tracks
        analysisPanel.getCellTracksRadioButton().addActionListener((ActionEvent e) -> {
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) analysisPanel.getBottomPanel().getLayout();
            layout.show(analysisPanel.getBottomPanel(), analysisPanel.getCellTracksPanel().getName());
            plotCellTracks();
        });

        // 
        analysisPanel.getCellSpeedRadioButton().addActionListener((ActionEvent e) -> {
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) analysisPanel.getBottomPanel().getLayout();
            layout.show(analysisPanel.getBottomPanel(), analysisPanel.getCellSpeedsPanel().getName());
            // take care of data and plots here
            plotData();
        });

        // go to the statistics
        analysisPanel.getStatisticsRadioButton().addActionListener((ActionEvent e) -> {
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) analysisPanel.getBottomPanel().getLayout();
            layout.show(analysisPanel.getBottomPanel(), analysisPanel.getStatisticsParentPanel().getName());
            singleCellStatisticsController.updateConditionList();
        });
        
        // go to normality tests
        analysisPanel.getNormalityTestsRadioButton().addActionListener((ActionEvent e) -> {
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) analysisPanel.getBottomPanel().getLayout();
            layout.show(analysisPanel.getBottomPanel(), analysisPanel.getNormalityTestParentPanel().getName());
            //hier moet wss nog iets komen
            String parameter = Integer.toString(analysisPanel.getNormalityTestParentPanel().getSelectedIndex());  
        });        

        //Default selected button
        analysisPanel.getCellTracksRadioButton().setSelected(true);
        AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.LEFT);
        for (int i = 0; i < analysisPanel.getDataTable().getColumnModel().getColumnCount(); i++) {
            analysisPanel.getDataTable().getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
        }
        analysisPanel.getDataTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));

        // add view to parent panel
        singleCellMainController.getSingleCellAnalysisPanel().getAnalysisParentPanel().add(analysisPanel, gridBagConstraints);
    }

    /**
     * Initialize plot options panel. (van cell tracks panel)
     */
    private void initPlotOptionsPanel() {
        // make new view
        plotOptionsPanel = new PlotOptionsPanel();

        // add radiobuttons to a button group
        ButtonGroup scaleAxesButtonGroup = new ButtonGroup();
        scaleAxesButtonGroup.add(plotOptionsPanel.getDoNotScaleAxesRadioButton());
        scaleAxesButtonGroup.add(plotOptionsPanel.getScaleAxesRadioButton());
        plotOptionsPanel.getDoNotScaleAxesRadioButton().setSelected(true);
        // another button group for the shifted/unshifted coordinates
        ButtonGroup shiftedCoordinatesButtonGroup = new ButtonGroup();
        shiftedCoordinatesButtonGroup.add(plotOptionsPanel.getShiftedCoordinatesRadioButton());
        shiftedCoordinatesButtonGroup.add(plotOptionsPanel.getUnshiftedCoordinatesRadioButton());
        plotOptionsPanel.getUnshiftedCoordinatesRadioButton().setSelected(true);

        /**
         * Action listeners
         */
        // do not scale axes
        plotOptionsPanel.getDoNotScaleAxesRadioButton().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
            resetPlotLogic();
            generateDataForTrackPlot(useRawData);
            // use the data to set the charts
            setTrackChartsWithCollections(nCols);
        });

        // scale axes to the experiment range
        plotOptionsPanel.getScaleAxesRadioButton().addActionListener((ActionEvent e) -> {
            boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
            cellTracksChartPanels.stream().forEach((chartPanel) -> {
                singleCellMainController.scaleAxesToExperiment(chartPanel.getChart(), useRawData);
            });
        });

        // shift the all coordinates to the origin
        plotOptionsPanel.getShiftedCoordinatesRadioButton().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            resetPlotLogic();
            generateDataForTrackPlot(false);
            // use the data to set the charts
            setTrackChartsWithCollections(nCols);
        });

        // replot the unshifted coordinates
        plotOptionsPanel.getUnshiftedCoordinatesRadioButton().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            resetPlotLogic();
            generateDataForTrackPlot(true);
            // use the data to set the charts
            setTrackChartsWithCollections(nCols);
        });

        // replot with a different number of columns
        plotOptionsPanel.getnColsComboBox().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
            resetPlotLogic();
            generateDataForTrackPlot(useRawData);
            // use the data to set the charts
            setTrackChartsWithCollections(nCols);
        });
        // add view to parent component
        analysisPanel.getPlotOptionsParentPanel().add(plotOptionsPanel, gridBagConstraints);
    }

    // initialize other views
    private void initOtherViews() {
        speedBoxPlotChartPanel = new ChartPanel(null);
        speedBoxPlotChartPanel.setOpaque(false);
        directPlotChartPanel = new ChartPanel(null);
        directPlotChartPanel.setOpaque(false);
        speedKDEChartPanel = new ChartPanel(null);
        speedKDEChartPanel.setOpaque(false);
        // add panels to parent containers
        analysisPanel.getSpeedBoxPlotPlotParentPanel().add(speedBoxPlotChartPanel, gridBagConstraints);
        analysisPanel.getSpeedKDEParentPanel().add(speedKDEChartPanel, gridBagConstraints);
        analysisPanel.getDirectPlotParentPanel().add(directPlotChartPanel, gridBagConstraints);
    }

    /**
     * Generate Box Plot Chart given a dataset and something to put on the y
     * label.
     *
     * @param dataset
     * @param yLabel
     * @return
     */
    private JFreeChart generateBoxPlotChart(DefaultBoxAndWhiskerCategoryDataset dataset, String yLabel, String title) {
        CategoryAxis xAxis = new CategoryAxis("Condition");
        NumberAxis yAxis = new NumberAxis(yLabel);
        yAxis.setAutoRangeIncludesZero(false);
        CategoryPlot boxPlot = new CategoryPlot(dataset, xAxis, yAxis, new ExtendedBoxAndWhiskerRenderer());
        JFreeChart boxPlotChart = new JFreeChart(title, JFreeChartUtils.getChartFont(), boxPlot, false);
        JFreeChartUtils.setupBoxPlotChart(boxPlotChart);
        return boxPlotChart;
    }

    /**
     * Generate the dataset for the box plot with the track speeds.
     *
     * @return a DefaultBoxAndWhiskerCategoryDataset
     */
    private DefaultBoxAndWhiskerCategoryDataset getSpeedBoxPlotDataset() {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        Collection<SingleCellConditionDataHolder> data;
        if (filteredData) {
            data = singleCellMainController.getFilteringMap().keySet();

        } else {
            data = singleCellMainController.getPreProcessingMap().values();
        }
        data.stream().forEach((singleCellConditionDataHolder) -> {
            dataset.add(Arrays.asList(singleCellConditionDataHolder.getTrackSpeedsVector()), singleCellConditionDataHolder.getPlateCondition().toString(), "");
        });
        return dataset;
    }

    /**
     * Generate the dataset for the box plot with the track directionality
     * values.
     *
     * @return a DefaultBoxAndWhiskerCategoryDataset
     */
    private DefaultBoxAndWhiskerCategoryDataset getDirecBoxPlotDataset() {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        Collection<SingleCellConditionDataHolder> data;
        if (filteredData) {
            data = singleCellMainController.getFilteringMap().keySet();

        } else {
            data = singleCellMainController.getPreProcessingMap().values();
        }
        data.stream().forEach((singleCellConditionDataHolder) -> {
            dataset.add(Arrays.asList(singleCellConditionDataHolder.getEndPointDirectionalityRatios()),
                    singleCellConditionDataHolder.getPlateCondition().toString(), "");
        });
        return dataset;
    }

    /**
     * Render the BoxPlot for the cell speeds.
     */
    private void plotSpeedBoxPlot() {
        DefaultBoxAndWhiskerCategoryDataset speedDataset = getSpeedBoxPlotDataset();
        JFreeChart chart = generateBoxPlotChart(speedDataset, "cell speed (µm/min)", "cell speed");
        speedBoxPlotChartPanel.setChart(chart);
    }

    /**
     * Render the BoxPlot for the cell directionality.
     */
    private void plotDirectBoxPlot() {
        DefaultBoxAndWhiskerCategoryDataset directDataset = getDirecBoxPlotDataset();
        JFreeChart chart = generateBoxPlotChart(directDataset, "cell directionality values", "end-point directionality");
        directPlotChartPanel.setChart(chart);
    }

    /**
     * Render the BoxPlot for the cell angles.
     */
    private void plotSpeedKDE() {
        List<List<double[]>> speedKDE = estimateSpeedKDE();
        XYSeriesCollection densityFunction = singleCellMainController.generateDensityFunction(speedKDE);
        JFreeChart chart = JFreeChartUtils.generateDensityFunctionChart(densityFunction, "KDE track speed", "track speed", false);
        speedKDEChartPanel.setChart(chart);
    }

    /**
     *
     * @return
     */
    private List<List<double[]>> estimateSpeedKDE() {
        String kernelDensityEstimatorBeanName = singleCellMainController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();

        if (!filteredData) {
            getPreProcessingMap().values().stream().map((conditionDataHolder)
                    -> conditionDataHolder.getTrackSpeedsVector()).map((trackSpeedsVector)
                    -> singleCellMainController.estimateDensityFunction(trackSpeedsVector, kernelDensityEstimatorBeanName)).forEach((oneConditionDensityFunction) -> {
                densityFunction.add(oneConditionDensityFunction);
            });
        } else {
            getFilteringMap().keySet().stream().map((conditionDataHolder)
                    -> conditionDataHolder.getTrackSpeedsVector()).map((trackSpeedsVector)
                    -> singleCellMainController.estimateDensityFunction(trackSpeedsVector, kernelDensityEstimatorBeanName)).forEach((oneConditionDensityFunction) -> {
                densityFunction.add(oneConditionDensityFunction);
            });
        }

        return densityFunction;
    }

    /**
     * Render the rose plots.
     */
    private void plotRosePlots() {
        List<XYSeriesCollection> datasets = getPolarTrackTADatasets();
        renderRosePlots(datasets);
    }

    /**
     * Render the rose plots for given datasets.
     *
     * @param datasets
     */
    private void renderRosePlots(List<XYSeriesCollection> datasets) {
        rosePlotChartPanels.clear();
        analysisPanel.getRosePlotParentPanel().removeAll();
        for (int i = 0; i < datasets.size(); i++) {
            XYSeriesCollection dataset = datasets.get(i);
            // create a new polar plot with this dataset, and set the custom renderer
            PolarPlot rosePlot = new PolarPlot(dataset, new NumberAxis(), new AngularHistogramRenderer(i, 5));
            // create a new chart with this plot
            JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, rosePlot, false);
            JFreeChartUtils.setupPolarChart(chart, i);
            ChartPanel rosePlotChartPanel = new ChartPanel(chart);
            rosePlotChartPanels.add(rosePlotChartPanel);
            // compute the constraints
            GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
            analysisPanel.getRosePlotParentPanel().add(rosePlotChartPanel, tempConstraints);
            analysisPanel.getRosePlotParentPanel().revalidate();
            analysisPanel.getRosePlotParentPanel().repaint();
        }
    }

    /**
     * Update the mean speed values in the list.
     */
    private void updateDataTable() {
        Collection<SingleCellConditionDataHolder> data;
        if (filteredData) {
            data = singleCellMainController.getFilteringMap().keySet();

        } else {
            data = singleCellMainController.getPreProcessingMap().values();
        }
        analysisPanel.getDataTable().setModel(new SingleCellConditionDataTableModel(
                new ArrayList<>(data)));
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
        int nRows;
        if (nPlots > 2) {
            nRows = (int) Math.ceil(nPlots / 3);
        } else {
            nRows = 1;
        }
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

    /**
     * Private classes and methods.
     *
     */
    /**
     * This will reset the plot logic.
     */
    private void resetPlotLogic() {
        cellTracksChartPanels.stream().forEach((chartPanel) -> {
            analysisPanel.getTrackPlotParentPanel().remove(chartPanel);
        });
        analysisPanel.getTrackPlotParentPanel().revalidate();
        analysisPanel.getTrackPlotParentPanel().repaint();
        if (!cellTracksData.isEmpty()) {
            cellTracksData.clear();
        }
        if (!cellTracksChartPanels.isEmpty()) {
            cellTracksChartPanels.clear();
        }
    }

    /**
     * Get the track data holders
     *
     * @return
     */
    private void setTrackDataHolders() {
        // check whether data was actually filtered! Otherwise null filteringmap
        if (filteredData && singleCellMainController.getFilteringMap() != null) {
            trackDataHolderList = new ArrayList<>(singleCellMainController.getFilteringMap().values());
        } else if (filteredData) {
            // notify user that he has not actually filtered anything
            showMessage("No filtered data found. Proceeding analysis with raw data.", "No filtering applied.", JOptionPane.INFORMATION_MESSAGE);
            // proceed with raw data
            List<List<TrackDataHolder>> list = new ArrayList<>();
            singleCellMainController.getPreProcessingMap().values().stream().forEach((conditionDataHolder) -> {
                list.add(conditionDataHolder.getTrackDataHolders());
            });
            trackDataHolderList = list;
        } else {
            List<List<TrackDataHolder>> list = new ArrayList<>();
            singleCellMainController.getPreProcessingMap().values().stream().forEach((conditionDataHolder) -> {
                list.add(conditionDataHolder.getTrackDataHolders());
            });
            trackDataHolderList = list;
        }
    }

    /**
     * Generate the actual data for the plot.
     *
     * @param useRawData
     * @return
     */
    private void generateDataForTrackPlot(boolean useRawData) {
        trackDataHolderList.stream().map((trackDataHolders) -> {
            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // this is not the best way to fix this multiple locations issue, but for the moment fair enough !!
            int counter = 0;
            for (TrackDataHolder trackDataHolder : trackDataHolders) {
                // the matrix to use is either the raw coordinates matrix or the shifted matrix
                Double[][] coordinatesMatrix;
                if (useRawData) {
                    coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix();
                } else {
                    coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getShiftedCoordinatesMatrix();
                }
                XYSeries xySeries = JFreeChartUtils.generateXYSeries(coordinatesMatrix);
                Track track = trackDataHolder.getTrack();
                int trackNumber = track.getTrackNumber();
                Well well = track.getWellHasImagingType().getWell();
                String key;
                key = "track " + trackNumber + ", well " + well;
                // we check here if the collection already contains this key
                int seriesIndex = xYSeriesCollection.getSeriesIndex(key);
                if (seriesIndex == -1) {
                    key = "track " + trackNumber + ", well " + well;
                } else {
                    // should be able to get the number of the series already present !!
                    key = "track " + trackNumber + ", well " + well + ", " + (counter + 1);
                    counter++;
                }
                xySeries.setKey(key);
                xYSeriesCollection.addSeries(xySeries);
            }
            return xYSeriesCollection;
        }).forEach((xYSeriesCollection) -> {
            cellTracksData.add(xYSeriesCollection);
        });
    }

    /**
     * Create a list of datasets for the polar plots of track turning angles.
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private List<XYSeriesCollection> getPolarTrackTADatasets() {
        List<XYSeriesCollection> list = new ArrayList<>();
        Collection<SingleCellConditionDataHolder> data;
        if (filteredData) {
            data = singleCellMainController.getFilteringMap().keySet();
            
        } else {
            data = singleCellMainController.getPreProcessingMap().values();
        }
        data.stream().forEach((conditionDataHolder) -> {
            list.add(getPolarDatasetForACondition(conditionDataHolder));
        });
        return list;
    }

    /**
     * Create a polar dataset for a condition.
     *
     *
     * @return
     */
    private XYSeriesCollection getPolarDatasetForACondition(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(createPolarSeries(singleCellConditionDataHolder));
        return dataset;
    }

    /**
     * Create a polar series for a well, given the data we want to make the
     * series (and downstream the plot) for.
     *
     * @param singleCellWellDataHolder
     * @param data
     * @return the series.
     */
    private XYSeries createPolarSeries(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        XYSeries series = new XYSeries(singleCellConditionDataHolder.getPlateCondition().toString(), false);
        HistogramDataset histogramDataset = getHistogramDatasetForACondition(singleCellConditionDataHolder, singleCellConditionDataHolder.getPlateCondition().toString(),
                getNumberOfBins(singleCellConditionDataHolder));
        // iterate through the series, even though we normally only have one here
        for (int i = 0; i < histogramDataset.getSeriesCount(); i++) {
            int itemCount = histogramDataset.getItemCount(i); // this is the number of bins
            for (int j = 0; j < itemCount; j++) {
                double startX = (double) histogramDataset.getStartX(i, j);
                double endX = (double) histogramDataset.getEndX(i, j);
                // the angle in the middle of the bin
                double theta = (startX + endX) / 2;
                // the frequency of this angle in the histogram
                Double radius = (Double) histogramDataset.getY(i, j);
                series.add(theta, radius);
            }
        }
        return series;
    }

    /**
     * For a single well, generate an histogram dataset.
     *
     * @param data
     * @param seriesKey
     * @param mapTo360
     * @return an HistogramDataset
     */
    private HistogramDataset getHistogramDatasetForACondition(SingleCellConditionDataHolder singleCellConditionDataHolder, String seriesKey, int bins) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellConditionDataHolder.getTurningAnglesVector()));

        double[] mappedData = new double[toPrimitive.length];
        for (int i = 0; i < toPrimitive.length; i++) {
            if (toPrimitive[i] > 0) {
                mappedData[i] = toPrimitive[i];
            } else {
                mappedData[i] = toPrimitive[i] + 360;
            }
        }
        double[] toAdd = mappedData;

        dataset.addSeries(seriesKey, toAdd, bins);
        return dataset;
    }

    /**
     * Compute number of bins for the angle histogram for a plate condition, so
     * that bin size is always of 10 degrees.
     *
     * @param singleCellConditionDataHolder
     * @return the number of bins, integer.
     */
    private int getNumberOfBins(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellConditionDataHolder.getTurningAnglesVector()));
        double range = Arrays.stream(toPrimitive).max().getAsDouble() - Arrays.stream(toPrimitive).min().getAsDouble();
        return (int) range / 10;
    }

    /**
     * Set all the charts with the generated XYSeriesCollections.
     *
     * @param nCols
     */
    private void setTrackChartsWithCollections(int nCols) {

        int length = GuiUtils.getAvailableColors().length;
        List<PlateCondition> plateConditionList = singleCellMainController.getPlateConditionList();
        for (int i = 0; i < plateConditionList.size(); i++) {
            XYSeriesCollection collection = cellTracksData.get(i);
            int numberTracks = collection.getSeries().size();
            String title = numberTracks + " tracks" + " - " + plateConditionList.get(i);
            // create a chart for each plate condition
            JFreeChart coordinatesChart = ChartFactory.createXYLineChart(title, "x (µm)", "y (µm)", collection,
                    PlotOrientation.VERTICAL, false, true, false);
            // and a new chart panel as well
            ChartPanel coordinatesChartPanel = new ChartPanel(null);
            coordinatesChartPanel.setOpaque(false);

            // compute the constraints
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(plateConditionList.size(), i, nCols);
            analysisPanel.getTrackPlotParentPanel().add(coordinatesChartPanel, tempBagConstraints);

            JFreeChartUtils.setupTrackChart(coordinatesChart);
            TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true,
                    false, false, null, -1, 2, false);
            trackXYLineAndShapeRenderer.setChosenColor(GuiUtils.getAvailableColors()[i % length]);
            coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
            coordinatesChartPanel.setChart(coordinatesChart);

            // add the chart panels to the list
            cellTracksChartPanels.add(coordinatesChartPanel);

            analysisPanel.getTrackPlotParentPanel().revalidate();
            analysisPanel.getTrackPlotParentPanel().repaint();
        }
    }

    /**
     * Swing Worker to render the global view.
     */
    private class PlotCellTracksSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            singleCellMainController.showWaitingDialog("Rendering global view... ");
            // show a waiting cursor, disable GUI components
            singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            singleCellMainController.controlGuiComponents(false);
            // set track data holders and data for the plots
            setTrackDataHolders();
            generateDataForTrackPlot(true);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // use the data to set the charts
                setTrackChartsWithCollections(Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem()));
                singleCellMainController.hideWaitingDialog();
                singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                //re-enable gui components but next button must be disabled
                singleCellMainController.controlGuiComponents(true);
                singleCellMainController.getAnalysisExperimentPanel().getNextButton().setEnabled(false);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellMainController.handleUnexpectedError(ex);
            }
        }
    }

}
