/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic;

import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.DRResultsController;
import be.ugent.maf.cellmissy.utils.PdfUtils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author CompOmics Gwen
 */
@Controller("genericDRResultsController")
public class GenericDRResultsController extends DRResultsController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericDRResultsController.class);

    //model: all in super class
    //view: in super class
    //parent controller: to be created
    @Autowired
    private  ;

    @Override
    protected void initDRResultsPanel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void setStatistics(DoseResponseAnalysisGroup analysisGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void initDRResultsData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void plotCharts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    File createAnalysisReport(File directory, String reportName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void tryToCreateFile(File pdfFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void createPdfFile(FileOutputStream outputStream) {
        document = null;
        writer = null;
        try {
            // get new instances
            // the Document is the base layout element
            document = new Document();
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

    @Override
    protected void addOverview() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addInitialFittingInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addNormalizedFittingInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addTable(PdfPTable dataTable) {
        try {
            document.add(dataTable);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected PdfPTable createAnalysisGroupInfoTable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected PdfPTable createFittingInfoTable(boolean normalized) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addImageFromChart(JFreeChart chart, int imageWidth, int imageHeight) {
        Image imageFromChart = PdfUtils.getImageFromJFreeChart(writer, chart, imageWidth, imageHeight);
        // put image in the center
        imageFromChart.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(imageFromChart);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

}
