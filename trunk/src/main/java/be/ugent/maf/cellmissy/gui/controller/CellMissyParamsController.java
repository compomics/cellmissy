/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.gui.CellMissyConfigDialog;
import be.ugent.maf.cellmissy.gui.PropertyGuiWrapper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("cellMissyParamsController")
public class CellMissyParamsController {

    private static final Logger LOG = Logger.getLogger(CellMissyParamsController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<PropertyGuiWrapper> propertyGuiWrapperBindingList;
    // view
    private CellMissyConfigDialog cellMissyConfigDialog;
    // parent controller
    @Autowired
    private LoginController loginController;
    // services

    /**
     * getters and setters
     *
     * @return
     */
    public CellMissyConfigDialog getCellMissyConfigDialog() {
        return cellMissyConfigDialog;
    }

    public void setCellMissyConfigDialog(CellMissyConfigDialog cellMissyConfigDialog) {
        this.cellMissyConfigDialog = cellMissyConfigDialog;
    }

    /**
     * Initialize controller
     */
    public void init() {
        cellMissyConfigDialog = new CellMissyConfigDialog(loginController.getCellMissyFrame(), true);
        //init bindings
        bindingGroup = new BindingGroup();
        //table binding 
        propertyGuiWrapperBindingList = ObservableCollections.observableList(new ArrayList<PropertyGuiWrapper>());
        initPropertyGuiWrappersBindingList();

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
        cellMissyConfigDialog.getSaveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  check that properties are not left blank first
                if (isValid()) {
                    try {
                        // save new properties
                        PropertiesConfigurationHolder.getInstance().save();
                        JOptionPane.showMessageDialog(loginController.getLoginDialog(), "New properties have been saved.\nYou will now exit the application.\nPlease restart CellMissy in order to use the new settings.", "new properties saved", JOptionPane.INFORMATION_MESSAGE);
                        // exit the application
                        System.exit(0);
                    } catch (ConfigurationException ex) {
                        LOG.error(ex.getMessage());
                        JOptionPane.showMessageDialog(loginController.getLoginDialog(), "New properties could not be saved to file." + "\n" + "Please check if a \"cell_missy.properties\" file exists.", "properties could not be saved", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // inform the user that every param needs to be set
                    JOptionPane.showMessageDialog(loginController.getLoginDialog(), "Please do not leave any property blank.", "error in setting new properties", JOptionPane.WARNING_MESSAGE);
                }

            }
        });

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
                    JOptionPane.showMessageDialog(loginController.getLoginDialog(), "Properties have been reset.\nYou will now exit the application.\nPlease restart CellMissy in order to use the settings.", "new properties saved", JOptionPane.INFORMATION_MESSAGE);
                    // exit the application
                    System.exit(0);
                } catch (ConfigurationException | IOException ex) {
                    LOG.error(ex.getMessage());
                    JOptionPane.showMessageDialog(loginController.getLoginDialog(), "Default settings cannot be loaded." + "\n" + "Please check if a \"cell_missy.properties\" file exists.", "properties could not be loaded", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    /**
     * This is checking that every parameter has a value and it is not left blank
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
