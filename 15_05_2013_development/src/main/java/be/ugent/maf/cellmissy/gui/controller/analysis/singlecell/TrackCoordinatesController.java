/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.SingleCellPreProcessingResults;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author paola
 */
@Component("trackCoordinatesController")
public class TrackCoordinatesController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrackCoordinatesController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<Well> wellBindingList;
    private JTable dataTable;
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
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getResultsHolder(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] fixedDataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] rawTrackCoordinatesMatrix = singleCellPreProcessingResults.getRawTrackCoordinatesMatrix();
            dataTable.setModel(new TrackCoordinatesTableModel(fixedDataStructure, rawTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(SwingConstants.CENTER, singleCellPreProcessingController.getFormat());
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            for (int i = 3; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        trackCoordinatesPanel.getTableInfoLabel().setText("Raw Tracks Coordinates");
    }

    /**
     *
     * @param plateCondition
     */
    public void updateTrackNumberLabel(PlateCondition plateCondition) {
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getResultsHolder(plateCondition);
        if (singleCellPreProcessingResults != null) {
            int trackNumber = singleCellPreProcessingResults.getTrackDataHolders().size();
            trackCoordinatesPanel.getTotalTracksNumberLabel().setText("" + trackNumber);
        }
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
        SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getResultsHolder(plateCondition);
        if (singleCellPreProcessingResults != null) {
            Object[][] fixedDataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] normalizedTrackCoordinatesMatrix = singleCellPreProcessingResults.getNormalizedTrackCoordinatesMatrix();
            dataTable.setModel(new TrackCoordinatesTableModel(fixedDataStructure, normalizedTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(SwingConstants.CENTER, singleCellPreProcessingController.getFormat());
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            for (int i = 3; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        trackCoordinatesPanel.getTableInfoLabel().setText("Tracks Coordinates normalized to 0");
    }

    /**
     *
     * @param plateCondition
     * @param tracks
     * @param plotLines
     * @param plotPoints
     */
    public void plotRawTrackCoordinates(PlateCondition plateCondition, boolean plotLines, boolean plotPoints) {
        List<TrackDataHolder> trackDataHoldersForPlot = generateTrackDataHoldersForPlot(plateCondition);
        XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
        int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(plateCondition) + 1;
        for (TrackDataHolder trackDataHolder : trackDataHoldersForPlot) {
            Double[][] trackCoordinatesMatrix = trackDataHolder.getTrackCoordinatesMatrix();
            Double[][] transposed = AnalysisUtils.transpose2DArray(trackCoordinatesMatrix);
            double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[0]));
            double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[1]));
            XYSeries xySeries = JFreeChartUtils.generateXYSeries(xCoordinates, yCoordinates);
            xySeries.setKey("" + (trackDataHolder.getTrack().getTrackNumber()));
            xYSeriesCollection.addSeries(xySeries);
        }
        // Plot Logic
        String chartTitle = "Raw track coordinates - Condition " + conditionIndex;
        JFreeChart rawCoordinatesChart = ChartFactory.createXYLineChart(chartTitle, "x", "y", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        JFreeChartUtils.setupTrackCoordinatesPlot(rawCoordinatesChart, plotLines, plotPoints);
        rawCoordinatesChartPanel.setChart(rawCoordinatesChart);
        trackCoordinatesPanel.getGraphicsParentPanel().add(rawCoordinatesChartPanel, gridBagConstraints);
    }

    /**
     * Initialize main panel
     */
    private void initTrackCoordinatesPanel() {
        // init new main panel
        trackCoordinatesPanel = new TrackCoordinatesPanel();
        // init well binding list
        wellBindingList = ObservableCollections.observableList(new ArrayList<Well>());
        // init jcombo box binding
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, wellBindingList, trackCoordinatesPanel.getWellsComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        //init dataTable
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
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
                //check that a condition is selected
                if (singleCellPreProcessingController.getCurrentCondition() != null) {
                    showRawTrackCoordinatesInTable(singleCellPreProcessingController.getCurrentCondition());
                }
            }
        });

        // track coordinates normalized to first time point
        trackCoordinatesPanel.getNormalizedCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (singleCellPreProcessingController.getCurrentCondition() != null) {
                    showNormalizedTrackCoordinatesInTable(singleCellPreProcessingController.getCurrentCondition());
                }
            }
        });

        // 
        trackCoordinatesPanel.getFromSameWellRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        //
        trackCoordinatesPanel.getFromDifferentWellsRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
    private List<TrackDataHolder> generateTrackDataHoldersForPlot(PlateCondition plateCondition) {
        List<TrackDataHolder> tracksDataHolders = new ArrayList<>();
        String text = trackCoordinatesPanel.getRandomTracksNumberTextField().getText();
        // if nothing is set, number is 10 
        int numberOfTracks = 10;
        if (!text.isEmpty()) {
            try {
                numberOfTracks = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage());
                singleCellPreProcessingController.showMessage("Please insert a valid number of tracks!", "error setting number of tracks", JOptionPane.WARNING_MESSAGE);
            }
        }
        // check if tracks need to be generated from xithin the same well or not
        if (trackCoordinatesPanel.getFromSameWellRadioButton().isSelected()) {
            List<TrackDataHolder> tempList = new ArrayList<>();
            // get the well selected
            Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
            SingleCellPreProcessingResults singleCellPreProcessingResults = singleCellPreProcessingController.getResultsHolder(plateCondition);
            for (TrackDataHolder trackDataHolder : singleCellPreProcessingResults.getTrackDataHolders()) {
                if (trackDataHolder.getTrack().getWellHasImagingType().getWell().equals(selectedWell)) {
                    tempList.add(trackDataHolder);
                }
            }
            for (int j = 0; j < numberOfTracks; j++) {
                // create a random number and take its int part
                Double random = Math.random() * tempList.size();
                int intValue = random.intValue();
                TrackDataHolder randomTrackDataHolder = tempList.get(intValue);
                if (!tracksDataHolders.contains(randomTrackDataHolder)) {
                    tracksDataHolders.add(randomTrackDataHolder);
                } else {
                    j--;
                }
            }
        } else {
            //
        }
        return tracksDataHolders;
    }
}
