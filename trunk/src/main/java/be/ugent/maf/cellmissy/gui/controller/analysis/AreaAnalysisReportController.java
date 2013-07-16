/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis;

import be.ugent.maf.cellmissy.analysis.MeasuredAreaType;
import be.ugent.maf.cellmissy.entity.result.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.AreaAnalysisResults;
import be.ugent.maf.cellmissy.gui.experiment.analysis.CustomizeReportDialog;
import be.ugent.maf.cellmissy.gui.plate.PdfPlatePanel;
import be.ugent.maf.cellmissy.gui.view.table.model.ConditionsCheckBoxesTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.PValuesTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.StatisticalSummaryTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.PdfUtils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableList;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for area analysis PDF report.
 *
 * @author Paola Masuzzo
 */
@Controller("areaAnalysisReportController")
public class AreaAnalysisReportController {

    private static final Logger LOG = Logger.getLogger(AreaAnalysisReportController.class);
    //model
    private Experiment experiment;
    private Document document;
    private PdfWriter writer;
    private boolean useCorrectedData;
    private static Font bodyFont = new Font(Font.HELVETICA, 8);
    private static Font boldFont = new Font(Font.HELVETICA, 8, Font.BOLD);
    private static Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static int chartWidth = 500;
    private static int chartHeight = 450;
    private static int rectChartWidth = 500;
    private static int rectChartHeigth = 300;
    //view
    private CustomizeReportDialog customizeReportDialog;
    //parent controller
    @Autowired
    private AreaAnalysisController areaAnalysisController;
    //child controllers
    //services

    /**
     * Initialize controller
     */
    public void init() {
        // init customize report dialog
        initCustomizeReportDialog();
    }

    /**
     * getters and setters
     *
     * @param useCorrectedData
     */
    public void setUseCorrectedData(boolean useCorrectedData) {
        this.useCorrectedData = useCorrectedData;
    }

    /**
     * Show the dialog.
     */
    public void showCustomizeReportDialog() {
        List<PlateCondition> processedConditions = areaAnalysisController.getProcessedConditions();
        ConditionsCheckBoxesTableModel conditionsCheckBoxesTableModel = new ConditionsCheckBoxesTableModel(processedConditions);
        conditionsCheckBoxesTableModel.setCheckboxes(new boolean[processedConditions.size()]);
        customizeReportDialog.getConditionsCheckBoxesTable().setModel(conditionsCheckBoxesTableModel);
        customizeReportDialog.pack();
        GuiUtils.centerDialogOnFrame(areaAnalysisController.getCellMissyFrame(), customizeReportDialog);
        customizeReportDialog.setVisible(true);
    }

    /**
     * Create the PDF report file.
     *
     * @param directory
     * @param reportName
     * @return the file created
     */
    public File createAnalysisReport(File directory, String reportName) {
        this.experiment = areaAnalysisController.getExperiment();
        File pdfFile = new File(directory, reportName);
        if (reportName.endsWith(".pdf")) {
            tryToCreateFile(pdfFile);
        } else {
            areaAnalysisController.showMessage("Please use .pdf extension for the file.", "extension file problem", JOptionPane.WARNING_MESSAGE);
            // retry to create pdf file
            try {
                areaAnalysisController.createPdfReport();
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                areaAnalysisController.showMessage("An error occurred: " + ex.getMessage(), "unexpected error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return pdfFile;
    }

    /**
     *
     */
    private void initCustomizeReportDialog() {
        // create a new dialog
        customizeReportDialog = new CustomizeReportDialog(areaAnalysisController.getCellMissyFrame(), true);
        /**
         * Add action listeners.
         */
        // create a new report with the selected options
        customizeReportDialog.getCreateReportButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // create the PDF report file
                    areaAnalysisController.createPdfReport();
                    // close the dialog
                    customizeReportDialog.setVisible(false);
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        });
    }

    /**
     *
     * @param pdfFile
     */
    private void tryToCreateFile(File pdfFile) {
        try {
            boolean success = pdfFile.createNewFile();
            if (success) {
                areaAnalysisController.showMessage("Pdf Report successfully created!", "Report created", JOptionPane.INFORMATION_MESSAGE);
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
            areaAnalysisController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            // actually create PDF file
            createPdfFile(fileOutputStream);
        } catch (IOException ex) {
            areaAnalysisController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *
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
        // overview: title, dataset, imaging type, brief summary os biological conditions
        addOverview();
        // plate view with the experimental set-up
        addPlatePanel();
        // table with info from biological conditions
        addConditionsInfoTable();
        // go to new page
        document.newPage();
        // global area chart: lines with SEM
        addGlobalAreaChart(false);
        PdfUtils.addEmptyLines(document, 2);
        // global area chart: points and lines with SEM
        addGlobalAreaChart(true);
//        PdfUtils.addEmptyLines(document, 2);
        // go to new page
        document.newPage();
        // linear regression table
        addLinearRegressionTable();
        // velocity bar chart
        addGlobalVelocityChart();
//        PdfUtils.addEmptyLines(document, 2);
        // go to new page
        document.newPage();
        // analysis statistics paragraph
        addParagraphPerAnalysis();
    }

    /**
     * Add plate view.
     */
    private void addPlatePanel() {
        PdfPlatePanel pdfPlatePanel = createPanelView();
        addImageFromJPanel(pdfPlatePanel, pdfPlatePanel.getWidth(), pdfPlatePanel.getHeight());
    }

    /**
     * Create panel view in the PDF file
     *
     * @return
     */
    private PdfPlatePanel createPanelView() {
        // what we need to show is actually an analysis plate panel
        PdfPlatePanel pdfPlatePanel = new PdfPlatePanel();
        pdfPlatePanel.initPanel(experiment.getPlateFormat(), new Dimension(400, 500));
        pdfPlatePanel.setExperiment(experiment);
        return pdfPlatePanel;
    }

    /**
     * Create Image from a aJFreeChart and add it to document
     *
     * @param chart
     */
    private void addImageFromJPanel(JPanel panel, int imageWidth, int imageHeight) {
        Image imageFromJPanel = PdfUtils.getImageFromJPanel(writer, panel, imageWidth, imageHeight);
        // put image in the center
        imageFromJPanel.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(imageFromJPanel);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Overview of Report: experiment and project numbers + some details.
     */
    private void addOverview() {
        String title = "CellMissy - ANALYSIS REPORT - EXPERIMENT " + experiment + " - " + "PROJECT " + experiment.getProject();
        PdfUtils.addTitle(document, title, titleFont);
        PdfUtils.addEmptyLines(document, 1);
        // add information on dataset (algorithm) and imaging type analyzed
        List<String> lines = new ArrayList<>();
        String line = "DATASET: " + areaAnalysisController.getSelectedALgorithm();
        lines.add(line);
        line = "IMAGING TYPE: " + areaAnalysisController.getSelectedImagingType();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        // add conditions number
        lines.clear();
        line = "NUMBER OF BIOLOGICAL CONDITIONS: " + experiment.getPlateConditionList().size();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        // add conditions info
        lines.clear();
        List<PlateCondition> plateConditonsList = experiment.getPlateConditionList();
        for (PlateCondition plateCondition : plateConditonsList) {
            lines.add("Condition " + (plateConditonsList.indexOf(plateCondition) + 1) + ": " + plateCondition.toString());
        }
        PdfUtils.addText(document, lines, true, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        // add extra info
        lines.clear();
        line = "MEASUREAD AREA TYPE: " + areaAnalysisController.getMeasuredAreaType().getStringForType();
        lines.add(line);
        String correctedData = useCorrectedData ? "Y" : "N";
        line = "DATA CORRECTED FOR OUTLIERS? " + correctedData;
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
    }

    /**
     * Add table with info per each condition.
     */
    private void addConditionsInfoTable() {
        //add title before the table
        PdfUtils.addTitle(document, "CONDITIONS SUMMARY", boldFont);
        PdfPTable conditionsInfoTable = createConditionsInfoTable();
        addTable(conditionsInfoTable);
    }

    /**
     * Add global area chart
     *
     * @param plotPoints
     */
    private void addGlobalAreaChart(boolean plotPoints) {
        List<PlateCondition> plateConditonsList = experiment.getPlateConditionList();
        MeasuredAreaType measuredAreaType = areaAnalysisController.getMeasuredAreaType();
        // create chart (for all conditions, error bars on top, both lines and points)
        JFreeChart globalAreaChart = areaAnalysisController.createGlobalAreaChart(plateConditonsList, useCorrectedData, true, true, plotPoints, measuredAreaType);
        // add chart as image
        addImageFromChart(globalAreaChart, chartWidth, chartHeight);
    }

    /**
     * Add a linear regression table to the document
     */
    private void addLinearRegressionTable() {
        PdfUtils.addTitle(document, "LINEAR REGRESSION MODEL: SLOPE + R²", boldFont);
        PdfPTable linearRegressionTable = createLinearRegressionTable();
        addTable(linearRegressionTable);
    }

    /**
     * Create Linear Regression Table.
     */
    private PdfPTable createLinearRegressionTable() {
        Map<PlateCondition, AreaAnalysisResults> analysisMap = areaAnalysisController.getAnalysisMap();
        List<Double[]> slopesList = new ArrayList();
        List<Double[]> coefficientsList = new ArrayList();
        List<Double> meanSlopesList = new ArrayList();
        List<Double> madSlopesList = new ArrayList();
        List<PlateCondition> processedConditions = areaAnalysisController.getProcessedConditions();
        // go through all conditions in map and estimate linear model for each of them
        for (PlateCondition plateCondition : processedConditions) {
            slopesList.add((analysisMap.get(plateCondition).getSlopes()));
            coefficientsList.add((analysisMap.get(plateCondition).getGoodnessOfFit()));
            meanSlopesList.add(analysisMap.get(plateCondition).getMeanSlope());
            madSlopesList.add(analysisMap.get(plateCondition).getMadSlope());
        }
        // data for table model: number of rows equal to number of conditions, number of columns equal to maximum number of replicates + 3
        // first column with conditions, last two with mean and mad values
        int maximumNumberOfReplicates = AnalysisUtils.getMaximumNumberOfReplicates(processedConditions);
        Object[][] data = new Object[processedConditions.size()][maximumNumberOfReplicates + 3];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 1; columnIndex < slopesList.get(rowIndex).length + 1; columnIndex++) {
                Double slope = slopesList.get(rowIndex)[columnIndex - 1];
                Double coefficient = coefficientsList.get(rowIndex)[columnIndex - 1];
                if (slope != null && coefficient != null && !slope.isNaN() && !coefficient.isNaN()) {
                    // round to three decimals slopes and coefficients
                    slope = AnalysisUtils.roundTwoDecimals(slopesList.get(rowIndex)[columnIndex - 1]);
                    coefficient = AnalysisUtils.roundTwoDecimals(coefficientsList.get(rowIndex)[columnIndex - 1]);
                    // show in table slope + (coefficient)
                    data[rowIndex][columnIndex] = slope + " (" + coefficient + ")";
                } else if (slope == null && coefficient == null) {
                    data[rowIndex][columnIndex] = "excluded";
                } else if (slope.isNaN() || coefficient.isNaN()) {
                    data[rowIndex][columnIndex] = "NaN";
                }
            }
            // first column contains conditions names
            data[rowIndex][0] = rowIndex + 1;
            // second column will show the icons with the colors
            // last 2 columns contain mean slopes, mad values
            data[rowIndex][data[0].length - 2] = meanSlopesList.get(rowIndex);
            data[rowIndex][data[0].length - 1] = madSlopesList.get(rowIndex);
        }
        // array of column names for table model
        String[] columnNames = new String[maximumNumberOfReplicates + 3];
        columnNames[0] = "Condition";
        for (int i = 1; i < columnNames.length - 2; i++) {
            columnNames[i] = "Repl " + i;
        }
        columnNames[columnNames.length - 2] = "median";
        columnNames[columnNames.length - 1] = "MAD";
        JTable table = new JTable(data, columnNames);

        PdfPTable linearRegressionTable = new PdfPTable(columnNames.length);
        PdfUtils.setUpPdfPTable(linearRegressionTable);
        // add 1st row: column names
        for (int i = 0; i < columnNames.length; i++) {
            PdfUtils.addCustomizedCell(linearRegressionTable, columnNames[i], boldFont);
        }
        copyDataFromJTable(linearRegressionTable, table);
        return linearRegressionTable;
    }

    /**
     * Add image with velocity chart Velocity Chart is created will all the
     * conditions
     */
    private void addGlobalVelocityChart() {
        List<PlateCondition> processedConditions = areaAnalysisController.getProcessedConditions();
        int[] conditionsToShow = new int[processedConditions.size()];
        for (int i = 0; i < conditionsToShow.length; i++) {
            conditionsToShow[i] = i;
        }
        // create chart
        JFreeChart velocityChart = areaAnalysisController.createVelocityChart(conditionsToShow);
        // add chart as image
        addImageFromChart(velocityChart, rectChartWidth, rectChartHeigth);
    }

    /**
     * Create Image from a aJFreeChart and add it to document
     *
     * @param chart
     */
    private void addImageFromChart(JFreeChart chart, int imageWidth, int imageHeight) {
        Image imageFromChart = PdfUtils.getImageFromChart(writer, chart, imageWidth, imageHeight);
        // put image in the center
        imageFromChart.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(imageFromChart);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * add a paragraph for each analysis group
     */
    private void addParagraphPerAnalysis() {
        // add main title for section
        PdfUtils.addTitle(document, "ANALYSIS GROUPS", titleFont);
        ObservableList<AnalysisGroup> groupsList = areaAnalysisController.getGroupsBindingList();
        for (int i = 0; i < groupsList.size(); i++) {
            Paragraph paragraph = new Paragraph("Analysis group: " + groupsList.get(i).getGroupName());
            try {
                document.add(paragraph);
                addAnalysisInfo(groupsList.get(i));
                PdfUtils.addEmptyLines(document, 2);
            } catch (DocumentException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Use a PdfTable with info on each condition;
     */
    private PdfPTable createConditionsInfoTable() {
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaAnalysisController.getPreProcessingMap();
        List<PlateCondition> plateConditionList = experiment.getPlateConditionList();
        // new table with 6 columns
        PdfPTable dataTable = new PdfPTable(6);
        PdfUtils.setUpPdfPTable(dataTable);
        // add 1st row: column names
        PdfUtils.addCustomizedCell(dataTable, "CONDITIONS", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "# TECHNICAL REPLICATES", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "TECHNICAL REPLICATES EXCLUDED?", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "USER SELECTED TIME INTERVAL", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "MAX. TIME POINT", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "ANALYZED TIME INTERVAL", boldFont);

        // for each condition get results and add a cell
        for (PlateCondition plateCondition : plateConditionList) {
            AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
            // condition index
            PdfUtils.addCustomizedCell(dataTable, "Condition " + (plateConditionList.indexOf(plateCondition) + 1), bodyFont);
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
     * Get statistical summary table
     *
     * @param analysisGroup
     * @return Table
     */
    private PdfPTable createStatisticalSummaryTable(AnalysisGroup analysisGroup) {
        // 7 columns
        PdfPTable dataTable = new PdfPTable(7);
        PdfUtils.setUpPdfPTable(dataTable);
        // add 1st row: column names
        PdfUtils.addCustomizedCell(dataTable, "Condition", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Max", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Min", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Mean", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "N", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "SD", boldFont);
        PdfUtils.addCustomizedCell(dataTable, "Variance", boldFont);
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaAnalysisController.getPreProcessingMap();
        List<PlateCondition> plateConditions = new ArrayList<>(preProcessingMap.keySet());
        // table with statistical summary for analysis group
        JTable table = new JTable(new StatisticalSummaryTableModel(analysisGroup, plateConditions));
        copyDataFromJTable(dataTable, table);
        return dataTable;
    }

    /**
     * Get P values table
     *
     * @param analysisGroup
     * @param isAdjusted
     * @return
     */
    private PdfPTable createPValuesTable(AnalysisGroup analysisGroup, boolean isAdjusted) {
        // list of conditions that have been compared
        List<PlateCondition> plateConditions = analysisGroup.getPlateConditions();
        PdfPTable pValuesTable = new PdfPTable(plateConditions.size() + 1);
        PdfUtils.setUpPdfPTable(pValuesTable);
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaAnalysisController.getPreProcessingMap();
        List<PlateCondition> plateConditionList = new ArrayList<>(preProcessingMap.keySet());
        // add 1st row
        PdfUtils.addCustomizedCell(pValuesTable, " ", boldFont);
        for (int i = 0; i < plateConditions.size(); i++) {
            PdfUtils.addCustomizedCell(pValuesTable, "Cond " + (plateConditionList.indexOf(plateConditions.get(i)) + 1), bodyFont);
        }
        // table with p values for analysis group
        JTable table = new JTable(new PValuesTableModel(analysisGroup, plateConditionList, isAdjusted));
        copyDataFromJTable(pValuesTable, table);
        return pValuesTable;
    }

    /**
     * Copy data from a JTable to a PdfPTable
     *
     * @param dataTable
     * @param table
     */
    private void copyDataFromJTable(PdfPTable dataTable, JTable table) {
        for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                Object valueAt = table.getModel().getValueAt(rowIndex, columnIndex);
                if (valueAt != null) {
                    // if value is a Double, get primitive value of it
                    if (valueAt.getClass().equals(Double.class)) {
                        String valueString = valueAt.toString();
                        double doubleValue = Double.valueOf(valueString).doubleValue();
                        Double roundedValue = AnalysisUtils.roundThreeDecimals(doubleValue);
                        PdfUtils.addCustomizedCell(dataTable, "" + roundedValue, bodyFont);
                    } else {
                        // if value is a string
                        PdfUtils.addCustomizedCell(dataTable, "" + valueAt, bodyFont);
                    }
                } else {
                    // if value is null, simply show a dash in the table
                    PdfUtils.addCustomizedCell(dataTable, "-", bodyFont);
                }
            }
        }
    }

    /**
     * Add table with statistical summary for each analysis group
     *
     * @param analysisGroup
     */
    private void addSummaryStatisticsTable(AnalysisGroup analysisGroup) {
        //add title before the table
        PdfUtils.addTitle(document, "SUMMARY STATISTICS", boldFont);
        PdfUtils.addEmptyLines(document, 1);
        PdfPTable statisticalSummaryTable = createStatisticalSummaryTable(analysisGroup);
        addTable(statisticalSummaryTable);
    }

    /**
     * Add table with p-values if isAdjusted is false, not corrected P values
     * are shown
     *
     * @param analysisGroup
     * @param isAdjusted
     */
    private void addPValuesTable(AnalysisGroup analysisGroup, boolean isAdjusted) {
        PdfPTable pValuesTable = createPValuesTable(analysisGroup, isAdjusted);
        addTable(pValuesTable);
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

    /*
     * Find number of Replicates used for each condition
     */
    private int findNumberOfReplicates(AreaPreProcessingResults areaPreProcessingResults) {
        // number of replicates info
        int numberOfReplicates = 0;
        boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
        for (int i = 0; i < excludeReplicates.length; i++) {
            if (!excludeReplicates[i]) {
                numberOfReplicates++;
            }
        }
        return numberOfReplicates;
    }

    /**
     *
     * @param areaPreProcessingResults
     * @return
     */
    private List<Well> getExcludedWells(PlateCondition plateCondition) {
        List<Well> excludedWells = new ArrayList<>();
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaAnalysisController.getPreProcessingMap();
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
        for (int i = 0; i < excludeReplicates.length; i++) {
            if (excludeReplicates[i]) {
                excludedWells.add(plateCondition.getWellList().get(i));
            }
        }
        return excludedWells;
    }

    /**
     * Add info for each analysis group
     *
     * @param analysisGroup
     */
    private void addAnalysisInfo(AnalysisGroup analysisGroup) {
        List<String> lines = new ArrayList<>();
        String line = "Number of conditions: " + analysisGroup.getPlateConditions().size();
        lines.add(line);
        PdfUtils.addText(document, lines, true, Element.ALIGN_JUSTIFIED, bodyFont);
        // check if the group was actually analyzed or not
        if (analysisGroup.getpValuesMatrix() != null) {
            PdfUtils.addEmptyLines(document, 1);
            // add statistical summary table
            addSummaryStatisticsTable(analysisGroup);
            PdfUtils.addEmptyLines(document, 1);
            String statisticalTestName = areaAnalysisController.getStatisticsDialog().getStatisticalTestComboBox().getSelectedItem().toString();
            PdfUtils.addTitle(document, "PAIRWISE COMPARISONS - " + statisticalTestName + " - (p-values)", boldFont);
            PdfUtils.addTitle(document, "Multiple comparisons correction: none", boldFont);
            // add not corrected p values
            addPValuesTable(analysisGroup, false);
            // if a correction method was chosen for the analysis group, choose also corrected values
            if (!analysisGroup.getCorrectionMethodName().equals("none")) {
                PdfUtils.addEmptyLines(document, 1);
                PdfUtils.addTitle(document, "Multiple comparisons correction: " + analysisGroup.getCorrectionMethodName(), boldFont);
                // add corrected p values
                addPValuesTable(analysisGroup, true);
            }
        }
    }
}
