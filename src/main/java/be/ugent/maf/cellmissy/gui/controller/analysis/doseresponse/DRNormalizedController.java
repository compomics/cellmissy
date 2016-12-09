/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRNormalizedPlotPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartPanel;

/**
 * Abstract class with fields and methods shared between generic and area
 * dose-response analysis. Method that call the parent controller or a logger
 * cannot be put here as they are specific per implementation.
 *
 * @author Gwendolien Sergeant
 */
public abstract class DRNormalizedController {

    //model
    protected Double bottomConstrainValue;
    protected Double topConstrainValue;
    protected NonEditableTableModel tableModel;
    //view
    protected DRNormalizedPlotPanel dRNormalizedPlotPanel;
    protected ChartPanel normalizedChartPanel;

    // services
    protected GridBagConstraints gridBagConstraints;

    /**
     * Getters and setters
     *
     * @return
     */
    public Double getBottomConstrainValue() {
        return bottomConstrainValue;
    }

    public Double getTopConstrainValue() {
        return topConstrainValue;
    }

    public DRNormalizedPlotPanel getDRNormalizedPlotPanel() {
        return dRNormalizedPlotPanel;
    }

    public NonEditableTableModel getTableModel() {
        return tableModel;
    }

    protected void setTableModel(NonEditableTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public ChartPanel getNormalizedChartPanel() {
        return normalizedChartPanel;
    }

    /**
     * Public methods
     */
    /**
     * Give information on how normalization was performed for the PDF report.
     *
     * @return
     */
    public String getNormalizationInfo() {
        Double bottomNormalize = Double.parseDouble(dRNormalizedPlotPanel.getBottomTextField().getText());
        Double topNormalize = Double.parseDouble(dRNormalizedPlotPanel.getTopTextField().getText());
        String result = "NORMALIZATION: ";
        String zero = " 0% = " + bottomNormalize + " (" + (String) dRNormalizedPlotPanel.getBottomComboBox().getSelectedItem() + ")  ";
        String hundred = " 100% = " + topNormalize + " (" + (String) dRNormalizedPlotPanel.getTopComboBox().getSelectedItem() + ")  ";
        return result + zero + hundred;
    }

    /**
     * Shared methods
     */
    /**
     * Create the table model for the top panel table. Table contains
     * log-transformed concentration and normalized slopes per condition
     *
     * @param dataToFit
     * @return the model
     */
    protected NonEditableTableModel createTableModel(LinkedHashMap<Double, List<Double>> dataToFit) {
        int maxReplicates = 0;
        for (Map.Entry<Double, List<Double>> entry : dataToFit.entrySet()) {
            int replicates = entry.getValue().size();
            if (replicates > maxReplicates) {
                maxReplicates = replicates;
            }
        }
        Object[][] data = new Object[dataToFit.size()][maxReplicates + 1];

        int rowIndex = 0;
        for (Map.Entry<Double, List<Double>> entry : dataToFit.entrySet()) {
            //log concentration is put on 1st column
            data[rowIndex][0] = AnalysisUtils.roundThreeDecimals(entry.getKey());

            for (int columnIndex = 1; columnIndex < entry.getValue().size() + 1; columnIndex++) {
                Double slope = entry.getValue().get(columnIndex - 1);
                if (slope != null && !slope.isNaN()) {
                    // round to three decimals slopes and coefficients
                    slope = AnalysisUtils.roundThreeDecimals(entry.getValue().get(columnIndex - 1));
                    // show in table slope + (coefficient)
                    data[rowIndex][columnIndex] = slope;
                } else if (slope == null) {
                    data[rowIndex][columnIndex] = "excluded";
                } else if (slope.isNaN()) {
                    data[rowIndex][columnIndex] = "NaN";
                }
            }
            rowIndex++;
        }
        // array of column names for table model
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "Log-concentration";
        for (int x = 1; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + (x);
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

    /**
     * Perform normalization on velocities
     */
    protected Double normalize(Double velocity) {
        //check which values will become 0% and 100%
        Double bottomNormalize = Double.parseDouble(dRNormalizedPlotPanel.getBottomTextField().getText());
        Double topNormalize = Double.parseDouble(dRNormalizedPlotPanel.getTopTextField().getText());

        if (velocity == null) {
            return velocity;
        } else if (velocity.isNaN()) {
            return velocity;
        }
        return 100 * (velocity - bottomNormalize) / (topNormalize - bottomNormalize);
    }

    /**
     * Compute mean values of every condition in the dose response analysis
     * group
     */
    protected List<Double> computeMeans(Collection<List<Double>> velocitiesCollection) {
        List<Double> allMeans = new ArrayList();
        for (List<Double> velocities : velocitiesCollection) {
            List<Double> tempData = new ArrayList();
            for (Double velocity : velocities) {
                if (velocity != null) {
                    tempData.add(velocity);
                }
            }
            double[] data = new double[tempData.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = tempData.get(i);
            }
            allMeans.add(AnalysisUtils.computeMean(data));
        }
        return allMeans;
    }

    /**
     * Compute median values of every condition in DR analysis group
     */
    protected List<Double> computeMedians(Collection<List<Double>> velocitiesCollection) {
        List<Double> allMedians = new ArrayList();
        for (List<Double> velocities : velocitiesCollection) {
            List<Double> tempData = new ArrayList();
            for (Double velocity : velocities) {
                if (velocity != null) {
                    tempData.add(velocity);
                }
            }
            double[] data = new double[tempData.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = tempData.get(i);
            }
            allMedians.add(AnalysisUtils.computeMedian(data));
        }
        return allMedians;
    }
}
