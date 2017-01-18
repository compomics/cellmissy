/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRDataLoadingPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author CompOmics Gwen
 */
@Controller("loadGenericDRDataController")
public class LoadGenericDRDataController {
    
    //model
    //need to somehow save
    //view
    private DRDataLoadingPanel dataLoadingPanel;    
    //parent controller
    @Autowired
    private GenericDoseResponseController doseResponseController;
    
    /**
     * Getters and setters.
     */
    
    
    public DRDataLoadingPanel getDataLoadingPanel() {
        return dataLoadingPanel;
    }

    public void init() {
        initDataLoadingPanel();
    }
    
    //init view
    private void initDataLoadingPanel() {
        
        
        
        
        
        //Action Listeners
        dataLoadingPanel.getChooseFileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a JFile Chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose a tabular file (XML, TXT, CSV...) for the import of the experiment");
//                // to select only xml files
//                fileChooser.setFileFilter(new FileFilter() {
//                    @Override
//                    public boolean accept(File f) {
//                        if (f.isDirectory()) {
//                            return true;
//                        }
//                        int index = f.getName().lastIndexOf(".");
//                        String extension = f.getName().substring(index + 1);
//                        return extension.equals("xml");
//                    }
//
//                    @Override
//                    public String getDescription() {
//                        return (".xml files");
//                    }
//                });
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
                }            }
        });
    }
}
