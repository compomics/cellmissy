/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Paola Masuzzo
 */
public class SetupReport {

    private SetupPlatePanel setupPlatePanel;
    private Experiment experiment;
    private JPanel reportPanel;
    private List<PlateCondition> plateConditionList;

    /**
     * constructor
     * @param setupPlatePanel
     * @param experiment 
     */
    public SetupReport(SetupPlatePanel setupPlatePanel, Experiment experiment) {
        this.setupPlatePanel = setupPlatePanel;
        this.experiment = experiment;
    }

    /**
     * create reportPanel that must be printed to PDF. This panel contains all the other panels
     */
    public JPanel createReportPanel() {

        //new panel and new Layout
        reportPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        gridBagConstraints.insets = new Insets(5, 10, 5, 10);
        //add info panel (exp number, project, user and date)
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        reportPanel.add(createInfoPanel(), gridBagConstraints);

        //add view panel (with condition list and plate view)
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        reportPanel.add(createViewPanel(), gridBagConstraints);

        //add report panel (report table with conditions' details)
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        reportPanel.add(createTablePanel(), gridBagConstraints);

        return reportPanel;
    }

    /**
     * infoPanel is shown at the beginning of the PDF report, with experiment number, project, user and date
     * @return 
     */
    private JPanel createInfoPanel() {
        BorderLayout borderLayout = new BorderLayout(1, 1);
        JPanel infoPanel = new JPanel(borderLayout);
        //font for info label
        Font font = new Font("Arial", Font.PLAIN, 14);

        //first info label
        String info = "Experiment " + experiment.getExperimentNumber() + " - Project " + experiment.getProject().getProjectNumber();
        JLabel infoField = new JLabel(info);
        infoField.setFont(font);
        infoPanel.add(infoField, BorderLayout.NORTH);

        //second info label
        info = "User: " + experiment.getUser().toString().toUpperCase() + " - Date: " + experiment.getExperimentDate();
        infoField = new JLabel(info);
        infoField.setFont(font);
        infoPanel.add(infoField, BorderLayout.CENTER);

        //third info label
        info = "Instrument: " + experiment.getInstrument() + " - Magnification : " + experiment.getMagnification();
        infoField = new JLabel(info);
        infoField.setFont(font);
        infoPanel.add(infoField, BorderLayout.SOUTH);

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
        Object columnNames[] = {"Condition", "Cell Line (Type, Seeding Density, Growth Medium)", "Matrix Dimension", "Assay", "ECM (density)", "Treatments (Type, Concentration)"};

        //do not work with collection, create a plateCondition List
        plateConditionList = new ArrayList<>();
        plateConditionList.addAll(experiment.getPlateConditionCollection());

        //row data
        Object[][] data = new Object[plateConditionList.size()][];
        for (int i = 0; i < data.length; i++) {
            PlateCondition plateCondition = plateConditionList.get(i);
            data[i] = new Object[]{plateCondition.getName(), plateCondition.getCellLine(), plateCondition.getMatrixDimension().getMatrixDimension(), plateCondition.getAssay().getAssayType(), plateCondition.getEcm(), plateCondition.getTreatmentCollection()};
        }

        //create new table with the defined row data and column names 
        JTable reportTable = new JTable(data, columnNames);

        //disable auto resizing for the JTable
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        reportTable.getColumnModel().setColumnSelectionAllowed(false);

        //adjust table columns width (still need to set proper size) ***********
        for (int i = 0; i < reportTable.getColumnCount(); i++) {
            reportTable.getColumnModel().getColumn(i).setMinWidth(2);
        }
        reportTable.getColumnModel().getColumn(0).setMaxWidth(125);
        reportTable.getColumnModel().getColumn(1).setMaxWidth(350);
        reportTable.getColumnModel().getColumn(2).setMaxWidth(110);
        reportTable.getColumnModel().getColumn(3).setMaxWidth(110);
        reportTable.getColumnModel().getColumn(4).setMaxWidth(250);
        reportTable.getColumnModel().getColumn(5).setMaxWidth(250);

        reportTable.getColumnModel().getColumn(0).setPreferredWidth(125);
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        reportTable.getColumnModel().getColumn(5).setPreferredWidth(250);


        //disable  JTable's tooltips
        ToolTipManager.sharedInstance().unregisterComponent(reportTable);
        ToolTipManager.sharedInstance().unregisterComponent(reportTable.getTableHeader());

        //JTable is Not used in a Jscrollable pane, then its header needs to be explicitly shown
        reportPanel.add(reportTable.getTableHeader(), BorderLayout.NORTH);
        reportTable.getTableHeader().setResizingAllowed(false);
        reportPanel.add(reportTable, BorderLayout.CENTER);

        //set Cell Renderer for first Column (Conditions List)
        reportTable.getColumnModel().getColumn(0).setCellRenderer(new TableRenderer());

        return reportPanel;
    }

    /**
     * view Panel contains Condition List and Setup Plate Panel (Wells view)
     * @return 
     */
    private JPanel createViewPanel() {

        JPanel viewPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        //add plate view
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        viewPanel.add(setupPlatePanel, gridBagConstraints);

        JTextArea jTextArea = new JTextArea();
        String info = "This file is saved into " + experiment.getSetupFolder().getName() + " folder.";
        jTextArea.setText(info);
        jTextArea.setEditable(false);
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        viewPanel.add(jTextArea, gridBagConstraints);

        return viewPanel;
    }

    /**
     * table cell render
     */
    private class TableRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            int length = ((String) value).length();
            String substring = ((String) value).substring(length - 1);
            int conditionIndex = Integer.parseInt(substring);
            setIcon(new rectIcon(GuiUtils.getAvailableColors()[conditionIndex]));
            return this;
        }
    }

    /**
     * rectangular icon for the Condition list
     */
    private class rectIcon implements Icon {

        private final Integer rectHeight = 10;
        private final Integer rectWidth = 25;
        private Color color;

        /**
         * constructor
         * @param color 
         */
        public rectIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            setupPlatePanel.setGraphics(g2d);
            g2d.setColor(color);
            g2d.fillRect(x, y, rectWidth, rectHeight);
        }

        @Override
        public int getIconWidth() {
            return rectWidth;
        }

        @Override
        public int getIconHeight() {
            return rectHeight;
        }
    }
}
