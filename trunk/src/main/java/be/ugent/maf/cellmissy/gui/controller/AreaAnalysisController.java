/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.AreaAnalyser;
import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.experiment.analysis.LinearRegressionPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.VelocityBarRenderer;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("areaAnalysisController")
public class AreaAnalysisController {

    // model
    private Map<PlateCondition, AreaAnalysisResults> analysisMap;
    // view
    private LinearRegressionPanel linearRegressionPanel;
    private ChartPanel velocityChartPanel;
    // parent controller
    @Autowired
    private DataAnalysisController dataAnalysisController;
    // child controllers
    //services
    @Autowired
    private AreaAnalyser areaAnalyser;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        analysisMap = new LinkedHashMap<>();
        //init views
        initLinearRegressionPanel();
    }

    /**
     * Show Results from Linear Regression Model in a table
     */
    public void showLinearModelInTable() {
        initMap();
        List<Double[]> slopesList = new ArrayList();
        List<Double[]> coefficientsList = new ArrayList();
        List<Double> meanSlopes = new ArrayList();
        List<Double> madSlopes = new ArrayList();
        // go through all conditions in map and estimate linear model for each of them
        for (PlateCondition plateCondition : analysisMap.keySet()) {
            estimateLinearModel(plateCondition);
            slopesList.add(analysisMap.get(plateCondition).getSlopes());
            coefficientsList.add(analysisMap.get(plateCondition).getGoodnessOfFit());
            meanSlopes.add(analysisMap.get(plateCondition).getMeanSlope());
            madSlopes.add(analysisMap.get(plateCondition).getMadSlope());
        }
        Object[][] data = new Object[analysisMap.keySet().size()][slopesList.get(0).length + 3];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 1; columnIndex < data[0].length - 2; columnIndex++) {
                if (slopesList.get(rowIndex)[columnIndex - 1] != null && coefficientsList.get(rowIndex)[columnIndex - 1] != null) {
                    double slope = AnalysisUtils.roundThreeDecimals(slopesList.get(rowIndex)[columnIndex - 1]);
                    double coefficient = AnalysisUtils.roundThreeDecimals(coefficientsList.get(rowIndex)[columnIndex - 1]);
                    data[rowIndex][columnIndex] = slope + " (" + coefficient + ")";
                }
            }
            data[rowIndex][0] = "Cond " + (rowIndex + 1);
            data[rowIndex][data[0].length - 2] = meanSlopes.get(rowIndex);
            data[rowIndex][data[0].length - 1] = madSlopes.get(rowIndex);
        }
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "Condition";
        for (int i = 1; i < columnNames.length - 2; i++) {
            columnNames[i] = "Repl " + i;
        }
        columnNames[columnNames.length - 2] = "Median";
        columnNames[columnNames.length - 1] = "MAD";

        linearRegressionPanel.getSlopesTable().setModel(new DefaultTableModel(data, columnNames));

        //set format renderer only for last two columns together with less width
        for (int columnIndex = columnNames.length - 2; columnIndex < linearRegressionPanel.getSlopesTable().getColumnCount(); columnIndex++) {
            linearRegressionPanel.getSlopesTable().getColumnModel().getColumn(columnIndex).setCellRenderer(new FormatRenderer(dataAnalysisController.getFormat()));
            linearRegressionPanel.getSlopesTable().getColumnModel().getColumn(columnIndex).setMaxWidth(50);
            linearRegressionPanel.getSlopesTable().getColumnModel().getColumn(columnIndex).setPreferredWidth(50);
        }
        linearRegressionPanel.getSlopesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        linearRegressionPanel.getSlopesTable().setRowSelectionInterval(0, linearRegressionPanel.getSlopesTable().getRowCount() - 1);
    }
//
//    /**
//     * show Bar charts for area velocity
//     */
//    public void showVelocityBars() {
//        TableModel model = linearRegressionPanel.getSlopesTable().getModel();
//        int[] selectedRows = linearRegressionPanel.getSlopesTable().getSelectedRows();
//        int columnCount = model.getColumnCount();
//        double[][] tableData = new double[columnCount - 1][];
//        for (int i = 1; i < columnCount; i++) {
//            List<Double> tempList = new ArrayList<>();
//            for (int j : selectedRows) {
//                if (model.getValueAt(j, i) != null) {
//                    tempList.add((double) model.getValueAt(j, i));
//                }
//            }
//            tableData[i - 1] = ArrayUtils.toPrimitive(tempList.toArray(new Double[tempList.size()]));
//        }
//        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
//        double[] meanVelocities = tableData[6];
//        double[] standardDeviations = tableData[7];
//        for (int i = 0; i < meanVelocities.length; i++) {
//            dataset.add(meanVelocities[i], standardDeviations[i], "Conditions", "Condition " + (selectedRows[i] + 1));
//        }
//
//        JFreeChart velocityChart = ChartFactory.createLineChart("Median Velocity", "", "Velocity " + "(\u00B5" + "m" + "\u00B2" + "\\min)", dataset, PlotOrientation.VERTICAL, true, true, false);
//        velocityChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
//        CategoryPlot velocityPlot = velocityChart.getCategoryPlot();
//        velocityPlot.setBackgroundPaint(Color.white);
//        VelocityBarRenderer velocityBarRenderer = new VelocityBarRenderer();
//        velocityBarRenderer.setErrorIndicatorPaint(Color.black);
//        velocityBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
//        velocityBarRenderer.setBaseItemLabelsVisible(true);
//        velocityBarRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));
//        velocityPlot.setRenderer(velocityBarRenderer);
//
//        velocityPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//        JFreeChartUtils.setShadowVisible(velocityChart, false);
//        velocityChartPanel.setChart(velocityChart);
//        linearRegressionPanel.getChartParentPanel().add(velocityChartPanel, gridBagConstraints);
//        linearRegressionPanel.getChartParentPanel().repaint();
//    }

    private void showVelocityChart() {
        int[] selectedRows = linearRegressionPanel.getSlopesTable().getSelectedRows();
        double[] meanSlopes = new double[selectedRows.length];
        double[] madSlopes = new double[selectedRows.length];
        for (int i : selectedRows) {
            AreaAnalysisResults areaAnalysisResults = analysisMap.get(dataAnalysisController.getPlateConditionList().get(i));
            double meanSlope = areaAnalysisResults.getMeanSlope();
            double madSlope = areaAnalysisResults.getMadSlope();
            meanSlopes[i] = meanSlope;
            madSlopes[i] = madSlope;
        }
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
        for (int i = 0; i < meanSlopes.length; i++) {
            dataset.add(meanSlopes[i], madSlopes[i], "Conditions", "Condition " + (selectedRows[i] + 1));
        }

        JFreeChart velocityChart = ChartFactory.createLineChart("Median Velocity", "", "Velocity " + "(\u00B5" + "m" + "\u00B2" + "\\min)", dataset, PlotOrientation.VERTICAL, true, true, false);
        velocityChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        CategoryPlot velocityPlot = velocityChart.getCategoryPlot();
        velocityPlot.setBackgroundPaint(Color.white);
        VelocityBarRenderer velocityBarRenderer = new VelocityBarRenderer();
        velocityBarRenderer.setErrorIndicatorPaint(Color.black);
        velocityBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        velocityBarRenderer.setBaseItemLabelsVisible(true);
        velocityBarRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));
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

        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = dataAnalysisController.getPreProcessingMap();
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        AreaAnalysisResults areaAnalysisResults = analysisMap.get(plateCondition);
        areaAnalyser.estimateLinearModel(areaPreProcessingResults, areaAnalysisResults, dataAnalysisController.getTimeFrames());
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
        /**
         * List selection Listener for linear model results Table
         * show bar charts according to user selection in model
         */
        linearRegressionPanel.getSlopesTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                showVelocityChart();
            }
        });
        // add view to parent panel
        dataAnalysisController.getAreaAnalysisPanel().getLinearModelParentPanel().add(linearRegressionPanel, gridBagConstraints);
    }

    /**
     * Initialize map with new values
     */
    private void initMap() {
        for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
            analysisMap.put(plateCondition, new AreaAnalysisResults());
        }
    }
}
