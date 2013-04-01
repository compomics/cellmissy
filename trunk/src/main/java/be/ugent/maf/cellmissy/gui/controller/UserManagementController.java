/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.utils.ValidationUtils;
import be.ugent.maf.cellmissy.gui.user.UserPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.UsersListRenderer;
import be.ugent.maf.cellmissy.service.UserService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * User Panel Controller: user management (create, search and delete users from DB) Parent Controller: CellMissy Controller (main controller)
 *
 * @author Paola
 */
@Controller("userManagementController")
public class UserManagementController {

    private static final Logger LOG = Logger.getLogger(UserManagementController.class);
    //model
    private User newUser;
    private ObservableList<User> userBindingList;
    private BindingGroup bindingGroup;
    //view
    private UserPanel userPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //services
    @Autowired
    private UserService userService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //create a new user panel and init view
        userPanel = new UserPanel();
        newUser = new User();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initUserPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public UserPanel getUserPanel() {
        return userPanel;
    }

    public ObservableList<User> getUserBindingList() {
        return userBindingList;
    }

    /**
     * validate User
     *
     * @return List of Messages to show to the user
     */
    public List<String> validateUser() {
        return ValidationUtils.validateObject(newUser);
    }

    /**
     *
     * @return
     */
    public boolean userInfoIsSaved() {
        String userEmailText = userPanel.getCreateUserEmailTextField().getText();
        String userFirstNameText = userPanel.getCreateUserFirstNameTextField().getText();
        String userLastNameText = userPanel.getCreateUserLastNameTextField().getText();
        char[] password = userPanel.getPasswordField().getPassword();
        // check if some of these text fields contain information that have not been stored yet
        if (((userEmailText.isEmpty() && userFirstNameText.isEmpty()) && userLastNameText.isEmpty()) && password.length == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Called in the main controller, reset fields if another view is being shown
     */
    public void resetAfterCardSwitch() {
        resetCreateUserTextFields();
        userPanel.getSearchUserFirstNameTextField().setText("");
        userPanel.getSearchUserLastNameTextField().setText("");
    }

    /**
     * initialize User Panel
     */
    private void initUserPanel() {
        //init userJList
        userBindingList = ObservableCollections.observableList(userService.findAll());
        JListBinding userListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, userBindingList, userPanel.getUserJList());
        bindingGroup.addBinding(userListBinding);

        userPanel.getUserJList().setCellRenderer(new UsersListRenderer());

        //init user binding
        //bind email
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, userPanel.getCreateUserEmailTextField(), ELProperty.create("${text}"), newUser, BeanProperty.create("email"), "emailbinding");
        bindingGroup.addBinding(binding);
        //bind first name
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, userPanel.getCreateUserFirstNameTextField(), ELProperty.create("${text}"), newUser, BeanProperty.create("firstName"), "firstnamebinding");
        bindingGroup.addBinding(binding);
        //bind last name
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, userPanel.getCreateUserLastNameTextField(), ELProperty.create("${text}"), newUser, BeanProperty.create("lastName"), "lastnamebinding");
        bindingGroup.addBinding(binding);
        //bind role
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, userPanel.getRoleComboBox(), ELProperty.create("${selectedItem}"), newUser, BeanProperty.create("role"), "rolebinding");
        bindingGroup.addBinding(binding);
        //bind password
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, userPanel.getPasswordField(), ELProperty.create("${text}"), newUser, BeanProperty.create("password"), "passwordbinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //add actionlisteners
        //"create user" action
        userPanel.getCreateUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if user validation was successful, save the new user to the db
                if (cellMissyController.validateUser()) {
                    try {
                        userService.save(newUser);
                        userBindingList.add(newUser);
                        resetCreateUserTextFields();
                        cellMissyController.showMessage("User inserted!", "user inserted into DB", JOptionPane.INFORMATION_MESSAGE);
                    } // handle ConstraintViolationException(UniqueConstraint)
                    catch (PersistenceException ex) {
                        LOG.error(ex.getMessage());
                        String message = "User already present in the db";
                        cellMissyController.showMessage(message, "Error in persisting user", JOptionPane.INFORMATION_MESSAGE);
                        resetCreateUserTextFields();
                    }
                } else {
                    resetCreateUserTextFields();
                }
            }
        });

        //"search user" action
        userPanel.getSearchUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!userPanel.getSearchUserFirstNameTextField().getText().isEmpty() && !userPanel.getSearchUserLastNameTextField().getText().isEmpty()) {
                    User user = userService.findByFullName(userPanel.getSearchUserFirstNameTextField().getText(), userPanel.getSearchUserLastNameTextField().getText());
                    if (user != null) {
                        String message = "User: " + user.getFirstName() + " " + user.getLastName() + ", email: " + user.getEmail() + " was found in the database.";
                        cellMissyController.showMessage(message, "User found", JOptionPane.INFORMATION_MESSAGE);
                        userPanel.getSearchUserFirstNameTextField().setText("");
                        userPanel.getSearchUserLastNameTextField().setText("");
                    } else {
                        String message = "No user found";
                        cellMissyController.showMessage(message, "No user found", JOptionPane.INFORMATION_MESSAGE);
                        userPanel.getSearchUserFirstNameTextField().setText("");
                        userPanel.getSearchUserLastNameTextField().setText("");
                    }
                } else {
                    if (!userPanel.getSearchUserFirstNameTextField().getText().isEmpty() && userPanel.getSearchUserLastNameTextField().getText().isEmpty()) {
                        User user = userService.findByFirstName(userPanel.getSearchUserFirstNameTextField().getText());
                        if (user != null) {
                            String message = "User: " + user.getFirstName() + " " + user.getLastName() + ", email: " + user.getEmail() + " was found in the database.";
                            cellMissyController.showMessage(message, "User found", JOptionPane.INFORMATION_MESSAGE);
                            userPanel.getSearchUserFirstNameTextField().setText("");
                        } else {
                            String message = "No user found";
                            cellMissyController.showMessage(message, "No user found", JOptionPane.INFORMATION_MESSAGE);
                            userPanel.getSearchUserFirstNameTextField().setText("");
                        }
                    } else {
                        if (userPanel.getSearchUserFirstNameTextField().getText().isEmpty() && !userPanel.getSearchUserLastNameTextField().getText().isEmpty()) {
                            User user = userService.findByLastName(userPanel.getSearchUserLastNameTextField().getText());
                            if (user != null) {
                                String message = "User: " + user.getFirstName() + " " + user.getLastName() + ", email: " + user.getEmail() + " was found in the database.";
                                cellMissyController.showMessage(message, "User found", JOptionPane.INFORMATION_MESSAGE);
                                userPanel.getSearchUserLastNameTextField().setText("");
                            } else {
                                String message = "No user found";
                                cellMissyController.showMessage(message, "No user found", JOptionPane.INFORMATION_MESSAGE);
                                userPanel.getSearchUserLastNameTextField().setText("");
                            }
                        } else {
                            String message = "Please fill in first and/or last name";
                            cellMissyController.showMessage(message, "Error in searching", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        //"delete user" action
        userPanel.getDeleteUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userPanel.getUserJList().getSelectedValue() != null) {
                    userService.delete((User) userPanel.getUserJList().getSelectedValue());
                    userBindingList.remove((User) userPanel.getUserJList().getSelectedValue());
                } else {
                    String message = "Please select a user to delete";
                    cellMissyController.showMessage(message, "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //add items to the "role" ComboBox
        for (Role role : Role.values()) {
            userPanel.getRoleComboBox().addItem(role);
        }
        //show standard user
        userPanel.getRoleComboBox().setSelectedIndex(1);

        cellMissyController.getCellMissyFrame().getUserParentPanel().add(userPanel, gridBagConstraints);
    }

    /**
     * reset text fields of panel
     */
    private void resetCreateUserTextFields() {
        // reset create user text fields
        userPanel.getCreateUserFirstNameTextField().setText("");
        userPanel.getCreateUserLastNameTextField().setText("");
        userPanel.getCreateUserEmailTextField().setText("");
        userPanel.getPasswordField().setText("");
    }
}
