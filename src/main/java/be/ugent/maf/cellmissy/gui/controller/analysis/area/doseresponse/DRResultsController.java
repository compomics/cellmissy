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
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
     * Initialise controller
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
     * Calculate statistics and set corresponding fields in analysisResults
     * class. Called by sibling controllers after fitting.
     *
     * @param analysisGroup
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

        //calculate and set statistics of the parameters
        //statistics of the initial fitting
        double[] standardErrors = AnalysisUtils.calculateStandardErrors(doseResponseController.getDataToFit(false), initialFittingResults);
        analysisResults.setStdErrBottomInitial(standardErrors[0]);
        analysisResults.setcIBottomInitial(checkAndGetCI(initialFittingResults.getBottom(), standardErrors[0]));
        analysisResults.setStdErrTopInitial(standardErrors[1]);
        analysisResults.setcITopInitial(checkAndGetCI(initialFittingResults.getTop(), standardErrors[1]));
        analysisResults.setStdErrLogEC50Initial(standardErrors[2]);
        analysisResults.setcILogEC50Initial(checkAndGetCI(initialFittingResults.getLogEC50(), standardErrors[2]));
        analysisResults.setStdErrHillslopeInitial(standardErrors[3]);
        analysisResults.setcIHillslopeInitial(checkAndGetCI(initialFittingResults.getHillslope(), standardErrors[3]));

        //statistics of the normalized fitting
        standardErrors = AnalysisUtils.calculateStandardErrors(doseResponseController.getDataToFit(true), normalizedFittingResults);
        analysisResults.setStdErrBottomNormalized(standardErrors[0]);
        analysisResults.setcIBottomNormalized(checkAndGetCI(normalizedFittingResults.getBottom(), standardErrors[0]));
        analysisResults.setStdErrTopNormalized(standardErrors[1]);
        analysisResults.setcITopNormalized(checkAndGetCI(normalizedFittingResults.getTop(), standardErrors[1]));
        analysisResults.setStdErrLogEC50Normalized(standardErrors[2]);
        analysisResults.setcILogEC50Normalized(checkAndGetCI(normalizedFittingResults.getLogEC50(), standardErrors[2]));
        analysisResults.setStdErrHillslopeNormalized(standardErrors[3]);
        analysisResults.setcIHillslopeNormalized(checkAndGetCI(normalizedFittingResults.getHillslope(), standardErrors[3]));

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

    private double[] checkAndGetCI(double parameter, double standardError) {
        if (standardError != 0.0) {
            return AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, standardError);
        } else {
            return null;
        }
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
        if (analysisResults.getStdErrBottomInitial() != 0) {
            data[8][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrBottomInitial());
        } else {
            data[8][1] = "--";
        }
        if (analysisResults.getStdErrTopInitial() != 0) {
            data[9][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrTopInitial());
        } else {
            data[9][1] = "--";
        }
        data[10][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrHillslopeInitial());
        data[11][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrLogEC50Initial());
        if (analysisResults.getcIBottomInitial() != null) {
            data[13][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomInitial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomInitial()[1]);
        } else {
            data[13][1] = "--";
        }
        if (analysisResults.getcITopInitial() != null) {
            data[14][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcITopInitial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcITopInitial()[1]);
        } else {
            data[14][1] = "--";
        }
        data[15][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeInitial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeInitial()[1]);
        data[16][1] = AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Initial()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Initial()[1]);
        data[17][1] = df.format(analysisResults.getcIEC50Initial()[0]) + " to " + df.format(analysisResults.getcIEC50Initial()[1]);
        //set third column (normalized fitting results)
        data[1][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getBottom());
        data[2][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getTop());
        data[3][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getHillslope());
        data[4][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getNormalizedFittingResults().getLogEC50());
        data[5][2] = df.format(analysisResults.getEc50Normalized());
        data[6][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getGoodnessOfFitNormalized());
        if (analysisResults.getStdErrBottomNormalized() != 0) {
            data[8][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrBottomNormalized());
        } else {
            data[8][2] = "--";
        }
        if (analysisResults.getStdErrTopNormalized() != 0) {
            data[9][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrTopNormalized());
        } else {
            data[9][2] = "--";
        }
        data[10][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrHillslopeNormalized());
        data[11][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getStdErrLogEC50Normalized());
        if (analysisResults.getcIBottomNormalized() != null) {
            data[13][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomNormalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIBottomNormalized()[1]);
        } else {
            data[13][2] = "--";
        }
        if (analysisResults.getcITopNormalized() != null) {
            data[14][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcITopNormalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcITopNormalized()[1]);
        } else {
            data[14][2] = "--";
        }
        data[15][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeNormalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcIHillslopeNormalized()[1]);
        data[16][2] = AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Normalized()[0]) + " to " + AnalysisUtils.roundThreeDecimals(analysisResults.getcILogEC50Normalized()[1]);
        data[17][2] = df.format(analysisResults.getcIEC50Normalized()[0]) + " to " + df.format(analysisResults.getcIEC50Normalized()[1]);
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
        String line = "DATASET: " + doseResponseController.getSelectedAlgorithm();
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
        //add title before the table
        PdfUtils.addTitle(document, "ANALYSIS GROUP SUMMARY", boldFont);
        PdfPTable conditionsInfoTable = createAnalysisGroupInfoTable();
        addTable(conditionsInfoTable);
    }

    /**
     * Add information on the initial fitting: parameters constrained Y/N +
     * values, table with statistical values and the graphical plot
     */
    private void addInitialFittingInfo() {
        //add title before the table
        PdfUtils.addTitle(document, "INITIAL FIT", boldFont);
        PdfUtils.addEmptyLines(document, 1);
        List<Double> constrainValues = doseResponseController.getConstrainValues(false);
        List<String> lines = new ArrayList<>();
        String parameters = "BOTTOM = ";
        if (constrainValues.get(0) == null) {
            parameters += "-";
        } else {
            parameters += constrainValues.get(0);
        }
        parameters += "    TOP + ";
        if (constrainValues.get(1) == null) {
            parameters += "-";
        } else {
            parameters += constrainValues.get(1);
        }
        String line = "CONSTRAINED PARAMETERS: " + parameters;
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
    }

    /**
     * Add information on the initial fitting: normalization settings + values,
     * parameters constrained Y/N, table with statistical values and the
     * graphical plot
     */
    private void addNormalizedFittingInfo() {
        //add title before the table
        PdfUtils.addTitle(document, "NORMALIZED FIT", boldFont);
        PdfUtils.addEmptyLines(document, 1);
        List<Double> constrainValues = doseResponseController.getConstrainValues(true);
        List<String> lines = new ArrayList<>();
        //add information about normalization
        lines.add(doseResponseController.getNormalizationInfo());
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        lines.clear();
        //add information about constraining
        String parameters = "BOTTOM = ";
        if (constrainValues.get(0) == null) {
            parameters += "-";
        } else {
            parameters += constrainValues.get(0);
        }
        parameters += "    TOP + ";
        if (constrainValues.get(1) == null) {
            parameters += "-";
        } else {
            parameters += constrainValues.get(1);
        }
        String line = "CONSTRAINED PARAMETERS: " + parameters;
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
    }

    /**
     * Add a PdfPTable to the document
     *
     * @param dataTable
     */
    private void addTable(PdfPTable dataTable) {
        try {
            document.add(dataTable);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Create PdfTable with info on each condition of the analysis group;
     */
    private PdfPTable createAnalysisGroupInfoTable() {
        //maps double conc to corresponding unit (string)
        LinkedHashMap concentrationsMap = doseResponseController.getdRAnalysisGroup().getConcentrationsMap().get(doseResponseController.getdRAnalysisGroup().getTreatmentToAnalyse());
        //maps log transformed conc (double) to list of velocities (double)
        LinkedHashMap fittedData = doseResponseController.getDataToFit(false);

        // new table with 6 columns
        PdfPTable dataTable = new PdfPTable(6);
        PdfUtils.setUpPdfPTable(dataTable);
        // add 1st row: column names
        PdfUtils.addCustomizedCell(dataTable, "CONDITIONS", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "# TECHNICAL REPLICATES", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "TECHNICAL REPLICATES EXCLUDED?", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "DRUG CONCENTRATION", boldFont);

        //these two are not definitive yet!!
        PdfUtils.addCustomizedCell(dataTable, "CALCULATED VELOCITIES", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "ANALYZED TIME INTERVAL", boldFont);

        int lenght = GuiUtils.getAvailableColors().length;
        // for each condition get results and add a cell
        for (int i = 0; i < plateConditionList.size(); i++) {
            PlateCondition plateCondition = plateConditionList.get(i);
            AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
            // condition index
            int conditionIndex = plateConditionList.indexOf(plateCondition);
            int indexOfColor = conditionIndex % lenght;
            Color color = GuiUtils.getAvailableColors()[indexOfColor];
            PdfUtils.addColoredCell(dataTable, color);
            // how many technical replicates?
            PdfUtils.addCustomizedCell(dataTable, "" + findNumberOfReplicates(areaPreProcessingResults), bodyFont);
            // techincal replicates were excluded, if Y, which ones?
            List<Well> excludedWells = getExcludedWells(plateCondition);
            String excluded;
            if (excludedWells.isEmpty()) {
                excluded = "N";
            } else {
                excluded = "Y " + excludedWells;
            }
            PdfUtils.addCustomizedCell(dataTable, excluded, bodyFont);
            // user chosen time interval
            PdfUtils.addCustomizedCell(dataTable, areaPreProcessingResults.getTimeInterval().toString(), bodyFont);
            // maximum time point
            PdfUtils.addCustomizedCell(dataTable, "" + areaPreProcessingResults.getTimeInterval().getProposedCutOff(), bodyFont);
            // analyzed time interval
            double[] analysisTimeFrames = areaAnalysisController.getAnalysisTimeFrames();
            int firstTimePoint = (int) (analysisTimeFrames[0] / experiment.getExperimentInterval());
            int lastTimePoint = (int) (analysisTimeFrames[analysisTimeFrames.length - 1] / experiment.getExperimentInterval());
            String analyzedTimeInterval = "(" + firstTimePoint + ", " + lastTimePoint + ")";
            PdfUtils.addCustomizedCell(dataTable, "" + analyzedTimeInterval, bodyFont);
        }
        return dataTable;
    }

    /**
     * Create info table for the corresponding fitting mode (initial/normalized)
     *
     * @return
     */
    private PdfPTable createFittingInfoTable() {
        // 4 columns
        PdfPTable dataTable = new PdfPTable(4);
        PdfUtils.setUpPdfPTable(dataTable);
        // add 1st row: column names
        PdfUtils.addCustomizedCell(dataTable, "Parameter", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Best-fit value", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Standard Error", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "95% Confidende Interval", boldFont);

    }
}
