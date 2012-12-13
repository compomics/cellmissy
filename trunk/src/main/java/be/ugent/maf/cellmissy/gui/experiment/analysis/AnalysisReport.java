/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis;

import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Class to generate a report for analysis
 * @author Paola Masuzzo
 */
public class AnalysisReport {

    private Experiment experiment;
    private Map<PlateCondition, AreaPreProcessingResults> preProcessingMap;
    private Map<PlateCondition, AreaAnalysisResults> analysisMap;
    private List<AnalysisGroup> analysisGroups;
    private PDDocument document;
    private final static PDSimpleFont normalFont = PDType1Font.HELVETICA;
    private final static PDSimpleFont boldFont = PDType1Font.HELVETICA_BOLD;
    private final static int normalFontSize = 10;
    private final static int bigFontSize = 12;

    /**
     * 
     * @param experiment
     * @param preProcessingMap
     * @param analysisMap
     * @param analysisGroups
     * @throws IOException  
     */
    public AnalysisReport(Experiment experiment, Map<PlateCondition, AreaPreProcessingResults> preProcessingMap, Map<PlateCondition, AreaAnalysisResults> analysisMap, List<AnalysisGroup> analysisGroups) throws IOException {
        this.experiment = experiment;
        this.preProcessingMap = preProcessingMap;
        this.analysisMap = analysisMap;
        this.analysisGroups = analysisGroups;
        setUpReport();
        closeAndSaveReport();
    }

    /**
     * 
     * @throws IOException 
     */
    private void setUpReport() throws IOException {
        // create new Document
        try {
            document = new PDDocument();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        createOverview();
    }

    /**
     * create first section of Report
     */
    private void createOverview() throws IOException {
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
            lines.add("Condition " + (plateConditonsList.indexOf(plateCondition) + 1)  + ": " + plateCondition.toString());
        }
               
        line = "GLOBAL VIEW";
        lines.add(line);

        float height = normalFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000;
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
        if (contentStream != null) {
            contentStream.endText();
            contentStream.close();
        }
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
