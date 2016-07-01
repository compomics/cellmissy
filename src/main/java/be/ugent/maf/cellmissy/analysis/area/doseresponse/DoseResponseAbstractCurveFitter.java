/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.analysis.area.impl.doseresponse.DoseResponseLMOptimizer;
import be.ugent.maf.cellmissy.analysis.area.impl.doseresponse.OptimumImpl;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import java.util.Collection;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;

/**
 * Extends to include extra argument in method calls.
 *
 * @author Gwendolien
 */
public abstract class DoseResponseAbstractCurveFitter extends AbstractCurveFitter {

    public DoseResponseAbstractCurveFitter() {
        super();
    }

    /**
     * Fits a curve. This method computes the best evaluation of the data
     * problem, among which are the coefficients of the curve that best fit the
     * sample of observed points.
     *
     * @param points Observations.
     * @return the fitted parameters.
     */
    public OptimumImpl fit(Collection<WeightedObservedPoint> points, SigmoidFittingResultsHolder resultsHolder) {
        // Perform the fit.
        return getOptimizer().optimize(getProblem(points), resultsHolder);
    }

    /**
     * Creates an optimizer set up to fit the appropriate curve.
     *
     * The default implementation uses a edited Levenberg-Marquardt optimizer.
     *
     * @return the optimizer to use for fitting the curve to the given
     * datapoints.
     */
    @Override
    protected DoseResponseLMOptimizer getOptimizer() {
        return new DoseResponseLMOptimizer();
    }
}
