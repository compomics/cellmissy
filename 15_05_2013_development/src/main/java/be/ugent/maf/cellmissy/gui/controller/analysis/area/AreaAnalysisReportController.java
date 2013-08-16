/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area;

import be.ugent.maf.cellmissy.analysis.MeasuredAreaType;
import be.ugent.maf.cellmissy.entity.result.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.AreaAnalysisResults;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.CustomizeReportDialog;
import be.ugent.maf.cellmissy.gui.plate.PdfPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.CheckBoxConditionsRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.CheckBoxesConditionsTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.CheckBoxesGlobalViewsTableModel;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
    private Map<PlateCondition, Boolean> conditionsToPlotMap;
    private Map<String, Boolean[]> globalViewsMap;
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
     * Action performed on cancel button: all we have to do is to reset the maps for the conditions to plot and
     */
    public void resetOnCancel() {
        // reset maps
        conditionsToPlotMap = null;
        globalViewsMap = null;
    }

    /**
     * getters and setters
     *
     * @param useCorrectedData
     */
    public void setUseCorrectedData(boolean useCorrectedData) {
        this.useCorrectedData = useCorrectedData;
    }

    public Map<PlateCondition, Boolean> getConditionsToPlotMap() {
        return conditionsToPlotMap;
    }

    public Map<String, Boolean[]> getGlobalViewsMap() {
        return globalViewsMap;
    }

    /**
     * In the conditions map we first put all the processed conditions and a
     * False for each of this condition: this means that by default no
     * conditions plots are added in the REPORT. The user can change this
     * behaviour just selecting or deselecting the relative checkBoxes.
     */
    public void initConditionsToPlotMap() {
        conditionsToPlotMap = new LinkedHashMap<>();
        for (PlateCondition plateCondition : areaAnalysisController.getProcessedConditions()) {
            conditionsToPlotMap.put(plateCondition, Boolean.FALSE);
        }
    }

    /**
     * In the Global Views map we first put a default global view, where we put
     * to TRUE the three options for the plot: points, SEM and time interval.
     */
    public void initGlobalViewsMap() {
        globalViewsMap = new LinkedHashMap<>();
        String firstGlobalView = "Global View 1";
        Boolean[] defaultOptions = new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
        globalViewsMap.put(firstGlobalView, defaultOptions);
    }

    /**
     * Show the dialog.
     */
    public void showCustomizeReportDialog() {
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = customizeReportDialog.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        customizeReportDialog.getInfoTextPane().setCaretPosition(0);
        // conditions to plot table
        CheckBoxesConditionsTableModel checkBoxesConditionsTableModel = new CheckBoxesConditionsTableModel(conditionsToPlotMap);
        customizeReportDialog.getConditionsCheckBoxesTable().setModel(checkBoxesConditionsTableModel);
        // set cell renderer and cell editor
        customizeReportDialog.getConditionsCheckBoxesTable().getColumnModel().getColumn(1).setCellRenderer(new CheckBoxConditionsRenderer());
        customizeReportDialog.getConditionsCheckBoxesTable().getColumnModel().getColumn(1).setCellEditor(new CheckBoxConditionsCellEditor(checkBoxesConditionsTableModel));
        // pack columns of table
        for (int i = 0; i < customizeReportDialog.getConditionsCheckBoxesTable().getColumnCount(); i++) {
            GuiUtils.packColumn(customizeReportDialog.getConditionsCheckBoxesTable(), i, 1);
        }
        // global views table
        CheckBoxesGlobalViewsTableModel checkBoxesGlobalViewsTableModel = new CheckBoxesGlobalViewsTableModel(globalViewsMap);
        customizeReportDialog.getGlobalViewsTable().setModel(checkBoxesGlobalViewsTableModel);
        CheckBoxConditionsRenderer checkBoxConditionsRenderer = new CheckBoxConditionsRenderer();
        CheckBoxGlobalViewsCellEditor checkBoxGlobalViewsCellEditor = new CheckBoxGlobalViewsCellEditor(checkBoxesGlobalViewsTableModel);
        // set cell renderer and cell editor
        for (int i = 1; i < customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumnCount(); i++) {
            customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumn(i).setCellRenderer(checkBoxConditionsRenderer);
            customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumn(i).setCellEditor(checkBoxGlobalViewsCellEditor);
        }
        // pack columns of table
        for (int i = 0; i < customizeReportDialog.getGlobalViewsTable().getColumnCount(); i++) {
            GuiUtils.packColumn(customizeReportDialog.getGlobalViewsTable(), i, 1);
        }
        // pack the dialog, center it on screen and show it
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
     * Initialize the dialog.
     */
    private void initCustomizeReportDialog() {
        // create a new dialog
        customizeReportDialog = new CustomizeReportDialog(areaAnalysisController.getCellMissyFrame(), true);
        // customize tables
        customizeReportDialog.getConditionsCheckBoxesTable().getTableHeader().setReorderingAllowed(false);
        customizeReportDialog.getConditionsCheckBoxesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        customizeReportDialog.getGlobalViewsTable().getTableHeader().setReorderingAllowed(false);
        customizeReportDialog.getGlobalViewsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        customizeReportDialog.getGlobalViewsTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        /**
         * Add action listeners.
         */
        // add a new global view to the report
        customizeReportDialog.getAddGlobalViewButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int actualSizeOfGloBalViews = globalViewsMap.size();
                String newGlobalView = "Global View " + (actualSizeOfGloBalViews + 1);
                Boolean[] options = new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
                globalViewsMap.put(newGlobalView, options);
                // refresh table model
                CheckBoxesGlobalViewsTableModel checkBoxesGlobalViewsTableModel = new CheckBoxesGlobalViewsTableModel(globalViewsMap);
                customizeReportDialog.getGlobalViewsTable().setModel(checkBoxesGlobalViewsTableModel);
                CheckBoxConditionsRenderer checkBoxConditionsRenderer = new CheckBoxConditionsRenderer();
                CheckBoxGlobalViewsCellEditor checkBoxGlobalViewsCellEditor = new CheckBoxGlobalViewsCellEditor(checkBoxesGlobalViewsTableModel);
                // set cell renderer and cell editor
                for (int i = 1; i < customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumnCount(); i++) {
                    customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumn(i).setCellRenderer(checkBoxConditionsRenderer);
                    customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumn(i).setCellEditor(checkBoxGlobalViewsCellEditor);
                }
            }
        });

        // delete a certain global view (or more) from the report customization
        customizeReportDialog.getRemoveGlobalViewButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the selected rows in the table
                int[] selectedRows = customizeReportDialog.getGlobalViewsTable().getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    int indexToRemove = selectedRows[i] + 1;
                    globalViewsMap.remove("Global View " + indexToRemove);
                }
                // refresh table model
                CheckBoxesGlobalViewsTableModel checkBoxesGlobalViewsTableModel = new CheckBoxesGlobalViewsTableModel(globalViewsMap);
                customizeReportDialog.getGlobalViewsTable().setModel(checkBoxesGlobalViewsTableModel);
                CheckBoxConditionsRenderer checkBoxConditionsRenderer = new CheckBoxConditionsRenderer();
                CheckBoxGlobalViewsCellEditor checkBoxGlobalViewsCellEditor = new CheckBoxGlobalViewsCellEditor(checkBoxesGlobalViewsTableModel);
                // set cell renderer and cell editor
                for (int i = 1; i < customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumnCount(); i++) {
                    customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumn(i).setCellRenderer(checkBoxConditionsRenderer);
                    customizeReportDialog.getGlobalViewsTable().getColumnModel().getColumn(i).setCellEditor(checkBoxGlobalViewsCellEditor);
                }
            }
        });

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
        // overview: title, dataset, imaging type, brief summary of the experiment biological conditions
        addOverview();
        // plate view with the experimental set-up
        addPlatePanel();
        // table with info from biological conditions
        addConditionsInfoTable();
        // we go here to a new page
        document.newPage();
        // we check for the user selection: if conditions plots need to be added, we added them here
        addConditionsCharts();
        // then, we move to next page
        document.newPage();
        // we move then to the global views
        addGlobalViews();
        // go to new page
        document.newPage();
        // linear regression table
        addLinearRegressionTable();
        // velocity bar chart
        addGlobalVelocityChart();
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
     * We go through the map, we check for the Boolean, if it's true, we need to
     * add 2 more charts for each conditions: one with the raw area data and one
     * with the corrected area data.
     */
    private void addConditionsCharts() {
        for (PlateCondition plateCondition : conditionsToPlotMap.keySet()) {
            // if the user has decided to put the conditions in the report, this Boolean is TRUES
            if (conditionsToPlotMap.get(plateCondition) == Boolean.TRUE) {
                // add first a title with the description of the condition
                PdfUtils.addTitle(document, plateCondition + " - raw (normalized) and corrected (normalized) area plots", titleFont);
                // create the raw area chart and add the image to the document
                JFreeChart rawChart = areaAnalysisController.createRawAreaChart(plateCondition);
                addImageFromChart(rawChart, 500, 350);
                // create the corrected area chart and add the image to the document
                JFreeChart correctedChart = areaAnalysisController.createCorrectedAreaChart(plateCondition);
                addImageFromChart(correctedChart, 500, 350);
                // we go to a new page before we add next plot
                document.newPage();
            }
        }
    }

    /**
     * We look at the table in the dialog, and we count the total number of
     * global views that need to be added to the PDF report. For each global
     * view, we get the selected options for the plot and we generate the
     * correspondent plot for the global area.
     */
    private void addGlobalViews() {
        // for each row, we need one more global view
        for (int i = 0; i < customizeReportDialog.getGlobalViewsTable().getRowCount(); i++) {
            // we add first a title for the global view
            String globalView = (String) customizeReportDialog.getGlobalViewsTable().getValueAt(i, 0);
            PdfUtils.addTitle(document, globalView, titleFont);
            Boolean points = (Boolean) customizeReportDialog.getGlobalViewsTable().getValueAt(i, 1);
            Boolean sem = (Boolean) customizeReportDialog.getGlobalViewsTable().getValueAt(i, 2);
            Boolean timeInterval = (Boolean) customizeReportDialog.getGlobalViewsTable().getValueAt(i, 3);
            // then we get the selected options from the map
            // we check first for the Time Interval
            if (!timeInterval) {
                addGlobalAreaChart(sem, points);
            } else {
                addGlobalAreaChartInTimeInterval(sem, points);
            }
            // we move to next page
            document.newPage();
        }
    }

    /**
     * Add Global Area chart.
     *
     * @param plotErrorBars
     * @param plotPoints
     */
    private void addGlobalAreaChart(boolean plotErrorBars, boolean plotPoints) {
        List<PlateCondition> plateConditonsList = areaAnalysisController.getProcessedConditions();
        MeasuredAreaType measuredAreaType = areaAnalysisController.getMeasuredAreaType();
        // create chart (for all conditions, error bars on top, both lines and points)
        JFreeChart globalAreaChart = areaAnalysisController.createGlobalAreaChart(plateConditonsList, useCorrectedData, plotErrorBars, true, plotPoints, measuredAreaType);
        // add chart as image
        addImageFromChart(globalAreaChart, chartWidth, chartHeight);
    }

    /**
     * Add Global Area chart showing the time interval.
     *
     * @param plotErrorBars
     * @param plotPoints
     */
    private void addGlobalAreaChartInTimeInterval(boolean plotErrorBars, boolean plotPoints) {
        List<PlateCondition> plateConditonsList = areaAnalysisController.getProcessedConditions();
        MeasuredAreaType measuredAreaType = areaAnalysisController.getMeasuredAreaType();
        // create chart (for all conditions, error bars on top, both lines and points)
        JFreeChart globalAreaChart = areaAnalysisController.createGlobalAreaChartInTimeInterval(plateConditonsList, useCorrectedData, plotErrorBars, true, plotPoints, measuredAreaType);
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
        Image imageFromChart = PdfUtils.getImageFromJFreeChart(writer, chart, imageWidth, imageHeight);
        // put image in the center
        imageFromChart.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(imageFromChart);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Add a paragraph for each analysis group, if any analysis group is
     * present.
     */
    private void addParagraphPerAnalysis() {
        ObservableList<AnalysisGroup> groupsList = areaAnalysisController.getGroupsBindingList();
        if (!groupsList.isEmpty()) {
            // add main title for section
            PdfUtils.addTitle(document, "ANALYSIS GROUPS", titleFont);
            for (int i = 0; i < groupsList.size(); i++) {
                Paragraph paragraph = new Paragraph("Analysis group: " + groupsList.get(i).getGroupName());
                try {
                    document.add(paragraph);
                    addAnalysisInfo(groupsList.get(i));
                    // go to new page
                    document.newPage();
                } catch (DocumentException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
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

    /**
     * Cell Editor for the check boxes conditions.
     */
    private final class CheckBoxConditionsCellEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {

        private JCheckBox checkBox;
        private CheckBoxesConditionsTableModel conditionsCheckBoxesTableModel;

        private CheckBoxConditionsCellEditor(CheckBoxesConditionsTableModel conditionsCheckBoxesTableModel) {
            checkBox = new JCheckBox();
            this.conditionsCheckBoxesTableModel = conditionsCheckBoxesTableModel;
            checkBox.setHorizontalAlignment(SwingConstants.LEFT);
            checkBox.addItemListener(this);
        }

        @Override
        public Object getCellEditorValue() {
            return Boolean.valueOf(checkBox.isSelected());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // if value is true, select checkbox, else do nothing
            checkBox.setSelected((boolean) value);
            return checkBox;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            fireEditingStopped();
            updateCheckBoxes();
        }

        /**
         *
         */
        private void updateCheckBoxes() {
            List<PlateCondition> processedConditions = areaAnalysisController.getProcessedConditions();
            boolean[] checkBoxes = conditionsCheckBoxesTableModel.getCheckBoxes();
            for (int i = 0; i < checkBoxes.length; i++) {
                conditionsToPlotMap.put(processedConditions.get(i), checkBoxes[i]);
            }
        }
    }

    /**
     * Cell Editor for the CheckBoxes Global views.
     */
    private final class CheckBoxGlobalViewsCellEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {

        private JCheckBox checkBox;
        private CheckBoxesGlobalViewsTableModel checkBoxesGlobalViewsTableModel;

        private CheckBoxGlobalViewsCellEditor(CheckBoxesGlobalViewsTableModel conditionsCheckBoxesTableModel) {
            checkBox = new JCheckBox();
            this.checkBoxesGlobalViewsTableModel = conditionsCheckBoxesTableModel;
            checkBox.setHorizontalAlignment(SwingConstants.LEFT);
            checkBox.addItemListener(this);
        }

        @Override
        public Object getCellEditorValue() {
            return Boolean.valueOf(checkBox.isSelected());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // if value is true, select checkbox, else do nothing
            checkBox.setSelected((boolean) value);
            return checkBox;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            fireEditingStopped();
            updateCheckBoxes();
        }

        /**
         *
         */
        private void updateCheckBoxes() {
            Boolean[][] checkBoxes = checkBoxesGlobalViewsTableModel.getCheckBoxes();
            globalViewsMap.clear();
            for (int i = 0; i < checkBoxes.length; i++) {
                globalViewsMap.put("Global View " + (i + 1), checkBoxes[i]);
            }
        }
    }
}
