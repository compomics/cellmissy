/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackDataHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackCoordinatesTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller for the track coordinates logic.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("trackCoordinatesController")
public class TrackCoordinatesController {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrackCoordinatesController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<Well> wellBindingList;
    private JTable coordinatesTable;
    private List<TrackDataHolder> randomTrackDataHolders;
    // view
    private TrackCoordinatesPanel trackCoordinatesPanel;
    private ChartPanel rawCoordinatesChartPanel;
    private ChartPanel normalizedCoordinatesChartPanel;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /*
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        initTrackCoordinatesPanel();
    }

    /**
     * getters
     */
    public TrackCoordinatesPanel getTrackCoordinatesPanel() {
        return trackCoordinatesPanel;
    }

    /**
     * For the given condition, show the raw track coordinates in a table.
     *
     * @param plateCondition
     */
    public void showRawTrackCoordinatesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] rawTrackCoordinatesMatrix = singleCellPreProcessingResults.getRawTrackCoordinatesMatrix();
            coordinatesTable.setModel(new TrackCoordinatesTableModel(dataStructure, rawTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(SwingConstants.CENTER, singleCellPreProcessingController.getFormat());
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            for (int i = 3; i < coordinatesTable.getColumnCount(); i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
            coordinatesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        trackCoordinatesPanel.getTableInfoLabel().setText("Raw Tracks Coordinates");
    }

    /**
     *
     */
    public void updateTrackNumberLabel() {
        int trackNumber = getTrackNumberForCondition();
        trackCoordinatesPanel.getTotalTracksNumberLabel().setText("" + trackNumber);
    }

    /**
     *
     * @param plateCondition
     */
    public void updateWellBindingList(PlateCondition plateCondition) {
        if (!wellBindingList.isEmpty()) {
            wellBindingList.clear();
        }
        for (Well well : plateCondition.getSingleCellAnalyzedWells()) {
            wellBindingList.add(well);
        }
    }

    /**
     * For the given condition, show the normalised track coordinates in a
     * table.
     *
     * @param plateCondition
     */
    public void showNormalizedTrackCoordinatesInTable(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] fixedDataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] normalizedTrackCoordinatesMatrix = singleCellPreProcessingResults.getNormalizedTrackCoordinatesMatrix();
            coordinatesTable.setModel(new TrackCoordinatesTableModel(fixedDataStructure, normalizedTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(SwingConstants.CENTER, singleCellPreProcessingController.getFormat());
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            for (int i = 3; i < coordinatesTable.getColumnCount(); i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
            coordinatesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        trackCoordinatesPanel.getTableInfoLabel().setText("Tracks Coordinates normalized to 0");
    }

    /**
     * Plot raw data track coordinates for current condition, specifying if
     * points and/or lines need to be shown on the plot.
     *
     * @param plateCondition
     * @param tracks
     * @param plotLines
     * @param plotPoints
     * @param showLegend
     */
    public void plotRawTrackCoordinates(PlateCondition plateCondition, boolean plotLines, boolean plotPoints, boolean showLegend) {
        if (randomTrackDataHolders.isEmpty()) {
            generateRandomTrackDataHolders();
        }
        XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
        int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(plateCondition) + 1;
        for (TrackDataHolder trackDataHolder : randomTrackDataHolders) {
            Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
            XYSeries xySeries = generateXYSeries(trackCoordinatesMatrix);
            Track track = trackDataHolder.getTrack();
            Well well = track.getWellHasImagingType().getWell();
            int trackNumber = track.getTrackNumber();
            xySeries.setKey("well " + well + ", track " + trackNumber);
            xYSeriesCollection.addSeries(xySeries);
        }
        // Plot Logic
        String chartTitle = "Raw track coordinates - Condition " + conditionIndex;
        JFreeChart rawCoordinatesChart = ChartFactory.createXYLineChart(chartTitle, "x", "y", xYSeriesCollection, PlotOrientation.VERTICAL, showLegend, true, false);
        JFreeChartUtils.setupTrackCoordinatesPlot(rawCoordinatesChart, plotLines, plotPoints);
        rawCoordinatesChartPanel.setChart(rawCoordinatesChart);
        trackCoordinatesPanel.getGraphicsParentPanel().remove(normalizedCoordinatesChartPanel);
        trackCoordinatesPanel.getGraphicsParentPanel().revalidate();
        trackCoordinatesPanel.getGraphicsParentPanel().repaint();
        trackCoordinatesPanel.getGraphicsParentPanel().add(rawCoordinatesChartPanel, gridBagConstraints);
    }

    /**
     * Plot track coordinates normalised to start (x, y) zero for current
     * condition, specifying if points and /or lines need to be shown on the
     * plot.
     *
     * @param plateCondition
     * @param plotLines
     * @param plotPoints
     * @param showLegend
     */
    public void plotNormalizedTrackCoordinates(PlateCondition plateCondition, boolean plotLines, boolean plotPoints, boolean showLegend) {
        if (randomTrackDataHolders.isEmpty()) {
            generateRandomTrackDataHolders();
        }
        XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
        int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(plateCondition) + 1;
        for (TrackDataHolder trackDataHolder : randomTrackDataHolders) {
            Double[][] normalizedTrackCoordinates = trackDataHolder.getNormalizedTrackCoordinates();
            XYSeries xySeries = generateXYSeries(normalizedTrackCoordinates);
            Track track = trackDataHolder.getTrack();
            Well well = track.getWellHasImagingType().getWell();
            int trackNumber = track.getTrackNumber();
            xySeries.setKey("well " + well + ", track " + trackNumber);
            xYSeriesCollection.addSeries(xySeries);
        }
        // Plot Logic
        String chartTitle = "Normalized track coordinates - Condition " + conditionIndex;
        JFreeChart normalizedCoordinatesChart = ChartFactory.createXYLineChart(chartTitle, "x", "y", xYSeriesCollection, PlotOrientation.VERTICAL, showLegend, true, false);
        JFreeChartUtils.setupTrackCoordinatesPlot(normalizedCoordinatesChart, plotLines, plotPoints);
        normalizedCoordinatesChartPanel.setChart(normalizedCoordinatesChart);
        trackCoordinatesPanel.getGraphicsParentPanel().remove(rawCoordinatesChartPanel);
        trackCoordinatesPanel.getGraphicsParentPanel().revalidate();
        trackCoordinatesPanel.getGraphicsParentPanel().repaint();
        trackCoordinatesPanel.getGraphicsParentPanel().add(normalizedCoordinatesChartPanel, gridBagConstraints);
    }

    /**
     *
     */
    public void resetRandomTracks() {
        if (!randomTrackDataHolders.isEmpty()) {
            randomTrackDataHolders.clear();
        }
    }

    /**
     * private methods and classes
     */
    private XYSeries generateXYSeries(Double[][] coordinatesToPlot) {
        // transpose the matrix
        Double[][] transposed = AnalysisUtils.transpose2DArray(coordinatesToPlot);
        // take first row: x coordinates
        double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[0]));
        // take second row: y coodinates
        double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[1]));
        // generate xy series for the plot
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(xCoordinates, yCoordinates);
        return xySeries;
    }

    /**
     * Initialize main panel
     */
    private void initTrackCoordinatesPanel() {
        // init new main panel
        trackCoordinatesPanel = new TrackCoordinatesPanel();
        // init well binding list
        wellBindingList = ObservableCollections.observableList(new ArrayList<Well>());
        randomTrackDataHolders = new ArrayList<>();
        // init jcombo box binding
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, wellBindingList, trackCoordinatesPanel.getWellsComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        //init dataTable
        coordinatesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(coordinatesTable);
        //the table will take all the viewport height available
        coordinatesTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        coordinatesTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        coordinatesTable.setColumnSelectionAllowed(true);
        coordinatesTable.setRowSelectionAllowed(false);
        trackCoordinatesPanel.getDataTablePanel().add(scrollPane);
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(trackCoordinatesPanel.getRawCoordinatesRadioButton());
        radioButtonGroup.add(trackCoordinatesPanel.getNormalizedCoordinatesRadioButton());
        //select as default first button (raw data track coordinates Computation)
        trackCoordinatesPanel.getRawCoordinatesRadioButton().setSelected(true);
        // create another radio button group for the others radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(trackCoordinatesPanel.getFromSameWellRadioButton());
        group.add(trackCoordinatesPanel.getFromDifferentWellsRadioButton());
        trackCoordinatesPanel.getFromSameWellRadioButton().setSelected(true);
        trackCoordinatesPanel.getPlotLinesCheckBox().setSelected(true);
        trackCoordinatesPanel.getPlotPointsCheckBox().setSelected(true);
        trackCoordinatesPanel.getShowLegendCheckBox().setSelected(true);
        //init chart panels
        rawCoordinatesChartPanel = new ChartPanel(null);
        rawCoordinatesChartPanel.setOpaque(false);
        normalizedCoordinatesChartPanel = new ChartPanel(null);
        normalizedCoordinatesChartPanel.setOpaque(false);

        /**
         * add action listeners
         */
        // raw track coordinates
        trackCoordinatesPanel.getRawCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showRawTrackCoordinatesInTable(currentCondition);
                    boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                    boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                    boolean showLegend = trackCoordinatesPanel.getShowLegendCheckBox().isSelected();
                    plotRawTrackCoordinates(currentCondition, plotLines, plotPoints, showLegend);
                }
            }
        });

        // track coordinates normalized to first time point
        trackCoordinatesPanel.getNormalizedCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showNormalizedTrackCoordinatesInTable(currentCondition);
                    boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                    boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                    boolean showLegend = trackCoordinatesPanel.getShowLegendCheckBox().isSelected();
                    plotNormalizedTrackCoordinates(currentCondition, plotLines, plotPoints, showLegend);
                }
            }
        });

        // plot lines
        trackCoordinatesPanel.getPlotLinesCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                boolean showLegend = trackCoordinatesPanel.getShowLegendCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (trackCoordinatesPanel.getRawCoordinatesRadioButton().isSelected()) {
                        plotRawTrackCoordinates(currentCondition, true, plotPoints, showLegend);
                    } else {
                        plotNormalizedTrackCoordinates(currentCondition, true, plotPoints, showLegend);
                    }
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotPoints) {
                        trackCoordinatesPanel.getPlotPointsCheckBox().setSelected(true);
                    }
                    if (trackCoordinatesPanel.getRawCoordinatesRadioButton().isSelected()) {
                        plotRawTrackCoordinates(currentCondition, false, true, showLegend);
                    } else {
                        plotNormalizedTrackCoordinates(currentCondition, false, true, showLegend);
                    }
                }
            }
        });

        // plot points
        trackCoordinatesPanel.getPlotPointsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                boolean showLegend = trackCoordinatesPanel.getShowLegendCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (trackCoordinatesPanel.getRawCoordinatesRadioButton().isSelected()) {
                        plotRawTrackCoordinates(currentCondition, plotLines, true, showLegend);
                    } else {
                        plotNormalizedTrackCoordinates(currentCondition, plotLines, true, showLegend);
                    }
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotLines) {
                        trackCoordinatesPanel.getPlotLinesCheckBox().setSelected(true);
                    }
                    if (trackCoordinatesPanel.getRawCoordinatesRadioButton().isSelected()) {
                        plotRawTrackCoordinates(currentCondition, true, false, showLegend);
                    } else {
                        plotNormalizedTrackCoordinates(currentCondition, true, false, showLegend);
                    }
                }
            }
        });

        // hide or show legend for current plot?
        trackCoordinatesPanel.getShowLegendCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (trackCoordinatesPanel.getRawCoordinatesRadioButton().isSelected()) {
                        plotRawTrackCoordinates(currentCondition, plotLines, plotPoints, true);
                    } else {
                        plotNormalizedTrackCoordinates(currentCondition, plotLines, plotPoints, true);
                    }
                } else {
                    if (trackCoordinatesPanel.getRawCoordinatesRadioButton().isSelected()) {
                        plotRawTrackCoordinates(currentCondition, plotLines, plotPoints, false);
                    } else {
                        plotNormalizedTrackCoordinates(currentCondition, plotLines, plotPoints, false);
                    }
                }
            }
        });

        // refresh plot xith current selected option
        trackCoordinatesPanel.getPlotButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                boolean plotLines = trackCoordinatesPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = trackCoordinatesPanel.getPlotPointsCheckBox().isSelected();
                boolean showLegend = trackCoordinatesPanel.getShowLegendCheckBox().isSelected();
                if (currentCondition != null) {
                    resetRandomTracks();
                    generateRandomTrackDataHolders();
                    if (trackCoordinatesPanel.getRawCoordinatesRadioButton().isSelected()) {
                        plotRawTrackCoordinates(currentCondition, plotLines, plotPoints, showLegend);
                    } else {
                        plotNormalizedTrackCoordinates(currentCondition, plotLines, plotPoints, showLegend);
                    }
                }
            }
        });

        // how many tracks for the selected well?
        trackCoordinatesPanel.getWellsComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<TrackDataHolder> trackHoldersForCurrentWell = getTrackHoldersForCurrentWell();
                int numberTracksForCurrentWell = trackHoldersForCurrentWell.size();
                // update info with number of tracks for current selected well
                trackCoordinatesPanel.getTracksNumberCurrentWellLabel().setText("" + numberTracksForCurrentWell);
            }
        });

        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesParentPanel().add(trackCoordinatesPanel, gridBagConstraints);
    }

    /**
     * Generate randomly the tracks to put into the plot
     *
     * @return
     */
    private void generateRandomTrackDataHolders() {
        String text = trackCoordinatesPanel.getRandomTracksNumberTextField().getText();
        // if the user does not write anything, number of tracks to be plotted is 10 
        int randomTracksNumber = 10;
        if (!text.isEmpty()) {
            try {
                randomTracksNumber = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage());
                singleCellPreProcessingController.showMessage("Please insert a valid number of tracks!", "error setting number of tracks", JOptionPane.WARNING_MESSAGE);
            }
        }
        // check if tracks need to be generated from within the same well or not
        if (trackCoordinatesPanel.getFromSameWellRadioButton().isSelected()) {
            List<TrackDataHolder> trackHoldersForCurrentWell = getTrackHoldersForCurrentWell();
            int numberTracksForCurrentWell = trackHoldersForCurrentWell.size();
            // update info with number of tracks for current selected well
            trackCoordinatesPanel.getTracksNumberCurrentWellLabel().setText("" + numberTracksForCurrentWell);
            // the tracks that you want to plot need to be less or equal to the tracks for the current well
            // else, show an info message and set the number back to 10
            if (randomTracksNumber > numberTracksForCurrentWell) {
                randomTracksNumber = 10;
                singleCellPreProcessingController.showMessage("Please insert a number of tracks to plot smaller or equal\nto the number of tracks for the selected well!", "error in choosing number of tracks", JOptionPane.WARNING_MESSAGE);
            }
            // update text field
            trackCoordinatesPanel.getRandomTracksNumberTextField().setText("" + randomTracksNumber);
            // pick up randomly the track holders and add them to the list
            for (int j = 0; j < randomTracksNumber; j++) {
                // create a random number and take its int part
                Double random = Math.random() * numberTracksForCurrentWell;
                int intValue = random.intValue();
                TrackDataHolder randomTrackDataHolder = trackHoldersForCurrentWell.get(intValue);
                if (!randomTrackDataHolders.contains(randomTrackDataHolder)) {
                    randomTrackDataHolders.add(randomTrackDataHolder);
                } else {
                    j--;
                }
            }
        } else {
            // get track holders for current condition
            List<TrackDataHolder> trackHoldersForCurrentCondition = getTrackHoldersForCurrentCondition();
            // else: the random tracks need to be generated from all the wells in the condition
            int trackNumberForCondition = trackHoldersForCurrentCondition.size();
            if (randomTracksNumber > trackNumberForCondition) {
                randomTracksNumber = 10;
                singleCellPreProcessingController.showMessage("Please insert a number of tracks to plot smaller or equal\nto the number of tracks for the current condition!", "error in choosing number of tracks", JOptionPane.WARNING_MESSAGE);
            }
            // update text field
            trackCoordinatesPanel.getRandomTracksNumberTextField().setText("" + randomTracksNumber);
            for (int j = 0; j < randomTracksNumber; j++) {
                // create a random number and take its int part
                Double random = Math.random() * trackNumberForCondition;
                int intValue = random.intValue();
                TrackDataHolder randomTrackDataHolder = trackHoldersForCurrentCondition.get(intValue);
                if (!randomTrackDataHolders.contains(randomTrackDataHolder)) {
                    randomTrackDataHolders.add(randomTrackDataHolder);
                } else {
                    j--;
                }
            }
        }
    }

    /**
     *
     * @return
     */
    private List<TrackDataHolder> getTrackHoldersForCurrentWell() {
        PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(currentCondition);
        List<TrackDataHolder> trackHoldersForCurrentWell = new ArrayList<>();
        // get the well selected
        Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
        for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
            if (trackDataHolder.getTrack().getWellHasImagingType().getWell().equals(selectedWell)) {
                trackHoldersForCurrentWell.add(trackDataHolder);
            }
        }
        return trackHoldersForCurrentWell;
    }

    /**
     *
     * @return
     */
    private List<TrackDataHolder> getTrackHoldersForCurrentCondition() {
        PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getPreProcessingResults(currentCondition);
        return singleCellPreProcessingResults.getTrackDataHolders();
    }

    /**
     *
     * @return
     */
    private int getTrackNumberForCondition() {
        return getTrackHoldersForCurrentCondition().size();
    }
}
