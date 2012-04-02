/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.ConditionsSetupPanel;
import be.ugent.maf.cellmissy.service.CellLineService;
import be.ugent.maf.cellmissy.service.MatrixDimensionService;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class ConditionsSetupPanelController {

    //model
    private ObservableList<CellLine> cellLineBindingList;
    private ObservableList<MatrixDimension> matrixDimensionBindingList;
    private BindingGroup bindingGroup;
    //view
    private ConditionsSetupPanel conditionsSetupPanel;
    //parent controller
    private ExperimentSetupPanelController experimentSetupPanelController;
    //services
    private CellLineService cellLineService;
    private MatrixDimensionService matrixDimensionService;
    
    private GridBagConstraints gridBagConstraints;

    public ConditionsSetupPanelController(ExperimentSetupPanelController experimentSetupPanelController) {
        this.experimentSetupPanelController = experimentSetupPanelController;
        conditionsSetupPanel = new ConditionsSetupPanel();

        cellLineService = (CellLineService) experimentSetupPanelController.getCellMissyController().getBeanByName("cellLineService");
        matrixDimensionService = (MatrixDimensionService) experimentSetupPanelController.getCellMissyController().getBeanByName("matrixDimensionService");
        
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initCellLinePanel();
        initEcmPanel();
        initPanel();
    }

    private void initCellLinePanel() {

        //init cellLineJCombo
        cellLineBindingList = ObservableCollections.observableList(cellLineService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, cellLineBindingList, conditionsSetupPanel.getCellLineComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        //add action listeners
        conditionsSetupPanel.getInsertCellLineButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!conditionsSetupPanel.getCellLineNameTextField().getText().isEmpty()) {
                    CellLine newCellLine = new CellLine();
                    newCellLine.setName(conditionsSetupPanel.getCellLineNameTextField().getText());
                    //insert cell line to DB
                    cellLineService.save(newCellLine);
                    //add the new cell line to the list
                    cellLineBindingList.add(newCellLine);
                }
            }
        });
    }
    
    private void initEcmPanel(){
        //init matrixDimensionJCombo
        matrixDimensionBindingList = ObservableCollections.observableList(matrixDimensionService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, matrixDimensionBindingList, conditionsSetupPanel.getMatrixDimensionComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
    }

    private void initPanel() {
        experimentSetupPanelController.experimentSetupPanel.getConditionsSetupParentPanel().add(conditionsSetupPanel, gridBagConstraints);

    }
}
