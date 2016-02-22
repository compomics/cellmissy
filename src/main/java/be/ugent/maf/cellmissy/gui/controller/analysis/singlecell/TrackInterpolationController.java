package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.singlecell.InterpolationMethod;
import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.PolarChartPanel;
import org.jfree.chart.axis.NumberAxis;
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
    // view
    private List<ChartPanel> interpolatedTrackChartPanels;
    private List<ChartPanel> combinedChartPanels;
    private List<ChartPanel> histChartPanels;
    private List<ChartPanel> polarChartPanels;
    // parent controller
    @Autowired
    private ExploreTrackController exploreTrackController;
    // child controllers
    // services

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrackInterpolationController.class);

    /**
     * Initialize controller.
     */
    public void init() {
        // initialize components for the main view
        initMainViewComponents();
    }

    /**
     * Plot data for an interpolated track.
     *
     * @param trackDataHolder
     */
    public void plotInterpolatedTrackData(TrackDataHolder trackDataHolder) {
        plotInterpolatedTrackCoordinates(trackDataHolder);
        plotInterpolatedTrackTimeSeries(trackDataHolder);
        plotInterpolatedHistograms(trackDataHolder);
        plotInterpolatedPolarPlots(trackDataHolder);
    }

    /**
     * Private methods and classes.
     */
    /**
     * Initialize main view components.
     */
    private void initMainViewComponents() {
        interpolatedTrackChartPanels = new ArrayList<>();
        combinedChartPanels = new ArrayList<>();
        histChartPanels = new ArrayList<>();
        polarChartPanels = new ArrayList<>();
    }

    /**
     * Plot the interpolated track coordinates.
     *
     * @param trackDataHolder
     */
    private void plotInterpolatedTrackCoordinates(TrackDataHolder trackDataHolder) {
        // first reset the view
        if (!interpolatedTrackChartPanels.isEmpty()) {
            interpolatedTrackChartPanels.clear();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().removeAll();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTrackParentPanel().repaint();
        }
        List<XYSeriesCollection> collections = makeInterpolationCoordinatesCollections(trackDataHolder);
        for (int i = 0; i < collections.size(); i++) {
            JFreeChart interpolatedTrackChart = ChartFactory.createXYLineChart("" + collections.get(i).getSeriesKey(0), "x (µm)", "y (µm)", collections.get(i),
                    PlotOrientation.VERTICAL, false, true, false);
            ChartPanel interpolatedTrackChartPanel = new ChartPanel(null);
            interpolatedTrackChartPanel.setOpaque(false);
            // compute the constraints
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(collections.size(), i, collections.size());
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
    private void plotInterpolatedTrackTimeSeries(TrackDataHolder trackDataHolder) {
        // first reset the view
        if (!combinedChartPanels.isEmpty()) {
            combinedChartPanels.clear();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().removeAll();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().repaint();
        }
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
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(xtCollections.size(), i, xtCollections.size());
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().add(combinedChartPanel, tempBagConstraints);

            combinedChartPanels.add(combinedChartPanel);
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedTemporalEvolutionParentPanel().repaint();
        }
    }

    /**
     * Plot the histogram distribution for the turning angle for a given track
     * data holder.
     *
     * @param trackDataHolder
     */
    private void plotInterpolatedHistograms(TrackDataHolder trackDataHolder) {
        // first reset the view
        if (!histChartPanels.isEmpty()) {
            histChartPanels.clear();
            exploreTrackController.getExploreTrackPanel().getInterpolatedHistogramParentPanel().removeAll();
            exploreTrackController.getExploreTrackPanel().getInterpolatedHistogramParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedHistogramParentPanel().repaint();
        }
        List<HistogramDataset> datasets = makeHistDatasets(trackDataHolder);
        for (int i = 0; i < datasets.size(); i++) {
            JFreeChart histogramChart = ChartFactory.createHistogram("",
                    "", "inst turn angle  - track " + trackDataHolder.getTrack().getTrackid(),
                    datasets.get(i), PlotOrientation.VERTICAL, false, true, false);
            JFreeChartUtils.setShadowVisible(histogramChart, false);
            JFreeChartUtils.setUpHistogramChart(histogramChart, exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
            ChartPanel histChartPanel = new ChartPanel(histogramChart);
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(datasets.size(), i, datasets.size());
            exploreTrackController.getExploreTrackPanel().getInterpolatedHistogramParentPanel().add(histChartPanel, tempBagConstraints);

            histChartPanels.add(histChartPanel);
            exploreTrackController.getExploreTrackPanel().getInterpolatedHistogramParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedHistogramParentPanel().repaint();
        }
    }

    /**
     * Plot the polar plot for turning angle for a given track data holder.
     *
     * @param trackDataHolder
     */
    private void plotInterpolatedPolarPlots(TrackDataHolder trackDataHolder) {
        // first reset the view
        if (!polarChartPanels.isEmpty()) {
            polarChartPanels.clear();
            exploreTrackController.getExploreTrackPanel().getInterpolatedPolarParentPanel().removeAll();
            exploreTrackController.getExploreTrackPanel().getInterpolatedPolarParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedPolarParentPanel().repaint();
        }
        List<XYSeriesCollection> polarData = makePolarDatasets(trackDataHolder);
        for (int i = 0; i < polarData.size(); i++) {
            PolarPlot plot = new PolarPlot(polarData.get(i), new NumberAxis(), new DefaultPolarItemRenderer());
            JFreeChart polarChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
            JFreeChartUtils.setupPolarChart(polarChart, exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
            ChartPanel polarChartPanel = new PolarChartPanel(polarChart);
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(polarData.size(), i, 1);
            exploreTrackController.getExploreTrackPanel().getInterpolatedPolarParentPanel().add(polarChartPanel, tempBagConstraints);

            polarChartPanels.add(polarChartPanel);
            exploreTrackController.getExploreTrackPanel().getInterpolatedPolarParentPanel().revalidate();
            exploreTrackController.getExploreTrackPanel().getInterpolatedPolarParentPanel().repaint();
        }
    }

    /**
     * For a specific track data holder, create the datasets for the histogram.
     *
     * @param trackDataHolder
     * @return
     */
    private List<HistogramDataset> makeHistDatasets(TrackDataHolder trackDataHolder) {
        List<HistogramDataset> datasets = new ArrayList<>();
        Map<InterpolationMethod, InterpolatedTrack> interpolationMap = trackDataHolder.getStepCentricDataHolder().getInterpolationMap();
        interpolationMap.keySet().stream().forEach((method) -> {
            double[] turningAngles = interpolationMap.get(method).getTurningAngles();
            HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramType.FREQUENCY);
            double range = Arrays.stream(turningAngles).max().getAsDouble() - Arrays.stream(turningAngles).min().getAsDouble();
            String seriesKey = method.getStringForType() + "\n" + interpolationMap.get(method).toString();
            dataset.addSeries(seriesKey, turningAngles, (int) range / 5);
            datasets.add(dataset);
        });
        return datasets;
    }

    /**
     * Make the datasets for the turning angle polar plot.
     *
     * @param trackDataHolder
     * @return
     */
    private List<XYSeriesCollection> makePolarDatasets(TrackDataHolder trackDataHolder) {
        List<XYSeriesCollection> collections = new ArrayList<>();
        Map<InterpolationMethod, InterpolatedTrack> interpolationMap = trackDataHolder.getStepCentricDataHolder().getInterpolationMap();
        List<InterpolationMethod> methods = new ArrayList<>(interpolationMap.keySet());
        List<HistogramDataset> histDatasets = makeHistDatasets(trackDataHolder);

        for (int i = 0; i < methods.size(); i++) {
            XYSeriesCollection data = new XYSeriesCollection();
            InterpolationMethod method = methods.get(i);
            String seriesKey = "interpolated track " + trackDataHolder.getTrack().getTrackNumber()
                    + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + ", " + method.getStringForType()
                    + "\n" + interpolationMap.get(method).toString();
            XYSeries series = new XYSeries(seriesKey, false);
            for (int seriesCount = 0; seriesCount < histDatasets.get(i).getSeriesCount(); seriesCount++) {
                for (int itemCount = 0; itemCount < histDatasets.get(i).getItemCount(seriesCount); itemCount++) {
                    double startX = (double) histDatasets.get(i).getStartX(seriesCount, itemCount);
                    double endX = (double) histDatasets.get(i).getEndX(seriesCount, itemCount);
                    double theta = (startX + endX) / 2;
                    Double radius = (Double) histDatasets.get(i).getY(seriesCount, itemCount);
                    series.add(theta, radius);
                }
            }
            data.addSeries(series);
            collections.add(data);
        }
        return collections;
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
                    + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + ", " + method.getStringForType()
                    + "\n" + interpolationMap.get(method).toString();
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
                    + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + ", " + method.getStringForType()
                    + "\n" + interpolationMap.get(method).toString();
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
                    + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + ", " + method.getStringForType()
                    + "\n" + interpolationMap.get(method).toString();
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
