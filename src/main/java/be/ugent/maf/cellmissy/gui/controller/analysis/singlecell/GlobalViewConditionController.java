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
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotOptionsPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsMenuBar;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsRendererGiver;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
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
 * for all wells of a condition.
 *
 * @author Paola
 */
@Controller("globalViewConditionController")
public class GlobalViewConditionController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlobalViewConditionController.class);
    // model
    private List<List<TrackDataHolder>> trackDataHoldersList;
    private List<XYSeriesCollection> xYSeriesCollections;
    // view
    private PlotSettingsMenuBar plotSettingsMenuBar;
    private List<ChartPanel> coordinatesChartPanels;
    private PlotOptionsPanel plotOptionsPanel;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // child controllers    
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        coordinatesChartPanels = new ArrayList<>();
        trackDataHoldersList = new ArrayList<>();
        xYSeriesCollections = new ArrayList<>();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        initPlotSettingsMenuBar();
        initPlotOptionsPanel();
    }

    /**
     *
     * @param plateCondition
     */
    public void renderConditionGlobalView(PlateCondition plateCondition) {
        resetPlotLogic();
        generateTrackDataHolders(plateCondition);
        generateDataForPlots(true);
        setChartsWithCollections(Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem()));
    }

    /**
     * This will reset the plot logic.
     */
    private void resetPlotLogic() {
        for (ChartPanel chartPanel : coordinatesChartPanels) {
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewConditionParentPanel().remove(chartPanel);
        }
        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewConditionParentPanel().revalidate();
        trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewConditionParentPanel().repaint();
        if (!xYSeriesCollections.isEmpty()) {
            xYSeriesCollections.clear();
        }
        if (!coordinatesChartPanels.isEmpty()) {
            coordinatesChartPanels.clear();
        }
        if (!trackDataHoldersList.isEmpty()) {
            trackDataHoldersList.clear();
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
            // iterate through the charts plotted and set the renderer for each of them
            for (int i = 0; i < coordinatesChartPanels.size(); i++) {
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
        plotOptionsPanel.getDoNotScaleAxesRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
                boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
                resetPlotLogic();
                generateTrackDataHolders(trackCoordinatesController.getCurrentCondition());
                generateDataForPlots(useRawData);
                // use the data to set the charts
                setChartsWithCollections(nCols);
            }
        });

        // scale axes to the experiment range
        plotOptionsPanel.getScaleAxesRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
                for (ChartPanel chartPanel : coordinatesChartPanels) {
                    trackCoordinatesController.scaleAxesToExperiment(chartPanel.getChart(), useRawData);
                }
            }
        });

        // shift the all coordinates to the origin
        plotOptionsPanel.getShiftedCoordinatesRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
                resetPlotLogic();
                generateTrackDataHolders(trackCoordinatesController.getCurrentCondition());
                generateDataForPlots(false);
                // use the data to set the charts
                setChartsWithCollections(nCols);
            }
        });

        // replot the unshifted coordinates
        plotOptionsPanel.getUnshiftedCoordinatesRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
                resetPlotLogic();
                generateTrackDataHolders(trackCoordinatesController.getCurrentCondition());
                generateDataForPlots(true);
                // use the data to set the charts
                setChartsWithCollections(nCols);
            }
        });

        // replot with a different number of columns
        plotOptionsPanel.getnColsComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
                boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
                resetPlotLogic();
                generateTrackDataHolders(trackCoordinatesController.getCurrentCondition());
                generateDataForPlots(useRawData);
                // use the data to set the charts
                setChartsWithCollections(nCols);
            }
        });

        plotOptionsPanel.getPlotSettingsPanel().add(plotSettingsMenuBar, BorderLayout.CENTER);

        // add view to parent component
        trackCoordinatesController.getTrackCoordinatesPanel().getOptionsConditionParentPanel().add(plotOptionsPanel, gridBagConstraints);
    }

    /**
     * Generate all the track data holders for a given condition.
     *
     * @param plateCondition
     */
    private void generateTrackDataHolders(PlateCondition plateCondition) {
        SingleCellConditionDataHolder conditionDataHolder = trackCoordinatesController.getConditionDataHolder(plateCondition);
        for (SingleCellWellDataHolder singleCellWellDataHolder : conditionDataHolder.getSingleCellWellDataHolders()) {
            trackDataHoldersList.add(singleCellWellDataHolder.getTrackDataHolders());
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
        boolean useCellMissyColor = plotSettingsMenuBar.getUseCellMissyColors().isSelected();
        int nPlots = xYSeriesCollections.size();
        int length = GuiUtils.getAvailableColors().length;
        for (int i = 0; i < nPlots; i++) {
            XYSeriesCollection collection = xYSeriesCollections.get(i);
            int numberTracks = collection.getSeries().size();
            SingleCellConditionDataHolder conditionDataHolder = trackCoordinatesController.getConditionDataHolder(trackCoordinatesController.getCurrentCondition());
            String title = numberTracks + " tracks" + " - " + conditionDataHolder.getSingleCellWellDataHolders().get(i).getWell();
            // create a chart for each plate condition
            JFreeChart coordinatesChart = ChartFactory.createXYLineChart(title, "x (µm)", "y (µm)", collection,
                      PlotOrientation.VERTICAL, false, true, false);
            // and a new chart panel as well
            ChartPanel coordinatesChartPanel = new ChartPanel(null);
            coordinatesChartPanel.setOpaque(false);

            // compute the constraints
            GridBagConstraints tempBagConstraints = getTempBagConstraints(nPlots, i, nCols);
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewConditionParentPanel().add(coordinatesChartPanel,
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
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewConditionParentPanel().revalidate();
            trackCoordinatesController.getTrackCoordinatesPanel().getGlobalViewConditionParentPanel().repaint();
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
    private GridBagConstraints getTempBagConstraints(int nPlots, int index, int nCols) {
        GridBagConstraints tempConstraints = new GridBagConstraints();
        int nRows = (int) Math.ceil(nPlots / nCols);
        tempConstraints.fill = GridBagConstraints.BOTH;
        tempConstraints.weightx = 1.0 / nCols;
        tempConstraints.weighty = 1.0 / nRows;
        tempConstraints.gridy = (int) Math.floor(index / nCols);
        if (index < nCols) {
            tempConstraints.gridx = index;
        } else {
            tempConstraints.gridx = index - ((index / nCols) * nCols);
        }
        return tempConstraints;
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
