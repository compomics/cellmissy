/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.doseresponse;

import be.ugent.maf.cellmissy.analysis.doseresponse.impl.DoseResponseLMOptimizer;
import be.ugent.maf.cellmissy.analysis.doseresponse.impl.OptimumImpl;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import java.util.Collection;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

/**
 * Extension to return OptimumImp to acquire covariances for statistics
 * calculation.
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
    public OptimumImpl performRegression(Collection<WeightedObservedPoint> points) {
        // Perform the fit.
        return getOptimizer().optimize(getProblem(points));
    }

    /**
     * Creates an optimizer set up to fit the appropriate curve.
     *
     * It is essentialy a Levenberg-Marquardt optimizer, the only edit is that
     * the fit returns an OptimumImp instead of just Optimum. This to acquire
     * the parameter covariances vor statustics calculation.
     *
     * @return the optimizer to use for fitting the curve to the given
     * datapoints.
     */
    @Override
    protected DoseResponseLMOptimizer getOptimizer() {
        return new DoseResponseLMOptimizer();
    }
}
