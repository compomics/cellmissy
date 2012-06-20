/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.LoadExperimentPanel;
import be.ugent.maf.cellmissy.gui.plate.LoadDataPlatePanel;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadExperimentPanelController {

    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private BindingGroup bindingGroup;
    private Experiment experiment;
    //view
    private LoadExperimentPanel loadExperimentPanel;
    private LoadDataPlatePanel loadDataPlatePanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controllers
    private LoadDataPlatePanelController loadDataPlatePanelController;
    //services
    private ExperimentService experimentService;
    private ProjectService projectService;
    private GridBagConstraints gridBagConstraints;
    private ObsepFileParser obsepFileParser;
    private ApplicationContext context;

    /**
     * constructor
     * @param cellMissyController 
     */
    public LoadExperimentPanelController(CellMissyController cellMissyController) {

        this.cellMissyController = cellMissyController;

        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        experimentService = (ExperimentService) context.getBean("experimentService");
        projectService = (ProjectService) context.getBean("projectService");
        obsepFileParser = (ObsepFileParser) context.getBean("obsepFileParser");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        loadExperimentPanel = new LoadExperimentPanel();

        //init child controllers
        loadDataPlatePanelController = new LoadDataPlatePanelController(this);

        //left panel: experiment data: data need to be retrieved from obsep file (microscope file)
        initExperimentDataPanel();
    }

    /*
     * getters and setters
     */
    public LoadExperimentPanel getLoadExperimentPanel() {
        return loadExperimentPanel;
    }

    /*
     * private methods and classes
     */
    /**
     * initializes the loading data panel
     */
    private void initExperimentDataPanel() {

        cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Select a project and then an experiment to load CELLMIA data. Choose a file from the microscope.");

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadExperimentPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        /**
         * add mouse listeners
         */
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
                    cellMissyController.showMessage("There are no experiments in progress for this project!", 1);
                }
            }
        });

        //set selected index to 0
        //loadExperimentPanel.getProjectJList().setSelectedIndex(-1);

        loadExperimentPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int locationToIndex = loadExperimentPanel.getExperimentJList().locationToIndex(e.getPoint());
                experiment = experimentBindingList.get(locationToIndex);
                loadDataPlatePanel = new LoadDataPlatePanel();
                Dimension parentDimension = loadExperimentPanel.getLoadDataPlateParentPanel().getSize();
                loadDataPlatePanel.initPanel(experiment.getPlateFormat(), parentDimension);
                loadExperimentPanel.getLoadDataPlateParentPanel().add(loadDataPlatePanel, gridBagConstraints);
                loadExperimentPanel.repaint();
            }
        });

        /**
         * add action listeners
         */
        loadExperimentPanel.getParseObsepFileButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //choose file to parse form microscope hierarchy
                JFileChooser chooseObsepFile = new JFileChooser();
                chooseObsepFile.setFileFilter(new FileFilter() {
                    // to select only (.obsep) files

                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase().endsWith(".obsep");
                    }

                    @Override
                    public String getDescription() {
                        return ("(.obsep)");
                    }
                });

                // in response to the button click, show open dialog 
                int returnVal = chooseObsepFile.showOpenDialog(cellMissyController.cellMissyFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File obsepFile = chooseObsepFile.getSelectedFile();
                    obsepFileParser.parseObsepFile(obsepFile);
                    List<Double> experimentInfo = obsepFileParser.getExperimentInfo();
                    loadExperimentPanel.getTimeFramesTextField().setText(experimentInfo.get(0).toString());
                    loadExperimentPanel.getIntervalTextField().setText(experimentInfo.get(1).toString());
                    loadExperimentPanel.getDurationTextField().setText(experimentInfo.get(2).toString());
                } else {
                    cellMissyController.showMessage("Open command cancelled by user", 1);
                }
            }
        });
    }
}
