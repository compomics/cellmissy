/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.TrackInterpolator;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.MathArrays;
import org.springframework.stereotype.Component;

/**
 *
 * The x values passed to the interpolator must be ordered in ascending order.
 * It is not valid to evaluate the function for values outside the range x0..xN
 * -- will throw an OutOfRangeException.
 *
 * @author Paola
 */
@Component("trackSplineInterpolator")
public class TrackSplineInterpolator implements TrackInterpolator {

    @Override
    public void interpolateTrack(CellCentricDataHolder cellCentricDataHolder, StepCentricDataHolder stepCentricDataHolder, int interpolationPoints) {
        // make sure we interpolate in the range of the x value
        double xMin = cellCentricDataHolder.getxMin();
        double xMax = cellCentricDataHolder.getxMax();
        double[] interpolationX = new double[interpolationPoints];
        double[] interpolatedY = new double[interpolationPoints];
        double interpolationStep = (xMax - xMin) / interpolationPoints;
        // create a new spline interpolator
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        Double[][] coordinatesMatrix = stepCentricDataHolder.getCoordinatesMatrix();
        // get the x and the y coordinates
        double[] xCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[0]));
        double[] yCoord = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.transpose2DArray(coordinatesMatrix)[1]));
        // [x] needs to be monotonic
//        MathArrays.sortInPlace(xCoord, yCoord);
        // now check for STRICT monocitity
        boolean monotonic = MathArrays.isMonotonic(xCoord, MathArrays.OrderDirection.INCREASING, true);
        if (!monotonic) {
            MathArrays.sortInPlace(xCoord, yCoord);
            // make monotic
            makeXStrictlyMonotonic(xCoord);
        }
        // call the interpolator
//        PolynomialSplineFunction function = splineInterpolator.interpolate(xCoord, yCoord);
//        for (int i = 0; i < interpolationPoints; i++) {
//            interpolationX[i] = xMin + (i * interpolationStep);
//            interpolatedY[i] = function.value(interpolationX[i]);
//        }
//        cellCentricDataHolder.setInterpolationX(interpolationX);
//        cellCentricDataHolder.setInterpolatedY(interpolatedY);
    }

    /**
     * Given an array of double, make it strictly monotonic.
     *
     * @param xArray
     * @return
     */
    private void makeXStrictlyMonotonic(double[] xArray) {
        // 
        Map<Double, Integer> map = new LinkedHashMap<>();
        double delta = .1;
        int counter = 1;

        double previous = xArray[0];

        final int max = xArray.length;
        for (int i = 1; i < max; i++) {
            double current = xArray[i];
            if (previous < current) {
                // reset the counter
                counter = 1;
//                xArray[i - 1] = previous;
            } else if (previous == current) {
//                xArray[i - 1] = current - delta;
                counter++;
            }
            map.put(current, counter);
            previous = xArray[i];
        }
        Set<Double> keySet = map.keySet();
        List<Double> list = new ArrayList<>();
        for (double d : keySet) {
            int count = map.get(d);
            for (int i = 0; i < count; i++) {
                double increment = delta / count;
                list.add(d + (i) * increment);
            }
        }

        Double[] array = list.stream().toArray(Double[]::new);
        xArray = Arrays.stream(array).mapToDouble(Double::doubleValue).toArray();
    }
}
