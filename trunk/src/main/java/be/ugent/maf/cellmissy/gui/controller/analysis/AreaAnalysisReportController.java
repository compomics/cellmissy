/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis;

import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrectionFactory.CorrectionMethod;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.view.table.models.PValuesTableModel;
import be.ugent.maf.cellmissy.gui.view.table.models.StatisticalSummaryTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.PdfUtils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableList;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
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
    private static Font bodyFont = new Font(Font.TIMES_ROMAN, 8);
    private static Font titleFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    private static int chartWidth = 500;
    private static int chartHeight = 450;
    private static int rectChartWidth = 500;
    private static int rectChartHeigth = 300;
    //view
    //parent controller
    @Autowired
    private AreaAnalysisController areaAnalysisController;
    //child controllers
    //services

    /**
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
            areaAnalysisController.showMessage("Please use .pdf extension for the file.", JOptionPane.WARNING_MESSAGE);
            // retry to create pdf file
            try {
                areaAnalysisController.createPdfReport();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return pdfFile;
    }

    /**
     *
     * @param pdfFile
     */
    private void tryToCreateFile(File pdfFile) {
        try {
            boolean success = pdfFile.createNewFile();
            if (success) {
                areaAnalysisController.showMessage("Pdf Report successfully created!", JOptionPane.INFORMATION_MESSAGE);
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
            areaAnalysisController.showMessage("Unexpected error: " + ex.getMessage() + ".", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            // actually create PDF file
            createPdfFile(fileOutputStream);
        } catch (IOException ex) {
            areaAnalysisController.showMessage("Unexpected error: " + ex.getMessage() + ".", JOptionPane.ERROR_MESSAGE);
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
     * Add content to document
     */
    private void addContent() {
        addOverview();
        PdfUtils.addEmptyLines(document, 1);
        addConditionsInfoTable();
        PdfUtils.addEmptyLines(document, 2);
        addGlobalAreaChart();
        PdfUtils.addEmptyLines(document, 2);
        addGlobalVelocityChart();
        PdfUtils.addEmptyLines(document, 2);
        addParagraphPerAnalysis();
    }

    /**
     * Overview of Report experiment and project numbers + number of conditions
     */
    private void addOverview() {
        String title = "Analysis Report of Experiment " + experiment.getExperimentNumber() + " - " + "Project " + experiment.getProject().getProjectNumber();
        PdfUtils.addTitle(document, title, titleFont);
        PdfUtils.addEmptyLines(document, 1);
        //lines to be printed
        List<String> lines = new ArrayList<>();
        String line = "Number of conditions: " + experiment.getPlateConditionCollection().size();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        PdfUtils.addEmptyLines(document, 1);
        // add conditions info
        lines.clear();
        List<PlateCondition> plateConditonsList = new ArrayList<>(experiment.getPlateConditionCollection());
        for (PlateCondition plateCondition : plateConditonsList) {
            lines.add("Condition " + (plateConditonsList.indexOf(plateCondition) + 1) + ": " + plateCondition.toString());
        }
        PdfUtils.addText(document, lines, true, Element.ALIGN_JUSTIFIED, bodyFont);
    }

    /**
     * Add image with global area chart
     */
    private void addGlobalAreaChart() {
        List<PlateCondition> plateConditonsList = new ArrayList<>(experiment.getPlateConditionCollection());
        // create chart (for all conditions, no error bars on top)
        JFreeChart globalAreaChart = areaAnalysisController.createGlobalAreaChart(plateConditonsList, false);
        // add chart as image
        addImageFromChart(globalAreaChart, chartWidth, chartHeight);
    }

    /**
     * Add image with velocity chart Velocity Chart is created will all the conditions
     */
    private void addGlobalVelocityChart() {
        List<PlateCondition> plateConditonsList = new ArrayList<>(experiment.getPlateConditionCollection());
        int[] conditionsToShow = new int[plateConditonsList.size()];
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
     * Use a PdfTable with info on each condition
     */
    private PdfPTable createConditionsInfoTable() {
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaAnalysisController.getPreProcessingMap();
        List<PlateCondition> plateConditionList = new ArrayList<>(experiment.getPlateConditionCollection());
        // new table with 5 columns
        PdfPTable dataTable = new PdfPTable(5);
        PdfUtils.setUpPdfPTable(dataTable);
        // add 1st row: column names
        PdfUtils.addCustomizedCell(dataTable, "Conditions", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "# Replicates", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "User Selected Replicates", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Time Interval", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Proposed cut off", titleFont);

        // for each condition get results and add a cell
        for (PlateCondition plateCondition : plateConditionList) {
            AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
            PdfUtils.addCustomizedCell(dataTable, "Condition " + (plateConditionList.indexOf(plateCondition) + 1), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, "" + findNumberOfReplicates(areaPreProcessingResults), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, areaPreProcessingResults.isUserSelectedReplicates() ? "Yes" : "No", bodyFont);
            PdfUtils.addCustomizedCell(dataTable, areaPreProcessingResults.getTimeInterval().toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, "" + areaPreProcessingResults.getTimeInterval().getProposedCutOff(), bodyFont);
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
        PdfUtils.addCustomizedCell(dataTable, "Condition", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Max", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Min", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Mean", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "N", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "SD", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Variance", titleFont);
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
        PdfPTable dataTable = new PdfPTable(plateConditions.size() + 1);
        PdfUtils.setUpPdfPTable(dataTable);
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaAnalysisController.getPreProcessingMap();
        List<PlateCondition> plateConditionList = new ArrayList<>(preProcessingMap.keySet());
        // add 1st row
        PdfUtils.addCustomizedCell(dataTable, " ", titleFont);
        for (int i = 0; i < plateConditions.size(); i++) {
            PdfUtils.addCustomizedCell(dataTable, "Cond " + (plateConditionList.indexOf(plateConditions.get(i)) + 1), titleFont);
        }
        // table with p values for analysis group
        JTable table = new JTable(new PValuesTableModel(analysisGroup, plateConditionList, isAdjusted));
        copyDataFromJTable(dataTable, table);
        return dataTable;
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
                    // if value is null, show a dash in the table
                    PdfUtils.addCustomizedCell(dataTable, "-", bodyFont);
                }
            }
        }
    }

    /**
     * Add table with info per each condition
     */
    private void addConditionsInfoTable() {
        //add title before the table
        PdfUtils.addTitle(document, "Conditions Summary", titleFont);
        PdfPTable conditionsInfoTable = createConditionsInfoTable();
        addTable(conditionsInfoTable);
    }

    /**
     * Add table with statistical summary for each analysis group
     *
     * @param analysisGroup
     */
    private void addSummaryStatisticsTable(AnalysisGroup analysisGroup) {
        //add title before the table
        PdfUtils.addTitle(document, "SUMMARY STATISTICS", titleFont);
        PdfUtils.addEmptyLines(document, 1);
        PdfPTable statisticalSummaryTable = createStatisticalSummaryTable(analysisGroup);
        addTable(statisticalSummaryTable);
    }

    /**
     * Add table with p-values if isAdjusted is false, not corrected P values are shown
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
     * Add info for each analysis group
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
            PdfUtils.addTitle(document, "PAIRWISE COMPARISONS", titleFont);
            PdfUtils.addTitle(document, "Multiple comparisons correction: NONE", titleFont);
            // add not corrected p values
            addPValuesTable(analysisGroup, false);
            // if a correction method was chosen for the analysis group, choose also corrected values
            if (analysisGroup.getCorrectionMethod() != CorrectionMethod.NONE) {
                PdfUtils.addEmptyLines(document, 1);
                PdfUtils.addTitle(document, "Multiple comparisons correction: " + analysisGroup.getCorrectionMethod(), titleFont);
                // add corrected p values
                addPValuesTable(analysisGroup, true);
            }
        }
    }
}
