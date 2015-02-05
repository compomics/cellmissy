/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.CellCentricOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.ConvexHullOperator;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.MostDistantPointsPair;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
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

    @Override
    public void computeTrackDuration(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        double[] timeIndexes = stepCentricDataHolder.getTimeIndexes();
        Double timeLapse = stepCentricDataHolder.getTimeLapse();
        int numberOfPoints = timeIndexes.length;
        double duration = (numberOfPoints - 1) * timeLapse;
        cellCentricDataHolder.setTrackDuration(duration);
    }

    @Override
    public void computeCoordinatesRange(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        Double[][] transposedCoordinatesMatrix = AnalysisUtils.transpose2DArray(coordinatesMatrix);
        Double[] xCoordinates = transposedCoordinatesMatrix[0];
        Double[] yCoordinates = transposedCoordinatesMatrix[1];
        List<Double> xCoordAsList = Arrays.asList(xCoordinates);
        List<Double> yCoordAsList = Arrays.asList(yCoordinates);
        Double xMin = Collections.min(xCoordAsList);
        Double xMax = Collections.max(xCoordAsList);
        Double yMin = Collections.min(yCoordAsList);
        Double yMax = Collections.max(yCoordAsList);
        cellCentricDataHolder.setxMin(xMin);
        cellCentricDataHolder.setxMax(xMax);
        cellCentricDataHolder.setyMin(yMin);
        cellCentricDataHolder.setyMax(yMax);
        cellCentricDataHolder.setxNetDisplacement(xMax - xMin);
        cellCentricDataHolder.setyNetDisplacement(yMax - yMin);
    }

    @Override
    public void computeMedianDisplacement(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[] instantaneousDisplacements = stepCentricDataHolder.getInstantaneousDisplacements();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(instantaneousDisplacements);
        double medianDisplacement = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(excludeNullValues));
        cellCentricDataHolder.setMedianDisplacement(medianDisplacement);
    }

    @Override
    public void computeCumulativeDistance(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        double cumulativeDistance = 0;
        Double[] instantaneousDisplacements = stepCentricDataHolder.getInstantaneousDisplacements();
        for (int i = 0; i < instantaneousDisplacements.length; i++) {
            if (instantaneousDisplacements[i] != null) {
                cumulativeDistance += instantaneousDisplacements[i];
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
        double medianDisplacement = cellCentricDataHolder.getMedianDisplacement();
        double duration = cellCentricDataHolder.getTrackDuration();
        double medianSpeed = medianDisplacement / duration;
        cellCentricDataHolder.setMedianSpeed(medianSpeed);
    }

    @Override
    public void computeEndPointDirectionalityRatio(CellCentricDataHolder cellCentricDataHolder) {
        double endPointDirectionalityRatio = cellCentricDataHolder.getEuclideanDistance() / cellCentricDataHolder.getCumulativeDistance();
        cellCentricDataHolder.setEndPointDirectionalityRatio(endPointDirectionalityRatio);
    }

    @Override
    public void computeMedianDirectionalityRatio(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        Double[] directionalityRatios = stepCentricDataHolder.getDirectionalityRatios();
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(directionalityRatios);
        double medianDirectionalityRatio = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(excludeNullValues));
        cellCentricDataHolder.setMedianDirectionalityRatio(medianDirectionalityRatio);
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
        // simply compute the median of the turning angles
        double medianTurningAngle = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(excludeNullValues));
        cellCentricDataHolder.setMedianTurningAngle(medianTurningAngle);
    }

    @Override
    public void computeMedianDirectionAutocorrelation(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        List<Double[]> directionAutocorrelationsList = stepCentricDataHolder.getDirectionAutocorrelations();
        Double[] directionAutocorrelations = directionAutocorrelationsList.get(1);
        // simply compute the median of the direction autocorrelations
        double medianDirectionAutocorrelation = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(directionAutocorrelations));
        cellCentricDataHolder.setMedianDirectionAutocorrelation(medianDirectionAutocorrelation);
    }
}
