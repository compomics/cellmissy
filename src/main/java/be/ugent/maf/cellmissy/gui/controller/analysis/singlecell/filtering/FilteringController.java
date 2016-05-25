/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.SingleCellPreProcessingController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.FilteringInfoDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.FilteringPanel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A controller to take care of filtering/quality control - on single cell
 * trajectories.
 *
 * @author Paola
 */
@Component("filteringController")
public class FilteringController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FilteringController.class);
    // model
    // view
    private FilteringPanel filteringPanel;
    private FilteringInfoDialog filteringInfoDialog;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    @Autowired
    private MultipleCutOffFilteringController multipleCutOffFilteringController;
    @Autowired
    private SingleCutOffFilteringController singleCutOffFilteringController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        filteringInfoDialog = new FilteringInfoDialog(singleCellPreProcessingController.getMainFrame(), true);
        // int main view
        initFilteringPanel();
        // int child controllers
        multipleCutOffFilteringController.init();
        singleCutOffFilteringController.init();
    }

    public FilteringPanel getFilteringPanel() {
        return filteringPanel;
    }

    public void showMessage(String message, String title, Integer messageType) {
        singleCellPreProcessingController.showMessage(message, title, messageType);
    }

    public void showWaitingDialog(String title) {
        singleCellPreProcessingController.showWaitingDialog(title);
    }

    public PlateCondition getCurrentCondition() {
        return singleCellPreProcessingController.getCurrentCondition();
    }

    public void setCursor(Cursor cursor) {
        singleCellPreProcessingController.setCursor(cursor);
    }

    public void controlGuiComponents(boolean enabled) {
        singleCellPreProcessingController.controlGuiComponents(enabled);
    }

    public SingleCellConditionDataHolder getConditionDataHolder(PlateCondition plateCondition) {
        return singleCellPreProcessingController.getConditionDataHolder(plateCondition);
    }

    public void hideWaitingDialog() {
        singleCellPreProcessingController.hideWaitingDialog();
    }

    public void handleUnexpectedError(Exception exception) {
        singleCellPreProcessingController.handleUnexpectedError(exception);
    }

    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName) {
        return singleCellPreProcessingController.estimateDensityFunction(data, kernelDensityEstimatorBeanName);
    }

    public String getKernelDensityEstimatorBeanName() {
        return singleCellPreProcessingController.getKernelDensityEstimatorBeanName();
    }

    public void plotRawKde(PlateCondition plateCondition) {
        multipleCutOffFilteringController.plotRawKdeMultipleCutOff(plateCondition);
        singleCutOffFilteringController.plotRawKdeSingleCutOff(plateCondition);
    }

    public void setMeanDisplForCondition(PlateCondition plateCondition) {
        multipleCutOffFilteringController.getMultipleCutOffPanel().getMeanDisplacReplTextField().
                setText("" + AnalysisUtils.roundThreeDecimals(getMeanDisplForCondition(plateCondition)));
    }

    public void setMeanDisplForExperiment() {
        multipleCutOffFilteringController.getMultipleCutOffPanel().getMeanDisplacCondTextField().
                setText("" + AnalysisUtils.roundThreeDecimals(getMeanDisplForExp()));
        singleCutOffFilteringController.getSingleCutOffPanel().getMeanDisplacTextField().
                setText("" + AnalysisUtils.roundThreeDecimals(getMeanDisplForExp()));
    }

    /**
     * Estimate the raw density functions for the replicates of a single cell
     * condition data holder.
     *
     * @param singleCellConditionDataHolder
     * @return a list of a list of double containing the estimated density
     * functions.
     */
    public List<List<double[]>> estimateRawDensityFunction(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        String kernelDensityEstimatorBeanName = getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().map((singleCellWellDataHolder)
                -> estimateDensityFunction(singleCellWellDataHolder.getTrackDisplacementsVector(),
                        kernelDensityEstimatorBeanName)).forEach((oneReplicateTrackDisplDensityFunction) -> {
                    densityFunction.add(oneReplicateTrackDisplDensityFunction);
                });

        return densityFunction;
    }

    /**
     *
     * @param singleCellConditionDataHolder
     * @param densityFunctions
     * @return
     */
    public XYSeriesCollection generateDensityFunction(SingleCellConditionDataHolder singleCellConditionDataHolder, List<List<double[]>> densityFunctions) {
        XYSeriesCollection collection = new XYSeriesCollection();
        int counter = 0;
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSingleCellAnalyzedSamplesPerWell(singleCellWellDataHolder.getWell());
            if (numberOfSamplesPerWell == 1) {
                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                    if (densityFunctions.get(i) != null) {
                        // x values
                        double[] xValues = densityFunctions.get(i).get(0);
                        // y values
                        double[] yValues = densityFunctions.get(i).get(1);
                        XYSeries series = new XYSeries("" + singleCellWellDataHolder.getWell() + " " + counter, false);
                        for (int j = 0; j < xValues.length; j++) {
                            double x = xValues[j];
                            double y = yValues[j];
                            series.add(x, y);
                        }
                        collection.addSeries(series);
                    }

                }
                counter += numberOfSamplesPerWell;
            } else {
                int label = 0;
                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                    // x values
                    double[] xValues = densityFunctions.get(i).get(0);
                    // y values
                    double[] yValues = densityFunctions.get(i).get(1);
                    XYSeries series = new XYSeries("" + (singleCellWellDataHolder.getWell()) + ", " + (label + 1), false);
                    for (int j = 0; j < xValues.length; j++) {
                        double x = xValues[j];
                        double y = yValues[j];
                        series.add(x, y);
                    }
                    collection.addSeries(series);
                    label++;
                }
                counter += numberOfSamplesPerWell;
            }
        }
        return collection;
    }

    /**
     * Private classes and methods.
     */
    /**
     * Initialize the main view: the filtering panel (the two sub-views are kept
     * in two separate controllers).
     */
    private void initFilteringPanel() {
        // make a new view
        filteringPanel = new FilteringPanel();
        // make a new radio button group for the radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(filteringPanel.getSingleCutOffRadioButton());
        radioButtonGroup.add(filteringPanel.getMultipleCutOffRadioButton());
        // select the first one as default
        filteringPanel.getSingleCutOffRadioButton().setSelected(true);

        // set icon for question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        ImageIcon scaledQuestionIcon = GuiUtils.getScaledIcon(questionIcon);
        filteringPanel.getQuestionButton().setIcon(scaledQuestionIcon);

        // action listeners
        // info button
        filteringPanel.getQuestionButton().addActionListener((ActionEvent e) -> {
            // pack and show info dialog
            GuiUtils.centerDialogOnFrame(singleCellPreProcessingController.getMainFrame(), filteringInfoDialog);
            filteringInfoDialog.setVisible(true);
        });

        // which criterium for the filtering?
        filteringPanel.getSingleCutOffRadioButton().addActionListener((ActionEvent e) -> {
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) filteringPanel.getBottomPanel().getLayout();
            layout.show(filteringPanel.getBottomPanel(), filteringPanel.getSingleCutOffParentPanel().getName());
            setMeanDisplForExperiment();
        });

        filteringPanel.getMultipleCutOffRadioButton().addActionListener((ActionEvent e) -> {
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) filteringPanel.getBottomPanel().getLayout();
            layout.show(filteringPanel.getBottomPanel(), filteringPanel.getMultipleCutOffParentPanel().getName());
            multipleCutOffFilteringController.plotRawKdeMultipleCutOff(getCurrentCondition());
            setMeanDisplForCondition(getCurrentCondition());
            setMeanDisplForExperiment();
        });

        // add view to parent container
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getFilteringParentPanel().add(filteringPanel, gridBagConstraints);
    }

    /**
     * Get the mean displacement for a condition.
     *
     * @param plateCondition
     * @return
     */
    private Double getMeanDisplForCondition(PlateCondition plateCondition) {
        SingleCellConditionDataHolder conditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        Double[] instantaneousDisplacementsVector = conditionDataHolder.getInstantaneousDisplacementsVector();
        return AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(instantaneousDisplacementsVector)));
    }

    /**
     * Get mean displacement for the experiment.
     *
     * @return
     */
    private Double getMeanDisplForExp() {
        Map<PlateCondition, SingleCellConditionDataHolder> preProcessingMap = singleCellPreProcessingController.getPreProcessingMap();
        Double[] meanValues = new Double[preProcessingMap.size()];
        for (int i = 0; i < meanValues.length; i++) {
            PlateCondition condition = singleCellPreProcessingController.getPlateConditionList().get(i);
            if (!condition.isComputed()) {
                singleCellPreProcessingController.computeCondition(condition);
            }
            meanValues[i] = getMeanDisplForCondition(singleCellPreProcessingController.getPlateConditionList().get(i));
        }
        return AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(meanValues)));
    }

}
