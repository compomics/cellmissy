/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Data Analysis Controller
 * Parent Controller: CellMissy Controller (main controller)
 * Child Controllers: Bulk Cell Analysis Controller - Single Cell Analysis Controller
 * @author Paola Masuzzo
 */
@Controller("dataAnalysisController")
public class DataAnalysisController {

    //model
    private Experiment experiment;
    private ObservableList<Algorithm> algorithmBindingList;
    private ObservableList<ImagingType> imagingTypeBindingList;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    //view
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private BulkCellAnalysisController bulkCellAnalysisPanelController;
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private WellService wellService;
    @Autowired
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        //init view
        dataAnalysisPanel = new DataAnalysisPanel();
        analysisPlatePanel = new AnalysisPlatePanel();
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init child controller
        bulkCellAnalysisPanelController.init();
        initPlatePanel();
        initExperimentDataPanel();
        initAnalysisPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public AnalysisPlatePanel getAnalysisPlatePanel() {
        return analysisPlatePanel;
    }

    public PlateCondition getSelectedCondition() {
        return (PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue();
    }

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    /**
     * Fetch time steps objects from DB, update TimeStepList according to Plate Condition
     * @param plateCondition 
     */
    private void fetchConditionTimeSteps(PlateCondition plateCondition) {
        List<Well> wellList = new ArrayList<>();
        wellList.addAll(plateCondition.getWellCollection());
        //fetch time steps for each well
        for (int i = 0; i < wellList.size(); i++) {
            //fetch time step collection for the wellhasimagingtype of interest
            wellService.fetchTimeSteps(wellList.get(i), algorithmBindingList.get(dataAnalysisPanel.getAlgorithmComboBox().getSelectedIndex()).getAlgorithmid(), imagingTypeBindingList.get(dataAnalysisPanel.getImagingTypeComboBox().getSelectedIndex()).getImagingTypeid());
        }
        //update timeStep List for current selected condition
        updateTimeStepsList(plateCondition);
    }

    /**
     * Fetch Tracks Objects from DB 
     * @param plateCondition 
     */
    private void fetchConditionTracks(PlateCondition plateCondition) {
        List<Well> wellList = new ArrayList<>();
        wellList.addAll(plateCondition.getWellCollection());
       
        for (int i = 0; i < wellList.size(); i++) {
            //fetch time step collection for the wellhasimagingtype of interest
            wellService.fetchTracks(wellList.get(i), algorithmBindingList.get(dataAnalysisPanel.getAlgorithmComboBox().getSelectedIndex()).getAlgorithmid(), imagingTypeBindingList.get(dataAnalysisPanel.getImagingTypeComboBox().getSelectedIndex()).getImagingTypeid());
            wellService.fetchTrackPoints(wellList.get(i), algorithmBindingList.get(dataAnalysisPanel.getAlgorithmComboBox().getSelectedIndex()).getAlgorithmid(), imagingTypeBindingList.get(dataAnalysisPanel.getImagingTypeComboBox().getSelectedIndex()).getImagingTypeid());
        }
    }

    /**
     * private methods and classes
     */
    /**
     * initialize plate panel view
     */
    private void initPlatePanel() {
        //show as default a 96 plate format
        Dimension parentDimension = dataAnalysisPanel.getAnalysisPlateParentPanel().getSize();
        analysisPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        dataAnalysisPanel.getAnalysisPlateParentPanel().add(analysisPlatePanel, gridBagConstraints);
        dataAnalysisPanel.getAnalysisPlateParentPanel().repaint();
    }

    /**
     * initialize left panel: projectList, experimentList, Algorithm and imaging type Combo box, plateConditions list
     */
    private void initExperimentDataPanel() {

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, dataAnalysisPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);

        //init algorithms combobox
        algorithmBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, algorithmBindingList, dataAnalysisPanel.getAlgorithmComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init imagingtypes combo box
        imagingTypeBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, imagingTypeBindingList, dataAnalysisPanel.getImagingTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments performed for that project        
        dataAnalysisPanel.getProjectJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("project ***");
                //init experimentJList
                int locationToIndex = dataAnalysisPanel.getProjectJList().locationToIndex(e.getPoint());
                if (experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.PERFORMED) != null) {
                    experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.PERFORMED));
                    JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, dataAnalysisPanel.getExperimentJList());
                    bindingGroup.addBinding(jListBinding);
                    bindingGroup.bind();
                } else {
                    cellMissyController.showMessage("There are no experiments performed yet for this project!", 1);
                    if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                        experimentBindingList.clear();
                    }
                }
            }
        });

        //when an experiment is selected, show algorithms and imaging types used for that experiment
        //show also conditions in the Jlist behind and plate view according to the conditions
        dataAnalysisPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("experiment ***");
                int locationToIndex = dataAnalysisPanel.getExperimentJList().locationToIndex(e.getPoint());
                experiment = experimentBindingList.get(locationToIndex);

                plateConditionList = new ArrayList<>();
                plateConditionList.addAll(experiment.getPlateConditionCollection());

                for (PlateCondition plateCondition : plateConditionList) {
                    for (Well well : plateCondition.getWellCollection()) {
                        //show algorithms used for experiment
                        for (Algorithm algorithm : wellService.findAlgosByWellId(well.getWellid())) {
                            if (!algorithmBindingList.contains(algorithm)) {
                                algorithmBindingList.add(algorithm);
                            }
                        }
                        //show imaging types used for experiment
                        for (ImagingType imagingType : wellService.findImagingTypesByWellId(well.getWellid())) {
                            if (!imagingTypeBindingList.contains(imagingType)) {
                                imagingTypeBindingList.add(imagingType);
                            }
                        }
                    }
                }
                //init map with conditions and results holders
                bulkCellAnalysisPanelController.initMap();
                //set selected algorithm to the first of the list
                dataAnalysisPanel.getAlgorithmComboBox().setSelectedIndex(0);
                //set selected imaging types to the first of the list
                dataAnalysisPanel.getImagingTypeComboBox().setSelectedIndex(0);
                //show conditions for selected experiment
                showConditions();
                //show conditions in the plate panel (with rectangles and colors)
                analysisPlatePanel.setExperiment(experiment);
                analysisPlatePanel.repaint();
            }
        });

        //when a certain condition is selected, fetch time steps for each well of the condition
        dataAnalysisPanel.getConditionsList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("condition ***");
                // Execute Swing Worker to fetch Selected Condition: 
                FetchConditionSwingWorker fetchSelectedConditionSW = new FetchConditionSwingWorker();
                fetchSelectedConditionSW.execute();
            }
        });

        // when an algorithm is selected, map needs to be init again and then filled in with new results
        // cache needs to be cleaned
        dataAnalysisPanel.getAlgorithmComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bulkCellAnalysisPanelController.initMap();
                bulkCellAnalysisPanelController.emptyDensityFunctionCache();
            }
        });

        // same for imaging type: map needs to be initialized again and fill in with new results
        // cache needs to be cleaned
        dataAnalysisPanel.getImagingTypeComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bulkCellAnalysisPanelController.initMap();
                bulkCellAnalysisPanelController.emptyDensityFunctionCache();
            }
        });

        // time steps table is not focusable, nor the user can select rows
        dataAnalysisPanel.getTimeStepsTable().setFocusable(false);
        dataAnalysisPanel.getTimeStepsTable().setRowSelectionAllowed(false);
        // set background to white 
        dataAnalysisPanel.getTimeStepsTableScrollPane().getViewport().setBackground(Color.white);
    }

    /**
     * initialize analysis panel
     */
    private void initAnalysisPanel() {
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup buttonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        buttonGroup.add(dataAnalysisPanel.getNormalizeAreaButton());
        buttonGroup.add(dataAnalysisPanel.getDeltaAreaButton());
        buttonGroup.add(dataAnalysisPanel.getPercentageAreaIncreaseButton());
        buttonGroup.add(dataAnalysisPanel.getCorrectedAreaButton());
        //select as default first button (Delta Area values Computation)
        dataAnalysisPanel.getNormalizeAreaButton().setSelected(true);
        //hide progress bar at first time
        dataAnalysisPanel.getFetchAllConditionsProgressBar().setVisible(false);
        dataAnalysisPanel.getFetchAllConditionsProgressBar().setStringPainted(true);
        // linear model results can not been shown before going to step: global view  plot
        dataAnalysisPanel.getBulkTabbedPane().setEnabledAt(3, false);
        // control opaque property of table
        dataAnalysisPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);

        /**
         * Calculate Normalized Area (with corrected values for Jumps)
         */
        dataAnalysisPanel.getNormalizeAreaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    //show normalized values in the table
                    bulkCellAnalysisPanelController.showNormalizedAreaInTable(getSelectedCondition());
                    //set charts panel to null
                    bulkCellAnalysisPanelController.getDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getCorrectedDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getRawDataChartPanel().setChart(null);
                    dataAnalysisPanel.getGraphicsParentPanel().remove(bulkCellAnalysisPanelController.getDistanceMatrixPanel());
                    dataAnalysisPanel.repaint();
                    // show raw data plot (replicates)
                    bulkCellAnalysisPanelController.plotRawDataReplicates(getSelectedCondition());
                }
            }
        });

        /**
         * Show Delta Area Values
         */
        dataAnalysisPanel.getDeltaAreaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    //show delta area values in the table            
                    bulkCellAnalysisPanelController.showDeltaAreaInTable(getSelectedCondition());
                    // remove other panels
                    bulkCellAnalysisPanelController.getRawDataChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getCorrectedDensityChartPanel().setChart(null);
                    dataAnalysisPanel.getGraphicsParentPanel().remove(bulkCellAnalysisPanelController.getDistanceMatrixPanel());
                    dataAnalysisPanel.repaint();
                }
            }
        });

        /**
         * Show %Area increase values
         */
        dataAnalysisPanel.getPercentageAreaIncreaseButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    //show %increments of area between two consecutive time frames and determine if a JUMP is present
                    bulkCellAnalysisPanelController.showAreaIncreaseInTable(getSelectedCondition());
                    // remove other panels
                    bulkCellAnalysisPanelController.getRawDataChartPanel().setChart(null);
                    dataAnalysisPanel.getGraphicsParentPanel().remove(bulkCellAnalysisPanelController.getDistanceMatrixPanel());
                    dataAnalysisPanel.getGraphicsParentPanel().revalidate();
                    dataAnalysisPanel.getGraphicsParentPanel().repaint();
                    //show density function for selected condition
                    bulkCellAnalysisPanelController.plotDensityFunctions(getSelectedCondition());
                }
            }
        });

        /**
         * show Corrected values for Area (corrected for outliers intra replicate)
         * show table with Euclidean distances between all replicates
         * plot area replicates according to distance matrix
         */
        dataAnalysisPanel.getCorrectedAreaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    // show values in table
                    bulkCellAnalysisPanelController.showCorrectedAreaInTable(getSelectedCondition());
                    // remove other panels
                    dataAnalysisPanel.getGraphicsParentPanel().remove(bulkCellAnalysisPanelController.getRawDataChartPanel());
                    dataAnalysisPanel.getGraphicsParentPanel().remove(bulkCellAnalysisPanelController.getDensityChartPanel());
                    dataAnalysisPanel.getGraphicsParentPanel().remove(bulkCellAnalysisPanelController.getCorrectedDensityChartPanel());
                    dataAnalysisPanel.getGraphicsParentPanel().revalidate();
                    dataAnalysisPanel.getGraphicsParentPanel().repaint();
                    bulkCellAnalysisPanelController.initDistanceMatrixPanel();
                    // show distance matrix
                    bulkCellAnalysisPanelController.showDistanceMatrix(getSelectedCondition());
                    // plot corrected area (all replicates for selected condition)
                    bulkCellAnalysisPanelController.plotCorrectedAreaReplicates(getSelectedCondition());
                }
            }
        });

        /**
         * Add a Change Listener to Bulk Tabbed Pane: Actions are triggered when a tab is being clicked
         */
        dataAnalysisPanel.getBulkTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                //click on Global View Panel and show Global Area increase among ALL conditions.
                //If some conditions still need to be analyzed, a swing worker is needed.
                if (dataAnalysisPanel.getBulkTabbedPane().getSelectedIndex() == 2) {
                    // check if a swing worker with a progress bar is actually needed: only if the number of fetched conditions is not equal to the number of all conditions of experiment
                    if (getNumberOfFetchedCondition() != plateConditionList.size()) {
                        // create and execute a swinger
                        FetchAllConditionsSwingWorker fetchAllConditionsSwingWorker = new FetchAllConditionsSwingWorker();
                        fetchAllConditionsSwingWorker.execute();
                    } else {
                        // swinger is no needed: plot Global Area
                        bulkCellAnalysisPanelController.plotGlobalArea();
                    }
                }
                // click on "Analysis" tab, show Linear Model Results
                if (dataAnalysisPanel.getBulkTabbedPane().getSelectedIndex() == 3) {
                    bulkCellAnalysisPanelController.showLinearModelInTable();
                }
            }
        });

        /**
         * List selection Listener for linear model results Table
         * show bar charts according to user selection in model
         */
        dataAnalysisPanel.getSlopesTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                bulkCellAnalysisPanelController.showVelocityBars();
            }
        });
    }

    /**
     * update conditions list for current experiment 
     */
    private void showConditions() {
        //set cell renderer for the List
        dataAnalysisPanel.getConditionsList().setCellRenderer(new ConditionsRenderer());
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, dataAnalysisPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Update time steps list with objects from actual selected condition
     * @param plateCondition 
     */
    private void updateTimeStepsList(PlateCondition plateCondition) {
        //clear the actual timeStepList
        if (!bulkCellAnalysisPanelController.getTimeStepsBindingList().isEmpty()) {
            bulkCellAnalysisPanelController.getTimeStepsBindingList().clear();
        }
        for (Well well : plateCondition.getWellCollection()) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeCollection()) {
                Collection<TimeStep> timeStepCollection = wellHasImagingType.getTimeStepCollection();
                for (TimeStep timeStep : timeStepCollection) {
                    bulkCellAnalysisPanelController.getTimeStepsBindingList().add(timeStep);
                }
            }
        }
    }

    /**
     * Get the number of conditions that have already been analyzed
     * The user has clicked on them and pre-process results were already computed
     * @return 
     */
    private int getNumberOfFetchedCondition() {
        int progress = 0;
        for (PlateCondition plateCondition : plateConditionList) {
            if (bulkCellAnalysisPanelController.getMap().get(plateCondition) != null) {
                progress++;
            }
        }
        return progress;
    }

    /**
     * Swing Worker for Global Area Plot: 
     * we check how many conditions were already fetched, and we update the map of bulk cell analysis controller
     * in background, all the computations needed for the global area view plot are performed.
     */
    private class FetchAllConditionsSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // show progress bar
            dataAnalysisPanel.getFetchAllConditionsProgressBar().setVisible(true);
            // set max value of progress bar to size of conditions' list
            dataAnalysisPanel.getFetchAllConditionsProgressBar().setMaximum(plateConditionList.size());
            // add property change listener to progress bar
            dataAnalysisPanel.getFetchAllConditionsProgressBar().addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        int progress = (Integer) evt.getNewValue();
                        dataAnalysisPanel.getFetchAllConditionsProgressBar().setValue(progress);
                    }
                }
            });

            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (PlateCondition plateCondition : plateConditionList) {
                // if for current condition computations were not performed yet
                if (bulkCellAnalysisPanelController.getMap().get(plateCondition) == null) {
                    // update status of progress bar with the current number of fetched conditions
                    dataAnalysisPanel.getFetchAllConditionsProgressBar().setValue(getNumberOfFetchedCondition());
                    dataAnalysisPanel.getFetchAllConditionsProgressBar().setString("Condition " + getNumberOfFetchedCondition() + "/" + dataAnalysisPanel.getFetchAllConditionsProgressBar().getMaximum());
                    // fetch current condition
                    fetchConditionTimeSteps(plateCondition);
                    // uodate map (this is actually doing all the computations)
                    bulkCellAnalysisPanelController.updateMapWithCondition(plateCondition);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            // when the thread is done, hide progress bar again
            dataAnalysisPanel.getFetchAllConditionsProgressBar().setVisible(false);
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // show all conditions in one plot (Global Area View)
            bulkCellAnalysisPanelController.plotGlobalArea();
            // enable now tab for analysis
            dataAnalysisPanel.getBulkTabbedPane().setEnabledAt(3, true);
        }
    }

    /**
     * Swing Worker to fetch one condition at once:
     * The user selects a condition, a waiting cursor is shown on the screen and time steps result are fetched from DB.
     * List of time steps is updated and time frames for experiment are computed.
     * In addition, map of child controller is updated: computations are performed here and then shown in the done method of the class.
     */
    private class FetchConditionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Well> wellList = new ArrayList<>();
            wellList.addAll(getSelectedCondition().getWellCollection());
            //fetch time steps for each well of condition 
            for (int i = 0; i < wellList.size(); i++) {
                //fetch time step collection for the wellhasimagingtype of interest
                wellService.fetchTimeSteps(wellList.get(i), algorithmBindingList.get(dataAnalysisPanel.getAlgorithmComboBox().getSelectedIndex()).getAlgorithmid(), imagingTypeBindingList.get(dataAnalysisPanel.getImagingTypeComboBox().getSelectedIndex()).getImagingTypeid());
            }
            // when all wells were fetched, update TimeStepList
            updateTimeStepsList(getSelectedCondition());
            //compute time frames array for child controller (bulk cell controller)
            bulkCellAnalysisPanelController.computeTimeFrames();
            //put the plate condition together with a pre-processing results holder in the map
            bulkCellAnalysisPanelController.updateMapWithCondition(getSelectedCondition());
            return null;
        }

        @Override
        protected void done() {
            //populate table with time steps for current condition (algorithm and imaging type assigned) === THIS IS ONLY TO look at motility track RESULTS
            bulkCellAnalysisPanelController.showTimeStepsInTable();
            //check which button is selected for analysis:
            if (dataAnalysisPanel.getNormalizeAreaButton().isSelected()) {
                //for current selected condition show normalized area values together with time frames
                bulkCellAnalysisPanelController.showNormalizedAreaInTable(getSelectedCondition());
                // show raw data plot (all replicates)
                bulkCellAnalysisPanelController.plotRawDataReplicates(getSelectedCondition());
            }
            if (dataAnalysisPanel.getDeltaAreaButton().isSelected()) {
                //for current selected condition show delta area values 
                bulkCellAnalysisPanelController.showDeltaAreaInTable(getSelectedCondition());
            }
            if (dataAnalysisPanel.getPercentageAreaIncreaseButton().isSelected()) {
                //for current selected condition show %increments (for outliers detection)
                bulkCellAnalysisPanelController.showAreaIncreaseInTable(getSelectedCondition());
                //show density function for selected condition (Raw Data)
                bulkCellAnalysisPanelController.plotDensityFunctions(getSelectedCondition());
            }
            if (dataAnalysisPanel.getCorrectedAreaButton().isSelected()) {
                //for current selected condition show corrected area values (outliers have been deleted from distribution)
                bulkCellAnalysisPanelController.showCorrectedAreaInTable(getSelectedCondition());
                //show Area increases with time frames
                bulkCellAnalysisPanelController.showDistanceMatrix(getSelectedCondition());
                bulkCellAnalysisPanelController.plotCorrectedAreaReplicates(getSelectedCondition());
            }
            // set cursor back to default and show all computed results for selected condition
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * renderer for the Conditions JList
     */
    private class ConditionsRenderer extends DefaultListCellRenderer {

        // constructor
        public ConditionsRenderer() {
            setOpaque(true);
            setIconTextGap(10);
        }

        //Overrides method from the DefaultListCellRenderer
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            int conditionIndex = plateConditionList.indexOf((PlateCondition) value);
            setIcon(new rectIcon(GuiUtils.getAvailableColors()[conditionIndex + 1]));
            return this;
        }
    }

    /**
     * rectangular icon for the Condition list
     */
    private class rectIcon implements Icon {

        private final Integer rectHeight = 10;
        private final Integer rectWidth = 25;
        private Color color;

        /**
         * constructor
         * @param color 
         */
        public rectIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            //loadDataPlatePanelController.getLoadDataPlatePanel().setGraphics(g2d);
            g2d.setColor(color);
            g2d.fillRect(x, y, rectWidth, rectHeight);
        }

        @Override
        public int getIconWidth() {
            return rectWidth;
        }

        @Override
        public int getIconHeight() {
            return rectHeight;
        }
    }

    public void setCursor(int type) {
        cellMissyController.setCursor(Cursor.getPredefinedCursor(type));
    }
}
