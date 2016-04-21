/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.impl.doseresponse;

import be.ugent.maf.cellmissy.analysis.area.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
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
    public void fitNoConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, int standardHillslope) {

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        //initial parameter values for fitting: lowest y, highest y, middle x and standard hillslope
        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
        double initialTop = yValues[0];
        double initialBottom = yValues[0];
        double initialLogEC50;
        double maxX = xValues[0];
        double minX = xValues[0];
        for (int i = 0; i < yValues.length; i++) {
            if (yValues[i] < initialBottom) {
                initialBottom = yValues[i];
            } else if (yValues[i] > initialTop) {
                initialTop = yValues[i];
            }
            if (xValues[i] < minX) {
                minX = xValues[i];
            } else if (xValues[i] > maxX) {
                maxX = xValues[i];
            }
        }
        initialLogEC50 = (maxX + minX) / 2;

        for (int i =0; i < xValues.length; i++){
            fitter.addObservedPoint(xValues[i], yValues[i]);
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
                    1 - (1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1)),
                    1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2)),
                    (Math.log(10) * (logEC50 - conc) * (bottom - top) * Math.pow(10, (logEC50 + conc) * hillslope))
                    / Math.pow((Math.pow(10, logEC50 * hillslope) + Math.pow(10, hillslope * conc)), 2)

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{initialBottom, initialTop, initialLogEC50, standardHillslope});

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
    public void fitBotConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, int standardHillslope) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double bottom = bottomConstrain;

        //initial parameter values for fitting: highest y, middle x and standard hillslope
        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
        double initialTop = yValues[0];
        double initialLogEC50;
        double maxX = xValues[0];
        double minX = xValues[0];
        for (int i = 0; i < yValues.length; i++) {
            if (yValues[i] > initialTop) {
                initialTop = yValues[i];
            }
            if (xValues[i] < minX) {
                minX = xValues[i];
            } else if (xValues[i] > maxX) {
                maxX = xValues[i];
            }
        }
        initialLogEC50 = (maxX + minX) / 2;

        for (int i =0; i < xValues.length; i++){
            fitter.addObservedPoint(xValues[i], yValues[i]);
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
                    1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2)),
                    (Math.log(10) * (logEC50 - conc) * (bottom - top) * Math.pow(10, (logEC50 + conc) * hillslope))
                    / Math.pow((Math.pow(10, logEC50 * hillslope) + Math.pow(10, hillslope * conc)), 2)

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{initialTop, initialLogEC50, standardHillslope});

        double top = params[0];
        double logEC50 = params[1];
        double hillslope = params[2];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

    @Override
    public void fitTopConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain, int standardHillslope) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double top = topConstrain;

        //initial parameter values for fitting: lowest y, middle x and standard hillslope
        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
        double initialBottom = yValues[0];
        double initialLogEC50;
        double maxX = xValues[0];
        double minX = xValues[0];
        for (int i = 0; i < yValues.length; i++) {
            if (yValues[i] < initialBottom) {
                initialBottom = yValues[i];
            }
            if (xValues[i] < minX) {
                minX = xValues[i];
            } else if (xValues[i] > maxX) {
                maxX = xValues[i];
            }
        }
        initialLogEC50 = (maxX + minX) / 2;

        for (int i =0; i < xValues.length; i++){
            fitter.addObservedPoint(xValues[i], yValues[i]);
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
                    1 - (1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1)),
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2)),
                    (Math.log(10) * (logEC50 - conc) * (bottom - top) * Math.pow(10, (logEC50 + conc) * hillslope))
                    / Math.pow((Math.pow(10, logEC50 * hillslope) + Math.pow(10, hillslope * conc)), 2)

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{initialBottom, initialLogEC50, standardHillslope});

        double bottom = params[0];
        double logEC50 = params[1];
        double hillslope = params[2];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

//    @Override
//    public void fitHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, int standardHillSlope) {
//        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
//        CurveFitter fitter = new CurveFitter(optimizer);
//
//        final int hillslope = standardHillSlope;
//
//        //initial parameter values for fitting: lowest y, highest y, middle x
//        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
//        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
//        double initialTop = yValues[0];
//        double initialBottom = yValues[0];
//        double initialLogEC50;
//        double maxX = xValues[0];
//        double minX = xValues[0];
//        for (int i = 0; i < yValues.length; i++) {
//            if (yValues[i] < initialBottom) {
//                initialBottom = yValues[i];
//            } else if (yValues[i] > initialTop) {
//                initialTop = yValues[i];
//            }
//            if (xValues[i] < minX) {
//                minX = xValues[i];
//            } else if (xValues[i] > maxX) {
//                maxX = xValues[i];
//            }
//        }
//        initialLogEC50 = (maxX + minX) / 2;
//
//        for (int i =0; i < xValues.length; i++){
//            fitter.addObservedPoint(xValues[i], yValues[i]);
//        }
//        
//        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
//            /**
//             * @param conc The concentration of the drug, log transformed
//             * @param paramaters The fitted parameters (bottom, top, logEC50 and
//             * hillslope)
//             * @return The velocity
//             */
//            @Override
//            public double value(double conc, double[] parameters) {
//                double bottom = parameters[0];
//                double top = parameters[1];
//                double logEC50 = parameters[2];
//
//                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
//            }
//
//            @Override
//            public double[] gradient(double conc, double[] parameters) {
//                double bottom = parameters[0];
//                double top = parameters[1];
//                double logEC50 = parameters[2];
//
//                return new double[]{
//                    1 - (1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1)),
//                    1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1),
//                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
//                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2))
//
//                };
//
//            }
//
//        };
//
//        double[] params = null;
//
//        params = fitter.fit(function, new double[]{initialBottom, initialTop, initialLogEC50});
//
//        double bottom = params[0];
//        double top = params[1];
//        double logEC50 = params[2];
//
//        resultsHolder.setBottom(bottom);
//        resultsHolder.setTop(top);
//        resultsHolder.setLogEC50(logEC50);
//        resultsHolder.setHillslope(hillslope);
//    }

    @Override
    public void fitBotTopConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain, int standardHillslope) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);

        final Double bottom = bottomConstrain;
        final Double top = topConstrain;

        //initial parameter values for fitting: middle x and standard hillslope
        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
        double initialLogEC50;
        double maxX = xValues[0];
        double minX = xValues[0];
        for (int i = 0; i < xValues.length; i++) {
            if (xValues[i] < minX) {
                minX = xValues[i];
            } else if (xValues[i] > maxX) {
                maxX = xValues[i];
            }
        }
        initialLogEC50 = (maxX + minX) / 2;

        for (int i =0; i < xValues.length; i++){
            fitter.addObservedPoint(xValues[i], yValues[i]);
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
                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2)),
                    (Math.log(10) * (logEC50 - conc) * (bottom - top) * Math.pow(10, (logEC50 + conc) * hillslope))
                    / Math.pow((Math.pow(10, logEC50 * hillslope) + Math.pow(10, hillslope * conc)), 2)

                };

            }

        };

        double[] params = null;

        params = fitter.fit(function, new double[]{initialLogEC50, standardHillslope});

        double logEC50 = params[0];
        double hillslope = params[1];

        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

//    @Override
//    public void fitBotHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, int standardHillSlope) {
//        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
//        CurveFitter fitter = new CurveFitter(optimizer);
//
//        final Double bottom = bottomConstrain;
//        final int hillslope = standardHillSlope;
//
//        //initial parameter values for fitting: highest y, middle x
//        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
//        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
//        double initialTop = yValues[0];
//        double initialLogEC50;
//        double maxX = xValues[0];
//        double minX = xValues[0];
//        for (int i = 0; i < yValues.length; i++) {
//            if (yValues[i] > initialTop) {
//                initialTop = yValues[i];
//            }
//            if (xValues[i] < minX) {
//                minX = xValues[i];
//            } else if (xValues[i] > maxX) {
//                maxX = xValues[i];
//            }
//        }
//        initialLogEC50 = (maxX + minX) / 2;
//
//        for (int i =0; i < xValues.length; i++){
//            fitter.addObservedPoint(xValues[i], yValues[i]);
//        }
//
//        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
//            /**
//             * @param conc The concentration of the drug, log transformed
//             * @param paramaters The fitted parameters (bottom, top, logEC50 and
//             * hillslope)
//             * @return The velocity
//             */
//            @Override
//            public double value(double conc, double[] parameters) {
//                double top = parameters[0];
//                double logEC50 = parameters[1];
//
//                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
//            }
//
//            @Override
//            public double[] gradient(double conc, double[] parameters) {
//                double top = parameters[0];
//                double logEC50 = parameters[1];
//
//                return new double[]{
//                    1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1),
//                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
//                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2))
//                };
//
//            }
//
//        };
//
//        double[] params = null;
//
//        params = fitter.fit(function, new double[]{initialTop, initialLogEC50});
//
//        double top = params[0];
//        double logEC50 = params[1];
//
//        resultsHolder.setBottom(bottom);
//        resultsHolder.setTop(top);
//        resultsHolder.setLogEC50(logEC50);
//        resultsHolder.setHillslope(hillslope);
//    }
//
//    @Override
//    public void fitTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain, int standardHillSlope) {
//        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
//        CurveFitter fitter = new CurveFitter(optimizer);
//
//        final Double top = topConstrain;
//        final int hillslope = standardHillSlope;
//
//        //initial parameter values for fitting: lowest y, middle x
//        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
//        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
//        double initialBottom = yValues[0];
//        double initialLogEC50;
//        double maxX = xValues[0];
//        double minX = xValues[0];
//        for (int i = 0; i < yValues.length; i++) {
//            if (yValues[i] < initialBottom) {
//                initialBottom = yValues[i];
//            }
//            if (xValues[i] < minX) {
//                minX = xValues[i];
//            } else if (xValues[i] > maxX) {
//                maxX = xValues[i];
//            }
//        }
//        initialLogEC50 = (maxX + minX) / 2;
//
//        for (int i =0; i < xValues.length; i++){
//            fitter.addObservedPoint(xValues[i], yValues[i]);
//        }
//
//        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
//            /**
//             * @param conc The concentration of the drug, log transformed
//             * @param paramaters The fitted parameters (bottom, top, logEC50 and
//             * hillslope)
//             * @return The velocity
//             */
//            @Override
//            public double value(double conc, double[] parameters) {
//                double bottom = parameters[0];
//                double logEC50 = parameters[1];
//
//                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
//            }
//
//            @Override
//            public double[] gradient(double conc, double[] parameters) {
//                double bottom = parameters[0];
//                double logEC50 = parameters[1];
//
//                return new double[]{
//                    1 - (1 / ((Math.pow(10, (logEC50 - conc) * hillslope)) + 1)),
//                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
//                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2))
//                };
//
//            }
//
//        };
//
//        double[] params = null;
//
//        params = fitter.fit(function, new double[]{initialBottom, initialLogEC50});
//
//        double bottom = params[0];
//        double logEC50 = params[1];
//
//        resultsHolder.setBottom(bottom);
//        resultsHolder.setTop(top);
//        resultsHolder.setLogEC50(logEC50);
//        resultsHolder.setHillslope(hillslope);
//    }
//
//    @Override
//    public void fitBotTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain, int standardHillSlope) {
//        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
//        CurveFitter fitter = new CurveFitter(optimizer);
//
//        final Double bottom = bottomConstrain;
//        final Double top = topConstrain;
//        final int hillslope = standardHillSlope;
//        
//        //initial parameter values for fitting: lowest y, highest y, middle x and standard hillslope
//        double[] xValues = AnalysisUtils.generateXValues(dataToFit);
//        double[] yValues = AnalysisUtils.generateYValues(dataToFit);
//        double initialLogEC50;
//        double maxX = xValues[0];
//        double minX = xValues[0];
//        for (int i = 0; i < xValues.length; i++) {
//            if (xValues[i] < minX) {
//                minX = xValues[i];
//            } else if (xValues[i] > maxX) {
//                maxX = xValues[i];
//            }
//        }
//        initialLogEC50 = (maxX + minX) / 2;
//
//        for (int i =0; i < xValues.length; i++){
//            fitter.addObservedPoint(xValues[i], yValues[i]);
//        }
//
//        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
//            /**
//             * @param conc The concentration of the drug, log transformed
//             * @param paramaters The fitted parameters (bottom, top, logEC50 and
//             * hillslope)
//             * @return The velocity
//             */
//            @Override
//            public double value(double conc, double[] parameters) {
//                double logEC50 = parameters[0];
//
//                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
//            }
//
//            @Override
//            public double[] gradient(double conc, double[] parameters) {
//                double logEC50 = parameters[0];
//
//                return new double[]{
//                    (hillslope * Math.log(10) * Math.pow(10, hillslope * (logEC50 + conc)) * (bottom - top))
//                    / (Math.pow(Math.pow(10, hillslope * conc) + Math.pow(10, hillslope * logEC50), 2))
//
//                };
//
//            }
//
//        };
//
//        double[] params = null;
//
//        params = fitter.fit(function, new double[]{initialLogEC50});
//
//        double logEC50 = params[0];
//
//        resultsHolder.setBottom(bottom);
//        resultsHolder.setTop(top);
//        resultsHolder.setLogEC50(logEC50);
//        resultsHolder.setHillslope(hillslope);
//    }

}
