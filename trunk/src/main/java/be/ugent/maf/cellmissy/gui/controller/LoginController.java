/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.bean.AuthenticationBean;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.LoginDialog;
import be.ugent.maf.cellmissy.service.UserService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Login controller
 *
 * @author Paola Masuzzo
 */
@Controller("loginController")
public class LoginController {

    private static final Logger LOG = Logger.getLogger(LoginController.class);
    // model
    @Autowired
    private AuthenticationBean authenticationBean;
    // view
    private LoginDialog loginDialog;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    // child controller
    @Autowired
    private CellMissyParamsController cellMissyParamsController;
    // services
    @Autowired
    private UserService userService;

    /**
     * getters and setters
     *
     * @return
     */
    public LoginDialog getLoginDialog() {
        return loginDialog;
    }

    /**
     * Initialize controller
     */
    public void init() {
        //init login view
        loginDialog = new LoginDialog(cellMissyController.getCellMissyFrame(), true);
        // init child controller
        cellMissyParamsController.init();
        initLoginPanel();
    }

    /**
     * Get current user from the authentication bean
     *
     * @return
     */
    public User getCurrentUser() {
        return authenticationBean.getCurrentUser();
    }

    /*
     * Get main frame from parent controller
     */
    public CellMissyFrame getCellMissyFrame() {
        return cellMissyController.getCellMissyFrame();
    }

    /**
     * Initialize main view
     */
    private void initLoginPanel() {

        // login button: validate user credentials and attempt the login
        loginDialog.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(loginDialog.getUserNameTextField().getText().isEmpty() && loginDialog.getPasswordTextField().getPassword().length == 0)) {
                    onLogin();
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Please provide an user name and a password", "login validation fail", JOptionPane.WARNING_MESSAGE);
                    loginDialog.getUserNameTextField().requestFocus();
                }
            }
        });

        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        // edit button: view CellMissy properties and edit them
        loginDialog.getEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellMissyParamsController.getCellMissyConfigDialog().pack();
                cellMissyParamsController.getCellMissyConfigDialog().setVisible(true);
            }
        });

        //show login dialog
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);
    }

    /**
     * On login
     */
    private void onLogin() {
        //check if a user with given user name and password is found in the db    
        LOG.info("Login attempt with user name: " + loginDialog.getUserNameTextField().getText());
        User currentUser = userService.findByLoginCredentials(loginDialog.getUserNameTextField().getText(), String.valueOf(loginDialog.getPasswordTextField().getPassword()));
        if (currentUser != null) {
            LOG.info("User " + loginDialog.getUserNameTextField().getText() + " successfully logged in.");
            loginDialog.setVisible(false);

            //check if the current user has Role.ADMIN.
            //If so, init the admin section
            if (currentUser.getRole().equals(Role.ADMIN_USER)) {
                cellMissyController.initAdminSection();
            } else {
                cellMissyController.disableAdminSection();
            }
            //set current user in authentication bean    
            authenticationBean.setCurrentUser(currentUser);
            cellMissyController.enterTheApplication();
        } else {
            LOG.error("Login validation failed");
            JOptionPane.showMessageDialog(loginDialog, "No user with the given credentials could be found, please try again.", "login fail", JOptionPane.ERROR_MESSAGE);
            loginDialog.getUserNameTextField().setText("");
            loginDialog.getPasswordTextField().setText("");
            loginDialog.getUserNameTextField().requestFocus();
        }
    }
}
