/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.StatisticsAnalyzer;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.experiment.analysis.LinearRegressionPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.MannWhitneyTestPanel;
import be.ugent.maf.cellmissy.gui.view.PValuesTableModel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.GroupsListRenderer;
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
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
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
    //private List<List<PlateCondition>> groupsList;
    private ObservableList<AnalysisGroup> groupsBindingList;
    // view
    private LinearRegressionPanel linearRegressionPanel;
    private ChartPanel velocityChartPanel;
    private JDialog dialog;
    private MannWhitneyTestPanel mannWhitneyTestPanel;
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
        mannWhitneyTestPanel = new MannWhitneyTestPanel();
        dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.getContentPane().setBackground(Color.white);
        dialog.getContentPane().setLayout(new GridBagLayout());
        //center the dialog on the main screen
        dialog.setLocationRelativeTo(null);

        mannWhitneyTestPanel.getpValuesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        mannWhitneyTestPanel.getpValuesScrollPane().getViewport().setBackground(Color.white);
        groupsBindingList = ObservableCollections.observableList(new ArrayList<AnalysisGroup>());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, groupsBindingList, linearRegressionPanel.getGroupsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        // set renderer for gropus list
        linearRegressionPanel.getGroupsList().setCellRenderer(new GroupsListRenderer(groupsBindingList));

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
        linearRegressionPanel.getMannWTestButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                // execute the test for the selected group of conditions
                statisticsAnalyzer.computePValues(selectedGroup);
                Double[][] pValuesMatrix = selectedGroup.getpValuesMatrix();
                // show the p-values in table
                showPValuesInTable(pValuesMatrix);
                // add new panel 
                dialog.getContentPane().add(mannWhitneyTestPanel, gridBagConstraints);
                // show the dialog
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        /**
         * Correct for multiple comparisons
         */
        mannWhitneyTestPanel.getCorrectionButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = linearRegressionPanel.getGroupsList().getSelectedIndex();
                AnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                // adjust p values
                statisticsAnalyzer.adjustPValues(selectedGroup);
                Double[][] adjustedPValues = selectedGroup.getAdjustedPValues();
                // show adjusted p values in a table
                showPValuesInTable(adjustedPValues);
            }
        });

        // add view to parent panel
        dataAnalysisController.getAreaAnalysisPanel().getLinearModelParentPanel().add(linearRegressionPanel, gridBagConstraints);
    }

    /**
     * Show P values in a table for a certain analysis group
     * @param analysisGroup 
     */
    private void showPValuesInTable(Double[][] pValues) {

        JTable pValuesTable = mannWhitneyTestPanel.getpValuesTable();
        pValuesTable.setModel(new PValuesTableModel(pValues));
        for (int i = 1; i < pValuesTable.getColumnCount(); i++) {
            pValuesTable.getColumnModel().getColumn(i).setCellRenderer(new FormatRenderer(new DecimalFormat("#.####")));
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
