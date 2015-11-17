/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInitialPlotPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRNormalizedPlotPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRResultsPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;

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
    private JTable dataTable;
    //view
    private DRPanel dRPanel;

    // move to child controllers 
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
    private DRInputController dRInputController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init view
        initMainView();
//        // init subviews
//        initDRInputPanel();
//        initDRInitialPlotPanel();
//        initDRNormalizedPlotPanel();
//        initDRResultsPanel();
        //init child controllers
        dRInputController.init();
    }

    /**
     * getters and setters
     *
     * @return
     */
    // move to child controllers
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
     * private methods
     */
    /**
     * Initialize main view
     */
    private void initMainView() {
        dRPanel = new DRPanel();
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup mainDRRadioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        mainDRRadioButtonGroup.add(dRPanel.getInputDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getInitialPlotDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getNormalizedPlotDRButton());
        mainDRRadioButtonGroup.add(dRPanel.getResultsDRButton());
        //select as default first button (Normalized Area values Computation)
        dRPanel.getInputDRButton().setSelected(true);
        //init dataTable
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row an column selection must be false
        //dataTable.setColumnSelectionAllowed(false);
        //dataTable.setRowSelectionAllowed(false);
        dRPanel.getDatatableDRPanel().add(scrollPane);
        
        
        /**
         * When button is selected, switch view to corresponding subview
         */
        dRPanel.getInputDRButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        //add view to parent panel
        areaMainController.getAreaAnalysisPanel().getDoseResponseParentPanel().add(dRPanel, gridBagConstraints);
    }

    /**
     * Initialize panel where conditions for further analysis will be selected
     * by the user
     */
    //move to child controller
//    private void initDRInputPanel() {
//        dRInputPanel = new DRInputPanel();
//        List<PlateCondition> processedConditions = getProcessedConditions();
//        List<PlateCondition> selectedConditions = getSelectedConditions();
//        List<Integer> numberOfReplicates = getNumberOfReplicates();
//        // control opaque property of table
//        dRInputPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);
//        JTable slopesTable = dRInputPanel.getSlopesTable();
//        slopesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
//        slopesTable.getTableHeader().setReorderingAllowed(false);
//        slopesTable.setFillsViewportHeight(true);
//        // put conditions in selectable list
//        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
//        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, dRInputPanel.getConditionsList());
//        bindingGroup.addBinding(jListBinding);
//        bindingGroup.bind();
//        dRInputPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, numberOfReplicates));
//    }

}
