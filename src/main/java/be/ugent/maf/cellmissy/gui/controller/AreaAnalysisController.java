/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrectionFactory;
import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrectionFactory.correctionMethod;
import be.ugent.maf.cellmissy.analysis.SignificanceLevel;
import be.ugent.maf.cellmissy.analysis.StatisticsAnalyzer;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
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
    private ObservableList<MultipleComparisonsCorrectionFactory.correctionMethod> correctionMethodsBindingList;
    // view
    private LinearRegressionPanel linearRegressionPanel;
    private ChartPanel velocityChartPanel;
    private JDialog dialog;
    private StatisticsPanel statisticsPanel;
    // parent controller
    @Autowired
    private DataAnalysisController dataAnalysisController;
    // child controllers
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

        JFreeChart velocityChart = ChartFactory.createLineChart("Median Velocity", "", "Velocity " + "(\u00B5" + "m" + "\u00B2" + "\\min)", dataset, PlotOrientation.VERTICAL, true, true, false);
        velocityChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        CategoryPlot velocityPlot = velocityChart.getCategoryPlot();
        velocityPlot.setBackgroundPaint(Color.white);
        VelocityBarRenderer velocityBarRenderer = new VelocityBarRenderer();
        velocityBarRenderer.setErrorIndicatorPaint(Color.black);
        // set CategoryTextAnnotation to show number of replicates on top of bars
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Double[] numberOfReplicates = AnalysisUtils.excludeNullValues(analysisMap.get(dataAnalysisController.getPlateConditionList().get(i)).getSlopes());
            CategoryAnnotation annotation = new CategoryTextAnnotation("N " + numberOfReplicates.length, dataset.getColumnKey(i), 10);
            velocityPlot.addAnnotation(annotation);
        }

        velocityPlot.setRenderer(velocityBarRenderer);
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
        dialog.setAlwaysOnTop(true);
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

        for (correctionMethod method : correctionMethod.values()) {
            statisticsPanel.getCorrectionMethodsComboBox().addItem(method);
        }

        statisticsPanel.getSignificanceLevelComboBox().setSelectedIndex(0);
        statisticsPanel.getCorrectionMethodsComboBox().setSelectedIndex(0);
        bindingGroup.bind();

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
         * Execute a Mann Whitney Test on selected Analysis Group
         */
        linearRegressionPanel.getStatisticsButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                // compute statistics
                computeStatistics(selectedGroup);
                // show statistics in tables
                showSummary(selectedGroup);
                showPValues(selectedGroup);
                // add new panel 
                dialog.getContentPane().add(statisticsPanel, gridBagConstraints);
                // show the dialog
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        /**
         * Correct for multiple comparisons
         */
        statisticsPanel.getCorrectionButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                // adjust p values
                statisticsAnalyzer.correctForMultipleComparisons(selectedGroup, (correctionMethod) statisticsPanel.getCorrectionMethodsComboBox().getSelectedItem());
                showPValues(selectedGroup);
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
                    for (int i = 1; i < statisticsPanel.getpValuesTable().getColumnCount(); i++) {
                        statisticsPanel.getpValuesTable().getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(selectedSignLevel, new DecimalFormat("#.####")));
                    }
                    statisticsPanel.getpValuesTable().repaint();
                }
            }
        });

        // add view to parent panel
        dataAnalysisController.getAreaAnalysisPanel().getLinearModelParentPanel().add(linearRegressionPanel, gridBagConstraints);
    }

    /**
     * Show Statistics for the selected Group
     * @param analysisGroup 
     */
    private void computeStatistics(AnalysisGroup analysisGroup) {
        // generate summary statistics
        statisticsAnalyzer.generateSummaryStatistics(analysisGroup);
        // execute mann whitney test
        statisticsAnalyzer.executePairwiseComparisons(analysisGroup);
    }

    /**
     * Show Summary Statistics in correspondent table
     * @param analysisGroup 
     */
    private void showSummary(AnalysisGroup analysisGroup) {
        statisticsPanel.getGroupNameLabel().setName(analysisGroup.getGroupName());
        // set model and cell renderer for statistics summary table
        JTable summaryTable = statisticsPanel.getSummaryTable();
        summaryTable.setModel(new StatisticalSummaryTableModel(analysisGroup));
        for (int i = 1; i < summaryTable.getColumnCount(); i++) {
            summaryTable.getColumnModel().getColumn(i).setCellRenderer(new FormatRenderer(new DecimalFormat("#.####")));
        }
    }

    /**
     * Show p-values in correspondent table
     * @param analysisGroup 
     */
    private void showPValues(AnalysisGroup analysisGroup) {
        // set model and cell renderer for p-values table
        JTable pValuesTable = statisticsPanel.getpValuesTable();
        pValuesTable.setModel(new PValuesTableModel(analysisGroup, dataAnalysisController.getPlateConditionList()));
        Double selectedSignLevel = (Double) statisticsPanel.getSignificanceLevelComboBox().getSelectedItem();
        for (int i = 1; i < pValuesTable.getColumnCount(); i++) {
            pValuesTable.getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(selectedSignLevel, new DecimalFormat("#.####")));
        }
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
        if (linearRegressionPanel.getGroupNameTextField().getText() != null) {
            analysisGroup.setGroupName(linearRegressionPanel.getGroupNameTextField().getText());
        }
        linearRegressionPanel.getGroupNameTextField().setText("");
        if (!groupsBindingList.contains(analysisGroup)) {
            groupsBindingList.add(analysisGroup);
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
}
