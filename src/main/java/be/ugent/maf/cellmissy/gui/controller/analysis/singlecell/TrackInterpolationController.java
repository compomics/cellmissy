package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.singlecell.InterpolationMethod;
import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdesktop.beansbinding.BindingGroup;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Component to control track interpolation logic. Has the explore track
 * controller as parent controller.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("trackInterpolationController")
public class TrackInterpolationController {

    // model
    private BindingGroup bindingGroup;

    // view
    private List<ChartPanel> interpolatedTrackChartPanels;
    private List<ChartPanel> combinedChartPanels;
    // parent controller
    @Autowired
    private ExploreTrackController exploreTrackController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrackInterpolationController.class);

    /**
     * Initialize controller.
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // initialize components for the main view
        initMainViewComponents();
    }

    /**
     * Initialize main view components.
     */
    private void initMainViewComponents() {
        interpolatedTrackChartPanels = new ArrayList<>();
        combinedChartPanels = new ArrayList<>();
    }

    /**
     * Plot the interpolated track coordinates.
     *
     * @param trackDataHolder
     */
    public void plotInterpolatedTrackCoordinates(TrackDataHolder trackDataHolder) {
        // first reset the view
        if (!interpolatedTrackChartPanels.isEmpty()) {
            interpolatedTrackChartPanels.clear();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().removeAll();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().repaint();
        }
        List<XYSeriesCollection> collections = makeInterpolationCoordinatesCollections(trackDataHolder);
        for (int i = 0; i < collections.size(); i++) {
            XYSeriesCollection collection = collections.get(i);
            JFreeChart interpolatedTrackChart = ChartFactory.createXYLineChart("" + collection.getSeriesKey(0), "x (µm)", "y (µm)", collection,
                      PlotOrientation.VERTICAL, false, true, false);
            ChartPanel interpolatedTrackChartPanel = new ChartPanel(null);
            interpolatedTrackChartPanel.setOpaque(false);
            // compute the constraints
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(collections.size(), i, 3);
            XYPlot xyPlot = interpolatedTrackChart.getXYPlot();
            JFreeChartUtils.setupSingleTrackPlot(interpolatedTrackChart,
                      exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder), false);

            NumberAxis axis = (NumberAxis) xyPlot.getDomainAxis();
            axis.setAutoRangeIncludesZero(false);
            axis.setAutoRange(true);

            axis = (NumberAxis) xyPlot.getRangeAxis();
            axis.setAutoRangeIncludesZero(false);
            axis.setAutoRange(true);

            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
            // show no points
            renderer.setSeriesShapesVisible(0, false);
            interpolatedTrackChartPanel.setChart(interpolatedTrackChart);
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().add(interpolatedTrackChartPanel, tempBagConstraints);

            interpolatedTrackChartPanels.add(interpolatedTrackChartPanel);
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().repaint();
        }
    }

    /**
     * Plot the interpolated track time series.
     *
     * @param trackDataHolder
     */
    public void plotInterpolatedTrackTimeSeries(TrackDataHolder trackDataHolder) {
        // first reset the view
        if (!combinedChartPanels.isEmpty()) {
            combinedChartPanels.clear();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().removeAll();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().repaint();
        }
        Map<InterpolationMethod, InterpolatedTrack> interpolationMap = trackDataHolder.getStepCentricDataHolder().getInterpolationMap();
        List<XYSeriesCollection> xtCollections = makeInterpolationXTSeriesCollections(trackDataHolder);
        List<XYSeriesCollection> ytCollections = makeInterpolationYTSeriesCollections(trackDataHolder);
        XYItemRenderer renderer = new StandardXYItemRenderer();
        NumberAxis xAxis = new NumberAxis("x (µm)");
        NumberAxis yAxis = new NumberAxis("y (µm)");
        for (int i = 0; i < xtCollections.size(); i++) {
            XYPlot xTPlot = new XYPlot(xtCollections.get(i), null, xAxis, renderer);
            XYPlot yTPlot = new XYPlot(ytCollections.get(i), null, yAxis, renderer);
            // domain axis
            NumberAxis domainAxis = new NumberAxis("time index");
            CombinedDomainXYPlot combinedDomainXYPlot = new CombinedDomainXYPlot(domainAxis);
            combinedDomainXYPlot.setRenderer(new XYLineAndShapeRenderer());
            combinedDomainXYPlot.add(xTPlot);
            combinedDomainXYPlot.add(yTPlot);
            combinedDomainXYPlot.setOrientation(PlotOrientation.VERTICAL);
            JFreeChart combinedChart = new JFreeChart("" + xtCollections.get(i).getSeries(0).getKey(),
                      JFreeChartUtils.getChartFont(), combinedDomainXYPlot, Boolean.FALSE);
            JFreeChartUtils.setupCombinedChart(combinedChart, exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
            ChartPanel combinedChartPanel = new ChartPanel(combinedChart);
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(xtCollections.size(), i, 3);
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().add(combinedChartPanel, tempBagConstraints);

            combinedChartPanels.add(combinedChartPanel);
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().repaint();
        }
    }

    /**
     * For a specific track data holder (the one selected in the parent
     * controller (explore-track-controller), create collections to generate x-y
     * plot for interpolated track coordinates.
     *
     * @param trackDataHolder
     * @return
     */
    private List<XYSeriesCollection> makeInterpolationCoordinatesCollections(TrackDataHolder trackDataHolder) {
        List<XYSeriesCollection> collections = new ArrayList<>();
        Map<InterpolationMethod, InterpolatedTrack> interpolationMap = trackDataHolder.getStepCentricDataHolder().getInterpolationMap();
        interpolationMap.keySet().stream().map((method) -> {
            double[] interpolatedX = interpolationMap.get(method).getInterpolatedX();
            double[] interpolatedY = interpolationMap.get(method).getInterpolatedY();
            String seriesKey = "interpolated track " + trackDataHolder.getTrack().getTrackNumber()
                      + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + ", " + method.getStringForType();
            XYSeries xYSeries = makeSeries(seriesKey, interpolatedX, interpolatedY);
            return xYSeries;
        }).map((xYSeries) -> new XYSeriesCollection(xYSeries)).forEach((collection) -> {
            collections.add(collection);
        });
        return collections;
    }

    /**
     * For a specific track data holder, create collections to generate x(t)
     * plots.
     *
     * @param trackDataHolder
     * @return
     */
    private List<XYSeriesCollection> makeInterpolationXTSeriesCollections(TrackDataHolder trackDataHolder) {
        List<XYSeriesCollection> collections = new ArrayList<>();
        Map<InterpolationMethod, InterpolatedTrack> interpolationMap = trackDataHolder.getStepCentricDataHolder().getInterpolationMap();
        interpolationMap.keySet().stream().map((method) -> {
            double[] interpolantTime = interpolationMap.get(method).getInterpolantTime();
            double[] interpolatedX = interpolationMap.get(method).getInterpolatedX();
            String seriesKey = "interpolated track " + trackDataHolder.getTrack().getTrackNumber()
                      + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + ", " + method.getStringForType();
            XYSeries xtSeries = makeSeries(seriesKey, interpolantTime, interpolatedX);
            return xtSeries;
        }).map((xYSeries) -> new XYSeriesCollection(xYSeries)).forEach((collection) -> {
            collections.add(collection);
        });
        return collections;
    }

    /**
     * For a specific track data holder, create collections to generate y(t)
     * plots.
     *
     * @param trackDataHolder
     * @return
     */
    private List<XYSeriesCollection> makeInterpolationYTSeriesCollections(TrackDataHolder trackDataHolder) {
        List<XYSeriesCollection> collections = new ArrayList<>();
        Map<InterpolationMethod, InterpolatedTrack> interpolationMap = trackDataHolder.getStepCentricDataHolder().getInterpolationMap();
        interpolationMap.keySet().stream().map((method) -> {
            double[] interpolantTime = interpolationMap.get(method).getInterpolantTime();
            double[] interpolatedY = interpolationMap.get(method).getInterpolatedY();
            String seriesKey = "interpolated track " + trackDataHolder.getTrack().getTrackNumber()
                      + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + ", " + method.getStringForType();
            XYSeries ytSeries = makeSeries(seriesKey, interpolantTime, interpolatedY);
            return ytSeries;
        }).map((xYSeries) -> new XYSeriesCollection(xYSeries)).forEach((collection) -> {
            collections.add(collection);
        });
        return collections;
    }

    /**
     *
     * @param seriesKey
     * @param a
     * @param b
     * @return
     */
    private XYSeries makeSeries(String seriesKey, double[] a, double[] b) {
        XYSeries series = JFreeChartUtils.generateXYSeries(a, b);
        series.setKey(seriesKey);
        return series;
    }
}
