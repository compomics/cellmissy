/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInitialPlotPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private Double bottomConstrainValue;
    private Double topConstrainValue;
    private boolean standardHillslope;
    private NonEditableTableModel tableModel;

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

    public NonEditableTableModel getTableModel() {
        return tableModel;
    }

    private void setTableModel(NonEditableTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public ChartPanel getInitialChartPanel() {
        return initialChartPanel;
    }
    
    /**
     * Initialize view
     */
    private void initDRInitialPanel() {
        dRInitialPlotPanel = new DRInitialPlotPanel();
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
        final LinkedHashMap<Double, List<Double>> dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup());
        //create and set the table model for the top panel table
        setTableModel(createTableModel(dataToFit));
        //Fit data according to initial parameters (standard hillslope, no constraints)
        doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getInitialFittingResults(), bottomConstrainValue, topConstrainValue, standardHillslope);
        //init chart panel
        initialChartPanel = new ChartPanel(null);
        initialChartPanel.setOpaque(false);
        //Plot fitted data in dose-response curve, along with R² annotation
        doseResponseController.plotDoseResponse();
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
                standardHillslope = true;
            }
        });
        /**
         * Set hillslope to variable for next fitting. Fitting will try to find
         * the optimal value for the parameter according to the data
         */
        dRInitialPlotPanel.getVariableHillslopeRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                standardHillslope = false;
            }
        });

        /**
         * If selected, text field to enter value for parameter constraining
         * will be taken into account on plotting.
         */
        dRInitialPlotPanel.getBottomCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    bottomConstrainValue = 0.0;
                } else {
                    bottomConstrainValue = null;
                }
            }
        });

        dRInitialPlotPanel.getTopCheckBox().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    topConstrainValue = 0.0;
                } else {
                    topConstrainValue = null;
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
                if (bottomConstrainValue != null) {
                    bottomConstrainValue = Double.parseDouble(dRInitialPlotPanel.getBottomTextField().getText());
                }
                if (topConstrainValue != null) {
                    topConstrainValue = Double.parseDouble(dRInitialPlotPanel.getTopTextField().getText());
                }
                doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getInitialFittingResults(), bottomConstrainValue, topConstrainValue, standardHillslope);
                doseResponseController.plotDoseResponse();
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

    /**
     * Create the table model for the top panel table. Table contains icon,
     * log-transformed concentration and replicate slopes per condition
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
