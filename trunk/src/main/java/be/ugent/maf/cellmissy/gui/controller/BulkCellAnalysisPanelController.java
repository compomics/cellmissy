/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.BulkCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola Masuzzo
 */
public class BulkCellAnalysisPanelController {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepBindingList;
    //view
    private BulkCellAnalysisPanel bulkCellAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    //parent controller
    private DataAnalysisPanelController dataAnalysisPanelController;
    //child controllers
    //services
    private ApplicationContext context;
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    /**
     * constructor (parent controller)
     * @param dataAnalysisPanelController 
     */
    public BulkCellAnalysisPanelController(DataAnalysisPanelController dataAnalysisPanelController) {
        this.dataAnalysisPanelController = dataAnalysisPanelController;

        //init views
        analysisPlatePanel = new AnalysisPlatePanel();
        bulkCellAnalysisPanel = new BulkCellAnalysisPanel();

        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        plateService = (PlateService) context.getBean("plateService");
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        initPlatePanel();
        initBulkCellAnalysisPanel();
        initPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public BulkCellAnalysisPanel getBulkCellAnalysisPanel() {
        return bulkCellAnalysisPanel;
    }

    public AnalysisPlatePanel getAnalysisPlatePanel() {
        return analysisPlatePanel;
    }

    public ObservableList<TimeStep> getTimeStepBindingList() {
        return timeStepBindingList;
    }

    /**
     * private methods and classes
     */
    private void initPlatePanel() {
        //show as default a 96 plate format
        Dimension parentDimension = bulkCellAnalysisPanel.getAnalysisPlateParentPanel().getSize();

        analysisPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        bulkCellAnalysisPanel.getAnalysisPlateParentPanel().add(analysisPlatePanel, gridBagConstraints);
        bulkCellAnalysisPanel.getAnalysisPlateParentPanel().repaint();
    }

    private void initBulkCellAnalysisPanel() {
        
        //init timeStepsBindingList
        timeStepBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
    }

    private void initPanel() {

        //add bulk cell analysis panel to the parent panel
        dataAnalysisPanelController.getDataAnalysisPanel().getBulkCellAnalysisParentPanel().add(bulkCellAnalysisPanel, gridBagConstraints);
    }

    /**
     * public methods and classes
     */
    public void showTimeSteps() {
        //table binding
        JTableBinding timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepBindingList, bulkCellAnalysisPanel.getTimeStepsTable());
        //add column bindings
        
        ColumnBinding columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.columnNumber}"));
        columnBinding.setColumnName("Column");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);
        
        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.rowNumber}"));
        columnBinding.setColumnName("Row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);
        
        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${timeStepSequence}"));
        columnBinding.setColumnName("Sequence");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${area}"));
        columnBinding.setColumnName("Area");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidX}"));
        columnBinding.setColumnName("Centroid_x");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidY}"));
        columnBinding.setColumnName("Centroid_y");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${eccentricity}"));
        columnBinding.setColumnName("Eccentricity");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${majorAxis}"));
        columnBinding.setColumnName("Major Axis");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${minorAxis}"));
        columnBinding.setColumnName("Minor Axis");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        
        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();

    }
}
