/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.impl;

import be.ugent.maf.cellmissy.analysis.factory.MultipleComparisonsCorrectionFactory;
import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrector;
import be.ugent.maf.cellmissy.analysis.area.AreaStatisticsAnalyzer;
import be.ugent.maf.cellmissy.analysis.StatisticsCalculator;
import be.ugent.maf.cellmissy.analysis.factory.StatisticsTestFactory;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisGroup;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.springframework.stereotype.Component;

/**
 * Implementation for the statistical analyzer.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("areaStatisticsAnalyzer")
public class AreaStatisticsAnalyzerImpl implements AreaStatisticsAnalyzer {

    @Override
    public void generateSummaryStatistics(AreaAnalysisGroup analysisGroup, String statisticalTestName) {
        StatisticsCalculator statisticsCalculator = StatisticsTestFactory.getInstance().getStatisticsCalculator(statisticalTestName);
        List<StatisticalSummary> statisticalSummaries = new ArrayList<>();
        for (AreaAnalysisResults areaAnalysisResults : analysisGroup.getAreaAnalysisResults()) {
            Double[] slopes = areaAnalysisResults.getSlopes();
            StatisticalSummary statisticalSummary = statisticsCalculator.getSummaryStatistics(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopes)));
            statisticalSummaries.add(statisticalSummary);
        }
        analysisGroup.setStatisticalSummaries(statisticalSummaries);
    }

    @Override
    public void executePairwiseComparisons(AreaAnalysisGroup analysisGroup, String statisticalTestName) {
        StatisticsCalculator statisticsCalculator = StatisticsTestFactory.getInstance().getStatisticsCalculator(statisticalTestName);
        List<PlateCondition> plateConditions = analysisGroup.getPlateConditions();
        int sizeOfGroup = plateConditions.size();
        int maximumNumberOfReplicates = AnalysisUtils.getMaximumNumberOfReplicates(plateConditions);
        Double[][] slopesMatrix = generateSlopesMatrix(sizeOfGroup, maximumNumberOfReplicates, analysisGroup.getAreaAnalysisResults());
        Double[][] pValuesMatrix = new Double[sizeOfGroup][sizeOfGroup];
        for (int i = 0; i < sizeOfGroup; i++) {
            double[] firstVector = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopesMatrix[i]));
            for (int seq = 0; seq < sizeOfGroup; seq++) {
                if (seq != i) {
                    double[] secondVector = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopesMatrix[seq]));
                    double pValue = statisticsCalculator.executeStatisticalTest(firstVector, secondVector);
                    pValuesMatrix[i][seq] = pValue;
                }
            }
        }
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(pValuesMatrix);
        Double[][] formatSymmetricMatrix = AnalysisUtils.formatSymmetricMatrix(transposedMatrix);
        analysisGroup.setpValuesMatrix(formatSymmetricMatrix);
    }

    @Override
    public void detectSignificance(AreaAnalysisGroup analysisGroup, String statisticalTestName, double alpha, boolean isAdjusted) {
        StatisticsCalculator statisticsCalculator = StatisticsTestFactory.getInstance().getStatisticsCalculator(statisticalTestName);
        Double[][] dataToLook;
        if (!isAdjusted) {
            dataToLook = analysisGroup.getpValuesMatrix();
        } else {
            dataToLook = analysisGroup.getAdjustedPValuesMatrix();
        }
        boolean[][] significances = statisticsCalculator.detectSignificance(dataToLook, alpha);
        analysisGroup.setSignificances(significances);
    }

    @Override
    public void correctForMultipleComparisons(AreaAnalysisGroup analysisGroup, String correctionBeanName) {
        MultipleComparisonsCorrector corrector = MultipleComparisonsCorrectionFactory.getInstance().getCorrector(correctionBeanName);
        corrector.correctForMultipleComparisons(analysisGroup);
    }

    /**
     * Put slopes in a matrix (data frame shape)
     *
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
