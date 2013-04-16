/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LinearRegressionPanel.java
 *
 * Created on Dec 4, 2012, 2:10:47 PM
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 *
 * @author Paola Masuzzo
 */
public class LinearRegressionPanel extends javax.swing.JPanel {

    /**
     * Creates new form LinearRegressionPanel
     */
    public LinearRegressionPanel() {
        initComponents();
    }

    public JPanel getChartParentPanel() {
        return chartParentPanel;
    }

    public JTable getSlopesTable() {
        return slopesTable;
    }

    public JScrollPane getSlopesTableScrollPane() {
        return slopesTableScrollPane;
    }

    public JButton getStatisticsButton() {
        return statisticsButton;
    }

    public JList getGroupsList() {
        return groupsList;
    }

    public JButton getAddGroupButton() {
        return addGroupButton;
    }

    public JButton getRemoveGroupButton() {
        return removeGroupButton;
    }

    public JTextField getGroupNameTextField() {
        return groupNameTextField;
    }

    public JButton getCreateReportButton() {
        return createReportButton;
    }

    public JTextPane getInfoTextPane() {
        return infoTextPane;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        linearRegressionTablePanel = new javax.swing.JPanel();
        slopesTableScrollPane = new javax.swing.JScrollPane();
        slopesTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        chartParentPanel = new javax.swing.JPanel();
        statisticsPanel = new javax.swing.JPanel();
        statisticsButton = new javax.swing.JButton();
        createReportButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        groupsList = new javax.swing.JList();
        addGroupButton = new javax.swing.JButton();
        removeGroupButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        groupNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        linearRegressionTablePanel.setMinimumSize(new java.awt.Dimension(20, 20));
        linearRegressionTablePanel.setOpaque(false);
        linearRegressionTablePanel.setPreferredSize(new java.awt.Dimension(20, 20));

        slopesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        slopesTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        slopesTableScrollPane.setViewportView(slopesTable);

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel1.setText("This table contains slopes together with goodness of fit coefficients for each replicate and each condition.");

        javax.swing.GroupLayout linearRegressionTablePanelLayout = new javax.swing.GroupLayout(linearRegressionTablePanel);
        linearRegressionTablePanel.setLayout(linearRegressionTablePanelLayout);
        linearRegressionTablePanelLayout.setHorizontalGroup(
            linearRegressionTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearRegressionTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(linearRegressionTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slopesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1138, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        linearRegressionTablePanelLayout.setVerticalGroup(
            linearRegressionTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearRegressionTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slopesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        add(linearRegressionTablePanel, gridBagConstraints);

        chartParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        chartParentPanel.setOpaque(false);
        chartParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        chartParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(chartParentPanel, gridBagConstraints);

        statisticsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));
        statisticsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        statisticsPanel.setOpaque(false);
        statisticsPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        statisticsButton.setText("Perform Statistical Analysis...");
        statisticsButton.setToolTipText("Run a Mann–Whitney U test for current group of conditions");

        createReportButton.setText("Create & Save PDF Report");
        createReportButton.setToolTipText("Create a PDF report for this analysis and save it");

        jScrollPane3.setBorder(null);
        jScrollPane3.setOpaque(false);

        groupsList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        groupsList.setMinimumSize(new java.awt.Dimension(20, 20));
        groupsList.setPreferredSize(new java.awt.Dimension(20, 20));
        groupsList.setSelectedIndex(0);
        jScrollPane3.setViewportView(groupsList);

        addGroupButton.setText("Add group");

        removeGroupButton.setText("Remove group");

        jLabel2.setText("Choose a name for the group");

        jLabel3.setText("Current analysis groups");

        jScrollPane1.setBorder(null);

        infoTextPane.setEditable(false);
        infoTextPane.setBorder(null);
        infoTextPane.setText("Choose conditions from the linerar regression table; each group of conditions is associated to a certain analysis group. You can perform or edit statistical analysis on each of the current defined groups.");
        infoTextPane.setFocusable(false);
        jScrollPane1.setViewportView(infoTextPane);

        javax.swing.GroupLayout statisticsPanelLayout = new javax.swing.GroupLayout(statisticsPanel);
        statisticsPanel.setLayout(statisticsPanelLayout);
        statisticsPanelLayout.setHorizontalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addGroupButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(groupNameTextField)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeGroupButton, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(statisticsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(createReportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        statisticsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {createReportButton, statisticsButton});

        statisticsPanelLayout.setVerticalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsPanelLayout.createSequentialGroup()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                        .addGap(0, 17, Short.MAX_VALUE)
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(statisticsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                                        .addComponent(statisticsButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(createReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(statisticsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groupNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(addGroupButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeGroupButton)))))
                .addContainerGap())
        );

        statisticsPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {createReportButton, statisticsButton});

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        add(statisticsPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addGroupButton;
    private javax.swing.JPanel chartParentPanel;
    private javax.swing.JButton createReportButton;
    private javax.swing.JTextField groupNameTextField;
    private javax.swing.JList groupsList;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel linearRegressionTablePanel;
    private javax.swing.JButton removeGroupButton;
    private javax.swing.JTable slopesTable;
    private javax.swing.JScrollPane slopesTableScrollPane;
    private javax.swing.JButton statisticsButton;
    private javax.swing.JPanel statisticsPanel;
    // End of variables declaration//GEN-END:variables
}
