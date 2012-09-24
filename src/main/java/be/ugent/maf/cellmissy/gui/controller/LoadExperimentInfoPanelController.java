/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.LoadExperimentInfoPanel;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
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
@Controller("loadExperimentInfoPanelController")
public class LoadExperimentInfoPanelController {

    private static final Logger LOG = Logger.getLogger(LoadExperimentPanelController.class);
    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    //view
    private LoadExperimentInfoPanel loadExperimentInfoPanel;
    //parent controller
    @Autowired
    private LoadExperimentPanelController loadExperimentPanelController;
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
        loadExperimentInfoPanel = new LoadExperimentInfoPanel();
        //init main view
        initExperimentInfoPanel();
    }

    /**
     * getters and setters
     */
    public LoadExperimentInfoPanel getLoadExperimentInfoPanel() {
        return loadExperimentInfoPanel;
    }

    private void initExperimentInfoPanel() {
        //hide conditions JList
        loadExperimentInfoPanel.getjScrollPane3().setVisible(false);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadExperimentInfoPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        //init experiment binding
        //bind Duration
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentInfoPanel.getExperimentJList(), BeanProperty.create("selectedElement.duration"), loadExperimentInfoPanel.getDurationTextField(), BeanProperty.create("text"), "durationbinding");
        bindingGroup.addBinding(binding);
        //bind Interval
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentInfoPanel.getExperimentJList(), BeanProperty.create("selectedElement.experimentInterval"), loadExperimentInfoPanel.getIntervalTextField(), BeanProperty.create("text"), "intervalbinding");
        bindingGroup.addBinding(binding);
        //bind Time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentInfoPanel.getExperimentJList(), BeanProperty.create("selectedElement.timeFrames"), loadExperimentInfoPanel.getTimeFramesTextField(), BeanProperty.create("text"), "timeframesbinding");
        bindingGroup.addBinding(binding);

        //do the binding
        bindingGroup.bind();

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments in progress for that project
        loadExperimentInfoPanel.getProjectJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                //init experimentJList
                int locationToIndex = loadExperimentInfoPanel.getProjectJList().locationToIndex(e.getPoint());
                if (experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS) != null) {
                    experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS));
                    JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, loadExperimentInfoPanel.getExperimentJList());
                    bindingGroup.addBinding(jListBinding);
                    bindingGroup.bind();
                } else {
                    loadExperimentPanelController.showMessage("There are no experiments in progress for this project!", 1);
                    if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                        experimentBindingList.clear();
                    }
                }
            }
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        loadExperimentInfoPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int locationToIndex = loadExperimentInfoPanel.getExperimentJList().locationToIndex(e.getPoint());
                //set experiment of parent controller
                loadExperimentPanelController.setExperiment(experimentBindingList.get(locationToIndex));
                plateConditionList = new ArrayList<>();
                plateConditionList.addAll(loadExperimentPanelController.getExperiment().getPlateConditionCollection());
                Dimension parentDimension = loadExperimentPanelController.getLoadExperimentPanel().getLoadDataPlateParentPanel().getSize();
                PlateFormat plateFormat = loadExperimentPanelController.getExperiment().getPlateFormat();
                //init plate panel with current experiment plate format
                loadExperimentPanelController.initPlatePanel(plateFormat, parentDimension);
            
                loadExperimentPanelController.getLoadDataPlatePanel().setExperiment(loadExperimentPanelController.getExperiment());
                loadExperimentPanelController.getLoadDataPlatePanel().repaint();

                //hide label
                loadExperimentInfoPanel.getjLabel2().setVisible(false);
                //and show Conditions JList
                showConditionsList();

                //load experiment folders
                experimentService.loadFolderStructure(loadExperimentPanelController.getExperiment());
                LOG.debug("Folders have been loaded");
                loadExperimentPanelController.updateInfoLabel(loadExperimentPanelController.getLoadExperimentPanel().getInfolabel(), "Click <<Exp Data>> to get experiment data from microscope.");
                loadExperimentPanelController.getLoadExperimentPanel().getExpDataButton().setEnabled(true);
            }
        });

        //add view to parent panel
        loadExperimentPanelController.getLoadExperimentPanel().getLoadExperimentInfoParentPanel().add(loadExperimentInfoPanel, gridBagConstraints);

    }

    /**
     * this method shows a list of conditions once an experiment is selected
     */
    private void showConditionsList() {
        //make the conditions List visible
        loadExperimentInfoPanel.getjScrollPane3().setVisible(true);
        //set Cell Renderer for Condition List
        loadExperimentInfoPanel.getConditionsList().setCellRenderer(new ConditionsRenderer());
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, loadExperimentInfoPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * renderer for the Conditions JList
     */
    private class ConditionsRenderer extends DefaultListCellRenderer {

        /*
         *constructor
         */
        public ConditionsRenderer() {
            setOpaque(true);
            setIconTextGap(10);
        }

        //Overrides method from the DefaultListCellRenderer
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, false, false);
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
            loadExperimentPanelController.getLoadDataPlatePanel().setGraphics(g2d);
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
}
