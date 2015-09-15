/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputMetadataPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.ConditionsLoadListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.ExperimentsOverviewListRenderer;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl.CycleTimeUnit;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * A controller to take care of experiment metadata at loading stage from a
 * generic input.
 *
 * @author Paola Masuzzo
 */
@Controller("genericExperimentDataController")
class GenericExperimentDataController {

    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    //view
    private LoadFromGenericInputMetadataPanel loadFromGenericInputMetadataPanel;
    //parent controller
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    //child controllers
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //create main panels
        loadFromGenericInputMetadataPanel = new LoadFromGenericInputMetadataPanel();
        //init main view
        initExperimentMetadataPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public LoadFromGenericInputMetadataPanel getLoadFromGenericInputMetadataPanel() {
        return loadFromGenericInputMetadataPanel;
    }

    public ObservableList<Experiment> getExperimentBindingList() {
        return experimentBindingList;
    }

    /**
     * Initialize Experiment metadata panel
     */
    private void initExperimentMetadataPanel() {
        loadFromGenericInputMetadataPanel.getProjectDescriptionTextArea().setLineWrap(true);
        loadFromGenericInputMetadataPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        loadFromGenericInputMetadataPanel.getInfoLabel().setIcon(scaledIcon);
        loadFromGenericInputMetadataPanel.getInfoLabel1().setIcon(scaledIcon);
        ExperimentsOverviewListRenderer experimentsOverviewListRenderer = new ExperimentsOverviewListRenderer(true);
        loadFromGenericInputMetadataPanel.getExperimentsList().setCellRenderer(experimentsOverviewListRenderer);

        //init projectJList
        List<Project> allProjects = projectService.findAll();
        Collections.sort(allProjects);
        projectBindingList = ObservableCollections.observableList(allProjects);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadFromGenericInputMetadataPanel.getProjectsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        // fill in combobox with units
        for (CycleTimeUnit unit : CycleTimeUnit.values()) {
            loadFromGenericInputMetadataPanel.getIntervalUnitComboBox().addItem(unit);
        }
        // show MINUTES as default
        loadFromGenericInputMetadataPanel.getIntervalUnitComboBox().setSelectedIndex(1);

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments in progress for that project
        loadFromGenericInputMetadataPanel.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    int selectedIndex = loadFromGenericInputMetadataPanel.getProjectsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedIndex);
                        if (loadExperimentFromGenericInputController.getExperiment() == null) {
                            // if experiment is still null, project is being selected for the first time
                            onSelectedProject(selectedProject);
                            // if experiment is not null and a different project is selected, reset redo on selected project
                        } else if (!loadExperimentFromGenericInputController.getExperiment().getProject().equals(selectedProject)) {
                            resetOnANewProject();
                            onSelectedProject(selectedProject);
                        }
                    }
                }
            }
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        loadFromGenericInputMetadataPanel.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected experiment
                    int selectedIndex = loadFromGenericInputMetadataPanel.getExperimentsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Experiment selectedExperiment = experimentBindingList.get(selectedIndex);
                        if (selectedExperiment != null && loadExperimentFromGenericInputController.getExperiment() == null) {
                            // if the experiment is still null, it is being selected for the first time
                            onSelectedExperiment(selectedExperiment);
                            // otherwise, if a different experiment has being selected, reset and recall the onselected experiment
                        } else if (selectedExperiment != null && !loadExperimentFromGenericInputController.getExperiment().equals(selectedExperiment)) {
                            resetOnANewExperiment();
                            onSelectedExperiment(selectedExperiment);
                        }
                    }
                }
            }
        });
    }

    /**
     * Reset after having chosen a new project
     */
    private void resetOnANewProject() {
        // resetData conditions list and plate view
        loadExperimentFromGenericInputController.setExperiment(null);
        loadExperimentFromGenericInputController.getLoadFromGenericInputPlatePanel().getConditionsList().setCellRenderer(null);
        loadExperimentFromGenericInputController.getImagedPlatePanel().setExperiment(null);
        loadExperimentFromGenericInputController.resetData();
        resetExperimentMetadataFields();
    }

    /**
     * Reset after having chosen a new experiment
     */
    private void resetOnANewExperiment() {
        loadExperimentFromGenericInputController.resetData();
        resetExperimentMetadataFields();
    }

    /**
     * Reset text fields of experiment after user has selected a different
     * experiment
     */
    private void resetExperimentMetadataFields() {
        loadFromGenericInputMetadataPanel.getTimeFramesTextField().setText("");
        loadFromGenericInputMetadataPanel.getIntervalTextField().setText("");
        loadFromGenericInputMetadataPanel.getDurationTextField().setText("");
    }

    /**
     * this method shows a list of conditions once an experiment is selected
     */
    private void showConditionsList() {
        JList conditionsList = loadExperimentFromGenericInputController.getLoadFromGenericInputPlatePanel().getConditionsList();
        //set Cell Renderer for Condition List
        conditionsList.setCellRenderer(new ConditionsLoadListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsList);
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Action on experiment selected, retrieve plate conditions and repaint
     * plate panel
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        // first inform the user if the expeirment already has some data loaded or not!
        String message = "";
        switch (selectedExperiment.getExperimentStatus()) {
            case IN_PROGRESS:
                message = "This experiment has no loaded migration data yet.\nPlease proceed loading NEW DATA.";
                break;
            case PERFORMED:
                message = "This experiment has already some loaded migration data.\nYou can now load ADDITIONAL DATA.";
                break;
        }
        loadExperimentFromGenericInputController.showMessage(message, "data loading info", JOptionPane.INFORMATION_MESSAGE);
        proceedToLoading(selectedExperiment);
    }

    /**
     * @param selectedExperiment
     */
    private void proceedToLoading(Experiment selectedExperiment) {
        //set experiment of parent controller
        loadExperimentFromGenericInputController.setExperiment(selectedExperiment);
        // init a new list with plate conditions
        plateConditionList = loadExperimentFromGenericInputController.getExperiment().getPlateConditionList();
        // repaint plate panel
        loadExperimentFromGenericInputController.getImagedPlatePanel().setExperiment(selectedExperiment);
        Dimension parentDimension = loadExperimentFromGenericInputController.getLoadFromGenericInputPlatePanel().getPlateParentPanel().getSize();
        loadExperimentFromGenericInputController.getImagedPlatePanel().initPanel(selectedExperiment.getPlateFormat(), parentDimension);
        loadExperimentFromGenericInputController.getImagedPlatePanel().repaint();
        // show Conditions JList
        showConditionsList();
    }

    /**
     * Action on selected project.
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
            experimentBindingList.clear();
        }
        // show project description
        String projectDescription = selectedProject.getProjectDescription();
        loadFromGenericInputMetadataPanel.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectId(projectid);
        if (experimentList != null) {
            Collections.sort(experimentList);
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, loadFromGenericInputMetadataPanel.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
            // check if the user has privileges on the selected project
            // if not, show a message and disable the experiments list
            if (!userHasPrivileges(selectedProject)) {
                String message = "Sorry, you don't have enough privileges for the selected project!";
                loadExperimentFromGenericInputController.showMessage(message, "no enough privileges", JOptionPane.WARNING_MESSAGE);
                loadFromGenericInputMetadataPanel.getExperimentsList().setEnabled(false);
            }
        } else {
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
            loadExperimentFromGenericInputController.showMessage("There are no experiments for this project!", "No experiments found", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Does the current user have privileges on the current project?
     *
     * @param project
     * @return true or false
     */
    private boolean userHasPrivileges(Project project) {
        boolean hasPrivileges = false;
        // get current user from main controller
        User currentUser = loadExperimentFromGenericInputController.getCurrentUser();
        // check for his/her role
        // ADMIN user: return true
        if (currentUser.getRole().equals(Role.ADMIN_USER)) {
            hasPrivileges = true;
        } else {
            // we have a STANDARD user
            // we need to check if he's involved in the selected project
            for (ProjectHasUser projectHasUser : project.getProjectHasUserList()) {
                if (projectHasUser.getUser().equals(currentUser)) {
                    hasPrivileges = true;
                    break;
                }
            }
        }
        return hasPrivileges;
    }
}
