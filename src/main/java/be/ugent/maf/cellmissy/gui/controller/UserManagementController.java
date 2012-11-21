/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.ValidationUtils;
import be.ugent.maf.cellmissy.gui.user.UserPanel;
import be.ugent.maf.cellmissy.service.UserService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.persistence.PersistenceException;
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
 * User Panel Controller: user management (create, search and delete users from DB)
 * Parent Controller: CellMissy Controller (main controller)
 * @author Paola
 */
@Controller("userManagementController")
public class UserManagementController {

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

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //create a new user panel and init view
        userPanel = new UserPanel();
        initUserPanel();
    }

    /**
     * getters and setters 
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
     * @return List of Messages to show to the user
     */
    public List<String> validateUser() {
        return ValidationUtils.validateObject(newUser);
    }

    /**
     * initialize User Panel
     */
    private void initUserPanel() {
        //init userJList
        userBindingList = ObservableCollections.observableList(userService.findAll());
        JListBinding userListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, userBindingList, userPanel.getUserJList());
        bindingGroup.addBinding(userListBinding);

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
                        User savedUser = userService.save(newUser);
                        userBindingList.add(savedUser);
                        resetCreateUserTextFields();
                    } // handle ConstraintViolationException(UniqueConstraint)
                    catch (PersistenceException persistenceException) {
                        String message = "User already present in the db";
                        cellMissyController.showMessage(message, 0);
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
                        cellMissyController.showMessage(message, 1);
                        userPanel.getSearchUserFirstNameTextField().setText("");
                        userPanel.getSearchUserLastNameTextField().setText("");
                    } else {
                        String message = "No user found";
                        cellMissyController.showMessage(message, 1);
                        userPanel.getSearchUserFirstNameTextField().setText("");
                        userPanel.getSearchUserLastNameTextField().setText("");
                    }
                } else {
                    if (!userPanel.getSearchUserFirstNameTextField().getText().isEmpty() && userPanel.getSearchUserLastNameTextField().getText().isEmpty()) {
                        User user = userService.findByFirstName(userPanel.getSearchUserFirstNameTextField().getText());
                        if (user != null) {
                            String message = "User: " + user.getFirstName() + " " + user.getLastName() + ", email: " + user.getEmail() + " was found in the database.";
                            cellMissyController.showMessage(message, 1);
                            userPanel.getSearchUserFirstNameTextField().setText("");
                        } else {
                            String message = "No user found";
                            cellMissyController.showMessage(message, 1);
                            userPanel.getSearchUserFirstNameTextField().setText("");
                        }
                    } else {
                        if (userPanel.getSearchUserFirstNameTextField().getText().isEmpty() && !userPanel.getSearchUserLastNameTextField().getText().isEmpty()) {
                            User user = userService.findByLastName(userPanel.getSearchUserLastNameTextField().getText());
                            if (user != null) {
                                String message = "User: " + user.getFirstName() + " " + user.getLastName() + ", email: " + user.getEmail() + " was found in the database.";
                                cellMissyController.showMessage(message, 1);
                                userPanel.getSearchUserLastNameTextField().setText("");
                            } else {
                                String message = "No user found";
                                cellMissyController.showMessage(message, 1);
                                userPanel.getSearchUserLastNameTextField().setText("");
                            }
                        } else {
                            String message = "Please fill in first and/or last name";
                            cellMissyController.showMessage(message, 1);
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
                    userPanel.getDeleteUserFirstNameTextField().setText("");
                    userPanel.getDeleteUserLastNameTextField().setText("");
                    userPanel.getDeleteUserEmailTextField().setText("");
                } else {
                    String message = "Please select a user to delete";
                    cellMissyController.showMessage(message, 1);
                }
            }
        });

        //add items to the "role" ComboBox
        for (Role role : Role.values()) {
            userPanel.getRoleComboBox().addItem(role);
        }
        //show standard user
        userPanel.getRoleComboBox().setSelectedIndex(1);
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
