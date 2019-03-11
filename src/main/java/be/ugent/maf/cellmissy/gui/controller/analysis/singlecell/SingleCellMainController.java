/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.factory.KernelDensityEstimatorFactory;
import be.ugent.maf.cellmissy.analysis.factory.OutliersHandlerFactory;
import be.ugent.maf.cellmissy.analysis.singlecell.TrackCoordinatesUnitOfMeasurement;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.*;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AnalysisExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.MetadataSingleCellPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisInfoDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.ConditionsAnalysisListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.CoordinatesUnitOfMeasurementComboBoxRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import org.jdesktop.beansbinding.*;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Main Controller for single cell analysis.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("singleCellMainController")
public class SingleCellMainController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCellMainController.class);
    // model
    private Experiment experiment;
    private PlateCondition currentCondition;
    private double plateMedianSpeed;
    private double plateMADSpeed;
    private double conversionFactor;
    private ObservableList<Algorithm> algorithmBindingList;
    private ObservableList<ImagingType> imagingTypeBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    private Format format;
    private GridBagConstraints gridBagConstraints;
    private boolean cmsoData;
    // view
    private AnalysisExperimentPanel analysisExperimentPanel;
    private MetadataSingleCellPanel metadataSingleCellPanel;
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    private SingleCellAnalysisInfoDialog singleCellAnalysisInfoDialog;
    private WaitingDialog waitingDialog;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    // child controllers
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    @Autowired
    private SingleCellAnalysisController singleCellAnalysisController;
    // services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private WellService wellService;
    @Autowired
    private PlateService plateService;

    /**
     * Initialize controller
     */
    public void init() {
        //init views
        analysisExperimentPanel = new AnalysisExperimentPanel();
        metadataSingleCellPanel = new MetadataSingleCellPanel();
        singleCellAnalysisInfoDialog = new SingleCellAnalysisInfoDialog(cellMissyController.getCellMissyFrame(), true);
        // make a new waiting dialog here
        waitingDialog = new WaitingDialog(cellMissyController.getCellMissyFrame(), false);
        // set icon for info labels
        Icon informationIcon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledInfoIcon = GuiUtils.getScaledIcon(informationIcon);
        metadataSingleCellPanel.getInfoLabel1().setIcon(scaledInfoIcon);
        metadataSingleCellPanel.getInfoLabel2().setIcon(scaledInfoIcon);
        // set icon for question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        ImageIcon scaledQuestionIcon = GuiUtils.getScaledIcon(questionIcon);
        metadataSingleCellPanel.getQuestionButton().setIcon(scaledQuestionIcon);
        dataAnalysisPanel = new DataAnalysisPanel();
        analysisPlatePanel = new AnalysisPlatePanel();
        bindingGroup = new BindingGroup();
        cmsoData = false;
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        format = new DecimalFormat(PropertiesConfigurationHolder.getInstance().getString("dataFormat"));
        // init child controllers
        singleCellPreProcessingController.init();
        singleCellAnalysisController.init();
        // init other views
        initPlatePanel();
        initMainPanel();
        initMetadataSingleCellPanel();
        initDataAnalysisPanel();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    private Algorithm getSelectedAlgorithm() {
        return algorithmBindingList.get(metadataSingleCellPanel.getAlgorithmComboBox().getSelectedIndex());
    }

    private ImagingType getSelectedImagingType() {
        return imagingTypeBindingList.get(metadataSingleCellPanel.getImagingTypeComboBox().getSelectedIndex());
    }

    public TrackCoordinatesUnitOfMeasurement getCoordinatesUnitOfMeasurement() {
        return (TrackCoordinatesUnitOfMeasurement) metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox()
                .getSelectedItem();
    }

    public double getPlateMedianSpeed() {
        return plateMedianSpeed;
    }

    public double getPlateMADSpeed() {
        return plateMADSpeed;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    public PlateCondition getCurrentCondition() {
        return currentCondition;
    }

    public void setCurrentCondition(PlateCondition currentCondition) {
        this.currentCondition = currentCondition;
    }

    public PlateCondition getSelectedCondition() {
        return (PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue();
    }

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    public Format getFormat() {
        return format;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public AnalysisExperimentPanel getAnalysisExperimentPanel() {
        return analysisExperimentPanel;
    }

    public void setCmsoData(boolean cmsoData) {
        this.cmsoData = cmsoData;
    }

    public CellMissyFrame getCellMissyFrame() {
        return cellMissyController.getCellMissyFrame();
    }

    public AnalysisPlatePanel getAnalysisPlatePanel() {
        return analysisPlatePanel;
    }

    public SingleCellAnalysisPanel getSingleCellAnalysisPanel() {
        return singleCellPreProcessingController.getSingleCellAnalysisPanel();
    }

    public Map<PlateCondition, SingleCellConditionDataHolder> getPreProcessingMap() {
        return singleCellPreProcessingController.getPreProcessingMap();
    }

    public Map<SingleCellConditionDataHolder, List<TrackDataHolder>> getFilteringMap() {
        return singleCellPreProcessingController.getFilteringMap();
    }

    public void scaleAxesToExperiment(JFreeChart chart, boolean useRawData) {
        singleCellPreProcessingController.scaleAxesToExperiment(chart, useRawData);
    }

    public String getKernelDensityEstimatorBeanName() {
        return singleCellPreProcessingController.getKernelDensityEstimatorBeanName();
    }

    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName) {
        return singleCellPreProcessingController.estimateDensityFunction(data, kernelDensityEstimatorBeanName);
    }

    public XYSeriesCollection generateDensityFunction(List<List<double[]>> densityFunctions) {
        return singleCellPreProcessingController.generateDensityFunction(densityFunctions);
    }

    /**
     * Show the waiting dialog: set the title and center the dialog on the main
     * frame. Set the dialog to visible.
     *
     * @param title
     */
    public void showWaitingDialog(String title) {
        waitingDialog.setTitle(title);
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), waitingDialog);
        waitingDialog.setVisible(true);
    }

    /**
     * Hide the waiting dialog when a task has been processed.
     */
    public void hideWaitingDialog() {
        waitingDialog.setVisible(false);
    }

    /**
     * Show message through the main controller
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        cellMissyController.showMessage(message, title, messageType);
    }

    /**
     * Set cursor from main controller.
     *
     * @param cursor
     */
    public void setCursor(Cursor cursor) {
        cellMissyController.setCursor(cursor);
    }

    /**
     * Handle unexpected errors through the main controller.
     *
     * @param ex: the thrown exception
     */
    public void handleUnexpectedError(Exception ex) {
        cellMissyController.handleUnexpectedError(ex);
    }

    /**
     * The condition is loaded and plate view is refreshed with not imaged wells
     * highlighted in gray
     *
     * @param plateCondition
     */
    public void showNotImagedWells(PlateCondition plateCondition) {
        plateCondition.setLoaded(true);
        analysisPlatePanel.repaint();
    }

    /**
     * Show the wells analyzed for current condition: put a star inside each
     * well.
     *
     * @param plateCondition
     */
    public void showWellsForCurrentCondition(PlateCondition plateCondition) {
        analysisPlatePanel.setCurrentCondition(plateCondition);
        analysisPlatePanel.repaint();
    }

    /**
     * Using the wellService, fetch track points from DB for selected condition
     * Use only imaged wells, i.e. wells with a non empty collection of
     * WellHasImagingType.
     *
     * @param plateCondition
     */
    public void fetchTrackPoints(PlateCondition plateCondition) {
        Algorithm selectedAlgorithm = getSelectedAlgorithm();
        ImagingType selectedImagingType = getSelectedImagingType();
        List<Well> imagedWells = plateCondition.getImagedWells();
        imagedWells.stream().forEach((imagedWell) -> {
            String info = "** fetching cell track points for sample: " + imagedWell + " **";
            LOG.info(info);
            wellService.fetchTrackPoints(imagedWell, selectedAlgorithm.getAlgorithmid(), selectedImagingType
                    .getImagingTypeid(), cmsoData);
        });
    }

    /**
     * Using the wellService, fetch tracks from DB for a condition.
     *
     * @param plateCondition
     */
    public void fetchTracks(PlateCondition plateCondition) {
        LOG.info("* Fetching data for plate condition: " + plateCondition + " *");
        // fetch tracks for each well of condition
        plateCondition.getWellList().stream().forEach((well) -> {
            LOG.info("** fetching cell tracks for sample: " + well + " **");
            wellService.fetchTracks(well, getSelectedAlgorithm().getAlgorithmid(), getSelectedImagingType().getImagingTypeid(), cmsoData);
        });
    }

    /**
     * Update track points list with objects from a selected track in upper
     * table.
     *
     * @param plateCondition
     * @param selectedTrack
     */
    public void updateTrackPointsList(PlateCondition plateCondition, Track selectedTrack) {
        // clear the actual tracksList
        if (!singleCellPreProcessingController.getTrackPointsBindingList().isEmpty()) {
            singleCellPreProcessingController.getTrackPointsBindingList().clear();
        }
        // get only the wells that have been imaged
        plateCondition.getImagedWells().stream().forEach((well) -> {
            well.getWellHasImagingTypeList().stream().forEach((wellHasImagingType) -> {
                wellHasImagingType.getTrackList().stream().filter((track) -> (track.equals(selectedTrack))).forEach((track) -> {
                    track.getTrackPointList().stream().forEach((trackPoint) -> {
                        singleCellPreProcessingController.getTrackPointsBindingList().add(trackPoint);
                    });
                });
            });
        });
    }

    /**
     * Disable/Enable some GUI components. Mainly used in Swing workers. In the
     * background, the application is busy in fetching data from DB, no
     * interaction should be possible anymore with the GUI. In the done, the
     * components are set to enabled again.
     *
     * @param enabled, F if disabled, T if enabled
     */
    public void controlGuiComponents(boolean enabled) {
        dataAnalysisPanel.getConditionsList().setEnabled(enabled);
        analysisExperimentPanel.getNextButton().setEnabled(enabled);
        analysisExperimentPanel.getPreviousButton().setEnabled(enabled);
        analysisExperimentPanel.getCancelButton().setEnabled(enabled);
    }

    /**
     * Update information message in the bottom panel
     *
     * @param messageToShow
     */
    public void showInfoMessage(String messageToShow) {
        cellMissyController.updateInfoLabel(analysisExperimentPanel.getInfoLabel(), messageToShow);
    }

    /**
     * Check for card name when switching.
     */
    public void onCardSwitch() {
        String currentCardName = GuiUtils.getCurrentCardName(singleCellPreProcessingController
                .getSingleCellAnalysisPanel().getBottomPanel());
        PlateCondition selectedCondition = getSelectedCondition();
        switch (currentCardName) {
            case "inspectingDataPanel":
                // disable previous button
                analysisExperimentPanel.getPreviousButton().setEnabled(false);
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                // enable next button
                analysisExperimentPanel.getNextButton().setEnabled(true);
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getInspectingDataLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getDisplSpeedLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getCellTracksLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAngleDirectLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAnalysisLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().
                        getFilteringLabel());
                showInfoMessage("Tracks are shown for each well, together with (column, row) coordinates");
                singleCellPreProcessingController.showTracksInTable();
                break;
            case "cellTracksParentPanel":
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getCellTracksLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getDisplSpeedLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getInspectingDataLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAngleDirectLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAnalysisLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().
                        getFilteringLabel());
                showInfoMessage("Track Coordinates are shown for each well");
                singleCellPreProcessingController.updateTracksNumberInfo(selectedCondition);
                singleCellPreProcessingController.updateWellBindingList(selectedCondition);
                //check which button is selected for analysis:
                boolean useRawCoordinates = singleCellPreProcessingController.getTrackCoordinatesPanel()
                        .getUnshiftedCoordinatesRadioButton().isSelected();
                singleCellPreProcessingController.plotRandomTrackCoordinates(selectedCondition, useRawCoordinates);
                singleCellPreProcessingController.showPlottedTracksInTable();
                if (useRawCoordinates) {
                    singleCellPreProcessingController.showRawTrackCoordinatesInTable(selectedCondition);
                } else {
                    singleCellPreProcessingController.showShiftedTrackCoordinatesInTable(selectedCondition);
                }
                singleCellPreProcessingController.renderConditionGlobalView(selectedCondition);
                if (singleCellPreProcessingController.getTrackCoordinatesPanel().getTrackCoordinatesTabbedPane().getSelectedIndex() == 4 && !proceedToAnalysis()) {
                    singleCellPreProcessingController.enableAnalysis();
                }
                break;
            case "displSpeedParentPanel":
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getDisplSpeedLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getInspectingDataLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getCellTracksLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAngleDirectLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAnalysisLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().
                        getFilteringLabel());
                showInfoMessage("Single Cell Displacements and Speeds");
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                // check which button is selected for analysis
                if (singleCellPreProcessingController.getDisplSpeedPanel().getInstantaneousDisplRadioButton().isSelected()) {
                    singleCellPreProcessingController.showInstantaneousSpeedsInTable(selectedCondition);
                    singleCellPreProcessingController.plotDisplAndSpeedData(selectedCondition);
                } else if (singleCellPreProcessingController.getDisplSpeedPanel().getTrackDisplRadioButton().isSelected()) {
                    singleCellPreProcessingController.showTrackDisplInTable(selectedCondition);
                    singleCellPreProcessingController.plotDisplAndSpeedData(selectedCondition);
                } else if (singleCellPreProcessingController.getDisplSpeedPanel().getTrackSpeedsRadioButton().isSelected()) {
                    singleCellPreProcessingController.showTrackSpeedsInTable(selectedCondition);
                    singleCellPreProcessingController.plotDisplAndSpeedData(selectedCondition);
                }
//                else if (singleCellPreProcessingController.getDisplSpeedPanel().getMsdRadioButton().isSelected()) {
//                    singleCellPreProcessingController.showMsdInTable(selectedCondition);
//                    singleCellPreProcessingController.plotMsdData(selectedCondition);
//                }
                break;
            case "angleDirectParentPanel":
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAngleDirectLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getDisplSpeedLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getCellTracksLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getInspectingDataLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAnalysisLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().
                        getFilteringLabel());
                showInfoMessage("Single Cell Turning Angles and Directionality Measures");
                // check which button is selected for analysis
                if (singleCellPreProcessingController.getAngleDirectPanel().getInstTurnAngleRadioButton().isSelected()) {
                    singleCellPreProcessingController.showInstAngleInTable(selectedCondition);
                } else if (singleCellPreProcessingController.getAngleDirectPanel().getTrackTurnAngleRadioButton().isSelected()) {
                    singleCellPreProcessingController.showTrackAngleInTable(selectedCondition);
                }
//                else if (singleCellPreProcessingController.getAngleDirectPanel().getDynamicDirectRatioRadioButton().isSelected()) {
//
//                }
                singleCellPreProcessingController.plotAngleAndDirectData(selectedCondition);
                break;
            case "filteringParentPanel":
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().
                        getFilteringLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAnalysisLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAngleDirectLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getDisplSpeedLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getCellTracksLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getInspectingDataLabel());
                // enable next button (in case of pressing previous on analysis parent panel)
                analysisExperimentPanel.getNextButton().setEnabled(true);
                showInfoMessage("Single-cell trajectories filtering - Quality Control");
                singleCellPreProcessingController.setMeanDisplForExperiment();
                if (singleCellPreProcessingController.getFilteringPanel().getMultipleCutOffRadioButton().isSelected()) {
                    singleCellPreProcessingController.plotRawKde(selectedCondition);
                    singleCellPreProcessingController.setMeanDisplForCondition(selectedCondition);
                    singleCellPreProcessingController.setPercentileDispl(selectedCondition);
                } else {
                    singleCellPreProcessingController.showMeanDisplInList();
                }
                break;
            case "analysisParentPanel":
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAnalysisLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getAngleDirectLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getDisplSpeedLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getCellTracksLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                        .getInspectingDataLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().
                        getFilteringLabel());
                showInfoMessage("Conditions-based analysis");
                analysisPlatePanel.setCurrentCondition(null);
                analysisPlatePanel.repaint();
                analysisPlatePanel.revalidate();
                dataAnalysisPanel.getConditionsList().setEnabled(false);
                // see if some conditions still need to be processed
                if (!proceedToAnalysis()) { // I am pretty sure this is no longer needed !!!
                    singleCellPreProcessingController.enableAnalysis();
                }
                // check if the user wants to proceed with filtered data or not
                Object[] options = {"Yes", "No"};
                int choice = JOptionPane.showOptionDialog(null, "Proceed with filtered data? If NO, raw data will be used.", "", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                // if choice is 0 -- filtered data
                if (choice == 0) {
                    singleCellAnalysisController.setFilteredData(Boolean.TRUE);
                } else {
                    singleCellAnalysisController.setFilteredData(Boolean.FALSE);
                }
                // check which button is actually selected
                if (singleCellAnalysisController.getAnalysisPanel().getCellTracksRadioButton().isSelected()) {
                    singleCellAnalysisController.plotCellTracks();
                } else if (singleCellAnalysisController.getAnalysisPanel().getCellSpeedRadioButton().isSelected()) {
//                    singleCellAnalysisController.plotData();
                } else if (singleCellAnalysisController.getAnalysisPanel().getStatisticsRadioButton().isSelected()) {

                }
                break;

        }
    }

    /**
     *
     * @return
     */
    private boolean proceedToAnalysis() {
        return plateConditionList.stream().noneMatch((plateCondition) -> (!plateCondition.isComputed()));
    }

    /**
     * Compute median speed across the plate.
     */
    public void computePlateMedianSpeed() {
        List<Double> list = new ArrayList<>();
        for (PlateCondition condition : plateConditionList) {
            SingleCellConditionDataHolder conditionDataHolder = singleCellPreProcessingController.getPreProcessingMap().get(condition);
            Double[] trackSpeedsVector = conditionDataHolder.getTrackSpeedsVector();
            list.addAll(Arrays.asList(AnalysisUtils.excludeNullValues(trackSpeedsVector)));
        }
        plateMedianSpeed = AnalysisUtils.computeMedian(list.stream().mapToDouble(i -> i).toArray());
    }

    /**
     * Compute the MAD speed across the plate.
     */
    public void computePlateMADSpeed() {
        List<Double> list = new ArrayList<>();
        for (PlateCondition condition : plateConditionList) {
            SingleCellConditionDataHolder conditionDataHolder = singleCellPreProcessingController.getPreProcessingMap().get(condition);
            double medianSpeed = conditionDataHolder.getMedianSpeed();
            list.add(medianSpeed);
        }
        plateMADSpeed = AnalysisUtils.scaleMAD(list.stream().mapToDouble(i -> i).toArray());
    }

    /**
     * Initialize plate panel view
     */
    private void initPlatePanel() {
        //show as default a 96 plate format
        Dimension parentDimension = dataAnalysisPanel.getAnalysisPlateParentPanel().getSize();
        analysisPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        dataAnalysisPanel.getAnalysisPlateParentPanel().add(analysisPlatePanel, gridBagConstraints);
        dataAnalysisPanel.getAnalysisPlateParentPanel().repaint();
    }

    /**
     * get Card Layout
     *
     * @return
     */
    private CardLayout getCardLayout() {
        return (CardLayout) singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel().getLayout();
    }

    /**
     * Initialize main panel
     */
    private void initMainPanel() {
        // be sure buttons are disabled at the beginning
        analysisExperimentPanel.getStartButton().setEnabled(false);
        analysisExperimentPanel.getNextButton().setEnabled(false);
        analysisExperimentPanel.getPreviousButton().setEnabled(false);
        analysisExperimentPanel.getCancelButton().setEnabled(false);
        //hide progress bar at first time
        analysisExperimentPanel.getFetchAllConditionsProgressBar().setVisible(false);
        analysisExperimentPanel.getFetchAllConditionsProgressBar().setStringPainted(true);
        String message = "Please select a project and an experiment to visualize and analyse single cell data.";
        showInfoMessage(message);
        // action listener on start button: this is switching the views in order to start the analysis
        analysisExperimentPanel.getStartButton().addActionListener((ActionEvent e) -> {
            analysisExperimentPanel.getStartButton().setEnabled(false);
            analysisExperimentPanel.getCancelButton().setEnabled(true);
            // switch between the two panels
            GuiUtils.switchChildPanels(analysisExperimentPanel.getTopPanel(), dataAnalysisPanel,
                    metadataSingleCellPanel);
            analysisExperimentPanel.getTopPanel().repaint();
            analysisExperimentPanel.getTopPanel().revalidate();
            getCardLayout().first(singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel());
            onCardSwitch();
            // update experiment info
            dataAnalysisPanel.getExperimentNumberTextField().setText(experiment.toString());
            dataAnalysisPanel.getTimeFramesNumberTextField().setText("" + experiment.getTimeFrames());
            dataAnalysisPanel.getDatasetTextField().setText(getSelectedAlgorithm().getAlgorithmName());
            dataAnalysisPanel.getImagingTypeTextField().setText(getSelectedImagingType().getName());
            showInfoMessage("CellMissy is retrieving data and computing... please wait.");
            // now we do all the computations, and then the user can start selecting conditions and look at them
            singleCellPreProcessingController.preProcessExperiment(experiment);
        });

        // action listener on previous button
        analysisExperimentPanel.getPreviousButton().addActionListener((ActionEvent e) -> {
            // go back of one step
            getCardLayout().previous(singleCellPreProcessingController.getSingleCellAnalysisPanel()
                    .getBottomPanel());
            onCardSwitch();
        });

        // action listener on next button
        analysisExperimentPanel.getNextButton().addActionListener((ActionEvent e) -> {
            // go forward of one step
            getCardLayout().next(singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel());
            onCardSwitch();
            if (!analysisExperimentPanel.getPreviousButton().isEnabled()) {
                analysisExperimentPanel.getPreviousButton().setEnabled(true);
            }
        });

        // action listener on cancel button
        analysisExperimentPanel.getCancelButton().addActionListener((ActionEvent e) -> {
            // warn the user and reset everything
            Object[] options = {"Yes", "No"};
            int showOptionDialog = JOptionPane.showOptionDialog(null, "Current analysis won't be saved. "
                    + "Continue?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    options,
                    options[1]);
            if (showOptionDialog == 0) {
                // reset everything
                cmsoData = false;
                onCancel();
            }
        });

        cellMissyController.getCellMissyFrame().getSingleCellAnalysisParentPanel().add(analysisExperimentPanel,
                gridBagConstraints);
    }

    /**
     * Initialize metadata area panel
     */
    private void initMetadataSingleCellPanel() {
        metadataSingleCellPanel.getPurposeTextArea().setLineWrap(true);
        metadataSingleCellPanel.getPurposeTextArea().setWrapStyleWord(true);
        metadataSingleCellPanel.getProjectDescriptionTextArea().setLineWrap(true);
        metadataSingleCellPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);
        //init projectJList: find all the projects from DB and sort them
        List<Project> allProjects = projectService.findAll();
        Collections.sort(allProjects);
        ObservableList<Project> projectBindingList = ObservableCollections.observableList(allProjects);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                projectBindingList, metadataSingleCellPanel.getProjectsList());
        bindingGroup.addBinding(jListBinding);

        //init algorithms combobox
        algorithmBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, algorithmBindingList, metadataSingleCellPanel.getAlgorithmComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init imagingtypes combo box
        imagingTypeBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                imagingTypeBindingList, metadataSingleCellPanel.getImagingTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();

        // add track coordinates unit of measure to combo box
        for (TrackCoordinatesUnitOfMeasurement trackCoordinatesUnitOfMeasurement : TrackCoordinatesUnitOfMeasurement
                .values()) {
            metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().addItem(trackCoordinatesUnitOfMeasurement);
        }

        metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().setRenderer(new CoordinatesUnitOfMeasurementComboBoxRenderer());
        // set default unit of measurement: pixels
        // then a conversion is applied to go to micrometers !
        metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().setSelectedItem(TrackCoordinatesUnitOfMeasurement.PIXELS);

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments performed for that project
        metadataSingleCellPanel.getProjectsList().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                // retrieve selected project
                Project selectedProject = (Project) metadataSingleCellPanel.getProjectsList().getSelectedValue();
                if (selectedProject != null) {
                    if (experiment == null || !selectedProject.equals(experiment.getProject())
                            || experimentBindingList.isEmpty()) {
                        // project is being selected for the first time
                        onSelectedProject(selectedProject);
                    }
                }
            }
        });

        //when an experiment is selected, show algorithms and imaging types used for that experiment
        //show also conditions in the Jlist behind and plate view according to the conditions setup
        metadataSingleCellPanel.getExperimentsList().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                // retrieve selected experiment
                Experiment selectedExperiment = (Experiment) metadataSingleCellPanel.getExperimentsList()
                        .getSelectedValue();
                if (selectedExperiment != null) {
                    if (experiment == null || !selectedExperiment.equals(experiment)) {
                        onSelectedExperiment(selectedExperiment);
                    }
                }
            }
        });
        // add action Listener to the question/info button
        metadataSingleCellPanel.getQuestionButton().addActionListener((ActionEvent e) -> {
            // pack and show info dialog
            GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), singleCellAnalysisInfoDialog);
            singleCellAnalysisInfoDialog.setVisible(true);
        });

        // bind information fields
        // exp user
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel
                .getExperimentsList(), BeanProperty.create("selectedElement.user.firstName"), metadataSingleCellPanel
                .getUserTextField(), BeanProperty.create("text"), "experimentuserbinding");
        bindingGroup.addBinding(binding);
        // exp purpose
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel
                .getExperimentsList(), BeanProperty.create("selectedElement.purpose"), metadataSingleCellPanel
                .getPurposeTextArea(), BeanProperty.create("text"), "experimentpurposebinding");
        bindingGroup.addBinding(binding);
        // instrument
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel
                .getExperimentsList(), BeanProperty.create("selectedElement.instrument.name"),
                metadataSingleCellPanel.getInstrumentTextField(), BeanProperty.create("text"), "instrumentbinding");
        bindingGroup.addBinding(binding);
        // exp time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel
                .getExperimentsList(), BeanProperty.create("selectedElement.timeFrames"), metadataSingleCellPanel
                .getTimeFramesTextField(), BeanProperty.create("text"), "experimentimeframesbinding");
        bindingGroup.addBinding(binding);
        // do the binding
        bindingGroup.bind();

        // add the analysis preferences to the comboboxes
        // these values are read from the spring XML config file
        // get all the outliers correction and detection algoritms from the factory
        Set<String> outliersHandlersBeanNames = OutliersHandlerFactory.getInstance().getOutliersHandlersBeanNames();
        outliersHandlersBeanNames.stream().forEach((outliersAlgorithm) -> {
            metadataSingleCellPanel.getOutliersAlgorithmsComboBox().addItem(outliersAlgorithm);
        });

        // add the action listener
        metadataSingleCellPanel.getOutliersAlgorithmsComboBox().addActionListener((ActionEvent e) -> {
            String selectedOutliersAlgorithm = metadataSingleCellPanel.getOutliersAlgorithmsComboBox().getSelectedItem().toString();
            singleCellPreProcessingController.setOutliersHandlerBeanName(selectedOutliersAlgorithm);
        });
        // set as default the first one
        metadataSingleCellPanel.getOutliersAlgorithmsComboBox().setSelectedIndex(0);

        // do exactly the same for the kernel density estimation and the distance metric
        Set<String> kernelDensityEstimatorsBeanNames = KernelDensityEstimatorFactory.getInstance().getKernelDensityEstimatorsBeanNames();
        kernelDensityEstimatorsBeanNames.stream().forEach((estimatorName) -> {
            metadataSingleCellPanel.getKernelDensityEstimatorsComboBox().addItem(estimatorName);
        });

        // add the action listener
        metadataSingleCellPanel.getKernelDensityEstimatorsComboBox().addActionListener((ActionEvent e) -> {
            String selectedKDEAlgorithm = metadataSingleCellPanel.getKernelDensityEstimatorsComboBox().getSelectedItem().toString();
            singleCellPreProcessingController.setKernelDensityEstimatorBeanName(selectedKDEAlgorithm);
        });
        // set as default the first one
        metadataSingleCellPanel.getKernelDensityEstimatorsComboBox().setSelectedIndex(0);

        analysisExperimentPanel.getTopPanel().add(metadataSingleCellPanel, gridBagConstraints);
    }

    /**
     * Initialize data analysis panel
     */
    private void initDataAnalysisPanel() {
        //when a certain condition is selected, fetch tracks for each well of the condition
        dataAnalysisPanel.getConditionsList().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                PlateCondition selectedCondition = (PlateCondition) dataAnalysisPanel.getConditionsList()
                        .getSelectedValue();
                if (selectedCondition != null) {
                    onSelectedCondition(selectedCondition);
                }
            }
        });
    }

    /**
     * On cancel: reset views
     */
    private void onCancel() {
        String message = "Please select a project and an experiment to visualize and analyse single cell data.";
        showInfoMessage(message);
        algorithmBindingList.clear();
        imagingTypeBindingList.clear();
        // clear plate conditions list, if not null
        if (plateConditionList != null) {
            plateConditionList.clear();
        }
        // clear selection on lists
        metadataSingleCellPanel.getProjectsList().clearSelection();
        metadataSingleCellPanel.getExperimentsList().clearSelection();
        // set text area to empty field
        metadataSingleCellPanel.getProjectDescriptionTextArea().setText("");
        singleCellPreProcessingController.getPreProcessingMap().clear();
        currentCondition = null;
        experiment = null;
        GuiUtils.switchChildPanels(analysisExperimentPanel.getTopPanel(), metadataSingleCellPanel, dataAnalysisPanel);
        analysisExperimentPanel.getTopPanel().repaint();
        analysisExperimentPanel.getTopPanel().revalidate();
        resetExperimentMetadataFields();
        analysisExperimentPanel.getStartButton().setEnabled(false);
        analysisExperimentPanel.getNextButton().setEnabled(false);
        analysisExperimentPanel.getCancelButton().setEnabled(false);
        analysisExperimentPanel.getPreviousButton().setEnabled(false);
        if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
            experimentBindingList.clear();
        }
        // reset subcontrollers
        singleCellAnalysisController.resetOnCancel();
        singleCellPreProcessingController.resetOnCancel();
    }

    /**
     * Reset text of experiment metadata fields
     */
    private void resetExperimentMetadataFields() {
        metadataSingleCellPanel.getUserTextField().setText("");
        metadataSingleCellPanel.getInstrumentTextField().setText("");
        metadataSingleCellPanel.getPurposeTextArea().setText("");
        metadataSingleCellPanel.getTimeFramesTextField().setText("");
    }

    /**
     * Action on selected project, find all relative performed experiments, if
     * any.
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        // clear up imaging and algorithm lists, if not empty
        if (!imagingTypeBindingList.isEmpty()) {
            imagingTypeBindingList.clear();
        }

        if (!algorithmBindingList.isEmpty()) {
            algorithmBindingList.clear();
        }
        // show project description
        String projectDescription = selectedProject.getProjectDescription();
        metadataSingleCellPanel.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments, fetch them from DB and then sort them
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid,
                ExperimentStatus.PERFORMED);
        if (experimentList != null) {
            Collections.sort(experimentList);
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                    experimentBindingList, metadataSingleCellPanel.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();// check if the user has privileges on the selected project
            // if not, show a message and disable the experiments list
            if (!userHasPrivileges(selectedProject)) {
                String message = "Sorry, you don't have enough privileges for the selected project!";
                cellMissyController.showMessage(message, "no enough privileges", JOptionPane.WARNING_MESSAGE);
                metadataSingleCellPanel.getExperimentsList().setEnabled(false);
            } else {
                metadataSingleCellPanel.getExperimentsList().setEnabled(true);
            }
        } else {
            String message = "There are no experiments performed yet for this project!";
            cellMissyController.showMessage(message, "no experiments found", JOptionPane.INFORMATION_MESSAGE);
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }

    /**
     * Does the current user have privileges on the current project?
     *
     * @param project
     * @return true or false
     */
    private boolean userHasPrivileges(Project project) {
        boolean hasPrivileges = false;
        // get current user from main controller
        User currentUser = cellMissyController.getCurrentUser();
        // check for his/her role
        // ADMIN user: return true
        if (currentUser.getRole().equals(Role.ADMIN_USER)) {
            hasPrivileges = true;
        } else {
            // we have a STANDARD user
            // we need to check if he's involved in the selected project
            for (ProjectHasUser projectHasUser : project.getProjectHasUserList()) {
                if (projectHasUser.getUser().equals(currentUser)) {
                    hasPrivileges = true;
                    break;
                }
            }
        }
        return hasPrivileges;
    }

    /**
     * Action on selected experiment, retrieve plate conditions and repaint
     * plate panel.
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        proceedToAnalysis(selectedExperiment, null, null);
    }

    /**
     * Action on selected condition: check if computations were already perfomed
     * on the selected condition. If this is the case, skip the computations,
     * else just go for it. already
     *
     * @param selectedCondition
     */
    private void onSelectedCondition(PlateCondition selectedCondition) {
        // first check that this condition is in the map, i.e. it has some data!
        if (singleCellPreProcessingController.getConditionDataHolder(selectedCondition) != null) {

            // empty the track points list if not empty
            if (!singleCellPreProcessingController.getTrackPointsBindingList().isEmpty()) {
                singleCellPreProcessingController.getTrackPointsBindingList().clear();
            }
            if (!singleCellPreProcessingController.getTracksBindingList().isEmpty()) {
                // since we are moving from one condition to another one,
                // we clear the list of tracks to plot, if it's not empty
                if (!singleCellPreProcessingController.getTrackDataHolderBindingList().isEmpty()) {
                    singleCellPreProcessingController.getTrackDataHolderBindingList().clear();
                }
            }
            // and we finally generate the random tracks to plot again
            // note that this is not done on the card switch method itself, because there we want
            // to keep the same random tracks every time we switch from one view to another one.
            singleCellPreProcessingController.generateRandomTrackDataHolders(singleCellPreProcessingController
                    .getCategoryToPlot(), selectedCondition);
            // update the tracks list for the current condition
            updateTracksList(selectedCondition);
            //Select the first row of the table to show first track as default
            singleCellPreProcessingController.getSingleCellAnalysisPanel().getTracksTable()
                    .setRowSelectionInterval(0, 0);

            // if we are clicking for the first time, current condition is still null
            // check also that we are not clicking again the same condition
            if (currentCondition == null || !currentCondition.equals(selectedCondition)) {
                // if computations for the condition were not performed yet, do so now
                if (!selectedCondition.isComputed()) {
                    singleCellPreProcessingController.operateOnCondition(selectedCondition);
                } else {
                    // update GUI according to current view on the Card Layout
                    onCardSwitch();
                    // the condition is loaded, and plate view is refreshed
                    showNotImagedWells(selectedCondition);
                    showWellsForCurrentCondition(selectedCondition);
                }
            }
            currentCondition = selectedCondition;
            singleCellPreProcessingController.showTracksInTable();
        } else {
            // inform the user and ignore the selection
            showInfoMessage("Condition: " + selectedCondition + " was not imaged/processed! Select another condition!");
            showMessage("Condition: " + selectedCondition + " was not imaged/processed!\nNo computations to"
                    + " perform!", "no data to show", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Proceed with the analysis of the data, given a chosen experiment to
     * analyze.
     *
     * @param selectedExperiment
     */
    public void proceedToAnalysis(Experiment selectedExperiment, List<Algorithm> cmsoAlgos, List<ImagingType> cmsoImgT) {
        // clear current lists
        if (!imagingTypeBindingList.isEmpty()) {
            imagingTypeBindingList.clear();
        }
        if (!algorithmBindingList.isEmpty()) {
            algorithmBindingList.clear();
        }
        // set experiment
        experiment = selectedExperiment;
        // compute the conversion factor
        computeConversionFactor(experiment);
        // init a new list of plate conditions
        plateConditionList = new ArrayList<>();
        plateConditionList.addAll(experiment.getPlateConditionList());
        Dimension parentDimension = dataAnalysisPanel.getAnalysisPlateParentPanel().getSize();
        analysisPlatePanel.initPanel(experiment.getPlateFormat(), parentDimension);
        // repaint plate panel
        analysisPlatePanel.setExperiment(experiment);
        analysisPlatePanel.repaint();
        //show conditions JList
        showConditionsList();
        // show algorithms and imaging types
        plateConditionList.stream().forEach((plateCondition) -> {
            plateCondition.getWellList().stream().map((well) -> {
                List<Algorithm> algorithms = wellService.findAlgosByWellId(well.getWellid());
                if (algorithms != null) {
                    algorithms.stream().filter((algorithm) -> (!algorithmBindingList.contains(algorithm))).forEach((algorithm) -> {
                        algorithmBindingList.add(algorithm);
                    });
                } else if (cmsoAlgos != null) {
                    for (Algorithm algo : cmsoAlgos) {
                        algorithmBindingList.add(algo);
                    }
                }
                List<ImagingType> imagingTypes = wellService.findImagingTypesByWellId(well.getWellid());
                return imagingTypes;
            }).filter((imagingTypes) -> (imagingTypes != null)).forEach((imagingTypes) -> {
                imagingTypes.stream().filter((imagingType) -> (imagingType != null && !imagingTypeBindingList.contains(imagingType))).forEach((imagingType) -> {
                    imagingTypeBindingList.add(imagingType);
                });
            });
        });
        if (cmsoImgT != null) {
            for (ImagingType imgT : cmsoImgT) {
                imagingTypeBindingList.add(imgT);
            }
        }
        //init map with conditions and results holders
        singleCellPreProcessingController.initMapWithConditions();
        //set selected algorithm to the first of the list
        metadataSingleCellPanel.getAlgorithmComboBox().setSelectedIndex(0);
        //set selected imaging types to the first of the list
        metadataSingleCellPanel.getImagingTypeComboBox().setSelectedIndex(0);
        // enable start button
        analysisExperimentPanel.getStartButton().setEnabled(true);
    }

    /**
     * Compute the conversion factor according to coordinates unit of
     * measurement and experiment magnification.
     *
     * @return
     */
    private void computeConversionFactor(Experiment experiment) {
        // by default, conversion factor is equal to 1
        // this is the case of having imported micrometers results to the DB
        conversionFactor = 1;
        // get the actual unit of measurement: if its pixels, override the conversion factor
        TrackCoordinatesUnitOfMeasurement coordinatesUnitOfMeasurement = (TrackCoordinatesUnitOfMeasurement) metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().getSelectedItem();
        if (coordinatesUnitOfMeasurement.equals(TrackCoordinatesUnitOfMeasurement.PIXELS)) {
            // conversion factor needs to be set according to conversion factor of instrument and magnification used
            // actual conversion factor = instrument conversion factor x magnification / 10
            Magnification magnification = experiment.getMagnification();
            double instrumentConversionFactor = experiment.getInstrument().getConversionFactor();
            double magnificationValue = magnification.getMagnificationValue();
            conversionFactor = instrumentConversionFactor * magnificationValue / 10;
        }
    }

    /**
     * Update track list with objects from actual selected condition.
     *
     * @param plateCondition
     */
    private void updateTracksList(PlateCondition plateCondition) {
        // clear the actual tracksList
        if (!singleCellPreProcessingController.getTracksBindingList().isEmpty()) {
            singleCellPreProcessingController.getTracksBindingList().clear();
        }
        // get only the wells that have been imaged
        List<Well> imagedWells = plateCondition.getImagedWells();
        imagedWells.stream().map((well) -> well.getWellHasImagingTypeList()).forEach((wellHasImagingTypeList) -> {
            wellHasImagingTypeList.stream().map((wellHasImagingType) -> wellHasImagingType.getTrackList()).forEach((trackList) -> {
                trackList.stream().forEach((track) -> {
                    singleCellPreProcessingController.getTracksBindingList().add(track);
                });
            });
        });
    }

    /**
     * Update conditions list for current experiment.
     */
    private void showConditionsList() {
        //set cell renderer for the List
        dataAnalysisPanel.getConditionsList().setCellRenderer(new ConditionsAnalysisListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                plateConditionBindingList, dataAnalysisPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

}
