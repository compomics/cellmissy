/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.ExploreTrackPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsRendererGiver;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsMenuBar;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TimePointTrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.PlottedTracksListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.SingleCellDataTableRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.ConvexHullTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataHolderTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of logic for exploring a track. Parent controller:
 * track coordinates controller.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("exploreTrackController")
class ExploreTrackController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExploreTrackController.class);
    // model
    private BindingGroup bindingGroup;
    private PlayTrackSwingWorker playTrackSwingWorker;
    // view
    private ExploreTrackPanel exploreTrackPanel;
    private PlotSettingsMenuBar plotSettingsMenuBar;
    private ChartPanel coordinatesChartPanel;
    private ChartPanel xYTCoordinateChartPanel;
    private ChartPanel displacementTChartPanel;
    private ChartPanel singleTrackCoordinatesChartPanel;
    private ChartPanel convexHullChartPanel;
    private ChartPanel histogramChartPanel;
    private ChartPanel polarPlotChartPanel;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // child controller
    @Autowired
    private DirectionTrackController directionTrackController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initPlotSettingsMenuBar();
        // init main view
        initExploreTrackPanel();
        // init child controller
        directionTrackController.init();
    }

    /**
     * Getters
     */
    public ChartPanel getCoordinatesChartPanel() {
        return coordinatesChartPanel;
    }

    public ExploreTrackPanel getExploreTrackPanel() {
        return exploreTrackPanel;
    }

    public ObservableList<TrackDataHolder> getTrackDataHolderBindingList() {
        return trackCoordinatesController.getTrackDataHolderBindingList();
    }

    public TrackDataHolder getSelectedTrackDataHolder() {
        return trackCoordinatesController.getTrackDataHolderBindingList().get(exploreTrackPanel.getTracksList().getSelectedIndex());
    }

    /**
     * Private methods
     */
    /**
     * Action performed on selection of a single track: set the renderer for the
     * x y plot and control some GUI elements.
     *
     * @param selectedTrackIndex: the index of the selected track
     */
    private void onSelectedTrack(int selectedTrackIndex) {
        TrackDataHolder selectedTrackDataHolder = trackCoordinatesController.getTrackDataHolderBindingList().get(selectedTrackIndex);
        // set up and enable the time /slider here
        setupTimeSlider(selectedTrackDataHolder);
        updateTrackData(selectedTrackDataHolder);
        updateConvexHullData(selectedTrackDataHolder);
        boolean plotLines = plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().isSelected();
        boolean plotPoints = plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().isSelected();
        boolean showEndPoints = plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().isSelected();
        Float lineWidth = plotSettingsMenuBar.getSelectedLineWidth();
        TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints,
                  showEndPoints, trackCoordinatesController.getEndPoints(), selectedTrackIndex, lineWidth, false);
        coordinatesChartPanel.getChart().getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
    }

    /**
     * Initialize plot settings menu bar
     */
    private void initPlotSettingsMenuBar() {
        // create new object
        plotSettingsMenuBar = new PlotSettingsMenuBar();
        /**
         * Add item listeners to the menu items
         */
        ItemActionListener itemActionListener = new ItemActionListener();
        plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().addItemListener(itemActionListener);
        plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().addItemListener(itemActionListener);
        plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().addItemListener(itemActionListener);
        for (Enumeration<AbstractButton> buttons = plotSettingsMenuBar.getLinesButtonGroup().getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            button.addItemListener(itemActionListener);
        }

        plotSettingsMenuBar.getUseCellMissyColors().addItemListener(new ColorItemActionListener());

    }

    /**
     * Initialize main view
     */
    private void initExploreTrackPanel() {
        // new view
        exploreTrackPanel = new ExploreTrackPanel();
        ObservableList<TrackDataHolder> trackDataHolderBindingList = trackCoordinatesController.getTrackDataHolderBindingList();
        // init jlist binding: track data holders
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, trackDataHolderBindingList, exploreTrackPanel.getTracksList());
        bindingGroup.addBinding(jListBinding);
        // do the binding
        bindingGroup.bind();
        // set cell renderer for the tracks list
        exploreTrackPanel.getTracksList().setCellRenderer(new PlottedTracksListRenderer(trackDataHolderBindingList));
        //init chart panels
        coordinatesChartPanel = new ChartPanel(null);
        coordinatesChartPanel.setOpaque(false);
        exploreTrackPanel.getGraphicsParentPanel().add(coordinatesChartPanel, gridBagConstraints);
        xYTCoordinateChartPanel = new ChartPanel(null);
        xYTCoordinateChartPanel.setOpaque(false);
        displacementTChartPanel = new ChartPanel(null);
        displacementTChartPanel.setOpaque(false);

        singleTrackCoordinatesChartPanel = new ChartPanel(null);
        singleTrackCoordinatesChartPanel.setOpaque(false);
        convexHullChartPanel = new ChartPanel(null);
        convexHullChartPanel.setOpaque(false);

        histogramChartPanel = new ChartPanel(null);
        histogramChartPanel.setOpaque(false);

        polarPlotChartPanel = new ChartPanel(null);
        polarPlotChartPanel.setOpaque(false);

        exploreTrackPanel.getxYTCoordinatesParentPanel().add(xYTCoordinateChartPanel, gridBagConstraints);
        exploreTrackPanel.getDisplacementTParentPanel().add(displacementTChartPanel, gridBagConstraints);

        exploreTrackPanel.getCoordinatesParentPanel().add(singleTrackCoordinatesChartPanel, gridBagConstraints);
        exploreTrackPanel.getConvexHullGraphicsParentPanel().add(convexHullChartPanel, gridBagConstraints);

        exploreTrackPanel.getHistogramParentPanel().add(histogramChartPanel, gridBagConstraints);
        exploreTrackPanel.getPolarPlotParentPanel().add(polarPlotChartPanel, gridBagConstraints);

        exploreTrackPanel.getTrackDataTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        exploreTrackPanel.getTrackDataTable().getTableHeader().setReorderingAllowed(false);
        exploreTrackPanel.getConvexHullTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        exploreTrackPanel.getConvexHullTable().getTableHeader().setReorderingAllowed(false);

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
                    exploreTrackPanel.getTracksList().setSelectedIndex(seriesIndex);
                    // scroll the list to the selected index
                    exploreTrackPanel.getTracksList().ensureIndexIsVisible(seriesIndex);
                    onSelectedTrack(seriesIndex);
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent e) {
            }
        });

        exploreTrackPanel.getGraphicsParentPanel().add(coordinatesChartPanel, gridBagConstraints);
        // add change listener to the slider
        exploreTrackPanel.getTimeSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                // we don't check for the adjustment of the value, because we want a continuous time scale
                int selectedTrackIndex = exploreTrackPanel.getTracksList().getSelectedIndex();
                if (selectedTrackIndex != -1) {
                    // get the current time value from the slider
                    int timePoint = source.getValue();
                    // show the track point in time: basically set the renderer for the chart
                    showTrackPointInTime(selectedTrackIndex, timePoint);
                    // update x and y coordinates field
                    updateCoordinatesInfoInTime(selectedTrackIndex, timePoint);
                }
            }
        });

        // action listeners
        // play a track in time
        exploreTrackPanel.getPlayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedTrackIndex = exploreTrackPanel.getTracksList().getSelectedIndex();
                if (selectedTrackIndex != -1) {
                    playTrack(selectedTrackIndex);
                }
            }
        });

        // stop a track in time
        exploreTrackPanel.getStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check for the status of the start button, if enables, worker is ongoing, we can stop it
                if (!exploreTrackPanel.getPlayButton().isEnabled()) {
                    // stop the worker
                    playTrackSwingWorker.cancel(true);
                    // enable the play button
                    exploreTrackPanel.getPlayButton().setEnabled(true);
                }
            }
        });

        // select a track and highlight it in the current plot
        exploreTrackPanel.getTracksList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedTrackIndex = exploreTrackPanel.getTracksList().getSelectedIndex();
                    if (selectedTrackIndex != -1) {
                        onSelectedTrack(selectedTrackIndex);
                    }
                }
            }
        });

        // clear selection on the tracks list
        exploreTrackPanel.getClearSelectionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClearSelection();
            }
        });

        exploreTrackPanel.getPlotSettingsPanel().add(plotSettingsMenuBar, BorderLayout.EAST);

        // add view to parent panel
        trackCoordinatesController.getTrackCoordinatesPanel().getExploreTrackParentPanel().add(exploreTrackPanel, gridBagConstraints);
    }

    /**
     * On clear selection on the list
     */
    private void onClearSelection() {
        // clear the selection on the list
        exploreTrackPanel.getTracksList().clearSelection();
        // refresh the plot
        JFreeChart coordinatesChart = coordinatesChartPanel.getChart();
        JFreeChartUtils.setupTrackChart(coordinatesChart);
        int length = GuiUtils.getAvailableColors().length;
        int conditionIndex = trackCoordinatesController.getPlateConditionList().indexOf(trackCoordinatesController.getCurrentCondition());
        boolean plotLines = plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().isSelected();
        boolean plotPoints = plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().isSelected();
        boolean showEndPoints = plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().isSelected();
        boolean useSingleColor = plotSettingsMenuBar.getUseCellMissyColors().isSelected();
        int selectedTrackIndex = exploreTrackPanel.getTracksList().getSelectedIndex();
        Float lineWidth = plotSettingsMenuBar.getSelectedLineWidth();
        TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints, showEndPoints,
                  trackCoordinatesController.getEndPoints(), selectedTrackIndex, lineWidth, useSingleColor);
        trackXYLineAndShapeRenderer.setChosenColor(GuiUtils.getAvailableColors()[conditionIndex % length]);
        coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
        // @todo: reset the time slider, null pointer exception !
//        exploreTrackPanel.getTimeSlider().setLabelTable(null);
    }

    /**
     * Action Listener for MenuItems
     */
    private class ItemActionListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            int selectedTrackIndex = exploreTrackPanel.getTracksList().getSelectedIndex();
            int length = GuiUtils.getAvailableColors().length;
            int conditionIndex = trackCoordinatesController.getPlateConditionList().indexOf(trackCoordinatesController.getCurrentCondition());
            List<Integer> endPoints = trackCoordinatesController.getEndPoints();
            PlotSettingsRendererGiver plotSettingsRendererGiver = new PlotSettingsRendererGiver(selectedTrackIndex, plotSettingsMenuBar, endPoints);
            TrackXYLineAndShapeRenderer renderer = plotSettingsRendererGiver.getRenderer(e);
            renderer.setChosenColor(GuiUtils.getAvailableColors()[conditionIndex % length]);
            coordinatesChartPanel.getChart().getXYPlot().setRenderer(renderer);
        }
    }

    /**
     * For the color menu item, a Color Chooser has to be shown for the user to
     * select the color to use.
     */
    private class ColorItemActionListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            int selectedTrackIndex = -1;
            int length = GuiUtils.getAvailableColors().length;
            int conditionIndex = trackCoordinatesController.getPlateConditionList().indexOf(trackCoordinatesController.getCurrentCondition());
            List<Integer> endPoints = trackCoordinatesController.getEndPoints();
            PlotSettingsRendererGiver plotSettingsRendererGiver = new PlotSettingsRendererGiver(selectedTrackIndex,
                      plotSettingsMenuBar, endPoints);
            TrackXYLineAndShapeRenderer renderer = plotSettingsRendererGiver.getRenderer(e);
            if (e.getStateChange() == ItemEvent.SELECTED) {
                renderer.setChosenColor(GuiUtils.getAvailableColors()[conditionIndex % length]);
            }
            coordinatesChartPanel.getChart().getXYPlot().setRenderer(renderer);
        }
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
        exploreTrackPanel.getxTextField().setText(" " + xValue);
        exploreTrackPanel.getyTextField().setText(" " + yValue);
    }

    /**
     * Set up a time slider: this will depend on the selected track data, since
     * each track has been detected for a different time interval.
     *
     * @param selectedTrackDataHolder
     */
    private void setupTimeSlider(TrackDataHolder selectedTrackDataHolder) {
        JSlider timeSlider = exploreTrackPanel.getTimeSlider();
        double[] timeIndexes = selectedTrackDataHolder.getStepCentricDataHolder().getTimeIndexes();
        timeSlider.setMinimum(0);
        int numberOfTimePoints = timeIndexes.length;
        int spacing = numberOfTimePoints / 5;
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
     * @param selectedTrackIndex: the series (track) index
     * @param timePoint: the actual time point to highlight
     */
    private void showTrackPointInTime(int selectedTrackIndex, int timePoint) {
        // get the xyplot from the chart and set it up
        XYPlot xyPlot = coordinatesChartPanel.getChart().getXYPlot();
        Float selectedLineWidth = plotSettingsMenuBar.getSelectedLineWidth();
        TimePointTrackXYLineAndShapeRenderer timePointTrackXYLineAndShapeRenderer = new TimePointTrackXYLineAndShapeRenderer(selectedTrackIndex, timePoint, selectedLineWidth);
        xyPlot.setRenderer(timePointTrackXYLineAndShapeRenderer);
    }

    /**
     * Play a track in time (using a swing worker).
     *
     * @param selectedTrackIndex
     */
    private void playTrack(int selectedTrackIndex) {
        // create a bnew instance of the play swing worker and execute it
        playTrackSwingWorker = new PlayTrackSwingWorker(selectedTrackIndex);
        playTrackSwingWorker.execute();
    }

    /**
     * Update data in the table for the selected track.
     *
     * @param trackDataHolder
     */
    private void updateTrackData(TrackDataHolder trackDataHolder) {
        // update the combo box with the time intervals
        directionTrackController.updateDeltaTComboBox(trackDataHolder);
        // plot the data associated with the current track
        plotSingleTrackData(trackDataHolder);
        // update model for the track table
        exploreTrackPanel.getTrackDataTable().setModel(new TrackDataHolderTableModel(trackDataHolder));
        SingleCellDataTableRenderer singleCellDataTableRenderer = new SingleCellDataTableRenderer(new DecimalFormat("###.###"));
        for (int i = 0; i < exploreTrackPanel.getTrackDataTable().getColumnCount(); i++) {
            exploreTrackPanel.getTrackDataTable().getColumnModel().getColumn(i).setCellRenderer(singleCellDataTableRenderer);
        }
        for (int i = 0; i < exploreTrackPanel.getTrackDataTable().getColumnCount(); i++) {
            GuiUtils.packColumn(exploreTrackPanel.getTrackDataTable(), i);
        }
    }

    /**
     * Update measurements of convex hull for a given selected track.
     *
     * @param trackDataHolder
     */
    private void updateConvexHullData(TrackDataHolder trackDataHolder) {
        // update convex hull data in table
        ConvexHull convexHull = trackDataHolder.getCellCentricDataHolder().getConvexHull();
        exploreTrackPanel.getConvexHullTable().setModel(new ConvexHullTableModel(convexHull));
        SingleCellDataTableRenderer singleCellDataTableRenderer = new SingleCellDataTableRenderer(new DecimalFormat("###.###"));
        for (int i = 0; i < exploreTrackPanel.getConvexHullTable().getColumnCount(); i++) {
            exploreTrackPanel.getConvexHullTable().getColumnModel().getColumn(i).setCellRenderer(singleCellDataTableRenderer);
        }
        for (int i = 0; i < exploreTrackPanel.getConvexHullTable().getColumnCount(); i++) {
            GuiUtils.packColumn(exploreTrackPanel.getConvexHullTable(), i);
        }
    }

    /**
     * Make the plots for the single track.
     *
     * @param trackDataHolder
     */
    private void plotSingleTrackData(TrackDataHolder trackDataHolder) {
        // plot the shifted track coordinates
        plotCoordinatesInSpace(trackDataHolder);
        // plot x and y coordinates in time + displacements in time
        plotCoordinatesInTime(trackDataHolder);
        plotDisplacementsInTime(trackDataHolder);
        // plot the convex hull of the track
        plotConvexHull(trackDataHolder);
        // plot the turning angles distribution
        plotTurnAngleHistogram(trackDataHolder);
        // plot the polar plot
        plotPolarPlot(trackDataHolder);
        // plot the directionality ratio in time
        directionTrackController.plotDirectionalityRatioInTime(trackDataHolder);
        // plot the direction autocorrelation coefficients in time
        directionTrackController.plotDirectionAutocorrelationsInTime(trackDataHolder);
        // plot direction autocorrelation at time interval equal to 1
        directionTrackController.plotDirectionAutocorrelationTimeOne(trackDataHolder);
        // plot direction autocorrelation with a specified time interval provided by the user
        directionTrackController.plotDirectionAutocorrelationForDeltaT(trackDataHolder, (int) exploreTrackPanel.getDeltaTComboBox().getSelectedItem());
    }

    /**
     * Plot x and y coordinates in time for the given track.
     *
     * @param trackDataHolder
     */
    private void plotCoordinatesInTime(TrackDataHolder trackDataHolder) {
        // get the selected track data holder, and thus the track to plot in time
        Track track = trackDataHolder.getTrack();
        // get the track coordinates matrix
        Double[][] trackCoordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix();
        // we need to transpose the matrix
        Double[][] transpose2DArray = AnalysisUtils.transpose2DArray(trackCoordinatesMatrix);
        // we get the x coordinates and the time information
        double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transpose2DArray[0]));
        double[] timeIndexes = trackDataHolder.getStepCentricDataHolder().getTimeIndexes();
        // we create the series and set its key
        XYSeries xtSeries = JFreeChartUtils.generateXYSeries(timeIndexes, xCoordinates);
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        xtSeries.setKey(seriesKey);
        // we then create the XYSeriesCollection and use it to make a new line chart
        XYSeriesCollection xtSeriesCollection = new XYSeriesCollection(xtSeries);
        XYItemRenderer renderer = new StandardXYItemRenderer();
        NumberAxis xAxis = new NumberAxis("x (µm)");
        XYPlot xTPlot = new XYPlot(xtSeriesCollection, null, xAxis, renderer);
        // y axis
        NumberAxis yAxis = new NumberAxis("y (µm)");
        // we repeat exactly the same with the y coordinates in time
        double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transpose2DArray[1]));
        XYSeries ytSeries = JFreeChartUtils.generateXYSeries(timeIndexes, yCoordinates);
        ytSeries.setKey(seriesKey);
        XYSeriesCollection ytSeriesCollection = new XYSeriesCollection(ytSeries);
        XYPlot yTPlot = new XYPlot(ytSeriesCollection, null, yAxis, renderer);
        // domain axis
        NumberAxis domainAxis = new NumberAxis("time index");
        CombinedDomainXYPlot combinedDomainXYPlot = new CombinedDomainXYPlot(domainAxis);
        combinedDomainXYPlot.setRenderer(new XYLineAndShapeRenderer());
        combinedDomainXYPlot.add(xTPlot);
        combinedDomainXYPlot.add(yTPlot);
        combinedDomainXYPlot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart combinedChart = new JFreeChart(seriesKey, JFreeChartUtils.getChartFont(), combinedDomainXYPlot, Boolean.FALSE);
        JFreeChartUtils.setupCombinedChart(combinedChart, trackCoordinatesController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
        xYTCoordinateChartPanel.setChart(combinedChart);
    }

    /**
     * Plot displacements in time.
     *
     * @param trackDataHolder
     */
    private void plotDisplacementsInTime(TrackDataHolder trackDataHolder) {
        // get the displacements and the time to plot
        Double[] instantaneousDisplacements = trackDataHolder.getStepCentricDataHolder().getInstantaneousDisplacements();
        double[] timeIndexes = trackDataHolder.getStepCentricDataHolder().getTimeIndexes();
        Track track = trackDataHolder.getTrack();
        // we create the series and set its key
        XYSeries xYSeries = JFreeChartUtils.generateXYSeries(timeIndexes, ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(instantaneousDisplacements)));
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        xYSeries.setKey(seriesKey);
        XYSeriesCollection ySeriesCollection = new XYSeriesCollection(xYSeries);
        JFreeChart displInTimeChart = ChartFactory.createXYLineChart(seriesKey + " - displacements in time", "time index", "displ (µm)", ySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupSingleTrackPlot(displInTimeChart, trackCoordinatesController.getTrackDataHolderBindingList().indexOf(trackDataHolder), true);
        displacementTChartPanel.setChart(displInTimeChart);
    }

    /**
     * Plot the shifted track coordinates for the current track. We get the
     * current track from the selected track data holder object.
     *
     * @param trackDataHolder
     */
    private void plotCoordinatesInSpace(TrackDataHolder trackDataHolder) {
        // get the coordinates matrix
        Double[][] shiftedCoordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getShiftedCoordinatesMatrix();
        XYSeries xYSeries = JFreeChartUtils.generateXYSeries(shiftedCoordinatesMatrix);
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        xYSeries.setKey(seriesKey);
        XYSeriesCollection ySeriesCollection = new XYSeriesCollection(xYSeries);
        JFreeChart shiftedCoordinatesChart = ChartFactory.createXYLineChart(seriesKey + " - shifted coordinates", "x (µm)", "y (µm)", ySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupSingleTrackPlot(shiftedCoordinatesChart, trackCoordinatesController.getTrackDataHolderBindingList().indexOf(trackDataHolder), false);
        singleTrackCoordinatesChartPanel.setChart(shiftedCoordinatesChart);
    }

    /**
     * Setup a convex hull chart for a selected track.
     *
     * @param trackDataHolder
     */
    private void setupConvexHullChart(JFreeChart convexHullChart, TrackDataHolder trackDataHolder) {
        XYPlot xyPlot = convexHullChart.getXYPlot();
        JFreeChartUtils.setupXYPlot(xyPlot);
        // set title font
        convexHullChart.getTitle().setFont(JFreeChartUtils.getChartFont());
        // set up the chart
        int trackIndex = trackCoordinatesController.getTrackDataHolderBindingList().indexOf(trackDataHolder);
        // assign 2 renderers: one for the coordinates line and one for the convex hull plot
        XYLineAndShapeRenderer coordinatesRenderer = new XYLineAndShapeRenderer();
        coordinatesRenderer.setSeriesStroke(0, JFreeChartUtils.getWideLine());
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = trackIndex % length;
        coordinatesRenderer.setSeriesPaint(0, GuiUtils.getAvailableColors()[colorIndex]);
        // show both lines and points
        coordinatesRenderer.setSeriesLinesVisible(0, true);
        coordinatesRenderer.setSeriesShapesVisible(0, true);
        xyPlot.setRenderer(0, coordinatesRenderer);
        XYLineAndShapeRenderer convexHullRenderer = new XYLineAndShapeRenderer();
        convexHullRenderer.setSeriesStroke(0, JFreeChartUtils.getDashedLine());
        convexHullRenderer.setSeriesPaint(0, Color.black);
        xyPlot.setRenderer(1, convexHullRenderer);
        XYSeriesCollection dataset = (XYSeriesCollection) xyPlot.getDataset(0);
        double minY = dataset.getSeries(0).getMinY();
        double maxY = dataset.getSeries(0).getMaxY();
        xyPlot.getRangeAxis().setRange(minY, maxY);
    }

    /**
     * Given a track data holder, plot the track coordinates surrounded by the
     * convex hull computed for the set of points that belong to the track.
     *
     * @param trackDataHolder
     */
    private void plotConvexHull(TrackDataHolder trackDataHolder) {
        ConvexHull convexHull = trackDataHolder.getCellCentricDataHolder().getConvexHull();
        Iterable<GeometricPoint> cHull = convexHull.getHull();
        int M = 0;
        for (GeometricPoint point : cHull) {
            M++;
        }
        // the hull, in counterclockwise order
        GeometricPoint[] hull = new GeometricPoint[M];
        int m = 0;
        for (GeometricPoint point : cHull) {
            hull[m++] = point;
        }
        // generate xy coordinates for the points of the hull
        double[] x = new double[m + 1];
        double[] y = new double[m + 1];
        for (int i = 0; i < m; i++) {
            GeometricPoint point = hull[i];
            x[i] = point.getX();
            y[i] = point.getY();
        }
        // repeat fisrt coordinates at the end, to close the polygon
        x[m] = hull[0].getX();
        y[m] = hull[0].getY();
        // get info for the title of the plot
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        // dataset for the convex hull
        XYSeries hullSeries = JFreeChartUtils.generateXYSeries(x, y);
        XYSeriesCollection hullDataset = new XYSeriesCollection(hullSeries);
        JFreeChart convexHullChart = ChartFactory.createXYLineChart(seriesKey + " - convex hull", "x (µm)", "y (µm)", hullDataset, PlotOrientation.VERTICAL, false, true, false);
        // dataset for the coordinates
        Double[][] coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix();
        XYSeries coordinatesSeries = JFreeChartUtils.generateXYSeries(coordinatesMatrix);
        XYSeriesCollection coordinatesDataset = new XYSeriesCollection(coordinatesSeries);
        // use both datasets for the plot
        XYPlot xyPlot = convexHullChart.getXYPlot();
        xyPlot.setDataset(0, coordinatesDataset);
        xyPlot.setDataset(1, hullDataset);
        setupConvexHullChart(convexHullChart, trackDataHolder);
        convexHullChartPanel.setChart(convexHullChart);
    }

    /**
     * Plot an histogram distribution of instantaneous turning angles for a
     * given track.
     *
     * @param trackDataHolder
     */
    private void plotTurnAngleHistogram(TrackDataHolder trackDataHolder) {
        HistogramDataset histDataset = getHistDataset(trackDataHolder);
        JFreeChart histogramChart = ChartFactory.createHistogram("", "", "inst turn angle  - track " + trackDataHolder.getTrack().getTrackid(), histDataset,
                  PlotOrientation.VERTICAL, true, true, false);
        JFreeChartUtils.setShadowVisible(histogramChart, false);
        JFreeChartUtils.setUpHistogramChart(histogramChart, trackCoordinatesController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
        histogramChartPanel.setChart(histogramChart);
    }

    /**
     * For a given track, get the histogram dataset for its turning angles.
     *
     * @param trackDataHolder
     * @return
     */
    private HistogramDataset getHistDataset(TrackDataHolder trackDataHolder) {
        Double[] turningAngles = trackDataHolder.getStepCentricDataHolder().getTurningAngles();
        double[] toPrimitive = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(turningAngles));
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        double range = Arrays.stream(toPrimitive).max().getAsDouble() - Arrays.stream(toPrimitive).min().getAsDouble();
        dataset.addSeries(trackDataHolder.getTrack().getTrackNumber(), toPrimitive, (int) range / 5);
        return dataset;
    }

    /**
     * Plot the polar plot for the instantaneous turning angles of a single cell
     * track.
     *
     * @param trackDataHolder
     */
    private void plotPolarPlot(TrackDataHolder trackDataHolder) {
        XYSeries series = new XYSeries(trackDataHolder.getTrack().getTrackNumber(), false);
        HistogramDataset histDataset = getHistDataset(trackDataHolder);
        for (int i = 0; i < histDataset.getSeriesCount(); i++) {
            int itemCount = histDataset.getItemCount(i); // this is the number of bins
            for (int j = 0; j < itemCount; j++) {
                double startX = (double) histDataset.getStartX(i, j);
                double endX = (double) histDataset.getEndX(i, j);
                double theta = (startX + endX) / 2;
                Double radius = (Double) histDataset.getY(i, j);
                series.add(theta, radius);
            }
        }
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series);
        PolarPlot plot = new PolarPlot(data, new NumberAxis(), new DefaultPolarItemRenderer());
        JFreeChart polarChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        JFreeChartUtils.setupPolarChart(polarChart, trackCoordinatesController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
        polarPlotChartPanel.setChart(polarChart);
    }

    /**
     * Swing worker to play a track.
     */
    private class PlayTrackSwingWorker extends SwingWorker<Void, Void> {

        private final int selectedTrackIndex;

        public PlayTrackSwingWorker(int selectedTrackIndex) {
            this.selectedTrackIndex = selectedTrackIndex;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // disable play button
            exploreTrackPanel.getPlayButton().setEnabled(false);
            TrackDataHolder trackDataHolder = trackCoordinatesController.getTrackDataHolderBindingList().get(selectedTrackIndex);
            double[] timeIndexes = trackDataHolder.getStepCentricDataHolder().getTimeIndexes();
            for (int i = 0; i < timeIndexes.length; i++) {
                showTrackPointInTime(selectedTrackIndex, i);
                Thread.sleep(50);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // enable play button
                exploreTrackPanel.getPlayButton().setEnabled(true);
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error("play track cancelled");
            }
        }
    }
}
