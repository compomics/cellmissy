/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackDataDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.PlottedTracksListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TimePointTrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackCoordinatesTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataHolderTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller for the track coordinates logic.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("trackCoordinatesController")
public class TrackCoordinatesController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrackCoordinatesController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<Well> wellBindingList;
    private JTable coordinatesTable;
    private ObservableList<TrackDataHolder> trackDataHolderBindingList;
    // view
    private TrackCoordinatesPanel trackCoordinatesPanel;
    private ChartPanel coordinatesChartPanel;
    private TrackDataDialog trackDataDialog;
    private ChartPanel xtCoordinateChartPanel;
    private ChartPanel ytCoordinateChartPanel;
    private ChartPanel unshiftedTrackChartPanel;
    private ChartPanel shiftedTrackChartPanel;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /*
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        initTrackCoordinatesPanel();
    }

    /**
     * getters
     */
    public TrackCoordinatesPanel getTrackCoordinatesPanel() {
        return trackCoordinatesPanel;
    }

    public ObservableList<TrackDataHolder> getTrackDataHolderBindingList() {
        return trackDataHolderBindingList;
    }

    /**
     * Show the number of total tracks for the current selected condition.
     */
    public void updateTrackNumberLabel() {
        int trackNumber = getTrackNumberForCondition();
        trackCoordinatesPanel.getTotalTracksNumberLabel().setText("" + trackNumber);
    }

    /**
     * Update the binding list with the current wells (according to the current
     * condition selected).
     *
     * @param plateCondition
     */
    public void updateWellBindingList(PlateCondition plateCondition) {
        if (!wellBindingList.isEmpty()) {
            wellBindingList.clear();
        }
        wellBindingList.addAll(plateCondition.getSingleCellAnalyzedWells());
    }

    /**
     * For the given condition, show the raw track coordinates in a table.
     *
     * @param plateCondition
     */
    public void showRawTrackCoordinatesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] rawTrackCoordinatesMatrix = singleCellPreProcessingResults.getRawTrackCoordinatesMatrix();
            coordinatesTable.setModel(new TrackCoordinatesTableModel(dataStructure, rawTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            for (int i = 3; i < coordinatesTable.getColumnCount(); i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
            coordinatesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        trackCoordinatesPanel.getTableInfoLabel().setText("Unshifted Tracks Coordinates");
    }

    /**
     * For the given condition, show the normalised track coordinates in a
     * table.
     *
     * @param plateCondition
     */
    public void showShiftedTrackCoordinatesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] fixedDataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] shiftedTrackCoordinatesMatrix = singleCellPreProcessingResults.getShiftedTrackCoordinatesMatrix();
            coordinatesTable.setModel(new TrackCoordinatesTableModel(fixedDataStructure, shiftedTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            for (int i = 3; i < coordinatesTable.getColumnCount(); i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
            coordinatesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        trackCoordinatesPanel.getTableInfoLabel().setText("Tracks Coordinates with origins of migration superimposed at (0, 0) ");
    }

    /**
     * Generate randomly the tracks to put into the plot. This depends on the
     * category we want to generate the plot from: 0 is the condition level,
     * while 1 is the well level.
     *
     * @param category
     */
    public void generateRandomTrackDataHolders(int category) {
        // check if tracks need to be generated from within the same well or not
        switch (category) {
            case 0:
                generateRandomTrackHoldersForCondition();
                break;
            case 1:
                generateRandomTrackDataHoldersForWell();
                break;
        }
    }

    /**
     * Plot raw data track coordinates for current condition, specifying if raw
     * data need to be used and if points and/or lines need to be shown on the
     * plot.
     *
     *
     * @param plateCondition: the plate condition to plot the tracks from
     * @param useRawCoordinates: if true, plot raw data, else take the shifted
     * to zero coordinates.
     * @param plotLines: if true, lines need to be rendered on the plot the plot
     * @param plotPoints: if true, points need to be rendered on
     */
    public void plotRandomTrackCoordinates(PlateCondition plateCondition, boolean useRawCoordinates, boolean plotLines, boolean plotPoints) {
        // we get the selected index from the tabbed pane
        // according to this, we generate the random tracks from the condition or from the well
        int categoryToPlot = trackCoordinatesPanel.getTrackingPlotTabbedPane().getSelectedIndex();
        if (trackDataHolderBindingList.isEmpty()) {
            generateRandomTrackDataHolders(categoryToPlot);
        }
        // generate the xy collection for the plot
        XYSeriesCollection xYSeriesCollectionForPlot = generateXYSeriesCollectionForPlot(useRawCoordinates);
        // Plot Logic
        int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(plateCondition) + 1;
        int numberOfTracks = trackDataHolderBindingList.size();
        // title of the chart depends on:
        // 1. the category to plot (condition or well based?) and
        // 2. the coordinates to plot (raw data or shifted coordinates?)
        String chartTitle = "";
        switch (categoryToPlot) {
            case 0:
                if (useRawCoordinates) {
                    chartTitle = numberOfTracks + " tracks - condition " + conditionIndex;
                } else {
                    chartTitle = numberOfTracks + " tracks, coordinates shifted to (0, 0) - condition " + conditionIndex;
                }
                break;
            case 1:
                Well well = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
                if (useRawCoordinates) {
                    chartTitle = numberOfTracks + " tracks - well " + well.toString();
                } else {
                    chartTitle = numberOfTracks + " tracks, coordinates shifted to (0, 0) - well " + well.toString();
                }
                break;
        }
        // we finally create the coordinates chart, no legend but yes tooltips
        JFreeChart coordinatesChart = ChartFactory.createXYLineChart(chartTitle, "x (µm)", "y (µm)", xYSeriesCollectionForPlot, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupTrackChart(coordinatesChart);
        // get the current selected track from the JList, and use it to set a customized renderer
        int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
        TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints, selectedTrackIndex);
        coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
        coordinatesChartPanel.setChart(coordinatesChart);
        trackCoordinatesPanel.getGraphicsParentPanel().revalidate();
        trackCoordinatesPanel.getGraphicsParentPanel().repaint();
    }

    /**
     * Empty the list with the track data holders.
     */
    public void resetTracksList() {
        if (!trackDataHolderBindingList.isEmpty()) {
            trackDataHolderBindingList.clear();
        }
    }

    /**
     * private methods and classes
     */
    /**
     * Generate the xySeries for a plot.
     *
     * @param coordinatesToPlot
     * @return
     */
    private XYSeries generateXYSeries(Double[][] coordinatesToPlot) {
        // transpose the matrix
        Double[][] transposed = AnalysisUtils.transpose2DArray(coordinatesToPlot);
        // take first row: x coordinates
        double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[0]));
        // take second row: y coordinates
        double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[1]));
        // generate xy series for the plot
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(xCoordinates, yCoordinates);
        return xySeries;
    }

    /**
     * Initialize main panel
     */
    private void initTrackCoordinatesPanel() {
        // init new main panel
        trackCoordinatesPanel = new TrackCoordinatesPanel();
        // init well binding list
        wellBindingList = ObservableCollections.observableList(new ArrayList<Well>());
        trackDataHolderBindingList = ObservableCollections.observableList(new ArrayList<TrackDataHolder>());
        // init jcombo box binding: wells
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, wellBindingList, trackCoordinatesPanel.getWellsComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        // init jlist binding: track data holders
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, trackDataHolderBindingList, trackCoordinatesPanel.getPlottedTracksJList());
        bindingGroup.addBinding(jListBinding);
        // do the binding
        bindingGroup.bind();
        // set cell renderer for the tracks list
        trackCoordinatesPanel.getPlottedTracksJList().setCellRenderer(new PlottedTracksListRenderer(trackDataHolderBindingList));
        //init dataTable
        coordinatesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(coordinatesTable);
        //the table will take all the viewport height available
        coordinatesTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        coordinatesTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        coordinatesTable.setColumnSelectionAllowed(true);
        coordinatesTable.setRowSelectionAllowed(false);
        trackCoordinatesPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton());
        radioButtonGroup.add(trackCoordinatesPanel.getShiftedCoordinatesRadioButton());
        //select as default first button (raw data track coordinates Computation)
        trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().setSelected(true);
        trackCoordinatesPanel.getPlotLinesCheckBox().setSelected(true);
        trackCoordinatesPanel.getPlotPointsCheckBox().setSelected(false);
        //init chart panels
        coordinatesChartPanel = new ChartPanel(null);
        coordinatesChartPanel.setOpaque(false);
        // add chart mouse listener to the chart panel: clicking on a track will make the track selected in the list and it will be highlighed in the plot
        coordinatesChartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent e) {
                // get the entity on from which the chart mouse event has been generated
                ChartEntity chartEntity = e.getEntity();
                // check that we don't click just on the background of the plot
                if (chartEntity instanceof XYItemEntity) {
                    XYItemEntity xYItemEntity = (XYItemEntity) e.getEntity();
                    // get the series to highlight in the list and in the plot
                    int seriesIndex = xYItemEntity.getSeriesIndex();
                    trackCoordinatesPanel.getPlottedTracksJList().setSelectedIndex(seriesIndex);
                    // scroll the list to the selected index
                    trackCoordinatesPanel.getPlottedTracksJList().ensureIndexIsVisible(seriesIndex);
                    onSelectedTrack(seriesIndex);
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent e) {
            }
        });

        trackCoordinatesPanel.getGraphicsParentPanel().add(coordinatesChartPanel, gridBagConstraints);
        xtCoordinateChartPanel = new ChartPanel(null);
        xtCoordinateChartPanel.setOpaque(false);
        ytCoordinateChartPanel = new ChartPanel(null);
        ytCoordinateChartPanel.setOpaque(false);
        unshiftedTrackChartPanel = new ChartPanel(null);
        unshiftedTrackChartPanel.setOpaque(false);
        shiftedTrackChartPanel = new ChartPanel(null);
        shiftedTrackChartPanel.setOpaque(false);
        // init track data dialog
        trackDataDialog = new TrackDataDialog(singleCellPreProcessingController.getMainFrame(), true);
        trackDataDialog.getXtCoordinateParentPanel().add(xtCoordinateChartPanel, gridBagConstraints);
        trackDataDialog.getYtCoordinateParentPanel().add(ytCoordinateChartPanel, gridBagConstraints);
        trackDataDialog.getUnshiftedParentPanel().add(unshiftedTrackChartPanel, gridBagConstraints);
        trackDataDialog.getShiftedParentPanel().add(shiftedTrackChartPanel, gridBagConstraints);
        trackDataDialog.getTrackDataTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        trackDataDialog.getTrackDataTable().getTableHeader().setReorderingAllowed(false);
        
        /**
         * add action listeners
         */
        // raw track coordinates
        trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showRawTrackCoordinatesInTable(currentCondition);
                    boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                    boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                    plotRandomTrackCoordinates(currentCondition, true, plotLines, plotPoints);
                }
            }
        });

        // track coordinates normalized to first time point
        // this means that all the tracks start from the origin (0, 0)
        trackCoordinatesPanel.getShiftedCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showShiftedTrackCoordinatesInTable(currentCondition);
                    boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                    boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                    plotRandomTrackCoordinates(currentCondition, false, plotLines, plotPoints);
                }
            }
        });

        // plot lines
        trackCoordinatesPanel.getPlotLinesCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                JFreeChart coordinatesChart = coordinatesChartPanel.getChart();
                JFreeChartUtils.setupTrackChart(coordinatesChart);
                int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true, plotPoints, selectedTrackIndex);
                    coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotPoints) {
                        trackCoordinatesPanel.getPlotPointsCheckBox().setSelected(true);
                    }
                    TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(false, true, selectedTrackIndex);
                    coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
                }
            }
        });

        // plot points
        trackCoordinatesPanel.getPlotPointsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                JFreeChart coordinatesChart = coordinatesChartPanel.getChart();
                JFreeChartUtils.setupTrackChart(coordinatesChart);
                int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, true, selectedTrackIndex);
                    coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotLines) {
                        trackCoordinatesPanel.getPlotLinesCheckBox().setSelected(true);
                    }
                    TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true, false, selectedTrackIndex);
                    coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
                }
            }
        });

        // refresh plot with current selected option
        trackCoordinatesPanel.getRandomAndPlotButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                boolean useRawData = trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().isSelected();
                int categoryToPlotIndex = trackCoordinatesPanel.getTrackingPlotTabbedPane().getSelectedIndex();
                if (currentCondition != null) {
                    resetTracksList();
                    generateRandomTrackDataHolders(categoryToPlotIndex);
                    plotRandomTrackCoordinates(currentCondition, useRawData, plotLines, plotPoints);
                    trackCoordinatesPanel.getTimeSlider().setEnabled(false);
                    // need to disable the plot points and lines check boxes here
                    trackCoordinatesPanel.getPlotLinesCheckBox().setEnabled(true);
                    trackCoordinatesPanel.getPlotPointsCheckBox().setEnabled(true);
                    // reset to empty the x and y fields
                    trackCoordinatesPanel.getxTextField().setText("");
                    trackCoordinatesPanel.getyTextField().setText("");
                }
            }
        });

        // how many tracks for the selected well?
        trackCoordinatesPanel.getWellsComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
                List<TrackDataHolder> trackHoldersForCurrentWell = getTrackHoldersForWell(selectedWell);
                int numberTracksForCurrentWell = trackHoldersForCurrentWell.size();
                // update info with number of tracks for current selected well
                trackCoordinatesPanel.getTracksNumberCurrentWellLabel().setText(" " + numberTracksForCurrentWell);
            }
        });

        // plot all tracks for current condition: we use a swing worker
        trackCoordinatesPanel.getPlotAllTracksForAConditionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlotAllTracksConditionSwingWorker plotAllTracksConditionSwingWorker = new PlotAllTracksConditionSwingWorker();
                plotAllTracksConditionSwingWorker.execute();
            }
        });

        // plot all tracks for current well: we use a swing worker
        trackCoordinatesPanel.getPlotAllTracksForAWellButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlotAllTracksWellSwingWorker plotAllTracksWellSwingWorker = new PlotAllTracksWellSwingWorker();
                plotAllTracksWellSwingWorker.execute();
            }
        });

        // show dialog with the currently selected track data + plots
        trackCoordinatesPanel.getShowDataForSelectedTrackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackDataHolder trackDataHolder = (TrackDataHolder) trackCoordinatesPanel.getPlottedTracksJList().getSelectedValue();
                if (trackDataHolder != null) {
                    showTrackDataDialog(trackDataHolder);
                }
            }
        });

        // select a track and highlight it in the current plot
        trackCoordinatesPanel.getPlottedTracksJList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
                    if (selectedTrackIndex != -1) {
                        onSelectedTrack(selectedTrackIndex);
                    }
                }
            }
        });

        // clear selection on the tracks list
        trackCoordinatesPanel.getClearSelectionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // clear the selection on the list
                trackCoordinatesPanel.getPlottedTracksJList().clearSelection();
                // refresh the plot
                JFreeChart coordinatesChart = coordinatesChartPanel.getChart();
                JFreeChartUtils.setupTrackChart(coordinatesChart);
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
                TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints, selectedTrackIndex);
                coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
                // disable the time slider
                trackCoordinatesPanel.getTimeSlider().setEnabled(false);
                // need to enable the plot points and lines check boxes here
                trackCoordinatesPanel.getPlotLinesCheckBox().setEnabled(true);
                trackCoordinatesPanel.getPlotPointsCheckBox().setEnabled(true);
                // reset to empty the x and y fields
                trackCoordinatesPanel.getxTextField().setText("");
                trackCoordinatesPanel.getyTextField().setText("");
            }
        });

        // add change listener to the slider
        trackCoordinatesPanel.getTimeSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                // we don't check for the adjustment of the value, because we want a continuous time scale
                int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
                if (selectedTrackIndex != -1) {
                    // get the current time value from the slider
                    int timePoint = (int) source.getValue();
                    // show the track point in time: basically set the renderer for the chart
                    showTrackPointInTime(selectedTrackIndex, timePoint);
                    // update x and y coordinates field
                    updateCoordinatesInfoInTime(selectedTrackIndex, timePoint);
                }
            }
        });

        // disable the time slider at first, only enable it when a single track is being selected in the plot or in the tracks list
        trackCoordinatesPanel.getTimeSlider().setEnabled(false);
        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesParentPanel().add(trackCoordinatesPanel, gridBagConstraints);
    }

    /**
     * Action performed on selection of a single track: set the renderer for the
     * x y plot and control some GUI elements.
     *
     * @param selectedTrackIndex: the index of the selected track
     */
    private void onSelectedTrack(int selectedTrackIndex) {
        TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true, false, selectedTrackIndex);
        coordinatesChartPanel.getChart().getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
        TrackDataHolder selectedTrackDataHolder = trackDataHolderBindingList.get(selectedTrackIndex);
        // set up and enable the time /slider here
        setupTimeSlider(selectedTrackDataHolder);
        trackCoordinatesPanel.getTimeSlider().setEnabled(true);
        // need to disable the plot points and lines check boxes here
        trackCoordinatesPanel.getPlotLinesCheckBox().setEnabled(false);
        trackCoordinatesPanel.getPlotPointsCheckBox().setEnabled(false);
    }

    /**
     * Given a track and a time point, get current x and y positions and update
     * info.
     *
     * @param selectedTrackIndex
     * @param timePoint
     */
    private void updateCoordinatesInfoInTime(int selectedTrackIndex, int timePoint) {
        // update x and y coordinates field
        XYDataset dataset = coordinatesChartPanel.getChart().getXYPlot().getDataset();
        double xValue = dataset.getXValue(selectedTrackIndex, timePoint);
        double yValue = dataset.getYValue(selectedTrackIndex, timePoint);
        trackCoordinatesPanel.getxTextField().setText(" " + xValue);
        trackCoordinatesPanel.getyTextField().setText(" " + yValue);
    }

    /**
     * Set up a time slider: this will depend on the selected track data, since
     * each track has been detected for a different time interval.
     *
     * @param selectedTrackDataHolder
     */
    private void setupTimeSlider(TrackDataHolder selectedTrackDataHolder) {
        JSlider timeSlider = singleCellPreProcessingController.getTrackCoordinatesPanel().getTimeSlider();
        double[] timeIndexes = selectedTrackDataHolder.getTimeIndexes();
        timeSlider.setMinimum(0);
        int numberOfTimePoints = timeIndexes.length;
        int spacing = (int) numberOfTimePoints / 5;
        timeSlider.setMaximum(numberOfTimePoints - 1); // this triggers the stateChanged
        timeSlider.setMajorTickSpacing(spacing);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        timeSlider.setValue(0); // this triggers the stateChanged
        Hashtable labelsTable = new Hashtable();
        // adjust the labels of the time slider to actually show the real time points
        for (int i = 0; i < numberOfTimePoints - 1; i++) {
            double actualTimePoint = timeIndexes[i];
            labelsTable.put(i, new JLabel("" + actualTimePoint));
            i += spacing - 1;
        }
        timeSlider.setLabelTable(labelsTable);
    }

    /**
     * Show the evolution of a track in time: while sliding on the time slide, a
     * cell is followed in time and a spot is highlighted.
     *
     * @param trackSeriesIndex: the series (track) index
     * @param currentTimePoint: the actual time point to highlight
     */
    private void showTrackPointInTime(int selectedTrackIndex, int timePoint) {
        // get the xyplot from the chart and set it up
        XYPlot xyPlot = coordinatesChartPanel.getChart().getXYPlot();
        TimePointTrackXYLineAndShapeRenderer timePointTrackXYLineAndShapeRenderer = new TimePointTrackXYLineAndShapeRenderer(selectedTrackIndex, timePoint);
        xyPlot.setRenderer(timePointTrackXYLineAndShapeRenderer);
    }

    /**
     * Show the track data dialog for a certain track data holder selected.
     *
     * @param trackDataHolder
     */
    private void showTrackDataDialog(TrackDataHolder trackDataHolder) {
        // plot the data associated with the current track
        plotSingleTrackData(trackDataHolder);
        // update model for the track table
        trackDataDialog.getTrackDataTable().setModel(new TrackDataHolderTableModel(trackDataHolder));
        trackDataDialog.getTrackDataTable().getColumnModel().getColumn(1).setCellRenderer(new AlignedTableRenderer(SwingConstants.LEFT));
        // pack and show the dialog with the plots
        trackDataDialog.pack();
        GuiUtils.centerDialogOnFrame(singleCellPreProcessingController.getMainFrame(), trackDataDialog);
        trackDataDialog.setVisible(true);
    }

    /**
     * Make the plots for the single track.
     *
     * @param trackDataHolder
     */
    private void plotSingleTrackData(TrackDataHolder trackDataHolder) {
        // plot x and y coordinates in time
        plotCoordinatesInTime(trackDataHolder);
        // plot the unshifted and the shifted track coordinates
        plotUnshiftedCoordinates(trackDataHolder);
        plotShiftedCoordinates(trackDataHolder);
    }

    /**
     * Plot x and y coordinates in time for the given track. The track is
     * actually get from the track data holder object.
     *
     * @param track
     */
    private void plotCoordinatesInTime(TrackDataHolder trackDataHolder) {
        // get the selected track data holder, and thus the track to plot in time
        Track track = trackDataHolder.getTrack();
        // get the track coordinates matrix
        Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
        // we need to transpose the matrix
        Double[][] transpose2DArray = AnalysisUtils.transpose2DArray(trackCoordinatesMatrix);
        // we get the x coordinates and the time information
        double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transpose2DArray[0]));
        double[] timeIndexes = trackDataHolder.getTimeIndexes();
        // we create the series and set its key
        XYSeries xtSeries = JFreeChartUtils.generateXYSeries(timeIndexes, xCoordinates);
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        xtSeries.setKey(seriesKey);
        // we then create the XYSeriesCollection and use it to make a new line chart
        XYSeriesCollection xtSeriesCollection = new XYSeriesCollection(xtSeries);
        JFreeChart xtCoordinatesChart = ChartFactory.createXYLineChart(seriesKey + " - x over time", "time index", "x (µm)", xtSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        // set up the plot of the chart
        JFreeChartUtils.setupSingleTrackPlot(xtCoordinatesChart, trackDataHolderBindingList.indexOf(trackDataHolder), true);
        xtCoordinateChartPanel.setChart(xtCoordinatesChart);
        // we repeat exactly the same with the y coordinates in time
        double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transpose2DArray[1]));
        XYSeries ytSeries = JFreeChartUtils.generateXYSeries(timeIndexes, yCoordinates);
        ytSeries.setKey(seriesKey);
        XYSeriesCollection ytSeriesCollection = new XYSeriesCollection(ytSeries);
        JFreeChart ytCoordinatesChart = ChartFactory.createXYLineChart(seriesKey + " - y over time", "time index", "y (µm)", ytSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        // set up the plot of the chart
        JFreeChartUtils.setupSingleTrackPlot(ytCoordinatesChart, trackDataHolderBindingList.indexOf(trackDataHolder), true);
        ytCoordinateChartPanel.setChart(ytCoordinatesChart);
    }

    /**
     * Plot the unshifted track coordinates for the current track. We get the
     * current track from the selected track data holder object.
     *
     * @param trackDataHolder
     */
    private void plotUnshiftedCoordinates(TrackDataHolder trackDataHolder) {
        // get the coordinates matrix
        Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
        XYSeries xYSeries = generateXYSeries(trackCoordinatesMatrix);
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        xYSeries.setKey(seriesKey);
        XYSeriesCollection ySeriesCollection = new XYSeriesCollection(xYSeries);
        JFreeChart unshiftedCoordinatesChart = ChartFactory.createXYLineChart(seriesKey + " - unshifted coordinates", "x (µm)", "y (µm)", ySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupSingleTrackPlot(unshiftedCoordinatesChart, trackDataHolderBindingList.indexOf(trackDataHolder), false);
        unshiftedTrackChartPanel.setChart(unshiftedCoordinatesChart);
    }

    /**
     * Plot the shifted track coordinates for the current track. We get the
     * current track from the selected track data holder object.
     *
     * @param trackDataHolder
     */
    private void plotShiftedCoordinates(TrackDataHolder trackDataHolder) {
        // get the coordinates matrix
        Double[][] shiftedTrackCoordinates = trackDataHolder.getShiftedTrackCoordinates();
        XYSeries xYSeries = generateXYSeries(shiftedTrackCoordinates);
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        xYSeries.setKey(seriesKey);
        XYSeriesCollection ySeriesCollection = new XYSeriesCollection(xYSeries);
        JFreeChart shiftedCoordinatesChart = ChartFactory.createXYLineChart(seriesKey + " - coordinates shifted to (0, 0)", "x (µm)", "y (µm)", ySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupSingleTrackPlot(shiftedCoordinatesChart, trackDataHolderBindingList.indexOf(trackDataHolder), false);
        shiftedTrackChartPanel.setChart(shiftedCoordinatesChart);
    }

    /**
     * Generate XYSeriesCollection for the tracks plot. This will depend on the
     * data to plot: the raw coordinates (boolean is true) or the shifted to
     * zero coordinates (boolean is false)?
     *
     * @param useRawCoordinates
     * @return a XYSeriesCollection
     */
    private XYSeriesCollection generateXYSeriesCollectionForPlot(boolean useRawCoordinates) {
        XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
        // the matrix to use is either the raw coordinates matrix or the shifted matrix
        Double[][] coordinatesMatrix;
        // this is not the best way to fix this multiple locations issue, but for the moment fair enough !!
        int counter = 0;
        for (TrackDataHolder trackDataHolder : trackDataHolderBindingList) {
            if (useRawCoordinates) {
                coordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
            } else {
                coordinatesMatrix = trackDataHolder.getShiftedTrackCoordinates();
            }
            XYSeries xySeries = generateXYSeries(coordinatesMatrix);
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
    }

    /**
     * Generate the random track holders within a well.
     */
    private void generateRandomTrackDataHoldersForWell() {
        // get only the track holders for the selected well
        Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
        List<TrackDataHolder> trackHoldersForCurrentWell = getTrackHoldersForWell(selectedWell);
        int numberTracksForCurrentWell = trackHoldersForCurrentWell.size();
        // update info with number of tracks for current selected well
        trackCoordinatesPanel.getTracksNumberCurrentWellLabel().setText("" + numberTracksForCurrentWell);
        // if the user does not write anything, number of tracks to be plotted is set to default
        String text = trackCoordinatesPanel.getRandomTracksNumberTextField().getText();
        // the default is set to 10, if possible, otherwise is less
        int defaultNumberOfTracks = getDefaultNumberOfTracks(numberTracksForCurrentWell);
        int randomTracksNumber = defaultNumberOfTracks;
        if (!text.isEmpty()) {
            try {
                randomTracksNumber = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage());
                String message = "Please insert a valid number of tracks!" + "\nDefault set back to: " + defaultNumberOfTracks + " tracks";
                singleCellPreProcessingController.showMessage(message, "error setting number of tracks", JOptionPane.ERROR_MESSAGE);
            }
        }
        // the # of tracks that you want to plot need to be less or equal to # of tracks for the current well
        // else, show an info message and set the number back to default
        if (randomTracksNumber > numberTracksForCurrentWell) {
            String message = "This well has " + numberTracksForCurrentWell + " tracks" + "\nI cannot plot " + randomTracksNumber + " tracks!" + "\nDefault set back to: " + defaultNumberOfTracks + " tracks";
            singleCellPreProcessingController.showMessage(message, "error in setting number of tracks", JOptionPane.WARNING_MESSAGE);
            randomTracksNumber = defaultNumberOfTracks;
        }
        // update text field
        trackCoordinatesPanel.getRandomTracksNumberTextField().setText("" + randomTracksNumber);
        // pick up randomly the track holders and add them to the list
        for (int j = 0; j < randomTracksNumber; j++) {
            // create a random number and take its int part
            Double random = Math.random() * numberTracksForCurrentWell;
            int intValue = random.intValue();
            TrackDataHolder randomTrackDataHolder = trackHoldersForCurrentWell.get(intValue);
            // make sure we do not use twice the same random track
            if (!trackDataHolderBindingList.contains(randomTrackDataHolder)) {
                trackDataHolderBindingList.add(randomTrackDataHolder);
            } else {
                j--;
            }
        }
    }

    /**
     * Generate the random track holders across random wells of a particular
     * condition.
     */
    private void generateRandomTrackHoldersForCondition() {
        // get track holders for current condition
        List<TrackDataHolder> trackHoldersForCurrentCondition = getTrackHoldersForCurrentCondition();
        int tracksNumberForCondition = trackHoldersForCurrentCondition.size();
        // if the user does not write anything, number of tracks to be plotted is set to default
        String text = trackCoordinatesPanel.getRandomTracksNumberTextField().getText();
        // the default is set to 10, if possible, otherwise is less
        int defaultNumberOfTracks = getDefaultNumberOfTracks(tracksNumberForCondition);
        int randomTracksNumber = defaultNumberOfTracks;
        if (!text.isEmpty()) {
            try {
                randomTracksNumber = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage());
                String message = "Please insert a valid number of tracks!" + "\nDefault set back to: " + defaultNumberOfTracks + " tracks";
                singleCellPreProcessingController.showMessage(message, "error setting number of tracks", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (randomTracksNumber > tracksNumberForCondition) {
            String message = "This condition has " + tracksNumberForCondition + " tracks" + "\nI cannot plot " + randomTracksNumber + " tracks!" + "\nDefault set back to: " + defaultNumberOfTracks + " tracks";
            singleCellPreProcessingController.showMessage(message, "error in setting number of tracks", JOptionPane.WARNING_MESSAGE);
            randomTracksNumber = defaultNumberOfTracks;
        }
        // update text field
        trackCoordinatesPanel.getRandomTracksNumberTextField().setText("" + randomTracksNumber);
        // pick up randomly the track holders and add them to the list
        for (int j = 0; j < randomTracksNumber; j++) {
            // create a random number and take its int part
            Double random = Math.random() * tracksNumberForCondition;
            int intValue = random.intValue();
            TrackDataHolder randomTrackDataHolder = trackHoldersForCurrentCondition.get(intValue);
            // make sure we do not use twice the same random track
            if (!trackDataHolderBindingList.contains(randomTrackDataHolder)) {
                trackDataHolderBindingList.add(randomTrackDataHolder);
            } else {
                j--;
            }
        }
    }

    /**
     * The default number of tracks to be plotted is 10; if the max available
     * number of tracks is less than 10, the default is decreased, until we get
     * the right number of tracks.
     *
     * @param maxTracks
     * @return the default number
     */
    private int getDefaultNumberOfTracks(int maxTracks) {
        int defaultNumber = 10;
        if (defaultNumber > maxTracks) {
            defaultNumber--;
        }
        return defaultNumber;
    }

    /**
     * From the entire track holders for the selected current condition, get
     * only the ones for the selected well.
     *
     * @param well
     * @return
     */
    private List<TrackDataHolder> getTrackHoldersForWell(Well well) {
        // first, get the track holders for the current condition
        List<TrackDataHolder> trackHoldersForCurrentCondition = getTrackHoldersForCurrentCondition();
        List<TrackDataHolder> trackHoldersForWell = new ArrayList<>();
        // then, get the selected well, and filter only the results from this well
        for (TrackDataHolder trackDataHolder : trackHoldersForCurrentCondition) {
            if (trackDataHolder.getTrack().getWellHasImagingType().getWell().equals(well)) {
                trackHoldersForWell.add(trackDataHolder);
            }
        }
        return trackHoldersForWell;
    }

    /**
     * Through the parent controller, get the track data holders for the current
     * condition.
     *
     * @return the track data holders
     */
    private List<TrackDataHolder> getTrackHoldersForCurrentCondition() {
        PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
        // through the map, we get the pre processing results for the current condition
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(currentCondition);
        return singleCellPreProcessingResults.getTrackDataHolders();
    }

    /**
     * For the current condition, generate the list with all the track data
     * holders.
     */
    private void generateAllTrackHoldersForCurrentCondition() {
        List<TrackDataHolder> trackHoldersForCurrentCondition = getTrackHoldersForCurrentCondition();
        trackDataHolderBindingList.addAll(trackHoldersForCurrentCondition);
    }

    /**
     * For a certain well, generate the list with all the track data holders.
     *
     * @param well
     */
    private void generateAllTrackHoldersForCurrentWell(Well well) {
        List<TrackDataHolder> trackHoldersForWell = getTrackHoldersForWell(well);
        trackDataHolderBindingList.addAll(trackHoldersForWell);
    }

    /**
     * Get the total number of tracks for the current condition.
     *
     * @return the number.
     */
    private int getTrackNumberForCondition() {
        return getTrackHoldersForCurrentCondition().size();
    }

    /**
     * Swing Worker to plot all track together!
     */
    private class PlotAllTracksConditionSwingWorker extends SwingWorker<Void, Void> {

        private boolean useRawCoordinates = trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().isSelected();
        private XYSeriesCollection xYSeriesCollection;

        @Override
        protected Void doInBackground() throws Exception {
            singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            resetTracksList();
            generateAllTrackHoldersForCurrentCondition();
            xYSeriesCollection = generateXYSeriesCollectionForPlot(useRawCoordinates);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                trackCoordinatesPanel.getTimeSlider().setEnabled(false);
                // need to enable the plot points and lines check boxes here
                trackCoordinatesPanel.getPlotLinesCheckBox().setEnabled(true);
                trackCoordinatesPanel.getPlotPointsCheckBox().setEnabled(true);
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                // Plot Logic
                int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(currentCondition) + 1;
                String chartTitle;
                if (useRawCoordinates) {
                    chartTitle = trackDataHolderBindingList.size() + " tracks - condition " + conditionIndex;
                } else {
                    chartTitle = trackDataHolderBindingList.size() + " tracks, coordinates shifted to (0, 0) - condition " + conditionIndex;
                }
                JFreeChart coordinatesChart = ChartFactory.createXYLineChart(chartTitle, "x (µm)", "y (µm)", xYSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
                int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
                JFreeChartUtils.setupTrackChart(coordinatesChart);
                TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints, selectedTrackIndex);
                coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
                coordinatesChartPanel.setChart(coordinatesChart);
                trackCoordinatesPanel.getGraphicsParentPanel().revalidate();
                trackCoordinatesPanel.getGraphicsParentPanel().repaint();
                singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellPreProcessingController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Swing Worker to plot all track together!
     */
    private class PlotAllTracksWellSwingWorker extends SwingWorker<Void, Void> {

        boolean useRawCoordinates = trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().isSelected();
        private Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
        private XYSeriesCollection xYSeriesCollection;

        @Override
        protected Void doInBackground() throws Exception {
            singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            resetTracksList();
            generateAllTrackHoldersForCurrentWell(selectedWell);
            xYSeriesCollection = generateXYSeriesCollectionForPlot(useRawCoordinates);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                trackCoordinatesPanel.getTimeSlider().setEnabled(false);
                // need to enable the plot points and lines check boxes here
                trackCoordinatesPanel.getPlotLinesCheckBox().setEnabled(true);
                trackCoordinatesPanel.getPlotPointsCheckBox().setEnabled(true);
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                // Plot Logic
                String chartTitle;
                if (useRawCoordinates) {
                    chartTitle = trackDataHolderBindingList.size() + " tracks - well " + selectedWell.toString();
                } else {
                    chartTitle = trackDataHolderBindingList.size() + " tracks,  coordinates shifted to (0, 0) - well " + selectedWell.toString();
                }
                JFreeChart coordinatesChart = ChartFactory.createXYLineChart(chartTitle, "x (µm)", "y (µm)", xYSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
                int selectedTrackIndex = trackCoordinatesPanel.getPlottedTracksJList().getSelectedIndex();
                JFreeChartUtils.setupTrackChart(coordinatesChart);
                TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints, selectedTrackIndex);
                coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
                coordinatesChartPanel.setChart(coordinatesChart);
                trackCoordinatesPanel.getGraphicsParentPanel().revalidate();
                trackCoordinatesPanel.getGraphicsParentPanel().repaint();
                singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellPreProcessingController.handleUnexpectedError(ex);
            }
        }
    }
}
