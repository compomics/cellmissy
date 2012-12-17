/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("setupReportController")
public class SetupReportController {

    // model
    private Experiment experiment;
    private Document document;
    private PdfWriter writer;
    private static Font titleFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
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
        try {
            createPdfFile(new FileOutputStream(pdfFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return pdfFile;
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
            ex.printStackTrace();
        }
    }

    /**
     * Add content to PDF file
     */
    private void addContent() {
        addOverview();
        addEmptyLine(1);
        addPlateView();
        addEmptyLine(1);
        addConditionsSummary();
    }

    /**
     * Add general info on report
     */
    private void addOverview() {
        String title = "Setup Report of Experiment " + experiment.getExperimentNumber() + " - " + "Project " + experiment.getProject().getProjectNumber();
        addTitle(title);
        addEmptyLine(1);
    }

    /**
     * Add plate view
     */
    private void addPlateView() {
        SetupPlatePanel setupPlatePanel = setupExperimentController.getSetupPlatePanel();
        Image imageFromJPanel = getImageFromJPanel(setupPlatePanel);
        float scale = 0.5f;
        imageFromJPanel.scalePercent(scale * 100);
        addImage(imageFromJPanel);
    }

    /**
     * Add summary with all conditions
     */
    private void addConditionsSummary() {
        JPanel conditionsOverviewPanel = setupExperimentController.getConditionsOverviewPanel();
        Image imageFromJPanel = getImageFromJPanel(conditionsOverviewPanel);
        float scale = (document.right() - document.left()) / ((float) imageFromJPanel.getWidth());
        imageFromJPanel.scalePercent(scale * 100);
        addImage(imageFromJPanel);
    }

    /**
     * 
     * @param paragraph
     * @param number 
     */
    private void addEmptyLine(int number) {
        for (int i = 0; i < number; i++) {
            try {
                document.add(new Paragraph(" "));
            } catch (DocumentException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Add a simple line with some text
     * @param title to add
     */
    private void addTitle(String title) {
        try {
            //Paragraph paragraph = new Paragraph(title, titleFont);
            Chunk chunk = new Chunk(title, titleFont);
            Paragraph paragraph = new Paragraph(chunk);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(1.5f);
            document.add(paragraph);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a pdfTemplate with a JPanel in it
     */
    private void addImage(Image image) {
        // put image in the center
        image.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(image);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     */
    private Image getImageFromJPanel(JPanel panel) {
        Image image = null;
        int width = panel.getWidth();
        int height = panel.getHeight();
        PdfContentByte contentByte = writer.getDirectContent();
        PdfTemplate template = contentByte.createTemplate(width, height);
        Graphics2D graphics = template.createGraphics(width, height);
        panel.paint(graphics);
        graphics.dispose();
        // wrap the pdfTemplate inside an image ensures better quality (pixels) 
        try {
            image = Image.getInstance(template);
        } catch (BadElementException ex) {
            ex.printStackTrace();
        }
        return image;
    }
}