/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.impl.doseresponse;

import be.ugent.maf.cellmissy.analysis.area.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.math3.optimization.fitting.CurveFitter;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.springframework.stereotype.Component;

/**
 *
 * @author Gwendolien
 */
@Component("sigmoidFitter")
public class SigmoidFitterImpl implements SigmoidFitter {

    //implementation of interface method
    @Override
    public void fitNoConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder) {

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, top, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double bottom = parameters[0];
                double top = parameters[1];
                double logEC50 = parameters[2];
                double hillslope = parameters[3];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double bottom = parameters[0];
                double top = parameters[1];
                double logEC50 = parameters[2];
                double hillslope = parameters[3];

                return new double[]{
                    1 - (1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1)),
                    1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2)),
                    -((Math.log(10) * (logEC50 - conc) * (top - bottom) * Math.pow(10, (logEC50 - conc) * hillslope))
                    / (Math.pow((Math.pow(10, (logEC50 - conc) * hillslope) + 1), 2)))

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1, 1, 1, 1});

        double bottom = params[0];
        double top = params[1];
        double logEC50 = params[2];
        double hillslope = params[3];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitBotConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double bottom = bottomConstrain;

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (top, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double top = parameters[0];
                double logEC50 = parameters[1];
                double hillslope = parameters[2];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double top = parameters[0];
                double logEC50 = parameters[1];
                double hillslope = parameters[2];

                return new double[]{
                    1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2)),
                    -((Math.log(10) * (logEC50 - conc) * (top - bottom) * Math.pow(10, (logEC50 - conc) * hillslope))
                    / (Math.pow((Math.pow(10, (logEC50 - conc) * hillslope) + 1), 2)))

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1, 1, 1});

        double top = params[0];
        double logEC50 = params[1];
        double hillslope = params[2];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitTopConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double top = topConstrain;

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double bottom = parameters[0];
                double logEC50 = parameters[1];
                double hillslope = parameters[2];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double bottom = parameters[0];
                double logEC50 = parameters[1];
                double hillslope = parameters[2];

                return new double[]{
                    1 - (1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1)),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2)),
                    -((Math.log(10) * (logEC50 - conc) * (top - bottom) * Math.pow(10, (logEC50 - conc) * hillslope))
                    / (Math.pow((Math.pow(10, (logEC50 - conc) * hillslope) + 1), 2)))

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1, 1, 1});

        double bottom = params[0];
        double logEC50 = params[1];
        double hillslope = params[2];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, int standardHillSlope) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final int hillslope = standardHillSlope;

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, top, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double bottom = parameters[0];
                double top = parameters[1];
                double logEC50 = parameters[2];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double bottom = parameters[0];
                double top = parameters[1];
                double logEC50 = parameters[2];

                return new double[]{
                    1 - (1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1)),
                    1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2)),};

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1, 1, 1});

        double bottom = params[0];
        double top = params[1];
        double logEC50 = params[2];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitBotTopConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double bottom = bottomConstrain;
        final Double top = topConstrain;

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, top, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double logEC50 = parameters[0];
                double hillslope = parameters[1];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double logEC50 = parameters[0];
                double hillslope = parameters[1];

                return new double[]{
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2)),
                    -((Math.log(10) * (logEC50 - conc) * (top - bottom) * Math.pow(10, (logEC50 - conc) * hillslope))
                    / (Math.pow((Math.pow(10, (logEC50 - conc) * hillslope) + 1), 2)))

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1, 1});

        double logEC50 = params[0];
        double hillslope = params[1];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitBotHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, int standardHillSlope) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double bottom = bottomConstrain;
        final int hillslope = standardHillSlope;

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, top, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double top = parameters[0];
                double logEC50 = parameters[1];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double top = parameters[0];
                double logEC50 = parameters[1];

                return new double[]{
                    1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2))};

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1, 1});

        double top = params[0];
        double logEC50 = params[1];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain, int standardHillSlope) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double top = topConstrain;
        final int hillslope = standardHillSlope;

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, top, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double bottom = parameters[0];
                double logEC50 = parameters[1];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double bottom = parameters[0];
                double logEC50 = parameters[1];

                return new double[]{
                    1 - (1 / (Math.pow(10, (logEC50 - conc) * hillslope) + 1)),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2))};

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1, 1});

        double bottom = params[0];
        double logEC50 = params[1];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitBotTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain, int standardHillSlope) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double bottom = bottomConstrain;
        final Double top = topConstrain;
        final int hillslope = standardHillSlope;

        for (Double concentration : dataToFit.keySet()) {
            List<Double> velocities = dataToFit.get(concentration);
            for (Double velocity : velocities) {
                fitter.addObservedPoint(concentration, velocity);
            }
        }

        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, top, logEC50 and
             * hillslope)
             * @return The velocity
             */
            @Override
            public double value(double conc, double[] parameters) {
                double logEC50 = parameters[0];

                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }

            @Override
            public double[] gradient(double conc, double[] parameters) {
                double logEC50 = parameters[0];

                return new double[]{
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (conc + logEC50)) * (bottom - top))
                    / (Math.pow((Math.pow(10, conc * hillslope) + Math.pow(10, logEC50 * hillslope)), 2))

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{1});

        double logEC50 = params[0];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

}
