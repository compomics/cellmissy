/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.explore;

import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import be.ugent.maf.cellmissy.entity.result.singlecell.EnclosingBall;
import be.ugent.maf.cellmissy.entity.result.singlecell.FractalDimension;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola
 */
@Controller("enclosingBallController")
class EnclosingBallController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnclosingBallController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<EnclosingBall> xYBalls;
    private ObservableList<EnclosingBall> xTBalls;
    private ObservableList<EnclosingBall> yTBalls;
    // view
    private ChartPanel xYBallsChartPanel;
    private ChartPanel xTBallsChartPanel;
    private ChartPanel yTBallsChartPanel;
    private ChartPanel xYFDChartPanel;
    private ChartPanel xTFDChartPanel;
    private ChartPanel yTFDChartPanel;
    // parent controller
    @Autowired
    private ExploreTrackController exploreTrackController;
    // child controllers
    // services
    @Autowired
    private LinearRegressor linearRegressor;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init main components
        initComponents();
    }

    /**
     * Initialize the main logic of the controller
     */
    private void initComponents() {
        // initialize the data lists
        xYBalls = ObservableCollections.observableList(new ArrayList<>());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, xYBalls,
                exploreTrackController.getExploreTrackPanel().getSpatialEnclosingBallList());
        bindingGroup.addBinding(jListBinding);

        xTBalls = ObservableCollections.observableList(new ArrayList<>());
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, xTBalls,
                exploreTrackController.getExploreTrackPanel().getXtEnclosingBallList());
        bindingGroup.addBinding(jListBinding);

        yTBalls = ObservableCollections.observableList(new ArrayList<>());
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, yTBalls,
                exploreTrackController.getExploreTrackPanel().getYtEnclosingBallList());
        bindingGroup.addBinding(jListBinding);
        // do the binding
        bindingGroup.bind();

        // initialize the view components
        xYBallsChartPanel = new ChartPanel(null);
        xYBallsChartPanel.setOpaque(false);

        xTBallsChartPanel = new ChartPanel(null);
        xTBallsChartPanel.setOpaque(false);

        yTBallsChartPanel = new ChartPanel(null);
        yTBallsChartPanel.setOpaque(false);

        xYFDChartPanel = new ChartPanel(null);
        xYFDChartPanel.setOpaque(false);

        xTFDChartPanel = new ChartPanel(null);
        xTFDChartPanel.setOpaque(false);

        yTFDChartPanel = new ChartPanel(null);
        yTFDChartPanel.setOpaque(false);

        // add the subviews to the main view
        exploreTrackController.getExploreTrackPanel().getxYBallsParentPanel().add(xYBallsChartPanel, gridBagConstraints);
        exploreTrackController.getExploreTrackPanel().getxTBallsParentPanel().add(xTBallsChartPanel, gridBagConstraints);
        exploreTrackController.getExploreTrackPanel().getyTBallsParentPanel().add(yTBallsChartPanel, gridBagConstraints);

        exploreTrackController.getExploreTrackPanel().getxYFDParentPanel().add(xYFDChartPanel, gridBagConstraints);
        exploreTrackController.getExploreTrackPanel().getxTFDParentPanel().add(xTFDChartPanel, gridBagConstraints);
        exploreTrackController.getExploreTrackPanel().getyTFDParentPanel().add(yTFDChartPanel, gridBagConstraints);

    }

    // update the x-y enclosing ball list.
    public void updateXYBalls(TrackDataHolder trackDataHolder, int index) {
        List<EnclosingBall> balls = trackDataHolder.getStepCentricDataHolder().getxYEnclosingBalls().get(index);
        xYBalls.clear();
        xYBalls.addAll(balls);
        exploreTrackController.getExploreTrackPanel().getSpatEnclosingBallsTextField().setText("" + balls.size());
    }

    // update the x-t and y-t enclosing ball lists.
    public void updateTBalls(TrackDataHolder trackDataHolder, int index) {
        List<EnclosingBall> balls = trackDataHolder.getStepCentricDataHolder().getxTEnclosingBalls().get(index);
        exploreTrackController.getExploreTrackPanel().getXtEnclosingBallsTextField().setText("" + balls.size());
        xTBalls.clear();
        xTBalls.addAll(balls);
        balls = trackDataHolder.getStepCentricDataHolder().getyTEnclosingBalls().get(index);
        exploreTrackController.getExploreTrackPanel().getYtEnclosingBallsTextField().setText("" + balls.size());
        yTBalls.clear();
        yTBalls.addAll(balls);
    }

    // update the value of the track entropy
    public void updateTrackEntropy(TrackDataHolder trackDataHolder, int index) {
        Double entropy = trackDataHolder.getCellCentricDataHolder().getEntropies().get(index);
        exploreTrackController.getExploreTrackPanel().getTrackEntropyTextField().setText("" + AnalysisUtils.roundThreeDecimals(entropy));
    }

    // plot x-y enclosing balls for specified track
    public void plotXYBalls(TrackDataHolder trackDataHolder) {
        int selectedIndexRadius = exploreTrackController.getExploreTrackPanel().getEnclosingBallRadiusCombobox().getSelectedIndex();
        List<List<EnclosingBall>> enclosingBallsList = trackDataHolder.getStepCentricDataHolder().getxYEnclosingBalls();
        List<EnclosingBall> enclosingBalls = enclosingBallsList.get(selectedIndexRadius);
        // get the coordinates matrix
        Double[][] coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix();
        XYSeries xYSeries = JFreeChartUtils.generateXYSeries(coordinatesMatrix);
        String seriesKey = "track " + trackDataHolder.getTrack().getTrackNumber() + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell();
        xYSeries.setKey(seriesKey);
        XYSeriesCollection ySeriesCollection = new XYSeriesCollection(xYSeries);
        JFreeChart chart = ChartFactory.createXYLineChart(seriesKey + " - enclosing balls", "x (µm)", "y (µm)", ySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        XYPlot xyPlot = chart.getXYPlot();
        JFreeChartUtils.setupXYPlot(xyPlot);
        JFreeChartUtils.setupSingleTrackPlot(chart, exploreTrackController.getExploreTrackPanel().getTracksList().getSelectedIndex(), true);
        XYSeriesCollection dataset = (XYSeriesCollection) xyPlot.getDataset(0);
        double minY = dataset.getSeries(0).getMinY();
        double maxY = dataset.getSeries(0).getMaxY();
        xyPlot.getRangeAxis().setRange(minY - 10, maxY + 10);

        xYBallsChartPanel.setChart(chart);
        enclosingBalls.stream().forEach((ball) -> {
            xyPlot.addAnnotation(new XYShapeAnnotation(ball.getShape(), JFreeChartUtils.getDashedLine(), GuiUtils.getDefaultColor()));
        });
    }

    // plot x-t enclosing balls for specified track
    public void plotXTBalls(TrackDataHolder trackDataHolder) {
        int selectedIndexEpsilon = exploreTrackController.getExploreTrackPanel().getEnclosingBallEpsCombobox().getSelectedIndex();
        List<List<EnclosingBall>> xtEnclosingBallList = trackDataHolder.getStepCentricDataHolder().getxTEnclosingBalls();
        List<EnclosingBall> xTempBalls = xtEnclosingBallList.get(selectedIndexEpsilon);
        // get the track coordinates matrix and transpose it
        Double[][] transpose2DArray = AnalysisUtils.transpose2DArray(trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix());
        // we get the x coordinates and the time information
        double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transpose2DArray[0]));
        double[] timeIndexes = trackDataHolder.getStepCentricDataHolder().getTimeIndexes();
        // we create the series and set its key
        XYSeries xtSeries = JFreeChartUtils.generateXYSeries(timeIndexes, xCoordinates);
        String seriesKey = "track " + trackDataHolder.getTrack().getTrackNumber() + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell();
        xtSeries.setKey(seriesKey);
        // we then create the XYSeriesCollection and use it to make a new line chart
        XYSeriesCollection xtSeriesCollection = new XYSeriesCollection(xtSeries);
        JFreeChart chart = ChartFactory.createXYLineChart(seriesKey + " - enclosing balls", "time", "x (µm)", xtSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis yaxis = (NumberAxis) xyPlot.getRangeAxis();
        yaxis.setAutoRangeIncludesZero(false);
        JFreeChartUtils.setupXYPlot(xyPlot);
        JFreeChartUtils.setupSingleTrackPlot(chart, exploreTrackController.getExploreTrackPanel().getTracksList().getSelectedIndex(), true);
        xTBallsChartPanel.setChart(chart);
        xTempBalls.stream().forEach((ball) -> {
            xyPlot.addAnnotation(new XYShapeAnnotation(ball.getShape(), JFreeChartUtils.getDashedLine(), GuiUtils.getDefaultColor()));
        });
    }

    // plot y-t enclosing balls for specified track
    public void plotYTBalls(TrackDataHolder trackDataHolder) {
        int selectedIndexEpsilon = exploreTrackController.getExploreTrackPanel().getEnclosingBallEpsCombobox().getSelectedIndex();
        List<List<EnclosingBall>> ytEnclosingBallList = trackDataHolder.getStepCentricDataHolder().getyTEnclosingBalls();
        List<EnclosingBall> yTempBalls = ytEnclosingBallList.get(selectedIndexEpsilon);
        // get the track coordinates matrix and transpose it
        Double[][] transpose2DArray = AnalysisUtils.transpose2DArray(trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix());
        // we get the y coordinates and the time information
        double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transpose2DArray[1]));
        double[] timeIndexes = trackDataHolder.getStepCentricDataHolder().getTimeIndexes();
        // we create the series and set its key
        XYSeries ytSeries = JFreeChartUtils.generateXYSeries(timeIndexes, yCoordinates);
        String seriesKey = "track " + trackDataHolder.getTrack().getTrackNumber() + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell();
        ytSeries.setKey(seriesKey);
        // we then create the XYSeriesCollection and use it to make a new line chart
        XYSeriesCollection ytSeriesCollection = new XYSeriesCollection(ytSeries);
        JFreeChart chart = ChartFactory.createXYLineChart(seriesKey + " - enclosing balls", "time", "y (µm)", ytSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis yaxis = (NumberAxis) xyPlot.getRangeAxis();
        yaxis.setAutoRangeIncludesZero(false);
        JFreeChartUtils.setupXYPlot(xyPlot);
        JFreeChartUtils.setupSingleTrackPlot(chart, exploreTrackController.getExploreTrackPanel().getTracksList().getSelectedIndex(), true);
        yTBallsChartPanel.setChart(chart);
        yTempBalls.stream().forEach((ball) -> {
            xyPlot.addAnnotation(new XYShapeAnnotation(ball.getShape(), JFreeChartUtils.getDashedLine(), GuiUtils.getDefaultColor()));
        });
    }

    // plot x-y fractal analysis
    public void plotXYFractalAnalysis(TrackDataHolder trackDataHolder) {
        FractalDimension xyfd = trackDataHolder.getCellCentricDataHolder().getxYFD();
        xYFDChartPanel.setChart(makeFDChart(trackDataHolder, xyfd));
    }

    // plot x-t fractal analysis
    public void plotXTFractalAnalysis(TrackDataHolder trackDataHolder) {
        FractalDimension xtfd = trackDataHolder.getCellCentricDataHolder().getxTFD();
        xTFDChartPanel.setChart(makeFDChart(trackDataHolder, xtfd));
    }

    // plot y-t fractal analysis
    public void plotYTFractalAnalysis(TrackDataHolder trackDataHolder) {
        FractalDimension ytfd = trackDataHolder.getCellCentricDataHolder().getyTFD();
        yTFDChartPanel.setChart(makeFDChart(trackDataHolder, ytfd));
    }

    // create a chart for a fractal dimension analysis
    private JFreeChart makeFDChart(TrackDataHolder trackDataHolder, FractalDimension fractalDimension) {
        XYSeries series = JFreeChartUtils.generateXYSeries(fractalDimension.getxValues(), fractalDimension.getyValues());
        String seriesKey = "track " + trackDataHolder.getTrack().getTrackNumber() + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell();
        series.setKey(seriesKey);
        XYSeriesCollection collection = new XYSeriesCollection(series);

        double regression[] = Regression.getOLSRegression(collection, 0);
        // first the intercept, then the slope
        LineFunction2D linefunction2d = new LineFunction2D(regression[0], regression[1]);

        fractalDimension.setFD(regression[1]);
        JFreeChart chart = ChartFactory.createScatterPlot(seriesKey + " - FD = " + AnalysisUtils.roundThreeDecimals(fractalDimension.getFD()),
                "log(1/r)", "log(N)", collection, PlotOrientation.VERTICAL, false, true, false);
        // start, end, number of samples
        XYDataset dataset = DatasetUtilities.sampleFunction2D(linefunction2d, series.getMinX(), series.getMaxX(), 1000, "Fitted Regression Line");
        chart.getXYPlot().setDataset(1, dataset);

        JFreeChartUtils.setupXYPlot(chart.getXYPlot());
        JFreeChartUtils.setupSingleTrackPlot(chart, exploreTrackController.getExploreTrackPanel().getTracksList().getSelectedIndex(), true);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShape(0, new Ellipse2D.Double(0, 0, 8, 8));

        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
        renderer2.setSeriesPaint(0, GuiUtils.getDefaultColor());
        chart.getXYPlot().setRenderer(1, renderer2);

        return chart;
    }

}
