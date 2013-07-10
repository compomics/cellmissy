/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis;

import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.MeasuredAreaType;
import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrectionFactory;
import be.ugent.maf.cellmissy.analysis.SignificanceLevel;
import be.ugent.maf.cellmissy.analysis.StatisticsAnalyzer;
import be.ugent.maf.cellmissy.analysis.StatisticsTestFactory;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.result.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.result.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.experiment.analysis.LinearRegressionPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.StatisticsDialog;
import be.ugent.maf.cellmissy.gui.view.table.model.PValuesTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.StatisticalSummaryTableModel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.PValuesTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.RectIconCellRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.VelocityBarRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.annotations.CategoryLineAnnotation;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("areaAnalysisController")
public class AreaAnalysisController {

    private static final Logger LOG = Logger.getLogger(AreaAnalysisController.class);
    // model
    private BindingGroup bindingGroup;
    private Map<PlateCondition, AreaAnalysisResults> analysisMap;
    private ObservableList<AnalysisGroup> groupsBindingList;
    private ObservableList<Double> significanceLevelsBindingList;
    // view
    private LinearRegressionPanel linearRegressionPanel;
    private ChartPanel velocityChartPanel;
    private StatisticsDialog statisticsDialog;
    // parent controller
    @Autowired
    private AreaController areaController;
    // child controllers
    @Autowired
    private AreaAnalysisReportController areaAnalysisReportController;
    //services
    @Autowired
    private AreaAnalyzer areaAnalyzer;
    @Autowired
    private StatisticsAnalyzer statisticsAnalyzer;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        analysisMap = new LinkedHashMap<>();
        //init views
        initLinearRegressionPanel();
    }

    public Experiment getExperiment() {
        return areaController.getExperiment();
    }

    public Map<PlateCondition, AreaAnalysisResults> getAnalysisMap() {
        return analysisMap;
    }

    public ObservableList<AnalysisGroup> getGroupsBindingList() {
        return groupsBindingList;
    }

    public Map<PlateCondition, AreaPreProcessingResults> getPreProcessingMap() {
        return areaController.getPreProcessingMap();
    }

    public JFreeChart createGlobalAreaChart(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, boolean plotLines, boolean plotPoints, MeasuredAreaType measuredAreaType) {
        return areaController.createGlobalAreaChart(plateConditionList, useCorrectedData, plotErrorBars, plotLines, plotPoints, measuredAreaType);
    }

    public void showMessage(String message, String title, Integer messageType) {
        areaController.showMessage(message, title, messageType);
    }

    public MeasuredAreaType getMeasuredAreaType() {
        return areaController.getAreaAnalysisHolder().getMeasuredAreaType();
    }

    public Algorithm getSelectedALgorithm() {
        return areaController.getSelectedALgorithm();
    }

    public ImagingType getSelectedImagingType() {
        return areaController.getSelectedImagingType();
    }

    public LinearRegressionPanel getLinearRegressionPanel() {
        return linearRegressionPanel;
    }

    /**
     * Show Results from Linear Regression Model in a table
     *
     * @param useCorrectedData
     */
    public void showLinearModelInTable(boolean useCorrectedData) {
        // initialize map for keeping the results
        initMap();
        List<Double[]> slopesList = new ArrayList();
        List<Double[]> coefficientsList = new ArrayList();
        List<Double> meanSlopesList = new ArrayList();
        List<Double> madSlopesList = new ArrayList();
        List<PlateCondition> processedConditions = areaController.getProcessedConditions();
        // go through all conditions in map and estimate linear model for each of them
        for (PlateCondition plateCondition : processedConditions) {
            estimateLinearModel(plateCondition, useCorrectedData);
            slopesList.add((analysisMap.get(plateCondition).getSlopes()));
            coefficientsList.add((analysisMap.get(plateCondition).getGoodnessOfFit()));
            meanSlopesList.add(analysisMap.get(plateCondition).getMeanSlope());
            madSlopesList.add(analysisMap.get(plateCondition).getMadSlope());
        }
        // data for table model: number of rows equal to number of conditions, number of columns equal to maximum number of replicates + 3
        // first column with conditions, last two with mean and mad values
        int maximumNumberOfReplicates = AnalysisUtils.getMaximumNumberOfReplicates(processedConditions);
        Object[][] data = new Object[processedConditions.size()][maximumNumberOfReplicates + 4];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 2; columnIndex < slopesList.get(rowIndex).length + 2; columnIndex++) {
                Double slope = slopesList.get(rowIndex)[columnIndex - 2];
                Double coefficient = coefficientsList.get(rowIndex)[columnIndex - 2];
                if (slope != null && coefficient != null && !slope.isNaN() && !coefficient.isNaN()) {
                    // round to three decimals slopes and coefficients
                    slope = AnalysisUtils.roundThreeDecimals(slopesList.get(rowIndex)[columnIndex - 2]);
                    coefficient = AnalysisUtils.roundThreeDecimals(coefficientsList.get(rowIndex)[columnIndex - 2]);
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
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "Cond";
        columnNames[1] = "";
        for (int i = 2; i < columnNames.length - 2; i++) {
            columnNames[i] = "Repl " + (i - 1);
        }
        columnNames[columnNames.length - 2] = "median";
        columnNames[columnNames.length - 1] = "MAD";
        JTable slopesTable = linearRegressionPanel.getSlopesTable();
        // set model of table
        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        slopesTable.setModel(nonEditableTableModel);
        // set cell renderer: rect icon in the second column
        slopesTable.getColumnModel().getColumn(1).setCellRenderer(new RectIconCellRenderer());
        for (int columnIndex = 0; columnIndex < slopesTable.getColumnCount(); columnIndex++) {
            if (columnIndex == columnNames.length - 1 | columnIndex == columnNames.length - 2) {
                slopesTable.getColumnModel().getColumn(columnIndex).setCellRenderer(new FormatRenderer(areaController.getFormat(), SwingConstants.LEFT));
            }
            GuiUtils.packColumn(slopesTable, columnIndex, 1);
        }
        // select by default all conditions: to show all bars in the velocity chart
        slopesTable.setRowSelectionInterval(0, linearRegressionPanel.getSlopesTable().getRowCount() - 1);
    }

    /**
     * Update information on time frames and corrected data in analysis
     */
    public void updateAnalysisInfo() {
        double[] analysisTimeFrames = areaController.getAnalysisTimeFrames();
        linearRegressionPanel.getFirstTimeFrameTextField().setText("" + analysisTimeFrames[0]);
        linearRegressionPanel.getLastTimeFrameTextField().setText("" + analysisTimeFrames[analysisTimeFrames.length - 1]);
        String correctedData;
        if (areaController.useCorrectedData()) {
            correctedData = "YES";
        } else {
            correctedData = "NO";
        }
        linearRegressionPanel.getCorrectedDataTextField().setText(correctedData);
    }

    /**
     * create velocity chart
     *
     * @param conditionsToShow
     * @return
     */
    public JFreeChart createVelocityChart(int[] conditionsToShow) {
        DefaultStatisticalCategoryDataset velocityDataset = getVelocityDataset(conditionsToShow);
        String areaUnitOfMeasurement = getAreaUnitOfMeasurement();
        JFreeChart velocityChart = ChartFactory.createLineChart("Median Velocity", "", "Velocity " + "(" + areaUnitOfMeasurement + "\\min)", velocityDataset, PlotOrientation.VERTICAL, false, false, false);
        velocityChart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 12));
        CategoryPlot velocityPlot = velocityChart.getCategoryPlot();
        velocityPlot.setBackgroundPaint(Color.white);
        velocityPlot.setOutlinePaint(Color.white);
        VelocityBarRenderer velocityBarRenderer = new VelocityBarRenderer();
        velocityBarRenderer.setErrorIndicatorPaint(Color.black);
        velocityPlot.setRenderer(velocityBarRenderer);
        // set CategoryTextAnnotation to show number of replicates on top of bars
        for (int i = 0; i < velocityDataset.getColumnCount(); i++) {
            Double[] numberOfReplicates = AnalysisUtils.excludeNullValues(analysisMap.get(areaController.getPlateConditionList().get(i)).getSlopes());
            CategoryAnnotation annotation = new CategoryTextAnnotation("N " + numberOfReplicates.length, velocityDataset.getColumnKey(i), 10);
            velocityPlot.addAnnotation(annotation);
        }
        velocityPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        JFreeChartUtils.setShadowVisible(velocityChart, false);
        return velocityChart;
    }

    /**
     * Ask user to choose for a directory and invoke swing worker for creating
     * PDF report
     *
     * @throws IOException
     */
    public void createPdfReport() throws IOException {
        Experiment experiment = areaController.getExperiment();
        // choose directory to save pdf file
        JFileChooser chooseDirectory = new JFileChooser();
        chooseDirectory.setDialogTitle("Choose a directory to save the report");
        chooseDirectory.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooseDirectory.setSelectedFile(new File("Analysis Report " + experiment.toString() + " - " + experiment.getProject().toString() + ".pdf"));
        // in response to the button click, show open dialog
        int returnVal = chooseDirectory.showSaveDialog(areaController.getDataAnalysisPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File directory = chooseDirectory.getCurrentDirectory();
            AnalysisReportSwingWorker analysisReportSwingWorker = new AnalysisReportSwingWorker(directory, chooseDirectory.getSelectedFile().getName());
            analysisReportSwingWorker.execute();
        } else {
            areaController.showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * private methods
     */
    /**
     * Get area unit of measurement
     *
     * @return
     */
    private String getAreaUnitOfMeasurement() {
        return areaController.getAreaUnitOfMeasurement();
    }

    /**
     *
     * @param conditionsToShow
     * @return
     */
    private DefaultStatisticalCategoryDataset getVelocityDataset(int[] conditionsToShow) {
        // dataset for chart
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
        double[] meanSlopes = new double[conditionsToShow.length];
        double[] semSlopes = new double[conditionsToShow.length];
        for (int i = 0; i < meanSlopes.length; i++) {
            AreaAnalysisResults areaAnalysisResults = analysisMap.get(areaController.getPlateConditionList().get(conditionsToShow[i]));
            double meanSlope = areaAnalysisResults.getMeanSlope();
            double madSlopes = areaAnalysisResults.getMadSlope();
            meanSlopes[i] = meanSlope;
            semSlopes[i] = madSlopes;
            dataset.add(meanSlopes[i], semSlopes[i], "Conditions", "Condition " + (conditionsToShow[i] + 1));
        }
        return dataset;
    }

    /**
     * show chart
     */
    private void showVelocityChart() {
        int[] selectedRows = linearRegressionPanel.getSlopesTable().getSelectedRows();
        JFreeChart velocityChart = createVelocityChart(selectedRows);
        velocityChartPanel.setChart(velocityChart);
        linearRegressionPanel.getChartParentPanel().add(velocityChartPanel, gridBagConstraints);
        linearRegressionPanel.getChartParentPanel().repaint();
    }

    /**
     * Estimate Linear Regression Model
     *
     * @param plateCondition
     */
    private void estimateLinearModel(PlateCondition plateCondition, boolean useCorrectedData) {
        // get the pre-processing results from main controller
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaController.getPreProcessingMap();
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        AreaAnalysisResults areaAnalysisResults = analysisMap.get(plateCondition);
        MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
        double[] analysisTimeFrames = areaController.getAnalysisTimeFrames();
        areaAnalyzer.estimateLinearModel(areaPreProcessingResults, areaAnalysisResults, useCorrectedData, measuredAreaType, analysisTimeFrames);
    }

    /**
     * Initialize view
     */
    private void initLinearRegressionPanel() {
        linearRegressionPanel = new LinearRegressionPanel();
        // control opaque property of table
        linearRegressionPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);
        JTable slopesTable = linearRegressionPanel.getSlopesTable();
        slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        slopesTable.getTableHeader().setReorderingAllowed(false);
        slopesTable.setFillsViewportHeight(true);
        // init chart panel
        velocityChartPanel = new ChartPanel(null);
        velocityChartPanel.setOpaque(false);
        // init statistics panel
        statisticsDialog = new StatisticsDialog(areaController.getCellMissyFrame(), true);
        // customize tables
        statisticsDialog.getStatisticalSummaryTable().getTableHeader().setReorderingAllowed(false);
        statisticsDialog.getpValuesTable().getTableHeader().setReorderingAllowed(false);
        statisticsDialog.getStatisticalSummaryTable().setFillsViewportHeight(true);
        statisticsDialog.getpValuesTable().setFillsViewportHeight(true);
        // init binding
        groupsBindingList = ObservableCollections.observableList(new ArrayList<AnalysisGroup>());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, groupsBindingList, linearRegressionPanel.getGroupsList());
        bindingGroup.addBinding(jListBinding);
        // fill in combo box
        List<Double> significanceLevels = new ArrayList<>();
        for (SignificanceLevel significanceLevel : SignificanceLevel.values()) {
            significanceLevels.add(significanceLevel.getValue());
        }
        significanceLevelsBindingList = ObservableCollections.observableList(significanceLevels);
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, significanceLevelsBindingList, statisticsDialog.getSignificanceLevelComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        // add the NONE (default) correction method
        // when the none is selected, CellMissy does not correct for multiple hypotheses
        statisticsDialog.getCorrectionMethodsComboBox().addItem("none");
        // fill in combo box: get all the correction methods from the factory
        Set<String> correctionBeanNames = MultipleComparisonsCorrectionFactory.getInstance().getCorrectionBeanNames();
        for (String correctionBeanName : correctionBeanNames) {
            statisticsDialog.getCorrectionMethodsComboBox().addItem(correctionBeanName);
        }
        // do the same for the statistical tests
        Set<String> statisticsCalculatorBeanNames = StatisticsTestFactory.getInstance().getStatisticsCalculatorBeanNames();
        for (String testName : statisticsCalculatorBeanNames) {
            statisticsDialog.getStatisticalTestComboBox().addItem(testName);
        }
        //significance level to 0.05
        statisticsDialog.getSignificanceLevelComboBox().setSelectedIndex(1);

        /**
         * List selection Listener for linear model results Table show bar
         * charts according to user selection in model
         */
        linearRegressionPanel.getSlopesTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // show velocity chart according to selected rows
                showVelocityChart();
            }
        });

        /**
         * Add a group to analysis
         */
        linearRegressionPanel.getAddGroupButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // from selected conditions make a new group and add it to the list
                addGroupToAnalysis();
            }
        });

        /**
         * remove a Group from analysis
         */
        linearRegressionPanel.getRemoveGroupButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // remove the selected group from list
                removeGroupFromAnalysis();
            }
        });

        /**
         * Create report from Analysis
         */
        linearRegressionPanel.getCreateReportButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if every group has been analyzed
                // create report
                if (validateAnalysis()) {
                    try {
                        createPdfReport();
                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                } else {
                    // else, show a message warning the user
                    // let him decide if continue with report creation or not
                    int reply = JOptionPane.showConfirmDialog(areaController.getDataAnalysisPanel(), "Not every group was analyzed.\nContinue with report creation?", "", JOptionPane.OK_CANCEL_OPTION);
                    if (reply == JOptionPane.OK_OPTION) {
                        try {
                            // if OK, create report
                            createPdfReport();
                        } catch (IOException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
        });

        /**
         * Execute a Mann Whitney Test on selected Analysis Group
         */
        linearRegressionPanel.getStatisticsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                String statisticalTestName = statisticsDialog.getStatisticalTestComboBox().getSelectedItem().toString();
                // check that an analysis group is being selected
                if (selectedIndex != -1) {
                    AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                    // compute statistics
                    computeStatistics(selectedGroup, statisticalTestName);
                    // show statistics in tables
                    showSummary(selectedGroup);
                    // set the correction combobox to the one already chosen
                    statisticsDialog.getCorrectionMethodsComboBox().setSelectedItem((Object) selectedGroup.getCorrectionMethodName());
                    if (selectedGroup.getCorrectionMethodName().equals("none")) {
                        // by default show p-values without adjustment
                        showPValues(selectedGroup, false);
                    } else {
                        // show p values with adjustement
                        showPValues(selectedGroup, true);
                    }
                    statisticsDialog.pack();
                    // center the dialog on the main frame
                    GuiUtils.centerDialogOnFrame(areaController.getCellMissyFrame(), statisticsDialog);
                    // show the dialog
                    statisticsDialog.setVisible(true);
                } else {
                    // ask user to select a group
                    areaController.showMessage("Please select a group to perform analysis on.", "You must select a group first", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        /**
         * Update button text if analysis was already done
         */
        linearRegressionPanel.getGroupsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        if (groupsBindingList.get(selectedIndex).getpValuesMatrix() == null) {
                            linearRegressionPanel.getStatisticsButton().setText("Perform Statistical Analysis...");
                        } else {
                            linearRegressionPanel.getStatisticsButton().setText("Modify Statistical Analysis...");
                        }
                    }
                }
            }
        });

        /**
         * Refresh p value table with current selected significance of level
         */
        statisticsDialog.getSignificanceLevelComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (statisticsDialog.getSignificanceLevelComboBox().getSelectedIndex() != -1) {
                    String statisticalTest = statisticsDialog.getStatisticalTestComboBox().getSelectedItem().toString();
                    Double selectedSignLevel = (Double) statisticsDialog.getSignificanceLevelComboBox().getSelectedItem();
                    AnalysisGroup selectedGroup = groupsBindingList.get(linearRegressionPanel.getGroupsList().getSelectedIndex());
                    boolean isAdjusted;
                    if (selectedGroup.getCorrectionMethodName().equals("none")) {
                        isAdjusted = false;
                    } else {
                        isAdjusted = true;
                    }
                    statisticsAnalyzer.detectSignificance(selectedGroup, statisticalTest, selectedSignLevel, isAdjusted);
                    boolean[][] significances = selectedGroup.getSignificances();
                    JTable pValuesTable = statisticsDialog.getpValuesTable();
                    for (int i = 1; i < pValuesTable.getColumnCount(); i++) {
                        pValuesTable.getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(new DecimalFormat("#.####"), significances));
                    }
                    pValuesTable.repaint();
                }
            }
        });

        /**
         * Apply correction for multiple comparisons: choose the algorithm!
         */
        statisticsDialog.getCorrectionMethodsComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                if (selectedIndex != -1) {
                    AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                    String correctionMethod = statisticsDialog.getCorrectionMethodsComboBox().getSelectedItem().toString();
                    // update test description
                    updateTestDescriptionPane(correctionMethod);
                    // if the correction method is not "NONE"
                    if (!correctionMethod.equals("none")) {
                        // adjust p values
                        statisticsAnalyzer.correctForMultipleComparisons(selectedGroup, correctionMethod);
                        // show p - values with the applied correction
                        showPValues(selectedGroup, true);
                    } else {
                        // if selected correction method is "NONE", do not apply correction and only show normal p-values
                        showPValues(selectedGroup, false);
                    }
                }
            }
        });

        /**
         * Perform statistical test: choose the test!!
         */
        statisticsDialog.getStatisticalTestComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the selected test to be executed
                String selectedTest = statisticsDialog.getStatisticalTestComboBox().getSelectedItem().toString();
                // analysis group
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                if (selectedIndex != -1) {
                    AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                    computeStatistics(selectedGroup, selectedTest);
                }
            }
        });

        //multiple comparison correction: set the default correction to none
        statisticsDialog.getCorrectionMethodsComboBox().setSelectedIndex(0);
        updateTestDescriptionPane("none");
        statisticsDialog.getStatisticalTestComboBox().setSelectedIndex(0);

        /**
         * Save analysis
         */
        statisticsDialog.getSaveAnalysisButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // analysis group
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                if (selectedIndex != -1) {
                    AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                    // set correction method (summary statistics, p-values and adjusted p-values are already set)
                    selectedGroup.setCorrectionMethodName(statisticsDialog.getCorrectionMethodsComboBox().getSelectedItem().toString());
                    //show message to the user
                    JOptionPane.showMessageDialog(statisticsDialog, "Analysis was saved!", "analysis saved", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // add view to parent panel
        areaController.getAreaAnalysisPanel().getLinearModelParentPanel().add(linearRegressionPanel, gridBagConstraints);
    }

    /**
     * Update text pane with description of statistical test
     *
     * @param correctionMethodName
     */
    private void updateTestDescriptionPane(String correctionMethodName) {
        String testDescription = "";
        switch (correctionMethodName) {
            case "none":
                testDescription = "No correction is applied for multiple comparisons.";
                break;
            case "bonferroni":
                testDescription = "This correction is the most stringent one; p values are multiplied for number of pairwise comparisons.";
                break;
            case "benjamini":
                testDescription = "This correction is less stringent than the Bonferroni one; the p values are first ranked from the smallest to the largest. The largest p value remains as it is. The second largest p value is multiplied by the total number of comparisons divided by its rank. This is repeated for the third p value and so on.";
                break;
            default:
                testDescription = "No information is available for the current selected correction method.";
                break;
        }
        statisticsDialog.getTestDescriptionTextPane().setText(testDescription);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = statisticsDialog.getTestDescriptionTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
    }

    /**
     * Compute Statistics for the selected Group
     *
     * @param analysisGroup
     */
    private void computeStatistics(AnalysisGroup analysisGroup, String statisticalTestName) {
        // generate summary statistics
        statisticsAnalyzer.generateSummaryStatistics(analysisGroup, statisticalTestName);
        // execute mann whitney test --- set p values matrix (no adjustment)
        statisticsAnalyzer.executePairwiseComparisons(analysisGroup, statisticalTestName);
    }

    /**
     * Show Summary Statistics in correspondent table
     *
     * @param analysisGroup
     */
    private void showSummary(AnalysisGroup analysisGroup) {
        statisticsDialog.getGroupNameLabel().setText(analysisGroup.getGroupName());
        // set model and cell renderer for statistics summary table
        StatisticalSummaryTableModel statisticalSummaryTableModel = new StatisticalSummaryTableModel(analysisGroup, areaController.getPlateConditionList());
        JTable statisticalSummaryTable = statisticsDialog.getStatisticalSummaryTable();
        statisticalSummaryTable.setModel(statisticalSummaryTableModel);
        for (int i = 1; i < statisticalSummaryTable.getColumnCount(); i++) {
            statisticalSummaryTable.getColumnModel().getColumn(i).setCellRenderer(new FormatRenderer(new DecimalFormat("#.####"), SwingConstants.CENTER));
        }
        statisticalSummaryTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
    }

    /**
     * Show p-values in correspondent table
     *
     * @param analysisGroup
     */
    private void showPValues(AnalysisGroup analysisGroup, boolean isAdjusted) {
        String statisticalTestName = statisticsDialog.getStatisticalTestComboBox().getSelectedItem().toString();
        PValuesTableModel pValuesTableModel = new PValuesTableModel(analysisGroup, areaController.getPlateConditionList(), isAdjusted);
        JTable pValuesTable = statisticsDialog.getpValuesTable();
        pValuesTable.setModel(pValuesTableModel);
        Double selectedSignLevel = (Double) statisticsDialog.getSignificanceLevelComboBox().getSelectedItem();
        // detect significances with selected alpha level
        statisticsAnalyzer.detectSignificance(analysisGroup, statisticalTestName, selectedSignLevel, isAdjusted);
        boolean[][] significances = analysisGroup.getSignificances();
        for (int i = 1; i < pValuesTable.getColumnCount(); i++) {
            pValuesTable.getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(new DecimalFormat("#.####"), significances));
        }
        pValuesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
    }

    /**
     * Add Annotations on velocity Chart A line is added horizontally between
     * two conditions that were detected to be statistically different. If lines
     * from previous analysis are present, delete them.
     */
    private void addAnnotationsOnVelocityChart(AnalysisGroup analysisGroup) {
        Stroke stroke = new BasicStroke(0.5f);
        CategoryPlot velocityPlot = velocityChartPanel.getChart().getCategoryPlot();
        DefaultStatisticalCategoryDataset dataset = (DefaultStatisticalCategoryDataset) velocityPlot.getDataset();
        Comparable rowKey = dataset.getRowKey(0);
        int counter = 30;
        boolean[][] significances = analysisGroup.getSignificances();
        // get eventual annotations already present on the plot and delete them
        List<CategoryAnnotation> annotations = velocityPlot.getAnnotations();
        if (!annotations.isEmpty()) {
            annotations.clear();
            velocityPlot.getRangeAxis().setAutoRange(true);
        }
        // check when the line has to be drawn
        for (int i = 0; i < significances.length; i++) {
            for (int j = 0; j < significances[0].length; j++) {
                // if p - value has been detected as significant
                if (significances[i][j]) {
                    // get the two conditions that were detected as significantly different
                    Comparable firstKey = dataset.getColumnKey(j);
                    Comparable secondKey = dataset.getColumnKey(i);
                    // mean values of the two conditions
                    Double value1 = (Double) dataset.getMeanValue(rowKey, firstKey);
                    Double value2 = (Double) dataset.getMeanValue(rowKey, secondKey);
                    // where the line has to start and finish (y-value)
                    double value = Math.max(value1, value2) + counter;
                    // add a line annotation on plot
                    CategoryLineAnnotation categoryLineAnnotation = new CategoryLineAnnotation(firstKey, value, secondKey, value, Color.black, stroke);
                    velocityPlot.addAnnotation(categoryLineAnnotation);
                    counter = counter + 50;
                }
            }
        }
        // add again annotation for number of replicates
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Double[] numberOfReplicates = AnalysisUtils.excludeNullValues(analysisMap.get(areaController.getPlateConditionList().get(i)).getSlopes());
            CategoryAnnotation annotation = new CategoryTextAnnotation("N " + numberOfReplicates.length, dataset.getColumnKey(i), 10);
            velocityPlot.addAnnotation(annotation);
        }
        Range range = new Range(0, velocityPlot.getRangeAxis().getRange().getUpperBound() + counter);
        velocityPlot.getRangeAxis().setRange(range);
    }

    /**
     * Initialize map with new values
     */
    private void initMap() {
        for (PlateCondition plateCondition : areaController.getPlateConditionList()) {
            analysisMap.put(plateCondition, new AreaAnalysisResults());
        }
    }

    /**
     * Get conditions according to selected rows and add them to the Analysis
     * Group
     */
    private void addGroupToAnalysis() {
        List<PlateCondition> plateConditionsList = new ArrayList<>();
        List<AreaAnalysisResults> areaAnalysisResultsList = new ArrayList<>();
        int[] selectedRows = linearRegressionPanel.getSlopesTable().getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            PlateCondition selectedCondition = areaController.getPlateConditionList().get(selectedRows[i]);
            plateConditionsList.add(selectedCondition);
            AreaAnalysisResults areaAnalysisResults = analysisMap.get(selectedCondition);
            areaAnalysisResultsList.add(areaAnalysisResults);
        }
        // we check here that at least two conditions have been selected to be part of the analysis group
        // else, the analysis does not really make sense
        if (plateConditionsList.size() > 1) {
            // make a new analysis group, with those conditions and those results
            AnalysisGroup analysisGroup = new AnalysisGroup(plateConditionsList, areaAnalysisResultsList);
            //set name for the group
            if (!linearRegressionPanel.getGroupNameTextField().getText().isEmpty()) {
                analysisGroup.setGroupName(linearRegressionPanel.getGroupNameTextField().getText());
                // set correction method to NONE by default
                analysisGroup.setCorrectionMethodName("none");
                linearRegressionPanel.getGroupNameTextField().setText("");
                // actually add the group to the analysis list
                if (!groupsBindingList.contains(analysisGroup)) {
                    groupsBindingList.add(analysisGroup);
                }
            } else {
                // ask the user to type a name for the group
                areaController.showMessage("Please type a name for the analysis group.", "no name typed for the analysis group", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // we tell the user that statistics cannot be performed on only one condition !!
            // the selection is basically ignored
            areaController.showMessage("Sorry! It is not possible to perform analysis on one condition only!\nPlease select at least two conditions.", "at least two conditions need to be chosen for analysis", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Remove a Group of Conditions
     */
    private void removeGroupFromAnalysis() {
        // selected group
        int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
        // check if an element is selected first
        if (selectedIndex != -1) {
            groupsBindingList.remove(selectedIndex);
        } else {
            showMessage("Select a group to remove from current analysis!", "remove group error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Validate analysis
     *
     * @return true if analysis was performed for each group created
     */
    private boolean validateAnalysis() {
        boolean isValid = true;
        // check if analysis was performed for each group
        for (AnalysisGroup analysisGroup : groupsBindingList) {
            if (analysisGroup.getpValuesMatrix() == null) {
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Swing Worker to generate PDF report
     */
    private class AnalysisReportSwingWorker extends SwingWorker<File, Void> {

        private File directory;
        private String reportName;

        public AnalysisReportSwingWorker(File directory, String reportName) {
            this.directory = directory;
            this.reportName = reportName;
        }

        @Override
        protected File doInBackground() throws Exception {
            // disable button
            linearRegressionPanel.getCreateReportButton().setEnabled(false);
            //set cursor to waiting one
            areaController.setCursor(Cursor.WAIT_CURSOR);
            boolean useCorrectedData = areaController.useCorrectedData();
            areaAnalysisReportController.setUseCorrectedData(useCorrectedData);
            //call the child controller to create report
            File file = areaAnalysisReportController.createAnalysisReport(directory, reportName);
            return file;
        }

        @Override
        protected void done() {
            File file = null;
            try {
                file = get();
            } catch (InterruptedException | CancellationException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            try {
                //if export to PDF was successfull, open the PDF file from the desktop
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                areaController.showMessage(ex.getMessage(), "Error while opening file", JOptionPane.ERROR_MESSAGE);
            }
            //set cursor back to default
            areaController.setCursor(Cursor.DEFAULT_CURSOR);
            // enable button
            linearRegressionPanel.getCreateReportButton().setEnabled(true);
        }
    }
}
