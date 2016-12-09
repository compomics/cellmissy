/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.analysis.doseresponse.SharedDoseResponse;
import be.ugent.maf.cellmissy.analysis.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author CompOmics Gwen
 */
public class GenericDoseResponseController {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericDoseResponseController.class);
    
    //model
    private SharedDoseResponse sharedDoseResponse;
    //view
    
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private GenericDRInputController dRInputController;
    @Autowired
    private GenericDRInitialController dRInitialController;
    @Autowired
    private GenericDRNormalizedController dRNormalizedController;
    @Autowired
    private GenericDRResultsController dRResultsController;
    // services
    @Autowired
    private SigmoidFitter sigmoidFitter;
    private GridBagConstraints gridBagConstraints;
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Ask user to choose for a directory and invoke swing worker for creating
     * PDF report
     *
     * @throws IOException
     */
    protected void createPdfReport() throws IOException {
        // choose directory to save pdf file
        JFileChooser chooseDirectory = new JFileChooser();
        chooseDirectory.setDialogTitle("Choose a directory to save the report");
        chooseDirectory.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        needs more information
        chooseDirectory.setSelectedFile(new File("Dose Response Report " + ".pdf"));
        // in response to the button click, show open dialog
//        TEST WHETHER THIS PARENT PANEL/FRAME IS OKAY
        int returnVal = chooseDirectory.showSaveDialog(cellMissyController.getCellMissyFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File directory = chooseDirectory.getCurrentDirectory();
            DoseResponseReportSwingWorker doseResponseReportSwingWorker = new DoseResponseReportSwingWorker(directory, chooseDirectory.getSelectedFile().getName());
            doseResponseReportSwingWorker.execute();
        } else {
            cellMissyController.showMessage("Open command cancelled by user", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * Swing Worker to generate PDF report
     */
    private class DoseResponseReportSwingWorker extends SwingWorker<File, Void> {

        private final File directory;
        private final String reportName;

        public DoseResponseReportSwingWorker(File directory, String reportName) {
            this.directory = directory;
            this.reportName = reportName;
        }

        @Override
        protected File doInBackground() throws Exception {
            // disable button
            dRResultsController.getdRResultsPanel().getCreateReportButton().setEnabled(false);
            //set cursor to waiting one
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //call the child controller to create report
            return dRResultsController.createAnalysisReport(directory, reportName);
        }

        @Override
        protected void done() {
            File file = null;
            try {
                file = get();
            } catch (InterruptedException | CancellationException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
            try {
                //if export to PDF was successful, open the PDF file from the desktop
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.showMessage("Cannot open the file!" + "\n" + ex.getMessage(), "error while opening file", JOptionPane.ERROR_MESSAGE);
            }
            //set cursor back to default
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // enable button
            dRResultsController.getdRResultsPanel().getCreateReportButton().setEnabled(true);
        }
    }
}
