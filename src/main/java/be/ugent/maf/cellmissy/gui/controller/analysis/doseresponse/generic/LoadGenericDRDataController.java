/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.gui.WaitingDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRDataLoadingPanel;
import be.ugent.maf.cellmissy.parser.GenericInputFileParser;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
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
    private LinkedHashMap<Double, List<Double>> importedData;
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

    public LinkedHashMap<Double, List<Double>> getImportedData() {
        return importedData;
    }

    public void init() {
        initDataLoadingPanel();
        // make a new waiting dialog here
        waitingDialog = new WaitingDialog(doseResponseController.getCellMissyFrame(), true);
    }
    
    /**
     * On reset of analysis, also reset importedData field.
     */
    public void reset() {
        importedData = null;
    }
    
    //init view
    private void initDataLoadingPanel() {

        //Action Listeners
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
                    // create and execute a new swing worker with the selected file for the import
                    ImportExperimentSwingWorker importExperimentSwingWorker = new ImportExperimentSwingWorker(chosenFile);
                    importExperimentSwingWorker.execute();
                } else {
                    JOptionPane.showMessageDialog(dataLoadingPanel, "Command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }
    
    private void parseDRFile(File dRFile) {
        
        try {
            this.importedData = genericInputFileParser.parseDoseResponseFile(dRFile);
        } catch (FileParserException ex) {
            LOG.error(ex.getMessage());
            doseResponseController.showMessage(ex.getMessage(), "Generic input file error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Show the waiting dialog: set the title and center the dialog on the main
     * frame. Set the dialog to visible.
     *
     * @param title
     */
    private void showWaitingDialog(String title) {
        waitingDialog.setTitle(title);
        GuiUtils.centerDialogOnFrame(doseResponseController.getCellMissyFrame(), waitingDialog);
        waitingDialog.setVisible(true);
    }

    /**
     * Swing worker to import dose-response data from a file.
     */
    private class ImportExperimentSwingWorker extends SwingWorker<Void, Void> {

        // the XML file that has to be parsed to import the experiment
        private final File dRFile;

        public ImportExperimentSwingWorker(File dRFile) {
            this.dRFile = dRFile;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting dialog
            String title = "File is being parsed. Please wait...";
            showWaitingDialog(title);
            // parse xmlfile
            parseDRFile(dRFile);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                waitingDialog.setVisible(false);
                // if parsing the file was successfull and the loaded data is not null, we can enable the next experiment button
                if (importedData != null) {
                    doseResponseController.getGenericDRParentPanel().getNextButton().setEnabled(true);
                }
            } catch (InterruptedException | ExecutionException | CancellationException ex) {
                JOptionPane.showMessageDialog(dataLoadingPanel, "Unexpected error: " + ex.getMessage(), "unexpected error", JOptionPane.ERROR_MESSAGE);
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
