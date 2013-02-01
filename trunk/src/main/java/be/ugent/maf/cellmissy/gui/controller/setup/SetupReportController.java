/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.PdfUtils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("setupReportController")
public class SetupReportController {

    private static final Logger LOG = Logger.getLogger(SetupReportController.class);
    // model
    private Experiment experiment;
    private Document document;
    private PdfWriter writer;
    private static Font titleFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    private static Font bodyFont = new Font(Font.TIMES_ROMAN, 8);
    // view
    // parent controller
    @Autowired
    private SetupExperimentController setupExperimentController;
    //services

    /**
     *
     * @param directory
     * @return
     */
    public File createSetupReport(File directory) {
        this.experiment = setupExperimentController.getExperiment();
        int experimentNumber = experiment.getExperimentNumber();
        int projectNumber = experiment.getProject().getProjectNumber();
        String reportName = "Setup Report " + experimentNumber + " - " + projectNumber + ".pdf";
        File pdfFile = new File(directory, reportName);
        tryToCreateFile(pdfFile);
        return pdfFile;
    }

    /**
     *
     * @param pdfFile
     */
    private void tryToCreateFile(File pdfFile) {
        try {
            boolean success = pdfFile.createNewFile();
            if (success) {
                setupExperimentController.showMessage("Pdf Report successfully created!", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Object[] options = {"Yes", "No", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(null, "File already exists. Do you want to replace it?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
                // if YES, user wants to delete existing file and replace it
                if (showOptionDialog == 0) {
                    boolean delete = pdfFile.delete();
                    if (!delete) {
                        return;
                    }
                    // if NO or CANCEL, returns already existing file
                } else if (showOptionDialog == 1 || showOptionDialog == 2) {
                    return;
                }
            }
        } catch (IOException ex) {
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            // actually create PDF file
            createPdfFile(fileOutputStream);
        } catch (IOException ex) {
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *
     */
    private void createPdfFile(FileOutputStream outputStream) {
        document = null;
        writer = null;
        try {
            // get new instances
            // the Document is the base layout element
            document = new Document(PageSize.A4.rotate());
            // the pdfWriter is actually creating the file
            writer = PdfWriter.getInstance(document, outputStream);
            //open document
            document.open();
            // add content to document
            addContent();
            //dispose resources
            document.close();
            document = null;
            writer.close();
            writer = null;
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Add content to PDF file
     */
    private void addContent() {
        addMainTitle();
        PdfUtils.addEmptyLines(document, 1);
        addPlatePanel();
        PdfUtils.addEmptyLines(document, 1);
        addConditionsTable();
    }

    /**
     * Add general info on report
     */
    private void addMainTitle() {
        String title = "Setup Report of Experiment " + experiment.getExperimentNumber() + " - " + "Project " + experiment.getProject().getProjectNumber();
        PdfUtils.addTitle(document, title, titleFont);
        PdfUtils.addEmptyLines(document, 1);
    }

    /**
     * Add plate view
     */
    private void addPlatePanel() {
        AnalysisPlatePanel platePanel = createPanelView();
        addImageFromJPanel(platePanel, platePanel.getWidth(), platePanel.getHeight());
    }

    /**
     * Create panel view in the PDF file
     *
     * @return
     */
    private AnalysisPlatePanel createPanelView() {
        // what we need to show is actually an analysis plate panel
        AnalysisPlatePanel platePanel = new AnalysisPlatePanel();
        platePanel.initPanel(setupExperimentController.getSelectedPlateFormat(), new Dimension(400, 500));
        platePanel.setExperiment(experiment);
        return platePanel;
    }

    /**
     * Create Image from a aJFreeChart and add it to document
     *
     * @param chart
     */
    private void addImageFromJPanel(JPanel panel, int imageWidth, int imageHeight) {
        Image imageFromJPanel = PdfUtils.getImageFromJPanel(writer, panel, imageWidth, imageHeight);
        // put image in the center
        imageFromJPanel.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(imageFromJPanel);
        } catch (DocumentException ex) {
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Add table with conditions overview
     */
    private void addConditionsTable() {
        PdfPTable conditionsTable = createConditionsTable();
        addTable(conditionsTable);
    }

    /**
     * Create Table with conditions overview
     *
     * @return
     */
    private PdfPTable createConditionsTable() {
        // 7 columns
        PdfPTable dataTable = new PdfPTable(7);
        PdfUtils.setUpPdfPTable(dataTable);
        PdfUtils.addCustomizedCell(dataTable, "Condition", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Cell Line", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "MD", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Assay", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "ECM", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Treatments", titleFont);
        PdfUtils.addCustomizedCell(dataTable, "Assay(Medium, %Serum)", titleFont);
        Collection<PlateCondition> plateConditionCollection = experiment.getPlateConditionCollection();
        List<PlateCondition> plateConditions = new ArrayList<>(plateConditionCollection);
        for (int i = 0; i < plateConditions.size(); i++) {
            PlateCondition plateCondition = plateConditions.get(i);
            Color color = GuiUtils.getAvailableColors()[i + 1];
            PdfUtils.addColoredCell(dataTable, color);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getCellLine().toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getAssay().getMatrixDimension().getDimension(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getAssay().getAssayType(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getEcm().toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getTreatmentCollection().toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getAssayMedium().toString(), bodyFont);
        }
        return dataTable;
    }

    /**
     * Add a PdfPTable to the document
     *
     * @param dataTable
     */
    private void addTable(PdfPTable dataTable) {
        try {
            document.add(dataTable);
        } catch (DocumentException ex) {
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", JOptionPane.ERROR_MESSAGE);
        }
    }
}