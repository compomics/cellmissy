/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.AssayMedium;
import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.cmso.CMSOReaderPanel;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.CellLineService;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.InstrumentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.TreatmentService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.in.OMEXMLReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Gwendolien Sergeant
 */
@Controller("cMSOReaderController")
public class CMSOReaderController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CMSOReaderController.class);
    //model
    private File investigationFile;
    private File studyFile;
    private File assayFile;
    private List<File> biotracksFolders; //separate folder per tracking software
    private boolean tracksPresent;
    private Experiment importedExperiment;
    private LinkedHashMap<Integer, List<Double>> objectsMap; //<object id, <all features of object>>
    private LinkedHashMap<Integer, List<Integer>> linksMap; //<link id, <all object ids>>
    //view
    private CMSOReaderPanel cmsoReaderPanel;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //services
    private GridBagConstraints gridBagConstraints;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private WellService wellService;
    @Autowired
    private PlateService plateService;
    @Autowired
    private CellLineService cellLineService;
    @Autowired
    private AssayService assayService;
    @Autowired
    private TreatmentService treatmentService;

    /**
     * Initialize controller
     */
    public void init() {
        cmsoReaderPanel = new CMSOReaderPanel();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        tracksPresent = false;
        importedExperiment = null;
        //init view
        initCMSOReaderPanel();
    }

    /**
     * Initialize view
     */
    private void initCMSOReaderPanel() {
        //disable next button
        cmsoReaderPanel.getNextButton().setEnabled(false);
        /**
         * Action Listeners
         */
        cmsoReaderPanel.getChooseFolderButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a JFile Chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose a CMSO dataset directory to import");
                fileChooser.setApproveButtonText("Choose directory");
                // Make sure only directories can be selected
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                // in response to the button click, show open dialog
                int returnVal = fileChooser.showOpenDialog(cmsoReaderPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    //check if directory name is conform ---?
                    // if so: set text field and parse data
                    if (chosenFile.getName().startsWith("cmsodataset")) {
                        cmsoReaderPanel.getFolderTextField().setText(chosenFile.getAbsolutePath());
                        parseCMSODataset(chosenFile);
                        //set continue button available when there is track data
                        cmsoReaderPanel.getNextButton().setEnabled(tracksPresent);
                    } // else display error popup
                    else {
                        JOptionPane.showMessageDialog(cmsoReaderPanel, "The chosen directory does not seem to be a CMSO dataset", "Folder name incorrect", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        cmsoReaderPanel.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check if a dataset has been loaded and it contains tracking data
                if (tracksPresent) {
                    //build up cellmissy experiment structure
                    //will probably have to reread all files in order
                    setupDataStructure(investigationFile, studyFile, assayFile);
                    //add tracks data to project/experiment
                    setupTracksData();

                    /**
                     * there is no way to know to which condition the tracks
                     * belong ---- possible solution: put dp folder inside (or
                     * rename) folder with well coordinates putting the info
                     * inside the json would be illogical, is not the point of
                     * the json
                     *
                     * switch to analysis view, this contains tracking data
                     * choice ??project in getoonde lijst steken (binding) dan
                     * (onselectedproject) om exp en rest te doen, gebruik
                     * singlecellmaincontroller ea ? toon single cell view maar
                     * met andere populated lists? of zelfs niet dat selectie
                     * deel? ??--> proceedtoanalysis(selectedexperiment) returnt
                     * bool
                     *
                     * solution: save project to database and then start from
                     * normal single cell analysis? Needs: unique identifier,
                     * set algorithm and tracks data
                     */
                    cellMissyController.proceedToAnalysis(importedExperiment);
                }
            }
        });

        //add panel to main view
        cellMissyController.getCellMissyFrame().getCmsoDatasetParentPanel().add(cmsoReaderPanel, gridBagConstraints);
    }

    /**
     * Called in the main controller, resetData views and models if another view
     * has being shown
     */
    public void resetAfterCardSwitch() {
        cmsoReaderPanel.getNextButton().setEnabled(false);
        importedExperiment = null;
        //reset text fields
        cmsoReaderPanel.getFolderTextField().setText("");
        cmsoReaderPanel.getSummaryTextArea().setText("");
        cmsoReaderPanel.getIsaTextArea().setText("");
        cmsoReaderPanel.getOmeTextArea().setText("");
        cmsoReaderPanel.getBiotracksTextArea().setText("");
        //reset model entities
        biotracksFolders = null;
        tracksPresent = false;
        objectsMap = null;
        linksMap = null;
    }

    /**
     *
     * @param file A folder that contains a CMSO dataset
     */
    private void parseCMSODataset(File datasetFolder) {
        try {

            File[] entireDataset = datasetFolder.listFiles();
            File[] isaFiles;
            biotracksFolders = new ArrayList<>();

            // TODO: add spec compliance validation??
            // TODO: plot something from biotracks as summary -- plot what?
            for (File file : entireDataset) {
                String name = file.getName();

                //put entire readme contents in summary text block
                if (name.equalsIgnoreCase("readme.md")) {
                    String summaryText = "";
                    //read file
                    summaryText += FileUtils.readFileToString(file, "UTF-8");
                    cmsoReaderPanel.getSummaryTextArea().setText(summaryText);
                }

                //get all isa files
                if (name.endsWith("isa")) {
                    isaFiles = file.listFiles();

                    String[] isaText = parseISAFiles(isaFiles);
                    // use this instead of toString() to avoid brackets and commas
                    String isaSummary = isaText[0] + isaText[1] + isaText[2] + "\n -- More details in the ISA files.";
                    cmsoReaderPanel.getIsaTextArea().setText(isaSummary);

                } //Search for ome companion file. Array will contain only one file
                else if (name.endsWith("companion.ome")) {
                    IFormatReader reader = new OMEXMLReader();
                    reader.setId(file.getAbsolutePath());

                    String omeText = "File name: " + file.getName() + "\n";
                    omeText += "Dataset structure: " + reader.getDatasetStructureDescription() + "\n";
                    omeText += "Total amount of images = " + reader.getImageCount() + "\n";
                    omeText += "Dimension order: " + reader.getDimensionOrder() + "\n";

                    cmsoReaderPanel.getOmeTextArea().setText(omeText);

                } //For biotracks we can't check the name or path since it will be the name of the tracking software
                else if (file.isDirectory() && !name.endsWith("miacme")) {
                    //add every folder to list
                    biotracksFolders.add(file);

                }
            }
            String biotracksText = "";
            if (!biotracksFolders.isEmpty()) {

                for (File trackingSoftware : biotracksFolders) {

                    // show: software name, total #objects, 
                    // total #tracks + parameters contained in objects table (x,y,z,t...)
                    biotracksText += "Software: " + trackingSoftware.getName() + "\n";

                    //software folder can contain multiple files: well nr and in there can be raw data, biotracks ini file and dp folder
                    // the dp folder is the one we need to show a summary of the tracks
                    // from in here we need the csv's with objects and links
                    for (File well : trackingSoftware.listFiles()) {
                        for (File file : well.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return name.equalsIgnoreCase("dp");
                            }
                        })) {
                            String[] softwareSummary = parseBiotracks(file);
                            biotracksText += softwareSummary[0] + softwareSummary[1];
                        }
                    }
                    biotracksText += "\n \n";
                    tracksPresent = true;
                }
            } else {
                biotracksText += "No tracks files present in the dataset.";
                tracksPresent = false;
            }
            cmsoReaderPanel.getBiotracksTextArea().setText(biotracksText);

        } catch (FormatException ex) {
            LOG.error(ex.getMessage());
            cellMissyController.showMessage(ex.getMessage(), "Something went wrong while reading a file", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            cellMissyController.showMessage(ex.getMessage(), "Something seems wrong with the input", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Parse ISA files. The goal is to show the user a short summary of the file
     * contents.
     *
     * @param isaFiles
     * @return An array containing information on the investigation, study and
     * assay file, each has it's own array index.
     */
    private String[] parseISAFiles(File[] isaFiles) {
        // initialize return
        String isaText = "";
        String[] isaTextArray = new String[3];

        // parser and reader
        CSVParser csvFileParser;
        FileReader fileReader;
        CSVFormat csvFileFormat;
        // fileformat specification depending on delimination
        // cannot infer header because of multiple "unit" columns
        csvFileFormat = CSVFormat.TDF;

        for (File isaFile : isaFiles) {
            // check name for correct spot in text array
            if (isaFile.getName().startsWith("i")) {
                investigationFile = isaFile;
                String text = "";

                // initialize the file reader and parser
                try {
                    fileReader = new FileReader(isaFile);
                    csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    // get the csv records (rows)
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();

                    // define which terms to show in the summary, set is fastest
                    List<String> investigationTerms = new ArrayList<String>(
                            Arrays.asList("Investigation Identifier", "Investigation Title", "Investigation Description",
                                    "Investigation Submission Date", "Investigation Publication DOI", "Investigation Publication Author List", "Investigation Publication Title",
                                    "Study Identifier", "Study Title", "Study Description", "Study File Name", "Study Publication DOI",
                                    "Study Publication Author List", "Study Design Type", "Study Factor Name", "Study Factor Type",
                                    "Study Assay Measurement Type", "Study Assay Technology Type", "Study Assay Technology Platform",
                                    "Study Assay File Name", "Study Protocol Name", "Study Protocol Type", "Study Protocol Description",
                                    "Study Protocol Parameters Name", "Study Protocol Components Name", "Study Protocol Components Type"));
                    Set<String> invSummaryTerms = new HashSet<String>(investigationTerms);
                    text += "INVESTIGATION FILE: \n";
                    // read the CSV file records
                    for (int row = 0; row < csvRecords.size(); row++) {
                        CSVRecord cSVRecord = csvRecords.get(row);

                        if (invSummaryTerms.contains(cSVRecord.get(0))) {
                            //create an iterator for the record (row) values
                            Iterator<String> iter = cSVRecord.iterator();
                            //add all text parts to variable
                            text += iter.next() + ": ";
                            while (iter.hasNext()) {
                                // additional check for empty strings
                                String content = iter.next();
                                if (!content.isEmpty()) {
                                    text += content + " // ";
                                }
                            }
                            //new line per info
                            text += "\n";

                        }

                    }
                    //extra line between files
                    text += "\n";
                } catch (IOException ex) {
                    LOG.error(ex.getMessage() + "/n Error while parsing Investigation file", ex);
                }

                isaTextArray[0] = text;

            } //study and assay files are slightly more complicated due to format
            else if (isaFile.getName().startsWith("s")) {
                studyFile = isaFile;
                String text = "";
                // initialize the file reader and parser
                try {
                    fileReader = new FileReader(isaFile);
                    csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    text += "STUDY FILE: \n";
                    // get the csv records (rows)
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();

                    // the information we want to show
                    String sourceName = "Sources: ";
                    String organism = "Organisms: ";
                    String celltypes = "Cell types: ";
                    String agentType = "Perturbation type ";
                    String agentName = "Perturbation name: ";
                    String agentDose = "Perturbation doses: ";
                    // cannot add concentrations because "unit" is not a unique column name

                    for (int row = 1; row < csvRecords.size(); row++) {
                        // for each row: check if information is not already recorded in the String
                        // using indices for column, inferring header is not possible (duplicate names)
                        CSVRecord cSVRecord = csvRecords.get(row);
                        if (!cSVRecord.get(0).isEmpty() && !sourceName.contains(cSVRecord.get(0))) {
                            sourceName += cSVRecord.get(0) + " // ";
                        }
                        if (!cSVRecord.get(1).isEmpty() && !organism.contains(cSVRecord.get(1))) {
                            organism += cSVRecord.get(1) + " // ";
                        }
                        if (!cSVRecord.get(4).isEmpty() && !celltypes.contains(cSVRecord.get(4))) {
                            celltypes += cSVRecord.get(4) + " // ";
                        }
                        if (!cSVRecord.get(43).isEmpty() && !agentType.contains(cSVRecord.get(43))) {
                            agentType += cSVRecord.get(43) + " // ";
                        }
                        if (!cSVRecord.get(44).isEmpty() && !agentName.contains(cSVRecord.get(44))) {
                            agentName += cSVRecord.get(44) + " // ";
                        }
                        if (!cSVRecord.get(68).isEmpty() && !agentDose.contains(cSVRecord.get(68))) {
                            agentDose += cSVRecord.get(68) + " // ";
                        }
                    }

                    // add modules of information to end string
                    text += sourceName + "\n" + organism + "\n" + celltypes + "\n"
                            + agentType + "\n" + agentName + "\n" + agentDose + "\n \n";
                } catch (IOException ex) {
                    LOG.error(ex.getMessage() + "/n Error while parsing Study file", ex);
                }

                isaTextArray[1] = text;

            } else if (isaFile.getName().startsWith("a")) {
                assayFile = isaFile;
                String text = "";
                // initialize the file reader and parser
                try {
                    fileReader = new FileReader(isaFile);
                    csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    text += "ASSAY FILE: \n";
                    // get the csv records (rows)
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();

                    // the information we want to show
                    String imagingTechnique = "Imaging Technique: ";
                    String acquisitionTime = "Acquisition duration: ";
                    String interval = "Image interval: ";
                    String software = "Software: ";

                    CSVRecord firstDataRow = csvRecords.get(1);
                    imagingTechnique += firstDataRow.get(19);
                    acquisitionTime += firstDataRow.get(21) + " " + firstDataRow.get(22);
                    interval += firstDataRow.get(25) + " " + firstDataRow.get(26);
                    software += firstDataRow.get(43);

                    text += imagingTechnique + "\n" + acquisitionTime + "\n"
                            + interval + "\n" + software + "\n";

                } catch (IOException ex) {
                    LOG.error(ex.getMessage() + "/n Error while parsing Assay file", ex);
                }

                isaTextArray[2] = text;
            }
        }

        return isaTextArray;
    }

    /**
     * Parse biotracks information. The goal is to show the user a short summary
     * of the file contents.
     *
     * @param dpFolder A biotracks "dp" folder directory that contains objects
     * and links csv and the dp.json files
     * @return Summary information of the biotracks package contents in String.
     * Two-parter array with info on objects and links.
     */
    private String[] parseBiotracks(File dpFolder) {
        //initialize return
        String[] biotracksText = new String[2];
        objectsMap = new LinkedHashMap<>();
        linksMap = new LinkedHashMap<>();

        // parser and reader
        CSVParser csvFileParser;
        FileReader fileReader;
        CSVFormat csvFileFormat;
        // fileformat specification depending on delimination, infer header
        csvFileFormat = CSVFormat.EXCEL.withHeader();

        for (File file : dpFolder.listFiles()) {
            if (file.getName().equalsIgnoreCase("objects.csv")) {

                try {
                    fileReader = new FileReader(file);
                    csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    // get the csv records (rows)
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();

                    String objectsText = "Total amount of objects detected: " + csvRecords.size() + "\n";
                    objectsText += "The objects table contains: " + csvFileParser.getHeaderMap().keySet() + "\n";

                    biotracksText[0] = objectsText;

                    //setup object data
                    for (CSVRecord row : csvRecords) {
                        List<Double> objectInfo = new ArrayList<>();
                        objectInfo.add(Double.parseDouble(row.get("cmso_frame_id")));
                        objectInfo.add(Double.parseDouble(row.get("cmso_x_coord")));
                        objectInfo.add(Double.parseDouble(row.get("cmso_y_coord")));
                        objectsMap.put((Integer.valueOf(row.get("cmso_object_id"))), objectInfo);
                    }

                } catch (IOException ex) {
                    LOG.error(ex.getMessage() + "/n Error while parsing Objects file", ex);
                }
            } else if (file.getName().equalsIgnoreCase("links.csv")) {

                try {
                    fileReader = new FileReader(file);
                    csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    // get the csv records (rows)
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();
                    Set<Integer> linkID = new HashSet<>();

                    for (CSVRecord row : csvRecords) {
                        linkID.add(Integer.parseInt(row.get("cmso_link_id")));
                    }
                    String linksText = "Total amount of links: " + linkID.size();
                    biotracksText[1] = linksText;

                    //setup links(=tracks) data
                    List<Integer> objectidsList = new ArrayList<>();
                    //TODO separate and write unit test for this
                    //create an iterator for the records (rows)
                    Iterator<CSVRecord> iter = csvRecords.iterator();
                    //first line
                    CSVRecord row = iter.next();
                    objectidsList.add(Integer.parseInt(row.get("cmso_object_id")));
                    Integer currentLinkid = Integer.parseInt(row.get("cmso_link_id"));
                    //go through all rows
                    while (iter.hasNext()) {
                        row = iter.next();
                        //on new link id
                        if (currentLinkid != Integer.parseInt(row.get("cmso_link_id"))) {
                            linksMap.put(currentLinkid, objectidsList);
                            currentLinkid = Integer.parseInt(row.get("cmso_link_id"));
                            objectidsList = new ArrayList<>();
                            objectidsList.add(Integer.parseInt(row.get("cmso_object_id")));
                        } else if (iter.hasNext() == false) { //check if we are at the last row
                            objectidsList.add(Integer.parseInt(row.get("cmso_object_id")));
                            linksMap.put(currentLinkid, objectidsList);
                        } else {
                            objectidsList.add(Integer.parseInt(row.get("cmso_object_id")));
                        }
                    }

                } catch (IOException ex) {
                    LOG.error(ex.getMessage() + "/n Error while parsing Links file", ex);
                }

            }
        }
        return biotracksText;
    }

    /**
     * Setup a CellMissy project/experiment structure from the ISA files and the
     * data therein.
     *
     * @param investigationFile
     * @param studyFile
     * @param assayFile
     * @return
     */
    private void setupDataStructure(File investigationFile, File studyFile, File assayFile) {
        Project project = null;
        // parser and reader
        CSVParser csvFileParser;
        FileReader fileReader;
        CSVFormat csvFileFormat;
        // fileformat specification depending on delimination
        // cannot infer header because of multiple "unit" columns
        csvFileFormat = CSVFormat.TDF;

        //setup investigation
        try {
            fileReader = new FileReader(investigationFile);
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            // get the csv records (rows)
            List<CSVRecord> csvRecords = csvFileParser.getRecords();
            //get specific terms and use these to setup cellmissy data structure
            project = new Project(csvRecords.get(7).get(1));
            project.setProjectNumber(Integer.parseInt(csvRecords.get(6).get(1)));
            project.setExperimentList(new ArrayList<>());
            project.getExperimentList().add(new Experiment());
            importedExperiment = project.getExperimentList().get(0);
            //no sure if a user needs to be set for a cmso project
            //user can be current cellmissy operator of experiment performer as in ISA file
//            importedExperiment.setUser(new User(csvRecords.get(23).get(1), csvRecords.get(22).get(1), Role.ADMIN_USER, "password", csvRecords.get(25).get(1)));
//            importedExperiment.setUser(cellMissyController.getCurrentUser());
            importedExperiment.setExperimentNumber(Integer.parseInt(csvRecords.get(34).get(1)));
            importedExperiment.setPurpose(csvRecords.get(35).get(1));
            importedExperiment.setPlateFormat(new PlateFormat(Long.MAX_VALUE, Integer.parseInt(csvRecords.get(40).get(1))));

        } catch (IOException ex) {
            LOG.error(ex.getMessage() + "/n Error while parsing Investigation file", ex);
        }

        //setup study
        try {
            fileReader = new FileReader(studyFile);
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            // get the csv records (rows)
            List<CSVRecord> csvRecords = csvFileParser.getRecords();
            List<PlateCondition> plateConditionList = new ArrayList<>();
            //go through all rows except header, infer not possible (duplicate names)
            // this makes that we cannot get fields by using column names
            for (int row = 1; row < csvRecords.size(); row++) {
                CSVRecord cSVRecord = csvRecords.get(row);
                //create new PlateCondition object per row
                //make sure n/A values in treatment are changed over to control
                PlateCondition conditionRow = new PlateCondition(Integer.toUnsignedLong(row));
                conditionRow.setCellLine(new CellLine(null, Integer.parseInt(cSVRecord.get(20)), cSVRecord.get(21), Double.parseDouble(cSVRecord.get(23)), new CellLineType(Long.MIN_VALUE, cSVRecord.get(4)), cSVRecord.get(22)));

                // we need to check if the cell line type is already present in the DB !
                CellLineType foundCellLineType = cellLineService.findByName(conditionRow.getCellLine().getCellLineType().getName());
                if (foundCellLineType != null) {
                    conditionRow.getCellLine().setCellLineType(foundCellLineType);
                } else { //reset id so that a new one can be set when savind to db
                    conditionRow.getCellLine().getCellLineType().setCellLineTypeid(null);
                }
                // for now ignore ecm     conditionRow.setEcm(new Ecm());
                conditionRow.setWellList(new ArrayList<>());
                // rownr is a letter in the isa files, need to convert this to int
                conditionRow.getWellList().add(new Well(Integer.parseInt(cSVRecord.get(39)), ((int) cSVRecord.get(39).charAt(0)) - 64));

                conditionRow.getWellList().get(0).setPlateCondition(conditionRow);
                conditionRow.setTreatmentList(new ArrayList<>());
                conditionRow.getTreatmentList().add(new Treatment(
                        Double.parseDouble(cSVRecord.get(51)), cSVRecord.get(52), null, null, null, new TreatmentType(
                        Long.MIN_VALUE, checkTreatmentName(cSVRecord.get(44)))));

                //check if treatment type is already present in the db
                TreatmentType foundTreatmentType = treatmentService.findByName(conditionRow.getTreatmentList().get(0).getTreatmentType().getName());
                if (foundTreatmentType != null) {
                    conditionRow.getTreatmentList().get(0).setTreatmentType(foundTreatmentType);
                } else { //reset id so that a new one can be set when savind to db
                    conditionRow.getTreatmentList().get(0).getTreatmentType().setTreatmentTypeid(null);
                }
                plateConditionList.add(conditionRow);
            }
            importedExperiment.setPlateConditionList(plateConditionList);
        } catch (IOException ex) {
            LOG.error(ex.getMessage() + "/n Error while parsing Investigation file", ex);
        }

        //setup assay
        try {
            fileReader = new FileReader(assayFile);
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            // get the csv records (rows)
            List<CSVRecord> csvRecords = csvFileParser.getRecords();

            //set experiment data
            importedExperiment.setDuration(Double.parseDouble(csvRecords.get(1).get(21)));
            importedExperiment.setExperimentInterval(Double.parseDouble(csvRecords.get(1).get(22)));
            importedExperiment.setMagnification(new Magnification());
            importedExperiment.getMagnification().setMagnificationNumber(csvRecords.get(1).get(32));
            //go through all rows except header, infer not possible (duplicate names)
            // this makes that we cannot get fields by using column names
            //set condition data
            for (int row = 1; row < csvRecords.size(); row++) {
                PlateCondition condition = importedExperiment.getPlateConditionList().get(row - 1);
                condition.setAssayMedium(new AssayMedium(csvRecords.get(row).get(2), csvRecords.get(row).get(3), Double.parseDouble(csvRecords.get(row).get(4)), Double.parseDouble(csvRecords.get(row).get(8))));
                condition.setAssay(new Assay());
                condition.getAssay().setAssayType(csvRecords.get(row).get(1));

                //as many imaging types as tracking data
                for (Well well : condition.getWellList()) {
                    well.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
                    int i = 0;
                    while (i < biotracksFolders.size()) {
                        well.getWellHasImagingTypeList().add(new WellHasImagingType());
                        well.getWellHasImagingTypeList().get(0).setImagingType(new ImagingType());
                        well.getWellHasImagingTypeList().get(0).getImagingType().setName(csvRecords.get(row).get(19));
                        i++;
                    }
                }
            }

        } catch (IOException ex) {
            LOG.error(ex.getMessage() + "/n Error while parsing Investigation file", ex);
        }

        // set some object referrals
        for (PlateCondition plateCondition : importedExperiment.getPlateConditionList()) {
            plateCondition.setExperiment(importedExperiment);
        }
        // set collection for imaging types and algorithms
        List<ImagingType> imagingTypes = experimentService.getImagingTypes(importedExperiment);
        for (ImagingType imagingType : imagingTypes) {
            List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
            importedExperiment.getPlateConditionList().forEach((plateCondition) -> {
                plateCondition.getWellList().forEach((well) -> {
                    for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                        if (wellHasImagingType.getImagingType().equals(imagingType)) {
                            wellHasImagingType.setImagingType(imagingType);
                            wellHasImagingTypes.add(wellHasImagingType);
                        }
                    }
                });
            });
            imagingType.setWellHasImagingTypeList(wellHasImagingTypes);
        }
        //add algorithm to project/experiment
        List<String> softwareList = new ArrayList<>();
        for (File software : biotracksFolders) {
            softwareList.add(software.getName());
        };
        importedExperiment.getPlateConditionList().forEach((plateCondition) -> {
            plateCondition.getWellList().forEach((well) -> {
                for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                    int i = 0;
                    Algorithm algorithm = new Algorithm();
                    algorithm.setAlgorithmName(softwareList.get(i));
                    wellHasImagingType.setAlgorithm(algorithm);
                    i++;
                }
            });
        });
        //set some object referrals
        List<Algorithm> algorithms = experimentService.getAlgorithms(importedExperiment);
        for (Algorithm algorithm : algorithms) {
            List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
            for (PlateCondition plateCondition : importedExperiment.getPlateConditionList()) {
                for (Well well : plateCondition.getWellList()) {
                    for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                        if (wellHasImagingType.getAlgorithm().equals(algorithm)) {
                            wellHasImagingType.setAlgorithm(algorithm);
                            wellHasImagingTypes.add(wellHasImagingType);
                        }
                    }
                }
            }
            algorithm.setWellHasImagingTypeList(wellHasImagingTypes);
        }

        // we need to check if other objects need to be stored
        persistNewObjects();
        //TODO: save project? save wells?
        // save the experiment, save the migration data and update the experiment
        experimentService.save(importedExperiment);
//      do not need to save WellHasImagingType experimentService.saveMigrationDataForExperiment(importedExperiment);
        importedExperiment = experimentService.update(importedExperiment);
    }

    private String checkTreatmentName(String record) {
        if (record.equalsIgnoreCase("n/A")) {
            return "control";
        } else {
            return record;
        }
    }

    /**
     * Persist new objects to the DB, if any.
     */
    private void persistNewObjects() {
        // plate format
        PlateFormat plateFormat = importedExperiment.getPlateFormat();
        PlateFormat foundFormat = plateService.findByFormat(plateFormat.getFormat());
        if (foundFormat == null) {
            plateService.save(plateFormat);
        }
        // cell line types
        List<CellLineType> foundCellLines = cellLineService.findNewCellLines(importedExperiment);
        if (!foundCellLines.isEmpty()) {
            for (CellLineType cellLineType : foundCellLines) {
                cellLineService.saveCellLineType(cellLineType);
            }
        }
        // assays
        List<Assay> foundAssays = assayService.findNewAssays(importedExperiment);
        if (!foundAssays.isEmpty()) {
            for (Assay assay : foundAssays) {
                assayService.save(assay);
            }
        }
        // treatment types
        List<TreatmentType> foundTreatmentTypes = treatmentService.findNewTreatmentTypes(importedExperiment);
        if (!foundTreatmentTypes.isEmpty()) {
            for (TreatmentType treatmentType : foundTreatmentTypes) {
                treatmentService.saveTreatmentType(treatmentType);
            }
        }
    }

    private void setupTracksData() {
        for (File software : biotracksFolders) {
            for (PlateCondition platecondition : importedExperiment.getPlateConditionList()) {
                for (Well well : platecondition.getWellList()) {
                    Integer x = well.getColumnNumber();
                    Integer y = well.getRowNumber();

                    // check well row and column to get the right tracks
                    for (WellHasImagingType imagingType : well.getWellHasImagingTypeList()) {

                        // check algorithm name with biotracksFolders name
                        if (software.getName().equals(imagingType.getAlgorithm().getAlgorithmName())) {
                            // depends on how my issue on github gets resolved. most likely check well position with folder name
                            //go into dp folder. This is located in folder with well coordinate
                            File dpFolder = getFileFromFolder(getFileFromFolder(software, "" + x + y), "dp");

                            /**
                             * go into dp folder (output of biotracks) first
                             * read linksMap and setup SQL track table copy link
                             * id (CellMissy cannot currently handle split or
                             * merge events) and size of nested list for track
                             * length read objectsMap and then what?
                             */
                            linksMap.get(y); //<link id, <all object ids>>
                            objectsMap.get(y); //<object id, <all features of object>>

                        }
                    }
                }
            }
        }

    }

    /**
     * Look for a certain named file(/subfolder) in a folder.
     *
     * @param folder The folder to search
     * @param fileName Name of the asked file(/subfolder)
     * @return
     */
    private File getFileFromFolder(File folder, String fileName) {
        for (File candidate : folder.listFiles()) {
            //change equals over to contains??
            if (candidate.getName().equalsIgnoreCase(fileName)) {
                return candidate;
            }
        }
    }
}
