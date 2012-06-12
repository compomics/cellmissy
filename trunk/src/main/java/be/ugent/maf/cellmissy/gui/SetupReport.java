/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import com.compomics.util.Export;
import com.compomics.util.enumeration.ImageType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;
import org.apache.batik.transcoder.TranscoderException;

/**
 *
 * @author Paola Masuzzo
 */
public class SetupReport {

    private SetupPlatePanel setupPlatePanel;
    private File file;

    public SetupReport(SetupPlatePanel setupPlatePanel) {
        this.setupPlatePanel = setupPlatePanel;     
        printPdf();
    }

    public File getFile() {
        return file;
    }

    private void printPdf() {

        Rectangle rect = new Rectangle(750, 750);
        file = new File("Report.pdf");
        try {
            Export.exportComponent(setupPlatePanel, rect, file, ImageType.PDF);
        } catch (IOException | TranscoderException ex) {
            ex.printStackTrace();
        }

    }

}
