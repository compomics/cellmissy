/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.BulkCellAnalysisPanel;
import java.awt.GridBagConstraints;
import org.jdesktop.beansbinding.BindingGroup;

/**
 *
 * @author Paola Masuzzo
 */
public class BulkCellAnalysisPanelController {

    //model
    private BindingGroup bindingGroup;
    //view
    private BulkCellAnalysisPanel bulkCellAnalysisPanel;
    //parent controller
    private DataAnalysisPanelController dataAnalysisPanelController;
    //child controllers
    //services
    private GridBagConstraints gridBagConstraints;

    /**
     * constructor (parent controller)
     * @param dataAnalysisPanelController 
     */
    public BulkCellAnalysisPanelController(DataAnalysisPanelController dataAnalysisPanelController) {
        this.dataAnalysisPanelController = dataAnalysisPanelController;

        //init views
        bulkCellAnalysisPanel = new BulkCellAnalysisPanel();

        //init services

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initBulkCellAnalysisPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public BulkCellAnalysisPanel getBulkCellAnalysisPanel() {
        return bulkCellAnalysisPanel;
    }

    /**
     * private methods and classes
     */
    private void initBulkCellAnalysisPanel() {

        //add bulk cell analysis panel to the parent panel
        dataAnalysisPanelController.getDataAnalysisPanel().getBulkCellAnalysisParentPanel().add(bulkCellAnalysisPanel, gridBagConstraints);
    }
    /**
     * public methods and classes
     */
}
