/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.setup;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.AssayMedium;
import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.plate.PdfPlatePanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.PdfUtils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private static Font bodyFont = new Font(Font.HELVETICA, 8);
    private static Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
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
        String reportName = "Set-up Report " + experiment.toString() + " - " + experiment.getProject().toString() + ".pdf";
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
            boolean success;
            success = pdfFile.createNewFile();
            if (success) {
                setupExperimentController.showMessage("Pdf Report successfully created!", "Report created", JOptionPane.INFORMATION_MESSAGE);
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
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            // actually create PDF file
            createPdfFile(fileOutputStream);
        } catch (IOException ex) {
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Create PDF File: create and open a new document, add content to it and
     * close the document. Makes use of a PdfWriter with a given
     * FileOutputStream
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
        // main title of file + empty line
        addMainTitle();
        // add some general extra info
        addGeneralInfo();
        // plate panel view + another empty line
        addPlatePanel();
        PdfUtils.addEmptyLines(document, 1);
        // table with summary of all conditions
        addConditionsTable();
        // start from new page and add detailed info for each condition
        document.newPage();
        addParagraphPerCondition();
    }

    /**
     * Add general info on report
     */
    private void addMainTitle() {
        DecimalFormat df = new DecimalFormat("000");
        String expNumber = df.format(experiment.getExperimentNumber());
        String projNumber = df.format(experiment.getProject().getProjectNumber());
        String title = "Setup report of Experiment " + expNumber + " - " + "Project " + projNumber;
        PdfUtils.addTitle(document, title, titleFont);
        PdfUtils.addEmptyLines(document, 1);
    }

    /**
     * Add some extra info to PDF file.
     */
    private void addGeneralInfo() {
        List<String> lines = new ArrayList<>();
        String line = "Experiment date: " + experiment.getExperimentDate();
        lines.add(line);
        line = "Experiment purpose: " + experiment.getPurpose();
        lines.add(line);
        line = "Instrument: " + experiment.getInstrument() + ", magnification: " + experiment.getMagnification();
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
    }

    /**
     * Add plate view.
     */
    private void addPlatePanel() {
        PdfPlatePanel pdfPlatePanel = createPanelView();
        addImageFromJPanel(pdfPlatePanel, pdfPlatePanel.getWidth(), pdfPlatePanel.getHeight());
    }

    /**
     * Create panel view in the PDF file
     *
     * @return
     */
    private PdfPlatePanel createPanelView() {
        // what we need to show is actually an analysis plate panel
        PdfPlatePanel pdfPlatePanel = new PdfPlatePanel();
        pdfPlatePanel.initPanel(experiment.getPlateFormat(), new Dimension(400, 500));
        pdfPlatePanel.setExperiment(experiment);
        return pdfPlatePanel;
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
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
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
     * Add a paragraph for each condition
     */
    private void addParagraphPerCondition() {
        // add main title for section
        PdfUtils.addTitle(document, "BIOLOGICAL CONDITIONS", titleFont);
        List<PlateCondition> plateConditions = new ArrayList(experiment.getPlateConditionList());
        for (int i = 0; i < plateConditions.size(); i++) {
            Paragraph paragraph = new Paragraph("" + plateConditions.get(i).getName(), titleFont);
            //set font color to condition index
            int lenght = GuiUtils.getAvailableColors().length;
            int conditionIndex = plateConditions.get(i).getConditionIndex() - 1;
            int indexOfColor = conditionIndex % lenght;
            titleFont.setColor(GuiUtils.getAvailableColors()[indexOfColor]);
            try {
                document.add(paragraph);
                addConditionInfo(plateConditions.get(i));
                PdfUtils.addEmptyLines(document, 1);
            } catch (DocumentException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Add info for each condition set up
     *
     * @param plateCondition
     */
    private void addConditionInfo(PlateCondition plateCondition) {
        // strings for text
        List<String> lines = new ArrayList<>();
        // set font color back to BLACK
        titleFont.setColor(Color.black);
        // technical replicates: how many, which ones on the plate
        String line = "TECHNICAL REPLICATES";
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, titleFont);
        lines.clear();
        List<Well> wellList = plateCondition.getWellList();
        line = "Number of wells: " + wellList.size();
        lines.add(line);
        line = "Wells (column, row): " + wellList;
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        lines.clear();
        // cell line: name, seeding density, seeding time, growth medium, serum, serum concentration
        line = "CELL LINE";
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, titleFont);
        lines.clear();
        CellLine cellLine = plateCondition.getCellLine();
        line = "Cell line name: " + cellLine.getCellLineType();
        lines.add(line);
        line = "Seeding density: " + cellLine.getSeedingDensity() + " cells/well";
        lines.add(line);
        line = "Seeding time: " + cellLine.getSeedingTime();
        lines.add(line);
        line = "Growth medium: " + cellLine.getGrowthMedium();
        lines.add(line);
        line = "Growth medium serum: " + cellLine.getSerum();
        lines.add(line);
        line = "Serum concentration: " + cellLine.getSerumConcentration() + " %";
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        lines.clear();
        // Assay
        line = "ASSAY";
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, titleFont);
        lines.clear();
        Assay assay = plateCondition.getAssay();
        line = "Assay: " + assay.getAssayType();
        lines.add(line);
        AssayMedium assayMedium = plateCondition.getAssayMedium();
        line = "Assay medium: " + assayMedium.getMedium();
        lines.add(line);
        line = "Assay medium serum: " + assayMedium.getSerum();
        lines.add(line);
        line = "Serum concentration: " + assayMedium.getSerumConcentration() + " %";
        lines.add(line);
        line = "Medium volume: " + assayMedium.getVolume() + "  " + "\u00B5" + "l";
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        lines.clear();
        // ECM
        line = "ECM";
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, titleFont);
        lines.clear();
        Ecm ecm = plateCondition.getEcm();
        line = "ECM dimension: " + assay.getMatrixDimension().getDimension();
        lines.add(line);
        line = "ECM composition: " + ecm.getEcmComposition();
        lines.add(line);
        switch (assay.getMatrixDimension().getDimension()) {
            case "2D":
                line = "ECM coating: monomeric coating";
                lines.add(line);
                line = "ECM coating temperature : " + ecm.getCoatingTemperature();
                lines.add(line);
                line = "ECM coating time (min): " + ecm.getCoatingTime();
                lines.add(line);
                line = "ECM concentration: " + ecm.getConcentration() + " " + ecm.getConcentrationUnit();
                lines.add(line);
                line = "ECM volume: " + ecm.getVolume() + " " + ecm.getVolumeUnit();
                lines.add(line);
                break;
            case "2.5D":
                line = "ECM density: " + ecm.getEcmDensity();
                lines.add(line);
                line = "Bottom matrix type: " + ecm.getBottomMatrix().getType();
                lines.add(line);
                switch (ecm.getBottomMatrix().getType()) {
                    case "thin gel coating":
                        break;
                    case "gel":
                        line = "Bottom matrix volume: " + ecm.getBottomMatrixVolume() + "\u00B5" + "l";
                        lines.add(line);
                        break;
                }
                line = "ECM polymerisation time (min): " + ecm.getPolymerisationTime();
                lines.add(line);
                line = "ECM polymerisation temperature: " + ecm.getPolymerisationTemperature();
                lines.add(line);
                line = "ECM polymerisation pH: " + ecm.getPolymerisationPh();
                lines.add(line);
                break;
            case "3D":
                line = "ECM density: " + ecm.getEcmDensity();
                lines.add(line);
                line = "Bottom matrix type: " + ecm.getBottomMatrix().getType();
                lines.add(line);
                switch (ecm.getBottomMatrix().getType()) {
                    case "thin gel coating":
                        break;
                    case "gel":
                        line = "Bottom matrix volume: " + ecm.getBottomMatrixVolume() + "\u00B5" + "l";
                        lines.add(line);
                        break;
                }
                line = "Top matrix volume: " + ecm.getTopMatrixVolume() + "\u00B5" + "l";
                lines.add(line);
                line = "ECM polymerisation time (min): " + ecm.getPolymerisationTime();
                lines.add(line);
                line = "ECM polymerisation temperature: " + ecm.getPolymerisationTemperature();
                lines.add(line);
                line = "ECM polymerisation pH: " + ecm.getPolymerisationPh();
                lines.add(line);
                break;
        }
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
        lines.clear();
        // drugs/treatments
        line = "DRUGS/TREATMENTS";
        lines.add(line);
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, titleFont);
        lines.clear();
        List<Treatment> treatments = plateCondition.getTreatmentList();
        for (int i = 0; i < treatments.size(); i++) {
            Treatment treatment = treatments.get(i);
            line = "Treatment type: " + treatment.getTreatmentType();
            lines.add(line);
            line = "Treatment concentration: " + treatment.getConcentration() + " " + treatment.getConcentrationUnit();
            lines.add(line);
            line = "Time of addition: " + treatment.getTiming();
            lines.add(line);
            if (treatment.getDrugSolvent() != null) {
                line = "Drug solvent: " + treatment.getDrugSolvent();
                lines.add(line);
                line = "Solvent final concentration: " + treatment.getDrugSolventConcentration() + " %";
                lines.add(line);
            }
        }
        PdfUtils.addText(document, lines, false, Element.ALIGN_JUSTIFIED, bodyFont);
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
        List<PlateCondition> plateConditions = experiment.getPlateConditionList();
        int lenght = GuiUtils.getAvailableColors().length;
        for (int i = 0; i < plateConditions.size(); i++) {
            PlateCondition plateCondition = plateConditions.get(i);
            int conditionIndex = plateConditions.get(i).getConditionIndex() - 1;
            int indexOfColor = conditionIndex % lenght;
            Color color = GuiUtils.getAvailableColors()[indexOfColor];
            PdfUtils.addColoredCell(dataTable, color);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getCellLine().toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getAssay().getMatrixDimension().getDimension(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getAssay().getAssayType(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getEcm().toString(), bodyFont);
            PdfUtils.addCustomizedCell(dataTable, plateCondition.getTreatmentList().toString(), bodyFont);
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
            setupExperimentController.showMessage("Unexpected error: " + ex.getMessage() + ".", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
    }
}