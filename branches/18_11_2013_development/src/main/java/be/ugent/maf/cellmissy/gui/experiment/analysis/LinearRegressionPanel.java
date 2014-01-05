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

    public JTextField getCorrectedDataTextField() {
        return correctedDataTextField;
    }

    public JTextField getFirstTimeFrameTextField() {
        return firstTimeFrameTextField;
    }

    public JTextField getLastTimeFrameTextField() {
        return lastTimeFrameTextField;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        linearRegressionTablePanel = new javax.swing.JPanel();
        slopesTableScrollPane = new javax.swing.JScrollPane();
        slopesTable = new javax.swing.JTable();
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        firstTimeFrameTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        lastTimeFrameTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        correctedDataTextField = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(900, 700));
        setLayout(new java.awt.GridBagLayout());

        linearRegressionTablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Lirear Regression Table: slope + R²"));
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
        slopesTable.getTableHeader().setResizingAllowed(false);
        slopesTable.getTableHeader().setReorderingAllowed(false);
        slopesTableScrollPane.setViewportView(slopesTable);

        javax.swing.GroupLayout linearRegressionTablePanelLayout = new javax.swing.GroupLayout(linearRegressionTablePanel);
        linearRegressionTablePanel.setLayout(linearRegressionTablePanelLayout);
        linearRegressionTablePanelLayout.setHorizontalGroup(
            linearRegressionTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearRegressionTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(slopesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE)
                .addContainerGap())
        );
        linearRegressionTablePanelLayout.setVerticalGroup(
            linearRegressionTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linearRegressionTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(slopesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        add(linearRegressionTablePanel, gridBagConstraints);

        chartParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Median Velocities"));
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
        gridBagConstraints.weighty = 0.5;
        add(chartParentPanel, gridBagConstraints);

        statisticsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));
        statisticsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        statisticsPanel.setOpaque(false);
        statisticsPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        statisticsButton.setText("Perform Statistis...");
        statisticsButton.setToolTipText("Run a Mann–Whitney U test for current group of conditions");

        createReportButton.setText("Create & Save PDF Report");
        createReportButton.setToolTipText("Create a PDF report for this analysis and save it");

        jScrollPane3.setOpaque(false);

        groupsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        groupsList.setMinimumSize(new java.awt.Dimension(20, 20));
        groupsList.setPreferredSize(new java.awt.Dimension(100, 200));
        jScrollPane3.setViewportView(groupsList);

        addGroupButton.setText("Add Analysis Group");

        removeGroupButton.setText("Remove Analysis Group");

        jLabel2.setText("Type  a name for the group");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Current analysis groups (name, conditions)");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Time frames for analysis");

        jLabel5.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel5.setText("first");

        firstTimeFrameTextField.setEditable(false);
        firstTimeFrameTextField.setBorder(null);
        firstTimeFrameTextField.setFocusable(false);
        firstTimeFrameTextField.setOpaque(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel6.setText("last");

        lastTimeFrameTextField.setEditable(false);
        lastTimeFrameTextField.setBorder(null);
        lastTimeFrameTextField.setFocusable(false);
        lastTimeFrameTextField.setOpaque(false);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Corrected data?");

        correctedDataTextField.setEditable(false);
        correctedDataTextField.setBorder(null);
        correctedDataTextField.setFocusable(false);
        correctedDataTextField.setOpaque(false);

        javax.swing.GroupLayout statisticsPanelLayout = new javax.swing.GroupLayout(statisticsPanel);
        statisticsPanel.setLayout(statisticsPanelLayout);
        statisticsPanelLayout.setHorizontalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(statisticsPanelLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lastTimeFrameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(firstTimeFrameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(8, 8, 8)
                        .addComponent(correctedDataTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(groupNameTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addGroupButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeGroupButton, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(statisticsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(createReportButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(68, 68, 68))
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(112, 112, 112))))
        );

        statisticsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {createReportButton, statisticsButton});

        statisticsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {firstTimeFrameTextField, lastTimeFrameTextField});

        statisticsPanelLayout.setVerticalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(statisticsPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(statisticsButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(createReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                        .addGap(0, 59, Short.MAX_VALUE)
                        .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groupNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(addGroupButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(removeGroupButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(firstTimeFrameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lastTimeFrameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(correctedDataTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
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
        add(statisticsPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addGroupButton;
    private javax.swing.JPanel chartParentPanel;
    private javax.swing.JTextField correctedDataTextField;
    private javax.swing.JButton createReportButton;
    private javax.swing.JTextField firstTimeFrameTextField;
    private javax.swing.JTextField groupNameTextField;
    private javax.swing.JList groupsList;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField lastTimeFrameTextField;
    private javax.swing.JPanel linearRegressionTablePanel;
    private javax.swing.JButton removeGroupButton;
    private javax.swing.JTable slopesTable;
    private javax.swing.JScrollPane slopesTableScrollPane;
    private javax.swing.JButton statisticsButton;
    private javax.swing.JPanel statisticsPanel;
    // End of variables declaration//GEN-END:variables
}