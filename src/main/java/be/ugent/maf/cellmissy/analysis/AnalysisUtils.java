/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Paola Masuzzo
 */
public class AnalysisUtils {

    //round double to 2 decimals
    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
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
}
