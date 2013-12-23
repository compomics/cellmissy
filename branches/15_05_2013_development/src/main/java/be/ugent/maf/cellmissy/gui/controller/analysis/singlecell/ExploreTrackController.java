/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.ExploreTrackPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TimePointTrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.PlottedTracksListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TrackDataHolderTableRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataHolderTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
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
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
public class ExploreTrackController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExploreTrackController.class);
    // model
    private BindingGroup bindingGroup;
    // view
    private ExploreTrackPanel exploreTrackPanel;
    private ChartPanel coordinatesChartPanel;
    private ChartPanel xYTCoordinateChartPanel;
    private ChartPanel shiftedTrackChartPanel;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init main view
        initExploreTrackPanel();
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

    /**
     * Public methods
     */
    /**
     * Action performed on selection of a single track: set the renderer for the
     * x y plot and control some GUI elements.
     *
     * @param selectedTrackIndex: the index of the selected track
     */
    public void onSelectedTrack(int selectedTrackIndex) {
        Float lineWidth = (Float) trackCoordinatesController.getTrackCoordinatesPanel().getLineWidthComboBox().getSelectedItem();
        TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true, false, false, null, selectedTrackIndex, lineWidth);
        coordinatesChartPanel.getChart().getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
        TrackDataHolder selectedTrackDataHolder = trackCoordinatesController.getTrackDataHolderBindingList().get(selectedTrackIndex);
        // set up and enable the time /slider here
        setupTimeSlider(selectedTrackDataHolder);
        updateTrackData(selectedTrackDataHolder);
    }

    /**
     * Private classes and methods
     */
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
        shiftedTrackChartPanel = new ChartPanel(null);
        shiftedTrackChartPanel.setOpaque(false);

        exploreTrackPanel.getxYTCoordinatesParentPanel().add(xYTCoordinateChartPanel, gridBagConstraints);
        exploreTrackPanel.getShiftedParentPanel().add(shiftedTrackChartPanel, gridBagConstraints);

        exploreTrackPanel.getTrackDataTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        exploreTrackPanel.getTrackDataTable().getTableHeader().setReorderingAllowed(false);

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
                    int timePoint = (int) source.getValue();
                    // show the track point in time: basically set the renderer for the chart
                    showTrackPointInTime(selectedTrackIndex, timePoint);
                    // update x and y coordinates field
                    updateCoordinatesInfoInTime(selectedTrackIndex, timePoint);
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
                        trackCoordinatesController.getTrackCoordinatesPanel().getPlottedTracksJList().setSelectedIndex(selectedTrackIndex);
                        onSelectedTrack(selectedTrackIndex);
                    }
                }
            }
        });

        // add view to parent panel
        trackCoordinatesController.getTrackCoordinatesPanel().getExploreTrackParentPanel().add(exploreTrackPanel, gridBagConstraints);
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
        Float lineWidth = (Float) trackCoordinatesController.getTrackCoordinatesPanel().getLineWidthComboBox().getSelectedItem();
        TimePointTrackXYLineAndShapeRenderer timePointTrackXYLineAndShapeRenderer = new TimePointTrackXYLineAndShapeRenderer(selectedTrackIndex, timePoint, lineWidth);
        xyPlot.setRenderer(timePointTrackXYLineAndShapeRenderer);
    }

    /**
     * Show the track data dialog for a certain track data holder selected.
     *
     * @param trackDataHolder
     */
    private void updateTrackData(TrackDataHolder trackDataHolder) {
        // plot the data associated with the current track
        plotSingleTrackData(trackDataHolder);
        // update model for the track table
        exploreTrackPanel.getTrackDataTable().setModel(new TrackDataHolderTableModel(trackDataHolder));
        TrackDataHolderTableRenderer trackDataHolderTableRenderer = new TrackDataHolderTableRenderer(new DecimalFormat("###.###"));
        for (int i = 0; i < exploreTrackPanel.getTrackDataTable().getColumnCount(); i++) {
            exploreTrackPanel.getTrackDataTable().getColumnModel().getColumn(i).setCellRenderer(trackDataHolderTableRenderer);
        }
    }

    /**
     * Make the plots for the single track.
     *
     * @param trackDataHolder
     */
    private void plotSingleTrackData(TrackDataHolder trackDataHolder) {
        // plot x and y coordinates in time
        plotCoordinatesInTime(trackDataHolder);
        // plot the shifted track coordinates
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
        Double[][] trackCoordinatesMatrix = trackDataHolder.getCoordinatesMatrix();
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
     * Plot the shifted track coordinates for the current track. We get the
     * current track from the selected track data holder object.
     *
     * @param trackDataHolder
     */
    private void plotShiftedCoordinates(TrackDataHolder trackDataHolder) {
        // get the coordinates matrix
        Double[][] shiftedTrackCoordinates = trackDataHolder.getShiftedCooordinatesMatrix();
        XYSeries xYSeries = JFreeChartUtils.generateXYSeries(shiftedTrackCoordinates);
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        xYSeries.setKey(seriesKey);
        XYSeriesCollection ySeriesCollection = new XYSeriesCollection(xYSeries);
        JFreeChart shiftedCoordinatesChart = ChartFactory.createXYLineChart(seriesKey + " - coordinates shifted to (0, 0)", "x (µm)", "y (µm)", ySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupSingleTrackPlot(shiftedCoordinatesChart, trackCoordinatesController.getTrackDataHolderBindingList().indexOf(trackDataHolder), false);
        shiftedTrackChartPanel.setChart(shiftedCoordinatesChart);
    }
}
