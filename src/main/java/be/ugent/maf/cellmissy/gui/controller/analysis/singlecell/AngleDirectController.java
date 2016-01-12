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
            plotHistInstTurnAngles(plateCondition);
            plotPolarInstTurnAngles(plateCondition);
        } else if (angleDirectPanel.getTrackTurnAngleRadioButton().isSelected()) {
            plotHistTrackTurnAngles(plateCondition);
        }
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
                    plotHistInstTurnAngles(currentCondition);
                    plotPolarInstTurnAngles(currentCondition);
                }
            }
        });

        // show and plot averaged-track turning angles
        angleDirectPanel.getTrackTurnAngleRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showTrackAngleInTable(currentCondition);
                    plotHistTrackTurnAngles(currentCondition);
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
        angleDirectPanel.getTurningAngleParentPanel().add(turningAnglePanel, gridBagConstraints);
    }

    /**
     * Plot the instantaneous turning angles for a certain condition.
     *
     * @param plateCondition
     */
    private void plotHistInstTurnAngles(PlateCondition plateCondition) {
        turningAnglePanel.getHistParentPanel().removeAll();
        turningAnglePanel.getLeftParentPanel().removeAll();
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            List<HistogramDataset> datasets = getInstTurnAngleHistDatasets(singleCellConditionDataHolder,
                      getNumberOfBins(singleCellConditionDataHolder), false);
            List<HistogramDataset> mappedDatasets = getInstTurnAngleHistDatasets(singleCellConditionDataHolder,
                      getNumberOfBins(singleCellConditionDataHolder), true);
            for (int i = 0; i < datasets.size(); i++) {
                JFreeChart chart = ChartFactory.createHistogram("", "", "ITA - RF",
                          datasets.get(i), PlotOrientation.VERTICAL, true, true, false);
                JFreeChartUtils.setShadowVisible(chart, false);
                JFreeChartUtils.setUpHistogramChart(chart, i);
                ChartPanel histChartPanel = new ChartPanel(chart);
                // compute the constraints
                GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
                turningAnglePanel.getHistParentPanel().add(histChartPanel, tempConstraints);
                turningAnglePanel.getHistParentPanel().revalidate();
                turningAnglePanel.getHistParentPanel().repaint();

                chart = ChartFactory.createHistogram("", "", "ITA - RF",
                          mappedDatasets.get(i), PlotOrientation.VERTICAL, true, true, false);
                JFreeChartUtils.setShadowVisible(chart, false);
                JFreeChartUtils.setUpHistogramChart(chart, i);
                histChartPanel = new ChartPanel(chart);
                // compute the constraints
                tempConstraints = getGridBagConstraints(datasets.size(), i);
                turningAnglePanel.getLeftParentPanel().add(histChartPanel, tempConstraints);
                turningAnglePanel.getLeftParentPanel().revalidate();
                turningAnglePanel.getLeftParentPanel().repaint();
            }
        }
    }

    /**
     * Plot the track turning angles for a certain condition. These are averaged
     * (namely, the median is computed).
     *
     * @param plateCondition
     */
    private void plotHistTrackTurnAngles(PlateCondition plateCondition) {
        turningAnglePanel.getHistParentPanel().removeAll();
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            List<HistogramDataset> histogramDatasets = getTrackTurnAngleHistDatasets(singleCellConditionDataHolder,
                      getNumberOfBins(singleCellConditionDataHolder), false);
            for (int i = 0; i < histogramDatasets.size(); i++) {
                JFreeChart chart = ChartFactory.createHistogram("", "", "MTA - RF",
                          histogramDatasets.get(i), PlotOrientation.VERTICAL, true, true, false);
                JFreeChartUtils.setShadowVisible(chart, false);
                JFreeChartUtils.setUpHistogramChart(chart, i);
                ChartPanel histChartPanel = new ChartPanel(chart);
                // compute the constraints
                GridBagConstraints tempConstraints = getGridBagConstraints(histogramDatasets.size(), i);
                turningAnglePanel.getHistParentPanel().add(histChartPanel, tempConstraints);
                turningAnglePanel.getHistParentPanel().revalidate();
                turningAnglePanel.getHistParentPanel().repaint();
            }
        }
    }

    /**
     * Compute number of bins for the angle histogram, so that bin size is
     * always of 10 degrees.
     *
     * @param singleCellConditionDataHolder
     * @return the number of bins, integer.
     */
    private int getNumberOfBins(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellConditionDataHolder.getTurningAnglesVector()));
        double range = Arrays.stream(toPrimitive).max().getAsDouble() - Arrays.stream(toPrimitive).min().getAsDouble();
        return (int) range / 10;
    }

    private int getNumberOfBins(SingleCellWellDataHolder singleCellWellDataHolder) {
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(singleCellWellDataHolder.getTurningAnglesVector()));
        double range = Arrays.stream(toPrimitive).max().getAsDouble() - Arrays.stream(toPrimitive).min().getAsDouble();
        return (int) range / 10;
    }

    /**
     * Plot the polar plots with the instantaneous turning angles.
     *
     * @param plateCondition
     */
    private void plotPolarInstTurnAngles(PlateCondition plateCondition) {
        turningAnglePanel.getRightParentPanel().removeAll();
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            // generate the datasets for the plots
            List<XYSeriesCollection> datasets = getInstTurnAngleDatasets(singleCellConditionDataHolder);
            for (int i = 0; i < datasets.size(); i++) {
                XYSeriesCollection dataset = datasets.get(i);
                // create a new polar plot with this dataset
                PolarPlot plot = new PolarPlot(dataset, new NumberAxis(), new DefaultPolarItemRenderer());
                // create a new chart with this plot
                JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
                JFreeChartUtils.setupPolarChart(chart, i);
                ChartPanel polarChartPanel = new ChartPanel(chart);
                // compute the constraints
                GridBagConstraints tempConstraints = getGridBagConstraints(datasets.size(), i);
                turningAnglePanel.getRightParentPanel().add(polarChartPanel, tempConstraints);
                turningAnglePanel.getRightParentPanel().revalidate();
                turningAnglePanel.getRightParentPanel().repaint();
            }
        }
    }

    /**
     * Create a list of datasets for the histograms (for the instantaneous
     * turning angles).
     *
     * @param singleCellConditionDataHolder
     * @param bins
     * @param mapTo360
     * @return
     */
    private List<HistogramDataset> getInstTurnAngleHistDatasets(SingleCellConditionDataHolder singleCellConditionDataHolder, int bins, boolean mapTo360) {
        List<HistogramDataset> datasets = new ArrayList<>();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            datasets.add(getHistogramDatasetForAWell(singleCellWellDataHolder.getWell().toString(),
                      singleCellWellDataHolder.getTurningAnglesVector(), bins, HistogramType.RELATIVE_FREQUENCY, mapTo360));
        }
        return datasets;
    }

    /**
     * Create a list of datasets for the histograms (for the track median
     * turning angles).
     *
     * @param singleCellConditionDataHolder
     * @param bins
     * @param mapTo360
     * @return
     */
    private List<HistogramDataset> getTrackTurnAngleHistDatasets(SingleCellConditionDataHolder singleCellConditionDataHolder, int bins, boolean mapTo360) {
        List<HistogramDataset> datasets = new ArrayList<>();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            datasets.add(getHistogramDatasetForAWell(singleCellWellDataHolder.getWell().toString(),
                      singleCellWellDataHolder.getMedianTurningAnglesVector(), bins, HistogramType.RELATIVE_FREQUENCY, mapTo360));
        }
        return datasets;
    }

    /**
     * Create a list of datasets for the polar plots.
     *
     * @param singleCellConditionDataHolder
     * @return
     */
    private List<XYSeriesCollection> getInstTurnAngleDatasets(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        List<XYSeriesCollection> list = new ArrayList<>();
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            list.add(getPolarDatasetForAWell(singleCellWellDataHolder));
        }
        return list;
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
     *
     * @param singleCellWellDataHolder
     * @return
     */
    private XYSeriesCollection getPolarDatasetForAWell(SingleCellWellDataHolder singleCellWellDataHolder) {
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(createPolarSeries(singleCellWellDataHolder));
        return data;
    }

    /**
     *
     * @param singleCellWellDataHolder
     * @return the series.
     */
    private XYSeries createPolarSeries(SingleCellWellDataHolder singleCellWellDataHolder) {
        XYSeries series = new XYSeries(singleCellWellDataHolder.getWell().toString(), false);
        HistogramDataset histogramDataset = getHistogramDatasetForAWell(singleCellWellDataHolder.getWell().toString(),
                  singleCellWellDataHolder.getTurningAnglesVector(), getNumberOfBins(singleCellWellDataHolder), HistogramType.FREQUENCY, true);
        // iterate through the series, even though we normally only have one here
        for (int i = 0; i < histogramDataset.getSeriesCount(); i++) {
            int itemCount = histogramDataset.getItemCount(i); // this is the number of bins
            for (int j = 0; j < itemCount; j++) {
                double startX = (double) histogramDataset.getStartX(i, j);
                double endX = (double) histogramDataset.getEndX(i, j);
                double theta = (startX + endX) / 2;
                Double radius = (Double) histogramDataset.getY(i, j);
                series.add(theta, radius);
            }
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
