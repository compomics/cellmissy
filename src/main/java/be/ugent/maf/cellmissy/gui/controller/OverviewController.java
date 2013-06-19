/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.project.OverviewDialog;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsOverviewListRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
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
 * @author Paola Masuzzo
 */
@Controller("overviewController")
public class OverviewController {

    private static final Logger LOG = Logger.getLogger(OverviewController.class);
    // model 
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private BindingGroup bindingGroup;
    // view
    private OverviewDialog overviewDialog;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    // child controllers
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
        // initialize main view component
        overviewDialog = new OverviewDialog(cellMissyController.getCellMissyFrame(), true);
        initDialog();
    }

    /**
     * Show main view through parent controller after click on main frame menu
     * item
     */
    public void showOverviewDialog() {
        overviewDialog.pack();
        overviewDialog.setVisible(true);
    }

    /**
     * Disable buttons for STANDARD users
     */
    public void disableAdminSection() {
        // disable delete experiment button
        overviewDialog.getDeleteExperimentButton().setEnabled(false);
        overviewDialog.getExperimentJList().setFocusable(false);
    }

    /**
     * Initialize dialog
     */
    private void initDialog() {
        overviewDialog.getProjectDescriptionTextArea().setLineWrap(true);
        overviewDialog.getProjectDescriptionTextArea().setWrapStyleWord(true);
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, overviewDialog.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        // customize dialog
        overviewDialog.setLocationRelativeTo(cellMissyController.getCellMissyFrame());
        // set cell renderer for experiments list
        ExperimentsOverviewListRenderer experimentsOverviewListRenderer = new ExperimentsOverviewListRenderer(true);
        overviewDialog.getExperimentJList().setCellRenderer(experimentsOverviewListRenderer);
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        // set icon for info label
        overviewDialog.getInfoLabel().setIcon(scaledIcon);
        //show experiments for the project selected
        overviewDialog.getProjectJList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //init experimentJList
                    int selectedIndex = overviewDialog.getProjectJList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedIndex);
                        // set text for project description
                        overviewDialog.getProjectDescriptionTextArea().setText(selectedProject.getProjectDescription());
                        Long projectid = selectedProject.getProjectid();
                        List<Integer> experimentNumbers = experimentService.findExperimentNumbersByProjectId(projectid);
                        if (experimentNumbers != null) {
                            List<Experiment> experimentList = experimentService.findExperimentsByProjectId(projectid);
                            experimentBindingList = (ObservableCollections.observableList(experimentList));
                            JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, overviewDialog.getExperimentJList());
                            bindingGroup.addBinding(jListBinding);
                            bindingGroup.bind();
                        } else {
                            cellMissyController.showMessage("There are no experiments yet for this project!", "", JOptionPane.INFORMATION_MESSAGE);
                            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                                experimentBindingList.clear();
                            }
                        }
                    }
                }
            }
        });

        /**
         * Add Action Listeners
         */
        // delete an experiment: give the right to perform this action only to ADMIN users!!
        overviewDialog.getDeleteExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = overviewDialog.getExperimentJList().getSelectedIndex();
                // if an experiment has been selected
                if (selectedIndex != -1) {
                    // ask for confirmation
                    Object[] options = {"Yes", "No", "Cancel"};
                    String message = "You are about to delete an experiment!" + "\n" + "Everything from this experiment will be deleted!" + "Continue?";
                    int showOptionDialog = JOptionPane.showOptionDialog(overviewDialog, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                    // if YES, continue, else, do nothing
                    if (showOptionDialog == 0) {
                        // create and execute a new swing worker
                        DeleteExperimentSwingWorker deleteExperimentSwingWorker = new DeleteExperimentSwingWorker();
                        deleteExperimentSwingWorker.execute();
                    }
                } else {
                    // else ask the user to select an experiment first
                    cellMissyController.showMessage("Please select an experiment first.", "delete exp error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    /**
     * Swing Worker to delete the Experiment
     */
    private class DeleteExperimentSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // disable the delete button
            overviewDialog.getDeleteExperimentButton().setEnabled(false);
            // show a waiting cursor
            overviewDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int selectedIndex = overviewDialog.getExperimentJList().getSelectedIndex();
            Experiment experimentToDelete = experimentBindingList.get(selectedIndex);
            // delete experiment
            experimentService.delete(experimentToDelete);
            // delete experiment from experiment binding list
            experimentBindingList.remove(experimentToDelete);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // enable the delete button
                overviewDialog.getDeleteExperimentButton().setEnabled(true);
                //show back default cursor 
                overviewDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                // show info message
                cellMissyController.showMessage("Experiment was successfully deleted!", "experiment deleted", JOptionPane.INFORMATION_MESSAGE);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }
}
