/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.plate.CellMiaImagedPlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("genericImagedPlatePanel")
public class GenericImagedPlateController {

    //model
    private ObservableList<Algorithm> algorithmsBindingList;
    private ObservableList<ImagingType> imagingTypesBindingList;
    private Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> algoMap;
    private BindingGroup bindingGroup;
    //view
    private CellMiaImagedPlatePanel cellMiaImagedPlatePanel;
    // parent controller
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    //services
    @Autowired
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        bindingGroup = new BindingGroup();
        cellMiaImagedPlatePanel = new CellMiaImagedPlatePanel();
        initLoadDataPlatePanel();
        initAlgoImagingPanel();
    }

    public CellMiaImagedPlatePanel getCellMiaImagedPlatePanel() {
        return cellMiaImagedPlatePanel;
    }

    private void initAlgoImagingPanel() {
        // new map
        algoMap = new HashMap<>();        
        
        // init algo list
        algorithmsBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, algorithmsBindingList, loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getAlgoList());
        bindingGroup.addBinding(jListBinding);
        // init imaging type list
        imagingTypesBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, imagingTypesBindingList, loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getImagingList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        /**
         * Add action Listeners
         */
        // add a new algorithm
        loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getAddAlgoButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String algoName = loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getAlgoTextField().getText();
                if (!algoName.isEmpty()) {
                    Algorithm newAlgorithm = new Algorithm();
                    newAlgorithm.setAlgorithmName(algoName);
                    algorithmsBindingList.add(newAlgorithm);
                    loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getAlgoTextField().setText("");
                } else {
                    loadExperimentFromGenericInputController.showMessage("Please insert a name for the algorithm.", JOptionPane.INFORMATION_MESSAGE);
                    loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getAlgoTextField().requestFocusInWindow();
                }
            }
        });
        
        // add a new imaging type
        loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getAddImagingButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String imagingName = loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getImagingTextField().getText();
                if(!imagingName.isEmpty()){
                    ImagingType newImagingType = new ImagingType();
                    newImagingType.setName(imagingName);
                    // exposure time and light intensity are not set
                    imagingTypesBindingList.add(newImagingType);
                    loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getImagingTextField().setText("");
                } else {
                    loadExperimentFromGenericInputController.showMessage("Please insert a name for the imaging type.", JOptionPane.INFORMATION_MESSAGE);
                    loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getImagingTextField().requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Init plate view
     */
    private void initLoadDataPlatePanel() {
        //show as default a 96 plate format
        Dimension parentDimension = loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getLoadDataPlateParentPanel().getSize();
        cellMiaImagedPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getLoadDataPlateParentPanel().add(cellMiaImagedPlatePanel, gridBagConstraints);
        loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel().getLoadDataPlateParentPanel().repaint();
    }
}
