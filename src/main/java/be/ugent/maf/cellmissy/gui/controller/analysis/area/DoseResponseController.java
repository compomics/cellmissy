/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInitialPlotPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRNormalizedPlotPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRResultsPanel;
import be.ugent.maf.cellmissy.entity.result.area.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.gui.view.renderer.list.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

import org.jfree.chart.ChartPanel;

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
    private BindingGroup bindingGroup;
    //view
    private AreaAnalysisPanel areaAnalysisPanel;
    private DRInputPanel dRInputPanel;
    private DRInitialPlotPanel dRInitialPlotPanel;
    private DRNormalizedPlotPanel dRNormalizedPlotPanel;
    private DRResultsPanel dRResultsPanel;
    private ChartPanel initialChartPanel;
    private ChartPanel normalizedChartPanel;
    private ChartPanel resultsChartPanel;
    // parent controller
    @Autowired
    private AreaMainController areaMainController;
    // child controller
    // services
    private GridBagConstraints gridBagConstraints;
    
    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init views
        initDRInputPanel();
        initDRInitialPlotPanel();
        initDRNormalizedPlotPanel();
        initDRResultsPanel();
    }
    
    /**
     * getters and setters
     *
     * @return
     */
    public DRInputPanel getDRInputPanel() {
        return dRInputPanel;
    }
    
    public DRInitialPlotPanel getDRInitialPlotPanel() {
        return dRInitialPlotPanel;
    }
    
    public DRNormalizedPlotPanel getDRNormalizedPlotPanel() {
        return dRNormalizedPlotPanel;
    }
    
    public DRResultsPanel getDRResultsPanel() {
        return dRResultsPanel;
    }

    
    /**
     * Get conditions retained after outlier detection in preprocessing steps
     * (drug id and concentration)
     * and their respective velocities
     */
    // Boolean (Exclude Replicates from dataset)
    boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
    
    
    
    /**
     * private methods
     */
    
    /**
     * Initialize view
    */
    private void initDRInputPanel() {
        dRInputPanel = new DRInputPanel();
        List<PlateCondition> processedConditions = getProcessedConditions();
        List<PlateCondition> selectedConditions = getSelectedConditions();
        List<Integer> numberOfReplicates = getNumberOfReplicates();
        // control opaque property of table
        dRInputPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);
        JTable slopesTable = dRInputPanel.getSlopesTable();
        slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        slopesTable.getTableHeader().setReorderingAllowed(false);
        slopesTable.setFillsViewportHeight(true);
        // put conditions in selectable list
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, dRInputPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        dRInputPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, numberOfReplicates));
    }
    
}
