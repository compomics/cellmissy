/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import com.compomics.software.CompomicsWrapper;
import java.io.File;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;

/**
 * CellMissy Starter
 *
 * @author Paola Masuzzo
 */
public class CellMissyStarter {

    private static final Logger LOG = Logger.getLogger(CellMissyStarter.class);

    /**
     * Make a new CompomicsWrapper and try to launch CellMissy
     */
    public static void main(String[] args) {
        try {
            File jarFile = new File(CellMissyStarter.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String mainClass = "be.ugent.maf.cellmissy.gui.CellMissyFrame";
            CompomicsWrapper compomicsWrapper = new CompomicsWrapper();
            compomicsWrapper.launchTool("CellMissy", jarFile, null, mainClass);
        } catch (URISyntaxException ex) {
            LOG.error(ex);
        }
    }
}
