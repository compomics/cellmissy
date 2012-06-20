/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.plate.LoadDataPlatePanel;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.GridBagConstraints;
import org.jdesktop.beansbinding.BindingGroup;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadDataPlatePanelController {

    //model
    private BindingGroup bindingGroup;
    //view
    private LoadDataPlatePanel loadDataPlatePanel;
    //parent controller
    private LoadExperimentPanelController loadExperimentPanelController;
    //child controllers
    //services
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
        
        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initLoadDataPlatePanel();
    }

    private void initLoadDataPlatePanel() {

        //init load data plate panel and add it to the bottom panel of the plate panel gui
        loadDataPlatePanel = new LoadDataPlatePanel();

      
    }
}
