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
import be.ugent.maf.cellmissy.service.EcmService;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    //child controllers
    private AssayEcmPanelController assayEcmPanelController;
    //services
    private CellLineService cellLineService;
    private EcmService ecmService;
    private GridBagConstraints gridBagConstraints;

    public ConditionsSetupPanelController(ExperimentSetupPanelController experimentSetupPanelController) {
        this.experimentSetupPanelController = experimentSetupPanelController;

        //init child controllers
        assayEcmPanelController = new AssayEcmPanelController(this);

        //init services
        cellLineService = (CellLineService) experimentSetupPanelController.getCellMissyController().getBeanByName("cellLineService");
        ecmService = (EcmService) experimentSetupPanelController.getCellMissyController().getBeanByName("ecmService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        conditionsSetupPanel = new ConditionsSetupPanel();
        initCellLinePanel();
        initEcmPanel();
        initPanel();
    }

    public ExperimentSetupPanelController getExperimentSetupPanelController() {
        return experimentSetupPanelController;
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

    private void initEcmPanel() {
        //init matrixDimensionJCombo
        matrixDimensionBindingList = ObservableCollections.observableList(ecmService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, matrixDimensionBindingList, conditionsSetupPanel.getMatrixDimensionComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        //add action listener
        //show different assay-ecm, 2D-3D panels
        conditionsSetupPanel.getMatrixDimensionComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (conditionsSetupPanel.getMatrixDimensionComboBox().getSelectedItem().equals(ecmService.findByDimension("2D"))) {
                    showEcm2DPanel();
                } else {
                    showEcm3DPanel();
                }
            }
        });
        conditionsSetupPanel.getMatrixDimensionComboBox().setSelectedIndex(0);

    }

    private void initPanel() {
        experimentSetupPanelController.getExperimentSetupPanel().getConditionsSetupParentPanel().add(conditionsSetupPanel, gridBagConstraints);
    }

    private void showEcm2DPanel() {
        if (!GuiUtils.containsComponent(conditionsSetupPanel, assayEcmPanelController.getAssayEcm2DPanel()) && !GuiUtils.containsComponent(conditionsSetupPanel, assayEcmPanelController.getAssayEcm3DPanel())) {
            conditionsSetupPanel.getAssayEcmParentPanel().add(assayEcmPanelController.getAssayEcm2DPanel(), gridBagConstraints);
        } else {
            conditionsSetupPanel.getAssayEcmParentPanel().remove(assayEcmPanelController.getAssayEcm2DPanel());
            conditionsSetupPanel.getAssayEcmParentPanel().add(assayEcmPanelController.getAssayEcm2DPanel(), gridBagConstraints);
        }

    }

    private void showEcm3DPanel() {
        if (!GuiUtils.containsComponent(conditionsSetupPanel, assayEcmPanelController.getAssayEcm2DPanel()) && !GuiUtils.containsComponent(conditionsSetupPanel, assayEcmPanelController.getAssayEcm3DPanel())) {
            conditionsSetupPanel.getAssayEcmParentPanel().add(assayEcmPanelController.getAssayEcm3DPanel(), gridBagConstraints);
        }
        else {
            conditionsSetupPanel.getAssayEcmParentPanel().remove(assayEcmPanelController.getAssayEcm2DPanel());
            conditionsSetupPanel.getAssayEcmParentPanel().add(assayEcmPanelController.getAssayEcm3DPanel(), gridBagConstraints);
        }

    }
}
