/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SetupPanel.java
 *
 * Created on May 11, 2012, 3:26:08 PM
 */
package be.ugent.maf.cellmissy.gui.experiment.setup;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Paola Masuzzo
 */
public class SetupPanel extends javax.swing.JPanel {

    /** Creates new form SetupPanel */
    public SetupPanel() {
        initComponents();
    }
    
    public JPanel getSetupPlateParentPanel() {
        return setupPlateParentPanel;
    }

    public JPanel getSetupConditionsParentPanel() {
        return setupConditionsParentPanel;
    }

    public JPanel getConditionsParentPanel() {
        return conditionsParentPanel;
    }

    public JButton getClearAllButton() {
        return clearAllButton;
    }

    public JButton getClearLastButton() {
        return clearLastButton;
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

        conditionsParentPanel = new javax.swing.JPanel();
        setupConditionsParentPanel = new javax.swing.JPanel();
        platePanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        clearLastButton = new javax.swing.JButton();
        clearAllButton = new javax.swing.JButton();
        setupPlateParentPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        conditionsParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Conditions List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        conditionsParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        conditionsParentPanel.setName(""); // NOI18N
        conditionsParentPanel.setOpaque(false);
        conditionsParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        conditionsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.2;
        add(conditionsParentPanel, gridBagConstraints);

        setupConditionsParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Conditions Setup", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        setupConditionsParentPanel.setMinimumSize(new java.awt.Dimension(10, 10));
        setupConditionsParentPanel.setOpaque(false);
        setupConditionsParentPanel.setPreferredSize(new java.awt.Dimension(10, 10));
        setupConditionsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        add(setupConditionsParentPanel, gridBagConstraints);

        platePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Plate", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        platePanel.setMinimumSize(new java.awt.Dimension(20, 20));
        platePanel.setName(""); // NOI18N
        platePanel.setOpaque(false);
        platePanel.setPreferredSize(new java.awt.Dimension(20, 20));
        platePanel.setLayout(new java.awt.GridBagLayout());

        buttonsPanel.setMaximumSize(new java.awt.Dimension(20, 20));
        buttonsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        clearLastButton.setText("Clear Last Selection");

        clearAllButton.setText("Clear All");

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clearAllButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(clearLastButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(364, Short.MAX_VALUE)
                .addComponent(clearLastButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearAllButton)
                .addGap(22, 22, 22))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        platePanel.add(buttonsPanel, gridBagConstraints);

        setupPlateParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        setupPlateParentPanel.setOpaque(false);
        setupPlateParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        setupPlateParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1.0;
        platePanel.add(setupPlateParentPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.8;
        add(platePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton clearAllButton;
    private javax.swing.JButton clearLastButton;
    private javax.swing.JPanel conditionsParentPanel;
    private javax.swing.JPanel platePanel;
    private javax.swing.JPanel setupConditionsParentPanel;
    private javax.swing.JPanel setupPlateParentPanel;
    // End of variables declaration//GEN-END:variables
}
