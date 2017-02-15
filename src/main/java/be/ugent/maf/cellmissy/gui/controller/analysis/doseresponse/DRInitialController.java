/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInitialPlotPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.util.List;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author CompOmics Gwen
 */
public abstract class DRInitialController {

    //model
    protected Double bottomConstrainValue;
    protected Double topConstrainValue;
    protected NonEditableTableModel tableModel;
    //view
    protected DRInitialPlotPanel dRInitialPlotPanel;
    protected ChartPanel initialChartPanel;
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

    public NonEditableTableModel getTableModel() {
        return tableModel;
    }

    protected void setTableModel(NonEditableTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public ChartPanel getInitialChartPanel() {
        return initialChartPanel;
    }

    public DRInitialPlotPanel getDRInitialPlotPanel() {
        return dRInitialPlotPanel;
    }

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initDRInitialPanel();

    }

    /**
     * Shared methods (protected)
     */
    /**
     * Create the table model for the top panel table. Table contains
     * log-transformed concentration and replicate slopes per condition
     *
     * @param dataToFit
     * @return the model
     */
    protected NonEditableTableModel createTableModel(List<DoseResponsePair> dataToFit) {
        int maxReplicates = 0;
        for (DoseResponsePair entry : dataToFit) {
            int replicates = entry.getResponses().size();
            if (replicates > maxReplicates) {
                maxReplicates = replicates;
            }
        }
        Object[][] data = new Object[dataToFit.size()][maxReplicates + 1];

        int rowIndex = 0;
        for (DoseResponsePair entry : dataToFit) {
            //log concentration is put on 1st column
            data[rowIndex][0] = AnalysisUtils.roundThreeDecimals(entry.getDose());

            for (int columnIndex = 1; columnIndex < entry.getResponses().size() + 1; columnIndex++) {
                try {
                    Double slope = entry.getResponses().get(columnIndex - 1);
                    if (slope != null && !slope.isNaN()) {
                        // round to three decimals slopes and coefficients
                        slope = AnalysisUtils.roundThreeDecimals(entry.getResponses().get(columnIndex - 1));
                        // show in table slope + (coefficient)
                        data[rowIndex][columnIndex] = slope;
                    } else if (slope == null) {
                        data[rowIndex][columnIndex] = "excluded";
                    } else if (slope.isNaN()) {
                        data[rowIndex][columnIndex] = "NaN";
                    }
                } catch (IndexOutOfBoundsException e) {
                    data[rowIndex][columnIndex] = "";
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
     * Abstract methods
     */
    public abstract void initDRInitialData();

    protected abstract void initDRInitialPanel();
}
