/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.StatisticsAnalyzer;
import be.ugent.maf.cellmissy.analysis.StatisticsCalculator;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("statisticsAnalyzer")
public class StatisticsAnalyzerImpl implements StatisticsAnalyzer {

    @Autowired
    private StatisticsCalculator statisticsCalculator;

    @Override
    public void computePValues(AnalysisGroup analysisGroup) {
        List<PlateCondition> plateConditions = analysisGroup.getPlateConditions();
        int sizeOfGroup = plateConditions.size();
        int maximumNumberOfReplicates = AnalysisUtils.getMaximumNumberOfReplicates(plateConditions);
        Double[][] slopesMatrix = generateSlopesMatrix(sizeOfGroup, maximumNumberOfReplicates, analysisGroup.getAnalysisResults());
        Double[][] pValuesMatrix = new Double[sizeOfGroup][sizeOfGroup];
        for (int i = 0; i < slopesMatrix.length; i++) {
            double[] firstVector = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopesMatrix[i]));
            for (int seq = 0; seq < pValuesMatrix.length; seq++) {
                if (seq != i) {
                    double[] secondVector = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopesMatrix[seq]));
                    double pValue = statisticsCalculator.executeMannWhitneyUTest(firstVector, secondVector);
                    pValuesMatrix[i][seq] = pValue;
                }
            }
        }
        analysisGroup.setpValuesMatrix(AnalysisUtils.transpose2DArray(pValuesMatrix));
    }

    @Override
    public void adjustPValues(AnalysisGroup analysisGroup) {
        Double[][] pValuesMatrix = analysisGroup.getpValuesMatrix();
        Double[][] adjustedPValues = statisticsCalculator.correctForMultipleComparisons(pValuesMatrix);
        analysisGroup.setAdjustedPValues(adjustedPValues);
    }

    /**
     * Put slopes in a matrix (data frame shape)
     * @param firstDimension
     * @param secondDimension
     * @param analysisResults
     * @return 
     */
    private Double[][] generateSlopesMatrix(int firstDimension, int secondDimension, List<AreaAnalysisResults> analysisResults) {

        Double[][] slopesMatrix = new Double[firstDimension][secondDimension];
        for (int i = 0; i < firstDimension; i++) {
            // exclude null values and cast to primitive
            Double[] slopes = analysisResults.get(i).getSlopes();
            slopesMatrix[i] = slopes;
        }
        return slopesMatrix;
    }
}
