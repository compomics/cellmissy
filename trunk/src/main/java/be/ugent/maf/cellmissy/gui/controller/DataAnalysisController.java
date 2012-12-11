/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

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
import be.ugent.maf.cellmissy.gui.experiment.analysis.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsListRenderer;
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
import javax.swing.SwingWorker;
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

    // format to show data
    private static final String DATA_FORMAT = PropertiesConfigurationHolder.getInstance().getString("dataFormat");
    //model
    private Experiment experiment;
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
     * @return 
     */
    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public PlateCondition getSelectedCondition() {
        return (PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue();
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

    /**
     * Fetch time steps objects from DB, update TimeStepList according to Plate Condition
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
     * @param message
     * @param messageType  
     */
    public void showMessage(String message, Integer messageType){
        cellMissyController.showMessage(message, messageType);
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
        //show also conditions in the Jlist behind and plate view according to the conditions setup
        dataAnalysisPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("experiment ***");
                int locationToIndex = dataAnalysisPanel.getExperimentJList().locationToIndex(e.getPoint());
                experiment = experimentBindingList.get(locationToIndex);
                //compute time frames array
                computeTimeFrames();
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
                areaPreProcessingController.initMap();
                // init timeframes binding list with an empty one
                areaPreProcessingController.initTimeFramesList();
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
                FetchConditionTimeStepsSwingWorker fetchSelectedConditionSW = new FetchConditionTimeStepsSwingWorker();
                fetchSelectedConditionSW.execute();
            }
        });


        // when an algorithm is selected, map needs to be init again and then filled in with new results
        // cache needs to be cleaned
        dataAnalysisPanel.getAlgorithmComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                areaPreProcessingController.initMap();
                areaPreProcessingController.emptyDensityFunctionCache();
            }
        });

        // same for imaging type: map needs to be initialized again and fill in with new results
        // cache needs to be cleaned
        dataAnalysisPanel.getImagingTypeComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                areaPreProcessingController.initMap();
                areaPreProcessingController.emptyDensityFunctionCache();
            }
        });

        //hide progress bar at first time
        dataAnalysisPanel.getFetchAllConditionsProgressBar().setVisible(false);
        dataAnalysisPanel.getFetchAllConditionsProgressBar().setStringPainted(true);
    }

    /**
     * Compute time frames from time steps list
     * This method only needs to be called one, since time frames is set for the entire experiment
     * Time frames are then equal for both types of analysis
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
     * update conditions list for current experiment 
     */
    private void showConditions() {
        //set cell renderer for the List
        dataAnalysisPanel.getConditionsList().setCellRenderer(new ConditionsListRenderer(plateConditionList));
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
        if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
            areaPreProcessingController.getTimeStepsBindingList().clear();
        }
        for (Well well : plateCondition.getWellCollection()) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeCollection()) {
                Collection<TimeStep> timeStepCollection = wellHasImagingType.getTimeStepCollection();
                for (TimeStep timeStep : timeStepCollection) {
                    areaPreProcessingController.getTimeStepsBindingList().add(timeStep);
                }
            }
        }
    }

    /**
     * Swing Worker to fetch one condition time steps at once:
     * The user selects a condition, a waiting cursor is shown on the screen and time steps result are fetched from DB.
     * List of time steps is updated.
     * In addition, map of child controller is updated: computations are performed here and then shown in the done method of the class.
     */
    private class FetchConditionTimeStepsSwingWorker extends SwingWorker<Void, Void> {

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

            //put the plate condition together with a pre-processing results holder in the map
            areaPreProcessingController.updateMapWithCondition(getSelectedCondition());
            return null;
        }

        @Override
        protected void done() {
            //populate table with time steps for current condition (algorithm and imaging type assigned) === THIS IS ONLY TO look at motility track RESULTS
            areaPreProcessingController.showTimeStepsInTable();
            //check which button is selected for analysis:
            if (areaPreProcessingController.getAreaAnalysisPanel().getNormalizeAreaButton().isSelected()) {
                //for current selected condition show normalized area values together with time frames
                areaPreProcessingController.showNormalizedAreaInTable(getSelectedCondition());
                // show raw data plot (all replicates)
                areaPreProcessingController.plotRawDataReplicates(getSelectedCondition());
            }
            if (areaPreProcessingController.getAreaAnalysisPanel().getDeltaAreaButton().isSelected()) {
                //for current selected condition show delta area values 
                areaPreProcessingController.showDeltaAreaInTable(getSelectedCondition());
            }
            if (areaPreProcessingController.getAreaAnalysisPanel().getPercentageAreaIncreaseButton().isSelected()) {
                //for current selected condition show %increments (for outliers detection)
                areaPreProcessingController.showAreaIncreaseInTable(getSelectedCondition());
                //show density function for selected condition (Raw Data)
                areaPreProcessingController.plotDensityFunctions(getSelectedCondition());
            }
            if (areaPreProcessingController.getAreaAnalysisPanel().getCorrectedAreaButton().isSelected()) {
                //for current selected condition show corrected area values (outliers have been deleted from distribution)
                areaPreProcessingController.showCorrectedAreaInTable(getSelectedCondition());
                //show Area increases with time frames
                areaPreProcessingController.plotCorrectedDataReplicates(getSelectedCondition());
            }
            // set cursor back to default and show all computed results for selected condition
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
