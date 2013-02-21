/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsAnalysisListRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Data Analysis Controller Parent Controller: CellMissy Controller (main controller) Child Controllers: Bulk Cell Analysis Controller - Single Cell Analysis Controller
 *
 * @author Paola Masuzzo
 */
@Controller("dataAnalysisController")
public class DataAnalysisController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataAnalysisController.class);
    // format to show data
    private static final String DATA_FORMAT = PropertiesConfigurationHolder.getInstance().getString("dataFormat");
    //model
    private Experiment experiment;
    private PlateCondition currentCondition;
    private ObservableList<Algorithm> algorithmBindingList;
    private ObservableList<ImagingType> imagingTypeBindingList;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    private Format format;
    //array with time frames
    private double[] timeFrames;
    //view
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private AreaPreProcessingController areaPreProcessingController;
    @Autowired
    private AreaAnalysisController areaAnalysisController;
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
        format = new DecimalFormat(DATA_FORMAT);
        //init child controllers
        areaPreProcessingController.init();
        areaAnalysisController.init();
        // init other view
        initPlatePanel();
        initExperimentDataPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public PlateCondition getCurrentCondition() {
        return currentCondition;
    }

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    public double[] getTimeFrames() {
        return timeFrames;
    }

    public Format getFormat() {
        return format;
    }

    public AreaAnalysisPanel getAreaAnalysisPanel() {
        return areaPreProcessingController.getAreaAnalysisPanel();
    }

    public Map<PlateCondition, AreaPreProcessingResults> getPreProcessingMap() {
        return areaPreProcessingController.getPreProcessingMap();
    }

    public JFreeChart createGlobalAreaChart(List<PlateCondition> plateConditionList, boolean plotErrorBars) {
        return areaPreProcessingController.createGlobalAreaChart(plateConditionList, plotErrorBars);
    }

    public List<PlateCondition> getProcessedConditions() {
        return areaPreProcessingController.getProcessedConditions();
    }

    /**
     * Fetch time steps objects from DB, update TimeStepList according to Plate Condition
     *
     * @param plateCondition
     */
    public void fetchConditionTimeSteps(PlateCondition plateCondition) {
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
     * Set cursor from main controller
     *
     * @param type
     */
    public void setCursor(int type) {
        cellMissyController.setCursor(Cursor.getPredefinedCursor(type));
    }

    /**
     * Show Linear regression results from child controller
     */
    public void showLinearModelInTable() {
        areaAnalysisController.showLinearModelInTable();
    }

    /**
     * Show message through the main controller
     *
     * @param message
     * @param messageType
     */
    public void showMessage(String message, Integer messageType) {
        cellMissyController.showMessage(message, messageType);
    }

    /**
     * The condition is loaded and plate view is refreshed with not imaged wells highlighted in gray
     *
     * @param plateCondition
     */
    public void showNotImagedWells(PlateCondition plateCondition) {
        plateCondition.setLoaded(true);
        analysisPlatePanel.repaint();
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
                // retrieve selected project
                int locationToIndex = dataAnalysisPanel.getProjectJList().locationToIndex(e.getPoint());
                Project selectedProject = projectBindingList.get(locationToIndex);
                if (experiment == null) {
                    // project is being selected for the first time
                    onSelectedProject(selectedProject);
                } else if (experiment.getProject() != selectedProject) {
                    showMessage("If you want to analyse data from another experiment,\nplease exit and restart the application.", JOptionPane.INFORMATION_MESSAGE);
                    // ignore selection and select previous (current) prject
                    Project currentProject = experiment.getProject();
                    dataAnalysisPanel.getProjectJList().setSelectedIndex(projectBindingList.indexOf(currentProject));
                }
            }
        });

        //when an experiment is selected, show algorithms and imaging types used for that experiment
        //show also conditions in the Jlist behind and plate view according to the conditions setup
        dataAnalysisPanel.getExperimentJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected experiment
                int locationToIndex = dataAnalysisPanel.getExperimentJList().locationToIndex(e.getPoint());
                Experiment selectedExperiment = experimentBindingList.get(locationToIndex);
                // check if experiment is still null, then set it, otherwise warn the user, because an experiment was already chosen and data analysis was started
                if (experiment == null) {
                    onSelectedExperiment(selectedExperiment);
                } else if (experiment != selectedExperiment) {
                    showMessage("If you want to analyse data from another experiment,\nplease exit and restart the application.", JOptionPane.INFORMATION_MESSAGE);
                    // ignore selection and select previous experiment
                    dataAnalysisPanel.getExperimentJList().setSelectedIndex(experimentBindingList.indexOf(experiment));
                }

            }
        });

        //when a certain condition is selected, fetch time steps for each well of the condition
        dataAnalysisPanel.getConditionsList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int locationToIndex = dataAnalysisPanel.getConditionsList().locationToIndex(e.getPoint());
                PlateCondition selectedCondition = plateConditionList.get(locationToIndex);
                if (currentCondition != null) {
                    if (currentCondition != selectedCondition) {
                        // Execute Swing Worker to fetch Selected Condition: 
                        FetchConditionSwingWorker fetchSelectedConditionSW = new FetchConditionSwingWorker();
                        fetchSelectedConditionSW.execute();
                    }
                } else {
                    // Execute Swing Worker to fetch Selected Condition: 
                    FetchConditionSwingWorker fetchSelectedConditionSW = new FetchConditionSwingWorker();
                    fetchSelectedConditionSW.execute();
                }
                currentCondition = selectedCondition;
            }
        });


        // when an algorithm is selected, map needs to be init again and then filled in with new results
        // cache needs to be cleaned
        dataAnalysisPanel.getAlgorithmComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                areaPreProcessingController.initMapWithConditions();
                areaPreProcessingController.emptyDensityFunctionCache();
                if (currentCondition != null) {
                    // Execute Swing Worker to fetch Selected Condition: 
                    FetchConditionSwingWorker fetchSelectedConditionSW = new FetchConditionSwingWorker();
                    fetchSelectedConditionSW.execute();
                }
            }
        });

        // same for imaging type: map needs to be initialized again and fill in with new results
        // cache needs to be cleaned
        dataAnalysisPanel.getImagingTypeComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                areaPreProcessingController.initMapWithConditions();
                areaPreProcessingController.emptyDensityFunctionCache();
                if (currentCondition != null) {
                    // Execute Swing Worker to fetch Selected Condition: 
                    FetchConditionSwingWorker fetchSelectedConditionSW = new FetchConditionSwingWorker();
                    fetchSelectedConditionSW.execute();
                }
            }
        });

        //hide progress bar at first time
        dataAnalysisPanel.getFetchAllConditionsProgressBar().setVisible(false);
        dataAnalysisPanel.getFetchAllConditionsProgressBar().setStringPainted(true);
    }

    /**
     * Compute time frames from time steps list This method only needs to be called one, since time frames is set for the entire experiment Time frames are then equal for both types of analysis
     */
    private void computeTimeFrames() {
        double[] timeFrames = new double[experiment.getTimeFrames()];
        for (int i = 0; i < timeFrames.length; i++) {
            double timeFrame = i * experiment.getExperimentInterval();
            timeFrames[i] = timeFrame;
        }
        this.timeFrames = timeFrames;
    }

    /**
     * Action on selected project, find all relative performed experiments, if any
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        if (experimentService.findExperimentsByProjectIdAndStatus(selectedProject.getProjectid(), ExperimentStatus.PERFORMED) != null) {
            experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(selectedProject.getProjectid(), ExperimentStatus.PERFORMED));
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

    /**
     * Action on selected experiment, retrieve plate conditions and repaint plate panel Furthermore,
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        // set experiment
        experiment = selectedExperiment;
        //compute time frames array
        computeTimeFrames();
        // init a new list of plate conditions
        plateConditionList = new ArrayList<>();
        plateConditionList.addAll(experiment.getPlateConditionCollection());
        Dimension parentDimension = dataAnalysisPanel.getAnalysisPlateParentPanel().getSize();
        analysisPlatePanel.initPanel(experiment.getPlateFormat(), parentDimension);
        // repaint plate panel
        analysisPlatePanel.setExperiment(experiment);
        //dataAnalysisPanel.getAnalysisPlateParentPanel().repaint();
        analysisPlatePanel.repaint();
        //show conditions JList
        showConditionsList();
        // show algorithms and imaging types
        for (PlateCondition plateCondition : plateConditionList) {
            for (Well well : plateCondition.getWellCollection()) {
                List<Algorithm> algorithms = wellService.findAlgosByWellId(well.getWellid());
                if (algorithms != null) {
                    for (Algorithm algorithm : algorithms) {
                        if (!algorithmBindingList.contains(algorithm)) {
                            algorithmBindingList.add(algorithm);
                        }
                    }
                }

                List<ImagingType> imagingTypes = wellService.findImagingTypesByWellId(well.getWellid());
                if (imagingTypes != null) {
                    for (ImagingType imagingType : imagingTypes) {
                        if (imagingType != null && !imagingTypeBindingList.contains(imagingType)) {
                            imagingTypeBindingList.add(imagingType);
                        }
                    }
                }
            }
        }

        //init map with conditions and results holders
        areaPreProcessingController.initMapWithConditions();
        // init timeframes binding list with an empty one
        areaPreProcessingController.initTimeFramesList();
        //set selected algorithm to the first of the list
        dataAnalysisPanel.getAlgorithmComboBox().setSelectedIndex(0);
        //set selected imaging types to the first of the list
        dataAnalysisPanel.getImagingTypeComboBox().setSelectedIndex(0);
    }

    /**
     * update conditions list for current experiment
     */
    private void showConditionsList() {
        //set cell renderer for the List
        dataAnalysisPanel.getConditionsList().setCellRenderer(new ConditionsAnalysisListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, dataAnalysisPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Update time steps list with objects from actual selected condition
     *
     * @param plateCondition
     */
    private void updateTimeStepsList(PlateCondition plateCondition) {
        //clear the actual timeStepList
        if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
            areaPreProcessingController.getTimeStepsBindingList().clear();
        }
        // get only the wells that have been imaged
        List<Well> imagedWells = plateCondition.getImagedWells();
        for (Well well : imagedWells) {
            Collection<WellHasImagingType> wellHasImagingTypeCollection = well.getWellHasImagingTypeCollection();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeCollection) {
                Collection<TimeStep> timeStepCollection = wellHasImagingType.getTimeStepCollection();
                for (TimeStep timeStep : timeStepCollection) {
                    areaPreProcessingController.getTimeStepsBindingList().add(timeStep);
                }
            }
        }
    }

    /**
     * Swing Worker to fetch one condition time steps at once: The user selects a condition, a waiting cursor is shown on the screen and time steps result are fetched from DB. List of time steps is
     * updated. In addition, map of child controller is updated: computations are performed here and then shown in the done method of the class.
     */
    private class FetchConditionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Well> wellList = new ArrayList<>();
            wellList.addAll(currentCondition.getWellCollection());
            //fetch time steps for each well of condition 
            for (int i = 0; i < wellList.size(); i++) {
                //fetch time step collection for the wellhasimagingtype of interest
                Algorithm algorithm = algorithmBindingList.get(dataAnalysisPanel.getAlgorithmComboBox().getSelectedIndex());
                ImagingType imagingType = imagingTypeBindingList.get(dataAnalysisPanel.getImagingTypeComboBox().getSelectedIndex());
                wellService.fetchTimeSteps(wellList.get(i), algorithm.getAlgorithmid(), imagingType.getImagingTypeid());
            }
            // when all wells were fetched, update TimeStepList
            updateTimeStepsList(currentCondition);
            // if time steps were actually fetched from DB, update map
            if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
                //put the plate condition together with a pre-processing results holder in the map
                areaPreProcessingController.updateMapWithCondition(currentCondition);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
                    //populate table with time steps for current condition (algorithm and imaging type assigned) === THIS IS ONLY TO look at motility track RESULTS
                    areaPreProcessingController.showTimeStepsInTable();
                    //check which button is selected for analysis:
                    if (areaPreProcessingController.getAreaAnalysisPanel().getNormalizeAreaButton().isSelected()) {
                        //for current selected condition show normalized area values together with time frames
                        areaPreProcessingController.showNormalizedAreaInTable(currentCondition);
                        // show raw data plot (all replicates)
                        areaPreProcessingController.plotRawDataReplicates(currentCondition);
                    }
                    if (areaPreProcessingController.getAreaAnalysisPanel().getDeltaAreaButton().isSelected()) {
                        //for current selected condition show delta area values 
                        areaPreProcessingController.showDeltaAreaInTable(currentCondition);
                    }
                    if (areaPreProcessingController.getAreaAnalysisPanel().getPercentageAreaIncreaseButton().isSelected()) {
                        //for current selected condition show %increments (for outliers detection)
                        areaPreProcessingController.showAreaIncreaseInTable(currentCondition);
                        //show density function for selected condition (Raw Data)
                        areaPreProcessingController.plotDensityFunctions(currentCondition);
                    }
                    if (areaPreProcessingController.getAreaAnalysisPanel().getCorrectedAreaButton().isSelected()) {
                        //for current selected condition show corrected area values (outliers have been deleted from distribution)
                        areaPreProcessingController.showCorrectedAreaInTable(currentCondition);
                        //show Area increases with time frames
                        areaPreProcessingController.plotCorrectedDataReplicates(currentCondition);
                    }
                } else {
                    // the entire condition was not imaged/analyzed: inform the user
                    showMessage("This condition was not imaged at all!", JOptionPane.INFORMATION_MESSAGE);
                    areaPreProcessingController.resetViews();
                }
                // set cursor back to default and show all computed results for selected condition
                cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                // the condition is loaded, and plate view is refreshed
                showNotImagedWells(currentCondition);
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ExecutionException ex) {
                showMessage("An expected error occured: " + ex.getMessage() + ", please try to restart the application.", JOptionPane.ERROR_MESSAGE);
            } catch (CancellationException ex) {
                LOG.info("Data fetching/computation was cancelled.");
            }
        }
    }
}
