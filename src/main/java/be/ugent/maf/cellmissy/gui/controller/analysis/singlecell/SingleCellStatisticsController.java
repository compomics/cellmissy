/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.SignificanceLevel;
import be.ugent.maf.cellmissy.analysis.factory.MultipleComparisonsCorrectionFactory;
import be.ugent.maf.cellmissy.analysis.factory.StatisticsTestFactory;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellStatisticsAnalyzer;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.AnalysisPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.PValuesTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.SingleCellPValuesTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.SingleCellStatSummaryTableModel;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * A controller to take care of the logic for the single cell statistics.
 *
 * @author Paola
 */
@Controller("singleCellStatisticsController")
public class SingleCellStatisticsController {

    private static final Logger LOG = Logger.getLogger(SingleCellStatisticsController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<SingleCellAnalysisGroup> groupsBindingList;
    // view
    // parent controller
    @Autowired
    private SingleCellAnalysisController singleCellAnalysisController;
    // child controllers
    // services
    @Autowired
    private SingleCellStatisticsAnalyzer singleCellStatisticsAnalyzer;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        // initialize the main view
        initMainView();
    }

    /**
     * Update the list with conditions.
     */
    public void updateConditionList() {
        JList conditionList = singleCellAnalysisController.getAnalysisPanel().getConditionList();        
        DefaultListModel model = (DefaultListModel) conditionList.getModel();
        model.clear();
        if (singleCellAnalysisController.isFilteredData()) {
            singleCellAnalysisController.getFilteringMap().keySet().stream().forEach((conditionDataHolder) -> {
                model.addElement(conditionDataHolder.getPlateCondition());
            });
        } else {
            singleCellAnalysisController.getPreProcessingMap().values().stream().forEach((conditionDataHolder) -> {
                model.addElement(conditionDataHolder.getPlateCondition());
            });
        }
    }

    /**
     * Initialize main view.
     */
    private void initMainView() {
        // the view is kept in the parent controllers
        AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        analysisPanel.getConditionList().setModel(new DefaultListModel());
        // customize tables
        analysisPanel.getStatTable().getTableHeader().setReorderingAllowed(false);
        analysisPanel.getStatTable().getTableHeader().setReorderingAllowed(false);
        analysisPanel.getComparisonTable().setFillsViewportHeight(true);
        analysisPanel.getComparisonTable().setFillsViewportHeight(true);

        // init binding
        groupsBindingList = ObservableCollections.observableList(new ArrayList<SingleCellAnalysisGroup>());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, groupsBindingList, analysisPanel.getAnalysisGroupList());
        bindingGroup.addBinding(jListBinding);

        // fill in combo box
        List<Double> significanceLevels = new ArrayList<>();
        for (SignificanceLevel significanceLevel : SignificanceLevel.values()) {
            significanceLevels.add(significanceLevel.getValue());
        }
        ObservableList<Double> significanceLevelsBindingList = ObservableCollections.observableList(significanceLevels);
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, significanceLevelsBindingList,
                analysisPanel.getSignLevelComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        // add the NONE (default) correction method
        // when the none is selected, CellMissy does not correct for multiple hypotheses
        analysisPanel.getCorrectionComboBox().addItem("none");
        // fill in combo box: get all the correction methods from the factory
        Set<String> correctionBeanNames = MultipleComparisonsCorrectionFactory.getInstance().getCorrectionBeanNames();
        correctionBeanNames.stream().forEach((correctionBeanName) -> {
            analysisPanel.getCorrectionComboBox().addItem(correctionBeanName);
        });
        // do the same for the statistical tests
        Set<String> statisticsCalculatorBeanNames = StatisticsTestFactory.getInstance().getStatisticsCalculatorBeanNames();
        statisticsCalculatorBeanNames.stream().forEach((testName) -> {
            analysisPanel.getStatTestComboBox().addItem(testName);
        });
        //significance level to 0.05
        analysisPanel.getSignLevelComboBox().setSelectedIndex(1);
        // add parameters to perform analysis on
        analysisPanel.getParameterComboBox().addItem("cell speed");
        analysisPanel.getParameterComboBox().addItem("cell direct");
        analysisPanel.getParameterComboBox().addItem("accumulated distance");
        analysisPanel.getParameterComboBox().addItem("euclidian distance");

        /**
         * Add a group to analysis
         */
        analysisPanel.getAddGroupButton().addActionListener((ActionEvent e) -> {
            // from selected conditions make a new group and add it to the list
            addGroupToAnalysis();
        });

        /**
         * Remove a Group from analysis
         */
        analysisPanel.getRemoveGroupButton().addActionListener((ActionEvent e) -> {
            // remove the selected group from list
            removeGroupFromAnalysis();
        });
        /**
         * Execute a Mann Whitney Test or other tests on selected Analysis Group
         */
        analysisPanel.getPerformStatButton().addActionListener((ActionEvent e) -> {
            int selectedIndex = analysisPanel.getAnalysisGroupList().getSelectedIndex();
            String statisticalTestName = analysisPanel.getStatTestComboBox().getSelectedItem().toString();
            String param = analysisPanel.getParameterComboBox().getSelectedItem().toString();
            // check that an analysis group is being selected
            if (selectedIndex != -1) {
                SingleCellAnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                // compute statistics
                computeStatistics(selectedGroup, statisticalTestName, param);
                // show statistics in tables
                showSummary(selectedGroup);
                // set the correction combobox to the one already chosen
                analysisPanel.getCorrectionComboBox().setSelectedItem(selectedGroup.getCorrectionMethodName());
                if (selectedGroup.getCorrectionMethodName().equals("none")) {
                    // by default show p-values without adjustment
                    showPValues(selectedGroup, false);
                } else {
                    // show p values with adjustement
                    showPValues(selectedGroup, true);
                }
            } else {
                // ask user to select a group
                singleCellAnalysisController.showMessage("Please select a group to perform analysis on.", "You must select a group first", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        /**
         * Refresh p value table with current selected significance of level
         */
        analysisPanel.getSignLevelComboBox().addActionListener((ActionEvent e) -> {
            if (analysisPanel.getSignLevelComboBox().getSelectedIndex() != -1) {
                String statisticalTest = analysisPanel.getStatTestComboBox().getSelectedItem().toString();
                Double selectedSignLevel = (Double) analysisPanel.getSignLevelComboBox().getSelectedItem();
                SingleCellAnalysisGroup selectedGroup = groupsBindingList.get(analysisPanel.getAnalysisGroupList().getSelectedIndex());
                boolean isAdjusted = !selectedGroup.getCorrectionMethodName().equals("none");
                singleCellStatisticsAnalyzer.detectSignificance(selectedGroup, statisticalTest, selectedSignLevel, isAdjusted);
                boolean[][] significances = selectedGroup.getSignificances();
                JTable pValuesTable = analysisPanel.getComparisonTable();
                for (int i = 1; i < pValuesTable.getColumnCount(); i++) {
                    pValuesTable.getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(new DecimalFormat("#.####"), significances));
                }
                pValuesTable.repaint();
            }
        });

        /**
         * Apply correction for multiple comparisons: choose the algorithm!
         */
        analysisPanel.getCorrectionComboBox().addActionListener((ActionEvent e) -> {
            int selectedIndex = analysisPanel.getAnalysisGroupList().getSelectedIndex();
            if (selectedIndex != -1) {
                SingleCellAnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                String correctionMethod = analysisPanel.getCorrectionComboBox().getSelectedItem().toString();

                // if the correction method is not "NONE"
                if (!correctionMethod.equals("none")) {
                    // adjust p values
                    singleCellStatisticsAnalyzer.correctForMultipleComparisons(selectedGroup, correctionMethod);
                    // show p - values with the applied correction
                    showPValues(selectedGroup, true);
                } else {
                    // if selected correction method is "NONE", do not apply correction and only show normal p-values
                    showPValues(selectedGroup, false);
                }
            }
        });

        /**
         * Perform statistical test: choose the test!!
         */
        analysisPanel.getPerformStatButton().addActionListener((ActionEvent e) -> {
            // get the selected test to be executed
            String selectedTest = analysisPanel.getStatTestComboBox().getSelectedItem().toString();
            String param = analysisPanel.getParameterComboBox().getSelectedItem().toString();
            // analysis group
            int selectedIndex = analysisPanel.getAnalysisGroupList().getSelectedIndex();
            if (selectedIndex != -1) {
                SingleCellAnalysisGroup selectedGroup = groupsBindingList.get(selectedIndex);
                computeStatistics(selectedGroup, selectedTest, param);
            }
        });

        //multiple comparison correction: set the default correction to none
        analysisPanel.getCorrectionComboBox().setSelectedIndex(0);
        analysisPanel.getStatTestComboBox().setSelectedIndex(0);
        analysisPanel.getParameterComboBox().setSelectedIndex(0);
    }

    /**
     * Show Summary Statistics in correspondent table
     *
     * @param analysisGroup
     */
    private void showSummary(SingleCellAnalysisGroup singleCellAnalysisGroup) {
        singleCellAnalysisController.getAnalysisPanel().getCurrentGroupName().setText(singleCellAnalysisGroup.getGroupName());
        // set model and cell renderer for statistics summary table
        SingleCellStatSummaryTableModel statisticalSummaryTableModel = new SingleCellStatSummaryTableModel(singleCellAnalysisGroup);
        JTable statisticalSummaryTable = singleCellAnalysisController.getAnalysisPanel().getStatTable();
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
    private void showPValues(SingleCellAnalysisGroup singleCellAnalysisGroup, boolean isAdjusted) {
        String statisticalTestName = singleCellAnalysisController.getAnalysisPanel().getStatTestComboBox().getSelectedItem().toString();
        SingleCellPValuesTableModel pValuesTableModel = new SingleCellPValuesTableModel(singleCellAnalysisGroup, isAdjusted);
        JTable pValuesTable = singleCellAnalysisController.getAnalysisPanel().getComparisonTable();
        pValuesTable.setModel(pValuesTableModel);
        Double selectedSignLevel = (Double) singleCellAnalysisController.getAnalysisPanel().getSignLevelComboBox().getSelectedItem();
        // detect significances with selected alpha level
        singleCellStatisticsAnalyzer.detectSignificance(singleCellAnalysisGroup, statisticalTestName, selectedSignLevel, isAdjusted);
        boolean[][] significances = singleCellAnalysisGroup.getSignificances();
        for (int i = 1; i < pValuesTable.getColumnCount(); i++) {
            pValuesTable.getColumnModel().getColumn(i).setCellRenderer(new PValuesTableRenderer(new DecimalFormat("#.####"), significances));
        }
        pValuesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
    }

    /**
     * Compute Statistics for the selected Group
     *
     * @param analysisGroup
     */
    private void computeStatistics(SingleCellAnalysisGroup analysisGroup, String statisticalTestName, String parameter) {
        // generate summary statistics
        singleCellStatisticsAnalyzer.generateSummaryStatistics(analysisGroup, statisticalTestName, parameter);
        // execute mann whitney test --- set p values matrix (no adjustment)
        singleCellStatisticsAnalyzer.executePairwiseComparisons(analysisGroup, statisticalTestName, parameter);
    }

    /**
     * Get conditions according to selected rows and add them to the Analysis
     * Group
     */
    private void addGroupToAnalysis() {
        List<SingleCellConditionDataHolder> conditionDataHolders = new ArrayList<>();

        Boolean filteredData = singleCellAnalysisController.isFilteredData();
        AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        int[] selectedIndices = analysisPanel.getConditionList().getSelectedIndices();

        // we check here that at least two conditions have been selected to be part of the analysis group
        // else, the analysis does not really make sense
        if (selectedIndices.length > 1) {

            if (filteredData) {
                conditionDataHolders.addAll(singleCellAnalysisController.getFilteringMap().keySet());
            } else {
                conditionDataHolders.addAll(singleCellAnalysisController.getPreProcessingMap().values());
            }

            // make a new analysis group, with those conditions and those results
            SingleCellAnalysisGroup singleCellAnalysisGroup = new SingleCellAnalysisGroup(conditionDataHolders);

            //set name for the group
            if (!analysisPanel.getGroupNameTextField().getText().isEmpty()) {
                singleCellAnalysisGroup.setGroupName(analysisPanel.getGroupNameTextField().getText());
                // set correction method to NONE by default
                singleCellAnalysisGroup.setCorrectionMethodName("none");
                analysisPanel.getGroupNameTextField().setText("");
                // actually add the group to the analysis list
                if (!groupsBindingList.contains(singleCellAnalysisGroup)) {
                    groupsBindingList.add(singleCellAnalysisGroup);
                }
            } else {
                // ask the user to type a name for the group
                singleCellAnalysisController.showMessage("Please type a name for the analysis group.", "no name typed for the analysis group",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // we tell the user that statistics cannot be performed on only one condition !!
            // the selection is basically ignored
            singleCellAnalysisController.showMessage("Sorry! It is not possible to perform analysis on one condition only!\nPlease select at least two conditions.",
                    "at least two conditions need to be chosen for analysis", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Remove a Group of Conditions
     */
    private void removeGroupFromAnalysis() {
        // selected group
        int selectedIndex = singleCellAnalysisController.getAnalysisPanel().getAnalysisGroupList().getSelectedIndex();
        // check if an element is selected first
        if (selectedIndex != -1) {
            groupsBindingList.remove(selectedIndex);
        } else {
            singleCellAnalysisController.showMessage("Select a group to remove from current analysis!",
                    "remove group error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
