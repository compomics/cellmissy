/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.analysis.area.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.analysis.area.AreaMainController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for area dose-response analysis
 *
 * @author Gwendolien
 */
@Controller("doseResponseController")
public class DoseResponseController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DoseResponseController.class);
    //model
    private JTable dataTable;
    private int standardHillslope;
    private DoseResponseAnalysisGroup dRAnalysisGroup;
    private boolean firstFitting;
    //view
    private DRPanel dRPanel;
    // parent controller
    @Autowired
    private AreaMainController areaMainController;
    // child controller
    @Autowired
    private DRInputController dRInputController;
    @Autowired
    private DRInitialController dRInitialController;
    @Autowired
    private DRNormalizedController dRNormalizedController;
    @Autowired
    private DRResultsController dRResultsController;
    // services
    @Autowired
    private SigmoidFitter sigmoidFitter;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init view
        initMainView();
        //init child controllers
        dRInputController.init();
        dRInitialController.init();
        dRNormalizedController.init();
        dRResultsController.init();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public DRPanel getDRPanel() {
        return dRPanel;
    }

    public int getStandardHillslope() {
        return standardHillslope;
    }

    public void setStandardHillslope(int standardHillslope) {
        this.standardHillslope = standardHillslope;
    }

    public void setdRAnalysisGroup(DoseResponseAnalysisGroup dRAnalysisGroup) {
        this.dRAnalysisGroup = dRAnalysisGroup;
    }

    public DoseResponseAnalysisGroup getdRAnalysisGroup() {
        return dRAnalysisGroup;
    }

    public List<PlateCondition> getProcessedConditions() {
        return areaMainController.getProcessedConditions();
    }

    public Map<PlateCondition, AreaAnalysisResults> getLinearResultsAnalysisMap() {
        return areaMainController.getLinearResultsAnalysisMap();
    }

    public List<Integer> getNumberOfReplicates() {
        return areaMainController.getNumberOfReplicates();
    }

    public CellMissyFrame getCellMissyFrame() {
        return areaMainController.getCellMissyFrame();
    }

    public void showMessage(String message, String title, Integer messageType) {
        areaMainController.showMessage(message, title, messageType);
    }

    public Experiment getExperiment() {
        return areaMainController.getExperiment();
    }

    public Algorithm getSelectedALgorithm() {
        return areaMainController.getSelectedALgorithm();
    }

    public boolean isFirstFitting() {
        return firstFitting;
    }

    public void setFirstFitting(boolean firstFitting) {
        this.firstFitting = firstFitting;
    }

    public LinkedHashMap<Double, List<Double>> getDataToFit(boolean normalized) {
        if (normalized) {
            return dRNormalizedController.getDataToFit();
        } else {
            return dRInitialController.getDataToFit();
        }
    }

    /**
     * Called by parent controller, initialize tables
     */
    public void onDoseResponse() {
        dRInputController.initDRInputData();
        //switch shared table view
        updateModelInTable(dRInputController.getTableModel());
        updateTableInfoMessage("This table contains all conditions and their respective slopes");
    }

    /**
     * Do a fitting according to initial, standard parameters and calculate
     * statistics.
     */
    public void initFirstFitting() {
        dRInitialController.initDRInitialData();
        dRNormalizedController.initDRNormalizedData();
        dRResultsController.initDRResultsData();
    }

    /**
     * update information message above table. Message will be different for
     * each subview
     *
     * @param messageToShow
     */
    public void updateTableInfoMessage(String messageToShow) {
        dRPanel.getTableInfoLabel().setText(messageToShow);
    }

    /**
     * When switching to a different subview, change the model for the main
     * table.
     */
    public void updateModelInTable(NonEditableTableModel tableModel) {
        dataTable.setModel(tableModel);
    }

    /**
     * Plots the fitted data.
     */
    public void plotDoseResponse(ChartPanel chartPanel, JPanel subviewPanel, LinkedHashMap<Double, List<Double>> dataToPlot, DoseResponseAnalysisGroup analysisGroup, boolean normalized) {
        JFreeChart doseResponseChart = createDoseResponseChart(dataToPlot, analysisGroup, normalized);
        chartPanel.setChart(doseResponseChart);
        //add chartpanel to graphics parent panel and repaint
        subviewPanel.add(chartPanel, gridBagConstraints);
        dRPanel.getGraphicsDRParentPanel().repaint();
        dRPanel.getGraphicsDRParentPanel().revalidate();
    }

    /**
     * Perform fitting according to user specifications. This method will check
     * how many parameters have been constrained and pick the right fitter
     * class.
     *
     * @param dataToFit The data (log-transformed concentration - velocity)
     * @param resultsHolder The class that will contain the results from fitting
     * @param bottomConstrained Double if user constrains, otherwise null
     * @param topConstrained Double if user constrains, otherwise null
     *
     */
    public void performFitting(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrained, Double topConstrained) {

        if (topConstrained != null) {
            if (bottomConstrained != null) {
                sigmoidFitter.fitBotTopConstrain(dataToFit, resultsHolder, bottomConstrained, topConstrained, getStandardHillslope());
            } else {
                sigmoidFitter.fitTopConstrain(dataToFit, resultsHolder, topConstrained, getStandardHillslope());
            }
        } else if (bottomConstrained != null) {
            sigmoidFitter.fitBotConstrain(dataToFit, resultsHolder, bottomConstrained, getStandardHillslope());
        } else {
            sigmoidFitter.fitNoConstrain(dataToFit, resultsHolder, getStandardHillslope());
        }
    }

    /**
     * Calculate statistics, method from results controller is called by other
     * child controllers on new fitting.
     */
    public void calculateStatistics() {
        dRResultsController.calculateStatistics(dRAnalysisGroup);
    }

    /**
     * Reset views on cancel
     */
    public void resetOnCancel() {
        dRAnalysisGroup = new DoseResponseAnalysisGroup();
        dataTable.setModel(new DefaultTableModel());
        dRInputController.onCancel();
        dRInputController.getdRInputPanel().getSlopesTable().setModel(new DefaultTableModel());
        dRInitialController.getInitialChartPanel().setChart(null);
        dRNormalizedController.getNormalizedChartPanel().setChart(null);
        dRResultsController.getDupeInitialChartPanel().setChart(null);
        dRResultsController.getDupeNormalizedChartPanel().setChart(null);
        dRPanel.getGraphicsDRParentPanel().remove(dRInputController.getdRInputPanel());
        dRPanel.getGraphicsDRParentPanel().remove(dRInitialController.getDRInitialPlotPanel());
        dRPanel.getGraphicsDRParentPanel().remove(dRNormalizedController.getDRNormalizedPlotPanel());
        dRPanel.getGraphicsDRParentPanel().remove(dRResultsController.getdRResultsPanel());
        dRPanel.revalidate();
        dRPanel.repaint();
    }

    /**
     * Returns a dose-response chart containing scattered xy experimental values
     * and the curve (line) from the fitting. To be visible in the program,
     * another method adds the chart to the right panel.
     *
     * @param dataToPlot The map created in a child controller, maps
     * log-transformed concentration to replicate (normalized) velocities.
     * @param analysisGroup The dose-response analysis group
     * @param normalized Whether the data is normalized or not
     * @return
     */
    public JFreeChart createDoseResponseChart(LinkedHashMap<Double, List<Double>> dataToPlot, DoseResponseAnalysisGroup analysisGroup, boolean normalized) {
        
        //setup scatter data of experimental concentrations/slopes, renderer and axis
        XYSeriesCollection experimentalData = new XYSeriesCollection();
        XYSeries scatterXYSeries = JFreeChartUtils.generateXYSeries(AnalysisUtils.generateXValues(dataToPlot), AnalysisUtils.generateYValues(dataToPlot));
        scatterXYSeries.setKey("Experimental data");
        experimentalData.addSeries(scatterXYSeries);
        
        // Create the line data, renderer, and axis
        XYSeriesCollection fitting = new XYSeriesCollection();
        // create xy series of simulated data from the parameters from the fitting
        XYSeries fittingData = simulateData(analysisGroup, normalized);
        fittingData.setKey("Fitting");
        fitting.addSeries(fittingData);
        
        XYPlot plot = JFreeChartUtils.setupDoseResponseDatasets(experimentalData, fitting, normalized);
        
        // show the r squared value
        SigmoidFittingResultsHolder resultsholder = null;
        if (normalized) {
            resultsholder = analysisGroup.getDoseResponseAnalysisResults().getNormalizedFittingResults();
        } else {
            resultsholder = analysisGroup.getDoseResponseAnalysisResults().getInitialFittingResults();
        }
        plot.addAnnotation(new XYTextAnnotation("R2=" + AnalysisUtils.roundThreeDecimals(AnalysisUtils.computeRSquared(dataToPlot, resultsholder)), -4, 1.0));

        // Create the chart with the plot and no legend
        JFreeChart chart = new JFreeChart("Title", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        String title = new String();
        if (normalized) {
            title = "Normalized fitting";
        } else {
            title = "Initial fitting";
        }
        JFreeChartUtils.setupDoseResponseChart(chart, title);
        return chart;
    }

    /**
     * This method takes the fitted parameters that make up the dose-response
     * function from the analysis group results holder and creates x and y
     * values. This is needed to put the fitted function curve on the plot.
     *
     * @param analysisGroup
     * @param normalized Whether the method takes the fitted parameters from the
     * normalized or initial fitting
     */
    public XYSeries simulateData(DoseResponseAnalysisGroup analysisGroup, boolean normalized) {
        DoseResponseAnalysisResults analysisResults = analysisGroup.getDoseResponseAnalysisResults();
        SigmoidFittingResultsHolder resultsholder = null;
        if (!normalized) {
            resultsholder = analysisResults.getInitialFittingResults();
        } else {
            resultsholder = analysisResults.getNormalizedFittingResults();
        }
        return JFreeChartUtils.createFittedDataset(resultsholder.getTop(), resultsholder.getBottom(), resultsholder.getHillslope(), resultsholder.getLogEC50());
    }

    /**
     * Ask user to choose for a directory and invoke swing worker for creating
     * PDF report
     *
     * @throws IOException
     */
    public void createPdfReport() throws IOException {
        Experiment experiment = areaMainController.getExperiment();
        // choose directory to save pdf file
        JFileChooser chooseDirectory = new JFileChooser();
        chooseDirectory.setDialogTitle("Choose a directory to save the report");
        chooseDirectory.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooseDirectory.setSelectedFile(new File("Dose Response Report " + experiment.toString() + " - " + experiment.getProject().toString() + ".pdf"));
        // in response to the button click, show open dialog
        int returnVal = chooseDirectory.showSaveDialog(areaMainController.getDataAnalysisPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File directory = chooseDirectory.getCurrentDirectory();
            DoseResponseReportSwingWorker doseResponseReportSwingWorker = new DoseResponseReportSwingWorker(directory, chooseDirectory.getSelectedFile().getName());
            doseResponseReportSwingWorker.execute();
        } else {
            areaMainController.showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * private methods
     */
    /**
     * Initialize main view
     */
    private void initMainView() {
        dRPanel = new DRPanel();
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup mainDRRadioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        mainDRRadioButtonGroup.add(dRPanel.getInputDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getInitialPlotDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getNormalizedPlotDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getResultsDRButton());
        //select as default first button
        dRPanel.getInputDRButton().setSelected(true);
        //init dataTable
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row and column selection must be false
        //dataTable.setColumnSelectionAllowed(false);
        //dataTable.setRowSelectionAllowed(false);
        dRPanel.getDatatableDRPanel().add(scrollPane, BorderLayout.CENTER);

        /**
         * When button is selected, switch view to corresponding subview
         */
        dRPanel.getInputDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //switch shared table view
                updateModelInTable(dRInputController.getTableModel());
                updateTableInfoMessage("This table contains all conditions and their respective slopes");
                /**
                 * for (int columnIndex = 0; columnIndex <
                 * dataTable.getColumnCount(); columnIndex++) {
                 * GuiUtils.packColumn(dataTable, columnIndex); }
                 */
                dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
                //remove other panels
                dRInitialController.getInitialChartPanel().setChart(null);
                dRNormalizedController.getNormalizedChartPanel().setChart(null);
                dRResultsController.getDupeInitialChartPanel().setChart(null);
                dRResultsController.getDupeNormalizedChartPanel().setChart(null);
                dRPanel.getGraphicsDRParentPanel().removeAll();
                dRPanel.getGraphicsDRParentPanel().revalidate();
                dRPanel.getGraphicsDRParentPanel().repaint();
                //add panel to view
                dRPanel.getGraphicsDRParentPanel().add(dRInputController.getdRInputPanel(), gridBagConstraints);
            }
        });

        dRPanel.getInitialPlotDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dRAnalysisGroup != null) {
                    if (isFirstFitting()) {
                        initFirstFitting();
                        setFirstFitting(false);
                    }
                    //switch shared table view
                    updateModelInTable(dRInitialController.getTableModel());
                    updateTableInfoMessage("Concentrations of conditions selected previously have been log-transformed, slopes have not been changed");
                    /**
                     * for (int columnIndex = 0; columnIndex <
                     * dataTable.getColumnCount(); columnIndex++) {
                     * GuiUtils.packColumn(dataTable, columnIndex); }
                     */
                    dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
                    //remove other panels
                    dRNormalizedController.getNormalizedChartPanel().setChart(null);
                    dRResultsController.getDupeInitialChartPanel().setChart(null);
                    dRResultsController.getDupeNormalizedChartPanel().setChart(null);
                    dRPanel.getGraphicsDRParentPanel().removeAll();
                    dRPanel.getGraphicsDRParentPanel().revalidate();
                    dRPanel.getGraphicsDRParentPanel().repaint();
                    dRPanel.getGraphicsDRParentPanel().add(dRInitialController.getDRInitialPlotPanel(), gridBagConstraints);
                    //Plot fitted data in dose-response curve, along with R² annotation
                    plotDoseResponse(dRInitialController.getInitialChartPanel(), dRInitialController.getDRInitialPlotPanel().getDoseResponseChartParentPanel(), getDataToFit(false), getdRAnalysisGroup(), false);
                }
            }
        });

        dRPanel.getNormalizedPlotDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dRAnalysisGroup != null) {
                    //in case user skips "initial" subview and goes straight to normalization
                    if (isFirstFitting()) {
                        initFirstFitting();
                        setFirstFitting(false);
                    }
                    //switch shared table view
                    updateModelInTable(dRNormalizedController.getTableModel());
                    updateTableInfoMessage("Log-transformed concentrations with their normalized responses per replicate");
                    /**
                     * for (int columnIndex = 0; columnIndex <
                     * dataTable.getColumnCount(); columnIndex++) {
                     * GuiUtils.packColumn(dataTable, columnIndex); }
                     */
                    dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
                    //remove other panels
                    dRInitialController.getInitialChartPanel().setChart(null);
                    dRResultsController.getDupeInitialChartPanel().setChart(null);
                    dRResultsController.getDupeNormalizedChartPanel().setChart(null);
                    dRPanel.getGraphicsDRParentPanel().removeAll();
                    dRPanel.getGraphicsDRParentPanel().revalidate();
                    dRPanel.getGraphicsDRParentPanel().repaint();
                    dRPanel.getGraphicsDRParentPanel().add(dRNormalizedController.getDRNormalizedPlotPanel(), gridBagConstraints);
                    //Plot fitted data in dose-response curve, along with R² annotation
                    plotDoseResponse(dRNormalizedController.getNormalizedChartPanel(), dRNormalizedController.getDRNormalizedPlotPanel().getDoseResponseChartParentPanel(), getDataToFit(true), getdRAnalysisGroup(), true);
                }
            }
        });

        dRPanel.getResultsDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dRAnalysisGroup != null) {
                //switch shared table view: create and set new table model with most recent statistical values
                    // (these values get recalculated after each new fitting)
                    dRResultsController.setTableModel(dRResultsController.reCreateTableModel(dRAnalysisGroup));
                    updateModelInTable(dRResultsController.getTableModel());
                    updateTableInfoMessage("Statistical values from the curve fit of the initial and normalized data.");

                    //remove other panels
                    dRInitialController.getInitialChartPanel().setChart(null);
                    dRNormalizedController.getNormalizedChartPanel().setChart(null);
                    dRPanel.getGraphicsDRParentPanel().removeAll();
                    dRPanel.getGraphicsDRParentPanel().revalidate();
                    dRPanel.getGraphicsDRParentPanel().repaint();
                    dRPanel.getGraphicsDRParentPanel().add(dRResultsController.getdRResultsPanel(), gridBagConstraints);
                    //plot curves
                    dRResultsController.plotCharts();
                }
            }
        });

        //add view to parent panel
        areaMainController.getAreaAnalysisPanel().getDoseResponseParentPanel().add(dRPanel, gridBagConstraints);
    }

    /**
     * Swing Worker to generate PDF report
     */
    private class DoseResponseReportSwingWorker extends SwingWorker<File, Void> {

        private final File directory;
        private final String reportName;

        public DoseResponseReportSwingWorker(File directory, String reportName) {
            this.directory = directory;
            this.reportName = reportName;
        }

        @Override
        protected File doInBackground() throws Exception {
            // disable button
            dRResultsController.getdRResultsPanel().getCreateReportButton().setEnabled(false);
            //set cursor to waiting one
            areaMainController.setCursor(Cursor.WAIT_CURSOR);
            //call the child controller to create report
            return dRResultsController.createAnalysisReport(directory, reportName);
        }

        @Override
        protected void done() {
            File file = null;
            try {
                file = get();
            } catch (InterruptedException | CancellationException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                areaMainController.handleUnexpectedError(ex);
            }
            try {
                //if export to PDF was successful, open the PDF file from the desktop
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                areaMainController.showMessage("Cannot open the file!" + "\n" + ex.getMessage(), "error while opening file", JOptionPane.ERROR_MESSAGE);
            }
            //set cursor back to default
            areaMainController.setCursor(Cursor.DEFAULT_CURSOR);
            // enable button
            dRResultsController.getdRResultsPanel().getCreateReportButton().setEnabled(true);
        }
    }

}
