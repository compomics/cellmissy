/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.result.doseresponse.AreaDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseStatisticsHolder;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRResultsPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.PdfUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
@Controller("areaDRResultsController")
public class AreaDRResultsController extends DRResultsController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AreaDRResultsController.class);

    //model: all in super class
    //view
    private DRResultsPanel dRResultsPanel;
    // parent controller
    @Autowired
    private DoseResponseController doseResponseController;
    

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

    public NonEditableTableModel reCreateTableModel(AreaDoseResponseAnalysisGroup analysisGroup) {
        return createTableModel(analysisGroup);
    }

    /**
     * When changing view to results panel, calculate statistics, set table
     * model and re-plot fittings.
     */
    public void initDRResultsData() {
        setStatistics(doseResponseController.getdRAnalysisGroup());
        setTableModel(createTableModel(doseResponseController.getdRAnalysisGroup()));
    }

    /**
     * Calculate statistics and set corresponding fields in analysisResults
     * class. Called by sibling controllers after fitting.
     *
     * @param analysisGroup
     */
    public void setStatistics(AreaDoseResponseAnalysisGroup analysisGroup) {

        //calculate and set statistics for initial fitting
        calculateStatistics(analysisGroup.getDoseResponseAnalysisResults().getStatistics(false), analysisGroup.getDoseResponseAnalysisResults().getFittingResults(false), doseResponseController.getDataToFit(false));

        //do the same for the normalized fitting
        calculateStatistics(analysisGroup.getDoseResponseAnalysisResults().getStatistics(true), analysisGroup.getDoseResponseAnalysisResults().getFittingResults(true), doseResponseController.getDataToFit(true));

    }

    public void plotCharts() {
        JFreeChart initialChart = doseResponseController.createDoseResponseChart(doseResponseController.getDataToFit(false), false);
        JFreeChart normalizedChart = doseResponseController.createDoseResponseChart(doseResponseController.getDataToFit(true), true);
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
                try {
                    //create the PDF report file
                    doseResponseController.createPdfReport();

                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
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
    private NonEditableTableModel createTableModel(AreaDoseResponseAnalysisGroup analysisGroup) {
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
        line = "NUMBER OF BIOLOGICAL CONDITIONS: " + doseResponseController.getdRAnalysisGroup().getVelocitiesMap().size();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        lines.clear();
        line = "DRUG ANALYSED: " + doseResponseController.getdRAnalysisGroup().getTreatmentToAnalyse();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        PdfUtils.addEmptyLines(document, 1);
    }

    private void addAnalysisGroupInfoTable() {
        //add title before the table
        PdfUtils.addTitle(document, "ANALYSIS GROUP SUMMARY", boldFont);
        PdfUtils.addEmptyLines(document, 1);
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
            parameters += "--";
        } else {
            parameters += constrainValues.get(0);
        }
        parameters += "    TOP = ";
        if (constrainValues.get(1) == null) {
            parameters += "--";
        } else {
            parameters += constrainValues.get(1);
        }
        String line = "CONSTRAINED PARAMETERS:   " + parameters;
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        lines.clear();
        line = "R SQUARED (GOODNESS OF FIT) = " + AnalysisUtils.roundThreeDecimals(doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getStatistics(false).getGoodnessOfFit());
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        //add table with statistical values
        PdfPTable initialFittingInfoTable = createFittingInfoTable(false);
        addTable(initialFittingInfoTable);
        PdfUtils.addEmptyLines(document, 1);
        //add graphical plot
        addImageFromChart(doseResponseController.createDoseResponseChart(doseResponseController.getDataToFit(false), false), chartWidth, chartHeight);
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
            parameters += "--";
        } else {
            parameters += constrainValues.get(0);
        }
        parameters += "    TOP = ";
        if (constrainValues.get(1) == null) {
            parameters += "--";
        } else {
            parameters += constrainValues.get(1);
        }
        String line = "CONSTRAINED PARAMETERS:   " + parameters;
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        lines.clear();
        line = "R SQUARED (GOODNESS OF FIT) = " + AnalysisUtils.roundThreeDecimals(doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getStatistics(true).getGoodnessOfFit());
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        //add table with statistical values
        PdfPTable normalizedFittingInfoTable = createFittingInfoTable(true);
        addTable(normalizedFittingInfoTable);
        PdfUtils.addEmptyLines(document, 1);
        //add graphical plot
        addImageFromChart(doseResponseController.createDoseResponseChart(doseResponseController.getDataToFit(true), true), chartWidth, chartHeight);
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
    //possibly reuse dRInputController's createTableModel(List<PlateCondition> processedConditions)
    private PdfPTable createAnalysisGroupInfoTable() {
        //maps log transformed conc (double) to list of velocities (double)
        LinkedHashMap<Double, List<Double>> fittedData = doseResponseController.getDataToFit(false);
        //CONTROL HAS BEEN GIVEN A CONCENTRATION FOR FITTING PURPOSES: find control concentration
        Double controlConcentration = Collections.min(fittedData.keySet());
        
        // new table with 6 columns
        PdfPTable dataTable = new PdfPTable(6);
        PdfUtils.setUpPdfPTable(dataTable);
        // add 1st row: column names
        PdfUtils.addCustomizedCell(dataTable, "DRUG CONCENTRATION", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "# TECHNICAL REPLICATES", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "TECHNICAL REPLICATES EXCLUDED?", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "LOWEST VELOCITY", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "HIGHEST VELOCITY", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "MEDIAN VELOCITY", boldFont);

        // for each condition get results and add a cell
        for (Map.Entry<Double, List<Double>> condition : fittedData.entrySet()) {
            Integer replicates = condition.getValue().size();
            String excluded;
            int excludedCount = 0;
            List<Double> velocities = condition.getValue();

            //count how many replicates were excluded
            for (int i = 0; i < velocities.size(); i++) {
                Double replicate = velocities.get(i);
                if (replicate == null) {
                    excludedCount++;
                }
            }
            if (excludedCount == 0) {
                excluded = "NO";
            } else {
                excluded = "YES, " + excludedCount;
            }
            
            //put log-value of the concentration back to an understandable format
            String concentration;
            Double logConc = condition.getKey();
            Double transformed = Math.pow(10, logConc);
            //check which concentration unit is to be used
            //if lower than 0.1 µM: use nM unit
            if (transformed < Math.pow(10, -7)) {
                concentration = AnalysisUtils.roundTwoDecimals(transformed * Math.pow(10,9)) + " nM";
            } //if lower than 0.1 mM: use µM unit
            else if (transformed < Math.pow(10, -3)) {
                concentration = AnalysisUtils.roundTwoDecimals(transformed * Math.pow(10,6)) + " µM";
            } //else for everything >= 1 mM use mM unit
            else {
                concentration = AnalysisUtils.roundTwoDecimals(transformed * Math.pow(10,3)) + " mM";
            }
            //if this is the control, replace concentration string
            if (logConc == controlConcentration) {
                concentration = "Control";
            }
            //remove null's (excluded replicates) from velocities collection
            velocities.removeAll(Collections.singleton(null));

            PdfUtils.addCustomizedCell(dataTable, concentration, bodyFont);
            PdfUtils.addCustomizedCell(dataTable, replicates.toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, excluded, bodyFont);
            PdfUtils.addCustomizedCell(dataTable, AnalysisUtils.roundThreeDecimals(Collections.min(velocities)).toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, AnalysisUtils.roundThreeDecimals(Collections.max(velocities)).toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, AnalysisUtils.roundThreeDecimals(AnalysisUtils.computeMedian(velocities)).toString(), bodyFont);

        }

        return dataTable;
    }

    /**
     * Create info table for the corresponding fitting mode (initial/normalized)
     *
     * @return
     */
    private PdfPTable createFittingInfoTable(boolean normalized) {
        // 4 columns
        PdfPTable dataTable = new PdfPTable(4);
        PdfUtils.setUpPdfPTable(dataTable);
        DecimalFormat df = new DecimalFormat("0.00E00");
        DoseResponseStatisticsHolder statistics = doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getStatistics(normalized);
        // add 1st row: column names
        PdfUtils.addCustomizedCell(dataTable, "Parameter", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Best-fit value", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Standard Error", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "95% Confidende Interval", boldFont);

        //parameter names
        List<String> parameters = new ArrayList<>();
        parameters.add("Bottom");
        parameters.add("Top");
        parameters.add("LogEC50");
        parameters.add("Hillslope");
        //best-fit values
        List<Double> bestFitValues = new ArrayList<>();
        bestFitValues.add(doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(normalized).getBottom());
        bestFitValues.add(doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(normalized).getTop());
        bestFitValues.add(doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(normalized).getLogEC50());
        bestFitValues.add(doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(normalized).getHillslope());
        //standard errors
        List<Double> standardErrors = new ArrayList<>();
        standardErrors.add(statistics.getStdErrBottom());
        standardErrors.add(statistics.getStdErrTop());
        standardErrors.add(statistics.getStdErrLogEC50());
        standardErrors.add(statistics.getStdErrHillslope());
        //ec50 does not have a SE, will need to be displayed differently
        standardErrors.add(Double.NaN);
        //confidence interval boundaries
        List<Double> cIBoundaries = new ArrayList<>();
        cIBoundaries = addArrayToList(cIBoundaries, statistics.getcIBottom());
        cIBoundaries = addArrayToList(cIBoundaries, statistics.getcITop());
        cIBoundaries = addArrayToList(cIBoundaries, statistics.getcILogEC50());
        cIBoundaries = addArrayToList(cIBoundaries, statistics.getcIHillslope());

        //for all parameters except EC50 (is handled separately for scientific notation purposes)
        for (int row = 0; row < parameters.size(); row++) {
            //parameter name in 1st column
            PdfUtils.addCustomizedCell(dataTable, parameters.get(row), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, AnalysisUtils.roundThreeDecimals(bestFitValues.get(row)).toString(), bodyFont);
            if (standardErrors.get(row) == 0.0) {
                PdfUtils.addCustomizedCell(dataTable, "--", bodyFont);
                PdfUtils.addCustomizedCell(dataTable, "--", bodyFont);
            } else {
                PdfUtils.addCustomizedCell(dataTable, AnalysisUtils.roundThreeDecimals(standardErrors.get(row)).toString(), bodyFont);
                PdfUtils.addCustomizedCell(dataTable, AnalysisUtils.roundThreeDecimals(cIBoundaries.get(row * 2)) + " to " + AnalysisUtils.roundThreeDecimals(cIBoundaries.get((row * 2) + 1)), bodyFont);
            }

        }
        //add EC50 information
        PdfUtils.addCustomizedCell(dataTable, "EC50", bodyFont);
        PdfUtils.addCustomizedCell(dataTable, df.format(statistics.getEc50()), bodyFont);
        PdfUtils.addCustomizedCell(dataTable, "--", bodyFont);
        PdfUtils.addCustomizedCell(dataTable, df.format(statistics.getcIEC50()[0]) + " to " + df.format(statistics.getcIEC50()[1]), bodyFont);
        return dataTable;
    }

    private List<Double> addArrayToList(List<Double> list, double[] array) {
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

    /**
     * Create Image from a aJFreeChart and add it to document
     *
     * @param chart
     */
    private void addImageFromChart(JFreeChart chart, int imageWidth, int imageHeight) {
        Image imageFromChart = PdfUtils.getImageFromJFreeChart(writer, chart, imageWidth, imageHeight);
        // put image in the center
        imageFromChart.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(imageFromChart);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
