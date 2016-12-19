/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseStatisticsHolder;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRResultsPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.PdfUtils;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * Abstract class with fields and methods shared between generic and area
 * dose-response analysis. Method that call the parent controller or a logger
 * cannot be put here as they are specific per implementation.
 *
 * @author CompOmics Gwen
 */
public abstract class DRResultsController {

    protected static final Font bodyFont = new Font(Font.HELVETICA, 8);
    protected static final Font boldFont = new Font(Font.HELVETICA, 8, Font.BOLD);
    protected static final Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    protected static final int chartWidth = 500;
    protected static final int chartHeight = 450;

    //model
    protected NonEditableTableModel tableModel;
    protected ChartPanel dupeInitialChartPanel;
    protected ChartPanel dupeNormalizedChartPanel;
    protected Experiment experiment;
    protected Document document;
    protected PdfWriter writer;
    //view
    protected DRResultsPanel dRResultsPanel;

    // services
    protected GridBagConstraints gridBagConstraints;

    /**
     * Getters and setters
     *
     * @return
     */
    public NonEditableTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(NonEditableTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public DRResultsPanel getdRResultsPanel() {
        return dRResultsPanel;
    }

    public ChartPanel getDupeInitialChartPanel() {
        return dupeInitialChartPanel;
    }

    public ChartPanel getDupeNormalizedChartPanel() {
        return dupeNormalizedChartPanel;
    }

    public NonEditableTableModel reCreateTableModel(DoseResponseAnalysisGroup analysisGroup) {
        return createTableModel(analysisGroup);
    }

    /**
     * Initialise controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init view
        initDRResultsPanel();
    }

    /**
     * Abstract methods
     */
    protected abstract void initDRResultsPanel();

    protected abstract void initDRResultsData();

    protected abstract void setStatistics(DoseResponseAnalysisGroup analysisGroup);

    protected abstract void plotCharts();

    protected abstract File createAnalysisReport(File directory, String reportName);

    protected abstract void tryToCreateFile(File pdfFile);

    protected abstract void createPdfFile(FileOutputStream outputStream);

    protected abstract void addOverview();

    protected abstract void addInitialFittingInfo();

    protected abstract void addNormalizedFittingInfo();

    protected abstract void addTable(PdfPTable dataTable);

    protected abstract PdfPTable createAnalysisGroupInfoTable();

    protected abstract PdfPTable createFittingInfoTable(boolean normalized);

    protected abstract void addImageFromChart(JFreeChart chart, int imageWidth, int imageHeight);

    /**
     * Shared methods
     */
    /**
     * Create the table model for the top panel table. Table contains icon,
     * log-transformed concentration and normalized slopes per condition
     *
     * @param dataToFit
     * @return the model
     */
    protected NonEditableTableModel createTableModel(DoseResponseAnalysisGroup analysisGroup) {
        DoseResponseAnalysisResults analysisResults = analysisGroup.getDoseResponseAnalysisResults();
        //specify decimal format for scientific notation
        DecimalFormat df = new DecimalFormat("00.00E00");
        Object[][] data = new Object[18][3];

        //set fields in first column
        data[0][0] = "Best-fit value";
        data[1][0] = "    Bottom";
        data[2][0] = "    Top";
        data[3][0] = "    Hillslope";
        data[4][0] = "    LogEC50";
        data[5][0] = "EC50";
        data[6][0] = "R² (goodness of fit)";
        data[7][0] = "Standard error";
        data[8][0] = "    Bottom";
        data[9][0] = "    Top";
        data[10][0] = "    Hillslope";
        data[11][0] = "    LogEC50";
        data[12][0] = "95% Confidence interval";
        data[13][0] = "    Bottom";
        data[14][0] = "    Top";
        data[15][0] = "    Hillslope";
        data[16][0] = "    LogEC50";
        data[17][0] = "    EC50";

        //set second column (initial fitting results)
        SigmoidFittingResultsHolder fittingResults = analysisResults.getFittingResults(false);
        DoseResponseStatisticsHolder statistics = analysisResults.getStatistics(false);
        data[1][1] = AnalysisUtils.roundThreeDecimals(fittingResults.getBottom());
        data[2][1] = AnalysisUtils.roundThreeDecimals(fittingResults.getTop());
        data[3][1] = AnalysisUtils.roundThreeDecimals(fittingResults.getHillslope());
        data[4][1] = AnalysisUtils.roundThreeDecimals(fittingResults.getLogEC50());
        data[5][1] = df.format(analysisResults.getStatistics(false).getEc50());
        data[6][1] = AnalysisUtils.roundThreeDecimals(statistics.getGoodnessOfFit());
        if (statistics.getStdErrBottom() != 0) {
            data[8][1] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrBottom());
        } else {
            data[8][1] = "--";
        }
        if (statistics.getStdErrTop() != 0) {
            data[9][1] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrTop());
        } else {
            data[9][1] = "--";
        }
        data[10][1] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrHillslope());
        data[11][1] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrLogEC50());
        if (statistics.getcIBottom() != null) {
            data[13][1] = AnalysisUtils.roundThreeDecimals(statistics.getcIBottom()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcIBottom()[1]);
        } else {
            data[13][1] = "--";
        }
        if (statistics.getcITop() != null) {
            data[14][1] = AnalysisUtils.roundThreeDecimals(statistics.getcITop()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcITop()[1]);
        } else {
            data[14][1] = "--";
        }
        data[15][1] = AnalysisUtils.roundThreeDecimals(statistics.getcIHillslope()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcIHillslope()[1]);
        data[16][1] = AnalysisUtils.roundThreeDecimals(statistics.getcILogEC50()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcILogEC50()[1]);
        data[17][1] = df.format(statistics.getcIEC50()[0]) + " to " + df.format(statistics.getcIEC50()[1]);

        //set third column (normalized fitting results)
        fittingResults = analysisResults.getFittingResults(true);
        statistics = analysisResults.getStatistics(true);
        data[1][2] = AnalysisUtils.roundThreeDecimals(fittingResults.getBottom());
        data[2][2] = AnalysisUtils.roundThreeDecimals(fittingResults.getTop());
        data[3][2] = AnalysisUtils.roundThreeDecimals(fittingResults.getHillslope());
        data[4][2] = AnalysisUtils.roundThreeDecimals(fittingResults.getLogEC50());
        data[5][2] = df.format(statistics.getEc50());
        data[6][2] = AnalysisUtils.roundThreeDecimals(statistics.getGoodnessOfFit());
        if (statistics.getStdErrBottom() != 0) {
            data[8][2] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrBottom());
        } else {
            data[8][2] = "--";
        }
        if (statistics.getStdErrTop() != 0) {
            data[9][2] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrTop());
        } else {
            data[9][2] = "--";
        }
        data[10][2] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrHillslope());
        data[11][2] = AnalysisUtils.roundThreeDecimals(statistics.getStdErrLogEC50());
        if (statistics.getcIBottom() != null) {
            data[13][2] = AnalysisUtils.roundThreeDecimals(statistics.getcIBottom()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcIBottom()[1]);
        } else {
            data[13][2] = "--";
        }
        if (statistics.getcITop() != null) {
            data[14][2] = AnalysisUtils.roundThreeDecimals(statistics.getcITop()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcITop()[1]);
        } else {
            data[14][2] = "--";
        }
        data[15][2] = AnalysisUtils.roundThreeDecimals(statistics.getcIHillslope()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcIHillslope()[1]);
        data[16][2] = AnalysisUtils.roundThreeDecimals(statistics.getcILogEC50()[0]) + " to " + AnalysisUtils.roundThreeDecimals(statistics.getcILogEC50()[1]);
        data[17][2] = df.format(statistics.getcIEC50()[0]) + " to " + df.format(statistics.getcIEC50()[1]);

        String[] columnNames = new String[data[0].length];
        columnNames[0] = "";
        columnNames[1] = "Initial fitting";
        columnNames[2] = "Normalized fitting";

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

    protected void calculateStatistics(DoseResponseStatisticsHolder statisticsHolder, SigmoidFittingResultsHolder resultsHolder, LinkedHashMap<Double, List<Double>> dataToFit) {
        //calculate and set R² and EC50
        statisticsHolder.setGoodnessOfFit(AnalysisUtils.computeRSquared(dataToFit, resultsHolder));
        statisticsHolder.setEc50(Math.pow(10, resultsHolder.getLogEC50()));

        //calculate and set standard errors of parameters
        //calculate and set 95% confidence interval boundaries
        double[] standardErrors = AnalysisUtils.calculateStandardErrors(dataToFit, resultsHolder);
        statisticsHolder.setStdErrBottom(standardErrors[0]);
        statisticsHolder.setcIBottom(checkAndGetCI(resultsHolder.getBottom(), standardErrors[0]));
        statisticsHolder.setStdErrTop(standardErrors[1]);
        statisticsHolder.setcITop(checkAndGetCI(resultsHolder.getTop(), standardErrors[1]));
        statisticsHolder.setStdErrLogEC50(standardErrors[2]);
        statisticsHolder.setcILogEC50(checkAndGetCI(resultsHolder.getLogEC50(), standardErrors[2]));
        statisticsHolder.setStdErrHillslope(standardErrors[3]);
        statisticsHolder.setcIHillslope(checkAndGetCI(resultsHolder.getHillslope(), standardErrors[3]));

        //confidence interval for ec50 (antilog of logec50 ci)
        double[] cILogEc50 = statisticsHolder.getcILogEC50();
        double[] cIEc50 = new double[2];
        for (int i = 0; i < cILogEc50.length; i++) {
            cIEc50[i] = Math.pow(10, cILogEc50[i]);
        }
        statisticsHolder.setcIEC50(cIEc50);
    }

    protected double[] checkAndGetCI(double parameter, double standardError) {
        if (standardError != 0.0) {
            return AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, standardError);
        } else {
            return null;
        }
    }

    /**
     * Add the contents of the array to the list
     *
     * @param list
     * @param array
     * @return A copy of the original list, with the array values added to it.
     */
    protected List<Double> addArrayToList(List<Double> list, double[] array) {
        List<Double> copiedList = new ArrayList<>(list);
        //if parameter is constrained there is a null instead of an array
        if (array == null) {
            copiedList.add(null);
            copiedList.add(null);
        } else {
            for (int i = 0; i < array.length; i++) {
                copiedList.add(array[i]);
            }
        }
        return copiedList;
    }

    protected void addContent() {
        // overview: title, dataset,  brief summary of the analysis group
        addOverview();
        // table with info from analysis group
        addAnalysisGroupInfoTable();
        // we go here to a new page
        document.newPage();
        // we add the initial fitting information
        addInitialFittingInfo();
        // then, we move to next page
        document.newPage();
        // we add the normalized fitting information
        addNormalizedFittingInfo();
    }

    protected void addAnalysisGroupInfoTable() {
        //add title before the table
        PdfUtils.addTitle(document, "ANALYSIS GROUP SUMMARY", boldFont);
        PdfUtils.addEmptyLines(document, 1);
        PdfPTable conditionsInfoTable = createAnalysisGroupInfoTable();
        addTable(conditionsInfoTable);
    }

}
