/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StatisticsPanel.java
 *
 * Created on Dec 6, 2012, 3:48:29 PM
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

/**
 *
 * @author Paola Masuzzo
 */
public class StatisticsPanel extends javax.swing.JPanel {

    /** Creates new form StatisticsPanel */
    public StatisticsPanel() {
        initComponents();
    }

    public JScrollPane getpValuesScrollPane() {
        return pValuesScrollPane;
    }

    public JTable getpValuesTable() {
        return pValuesTable;
    }

    public JScrollPane getSummaryScrollPane() {
        return summaryScrollPane;
    }

    public JTable getSummaryTable() {
        return summaryTable;
    }

    public JButton getCorrectionButton() {
        return correctionButton;
    }

    public JTextPane getInfoTextPane() {
        return infoTextPane;
    }

    public JComboBox getSignificanceLevelComboBox() {
        return significanceLevelComboBox;
    }

    public JLabel getGroupNameLabel() {
        return groupNameLabel;
    }

    public JComboBox getCorrectionMethodsComboBox() {
        return correctionMethodsComboBox;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        infoPanel = new javax.swing.JPanel();
        infoScrollPane = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();
        groupNameLabel = new javax.swing.JLabel();
        summaryPanel = new javax.swing.JPanel();
        summaryScrollPane = new javax.swing.JScrollPane();
        summaryTable = new javax.swing.JTable();
        pValuesPanel = new javax.swing.JPanel();
        pValuesScrollPane = new javax.swing.JScrollPane();
        pValuesTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        correctionButton = new javax.swing.JButton();
        significanceLevelComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        correctionMethodsComboBox = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(600, 300));
        setLayout(new java.awt.GridBagLayout());

        infoPanel.setMinimumSize(new java.awt.Dimension(400, 300));
        infoPanel.setOpaque(false);
        infoPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        infoScrollPane.setBorder(null);
        infoScrollPane.setFocusable(false);
        infoScrollPane.setMinimumSize(new java.awt.Dimension(600, 100));
        infoScrollPane.setPreferredSize(new java.awt.Dimension(600, 100));

        infoTextPane.setBorder(null);
        infoTextPane.setText("This panel presents a Summary Statistics Table together with a p-values Table, where p values have been computed on the selected Analysis Group performing a Mann-Whitney U test (also called Wilcoxon rank-sum test). By default, no correction is applied for multiple comparisons; you can select to adjust the p-values choosing a correction method form the drop down list.\n");
        infoTextPane.setFocusable(false);
        infoTextPane.setMinimumSize(new java.awt.Dimension(600, 150));
        infoTextPane.setPreferredSize(new java.awt.Dimension(600, 150));
        infoScrollPane.setViewportView(infoTextPane);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("ANALYISIS OF GROUP: ");

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(groupNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(595, Short.MAX_VALUE))
            .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(infoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 935, Short.MAX_VALUE))
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoPanelLayout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(groupNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
            .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(infoPanelLayout.createSequentialGroup()
                    .addComponent(infoScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(45, Short.MAX_VALUE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(infoPanel, gridBagConstraints);

        summaryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Summary Statistics"));
        summaryPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        summaryScrollPane.setBorder(null);

        summaryTable.setModel(new javax.swing.table.DefaultTableModel(
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
        summaryScrollPane.setViewportView(summaryTable);

        javax.swing.GroupLayout summaryPanelLayout = new javax.swing.GroupLayout(summaryPanel);
        summaryPanel.setLayout(summaryPanelLayout);
        summaryPanelLayout.setHorizontalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(summaryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
        );
        summaryPanelLayout.setVerticalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(summaryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.4;
        add(summaryPanel, gridBagConstraints);

        pValuesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Mann - Whitney U Test"));
        pValuesPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        pValuesPanel.setOpaque(false);
        pValuesPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        pValuesScrollPane.setBorder(null);
        pValuesScrollPane.setPreferredSize(new java.awt.Dimension(20, 20));

        pValuesTable.setModel(new javax.swing.table.DefaultTableModel(
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
        pValuesScrollPane.setViewportView(pValuesTable);

        javax.swing.GroupLayout pValuesPanelLayout = new javax.swing.GroupLayout(pValuesPanel);
        pValuesPanel.setLayout(pValuesPanelLayout);
        pValuesPanelLayout.setHorizontalGroup(
            pValuesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pValuesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
        );
        pValuesPanelLayout.setVerticalGroup(
            pValuesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pValuesScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.4;
        add(pValuesPanel, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(200, 150));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Multiple Comparisons Correction");

        correctionButton.setText("Correct");

        significanceLevelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Significance Level");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(significanceLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(correctionMethodsComboBox, 0, 143, Short.MAX_VALUE))
                    .addComponent(correctionButton))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(significanceLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(correctionMethodsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(correctionButton)))
                .addContainerGap(273, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton correctionButton;
    private javax.swing.JComboBox correctionMethodsComboBox;
    private javax.swing.JLabel groupNameLabel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JScrollPane infoScrollPane;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pValuesPanel;
    private javax.swing.JScrollPane pValuesScrollPane;
    private javax.swing.JTable pValuesTable;
    private javax.swing.JComboBox significanceLevelComboBox;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JScrollPane summaryScrollPane;
    private javax.swing.JTable summaryTable;
    // End of variables declaration//GEN-END:variables
}
