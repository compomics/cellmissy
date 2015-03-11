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
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsMenuBar;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsRendererGiver;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This class takes care of rendering a global view showing cell trajectories
 * for all the conditions of the experiment.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("globalViewController")
class GlobalViewController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlobalViewController.class);
    // model
    private List<List<TrackDataHolder>> trackDataHoldersList;
    private List<XYSeriesCollection> xYSeriesCollections;
    private boolean firstView;
    // view
    private PlotSettingsMenuBar plotSettingsMenuBar;
    private List<ChartPanel> coordinatesChartPanels;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // services

    /**
     * Initialize controller.
     */
    public void init() {
        coordinatesChartPanels = new ArrayList<>();
        trackDataHoldersList = new ArrayList<>();
        xYSeriesCollections = new ArrayList<>();
        firstView = true;
        // init views
        initPlotSettingsMenuBar();
        initGlobalViewPanel();
    }

    /**
     * Initialize main view.
     */
    private void initGlobalViewPanel() {
        // add radiobuttons to a button group
        ButtonGroup scaleAxesButtonGroup = new ButtonGroup();
        scaleAxesButtonGroup.add(trackCoordinatesController.getTrackCoordinatesPanel()
                .getDoNotScaleGlobViewRadioButton());
        scaleAxesButtonGroup.add(trackCoordinatesController.getTrackCoordinatesPanel().getScaleGlobViewRadioButton());
        trackCoordinatesController.getTrackCoordinatesPanel().getDoNotScaleGlobViewRadioButton().setSelected(true);
        // another button group for the shifted/unshifted coordinates
        ButtonGroup shiftedCoordinatesButtonGroup = new ButtonGroup();
        shiftedCoordinatesButtonGroup.add(trackCoordinatesController.getTrackCoordinatesPanel()
                .getGlobalViewShiftedCoordinatesRadioButton());
        shiftedCoordinatesButtonGroup.add(trackCoordinatesController.getTrackCoordinatesPanel()
                .getGlobalViewUnshiftedCoordinatesRadioButton());
        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewUnshiftedCoordinatesRadioButton()
                .setSelected(true);

        /**
         * Action listeners
         */
        // do not scale axes
        trackCoordinatesController.getTrackCoordinatesPanel().getDoNotScaleGlobViewRadioButton().addActionListener
                (new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nCols = Integer.parseInt((String) trackCoordinatesController.getTrackCoordinatesPanel()
                        .getnColsComboBox().getSelectedItem());
                boolean useRawData = trackCoordinatesController.getTrackCoordinatesPanel()
                        .getGlobalViewUnshiftedCoordinatesRadioButton().isSelected();
                resetPlotLogic();
                generateDataForPlots(useRawData);
                // use the data to set the charts
                setChartsWithCollections(nCols);
            }
        });

        // scale axes to the experiment range
        trackCoordinatesController.getTrackCoordinatesPanel().getScaleGlobViewRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean useRawData = trackCoordinatesController.getTrackCoordinatesPanel()
                        .getGlobalViewUnshiftedCoordinatesRadioButton().isSelected();
                for (ChartPanel chartPanel : coordinatesChartPanels) {
                    trackCoordinatesController.scaleAxesToExperiment(chartPanel.getChart(), useRawData);
                }
            }
        });

        // shift the all coordinates to the origin
        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewShiftedCoordinatesRadioButton()
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int nCols = Integer.parseInt((String) trackCoordinatesController.getTrackCoordinatesPanel()
                                .getnColsComboBox().getSelectedItem());
                        resetPlotLogic();
                        generateDataForPlots(false);
                        // use the data to set the charts
                        setChartsWithCollections(nCols);
                    }
                });

        // replot the unshifted coordinates
        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewUnshiftedCoordinatesRadioButton()
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int nCols = Integer.parseInt((String) trackCoordinatesController.getTrackCoordinatesPanel()
                                .getnColsComboBox().getSelectedItem());
                        resetPlotLogic();
                        generateDataForPlots(true);
                        // use the data to set the charts
                        setChartsWithCollections(nCols);
                    }
                });

        // replot with a different number of columns
        trackCoordinatesController.getTrackCoordinatesPanel().getnColsComboBox().addActionListener(new ActionListener
                () {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nCols = Integer.parseInt((String) trackCoordinatesController.getTrackCoordinatesPanel()
                        .getnColsComboBox().getSelectedItem());
                boolean useRawData = trackCoordinatesController.getTrackCoordinatesPanel()
                        .getGlobalViewUnshiftedCoordinatesRadioButton().isSelected();
                resetPlotLogic();
                generateDataForPlots(useRawData);
                // use the data to set the charts
                setChartsWithCollections(nCols);
            }
        });

        // change listener to the tabbed pane:
        // if the global view panel is clicked, and it is the first time we render a global view, we launch a swing
        // worker
        trackCoordinatesController.getTrackCoordinatesPanel().getTrackCoordinatesTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (trackCoordinatesController.getTrackCoordinatesPanel().getTrackCoordinatesTabbedPane()
                        .getSelectedIndex() == 1 && firstView) {
                    renderGlobalView();
                    firstView = false;
                }
            }
        });

        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewPlotSettingsPanel().add
                (plotSettingsMenuBar, BorderLayout.CENTER);
    }

    /**
     * This will reset the plot logic.
     */
    private void resetPlotLogic() {
        for (ChartPanel chartPanel : coordinatesChartPanels) {
            trackCoordinatesController.getTrackCoordinatesPanel().getGraphicsParentPanel().remove(chartPanel);
        }
        trackCoordinatesController.getTrackCoordinatesPanel().getGraphicsParentPanel().revalidate();
        trackCoordinatesController.getTrackCoordinatesPanel().getGraphicsParentPanel().repaint();
        if (!xYSeriesCollections.isEmpty()) {
            xYSeriesCollections.clear();
        }
        if (!coordinatesChartPanels.isEmpty()) {
            coordinatesChartPanels.clear();
        }
    }

    /**
     * Action Listener for MenuItems
     */
    private class ItemActionListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            // iterate through the charts plotted and set the renderer for each of them
            for (ChartPanel coordinatesChartPanel : coordinatesChartPanels) {
                JFreeChart chart = coordinatesChartPanel.getChart();
                XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) chart.getXYPlot().getDataset();
                List<Integer> endPoints = getEndPoints(xYSeriesCollection);
                PlotSettingsRendererGiver plotSettingsRendererGiver = new PlotSettingsRendererGiver(-1,
                        plotSettingsMenuBar, endPoints);
                TrackXYLineAndShapeRenderer renderer = plotSettingsRendererGiver.getRenderer(e);
                coordinatesChartPanel.getChart().getXYPlot().setRenderer(renderer);
            }
        }
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
        for (Enumeration<AbstractButton> buttons = plotSettingsMenuBar.getLinesButtonGroup().getElements(); buttons
                .hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            button.addItemListener(itemActionListener);
        }
    }

    /**
     * Render the global view: launch a swing worker, and show all the
     * conditions tracks at once.
     */
    private void renderGlobalView() {
        GlobalViewSwingWorker globalViewSwingWorker = new GlobalViewSwingWorker();
        globalViewSwingWorker.execute();
    }

    /**
     * Swing Worker to render the global view.
     */
    private class GlobalViewSwingWorker extends SwingWorker<Void, Void> {

        private final WaitingDialog waitingDialog = new WaitingDialog(trackCoordinatesController.getMainFrame(), false);

        @Override
        protected Void doInBackground() throws Exception {
            // show a waiting dialog
            waitingDialog.setTitle("Rendering global view... ");
            GuiUtils.centerDialogOnFrame(trackCoordinatesController.getMainFrame(), waitingDialog);
            waitingDialog.setVisible(true);
            // show a waiting cursor, disable GUI components
            trackCoordinatesController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            trackCoordinatesController.controlGuiComponents(false);
            // generate track data holders and data for the plots
            generateTrackDataHolders();
            generateDataForPlots(true);
            /*// use the data to set the charts
            int nCols = Integer.parseInt((String) trackCoordinatesController.getTrackCoordinatesPanel()
                    .getnColsComboBox().getSelectedItem());
            setChartsWithCollections(nCols);*/
            return null;
        }

        @Override
        protected void done() {
            try {
                get();

                // use the data to set the charts
                int nCols = Integer.parseInt((String) trackCoordinatesController.getTrackCoordinatesPanel()
                        .getnColsComboBox().getSelectedItem());
                setChartsWithCollections(nCols);

                waitingDialog.setVisible(false);
                trackCoordinatesController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                trackCoordinatesController.controlGuiComponents(true);

            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                trackCoordinatesController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Generate all the track data holders.
     */
    private void generateTrackDataHolders() {
        List<PlateCondition> plateConditionList = trackCoordinatesController.getPlateConditionList();
        for (PlateCondition plateCondition : plateConditionList) {
            SingleCellConditionDataHolder singleCellConditionDataHolder = trackCoordinatesController
                    .getConditionDataHolder(plateCondition);
            trackDataHoldersList.add(singleCellConditionDataHolder.getTrackDataHolders());
        }
    }

    /**
     * @param useRawData
     * @return
     */
    private void generateDataForPlots(boolean useRawData) {
        for (List<TrackDataHolder> trackDataHolders : trackDataHoldersList) {
            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // this is not the best way to fix this multiple locations issue, but for the moment fair enough !!
            int counter = 0;
            for (TrackDataHolder trackDataHolder : trackDataHolders) {
                // the matrix to use is either the raw coordinates matrix or the shifted matrix
                Double[][] coordinatesMatrix;
                if (useRawData) {
                    coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix();
                } else {
                    coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getShiftedCooordinatesMatrix();
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
            xYSeriesCollections.add(xYSeriesCollection);
        }
    }

    /**
     * Set all the charts with the generated XYSeriesCollections.
     *
     * @param nCols
     */
    private void setChartsWithCollections(int nCols) {
        // plot logic
        boolean plotLines = plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().isSelected();
        boolean plotPoints = plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().isSelected();
        boolean showEndPoints = plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().isSelected();
        Float lineWidth = plotSettingsMenuBar.getSelectedLineWidth();
        int nPlots = xYSeriesCollections.size();
        List<PlateCondition> plateConditionList = trackCoordinatesController.getPlateConditionList();
        for (int i = 0; i < nPlots; i++) {
            XYSeriesCollection collection = xYSeriesCollections.get(i);
            int numberTracks = collection.getSeries().size();
            String title = numberTracks + " tracks" + " - " + plateConditionList.get(i);
            // create a chart for each plate condition
            JFreeChart coordinatesChart = ChartFactory.createXYLineChart(title, "x (µm)", "y (µm)", collection,
                    PlotOrientation.VERTICAL, false, true, false);
            // and a new chart panel as well
            ChartPanel coordinatesChartPanel = new ChartPanel(null);
            coordinatesChartPanel.setOpaque(false);

            // compute the constraints
            GridBagConstraints gridBagConstraints = getGridBagConstraints(nPlots, i, nCols);
            trackCoordinatesController.getTrackCoordinatesPanel().getGraphicsParentPanel().add(coordinatesChartPanel,
                    gridBagConstraints);
            if (trackCoordinatesController.getTrackCoordinatesPanel().getScaleGlobViewRadioButton().isSelected()) {
                trackCoordinatesController.scaleAxesToExperiment(coordinatesChart, trackCoordinatesController
                        .getTrackCoordinatesPanel().getGlobalViewUnshiftedCoordinatesRadioButton().isSelected());
            }
            JFreeChartUtils.setupTrackChart(coordinatesChart);
            TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines,
                    plotPoints, showEndPoints, getEndPoints(collection), -1, lineWidth);
            coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
            coordinatesChartPanel.setChart(coordinatesChart);

            // add the chart panels to the list
            coordinatesChartPanels.add(coordinatesChartPanel);
            trackCoordinatesController.getTrackCoordinatesPanel().getGraphicsParentPanel().revalidate();
            trackCoordinatesController.getTrackCoordinatesPanel().getGraphicsParentPanel().repaint();
        }
    }

    /**
     * Given the amount of plots to render, and the index of the current plot,
     * as well as the number of columns to use, get the appropriate
     * GridBagConstraints.
     *
     * @param nPlots
     * @param index
     * @param nCols
     * @return the GridBagConstraints
     */
    private GridBagConstraints getGridBagConstraints(int nPlots, int index, int nCols) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        int nRows = (int) Math.ceil(nPlots / nCols);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0 / nCols;
        gridBagConstraints.weighty = 1.0 / nRows;
        gridBagConstraints.gridy = (int) Math.floor(index / nCols);
        if (index < nCols) {
            gridBagConstraints.gridx = index;
        } else {
            gridBagConstraints.gridx = index - ((index / nCols) * nCols);
        }
        return gridBagConstraints;
    }

    /**
     * Iterate through the current track data holders and get the endpoints of
     * the correspondent tracks.
     *
     * @return: a List of Integers, each Integer being the endpoint for a track.
     */
    private List<Integer> getEndPoints(XYSeriesCollection xYSeriesCollection) {
        List<Integer> endPoints = new ArrayList<>();
        for (int i = 0; i < xYSeriesCollection.getSeries().size(); i++) {
            endPoints.add(xYSeriesCollection.getSeries(i).getItemCount());
        }
        return endPoints;
    }
}
