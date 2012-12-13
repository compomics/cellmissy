/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrectionFactory.CorrectionMethod;
import be.ugent.maf.cellmissy.analysis.SignificanceLevel;
import be.ugent.maf.cellmissy.analysis.StatisticsAnalyzer;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AnalysisReport;
import be.ugent.maf.cellmissy.gui.experiment.analysis.LinearRegressionPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.StatisticsPanel;
import be.ugent.maf.cellmissy.gui.view.PValuesTableModel;
import be.ugent.maf.cellmissy.gui.view.StatisticalSummaryTableModel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.PValuesTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.VelocityBarRenderer;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.pdfbox.exceptions.COSVisitorException;
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

    // model
    private BindingGroup bindingGroup;
    private Map<PlateCondition, AreaAnalysisResults> analysisMap;
    private ObservableList<AnalysisGroup> groupsBindingList;
    private ObservableList<Double> significanceLevelsBindingList;
    // view
    private LinearRegressionPanel linearRegressionPanel;
    private ChartPanel velocityChartPanel;
    private JDialog dialog;
    private StatisticsPanel statisticsPanel;
    // parent controller
    @Autowired
    private DataAnalysisController dataAnalysisController;
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
        //init child controller
        areaAnalysisReportController.init();
        //init views
        initLinearRegressionPanel();
    }
    
    public Experiment getExperiment(){
        return dataAnalysisController.getExperiment();
    }

    public ChartPanel getGlobalAreaChartPanel(){
        return dataAnalysisController.getGlobalAreaChartPanel();
    }
    
    /**
     * Show Results from Linear Regression Model in a table
     */
    public void showLinearModelInTable() {
        // initialize map for keeping the results
        initMap();
        List<Double[]> slopesList = new ArrayList();
        List<Double[]> coefficientsList = new ArrayList();
        List<Double> meanSlopesList = new ArrayList();
        List<Double> madSlopesList = new ArrayList();

        // go through all conditions in map and estimate linear model for each of them
        for (PlateCondition plateCondition : analysisMap.keySet()) {
            estimateLinearModel(plateCondition);
            slopesList.add((analysisMap.get(plateCondition).getSlopes()));
            coefficientsList.add((analysisMap.get(plateCondition).getGoodnessOfFit()));
            meanSlopesList.add(analysisMap.get(plateCondition).getMeanSlope());
            madSlopesList.add(analysisMap.get(plateCondition).getMadSlope());
        }
        // data for table model: number of rows equal to number of conditions, number of columns equal to maximum number of replicates + 3
        // first column with conditions, last two with mean and mad values
        int maximumNumberOfReplicates = AnalysisUtils.getMaximumNumberOfReplicates(dataAnalysisController.getPlateConditionList());
        Object[][] data = new Object[analysisMap.keySet().size()][maximumNumberOfReplicates + 3];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 1; columnIndex < maximumNumberOfReplicates + 1; columnIndex++) {
                if (slopesList.get(rowIndex)[columnIndex - 1] != null && coefficientsList.get(rowIndex)[columnIndex - 1] != null) {
                    // round to three decimals slopes and coefficients
                    double slope = AnalysisUtils.roundThreeDecimals(slopesList.get(rowIndex)[columnIndex - 1]);
                    double coefficient = AnalysisUtils.roundThreeDecimals(coefficientsList.get(rowIndex)[columnIndex - 1]);
                    // show in table slope + (coefficient)
                    data[rowIndex][columnIndex] = slope + " (" + coefficient + ")";
                } else {
                    data[rowIndex][columnIndex] = "excluded";
                }
            }
            // first column contains conditions names
            data[rowIndex][0] = rowIndex + 1;
            // last 2 columns contain mean slopes, mad values
            data[rowIndex][data[0].length - 2] = meanSlopesList.get(rowIndex);
            data[rowIndex][data[0].length - 1] = madSlopesList.get(rowIndex);
        }
        // array of column names for table model
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "COND";
        for (int i = 1; i < columnNames.length - 2; i++) {
            columnNames[i] = "Repl " + i;
        }
        columnNames[columnNames.length - 2] = "Median";
        columnNames[columnNames.length - 1] = "MAD";
        // set model of table
        linearRegressionPanel.getSlopesTable().setModel(new DefaultTableModel(data, columnNames));
        //set format renderer only for last two columns together with less width
        for (int columnIndex = columnNames.length - 2; columnIndex < linearRegressionPanel.getSlopesTable().getColumnCount(); columnIndex++) {
            linearRegressionPanel.getSlopesTable().getColumnModel().getColumn(columnIndex).setCellRenderer(new FormatRenderer(dataAnalysisController.getFormat()));
            linearRegressionPanel.getSlopesTable().getColumnModel().getColumn(columnIndex).setMaxWidth(50);
            linearRegressionPanel.getSlopesTable().getColumnModel().getColumn(columnIndex).setPreferredWidth(50);
        }
        linearRegressionPanel.getSlopesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        // select by default all conditions: to show all bars in the velocity chart
        linearRegressionPanel.getSlopesTable().setRowSelectionInterval(0, linearRegressionPanel.getSlopesTable().getRowCount() - 1);
    }

    /**
     * Plot bar charts for velocity
     */
    private void showVelocityChart() {
        // dataset for chart
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
        int[] selectedRows = linearRegressionPanel.getSlopesTable().getSelectedRows();
        double[] meanSlopes = new double[selectedRows.length];
        double[] semSlopes = new double[selectedRows.length];
        for (int i = 0; i < meanSlopes.length; i++) {
            AreaAnalysisResults areaAnalysisResults = analysisMap.get(dataAnalysisController.getPlateConditionList().get(selectedRows[i]));
            double meanSlope = areaAnalysisResults.getMeanSlope();
            double madSlopes = areaAnalysisResults.getMadSlope();
            meanSlopes[i] = meanSlope;
            semSlopes[i] = madSlopes;
            dataset.add(meanSlopes[i], semSlopes[i], "Conditions", "Condition " + (selectedRows[i] + 1));
        }

        JFreeChart velocityChart = ChartFactory.createLineChart("Median Velocity", "", "Velocity " + "(\u00B5" + "m" + "\u00B2" + "\\min)", dataset, PlotOrientation.VERTICAL, false, false, false);
        velocityChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        CategoryPlot velocityPlot = velocityChart.getCategoryPlot();
        velocityPlot.setBackgroundPaint(Color.white);
        VelocityBarRenderer velocityBarRenderer = new VelocityBarRenderer();
        velocityBarRenderer.setErrorIndicatorPaint(Color.black);
        velocityPlot.setRenderer(velocityBarRenderer);

        // set CategoryTextAnnotation to show number of replicates on top of bars
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Double[] numberOfReplicates = AnalysisUtils.excludeNullValues(analysisMap.get(dataAnalysisController.getPlateConditionList().get(i)).getSlopes());
            CategoryAnnotation annotation = new CategoryTextAnnotation("N " + numberOfReplicates.length, dataset.getColumnKey(i), 10);
            velocityPlot.addAnnotation(annotation);
        }

        velocityPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        JFreeChartUtils.setShadowVisible(velocityChart, false);
        velocityChartPanel.setChart(velocityChart);
        linearRegressionPanel.getChartParentPanel().add(velocityChartPanel, gridBagConstraints);
        linearRegressionPanel.getChartParentPanel().repaint();
    }

    /**
     * Estimate Linear Regression Model
     * @param plateCondition 
     */
    private void estimateLinearModel(PlateCondition plateCondition) {
        // get the pre-processing results from main controller
        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = dataAnalysisController.getPreProcessingMap();
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        AreaAnalysisResults areaAnalysisResults = analysisMap.get(plateCondition);
        areaAnalyzer.estimateLinearModel(areaPreProcessingResults, areaAnalysisResults, dataAnalysisController.getTimeFrames());
    }

    /**
     * Initialize view
     */
    private void initLinearRegressionPanel() {
        linearRegressionPanel = new LinearRegressionPanel();
        // control opaque property of table
        linearRegressionPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);
        // init chart panel
        velocityChartPanel = new ChartPanel(null);
        velocityChartPanel.setOpaque(false);
        statisticsPanel = new StatisticsPanel();
        dialog = new JDialog();
        dialog.setAlwaysOnTop(false);
        dialog.setModal(true);
        dialog.getContentPane().setBackground(Color.white);
        dialog.getContentPane().setLayout(new GridBagLayout());
        //center the dialog on the main screen
        dialog.setLocationRelativeTo(null);
        dialog.setTitle("Statistics");

        statisticsPanel.getSummaryTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        statisticsPanel.getSummaryScrollPane().getViewport().setBackground(Color.white);
        statisticsPanel.getpValuesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        statisticsPanel.getpValuesScrollPane().getViewport().setBackground(Color.white);

        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = statisticsPanel.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);

        groupsBindingList = ObservableCollections.observableList(new ArrayList<AnalysisGroup>());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, groupsBindingList, linearRegressionPanel.getGroupsList());
        bindingGroup.addBinding(jListBinding);

        List<Double> significanceLevels = new ArrayList<>();
        for (SignificanceLevel significanceLevel : SignificanceLevel.values()) {
            significanceLevels.add(significanceLevel.getValue());
        }
        significanceLevelsBindingList = ObservableCollections.observableList(significanceLevels);
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, significanceLevelsBindingList, statisticsPanel.getSignificanceLevelComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        for (CorrectionMethod method : CorrectionMethod.values()) {
            statisticsPanel.getCorrectionMethodsComboBox().addItem(method);
        }
        //significance level to 0.05
        statisticsPanel.getSignificanceLevelComboBox().setSelectedIndex(1);

        /**
         * List selection Listener for linear model results Table
         * show bar charts according to user selection in model
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
                AnalysisReportSwingWorker analysisReportSwingWorker = new AnalysisReportSwingWorker();
                analysisReportSwingWorker.execute();
            }
        });

        /**
         * Execute a Mann Whitney Test on selected Analysis Group
         */
        linearRegressionPanel.getStatisticsButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                // check that an analysis group is being selected
                if (selectedIndex != -1) {
                    AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                    // compute statistics
                    computeStatistics(selectedGroup);
                    // show statistics in tables
                    showSummary(selectedGroup);
                    // set the correction combobox to the one already chosen
                    statisticsPanel.getCorrectionMethodsComboBox().setSelectedItem((Object) selectedGroup.getCorrectionMethod());
                    if (selectedGroup.getCorrectionMethod() == CorrectionMethod.NONE) {
                        // by default show p-values without adjustment
                        showPValues(selectedGroup, false);
                    } else {
                        // show p values with adjustement
                        showPValues(selectedGroup, true);
                    }
                    // add new panel 
                    dialog.getContentPane().add(statisticsPanel, gridBagConstraints);
                    // show the dialog
                    dialog.pack();
                    dialog.setVisible(true);
                }
            }
        });

        /**
         * Update button text if analysis was already done
         */
        linearRegressionPanel.getGroupsList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int locationToIndex = linearRegressionPanel.getGroupsList().locationToIndex(e.getPoint());
                if (locationToIndex != -1) {
                    if (groupsBindingList.get(locationToIndex).getpValuesMatrix() == null) {
                        linearRegressionPanel.getStatisticsButton().setText("Perform Statistical Analysis...");
                    } else {
                        linearRegressionPanel.getStatisticsButton().setText("Modify Statistical Analysis...");
                    }
                }
            }
        });

        /**
         * Refresh p value table with current selected significance of level
         */
        statisticsPanel.getSignificanceLevelComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (statisticsPanel.getSignificanceLevelComboBox().getSelectedIndex() != -1) {
                    Double selectedSignLevel = (Double) statisticsPanel.getSignificanceLevelComboBox().getSelectedItem();
                    AnalysisGroup selectedGroup = groupsBindingList.get(linearRegressionPanel.getGroupsList().getSelectedIndex());
                    boolean isAdjusted;
                    if (selectedGroup.getCorrectionMethod() == CorrectionMethod.NONE) {
                        isAdjusted = false;
                    } else {
                        isAdjusted = true;
                    }
                    statisticsAnalyzer.detectSignificance(selectedGroup, selectedSignLevel, isAdjusted);
                    boolean[][] significances = selectedGroup.getSignificances();
                    for (int i = 1; i < statisticsPanel.getpValuesTable().getColumnCount(); i++) {
                        statisticsPanel.getpValuesTable().getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(new DecimalFormat("#.####"), significances));
                    }
                    statisticsPanel.getpValuesTable().repaint();
                }
            }
        });

        /**
         * Apply correction for multiple comparisons
         */
        statisticsPanel.getCorrectionMethodsComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                if (selectedIndex != -1) {
                    AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                    CorrectionMethod correctionMethod = (CorrectionMethod) statisticsPanel.getCorrectionMethodsComboBox().getSelectedItem();
                    // update test description
                    updateTestDescriptionPane(correctionMethod);
                    // if the correction method is not "NONE"
                    if (correctionMethod != CorrectionMethod.NONE) {
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
        //multiple comparison correction: NONE
        statisticsPanel.getCorrectionMethodsComboBox().setSelectedItem((Object) CorrectionMethod.NONE);
        updateTestDescriptionPane(CorrectionMethod.NONE);

        /**
         * Save analysis
         */
        statisticsPanel.getSaveAnalysisButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // analysis group
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                if (selectedIndex != -1) {
                    AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                    // set correction method (summary statistics, p-values and adjusted p-values are already set)
                    selectedGroup.setCorrectionMethod((CorrectionMethod) statisticsPanel.getCorrectionMethodsComboBox().getSelectedItem());
                    //show message to the user
                    dataAnalysisController.showMessage("Analysis was saved", 1);
                    addAnnotationsOnVelocityChart(selectedGroup);
                }
            }
        });

        // add view to parent panel
        dataAnalysisController.getAreaAnalysisPanel().getLinearModelParentPanel().add(linearRegressionPanel, gridBagConstraints);
    }

    /**
     * Update text pane with description of statistical test
     * @param correctionMethod 
     */
    private void updateTestDescriptionPane(CorrectionMethod correctionMethod) {
        String testDescription = "";
        if (correctionMethod == CorrectionMethod.NONE) {
            testDescription = "No correction is applied.";
        } else if (correctionMethod == CorrectionMethod.BONFERRONI) {
            testDescription = "This correction is the stringent one; p values are multiplied for number of pairwise comparisons.";
        } else if (correctionMethod == CorrectionMethod.BENJAMINI) {
            testDescription = "This correction is less stringent than the Bonferroni one; the p values are first ranked from the smallest to the largest. The largest p value remains as it is. The second largest p value is multiplied by the total number of comparisons divided by its rank. This is repeated for the third p value and so on.";
        }
        statisticsPanel.getTestDescriptionTextPane().setText(testDescription);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = statisticsPanel.getTestDescriptionTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
    }

    /**
     * Compute Statistics for the selected Group
     * @param analysisGroup 
     */
    private void computeStatistics(AnalysisGroup analysisGroup) {
        // generate summary statistics
        statisticsAnalyzer.generateSummaryStatistics(analysisGroup);
        // execute mann whitney test --- set p values matrix (no adjustment)
        statisticsAnalyzer.executePairwiseComparisons(analysisGroup);
    }

    /**
     * Show Summary Statistics in correspondent table
     * @param analysisGroup 
     */
    private void showSummary(AnalysisGroup analysisGroup) {
        statisticsPanel.getGroupNameLabel().setText(analysisGroup.getGroupName());
        // set model and cell renderer for statistics summary table
        JTable summaryTable = statisticsPanel.getSummaryTable();
        summaryTable.setModel(new StatisticalSummaryTableModel(analysisGroup, dataAnalysisController.getPlateConditionList()));
        for (int i = 1; i < summaryTable.getColumnCount(); i++) {
            summaryTable.getColumnModel().getColumn(i).setCellRenderer(new FormatRenderer(new DecimalFormat("#.####")));
        }
    }

    /**
     * Show p-values in correspondent table
     * @param analysisGroup 
     */
    private void showPValues(AnalysisGroup analysisGroup, boolean isAdjusted) {
        // set model and cell renderer for p-values table
        JTable pValuesTable = statisticsPanel.getpValuesTable();
        pValuesTable.setModel(new PValuesTableModel(analysisGroup, dataAnalysisController.getPlateConditionList(), isAdjusted));
        Double selectedSignLevel = (Double) statisticsPanel.getSignificanceLevelComboBox().getSelectedItem();
        // detect significances with selected alpha level
        statisticsAnalyzer.detectSignificance(analysisGroup, selectedSignLevel, isAdjusted);
        boolean[][] significances = analysisGroup.getSignificances();
        for (int i = 1; i < pValuesTable.getColumnCount(); i++) {
            pValuesTable.getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(new DecimalFormat("#.####"), significances));
        }
    }

    /**
     * Add Annotations on velocity Chart
     * A line is added horizontally between two conditions that were detected to be statistically different.
     * If lines from previous analysis are present, delete them. 
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
            Double[] numberOfReplicates = AnalysisUtils.excludeNullValues(analysisMap.get(dataAnalysisController.getPlateConditionList().get(i)).getSlopes());
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
        for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
            analysisMap.put(plateCondition, new AreaAnalysisResults());
        }
    }

    /**
     * Get conditions according to selected rows and add them to the groupsList
     */
    private void addGroupToAnalysis() {
        List<PlateCondition> plateConditionsList = new ArrayList<>();
        List<AreaAnalysisResults> areaAnalysisResultsList = new ArrayList<>();
        int[] selectedRows = linearRegressionPanel.getSlopesTable().getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            PlateCondition selectedCondition = dataAnalysisController.getPlateConditionList().get(selectedRows[i]);
            plateConditionsList.add(selectedCondition);
            AreaAnalysisResults areaAnalysisResults = analysisMap.get(selectedCondition);
            areaAnalysisResultsList.add(areaAnalysisResults);
        }
        // make a new analysis group, with those conditions and those results
        AnalysisGroup analysisGroup = new AnalysisGroup(plateConditionsList, areaAnalysisResultsList);
        //set name for the group
        if (!linearRegressionPanel.getGroupNameTextField().getText().isEmpty()) {
            analysisGroup.setGroupName(linearRegressionPanel.getGroupNameTextField().getText());
            // set correction method to NONE by default
            analysisGroup.setCorrectionMethod(CorrectionMethod.NONE);
            linearRegressionPanel.getGroupNameTextField().setText("");
            // actually add the group to the analysis list
            if (!groupsBindingList.contains(analysisGroup)) {
                groupsBindingList.add(analysisGroup);
            }
        } else {
            // ask the user to type a name for the group
            dataAnalysisController.showMessage("Please choose a name for the analysis group.", 1);
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
        }
    }

    /**
     * 
     * @throws IOException
     * @throws COSVisitorException 
     */
    private void generatePdfReport() throws IOException, COSVisitorException {
        AnalysisReport analysisReport = new AnalysisReport(dataAnalysisController.getExperiment(), dataAnalysisController.getPreProcessingMap(), analysisMap, groupsBindingList);
    }

    /**
     * Swing Worker to generate PDF report
     */
    private class AnalysisReportSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            //set cursor to waiting one
            dataAnalysisController.setCursor(Cursor.WAIT_CURSOR);
            //call the child controller to create report
            areaAnalysisReportController.createAnalysisReport();
            return null;
        }

        @Override
        protected void done() {
            //set cursor back to default
            dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
            // open the created pdf file
        }
    }
}
