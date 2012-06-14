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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
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
    }

    /**
     * getters and setters
     * @return 
     */
    public File getFile() {
        return file;
    }

    /**
     * print to PDF (Export class from Compomics Utilities)
     */
    public void exportPanelToPdf() {

        file = new File("Experiment " + experiment.getExperimentNumber() + " - Project " + experiment.getProject().getProjectNumber() + ".pdf");
        try {

            Export.exportComponent(reportPanel, reportPanel.getBounds(), file, ImageType.PDF);
        } catch (IOException | TranscoderException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * infoPanel is shown at the beginning of the PDF report, with experiment number, project, user and date
     * @return 
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());

        //font fot info label
        Font font = new Font("Arial", Font.BOLD, 20);
        //first info label
        String info = "Experiment " + experiment.getExperimentNumber() + " - Project " + experiment.getProject().getProjectNumber();
        JLabel infoField = new JLabel(info);
        infoField.setFont(font);
        //second info label
        String info2 = "User: " + experiment.getUser() + " - Date: " + experiment.getExperimentDate();
        JLabel info2Field = new JLabel(info2);
        info2Field.setFont(font);

        infoPanel.add(infoField, BorderLayout.NORTH);
        infoPanel.add(info2Field, BorderLayout.CENTER);
        return infoPanel;
    }

    /**
     * reportPanel contains a JTable listing all the conditions and their setup details
     * @return 
     */
    private JPanel createTablePanel() {

        //create a new JPanel
        JPanel reportPanel = new JPanel(new BorderLayout());
        //creta a JTable
        //column names
        Object columnNames[] = {"Condition", "Cell Line", "Matrix Dimension", "Assay", "ECM", "Treatments"};

        //do not work with collection, create a plateCondition List
        List<PlateCondition> plateConditionList = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionCollection()) {
            plateConditionList.add(plateCondition);
        }

        //row data
        Object[][] data = new Object[plateConditionList.size()][];

        for (int i = 0; i < data.length; i++) {
            PlateCondition plateCondition = plateConditionList.get(i);
            data[i] = new Object[]{plateCondition.getName(), plateCondition.getCellLine().getCellLineType(), plateCondition.getMatrixDimension().getMatrixDimension(), plateCondition.getAssay().getAssayType(), plateCondition.getEcm(), plateCondition.getTreatmentCollection()};
        }

        //create new table with the defined row data and column names 
        JTable reportTable = new JTable(data, columnNames);
        //JTable is Not used in a Jscrollable pane, then its header needs to be explicitly shown
        JTableHeader tableHeader = reportTable.getTableHeader();

        reportPanel.add(tableHeader, BorderLayout.NORTH);
        reportPanel.add(reportTable, BorderLayout.CENTER);

        //adjust table column width
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(5);
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(8);
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(15);
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(20);

        return reportPanel;

    }

    private JPanel createViewPanel() {

        JPanel viewPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        //add conditions list
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        viewPanel.add(conditionsJList, gridBagConstraints);
        //add plate view
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        viewPanel.add(setupPlatePanel, gridBagConstraints);
        return viewPanel;
    }

    /**
     * create reportPanel that must be printed to PDF. This panel contains all the other panels
     */
    public JPanel createReportPanel() {
        //new panel and new Layout
        reportPanel = new JPanel(new GridBagLayout());
        reportPanel.setPreferredSize(new Dimension(100, 100));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        //add info panel (exp number, project, user and date)
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        reportPanel.add(createInfoPanel(), gridBagConstraints);

        //add view panel (with condition list and plate view)
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        reportPanel.add(createViewPanel(), gridBagConstraints);

        //add report panel (report table with conditions' details)
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        reportPanel.add(createTablePanel(), gridBagConstraints);
        
        return reportPanel;
    }
}
