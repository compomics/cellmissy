/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

/**
 *
 * @author Paola Masuzzo
 */
@Service("outliersHandler")
public class OutliersHandlerImpl implements OutliersHandler {

    @Override
    public List<double[]> handleOutliers(double[] data) {
        List<double[]> list = new ArrayList<>();

        //List for outliers
        List<Double> outliers = new ArrayList<>();
        //List for new corrected data
        List<Double> correctedData = new ArrayList<>();
        DescriptiveStatistics dataStatistics = new DescriptiveStatistics();
        double k = 1.5;
        for (int i = 0; i < data.length; i++) {
            dataStatistics.addValue(data[i]);
        }
        double q1 = dataStatistics.getPercentile(25);
        double q3 = dataStatistics.getPercentile(75);
        for (int i = 0; i < data.length; i++) {
            //check if value is outside the range: [Q1-k*IQR], [Q3+k*IQR]
            if (data[i] < (q1 - k * (q3 - q1)) || data[i] > (q3 + k * (q3 - q1))) {
                outliers.add(data[i]);
            }
            else{
                correctedData.add(data[i]);
            }
        }
        
        //caste list to arrays and add arrays to the List
        double[] outliersArray = ArrayUtils.toPrimitive(outliers.toArray(new Double[outliers.size()]));
        list.add(outliersArray);
        double[] correctedDataArray = ArrayUtils.toPrimitive(correctedData.toArray(new Double[correctedData.size()]));
        list.add(correctedDataArray);
        
        return list;
    }
}
