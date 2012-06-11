/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import be.ugent.maf.cellmissy.gui.experiment.SetupPlatePanelGui;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author Paola Masuzzo
 */
public class SetupReport extends JFrame {

    private SetupPlatePanel setupPlatePanel;
    private JTable table;

    public SetupReport(SetupPlatePanel setupPlatePanel) {
        this.setupPlatePanel = setupPlatePanel;
        createPdf();
    }

    private void createPdf() {

        try {
            getContentPane().setLayout(new BorderLayout());

            table = new JTable();

            JPanel tPanel = new JPanel(new BorderLayout());
            tPanel.add(table.getTableHeader(), BorderLayout.NORTH);
            tPanel.add(setupPlatePanel, BorderLayout.CENTER);

            getContentPane().add(tPanel, BorderLayout.CENTER);

            Document document = new Document();
            PdfWriter writer;
            writer = PdfWriter.getInstance(document, new FileOutputStream("Report.pdf"));
            document.setPageSize(new Rectangle(612, 792));

            document.open();
            PdfContentByte cb = writer.getDirectContent();

            PdfTemplate tp = cb.createTemplate(500, 500);
            Graphics2D g2;

            g2 = tp.createGraphics(500, 500);

            // g2 = tp.createGraphics(500, 500);
            table.print(g2);
            g2.dispose();
            cb.addTemplate(tp, 30, 300);

            addMetadata(document);

            // step 5: we close the document
            document.close();

        } catch (FileNotFoundException | DocumentException e) {
        }

    }


    private void addMetadata(Document document) {
        document.addTitle("Report");
    }
}
