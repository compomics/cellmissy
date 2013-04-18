/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.project.OverviewDialog;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsListRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
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
     * Show main view through parent controller after click on main frame menu item
     */
    public void showOverviewDialog() {
        overviewDialog.pack();
        overviewDialog.setVisible(true);
    }

    /**
     * Disable buttons for STANDARD users
     */
    public void disableActionsOnExperiments() {
        // disable delete experiment button
        overviewDialog.getDeleteExperimentButton().setEnabled(false);
    }

    /**
     * Initialize dialog
     */
    private void initDialog() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, overviewDialog.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        // customize dialog
        overviewDialog.setLocationRelativeTo(cellMissyController.getCellMissyFrame());
        // set cell renderer for experiments list
        overviewDialog.getExperimentJList().setCellRenderer(new ExperimentsListRenderer(true));
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        // set icon for info label
        overviewDialog.getInfoLabel().setIcon(scaledIcon);
        //show experiments for the project selected
        overviewDialog.getProjectJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //init experimentJList
                int locationToIndex = overviewDialog.getProjectJList().locationToIndex(e.getPoint());
                if (experimentService.findExperimentNumbersByProjectId(projectBindingList.get(locationToIndex).getProjectid()) != null) {
                    experimentBindingList = (ObservableCollections.observableList(experimentService.findExperimentsByProjectId(projectBindingList.get(locationToIndex).getProjectid())));
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
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "You are about to delete an experiment!" + "\nContinue?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                    // if YES, continue, else, do nothing
                    if (showOptionDialog == 0) {
                        Experiment selectedExperiment = experimentBindingList.get(selectedIndex);
                        // delete experiment from DB
                        experimentService.delete(selectedExperiment);
                        // delete experiment from experiment binding list
                        experimentBindingList.remove(selectedExperiment);
                        // inform the user that the experimet was deleted
                        cellMissyController.showMessage("Experiment was successfully deleted!", "experiment deleted", JOptionPane.INFORMATION_MESSAGE);                       
                    }
                } else {
                    // else ask the user to select an experiment first
                    cellMissyController.showMessage("Please select an experiment first.", "delete exp error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // edit an experiment
        overviewDialog.getEditExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }
}
