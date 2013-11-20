/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.exception.CellMiaFoldersException;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.setup.CopyExperimentSettingsDialog;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.ValidationUtils;
import be.ugent.maf.cellmissy.gui.experiment.setup.ExperimentInfoPanel;
import be.ugent.maf.cellmissy.gui.experiment.setup.SetupExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.setup.SetupPanel;
import be.ugent.maf.cellmissy.gui.experiment.setup.ImportTemplateDialog;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsOverviewListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.InstrumentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
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
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * SetupExperiment Panel Controller: set up a new experiment. Parent controller:
 * CellMissy Controller (main controller) Child controllers: Conditions
 * Controller, Setup Plate Controller, Setup Report Controller, Treatment
 * Controller, AssayECM Controller and ProjectController.
 *
 * @author Paola
 */
@Controller("setupExperimentController")
public class SetupExperimentController {

    private static final Logger LOG = Logger.getLogger(SetupExperimentController.class);
    //model
    private Experiment experiment;
    private Experiment experimentFromXMLFile;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<Magnification> magnificationBindingList;
    private BindingGroup bindingGroup;
    private File mainDirectory;
    private boolean setupSaved;
    //view
    private SetupExperimentPanel setupExperimentPanel;
    private ExperimentInfoPanel experimentInfoPanel;
    private SetupPanel setupPanel;
    private CopyExperimentSettingsDialog copyExperimentSettingsDialog;
    private ImportTemplateDialog importTemplateDialog;
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
    @Autowired
    private SetupProjectController setupProjectController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private InstrumentService instrumentService;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize Controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        mainDirectory = new File(PropertiesConfigurationHolder.getInstance().getString("mainDirectory"));
        experimentService.init(mainDirectory);
        setupSaved = false;
        //create panels
        experimentInfoPanel = new ExperimentInfoPanel();
        setupExperimentPanel = new SetupExperimentPanel();
        setupPanel = new SetupPanel();

        //init views
        initExperimentInfoPanel();
        initSetupExperimentPanel();
        initCopySettingsDialog();
        initImportTemplateDialog();
        //init child controllers
        setupPlateController.init();
        setupConditionsController.init();
        setupProjectController.init();
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

    public CellMissyFrame getCellMissyFrame() {
        return cellMissyController.getCellMissyFrame();
    }

    public ExperimentInfoPanel getExperimentInfoPanel() {
        return experimentInfoPanel;
    }

    /**
     *
     * If the user adds a new condition, add a new entry to the map: new
     * condition-empty list of rectangles
     *
     * @param conditionToAdd added to the list
     */
    public void onNewConditionAdded(PlateCondition conditionToAdd) {
        setupPlateController.addNewRectangleEntry(conditionToAdd);
    }

    /**
     * if the user removes a condition from the list, wells conditions are set
     * back to null, rectangles are removed from the map and repaint is called
     *
     * @param conditionToRemove
     */
    public void onConditionToRemove(PlateCondition conditionToRemove) {
        setupPlateController.onClearSelection(conditionToRemove);
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
     * show a message through the main frame
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        cellMissyController.showMessage(message, title, messageType);
    }

    public void disableAdminSection() {
        experimentInfoPanel.getNewProjectButton().setEnabled(false);
    }

    public ObservableList<PlateCondition> getPlateConditionBindingList() {
        return setupConditionsController.getPlateConditionBindingList();
    }

    /**
     * Import settings from another experiment. This action will show a JDialog,
     * with all the experiments for the current project. The user can select an
     * experiment and copy the settings from it.
     */
    public void onImportSettings() {
        // fill in the lists of experiments
        Project selectedProject = (Project) experimentInfoPanel.getProjectsList().getSelectedValue();
        List<Experiment> experimentsForCurrentProject = experimentService.findExperimentsByProjectId(selectedProject.getProjectid());
        // check that there're actually experiments!
        if (experimentsForCurrentProject != null) {
            // bind the Jlist to the experimentsList
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentsForCurrentProject, copyExperimentSettingsDialog.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
            // pack, center and show the dialog
            copyExperimentSettingsDialog.pack();
            GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), copyExperimentSettingsDialog);
            copyExperimentSettingsDialog.setVisible(true);
        } else {
            // no experiments! inform the user
            cellMissyController.showMessage("Sorry! There are no experiments here to copy settings from!", "cannot copy settings-no experiments in this project", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Export the current experiment setup template to an external XML file.
     */
    public void onExportTemplateForCurrentExperiment() {
        JFileChooser chooseDirectory = new JFileChooser();
        chooseDirectory.setDialogTitle("Choose a directory to save the template");
        chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooseDirectory.showSaveDialog(setupExperimentPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File currentDirectory = chooseDirectory.getSelectedFile();
            ExportTemplateToXMLSwingWorker exportTemplateToXMLSwingWorker = new ExportTemplateToXMLSwingWorker(currentDirectory);
            exportTemplateToXMLSwingWorker.execute();
        } else {
            showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Import setup template from external XML file to current experiment.
     */
    public void onImportTemplateToCurrentExperiment() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose an XML file for the template to import");
        // to select only xml files
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                int index = f.getName().lastIndexOf(".");
                String extension = f.getName().substring(index + 1);
                if (extension.equals("xml")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return (".xml files");
            }
        });
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = fileChooser.showOpenDialog(setupExperimentPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // parse this selected XML file through the experiment service
            // according to what we find here, we update the dialog to show (in the same method!)
            parseXMLFile(selectedFile);
        } else {
            showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Check if current set up has being saved
     *
     * @return
     */
    public boolean setupWasSaved() {
        boolean saved = true;
        String numberText = experimentInfoPanel.getNumberTextField().getText();
        String purposeText = experimentInfoPanel.getPurposeTextArea().getText();
        if (experiment == null && (!numberText.isEmpty() | !purposeText.isEmpty())) {
            saved = false;
        } else if (experiment != null && !setupSaved) {
            saved = false;
        }
        return saved;
    }

    /**
     * Called in the main controller, reset views and models if another view has
     * being shown
     */
    public void resetAfterCardSwitch() {
        // set experiment back to null
        experiment = null;
        setupSaved = false;
        // disable finish button
        setupExperimentPanel.getFinishButton().setEnabled(false);
        // reset experiment info text fields
        experimentInfoPanel.getNumberTextField().setText("");
        experimentInfoPanel.getPurposeTextArea().setText("");
        // clear selection on both project and experiment lists
        experimentInfoPanel.getProjectsList().clearSelection();
        experimentInfoPanel.getExperimentsList().clearSelection();

        // set text area to empty field
        experimentInfoPanel.getProjectDescriptionTextArea().setText("");
        // clear also experiments list, if not null
        if (experimentBindingList != null) {
            experimentBindingList.clear();
        }
        // clear plate
        setupPlateController.onClearPlate();
        setupPlateController.removeAllRectangleEntries();
        setupPlateController.setSelectionStarted(false);
        // and then empty plate condition list
        setupConditionsController.getPlateConditionBindingList().clear();
        // reset condition indexes
        setupConditionsController.resetConditionIndexes();
        // create first, default condition and add it to the list
        PlateCondition firstCondition = setupConditionsController.createFirstCondition();
        setupConditionsController.getPlateConditionBindingList().add(firstCondition);
        onNewConditionAdded(firstCondition);
        // empty treatment list
        setupConditionsController.getTreatmentBindingList().clear();
        // show again metadata (first) panel
        onPrevious();
    }

    /**
     * When the mouse is released and the rectangle has been drawn, this method
     * is called: set well collection of the current condition and set the
     * condition of the selected wells
     *
     * @param plateCondition
     * @param rectangle
     * @return true if the selection of wells is valid, else show a message
     */
    public boolean updateWellCollection(PlateCondition plateCondition, Rectangle rectangle) {
        boolean isSelectionValid = true;
        List<Well> wellList = plateCondition.getWellList();
        outerloop:
        for (WellGui wellGui : setupPlateController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                //check if the collection already contains that well
                if (!wellList.contains(wellGui.getWell())) {
                    //the selection is valid if the wells do not have a condition yet
                    if (!hasCondition(wellGui)) {
                        //in this case, add the well to the collection and set the condition of the well
                        wellList.add(wellGui.getWell());
                        wellGui.getWell().setPlateCondition(plateCondition);
                    } else {
                        //if the wells do have a condition already, the selection is not valid
                        isSelectionValid = false;
                        //in this case, show a message through the main controller
                        cellMissyController.showMessage("Some wells already have a condition\nPlease make another selection", "Wells' selection error", JOptionPane.WARNING_MESSAGE);
                        //exit from the outer loop
                        break outerloop;
                    }
                }
            }
        }
        return isSelectionValid;
    }

    /**
     * set back to null the condition of the wells selected (for a certain
     * Condition)
     *
     * @param plateCondition
     */
    public void resetWellsCondition(PlateCondition plateCondition) {
        //set plate condition of wells again to null
        for (WellGui wellGui : setupPlateController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            List<Rectangle> rectangles = setupPlateController.getSetupPlatePanel().getRectangles().get(plateCondition);
            if (rectangles != null) {
                for (Rectangle rectangle : rectangles) {
                    if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                        wellGui.getWell().setPlateCondition(null);
                    }
                }
            }
        }
    }

    /**
     * set back to null the conditions of all wells selected (for all
     * conditions)
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
            if (!projectHasExperiment(((Project) experimentInfoPanel.getProjectsList().getSelectedValue()).getProjectid(), Integer.parseInt(experimentInfoPanel.getNumberTextField().getText()))) {
                experiment.setExperimentNumber(Integer.parseInt(experimentInfoPanel.getNumberTextField().getText()));
            } else {
                String message = "Experiment number " + experimentInfoPanel.getNumberTextField().getText() + " already exists for this project";
                messages.add(message);
                experimentInfoPanel.getNumberTextField().requestFocusInWindow();
            }

        } catch (NumberFormatException e) {
            messages.add("Please insert a valid Experiment Number!");
            experimentInfoPanel.getNumberTextField().requestFocusInWindow();
        }
        if (messages.isEmpty()) {
            messages.addAll(ValidationUtils.validateObject(experiment));
        }
        return messages;
    }

    /**
     * validate PlateCondition, if PlateCondition is not valid, go back to the
     * previous one
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
            cellMissyController.showMessage(message, "Condition validation problem", JOptionPane.WARNING_MESSAGE);
            setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(setupConditionsController.getPreviousConditionIndex());
        }
        return isValid;
    }

    /**
     * add mouse listener to setup plate panel (Only when a condition is
     * selected)
     */
    public void addMouseListener() {
        setupPlateController.addMouseListener();
    }

    /*
     * private methods and classes
     */
    /**
     * Initialize the experiment info panel
     */
    private void initExperimentInfoPanel() {
        experimentInfoPanel.getProjectDescriptionTextArea().setLineWrap(true);
        experimentInfoPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);
        experimentInfoPanel.getPurposeTextArea().setLineWrap(true);
        experimentInfoPanel.getPurposeTextArea().setWrapStyleWord(true);
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        experimentInfoPanel.getInfoLabel1().setIcon(scaledIcon);
        experimentInfoPanel.getInfoLabel().setIcon(scaledIcon);
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentInfoPanel.getProjectsList());
        bindingGroup.addBinding(jListBinding);
        //init instrument combo box
        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, instrumentBindingList, experimentInfoPanel.getInstrumentComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init magnification combo box
        magnificationBindingList = ObservableCollections.observableList(instrumentService.findAllMagnifications());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, magnificationBindingList, experimentInfoPanel.getMagnificationComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        // do the binding
        bindingGroup.bind();
        //add experimentInfoPanel to parent panel
        setupExperimentPanel.getTopPanel().add(experimentInfoPanel, gridBagConstraints);

        //set cell renderer for experimentJList
        ExperimentsOverviewListRenderer experimentsOverviewListRenderer = new ExperimentsOverviewListRenderer(false);
        experimentInfoPanel.getExperimentsList().setCellRenderer(experimentsOverviewListRenderer);

        //date cannot be modified manually
        experimentInfoPanel.getDateChooser().getDateEditor().setEnabled(false);
        //get current date with Date()
        Date date = new Date();
        experimentInfoPanel.getDateChooser().setDate(date);

        // button group for radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(experimentInfoPanel.getCellMiaRadioButton());
        buttonGroup.add(experimentInfoPanel.getGenericRadioButton());
        // select by default the CELLMIA button
        experimentInfoPanel.getCellMiaRadioButton().setSelected(true);

        // document listener for the Next button
        ExperimentListener nextButtonListener = new ExperimentListener(setupExperimentPanel.getNextButton());
        nextButtonListener.registerDoc(experimentInfoPanel.getNumberTextField().getDocument());
        nextButtonListener.registerDoc(experimentInfoPanel.getPurposeTextArea().getDocument());
        nextButtonListener.registerDoc(((JTextField) experimentInfoPanel.getDateChooser().getDateEditor().getUiComponent()).getDocument());

        /**
         * add mouse listeners
         */
        //show experiments for the project selected
        experimentInfoPanel.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //init experimentJList
                    int selectedIndex = experimentInfoPanel.getProjectsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedIndex);
                        // set text for project description
                        experimentInfoPanel.getProjectDescriptionTextArea().setText(selectedProject.getProjectDescription());
                        // request focus on experiment number
                        experimentInfoPanel.getNumberTextField().requestFocusInWindow();
                        Long projectid = selectedProject.getProjectid();
                        List<Integer> experimentNumbers = experimentService.findExperimentNumbersByProjectId(projectid);
                        if (experimentNumbers != null) {
                            List<Experiment> experimentList = experimentService.findExperimentsByProjectId(projectid);
                            experimentBindingList = ObservableCollections.observableList(experimentList);
                            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, experimentInfoPanel.getExperimentsList());
                            bindingGroup.addBinding(jListBinding);
                            bindingGroup.bind();
                        } else {
                            cellMissyController.showMessage("There are no experiments yet for this project!", "No experiments found", JOptionPane.INFORMATION_MESSAGE);
                            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                                experimentBindingList.clear();
                            }
                        }
                    }
                }
            }
        });

        // create a new project
        experimentInfoPanel.getNewProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupProjectController.showNewProjectDialog();
            }
        });
    }

    /**
     * Initialize the copy settings dialog
     */
    private void initCopySettingsDialog() {
        // make a new dialog
        copyExperimentSettingsDialog = new CopyExperimentSettingsDialog(cellMissyController.getCellMissyFrame(), true);
        // justify info text
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = copyExperimentSettingsDialog.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        // customize table
        copyExperimentSettingsDialog.getConditionsDetailsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        copyExperimentSettingsDialog.getConditionsDetailsTable().getTableHeader().setReorderingAllowed(false);
        // set the cell renderer for the experiments list: the experiments are selectable
        copyExperimentSettingsDialog.getExperimentsList().setCellRenderer(new ExperimentsOverviewListRenderer(true));
        // update fields when an exp is selected
        copyExperimentSettingsDialog.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //init experimentJList
                    Experiment selectedExperiment = (Experiment) copyExperimentSettingsDialog.getExperimentsList().getSelectedValue();
                    if (selectedExperiment != null) {
                        // get the information and update the fields
                        copyExperimentSettingsDialog.getUserLabel().setText(selectedExperiment.getUser().toString());
                        copyExperimentSettingsDialog.getExpPurposeLabel().setText(selectedExperiment.getPurpose());
                        copyExperimentSettingsDialog.getExpDateLabel().setText(selectedExperiment.getExperimentDate().toString());
                        copyExperimentSettingsDialog.getInstrumentLabel().setText(selectedExperiment.getInstrument().getName());
                        copyExperimentSettingsDialog.getMagnificationLabel().setText(selectedExperiment.getMagnification().toString());
                        copyExperimentSettingsDialog.getPlateFormatLabel().setText(selectedExperiment.getPlateFormat().toString());
                        copyExperimentSettingsDialog.getNumberConditionsLabel().setText("" + selectedExperiment.getPlateConditionList().size());
                        // set the model of the conditions table
                        updateConditionsTableModel(copyExperimentSettingsDialog.getConditionsDetailsTable(), selectedExperiment);
                    }
                }
            }
        });

        // close the dialog: just empty the text fields
        copyExperimentSettingsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // reset the information fields
                copyExperimentSettingsDialog.getUserLabel().setText("");
                copyExperimentSettingsDialog.getExpPurposeLabel().setText("");
                copyExperimentSettingsDialog.getExpDateLabel().setText("");
                copyExperimentSettingsDialog.getInstrumentLabel().setText("");
                copyExperimentSettingsDialog.getMagnificationLabel().setText("");
                copyExperimentSettingsDialog.getPlateFormatLabel().setText("");
                copyExperimentSettingsDialog.getNumberConditionsLabel().setText("");
                // reset table model to a default one
                copyExperimentSettingsDialog.getConditionsDetailsTable().setModel(new DefaultTableModel());
            }
        });

        // add action listeners
        // copy the settings for current experiment: execute the swing worker
        copyExperimentSettingsDialog.getCopySettingsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Experiment experimentToCopy = (Experiment) copyExperimentSettingsDialog.getExperimentsList().getSelectedValue();
                // be sure that one experiment is selected in the list
                if (experimentToCopy != null) {
                    // get the settings from selected experiment and use them as settings for the new experiment
                    CopyExpSettingsSwingWorker copyExpSettingsSwingWorker = new CopyExpSettingsSwingWorker(experimentToCopy);
                    copyExpSettingsSwingWorker.execute();
                } else {
                    // tell the user that he needs to select an experiment!
                    JOptionPane.showMessageDialog(copyExperimentSettingsDialog, "Please select an experiment to copy settings from!", "no exp selected error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // cancel button
        copyExperimentSettingsDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // cancel: hide the dialog
                copyExperimentSettingsDialog.setVisible(false);
            }
        });
    }

    /**
     * Initialize template dialog
     */
    private void initImportTemplateDialog() {
        // make a new dialog
        importTemplateDialog = new ImportTemplateDialog(cellMissyController.getCellMissyFrame(), true);
        // customize table
        importTemplateDialog.getConditionsDetailsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        importTemplateDialog.getConditionsDetailsTable().getTableHeader().setReorderingAllowed(false);

        // close the dialog: just empty the text fields
        importTemplateDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // reset the information fields
                importTemplateDialog.getXmlFileLabel().setText("");
                importTemplateDialog.getPlateFormatLabel().setText("");
                importTemplateDialog.getNumberConditionsLabel().setText("");
                // reset table model to a default one
                importTemplateDialog.getConditionsDetailsTable().setModel(new DefaultTableModel());
            }
        });
        // add action listeners
        // copy the settings for current experiment: execute the swing worker
        importTemplateDialog.getCopySettingsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // execute the swing worker
                CopyExpFromXMLSwingWorker copyExpFromXMLSwingWorker = new CopyExpFromXMLSwingWorker();
                copyExpFromXMLSwingWorker.execute();
            }
        });

        // cancel button
        importTemplateDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reset the information fields
                importTemplateDialog.getXmlFileLabel().setText("");
                importTemplateDialog.getPlateFormatLabel().setText("");
                importTemplateDialog.getNumberConditionsLabel().setText("");
                // reset table model to a default one
                importTemplateDialog.getConditionsDetailsTable().setModel(new DefaultTableModel());
                // cancel: hide the dialog
                importTemplateDialog.setVisible(false);
            }
        });
    }

    /**
     * Initialize the experiment set up panel
     */
    private void initSetupExperimentPanel() {
        // show the next button only
        setupExperimentPanel.getNextButton().setVisible(true);
        setupExperimentPanel.getNextButton().setEnabled(false);
        setupExperimentPanel.getPreviousButton().setVisible(false);
        //hide Report and Finish buttons
        setupExperimentPanel.getFinishButton().setVisible(false);
        setupExperimentPanel.getFinishButton().setEnabled(false);
        setupExperimentPanel.getReportButton().setVisible(false);
        // disable the main frame menu items: import template and import settings
        cellMissyController.getCellMissyFrame().getImportSettingsMenuItem().setEnabled(false);
        cellMissyController.getCellMissyFrame().getImportTemplateMenuItem().setEnabled(false);

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
                    experiment.setUser(cellMissyController.getCurrentUser());
                    experiment.setProject((Project) experimentInfoPanel.getProjectsList().getSelectedValue());
                    experiment.setInstrument((Instrument) experimentInfoPanel.getInstrumentComboBox().getSelectedItem());
                    experiment.setMagnification((Magnification) experimentInfoPanel.getMagnificationComboBox().getSelectedItem());
                    experiment.setExperimentDate(experimentInfoPanel.getDateChooser().getDate());
                    experiment.setPurpose(experimentInfoPanel.getPurposeTextArea().getText());
                    // check if image analysis is going to be performed with cellmia
                    // in this case only, create folder structure
                    if (experimentInfoPanel.getCellMiaRadioButton().isSelected()) {
                        try {
                            experimentService.createFolderStructure(experiment);
                            onNext();
                        } catch (CellMiaFoldersException ex) {
                            LOG.error(ex.getMessage());
                            showMessage(ex.getMessage(), "no connection to server error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        onNext();
                    }
                }
            }
        });

        // go back to previous view
        setupExperimentPanel.getPreviousButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPrevious();
            }
        });

        //create a pdf from the plate panel (ONLY if experiment set up is OK)
        setupExperimentPanel.getReportButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                List<WellGui> wellGuiList = setupPlateController.getSetupPlatePanel().getWellGuiList();
                for (PlateCondition plateCondition : setupConditionsController.getPlateConditionBindingList()) {
                    for (Well well : plateCondition.getWellList()) {
                        for (WellGui wellGui : wellGuiList) {
                            if (well.equals(wellGui.getWell())) {
                                wellGui.getWell().setPlateCondition(plateCondition);
                            }
                        }
                    }
                }

                if (validateCondition(setupConditionsController.getCurrentCondition())) {
                    //update last condition of the experiment
                    updateLastCondition();
                }
                // validate all conditions
                if (validateAllConditions()) {
                    // validate selection on plate
                    if (setupPlateController.validateWells()) {
                        //set the experiment for each plate condition in the List
                        for (PlateCondition plateCondition : setupConditionsController.getPlateConditionBindingList()) {
                            plateCondition.setExperiment(experiment);
                        }
                        // set plate format
                        PlateFormat plateFormat = (PlateFormat) setupPanel.getPlateFormatComboBox().getSelectedItem();
                        experiment.setPlateFormat(plateFormat);
                        //set the condition's collection of the experiment
                        experiment.setPlateConditionList(setupConditionsController.getPlateConditionBindingList());
                        //create PDF report, execute SwingWorker
                        // check for cellmia or other software
                        SetupReportWorker setupReportWorker = null;
                        if (experimentInfoPanel.getCellMiaRadioButton().isSelected()) {
                            setupReportWorker = new SetupReportWorker(experiment.getSetupFolder());
                        } else if (experimentInfoPanel.getGenericRadioButton().isSelected()) {
                            // show a jfile chooser to decide where to save the file
                            JFileChooser chooseDirectory = new JFileChooser();
                            chooseDirectory.setDialogTitle("Choose a directory to save the report");
                            chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            // in response to the button click, show open dialog
                            int returnVal = chooseDirectory.showSaveDialog(setupExperimentPanel);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File currentDirectory = chooseDirectory.getSelectedFile();
                                setupReportWorker = new SetupReportWorker(currentDirectory);
                            } else {
                                showMessage("Command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        // check for the worker instance and execute it
                        if (setupReportWorker != null) {
                            setupReportWorker.execute();
                        }
                    } else {
                        showMessage("Some wells do not have a condition, please reset view.", "Wells' selection error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    showMessage("Validation problem." + "\n" + "Check your setup and try again to create the report.", "error in setup", JOptionPane.WARNING_MESSAGE);
                    setupExperimentPanel.getFinishButton().setEnabled(false);
                }
            }
        });

        //click on Finish button: save the experiment
        setupExperimentPanel.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // create and execute a new swing worker to save the experiment in progress
                SaveExperimentSwingWorker saveExperimentSwingWorker = new SaveExperimentSwingWorker();
                saveExperimentSwingWorker.execute();
            }
        });

        cellMissyController.getCellMissyFrame().getSetupExperimentParentPanel().add(setupExperimentPanel, gridBagConstraints);
    }

    /**
     * Once you select an XML file, this method is using the experiment service
     * to get the experiment back from the XML file. Then, if you click the copy
     * settings button in the correspondent dialog, you can copy its settings to
     * the current experiment.
     *
     * @param xmlFile: the file to parse, to get the template back
     */
    private void parseXMLFile(File xmlFile) {
        try {
            // with the exp service get the EXPERIMENT object from the XML file
            experimentFromXMLFile = experimentService.getExperimentFromXMLFile(xmlFile);
            // we check here for the validation errors (these are retrieved from the xml validator)
            List<String> xmlValidationErrorMesages = experimentService.getXmlValidationErrorMesages();
            // if no errors during unmarshal, continue, else, show the errors
            if (xmlValidationErrorMesages.isEmpty()) {
                // inform the user that parsing was OK
                showMessage("Set up template was successfully imported from " + xmlFile.getAbsolutePath() + " !", "template imported from XML file", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("Template imported from XML file for experiment " + experiment + "_" + experiment.getProject());
                // show the template dialog according to this XML file + experiment obtained from the file
                showTemplateDialog(xmlFile, experimentFromXMLFile);
            } else {
                // validation of the XML file was not successful: collect the validation messages and inform the user
                String mainMessage = "Error in validating " + xmlFile.getAbsolutePath() + "\n";
                String totalMessage = "";
                for (String string : xmlValidationErrorMesages) {
                    totalMessage += mainMessage.concat(string + "\n");
                }
                showMessage(totalMessage, "invalid xml file", JOptionPane.ERROR_MESSAGE);
            }
            // this error is related to the xsd schema: normally this is OK, but we need to catch this
        } catch (SAXException ex) {
            LOG.error(ex.getMessage(), ex);
            String message = "Error occurred during parsing the xsd schema for CellMissy!";
            showMessage(message, "xsd schema error", JOptionPane.ERROR_MESSAGE);
        } catch (JAXBException ex) {
            // we still need to catch exceptions in parsing the XML file
            LOG.error(ex.getMessage(), ex);
            // check for exception's instance here
            if (ex instanceof UnmarshalException) {
                if (ex.getCause() != null && ex.getCause() instanceof SAXParseException) {
                    // a SAXParseException encapsulates an XML parse error
                    SAXParseException sAXParseException = (SAXParseException) ex.getCause();
                    // get the  line number of the end of the text where the exception occurred
                    int lineNumber = sAXParseException.getLineNumber();
                    // shiow a detailed exception error !
                    String errorMessage = "An error occurred while importing template from " + xmlFile + "\n" + sAXParseException.getLocalizedMessage() + "\nCheck line number " + lineNumber + " in the XML file.";
                    showMessage(errorMessage, "not valid XML file", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException ex) {
            // this IO exception comes from using the resource for the xsd schema !
            LOG.error(ex.getMessage(), ex);
            showMessage("CellMissy did not find a valid xsd schema for the validation of the XML file.", "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update the GUI after having copied settings from an experiment.
     */
    private void updateGUIOnCopying() {
        SetupPlatePanel setupPlatePanel = setupPlateController.getSetupPlatePanel();
        // set the right plate format
        PlateFormat plateFormat = experiment.getPlateFormat();
        setupPanel.getPlateFormatComboBox().setSelectedItem(plateFormat);
        // clear and fill the conditions list
        List<PlateCondition> plateConditionList = experiment.getPlateConditionList();
        setupConditionsController.getPlateConditionBindingList().clear();
        setupConditionsController.getPlateConditionBindingList().addAll(plateConditionList);
        //increment the condition index
        Integer conditionIndex = setupConditionsController.getConditionIndex();
        conditionIndex += setupConditionsController.getPlateConditionBindingList().size() - 1;
        setupConditionsController.setConditionIndex(conditionIndex);
        setupConditionsController.getConditionsPanel().getConditionsJList().setSelectedIndex(0);
        // update GUI fields for ECM and treatment
        setupConditionsController.updateFields((PlateCondition) setupConditionsController.getConditionsPanel().getConditionsJList().getSelectedValue());
        // repaint the layout !!
        // get the map with the rectangles and clear it
        Map<PlateCondition, List<Rectangle>> rectanglesMap = setupPlatePanel.getRectangles();
        rectanglesMap.clear();
        // get the list with all the wellGuis
        List<WellGui> wellGuiList = setupPlatePanel.getWellGuiList();
        for (PlateCondition plateCondition : plateConditionList) {
            rectanglesMap.put(plateCondition, new ArrayList<Rectangle>());
            List<Rectangle> rectangles = rectanglesMap.get(plateCondition);
            for (Well well : plateCondition.getWellList()) {
                for (WellGui wellGui : wellGuiList) {
                    if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                        int x = (int) wellGui.getEllipsi().get(0).getX() - 8 / 4;
                        int y = (int) wellGui.getEllipsi().get(0).getY() - 8 / 4;

                        int width = (int) wellGui.getEllipsi().get(0).getWidth() + 8 / 2;
                        int height = (int) wellGui.getEllipsi().get(0).getHeight() + 8 / 2;
                        //create rectangle that sorrounds the wellGui add it to the map
                        Rectangle rect = new Rectangle(x, y, width, height);
                        wellGui.setRectangle(rect);
                        rectangles.add(rect);
                    }
                }
            }
        }
        setupPlatePanel.repaint();
    }

    /**
     * For a certain table, this method creates a model from the given
     * experiment with the conditions details and assign the model to the table.
     *
     *
     * @param table
     * @param exp
     */
    private void updateConditionsTableModel(JTable table, Experiment exp) {
        List<PlateCondition> plateConditionList = exp.getPlateConditionList();
        String[] columnNames = new String[7];
        columnNames[0] = "Condition";
        columnNames[1] = "Cell Line";
        columnNames[2] = "MD";
        columnNames[3] = "Assay";
        columnNames[4] = "ECM";
        columnNames[5] = "Treatments";
        columnNames[6] = "Assay(Medium, %Serum)";

        Object[][] data = new Object[plateConditionList.size()][columnNames.length];
        for (int i = 0; i < data.length; i++) {
            data[i][0] = "Cond " + (i + 1);
            data[i][1] = plateConditionList.get(i).getCellLine().toString();
            data[i][2] = plateConditionList.get(i).getAssay().getMatrixDimension().getDimension();
            data[i][3] = plateConditionList.get(i).getAssay().getAssayType();
            data[i][4] = plateConditionList.get(i).getEcm().toString();
            data[i][5] = plateConditionList.get(i).getTreatmentList().toString();
            data[i][6] = plateConditionList.get(i).getAssayMedium().toString();
        }
        // create a new table model
        NonEditableTableModel nonEditableTableModel = new NonEditableTableModel();
        nonEditableTableModel.setDataVector(data, columnNames);
        table.setModel(nonEditableTableModel);
        for (int i = 0; i < nonEditableTableModel.getColumnCount(); i++) {
            GuiUtils.packColumn(table, i, 1);
        }
    }

    /**
     * If an experiment has been copied from an XML file, it might be that new
     * objects need to be persisted to DB. This is done before the experiment is
     * saved to DB.
     */
    private void persistNewObjects() {
        // plate format
        PlateFormat foundFormat = setupPlateController.findByFormat(experiment);
        if (foundFormat == null) {
            setupPlateController.savePlateFormat(experiment.getPlateFormat());
        }
        // cell line types
        List<CellLineType> foundCellLines = setupConditionsController.findNewCellLines(experiment);
        if (!foundCellLines.isEmpty()) {
            for (CellLineType cellLineType : foundCellLines) {
                setupConditionsController.saveCellLineType(cellLineType);
            }
        }
        // assays
        List<Assay> foundAssays = setupConditionsController.findNewAssays(experiment);
        if (!foundAssays.isEmpty()) {
            for (Assay assay : foundAssays) {
                setupConditionsController.saveAssay(assay);
            }
        }
        // bottom matrix
        List<BottomMatrix> foundBottomMatrixs = setupConditionsController.findNewBottomMatrices(experiment);
        if (!foundBottomMatrixs.isEmpty()) {
            for (BottomMatrix bottomMatrix : foundBottomMatrixs) {
                setupConditionsController.saveBottomMatrix(bottomMatrix);
            }
        }
        // ecm composition
        List<EcmComposition> foundCompositions = setupConditionsController.findNewEcmCompositions(experiment);
        if (!foundCompositions.isEmpty()) {
            for (EcmComposition ecmComposition : foundCompositions) {
                setupConditionsController.saveEcmComposition(ecmComposition);
            }
        }
        // ecm densities
        List<EcmDensity> foundDensitys = setupConditionsController.findNewEcmDensities(experiment);
        if (!foundDensitys.isEmpty()) {
            for (EcmDensity ecmDensity : foundDensitys) {
                setupConditionsController.saveEcmDensity(ecmDensity);
            }
        }
        // treatment types
        List<TreatmentType> foundTreatmentTypes = setupConditionsController.findNewTreatmentTypes(experiment);
        if (!foundTreatmentTypes.isEmpty()) {
            for (TreatmentType treatmentType : foundTreatmentTypes) {
                setupConditionsController.saveTreatmentType(treatmentType);
            }
        }
    }

    /**
     * Swing Worker to save the Experiment
     */
    private class SaveExperimentSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // finish disable button
            setupExperimentPanel.getFinishButton().setEnabled(false);
            // show waiting cursor
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //save the new experiment to the DB
            // first we need to check if other objects need to be stored, then we actually save the experiment
            persistNewObjects();
            experimentService.save(experiment);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                setupSaved = true;
                //show back default cursor
                cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                showMessage("Experiment was successfully saved to DB.\nPlease choose what you want to do next.", "Experiment saved", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("Experiment " + experiment + "_" + experiment.getProject() + " saved; experiment is " + experiment.getExperimentStatus());
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Swing worker to export the setup template to an XML file
     */
    private class ExportTemplateToXMLSwingWorker extends SwingWorker<Void, Void> {

        private File directory;

        // constructor
        public ExportTemplateToXMLSwingWorker(File directory) {
            this.directory = directory;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting cursor
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            exportExperimentToXMLFile(directory);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                //show back default cursor
                cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                showMessage("Experiment Template was exported to XML file!", "template exported to XML", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("Template for experiment " + experiment + "_" + experiment.getProject() + " exported to XML file");
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * This Swing Worker copies the settings from a selected experiment to the
     * new experiment that is being setting up.
     */
    private class CopyExpSettingsSwingWorker extends SwingWorker<Void, Void> {

        private Experiment experimentToCopy;

        public CopyExpSettingsSwingWorker(Experiment experimentToCopy) {
            this.experimentToCopy = experimentToCopy;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // we need to copy everything to new experiment
            // we should update here the GUI
            experimentService.copySetupSettingsFromOtherExperiment(experimentToCopy, experiment);
            updateGUIOnCopying();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                JOptionPane.showMessageDialog(copyExperimentSettingsDialog, "Settings have been copied to new experiment.\nYou will now see the layout.", "settings copied", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("Settings copied from experiment " + experimentToCopy + "_" + experimentToCopy.getProject() + " to" + experiment + "_" + experiment.getProject());
                copyExperimentSettingsDialog.setVisible(false);
                setupPlateController.setSelectionStarted(true);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * This Swing Worker copies experiment from an XML.
     */
    private class CopyExpFromXMLSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // we need to copy everything to new experiment
            // we should update here the GUI
            experimentService.copySetupSettingsFromXMLExperiment(experimentFromXMLFile, experiment);
            // if new objects need to be created, they need to be added now to the GUI components
            addNewObjectsToGui();
            updateGUIOnCopying();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                JOptionPane.showMessageDialog(importTemplateDialog, "Template settings have been copied to new experiment.\nYou will now see the layout.", "settings copied", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("Template settings copied to Experiment: " + experiment + "_" + experiment.getProject());
                importTemplateDialog.setVisible(false);
                setupPlateController.setSelectionStarted(true);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * When setting up an experiment from an XML file it might very well be that
     * new objects need to be added to the GUI components before saving the
     * experiment. We do it here through the child controllers.
     */
    private void addNewObjectsToGui() {
        setupPlateController.addNewPlateFormat(experimentFromXMLFile);
        List<CellLineType> newCellLines = setupConditionsController.findNewCellLines(experimentFromXMLFile);
        setupConditionsController.addNewCellLines(newCellLines);
        setupConditionsController.addNewSera(experimentFromXMLFile);
        setupConditionsController.addNewMedia(experimentFromXMLFile);
        setupConditionsController.addNewDrugSolvents(experimentFromXMLFile);
        List<Assay> newAssays = setupConditionsController.findNewAssays(experimentFromXMLFile);
        setupConditionsController.addNewAssays(newAssays);
        List<BottomMatrix> newBottomMatrices = setupConditionsController.findNewBottomMatrices(experimentFromXMLFile);
        setupConditionsController.addNewBottomMatrices(newBottomMatrices);
        List<EcmComposition> newEcmCompositions = setupConditionsController.findNewEcmCompositions(experimentFromXMLFile);
        setupConditionsController.addNewEcmCompositions(newEcmCompositions);
        List<EcmDensity> newEcmDensities = setupConditionsController.findNewEcmDensities(experimentFromXMLFile);
        setupConditionsController.addNewEcmDensities(newEcmDensities);
        List<TreatmentType> newTreatmentTypes = setupConditionsController.findNewTreatmentTypes(experimentFromXMLFile);
        setupConditionsController.addNewTreatmentTypes(newTreatmentTypes);
    }

    /**
     * Update the template dialog with the info that come from the XML file and
     * the experiment object that we got from this file. We also pack and show
     * the dialog here.
     *
     * @param xmlFile: we need this to just show the name
     * @param experimentFromXMLFile: we need this to show the conditions and see
     * if some other parameters need to be persisted before the experiment can
     * be saved
     */
    private void showTemplateDialog(File xmlFile, Experiment experimentFromXMLFile) {
        // update info and table in the dialog
        importTemplateDialog.getXmlFileLabel().setText(" " + xmlFile.getAbsolutePath());
        importTemplateDialog.getPlateFormatLabel().setText(" " + experimentFromXMLFile.getPlateFormat().toString());
        importTemplateDialog.getNumberConditionsLabel().setText(" " + experimentFromXMLFile.getPlateConditionList().size());
        updateConditionsTableModel(importTemplateDialog.getConditionsDetailsTable(), experimentFromXMLFile);
        // we need, at last, to update the new parameters fields
        // if a new parameter needs to be inserted to DB, we use its name for the label
        // otherwise, we set the label to "no new parameters to add"
        // PLATE FORMAT
        PlateFormat newPlateFormat = setupPlateController.findByFormat(experimentFromXMLFile);
        if (newPlateFormat == null) {
            importTemplateDialog.getNewPlateFormatLabel().setText(" " + experimentFromXMLFile.getPlateFormat());
            importTemplateDialog.getNewPlateFormatLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importTemplateDialog.getNewPlateFormatLabel().setText(" no new parameters to add");
            importTemplateDialog.getNewPlateFormatLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // CELL LINES
        List<CellLineType> newCellLines = setupConditionsController.findNewCellLines(experimentFromXMLFile);
        if (!newCellLines.isEmpty()) {
            importTemplateDialog.getNewCellLineLabel().setText(" " + newCellLines);
            importTemplateDialog.getNewCellLineLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importTemplateDialog.getNewCellLineLabel().setText(" no new parameters to add");
            importTemplateDialog.getNewCellLineLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // ASSAYS
        List<Assay> newAssays = setupConditionsController.findNewAssays(experimentFromXMLFile);
        if (!newAssays.isEmpty()) {
            importTemplateDialog.getNewAssayLabel().setText(" " + newAssays);
            importTemplateDialog.getNewAssayLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importTemplateDialog.getNewAssayLabel().setText(" no new parameters to add");
            importTemplateDialog.getNewAssayLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // BOTTOM MATRICES
        List<BottomMatrix> newBottomMatrices = setupConditionsController.findNewBottomMatrices(experimentFromXMLFile);
        if (!newBottomMatrices.isEmpty()) {
            importTemplateDialog.getNewBottomMatrixLabel().setText(" " + newBottomMatrices);
            importTemplateDialog.getNewBottomMatrixLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importTemplateDialog.getNewBottomMatrixLabel().setText(" no new parameters to add");
            importTemplateDialog.getNewBottomMatrixLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // ECM COMPOSITIONS
        List<EcmComposition> newEcmCompositions = setupConditionsController.findNewEcmCompositions(experimentFromXMLFile);
        if (!newEcmCompositions.isEmpty()) {
            importTemplateDialog.getNewEcmCompositionLabel().setText(" " + newEcmCompositions);
            importTemplateDialog.getNewEcmCompositionLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importTemplateDialog.getNewEcmCompositionLabel().setText(" no new parameters to add");
            importTemplateDialog.getNewEcmCompositionLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // ECM DENSITIES
        List<EcmDensity> newEcmDensities = setupConditionsController.findNewEcmDensities(experimentFromXMLFile);
        if (!newEcmDensities.isEmpty()) {
            importTemplateDialog.getNewEcmDensityLabel().setText(" " + newEcmDensities);
            importTemplateDialog.getNewEcmDensityLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importTemplateDialog.getNewEcmDensityLabel().setText(" no new parameters to add");
            importTemplateDialog.getNewEcmDensityLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // TREATMENTS TYPES
        List<TreatmentType> newTreatmentTypes = setupConditionsController.findNewTreatmentTypes(experimentFromXMLFile);
        if (!newTreatmentTypes.isEmpty()) {
            importTemplateDialog.getNewTreatmentLabel().setText(" " + newTreatmentTypes);
            importTemplateDialog.getNewTreatmentLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importTemplateDialog.getNewTreatmentLabel().setText(" no new parameters to add");
            importTemplateDialog.getNewTreatmentLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // pack, center and show the template dialog
        importTemplateDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), importTemplateDialog);
        importTemplateDialog.setVisible(true);
    }

    /**
     * Create a new Project
     *
     * @param projectNumber
     * @param projectDescription
     */
    private void createNewProject(int projectNumber, String projectDescription) {
        Project savedProject = projectService.setupProject(projectNumber, projectDescription, mainDirectory);
        projectBindingList.add(savedProject);
    }

    /**
     * Given a certain directory chosen by the user, this method is exporting
     * the experiment to an XML file that is saved in the directory. The XML
     * file has as title information that comes from the experiment itself.
     *
     * @param directory
     */
    private void exportExperimentToXMLFile(File directory) {
        // we create the unique XML file using the experiment info
        String fileName = "setup_template_" + experiment + "_" + experiment.getProject() + ".xml";
        File xmlFile = new File(directory, fileName);
        try {
            boolean success;
            success = xmlFile.createNewFile();
            if (!success) {
                Object[] options = {"Yes", "No", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(null, "File already exists in this directory. Do you want to replace it?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                // if YES, user wants to delete existing file and replace it
                if (showOptionDialog == 0) {
                    boolean delete = xmlFile.delete();
                    if (delete) {
                        showMessage("XML file was replaced!", "file replaced", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        return;
                    }
                    // if NO or CANCEL, returns already existing file
                } else if (showOptionDialog == 1 || showOptionDialog == 2) {
                    return;
                }
            }
        } catch (IOException ex) {
            showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            experimentService.exportExperimentToXMLFile(experiment, xmlFile);
        } catch (JAXBException | FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
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
     * Call a validation method on all conditions
     *
     * @return
     */
    private boolean validateAllConditions() {
        boolean valid = true;
        for (PlateCondition condition : setupConditionsController.getPlateConditionBindingList()) {
            if (!validateCondition(condition)) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    /**
     * On next action
     */
    private void onNext() {
        //show the setupPanel and hide the experimentInfoPanel
        GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), setupPanel, experimentInfoPanel);
        cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Add conditions and select wells for each condition. Conditions details can be chosen in the right panel.");
        // show the Previous Button
        setupExperimentPanel.getPreviousButton().setVisible(true);
        // hide the next and show the finish
        setupExperimentPanel.getNextButton().setVisible(false);
        setupExperimentPanel.getFinishButton().setVisible(true);
        setupExperimentPanel.getFinishButton().setEnabled(setupExperimentPanel.getFinishButton().isEnabled());
        // enable the main frame menu items: import template and import settings
        cellMissyController.getCellMissyFrame().getImportSettingsMenuItem().setEnabled(true);
        cellMissyController.getCellMissyFrame().getImportTemplateMenuItem().setEnabled(true);
        // the same for the PDF report button
        setupExperimentPanel.getReportButton().setVisible(true);
        setupExperimentPanel.getTopPanel().revalidate();
        setupExperimentPanel.getTopPanel().repaint();
        // update labels with experiment metadata
        setupPanel.getProjNumberLabel().setText(experiment.getProject().toString());
        setupPanel.getExpNumberLabel().setText(experiment.toString());
        setupPanel.getExpPurposeLabel().setText(experiment.getPurpose());
        setupPlateController.getSetupPlatePanel().repaint();
    }

    /**
     * On previous action
     */
    private void onPrevious() {
        GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), experimentInfoPanel, setupPanel);
        cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and provide microscope/experiment data");
        setupExperimentPanel.getPreviousButton().setVisible(false);
        setupExperimentPanel.getNextButton().setVisible(true);
        setupExperimentPanel.getFinishButton().setVisible(false);
        setupExperimentPanel.getReportButton().setVisible(false);
        // disable the main frame menu items: import template and import settings
        cellMissyController.getCellMissyFrame().getImportSettingsMenuItem().setEnabled(false);
        cellMissyController.getCellMissyFrame().getImportTemplateMenuItem().setEnabled(false);
        setupExperimentPanel.getTopPanel().revalidate();
        setupExperimentPanel.getTopPanel().repaint();
    }

    /**
     * This class extends a document listener on "next" button
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
     * This method checks if a project already has a certain experiment
     * (checking for experiment number)
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
     * Update last condition before creating the PDf report and saving the
     * experiment
     */
    private void updateLastCondition() {
        setupConditionsController.updateCondition(setupConditionsController.getPlateConditionBindingList().size() - 1);


    }

    /**
     * SwingWorker to create PDF file (REPORT)
     */
    private class SetupReportWorker extends SwingWorker<File, Void> {
        // directory to save the setup

        private File directory;

        public SetupReportWorker(File directory) {
            this.directory = directory;
        }

        @Override
        protected File doInBackground() throws Exception {
            //disable buttons and show a waiting cursor
            setupExperimentPanel.getReportButton().setEnabled(false);
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = setupReportController.createSetupReport(directory);
            return file;
        }

        @Override
        protected void done() {
            File file = null;
            try {
                file = get();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
            try {
                //if export to PDF was successfull, open the PDF file from the desktop
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                showMessage("Cannot open the file!" + "\n" + ex.getMessage(), "error while opening file", JOptionPane.ERROR_MESSAGE);
            }
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // enable the report, finish and export template buttons
            setupExperimentPanel.getReportButton().setEnabled(true);
            setupExperimentPanel.getFinishButton().setEnabled(true);
        }
    }
}
