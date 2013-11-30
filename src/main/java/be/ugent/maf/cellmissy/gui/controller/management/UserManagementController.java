/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.management;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.user.UserInfoDialog;
import be.ugent.maf.cellmissy.gui.user.UserManagementDialog;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.ValidationUtils;
import be.ugent.maf.cellmissy.service.UserService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * User Management Controller: user management (create, search and delete users
 * from DB) Parent Controller: CellMissy Controller (main controller)
 *
 * @author Paola
 */
@Controller("userManagementController")
public class UserManagementController {

    private static final Logger LOG = Logger.getLogger(UserManagementController.class);
    //model
    private ObservableList<User> userBindingList;
    private ObservableList<Project> projectBindingList;
    private BindingGroup bindingGroup;
    //view
    private UserManagementDialog userManagementDialog;
    private UserInfoDialog userInfoDialog;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //services
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //create a new user dialog and init view
        userManagementDialog = new UserManagementDialog(cellMissyController.getCellMissyFrame(), true);
        userInfoDialog = new UserInfoDialog(cellMissyController.getCellMissyFrame(), true);
        // init main view
        initUserManagementDialog();
    }

    /**
     * validate User
     *
     * @param userToValidate
     * @return List of Messages to show to the user
     */
    public List<String> validateUser(User userToValidate) {
        return ValidationUtils.validateObject(userToValidate);
    }

    /**
     * Show user management dialog
     */
    public void showUserManagementDialog() {
        userManagementDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), userManagementDialog);
        userManagementDialog.setVisible(true);
    }

    /**
     * Called in the main controller, reset fields if another view is being
     * shown
     */
    public void resetAfterCardSwitch() {
        // clear selection on users list
        userManagementDialog.getUsersList().clearSelection();
    }

    /**
     * initialize User Dialog
     */
    private void initUserManagementDialog() {
        // set icon for question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        ImageIcon scaledQuestionIcon = GuiUtils.getScaledIcon(questionIcon);
        userManagementDialog.getQuestionButton().setIcon(scaledQuestionIcon);
        // init userJList
        List<User> allUsers = userService.findAll();
        Collections.sort(allUsers);
        userBindingList = ObservableCollections.observableList(allUsers);
        JListBinding userListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, userBindingList, userManagementDialog.getUsersList());
        bindingGroup.addBinding(userListBinding);
        // init user binding
        // autobind first name
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUsersList(), BeanProperty.create("selectedElement.firstName"), userManagementDialog.getFirstNameTextField(), BeanProperty.create("text"), "firstnamebinding");
        bindingGroup.addBinding(binding);
        // autobind last name
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUsersList(), BeanProperty.create("selectedElement.lastName"), userManagementDialog.getLastNameTextField(), BeanProperty.create("text"), "lastnamebinding");
        bindingGroup.addBinding(binding);
        // autobind email address
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUsersList(), BeanProperty.create("selectedElement.email"), userManagementDialog.getEmailTextField(), BeanProperty.create("text"), "emailbinding");
        bindingGroup.addBinding(binding);
        // autobind role
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUsersList(), BeanProperty.create("selectedElement.role"), userManagementDialog.getRoleComboBox(), BeanProperty.create("selectedItem"), "rolebinding");
        bindingGroup.addBinding(binding);
        // autobind password
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUsersList(), BeanProperty.create("selectedElement.password"), userManagementDialog.getPasswordField(), BeanProperty.create("text"), "passwordbinding");
        bindingGroup.addBinding(binding);
        // do the binding
        bindingGroup.bind();
        // do nothing on close the dialog
        userManagementDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        /**
         * add mouse listeners
         */
        //show experiments for the project selected
        userManagementDialog.getUsersList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    User selectedUser = (User) userManagementDialog.getUsersList().getSelectedValue();
                    if (selectedUser != null) {
                        Long userid = selectedUser.getUserid();
                        // get the relative projects by userid
                        List<Project> projects = projectService.findProjectsByUserid(userid);
                        Collections.sort(projects);
                        // init projectJlist
                        projectBindingList = ObservableCollections.observableList(projects);
                        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, userManagementDialog.getProjectsList());
                        bindingGroup.addBinding(jListBinding);
                        bindingGroup.bind();
                    }
                }
            }
        });

        /**
         * Add action listeners
         */
        //"create user" action
        userManagementDialog.getAddUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // create a new user
                User newUser = new User();
                // set default nonsense values for it
                newUser.setFirstName("first");
                newUser.setLastName("last");
                newUser.setEmail("first.last@email.com");
                newUser.setRole(Role.STANDARD_USER);
                newUser.setPassword("cellmissy");
                // add the user to the current list
                userBindingList.add(newUser);
                // select the user in the list
                userManagementDialog.getUsersList().setSelectedIndex(userBindingList.indexOf(newUser));
                // the user still has to be saved to DB!
                cellMissyController.showMessage("The new user has been added to the list." + "\n" + "You can now edit its properties and save it to DB.", "user added, not saved yet", JOptionPane.INFORMATION_MESSAGE);
                userManagementDialog.getFirstNameTextField().requestFocusInWindow();
            }
        });

        //"save user" action
        userManagementDialog.getSaveUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userManagementDialog.getUsersList().getSelectedValue() != null) {
                    User selectedUser = (User) userManagementDialog.getUsersList().getSelectedValue();
                    // if user validation was successful, save the new user to the db
                    if (cellMissyController.validateUser(selectedUser)) {
                        // if user id is null, persist new object to DB
                        if (selectedUser.getUserid() == null) {
                            try {
                                userService.save(selectedUser);
                                cellMissyController.showMessage("User was saved to DB!", "user inserted into DB", JOptionPane.INFORMATION_MESSAGE);
                            } // handle ConstraintViolationException(UniqueConstraint)
                            catch (PersistenceException ex) {
                                LOG.error(ex.getMessage());
                                String message = "User already present in the db!";
                                cellMissyController.showMessage(message, "Error in persisting user", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            //otherwise, update the entity: this is calling a merge
                            userService.update(selectedUser);
                            cellMissyController.showMessage("User was updated!", "user updated", JOptionPane.INFORMATION_MESSAGE);
                        }
                        userManagementDialog.getUsersList().repaint();
                    }
                } else {
                    String message = "Please select a user to save or update!";
                    cellMissyController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //"delete user" action
        userManagementDialog.getDeleteUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message;
                // check that a user has been selected first
                if (userManagementDialog.getUsersList().getSelectedValue() != null) {
                    User userToDelete = (User) userManagementDialog.getUsersList().getSelectedValue();
                    // if user id is not null, delete the user from the Db, else only from the list
                    if (userToDelete.getUserid() != null) {
                        message = "User (" + userToDelete + ")" + " was deleted from DB!";
                        // delete user from DB
                        userService.delete(userToDelete);
                    } else {
                        message = "User (" + userToDelete + ")" + " was deleted from current list!";
                    }
                    // remove user from users list
                    userBindingList.remove(userToDelete);
                    cellMissyController.showMessage(message, "user deleted", JOptionPane.INFORMATION_MESSAGE);
                    resetUserFields();
                } else {
                    message = "Please select a user to delete!";
                    cellMissyController.showMessage(message, "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // window listener: check if changes are still pending
        userManagementDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // if user changes are pending, warn the user
                if (userNotSaved()) {
                    cellMissyController.showMessage("An user added to the list has not been saved!" + "\n" + "Save the user, or delete it.", "user not saved", JOptionPane.WARNING_MESSAGE);
                } else {
                    userManagementDialog.setVisible(false);
                }
            }
        });

        // add action Listener to the question/info button
        userManagementDialog.getQuestionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userInfoDialog.pack();
                // pack and show info dialog
                GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), userInfoDialog);
                userInfoDialog.setVisible(true);
            }
        });

        //add items to the "role" ComboBox
        for (Role role : Role.values()) {
            userManagementDialog.getRoleComboBox().addItem(role);
        }
        //show standard user
        userManagementDialog.getRoleComboBox().setSelectedIndex(1);
    }

    /**
     * reset text fields of panel
     */
    private void resetUserFields() {
        userManagementDialog.getFirstNameTextField().setText("");
        userManagementDialog.getLastNameTextField().setText("");
        userManagementDialog.getEmailTextField().setText("");
        userManagementDialog.getPasswordField().setText("");
        userManagementDialog.getRoleComboBox().setSelectedIndex(1);
    }

    /**
     * Check if user changes are still pending This is called when you try to
     * close the user management dialog
     *
     * @return
     */
    private boolean userNotSaved() {
        boolean userNotSaved = false;
        for (User user : userBindingList) {
            if (user.getUserid() == null) {
                userNotSaved = true;
                break;
            }
        }
        return userNotSaved;
    }
}