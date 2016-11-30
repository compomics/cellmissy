/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.HeatMapScalePanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.plate.HeatMapPlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of rendering a heat map of the plate for an
 * experiment.
 *
 * @author Paola
 */
@Controller("plateHeatMapController")
class PlateHeatMapController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PlateHeatMapController.class);
    // model
    private boolean firstView;
    // view
    private HeatMapPlatePanel heatMapPlatePanel;
    private ChartPanel zScoreChartPanel;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // services
    private GridBagConstraints gridBagConstraints;
    @Autowired
    private PlateService plateService;

    /**
     * Initialize controller.
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        heatMapPlatePanel = new HeatMapPlatePanel();
        firstView = true;
        initPlatePanel();
        initView();
    }

    /**
     * Initialize the main view
     */
    private void initView() {
        TrackCoordinatesPanel trackCoordinatesPanel = trackCoordinatesController.getTrackCoordinatesPanel();
        trackCoordinatesPanel.getMeasurementComboBox().setSelectedIndex(0);
        trackCoordinatesPanel.getAggregationComboBox().setSelectedIndex(0);

        zScoreChartPanel = new ChartPanel(null);
        zScoreChartPanel.setOpaque(false);
        trackCoordinatesPanel.getzScoreParentPanel().add(zScoreChartPanel, gridBagConstraints);

        // the real plotting action
        trackCoordinatesPanel.getPlotHeatMapButton().addActionListener((ActionEvent e) -> {
            plotHeatMap();
            heatMapPlatePanel.repaint();
        });

        trackCoordinatesPanel.getPlotZScoreButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // if these are still null, compute them
                if (trackCoordinatesController.getPlateMedianSpeed() == 0 & trackCoordinatesController.getPlateMADSpeed() == 0) {
                    trackCoordinatesController.computePlateMedianSpeed();
                    trackCoordinatesController.computePlateMADSpeed();
                    // set value sin the text fields
                    trackCoordinatesController.getTrackCoordinatesPanel().getPlateMedianSpeedTextField().setText(""
                            + AnalysisUtils.roundThreeDecimals(trackCoordinatesController.getPlateMedianSpeed()));
                    trackCoordinatesController.getTrackCoordinatesPanel().getPlateMADSpeedTextField().setText(""
                            + AnalysisUtils.roundThreeDecimals(trackCoordinatesController.getPlateMADSpeed()));
                }
                plotZScoreHeatMap();
                plotZScores();
            }
        });

        // click on first view
        trackCoordinatesPanel.getTrackCoordinatesTabbedPane().addChangeListener((ChangeEvent e) -> {
            if (firstView) {
                Dimension parentDimension = trackCoordinatesController.getTrackCoordinatesPanel().getGraphicParentPanel().getSize();
                heatMapPlatePanel.setExperiment(trackCoordinatesController.getExperiment());
                heatMapPlatePanel.initPanel(trackCoordinatesController.getExperiment().getPlateFormat(), parentDimension);
                firstView = false;
            }
        });
    }

    /**
     * Initialize the plate panel.
     */
    private void initPlatePanel() {
        Dimension parentDimension = trackCoordinatesController.getTrackCoordinatesPanel().getGraphicParentPanel().getSize();
        heatMapPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        trackCoordinatesController.getTrackCoordinatesPanel().getGraphicParentPanel().add(heatMapPlatePanel, gridBagConstraints);
        heatMapPlatePanel.repaint();
    }

    /**
     * Action called on pressing the button: refresh the heat map view
     */
    private void plotHeatMap() {
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().removeAll();
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().repaint();

        heatMapPlatePanel.setValues(computeValuesMap());
        heatMapPlatePanel.repaint();

        HeatMapScalePanel heatMapScalePanel = new HeatMapScalePanel(heatMapPlatePanel.getMin(), heatMapPlatePanel.getMax());
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().add(heatMapScalePanel, gridBagConstraints);
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().revalidate();

        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().repaint();
    }

    /**
     * Plot the heat-map, but this time using the robust z-scores from the
     * median cell speed.
     */
    private void plotZScoreHeatMap() {
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().removeAll();
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().repaint();

        heatMapPlatePanel.setValues(computeZScoresForMap());
        heatMapPlatePanel.repaint();

        HeatMapScalePanel heatMapScalePanel = new HeatMapScalePanel(heatMapPlatePanel.getMin(), heatMapPlatePanel.getMax());
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().add(heatMapScalePanel, gridBagConstraints);
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().revalidate();

        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().repaint();
    }

    /**
     * Plot the z-scores: simple scatterplot
     */
    private void plotZScores() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        Map<Well, Double> map = computeZScoresForMap();

        List<PlateCondition> plateConditionList = trackCoordinatesController.getPlateConditionList();

        for (int i = 0; i < plateConditionList.size(); i++) {
            // current condition
            PlateCondition condition = plateConditionList.get(i);
            XYSeries series = new XYSeries(i + "-" + condition);
            for (int j = 0; j < condition.getSingleCellAnalyzedWells().size(); j++) {
                // current well
                Well well = condition.getSingleCellAnalyzedWells().get(j);
                series.add(i + 1, map.get(well));
            }
            xySeriesCollection.addSeries(series);
        }

        JFreeChart jfreechart = ChartFactory.createScatterPlot("z*-score", "condition number", "z*-score", xySeriesCollection,
                PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupXYPlot(jfreechart.getXYPlot());
        jfreechart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // line for the median speed
        ValueMarker marker = new ValueMarker(0);
        marker.setPaint(Color.GRAY);
        Stroke dashedStroke = new BasicStroke(
                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{1.0f, 3.0f}, 0.0f);
        marker.setStroke(dashedStroke);
        jfreechart.getXYPlot().addRangeMarker(marker);

        marker = new ValueMarker(3);
        marker.setPaint(Color.GRAY);
        marker.setStroke(dashedStroke);
        jfreechart.getXYPlot().addRangeMarker(marker);

        marker = new ValueMarker(-3);
        marker.setPaint(Color.GRAY);
        marker.setStroke(dashedStroke);
        jfreechart.getXYPlot().addRangeMarker(marker);

        XYItemRenderer renderer = jfreechart.getXYPlot().getRenderer();
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            // plot lines according to conditions indexes
            int colorIndex = i % GuiUtils.getAvailableColors().length;
            Color color = GuiUtils.getAvailableColors()[colorIndex];
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 127);
            renderer.setSeriesPaint(i, color);
            renderer.setSeriesShape(i, new Ellipse2D.Double(0, 0, 10, 10));
        }
        zScoreChartPanel.setChart(jfreechart);
    }

    /**
     *
     * @return
     */
    private Map<Well, Double> computeZScoresForMap() {
        Map<Well, Double> zScoresMap = new LinkedHashMap<>();
        double plateMedianSpeed = trackCoordinatesController.getPlateMedianSpeed();
        double plateMADSpeed = trackCoordinatesController.getPlateMADSpeed();

        double min = 0.0000000000000000;
        double max = 0.0000000000000000;

        List<PlateCondition> plateConditionList = trackCoordinatesController.getPlateConditionList();
        for (PlateCondition condition : plateConditionList) {
            SingleCellConditionDataHolder conditionDataHolder = trackCoordinatesController.getConditionDataHolder(condition);
            List<SingleCellWellDataHolder> singleCellWellDataHolders = conditionDataHolder.getSingleCellWellDataHolders();
            for (SingleCellWellDataHolder wellDataHolder : singleCellWellDataHolders) {
                if (!wellDataHolder.getTrackDataHolders().isEmpty()) {
                    Well well = wellDataHolder.getWell();

                    Double[] trackSpeedsVector = wellDataHolder.getTrackSpeedsVector();
                    double wellMedianSpeed = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(trackSpeedsVector)));
                    double zScore = (wellMedianSpeed - plateMedianSpeed) / plateMADSpeed;
                    if (zScore > max) {
                        max = zScore;
                    }
                    if (zScore < min) {
                        min = zScore;
                    }
                    zScoresMap.put(well, zScore);
                }
            }
        }
        heatMapPlatePanel.setMin(min);
        heatMapPlatePanel.setMax(max);
        return zScoresMap;
    }

    /**
     *
     * @return
     */
    private Map<Well, Double> computeValuesMap() {
        Map<Well, Double> valuesMap = new LinkedHashMap<>();

        TrackCoordinatesPanel trackCoordinatesPanel = trackCoordinatesController.getTrackCoordinatesPanel();
        int measurement = trackCoordinatesPanel.getMeasurementComboBox().getSelectedIndex();
        int aggregation = trackCoordinatesPanel.getAggregationComboBox().getSelectedIndex();
        Double value = 0.0;
        double min = 0.0000000000000000;
        double max = 0.0000000000000000;
        List<PlateCondition> plateConditionList = trackCoordinatesController.getPlateConditionList();
        for (PlateCondition condition : plateConditionList) {
            SingleCellConditionDataHolder conditionDataHolder = trackCoordinatesController.getConditionDataHolder(condition);
            List<SingleCellWellDataHolder> singleCellWellDataHolders = conditionDataHolder.getSingleCellWellDataHolders();
            for (SingleCellWellDataHolder wellDataHolder : singleCellWellDataHolders) {
                if (!wellDataHolder.getTrackDataHolders().isEmpty()) {
                    Well well = wellDataHolder.getWell();

                    switch (measurement) {
                        case 0: // number of trajectories
                            int nrTrack = wellDataHolder.getTrackDataHolders().size();
                            value = (double) nrTrack;

                            if (value > max) {
                                max = value;
                            }
                            if (value < min) {
                                min = value;
                            }
                            break;
                        case 1: // speed
                            Double[] trackSpeedsVector = wellDataHolder.getTrackSpeedsVector();
                            switch (aggregation) {
                                case 0: // mean
                                    value = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(trackSpeedsVector)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                                case 1: // median
                                    value = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(trackSpeedsVector)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                            }
                            break;
                        case 3: // directionality
                            Double[] endPointDirectionalityRatios = wellDataHolder.getEndPointDirectionalityRatios();
                            switch (aggregation) {
                                case 0: // mean
                                    value = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(endPointDirectionalityRatios)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                                case 1: // median
                                    value = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(endPointDirectionalityRatios)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                            }
                            break;
                    }
                    valuesMap.put(well, value);
                }

            }
        }
        heatMapPlatePanel.setMin(min);
        heatMapPlatePanel.setMax(max);
        return valuesMap;

    }
}
