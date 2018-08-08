/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.cmso.CMSOExportPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.ExperimentsOverviewListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Gwendolien Sergeant
 */
@Controller("cMSOExportController")
public class CMSOExportController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CMSOExportController.class);

    //model
    private Experiment experimentToExport;
    private BindingGroup bindingGroup;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private WaitingDialog waitingDialog;
    //view
    private CMSOExportPanel cmsoExportPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        // make a new waiting dialog here
        waitingDialog = new WaitingDialog(cellMissyController.getCellMissyFrame(), true);
        // init views
        initExportPanel();
    }

    /**
     * Initialize Export Panel
     */
    private void initExportPanel() {
        cmsoExportPanel.getProjectDescriptionTextArea().setLineWrap(true);
        cmsoExportPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        cmsoExportPanel.getInfoLabel().setIcon(scaledIcon);
        // init projects list
        List<Project> allProjects = projectService.findAll();
        // sort the projects
        Collections.sort(allProjects);
        projectBindingList = ObservableCollections.observableList(allProjects);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, cmsoExportPanel.getProjectsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        // customize table
        cmsoExportPanel.getConditionsDetailsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        cmsoExportPanel.getConditionsDetailsTable().getTableHeader().setReorderingAllowed(false);
        // set the cell renderer for the experiments list: the experiments are selectable
        cmsoExportPanel.getExperimentsList().setCellRenderer(new ExperimentsOverviewListRenderer(true));
        //when a project from the list is selected, show all experiments performed for that project
        cmsoExportPanel.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    int selectedIndex = cmsoExportPanel.getProjectsList().getSelectedIndex();
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
        cmsoExportPanel.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //init experimentJList
                    Experiment selectedExperiment = (Experiment) cmsoExportPanel.getExperimentsList().getSelectedValue();
                    if (selectedExperiment != null) {
                        experimentToExport = selectedExperiment;
                        // get the information and update the fields
                        cmsoExportPanel.getUserLabel().setText(" " + selectedExperiment.getUser().toString());
                        cmsoExportPanel.getPurposeTextArea().setText(" " + selectedExperiment.getPurpose());
                        cmsoExportPanel.getTimeFramesLabel().setText(" " + selectedExperiment.getTimeFrames().toString());
                        cmsoExportPanel.getInstrumentLabel().setText(" " + selectedExperiment.getInstrument().getName());
                        cmsoExportPanel.getPlateFormatLabel().setText(" " + selectedExperiment.getPlateFormat().toString());
                        cmsoExportPanel.getNumberConditionsLabel().setText(" " + selectedExperiment.getPlateConditionList().size());
                        // set the model of the conditions table
                        updateConditionsTableModel(cmsoExportPanel.getConditionsDetailsTable(), selectedExperiment);
                    }
                }
            }
        });

        // add action listeners
        // copy the settings for current experiment: execute the swing worker
        cmsoExportPanel.getExportButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Experiment experimentToExport = (Experiment) cmsoExportPanel.getExperimentsList().getSelectedValue();
                // be sure that one experiment is selected in the list
                if (experimentToExport != null) {
                    // show a jfile chooser to decide where to save the file
                    JFileChooser chooseDirectory = new JFileChooser();
                    chooseDirectory.setDialogTitle("Choose a directory to save the dataset to");
                    chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // in response to the button click, show open dialog
                    int returnVal = chooseDirectory.showSaveDialog(cmsoExportPanel);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File currentDirectory = chooseDirectory.getSelectedFile();

                        // create cmso folder strucure to export the files
                        String topFolderName = "CellMissyExport_" + experimentToExport + "_" + experimentToExport.getProject() + ".xml";
                        File topFolder = createFolderStructure(topFolderName, currentDirectory);

                        
                        File xmlFile = createXmlFile(fileName, currentDirectory);

                        // if the XML file was successfully created, we execute a swing worker and export the experiment to the file.
                        if (xmlFile != null) {
                            ExportExperimentSwingWorker exportExperimentSwingWorker = new ExportExperimentSwingWorker(xmlFile);
                            exportExperimentSwingWorker.execute();
                        }
                    } else {
                        JOptionPane.showMessageDialog(cmsoExportPanel, "Command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // tell the user that he needs to select an experiment!
                    JOptionPane.showMessageDialog(cmsoExportPanel, "Please select an experiment to export!", "no exp selected error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

    }

    private File createFolderStructure(String mainFolderName, File directory) {
        //folders that need to be created are: main folder, isa and tracking software(s?)
        //tracking software has all wells inside and in there biotracks dp
        Path path = Paths.get(directory.getPath() + "");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
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
        cmsoExportPanel.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid, ExperimentStatus.PERFORMED);
        if (experimentList != null) {
            // sort the experiments
            Collections.sort(experimentList);
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, cmsoExportPanel.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
        } else {
            String message = "There are no experiments performed yet for this project!";
            JOptionPane.showMessageDialog(cmsoExportPanel, message, "No experiments found", JOptionPane.INFORMATION_MESSAGE);
            resetView();
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }

    /**
     * Reset views.
     */
    private void resetView() {
        // reset the information fields
        cmsoExportPanel.getUserLabel().setText("");
        cmsoExportPanel.getPurposeTextArea().setText("");
        cmsoExportPanel.getTimeFramesLabel().setText("");
        cmsoExportPanel.getInstrumentLabel().setText("");
        cmsoExportPanel.getPlateFormatLabel().setText("");
        cmsoExportPanel.getNumberConditionsLabel().setText("");
        cmsoExportPanel.getProjectDescriptionTextArea().setText("");
        // reset table model to a default one
        cmsoExportPanel.getConditionsDetailsTable().setModel(new DefaultTableModel());
        // clear selection on both projects and experiments lists
        cmsoExportPanel.getExperimentsList().clearSelection();
        cmsoExportPanel.getProjectsList().clearSelection();
        // clear the experiments binding list
        if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
            experimentBindingList.clear();
        }
    }

    /**
     * For a certain table, this method creates a model from the given
     * experiment with the conditions details and assign the model to the table.
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
            GuiUtils.packColumn(table, i);
        }
    }

    /**
     * Given a certain directory chosen by the user, this method attempts to
     * create an XML file. The XML file has as title information that comes from
     * the experiment itself.
     *
     * @param directory
     */
    private File createXmlFile(String fileName, File directory) {
        // we create the unique XML file using the experiment info
        File xmlFile = new File(directory, fileName);
        try {
            boolean success = xmlFile.createNewFile();
            if (!success) {
                Object[] options = {"Yes", "No", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(cmsoExportPanel, "File already exists in this directory. Do you want to replace it?", "file already exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
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
            JOptionPane.showMessageDialog(cmsoExportPanel, "Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
        return xmlFile;
    }

    /**
     * Swing worker to export the experiment to an XML file
     */
    private class ExportExperimentSwingWorker extends SwingWorker<Void, Void> {

        // the file to write the experiment to
        private final File xmlFile;

        public ExportExperimentSwingWorker(File xmlFile) {
            this.xmlFile = xmlFile;
        }

        @Override
        protected Void doInBackground() throws Exception {
            //disable buttons and show a waiting cursor
            exportExperimentDialog.getExportButton().setEnabled(false);
            exportExperimentDialog.getCancelButton().setEnabled(false);
            // show waiting dialog
            String title = "Experiment is being exported to file. Please wait...";
            showWaitingDialog(title);
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
                // hide waiting dialog
                waitingDialog.setVisible(false);
                JOptionPane.showMessageDialog(exportExperimentDialog, "Experiment was successfully exported!", "experiment exported", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("experiment " + experimentToExport + "_" + experimentToExport.getProject() + " exported to file");
                // enable the buttons again
                exportExperimentDialog.getCancelButton().setEnabled(true);
                exportExperimentDialog.getExportButton().setEnabled(true);
                exportExperimentDialog.setVisible(false);
                resetViewOnExportExperimentDialog();
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
            }

        }
    }

    /**
     * Swing worker to export the experiment template
     */
    private class ExportTemplateSwingWorker extends SwingWorker<Void, Void> {

        private final File xmlFile;

        public ExportTemplateSwingWorker(File xmlFile) {
            this.xmlFile = xmlFile;
        }

        @Override
        protected Void doInBackground() throws Exception {
            //disable buttons and show a waiting cursor
            exportTemplateDialog.getExportButton().setEnabled(false);
            // show waiting dialog
            String title = "Template is being exported to file. Please wait...";
            showWaitingDialog(title);
            // export the experiment to file
            exportExperimentTemplateToXMLFile(xmlFile);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                waitingDialog.setVisible(false);
                JOptionPane.showMessageDialog(exportTemplateDialog, "Experiment template was successfully exported!", "experiment template exported", JOptionPane.INFORMATION_MESSAGE);
                LOG.info("experiment template " + experimentTemplateToExport + "_" + experimentTemplateToExport.getProject() + " exported to file");
                resetViewOnExportTemplateDialog();
                exportTemplateDialog.setVisible(false);
                cellMissyController.onStartup();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            // enable the button again
            exportTemplateDialog.getExportButton().setEnabled(true);
        }
    }
}
