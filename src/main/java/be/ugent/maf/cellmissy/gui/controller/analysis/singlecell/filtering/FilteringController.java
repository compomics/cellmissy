/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.SingleCellPreProcessingController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.FilteringInfoDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.FilteringPanel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.UIManager;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A controller to take care of filtering/quality control - on single cell
 * trajectories.
 *
 * @author Paola
 */
@Component("filteringController")
public class FilteringController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FilteringController.class);
    // model
    private Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap;
    private List<Integer> originalNumberTracks;
    // view
    private FilteringPanel filteringPanel;
    private FilteringInfoDialog filteringInfoDialog;
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    @Autowired
    private MultipleCutOffFilteringController multipleCutOffFilteringController;
    @Autowired
    private SingleCutOffFilteringController singleCutOffFilteringController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init views
        filteringInfoDialog = new FilteringInfoDialog(singleCellPreProcessingController.getMainFrame(), true);
        // int main view
        initFilteringPanel();
        // int child controllers
        multipleCutOffFilteringController.init();
        singleCutOffFilteringController.init();
    }

    /**
     * Reset everything when cancelling analysis. Called by parent controller.
     */
    public void resetOnCancel() {
        filteringMap = null;
        filteringPanel.getMultipleCutOffRadioButton().setSelected(true);
        multipleCutOffFilteringController.resetOnCancel();
        singleCutOffFilteringController.resetOnCancel();
    }

    public FilteringPanel getFilteringPanel() {
        return filteringPanel;
    }

    public void showMessage(String message, String title, Integer messageType) {
        singleCellPreProcessingController.showMessage(message, title, messageType);
    }

    public void showWaitingDialog(String title) {
        singleCellPreProcessingController.showWaitingDialog(title);
    }

    public PlateCondition getCurrentCondition() {
        return singleCellPreProcessingController.getCurrentCondition();
    }

    public void setCursor(Cursor cursor) {
        singleCellPreProcessingController.setCursor(cursor);
    }

    public void controlGuiComponents(boolean enabled) {
        singleCellPreProcessingController.controlGuiComponents(enabled);
    }

    public SingleCellConditionDataHolder getConditionDataHolder(PlateCondition plateCondition) {
        return singleCellPreProcessingController.getConditionDataHolder(plateCondition);
    }

    public void hideWaitingDialog() {
        singleCellPreProcessingController.hideWaitingDialog();
    }

    public void handleUnexpectedError(Exception exception) {
        singleCellPreProcessingController.handleUnexpectedError(exception);
    }

    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName) {
        return singleCellPreProcessingController.estimateDensityFunction(data, kernelDensityEstimatorBeanName);
    }

    public String getKernelDensityEstimatorBeanName() {
        return singleCellPreProcessingController.getKernelDensityEstimatorBeanName();
    }

    public void plotRawKde(PlateCondition plateCondition) {
        multipleCutOffFilteringController.plotRawKdeMultipleCutOff(plateCondition);
        singleCutOffFilteringController.plotRawKdeSingleCutOff();
    }

    public void showMedianDisplInList() {
        singleCutOffFilteringController.showMedianDisplInList();
    }

    public Map<SingleCellConditionDataHolder, List<TrackDataHolder>> getFilteringMap() {
        return filteringMap;
    }

    public void setFilteringMap(Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap) {
        this.filteringMap = filteringMap;
    }

    public Map<PlateCondition, SingleCellConditionDataHolder> getPreProcessingMap() {
        return singleCellPreProcessingController.getPreProcessingMap();
    }

    public List<Integer> getOriginalNumberTracks() {
        return originalNumberTracks;
    }

    public void setOriginalNumberTracks(List<Integer> originalNumberTracks) {
        this.originalNumberTracks = originalNumberTracks;
    }
    
    /**
     * Get the mean displacement for a condition.
     *
     * @param plateCondition
     * @return
     */
    public Double getMedianDisplAcrossReplicates(PlateCondition plateCondition) {
        SingleCellConditionDataHolder conditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        Double[] instantaneousDisplacementsVector = conditionDataHolder.getInstantaneousDisplacementsVector();
        return AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(instantaneousDisplacementsVector)));
    }

    public void setMedianDisplForCondition(PlateCondition plateCondition) {
        multipleCutOffFilteringController.getMultipleCutOffPanel().getMedianDisplacReplTextField().
                setText("" + AnalysisUtils.roundThreeDecimals(getMedianDisplAcrossReplicates(plateCondition)));
    }

    public void setPercentileDispl(PlateCondition plateCondition) {
        SingleCellConditionDataHolder conditionDataHolder = singleCellPreProcessingController.getConditionDataHolder(plateCondition);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getPercentileDisplTextField().
                setText("" + AnalysisUtils.roundThreeDecimals(AnalysisUtils.computeQuantile(
                        ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(conditionDataHolder.getTrackDisplacementsVector())), 5)));
    }

    public void setMedianDisplForExperiment() {
        multipleCutOffFilteringController.getMultipleCutOffPanel().getMedianDisplacCondTextField().
                setText("" + AnalysisUtils.roundThreeDecimals(getMedianDisplAcrossCondition()));
        singleCutOffFilteringController.getSingleCutOffPanel().getMeanDisplacTextField().
                setText("" + AnalysisUtils.roundThreeDecimals(getMedianDisplAcrossCondition()));
    }

    public List<PlateCondition> getPlateConditions() {
        return singleCellPreProcessingController.getPlateConditionList();
    }

    public JList getConditionsList() {
        return singleCellPreProcessingController.getConditionsList();
    }

    /**
     * Estimate the raw density functions for the replicates of a single cell
     * condition data holder.
     *
     * @param singleCellConditionDataHolder
     * @return a list of a list of double containing the estimated density
     * functions.
     */
    public List<List<double[]>> estimateRawDensityFunction(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        String kernelDensityEstimatorBeanName = getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();
        singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().map((singleCellWellDataHolder)
                -> estimateDensityFunction(singleCellWellDataHolder.getTrackDisplacementsVector(),
                        kernelDensityEstimatorBeanName)).forEach((oneReplicateTrackDisplDensityFunction) -> {
            densityFunction.add(oneReplicateTrackDisplDensityFunction);
        });

        return densityFunction;
    }

    /**
     * Estimate the raw density functions for all conditions.
     *
     * @return a list of a list of double containing the estimated density
     * functions.
     */
    public List<List<double[]>> estimateRawDisplKDE() {
        String kernelDensityEstimatorBeanName = getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();
        getPreProcessingMap().values().stream().map((conditionDataHolder)
                -> conditionDataHolder.getInstantaneousDisplacementsVector()).map((instantaneousDisplacementsVector)
                -> estimateDensityFunction(instantaneousDisplacementsVector, kernelDensityEstimatorBeanName)).forEach((oneConditionDensityFunction) -> {
            densityFunction.add(oneConditionDensityFunction);
        });
        return densityFunction;
    }

    public List<List<double[]>> estimateRawSpeedKDE() {
        String kernelDensityEstimatorBeanName = getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();
        getPreProcessingMap().values().stream().map((conditionDataHolder)
                -> conditionDataHolder.getTrackSpeedsVector()).map((trackSpeedsVector)
                -> estimateDensityFunction(trackSpeedsVector, kernelDensityEstimatorBeanName)).forEach((oneConditionDensityFunction) -> {
            densityFunction.add(oneConditionDensityFunction);
        });
        return densityFunction;
    }

    /**
     * Generate the density function for a specific condition: takes the wells
     * in consideration.
     *
     * @param singleCellConditionDataHolder
     * @param densityFunctions
     * @return the dataset
     */
    public XYSeriesCollection generateDensityFunction(SingleCellConditionDataHolder singleCellConditionDataHolder, List<List<double[]>> densityFunctions) {
        XYSeriesCollection collection = new XYSeriesCollection();

        List<SingleCellWellDataHolder> singleCellWellDataHolders = singleCellConditionDataHolder.getSingleCellWellDataHolders();

        for (int j = 0; j < singleCellWellDataHolders.size(); j++) {
            // x values
            double[] xValues = densityFunctions.get(j).get(0);
            // y values
            double[] yValues = densityFunctions.get(j).get(1);
            XYSeries series = new XYSeries("" + singleCellWellDataHolders.get(j).getWell(), false);
            for (int z = 0; z < xValues.length; z++) {
                double x = xValues[z];
                double y = yValues[z];
                series.add(x, y);
            }
            collection.addSeries(series);
        }

//        int counter = 0;
//        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
//            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSingleCellAnalyzedSamplesPerWell(singleCellWellDataHolder.getWell());
//            if (numberOfSamplesPerWell == 1) {
//                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
//                    if (densityFunctions.get(i) != null) {
//                        // x values
//                        double[] xValues = densityFunctions.get(i).get(0);
//                        // y values
//                        double[] yValues = densityFunctions.get(i).get(1);
//                        XYSeries series = new XYSeries("" + singleCellWellDataHolder.getWell() + " " + counter, false);
//                        for (int j = 0; j < xValues.length; j++) {
//                            double x = xValues[j];
//                            double y = yValues[j];
//                            series.add(x, y);
//                        }
//                        collection.addSeries(series);
//                    }
//
//                }
//                counter += numberOfSamplesPerWell;
//            } else {
//                int label = 0;
//                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
//                    // x values
//                    double[] xValues = densityFunctions.get(i).get(0);
//                    // y values
//                    double[] yValues = densityFunctions.get(i).get(1);
//                    XYSeries series = new XYSeries("" + (singleCellWellDataHolder.getWell()) + ", " + (label + 1), false);
//                    for (int j = 0; j < xValues.length; j++) {
//                        double x = xValues[j];
//                        double y = yValues[j];
//                        series.add(x, y);
//                    }
//                    collection.addSeries(series);
//                    label++;
//                }
//                counter += numberOfSamplesPerWell;
//            }
//        }
        return collection;
    }

    /**
     * Generate density function for the overall experiment: takes conditions in
     * consideration.
     *
     * @param densityFunctions
     * @return the dataset
     */
    public XYSeriesCollection generateDensityFunction(List<List<double[]>> densityFunctions) {
        XYSeriesCollection collection = new XYSeriesCollection();
        List<SingleCellConditionDataHolder> list = new ArrayList<>(getPreProcessingMap().values());
        for (int i = 0; i < densityFunctions.size(); i++) {

            if (densityFunctions.get(i) != null) {
                // x values
                double[] xValues = densityFunctions.get(i).get(0);
                // y values
                double[] yValues = densityFunctions.get(i).get(1);
                XYSeries series = new XYSeries("" + list.get(i) + " ", false);
                for (int j = 0; j < xValues.length; j++) {
                    double x = xValues[j];
                    double y = yValues[j];
                    series.add(x, y);
                }
                collection.addSeries(series);
            }

        }
        return collection;
    }

    /**
     * Private classes and methods.
     */
    /**
     * Initialize the main view: the filtering panel (the two sub-views are kept
     * in two separate controllers).
     */
    private void initFilteringPanel() {
        // make a new view
        filteringPanel = new FilteringPanel();
        // make a new radio button group for the radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(filteringPanel.getSingleCutOffRadioButton());
        radioButtonGroup.add(filteringPanel.getMultipleCutOffRadioButton());
        // select the first one as default
        filteringPanel.getMultipleCutOffRadioButton().setSelected(true);

        // set icon for question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        ImageIcon scaledQuestionIcon = GuiUtils.getScaledIcon(questionIcon);
        filteringPanel.getQuestionButton().setIcon(scaledQuestionIcon);

        // action listeners
        // info button
        filteringPanel.getQuestionButton().addActionListener((ActionEvent e) -> {
            // pack and show info dialog
            GuiUtils.centerDialogOnFrame(singleCellPreProcessingController.getMainFrame(), filteringInfoDialog);
            filteringInfoDialog.setVisible(true);
        });

        // which criterium for the filtering?
        filteringPanel.getMultipleCutOffRadioButton().addActionListener((ActionEvent e) -> {
            // need to reset the conditions list back to active
            singleCellPreProcessingController.getConditionsList().setEnabled(true);
            // set as the current condition the first one in the list
            singleCellPreProcessingController.setCurrentCondition(singleCellPreProcessingController.getPlateConditionList().get(0));
            singleCellPreProcessingController.getAnalysisPlatePanel().setCurrentCondition(singleCellPreProcessingController.getPlateConditionList().get(0));
            singleCellPreProcessingController.getAnalysisPlatePanel().repaint();

            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) filteringPanel.getBottomPanel().getLayout();
            layout.show(filteringPanel.getBottomPanel(), filteringPanel.getMultipleCutOffParentPanel().getName());

            multipleCutOffFilteringController.plotRawKdeMultipleCutOff(getCurrentCondition());
            setMedianDisplForCondition(getCurrentCondition());
            setMedianDisplForExperiment();
        });

        filteringPanel.getSingleCutOffRadioButton().addActionListener((ActionEvent e) -> {
            // need to disable the conditions list
            singleCellPreProcessingController.getConditionsList().clearSelection();
            singleCellPreProcessingController.getConditionsList().setEnabled(false);
            singleCellPreProcessingController.setCurrentCondition(null);
            singleCellPreProcessingController.getAnalysisPlatePanel().setCurrentCondition(null);
            singleCellPreProcessingController.getAnalysisPlatePanel().repaint();
            // get the layout from the bottom panel and show the appropriate one
            CardLayout layout = (CardLayout) filteringPanel.getBottomPanel().getLayout();
            layout.show(filteringPanel.getBottomPanel(), filteringPanel.getSingleCutOffParentPanel().getName());

            singleCutOffFilteringController.plotRawKdeSingleCutOff();
            setMedianDisplForExperiment();
            singleCutOffFilteringController.showMedianDisplInList();

        });

        // add view to parent container
        singleCellPreProcessingController.getSingleCellAnalysisPanel().getFilteringParentPanel().add(filteringPanel, gridBagConstraints);
    }

    /**
     * Get mean displacement for the experiment.
     *
     * @return
     */
    private Double getMedianDisplAcrossCondition() {
        Map<PlateCondition, SingleCellConditionDataHolder> preProcessingMap = singleCellPreProcessingController.getPreProcessingMap();
        Double[] medianValues = new Double[preProcessingMap.size()];
        for (int i = 0; i < medianValues.length; i++) {
            PlateCondition condition = singleCellPreProcessingController.getPlateConditionList().get(i);
            if (!condition.isComputed()) {
                singleCellPreProcessingController.computeCondition(condition);
            }
            medianValues[i] = getMedianDisplAcrossReplicates(singleCellPreProcessingController.getPlateConditionList().get(i));
        }
        return AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(medianValues)));
    }

    /**
     * Transfer track and parameter data from original to new data holder. This
     * method is called after determining which tracks are retained after
     * filtering. Only the data from the retained tracks is transferred to the
     * return value.
     *
     * @param conditionDataHolder The original data holder with all tracks data.
     * @param retainedIndices List of indices of tracks that were retained after
     * filtering.
     * @return Data holder with tracks data of only retained tracks.
     */
    protected SingleCellConditionDataHolder transferFilteredData(SingleCellConditionDataHolder conditionDataHolder, List<Integer> retainedIndices) {
        SingleCellConditionDataHolder filteredCondDataHolder = new SingleCellConditionDataHolder(conditionDataHolder.getPlateCondition());
        //setup 14 new arrays for features of retained tracks, length of retainedIndices
        int newSize = retainedIndices.size();
        Double[] instantaneousDisplacementsVector = new Double[newSize];
        Double[] directionalityRatiosVector = new Double[newSize];
        Double[] medianDirectionalityRatiosVector = new Double[newSize];
        Double[] trackDisplacementsVector = new Double[newSize];
        Double[] trackSpeedsVector = new Double[newSize];
        Double[] cumulativeDistancesVector = new Double[newSize];
        Double[] euclideanDistancesVector = new Double[newSize];
        Double[] endPointDirectionalityRatios = new Double[newSize];
        Double[] displacementRatiosVector = new Double[newSize];
        Double[] outreachRatiosVector = new Double[newSize];
        Double[] turningAnglesVector = new Double[newSize];
        Double[] medianTurningAnglesVector = new Double[newSize];
        Double[] medianDirectionAutocorrelationsVector = new Double[newSize];
        ConvexHull[] convexHullsVector = new ConvexHull[newSize];
        for (int j = 0; j < retainedIndices.size(); j++) {
            int dhIndex = retainedIndices.get(j);
            // get index i from feature arrays in old cdh and set new one index j
            instantaneousDisplacementsVector[j] = conditionDataHolder.getInstantaneousDisplacementsVector()[dhIndex];
            directionalityRatiosVector[j] = conditionDataHolder.getDirectionalityRatiosVector()[dhIndex];
            medianDirectionalityRatiosVector[j] = conditionDataHolder.getMedianDirectionalityRatiosVector()[dhIndex];
            trackDisplacementsVector[j] = conditionDataHolder.getTrackDisplacementsVector()[dhIndex];
            trackSpeedsVector[j] = conditionDataHolder.getTrackSpeedsVector()[dhIndex];
            cumulativeDistancesVector[j] = conditionDataHolder.getCumulativeDistancesVector()[dhIndex];
            euclideanDistancesVector[j] = conditionDataHolder.getEuclideanDistancesVector()[dhIndex];
            endPointDirectionalityRatios[j] = conditionDataHolder.getEndPointDirectionalityRatios()[dhIndex];
//            displacementRatiosVector[j] = conditionDataHolder.getDisplacementRatiosVector()[dhIndex];   // currently null, not used
//            outreachRatiosVector[j] = conditionDataHolder.getOutreachRatiosVector()[dhIndex];           // currently null, not used
            turningAnglesVector[j] = conditionDataHolder.getTurningAnglesVector()[dhIndex];
            medianTurningAnglesVector[j] = conditionDataHolder.getMedianTurningAnglesVector()[dhIndex];
//            medianDirectionAutocorrelationsVector[j] = conditionDataHolder.getMedianDirectionAutocorrelationsVector()[dhIndex]; // currently null, not used
//            convexHullsVector[j] = conditionDataHolder.getConvexHullsVector()[dhIndex];                 // currently null, not used
        }
        //set feature arrays
        filteredCondDataHolder.setInstantaneousDisplacementsVector(instantaneousDisplacementsVector);
        filteredCondDataHolder.setDirectionalityRatiosVector(directionalityRatiosVector);
        filteredCondDataHolder.setMedianDirectionalityRatiosVector(medianDirectionalityRatiosVector);
        filteredCondDataHolder.setTrackDisplacementsVector(trackDisplacementsVector);
        filteredCondDataHolder.setTrackSpeedsVector(trackSpeedsVector);
        filteredCondDataHolder.setCumulativeDistancesVector(cumulativeDistancesVector);
        filteredCondDataHolder.setEuclideanDistancesVector(euclideanDistancesVector);
        filteredCondDataHolder.setEndPointDirectionalityRatios(endPointDirectionalityRatios);
//        filteredCondDataHolder.setDisplacementRatiosVector(displacementRatiosVector);
//        filteredCondDataHolder.setOutreachRatiosVector(outreachRatiosVector);
        filteredCondDataHolder.setTurningAnglesVector(turningAnglesVector);
        filteredCondDataHolder.setMedianTurningAnglesVector(medianTurningAnglesVector);
//        filteredCondDataHolder.setMedianDirectionAutocorrelationsVector(medianDirectionAutocorrelationsVector);
//        filteredCondDataHolder.setConvexHullsVector(convexHullsVector);
        //calculate new median speed
        filteredCondDataHolder.setMedianSpeed(AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(trackSpeedsVector))));
        return filteredCondDataHolder;
    }

}
