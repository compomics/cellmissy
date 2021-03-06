/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotOptionsPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsMenuBar;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsRendererGiver;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.SwingWorker;

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
@Controller("globalViewExperimentController")
class GlobalViewExperimentController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlobalViewExperimentController.class);
    // model
    private List<List<TrackDataHolder>> trackDataHoldersList;
    private List<XYSeriesCollection> xYSeriesCollections;
    private boolean firstView;
    // view
    private PlotSettingsMenuBar plotSettingsMenuBar;
    private List<ChartPanel> coordinatesChartPanels;
    private PlotOptionsPanel plotOptionsPanel;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller.
     */
    public void init() {
        coordinatesChartPanels = new ArrayList<>();
        trackDataHoldersList = new ArrayList<>();
        xYSeriesCollections = new ArrayList<>();
        firstView = true;
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        initPlotSettingsMenuBar();
        initPlotOptionsPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public boolean isFirstView() {
        return firstView;
    }

    public void setFirstView(boolean firstView) {
        this.firstView = firstView;
    }

    /**
     * Render the global view: launch a swing worker, and show all the
     * conditions tracks at once.
     */
    public void renderExperimentGlobalView() {
        GlobalViewExperimentSwingWorker globalViewSwingWorker = new GlobalViewExperimentSwingWorker();
        globalViewSwingWorker.execute();
    }

    /**
     * Private methods and classes.
     */
    /**
     * This will reset the plot logic.
     */
    private void resetPlotLogic() {
        coordinatesChartPanels.stream().forEach((chartPanel) -> {
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewExpParentPanel().remove(chartPanel);
        });
        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewExpParentPanel().revalidate();
        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewExpParentPanel().repaint();
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
            int length = GuiUtils.getAvailableColors().length;
            // iterate through the charts plotted and set the renderer for each of them
            for (int i = 0; i < coordinatesChartPanels.size(); i++) {
                ChartPanel coordinatesChartPanel = coordinatesChartPanels.get(i);
                JFreeChart chart = coordinatesChartPanel.getChart();
                XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) chart.getXYPlot().getDataset();
                List<Integer> endPoints = getEndPoints(xYSeriesCollection);
                PlotSettingsRendererGiver plotSettingsRendererGiver = new PlotSettingsRendererGiver(-1,
                        plotSettingsMenuBar, endPoints);
                TrackXYLineAndShapeRenderer renderer = plotSettingsRendererGiver.getRenderer(e);
                renderer.setChosenColor(GuiUtils.getAvailableColors()[i % length]);
                chart.getXYPlot().setRenderer(renderer);
            }
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
            for (int i = 0; i < trackCoordinatesController.getPlateConditionList().size(); i++) {
                ChartPanel coordinatesChartPanel = coordinatesChartPanels.get(i);
                JFreeChart chart = coordinatesChartPanel.getChart();
                XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) chart.getXYPlot().getDataset();
                List<Integer> endPoints = getEndPoints(xYSeriesCollection);
                PlotSettingsRendererGiver plotSettingsRendererGiver = new PlotSettingsRendererGiver(selectedTrackIndex,
                        plotSettingsMenuBar, endPoints);
                TrackXYLineAndShapeRenderer renderer = plotSettingsRendererGiver.getRenderer(e);
                renderer.setChosenColor(GuiUtils.getAvailableColors()[i % length]);
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
                .hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            button.addItemListener(itemActionListener);
        }
        plotSettingsMenuBar.getUseCellMissyColors().addItemListener(new ColorItemActionListener());
    }

    /**
     * Initialize plot options panel.
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
            generateDataForPlots(useRawData);
            // use the data to set the charts
            setChartsWithCollections(nCols);
        });

        // scale axes to the experiment range
        plotOptionsPanel.getScaleAxesRadioButton().addActionListener((ActionEvent e) -> {
            boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
            coordinatesChartPanels.stream().forEach((chartPanel) -> {
                trackCoordinatesController.scaleAxesToExperiment(chartPanel.getChart(), useRawData);
            });
        });

        // shift the all coordinates to the origin
        plotOptionsPanel.getShiftedCoordinatesRadioButton().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            resetPlotLogic();
            generateDataForPlots(false);
            // use the data to set the charts
            setChartsWithCollections(nCols);
        });

        // replot the unshifted coordinates
        plotOptionsPanel.getUnshiftedCoordinatesRadioButton().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            resetPlotLogic();
            generateDataForPlots(true);
            // use the data to set the charts
            setChartsWithCollections(nCols);
        });

        // replot with a different number of columns
        plotOptionsPanel.getnColsComboBox().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
            resetPlotLogic();
            generateDataForPlots(useRawData);
            // use the data to set the charts
            setChartsWithCollections(nCols);
        });

        plotOptionsPanel.getPlotSettingsPanel().add(plotSettingsMenuBar, BorderLayout.CENTER);

        // add view to parent component
        trackCoordinatesController.getTrackCoordinatesPanel().getOptionsExperimentParentPanel().add(plotOptionsPanel, gridBagConstraints);
    }

    /**
     * Swing Worker to render the global view.
     */
    private class GlobalViewExperimentSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            trackCoordinatesController.showWaitingDialog("Rendering global view... ");
            // show a waiting cursor, disable GUI components
            trackCoordinatesController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            trackCoordinatesController.controlGuiComponents(false);
            // generate track data holders and data for the plots
            generateTrackDataHolders();
            generateDataForPlots(true);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // use the data to set the charts
                setChartsWithCollections(Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem()));
                trackCoordinatesController.hideWaitingDialog();
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
        plateConditionList.stream().map((plateCondition) -> trackCoordinatesController
                .getConditionDataHolder(plateCondition)).forEach((singleCellConditionDataHolder) -> {
                    trackDataHoldersList.add(singleCellConditionDataHolder.getTrackDataHolders());
                });
    }

    /**
     * Generate the actual data for the plot.
     *
     * @param useRawData
     * @return
     */
    private void generateDataForPlots(boolean useRawData) {
        trackDataHoldersList.stream().map((trackDataHolders) -> {
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
            xYSeriesCollections.add(xYSeriesCollection);
        });
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
        boolean useCellMissyColor = plotSettingsMenuBar.getUseCellMissyColors().isSelected();
        int nPlots = xYSeriesCollections.size();
        int length = GuiUtils.getAvailableColors().length;
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
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(nPlots, i, nCols);
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewExpParentPanel().add(coordinatesChartPanel,
                    tempBagConstraints);
            // see if exes need to be scaled
            if (plotOptionsPanel.getScaleAxesRadioButton().isSelected()) {
                trackCoordinatesController.scaleAxesToExperiment(coordinatesChart,
                        plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected());
            }
            JFreeChartUtils.setupTrackChart(coordinatesChart);
            TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines,
                    plotPoints, showEndPoints, getEndPoints(collection), -1, lineWidth, useCellMissyColor);
            trackXYLineAndShapeRenderer.setChosenColor(GuiUtils.getAvailableColors()[i % length]);
            coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
            coordinatesChartPanel.setChart(coordinatesChart);

            // add the chart panels to the list
            coordinatesChartPanels.add(coordinatesChartPanel);
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewExpParentPanel().revalidate();
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewExpParentPanel().repaint();
        }
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
