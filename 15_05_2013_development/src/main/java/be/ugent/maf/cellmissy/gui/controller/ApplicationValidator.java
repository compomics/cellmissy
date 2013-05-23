/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.gui.CellMissyConfigDialog;
import be.ugent.maf.cellmissy.gui.PropertyGuiWrapper;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.persistence.PersistenceException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.SQLGrammarException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.TransactionException;

/**
 * This class is not a bean, it is used if errors are encountered before the
 * login is enabled.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class ApplicationValidator {

    private static final Logger LOG = Logger.getLogger(ApplicationValidator.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<PropertyGuiWrapper> propertyGuiWrapperBindingList;
    // view
    private CellMissyConfigDialog cellMissyConfigDialog;
    private JFrame mainFrame;

    /**
     * Initialize
     */
    public void init() {
        // init views
        mainFrame = new JFrame();
        cellMissyConfigDialog = new CellMissyConfigDialog(mainFrame, false);

        // closing the configuration dialog causes the application to shut downs
        cellMissyConfigDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        //init bindings
        bindingGroup = new BindingGroup();
        //table binding 
        propertyGuiWrapperBindingList = ObservableCollections.observableList(new ArrayList<PropertyGuiWrapper>());
        initPropertyGuiWrappersBindingList();
        // table binding for properties
        JTableBinding tableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE, propertyGuiWrapperBindingList, cellMissyConfigDialog.getParamsTable());

        //Add column bindings
        JTableBinding.ColumnBinding columnBinding = tableBinding.addColumnBinding(ELProperty.create("${key}"));
        columnBinding.setColumnName("Parameter");
        columnBinding.setEditable(Boolean.FALSE);
        columnBinding.setColumnClass(String.class);

        columnBinding = tableBinding.addColumnBinding(ELProperty.create("${value}"));
        columnBinding.setColumnName("Value");
        columnBinding.setEditable(true);
        columnBinding.setColumnClass(Object.class);

        bindingGroup.addBinding(tableBinding);
        bindingGroup.bind();

        /**
         * action listeners
         */
        // save properties
        cellMissyConfigDialog.getSaveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  check that properties are not left blank first
                if (isValid()) {
                    try {
                        // save new properties
                        PropertiesConfigurationHolder.getInstance().save();
                        if (PropertyGuiWrapper.isTransactionPropertyChanged()) {
                            showMessage("New DB properties have been saved.\nYou will now exit the application.\nPlease restart CellMissy in order to use the new settings.", "new properties saved", JOptionPane.INFORMATION_MESSAGE);
                            // exit the application
                            System.exit(0);
                        } else {
                            showMessage("New properties have been saved to file.", "new properties saved", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (ConfigurationException ex) {
                        LOG.error(ex.getMessage());
                        showMessage("New properties could not be saved to file." + "\n" + "Please check if a \"cell_missy.properties\" file exists.", "properties could not be saved", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // inform the user that every param needs to be set
                    showMessage("Please do not leave any property blank.", "error in setting new properties", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // reset properties back to default
        cellMissyConfigDialog.getResetButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //clear holder and reload default properties
                    PropertiesConfigurationHolder.getInstance().clear();
                    PropertiesConfigurationHolder.getInstance().load(new ClassPathResource("cell_missy.properties").getInputStream());
                    //reset binding list
                    propertyGuiWrapperBindingList.clear();
                    initPropertyGuiWrappersBindingList();
                    showMessage("Properties have been reset.\nYou will now exit the application.\nPlease restart CellMissy in order to use the settings.", "new properties saved", JOptionPane.INFORMATION_MESSAGE);
                    // exit the application
                    System.exit(0);
                } catch (ConfigurationException | IOException ex) {
                    LOG.error(ex.getMessage());
                    showMessage("Default settings cannot be loaded." + "\n" + "Please check if a \"cell_missy.properties\" file exists.", "properties could not be loaded", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    /**
     * Show a message dialog through main frame content pane
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        JOptionPane.showMessageDialog(mainFrame.getContentPane(), message, title, messageType);
    }

    /**
     * Handle exceptions when getting the application context
     */
    public void getApplicationContext() {
        try {
            // try to get the application context
            ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
            CellMissyController cellMissyController = (CellMissyController) context.getBean("cellMissyController");
            // init main controller, if application context was got properly
            cellMissyController.init();
            // catch TransactionException
        } catch (TransactionException ex) {
            String message = "";
            if (ex.getCause() != null && ex.getCause() instanceof PersistenceException) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause() instanceof GenericJDBCException) {
                    if (ex.getCause().getCause().getCause() != null && ex.getCause().getCause().getCause() instanceof SQLException) {
                        String causeMessage = ex.getCause().getCause().getCause().getMessage();
                        // check for access denied exception
                        // error with DB login credentials
                        if (causeMessage.contains("Access denied")) {
                            message = causeMessage + "\n" + "Access denied; please check your credentials to connect to DB." + "\n" + "You can edit CellMissy parameters now.";
                        }
                    }
                } else if (ex.getCause().getCause() != null && ex.getCause().getCause() instanceof JDBCConnectionException) {
                    if (ex.getCause().getCause().getCause().getCause() != null) {
                        // connection to DB failed
                        String causeMessage = ex.getCause().getCause().getCause().getCause().getMessage();
                        message = causeMessage + "\n" + "Connection to DB failed." + "\n" + "If you are using a not-local DB, make sure to have internet connection." + "\n" + "You can edit CellMissy properties now.";
                    }
                } else if (ex.getCause().getCause() != null && ex.getCause().getCause() instanceof SQLGrammarException) {
                    if (ex.getCause().getCause().getCause() != null) {
                        SQLGrammarException exception = (SQLGrammarException) ex.getCause().getCause();
                        SQLException sqlException = exception.getSQLException();
                        // SQl exception, check right DB schema and right credentials
                        String causeMessage = sqlException.getMessage();
                        message = causeMessage + "\n" + "Please make sure you are using the right credentials and the right DB schema name." + "\n" + "You can edit CellMissy properties now.";
                    }
                }
            }
            // warn the user that a problem occurred
            showMessage(message, "Error connecting to DB", JOptionPane.ERROR_MESSAGE);
            // allow the user to change cellmissy parameters now
            editCellMissyParams();
            LOG.error(ex.getMessage());
            // catch also generic JDBC exception: e.g. when a db name is not specified in the db.url property
        } catch (GenericJDBCException ex) {
            String message = "";
            if (ex.getCause() != null && ex.getCause() instanceof SQLException) {
                SQLException sqlException = (SQLException) ex.getCause();
                String causeMessage = sqlException.getMessage();
                message = causeMessage + "\n" + "Please make sure you specify a DB name in the db.url property." + "\n" + "You can edit CellMissy properties now.";
            }
            // warn the user that a problem occurred
            showMessage(message, "Error connecting to DB", JOptionPane.ERROR_MESSAGE);
            // allow the user to change cellmissy parameters now
            editCellMissyParams();
            LOG.error(ex.getMessage());
        }
    }

    /**
     * Show a dialog to edit parameters
     */
    private void editCellMissyParams() {
        // pack dialog
        cellMissyConfigDialog.pack();
        // show dialog
        cellMissyConfigDialog.setVisible(true);
    }

    /**
     * This is checking that every parameter has a value and it is not left
     * blank
     *
     * @return
     */
    private boolean isValid() {
        boolean isValid = true;
        for (int i = 0; i < cellMissyConfigDialog.getParamsTable().getRowCount(); i++) {
            String value = (String) cellMissyConfigDialog.getParamsTable().getValueAt(i, 1);
            if (value.isEmpty()) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    /**
     * Initialize list
     */
    private void initPropertyGuiWrappersBindingList() {
        Iterator<String> iterator = PropertiesConfigurationHolder.getInstance().getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            propertyGuiWrapperBindingList.add(new PropertyGuiWrapper(key, PropertiesConfigurationHolder.getInstance().getProperty(key)));
        }
    }
}
