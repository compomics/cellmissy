/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.area;

import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DoseResponseController;
import be.ugent.maf.cellmissy.analysis.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.doseresponse.AreaDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.analysis.area.AreaMainController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.JFreeChart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for area dose-response analysis
 *
 * @author Gwendolien
 */
@Controller("areaDoseResponseController")
public class AreaDoseResponseController extends DoseResponseController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AreaDoseResponseController.class);
    //model: in super class
    private AreaDoseResponseAnalysisGroup dRAnalysisGroup;
    //view: in super class
    // parent controller
    @Autowired
    private AreaMainController areaMainController;
    // child controller
    @Autowired
    private AreaDRInputController dRInputController;
    @Autowired
    private AreaDRInitialController dRInitialController;
    @Autowired
    private AreaDRNormalizedController dRNormalizedController;
    @Autowired
    private AreaDRResultsController dRResultsController;
    // services
    @Autowired
    private SigmoidFitter sigmoidFitter;

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
    public AreaDoseResponseAnalysisGroup getdRAnalysisGroup() {
        return dRAnalysisGroup;
    }

    public void setdRAnalysisGroup(AreaDoseResponseAnalysisGroup dRAnalysisGroup) {
        this.dRAnalysisGroup = dRAnalysisGroup;
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

    public Algorithm getSelectedAlgorithm() {
        return areaMainController.getSelectedALgorithm();
    }

    @Override
    public List<DoseResponsePair> getDataToFit(boolean normalized) {
        if (normalized) {
            return dRNormalizedController.getDataToFit();
        } else {
            return dRInitialController.getDataToFit();
        }
    }

    /**
     * Called by parent controller, initialise tables
     */
    public void onDoseResponse() {
        dRPanel.getInputDRButton().setSelected(true);
        dRInputController.initDRInputData();
        //switch shared table view
        updateModelInTable(dRInputController.getTableModel());
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        updateTableInfoMessage("This table contains all conditions and their respective slopes");
    }

    /**
     * Do a fitting according to initial, standard parameters and calculate
     * statistics. This method is called when the user switches to the initial
     * or normalized subview for the first time.
     */
    @Override
    protected void initFirstFitting() {
        dRInitialController.initDRInitialData();
        dRNormalizedController.initDRNormalizedData();
        calculateStatistics();
    }

    /**
     * Get the constrain values for the bottom and top parameter. (Double number
     * or null if not constrained)
     *
     * @param normalized True if from normalized fit
     * @return
     */
    @Override
    protected List<Double> getConstrainValues(boolean normalized) {
        List<Double> result = new ArrayList<>();
        if (!normalized) {
            result.add(dRInitialController.getBottomConstrainValue());
            result.add(dRInitialController.getTopConstrainValue());
        } else {
            result.add(dRNormalizedController.getBottomConstrainValue());
            result.add(dRNormalizedController.getTopConstrainValue());
        }
        return result;
    }

    /**
     * Perform fitting according to user specifications. Called by subclasses.
     *
     * @param dataToFit The data (log-transformed concentration - velocity)
     * @param resultsHolder The class that will contain the results from fitting
     * @param bottomConstrained Double if user constrains, otherwise null
     * @param topConstrained Double if user constrains, otherwise null
     *
     */
    protected void performFitting(List<DoseResponsePair> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrained, Double topConstrained) {
        performFitting(sigmoidFitter, dataToFit, resultsHolder, bottomConstrained, topConstrained);
    }

    /**
     * Calculate statistics, method from results controller is called by other
     * child controllers on new fitting.
     */
    @Override
    protected void calculateStatistics() {
        dRResultsController.setStatistics(dRAnalysisGroup);
    }

    /**
     * Reset views on cancel
     */
    @Override
    public void resetOnCancel() {
        super.resetOnCancel();
        dRAnalysisGroup = null;
        dRInputController.onCancel();
        //remove tables, graphs and subpanels
        dRInputController.getdRInputPanel().getSlopesTable().setModel(new DefaultTableModel());
        dRInitialController.getInitialChartPanel().setChart(null);
        dRNormalizedController.getNormalizedChartPanel().setChart(null);
        dRResultsController.getDupeInitialChartPanel().setChart(null);
        dRResultsController.getDupeNormalizedChartPanel().setChart(null);
        //set view back to first one
        dRPanel.getInputDRButton().setSelected(true);
        dRPanel.revalidate();
        dRPanel.repaint();
    }

    /**
     * Returns a dose-response chart containing scattered xy experimental values
     * and the curve (line) from the fitting. To be visible in the program,
     * another method adds the chart to the right panel.
     *
     * @param dataToPlot Maps log-transformed concentration to replicate
     * (normalized) velocities.
     * @param normalized Whether the data is normalized or not
     * @return
     */
    @Override
    protected JFreeChart createDoseResponseChart(List<DoseResponsePair> dataToPlot, boolean normalized) {
        return createDoseResponseChart(dataToPlot, dRAnalysisGroup, normalized);
    }

    /**
     * Ask user to choose for a directory and invoke swing worker for creating
     * PDF report
     *
     * @throws IOException
     */
    protected void createPdfReport() throws IOException {
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
    @Override
    protected void initMainView() {
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

    protected String getNormalizationInfo() {
        return dRNormalizedController.getNormalizationInfo();
    }

    @Override
    protected List<String> getPlotAxesNames(boolean normalized) {
        List<String> result = new ArrayList<>();
        result.add("Log10 of concentration");
        if (normalized) {
            result.add("Response (%)");
        } else {
            result.add("Velocity (µM²/min)");
        }
        return result;
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
