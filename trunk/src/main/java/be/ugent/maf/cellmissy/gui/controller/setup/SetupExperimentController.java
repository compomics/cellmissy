/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.ValidationUtils;
import be.ugent.maf.cellmissy.gui.experiment.setup.ExperimentInfoPanel;
import be.ugent.maf.cellmissy.gui.experiment.setup.SetupExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.setup.SetupPanel;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.project.OverviewPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsListRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.apache.log4j.Logger;
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
 * SetupExperiment Panel Controller: set up a new experiment. Parent controller: CellMissy Controller (main controller) Child controllers: Conditions Controller, Setup Plate Controller
 *
 * @author Paola
 */
@Controller("setupExperimentController")
public class SetupExperimentController {

    private static final Logger LOG = Logger.getLogger(SetupExperimentController.class);
    //model
    private Experiment experiment;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<Magnification> magnificationBindingList;
    private BindingGroup bindingGroup;
    private File mainDirectory;
    //view
    private SetupExperimentPanel setupExperimentPanel;
    private ExperimentInfoPanel experimentInfoPanel;
    private SetupPanel setupPanel;
    private OverviewPanel overviewPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private SetupConditionsController setupConditionsController;
    @Autowired
    private SetupPlateController setupPlateController;
    @Autowired
    private SetupReportController setupReportController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize Controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        mainDirectory = new File(PropertiesConfigurationHolder.getInstance().getString("mainDirectory"));
        experimentService.init(mainDirectory);

        //create panels
        experimentInfoPanel = new ExperimentInfoPanel();
        setupExperimentPanel = new SetupExperimentPanel();
        setupPanel = new SetupPanel();

        //init views
        initExperimentInfoPanel();
        initSetupExperimentPanel();

        //init child controllers
        setupPlateController.init();
        setupConditionsController.init();
    }

    /**
     * setters and getters
     *
     * @return
     */
    public SetupExperimentPanel getSetupExperimentPanel() {
        return setupExperimentPanel;
    }

    public SetupPanel getSetupPanel() {
        return setupPanel;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public ObservableList<Project> getProjectBindingList() {
        return projectBindingList;
    }

    public ObservableList<Experiment> getExperimentBindingList() {
        return experimentBindingList;
    }

    public void setExperimentBindingList(ObservableList<Experiment> experimentBindingList) {
        this.experimentBindingList = experimentBindingList;
    }

    /**
     * public methods
     */
    /**
     *
     * if the user adds a new condition, add a new entry to the map: new condition-empty list of rectangles
     *
     * @param conditionToAdd added to the list
     */
    public void onNewConditionAdded(PlateCondition conditionToAdd) {
        setupPlateController.addNewRectangleEntry(conditionToAdd);
    }

    /**
     * if the user removes a condition from the list, wells conditions are set back to null, rectangles are removed from the map and repaint is called
     *
     * @param conditionToRemove
     */
    public void onConditionToRemove(PlateCondition conditionToRemove) {
        //set back to null the condition of the wells selected 
        resetWellsCondition(conditionToRemove);
        //remove the rectangles from the map
        setupPlateController.removeRectangleEntry(conditionToRemove);
        //repaint
        setupPlateController.getSetupPlatePanel().repaint();
    }

    /**
     * get the current condition from the child controller
     *
     * @return the current condition
     */
    public PlateCondition getCurrentCondition() {
        return setupConditionsController.getCurrentCondition();
    }

    /**
     * get the setup plate panel from the child controller
     *
     * @return setup plate panel
     */
    public SetupPlatePanel getSetupPlatePanel() {
        return setupPlateController.getSetupPlatePanel();
    }

    /**
     * show a message through the main frame
     *
     * @param message
     * @param messageType
     */
    public void showMessage(String message, Integer messageType) {
        cellMissyController.showMessage(message, messageType);
    }

    /**
     * Create a new Project
     *
     * @param projectNumber
     * @param projectDescription
     */
    public void createNewProject(int projectNumber, String projectDescription) {
        Project savedProject = projectService.setupProject(projectNumber, projectDescription, mainDirectory);
        projectBindingList.add(savedProject);
    }

    /**
     * When the mouse is released and the rectangle has been drawn, this method is called: set well collection of the current condition and set the condition of the selected wells
     *
     * @param plateCondition
     * @param rectangle
     * @return true if the selection of wells is valid, else show a message
     */
    public boolean updateWellCollection(PlateCondition plateCondition, Rectangle rectangle) {
        boolean isSelectionValid = true;
        Collection<Well> wellCollection = plateCondition.getWellCollection();
        outerloop:
        for (WellGui wellGui : setupPlateController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                //check if the collection already contains that well
                if (!wellCollection.contains(wellGui.getWell())) {
                    //the selection is valid if the wells do not have a condition yet
                    if (!hasCondition(wellGui)) {
                        //in this case, add the well to the collection and set the condition of the well
                        wellCollection.add(wellGui.getWell());
                        wellGui.getWell().setPlateCondition(plateCondition);
                    } else {
                        //if the wells do have a condition already, the selection is not valid
                        isSelectionValid = false;
                        //in this case, show a message through the main controller
                        cellMissyController.showMessage("Some wells already have a condition\nPlease make another selection", JOptionPane.WARNING_MESSAGE);
                        //exit from the outer loop
                        break outerloop;
                    }
                }
            }
        }
        return isSelectionValid;
    }

    /**
     * set back to null the condition of the wells selected (for a certain Condition)
     *
     * @param plateCondition
     */
    public void resetWellsCondition(PlateCondition plateCondition) {
        //set plate condition of wells again to null
        for (WellGui wellGui : setupPlateController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            for (Rectangle rectangle : setupPlateController.getSetupPlatePanel().getRectangles().get(plateCondition)) {
                if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                    wellGui.getWell().setPlateCondition(null);
                }
            }
        }
    }

    /**
     * set back to null the conditions of all wells selected (for all conditions)
     */
    public void resetAllWellsCondition() {
        //set plate condition of all wells selected again to null
        for (PlateCondition plateCondition : setupConditionsController.getPlateConditionBindingList()) {
            resetWellsCondition(plateCondition);
        }
    }

    /**
     * this method checks experiment Info
     *
     * @return messages to show if validation was not successful
     */
    public List<String> validateExperimentInfo() {
        List<String> messages = new ArrayList<>();
        try {
            //if the selected project does not have already the current experiment number, set the experiment number
            if (!projectHasExperiment(((Project) overviewPanel.getProjectJList().getSelectedValue()).getProjectid(), Integer.parseInt(experimentInfoPanel.getNumberTextField().getText()))) {
                experiment.setExperimentNumber(Integer.parseInt(experimentInfoPanel.getNumberTextField().getText()));
            } else {
                String message = "Experiment number " + experimentInfoPanel.getNumberTextField().getText() + " already exists for this project";
                messages.add(message);
                experimentInfoPanel.getNumberTextField().requestFocusInWindow();
            }

        } catch (NumberFormatException e) {
            messages.add("Please insert a valid Experiment Number");
            experimentInfoPanel.getNumberTextField().requestFocusInWindow();
        }
        if (messages.isEmpty()) {
            messages.addAll(ValidationUtils.validateObject(experiment));
        }
        return messages;
    }

    /**
     * validate PlateCondition, if PlateCondition is not valid, go back to the previous one
     *
     * @param plateCondition
     * @return
     */
    public boolean validateCondition(PlateCondition plateCondition) {
        boolean isValid = false;
        if (setupConditionsController.validateCondition(plateCondition).isEmpty()) {
            isValid = true;
        } else {
            String message = "";
            for (String string : setupConditionsController.validateCondition(plateCondition)) {
                message += string + "\n";
            }
            cellMissyController.showMessage(message, JOptionPane.WARNING_MESSAGE);
            setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
        }
        return isValid;
    }

    /**
     *
     * @param projectId
     * @return
     */
    public List<Integer> findExperimentNumbersByProjectId(Long projectId) {
        return experimentService.findExperimentNumbersByProjectId(projectId);
    }

    /**
     *
     * @param projectId
     * @return
     */
    public List<Experiment> findExperimentsByProjectId(Long projectId) {
        return experimentService.findExperimentsByProjectId(projectId);
    }

    /*
     * private methods and classes
     */
    /**
     * initializes the experiment info panel
     */
    private void initExperimentInfoPanel() {
        overviewPanel = new OverviewPanel();
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        overviewPanel.getInfoLabel().setIcon(scaledIcon);
        // add overview panel
        experimentInfoPanel.getOverviewParentPanel().add(overviewPanel, gridBagConstraints);
        experimentInfoPanel.getInfoLabel().setIcon(scaledIcon);
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, overviewPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        //init instrument combo box
        instrumentBindingList = ObservableCollections.observableList(experimentService.findAllInstruments());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, instrumentBindingList, experimentInfoPanel.getInstrumentComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init magnification combo box
        magnificationBindingList = ObservableCollections.observableList(experimentService.findAllMagnifications());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, magnificationBindingList, experimentInfoPanel.getMagnificationComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        bindingGroup.bind();
        //add experimentInfoPanel to parent panel
        setupExperimentPanel.getTopPanel().add(experimentInfoPanel, gridBagConstraints);

        //select first project in the ProjectList
        overviewPanel.getProjectJList().setSelectedIndex(0);

        //set cell renderer for experimentJList
        overviewPanel.getExperimentJList().setCellRenderer(new ExperimentsListRenderer());

        //date cannot be modified manually
        experimentInfoPanel.getDateChooser().getDateEditor().setEnabled(false);

        //get current date with Date()
        Date date = new Date();
        experimentInfoPanel.getDateChooser().setDate(date);

        ExperimentListener experimentListener = new ExperimentListener(setupExperimentPanel.getNextButton());
        experimentListener.registerDoc(experimentInfoPanel.getNumberTextField().getDocument());
        experimentListener.registerDoc(experimentInfoPanel.getPurposeTextArea().getDocument());
        experimentListener.registerDoc(((JTextField) experimentInfoPanel.getDateChooser().getDateEditor().getUiComponent()).getDocument());

        /**
         * add mouse listeners
         */
        //show experiments for the project selected
        overviewPanel.getProjectJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //init experimentJList
                int locationToIndex = overviewPanel.getProjectJList().locationToIndex(e.getPoint());
                if (findExperimentNumbersByProjectId(projectBindingList.get(locationToIndex).getProjectid()) != null) {
                    experimentBindingList = ObservableCollections.observableList(findExperimentsByProjectId(projectBindingList.get(locationToIndex).getProjectid()));
                    JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, overviewPanel.getExperimentJList());
                    bindingGroup.addBinding(jListBinding);
                    bindingGroup.bind();
                } else {
                    cellMissyController.showMessage("There are no experiments yet for this project!", JOptionPane.INFORMATION_MESSAGE);
                    if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                        experimentBindingList.clear();
                    }
                }
            }
        });
    }

    /**
     * Initialize the experiment set up panel
     */
    private void initSetupExperimentPanel() {
        //disable Next and Previous buttons
        setupExperimentPanel.getNextButton().setEnabled(false);
        setupExperimentPanel.getPreviousButton().setEnabled(false);
        //hide Report and Finish buttons
        setupExperimentPanel.getFinishButton().setVisible(false);
        setupExperimentPanel.getFinishButton().setEnabled(false);
        setupExperimentPanel.getReportButton().setVisible(false);
        cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and fill in experiment/microscope metadata.");

        /**
         * add action listeners
         */
        setupExperimentPanel.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //create a new experiment (in progress)
                experiment = new Experiment();
                //check if the info was filled in properly
                if (cellMissyController.validateExperimentInfo()) {
                    experiment.setExperimentStatus(ExperimentStatus.IN_PROGRESS);
                    //set the User of the experiment
                    //@todo: need to set the user like this NOW, to be changed!
                    experiment.setUser(cellMissyController.getAUser());
                    experiment.setProject((Project) overviewPanel.getProjectJList().getSelectedValue());
                    experiment.setInstrument((Instrument) experimentInfoPanel.getInstrumentComboBox().getSelectedItem());
                    experiment.setMagnification((Magnification) experimentInfoPanel.getMagnificationComboBox().getSelectedItem());
                    experiment.setExperimentDate(experimentInfoPanel.getDateChooser().getDate());
                    experiment.setPurpose(experimentInfoPanel.getPurposeTextArea().getText());
                    //create experiment's folder structure on the server
                    //@todo: this method is not needed for generic input loading
                    experimentService.createFolderStructure(experiment);
                    //show the setupPanel and hide the experimentInfoPanel
                    GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), setupPanel, experimentInfoPanel);
                    cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Add conditions and select wells for each condition. Conditions details can be chosen in the right panel.");
                    //enable the Previous Button
                    setupExperimentPanel.getPreviousButton().setEnabled(true);
                    setupExperimentPanel.getNextButton().setEnabled(false);
                    setupExperimentPanel.getFinishButton().setVisible(true);
                    if (setupExperimentPanel.getFinishButton().isEnabled()) {
                        setupExperimentPanel.getFinishButton().setEnabled(true);
                    } else {
                        setupExperimentPanel.getFinishButton().setEnabled(false);
                    }
                    setupExperimentPanel.getReportButton().setVisible(true);
                    setupExperimentPanel.getTopPanel().revalidate();
                    setupExperimentPanel.getTopPanel().repaint();

                    // update labels with experiment metadata
                    setupPanel.getProjNumberLabel().setText(experiment.getProject().toString());
                    setupPanel.getExpNumberLabel().setText(experiment.toString());
                    setupPanel.getExpPurposeLabel().setText(experiment.getPurpose());
                }
            }
        });

        // go back to previous view
        setupExperimentPanel.getPreviousButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), experimentInfoPanel, setupPanel);
                cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and provide microscope/experiment data");
                setupExperimentPanel.getPreviousButton().setEnabled(false);
                setupExperimentPanel.getNextButton().setEnabled(true);
                setupExperimentPanel.getFinishButton().setVisible(false);
                setupExperimentPanel.getReportButton().setVisible(false);
                setupExperimentPanel.getTopPanel().revalidate();
                setupExperimentPanel.getTopPanel().repaint();
            }
        });

        //create a pdf from the plate panel (ONLY if experiment set up is OK)
        setupExperimentPanel.getReportButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateCondition(setupConditionsController.getCurrentCondition())) {
                    //update last condition of the experiment
                    updateLastCondition();
                }
                if (setupPlateController.validateWells()) {
                    //set the experiment for each plate condition in the List
                    for (PlateCondition plateCondition : setupConditionsController.getPlateConditionBindingList()) {
                        plateCondition.setExperiment(experiment);
                    }
                    //set experiment plate format
                    experiment.setPlateFormat((PlateFormat) setupPlateController.getPlatePanelGui().getPlateFormatComboBox().getSelectedItem());
                    //set the condition's collection of the experiment
                    experiment.setPlateConditionCollection(setupConditionsController.getPlateConditionBindingList());
                    //create PDF report, execute SwingWorker
                    SetupReportWorker setupReportWorker = new SetupReportWorker();
                    setupReportWorker.execute();
                } else {
                    showMessage("Some wells do not have a condition, please reset view.", JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        //click on Finish button: update the experiment
        setupExperimentPanel.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //save the new experiment to the DB
                experimentService.save(experiment);
                //disable button
                setupExperimentPanel.getFinishButton().setEnabled(false);
                showMessage("Experiment was successfully saved to DB.", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * this method checks if a well already has a condition
     *
     * @param wellGui
     * @return true if a well already has a condition assigned
     */
    private boolean hasCondition(WellGui wellGui) {
        boolean hasCondition = false;
        Ellipse2D ellipse = wellGui.getEllipsi().get(0);
        for (List<Rectangle> list : setupPlateController.getSetupPlatePanel().getRectangles().values()) {
            for (Rectangle rectangle : list) {
                if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight()) && wellGui.getWell().getPlateCondition() != null) {
                    hasCondition = true;
                }
            }
        }
        return hasCondition;
    }

    /**
     * add mouse listener to setup plate panel (Only when a condition is selected)
     */
    public void addMouseListener() {
        setupPlateController.addMouseListener();
    }

    /**
     * this class extends a document listener on "next" button
     */
    private static class ExperimentListener implements DocumentListener {

        private List<Document> documentList = new ArrayList<>();
        private JButton button;

        public ExperimentListener(JButton button) {
            this.button = button;
        }

        public void registerDoc(Document document) {
            documentList.add(document);
            document.addDocumentListener(this);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update();
        }

        //for each document check the lenght, when it's not zero enable the button
        private void update() {
            for (Document document : documentList) {
                if (document.getLength() == 0) {
                    button.setEnabled(false);
                    return;
                }
            }
            button.setEnabled(true);
        }
    }

    /**
     * this method checks if a project already has a certain experiment (checking for experiment number)
     *
     * @param projectId
     * @param experimentNumber
     * @return
     */
    private boolean projectHasExperiment(Long projectId, Integer experimentNumber) {
        boolean hasExperiment = false;
        if (experimentService.findExperimentNumbersByProjectId(projectId) != null) {
            for (Integer number : experimentService.findExperimentNumbersByProjectId(projectId)) {
                if (number == experimentNumber) {
                    hasExperiment = true;
                }
            }
        }
        return hasExperiment;
    }

    /**
     * update last condition before creating the PDf report and saving the experiment
     */
    private void updateLastCondition() {
        setupConditionsController.updateCondition(setupConditionsController.getPlateConditionBindingList().size() - 1);
    }

    /**
     * SwingWorker to create PDF file (REPORT)
     */
    private class SetupReportWorker extends SwingWorker<File, Void> {

        @Override
        protected File doInBackground() throws Exception {
            //disable the pdf report button and show a waiting cursor
            setupExperimentPanel.getReportButton().setEnabled(false);
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = setupReportController.createSetupReport(experiment.getSetupFolder());
            return file;
        }

        @Override
        protected void done() {
            File file = null;
            try {
                file = get();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            try {
                //if export to PDF was successfull, open the PDF file from the desktop
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            setupExperimentPanel.getFinishButton().setEnabled(true);
            setupExperimentPanel.getReportButton().setEnabled(true);
        }
    }
}
