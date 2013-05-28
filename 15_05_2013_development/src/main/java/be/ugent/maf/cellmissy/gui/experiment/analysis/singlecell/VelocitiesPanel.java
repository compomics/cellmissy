/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author paola
 */
public class VelocitiesPanel extends javax.swing.JPanel {

    public JPanel getDataTablePanel() {
        return dataTablePanel;
    }

    public JPanel getGraphicsParentPanel() {
        return graphicsParentPanel;
    }

    public JRadioButton getRawVelocitiesRadioButton() {
        return rawVelocitiesRadioButton;
    }

    public JRadioButton getMotileStepsRadioButton() {
        return motileStepsRadioButton;
    }

    public JLabel getTableInfoLabel() {
        return tableInfoLabel;
    }

    public JButton getFilterNonMotileStepsButton() {
        return filterNonMotileStepsButton;
    }

    public JTextField getMotileCriteriumTextField() {
        return motileCriteriumTextField;
    }

    /**
     * Creates new form VelocitiesPanel
     */
    public VelocitiesPanel() {
        initComponents();
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

        radioButtonsPanel = new javax.swing.JPanel();
        rawVelocitiesRadioButton = new javax.swing.JRadioButton();
        tableInfoLabel = new javax.swing.JLabel();
        motileStepsRadioButton = new javax.swing.JRadioButton();
        dataTablePanel = new javax.swing.JPanel();
        extraPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        motileCriteriumTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        filterNonMotileStepsButton = new javax.swing.JButton();
        graphicsParentPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        radioButtonsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        radioButtonsPanel.setOpaque(false);
        radioButtonsPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        rawVelocitiesRadioButton.setText("raw velocities");
        rawVelocitiesRadioButton.setOpaque(false);

        tableInfoLabel.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        tableInfoLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        tableInfoLabel.setPreferredSize(new java.awt.Dimension(20, 20));

        motileStepsRadioButton.setText("motile steps");
        motileStepsRadioButton.setOpaque(false);

        javax.swing.GroupLayout radioButtonsPanelLayout = new javax.swing.GroupLayout(radioButtonsPanel);
        radioButtonsPanel.setLayout(radioButtonsPanelLayout);
        radioButtonsPanelLayout.setHorizontalGroup(
            radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(radioButtonsPanelLayout.createSequentialGroup()
                        .addComponent(rawVelocitiesRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(motileStepsRadioButton)))
                .addContainerGap(468, Short.MAX_VALUE))
        );
        radioButtonsPanelLayout.setVerticalGroup(
            radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rawVelocitiesRadioButton)
                    .addComponent(motileStepsRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(tableInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(radioButtonsPanel, gridBagConstraints);

        dataTablePanel.setMinimumSize(new java.awt.Dimension(20, 20));
        dataTablePanel.setOpaque(false);
        dataTablePanel.setPreferredSize(new java.awt.Dimension(20, 20));
        dataTablePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.35;
        add(dataTablePanel, gridBagConstraints);

        extraPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        extraPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        extraPanel.setOpaque(false);
        extraPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel1.setText("Motile steps criterium:");

        jLabel2.setText("pixels/frame");

        filterNonMotileStepsButton.setText("Filter non motile steps");

        javax.swing.GroupLayout extraPanelLayout = new javax.swing.GroupLayout(extraPanel);
        extraPanel.setLayout(extraPanelLayout);
        extraPanelLayout.setHorizontalGroup(
            extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extraPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterNonMotileStepsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(extraPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(motileCriteriumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        extraPanelLayout.setVerticalGroup(
            extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extraPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(motileCriteriumTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterNonMotileStepsButton)
                .addContainerGap(172, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.35;
        add(extraPanel, gridBagConstraints);

        graphicsParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        graphicsParentPanel.setOpaque(false);
        graphicsParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        graphicsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.55;
        add(graphicsParentPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dataTablePanel;
    private javax.swing.JPanel extraPanel;
    private javax.swing.JButton filterNonMotileStepsButton;
    private javax.swing.JPanel graphicsParentPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField motileCriteriumTextField;
    private javax.swing.JRadioButton motileStepsRadioButton;
    private javax.swing.JPanel radioButtonsPanel;
    private javax.swing.JRadioButton rawVelocitiesRadioButton;
    private javax.swing.JLabel tableInfoLabel;
    // End of variables declaration//GEN-END:variables
}