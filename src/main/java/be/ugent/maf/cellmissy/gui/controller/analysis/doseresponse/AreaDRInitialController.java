/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.result.doseresponse.AreaDoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInitialPlotPanel;
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
import org.jfree.chart.ChartPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for initial plot panel of dose-response analysis.
 *
 * @author Gwendolien
 */
@Controller("areaDRInitialController")
public class AreaDRInitialController implements DRInitialController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AreaDRInitialController.class);
    //model
    private Double bottomConstrainValue;
    private Double topConstrainValue;
    private NonEditableTableModel tableModel;
    private LinkedHashMap<Double, List<Double>> dataToFit;
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

    public LinkedHashMap<Double, List<Double>> getDataToFit() {
        return dataToFit;
    }

    public Double getBottomConstrainValue() {
        return bottomConstrainValue;
    }

    public Double getTopConstrainValue() {
        return topConstrainValue;
    }
    
    /**
     * When changing view from input panel: make dataset, do fitting and plot
     * according to starting parameters.
     */
    public void initDRInitialData() {
        //Log transform concentrations, keeping slopes the same
        dataToFit = prepareFittingData(doseResponseController.getdRAnalysisGroup());
        //create and set the table model for the top panel table
        setTableModel(createTableModel(dataToFit));
        //Fit data according to initial parameters (standard hillslope, no constraints)
        doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(false), null, null);

    }

    /**
     * Initialize view
     */
    private void initDRInitialPanel() {
        dRInitialPlotPanel = new DRInitialPlotPanel();

        //init chart panel
        initialChartPanel = new ChartPanel(null);
        initialChartPanel.setOpaque(false);
        /**
         * Action listeners for buttons
         */

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
                doseResponseController.performFitting(dataToFit, doseResponseController.getdRAnalysisGroup().getDoseResponseAnalysisResults().getFittingResults(false), bottomConstrainValue, topConstrainValue);
                //Plot fitted data in dose-response curve, along with R² annotation
                doseResponseController.plotDoseResponse(initialChartPanel, dRInitialPlotPanel.getDoseResponseChartParentPanel(), dataToFit, doseResponseController.getdRAnalysisGroup(), false);
                //Calculate new statistics
                doseResponseController.calculateStatistics();
            }
        });
    }

    /**
     * Private methods
     */
    /**
     * Prepare data for fitting starting from the analysis group.
     *
     * @param dRAnalysisGroup
     * @return LinkedHashMap That maps the concentration (log-transformed!) to
     * the replicate velocities
     */
    private LinkedHashMap<Double, List<Double>> prepareFittingData(AreaDoseResponseAnalysisGroup dRAnalysisGroup) {
        LinkedHashMap<Double, List<Double>> result = new LinkedHashMap<>();

        List<List<Double>> allVelocities = new ArrayList<List<Double>>();
        List<Double> allLogConcentrations = new ArrayList<Double>();

        //put concentrations of treatment to analyze (control not included!) in list
        LinkedHashMap<Double, String> nestedMap = dRAnalysisGroup.getConcentrationsMap().get(dRAnalysisGroup.getTreatmentToAnalyse());
        for (Double concentration : nestedMap.keySet()) {
            //key can only be linked with a single value, if one concentration is setup to have more than one associated concentration unit, only the last will remain
            String unit = nestedMap.get(concentration);

            Double logConcentration = AnalysisUtils.logTransform(concentration, unit);
            allLogConcentrations.add(logConcentration);
        }

        Double lowestLogConc = Collections.min(allLogConcentrations);
        //iterate through conditions
        int x = 0;
        for (PlateCondition plateCondition : dRAnalysisGroup.getVelocitiesMap().keySet()) {
            List<Double> replicateVelocities = dRAnalysisGroup.getVelocitiesMap().get(plateCondition);

            //check if this platecondition is the control
            for (Treatment treatment : plateCondition.getTreatmentList()) {
                if (treatment.getTreatmentType().getName().contains("ontrol")) {
                    allLogConcentrations.add(x, lowestLogConc - 1.0);
                }
            }

            allVelocities.add(replicateVelocities);
            x++;
        }

        for (int i = 0; i < allVelocities.size(); i++) {
            result.put(allLogConcentrations.get(i), allVelocities.get(i));
        }
        return result;
    }

    /**
     * Create the table model for the top panel table. Table contains
     * log-transformed concentration and replicate slopes per condition
     *
     * @param dataToFit
     * @return the model
     */
    private NonEditableTableModel createTableModel(LinkedHashMap<Double, List<Double>> dataToFit) {
        int maxReplicates = 0;
        for (Map.Entry<Double, List<Double>> entry : dataToFit.entrySet()) {
            int replicates = entry.getValue().size();
            if (replicates > maxReplicates) {
                maxReplicates = replicates;
            }
        }
        Object[][] data = new Object[dataToFit.size()][maxReplicates + 1];

        int rowIndex = 0;
        for (Map.Entry<Double, List<Double>> entry : dataToFit.entrySet()) {
            //log concentration is put on 1st column
            data[rowIndex][0] = AnalysisUtils.roundThreeDecimals(entry.getKey());

            for (int columnIndex = 1; columnIndex < entry.getValue().size() + 1; columnIndex++) {
                Double slope = entry.getValue().get(columnIndex - 1);
                if (slope != null && !slope.isNaN()) {
                    // round to three decimals slopes and coefficients
                    slope = AnalysisUtils.roundThreeDecimals(entry.getValue().get(columnIndex - 1));
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
        columnNames[0] = "Log-concentration";
        for (int x = 1; x < columnNames.length; x++) {
            columnNames[x] = "Repl " + (x);
        }

        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        return nonEditableTableModel;
    }
}
