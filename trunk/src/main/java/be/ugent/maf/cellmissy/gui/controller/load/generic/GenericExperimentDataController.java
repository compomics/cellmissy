/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputMetadataPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsLoadListRenderer;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl.CycleTimeUnit;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
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
     * initialize controller
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
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        loadFromGenericInputMetadataPanel.getInfoLabel().setIcon(scaledIcon);
        loadFromGenericInputMetadataPanel.getInfoLabel1().setIcon(scaledIcon);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadFromGenericInputMetadataPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        //do the binding
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
        loadFromGenericInputMetadataPanel.getProjectJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected project
                int locationToIndex = loadFromGenericInputMetadataPanel.getProjectJList().locationToIndex(e.getPoint());
                Project selectedProject = projectBindingList.get(locationToIndex);
                if (loadExperimentFromGenericInputController.getExperiment() == null) {
                    // if experiment is still null, project is being selected for the first time
                    onSelectedProject(selectedProject);
                    // if experiment is not null and a different project is selected, reset redo on selected project
                } else if (loadExperimentFromGenericInputController.getExperiment() != null && !loadExperimentFromGenericInputController.getExperiment().getProject().equals(selectedProject)) {
                    resetOnANewProject();
                    onSelectedProject(selectedProject);
                }

            }
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        loadFromGenericInputMetadataPanel.getExperimentJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected experiment
                int locationToIndex = loadFromGenericInputMetadataPanel.getExperimentJList().locationToIndex(e.getPoint());
                Experiment selectedExperiment = experimentBindingList.get(locationToIndex);
                if (selectedExperiment != null && loadExperimentFromGenericInputController.getExperiment() == null) {
                    // if the experiment is still null, it is being selected for the first time
                    onSelectedExperiment(selectedExperiment);
                    // otherwise, if a different experiment has being selected, reset and recall the onselected experiment
                } else if (selectedExperiment != null && loadExperimentFromGenericInputController.getExperiment().equals(selectedExperiment)) {
                    resetOnANewExperiment();
                    onSelectedExperiment(selectedExperiment);
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
     * Reset text fields of experiment after user has selected a different experiment
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
        Dimension parentDimension = loadExperimentFromGenericInputController.getLoadFromGenericInputPlatePanel().getPlateParentPanel().getSize();
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
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, loadFromGenericInputMetadataPanel.getExperimentJList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
        } else {
            loadExperimentFromGenericInputController.showMessage("There are no experiments in progress for this project!", "No experiments found", JOptionPane.INFORMATION_MESSAGE);
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }
}
