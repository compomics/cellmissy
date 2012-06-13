/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import com.compomics.util.Export;
import com.compomics.util.enumeration.ImageType;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import org.apache.batik.transcoder.TranscoderException;

/**
 *
 * @author Paola Masuzzo
 */
public class SetupReport {

    private JPanel setupPlatePanel;
    private JList conditionsJList;
    private Experiment experiment;
    private File file;
    private JPanel reportPanel;

    /**
     * constructor
     * @param setupPlatePanel
     * @param experiment 
     */
    public SetupReport(JPanel setupPlatePanel, JList conditionsJList, Experiment experiment) {
        this.setupPlatePanel = setupPlatePanel;
        this.conditionsJList = conditionsJList;
        this.experiment = experiment;
        createReportPanel();
        exportPanelToPdf();
    }

    /**
     * getters and setters
     * @return 
     */
    public File getFile() {
        return file;
    }

    /**
     * create reportPanel that must be printed to PDF. This panel contains all the other panels
     */
    private void createReportPanel() {

        //new panel and new Layout
        reportPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
                
        //add info panel
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        reportPanel.add(createInfoPanel(), gridBagConstraints);
        //add setupPlatePanel
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        reportPanel.add(setupPlatePanel, gridBagConstraints);
        //add conditionsJList
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        reportPanel.add(conditionsJList, gridBagConstraints);
        //add report panel (report table)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        reportPanel.add(createTablePanel(), gridBagConstraints);

        //to be removed **************************
        JFrame frame = new JFrame();
        frame.add(reportPanel);
        frame.setVisible(true);

    }

    /**
     * print to PDF (Export class from Compomics Utilities)
     */
    private void exportPanelToPdf() {

        file = new File("Report for Experiment " + experiment.getExperimentNumber() + ", Project " + experiment.getProject().getProjectNumber() + ".pdf");
        try {
            Export.exportComponent(reportPanel, reportPanel.getBounds(), file, ImageType.PDF);
        } catch (IOException | TranscoderException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * infoPanel is a panel shown at the beginning of the PDF report, with experiment info, project, purpose, user and date
     * @return 
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        //add info text
        String info = "Report for experiment " + experiment.getExperimentNumber() + " of project " + experiment.getProject().getProjectNumber();
        JTextField infoField = new JTextField(info);

        infoPanel.add(infoField, BorderLayout.NORTH);
        return infoPanel;
    }

    /**
     * reportPanel is a JPanel containing a JTable listing all the conditions and their setup details
     * @return 
     */
    private JPanel createTablePanel() {

        //create a new JPanel
        JPanel reportPanel = new JPanel(new BorderLayout());
        //creta a JTable
        //column names
        Object columnNames[] = {"Condition", "Cell Line", "Matrix Dimension", "Assay", "ECM"};

        //do not work with collection, create a plateCondition List
        List<PlateCondition> plateConditionList = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionCollection()) {
            plateConditionList.add(plateCondition);
        }

        //row data
        Object[][] data = new Object[plateConditionList.size()][];

        for (int i = 0; i < data.length; i++) {
            PlateCondition plateCondition = plateConditionList.get(i);
            data[i] = new Object[]{plateCondition.getName(), plateCondition.getCellLine().getCellLineType(), plateCondition.getMatrixDimension().getMatrixDimension(), plateCondition.getAssay().getAssayType(), plateCondition.getEcm()};
        }

        //create new table with the defined row data and column names 
        JTable reportTable = new JTable(data, columnNames);
        //JTable is Not used in a Jscrollable pane, then its header needs to be explicitly shown
        JTableHeader tableHeader = reportTable.getTableHeader();
        //adjust table column width
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(40);
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(10);
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(50);

        reportPanel.add(tableHeader, BorderLayout.NORTH);
        reportPanel.add(reportTable, BorderLayout.CENTER);

        return reportPanel;

    }
}
