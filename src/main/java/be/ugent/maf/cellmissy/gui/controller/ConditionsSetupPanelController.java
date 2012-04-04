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
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
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

    public ConditionsSetupPanel getConditionsSetupPanel() {
        return conditionsSetupPanel;
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
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, matrixDimensionBindingList, conditionsSetupPanel.getEcmDimensionComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        //add action listener
        //show different assay-ecm, 2D-3D panels
        conditionsSetupPanel.getEcmDimensionComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (((MatrixDimension)(conditionsSetupPanel.getEcmDimensionComboBox().getSelectedItem())).getMatrixDimension().equals("2D")) {
                    switchChildPanels(assayEcmPanelController.getAssayEcm2DPanel(), assayEcmPanelController.getAssayEcm3DPanel());
                } else {
                    switchChildPanels(assayEcmPanelController.getAssayEcm3DPanel(), assayEcmPanelController.getAssayEcm2DPanel());
                }
                conditionsSetupPanel.getAssayEcmParentPanel().revalidate();
                conditionsSetupPanel.getAssayEcmParentPanel().repaint();
            }
        });
        conditionsSetupPanel.getEcmDimensionComboBox().setSelectedIndex(0);

    }

    private void initPanel() {
        experimentSetupPanelController.getExperimentSetupPanel().getConditionsSetupParentPanel().add(conditionsSetupPanel, gridBagConstraints);
    }
    
    private void switchChildPanels(JPanel panelToAdd, JPanel panelToRemove) {
        if (!GuiUtils.containsComponent(conditionsSetupPanel.getAssayEcmParentPanel(), panelToAdd)) {
            conditionsSetupPanel.getAssayEcmParentPanel().add(panelToAdd, gridBagConstraints);
        }
        if (GuiUtils.containsComponent(conditionsSetupPanel.getAssayEcmParentPanel(), panelToRemove)) {
            conditionsSetupPanel.getAssayEcmParentPanel().remove(panelToRemove);
        }

    }
}
