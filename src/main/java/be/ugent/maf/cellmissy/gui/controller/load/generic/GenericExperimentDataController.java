/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.experiment.load.ExperimentMetadataPanel;
import be.ugent.maf.cellmissy.gui.experiment.load.ExperimentOverviewPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsLoadListRenderer;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl.CycleTimeUnit;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("genericExperimentDataController")
public class GenericExperimentDataController {

    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    //view
    private ExperimentOverviewPanel experimentOverviewPanel;
    private ExperimentMetadataPanel experimentMetadataPanel;
    //parent controller
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    //child controllers
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        bindingGroup = new BindingGroup();
        //create main panels
        experimentMetadataPanel = new ExperimentMetadataPanel();
        experimentOverviewPanel = new ExperimentOverviewPanel();
        //init main view
        initExperimentMetadataPanel();
    }

    /**
     * this method checks experiment Info
     *
     * @return
     */
    public List<String> setExperimentMetadata() {
        Experiment experiment = loadExperimentFromGenericInputController.getExperiment();
        List<String> messages = validateExperimentMetadata();
        // check that info is not left blank
        if (messages.isEmpty()) {
            try {
                // time frames
                experiment.setTimeFrames(Integer.parseInt(experimentMetadataPanel.getTimeFramesTextField().getText()));
                // interval
                experiment.setExperimentInterval(Double.parseDouble(experimentMetadataPanel.getIntervalTextField().getText()));
                // duration
                experiment.setDuration(Double.parseDouble(experimentMetadataPanel.getDurationTextField().getText()));
            } catch (NumberFormatException e) {
                messages.add("Please insert valid experiment metadata");
            }
        }
        return messages;
    }

    /**
     * Validate Experiment Metadata
     *
     * @return a list of string to show to the user if validation was not OK.
     */
    private List<String> validateExperimentMetadata() {
        List<String> messages = new ArrayList<>();
        String durationInfo = experimentMetadataPanel.getDurationTextField().getText();
        String intervalInfo = experimentMetadataPanel.getIntervalTextField().getText();
        String timeFramesInfo = experimentMetadataPanel.getTimeFramesTextField().getText();
        // if one of these fields is empty, set boolean to false
        if (durationInfo.equals("") || intervalInfo.equals("") || timeFramesInfo.equals("")) {
            messages.add("Please insert all experiment metadata.");
        }
        return messages;
    }

    /**
     * Initialize Experiment metadata panel
     */
    private void initExperimentMetadataPanel() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentOverviewPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        //do the binding
        bindingGroup.bind();

        // fill in combobox with units
        for (CycleTimeUnit unit : CycleTimeUnit.values()) {
            experimentMetadataPanel.getIntervalUnitComboBox().addItem(unit);
        }
        // show MINUTES as default
        experimentMetadataPanel.getIntervalUnitComboBox().setSelectedIndex(1);

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments in progress for that project
        experimentOverviewPanel.getProjectJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected project
                int locationToIndex = experimentOverviewPanel.getProjectJList().locationToIndex(e.getPoint());
                Project selectedProject = projectBindingList.get(locationToIndex);
                if (loadExperimentFromGenericInputController.getExperiment() == null) {
                    // project is being selected for the first time
                    onSelectedProject(selectedProject);
                } else if (loadExperimentFromGenericInputController.getExperiment().getProject() != selectedProject) {
                    // another project has been selected, different from current one: warn the user!
                    Object[] options = {"Yes", "No"};
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "Current data will not be saved.\nContinue with another project?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    switch (showOptionDialog) {
                        case 0:
                            // another project is selected, reset objects and move on new project
                            resetOnANewProject();
                            onSelectedProject(selectedProject);
                            break;
                        case 1:
                            // ignore selection and select previous (current) prject
                            Project currentProject = loadExperimentFromGenericInputController.getExperiment().getProject();
                            experimentOverviewPanel.getProjectJList().setSelectedIndex(projectBindingList.indexOf(currentProject));
                            break;
                    }
                }

            }
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        experimentOverviewPanel.getExperimentJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected experiment
                int locationToIndex = experimentOverviewPanel.getExperimentJList().locationToIndex(e.getPoint());
                Experiment selectedExperiment = experimentBindingList.get(locationToIndex);
                // check if experiment is still null, then set it, otherwise warn the user, because an experiment was already chosen and import was started
                if (loadExperimentFromGenericInputController.getExperiment() == null) {
                    // experiment is being selected for the first time
                    onSelectedExperiment(selectedExperiment);
                } else if (loadExperimentFromGenericInputController.getExperiment() != selectedExperiment) {
                    // another experiment has been selected, different from current one: warn the user!
                    Object[] options = {"Yes", "No"};
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "Current data will not be saved.\nContinue with another experiment?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    // if YES, user wants to delete existing file and replace it
                    switch (showOptionDialog) {
                        case 0:
                            // another experiment will be processed (previously data not stored)
                            resetOnANewExperiment();
                            onSelectedExperiment(selectedExperiment);
                            break;
                        case 1:
                            // ignore selection and select previous experiment
                            Experiment currentExperiment = loadExperimentFromGenericInputController.getExperiment();
                            experimentOverviewPanel.getExperimentJList().setSelectedIndex(experimentBindingList.indexOf(currentExperiment));
                            return;
                    }
                } else if (loadExperimentFromGenericInputController.getExperiment() == selectedExperiment) {
                    // click again on same experiment: ignore selection
                    return;
                }
                loadExperimentFromGenericInputController.updateInfoLabel(loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getInfolabel(), "Add datasets and imaging types you want to import. Then select an imaging type to start importing data.");
                loadExperimentFromGenericInputController.enableButtons();
            }
        });

        //add view to parent panel
        loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getExpOverviewParentPanel().add(experimentOverviewPanel, gridBagConstraints);
        loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getExpMetadataParentPanel().add(experimentMetadataPanel, gridBagConstraints);
    }

    /**
     * Reset after having chosen a new project
     */
    private void resetOnANewProject() {
        // reset conditions list and plate view
        loadExperimentFromGenericInputController.setExperiment(null);
        experimentOverviewPanel.getConditionsList().setCellRenderer(null);
        loadExperimentFromGenericInputController.getImagedPlatePanel().setExperiment(null);
        loadExperimentFromGenericInputController.reset();
    }

    /**
     * Reset after having chosen a new experiment
     */
    private void resetOnANewExperiment() {
        loadExperimentFromGenericInputController.reset();
    }

    /**
     * this method shows a list of conditions once an experiment is selected
     */
    private void showConditionsList() {
        //set Cell Renderer for Condition List
        experimentOverviewPanel.getConditionsList().setCellRenderer(new ConditionsLoadListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, experimentOverviewPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Action on experiment selected, retrieve plate conditions and repaint plate panel
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        //set experiment of parent controller
        loadExperimentFromGenericInputController.setExperiment(selectedExperiment);
        // init a new list with plate conditions
        plateConditionList = new ArrayList<>();
        plateConditionList.addAll(loadExperimentFromGenericInputController.getExperiment().getPlateConditionCollection());
        // repaint plate panel
        loadExperimentFromGenericInputController.getImagedPlatePanel().setExperiment(selectedExperiment);
        Dimension parentDimension = loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getPlateViewParentPanel().getSize();
        loadExperimentFromGenericInputController.getImagedPlatePanel().initPanel(selectedExperiment.getPlateFormat(), parentDimension);
        loadExperimentFromGenericInputController.getImagedPlatePanel().repaint();
        // show Conditions JList
        showConditionsList();
    }

    /**
     * Action on selected project, find all relative in progress experiments, if any
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        if (experimentService.findExperimentsByProjectIdAndStatus(selectedProject.getProjectid(), ExperimentStatus.IN_PROGRESS) != null) {
            experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(selectedProject.getProjectid(), ExperimentStatus.IN_PROGRESS));
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, experimentOverviewPanel.getExperimentJList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
        } else {
            loadExperimentFromGenericInputController.showMessage("There are no experiments in progress for this project!", 1);
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }
}
