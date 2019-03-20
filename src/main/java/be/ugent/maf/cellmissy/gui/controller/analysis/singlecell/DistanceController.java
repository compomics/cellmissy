/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.DistancePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataTableModel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import smile.plot.BoxPlot;
import smile.plot.PlotCanvas;



/** Controller for the distance parent panel
 *
 * @author ninad
 */
@Controller("distanceController")
public class DistanceController {
    
    //view
    private DistancePanel distancePanel;

    // model
    private JTable distanceDataTable;
    
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    //services
    private GridBagConstraints gridBagConstraints;

    // Initialise controller
    
    public static GridBagConstraints getBoxPlotGridBagConstraints() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        return gridBagConstraints;
    }
     
    public void init(){
        
        gridBagConstraints = getBoxPlotGridBagConstraints();

         distancePanel = new DistancePanel();
         
        // initialise main view
        initDistancePanel();
    }
    
    // Getters

    public DistancePanel getDistancePanel() {
        return distancePanel;
    }
    
    // Reset on cancel
    
    public void resetOnCancel() {
        distanceDataTable.setModel(new DefaultTableModel());
        
    }
    /**
     * SHOW DATA IN TABLE.
     */
    /**
     * Show the accumulated distance for each plate condition.
     *
     * @param plateCondition
     */
    public void showAccumDistInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) { 
            String[] columnNames = {"well", "track", "accumulated distance"};
            distanceDataTable.setModel(new TrackDataTableModel(columnNames, singleCellConditionDataHolder,
                    singleCellConditionDataHolder.getCumulativeDistancesVector()));
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(), SwingConstants.CENTER);
            for (int i = 0; i < distanceDataTable.getColumnModel().getColumnCount(); i++) {
                distanceDataTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            distanceDataTable.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
            distanceDataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        distancePanel.getTableInfoLabel().setText("Distances for each track");
    }
        /**
     * Show the euclidian distance for each plate condition.
     *
     * @param plateCondition
     */
    public void showEuclDistInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Double[] euclidianDistanceVector = singleCellConditionDataHolder.getEuclideanDistancesVector();
            String[] columnNames = {"well", "track", "euclidian distance"};
            singleCellPreProcessingController.showTrackDataInTable(plateCondition, distanceDataTable, columnNames, euclidianDistanceVector);
        }
    }

    // initialise main view
    
    private void initDistancePanel() {
        // initialise main view
        distancePanel = new DistancePanel();
        // initialise the datatable
        distanceDataTable = new JTable();
        JScrollPane scrollpane = new JScrollPane(distanceDataTable);
        //the table will take all the viewport height available (the entire available screen will be taken by the table)
        distanceDataTable.setFillsViewportHeight(true);
        scrollpane.getViewport().setBackground(Color.white);
        distanceDataTable.getTableHeader().setReorderingAllowed(false);
        // row selection and column selection
        distanceDataTable.setColumnSelectionAllowed(true);
        distanceDataTable.setRowSelectionAllowed(false);
        distancePanel.getDistanceDataPanel().add(scrollpane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(distancePanel.getAccumulatedDistanceRadioButton());
        radioButtonGroup.add(distancePanel.getEuclidianDistanceRadioButton());

        
        //Show accumulated distance boxplot of different conditions
        distancePanel.getAccumulatedDistanceRadioButton().addActionListener((ActionEvent e) -> {
            PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
            //check that a condition is selected
            if (currentCondition != null) {
                showAccumDistInTable(currentCondition);
                plotAccumDistBoxPlot(currentCondition);
            }
        });
        
        //Show euclidian distance boxplot of different conditions
        distancePanel.getEuclidianDistanceRadioButton().addActionListener((ActionEvent e) -> {
            PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
            //check that a condition is selected
            if (currentCondition != null) {
                showEuclDistInTable(currentCondition);
                plotEucDistBoxPlot(currentCondition);
            }
        });

        
        //Set accumulated distace as default selected button
        distancePanel.getAccumulatedDistanceRadioButton().setSelected(true);
        //Add view to parent panel (single cell analysis panel)
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getDistanceParentPanel().add(distancePanel, gridBagConstraints);
    }

    
    
   //Render the boxplots for accumulated distance
    public void plotAccumDistBoxPlot(PlateCondition plateCondition){
        distancePanel.getBoxplotPanel().removeAll();
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) { 
            double [] accumDistVector = ArrayUtils.toPrimitive(singleCellConditionDataHolder.getCumulativeDistancesVector());
            PlotCanvas accumDistCanvas = BoxPlot.plot(accumDistVector);
            accumDistCanvas.setMargin(0.05);
            distancePanel.getBoxplotPanel().add(accumDistCanvas, gridBagConstraints);
            distancePanel.getBoxplotPanel().revalidate();
            distancePanel.getBoxplotPanel().repaint();
        }
      
    }
    
    //Render the boxplots for euclidian distance
    public void plotEucDistBoxPlot(PlateCondition plateCondition){
        distancePanel.getBoxplotPanel().removeAll();
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) { 
            double [] eucDistVector = ArrayUtils.toPrimitive(singleCellConditionDataHolder.getEuclideanDistancesVector());
            PlotCanvas eucDistCanvas = BoxPlot.plot(eucDistVector);
            eucDistCanvas.setMargin(0.05);
            distancePanel.getBoxplotPanel().add(eucDistCanvas, gridBagConstraints);
            distancePanel.getBoxplotPanel().revalidate();
            distancePanel.getBoxplotPanel().repaint();
        }
    }

    }
    
 
