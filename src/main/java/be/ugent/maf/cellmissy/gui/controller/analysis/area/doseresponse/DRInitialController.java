/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInitialPlotPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.ButtonGroup;
import org.jfree.chart.ChartPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for initial plot panel of dose-response analysis.
 *
 * @author Gwendolien
 */
@Controller("dRInitialController")
public class DRInitialController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DRInitialController.class);
    //model
    //view
    private DRInitialPlotPanel dRInitialPlotPanel;
    private ChartPanel initialChartPanel;
    // parent controller
    @Autowired
    private DoseResponseController doseResponseController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initDRInitialPanel();

    }

    /**
     * Getters and setters
     *
     * @return
     */
    public DRInitialPlotPanel getDRInitialPlotPanel() {
        return dRInitialPlotPanel;
    }

    /**
     * Initialize view
     */
    private void initDRInitialPanel() {
        dRInitialPlotPanel = new DRInitialPlotPanel();
        //update table info label
        doseResponseController.updateTableInfoMessage("Concentrations of conditions selected previously have been log-transformed, slopes have not been changed");
        //create a ButtonGroup for the radioButtons of the hillslope choice
        ButtonGroup hillslopeRadioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        hillslopeRadioButtonGroup.add(dRInitialPlotPanel.getStandardHillslopeRadioButton());
        hillslopeRadioButtonGroup.add(dRInitialPlotPanel.getVariableHillslopeRadioButton());
        //select as default first button (standard hillslope)
        dRInitialPlotPanel.getStandardHillslopeRadioButton().setSelected(true);
        //set text field for standard hillslope and make uneditable
        dRInitialPlotPanel.getStandardHillslopeTextField().setText(String.valueOf(doseResponseController.getStandardHillslope()));
        dRInitialPlotPanel.getStandardHillslopeTextField().setEditable(false);

        //Log transform concentrations, keeping slopes the same
        LinkedHashMap<Double, List<Double>> dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup());
        //Populate table with the data
        //Fit data according to initial parameters (standard hillslope, no constraints)
        //Plot fitted data in dose-response curve, along with R² annotation
        /**
         * Action listeners for buttons
         */
        /**
         * Set hillslope to standard for next fitting. Standard is 1 or -1
         * depending on type of experiment -- see input panel
         */
        dRInitialPlotPanel.getStandardHillslopeRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        /**
         * Set hillslope to variable for next fitting. Fitting will try to find
         * the optimal value for the parameter according to the data
         */
        dRInitialPlotPanel.getVariableHillslopeRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        /**
         * If selected, text field to enter value for parameter constraining
         * will be editable and taken into account on plotting.
         */
        dRInitialPlotPanel.getBottomCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    dRInitialPlotPanel.getBottomTextField().setEditable(true);
                } else {
                    dRInitialPlotPanel.getBottomTextField().setEditable(false);
                }
            }
        });

        dRInitialPlotPanel.getTopCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    dRInitialPlotPanel.getTopTextField().setEditable(true);
                } else {
                    dRInitialPlotPanel.getTopTextField().setEditable(false);
                }
            }
        });

        /**
         * Perform fitting and plot new dose-response graph, taking into account
         * any choices made by the user.
         */
        dRInitialPlotPanel.getPlotGraphButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        //add view to parent panel
        doseResponseController.getDRPanel().getGraphicsDRParentPanel().add(dRInitialPlotPanel, gridBagConstraints);
    }

    /**
     * Private methods
     */
    /**
     * Prepare data for fitting starting from the analysis group.
     *
     * @param dRAnalysisGroup
     * @return LinkedHashMap That maps the concentration (log-transformed!) to
     * the replicate velocites
     */
    private LinkedHashMap<Double, List<Double>> prepareFittingData(DoseResponseAnalysisGroup dRAnalysisGroup) {
        LinkedHashMap<Double, List<Double>> result = new LinkedHashMap<>();
        
        List<List<Double>> allVelocities = new ArrayList<List<Double>>(dRAnalysisGroup.getVelocitiesMap().size());
        for (PlateCondition plateCondition : dRAnalysisGroup.getVelocitiesMap().keySet()) {
            List<Double> replicateVelocities = dRAnalysisGroup.getVelocitiesMap().get(plateCondition);
            
            allVelocities.add(replicateVelocities);
        }
        
        int i = 0;
        LinkedHashMap<Double, String> nestedMap = dRAnalysisGroup.getConcentrationsMap().get(dRAnalysisGroup.getTreatmentToAnalyse());
        for (Double concentration : nestedMap.keySet()) {
            String unit = nestedMap.get(concentration);

            Double logConcentration = doseResponseController.logTransform(concentration, unit);
            result.put(logConcentration, allVelocities.get(i));
            
            i++;
        }


        return result;
    }
}
