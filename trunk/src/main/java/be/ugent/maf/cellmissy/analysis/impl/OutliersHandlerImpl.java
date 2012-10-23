/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("outliersHandler")
public class OutliersHandlerImpl implements OutliersHandler {

    @Override
    public List<Double[]> handleOutliers(Double[] data) {
        Double[] excludeNullValues = AnalysisUtils.excludeNullValues(data);
        List<Double[]> list = new ArrayList<>();

        //List for outliers
        List<Double> outliers = new ArrayList<>();
        //List for new corrected data
        List<Double> correctedData = new ArrayList<>();
        final double k = 1.5;
        
        double firstQuartile = AnalysisUtils.computeFirstQuartile(ArrayUtils.toPrimitive(excludeNullValues));
        double thirdQuartile = AnalysisUtils.computeThirdQuartile(ArrayUtils.toPrimitive(excludeNullValues));
        double IQR = thirdQuartile - firstQuartile;
        for (int i = 0; i < excludeNullValues.length; i++) {
            //check if value is greater than [Q3+k*IQR]
            if (excludeNullValues[i] > (thirdQuartile + k * IQR)) {
                outliers.add(excludeNullValues[i]);
            } else {
                correctedData.add(excludeNullValues[i]);
            }
        }
        //caste list to arrays and add arrays to the List
        Double[] outliersToArray = outliers.toArray(new Double[outliers.size()]);
        list.add(outliersToArray);
        Double[] correctedDataToArray = correctedData.toArray(new Double[correctedData.size()]);
        list.add(correctedDataToArray);

        return list;
    }
}
