/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.LoadExperimentPanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
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

    private static final Logger LOG = Logger.getLogger(LoadExperimentPanelController.class);
    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    private Experiment experiment;
    //view
    private LoadExperimentPanel loadExperimentPanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controllers
    private LoadDataPlatePanelController loadDataPlatePanelController;
    //services
    private ExperimentService experimentService;
    private ProjectService projectService;
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

        //init views
        loadExperimentPanel = new LoadExperimentPanel();

        //init child controllers
        loadDataPlatePanelController = new LoadDataPlatePanelController(this);

        //left panel: experiment data: data need to be retrieved from obsep file (microscope file)
        initExperimentPanel();
    }

    /*
     * getters and setters
     */
    public LoadExperimentPanel getLoadExperimentPanel() {
        return loadExperimentPanel;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void updateInfoLabel(JLabel label, String message) {
        cellMissyController.updateInfoLabel(label, message);
    }

    public void setCursor(Cursor cursor) {
        cellMissyController.setCursor(cursor);
    }

    public void showMessage(String message, Integer messageType) {
        cellMissyController.showMessage(message, messageType);
    }

    public int showConfirmDialog(String message, String title, Integer optionType) {
        return JOptionPane.showConfirmDialog(cellMissyController.cellMissyFrame, message, title, optionType);
    }

    /*
     * private methods and classes
     */
    /**
     * initializes the loading data panel
     */
    private void initExperimentPanel() {

        //disable buttons
        loadExperimentPanel.getFinishButton().setEnabled(false);
        loadExperimentPanel.getExpDataButton().setEnabled(false);
        loadExperimentPanel.getForwardButton().setEnabled(false);
        loadExperimentPanel.getCancelButton().setEnabled(false);
        //hide progress bar
        loadExperimentPanel.getjProgressBar1().setVisible(false);
        //hide conditions JList
        loadExperimentPanel.getjScrollPane3().setVisible(false);

        //update info message
        cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Select a project and then an experiment in progress to load CELLMIA data.");

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadExperimentPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        //init experiment binding
        //bind Duration
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.duration"), loadExperimentPanel.getDurationTextField(), BeanProperty.create("text"), "durationbinding");
        bindingGroup.addBinding(binding);
        //bind Interval
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.experimentInterval"), loadExperimentPanel.getIntervalTextField(), BeanProperty.create("text"), "intervalbinding");
        bindingGroup.addBinding(binding);
        //bind Time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, loadExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.timeFrames"), loadExperimentPanel.getTimeFramesTextField(), BeanProperty.create("text"), "timeframesbinding");
        bindingGroup.addBinding(binding);

        //do the binding
        bindingGroup.bind();


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
                    cellMissyController.showMessage("There are no experiments in progress for this project!", 1);
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
                experiment = experimentBindingList.get(locationToIndex);
                plateConditionList = new ArrayList<>();
                plateConditionList.addAll(experiment.getPlateConditionCollection());
                Dimension parentDimension = loadExperimentPanel.getLoadDataPlateParentPanel().getSize();
                //init plate panel with current experiment plate format
                loadDataPlatePanelController.getLoadDataPlatePanel().initPanel(experiment.getPlateFormat(), parentDimension);

                //this call the paintComponent method and rectangles are drawn around the wells that have a condition
                loadDataPlatePanelController.getLoadDataPlatePanel().setExperiment(experiment);
                loadDataPlatePanelController.getLoadDataPlatePanel().repaint();

                //hide label
                loadExperimentPanel.getjLabel2().setVisible(false);
                //and show Conditions JList
                showConditionsList();

                //load experiment folders
                experimentService.loadFolderStructure(experiment);
                LOG.debug("Folders have been loaded");
                cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Click <<Exp Data>> to get experiment data from microscope.");
                loadExperimentPanel.getExpDataButton().setEnabled(true);
            }
        });

        /**
         * add action listeners
         */
        //parse obseo file from the microscope
        loadExperimentPanel.getExpDataButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (experiment.getObsepFile() != null) {
                    File obsepFile = experiment.getObsepFile();
                    setExperimentData(obsepFile);
                } else {
                    cellMissyController.showMessage("No valid microscope file was found. Please select a file.", 0);
                    //choose file to parse form microscope folder
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
                        setExperimentData(obsepFile);
                    } else {
                        cellMissyController.showMessage("Open command cancelled by user", 1);
                    }
                }
                cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Click <<Forward>> to process imaging data for the experiment.");
                loadExperimentPanel.getForwardButton().setEnabled(true);
                loadExperimentPanel.getExpDataButton().setEnabled(false);
            }
        });

        //cancel the selection: reset Plate View
        loadExperimentPanel.getCancelButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (WellGui wellGui : loadDataPlatePanelController.getLoadDataPlatePanel().getWellGuiList()) {

                    //empty the collection of WellHasImagingType (so color is set to default)
                    wellGui.getWell().getWellHasImagingTypeCollection().clear();

                    //remove smaller ellipsi
                    List<Ellipse2D> ellipse2DList = new ArrayList<>();
                    for (Ellipse2D ellipse2D : wellGui.getEllipsi()) {
                        if (wellGui.getEllipsi().indexOf(ellipse2D) > 0) {
                            ellipse2DList.add(ellipse2D);
                        }
                    }
                    wellGui.getEllipsi().removeAll(ellipse2DList);
                    loadDataPlatePanelController.getLoadDataPlatePanel().repaint();
                }
                //update info message (the user needs to click again on forward)
                updateInfoLabel(loadExperimentPanel.getInfolabel(), "Click again <<Forward>> to process imaging data.");
                //set boolean isFirtTime to false
                loadDataPlatePanelController.setIsFirtTime(false);
                //disable and enable buttons
                loadExperimentPanel.getFinishButton().setEnabled(false);
                loadExperimentPanel.getForwardButton().setEnabled(true);
                loadExperimentPanel.getCancelButton().setEnabled(false);
            }
        });

        //save the experiment once all data have been loaded
        loadExperimentPanel.getFinishButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //set CellMia Data
                setCellMiaData();
                //set experiment status to "performed" and save it to DB
                experiment.setExperimentStatus(ExperimentStatus.PERFORMED);
                //launch a swing worker to save the experiment in the background thread
                SaveExperimentWorker worker = new SaveExperimentWorker();
                worker.execute();
            }
        });
    }

    /**
     * set experiment data parsing the obsep file from microscope
     * @param obsepFile: this is loaded from the experiment or it is rather chosen by the user
     */
    private void setExperimentData(File obsepFile) {
        obsepFileParser.parseObsepFile(obsepFile);
        List<Double> experimentInfo = obsepFileParser.getExperimentInfo();
        loadExperimentPanel.getTimeFramesTextField().setText(experimentInfo.get(0).toString());
        loadExperimentPanel.getIntervalTextField().setText(experimentInfo.get(1).toString());
        loadExperimentPanel.getUnitLabel().setText(obsepFileParser.getUnit().name().toLowerCase());
        loadExperimentPanel.getDurationTextField().setText(experimentInfo.get(2).toString());
    }

    /**
     * this method shows a list of conditions once an experiment is selected
     */
    private void showConditionsList() {
        //make the conditions List visible
        loadExperimentPanel.getjScrollPane3().setVisible(true);
        //set Cell Renderer for Condition List
        loadExperimentPanel.getConditionsList().setCellRenderer(new ConditionsRenderer());
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, loadExperimentPanel.getConditionsList());
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
            loadDataPlatePanelController.getLoadDataPlatePanel().setGraphics(g2d);
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

    /**
     * this method sets Migration data of wells, before the experiment is saved to DB
     */
    private void setCellMiaData() {

        for (PlateCondition plateCondition : experiment.getPlateConditionCollection()) {
            for (WellGui wellGui : loadDataPlatePanelController.getLoadDataPlatePanel().getWellGuiList()) {

                //if the wellGui has a well with a NOT empty collection of wellHasImagingTypes, the well has been imaged
                //if the wellGui has a rectangle, the well belongs to a certain condition
                //only if these two conditions are true, motility data must be set and stored to DB
                if (!wellGui.getWell().getWellHasImagingTypeCollection().isEmpty() && wellGui.getRectangle() != null) {

                    for (Well well : plateCondition.getWellCollection()) {
                        //check for coordinates
                        if (well.getColumnNumber() == wellGui.getColumnNumber() && well.getRowNumber() == wellGui.getRowNumber()) {
                            //set collection of wellHasImagingType to the well of the plateCondition
                            well.setWellHasImagingTypeCollection(wellGui.getWell().getWellHasImagingTypeCollection());

                            //the other way around: set the well for each wellHasImagingType
                            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeCollection()) {
                                wellHasImagingType.setWell(well);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Swing Worker to save the Experiment
     */
    private class SaveExperimentWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            //disable the Finish button and show a waiting cursor
            loadExperimentPanel.getFinishButton().setEnabled(false);
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //show a progress bar (indeterminate)
            loadExperimentPanel.getjProgressBar1().setVisible(true);
            loadExperimentPanel.getjProgressBar1().setIndeterminate(true);

            //INSERT experiment to DB
            experimentService.save(experiment);
            return null;
        }

        @Override
        protected void done() {

            //show back default cursor and hide the progress bar
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            loadExperimentPanel.getjProgressBar1().setVisible(false);
            //update info for the user
            updateInfoLabel(loadExperimentPanel.getInfolabel(), "Experiment was successfully saved to DB.");
        }
    }
}
