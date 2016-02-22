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
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TurningAnglePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.AngularHistogramRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.CompassRenderer;
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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
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
    private TurningAnglePanel turningAnglePanel;
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
        turningAnglePanel = new TurningAnglePanel();
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
     * SHOW DATA IN TABLE.
     */
    /**
     * Show the instantaneous turning angles for each plate condition.
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
     * Show the track turning angle for each plate condition.
     *
     * @param plateCondition
     */
    public void showTrackAngleInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Double[] turningAngleVector = singleCellConditionDataHolder.getMedianTurningAnglesVector();
            String[] columnNames = {"well", "track", "turning angle"};
            singleCellPreProcessingController.showTrackDataInTable(plateCondition, dataTable, columnNames, turningAngleVector);
        }
    }

    /**
     * Plot the angle and the directionality data for a certain plate condition.
     *
     * @param plateCondition
     */
    public void plotAngleAndDirectData(PlateCondition plateCondition) {
        if (angleDirectPanel.getInstTurnAngleRadioButton().isSelected()) {
            plotHistTA(plateCondition);
            plotPolarTA(plateCondition);
            plotRoseTA(plateCondition);
            plotCompassTA(plateCondition);
        } else if (angleDirectPanel.getTrackTurnAngleRadioButton().isSelected()) {
            plotHistTrackTA(plateCondition);
            plotPolarTrackTA(plateCondition);
            plotRoseTrackTA(plateCondition);
            plotCompassTrackTA(plateCondition);
        }
    }

    /**
     * Private methods and classes.
     */
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
        angleDirectPanel.getInstTurnAngleRadioButton().addActionListener((ActionEvent e) -> {
            PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
            //check that a condition is selected
            if (currentCondition != null) {
                showInstAngleInTable(currentCondition);
                plotHistTA(currentCondition);
                plotPolarTA(currentCondition);
                plotRoseTA(currentCondition);
                plotCompassTA(currentCondition);
            }
        });

        // show and plot averaged-track turning angles
        angleDirectPanel.getTrackTurnAngleRadioButton().addActionListener((ActionEvent e) -> {
            PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
            //check that a condition is selected
            if (currentCondition != null) {
                showTrackAngleInTable(currentCondition);
                plotHistTrackTA(currentCondition);
                plotPolarTrackTA(currentCondition);
                plotRoseTrackTA(currentCondition);
                plotCompassTrackTA(currentCondition);
            }
        });

        // show dynamic directionality ratios
        angleDirectPanel.getDynamicDirectRatioRadioButton().addActionListener((ActionEvent e) -> {
            PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
            //check that a condition is selected
            if (currentCondition != null) {

            }
        });

        // show end-point directionality ratios
        angleDirectPanel.getEndPointDirectRatioRadioButton().addActionListener((ActionEvent e) -> {
            PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
            //check that a condition is selected
            if (currentCondition != null) {

            }
        });

        //select as default first button 
        angleDirectPanel.getInstTurnAngleRadioButton().setSelected(true);

        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getAngleDirectParentPanel().add(angleDirectPanel, gridBagConstraints);
        angleDirectPanel.getTurningAngleParentPanel().add(turningAnglePanel, gridBagConstraints);
    }

    /**
     * RENDER SOME PLOTS.
     *
     *
     * /** Render the histograms: both the (-pi, +pi) and the (0, 2*pi)
     *
     * @param datasets
     * @param mappedDatasets
     */
    private void renderHistograms(List<HistogramDataset> datasets, List<HistogramDataset> mappedDatasets) {
        // remove everything from the appropriate panels
        turningAnglePanel.getHistParentPanel().removeAll();
        turningAnglePanel.getMappedHistParentPanel().removeAll();
        for (int i = 0; i < datasets.size(); i++) {
            JFreeChart chart = ChartFactory.createHistogram("", "", "", datasets.get(i), PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setShadowVisible(chart, false);
            JFreeChartUtils.setUpHistogramChart(chart, i);
            ChartPanel histChartPanel = new ChartPanel(chart);
            // compute the constraints
            GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
            turningAnglePanel.getHistParentPanel().add(histChartPanel, tempConstraints);
            turningAnglePanel.getHistParentPanel().revalidate();
            turningAnglePanel.getHistParentPanel().repaint();

            chart = ChartFactory.createHistogram("", "", "", mappedDatasets.get(i), PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setShadowVisible(chart, false);
            JFreeChartUtils.setUpHistogramChart(chart, i);
            histChartPanel = new ChartPanel(chart);

            turningAnglePanel.getMappedHistParentPanel().add(histChartPanel, tempConstraints);
            turningAnglePanel.getMappedHistParentPanel().revalidate();
            turningAnglePanel.getMappedHistParentPanel().repaint();
        }
    }

    /**
     * Render polar plots for given datasets.
     *
     * @param datasets
     */
    private void renderPolarPlots(List<XYSeriesCollection> datasets) {
        turningAnglePanel.getPolarPlotParentPanel().removeAll();
        for (int i = 0; i < datasets.size(); i++) {
            XYSeriesCollection dataset = datasets.get(i);
            // create a new polar plot with this dataset
            PolarPlot polarPlot = new PolarPlot(dataset, new NumberAxis(), new DefaultPolarItemRenderer());
            // create a new chart with this plot
            JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, polarPlot, true);
            JFreeChartUtils.setupPolarChart(chart, i);
            ChartPanel polarChartPanel = new ChartPanel(chart);
            // compute the constraints
            GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
            turningAnglePanel.getPolarPlotParentPanel().add(polarChartPanel, tempConstraints);
            turningAnglePanel.getPolarPlotParentPanel().revalidate();
            turningAnglePanel.getPolarPlotParentPanel().repaint();
        }
    }

    /**
     * Render the rose plots for given datasets.
     *
     * @param datasets
     */
    private void renderRosePlots(List<XYSeriesCollection> datasets) {
        turningAnglePanel.getRosePlotParentPanel().removeAll();
        for (int i = 0; i < datasets.size(); i++) {
            XYSeriesCollection dataset = datasets.get(i);
            // create a new polar plot with this dataset, and set the custom renderer
            PolarPlot rosePlot = new PolarPlot(dataset, new NumberAxis(), new AngularHistogramRenderer(i, 5));
            // create a new chart with this plot
            JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, rosePlot, true);
            JFreeChartUtils.setupPolarChart(chart, i);
            ChartPanel rosePlotChartPanel = new ChartPanel(chart);
            // compute the constraints
            GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
            turningAnglePanel.getRosePlotParentPanel().add(rosePlotChartPanel, tempConstraints);
            turningAnglePanel.getRosePlotParentPanel().revalidate();
            turningAnglePanel.getRosePlotParentPanel().repaint();
        }
    }

    /**
     *
     * @param datasets
     */
    private void renderCompassPlots(List<XYSeriesCollection> datasets) {
        turningAnglePanel.getCompassPlotParentPanel().removeAll();
        for (int i = 0; i < datasets.size(); i++) {
            XYSeriesCollection dataset = datasets.get(i);
            // create a new polar plot with this dataset, and set the custom renderer
            PolarPlot polarPlot = new PolarPlot(dataset, new NumberAxis(), new CompassRenderer(i));
            // create a new chart with this plot
            JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, polarPlot, true);
            JFreeChartUtils.setupPolarChart(chart, i);
            ChartPanel compassChartPanel = new ChartPanel(chart);
            // compute the constraints
            GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
            turningAnglePanel.getCompassPlotParentPanel().add(compassChartPanel, tempConstraints);
            turningAnglePanel.getCompassPlotParentPanel().revalidate();
            turningAnglePanel.getCompassPlotParentPanel().repaint();
        }
    }

    /**
     * ACTUALLY PLOT.
     *
     *
     * /** Plot the instantaneous turning angles for a certain condition.
     *
     * @param plateCondition
     */
    private void plotHistTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            List<HistogramDataset> datasets = getHistTADatasets(singleCellConditionDataHolder,
                    getNumberOfBins(singleCellConditionDataHolder), false);
            List<HistogramDataset> mappedDatasets = getHistTADatasets(singleCellConditionDataHolder,
                    getNumberOfBins(singleCellConditionDataHolder), true);
            renderHistograms(datasets, mappedDatasets);
        }
    }

    /**
     * Plot the track turning angles for a certain condition. These are averaged
     * (namely, the median is computed).
     *
     * @param plateCondition
     */
    private void plotHistTrackTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            List<HistogramDataset> datasets = getHistTrackTADatasets(singleCellConditionDataHolder,
                    getNumberOfBins(singleCellConditionDataHolder), false);
            List<HistogramDataset> mappedDatasets = getHistTrackTADatasets(singleCellConditionDataHolder,
                    getNumberOfBins(singleCellConditionDataHolder), true); // these datasets are mapped to 0-360
            renderHistograms(datasets, mappedDatasets);
        }
    }

    /**
     * Plot the polar plots with the instantaneous turning angles.
     *
     * @param plateCondition
     */
    private void plotPolarTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            // generate the datasets for the plots
            List<XYSeriesCollection> datasets = getPolarTADatasets(singleCellConditionDataHolder);
            renderPolarPlots(datasets);
        }
    }

    /**
     * Plot the polar plots with the track turning angles.
     *
     * @param plateCondition
     */
    private void plotPolarTrackTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            // generate the datasets for the plots
            List<XYSeriesCollection> datasets = getPolarTrackTADatasets(singleCellConditionDataHolder);
            renderPolarPlots(datasets);
        }
    }

    /**
     * Rose plot for instantaneous angles.
     *
     * @param plateCondition
     */
    private void plotRoseTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            // generate the datasets for the plots
            List<XYSeriesCollection> datasets = getPolarTADatasets(singleCellConditionDataHolder);
            renderRosePlots(datasets);
        }
    }

    /**
     * Rose plot for track angles.
     *
     * @param plateCondition
     */
    private void plotRoseTrackTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            // generate the datasets for the plots
            List<XYSeriesCollection> datasets = getPolarTrackTADatasets(singleCellConditionDataHolder);
            renderRosePlots(datasets);
        }
    }

    /**
     *
     * @param plateCondition
     */
    private void plotCompassTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            // generate the datasets for the plots
            List<XYSeriesCollection> datasets = getCompassTADataset(singleCellConditionDataHolder);
            renderCompassPlots(datasets);
        }
    }

    /**
     *
     * @param plateCondition
     */
    private void plotCompassTrackTA(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            // generate the datasets for the plots
            List<XYSeriesCollection> datasets = getCompassTrackTADatasets(singleCellConditionDataHolder);
            renderCompassPlots(datasets);
        }
    }

    /**
     * CREATE THE DATASETS FOR THE PLOTS.
     */
    /**
     * Create a list of datasets for the histograms (for the instantaneous
     * turning angles).
     *
     * @param singleCellConditionDataHolder
     * @param bins
     * @param mapTo360
     * @return
     */
    private List<HistogramDataset> getHistTADatasets(SingleCellConditionDataHolder singleCellConditionDataHolder, int bins, boolean mapTo360) {
        List<HistogramDataset> datasets = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            datasets.add(getHistogramDatasetForAWell(singleCellWellDataHolder.getWell().toString(),
                    singleCellWellDataHolder.getTurningAnglesVector(), bins, HistogramType.RELATIVE_FREQUENCY, mapTo360));
        });
        return datasets;
    }

    /**
     * Create a list of datasets for the histograms (for the track turning
     * angles).
     *
     * @param singleCellConditionDataHolder
     * @param bins
     * @param mapTo360
     * @return
     */
    private List<HistogramDataset> getHistTrackTADatasets(SingleCellConditionDataHolder singleCellConditionDataHolder, int bins, boolean mapTo360) {
        List<HistogramDataset> datasets = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            datasets.add(getHistogramDatasetForAWell(singleCellWellDataHolder.getWell().toString(),
                    singleCellWellDataHolder.getMedianTurningAnglesVector(), bins, HistogramType.RELATIVE_FREQUENCY, mapTo360));
        });
        return datasets;
    }

    /**
     * Create a list of datasets for the polar plots of instantaneous turning
     * angles.
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private List<XYSeriesCollection> getPolarTADatasets(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<XYSeriesCollection> list = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            list.add(getPolarDatasetForAWell(singleCellWellDataHolder, singleCellWellDataHolder.getTurningAnglesVector()));
        });
        return list;
    }

    /**
     * Create a list of datasets for the polar plots of track turning angles.
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private List<XYSeriesCollection> getPolarTrackTADatasets(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<XYSeriesCollection> list = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            list.add(getPolarDatasetForAWell(singleCellWellDataHolder, singleCellWellDataHolder.getMedianTurningAnglesVector()));
        });
        return list;
    }

    /**
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private List<XYSeriesCollection> getCompassTADataset(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<XYSeriesCollection> list = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            double[] angles = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellWellDataHolder.getTurningAnglesVector()));
            double[] displacements = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellWellDataHolder.getInstantaneousDisplacementsVector()));
            list.add(getCompassDatasetForAWell(singleCellWellDataHolder, angles, displacements));
        });
        return list;
    }

    /**
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private List<XYSeriesCollection> getCompassTrackTADatasets(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<XYSeriesCollection> list = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            double[] angles = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellWellDataHolder.getMedianTurningAnglesVector()));
            double[] displacements = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellWellDataHolder.getEuclideanDistancesVector()));
            list.add(getCompassDatasetForAWell(singleCellWellDataHolder, angles, displacements));
        });
        return list;
    }

    /**
     *
     * @param singleCellWellDataHolder
     * @param thetas: the angle values (x)
     * @param radii: the displacement values (y)
     * @return
     */
    private XYSeriesCollection getCompassDatasetForAWell(SingleCellWellDataHolder singleCellWellDataHolder, double[] thetas, double[] radii) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(singleCellWellDataHolder.getWell().toString(), false);
        for (int i = 0; i < thetas.length; i++) {
            series.add(thetas[i], radii[i]);
        }
        dataset.addSeries(series);
        return dataset;
    }

    /**
     * For a single well, generate an histogram dataset.
     *
     * @param data
     * @param seriesKey
     * @param mapTo360
     * @return an HistogramDataset
     */
    private HistogramDataset getHistogramDatasetForAWell(String seriesKey, Double[] data, int bins, HistogramType type, boolean mapTo360) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(type);
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(data));
        double[] toAdd;
        if (!mapTo360) {
            toAdd = toPrimitive;
        } else {
            double[] mappedData = new double[toPrimitive.length];
            for (int i = 0; i < toPrimitive.length; i++) {
                if (toPrimitive[i] > 0) {
                    mappedData[i] = toPrimitive[i];
                } else {
                    mappedData[i] = toPrimitive[i] + 360;
                }
            }
            toAdd = mappedData;
        }
        dataset.addSeries(seriesKey, toAdd, bins);
        return dataset;
    }

    /**
     * Create a polar dataset for a single well, given the data we want to make
     * the plot for.
     *
     * @param singleCellWellDataHolder
     * @param data
     * @return
     */
    private XYSeriesCollection getPolarDatasetForAWell(SingleCellWellDataHolder singleCellWellDataHolder, Double[] data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(createPolarSeries(singleCellWellDataHolder, data));
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
    private XYSeries createPolarSeries(SingleCellWellDataHolder singleCellWellDataHolder, Double[] data) {
        XYSeries series = new XYSeries(singleCellWellDataHolder.getWell().toString(), false);
        HistogramDataset histogramDataset = getHistogramDatasetForAWell(singleCellWellDataHolder.getWell().toString(),
                data, getNumberOfBins(singleCellWellDataHolder), HistogramType.FREQUENCY, true);
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
     * AUXILIARY METHODS.
     */
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
     * Same as previous method, but for a single well.
     *
     * @param singleCellWellDataHolder
     * @return
     */
    private int getNumberOfBins(SingleCellWellDataHolder singleCellWellDataHolder) {
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellWellDataHolder.getTurningAnglesVector()));
        double range = Arrays.stream(toPrimitive).max().getAsDouble() - Arrays.stream(toPrimitive).min().getAsDouble();
        return (int) range / 10;
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
