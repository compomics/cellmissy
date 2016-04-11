/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRNormalizedPlotPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private Double bottomConstrainValue;
    private Double topConstrainValue;
    private boolean standardHillslope;
    private NonEditableTableModel tableModel;
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

    public NonEditableTableModel getTableModel() {
        return tableModel;
    }

    private void setTableModel(NonEditableTableModel tableModel) {
        this.tableModel = tableModel;
    }
    
    /**
     * Initialize view
     */
    private void initDRNormalizedPanel() {
        dRNormalizedPlotPanel = new DRNormalizedPlotPanel();
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

        //LogTransform concentrations and perform initial normalization (mean values)
        LinkedHashMap<Double, List<Double>> dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup());
        //create and set the table model for the top panel table (dependent on normalization)
        setTableModel(createTableModel(dataToFit));
        //Perform initial curve fitting (standard hillslope, no constraints)
        doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getNormalizedFittingResults(), bottomConstrainValue, topConstrainValue,standardHillslope);
        //Plot fitted data in dose-response curve, along with R² annotation
        doseResponseController.plotDoseResponse();
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
                standardHillslope = true;
            }
        });
        /**
         * Set hillslope to variable for next fitting. Fitting will try to find
         * the optimal value for the parameter according to the data
         */
        dRNormalizedPlotPanel.getVariableHillslopeRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                standardHillslope = false;
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
                    bottomConstrainValue = Double.parseDouble(dRNormalizedPlotPanel.getBottomTextField().getText());
                } else {
                    bottomConstrainValue = null;
                }
            }
        });

        dRNormalizedPlotPanel.getTopConstrainCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    topConstrainValue = Double.parseDouble(dRNormalizedPlotPanel.getTopTextField().getText());

                } else {
                    topConstrainValue = null;
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
                LinkedHashMap<Double, List<Double>> fittingData = prepareFittingData(doseResponseController.getdRAnalysisGroup());
                setTableModel(createTableModel(fittingData));
                doseResponseController.performFitting(fittingData, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getNormalizedFittingResults(), bottomConstrainValue, topConstrainValue,standardHillslope);
                doseResponseController.plotDoseResponse();
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

    /**
     * Prepare data for fitting starting from the analysis group.
     *
     * @param dRAnalysisGroup
     * @return LinkedHashMap That maps the concentration (log-transformed!) to
     * the normalized replicate velocites
     */
    private LinkedHashMap<Double, List<Double>> prepareFittingData(DoseResponseAnalysisGroup dRAnalysisGroup) {
        LinkedHashMap<Double, List<Double>> result = new LinkedHashMap<>();
        
        List<List<Double>> allVelocities = new ArrayList<List<Double>>(dRAnalysisGroup.getVelocitiesMap().size());
        for (PlateCondition plateCondition : dRAnalysisGroup.getVelocitiesMap().keySet()) {
            List<Double> replicateVelocities = dRAnalysisGroup.getVelocitiesMap().get(plateCondition);
            //normalize each value
            List<Double> normalizedVelocities = new ArrayList<>();
            for (Double value : replicateVelocities) {
                normalizedVelocities.add(normalize(value));
            }
            
            allVelocities.add(normalizedVelocities);
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
    
    /**
     * Perform normalization on velocities
     */
    private Double normalize(Double velocity) {
        //check which values will become 0% and 100%
        Double topNormalize = Double.parseDouble(dRNormalizedPlotPanel.getBottomTextField().getText());
        Double bottomNormalize = Double.parseDouble(dRNormalizedPlotPanel.getTopTextField().getText());
        
        return (velocity - bottomNormalize) / (topNormalize - bottomNormalize);
    }
    
    /**
     * Create the table model for the top panel table. Table contains icon,
     * log-transformed concentration and normalized slopes per condition
     *
     * @param dataToFit
     * @return the model
     */
    private NonEditableTableModel createTableModel(LinkedHashMap<Double, List<Double>> dataToFit) {
        Object[][] data = new Object[dataToFit.size()][dataToFit.entrySet().iterator().next().getValue().size() + 2];

        int rowIndex = 0;
        for (Map.Entry<Double, List<Double>> entry : dataToFit.entrySet()) {
            //log concentration is put on 2nd column
            data[rowIndex][1] = entry.getKey();
            for (int columnIndex = 2; columnIndex < entry.getValue().size() + 2; columnIndex++) {
                Double slope = entry.getValue().get(columnIndex - 2);
                if (slope != null && !slope.isNaN()) {
                    // round to three decimals slopes and coefficients
                    slope = AnalysisUtils.roundThreeDecimals(entry.getValue().get(columnIndex - 2));
                    // show in table slope + (coefficient)
                    data[rowIndex][columnIndex] = slope;
                } else if (slope == null) {
                    data[rowIndex][columnIndex] = "excluded";
                } else if (slope.isNaN()) {
                    data[rowIndex][columnIndex] = "NaN";
                }
            }
            rowIndex++;
        }
        // array of column names for table model
        String[] columnNames = new String[data[0].length];
        columnNames[1] = "Log-concentration";
        for (int x = 2; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + (x - 1);
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }

}
