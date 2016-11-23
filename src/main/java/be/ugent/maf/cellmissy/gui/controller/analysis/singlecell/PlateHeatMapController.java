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
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.plate.HeatMapPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ColorScale;
import be.ugent.maf.cellmissy.gui.view.renderer.ColorScale1;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
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

        // the real plotting action
        trackCoordinatesPanel.getPlotHeatMapButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                plotHeatMap();
                heatMapPlatePanel.repaint();
            }
        });

        // click on first view
        trackCoordinatesPanel.getTrackCoordinatesTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (firstView) {
                    Dimension parentDimension = trackCoordinatesController.getTrackCoordinatesPanel().getGraphicParentPanel().getSize();
                    heatMapPlatePanel.setExperiment(trackCoordinatesController.getExperiment());
                    heatMapPlatePanel.initPanel(trackCoordinatesController.getExperiment().getPlateFormat(), parentDimension);
                    firstView = false;
                }
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
        heatMapPlatePanel.setValues(computeValuesMap());
        heatMapPlatePanel.repaint();
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().removeAll();

        // does not work!
//        JFreeChart legendChart = new JFreeChart("", null,
//                         new XYPlot(), 
//                         false); //Hidden plot is a plot implementation that does not draw anything.
//
//        LegendTitle legendTitle = new LegendTitle(createChart(createDataset()).getPlot());
//        legendTitle.setVisible(true);
//        legendTitle.setVerticalAlignment(VerticalAlignment.TOP);
//        legendChart.addLegend(legendTitle);
        
        
        ColorScale colorScale = new ColorScale(heatMapPlatePanel.getMin(), heatMapPlatePanel.getMax());
        JPanel initialize = colorScale.initialize(trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel());
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().add(initialize, gridBagConstraints);
        
//        JFreeChart createChart = createChart(createDataset());
//        ChartPanel chartPanel = new ChartPanel(createChart);
//
//        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().add(chartPanel, gridBagConstraints);
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().repaint();

    }

    private XYZDataset createDataset() {
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        Map<Well, Double> map = heatMapPlatePanel.getValues();
        int nRow = Integer.parseInt(trackCoordinatesController.getTrackCoordinatesPanel().getnRowTextField().getText());
        int nCol = Integer.parseInt(trackCoordinatesController.getTrackCoordinatesPanel().getnColTextField().getText());
        double[][] data = new double[3][nRow * nCol];

        int z = 0;
        List<Well> wells = new ArrayList<>(map.keySet());
        for (int i = 0; i < nRow - 1; i++) {

            for (int j = 0; j < nCol - 1; j++) {
                data[0][j] = i + 1;
                data[1][j] = j + 1;
                data[2][j] = map.get(wells.get(z));
                z++;
            }

        }
        dataset.addSeries("", data);
        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        ColorScale1 colorScale1 = new ColorScale1(heatMapPlatePanel.getMin(), heatMapPlatePanel.getMax());
        NumberAxis xAxis = new NumberAxis("col");
        NumberAxis yAxis = new NumberAxis("row");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

        JFreeChart chart = new JFreeChart(plot);

        NumberAxis scaleAxis = new NumberAxis("scale");
        scaleAxis.setAxisLinePaint(Color.white);
        scaleAxis.setTickMarkPaint(Color.white);

        XYBlockRenderer r = new XYBlockRenderer();

        r.setPaintScale(colorScale1);
        r.setBlockHeight(1);
        r.setBlockWidth(1);
        plot.setRenderer(r);

        PaintScaleLegend legend = new PaintScaleLegend(colorScale1, scaleAxis);
//        legend.setSubdivisionCount(128);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
//        legend.setPadding(new RectangleInsets(10, 10, 10, 10));
//        legend.setStripWidth(20);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(Color.WHITE);
        chart.addSubtitle(legend);
        chart.setBackgroundPaint(Color.white);
        return chart;
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
        double min = 0.00000;
        double max = 0.00000;
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
