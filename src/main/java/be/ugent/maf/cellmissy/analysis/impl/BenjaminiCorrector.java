/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrector;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.ranking.NaturalRanking;

/**
 * This is performing a multiple comparisons correction with the Benjamini
 * algorithm.
 *
 * @author Paola Masuzzo
 */
public class BenjaminiCorrector implements MultipleComparisonsCorrector {

    @Override
    public void correctForMultipleComparisons(AreaAnalysisGroup analysisGroup) {
        Double[][] pValuesMatrix = analysisGroup.getpValuesMatrix();
        Double[][] adjustedPValuesMatrix = new Double[pValuesMatrix.length][pValuesMatrix.length];
        double[] adjustedPValues = adjustPValues(pValuesMatrix);
        // put back pvalues in a matrix
        int counter = 0;
        for (int rowIndex = 0; rowIndex < adjustedPValuesMatrix.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < adjustedPValuesMatrix[0].length; columnIndex++) {
                if (pValuesMatrix[rowIndex][columnIndex] != null) {
                    adjustedPValuesMatrix[rowIndex][columnIndex] = adjustedPValues[counter];
                    counter++;
                }
            }
        }
        analysisGroup.setAdjustedPValuesMatrix(adjustedPValuesMatrix);
    }

    /**
     * This is actually adjusting the p-values with the Benjamini correction.
     *
     * @param matrix
     * @return
     */
    private double[] adjustPValues(Double[][] matrix) {
        // get p values from the matrix and put them in a vector
        double[] pValues = putPValuesInAVector(matrix);
        // number of comparisons
        int numberOfComparisons = pValues.length;
        // new array for adjusted p-values
        double[] adjustedPValues = new double[numberOfComparisons];

        // Ranking p values
        NaturalRanking naturalRanking = new NaturalRanking();
        double[] ranks = naturalRanking.rank(pValues);

        List<PValue> pValuesList = new ArrayList<>();
        for (int i = 0; i < numberOfComparisons; i++) {
            pValuesList.add(new PValue(i, pValues[i], ranks[i]));
        }

        // sort p values according to ranks
        Collections.sort(pValuesList, new PValueComparator());
        // first one: max rank -- the largest p-value remains as it is.
        PValue maxRankPValue = pValuesList.get(0);
        int index = maxRankPValue.getIndex();
        adjustedPValues[index] = maxRankPValue.getpValue();

        // from the second largest p-valus on, the p-value is multiplied by the total number of comparisons divided by its own rank
        for (int i = 1; i < numberOfComparisons; i++) {
            PValue pValue = pValuesList.get(i);
            double rank = pValue.getRank();
            adjustedPValues[pValue.getIndex()] = pValue.pValue * numberOfComparisons / rank;
        }
        return adjustedPValues;
    }

    /**
     * get p values from a symmetric matrix and put them in a vector
     *
     * @param symmetricMatrix
     * @return
     */
    private double[] putPValuesInAVector(Double[][] symmetricMatrix) {
        List<Double> list = new ArrayList<>();
        for (Double[] aSymmetricMatrix : symmetricMatrix) {
            for (int columnIndex = 0; columnIndex < symmetricMatrix[0].length; columnIndex++) {
                if (aSymmetricMatrix[columnIndex] != null) {
                    list.add(aSymmetricMatrix[columnIndex]);
                }
            }
        }
        return ArrayUtils.toPrimitive(list.toArray(new Double[list.size()]));
    }

    @Override
    public void correctForMultipleComparisons(SingleCellAnalysisGroup singleCellAnalysisGroup) {
        Double[][] pValuesMatrix = singleCellAnalysisGroup.getpValuesMatrix();
        Double[][] adjustedPValuesMatrix = new Double[pValuesMatrix.length][pValuesMatrix.length];
        double[] adjustedPValues = adjustPValues(pValuesMatrix);
        // put back pvalues in a matrix
        int counter = 0;
        for (int rowIndex = 0; rowIndex < adjustedPValuesMatrix.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < adjustedPValuesMatrix[0].length; columnIndex++) {
                if (pValuesMatrix[rowIndex][columnIndex] != null) {
                    adjustedPValuesMatrix[rowIndex][columnIndex] = adjustedPValues[counter];
                    counter++;
                }
            }
        }
        singleCellAnalysisGroup.setAdjustedPValuesMatrix(adjustedPValuesMatrix);
    }

    /**
     * Class with pValue, index and ranking
     */
    private static class PValue {

        private int index;
        private double pValue;
        private double rank;

        public PValue(int index, double pValue, double rank) {
            this.index = index;
            this.pValue = pValue;
            this.rank = rank;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getpValue() {
            return pValue;
        }

        public void setpValue(double pValue) {
            this.pValue = pValue;
        }

        public double getRank() {
            return rank;
        }

        public void setRank(double rank) {
            this.rank = rank;
        }
    }

    /*
     * Comparator for Pvalue class (compare through ranking)
     */
    private static class PValueComparator implements Comparator<PValue> {

        @Override
        public int compare(PValue o1, PValue o2) {
            return Double.compare(o2.getRank(), o1.getRank());
        }
    }
}
