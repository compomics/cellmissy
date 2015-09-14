/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
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
 * A controller to implement logic for loading generic single cell data.
 *
 * @author Paola
 */
@Controller("genericSingleCellImagedPlateController")
public class GenericSingleCellImagedPlateController {

    private static final Logger LOG = Logger.getLogger(GenericSingleCellImagedPlateController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<TrackPoint> trackPointsBindingList;
    private JTableBinding trackPointsTableBinding;
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
        trackPointsBindingList = ObservableCollections.observableList(new ArrayList<TrackPoint>());

    }

    public JTableBinding getTrackPointsTableBinding() {
        return trackPointsTableBinding;
    }

    public ObservableList<TrackPoint> getTrackPointsBindingList() {
        return trackPointsBindingList;
    }

    /**
     * Show Area values in table
     */
    public void showRawDataInTable() {
        LoadFromGenericInputPlatePanel loadFromGenericInputPlatePanel = genericImagedPlateController.getLoadFromGenericInputPlatePanel();
        //table binding
        trackPointsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, trackPointsBindingList, loadFromGenericInputPlatePanel.getRawDataTable());
        //add column bindings

        JTableBinding.ColumnBinding columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track.wellHasImagingType.well.columnNumber}"));
        columnBinding.setColumnName("Column");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track.wellHasImagingType.well.rowNumber}"));
        columnBinding.setColumnName("Row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track.wellHasImagingType.algorithm.algorithmName}"));
        columnBinding.setColumnName("Algorithm");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(String.class);
        columnBinding.setRenderer(new AlignedTableRenderer(SwingConstants.RIGHT));

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track.wellHasImagingType.imagingType.name}"));
        columnBinding.setColumnName("Imaging type");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(String.class);
        columnBinding.setRenderer(new AlignedTableRenderer(SwingConstants.RIGHT));

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${track.trackNumber}"));
        columnBinding.setColumnName("Track number");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${timeIndex}"));
        columnBinding.setColumnName("Time index");
        columnBinding.setEditable(false);
        columnBinding.setRenderer(new FormatRenderer(genericImagedPlateController.getFormat(), SwingConstants.RIGHT));

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellRow}"));
        columnBinding.setColumnName("x");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(genericImagedPlateController.getFormat(), SwingConstants.RIGHT));

        columnBinding = trackPointsTableBinding.addColumnBinding(ELProperty.create("${cellCol}"));
        columnBinding.setColumnName("y");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(genericImagedPlateController.getFormat(), SwingConstants.RIGHT));

        bindingGroup.addBinding(trackPointsTableBinding);
        bindingGroup.bind();
    }

    /**
     * This is parsing a certain file, for a certain selected well and a
     * wellHasImagingType (i.e. dataset and imaging type are chosen)
     *
     * @param trackFile
     * @param newWellHasImagingType
     * @param selectedWellGui
     */
    public void loadData(File trackFile, WellHasImagingType newWellHasImagingType, WellGui selectedWellGui) {
        List<WellHasImagingType> wellHasImagingTypes = selectedWellGui.getWell().getWellHasImagingTypeList();
        // parse raw data for selected well
        try {
            List<Track> tracks = genericInputFileParser.parseTrackFile(trackFile);
            // set the track list and add the wellHasImagingType to the list
            newWellHasImagingType.setTrackList(tracks);
            wellHasImagingTypes.add(newWellHasImagingType);
            for (Track track : tracks) {
                track.setWellHasImagingType(newWellHasImagingType);
            }
            for (Track track : tracks) {
                trackPointsBindingList.addAll(track.getTrackPointList());
            }
        } catch (FileParserException ex) {
            LOG.error(ex.getMessage());
            genericImagedPlateController.showMessage(ex.getMessage(), "Generic input file error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remove timeSteps from List: this is called when the user wants to
     * overwrite data or to clear data.
     *
     * @param wellHasImagingTypeToOverwrite
     * @return
     */
    public List<WellHasImagingType> removeOldDataFromList(WellHasImagingType wellHasImagingTypeToOverwrite) {
        List<WellHasImagingType> list = new ArrayList<>();
        Iterator<TrackPoint> iterator = trackPointsBindingList.iterator();
        while (iterator.hasNext()) {
            WellHasImagingType wellHasImagingType = iterator.next().getTrack().getWellHasImagingType();
            if (wellHasImagingType.equals(wellHasImagingTypeToOverwrite)) {
                list.add(wellHasImagingType);
                iterator.remove();
            }
        }
        return list;
    }
}
