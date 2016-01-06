/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsMenuBar;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.PlotSettingsRendererGiver;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.TrackCoordinatesPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.RectIconCellRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackCoordinatesTableModel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.JTableHeader;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for the track coordinates logic.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("trackCoordinatesController")
class TrackCoordinatesController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrackCoordinatesController.class);
    // model
    private BindingGroup bindingGroup;
    private ObservableList<Well> wellBindingList;
    private JTable coordinatesTable;
    private ObservableList<TrackDataHolder> trackDataHolderBindingList;
    private Color chosenColor;
    // view
    private TrackCoordinatesPanel trackCoordinatesPanel;
    private PlotSettingsMenuBar plotSettingsMenuBar;
    private ChartPanel coordinatesChartPanel;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    @Autowired
    private ExploreTrackController exploreTrackController;
    @Autowired
    private GlobalViewController globalViewController;
    // services
    private GridBagConstraints gridBagConstraints;

    /*
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        initPlotSettingsMenuBar();
        initTrackCoordinatesPanel();
        // init child controllers
        exploreTrackController.init();
        globalViewController.init();
    }

    /**
     * Getters
     */
    public TrackCoordinatesPanel getTrackCoordinatesPanel() {
        return trackCoordinatesPanel;
    }

    public ObservableList<TrackDataHolder> getTrackDataHolderBindingList() {
        return trackDataHolderBindingList;
    }

    CellMissyFrame getMainFrame() {
        return singleCellPreProcessingController.getMainFrame();
    }

    public void setCursor(Cursor cursor) {
        singleCellPreProcessingController.setCursor(cursor);
    }

    public void handleUnexpectedError(Exception ex) {
        singleCellPreProcessingController.handleUnexpectedError(ex);
    }

    public void controlGuiComponents(boolean enabled) {
        singleCellPreProcessingController.controlGuiComponents(enabled);
    }

    public JList getConditionsList() {
        return singleCellPreProcessingController.getConditionsList();
    }

    public void showWaitingDialog(String title) {
        singleCellPreProcessingController.showWaitingDialog(title);
    }

    public void hideWaitingDialog() {
        singleCellPreProcessingController.hideWaitingDialog();
    }

    /**
     * @return
     */
    public List<PlateCondition> getPlateConditionList() {
        return singleCellPreProcessingController.getPlateConditionList();
    }

    /**
     * Get the category to plot for the tracks: normally 0, the condition
     * category; if the well radio button is selected, set the category to plot
     * to 1.
     *
     * @return
     */
    public int getCategoryToPlot() {
        int categoryToPlot = 0;
        if (trackCoordinatesPanel.getWellRadioButton().isSelected()) {
            categoryToPlot = 1;
        }
        return categoryToPlot;
    }

    public AnalysisPlatePanel getAnalysisPlatePanel() {
        return singleCellPreProcessingController.getAnalysisPlatePanel();
    }

    public PlateCondition getCurrentCondition() {
        return singleCellPreProcessingController.getCurrentCondition();
    }

    public void setCurrentCondition(PlateCondition currentCondition) {
        singleCellPreProcessingController.setCurrentCondition(currentCondition);
    }

    public SingleCellConditionDataHolder getConditionDataHolder(PlateCondition plateCondition) {
        return singleCellPreProcessingController.getConditionDataHolder(plateCondition);
    }

    /**
     * Iterate through the current track data holders and get the endpoints of
     * the correspondent tracks.
     *
     * @return: a List of Integers, each Integer being the endpoint for a track.
     */
    public List<Integer> getEndPoints() {
        List<Integer> endPoints = new ArrayList<>();
        for (TrackDataHolder trackDataHolder : trackDataHolderBindingList) {
            double[] timeIndexes = trackDataHolder.getStepCentricDataHolder().getTimeIndexes();
            int numberOfTimePoints = timeIndexes.length - 1;
            endPoints.add(numberOfTimePoints);
        }
        return endPoints;
    }

    /**
     * Show message through the main controller
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        singleCellPreProcessingController.showMessage(message, title, messageType);
    }

    /**
     * Show the number of total tracks for the current selected condition.
     */
    public void updateTracksNumberInfo(PlateCondition plateCondition) {
        int trackNumber = getTrackNumberForCondition(plateCondition);
        trackCoordinatesPanel.getTracksNumberConditionTextField().setText("" + trackNumber);
    }

    /**
     * Update the binding list with the current wells (according to the current
     * condition selected).
     *
     * @param plateCondition
     */
    public void updateWellBindingList(PlateCondition plateCondition) {
        if (!wellBindingList.isEmpty()) {
            wellBindingList.clear();
        }
        wellBindingList.addAll(plateCondition.getSingleCellAnalyzedWells());
    }

    /**
     * For the given condition, show the raw track coordinates in a table.
     *
     * @param plateCondition
     */
    public void showRawTrackCoordinatesInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController
                  .getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Object[][] dataStructure = singleCellConditionDataHolder.getDataStructure();
            Double[][] rawTrackCoordinatesMatrix = singleCellConditionDataHolder.getRawTrackCoordinatesMatrix();
            coordinatesTable.setModel(new TrackCoordinatesTableModel(dataStructure, rawTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(),
                      SwingConstants.CENTER);
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
     * For the given condition, show the normalised track coordinates in a
     * table.
     *
     * @param plateCondition
     */
    public void showShiftedTrackCoordinatesInTable(PlateCondition plateCondition) {
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController
                  .getConditionDataHolder(plateCondition);
        if (singleCellConditionDataHolder != null) {
            Object[][] fixedDataStructure = singleCellConditionDataHolder.getDataStructure();
            Double[][] shiftedTrackCoordinatesMatrix = singleCellConditionDataHolder.getShiftedTrackCoordinatesMatrix();
            coordinatesTable.setModel(new TrackCoordinatesTableModel(fixedDataStructure,
                      shiftedTrackCoordinatesMatrix));
            FormatRenderer formatRenderer = new FormatRenderer(singleCellPreProcessingController.getFormat(),
                      SwingConstants.CENTER);
            AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.CENTER);
            for (int i = 0; i < 3; i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            }
            for (int i = 3; i < coordinatesTable.getColumnCount(); i++) {
                coordinatesTable.getColumnModel().getColumn(i).setCellRenderer(formatRenderer);
            }
            coordinatesTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.CENTER));
        }
        trackCoordinatesPanel.getTableInfoLabel().setText("Tracks Coordinates with origins of migration superimposed "
                  + "at (0, 0) ");
    }

    /**
     * Generate randomly the tracks to put into the plot. This depends on the
     * category we want to generate the plot from: 0 is the condition level,
     * while 1 is the well level.
     *
     * @param category: can be 0 or 1
     * @param plateCondition: the condition to generate the tracks from
     */
    public void generateRandomTrackDataHolders(int category, PlateCondition plateCondition) {
        // check if tracks need to be generated from within the same well or not
        switch (category) {
            case 0:
                generateRandomTrackHoldersForCondition(plateCondition);
                break;
            case 1:
                generateRandomTrackDataHoldersForWell();
                break;
        }
    }

    /**
     * Plot raw data track coordinates for current condition, specifying if raw
     * data need to be used and if points and/or lines need to be shown on the
     * plot.
     *
     * @param plateCondition: the plate condition to plot the tracks from
     * @param useRawCoordinates: if true, plot raw data, else take the shifted
     * to zero coordinates.
     */
    public void plotRandomTrackCoordinates(PlateCondition plateCondition, boolean useRawCoordinates) {
        // we get the selected index from the tabbed pane
        // according to this, we generate the random tracks from the condition or from the well
        int categoryToPlot = getCategoryToPlot();
        // if we don't actually have tracks to plot, we generate them
        if (trackDataHolderBindingList.isEmpty()) {
            generateRandomTrackDataHolders(categoryToPlot, plateCondition);
        }
        // generate the xy collection for the plot
        XYSeriesCollection xYSeriesCollectionForPlot = generateXYSeriesCollectionForPlot(useRawCoordinates);
        // Plot Logic
        int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(plateCondition) + 1;
        int numberOfTracks = trackDataHolderBindingList.size();
        // title of the chart depends on:
        // 1. the category to plot (condition or well based?) and
        // 2. the coordinates to plot (raw data or shifted coordinates?)
        String chartTitle = "";
        switch (categoryToPlot) {
            case 0:
                if (useRawCoordinates) {
                    chartTitle = numberOfTracks + " tracks - condition " + conditionIndex;
                } else {
                    chartTitle = numberOfTracks + " tracks, coordinates shifted to (0, 0) - condition "
                              + conditionIndex;
                }
                break;
            case 1:
                Well well = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
                if (useRawCoordinates) {
                    chartTitle = numberOfTracks + " tracks - well " + well.toString();
                } else {
                    chartTitle = numberOfTracks + " tracks, coordinates shifted to (0, 0) - well " + well.toString();
                }
                break;
        }
        // we finally create the coordinates charts
        setChartsWithXyCollection(chartTitle, xYSeriesCollectionForPlot);
    }

    /**
     * Empty the list with the track data holders.
     */
    void resetTracksList() {
        if (!trackDataHolderBindingList.isEmpty()) {
            trackDataHolderBindingList.clear();
        }
    }

    /**
     * Show the tracks currently plotted in a table.
     */
    public void showPlottedTracksInTable() {
        JTableBinding trackHoldersTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE, trackDataHolderBindingList, trackCoordinatesPanel.getPlottedTracksTable());
        // add column bindings
        BeanProperty index = BeanProperty.create("index");
        JTableBinding.ColumnBinding columnBinding = trackHoldersTableBinding.addColumnBinding(index);
        columnBinding.setColumnName("");
        columnBinding.setEditable(false);

        columnBinding = trackHoldersTableBinding.addColumnBinding(ELProperty.create("${track}"));
        columnBinding.setColumnName("track number");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Track.class);

        columnBinding = trackHoldersTableBinding.addColumnBinding(ELProperty.create("${track.wellHasImagingType"
                  + ".well}"));
        columnBinding.setColumnName("well");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Well.class);

        bindingGroup.addBinding(trackHoldersTableBinding);
        // do the binding
        bindingGroup.bind();
        //set renderer for first column
        trackCoordinatesPanel.getPlottedTracksTable().getColumnModel().getColumn(0).setCellRenderer(new RectIconCellRenderer());
    }

    /**
     * Action Listener for MenuItems
     */
    private class ItemActionListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            int selectedTrackIndex = -1;
            List<Integer> endPoints = getEndPoints();
            PlotSettingsRendererGiver plotSettingsRendererGiver = new PlotSettingsRendererGiver(selectedTrackIndex,
                      plotSettingsMenuBar, endPoints);
            TrackXYLineAndShapeRenderer renderer = plotSettingsRendererGiver.getRenderer(e);
            renderer.setChosenColor(chosenColor);
            coordinatesChartPanel.getChart().getXYPlot().setRenderer(renderer);
        }
    }

    /**
     * For the color menu item, a Color Chooser has to be shown for the user to
     * select the color to use.
     */
    private class ColorItemActionListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            int selectedTrackIndex = -1;
            List<Integer> endPoints = getEndPoints();
            PlotSettingsRendererGiver plotSettingsRendererGiver = new PlotSettingsRendererGiver(selectedTrackIndex,
                      plotSettingsMenuBar, endPoints);
            TrackXYLineAndShapeRenderer renderer = plotSettingsRendererGiver.getRenderer(e);
            // show the color chooser only if the item is being selected
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // show a color chooser
                chosenColor = JColorChooser.showDialog(null, "pick a color", Color.BLACK);
            }
            renderer.setChosenColor(chosenColor);
            coordinatesChartPanel.getChart().getXYPlot().setRenderer(renderer);
        }
    }

    /**
     * Initialize plot settings menu bar
     */
    private void initPlotSettingsMenuBar() {
        // create new object
        plotSettingsMenuBar = new PlotSettingsMenuBar();
        /**
         * Add item listeners to the menu items
         */
        ItemActionListener itemActionListener = new ItemActionListener();
        plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().addItemListener(itemActionListener);
        plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().addItemListener(itemActionListener);
        plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().addItemListener(itemActionListener);

        for (Enumeration<AbstractButton> buttons = plotSettingsMenuBar.getLinesButtonGroup().getElements(); buttons
                  .hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            button.addItemListener(itemActionListener);
        }

        plotSettingsMenuBar.getUseSingleColorCheckBoxMenuItem().addItemListener(new ColorItemActionListener());
    }

    /**
     * Initialize main panel
     */
    private void initTrackCoordinatesPanel() {
        // init new main panel
        trackCoordinatesPanel = new TrackCoordinatesPanel();
        // create a new button group and add the two radio buttons to it
        ButtonGroup categoriesToPlotButtonGroup = new ButtonGroup();
        categoriesToPlotButtonGroup.add(trackCoordinatesPanel.getConditionRadioButton());
        categoriesToPlotButtonGroup.add(trackCoordinatesPanel.getWellRadioButton());
        trackCoordinatesPanel.getConditionRadioButton().setSelected(true);
        // another ButtonGroup is needed for the scaling of the axes
        ButtonGroup scaleAxesButtonGroup = new ButtonGroup();
        scaleAxesButtonGroup.add(trackCoordinatesPanel.getDoNotScaleRadioButton());
        scaleAxesButtonGroup.add(trackCoordinatesPanel.getScaleToConditionRadioButton());
        scaleAxesButtonGroup.add(trackCoordinatesPanel.getScaleToExperimentRadioButton());
        trackCoordinatesPanel.getDoNotScaleRadioButton().setSelected(true);
        // init well binding list
        wellBindingList = ObservableCollections.observableList(new ArrayList<Well>());
        trackDataHolderBindingList = ObservableCollections.observableList(new ArrayList<TrackDataHolder>());
        // init jcombo box binding: wells
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, wellBindingList, trackCoordinatesPanel.getWellsComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        // do the binding
        bindingGroup.bind();
        // get the tables header
        JTableHeader tracksTableHeader = trackCoordinatesPanel.getPlottedTracksTable().getTableHeader();
        tracksTableHeader.setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        tracksTableHeader.setReorderingAllowed(false);

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
        radioButtonGroup.add(trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton());
        radioButtonGroup.add(trackCoordinatesPanel.getShiftedCoordinatesRadioButton());
        //select as default first button (raw data track coordinates Computation)
        trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().setSelected(true);
        //init chart panels
        coordinatesChartPanel = new ChartPanel(null);
        coordinatesChartPanel.setOpaque(false);
        trackCoordinatesPanel.getCoordinatesParentPanel().add(coordinatesChartPanel, gridBagConstraints);

        /**
         * add action listeners
         */
        // raw track coordinates
        trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showRawTrackCoordinatesInTable(currentCondition);
                    plotRandomTrackCoordinates(currentCondition, true);
                }
            }
        });

        // track coordinates normalized to first time point
        // this means that all the tracks start from the origin (0, 0)
        trackCoordinatesPanel.getShiftedCoordinatesRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                //check that a condition is selected
                if (currentCondition != null) {
                    showShiftedTrackCoordinatesInTable(currentCondition);
                    plotRandomTrackCoordinates(currentCondition, false);
                }
            }
        });

        // scale the plot to condition x and y min, max coordinates
        trackCoordinatesPanel.getScaleToConditionRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                scaleAxesToCondition(coordinatesChartPanel.getChart(), currentCondition);
                JFreeChartUtils.setupTrackChart(coordinatesChartPanel.getChart());
            }
        });

        // scale the plot to experiment x and y min, max coordinates
        trackCoordinatesPanel.getScaleToExperimentRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleAxesToExperiment(coordinatesChartPanel.getChart(), trackCoordinatesPanel
                          .getUnshiftedCoordinatesRadioButton().isSelected());
            }
        });

        // do not scale the axes
        trackCoordinatesPanel.getDoNotScaleRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plotRandomTrackCoordinates(singleCellPreProcessingController.getCurrentCondition(),
                          trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().isSelected());
            }
        });

        // refresh plot with current selected option
        trackCoordinatesPanel.getRandomAndPlotButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition selectedCondition = singleCellPreProcessingController.getSelectedCondition();
                boolean useRawData = trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().isSelected();
                int categoryToPlot = getCategoryToPlot();
                if (selectedCondition != null) {
                    resetTracksList();
                    generateRandomTrackDataHolders(categoryToPlot, selectedCondition);
                    plotRandomTrackCoordinates(selectedCondition, useRawData);
                }
            }
        });

        // how many tracks for the selected well?
        trackCoordinatesPanel.getWellsComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
                PlateCondition selectedCondition = singleCellPreProcessingController.getSelectedCondition();
                List<TrackDataHolder> trackHoldersForCurrentWell = getTrackHoldersForWell(selectedCondition,
                          selectedWell);
                int numberTracksForCurrentWell = trackHoldersForCurrentWell.size();
                // update info with number of tracks for current selected well
                trackCoordinatesPanel.getTracksNumberWellTextField().setText(" " + numberTracksForCurrentWell);
            }
        });

        // plot all tracks for current condition: we use a swing worker
        trackCoordinatesPanel.getPlotAllTracksButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // we check for the selected tabbed pane and execute a swing worker to plot all tracks together
                if (getCategoryToPlot() == 0) {
                    PlotAllTracksConditionSwingWorker plotAllTracksConditionSwingWorker = new PlotAllTracksConditionSwingWorker();
                    plotAllTracksConditionSwingWorker.execute();
                } else {
                    PlotAllTracksWellSwingWorker plotAllTracksWellSwingWorker = new PlotAllTracksWellSwingWorker();
                    plotAllTracksWellSwingWorker.execute();
                }
            }
        });

        trackCoordinatesPanel.getPlotSettingsPanel().add(plotSettingsMenuBar, BorderLayout.CENTER);
        // add view to parent panel
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getCellTracksParentPanel().add(trackCoordinatesPanel, gridBagConstraints);
    }

    /**
     * Generate XYSeriesCollection for the tracks plot. This will depend on the
     * data to plot: the raw coordinates (boolean is true) or the shifted to
     * zero coordinates (boolean is false)?
     *
     * @param useRawCoordinates
     * @return a XYSeriesCollection
     */
    XYSeriesCollection generateXYSeriesCollectionForPlot(boolean useRawCoordinates) {
        XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
        // the matrix to use is either the raw coordinates matrix or the shifted matrix
        Double[][] coordinatesMatrix;
        // this is not the best way to fix this multiple locations issue, but for the moment fair enough !!
        int counter = 0;
        for (TrackDataHolder trackDataHolder : trackDataHolderBindingList) {
            if (useRawCoordinates) {
                coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix();
            } else {
                coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getShiftedCoordinatesMatrix();
            }
            XYSeries xySeries = JFreeChartUtils.generateXYSeries(coordinatesMatrix);
            Track track = trackDataHolder.getTrack();
            int trackNumber = track.getTrackNumber();
            Well well = track.getWellHasImagingType().getWell();
            String key;
            key = "track " + trackNumber + ", well " + well;
            // we check here if the collection already contains this key
            int seriesIndex = xYSeriesCollection.getSeriesIndex(key);
            if (seriesIndex == -1) {
                key = "track " + trackNumber + ", well " + well;
            } else {
                // should be able to get the number of the series already present !!
                key = "track " + trackNumber + ", well " + well + ", " + (counter + 1);
                counter++;
            }
            xySeries.setKey(key);
            xYSeriesCollection.addSeries(xySeries);
        }
        return xYSeriesCollection;
    }

    /**
     * Scale the axes to the experiment coordinates ranges.
     *
     * @param chart
     */
    public void scaleAxesToExperiment(JFreeChart chart, boolean useRawData) {
        Double[][] coordinatesRanges;
        if (useRawData) {
            coordinatesRanges = singleCellPreProcessingController.getExperimentRawCoordinatesRanges();
        } else {
            coordinatesRanges = singleCellPreProcessingController.getExperimentShiftedCoordinatesRanges();
        }
        XYPlot xYPlot = chart.getXYPlot();
        Double[] xCoords = coordinatesRanges[0];
        Double[] yCoords = coordinatesRanges[1];
        xYPlot.getDomainAxis().setRange(new Range(yCoords[0], yCoords[1]));
        xYPlot.getRangeAxis().setRange(new Range(xCoords[0], xCoords[1]));
        JFreeChartUtils.setupTrackChart(chart);
    }

    /**
     * Generate the random track holders within a well.
     */
    private void generateRandomTrackDataHoldersForWell() {
        // get only the track holders for the selected well
        Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
        List<TrackDataHolder> trackHoldersForCurrentWell = getTrackHoldersForWell(singleCellPreProcessingController
                  .getCurrentCondition(), selectedWell);
        int numberTracksForCurrentWell = trackHoldersForCurrentWell.size();
        // update info with number of tracks for current selected well
        trackCoordinatesPanel.getTracksNumberWellTextField().setText("" + numberTracksForCurrentWell);
        // if the user does not write anything, number of tracks to be plotted is set to default
        String text = trackCoordinatesPanel.getRandomTracksNumberTextField().getText();
        // the default is set to 10, if possible, otherwise is less
        int defaultNumberOfTracks = getDefaultNumberOfTracks(numberTracksForCurrentWell);
        int randomTracksNumber = defaultNumberOfTracks;
        if (!text.isEmpty()) {
            try {
                randomTracksNumber = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage());
                String message = "Please insert a valid number of tracks!" + "\nDefault set back to: "
                          + defaultNumberOfTracks + " tracks";
                singleCellPreProcessingController.showMessage(message, "error setting number of tracks", JOptionPane.ERROR_MESSAGE);
            }
        }
        // the # of tracks that you want to plot need to be less or equal to # of tracks for the current well
        // else, show an info message and set the number back to default
        if (randomTracksNumber > numberTracksForCurrentWell) {
            String message = "This well has " + numberTracksForCurrentWell + " tracks" + "\nI cannot plot "
                      + randomTracksNumber + " tracks!" + "\nDefault set back to: " + defaultNumberOfTracks + " tracks";
            singleCellPreProcessingController.showMessage(message, "error in setting number of tracks", JOptionPane.WARNING_MESSAGE);
            randomTracksNumber = defaultNumberOfTracks;
        }
        // update text field
        trackCoordinatesPanel.getRandomTracksNumberTextField().setText("" + randomTracksNumber);
        // pick up randomly the track holders and add them to the list
        for (int j = 0; j < randomTracksNumber; j++) {
            // create a random number and take its int part
            Double random = Math.random() * numberTracksForCurrentWell;
            int intValue = random.intValue();
            TrackDataHolder randomTrackDataHolder = trackHoldersForCurrentWell.get(intValue);
            // make sure we do not use twice the same random track
            if (!trackDataHolderBindingList.contains(randomTrackDataHolder)) {
                trackDataHolderBindingList.add(randomTrackDataHolder);
            } else {
                j--;
            }
        }
    }

    /**
     * Generate the random track holders across random wells of a particular
     * condition.
     */
    private void generateRandomTrackHoldersForCondition(PlateCondition plateCondition) {
        // get track holders for current condition
        List<TrackDataHolder> trackHoldersForCurrentCondition = getTrackHoldersForCondition(plateCondition);
        int tracksNumberForCondition = trackHoldersForCurrentCondition.size();
        // if the user does not write anything, number of tracks to be plotted is set to default
        String text = trackCoordinatesPanel.getRandomTracksNumberTextField().getText();
        // the default is set to 10, if possible, otherwise is less
        int defaultNumberOfTracks = getDefaultNumberOfTracks(tracksNumberForCondition);
        int randomTracksNumber = defaultNumberOfTracks;
        if (!text.isEmpty()) {
            try {
                randomTracksNumber = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage());
                String message = "Please insert a valid number of tracks!" + "\nDefault set back to: "
                          + defaultNumberOfTracks + " tracks";
                singleCellPreProcessingController.showMessage(message, "error setting number of tracks", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (randomTracksNumber > tracksNumberForCondition) {
            String message = "This condition has " + tracksNumberForCondition + " tracks" + "\nI cannot plot "
                      + randomTracksNumber + " tracks!" + "\nDefault set back to: " + defaultNumberOfTracks + " tracks";
            singleCellPreProcessingController.showMessage(message, "error in setting number of tracks", JOptionPane.WARNING_MESSAGE);
            randomTracksNumber = defaultNumberOfTracks;
        }
        // update text field
        trackCoordinatesPanel.getRandomTracksNumberTextField().setText("" + randomTracksNumber);
        // pick up randomly the track holders and add them to the list
        for (int j = 0; j < randomTracksNumber; j++) {
            // create a random number and take its int part
            Double random = Math.random() * tracksNumberForCondition;
            int intValue = random.intValue();
            TrackDataHolder randomTrackDataHolder = trackHoldersForCurrentCondition.get(intValue);
            // make sure we do not use twice the same random track
            if (!trackDataHolderBindingList.contains(randomTrackDataHolder)) {
                trackDataHolderBindingList.add(randomTrackDataHolder);
            } else {
                j--;
            }
        }
    }

    /**
     * The default number of tracks to be plotted is 10; if the max available
     * number of tracks is less than 10, the default is decreased, until we get
     * the right number of tracks.
     *
     * @param maxTracks
     * @return the default number
     */
    private int getDefaultNumberOfTracks(int maxTracks) {
        int defaultNumber = 10;
        if (defaultNumber > maxTracks) {
            defaultNumber--;
        }
        return defaultNumber;
    }

    /**
     * From the entire track holders for the selected current condition, get
     * only the ones for the selected well.
     *
     * @param well
     * @return
     */
    private List<TrackDataHolder> getTrackHoldersForWell(PlateCondition plateCondition, Well well) {
        // first, get the track holders for the current condition
        List<TrackDataHolder> trackHoldersForCurrentCondition = getTrackHoldersForCondition(plateCondition);
        List<TrackDataHolder> trackHoldersForWell = new ArrayList<>();
        // then, get the selected well, and filter only the results from this well
        for (TrackDataHolder trackDataHolder : trackHoldersForCurrentCondition) {
            if (trackDataHolder.getTrack().getWellHasImagingType().getWell().equals(well)) {
                trackHoldersForWell.add(trackDataHolder);
            }
        }
        return trackHoldersForWell;
    }

    /**
     * Through the parent controller, get the track data holders for the current
     * condition.
     *
     * @return the track data holders
     */
    private List<TrackDataHolder> getTrackHoldersForCondition(PlateCondition plateCondition) {
        // through the map, we get the pre processing results for the current condition
        SingleCellConditionDataHolder singleCellConditionDataHolder = singleCellPreProcessingController
                  .getConditionDataHolder(plateCondition);
        return singleCellConditionDataHolder.getTrackDataHolders();
    }

    /**
     * For the current condition, generate the list with all the track data
     * holders.
     */
    private void generateAllTrackHoldersForCondition(PlateCondition plateCondition) {
        List<TrackDataHolder> trackHoldersForCurrentCondition = getTrackHoldersForCondition(plateCondition);
        trackDataHolderBindingList.addAll(trackHoldersForCurrentCondition);
    }

    /**
     * For a certain well, generate the list with all the track data holders.
     *
     * @param well
     */
    private void generateAllTrackHoldersForCurrentWell(PlateCondition plateCondition, Well well) {
        List<TrackDataHolder> trackHoldersForWell = getTrackHoldersForWell(plateCondition, well);
        trackDataHolderBindingList.addAll(trackHoldersForWell);
    }

    /**
     * Get the total number of tracks for the current condition.
     *
     * @return the number.
     */
    private int getTrackNumberForCondition(PlateCondition plateCondition) {
        return getTrackHoldersForCondition(plateCondition).size();
    }

    /**
     * Given a title and a xyseriesCollection, set the charts of the 2 main
     * chart panels.
     *
     * @param title
     * @param xYSeriesCollection
     */
    private void setChartsWithXyCollection(String title, XYSeriesCollection xYSeriesCollection) {
        boolean plotLines = plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().isSelected();
        boolean plotPoints = plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().isSelected();
        boolean showEndPoints = plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().isSelected();
        boolean useSingleColor = plotSettingsMenuBar.getUseSingleColorCheckBoxMenuItem().isSelected();
        Float lineWidth = plotSettingsMenuBar.getSelectedLineWidth();
        JFreeChart firstCoordinatesChart = ChartFactory.createXYLineChart(title, "x (µm)", "y (µm)",
                  xYSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        // check if we need to scale axes to condition or to experiment
        if (trackCoordinatesPanel.getScaleToConditionRadioButton().isSelected()) {
            scaleAxesToCondition(firstCoordinatesChart, singleCellPreProcessingController.getCurrentCondition());
        } else if (trackCoordinatesPanel.getScaleToExperimentRadioButton().isSelected()) {
            scaleAxesToExperiment(firstCoordinatesChart, trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton()
                      .isSelected());
        }
        JFreeChartUtils.setupTrackChart(firstCoordinatesChart);
        JFreeChart secondCoordinatesChart = ChartFactory.createXYLineChart(title, "x (µm)", "y (µm)",
                  xYSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupTrackChart(secondCoordinatesChart);
        TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines,
                  plotPoints, showEndPoints, getEndPoints(), -1, lineWidth, useSingleColor);
        trackXYLineAndShapeRenderer.setChosenColor(chosenColor);
        firstCoordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
        secondCoordinatesChart.getXYPlot().setRenderer(trackXYLineAndShapeRenderer);
        coordinatesChartPanel.setChart(firstCoordinatesChart);
        exploreTrackController.getCoordinatesChartPanel().setChart(secondCoordinatesChart);
        trackCoordinatesPanel.getCoordinatesParentPanel().revalidate();
        trackCoordinatesPanel.getCoordinatesParentPanel().repaint();
    }

    /**
     * Scale axes to the condition coordinates ranges.
     *
     * @param chart
     * @param plateCondition
     */
    private void scaleAxesToCondition(JFreeChart chart, PlateCondition plateCondition) {
        SingleCellConditionDataHolder preProcessingResults = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        boolean useRawData = trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().isSelected();
        Double[][] coordinatesRanges;
        if (useRawData) {
            coordinatesRanges = preProcessingResults.getRawCoordinatesRanges();
        } else {
            coordinatesRanges = preProcessingResults.getShiftedCoordinatesRanges();
        }
        XYPlot xYPlot = chart.getXYPlot();
        Double[] xCoords = coordinatesRanges[0];
        Double[] yCoords = coordinatesRanges[1];
        xYPlot.getDomainAxis().setRange(new Range(yCoords[0], yCoords[1]));
        xYPlot.getRangeAxis().setRange(new Range(xCoords[0], xCoords[1]));
    }

    /**
     * Swing Worker to plot all tracks together at once!
     */
    private class PlotAllTracksConditionSwingWorker extends SwingWorker<Void, Void> {

        private final boolean useRawCoordinates = trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton()
                  .isSelected();
        private XYSeriesCollection xYSeriesCollection;

        @Override
        protected Void doInBackground() throws Exception {
            singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            resetTracksList();
            generateAllTrackHoldersForCondition(singleCellPreProcessingController.getCurrentCondition());
            xYSeriesCollection = generateXYSeriesCollectionForPlot(useRawCoordinates);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                PlateCondition currentCondition = singleCellPreProcessingController.getCurrentCondition();
                // Plot Logic
                int conditionIndex = singleCellPreProcessingController.getPlateConditionList().indexOf(currentCondition) + 1;
                String chartTitle;
                if (useRawCoordinates) {
                    chartTitle = trackDataHolderBindingList.size() + " tracks - condition " + conditionIndex;
                } else {
                    chartTitle = trackDataHolderBindingList.size() + " tracks, coordinates shifted to (0, 0) - "
                              + "condition " + conditionIndex;
                }
                setChartsWithXyCollection(chartTitle, xYSeriesCollection);
                singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellPreProcessingController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Swing Worker to plot all track together!
     */
    private class PlotAllTracksWellSwingWorker extends SwingWorker<Void, Void> {

        final boolean useRawCoordinates = trackCoordinatesPanel.getUnshiftedCoordinatesRadioButton().isSelected();
        private final Well selectedWell = (Well) trackCoordinatesPanel.getWellsComboBox().getSelectedItem();
        private XYSeriesCollection xYSeriesCollection;

        @Override
        protected Void doInBackground() throws Exception {
            singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            resetTracksList();
            generateAllTrackHoldersForCurrentWell(singleCellPreProcessingController.getCurrentCondition(),
                      selectedWell);
            xYSeriesCollection = generateXYSeriesCollectionForPlot(useRawCoordinates);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // Plot Logic
                String chartTitle;
                if (useRawCoordinates) {
                    chartTitle = trackDataHolderBindingList.size() + " tracks - well " + selectedWell.toString();
                } else {
                    chartTitle = trackDataHolderBindingList.size() + " tracks,  coordinates shifted to (0, 0) - well "
                              + "" + selectedWell.toString();
                }
                setChartsWithXyCollection(chartTitle, xYSeriesCollection);
                singleCellPreProcessingController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                singleCellPreProcessingController.handleUnexpectedError(ex);
            }
        }
    }
}
