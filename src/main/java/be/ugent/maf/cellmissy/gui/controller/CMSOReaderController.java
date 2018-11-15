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
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.entity.result.BiotracksDataHolder;
import be.ugent.maf.cellmissy.gui.cmso.CMSOReaderPanel;
import be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.SingleCellMainController;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.CellLineService;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.InstrumentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.TreatmentService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private HashMap<String, String> investigationMap; //map with isa Investigation data, does not contain all data!!
    private HashMap<String, List<String>> studyMap; //map with isa Study data, <header, <data>>
    private HashMap<String, List<String>> assayMap; //map with isa Assay data, <header, <data>>;
    private List<File> biotracksFolders; //separate folder per tracking software
    private boolean tracksPresent;
    private Experiment importedExperiment;
    private HashMap<String, BiotracksDataHolder> biotracksDataHolders;
    private List<String> imagedWells;
    private List<Algorithm> algorithmsList;
    private List<ImagingType> imgTypesList;
    //view
    private CMSOReaderPanel cmsoReaderPanel;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    @Autowired
    private SingleCellMainController singleCellMainController;
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
        //initiate track data maps
        investigationMap = new HashMap<>();
        studyMap = new HashMap<>();
        assayMap = new HashMap<>();
        biotracksDataHolders = new HashMap<>();
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
                    setupDataStructure();
                    //add tracks data to project/experiment
                    setupTracksData();

                    /**
                     * 1)proceedToAnalysis method 2)remove current gui layer and
                     * add single cell master gui from controller 3)'press'
                     * start button (choice of algorithm and imaging type?)
                     */
                    singleCellMainController.setCmsoData(tracksPresent);
                    singleCellMainController.proceedToAnalysis(importedExperiment, algorithmsList, imgTypesList);
                    cellMissyController.getCellMissyFrame().getCmsoDatasetParentPanel().removeAll();
                    cellMissyController.getCellMissyFrame().getCmsoDatasetParentPanel().add(singleCellMainController.getAnalysisExperimentPanel(), gridBagConstraints);
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
        investigationMap = new HashMap<>();
        studyMap = new HashMap<>();
        assayMap = new HashMap<>();
        biotracksDataHolders = new HashMap();
        imagedWells = new ArrayList<>();
        algorithmsList = new ArrayList<>();
        imgTypesList = new ArrayList<>();
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
                imagedWells = new ArrayList<>();
                for (File trackingSoftware : biotracksFolders) {

                    // show: software name, total #objects, 
                    // total #tracks + parameters contained in objects table (x,y,z,t...)
                    biotracksText += "Software: " + trackingSoftware.getName() + "\n";

                    //software folder can contain multiple files: well nr and in there can be raw data, biotracks ini file and dp folder
                    // the dp folder is the one we need to show a summary of the tracks
                    // from in here we need the csv's with objects and links
                    for (File well : trackingSoftware.listFiles()) {
                        imagedWells.add(well.getName().split("_")[0] + AnalysisUtils.checkRowCoordinate(well.getName().split("_")[1]));
                        biotracksText += "Well: " + well.getName() + "\n";
                        File dp = getFileFromFolder(well, "dp");
                        String[] softwareSummary = parseBiotracks(dp, trackingSoftware.getName(), well.getName());
                        biotracksText += softwareSummary[0] + softwareSummary[1];
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
                        try {
                            investigationMap.put(cSVRecord.get(0), cSVRecord.get(1));
                        } catch (ArrayIndexOutOfBoundsException aiob) {
                            investigationMap.put(cSVRecord.get(0), "");
                        }
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
                String text = "";
                // initialize the file reader and parser
                try {
                    fileReader = new FileReader(isaFile);
                    csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    text += "STUDY FILE: \n";
                    // get the csv records (rows)
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();

                    //iterate and save the column numbers??? otherwise we do not know which cell values to copy where
                    HashMap<Integer, String> headerNames = new HashMap<>(); //maps column number to header

                    //iterate through rows
                    int i = 0;
                    for (CSVRecord row : csvRecords) {
                        //iterate through cells in the row
                        for (int j = 0; j < row.size(); j++) {
                            //first row: set column header map and init list in csv map
                            if (i == 0) {
                                headerNames.put(j, row.get(j));
                                studyMap.put(row.get(j), new ArrayList<>());
                            } else if (!headerNames.get(j).equalsIgnoreCase("Term Source REF") && !headerNames.get(j).equalsIgnoreCase("Term Accession Number")) {
                                //add cell content to map value list, even if empty ((not my problem if the data if user did not fill in))
                                studyMap.get(headerNames.get(j)).add(row.get(j));
                            }
                        }
                        //increment row index
                        i++;
                    }

                    // The information we want to show
                    String sourceName = "Sources: ";  //Source Name
                    String organism = "Organisms: ";  //Characteristics[organism]
                    String celltypes = "Cell types: ";  //Characteristics[genotype]
                    String agentType = "Perturbation types ";  //Parameter Value[perturbation agent type]
                    String agentName = "Perturbation names: ";  //Factor Value[perturbation agent]
                    String agentDose = "Perturbation doses: ";  //Factor Value[perturbation dose]

                    //get desired info from studyMap
                    sourceName += new HashSet(studyMap.get("Source Name"));
                    organism += new HashSet(studyMap.get("Characteristics[organism]"));
                    celltypes += new HashSet(studyMap.get("Characteristics[genotype]"));
                    agentType += new HashSet(studyMap.get("Parameter Value[perturbation agent type]"));
                    agentName += new HashSet(studyMap.get("Factor Value[perturbation agent]"));
                    agentDose += new HashSet(studyMap.get("Factor Value[perturbation dose]"));

                    // add modules of information to end string
                    text += sourceName + "\n" + organism + "\n" + celltypes + "\n"
                            + agentType + "\n" + agentName + "\n" + agentDose + "\n \n";
                } catch (IOException ex) {
                    LOG.error(ex.getMessage() + "/n Error while parsing Study file", ex);
                }

                isaTextArray[1] = text;

            } else if (isaFile.getName().startsWith("a")) {
                String text = "";
                // initialize the file reader and parser
                try {
                    fileReader = new FileReader(isaFile);
                    csvFileParser = new CSVParser(fileReader, csvFileFormat);
                    text += "ASSAY FILE: \n";
                    // get the csv records (rows)
                    List<CSVRecord> csvRecords = csvFileParser.getRecords();

                    HashMap<Integer, String> headerNames = new HashMap<>(); //maps column number to header

                    //iterate through rows
                    int i = 0;
                    for (CSVRecord row : csvRecords) {
                        //iterate through cells in the row
                        for (int j = 0; j < row.size(); j++) {
                            //first row: set column header map and init list in csv map
                            if (i == 0) {
                                headerNames.put(j, row.get(j));
                                assayMap.put(row.get(j), new ArrayList<>());
                            } else if (!headerNames.get(j).equalsIgnoreCase("Term Source REF") && !headerNames.get(j).equalsIgnoreCase("Term Accession Number")) {
                                //add cell content to map value list, even if empty ((not my problem if the data if user did not fill in))
                                assayMap.get(headerNames.get(j)).add(row.get(j));
                            }
                        }
                        //increment row index
                        i++;
                    }
                    // the information we want to show
                    String imagingTechnique = "Imaging Technique: "; //Parameter Value[imaging technique]
                    String acquisitionTime = "Acquisition duration: "; //Parameter Value[acquisition duration] + unit column after this
                    String interval = "Image interval: "; //Parameter Value[time interval] + unit column after this
                    String software = "Software: "; //Parameter Value[software]

                    //get desired info from assayMap
                    imagingTechnique += new HashSet(assayMap.get("Parameter Value[imaging technique]"));
                    acquisitionTime += new HashSet(assayMap.get("Parameter Value[acquisition duration]")) + " hr"; //unit standard hour for now
                    interval += new HashSet(assayMap.get("Parameter Value[time interval]")) + " min"; //unit standard minute for now
                    software += new HashSet(assayMap.get("Parameter Value[software]"));

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
    private String[] parseBiotracks(File dpFolder, String software, String wellName) {
        //initialize return
        String[] biotracksText = new String[2];
        LinkedHashMap<Integer, List<Double>> objectsMap = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<Integer>> linksMap = new LinkedHashMap<>();

        // parser and reader
        CSVParser csvFileParser;
        FileReader fileReader;
        CSVFormat csvFileFormat;
        // fileformat specification depending on delimination, INFER HEADER
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
                        // put in objects map for this software and well
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

                    //setup links(=tracks) data
                    List<Integer> objectidsList = new ArrayList<>();

                    //TODO separate and write unit test for this
                    //create an iterator for the records (rows)
                    Iterator<CSVRecord> iter = csvRecords.iterator();
                    //first line, header is already inferred
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
                        } //check if we are at the last row
                        else if (iter.hasNext() == false) {
                            objectidsList.add(Integer.parseInt(row.get("cmso_object_id")));
                            linksMap.put(currentLinkid, objectidsList);
                        } else {
                            objectidsList.add(Integer.parseInt(row.get("cmso_object_id")));
                        }
                    }
                    String linksText = "Total amount of links: " + linksMap.size();
                    biotracksText[1] = linksText;

                } catch (IOException ex) {
                    LOG.error(ex.getMessage() + "/n Error while parsing Links file", ex);
                }

            }
        }
        biotracksDataHolders.put((software + AnalysisUtils.checkRowCoordinate(wellName.split("_")[1]) + wellName.split("_")[0]), new BiotracksDataHolder(objectsMap, linksMap));
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
    private void setupDataStructure() {
        Project project = null;
        //setup investigation
        //get specific terms and use these to setup cellmissy data structure
        project = new Project(investigationMap.get("Investigation Title"));
//            project.setProjectNumber(investigationMap.get("Investigation Identifier"));   gives error if there is a letter, which is the case in my examples. Not important for further methods so set to whatever number
        project.setProjectNumber(31071992);
        project.setExperimentList(new ArrayList<>());
        project.getExperimentList().add(new Experiment());
        importedExperiment = project.getExperimentList().get(0);
        //not sure if a user needs to be set for a cmso project
        //user can be current cellmissy operator of experiment performer as in ISA file
//            importedExperiment.setUser(new User(investigationMap.get("Study Person Last Namee"), investigationMap.get("Study Person First Name"), Role.ADMIN_USER, "password", investigationMap.get("Study Person Email")));
//            importedExperiment.setUser(cellMissyController.getCurrentUser());
        importedExperiment.setExperimentNumber(Integer.parseInt(investigationMap.get("Study Identifier")));
        importedExperiment.setPurpose(investigationMap.get("Study Description"));
        importedExperiment.setPlateFormat(new PlateFormat(Long.MAX_VALUE, Integer.parseInt(investigationMap.get("Comment[Plate Format]").split("-")[0])));
        //plateformat needs amount of columns and rows set, most plate sizes are already in standard database
        //check database for corresponding format and set if we find one
        PlateFormat plateFormat = importedExperiment.getPlateFormat();
        PlateFormat foundFormat = plateService.findByFormat(plateFormat.getFormat());
        if (foundFormat != null) {
            importedExperiment.setPlateFormat(foundFormat);
        }
        // set default conversion factor to 1
        importedExperiment.setInstrument(new Instrument());
        importedExperiment.getInstrument().setConversionFactor(1);

        //setup study
        List<PlateCondition> plateConditionList = new ArrayList<>();
        int numberConditions = studyMap.get("Parameter Value[plate well number]").size();
        //we need to create a certain amount of PlateCondition objects, same amount as number of wells
        for (int condition = 0; condition < numberConditions; condition++) {

            //make sure n/A values in treatment are changed over to control
            PlateCondition conditionRow = new PlateCondition();
            conditionRow.setCellLine(new CellLine(null, Integer.parseInt(studyMap.get("Parameter Value[seeding density]").get(condition).split(" ")[0]),
                    studyMap.get("Parameter Value[cell culture medium]").get(condition), Double.parseDouble(studyMap.get("Parameter Value[cell culture serum concentration]").get(condition)),
                    new CellLineType(Long.MIN_VALUE, studyMap.get("Characteristics[cell]").get(condition)), studyMap.get("Parameter Value[cell culture serum]").get(condition)));

//                //check if the cell line type is already present in the DB
//                CellLineType foundCellLineType = cellLineService.findByName(conditionRow.getCellLine().getCellLineType().getName());
//                if (foundCellLineType != null) {
//                    conditionRow.getCellLine().setCellLineType(foundCellLineType);
//                } else { //reset id so that a new one can be set when savind to db
//                    conditionRow.getCellLine().getCellLineType().setCellLineTypeid(null);
//                }
            // for now ignore ecm     conditionRow.setEcm(new Ecm());
            conditionRow.setWellList(new ArrayList<>());
            // rownr is a letter in the isa files, need to convert this to int
            conditionRow.getWellList().add(new Well(Integer.parseInt(studyMap.get("Parameter Value[plate well column coordinate]").get(condition)),
                    (AnalysisUtils.checkRowCoordinate(studyMap.get("Parameter Value[plate well row coordinate]").get(condition)))));

            conditionRow.getWellList().get(0).setPlateCondition(conditionRow);
            conditionRow.setTreatmentList(new ArrayList<>());
            try {
                conditionRow.getTreatmentList().add(new Treatment(
                        Double.parseDouble(studyMap.get("Factor Value[perturbation dose]").get(condition).split(" ")[0]),
                        studyMap.get("Factor Value[perturbation dose]").get(condition).split(" ")[1], null, null, null,
                        new TreatmentType(Long.MIN_VALUE, checkTreatmentName(studyMap.get("Factor Value[perturbation agent]").get(condition)))));
            } catch (NumberFormatException nf) { //in case of control. Bad form to use an exception, temporary solution. Should check first term for control (n/A)
                conditionRow.getTreatmentList().add(new Treatment(
                        Double.parseDouble("0"), "µM", null, null, null,
                        new TreatmentType(Long.MIN_VALUE, checkTreatmentName(studyMap.get("Factor Value[perturbation agent]").get(condition)))));
            }

            //check if treatment type is already present in the db
            TreatmentType foundTreatmentType = treatmentService.findByName(conditionRow.getTreatmentList().get(0).getTreatmentType().getName());
            if (foundTreatmentType != null) {
                conditionRow.getTreatmentList().get(0).setTreatmentType(foundTreatmentType);
            } else { //reset id so that a new one can be set if necessary
                conditionRow.getTreatmentList().get(0).getTreatmentType().setTreatmentTypeid(null);
            }
            plateConditionList.add(conditionRow);
        }
        importedExperiment.setPlateConditionList(plateConditionList);

        //setup general assay information
        importedExperiment.setDuration(Double.parseDouble(assayMap.get("Parameter Value[acquisition duration]").get(0)));
        importedExperiment.setExperimentInterval(Double.parseDouble(assayMap.get("Parameter Value[time interval]").get(0)));
        importedExperiment.setMagnification(new Magnification());
        importedExperiment.getMagnification().setMagnificationNumber(assayMap.get("Parameter Value[objective magnification]").get(0));

        //set condition data per well
        for (int row = 0; row < numberConditions; row++) {
            PlateCondition condition = importedExperiment.getPlateConditionList().get(row);
            condition.setAssayMedium(new AssayMedium(assayMap.get("Parameter Value[medium]").get(row), assayMap.get("Parameter Value[serum]").get(row),
                    Double.parseDouble(assayMap.get("Parameter Value[serum concentration]").get(row)), Double.parseDouble(assayMap.get("Parameter Value[medium volume]").get(row))));
            condition.setAssay(new Assay());
            condition.getAssay().setAssayType(assayMap.get("Protocol REF").get(row));

            //as many imaging types as tracking software used
            for (Well well : condition.getWellList()) {
                well.setWellHasImagingTypeList(new ArrayList<WellHasImagingType>());
                // !! IMPORTANT !! wells with no biotracks data available need an empty WellHasImagingTypesList
                for (int i = 0; i < biotracksFolders.size(); i++) {
                    if (imagedWells.contains("" + well.getColumnNumber() + well.getRowNumber())) {
                        well.getWellHasImagingTypeList().add(new WellHasImagingType());
                        well.getWellHasImagingTypeList().get(0).setImagingType(new ImagingType());
                        well.getWellHasImagingTypeList().get(0).getImagingType().setName(assayMap.get("Parameter Value[imaging technique]").get(i));
                    }
                }
            }
        }

        // set some object referrals
        algorithmsList = new ArrayList<>();
        imgTypesList = new ArrayList<>();
        for (PlateCondition plateCondition : importedExperiment.getPlateConditionList()) {
            plateCondition.setExperiment(importedExperiment);
        }
        // set collection for imaging types and wells
        List<ImagingType> imagingTypes = experimentService.getImagingTypes(importedExperiment);
        for (ImagingType imagingType : imagingTypes) {
            List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
            importedExperiment.getPlateConditionList().forEach((plateCondition) -> {
                plateCondition.getWellList().forEach((well) -> {
                    for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                        if (wellHasImagingType.getImagingType().equals(imagingType)) {
                            wellHasImagingType.setImagingType(imagingType);
                            wellHasImagingTypes.add(wellHasImagingType);
                            wellHasImagingType.setWell(well);
                        }

                    }
                });
            });
            if (!imgTypesList.contains(imagingType)) {
                imgTypesList.add(imagingType);
            }
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
                    algorithmsList.add(algorithm);
                    wellHasImagingType.setAlgorithm(algorithm);
                    i++;
                }
            });
        });
        //set some object referrals for algorithms and imaging types
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
        /**
         * Currently not saving anything to database
         */
//        // we need to check if other objects need to be stored
//        persistNewObjects();
//        //TODO: save project? save wells?
//        // save the experiment, save the migration data and update the experiment
//        experimentService.save(importedExperiment);
//        do not need to save WellHasImagingType: experimentService.saveMigrationDataForExperiment(importedExperiment);
//        importedExperiment = experimentService.update(importedExperiment);
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
            foundCellLines.forEach((cellLineType) -> {
                cellLineService.saveCellLineType(cellLineType);
            });
        }
        // assays
        List<Assay> foundAssays = assayService.findNewAssays(importedExperiment);
        if (!foundAssays.isEmpty()) {
            foundAssays.forEach((assay) -> {
                assayService.save(assay);
            });
        }
        // treatment types
        List<TreatmentType> foundTreatmentTypes = treatmentService.findNewTreatmentTypes(importedExperiment);
        if (!foundTreatmentTypes.isEmpty()) {
            foundTreatmentTypes.forEach((treatmentType) -> {
                treatmentService.saveTreatmentType(treatmentType);
            });
        }
    }

    private void setupTracksData() {
        importedExperiment.getPlateConditionList().forEach((platecondition) -> {
            platecondition.getImagedWells().forEach((well) -> {
                Integer x = well.getColumnNumber();
                Integer y = well.getRowNumber();
                // check well row and column to get the right tracks
                for (WellHasImagingType imagingType : well.getWellHasImagingTypeList()) {
                    // wells that have no track data will give nullpointer!!!
                    // set these wells to not imaged = empty WellHasImagingTypeList (method used in normal single cell analysis)
                    BiotracksDataHolder dataHolder = biotracksDataHolders.get(imagingType.getAlgorithm().getAlgorithmName() + x + y);

                    // read linksMap and setup SQL track table, copy link id (CellMissy cannot currently handle split or merge events)
                    // size of nested links list for track length 
                    HashMap<Integer, List<Integer>> linksMap = dataHolder.getLinksMap();
                    HashMap<Integer, List<Double>> objectsMap = dataHolder.getObjectsMap();
                    List<Track> trackList = new ArrayList<>();

                    for (Integer trackNo : linksMap.keySet()) {
                        //create track and set stuff
                        List<Integer> trackedObjects = linksMap.get(trackNo);
                        Track track = new Track(Integer.toUnsignedLong(trackNo), trackNo, trackedObjects.size());

                        //create track points
                        List<TrackPoint> trackPointList = new ArrayList<>();
                        //cycle through trackedObjects and get info from objectsMap
                        for (Integer objectid : trackedObjects) {
                            List<Double> objectInfo = objectsMap.get(objectid);
                            TrackPoint point = new TrackPoint(Integer.toUnsignedLong(objectid), objectInfo.get(0).intValue(),
                                    objectInfo.get(2), objectInfo.get(1));
                            point.setTrack(track);
                            trackPointList.add(point);
                        }
                        track.setTrackPointList(trackPointList);
                        track.setWellHasImagingType(imagingType);
                        trackList.add(track);
                    }
                    imagingType.setTrackList(trackList);
                }
            });
        });
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
        //seems like it needs a final return here, even if it might never be used
        return null;
    }


}
