/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.experiment.exporting.ExportExperimentDialog;
import be.ugent.maf.cellmissy.gui.experiment.exporting.ExportTemplateDialog;
import be.ugent.maf.cellmissy.gui.experiment.importing.ImportExperimentDialog;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsOverviewListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.CellLineService;
import be.ugent.maf.cellmissy.service.EcmService;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.TreatmentService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import org.jdesktop.beansbinding.AutoBinding;
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
 * This class is registered as a controller and takes care of
 * importing/exporting experiment and template.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("importExportController")
public class ImportExportController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ImportExportController.class);
    // model
    private Experiment experimentToExport;
    private Experiment importedExperiment;
    private Experiment experimentTemplateToExport;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<Magnification> magnificationBindingList;
    private BindingGroup bindingGroup;
    // view
    private ExportExperimentDialog exportExperimentDialog;
    private ImportExperimentDialog importExperimentDialog;
    private ExportTemplateDialog exportTemplateDialog;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    // child controllers
    // services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private WellService wellService;
    @Autowired
    private PlateService plateService;
    @Autowired
    private CellLineService cellLineService;
    @Autowired
    private AssayService assayService;
    @Autowired
    private EcmService ecmService;
    @Autowired
    private TreatmentService treatmentService;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        // init views
        initExportExperimentDialog();
        initImportExperimentDialog();
        initExportTemplateDialog();
    }

    /**
     * Method called from main controller: show the export dialog.
     */
    public void showExportExperimentDialog() {
        exportExperimentDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), exportExperimentDialog);
        exportExperimentDialog.setVisible(true);
    }

    /**
     * Method called from main controller: show the import dialog.
     */
    public void showImportExperimentDialog() {
        CardLayout cardLayout = (CardLayout) importExperimentDialog.getTopPanel().getLayout();
        cardLayout.first(importExperimentDialog.getTopPanel());
        importExperimentDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), importExperimentDialog);
        importExperimentDialog.setVisible(true);
    }

    /**
     * Method called from main controller: show the export template dialog.
     */
    public void showExportTemplateDialog() {
        exportTemplateDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), exportTemplateDialog);
        exportTemplateDialog.setVisible(true);
    }

    /**
     * Initialize Export Experiment Dialog
     */
    private void initExportExperimentDialog() {
        // make a new dialog
        exportExperimentDialog = new ExportExperimentDialog(cellMissyController.getCellMissyFrame(), true);
        exportExperimentDialog.getProjectDescriptionTextArea().setLineWrap(true);
        exportExperimentDialog.getProjectDescriptionTextArea().setWrapStyleWord(true);
        // hide progress bar and its label
        exportExperimentDialog.getProgressBarLabel().setVisible(false);
        exportExperimentDialog.getExportProgressBar().setVisible(false);
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        exportExperimentDialog.getInfoLabel().setIcon(scaledIcon);
        // init projects list
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, exportExperimentDialog.getProjectsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        // customize table
        exportExperimentDialog.getConditionsDetailsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        exportExperimentDialog.getConditionsDetailsTable().getTableHeader().setReorderingAllowed(false);
        // set the cell renderer for the experiments list: the experiments are selectable
        exportExperimentDialog.getExperimentsList().setCellRenderer(new ExperimentsOverviewListRenderer(true));
        //when a project from the list is selected, show all experiments performed for that project
        exportExperimentDialog.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    int selectedIndex = exportExperimentDialog.getProjectsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedIndex);
                        if (experimentToExport == null || !selectedProject.equals(experimentToExport.getProject()) || experimentBindingList.isEmpty()) {
                            // project is being selected for the first time
                            onSelectedProject(selectedProject);
                        }
                    }
                }
            }
        });

        // update fields when an exp is selected
        exportExperimentDialog.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //init experimentJList
                    Experiment selectedExperiment = (Experiment) exportExperimentDialog.getExperimentsList().getSelectedValue();
                    if (selectedExperiment != null) {
                        experimentToExport = selectedExperiment;
                        // get the information and update the fields
                        exportExperimentDialog.getUserLabel().setText(" " + selectedExperiment.getUser().toString());
                        exportExperimentDialog.getPurposeTextArea().setText(" " + selectedExperiment.getPurpose());
                        exportExperimentDialog.getTimeFramesLabel().setText(" " + selectedExperiment.getTimeFrames().toString());
                        exportExperimentDialog.getInstrumentLabel().setText(" " + selectedExperiment.getInstrument().getName());
                        exportExperimentDialog.getPlateFormatLabel().setText(" " + selectedExperiment.getPlateFormat().toString());
                        exportExperimentDialog.getNumberConditionsLabel().setText("" + selectedExperiment.getPlateConditionList().size());
                        // set the model of the conditions table
                        updateConditionsTableModel(exportExperimentDialog.getConditionsDetailsTable(), selectedExperiment);
                    }
                }
            }
        });

        // close the dialog: just empty the text fields
        exportExperimentDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // reset view when we close the dialog
                resetViewOnExportExperimentDialog();
            }
        });

        // add action listeners
        // copy the settings for current experiment: execute the swing worker
        exportExperimentDialog.getExportButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Experiment experimentToExport = (Experiment) exportExperimentDialog.getExperimentsList().getSelectedValue();
                // be sure that one experiment is selected in the list
                if (experimentToExport != null) {
                    // show a jfile chooser to decide where to save the file
                    JFileChooser chooseDirectory = new JFileChooser();
                    chooseDirectory.setDialogTitle("Choose a directory to save the file");
                    chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // in response to the button click, show open dialog
                    int returnVal = chooseDirectory.showSaveDialog(exportExperimentDialog);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File currentDirectory = chooseDirectory.getSelectedFile();
                        String fileName = "experiment_" + experimentToExport + "_" + experimentToExport.getProject() + ".xml";
                        File xmlFile = createXmlFile(fileName, currentDirectory, exportExperimentDialog);
                        // if the XML file was successfully created, we execute a swing worker and export the experiment to the file.
                        if (xmlFile != null) {
                            ExportExperimentSwingWorker exportExperimentSwingWorker = new ExportExperimentSwingWorker(xmlFile);
                            exportExperimentSwingWorker.execute();
                        }
                    } else {
                        JOptionPane.showMessageDialog(exportExperimentDialog, "Command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // tell the user that he needs to select an experiment!
                    JOptionPane.showMessageDialog(exportExperimentDialog, "Please select an experiment to export!", "no exp selected error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // cancel button
        exportExperimentDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // cancel: hide the dialog
                exportExperimentDialog.setVisible(false);
                // reset views
                resetViewOnExportExperimentDialog();
            }
        });
    }

    /**
     * Initialize import experiment dialog
     */
    private void initImportExperimentDialog() {
        // create a new dialog
        importExperimentDialog = new ImportExperimentDialog(cellMissyController.getCellMissyFrame(), true);
        importExperimentDialog.getPurposeTextArea().setLineWrap(true);
        importExperimentDialog.getPurposeTextArea().setWrapStyleWord(true);
        // we first disable the previous, next and the save buttons
        importExperimentDialog.getSaveExperimentButton().setEnabled(false);
        importExperimentDialog.getPreviousButton().setEnabled(false);
        importExperimentDialog.getNextButton().setEnabled(false);
        // we also hide the label and the progress bar
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        importExperimentDialog.getInfoLabel().setIcon(scaledIcon);
        // set icon for info label
        importExperimentDialog.getInfoLabel1().setIcon(scaledIcon);
        // hide progress bar and its label
        importExperimentDialog.getProgressBarLabel().setVisible(false);
        importExperimentDialog.getSaveExperimentProgressBar().setVisible(false);
        // customize table
        importExperimentDialog.getConditionsDetailsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        importExperimentDialog.getConditionsDetailsTable().getTableHeader().setReorderingAllowed(false);
        // instruments
        //init instrument combo box
        instrumentBindingList = ObservableCollections.observableList(experimentService.findAllInstruments());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, importExperimentDialog.getInstrumentComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        // magnifications
        magnificationBindingList = ObservableCollections.observableList(experimentService.findAllMagnifications());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, magnificationBindingList, importExperimentDialog.getMagnificationComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        // projects
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, importExperimentDialog.getProjectComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        // add action listeners
        // choose an xml file: the chosen XML file will be parsed and the experment object will be created
        importExperimentDialog.getChooseFileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a JFile Chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose an XML file for the import of the experiment");
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
                // in response to the button click, show open dialog
                int returnVal = fileChooser.showOpenDialog(importExperimentDialog);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    // create and execute a new swing worker with the selected file for the import
                    ImportExperimentSwingWorker importExperimentSwingWorker = new ImportExperimentSwingWorker(chosenFile);
                    importExperimentSwingWorker.execute();
                } else {
                    JOptionPane.showMessageDialog(importExperimentDialog, "Command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // close the dialog: just empty the text fields
        importExperimentDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // reset view when we close the dialog
                resetViewOnImportExperimentDialog();
            }
        });

        // cancel button
        importExperimentDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // cancel: hide the dialog
                importExperimentDialog.setVisible(false);
                // reset views
                resetViewOnImportExperimentDialog();
            }
        });

        // next/ we move to the next panel, but first we update the fields
        importExperimentDialog.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDetailsOnImportExperimentDialog();
                CardLayout cardLayout = (CardLayout) importExperimentDialog.getTopPanel().getLayout();
                // we move to next, disable the next button and enable the previous one, we enable as well the save experiment button
                cardLayout.next(importExperimentDialog.getTopPanel());
                importExperimentDialog.getNextButton().setEnabled(false);
                importExperimentDialog.getPreviousButton().setEnabled(true);
                importExperimentDialog.getSaveExperimentButton().setEnabled(true);
            }
        });

        // previous
        importExperimentDialog.getPreviousButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) importExperimentDialog.getTopPanel().getLayout();
                // we move to previous, disable the previous button and the save experiment button, and we enable the next button
                cardLayout.previous(importExperimentDialog.getTopPanel());
                importExperimentDialog.getPreviousButton().setEnabled(false);
                importExperimentDialog.getNextButton().setEnabled(true);
                importExperimentDialog.getSaveExperimentButton().setEnabled(false);
            }
        });

        // save the experiment
        importExperimentDialog.getSaveExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                experimentService.copyExperimentFromXML(importedExperiment);
                // assign user, project, instrument and magnification to the experiment
                importedExperiment.setUser(cellMissyController.getCurrentUser());
                importedExperiment.setInstrument((Instrument) importExperimentDialog.getInstrumentComboBox().getSelectedItem());
                importedExperiment.setMagnification((Magnification) importExperimentDialog.getMagnificationComboBox().getSelectedItem());
                // validate the experiment:
                if (validateImportedExperiment()) {
                    importedExperiment.setProject((Project) importExperimentDialog.getProjectComboBox().getSelectedItem());
                    for (PlateCondition plateCondition : importedExperiment.getPlateConditionList()) {
                        plateCondition.setExperiment(importedExperiment);
                    }
                    // make a new swing worker and execute it
                    SaveExperimentSwingWorker saveExperimentSwingWorker = new SaveExperimentSwingWorker();
                    saveExperimentSwingWorker.execute();
                } else {
                    String message = "Oooops! Imported experiment already exists for this project!\nYou can choose a different project....\n\n...or maybe you have already imported this experiment !!!";
                    JOptionPane.showMessageDialog(importExperimentDialog, message, "experiment is present in DB", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    /**
     * Initialize export template dialog.
     */
    private void initExportTemplateDialog() {
        // make a new dialog
        exportTemplateDialog = new ExportTemplateDialog(cellMissyController.getCellMissyFrame(), true);
        exportTemplateDialog.getProjectDescriptionTextArea().setLineWrap(true);
        exportTemplateDialog.getProjectDescriptionTextArea().setWrapStyleWord(true);
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        exportTemplateDialog.getInfoLabel().setIcon(scaledIcon);
        // projects list
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, exportTemplateDialog.getProjectsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        // customize table
        exportTemplateDialog.getConditionsDetailsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        exportTemplateDialog.getConditionsDetailsTable().getTableHeader().setReorderingAllowed(false);
        // set the cell renderer for the experiments list: the experiments are selectable
        exportTemplateDialog.getExperimentsList().setCellRenderer(new ExperimentsOverviewListRenderer(true));
        //when a project from the list is selected, show all experiments performed for that project
        exportTemplateDialog.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    int selectedIndex = exportTemplateDialog.getProjectsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedIndex);
                        if (experimentTemplateToExport == null || !selectedProject.equals(experimentTemplateToExport.getProject()) || experimentBindingList.isEmpty()) {
                            // project is being selected for the first time
                            // show project description
                            String projectDescription = selectedProject.getProjectDescription();
                            exportTemplateDialog.getProjectDescriptionTextArea().setText(projectDescription);
                            // show relative experiments
                            Long projectid = selectedProject.getProjectid();
                            List<Experiment> experimentList = experimentService.findExperimentsByProjectId(projectid);
                            if (experimentList != null) {
                                experimentBindingList = ObservableCollections.observableList(experimentList);
                                JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, exportTemplateDialog.getExperimentsList());
                                bindingGroup.addBinding(jListBinding);
                                bindingGroup.bind();
                            } else {
                                String message = "There are no experiments performed yet for this project!";
                                JOptionPane.showMessageDialog(exportTemplateDialog, message, "No experiments found", JOptionPane.INFORMATION_MESSAGE);
                                resetViewOnExportTemplateDialog();
                                if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                                    experimentBindingList.clear();
                                }
                            }
                        }
                    }
                }
            }
        });
        // update fields when an exp is selected
        exportTemplateDialog.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //init experimentJList
                    Experiment selectedExperiment = (Experiment) exportTemplateDialog.getExperimentsList().getSelectedValue();
                    if (selectedExperiment != null) {
                        experimentTemplateToExport = selectedExperiment;
                        // get the information and update the fields
                        exportTemplateDialog.getUserLabel().setText(" " + selectedExperiment.getUser().toString());
                        exportTemplateDialog.getPurposeTextArea().setText(" " + selectedExperiment.getPurpose());
                        exportTemplateDialog.getInstrumentLabel().setText(" " + selectedExperiment.getInstrument().getName());
                        exportTemplateDialog.getPlateFormatLabel().setText(" " + selectedExperiment.getPlateFormat().toString());
                        exportTemplateDialog.getNumberConditionsLabel().setText(" " + selectedExperiment.getPlateConditionList().size());
                        // set the model of the conditions table
                        updateConditionsTableModel(exportTemplateDialog.getConditionsDetailsTable(), experimentTemplateToExport);
                    }
                }
            }
        });

        // close the dialog: just empty the text fields
        exportTemplateDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // reset view when we close the dialog
                resetViewOnExportTemplateDialog();
            }
        });

        // add action listeners
        // copy the settings for current experiment: execute the swing worker
        exportTemplateDialog.getExportButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (experimentTemplateToExport != null) {
                    // show a jfile chooser to decide where to save the file
                    JFileChooser chooseDirectory = new JFileChooser();
                    chooseDirectory.setDialogTitle("Choose a directory to save the XML template file");
                    chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // in response to the button click, show open dialog
                    int returnVal = chooseDirectory.showSaveDialog(exportTemplateDialog);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File currentDirectory = chooseDirectory.getSelectedFile();
                        String fileName = "setup_template_" + experimentTemplateToExport + "_" + experimentTemplateToExport.getProject() + ".xml";
                        File xmlFile = createXmlFile(fileName, currentDirectory, exportTemplateDialog);
                        if (xmlFile != null) {
                            // if the XML file was successfully created, we execute a new swing worker
                            ExportTemplateSwingWorker exportTemplateSwingWorker = new ExportTemplateSwingWorker(xmlFile);
                            exportTemplateSwingWorker.execute();
                        }
                    } else {
                        JOptionPane.showMessageDialog(exportTemplateDialog, "Command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // tell the user that he needs to select an experiment!
                    JOptionPane.showMessageDialog(exportExperimentDialog, "Please select an experiment to export!", "no exp selected error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // cancel button
        exportTemplateDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // cancel: hide the dialog
                exportTemplateDialog.setVisible(false);
                // reset views
                resetViewOnExportTemplateDialog();
            }
        });
    }

    /**
     * Check if the imported experiment already exists for the selected project.
     *
     * @return
     */
    private boolean validateImportedExperiment() {
        boolean isValid = true;
        //if the selected project does not have already the current experiment number, set the experiment number
        if (projectHasExperiment(((Project) importExperimentDialog.getProjectComboBox().getSelectedItem()).getProjectid(), importedExperiment.getExperimentNumber())) {
            isValid = false;
        }
        return isValid;
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
     * Action on selected project, find all relative performed experiments, if
     * any
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        // show project description
        String projectDescription = selectedProject.getProjectDescription();
        exportExperimentDialog.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid, ExperimentStatus.PERFORMED);
        if (experimentList != null) {
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, exportExperimentDialog.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
        } else {
            String message = "There are no experiments performed yet for this project!";
            JOptionPane.showMessageDialog(exportExperimentDialog, message, "No experiments found", JOptionPane.INFORMATION_MESSAGE);
            resetViewOnExportExperimentDialog();
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }

    /**
     * Reset views.
     */
    private void resetViewOnExportExperimentDialog() {
        // reset the information fields
        exportExperimentDialog.getUserLabel().setText("");
        exportExperimentDialog.getPurposeTextArea().setText("");
        exportExperimentDialog.getTimeFramesLabel().setText("");
        exportExperimentDialog.getInstrumentLabel().setText("");
        exportExperimentDialog.getPlateFormatLabel().setText("");
        exportExperimentDialog.getNumberConditionsLabel().setText("");
        exportExperimentDialog.getProjectDescriptionTextArea().setText("");
        // reset table model to a default one
        exportExperimentDialog.getConditionsDetailsTable().setModel(new DefaultTableModel());
        // clear selection on both projects and experiments lists
        exportExperimentDialog.getExperimentsList().clearSelection();
        exportExperimentDialog.getProjectsList().clearSelection();
        // clear the experiments binding list
        if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
            experimentBindingList.clear();
        }
    }

    /**
     *
     */
    private void resetViewOnExportTemplateDialog() {
        // reset the information fields
        exportTemplateDialog.getUserLabel().setText("");
        exportTemplateDialog.getPurposeTextArea().setText("");
        exportTemplateDialog.getInstrumentLabel().setText("");
        exportTemplateDialog.getPlateFormatLabel().setText("");
        exportTemplateDialog.getNumberConditionsLabel().setText("");
        exportTemplateDialog.getProjectDescriptionTextArea().setText("");
        // reset table model to a default one
        exportTemplateDialog.getConditionsDetailsTable().setModel(new DefaultTableModel());
        // clear selection on both projects and experiments lists
        exportTemplateDialog.getExperimentsList().clearSelection();
        exportTemplateDialog.getProjectsList().clearSelection();
        // clear the experiments binding list
        if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
            experimentBindingList.clear();
        }
    }

    /**
     * Reset views.
     */
    private void resetViewOnImportExperimentDialog() {
        // reset the information fields
        importExperimentDialog.getFileLabel().setText("");
        importExperimentDialog.getPurposeTextArea().setText("");
        importExperimentDialog.getTimeFramesLabel().setText("");
        importExperimentDialog.getInstrumentLabel().setText("");
        importExperimentDialog.getPlateFormatLabel().setText("");
        importExperimentDialog.getNumberConditionsLabel().setText("");
        importExperimentDialog.getIntervalLabel().setText("");
        importExperimentDialog.getDurationLabel().setText("");
        importExperimentDialog.getExpNumberLabel().setText("");
        importExperimentDialog.getAlgoritmsLabel().setText("");
        importExperimentDialog.getImagingTypesLabel().setText("");
        // reset table model to a default one
        importExperimentDialog.getConditionsDetailsTable().setModel(new DefaultTableModel());
    }

    /**
     * Swing worker to export the experiment
     */
    private class ExportExperimentSwingWorker extends SwingWorker<Void, Void> {

        private File xmlFile;

        public ExportExperimentSwingWorker(File xmlFile) {
            this.xmlFile = xmlFile;
        }

        @Override
        protected Void doInBackground() throws Exception {
            exportExperimentDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // show progress bar and its label
            exportExperimentDialog.getProgressBarLabel().setVisible(true);
            exportExperimentDialog.getExportProgressBar().setVisible(true);
            //disable buttons and show a waiting cursor
            exportExperimentDialog.getExportButton().setEnabled(false);
            // fetch the migration data
            for (PlateCondition plateCondition : experimentToExport.getPlateConditionList()) {
                List<Well> wells = new ArrayList<>();
                for (Well well : plateCondition.getWellList()) {
                    Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
                    wells.add(fetchedWell);
                }
                plateCondition.setWellList(wells);
            }
            // export the experiment to file !
            exportExperimentToXMLFile(xmlFile);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // hide progress bar and its label
                exportExperimentDialog.getProgressBarLabel().setVisible(false);
                exportExperimentDialog.getExportProgressBar().setVisible(false);
                JOptionPane.showMessageDialog(exportExperimentDialog, "Experiment was successfully exported!", "experiment exported", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("experiment " + experimentToExport + "_" + experimentToExport.getProject() + " exported to file");
                resetViewOnExportExperimentDialog();
                exportExperimentDialog.setVisible(false);
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            exportExperimentDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // enable the button again
            exportExperimentDialog.getExportButton().setEnabled(true);
        }
    }

    /**
     * Swing worker to export the experiment template
     */
    private class ExportTemplateSwingWorker extends SwingWorker<Void, Void> {

        private File xmlFile;

        public ExportTemplateSwingWorker(File xmlFile) {
            this.xmlFile = xmlFile;
        }

        @Override
        protected Void doInBackground() throws Exception {
            exportTemplateDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //disable buttons and show a waiting cursor
            exportTemplateDialog.getExportButton().setEnabled(false);
            // export the experiment to file !
            exportExperimentTemplateToXMLFile(xmlFile);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                JOptionPane.showMessageDialog(exportTemplateDialog, "Experiment template was successfully exported!", "experiment template exported", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("experiment template " + experimentTemplateToExport + "_" + experimentTemplateToExport.getProject() + " exported to file");
                resetViewOnExportTemplateDialog();
                exportTemplateDialog.setVisible(false);
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            exportTemplateDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // enable the button again
            exportTemplateDialog.getExportButton().setEnabled(true);
        }
    }

    /**
     * Swing worker to import an experiment from an XML file
     */
    private class ImportExperimentSwingWorker extends SwingWorker<Void, Void> {

        private File xmlFile;

        public ImportExperimentSwingWorker(File xmlFile) {
            this.xmlFile = xmlFile;
        }

        @Override
        protected Void doInBackground() throws Exception {
            importExperimentDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // parse xmlfile
            parseXMLFile(xmlFile);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // if parsing the XML file was successfull, the experiment is not null, and we can enable the next experiment button
                if (importedExperiment != null) {
                    importExperimentDialog.getNextButton().setEnabled(true);
                }
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                JOptionPane.showMessageDialog(importExperimentDialog, "Unexpected error: " + ex.getMessage(), "unexpected error", JOptionPane.ERROR_MESSAGE);
                LOG.error(ex.getMessage(), ex);
            }
            importExperimentDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Swing Worker to save the Imported Experiment to DB.
     */
    private class SaveExperimentSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // finish disable button
            importExperimentDialog.getSaveExperimentButton().setEnabled(false);
            // show waiting cursor
            importExperimentDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // show also progress bar and its label
            importExperimentDialog.getProgressBarLabel().setVisible(true);
            importExperimentDialog.getSaveExperimentProgressBar().setVisible(true);
            // disable other buttons as well
            importExperimentDialog.getPreviousButton().setEnabled(false);
            importExperimentDialog.getNextButton().setEnabled(false);
            importExperimentDialog.getCancelButton().setEnabled(false);
            //save the new experiment to the DB
            // first we need to check if other objects need to be stored, then we actually save the experiment
            persistNewObjects();
            // set collection for imaging types and algorithms
            List<ImagingType> imagingTypes = experimentService.getImagingTypes(importedExperiment);
            for (ImagingType imagingType : imagingTypes) {
                List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
                for (PlateCondition plateCondition : importedExperiment.getPlateConditionList()) {
                    for (Well well : plateCondition.getWellList()) {
                        for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                            if (wellHasImagingType.getImagingType().equals(imagingType)) {
                                wellHasImagingType.setImagingType(imagingType);
                                wellHasImagingTypes.add(wellHasImagingType);
                            }
                        }
                    }
                }
                imagingType.setWellHasImagingTypeList(wellHasImagingTypes);
            }

            List<Algorithm> algorithms = experimentService.getAlgorithms(importedExperiment);
            for (Algorithm algorithm : algorithms) {
                List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
                for (PlateCondition plateCondition : importedExperiment.getPlateConditionList()) {
                    for (Well well : plateCondition.getWellList()) {
                        for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                            if (wellHasImagingType.getAlgorithm().equals(algorithm)) {
                                wellHasImagingType.setAlgorithm(algorithm);
                                wellHasImagingTypes.add(wellHasImagingType);
                            }
                        }
                    }
                }
                algorithm.setWellHasImagingTypeList(wellHasImagingTypes);
            }
            // save the experiment, save the migration data and update the experiment
            experimentService.save(importedExperiment);
            experimentService.saveMigrationDataForExperiment(importedExperiment);
            importedExperiment = experimentService.update(importedExperiment);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                //show back default cursor and hide progress bar and its label
                importExperimentDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                importExperimentDialog.getProgressBarLabel().setVisible(false);
                importExperimentDialog.getSaveExperimentProgressBar().setVisible(false);
                JOptionPane.showMessageDialog(importExperimentDialog, "Imported experiment was successfully saved to DB.", "imported experiment saved", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("Experiment " + importedExperiment + "_" + importedExperiment.getProject() + " saved");
                // hide dialog
                resetViewOnImportExperimentDialog();
                importExperimentDialog.setVisible(false);
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Given a certain directory chosen by the user, this method attempts to
     * create an XML file. The XML file has as title information that comes from
     * the experiment itself.
     *
     * @param directory
     */
    private File createXmlFile(String fileName, File directory, JDialog dialog) {
        // we create the unique XML file using the experiment info
        File xmlFile = new File(directory, fileName);
        try {
            boolean success = xmlFile.createNewFile();
            if (!success) {
                Object[] options = {"Yes", "No", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(dialog, "File already exists in this directory. Do you want to replace it?", "file already exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                // if YES, user wants to delete existing file and replace it
                if (showOptionDialog == 0) {
                    boolean delete = xmlFile.delete();
                    if (!delete) {
                        return null;
                    }
                    // if NO or CANCEL, returns already existing file
                } else if (showOptionDialog == 1 || showOptionDialog == 2) {
                    return null;
                }
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(dialog, "Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
        return xmlFile;
    }

    /**
     * Export the experiment to the XML file with the experiment service.
     *
     * @param xmlFile
     */
    private void exportExperimentToXMLFile(File xmlFile) {
        try {
            experimentService.exportExperimentToXMLFile(experimentToExport, xmlFile);
        } catch (JAXBException | FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(exportExperimentDialog, "Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Export the experiment template to the XML file with the experiment
     * service.
     *
     * @param xmlFile
     */
    private void exportExperimentTemplateToXMLFile(File xmlFile) {
        // we create a new temporary experiment, we copy the set up settings to it
        // and then we export this new experiment to the XML file
        Experiment tempExperiment = new Experiment();
        experimentService.copySetupSettingsFromOtherExperiment(experimentTemplateToExport, tempExperiment);
        tempExperiment.setPurpose(experimentTemplateToExport.getPurpose());
        tempExperiment.setExperimentStatus(experimentTemplateToExport.getExperimentStatus());
        tempExperiment.setExperimentNumber(experimentTemplateToExport.getExperimentNumber());
        try {
            experimentService.exportExperimentToXMLFile(tempExperiment, xmlFile);
        } catch (JAXBException | FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(exportTemplateDialog, "Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Parse the XML file using the experiment service.
     *
     * @param xmlFile
     */
    private void parseXMLFile(File xmlFile) {
        try {
            // with the exp service get the EXPERIMENT object from the XML file
            importedExperiment = experimentService.getExperimentFromXMLFile(xmlFile);
            // we check here for the validation errors (these are retrieved from the xml validator)
            List<String> xmlValidationErrorMesages = experimentService.getXmlValidationErrorMesages();
            // if no errors during unmarshal, continue, else, show the errors
            if (xmlValidationErrorMesages.isEmpty()) {
                LOG.info("Experiment imported from XML file " + xmlFile.getAbsolutePath());
                // update info and condiitons table
                updateInfoOnImportExperimentDialog(xmlFile);
                updateConditionsTableModel(importExperimentDialog.getConditionsDetailsTable(), importedExperiment);
            } else {
                // validation of the XML file was not successful: collect the validation messages and inform the user
                String mainMessage = "Error in validating " + xmlFile.getAbsolutePath() + "\n";
                String totalMessage = "";
                for (String string : xmlValidationErrorMesages) {
                    totalMessage += mainMessage.concat(string + "\n");
                }
                JOptionPane.showMessageDialog(importExperimentDialog, totalMessage, "invalid xml file", JOptionPane.ERROR_MESSAGE);
            }
            // this error is related to the xsd schema: normally this is OK, but we need to catch this
        } catch (SAXException ex) {
            LOG.error(ex.getMessage(), ex);
            String message = "Error occurred during parsing the xsd schema for CellMissy!";
            JOptionPane.showMessageDialog(importExperimentDialog, message, "xsd schema error", JOptionPane.ERROR_MESSAGE);
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
                    String errorMessage = "An error occurred while importing experiment from " + xmlFile + "\n" + sAXParseException.getLocalizedMessage() + "\nCheck line number " + lineNumber + " in the XML file.";
                    JOptionPane.showMessageDialog(importExperimentDialog, errorMessage, "not valid XML file", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException ex) {
            // this IO exception comes from using the resource for the xsd schema !
            LOG.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(importExperimentDialog, "CellMissy did not find a valid xsd schema for the validation of the XML file.", "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Persist new objects to the DB, if any.
     */
    private void persistNewObjects() {
        // plate format
        PlateFormat plateFormat = importedExperiment.getPlateFormat();
        PlateFormat foundFormat = plateService.findByFormat(plateFormat.getFormat());
        if (foundFormat == null) {
            plateService.save(plateFormat);
        }
        // cell line types
        List<CellLineType> foundCellLines = cellLineService.findNewCellLines(importedExperiment);
        if (!foundCellLines.isEmpty()) {
            for (CellLineType cellLineType : foundCellLines) {
                cellLineService.saveCellLineType(cellLineType);
            }
        }
        // assays
        List<Assay> foundAssays = assayService.findNewAssays(importedExperiment);
        if (!foundAssays.isEmpty()) {
            for (Assay assay : foundAssays) {
                assayService.save(assay);
            }
        }
        // bottom matrix
        List<BottomMatrix> foundBottomMatrixs = ecmService.findNewBottomMatrices(importedExperiment);
        if (!foundBottomMatrixs.isEmpty()) {
            for (BottomMatrix bottomMatrix : foundBottomMatrixs) {
                ecmService.saveBottomMatrix(bottomMatrix);
            }
        }
        // ecm composition
        List<EcmComposition> foundCompositions = ecmService.findNewEcmCompositions(importedExperiment);
        if (!foundCompositions.isEmpty()) {
            for (EcmComposition ecmComposition : foundCompositions) {
                ecmService.saveEcmComposition(ecmComposition);
            }
        }
        // ecm densities
        List<EcmDensity> foundDensitys = ecmService.findNewEcmDensities(importedExperiment);
        if (!foundDensitys.isEmpty()) {
            for (EcmDensity ecmDensity : foundDensitys) {
                ecmService.saveEcmDensity(ecmDensity);
            }
        }
        // treatment types
        List<TreatmentType> foundTreatmentTypes = treatmentService.findNewTreatmentTypes(importedExperiment);
        if (!foundTreatmentTypes.isEmpty()) {
            for (TreatmentType treatmentType : foundTreatmentTypes) {
                treatmentService.saveTreatmentType(treatmentType);
            }
        }
    }

    /**
     * Update the info on the import dialog.
     *
     * @param xmlFile
     */
    private void updateInfoOnImportExperimentDialog(File xmlFile) {
        // file label
        importExperimentDialog.getFileLabel().setText(" " + xmlFile.getAbsolutePath());
        importExperimentDialog.getExpNumberLabel().setText(" " + importedExperiment);
        importExperimentDialog.getInstrumentLabel().setText(" " + importedExperiment.getInstrument().getName());
        importExperimentDialog.getTimeFramesLabel().setText(" " + importedExperiment.getTimeFrames());
        importExperimentDialog.getNumberConditionsLabel().setText(" " + importedExperiment.getPlateConditionList().size());
        importExperimentDialog.getPlateFormatLabel().setText(" " + importedExperiment.getPlateFormat());
        importExperimentDialog.getPurposeTextArea().setText(" " + importedExperiment.getPurpose());
        importExperimentDialog.getDurationLabel().setText(" " + importedExperiment.getDuration());
        importExperimentDialog.getIntervalLabel().setText(" " + importedExperiment.getExperimentInterval());
        importExperimentDialog.getAlgoritmsLabel().setText(" " + experimentService.getAlgorithms(importedExperiment).size());
        importExperimentDialog.getImagingTypesLabel().setText(" " + experimentService.getImagingTypes(importedExperiment).size());
    }

    /**
     *
     */
    private void updateDetailsOnImportExperimentDialog() {
        // if a new parameter needs to be inserted to DB, we use its name for the label
        // otherwise, we set the label to "no new parameters to add"
        // PLATE FORMAT
        PlateFormat newPlateFormat = plateService.findByFormat(importedExperiment.getPlateFormat().getFormat());
        if (newPlateFormat == null) {
            importExperimentDialog.getNewPlateFormatLabel().setText(" " + importedExperiment.getPlateFormat());
            importExperimentDialog.getNewPlateFormatLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importExperimentDialog.getNewPlateFormatLabel().setText(" no new parameters to add");
            importExperimentDialog.getNewPlateFormatLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // CELL LINES
        List<CellLineType> newCellLines = cellLineService.findNewCellLines(importedExperiment);
        if (!newCellLines.isEmpty()) {
            importExperimentDialog.getNewCellLineLabel().setText(" " + newCellLines);
            importExperimentDialog.getNewCellLineLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importExperimentDialog.getNewCellLineLabel().setText(" no new parameters to add");
            importExperimentDialog.getNewCellLineLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // ASSAYS
        List<Assay> newAssays = assayService.findNewAssays(importedExperiment);
        if (!newAssays.isEmpty()) {
            importExperimentDialog.getNewAssayLabel().setText(" " + newAssays);
            importExperimentDialog.getNewAssayLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importExperimentDialog.getNewAssayLabel().setText(" no new parameters to add");
            importExperimentDialog.getNewAssayLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // BOTTOM MATRICES
        List<BottomMatrix> newBottomMatrices = ecmService.findNewBottomMatrices(importedExperiment);
        if (!newBottomMatrices.isEmpty()) {
            importExperimentDialog.getNewBottomMatrixLabel().setText(" " + newBottomMatrices);
            importExperimentDialog.getNewBottomMatrixLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importExperimentDialog.getNewBottomMatrixLabel().setText(" no new parameters to add");
            importExperimentDialog.getNewBottomMatrixLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // ECM COMPOSITIONS
        List<EcmComposition> newEcmCompositions = ecmService.findNewEcmCompositions(importedExperiment);
        if (!newEcmCompositions.isEmpty()) {
            importExperimentDialog.getNewEcmCompositionLabel().setText(" " + newEcmCompositions);
            importExperimentDialog.getNewEcmCompositionLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importExperimentDialog.getNewEcmCompositionLabel().setText(" no new parameters to add");
            importExperimentDialog.getNewEcmCompositionLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // ECM DENSITIES
        List<EcmDensity> newEcmDensities = ecmService.findNewEcmDensities(importedExperiment);
        if (!newEcmDensities.isEmpty()) {
            importExperimentDialog.getNewEcmDensityLabel().setText(" " + newEcmDensities);
            importExperimentDialog.getNewEcmDensityLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importExperimentDialog.getNewEcmDensityLabel().setText(" no new parameters to add");
            importExperimentDialog.getNewEcmDensityLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
        // TREATMENTS TYPES
        List<TreatmentType> newTreatmentTypes = treatmentService.findNewTreatmentTypes(importedExperiment);
        if (!newTreatmentTypes.isEmpty()) {
            importExperimentDialog.getNewTreatmentLabel().setText(" " + newTreatmentTypes);
            importExperimentDialog.getNewTreatmentLabel().setFont(new Font("Tahoma", Font.PLAIN, 12));
        } else {
            importExperimentDialog.getNewTreatmentLabel().setText(" no new parameters to add");
            importExperimentDialog.getNewTreatmentLabel().setFont(new Font("Tahoma", Font.ITALIC, 12));
        }
    }
}
