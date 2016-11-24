/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.HeatMapScalePanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.plate.HeatMapPlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of rendering a heat map of the plate for an
 * experiment.
 *
 * @author Paola
 */
@Controller("plateHeatMapController")
class PlateHeatMapController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PlateHeatMapController.class);
    // model
    private boolean firstView;
    // view
    private HeatMapPlatePanel heatMapPlatePanel;
    // parent controller
    @Autowired
    private TrackCoordinatesController trackCoordinatesController;
    // services
    private GridBagConstraints gridBagConstraints;
    @Autowired
    private PlateService plateService;

    /**
     * Initialize controller.
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        heatMapPlatePanel = new HeatMapPlatePanel();
        firstView = true;
        initPlatePanel();
        initView();
    }

    /**
     * Initialize the main view
     */
    private void initView() {
        TrackCoordinatesPanel trackCoordinatesPanel = trackCoordinatesController.getTrackCoordinatesPanel();
        trackCoordinatesPanel.getMeasurementComboBox().setSelectedIndex(0);
        trackCoordinatesPanel.getAggregationComboBox().setSelectedIndex(0);

        // the real plotting action
        trackCoordinatesPanel.getPlotHeatMapButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                plotHeatMap();
                heatMapPlatePanel.repaint();
            }
        });

        // click on first view
        trackCoordinatesPanel.getTrackCoordinatesTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (firstView) {
                    Dimension parentDimension = trackCoordinatesController.getTrackCoordinatesPanel().getGraphicParentPanel().getSize();
                    heatMapPlatePanel.setExperiment(trackCoordinatesController.getExperiment());
                    heatMapPlatePanel.initPanel(trackCoordinatesController.getExperiment().getPlateFormat(), parentDimension);
                    firstView = false;
                }
            }
        });
    }

    /**
     * Initialize the plate panel.
     */
    private void initPlatePanel() {
        Dimension parentDimension = trackCoordinatesController.getTrackCoordinatesPanel().getGraphicParentPanel().getSize();
        heatMapPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        trackCoordinatesController.getTrackCoordinatesPanel().getGraphicParentPanel().add(heatMapPlatePanel, gridBagConstraints);
        heatMapPlatePanel.repaint();
    }

    /**
     * Action called on pressing the button: refresh the heat map view
     */
    private void plotHeatMap() {
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().removeAll();
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().repaint();

        heatMapPlatePanel.setValues(computeValuesMap());
        heatMapPlatePanel.repaint();

        HeatMapScalePanel heatMapScalePanel = new HeatMapScalePanel(heatMapPlatePanel.getMin(), heatMapPlatePanel.getMax());
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().add(heatMapScalePanel, gridBagConstraints);
        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().revalidate();

        trackCoordinatesController.getTrackCoordinatesPanel().getColorBarPanel().repaint();
    }

    /**
     *
     * @return
     */
    private Map<Well, Double> computeValuesMap() {
        Map<Well, Double> valuesMap = new LinkedHashMap<>();

        TrackCoordinatesPanel trackCoordinatesPanel = trackCoordinatesController.getTrackCoordinatesPanel();
        int measurement = trackCoordinatesPanel.getMeasurementComboBox().getSelectedIndex();
        int aggregation = trackCoordinatesPanel.getAggregationComboBox().getSelectedIndex();
        Double value = 0.0;
        double min = 0.00000;
        double max = 0.00000;
        List<PlateCondition> plateConditionList = trackCoordinatesController.getPlateConditionList();
        for (PlateCondition condition : plateConditionList) {
            SingleCellConditionDataHolder conditionDataHolder = trackCoordinatesController.getConditionDataHolder(condition);
            List<SingleCellWellDataHolder> singleCellWellDataHolders = conditionDataHolder.getSingleCellWellDataHolders();
            for (SingleCellWellDataHolder wellDataHolder : singleCellWellDataHolders) {
                if (!wellDataHolder.getTrackDataHolders().isEmpty()) {
                    Well well = wellDataHolder.getWell();

                    switch (measurement) {
                        case 0: // number of trajectories
                            int nrTrack = wellDataHolder.getTrackDataHolders().size();
                            value = (double) nrTrack;

                            if (value > max) {
                                max = value;
                            }
                            if (value < min) {
                                min = value;
                            }
                            break;
                        case 1: // speed
                            Double[] trackSpeedsVector = wellDataHolder.getTrackSpeedsVector();
                            switch (aggregation) {
                                case 0: // mean
                                    value = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(trackSpeedsVector)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                                case 1: // median
                                    value = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(trackSpeedsVector)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                            }
                            break;
                        case 3: // directionality
                            Double[] endPointDirectionalityRatios = wellDataHolder.getEndPointDirectionalityRatios();
                            switch (aggregation) {
                                case 0: // mean
                                    value = AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(endPointDirectionalityRatios)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                                case 1: // median
                                    value = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(endPointDirectionalityRatios)));

                                    if (value > max) {
                                        max = value;
                                    }
                                    if (value < min) {
                                        min = value;
                                    }
                                    break;
                            }
                            break;
                    }
                    valuesMap.put(well, value);
                }

            }
        }
        heatMapPlatePanel.setMin(min);
        heatMapPlatePanel.setMax(max);
        return valuesMap;

    }
}
