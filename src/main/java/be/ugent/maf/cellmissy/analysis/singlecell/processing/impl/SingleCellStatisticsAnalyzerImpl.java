/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrector;
import be.ugent.maf.cellmissy.analysis.StatisticsCalculator;
import be.ugent.maf.cellmissy.analysis.factory.MultipleComparisonsCorrectionFactory;
import be.ugent.maf.cellmissy.analysis.factory.StatisticsTestFactory;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellStatisticsAnalyzer;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola
 */
@Component("singleCellStatisticsAnalyzerImpl")
public class SingleCellStatisticsAnalyzerImpl implements SingleCellStatisticsAnalyzer {

    @Override
    public void generateSummaryStatistics(SingleCellAnalysisGroup singleCellAnalysisGroup, String statisticalTestName, String parameter) {
        StatisticsCalculator statisticsCalculator = StatisticsTestFactory.getInstance().getStatisticsCalculator(statisticalTestName);
        List<StatisticalSummary> statisticalSummaries = new ArrayList<>();

        singleCellAnalysisGroup.getConditionDataHolders().stream().map((conditionDataHolder) -> {
            Double data[] = null;
            if (parameter.equalsIgnoreCase("cell speed")) {
                data = conditionDataHolder.getTrackSpeedsVector();
            } else if (parameter.equals("cell direct")) {
                data = conditionDataHolder.getEndPointDirectionalityRatios();
            }
             else if (parameter.equalsIgnoreCase("accumulated distance")) {
                data = conditionDataHolder.getCumulativeDistancesVector();
            }
            else if (parameter.equalsIgnoreCase("euclidian distance")) {
                data = conditionDataHolder.getEuclideanDistancesVector();
            }
            return data;
        }).map((data) -> statisticsCalculator.getSummaryStatistics(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(data)))).forEach((statisticalSummary) -> {
            statisticalSummaries.add(statisticalSummary);
        });
        singleCellAnalysisGroup.setStatisticalSummaries(statisticalSummaries);
    }

    @Override
    public void executePairwiseComparisons(SingleCellAnalysisGroup singleCellAnalysisGroup, String statisticalTestName, String parameter) {
        StatisticsCalculator statisticsCalculator = StatisticsTestFactory.getInstance().getStatisticsCalculator(statisticalTestName);

        Double[][] dataMatrix = generateDataMatrix(singleCellAnalysisGroup, parameter);

        Double[][] pValuesMatrix = new Double[dataMatrix.length][dataMatrix.length];
        for (int i = 0; i < dataMatrix.length; i++) {
            double[] firstVector = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(dataMatrix[i]));
            for (int seq = 0; seq < dataMatrix.length; seq++) {
                if (seq != i) {
                    double[] secondVector = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(dataMatrix[seq]));
                    double pValue = statisticsCalculator.executeStatisticalTest(firstVector, secondVector);
                    pValuesMatrix[i][seq] = pValue;
                }
            }
        }
        Double[][] transposedMatrix = AnalysisUtils.transpose2DArray(pValuesMatrix);
        Double[][] formatSymmetricMatrix = AnalysisUtils.formatSymmetricMatrix(transposedMatrix);
        singleCellAnalysisGroup.setpValuesMatrix(formatSymmetricMatrix);
    }

    @Override
    public void detectSignificance(SingleCellAnalysisGroup singleCellAnalysisGroup, String statisticalTestName, double alpha, boolean isAdjusted) {
        StatisticsCalculator statisticsCalculator = StatisticsTestFactory.getInstance().getStatisticsCalculator(statisticalTestName);
        Double[][] dataToLook;
        if (!isAdjusted) {
            dataToLook = singleCellAnalysisGroup.getpValuesMatrix();
        } else {
            dataToLook = singleCellAnalysisGroup.getAdjustedPValuesMatrix();
        }
        boolean[][] significances = statisticsCalculator.detectSignificance(dataToLook, alpha);
        singleCellAnalysisGroup.setSignificances(significances);
    }

    @Override
    public void correctForMultipleComparisons(SingleCellAnalysisGroup singleCellAnalysisGroup, String correctionBeanName) {
        MultipleComparisonsCorrector corrector = MultipleComparisonsCorrectionFactory.getInstance().getCorrector(correctionBeanName);
        corrector.correctForMultipleComparisons(singleCellAnalysisGroup);
    }

    /**
     * Put data in a matrix (data frame shape)
     *
     * @param firstDimension
     * @param secondDimension
     * @param analysisResults
     * @return
     */
    private Double[][] generateDataMatrix(SingleCellAnalysisGroup singleCellAnalysisGroup, String parameter) {

        int size = singleCellAnalysisGroup.getConditionDataHolders().size();
        Double[][] dataMatrix = new Double[size][size];
        Double data[] = null;
        for (int i = 0; i < size; i++) {
            if (parameter.equalsIgnoreCase("cell speed")) {
                data = singleCellAnalysisGroup.getConditionDataHolders().get(i).getTrackSpeedsVector();
            } else if (parameter.equals("cell direct")) {
                data = singleCellAnalysisGroup.getConditionDataHolders().get(i).getEndPointDirectionalityRatios();
            }
            else if (parameter.equalsIgnoreCase("accumulated distance")) {
                data = singleCellAnalysisGroup.getConditionDataHolders().get(i).getCumulativeDistancesVector();
            }
            else if (parameter.equalsIgnoreCase("euclidian distance")) {
                data = singleCellAnalysisGroup.getConditionDataHolders().get(i).getEuclideanDistancesVector();
            }
            dataMatrix[i] = data;
        }
        return dataMatrix;
    }
}
