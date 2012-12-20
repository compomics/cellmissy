/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.experiment.load.ExperimentMetadataPanel;
import be.ugent.maf.cellmissy.gui.experiment.load.LoadExperimentPanel;
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
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;

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
    private LoadExperimentPanel loadExperimentPanel;
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
        //create main panel
        experimentMetadataPanel = new ExperimentMetadataPanel();
        loadExperimentPanel = new LoadExperimentPanel();
        //init main view
        initExperimentInfoPanel();
    }

    /**
     * 
     */
    private void initExperimentInfoPanel() {
        //hide conditions JList
        loadExperimentPanel.getjScrollPane3().setVisible(false);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadExperimentPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        //init experiment binding
        //bind Duration
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.duration"), experimentMetadataPanel.getDurationTextField(), BeanProperty.create("text"), "durationbinding");
        bindingGroup.addBinding(binding);
        //bind Interval
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.experimentInterval"), experimentMetadataPanel.getIntervalTextField(), BeanProperty.create("text"), "intervalbinding");
        bindingGroup.addBinding(binding);
        //bind Time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.timeFrames"), experimentMetadataPanel.getTimeFramesTextField(), BeanProperty.create("text"), "timeframesbinding");
        bindingGroup.addBinding(binding);

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
        loadExperimentPanel.getProjectJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                //init experimentJList
                int locationToIndex = loadExperimentPanel.getProjectJList().locationToIndex(e.getPoint());
                if (experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS) != null) {
                    experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS));
                    JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, loadExperimentPanel.getExperimentJList());
                    bindingGroup.addBinding(jListBinding);
                    bindingGroup.bind();
                } else {
                    loadExperimentFromGenericInputController.showMessage("There are no experiments in progress for this project!", 1);
                    if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                        experimentBindingList.clear();
                    }
                }
            }
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        loadExperimentPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int locationToIndex = loadExperimentPanel.getExperimentJList().locationToIndex(e.getPoint());
                //set experiment of parent controller
                loadExperimentFromGenericInputController.setExperiment(experimentBindingList.get(locationToIndex));
                plateConditionList = new ArrayList<>();
                plateConditionList.addAll(loadExperimentFromGenericInputController.getExperiment().getPlateConditionCollection());
                Dimension parentDimension = loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getLoadDataPlateParentPanel().getSize();
                PlateFormat plateFormat = loadExperimentFromGenericInputController.getExperiment().getPlateFormat();
                //init plate panel with current experiment plate format
                loadExperimentFromGenericInputController.initPlatePanel(plateFormat, parentDimension);

                loadExperimentFromGenericInputController.getCellMiaImagedPlatePanel().setExperiment(loadExperimentFromGenericInputController.getExperiment());
                loadExperimentFromGenericInputController.getCellMiaImagedPlatePanel().repaint();

                //hide label
                loadExperimentPanel.getjLabel2().setVisible(false);
                //and show Conditions JList
                showConditionsList();

                loadExperimentFromGenericInputController.updateInfoLabel(loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getInfolabel(), "Insert experiment metadata.");
            }
        });

        //add view to parent panel
        loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getLoadExperimentParentPanel().add(loadExperimentPanel, gridBagConstraints);
        loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getExpMetadataParentPanel().add(experimentMetadataPanel, gridBagConstraints);
    }

    /**
     * this method shows a list of conditions once an experiment is selected
     */
    private void showConditionsList() {
        //make the conditions List visible
        loadExperimentPanel.getjScrollPane3().setVisible(true);
        //set Cell Renderer for Condition List
        loadExperimentPanel.getConditionsList().setCellRenderer(new ConditionsLoadListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, loadExperimentPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * this method checks experiment Info
     * @return messages to show if validation was not successful 
     */
    private void setExperimentMetadata() {
        Experiment experiment = loadExperimentFromGenericInputController.getExperiment();
        List<String> messages = new ArrayList<>();
        try {
            // time frames
            experiment.setTimeFrames(Integer.parseInt(experimentMetadataPanel.getTimeFramesTextField().getText()));
            // interval
            experiment.setExperimentInterval(Double.parseDouble(experimentMetadataPanel.getIntervalTextField().getText()));
        } catch (NumberFormatException e) {
            messages.add("Please insert Numbers!");
        }
    }
}
