/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.PlatePanelGui;
import be.ugent.maf.cellmissy.gui.plate.LoadDataPlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadDataPlatePanelController {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<PlateFormat> plateFormatBindingList;
    //view
    private LoadDataPlatePanel loadDataPlatePanel;
    private PlatePanelGui platePanelGui;
    //parent controller
    private LoadExperimentPanelController loadExperimentPanelController;
    //child controllers
    //services
    private PlateService plateService;
    private ApplicationContext context;
    private GridBagConstraints gridBagConstraints;

    /**
     * constructor (parent Controller)
     * @param loadExperimentPanelController 
     */
    public LoadDataPlatePanelController(LoadExperimentPanelController loadExperimentPanelController) {

        this.loadExperimentPanelController = loadExperimentPanelController;
        //init views
        loadDataPlatePanel = new LoadDataPlatePanel();
        platePanelGui = new PlatePanelGui();
        
        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        plateService = (PlateService) context.getBean("plateService");
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initLoadDataPlatePanel();
    }

    private void initLoadDataPlatePanel() {

        //init set up plate panel and add it to the bottom panel of the set up plate panel gui
        loadDataPlatePanel = new LoadDataPlatePanel();
        platePanelGui.getBottomPanel().add(loadDataPlatePanel, gridBagConstraints);

        //init plateFormatJcombo
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, plateFormatBindingList, platePanelGui.getPlateFormatComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        /**
         * add action listeners
         */
        //plate format combo box
        platePanelGui.getPlateFormatComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateFormat selectedPlateFormat = plateFormatBindingList.get(platePanelGui.getPlateFormatComboBox().getSelectedIndex());
                Dimension parentDimension = platePanelGui.getBottomPanel().getSize();
                loadDataPlatePanel.initPanel(selectedPlateFormat, parentDimension);
                loadDataPlatePanel.repaint();
            }
        });
    }
}
