/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.doseresponse.impl;

import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.Precision;

/**
 * The existing apache math class is extended to save each parameter estimation,
 * in order to obtain a distribution of these parameters. This permits
 * calculation of standard errors and confidence intervals.
 *
 *
 * @author Gwendolien
 */
public class DoseResponseLMOptimizer extends LevenbergMarquardtOptimizer {

    // edit extisting .optimize code to save parameter values in (separate) collection
    // just before end of optimize: call method that saves collection in resultsHolder
    public DoseResponseLMOptimizer() {
        super();
    }

    /**
     * Twice the "epsilon machine".
     */
    private static final double TWO_EPS = 2 * Precision.EPSILON;

    /**
     * Optimizes the problem to obtain parameter estimates. Difference with
     * inherited method: for each estimated parameter, a collection is created
     * where every iteration's estimates are added to.
     *
     * @param problem Contains datapoints, function and parameters to fit.
     * @param resultsHolder Holds the results of the fitting
     * @return
     */
    public OptimumImpl optimize(final LeastSquaresProblem problem, SigmoidFittingResultsHolder resultsHolder) {

        // Empty collection for parameter distributions
        HashMap<Integer, ArrayList<Double>> parameterDistr = new HashMap<>();
        // Pull in relevant data from the problem as locals.
        final int nR = problem.getObservationSize(); // Number of observed data.
        final int nC = problem.getParameterSize(); // Number of parameters.
        // Counters.
        final Incrementor iterationCounter = problem.getIterationCounter();
        final Incrementor evaluationCounter = problem.getEvaluationCounter();
        // Convergence criterion.
        final ConvergenceChecker<Evaluation> checker = problem.getConvergenceChecker();

        // arrays shared with the other private methods
        final int solvedCols = FastMath.min(nR, nC);
        /* Parameters evolution direction associated with lmPar. */
        double[] lmDir = new double[nC];
        /* Levenberg-Marquardt parameter. */
        double lmPar = 0;

        // local point
        double delta = 0;
        double xNorm = 0;
        double[] diag = new double[nC];
        double[] oldX = new double[nC];
        double[] oldRes = new double[nR];
        double[] qtf = new double[nR];
        double[] work1 = new double[nC];
        double[] work2 = new double[nC];
        double[] work3 = new double[nC];

        // Evaluate the function at the starting point and calculate its norm.
        evaluationCounter.incrementCount();
        //value will be reassigned in the loop
        Evaluation current = problem.evaluate(problem.getStart());
        double[] currentResiduals = current.getResiduals().toArray();
        double currentCost = current.getCost();
        double[] currentPoint = current.getPoint().toArray();
        // Outer loop.
        boolean firstIteration = true;
        while (true) {
            iterationCounter.incrementCount();

            final Evaluation previous = current;

            // QR decomposition of the jacobian matrix
            final InternalData internalData
                    = qrDecomposition(current.getJacobian(), solvedCols);
            final double[][] weightedJacobian = internalData.weightedJacobian;
            final int[] permutation = internalData.permutation;
            final double[] diagR = internalData.diagR;
            final double[] jacNorm = internalData.jacNorm;

            //residuals already have weights applied
            double[] weightedResidual = currentResiduals;
            for (int i = 0; i < nR; i++) {
                qtf[i] = weightedResidual[i];
            }

            // compute Qt.res
            qTy(qtf, internalData);

            // now we don't need Q anymore,
            // so let jacobian contain the R matrix with its diagonal elements
            for (int k = 0; k < solvedCols; ++k) {
                int pk = permutation[k];
                weightedJacobian[k][pk] = diagR[pk];
            }

            if (firstIteration) {
                // scale the point according to the norms of the columns
                // of the initial jacobian
                xNorm = 0;
                for (int k = 0; k < nC; ++k) {
                    double dk = jacNorm[k];
                    if (dk == 0) {
                        dk = 1.0;
                    }
                    double xk = dk * currentPoint[k];
                    xNorm += xk * xk;
                    diag[k] = dk;
                }
                xNorm = FastMath.sqrt(xNorm);

                // initialize the step bound delta
                delta = (xNorm == 0) ? getInitialStepBoundFactor() : (getInitialStepBoundFactor() * xNorm);
            }

            // check orthogonality between function vector and jacobian columns
            double maxCosine = 0;
            if (currentCost != 0) {
                for (int j = 0; j < solvedCols; ++j) {
                    int pj = permutation[j];
                    double s = jacNorm[pj];
                    if (s != 0) {
                        double sum = 0;
                        for (int i = 0; i <= j; ++i) {
                            sum += weightedJacobian[i][pj] * qtf[i];
                        }
                        maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * currentCost));
                    }
                }
            }
            if (maxCosine <= getOrthoTolerance()) {
                // Convergence has been reached.
                return new OptimumImpl(
                        current,
                        evaluationCounter.getCount(),
                        iterationCounter.getCount(), parameterDistr);
            }

            // rescale if necessary
            for (int j = 0; j < nC; ++j) {
                diag[j] = FastMath.max(diag[j], jacNorm[j]);
            }

            // Inner loop.
            for (double ratio = 0; ratio < 1.0e-4;) {

                // save the state
                for (int j = 0; j < solvedCols; ++j) {
                    int pj = permutation[j];
                    oldX[pj] = currentPoint[pj];
                }
                final double previousCost = currentCost;
                double[] tmpVec = weightedResidual;
                weightedResidual = oldRes;
                oldRes = tmpVec;

                // determine the Levenberg-Marquardt parameter
                lmPar = determineLMParameter(qtf, delta, diag,
                        internalData, solvedCols,
                        work1, work2, work3, lmDir, lmPar);

                // compute the new point and the norm of the evolution direction
                double lmNorm = 0;
                for (int j = 0; j < solvedCols; ++j) {
                    int pj = permutation[j];
                    lmDir[pj] = -lmDir[pj];
                    currentPoint[pj] = oldX[pj] + lmDir[pj];
                    double s = diag[pj] * lmDir[pj];
                    lmNorm += s * s;
                }
                lmNorm = FastMath.sqrt(lmNorm);
                // on the first iteration, adjust the initial step bound.
                if (firstIteration) {
                    delta = FastMath.min(delta, lmNorm);
                }

                // Evaluate the function at x + p and calculate its norm.
                evaluationCounter.incrementCount();
                current = problem.evaluate(new ArrayRealVector(currentPoint));
                currentResiduals = current.getResiduals().toArray();
                currentCost = current.getCost();
                currentPoint = current.getPoint().toArray();
                //put new parameter estimates in collection
                for (int i = 0; i < currentPoint.length; i++) {
                    if (parameterDistr.get(i) != null) {
                        parameterDistr.get(i).add(currentPoint[i]);
                    } else {
                        parameterDistr.put(i, new ArrayList<Double>());
                        parameterDistr.get(i).add(currentPoint[i]);
                    }
                    
                }

                // compute the scaled actual reduction
                double actRed = -1.0;
                if (0.1 * currentCost < previousCost) {
                    double r = currentCost / previousCost;
                    actRed = 1.0 - r * r;
                }

                // compute the scaled predicted reduction
                // and the scaled directional derivative
                for (int j = 0; j < solvedCols; ++j) {
                    int pj = permutation[j];
                    double dirJ = lmDir[pj];
                    work1[j] = 0;
                    for (int i = 0; i <= j; ++i) {
                        work1[i] += weightedJacobian[i][pj] * dirJ;
                    }
                }
                double coeff1 = 0;
                for (int j = 0; j < solvedCols; ++j) {
                    coeff1 += work1[j] * work1[j];
                }
                double pc2 = previousCost * previousCost;
                coeff1 /= pc2;
                double coeff2 = lmPar * lmNorm * lmNorm / pc2;
                double preRed = coeff1 + 2 * coeff2;
                double dirDer = -(coeff1 + coeff2);

                // ratio of the actual to the predicted reduction
                ratio = (preRed == 0) ? 0 : (actRed / preRed);

                // update the step bound
                if (ratio <= 0.25) {
                    double tmp
                            = (actRed < 0) ? (0.5 * dirDer / (dirDer + 0.5 * actRed)) : 0.5;
                    if ((0.1 * currentCost >= previousCost) || (tmp < 0.1)) {
                        tmp = 0.1;
                    }
                    delta = tmp * FastMath.min(delta, 10.0 * lmNorm);
                    lmPar /= tmp;
                } else if ((lmPar == 0) || (ratio >= 0.75)) {
                    delta = 2 * lmNorm;
                    lmPar *= 0.5;
                }

                // test for successful iteration.
                if (ratio >= 1.0e-4) {
                    // successful iteration, update the norm
                    firstIteration = false;
                    xNorm = 0;
                    for (int k = 0; k < nC; ++k) {
                        double xK = diag[k] * currentPoint[k];
                        xNorm += xK * xK;
                    }
                    xNorm = FastMath.sqrt(xNorm);

                    // tests for convergence.
                    if (checker != null && checker.converged(iterationCounter.getCount(), previous, current)) {
                        return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount(),parameterDistr);
                    }
                } else {
                    // failed iteration, reset the previous values
                    currentCost = previousCost;
                    for (int j = 0; j < solvedCols; ++j) {
                        int pj = permutation[j];
                        currentPoint[pj] = oldX[pj];
                    }
                    tmpVec = weightedResidual;
                    weightedResidual = oldRes;
                    oldRes = tmpVec;
                    // Reset "current" to previous values.
                    current = previous;
                }

                // Default convergence criteria.
                if ((FastMath.abs(actRed) <= getCostRelativeTolerance()
                        && preRed <= getCostRelativeTolerance()
                        && ratio <= 2.0)
                        || delta <= getParameterRelativeTolerance() * xNorm) {
                    return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount(),parameterDistr);
                }

                // tests for termination and stringent tolerances
                if (FastMath.abs(actRed) <= TWO_EPS
                        && preRed <= TWO_EPS
                        && ratio <= 2.0) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE,
                            getCostRelativeTolerance());
                } else if (delta <= TWO_EPS * xNorm) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE,
                            getParameterRelativeTolerance());
                } else if (maxCosine <= TWO_EPS) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE,
                            getOrthoTolerance());
                }
            }
        }
    }

    /**
     * Copy of super source code. Decompose a matrix A as A.P = Q.R using
     * Householder transforms.
     * <p>
     * As suggested in the P. Lascaux and R. Theodor book
     * <i>Analyse num&eacute;rique matricielle appliqu&eacute;e &agrave; l'art
     * de l'ing&eacute;nieur</i> (Masson, 1986), instead of representing the
     * Householder transforms with u<sub>k</sub> unit vectors such that:
     * <pre>
     * H<sub>k</sub> = I - 2u<sub>k</sub>.u<sub>k</sub><sup>t</sup>
     * </pre> we use <sub>k</sub> non-unit vectors such that:
     * <pre>
     * H<sub>k</sub> = I - beta<sub>k</sub>v<sub>k</sub>.v<sub>k</sub><sup>t</sup>
     * </pre> where v<sub>k</sub> = a<sub>k</sub> - alpha<sub>k</sub>
     * e<sub>k</sub>. The beta<sub>k</sub> coefficients are provided upon exit
     * as recomputing them from the v<sub>k</sub> vectors would be costly.</p>
     * <p>
     * This decomposition handles rank deficient cases since the tranformations
     * are performed in non-increasing columns norms order thanks to columns
     * pivoting. The diagonal elements of the R matrix are therefore also in
     * non-increasing absolute values order.</p>
     *
     * @param jacobian Weighted Jacobian matrix at the current point.
     * @param solvedCols Number of solved point.
     * @return data used in other methods of this class.
     * @throws ConvergenceException if the decomposition cannot be performed.
     */
    private InternalData qrDecomposition(RealMatrix jacobian,
            int solvedCols) throws ConvergenceException {
        // Code in this class assumes that the weighted Jacobian is -(W^(1/2) J),
        // hence the multiplication by -1.
        final double[][] weightedJacobian = jacobian.scalarMultiply(-1).getData();

        final int nR = weightedJacobian.length;
        final int nC = weightedJacobian[0].length;

        final int[] permutation = new int[nC];
        final double[] diagR = new double[nC];
        final double[] jacNorm = new double[nC];
        final double[] beta = new double[nC];

        // initializations
        for (int k = 0; k < nC; ++k) {
            permutation[k] = k;
            double norm2 = 0;
            for (int i = 0; i < nR; ++i) {
                double akk = weightedJacobian[i][k];
                norm2 += akk * akk;
            }
            jacNorm[k] = FastMath.sqrt(norm2);
        }

        // transform the matrix column after column
        for (int k = 0; k < nC; ++k) {

            // select the column with the greatest norm on active components
            int nextColumn = -1;
            double ak2 = Double.NEGATIVE_INFINITY;
            for (int i = k; i < nC; ++i) {
                double norm2 = 0;
                for (int j = k; j < nR; ++j) {
                    double aki = weightedJacobian[j][permutation[i]];
                    norm2 += aki * aki;
                }
                if (Double.isInfinite(norm2) || Double.isNaN(norm2)) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, nR, nC);
                }
                if (norm2 > ak2) {
                    nextColumn = i;
                    ak2 = norm2;
                }
            }
            if (ak2 <= getRankingThreshold()) {
                return new InternalData(weightedJacobian, permutation, k, diagR, jacNorm, beta);
            }
            int pk = permutation[nextColumn];
            permutation[nextColumn] = permutation[k];
            permutation[k] = pk;

            // choose alpha such that Hk.u = alpha ek
            double akk = weightedJacobian[k][pk];
            double alpha = (akk > 0) ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
            double betak = 1.0 / (ak2 - akk * alpha);
            beta[pk] = betak;

            // transform the current column
            diagR[pk] = alpha;
            weightedJacobian[k][pk] -= alpha;

            // transform the remaining columns
            for (int dk = nC - 1 - k; dk > 0; --dk) {
                double gamma = 0;
                for (int j = k; j < nR; ++j) {
                    gamma += weightedJacobian[j][pk] * weightedJacobian[j][permutation[k + dk]];
                }
                gamma *= betak;
                for (int j = k; j < nR; ++j) {
                    weightedJacobian[j][permutation[k + dk]] -= gamma * weightedJacobian[j][pk];
                }
            }
        }

        return new InternalData(weightedJacobian, permutation, solvedCols, diagR, jacNorm, beta);
    }

    /**
     * Copy of super source code. Because of inaccessible private internal class
     * InternalData in super class, method is copied over. Compute the product
     * Qt.y for some Q.R. decomposition.
     *
     * @param y vector to multiply (will be overwritten with the result)
     * @param internalData Data.
     */
    private void qTy(double[] y,
            InternalData internalData) {
        final double[][] weightedJacobian = internalData.weightedJacobian;
        final int[] permutation = internalData.permutation;
        final double[] beta = internalData.beta;

        final int nR = weightedJacobian.length;
        final int nC = weightedJacobian[0].length;

        for (int k = 0; k < nC; ++k) {
            int pk = permutation[k];
            double gamma = 0;
            for (int i = k; i < nR; ++i) {
                gamma += weightedJacobian[i][pk] * y[i];
            }
            gamma *= beta[pk];
            for (int i = k; i < nR; ++i) {
                y[i] -= gamma * weightedJacobian[i][pk];
            }
        }
    }

    /**
     * Copy of super source code, because of inaccessible super InternalData
     * class. Determines the Levenberg-Marquardt parameter.
     *
     * <p>
     * This implementation is a translation in Java of the MINPACK
     * <a href="http://www.netlib.org/minpack/lmpar.f">lmpar</a>
     * routine.</p>
     * <p>
     * This method sets the lmPar and lmDir attributes.</p>
     * <p>
     * The authors of the original fortran function are:</p>
     * <ul>
     * <li>Argonne National Laboratory. MINPACK project. March 1980</li>
     * <li>Burton S. Garbow</li>
     * <li>Kenneth E. Hillstrom</li>
     * <li>Jorge J. More</li>
     * </ul>
     * <p>
     * Luc Maisonobe did the Java translation.</p>
     *
     * @param qy Array containing qTy.
     * @param delta Upper bound on the euclidean norm of diagR * lmDir.
     * @param diag Diagonal matrix.
     * @param internalData Data (modified in-place in this method).
     * @param solvedCols Number of solved point.
     * @param work1 work array
     * @param work2 work array
     * @param work3 work array
     * @param lmDir the "returned" LM direction will be stored in this array.
     * @param lmPar the value of the LM parameter from the previous iteration.
     * @return the new LM parameter
     */
    private double determineLMParameter(double[] qy, double delta, double[] diag,
            InternalData internalData, int solvedCols,
            double[] work1, double[] work2, double[] work3,
            double[] lmDir, double lmPar) {
        final double[][] weightedJacobian = internalData.weightedJacobian;
        final int[] permutation = internalData.permutation;
        final int rank = internalData.rank;
        final double[] diagR = internalData.diagR;

        final int nC = weightedJacobian[0].length;

        // compute and store in x the gauss-newton direction, if the
        // jacobian is rank-deficient, obtain a least squares solution
        for (int j = 0; j < rank; ++j) {
            lmDir[permutation[j]] = qy[j];
        }
        for (int j = rank; j < nC; ++j) {
            lmDir[permutation[j]] = 0;
        }
        for (int k = rank - 1; k >= 0; --k) {
            int pk = permutation[k];
            double ypk = lmDir[pk] / diagR[pk];
            for (int i = 0; i < k; ++i) {
                lmDir[permutation[i]] -= ypk * weightedJacobian[i][pk];
            }
            lmDir[pk] = ypk;
        }

        // evaluate the function at the origin, and test
        // for acceptance of the Gauss-Newton direction
        double dxNorm = 0;
        for (int j = 0; j < solvedCols; ++j) {
            int pj = permutation[j];
            double s = diag[pj] * lmDir[pj];
            work1[pj] = s;
            dxNorm += s * s;
        }
        dxNorm = FastMath.sqrt(dxNorm);
        double fp = dxNorm - delta;
        if (fp <= 0.1 * delta) {
            lmPar = 0;
            return lmPar;
        }

        // if the jacobian is not rank deficient, the Newton step provides
        // a lower bound, parl, for the zero of the function,
        // otherwise set this bound to zero
        double sum2;
        double parl = 0;
        if (rank == solvedCols) {
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                work1[pj] *= diag[pj] / dxNorm;
            }
            sum2 = 0;
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                double sum = 0;
                for (int i = 0; i < j; ++i) {
                    sum += weightedJacobian[i][pj] * work1[permutation[i]];
                }
                double s = (work1[pj] - sum) / diagR[pj];
                work1[pj] = s;
                sum2 += s * s;
            }
            parl = fp / (delta * sum2);
        }

        // calculate an upper bound, paru, for the zero of the function
        sum2 = 0;
        for (int j = 0; j < solvedCols; ++j) {
            int pj = permutation[j];
            double sum = 0;
            for (int i = 0; i <= j; ++i) {
                sum += weightedJacobian[i][pj] * qy[i];
            }
            sum /= diag[pj];
            sum2 += sum * sum;
        }
        double gNorm = FastMath.sqrt(sum2);
        double paru = gNorm / delta;
        if (paru == 0) {
            paru = Precision.SAFE_MIN / FastMath.min(delta, 0.1);
        }

        // if the input par lies outside of the interval (parl,paru),
        // set par to the closer endpoint
        lmPar = FastMath.min(paru, FastMath.max(lmPar, parl));
        if (lmPar == 0) {
            lmPar = gNorm / dxNorm;
        }

        for (int countdown = 10; countdown >= 0; --countdown) {

            // evaluate the function at the current value of lmPar
            if (lmPar == 0) {
                lmPar = FastMath.max(Precision.SAFE_MIN, 0.001 * paru);
            }
            double sPar = FastMath.sqrt(lmPar);
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                work1[pj] = sPar * diag[pj];
            }
            determineLMDirection(qy, work1, work2, internalData, solvedCols, work3, lmDir);

            dxNorm = 0;
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                double s = diag[pj] * lmDir[pj];
                work3[pj] = s;
                dxNorm += s * s;
            }
            dxNorm = FastMath.sqrt(dxNorm);
            double previousFP = fp;
            fp = dxNorm - delta;

            // if the function is small enough, accept the current value
            // of lmPar, also test for the exceptional cases where parl is zero
            if (FastMath.abs(fp) <= 0.1 * delta
                    || (parl == 0
                    && fp <= previousFP
                    && previousFP < 0)) {
                return lmPar;
            }

            // compute the Newton correction
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                work1[pj] = work3[pj] * diag[pj] / dxNorm;
            }
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                work1[pj] /= work2[j];
                double tmp = work1[pj];
                for (int i = j + 1; i < solvedCols; ++i) {
                    work1[permutation[i]] -= weightedJacobian[i][pj] * tmp;
                }
            }
            sum2 = 0;
            for (int j = 0; j < solvedCols; ++j) {
                double s = work1[permutation[j]];
                sum2 += s * s;
            }
            double correction = fp / (delta * sum2);

            // depending on the sign of the function, update parl or paru.
            if (fp > 0) {
                parl = FastMath.max(parl, lmPar);
            } else if (fp < 0) {
                paru = FastMath.min(paru, lmPar);
            }

            // compute an improved estimate for lmPar
            lmPar = FastMath.max(parl, lmPar + correction);
        }

        return lmPar;
    }

    /**
     * Copy of super source code, because of inaccessible InternalData class in
     * super. Solve a*x = b and d*x = 0 in the least squares sense.
     * <p>
     * This implementation is a translation in Java of the MINPACK
     * <a href="http://www.netlib.org/minpack/qrsolv.f">qrsolv</a>
     * routine.</p>
     * <p>
     * This method sets the lmDir and lmDiag attributes.</p>
     * <p>
     * The authors of the original fortran function are:</p>
     * <ul>
     * <li>Argonne National Laboratory. MINPACK project. March 1980</li>
     * <li>Burton S. Garbow</li>
     * <li>Kenneth E. Hillstrom</li>
     * <li>Jorge J. More</li>
     * </ul>
     * <p>
     * Luc Maisonobe did the Java translation.</p>
     *
     * @param qy array containing qTy
     * @param diag diagonal matrix
     * @param lmDiag diagonal elements associated with lmDir
     * @param internalData Data (modified in-place in this method).
     * @param solvedCols Number of sloved point.
     * @param work work array
     * @param lmDir the "returned" LM direction is stored in this array
     */
    private void determineLMDirection(double[] qy, double[] diag,
            double[] lmDiag,
            InternalData internalData,
            int solvedCols,
            double[] work,
            double[] lmDir) {
        final int[] permutation = internalData.permutation;
        final double[][] weightedJacobian = internalData.weightedJacobian;
        final double[] diagR = internalData.diagR;

        // copy R and Qty to preserve input and initialize s
        //  in particular, save the diagonal elements of R in lmDir
        for (int j = 0; j < solvedCols; ++j) {
            int pj = permutation[j];
            for (int i = j + 1; i < solvedCols; ++i) {
                weightedJacobian[i][pj] = weightedJacobian[j][permutation[i]];
            }
            lmDir[j] = diagR[pj];
            work[j] = qy[j];
        }

        // eliminate the diagonal matrix d using a Givens rotation
        for (int j = 0; j < solvedCols; ++j) {

            // prepare the row of d to be eliminated, locating the
            // diagonal element using p from the Q.R. factorization
            int pj = permutation[j];
            double dpj = diag[pj];
            if (dpj != 0) {
                Arrays.fill(lmDiag, j + 1, lmDiag.length, 0);
            }
            lmDiag[j] = dpj;

            //  the transformations to eliminate the row of d
            // modify only a single element of Qty
            // beyond the first n, which is initially zero.
            double qtbpj = 0;
            for (int k = j; k < solvedCols; ++k) {
                int pk = permutation[k];

                // determine a Givens rotation which eliminates the
                // appropriate element in the current row of d
                if (lmDiag[k] != 0) {

                    final double sin;
                    final double cos;
                    double rkk = weightedJacobian[k][pk];
                    if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
                        final double cotan = rkk / lmDiag[k];
                        sin = 1.0 / FastMath.sqrt(1.0 + cotan * cotan);
                        cos = sin * cotan;
                    } else {
                        final double tan = lmDiag[k] / rkk;
                        cos = 1.0 / FastMath.sqrt(1.0 + tan * tan);
                        sin = cos * tan;
                    }

                    // compute the modified diagonal element of R and
                    // the modified element of (Qty,0)
                    weightedJacobian[k][pk] = cos * rkk + sin * lmDiag[k];
                    final double temp = cos * work[k] + sin * qtbpj;
                    qtbpj = -sin * work[k] + cos * qtbpj;
                    work[k] = temp;

                    // accumulate the tranformation in the row of s
                    for (int i = k + 1; i < solvedCols; ++i) {
                        double rik = weightedJacobian[i][pk];
                        final double temp2 = cos * rik + sin * lmDiag[i];
                        lmDiag[i] = -sin * rik + cos * lmDiag[i];
                        weightedJacobian[i][pk] = temp2;
                    }
                }
            }

            // store the diagonal element of s and restore
            // the corresponding diagonal element of R
            lmDiag[j] = weightedJacobian[j][permutation[j]];
            weightedJacobian[j][permutation[j]] = lmDir[j];
        }

        // solve the triangular system for z, if the system is
        // singular, then obtain a least squares solution
        int nSing = solvedCols;
        for (int j = 0; j < solvedCols; ++j) {
            if ((lmDiag[j] == 0) && (nSing == solvedCols)) {
                nSing = j;
            }
            if (nSing < solvedCols) {
                work[j] = 0;
            }
        }
        if (nSing > 0) {
            for (int j = nSing - 1; j >= 0; --j) {
                int pj = permutation[j];
                double sum = 0;
                for (int i = j + 1; i < nSing; ++i) {
                    sum += weightedJacobian[i][pj] * work[i];
                }
                work[j] = (work[j] - sum) / lmDiag[j];
            }
        }

        // permute the components of z back to components of lmDir
        for (int j = 0; j < lmDir.length; ++j) {
            lmDir[permutation[j]] = work[j];
        }
    }

    /**
     * Copy of super source code. Holds internal data. This structure was
     * created so that all optimizer fields can be "final". Code should be
     * further refactored in order to not pass around arguments that will
     * modified in-place (cf. "work" arrays).
     */
    private static class InternalData {

        /**
         * Weighted Jacobian.
         */
        private final double[][] weightedJacobian;
        /**
         * Columns permutation array.
         */
        private final int[] permutation;
        /**
         * Rank of the Jacobian matrix.
         */
        private final int rank;
        /**
         * Diagonal elements of the R matrix in the QR decomposition.
         */
        private final double[] diagR;
        /**
         * Norms of the columns of the jacobian matrix.
         */
        private final double[] jacNorm;
        /**
         * Coefficients of the Householder transforms vectors.
         */
        private final double[] beta;

        /**
         * @param weightedJacobian Weighted Jacobian.
         * @param permutation Columns permutation array.
         * @param rank Rank of the Jacobian matrix.
         * @param diagR Diagonal elements of the R matrix in the QR
         * decomposition.
         * @param jacNorm Norms of the columns of the jacobian matrix.
         * @param beta Coefficients of the Householder transforms vectors.
         */
        InternalData(double[][] weightedJacobian,
                int[] permutation,
                int rank,
                double[] diagR,
                double[] jacNorm,
                double[] beta) {
            this.weightedJacobian = weightedJacobian;
            this.permutation = permutation;
            this.rank = rank;
            this.diagR = diagR;
            this.jacNorm = jacNorm;
            this.beta = beta;
        }
    }

}
