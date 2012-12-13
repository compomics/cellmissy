/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import com.compomics.util.Export;
import com.compomics.util.enumeration.ImageType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("areaAnalysisReportController")
public class AreaAnalysisReportController {

    //model
    private PDDocument document;
    private final static PDSimpleFont normalFont = PDType1Font.HELVETICA;
    private final static PDSimpleFont boldFont = PDType1Font.HELVETICA_BOLD;
    private final static int normalFontSize = 10;
    private final static int bigFontSize = 12;
    private float height;
    //view
    //parent controller
    @Autowired
    private AreaAnalysisController areaAnalysisController;
    //child controllers
    //services

    /**
     * Initialize controller
     */
    public void init() {
        try {
            document = new PDDocument();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @throws IOException 
     */
    public void createAnalysisReport() throws IOException {
        createOverview();
        closeAndSaveReport();
    }

    /**
     * create first section of Report
     */
    private void createOverview() throws IOException {

        Experiment experiment = areaAnalysisController.getExperiment();
        //lines to be printed
        List<String> lines = new ArrayList<>();
        String line = "OVERVIEW";
        lines.add(line);
        line = "Analysis Report of Experiment " + experiment.getExperimentNumber() + " - " + "Project " + experiment.getProject().getProjectNumber();
        lines.add(line);
        line = "Number of Conditions: " + experiment.getPlateConditionCollection().size();
        lines.add(line);

        // descriptions for plate conditions
        List<PlateCondition> plateConditonsList = new ArrayList<>(experiment.getPlateConditionCollection());
        for (PlateCondition plateCondition : plateConditonsList) {
            lines.add("Condition " + (plateConditonsList.indexOf(plateCondition) + 1) + ": " + plateCondition.toString());
        }

        height = normalFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000;
        height = height * normalFontSize * 1.05f;
        int margin = 20;
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = null;
        float y = -1;
        for (int i = 0; i < lines.size(); i++) {

            if (y < margin) {
                if (contentStream != null) {
                    contentStream.endText();
                    contentStream.close();
                }
                contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(normalFont, normalFontSize);
                contentStream.beginText();
                y = page.getMediaBox().getHeight() - margin + height;
                contentStream.moveTextPositionByAmount(margin, y);
            }

            if (contentStream != null) {
                contentStream.moveTextPositionByAmount(0, -height);
                y -= height;
                contentStream.drawString(lines.get(i));
            }
        }
        // move to print Global view
        contentStream.moveTextPositionByAmount(0, -height);
        contentStream.drawString("GLOBAL VIEW");
        
        // add global view image
        y -= height;
        contentStream.moveTextPositionByAmount(0, -height);
        PDJpeg globalViewImage = getGlobalViewImage();

        contentStream.drawImage(globalViewImage, margin, y);

        if (contentStream != null) {
            contentStream.endText();
            contentStream.close();
        }
    }

    /**
     * 
     * @return 
     */
    private PDJpeg getGlobalViewImage() {
        PDJpeg pdj = null;
        JFreeChart chart = areaAnalysisController.getGlobalAreaChartPanel().getChart();
        BufferedImage createBufferedImage = chart.createBufferedImage(700, 300);
        try {
            pdj = new PDJpeg(document, createBufferedImage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return pdj;
    }

    /**
     * 
     */
    private void closeAndSaveReport() {
        try {
            File file = new File("C:\\CellMissy_Test", "Analysis-Report" + ".pdf");
            document.save(file.getPath());
            document.close();
        } catch (IOException | COSVisitorException ex) {
            ex.printStackTrace();
        }
    }
}
