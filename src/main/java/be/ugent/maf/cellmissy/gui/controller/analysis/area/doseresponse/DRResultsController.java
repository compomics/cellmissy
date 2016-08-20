/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRResultsPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.PdfUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for dose-response analysis statistics and PFD report.
 *
 * @author Gwendolien
 */
@Controller("dRResultsController")
public class DRResultsController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DRResultsController.class);

    //model
    private NonEditableTableModel tableModel;
    private ChartPanel dupeInitialChartPanel;
    private ChartPanel dupeNormalizedChartPanel;
    private Experiment experiment;
    private Document document;
    private PdfWriter writer;
    private static final Font bodyFont = new Font(Font.HELVETICA, 8);
    private static final Font boldFont = new Font(Font.HELVETICA, 8, Font.BOLD);
    private static final Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final int chartWidth = 500;
    private static final int chartHeight = 450;
    //view
    private DRResultsPanel dRResultsPanel;
    // parent controller
    @Autowired
    private DoseResponseController doseResponseController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init view
        initDRResultsPanel();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public DRResultsPanel getdRResultsPanel() {
        return dRResultsPanel;
    }

    public NonEditableTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(NonEditableTableModel tableModel) {
        this.tableModel = tableModel;
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
     * When changing view to results panel, calculate statistics, set table
     * model and re-plot fittings.
     */
    public void initDRResultsData() {
        calculateStatistics(doseResponseController.getdRAnalysisGroup());
        setTableModel(createTableModel(doseResponseController.getdRAnalysisGroup()));
    }

    /**
     * Calculate statictics and set corresponding fields in analysisResults
     * class. Called by sibling controllers after fitting.
     */
    public void calculateStatistics(DoseResponseAnalysisGroup analysisGroup) {
        DoseResponseAnalysisResults analysisResults = analysisGroup.getDoseResponseAnalysisResults();
        SigmoidFittingResultsHolder initialFittingResults = analysisResults.getInitialFittingResults();
        SigmoidFittingResultsHolder normalizedFittingResults = analysisResults.getNormalizedFittingResults();
        //calculate and set R²
        analysisResults.setGoodnessOfFitInitial(AnalysisUtils.computeRSquared(doseResponseController.getDataToFit(false), initialFittingResults));
        analysisResults.setGoodnessOfFitNormalized(AnalysisUtils.computeRSquared(doseResponseController.getDataToFit(true), normalizedFittingResults));
        //calculate and set EC50
        analysisResults.setEc50Initial(Math.pow(10, initialFittingResults.getLogEC50()));
        analysisResults.setEc50Normalized(Math.pow(10, normalizedFittingResults.getLogEC50()));

        //calculate and set statistics per parameter
        //"bottom"
        double parameter = initialFittingResults.getBottom();
        double stdErr = AnalysisUtils.calculateStandardError(parameter, initialFittingResults.getParameterDistributions().get("bottom"));
        analysisResults.setStdErrBottomInitial(stdErr);
        analysisResults.setcIBottomInitial(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));
        parameter = normalizedFittingResults.getBottom();
        stdErr = AnalysisUtils.calculateStandardError(parameter, normalizedFittingResults.getParameterDistributions().get("bottom"));
        analysisResults.setStdErrBottomNormalized(stdErr);
        analysisResults.setcIBottomNormalized(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));

        //"top"
        parameter = initialFittingResults.getTop();
        stdErr = AnalysisUtils.calculateStandardError(parameter, initialFittingResults.getParameterDistributions().get("top"));
        analysisResults.setStdErrTopInitial(stdErr);
        analysisResults.setcITopInitial(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));
        parameter = normalizedFittingResults.getTop();
        stdErr = AnalysisUtils.calculateStandardError(parameter, normalizedFittingResults.getParameterDistributions().get("top"));
        analysisResults.setStdErrTopNormalized(stdErr);
        analysisResults.setcITopNormalized(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));

        //"logEC50"
        parameter = initialFittingResults.getLogEC50();
        stdErr = AnalysisUtils.calculateStandardError(parameter, initialFittingResults.getParameterDistributions().get("logec50"));
        analysisResults.setStdErrLogEC50Initial(stdErr);
        analysisResults.setcILogEC50Initial(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));
        parameter = normalizedFittingResults.getLogEC50();
        stdErr = AnalysisUtils.calculateStandardError(parameter, normalizedFittingResults.getParameterDistributions().get("logec50"));
        analysisResults.setStdErrLogEC50Normalized(stdErr);
        analysisResults.setcILogEC50Normalized(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));

        //"hillslope"
        parameter = initialFittingResults.getHillslope();
        stdErr = AnalysisUtils.calculateStandardError(parameter, initialFittingResults.getParameterDistributions().get("hillslope"));
        analysisResults.setStdErrHillslopeInitial(stdErr);
        analysisResults.setcIHillslopeInitial(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));
        parameter = normalizedFittingResults.getLogEC50();
        stdErr = AnalysisUtils.calculateStandardError(parameter, normalizedFittingResults.getParameterDistributions().get("hillslope"));
        analysisResults.setStdErrHillslopeNormalized(stdErr);
        analysisResults.setcIHillslopeNormalized(AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, stdErr, 1.96));

        //confidence interval for ec50 (antilog of logec50 ci bounds)
        double[] cILogEc50initial = analysisResults.getcILogEC50Initial();
        double[] cILogEc50normalized = analysisResults.getcILogEC50Normalized();
        double[] cIEc50initial = new double[2];
        double[] cIEc50normalized = new double[2];
        for (int i = 0; i < cILogEc50initial.length; i++) {
            cIEc50initial[i] = Math.pow(10, cILogEc50initial[i]);
            cIEc50normalized[i] = Math.pow(10, cILogEc50normalized[i]);
        }
        analysisResults.setcIEC50Initial(cIEc50initial);
        analysisResults.setcIEC50Normalized(cIEc50normalized);

    }

    public void plotCharts() {
        JFreeChart initialChart = doseResponseController.createDoseResponseChart(doseResponseController.getDataToFit(false), doseResponseController.getdRAnalysisGroup(), false);
        JFreeChart normalizedChart = doseResponseController.createDoseResponseChart(doseResponseController.getDataToFit(true), doseResponseController.getdRAnalysisGroup(), true);
        dupeInitialChartPanel.setChart(initialChart);
        dupeNormalizedChartPanel.setChart(normalizedChart);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        dRResultsPanel.getDoseResponseChartParentPanel().add(dupeInitialChartPanel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        dRResultsPanel.getDoseResponseChartParentPanel().add(dupeNormalizedChartPanel, gridBagConstraints);
        doseResponseController.getDRPanel().revalidate();
        doseResponseController.getDRPanel().repaint();
    }

    /**
     * Create the PDF report file.
     *
     * @param directory
     * @param reportName
     * @return the file created
     */
    public File createAnalysisReport(File directory, String reportName) {
        this.experiment = doseResponseController.getExperiment();
        File pdfFile = new File(directory, reportName);
        if (reportName.endsWith(".pdf")) {
            tryToCreateFile(pdfFile);
        } else {
            doseResponseController.showMessage("Please use .pdf extension for the file.", "extension file problem", JOptionPane.WARNING_MESSAGE);
            // retry to create pdf file
            try {
                doseResponseController.createPdfReport();
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                doseResponseController.showMessage("An error occurred: " + ex.getMessage(), "unexpected error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return pdfFile;
    }

    /**
     * Initialize view
     */
    private void initDRResultsPanel() {
        dRResultsPanel = new DRResultsPanel();
        //init chart panels
        dupeInitialChartPanel = new ChartPanel(null);
        dupeInitialChartPanel.setOpaque(false);
        dupeNormalizedChartPanel = new ChartPanel(null);
        dupeNormalizedChartPanel.setOpaque(false);

        /**
         * Action listener for button. Copies the table with statistical values
         * and the plots and puts them in a PDF report.
         */
        dRResultsPanel.getCreateReportButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    /**
     * Create the table model for the top panel table. Table contains icon,
     * log-transformed concentration and normalized slopes per condition
     *
     * @param dataToFit
     * @return the model
     */
    private NonEditableTableModel createTableModel(DoseResponseAnalysisGroup analysisGroup) {
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
        data[1][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getInitialFittingResults().getBottom());
        data[2][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getInitialFittingResults().getTop());
        data[3][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getInitialFittingResults().getHillslope());
        data[4][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getInitialFittingResults().getLogEC50());
        data[5][1] = df.format(analysisResults.getEc50Initial());
        data[6][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getGoodnessOfFitInitial());
        data[8][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrBottomInitial());
        data[9][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrTopInitial());
        data[10][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrHillslopeInitial());
        data[11][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrLogEC50Initial());
        data[13][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomInitial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomInitial()[1]);
        data[14][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcITopInitial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcITopInitial()[1]);
        data[15][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeInitial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeInitial()[1]);
        data[16][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Initial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Initial()[1]);
        data[17][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIEC50Initial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIEC50Initial()[1]);
        //set third column (normalized fitting results)
        data[1][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getBottom());
        data[2][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getTop());
        data[3][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getHillslope());
        data[4][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getLogEC50());
        data[5][2] = df.format(analysisResults.getEc50Normalized());
        data[6][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getGoodnessOfFitNormalized());
        data[8][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrBottomNormalized());
        data[9][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrTopNormalized());
        data[10][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrHillslopeNormalized());
        data[11][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrLogEC50Normalized());
        data[13][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomNormalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomNormalized()[1]);
        data[14][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcITopNormalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcITopNormalized()[1]);
        data[15][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeNormalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeNormalized()[1]);
        data[16][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Normalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Normalized()[1]);
        data[17][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIEC50Normalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIEC50Normalized()[1]);
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "";
        columnNames[1] = "Initial fitting";
        columnNames[2] = "Normalized fitting";

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

    /**
     * @param pdfFile
     */
    private void tryToCreateFile(File pdfFile) {
        try {
            boolean success = pdfFile.createNewFile();
            if (success) {
                doseResponseController.showMessage("Pdf Report successfully created!", "Report created", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Object[] options = {"Yes", "No", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(null, "File already exists. Do you want to replace it?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                // if YES, user wants to delete existing file and replace it
                if (showOptionDialog == 0) {
                    boolean delete = pdfFile.delete();
                    if (!delete) {
                        return;
                    }
                    // if NO, returns already existing file
                } else if (showOptionDialog == 1) {
                    return;
                }
            }
        } catch (IOException ex) {
            doseResponseController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            // actually create PDF file
            createPdfFile(fileOutputStream);
        } catch (IOException ex) {
            doseResponseController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param outputStream
     */
    private void createPdfFile(FileOutputStream outputStream) {
        document = null;
        writer = null;
        try {
            // get new instances
            // the Document is the base layout element
            document = new Document();
            // the pdfWriter is actually creating the file
            writer = PdfWriter.getInstance(document, outputStream);
            //open document
            document.open();
            // add content to document
            addContent();
            //dispose resources
            document.close();
            document = null;
            writer.close();
            writer = null;
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Add content to document.
     */
    private void addContent() {
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

    /**
     * Overview of Report: experiment and project numbers + some details.
     */
    //STILL NEEDS TO BE EDITED/ADJUSTED FOR DOSE-RESPONSE!!
    private void addOverview() {
        String title = "CellMissy - DOSE RESPONSE ANALYSIS REPORT - EXPERIMENT " + experiment + " - " + "PROJECT " + experiment.getProject();
        PdfUtils.addTitle(document, title, titleFont);
        PdfUtils.addEmptyLines(document, 1);
        // add information on dataset (algorithm) and imaging type analyzed
        List<String> lines = new ArrayList<>();
        String line = "DATASET: " + doseResponseController.getSelectedALgorithm();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        // add conditions number
        lines.clear();
        line = "NUMBER OF BIOLOGICAL CONDITIONS: " + experiment.getPlateConditionList().size();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
    }

    private void addAnalysisGroupInfoTable() {

    }

    private void addInitialFittingInfo() {

    }

    private void addNormalizedFittingInfo() {

    }
}
