/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic.area;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.gui.controller.load.generic.GenericImagedPlateController;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadFromGenericInputPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.parser.GenericInputFileParser;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * A controller to implements logic for loading generic area.
 *
 * @author Paola
 */
@Controller("genericAreaImagedPlateController")
public class GenericAreaImagedPlateController {

    private static final Logger LOG = Logger.getLogger(GenericAreaImagedPlateController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepsBindingList;
    private JTableBinding timeStepsTableBinding;
    // view 
    // parent controller
    @Autowired
    private GenericImagedPlateController genericImagedPlateController;
    // services
    @Autowired
    private GenericInputFileParser genericInputFileParser;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //init timeStepsBindingList
        timeStepsBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
    }

    public JTableBinding getTimeStepsTableBinding() {
        return timeStepsTableBinding;
    }

    public ObservableList<TimeStep> getTimeStepsBindingList() {
        return timeStepsBindingList;
    }

    /**
     * Reload data already parsed for a selected well
     *
     * @param selectedWellGui
     */
    public void reloadData(WellGui selectedWellGui) {
        // empty the list
        timeStepsBindingList.clear();
        List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
            for (TimeStep timeStep : wellHasImagingType.getTimeStepList()) {
                timeStepsBindingList.add(timeStep);
            }
        }
    }

    /**
     * Show Area values in table
     */
    public void showRawDataInTable() {
        LoadFromGenericInputPlatePanel loadFromGenericInputPlatePanel = genericImagedPlateController.getLoadFromGenericInputPlatePanel();
        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepsBindingList, loadFromGenericInputPlatePanel.getRawDataTable());
        //add column bindings
        JTableBinding.ColumnBinding columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.columnNumber}"));
        columnBinding.setColumnName("Column");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.rowNumber}"));
        columnBinding.setColumnName("Row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.algorithm.algorithmName}"));
        columnBinding.setColumnName("Dataset");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(String.class);
        columnBinding.setRenderer(new AlignedTableRenderer(SwingConstants.RIGHT));

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.imagingType.name}"));
        columnBinding.setColumnName("Imaging type");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(String.class);
        columnBinding.setRenderer(new AlignedTableRenderer(SwingConstants.RIGHT));

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${timeStepSequence}"));
        columnBinding.setColumnName("Time sequence");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${area}"));
        columnBinding.setColumnName("Area");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(genericImagedPlateController.getFormat(), SwingConstants.RIGHT));

        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();
    }

    /**
     * This is parsing a certain file, for a certain selected well and a
     * wellHasImagingType (i.e. dataset and imaging type are chosen)
     *
     * @param bulkCellFile
     * @param newWellHasImagingType
     * @param selectedWellGui
     */
    public void loadData(File bulkCellFile, WellHasImagingType newWellHasImagingType, WellGui selectedWellGui) {
        List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
        // parse raw data for selected well
        try {
            List<TimeStep> timeSteps = genericInputFileParser.parseBulkCellFile(bulkCellFile);
            // set the timeStepList and add the wellHasImagingType to the list
            newWellHasImagingType.setTimeStepList(timeSteps);
            wellHasImagingTypeList.add(newWellHasImagingType);
            for (TimeStep timeStep : timeSteps) {
                timeStep.setWellHasImagingType(newWellHasImagingType);
            }
            // if the list is not empty and does not contain the selected well, clear it before adding the new timeSteps
            if (!timeStepsBindingList.isEmpty() && !containsWell(selectedWellGui)) {
                timeStepsBindingList.clear();
            }
            timeStepsBindingList.addAll(timeSteps);
        } catch (FileParserException ex) {
            LOG.error(ex.getMessage());
            genericImagedPlateController.showMessage(ex.getMessage(), "Generic input file error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * remove timeSteps from List: this is called when the user wants to
     * overwrite data or to clear data.
     *
     * @param wellHasImagingTypeToOverwrite
     * @return
     */
    public List<WellHasImagingType> removeOldDataFromList(WellHasImagingType wellHasImagingTypeToOverwrite) {
        List<WellHasImagingType> list = new ArrayList<>();
        Iterator<TimeStep> iterator = timeStepsBindingList.iterator();
        while (iterator.hasNext()) {
            WellHasImagingType wellHasImagingType = iterator.next().getWellHasImagingType();
            if (wellHasImagingType.equals(wellHasImagingTypeToOverwrite)) {
                list.add(wellHasImagingType);
                iterator.remove();
            }
        }
        return list;
    }

    /**
     * Check if the TimeStepsList contains the selected well
     *
     * @param selectedWellGui
     * @return
     */
    private boolean containsWell(WellGui selectedWellGui) {
        boolean containsWell = false;
        Well selectedWell = selectedWellGui.getWell();
        for (TimeStep timeStep : timeStepsBindingList) {
            if (timeStep.getWellHasImagingType().getWell().equals(selectedWell)) {
                containsWell = true;
                break;
            }
        }
        return containsWell;
    }
}
