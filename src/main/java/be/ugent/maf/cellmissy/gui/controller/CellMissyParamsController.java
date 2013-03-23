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
import java.util.ArrayList;
import java.util.Iterator;
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
import org.springframework.context.ConfigurableApplicationContext;
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
                try {
                    PropertiesConfigurationHolder.getInstance().save();
                    ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) ApplicationContextProvider.getInstance().getApplicationContext();
                    configurableApplicationContext.close();
                    configurableApplicationContext.refresh();
                    configurableApplicationContext.start();
                } catch (ConfigurationException ex) {
                    LOG.error(ex.getMessage());
                }
            }
        });
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
