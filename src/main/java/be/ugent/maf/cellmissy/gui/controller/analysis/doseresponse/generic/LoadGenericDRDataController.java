/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.entity.result.doseresponse.ImportedDRDataHolder;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRDataLoadingPanel;
import be.ugent.maf.cellmissy.parser.GenericInputFileParser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of reading and saving dose-response and metadata
 * for analysis and display in an analysis report.
 *
 * @author Gwendolien Sergeant
 */
@Controller("loadGenericDRDataController")
public class LoadGenericDRDataController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadGenericDRDataController.class);

    //model
    //need to somehow save the information the user fills in manually
    //view
    private DRDataLoadingPanel dataLoadingPanel;
    private WaitingDialog waitingDialog;
    //parent controller
    @Autowired
    private GenericDoseResponseController doseResponseController;
    //services
    @Autowired
    private GenericInputFileParser genericInputFileParser;

    /**
     * Getters and setters.
     *
     * @return
     */
    public DRDataLoadingPanel getDataLoadingPanel() {
        return dataLoadingPanel;
    }

    public void init() {
        initDataLoadingPanel();
        // make a new waiting dialog here
        waitingDialog = new WaitingDialog(doseResponseController.getCellMissyFrame(), true);
    }

    public void reset() {
        dataLoadingPanel.getFileLabel().setText("");
        dataLoadingPanel.getAssayTextField().setText("");
        dataLoadingPanel.getCellLineTextField().setText("");
        dataLoadingPanel.getDatasetTextField().setText("");
        dataLoadingPanel.getExpNumberTextField().setText("");
        dataLoadingPanel.getExpTitleTextField().setText("");
        dataLoadingPanel.getPlateFormatTextField().setText("");
        dataLoadingPanel.getPurposeTextArea().setText("");
        dataLoadingPanel.getTreatmentTextField().setText("");
    }

    /**
     * Read the view's text areas and fills in corresponding fields in the
     * imported data holder. This method is executed when the user presses the
     * next button.
     *
     * @param dRDataHolder
     */
    public void setManualMetaData(ImportedDRDataHolder dRDataHolder) {
        // !!! METHODS MIGHT GIVE NULLPOINTER EX WHEN THERE IS NO TEXT! --> FIND WORKAROUND
        dRDataHolder.setExperimentNumber(dataLoadingPanel.getExpNumberTextField().getText());
        dRDataHolder.setExperimentTitle(dataLoadingPanel.getExpTitleTextField().getText());
        dRDataHolder.setDataset(dataLoadingPanel.getDatasetTextField().getText());
        dRDataHolder.setAssayType(dataLoadingPanel.getAssayTextField().getText());
        dRDataHolder.setCellLine(dataLoadingPanel.getCellLineTextField().getText());
        dRDataHolder.setPlateFormat(dataLoadingPanel.getPlateFormatTextField().getText());
        dRDataHolder.setTreatmentName(dataLoadingPanel.getTreatmentTextField().getText());
        dRDataHolder.setPurpose(dataLoadingPanel.getPurposeTextArea().getText());
    }

    /**
     * Private methods
     */
    //init view
    private void initDataLoadingPanel() {
        dataLoadingPanel = new DRDataLoadingPanel();
        /**
         * Action Listeners.
         */
        //Popup file selector and import and parse the file
        dataLoadingPanel.getChooseFileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a JFile Chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose a tabular file (XLS, XLSX, CSV, TSV) for the import of the experiment");
                // to select only appropriate files
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }
                        int index = f.getName().lastIndexOf(".");
                        String extension = f.getName().substring(index + 1);
                        return extension.equals("xls") || extension.equals("xlsx") || extension.equals("csv") || extension.equals("tsv");
                    }

                    @Override
                    public String getDescription() {
                        return (".xls, .xlsx, .csv and .tsv files");
                    }
                });
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setAcceptAllFileFilterUsed(false);
                // in response to the button click, show open dialog
                int returnVal = fileChooser.showOpenDialog(dataLoadingPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
//                    // create and execute a new swing worker with the selected file for the import
//                    ImportExperimentSwingWorker importExperimentSwingWorker = new ImportExperimentSwingWorker(chosenFile);
//                    importExperimentSwingWorker.execute();
                    parseDRFile(chosenFile);
                    if (doseResponseController.getImportedDRDataHolder().getDoseResponseData() != null) {
                        dataLoadingPanel.getFileLabel().setText(chosenFile.getAbsolutePath());
                        doseResponseController.getGenericDRParentPanel().getNextButton().setEnabled(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(dataLoadingPanel, "Command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        dataLoadingPanel.getLogTransformCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    doseResponseController.setLogTransform(true);
                } else {
                    doseResponseController.setLogTransform(false);
                }
            }
        });
    }

    private void parseDRFile(File dRFile) {

        try {
            //parsing and setting separated into two lines for debugging
            List<DoseResponsePair> data = genericInputFileParser.parseDoseResponseFile(dRFile);
            doseResponseController.getImportedDRDataHolder().setDoseResponseData(data);
        } catch (FileParserException ex) {
            LOG.error(ex.getMessage());
            doseResponseController.showMessage(ex.getMessage(), "Error in input file", JOptionPane.ERROR_MESSAGE);
        }
    }
//  //COMMENTED THIS WHOLE SECTION BECAUSE showWaitingDialog WOULD FREEZE ALL OTHER CODE EXECUTION AND THUS NOT PARSING THE DATA IN THE BACKGROUND
//    //MIGHT BE NECESSARY TO REIMPLEMENT IN THE FUTURE IF USERS USE LARGE FILES
//    /**
//     * Show the waiting dialog: set the title and center the dialog on the main
//     * frame. Set the dialog to visible.
//     *
//     * @param title
//     */
//    private void showWaitingDialog(String title) {
//        waitingDialog.setTitle(title);
//        GuiUtils.centerDialogOnFrame(doseResponseController.getCellMissyFrame(), waitingDialog);
//        waitingDialog.setVisible(true);
//    }
//
//    /**
//     * Swing worker to import dose-response data from a file.
//     */
//    private class ImportExperimentSwingWorker extends SwingWorker<Void, Void> {
//
//        // the XML file that has to be parsed to import the experiment
//        private final File dRFile;
//
//        public ImportExperimentSwingWorker(File dRFile) {
//            this.dRFile = dRFile;
//        }
//
//        @Override
//        protected Void doInBackground() throws Exception {
//            // show waiting dialog
//            String title = "File is being parsed. Please wait...";
//            showWaitingDialog(title);
//            // parse xmlfile
//            parseDRFile(dRFile);
//            return null;
//        }
//
//        @Override
//        protected void done() {
//            try {
//                get();
//                waitingDialog.setVisible(false);
//                // if parsing the file was successfull and the loaded data is not null, we can enable the next experiment button
//                if (doseResponseController.getImportedDRDataHolder().getDoseResponseData() != null) {
//                    dataLoadingPanel.getFileLabel().setText(dRFile.getAbsolutePath());
//                    doseResponseController.getGenericDRParentPanel().getNextButton().setEnabled(true);
//                }
//            } catch (InterruptedException | ExecutionException | CancellationException ex) {
//                JOptionPane.showMessageDialog(dataLoadingPanel, "Unexpected error: " + ex.getMessage(), "unexpected error", JOptionPane.ERROR_MESSAGE);
//                LOG.error(ex.getMessage(), ex);
//            }
//        }
//    }
}
