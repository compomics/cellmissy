/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.view.renderer.table.SingleCellDataTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.ConvexHullTableModel;
import be.ugent.maf.cellmissy.gui.view.table.model.TrackDataHolderTableModel;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
class TracksClassifierFrame extends javax.swing.JFrame {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TracksClassifierFrame.class);
    private final BindingGroup bindingGroup;
    private ObservableList<TrackDataHolder> trackDatasetList;
    private final GridBagConstraints gridBagConstraints;
    private ObservableList<TrackDataHolder> trackDataHolderBindingList;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private Experiment experiment;
    private final static String newLine = "\n";
    private ChartPanel convexHullChartPanel;
    private final ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
    private final ProjectService projectService = context.getBean("projectService", ProjectService.class);
    private final ExperimentService experimentService = context.getBean("experimentService", ExperimentService.class);
    private final WellService wellService = context.getBean("wellService", WellService.class);
    private final SingleCellPreProcessor singleCellPreProcessor = (SingleCellPreProcessor) context.getBean("singleCellPreProcessor");

    /**
     * Creates new form TracksClassifierFrame
     */
    private TracksClassifierFrame() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initComponents();
        initFrame();
        // set background to white
        this.getContentPane().setBackground(new Color(255, 255, 255));
        UIManager.put("nimbusBase", Color.lightGray);      // Base color
        UIManager.put("nimbusBlueGrey", Color.lightGray);  // BlueGrey
        UIManager.put("control", Color.white);         // Control
        UIManager.put("OptionPane.background", Color.white); // Background for option pane
        UIManager.put("info", Color.white); // Background for tooltip texts (info class)
    }

    /**
     * Initialize the frame, with all the GUI components.
     */
    private void initFrame() {
        // button group for the labels
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(label0RadioButton);
        buttonGroup.add(label1RadioButton);
        buttonGroup.add(label2RadioButton);
        buttonGroup.add(label3RadioButton);
        // select label 0 by default
        label0RadioButton.setSelected(true);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        expTextArea.setLineWrap(true);
        expTextArea.setWrapStyleWord(true);
        //init projectJList: find all the projects from DB and sort them
        List<Project> allProjects = projectService.findAll();
        Collections.sort(allProjects);
        projectBindingList = ObservableCollections.observableList(allProjects);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, projectsList);
        bindingGroup.addBinding(jListBinding);
        // init jlist binding: track data holders
        trackDatasetList = ObservableCollections.observableList(new ArrayList<TrackDataHolder>());
        JListBinding jListBinding2 = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, trackDatasetList, datasetList);
        bindingGroup.addBinding(jListBinding2);
        trackDataHolderBindingList = ObservableCollections.observableList(new ArrayList<TrackDataHolder>());
        JListBinding jListBinding3 = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, trackDataHolderBindingList, tracksList);
        bindingGroup.addBinding(jListBinding3);
        // do the binding
        bindingGroup.bind();

        convexHullChartPanel = new ChartPanel(null);
        convexHullChartPanel.setOpaque(false);
        convexHullGraphPanel.add(convexHullChartPanel, gridBagConstraints);
        trackTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        trackTable.getTableHeader().setReorderingAllowed(false);
        convexHullTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        convexHullTable.getTableHeader().setReorderingAllowed(false);

        //when a project from the list is selected, show all experiments performed for that project
        projectsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    Project selectedProject = (Project) projectsList.getSelectedValue();
                    if (selectedProject != null) {
                        onSelectedProject(selectedProject);
                    }
                }
            }
        });

        // when an experiment is selected, fetch all the data and fill in tracks list
        experimentsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected experiment
                    Experiment selectedExperiment = (Experiment) experimentsList.getSelectedValue();
                    if (selectedExperiment != null) {
                        if (experiment == null || !selectedExperiment.equals(experiment)) {
                            onSelectedExperiment(selectedExperiment);
                        }
                    }
                }
            }
        });

        //show data for current track
        tracksList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedTrackIndex = tracksList.getSelectedIndex();
                    if (selectedTrackIndex != -1) {
                        TrackDataHolder selectedTrackDataHolder = trackDataHolderBindingList.get(selectedTrackIndex);
                        // update model for the track table
                        trackTable.setModel(new TrackDataHolderTableModel(selectedTrackDataHolder));
                        SingleCellDataTableRenderer singleCellDataTableRenderer = new SingleCellDataTableRenderer(new DecimalFormat("###.###"));
                        for (int i = 0; i < trackTable.getColumnCount(); i++) {
                            trackTable.getColumnModel().getColumn(i).setCellRenderer(singleCellDataTableRenderer);
                        }
                        for (int i = 0; i < trackTable.getColumnCount(); i++) {
                            GuiUtils.packColumn(trackTable, i);
                        }
                        // upate convex hull data in table
                        CellCentricDataHolder cellCentricDataHolder = selectedTrackDataHolder.getCellCentricDataHolder();
                        ConvexHull convexHull = cellCentricDataHolder.getConvexHull();
                        convexHullTable.setModel(new ConvexHullTableModel(convexHull));
                        for (int i = 0; i < convexHullTable.getColumnCount(); i++) {
                            convexHullTable.getColumnModel().getColumn(i).setCellRenderer(singleCellDataTableRenderer);
                        }
                        for (int i = 0; i < convexHullTable.getColumnCount(); i++) {
                            GuiUtils.packColumn(convexHullTable, i);
                        }
                        plotConvexHull(selectedTrackDataHolder);
                    }
                }
            }
        });
        /**
         * Action Listeners
         */
        // add selected track to dataset
        addTrackToDatasetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackDataHolder selectedTrack = (TrackDataHolder) tracksList.getSelectedValue();
                if (selectedTrack != null) {
                    if (!trackDatasetList.contains(selectedTrack)) {
                        //set the label
                        int label = 0;
                        if (label1RadioButton.isSelected()) {
                            label = 1;
                        } else if (label2RadioButton.isSelected()) {
                            label = 2;
                        } else if (label3RadioButton.isSelected()) {
                            label = 3;
                        }
                        selectedTrack.getCellCentricDataHolder().setLabel(label);
                        trackDatasetList.add(selectedTrack);
                    }
                }
            }
        });
        //remove trasck from dataset
        removeTrackFromDatasetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackDataHolder selectedTrack = (TrackDataHolder) datasetList.getSelectedValue();
                if (selectedTrack != null) {
                    trackDatasetList.remove(selectedTrack);
                }
            }
        });

        // write final dataset to file
        writeToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // create and execute new swing worker
                WriteToFileSwingWorker writeToFileSwingWorker = new WriteToFileSwingWorker();
                writeToFileSwingWorker.execute();
            }
        });
        computeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FetchDataForExperimentSwingWorker fetchDataForExperimentSwingWorker = new FetchDataForExperimentSwingWorker();
                fetchDataForExperimentSwingWorker.execute();
            }
        });

        //add ad hoc renderer to the datatset list
        datasetList.setCellRenderer(new DatasetRenderer());
    }

    /**
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        if (experiment == null || !selectedProject.equals(experiment.getProject()) || experimentBindingList.isEmpty()) {
            // show relative experiments, fetch them from DB and then sort them
            Long projectid = selectedProject.getProjectid();
            List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid, ExperimentStatus.PERFORMED);
            if (experimentList != null) {
                Collections.sort(experimentList);
                experimentBindingList = ObservableCollections.observableList(experimentList);
                JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, experimentsList);
                bindingGroup.addBinding(jListBinding);
                bindingGroup.bind();
            } else {
                JOptionPane.showMessageDialog(getContentPane(), "no experiments yet", "no experiments found", JOptionPane.INFORMATION_MESSAGE);
                if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                    experimentBindingList.clear();
                }
            }
        }
    }

    /**
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        experiment = selectedExperiment;
        expTextArea.setText(experiment.getPurpose());
    }

    /**
     *
     * @param trackDataHolder
     */
    private void plotConvexHull(TrackDataHolder trackDataHolder) {
        ConvexHull convexHull = trackDataHolder.getCellCentricDataHolder().getConvexHull();
        Iterable<GeometricPoint> cHull = convexHull.getHull();
        int M = 0;
        for (GeometricPoint point : cHull) {
            M++;
        }
        // the hull, in counterclockwise order
        GeometricPoint[] hull = new GeometricPoint[M];
        int m = 0;
        for (GeometricPoint point : cHull) {
            hull[m++] = point;
        }
        // generate xy coordinates for the points of the hull
        double[] x = new double[m + 1];
        double[] y = new double[m + 1];
        for (int i = 0; i < m; i++) {
            GeometricPoint point = hull[i];
            x[i] = point.getX();
            y[i] = point.getY();
        }
        // repeat fisrt coordinates at the end, to close the polygon
        x[m] = hull[0].getX();
        y[m] = hull[0].getY();
        // get info for the title of the plot
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        // dataset for the convex hull
        XYSeries hullSeries = JFreeChartUtils.generateXYSeries(x, y);
        XYSeriesCollection hullDataset = new XYSeriesCollection(hullSeries);
        JFreeChart convexHullChart = ChartFactory.createXYLineChart(seriesKey + " - convex hull", "x (µm)", "y (µm)", hullDataset, PlotOrientation.VERTICAL, false, true, false);
        // dataset for the coordinates
        Double[][] coordinatesMatrix = trackDataHolder.getStepCentricDataHolder().getCoordinatesMatrix();
        XYSeries coordinatesSeries = JFreeChartUtils.generateXYSeries(coordinatesMatrix);
        XYSeriesCollection coordinatesDataset = new XYSeriesCollection(coordinatesSeries);
        // use both datasets for the plot
        XYPlot xyPlot = convexHullChart.getXYPlot();
        xyPlot.setDataset(0, coordinatesDataset);
        xyPlot.setDataset(1, hullDataset);
        convexHullChartPanel.setChart(convexHullChart);
    }

    /**
     *
     */
    private class FetchDataForExperimentSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            double instrumentConversionFactor = experiment.getInstrument().getConversionFactor();
            double magnificationValue = experiment.getMagnification().getMagnificationValue();
            double conversionFactor = instrumentConversionFactor * magnificationValue / 10;
            // fetch the migration data
            for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                String info = "Starting with condition: " + plateCondition;
                appendInfo(info);
                List<Well> wells = new ArrayList<>();
                for (Well well : plateCondition.getWellList()) {
                    Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
                    wells.add(fetchedWell);
                    info = "I am fetching data for sample: " + fetchedWell;
                    appendInfo(info);
                }
                plateCondition.setWellList(wells);
                info = "I am done with data from condition: " + plateCondition;
                appendInfo(info);
            }
            String info = "no more data to fetch... Computing stuff now...";
            appendInfo(info);
            // now do the computations
            int totTracks = 0;
            for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                info = "starting computations for condition: " + plateCondition;
                appendInfo(info);
                // create a new object to hold pre-processing results
                SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
                // do the computations
                singleCellPreProcessor.generateTrackDataHolders(singleCellPreProcessingResults, plateCondition, conversionFactor, experiment.getExperimentInterval());
                info = "track data holders generated...";
                appendInfo(info);
                singleCellPreProcessor.generateDataStructure(singleCellPreProcessingResults);
                info = "data structure generated ...\n*****";
                appendInfo(info);
                singleCellPreProcessor.operateOnStepsAndCells(singleCellPreProcessingResults);
                info = "step-centric and cell-centric operations performed ...";
                appendInfo(info);
                singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellPreProcessingResults);
                singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellPreProcessingResults);
                info = "tracks coordinates computed...";
                appendInfo(info);
                singleCellPreProcessor.generateInstantaneousDisplacementsVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateDirectionalityRatiosVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateMedianDirectionalityRatiosVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateTrackDisplacementsVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateCumulativeDistancesVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateEuclideanDistancesVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateTrackSpeedsVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateEndPointDirectionalityRatiosVector(singleCellPreProcessingResults);
                info = "displacements, speeds and directionalities computed...";
                appendInfo(info);
                singleCellPreProcessor.generateConvexHullsVector(singleCellPreProcessingResults);
                info = "convex hulls computed...";
                appendInfo(info);
                singleCellPreProcessor.generateDisplacementRatiosVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateOutreachRatiosVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateTurningAnglesVector(singleCellPreProcessingResults);
                singleCellPreProcessor.generateMedianTurningAnglesVector(singleCellPreProcessingResults);
                info = "angles data computed...";
                appendInfo(info);
                List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
                // add tracks to list
                trackDataHolderBindingList.addAll(trackDataHolders);
                totTracks += trackDataHolders.size();
                appendInfo("$$$ tracks for current condition: " + trackDataHolders.size());
                appendInfo("*-*-*" + plateCondition + " processed!");
            }
            appendInfo("I have done what you asked for " + totTracks + " cell tracks! Congrats!");
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private void appendInfo(String info) {
        logTextArea.append(info + newLine);
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }

    /**
     * Swing Worker to write to file.
     */
    private class WriteToFileSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            writeToFileButton.setEnabled(false);
            // show waiting cursor
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            writeDatasetToFile();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                //show back default cursor
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(getContentPane(), "tracks written to file", "dataset written to file", JOptionPane.INFORMATION_MESSAGE);
                writeToFileButton.setEnabled(true);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Renderer for the dataset List
     */
    private class DatasetRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            TrackDataHolder trackDataHolder = (TrackDataHolder) value;
            if (trackDataHolder != null) {
                String text = "" + trackDataHolder + ", label:" + trackDataHolder.getCellCentricDataHolder().getLabel();
                setText(text);
            } else {
                setText("");
            }
            return this;
        }
    }

    /**
     * Write the dataset to file
     */
    private void writeDatasetToFile() {
        File folder = new File("C:\\Users\\paola\\Desktop\\cells_classification\\new_data_muppet");
        Project project = (Project) projectsList.getSelectedValue();
        String fileName = project + "_" + experiment + "_" + "data_label.csv";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, fileName)))) {
            // header of the file
            bufferedWriter.append("id" + " " + "label" + " " + "dur" + " " + "xmin" + " " + "xmax" + " " + "ymin" + " " + "ymax" + " "
                    + "xnd" + " " + "ynd" + " " + "cd" + " " + "ed" + " " + "dir" + " " + "md" + " " + "ms" + " " + "mta" + " " + "maxdis" + " "
                    + "dr" + " " + "or" + " " + "perim" + " " + "area" + " " + "acirc" + " " + "dir2" + " " + "vertices");
            // new line
            bufferedWriter.newLine();
            for (TrackDataHolder trackDataHolder : trackDatasetList) {
                Track track = trackDataHolder.getTrack();
                CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
                bufferedWriter.append("" + track.getTrackid());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getLabel());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getTrackDuration());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getxMin());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getxMax());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getyMin());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getyMax());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + (cellCentricDataHolder.getxNetDisplacement()));
                bufferedWriter.append(" ");
                bufferedWriter.append("" + (cellCentricDataHolder.getyNetDisplacement()));
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getCumulativeDistance());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getEuclideanDistance());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getEndPointDirectionalityRatio());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getMedianDisplacement());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getMedianSpeed());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getMedianTurningAngle());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getMostDistantPointsPair().getMaxSpan());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getDisplacementRatio());
                bufferedWriter.append("\t");
                bufferedWriter.append("" + cellCentricDataHolder.getOutreachRatio());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getPerimeter());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getArea());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getAcircularity());
                bufferedWriter.append(" ");
                bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getDirectionality());
                bufferedWriter.append(" ");
//                        bufferedWriter.append("-1");
                bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getHullSize());
                bufferedWriter.newLine();
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        projectsList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        experimentsList = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        tracksList = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        trackTable = new javax.swing.JTable();
        label0RadioButton = new javax.swing.JRadioButton();
        label1RadioButton = new javax.swing.JRadioButton();
        label2RadioButton = new javax.swing.JRadioButton();
        label3RadioButton = new javax.swing.JRadioButton();
        addTrackToDatasetButton = new javax.swing.JButton();
        removeTrackFromDatasetButton = new javax.swing.JButton();
        writeToFileButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        datasetList = new javax.swing.JList();
        convexHullGraphPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        convexHullTable = new javax.swing.JTable();
        expScrollPane = new javax.swing.JScrollPane();
        expTextArea = new javax.swing.JTextArea();
        computeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tracks Classifier");
        setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("projects")));

        projectsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(projectsList);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("experiments"));

        experimentsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(experimentsList);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("tracks"));

        tracksList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(tracksList);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("track data"));

        trackTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(trackTable);

        label0RadioButton.setText("Label 0 (straight)");

        label1RadioButton.setText("Label 1 (curve)");

        label2RadioButton.setText("Label 2 (confined)");

        label3RadioButton.setText("Label 3 (random)");

        addTrackToDatasetButton.setText("Add Track To Dataset");

        removeTrackFromDatasetButton.setText("Remove Track From Dataset");

        writeToFileButton.setText("Write Dataset To File");

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("current dataset tracks"));

        datasetList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(datasetList);

        convexHullGraphPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("convex hull graph"));
        convexHullGraphPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder("Log"));

        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        logTextArea.setForeground(new java.awt.Color(0, 51, 204));
        logTextArea.setLineWrap(true);
        logTextArea.setRows(5);
        jScrollPane6.setViewportView(logTextArea);

        jScrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder("convex hull table"));

        convexHullTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(convexHullTable);

        expScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        expTextArea.setEditable(false);
        expTextArea.setColumns(20);
        expTextArea.setLineWrap(true);
        expTextArea.setRows(5);
        expTextArea.setWrapStyleWord(true);
        expScrollPane.setViewportView(expTextArea);

        computeButton.setText("Compute for Current Exp...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(expScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(computeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(jScrollPane6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label0RadioButton)
                            .addComponent(label3RadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addTrackToDatasetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(writeToFileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeTrackFromDatasetButton)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(label1RadioButton)
                                .addComponent(label2RadioButton)))
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                    .addComponent(convexHullGraphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, label0RadioButton, label1RadioButton, label2RadioButton, label3RadioButton);

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(computeButton))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(convexHullGraphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(label0RadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label1RadioButton)
                                .addGap(5, 5, 5)
                                .addComponent(label2RadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label3RadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(addTrackToDatasetButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(removeTrackFromDatasetButton)
                                .addGap(18, 18, 18)
                                .addComponent(writeToFileButton)))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TracksClassifierFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TracksClassifierFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTrackToDatasetButton;
    private javax.swing.JButton computeButton;
    private javax.swing.JPanel convexHullGraphPanel;
    private javax.swing.JTable convexHullTable;
    private javax.swing.JList datasetList;
    private javax.swing.JScrollPane expScrollPane;
    private javax.swing.JTextArea expTextArea;
    private javax.swing.JList experimentsList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JRadioButton label0RadioButton;
    private javax.swing.JRadioButton label1RadioButton;
    private javax.swing.JRadioButton label2RadioButton;
    private javax.swing.JRadioButton label3RadioButton;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JList projectsList;
    private javax.swing.JButton removeTrackFromDatasetButton;
    private javax.swing.JTable trackTable;
    private javax.swing.JList tracksList;
    private javax.swing.JButton writeToFileButton;
    // End of variables declaration//GEN-END:variables
}
