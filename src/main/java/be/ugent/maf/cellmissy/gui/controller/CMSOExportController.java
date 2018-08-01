/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.cmso.CMSOExportPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.ExperimentsOverviewListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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

        // close the dialog: just empty the text fields
        cmsoExportPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // reset view when we close the dialog
                resetViewOnExportExperimentDialog();
            }
        });

        // add action listeners
        // copy the settings for current experiment: execute the swing worker
        cmsoExportPanel.getExportButton().addActionListener(new ActionListener() {
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
        cmsoExportPanel.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // cancel: hide the dialog
                exportExperimentDialog.setVisible(false);
                // reset views
                resetViewOnExportExperimentDialog();
            }
        });
    }
}
