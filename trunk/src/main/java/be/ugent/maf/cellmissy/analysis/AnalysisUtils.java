/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 *
 * @author Paola Masuzzo
 */
public class AnalysisUtils {

    //round double to 2 decimals
    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.00");
        return Double.valueOf(twoDForm.format(d));
    }

    //exclude null values from an array of Double 
    public static Double[] excludeNullValues(Double[] data) {
        List<Double> list = new ArrayList<>();
        for (Double value : data) {
            if (value != null) {
                list.add(value);
            }
        }
        Double[] toArray = list.toArray(new Double[list.size()]);
        return toArray;
    }

    public static double computeMean(double[] data) {
        // sum of all the elements
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum / data.length;
    }

    public static double computeMedian(double[] data) {
        // sort the input data
        Arrays.sort(data);
        //make a distinction between odd and even data points
        if (data.length % 2 == 1) {
            return data[(data.length + 1) / 2 - 1];
        } else {
            double lower = data[(data.length / 2 - 1)];
            double upper = data[(data.length / 2)];

            return (lower + upper) / 2;
        }
    }

    public static double computeStandardDeviation(double[] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            final double v = data[i] - computeMean(data);
            sum += v * v;
        }
        return Math.sqrt(sum / data.length);
    }

    public static double computeSEM(double[] data) {
        return (computeStandardDeviation(data) / Math.sqrt(data.length));
    }

    public static double computeMAD(double[] data) {
        //scale factor for asymptotically normal consistency
        final double constant = 1.4826;
        double[] deviations = new double[data.length];
        double median = computeMedian(data);

        for (int i = 0; i < data.length; i++) {
            deviations[i] = Math.abs(data[i] - median);
        }
        return constant * computeMedian(deviations);
    }

    public static void setShadowVisible(final JFreeChart chart, final boolean state) {
        if (chart != null) {
            final Plot p = chart.getPlot();
            if (p instanceof XYPlot) {
                final XYPlot xyplot = (XYPlot) p;
                final XYItemRenderer xyItemRenderer = xyplot.getRenderer();
                if (xyItemRenderer instanceof XYBarRenderer) {
                    final XYBarRenderer br = (XYBarRenderer) xyItemRenderer;
                    br.setBarPainter(new StandardXYBarPainter());
                    br.setShadowVisible(state);
                }
            } else if (p instanceof CategoryPlot) {
                final CategoryPlot categoryPlot = (CategoryPlot) p;
                final CategoryItemRenderer categoryItemRenderer = categoryPlot.getRenderer();
                if (categoryItemRenderer instanceof BarRenderer) {
                    final BarRenderer br = (BarRenderer) categoryItemRenderer;
                    br.setBarPainter(new StandardBarPainter());
                    br.setShadowVisible(state);
                }
            }
        }
    }
}
