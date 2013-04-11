/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CorrectedAreaPanel.java
 *
 * Created on Nov 19, 2012, 4:44:09 PM
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Paola Masuzzo
 */
public class CorrectedAreaPanel extends javax.swing.JPanel {

    public JPanel getReplicatesAreaChartParentPanel() {
        return replicatesAreaChartParentPanel;
    }

    public JTextField getCutOffTextField() {
        return cutOffTextField;
    }

    public JCheckBox getCutOffCheckBox() {
        return cutOffCheckBox;
    }

    public JButton getChooseTimeFramesButton() {
        return chooseTimeFramesButton;
    }

    public JTextArea getExcludedReplicatesTextArea() {
        return excludedReplicatesTextArea;
    }

    public JButton getSelectReplicatesButton() {
        return selectReplicatesButton;
    }

    /**
     * Creates new form CorrectedAreaPanel
     */
    public CorrectedAreaPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        leftPanel = new javax.swing.JPanel();
        cutOffInfoLabel = new javax.swing.JLabel();
        cutOffTextField = new javax.swing.JTextField();
        cutOffCheckBox = new javax.swing.JCheckBox();
        chooseTimeFramesButton = new javax.swing.JButton();
        excludedReplicatesLabel = new javax.swing.JLabel();
        selectReplicatesButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        excludedReplicatesTextArea = new javax.swing.JTextArea();
        replicatesAreaChartParentPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        leftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        leftPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        cutOffInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cutOffInfoLabel.setText("Current cut off:");
        cutOffInfoLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        cutOffInfoLabel.setPreferredSize(new java.awt.Dimension(20, 20));

        cutOffTextField.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        cutOffTextField.setFocusable(false);
        cutOffTextField.setMinimumSize(new java.awt.Dimension(20, 20));
        cutOffTextField.setPreferredSize(new java.awt.Dimension(20, 20));

        cutOffCheckBox.setText("Show cut off");
        cutOffCheckBox.setToolTipText("Show the current cut off on plot");
        cutOffCheckBox.setOpaque(false);

        chooseTimeFramesButton.setText("Choose Time Frames...");
        chooseTimeFramesButton.setToolTipText("Choose first and last time frame to analyze");

        excludedReplicatesLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        excludedReplicatesLabel.setText("Excluded Replicates:");
        excludedReplicatesLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        excludedReplicatesLabel.setPreferredSize(new java.awt.Dimension(20, 20));

        selectReplicatesButton.setText("Select replicates...");
        selectReplicatesButton.setToolTipText("Compute Distance Matrix and exclude replicates from dataset");

        excludedReplicatesTextArea.setEditable(false);
        excludedReplicatesTextArea.setColumns(20);
        excludedReplicatesTextArea.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        excludedReplicatesTextArea.setRows(5);
        excludedReplicatesTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(excludedReplicatesTextArea);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(selectReplicatesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(leftPanelLayout.createSequentialGroup()
                            .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cutOffInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cutOffCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addComponent(cutOffTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(excludedReplicatesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chooseTimeFramesButton))
                .addContainerGap())
        );

        leftPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chooseTimeFramesButton, selectReplicatesButton});

        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                .addComponent(excludedReplicatesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectReplicatesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cutOffInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cutOffTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cutOffCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chooseTimeFramesButton)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.weighty = 1.0;
        add(leftPanel, gridBagConstraints);

        replicatesAreaChartParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        replicatesAreaChartParentPanel.setOpaque(false);
        replicatesAreaChartParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        replicatesAreaChartParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.weighty = 1.0;
        add(replicatesAreaChartParentPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton chooseTimeFramesButton;
    private javax.swing.JCheckBox cutOffCheckBox;
    private javax.swing.JLabel cutOffInfoLabel;
    private javax.swing.JTextField cutOffTextField;
    private javax.swing.JLabel excludedReplicatesLabel;
    private javax.swing.JTextArea excludedReplicatesTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel replicatesAreaChartParentPanel;
    private javax.swing.JButton selectReplicatesButton;
    // End of variables declaration//GEN-END:variables
}
