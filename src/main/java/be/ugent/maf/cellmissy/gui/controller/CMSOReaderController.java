/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.cmso.CMSOReaderPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
 * @author CompOmics Gwen
 */
@Controller("cMSOReaderController")
public class CMSOReaderController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CMSOReaderController.class);
    //model
    //view
    private CMSOReaderPanel cmsoReaderPanel;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        cmsoReaderPanel = new CMSOReaderPanel();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init view
        initCMSOReaderPanel();
    }

    /**
     * Initialize view
     */
    private void initCMSOReaderPanel() {

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
                    } // else display error popup
                    else {
                        JOptionPane.showMessageDialog(cmsoReaderPanel, "The chosen directory does not seem to be a CMSO dataset", "Folder name incorrect", JOptionPane.INFORMATION_MESSAGE);
                    }
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
        //reset text fields
        cmsoReaderPanel.getFolderTextField().setText("");
        cmsoReaderPanel.getSummaryTextArea().setText("");
        cmsoReaderPanel.getIsaTextArea().setText("");
        cmsoReaderPanel.getOmeTextArea().setText("");
        cmsoReaderPanel.getBiotracksTextArea().setText("");
    }

    /**
     *
     * @param file A folder that contains a CMSO dataset
     */
    private void parseCMSODataset(File datasetFolder) {
        try {
//            //in case of using separate parser
//            data = Parser.parseFile(file);

            File[] entireDataset = datasetFolder.listFiles();
            File[] isaFiles;
            List<File> biotracksFolders = new ArrayList<>(); //separate folder per tracking software used

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

                    String isaSummary = parseISAFiles(isaFiles);
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
                    //add every inner folder to list (all called "dp")
                    //when implementing downstream analysis, might need to make biotracksFolders a class member
                    biotracksFolders.add(file);

                }
            }
            String biotracksText = "";
            if (!biotracksFolders.isEmpty()) {
                // dp folders contain objects and links csv and json metadata

                for (File trackingData : biotracksFolders) {
                    // per folder, show: software name, total #objects, 
                    // total #links + parameters contained in objects table (x,y,z,t...)
                    biotracksText += "Software: " + trackingData.getName() + "\n";
                    //only need the csv's
                    File[] dp = trackingData.listFiles();
                }

            } else {
                biotracksText += "No tracks files present in the dataset.";
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

    private String parseISAFiles(File[] isaFiles) {
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
                            text += "\n \n";

                        }

                    }
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
                        // using indices for column, inferring header is not possible
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
        isaText = Arrays.toString(isaTextArray);
        return isaText
                + "\n -- More details in the ISA files.";
    }
}
