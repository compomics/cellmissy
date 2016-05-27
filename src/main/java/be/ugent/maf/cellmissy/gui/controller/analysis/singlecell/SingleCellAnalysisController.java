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
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.AnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotOptionsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
    // view
    private AnalysisPanel analysisPanel;
    private List<ChartPanel> cellTracksChartPanels;
    private List<ChartPanel> cellSpeedsChartPanels;

    private PlotOptionsPanel plotOptionsPanel;
    // parent controller
    @Autowired
    private SingleCellMainController singleCellMainController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        cellTracksChartPanels = new ArrayList<>();
        cellSpeedsChartPanels = new ArrayList<>();

        cellTracksData = new ArrayList<>();
        filteredData = Boolean.FALSE;
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initAnalysisPanel();
        initPlotOptionsPanel();
    }

    /**
     * The analysis panel view to be passed on parent controller.
     *
     * @return
     */
    public AnalysisPanel getAnalysisPanel() {
        return analysisPanel;
    }

    public void setFilteredData(Boolean filteredData) {
        this.filteredData = filteredData;
    }

    public void plotCellTracks() {
        if (cellTracksData.isEmpty()) {
            PlotCellTracksSwingWorker plotCellTracksSwingWorker = new PlotCellTracksSwingWorker();
            plotCellTracksSwingWorker.execute();
        }
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
        radioButtonGroup.add(analysisPanel.getCellSpeedsRadioButton());
        radioButtonGroup.add(analysisPanel.getAngleRadioButton());
        radioButtonGroup.add(analysisPanel.getStatisticsRadioButton());

        /**
         * Add action listeners
         */
        // plot the cell tracks
        analysisPanel.getCellTracksRadioButton().addActionListener((ActionEvent e) -> {
            plotCellTracks();
        });

        analysisPanel.getCellTracksRadioButton().setSelected(true);

        // add view to parent panel
        singleCellMainController.getSingleCellAnalysisPanel().getAnalysisParentPanel().add(analysisPanel, gridBagConstraints);
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
            generateDataForPlots(generateTrackDataHolders(), useRawData);
            // use the data to set the charts
            setChartsWithCollections(nCols);
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
            generateDataForPlots(generateTrackDataHolders(), false);
            // use the data to set the charts
            setChartsWithCollections(nCols);
        });

        // replot the unshifted coordinates
        plotOptionsPanel.getUnshiftedCoordinatesRadioButton().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            resetPlotLogic();
            generateDataForPlots(generateTrackDataHolders(), true);
            // use the data to set the charts
            setChartsWithCollections(nCols);
        });

        // replot with a different number of columns
        plotOptionsPanel.getnColsComboBox().addActionListener((ActionEvent e) -> {
            int nCols = Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem());
            boolean useRawData = plotOptionsPanel.getUnshiftedCoordinatesRadioButton().isSelected();
            resetPlotLogic();
            generateDataForPlots(generateTrackDataHolders(), useRawData);
            // use the data to set the charts
            setChartsWithCollections(nCols);
        });
        // add view to parent component
        analysisPanel.getPlotOptionsParentPanel().add(plotOptionsPanel, gridBagConstraints);
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
            analysisPanel.getCellTracksParentPanel().removeAll();
        });
        analysisPanel.getCellTracksParentPanel().revalidate();
        analysisPanel.getCellTracksParentPanel().repaint();
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
    private List<List<TrackDataHolder>> generateTrackDataHolders() {
        if (filteredData) {
            return new ArrayList<>(singleCellMainController.getFilteringMap().values());
        } else {
            List<List<TrackDataHolder>> list = new ArrayList<>();
            singleCellMainController.getPreProcessingMap().values().stream().forEach((conditionDataHolder) -> {
                list.add(conditionDataHolder.getTrackDataHolders());
            });
            return list;
        }
    }

    /**
     * Generate the actual data for the plot.
     *
     * @param useRawData
     * @return
     */
    private void generateDataForPlots(List<List<TrackDataHolder>> trackDataHoldersList, boolean useRawData) {
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
            cellTracksData.add(xYSeriesCollection);
        });
    }

    /**
     * Set all the charts with the generated XYSeriesCollections.
     *
     * @param nCols
     */
    private void setChartsWithCollections(int nCols) {

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
            analysisPanel.getCellTracksParentPanel().add(coordinatesChartPanel, tempBagConstraints);

            JFreeChartUtils.setupTrackChart(coordinatesChart);
            TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true,
                    false, false, null, -1, 2, false);
            trackXYLineAndShapeRenderer.setChosenColor(GuiUtils.getAvailableColors()[i % length]);
            coordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
            coordinatesChartPanel.setChart(coordinatesChart);

            // add the chart panels to the list
            cellTracksChartPanels.add(coordinatesChartPanel);

            analysisPanel.getCellTracksParentPanel().revalidate();
            analysisPanel.getCellTracksParentPanel().repaint();
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
            // generate track data holders and data for the plots
            List<List<TrackDataHolder>> trackDataHolders = generateTrackDataHolders();
            generateDataForPlots(trackDataHolders, true);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // use the data to set the charts
                setChartsWithCollections(Integer.parseInt((String) plotOptionsPanel.getnColsComboBox().getSelectedItem()));
                singleCellMainController.hideWaitingDialog();
                singleCellMainController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                singleCellMainController.controlGuiComponents(true);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellMainController.handleUnexpectedError(ex);
            }
        }
    }

}
