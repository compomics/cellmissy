/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrector;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.ranking.NaturalRanking;

/**
 *
 * @author Paola Masuzzo
 */
public class BenjaminiCorrector implements MultipleComparisonsCorrector {

    @Override
    public void correctForMultipleComparisons(AnalysisGroup analysisGroup) {
        Double[][] pValuesMatrix = analysisGroup.getpValuesMatrix();
        Double[][] adjustedPValuesMatrix = new Double[pValuesMatrix.length][pValuesMatrix.length];
        // format the matrux since its symmetric
        Double[][] symmetricMatrix = AnalysisUtils.formatSymmetricMatrix(pValuesMatrix);
        double[] adjustedPValues = adjustPValues(symmetricMatrix);
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
     * 
     * @param matrix
     * @return 
     */
    private double[] adjustPValues(Double[][] matrix) {
        // get p values from the matrix and put them in a vector
        double[] pValues = putPValuesInAVector(matrix);
        // number of comparisons
        int numberOfComp = pValues.length;
        // new array for adjusted p-values
        double[] adjustedPValues = new double[numberOfComp];

        // Ranking p values
        NaturalRanking naturalRanking = new NaturalRanking();
        double[] ranks = naturalRanking.rank(pValues);

        List<PValue> pValuesList = new ArrayList<>();
        for (int i = 0; i < numberOfComp; i++) {
            pValuesList.add(new PValue(i, pValues[i], ranks[i]));
        }
        
        Collections.sort(pValuesList, new PValueComparator());
        PValue maxRankPValue = pValuesList.get(0);
        int index = maxRankPValue.getIndex();
        adjustedPValues[index] = maxRankPValue.getpValue();
        
        for (int i = 1; i < numberOfComp; i++) {
            PValue pValue = pValuesList.get(i); 
            double rank = pValue.getRank();
            adjustedPValues[pValue.getIndex()] = pValues[i] * numberOfComp / rank;
        }
        return adjustedPValues;
    }

    /**
     * get p values from a symmetric matrix and put them in a vector
     * @param symmetricMatrix
     * @return 
     */
    private double[] putPValuesInAVector(Double[][] symmetricMatrix) {
        List<Double> list = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < symmetricMatrix.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < symmetricMatrix[0].length; columnIndex++) {
                if (symmetricMatrix[rowIndex][columnIndex] != null) {
                    list.add(symmetricMatrix[rowIndex][columnIndex]);
                }
            }
        }
        return ArrayUtils.toPrimitive(list.toArray(new Double[list.size()]));
    }

    /**
     * Class with pValue, index and ranking
     */
    private class PValue {

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
    private class PValueComparator implements Comparator<PValue> {

        @Override
        public int compare(PValue o1, PValue o2) {
            return Double.compare(o2.getRank(), o1.getRank());
        }
        
    }
}
