/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.project.NewProjectDialog;
import be.ugent.maf.cellmissy.gui.project.ProjectInfoDialog;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.UserService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
 * Controller to set up a new project: child controller for the setup experiment
 * controller.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("setupProjectController")
public class SetupProjectController {

    private static final Logger LOG = Logger.getLogger(SetupProjectController.class);
    // model
    private ObservableList<User> sourceUsersBindingList;
    private ObservableList<User> destinationUsersBindingList;
    private BindingGroup bindingGroup;
    // view
    private NewProjectDialog newProjectDialog;
    private ProjectInfoDialog projectInfoDialog;
    // parent controller
    @Autowired
    private SetupExperimentController setupExperimentController;
    // child controllers
    // services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        projectInfoDialog = new ProjectInfoDialog(setupExperimentController.getCellMissyFrame(), true);
        // init views
        initNewProjectDialog();
    }

    /**
     * Getters
     */
    public NewProjectDialog getNewProjectDialog() {
        return newProjectDialog;

    }

    /**
     * Pack and show the new project dialog
     */
    public void showNewProjectDialog() {
        newProjectDialog.getProjectNumberTextField().setText("");
        newProjectDialog.getDescriptionTextArea().setText("");
        // show a newProjectDialog
        newProjectDialog.pack();
        newProjectDialog.setVisible(true);
    }

    /**
     * Initialize new project dialog. This is the main view in this controller
     * to set up, thus create and save a new project.
     */
    private void initNewProjectDialog() {
        // customize dialog
        newProjectDialog = new NewProjectDialog(setupExperimentController.getCellMissyFrame(), true);
        //center the dialog on the main screen
        newProjectDialog.setLocationRelativeTo(setupExperimentController.getCellMissyFrame());
        // set icon for info label
        Icon infoIcon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledInfoIcon = GuiUtils.getScaledIcon(infoIcon);
        newProjectDialog.getInfoLabel().setIcon(scaledInfoIcon);
        // set icon for question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        ImageIcon scaledQuestionIcon = GuiUtils.getScaledIcon(questionIcon);
        newProjectDialog.getQuestionButton().setIcon(scaledQuestionIcon);
        // users binding lists: source is filled in with all users from the database
        sourceUsersBindingList = ObservableCollections.observableList(userService.findAll());
        // destination list is empty at first place
        destinationUsersBindingList = ObservableCollections.observableList(new ArrayList<User>());
        //init source and destination Jlists bindings
        JListBinding sourceListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, sourceUsersBindingList, newProjectDialog.getSourceUsersList());
        bindingGroup.addBinding(sourceListBinding);
        //init general treatment JList binding
        JListBinding destinationListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, destinationUsersBindingList, newProjectDialog.getDestinationUsersList());
        bindingGroup.addBinding(destinationListBinding);

        // do the binding
        bindingGroup.bind();
        /**
         * Add action listeners.
         */
        // create a new project
        newProjectDialog.getCreateProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create new project: save it to DB!
                if (!newProjectDialog.getProjectNumberTextField().getText().isEmpty()) {
                    saveNewProject();
                } else {
                    setupExperimentController.showMessage("Please insert a number for the project you want to create", "Error while creating new project", JOptionPane.WARNING_MESSAGE);
                    newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
                }
            }
        });

        // add action Listener to the question/info button
        newProjectDialog.getQuestionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectInfoDialog.pack();
                // pack and show info dialog
                GuiUtils.centerDialogOnFrame(setupExperimentController.getCellMissyFrame(), projectInfoDialog);
                projectInfoDialog.setVisible(true);
            }
        });

        // add mouse listeners
        // select source user OR destination user
        newProjectDialog.getSourceUsersList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (newProjectDialog.getSourceUsersList().getSelectedIndex() != -1) {
                        newProjectDialog.getDestinationUsersList().clearSelection();
                    }
                }
            }
        });

        newProjectDialog.getDestinationUsersList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (newProjectDialog.getDestinationUsersList().getSelectedIndex() != -1) {
                        newProjectDialog.getSourceUsersList().clearSelection();
                    }
                }
            }
        });

        // assign users to the current project
        // add and remove buttons will move around users from source to destination list
        newProjectDialog.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User sourceSelectedUser = (User) newProjectDialog.getSourceUsersList().getSelectedValue();
                if (sourceSelectedUser != null) {
                    // move the user from the source list to the destination list
                    destinationUsersBindingList.add(sourceSelectedUser);
                    sourceUsersBindingList.remove(sourceSelectedUser);
                    // select the just added element in the destination list
                    newProjectDialog.getDestinationUsersList().setSelectedIndex(destinationUsersBindingList.indexOf(sourceSelectedUser));
                }
            }
        });

        newProjectDialog.getRemoveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User destinationSelectedUser = (User) newProjectDialog.getDestinationUsersList().getSelectedValue();
                if (destinationSelectedUser != null) {
                    // move the user from the destination list to the source list
                    sourceUsersBindingList.add(destinationSelectedUser);
                    destinationUsersBindingList.remove(destinationSelectedUser);
                    // select the just added element in the destination list
                    newProjectDialog.getDestinationUsersList().setSelectedIndex(sourceUsersBindingList.indexOf(destinationSelectedUser));
                }
            }
        });
    }

    /**
     * Setup a new project, with number, description and a list of users
     * associated to it.
     */
    private void saveNewProject() {
        // create a new project
        Project newProject = new Project();
        try {
            // project number and project description
            int projectNumber = Integer.parseInt(newProjectDialog.getProjectNumberTextField().getText());
            String projectDescription = newProjectDialog.getDescriptionTextArea().getText();
            // set number, description
            newProject.setProjectNumber(projectNumber);
            newProject.setProjectDescription(projectDescription);
            // get the list of the users here and assign them to the project
            List<ProjectHasUser> projectHasUsersList = new ArrayList<>();
            for (User destinationUser : destinationUsersBindingList) {
                ProjectHasUser projectHasUser = new ProjectHasUser(newProject, destinationUser);
                projectHasUsersList.add(projectHasUser);
            }
            newProject.setProjectHasUserList(projectHasUsersList);
            for (User destinationUser : destinationUsersBindingList) {
                destinationUser.setProjectHasUserList(projectHasUsersList);
            }

            // save the users
            projectService.saveProjectUsers(newProject);
            projectService.save(newProject);
            LOG.info(newProject + "was created");
            // creation of new project was successfull
            setupExperimentController.showMessage(newProject + "was created", "project created", JOptionPane.INFORMATION_MESSAGE);
            newProjectDialog.getProjectNumberTextField().setText("");
            newProjectDialog.getDescriptionTextArea().setText("");
            // close the dialog
            newProjectDialog.setVisible(false);
        } catch (NumberFormatException exception) {
            setupExperimentController.showMessage("Please insert a valid number", "Error while creating new project", JOptionPane.WARNING_MESSAGE);
            LOG.error(exception.getMessage());
            newProjectDialog.getProjectNumberTextField().setText("");
            newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
        } catch (PersistenceException exception) {
            setupExperimentController.showMessage("Project already present in the DB", "Error in persisting project", JOptionPane.WARNING_MESSAGE);
            LOG.error(exception.getMessage());
            newProjectDialog.getProjectNumberTextField().setText("");
            newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
        }
    }
}
