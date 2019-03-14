/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.LinearRegressor;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.CellCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.ConvexHullOperator;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.BoundingBox;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.EnclosingBall;
import be.ugent.maf.cellmissy.entity.result.singlecell.FractalDimension;
import be.ugent.maf.cellmissy.entity.result.singlecell.MostDistantPointsPair;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An implementation of the cell centric operator.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("cellCentricOperator")
public class CellCentricOperatorImpl implements CellCentricOperator {

    @Autowired
    private GrahamScanAlgorithm grahamScanAlgorithm;
    @Autowired
    private ConvexHullOperator convexHullOperator;
    @Autowired
    private LinearRegressor linearRegressor;

    @Override
    public void computeTrackDuration(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder, double timeLapse) {
        double[] timeIndexes = stepCentricDataHolder.getTimeIndexes();
        int numberOfPoints = timeIndexes.length;
        double duration = (numberOfPoints - 1) * timeLapse;
        cellCentricDataHolder.setTrackDuration(duration);
    }

    @Override
    public void computeBoundingBox(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        Double[][] transposedCoordinatesMatrix = AnalysisUtils.transpose2DArray(coordinatesMatrix);
        List<Double> xCoordAsList = Arrays.asList(transposedCoordinatesMatrix[0]);
        List<Double> yCoordAsList = Arrays.asList(transposedCoordinatesMatrix[1]);
        Double xMin = Collections.min(xCoordAsList);
        Double xMax = Collections.max(xCoordAsList);
        Double yMin = Collections.min(yCoordAsList);
        Double yMax = Collections.max(yCoordAsList);
        BoundingBox boundingBox = new BoundingBox(xMin, xMax, yMin, yMax);
        boundingBox.setxNetDisplacement(xMax - xMin);
        boundingBox.setyNetDisplacement(yMax - yMin);
        cellCentricDataHolder.setBoundingBox(boundingBox);
    }

    @Override
    public void computeMedianDisplacement(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[] instantaneousDisplacements = stepCentricDataHolder.getInstantaneousDisplacements();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(instantaneousDisplacements);
        double medianDisplacement = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNullValues));
        cellCentricDataHolder.setMedianDisplacement(medianDisplacement);
    }

    @Override
    public void computeCumulativeDistance(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        double cumulativeDistance = 0;
        Double[] instantaneousDisplacements = stepCentricDataHolder.getInstantaneousDisplacements();
        for (Double instantaneousDisplacement : instantaneousDisplacements) {
            if (instantaneousDisplacement != null) {
                cumulativeDistance += instantaneousDisplacement;
            }
        }
        cellCentricDataHolder.setCumulativeDistance(cumulativeDistance);
    }

    @Override
    public void computeEuclideanDistance(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        Double[][] transposedCoordinatesMatrix = AnalysisUtils.transpose2DArray(coordinatesMatrix);
        double firstX = transposedCoordinatesMatrix[0][0];
        double firstY = transposedCoordinatesMatrix[1][0];
        double lastX = transposedCoordinatesMatrix[0][transposedCoordinatesMatrix[0].length - 1];
        double lastY = transposedCoordinatesMatrix[1][transposedCoordinatesMatrix[0].length - 1];
        double deltaX = lastX - firstX;
        double deltaY = lastY - firstY;
        // Math.hypot: sqrt(x2 +y2) without intermediate overflow or underflow
        double euclideanDistance = Math.hypot(deltaX, deltaY);
        cellCentricDataHolder.setEuclideanDistance(euclideanDistance);
    }

    @Override
    public void computeMedianSpeed(CellCentricDataHolder cellCentricDataHolder) {
        double cumDistance = cellCentricDataHolder.getCumulativeDistance();
        double duration = cellCentricDataHolder.getTrackDuration();
        double medianSpeed = cumDistance / duration;
        cellCentricDataHolder.setMedianSpeed(medianSpeed);
    }

    @Override
    public void computeEndPointDirectionalityRatio(CellCentricDataHolder cellCentricDataHolder) {
        double endPointDirectionalityRatio = cellCentricDataHolder.getEuclideanDistance() / cellCentricDataHolder.getCumulativeDistance();
        if (!Double.isNaN(endPointDirectionalityRatio)){
            cellCentricDataHolder.setEndPointDirectionalityRatio(endPointDirectionalityRatio);
        } else {
            cellCentricDataHolder.setEndPointDirectionalityRatio(0.0);
        }
        
    }

    @Override
    public void computeMedianDirectionalityRatio(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[] directionalityRatios = stepCentricDataHolder.getDirectionalityRatios();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(directionalityRatios);
        Double[] excludeNaNvalues = AnalysisUtils.excludeNaNvalues(excludeNullValues);
        if (excludeNaNvalues.length != 0) {
            double medianDirectionalityRatio = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNaNvalues));
            cellCentricDataHolder.setMedianDirectionalityRatio(medianDirectionalityRatio);
        }
    }

    @Override
    public void computeConvexHull(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Track track = stepCentricDataHolder.getTrack();
        ConvexHull convexHull = new ConvexHull();
        grahamScanAlgorithm.computeHull(track, convexHull);
        grahamScanAlgorithm.computeHullSize(convexHull);
        grahamScanAlgorithm.findMostDistantPoints(track, convexHull);
        convexHullOperator.computePerimeter(convexHull);
        convexHullOperator.computeArea(convexHull);
        convexHullOperator.computeAcircularity(convexHull);
        convexHullOperator.computeDirectionality(convexHull);
        cellCentricDataHolder.setConvexHull(convexHull);
    }

    @Override
    public void computeDisplacementRatio(CellCentricDataHolder cellCentricDataHolder) {
        ConvexHull convexHull = cellCentricDataHolder.getConvexHull();
        MostDistantPointsPair mostDistantPointsPair = convexHull.getMostDistantPointsPair();
        double maxSpan = mostDistantPointsPair.getMaxSpan();
        double displacementRatio = cellCentricDataHolder.getEuclideanDistance() / maxSpan;
        cellCentricDataHolder.setDisplacementRatio(displacementRatio);
    }

    @Override
    public void computeOutreachRatio(CellCentricDataHolder cellCentricDataHolder) {
        ConvexHull convexHull = cellCentricDataHolder.getConvexHull();
        MostDistantPointsPair mostDistantPointsPair = convexHull.getMostDistantPointsPair();
        double maxSpan = mostDistantPointsPair.getMaxSpan();
        double outreachRatio = maxSpan / cellCentricDataHolder.getCumulativeDistance();
        cellCentricDataHolder.setOutreachRatio(outreachRatio);
    }

    @Override
    public void computeMedianTurningAngle(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[] turningAngles = stepCentricDataHolder.getTurningAngles();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(turningAngles);
        Double[] excludeNaNvalues = AnalysisUtils.excludeNaNvalues(excludeNullValues);
        // simply compute the median of the turning angles
        double medianTurningAngle = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNaNvalues));
        cellCentricDataHolder.setMedianTurningAngle(medianTurningAngle);
    }

    @Override
    public void computeEntropies(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        int N = stepCentricDataHolder.getTrack().getTrackPointList().size();
        List<Double> entropies = new ArrayList<>();
        List<List<EnclosingBall>> xyEnclosingBalls = stepCentricDataHolder.getxYEnclosingBalls();

        for (List<EnclosingBall> list : xyEnclosingBalls) {
            int nBalls = list.size();
            int nPoints = 0;
            for (EnclosingBall enclosingBall : list) {
                nPoints += enclosingBall.getEnclosingPoints().size();
            }
            double entropy = (double) nBalls / (double) nPoints;
            entropy = entropy / (double) N;
            entropies.add(entropy);
        }

//        xyEnclosingBalls.stream().map((enclosingBalls) -> {
//            double sum = 0;
//            for (EnclosingBall enclosingBall : enclosingBalls) {
////                 sum += (double) enclosingBall.getEnclosingPoints().size()  * Math.log10((double) enclosingBall.getEnclosingPoints().size());
//                sum += ((double) enclosingBall.getEnclosingPoints().size() / (double) N) * Math.log10((double) enclosingBall.getEnclosingPoints().size() / (double) N);
//            }
//            return sum;
//        }).map((sum) -> -(1 / (double) N) * sum).forEach((entropy) -> {
//            entropies.add(entropy);
//        });
        cellCentricDataHolder.setEntropies(entropies);
    }

    @Override
    public void computeXYFD(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        List<List<EnclosingBall>> xyEnclosingBalls = stepCentricDataHolder.getxYEnclosingBalls();
        double r_min = PropertiesConfigurationHolder.getInstance().getDouble("r_min");
        double r_max = PropertiesConfigurationHolder.getInstance().getDouble("r_max");
        double r_step = PropertiesConfigurationHolder.getInstance().getDouble("r_step");
        int N = (int) ((r_max - r_min) / r_step) + 1;
        double[] balls = new double[N];
        double[] radii = new double[N];
        for (int i = 0; i < N; i++) {
            radii[i] = Math.log10(1 / (r_min + (i * r_step)));
            balls[i] = Math.log10(xyEnclosingBalls.get(i).size());
        }

        FractalDimension fractalDimension = new FractalDimension(radii, balls);
//        List<Double> linearModel = linearRegressor.estimateLinearModel(new double[][]{fractalDimension.getxValues(), fractalDimension.getyValues()});
//        fractalDimension.setFD(linearModel.get(0));

        XYSeries series = JFreeChartUtils.generateXYSeries(fractalDimension.getxValues(), fractalDimension.getyValues());
        XYSeriesCollection collection = new XYSeriesCollection(series);
        double regression[] = Regression.getOLSRegression(collection, 0);
        fractalDimension.setFD(regression[1]);
        cellCentricDataHolder.setxYFD(fractalDimension);
    }

    @Override
    public void computeXTFD(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        List<List<EnclosingBall>> xtEnclosingBalls = stepCentricDataHolder.getxTEnclosingBalls();
        double eps_min = PropertiesConfigurationHolder.getInstance().getDouble("eps_min");
        double eps_max = PropertiesConfigurationHolder.getInstance().getDouble("eps_max");
        double eps_step = PropertiesConfigurationHolder.getInstance().getDouble("eps_step");
        int N = (int) ((eps_max - eps_min) / eps_step) + 1;
        double[] balls = new double[N];
        double[] radii = new double[N];
        for (int i = 0; i < N; i++) {
            radii[i] = Math.log10(1 / (eps_min + (i * eps_step)));
            balls[i] = Math.log10(xtEnclosingBalls.get(i).size());
        }
        FractalDimension fractalDimension = new FractalDimension(radii, balls);
//        List<Double> linearModel = linearRegressor.estimateLinearModel(new double[][]{fractalDimension.getxValues(), fractalDimension.getyValues()});
//        fractalDimension.setFD(linearModel.get(0));

        XYSeries series = JFreeChartUtils.generateXYSeries(fractalDimension.getxValues(), fractalDimension.getyValues());
        XYSeriesCollection collection = new XYSeriesCollection(series);
        double regression[] = Regression.getOLSRegression(collection, 0);
        fractalDimension.setFD(regression[1]);
        cellCentricDataHolder.setxTFD(fractalDimension);
    }

    @Override
    public void computeYTFD(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        List<List<EnclosingBall>> ytEnclosingBalls = stepCentricDataHolder.getyTEnclosingBalls();
        double eps_min = PropertiesConfigurationHolder.getInstance().getDouble("eps_min");
        double eps_max = PropertiesConfigurationHolder.getInstance().getDouble("eps_max");
        double eps_step = PropertiesConfigurationHolder.getInstance().getDouble("eps_step");
        int N = (int) ((eps_max - eps_min) / eps_step) + 1;
        double[] balls = new double[N];
        double[] radii = new double[N];
        for (int i = 0; i < N; i++) {
            radii[i] = Math.log10(1 / (eps_min + (i * eps_step)));
            balls[i] = Math.log10(ytEnclosingBalls.get(i).size());
        }
        FractalDimension fractalDimension = new FractalDimension(radii, balls);
//        List<Double> linearModel = linearRegressor.estimateLinearModel(new double[][]{fractalDimension.getxValues(), fractalDimension.getyValues()});
//        fractalDimension.setFD(linearModel.get(0));

        XYSeries series = JFreeChartUtils.generateXYSeries(fractalDimension.getxValues(), fractalDimension.getyValues());
        XYSeriesCollection collection = new XYSeriesCollection(series);
        double regression[] = Regression.getOLSRegression(collection, 0);
        fractalDimension.setFD(regression[1]);
        cellCentricDataHolder.setyTFD(fractalDimension);

    }

    @Override
    public void computeMedianDirectionAutocorrelation(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        List<Double[]> directionAutocorrelationsList = stepCentricDataHolder.getDirectionAutocorrelations();
        Double[] directionAutocorrelations;
        if (directionAutocorrelationsList.size() > 1) {
            directionAutocorrelations = directionAutocorrelationsList.get(1);
        } else {
            directionAutocorrelations = directionAutocorrelationsList.get(0);
        }
        // simply compute the median of the direction autocorrelations
        double medianDirectionAutocorrelation = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(directionAutocorrelations));
        cellCentricDataHolder.setMedianDirectionAutocorrelation(medianDirectionAutocorrelation);
    }

}
