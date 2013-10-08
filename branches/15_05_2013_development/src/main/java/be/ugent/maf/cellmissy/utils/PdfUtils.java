/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;

/**
 * A utilities class for PDF files design - creation.
 *
 * @author Paola Masuzzo
 */
public class PdfUtils {

    private static final Logger LOG = Logger.getLogger(PdfUtils.class);

    /**
     * Get an image to add to the document from a JFreeChart
     *
     * @param pdfWriter
     * @param chart to be added
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    public static Image getImageFromJFreeChart(PdfWriter pdfWriter, JFreeChart chart, int imageWidth, int imageHeight) {
        Image imageFromChart = null;
        PdfContentByte contentByte = pdfWriter.getDirectContent();
        PdfTemplate template = contentByte.createTemplate(imageWidth, imageHeight);
        Graphics2D graphics = template.createGraphics(imageWidth, imageHeight);
        Rectangle2D rect = new Rectangle(0, 0, imageWidth, imageHeight);
        chart.draw(graphics, rect);
        graphics.dispose();
        try {
            imageFromChart = Image.getInstance(template);
        } catch (BadElementException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return imageFromChart;
    }

    /**
     * Get an image to add to the document from a JPanel
     *
     * @param pdfWriter
     * @param panel
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    public static Image getImageFromJPanel(PdfWriter pdfWriter, JPanel panel, int imageWidth, int imageHeight) {
        Image image = null;
        PdfContentByte contentByte = pdfWriter.getDirectContent();
        //@todo: check here for this null pointer exception
        PdfTemplate template = contentByte.createTemplate(imageWidth, imageHeight);
        Graphics2D graphics = template.createGraphics(imageWidth, imageHeight);
        panel.printAll(graphics);
        graphics.dispose();
        // wrap the pdfTemplate inside an image ensures better quality (pixels) 
        try {
            image = Image.getInstance(template);
        } catch (BadElementException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return image;
    }

    /**
     * Add empty lines between one section and another one
     *
     * @param document: the document to which the lines need to be added
     * @param numberOfLines: how many lines need to be added
     */
    public static void addEmptyLines(Document document, int numberOfLines) {
        for (int i = 0; i < numberOfLines; i++) {
            try {
                document.add(new Paragraph(" "));
            } catch (DocumentException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Add text to document
     *
     * @param document: the document to which the text needs to be added
     * @param text: this is a List of String, the text to add, each String is
     * added underneath each other.
     * @param isIndented: a boolean to check if the text needs to be indented or
     * not; if yes, indentation is set to 10
     * @param alignment: an integer for the alignment
     * @param font: the font to use to render the text
     */
    public static void addText(Document document, List<String> text, boolean isIndented, int alignment, Font font) {
        for (String string : text) {
            try {
                Chunk chunk = new Chunk(string, font);
                Paragraph paragraph = new Paragraph(chunk);
                paragraph.setAlignment(alignment);
                paragraph.setSpacingAfter(1.8f);
                if (isIndented) {
                    paragraph.setIndentationLeft(10);
                }
                document.add(paragraph);
            } catch (DocumentException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Add a cell to a table, with customised font to display
     *
     * @param table
     * @param cellText
     * @param font
     */
    public static void addCustomizedCell(PdfPTable table, String cellText, Font font) {
        Paragraph paragraph = new Paragraph(cellText, font);
        table.addCell(paragraph);
    }

    /**
     * Set up the look of a PdfPTable.
     *
     *
     * @param dataTable
     */
    public static void setUpPdfPTable(PdfPTable dataTable) {
        dataTable.setWidthPercentage(100f);
        dataTable.getDefaultCell().setPadding(3);
        dataTable.getDefaultCell().setUseAscender(true);
        dataTable.getDefaultCell().setUseDescender(true);
        dataTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.getDefaultCell().setColspan(1);
    }

    /**
     * Add a simple line with some text as Title
     *
     * @param document
     * @param title to add
     * @param titleFont
     */
    public static void addTitle(Document document, String title, Font titleFont) {
        try {
            //Paragraph paragraph = new Paragraph(title, titleFont);
            Chunk chunk = new Chunk(title, titleFont);
            Paragraph paragraph = new Paragraph(chunk);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(1.5f);
            document.add(paragraph);
        } catch (DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Add a cell with coloured border to a table
     *
     *
     * @param table
     * @param color
     */
    public static void addColoredCell(PdfPTable table, Color color) {
        PdfPCell coloredCell = new PdfPCell();
        coloredCell.setBorderColor(color);
        coloredCell.setBorderWidth(1.5f);
        table.addCell(coloredCell);
    }
}
