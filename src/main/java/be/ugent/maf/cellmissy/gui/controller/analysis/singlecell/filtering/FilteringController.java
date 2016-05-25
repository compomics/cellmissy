/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.SingleCellPreProcessingController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.FilteringInfoDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.FilteringPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
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
        });

        filteringPanel.getMultipleCutOffRadioButton().addActionListener((ActionEvent e) -> {
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) filteringPanel.getBottomPanel().getLayout();
            layout.show(filteringPanel.getBottomPanel(), filteringPanel.getMultipleCutOffParentPanel().getName());
        });

        // add view to parent container
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getFilteringParentPanel().add(filteringPanel, gridBagConstraints);
    }

}
