/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRNormalizedPlotPanel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ButtonGroup;
import org.jfree.chart.ChartPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for normalized plot panel of dose-response analysis
 *
 * @author Gwendolien
 */
@Controller("dRNormalizedController")
public class DRNormalizedController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DRNormalizedController.class);

    //model
    //view
    private DRNormalizedPlotPanel dRNormalizedPlotPanel;
    private ChartPanel normalizedChartPanel;
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
        //init view
        initDRNormalizedPanel();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public DRNormalizedPlotPanel getDRNormalizedPlotPanel() {
        return dRNormalizedPlotPanel;
    }

    /**
     * Initialize view
     */
    private void initDRNormalizedPanel() {
        dRNormalizedPlotPanel = new DRNormalizedPlotPanel();

        //update table info label
        doseResponseController.updateTableInfoMessage("Log-transformed concentrations with their normalized responses per replicate");
        //create a ButtonGroup for the radioButtons of the hillslope choice
        ButtonGroup hillslopeRadioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        hillslopeRadioButtonGroup.add(dRNormalizedPlotPanel.getStandardHillslopeRadioButton());
        hillslopeRadioButtonGroup.add(dRNormalizedPlotPanel.getVariableHillslopeRadioButton());
        //select as default first button (standard hillslope)
        dRNormalizedPlotPanel.getStandardHillslopeRadioButton().setSelected(true);
        //set text field for standard hillslope and make uneditable
        dRNormalizedPlotPanel.getStandardHillslopeTextField().setText(String.valueOf(doseResponseController.getStandardHillslope()));
        dRNormalizedPlotPanel.getStandardHillslopeTextField().setEditable(false);

        //Perform initial normalization (mean values)
        //Populate table with normalized data
        //Perform initial curve fitting (standard hillslope, no constraints)
        //Plot fitted data in dose-response curve, along with R² annotation
        /**
         * Action listeners for buttons
         */
        /**
         * Set hillslope to standard for next fitting. Standard is 1 or -1
         * depending on type of experiment -- see input panel
         */
        dRNormalizedPlotPanel.getStandardHillslopeRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        /**
         * Set hillslope to variable for next fitting. Fitting will try to find
         * the optimal value for the parameter according to the data
         */
        dRNormalizedPlotPanel.getVariableHillslopeRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        /**
         * The combo box determines how the normalization is done. Bottom combo
         * box defines what the value for 0% response is.
         */
        dRNormalizedPlotPanel.getBottomComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = (String) dRNormalizedPlotPanel.getBottomComboBox().getSelectedItem();
                switch (value) {
                    case "Smallest Mean Value":
                        dRNormalizedPlotPanel.getBottomTextField().setEditable(false);
                        dRNormalizedPlotPanel.getBottomTextField().setText(Collections.min(computeMeans(doseResponseController.getdRAnalysisGroup())).toString());
                        break;
                    case "Smallest Median Value":
                        dRNormalizedPlotPanel.getBottomTextField().setEditable(false);
                        dRNormalizedPlotPanel.getBottomTextField().setText(Collections.min(computeMedians(doseResponseController.getdRAnalysisGroup())).toString());
                        break;
                    case "Other Value":
                        dRNormalizedPlotPanel.getBottomTextField().setText("");
                        dRNormalizedPlotPanel.getBottomTextField().setEditable(true);
                        break;
                }
            }
        });

        /**
         * The combo box determines how the normalization is done. Top combo box
         * defines what the value for 100% response is.
         */
        dRNormalizedPlotPanel.getTopComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String choice = (String) dRNormalizedPlotPanel.getTopComboBox().getSelectedItem();
                switch (choice) {
                    case "Largest Mean Value":
                        dRNormalizedPlotPanel.getTopTextField().setEditable(false);
                        dRNormalizedPlotPanel.getTopTextField().setText(Collections.max(computeMeans(doseResponseController.getdRAnalysisGroup())).toString());
                        break;
                    case "Largest Median Value":
                        dRNormalizedPlotPanel.getTopTextField().setEditable(false);
                        dRNormalizedPlotPanel.getTopTextField().setText(Collections.max(computeMeans(doseResponseController.getdRAnalysisGroup())).toString());
                        break;
                    case "Other Value":
                        dRNormalizedPlotPanel.getTopTextField().setText("");
                        dRNormalizedPlotPanel.getTopTextField().setEditable(true);
                        break;
                }
            }
        });

        /**
         * If selected, text field with chosen value (combobox) will be used to
         * constrain the curve fit.
         */
        dRNormalizedPlotPanel.getBottomConstrainCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    double bottomConstrainValue = Double.parseDouble(dRNormalizedPlotPanel.getBottomTextField().getText());
                } else {

                }
            }
        });

        dRNormalizedPlotPanel.getTopConstrainCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    double topConstrainValue = Double.parseDouble(dRNormalizedPlotPanel.getTopTextField().getText());

                } else {

                }
            }
        });

        /**
         * Re-normalize and plot new dose-response graph, taking into account
         * any choices made by the user.
         */
        dRNormalizedPlotPanel.getPlotGraphButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                doseResponseController.populateTable();
            }
        });

        //add view to parent panel
        doseResponseController.getDRPanel().getGraphicsDRParentPanel().add(dRNormalizedPlotPanel, gridBagConstraints);
    }

    /**
     * Private methods
     */
    /**
     * Compute mean values of every condition in the dose response analysis
     * group
     */
    private List<Double> computeMeans(DoseResponseAnalysisGroup doseResponseAnalysisGroup) {
        List<Double> allMeans = new ArrayList();
        for (List<Double> velocities : doseResponseAnalysisGroup.getVelocitiesMap().values()) {
            double[] data = new double[velocities.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = velocities.get(i);
            }
            allMeans.add(AnalysisUtils.computeMean(data));
        }
        return allMeans;
    }

    /**
     * Compute median values of every condition in DR analysis group
     */
    private List<Double> computeMedians(DoseResponseAnalysisGroup doseResponseAnalysisGroup) {
        List<Double> allMedians = new ArrayList();
        for (List<Double> velocities : doseResponseAnalysisGroup.getVelocitiesMap().values()) {
            double[] data = new double[velocities.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = velocities.get(i);
            }
            allMedians.add(AnalysisUtils.computeMedian(data));
        }
        return allMedians;
    }

}
