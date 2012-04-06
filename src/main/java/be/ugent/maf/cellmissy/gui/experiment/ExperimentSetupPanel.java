/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExperimentSetupPanel.java
 *
 * Created on Mar 29, 2012, 9:01:25 AM
 */
package be.ugent.maf.cellmissy.gui.experiment;

import javax.swing.JPanel;

/**
 *
 * @author Paola
 */
public class ExperimentSetupPanel extends javax.swing.JPanel {

    /** Creates new form ExperimentSetupPanel */
    public ExperimentSetupPanel() {
        initComponents();
    }

    public JPanel getExperimentInfoParentPanel() {
        return experimentInfoParentPanel;
    }

    public JPanel getPlateSetupParentPanel() {
        return plateSetupParentPanel;
    }

    public JPanel getConditionsSetupParentPanel() {
        return conditionsSetupParentPanel;
    }

    public JPanel getConditionsParentPanel() {
        return conditionsParentPanel;
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

        experimentInfoParentPanel = new javax.swing.JPanel();
        plateSetupParentPanel = new javax.swing.JPanel();
        conditionsParentPanel = new javax.swing.JPanel();
        conditionsSetupParentPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(20, 20));
        setLayout(new java.awt.GridBagLayout());

        experimentInfoParentPanel.setMinimumSize(new java.awt.Dimension(40, 40));
        experimentInfoParentPanel.setPreferredSize(new java.awt.Dimension(150, 40));
        experimentInfoParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.3;
        add(experimentInfoParentPanel, gridBagConstraints);

        plateSetupParentPanel.setMinimumSize(new java.awt.Dimension(30, 30));
        plateSetupParentPanel.setPreferredSize(new java.awt.Dimension(150, 150));
        plateSetupParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.6;
        add(plateSetupParentPanel, gridBagConstraints);

        conditionsParentPanel.setMinimumSize(new java.awt.Dimension(40, 30));
        conditionsParentPanel.setPreferredSize(new java.awt.Dimension(40, 30));
        conditionsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        add(conditionsParentPanel, gridBagConstraints);

        conditionsSetupParentPanel.setMinimumSize(new java.awt.Dimension(30, 20));
        conditionsSetupParentPanel.setPreferredSize(new java.awt.Dimension(30, 10));
        conditionsSetupParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        add(conditionsSetupParentPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel conditionsParentPanel;
    private javax.swing.JPanel conditionsSetupParentPanel;
    private javax.swing.JPanel experimentInfoParentPanel;
    private javax.swing.JPanel plateSetupParentPanel;
    // End of variables declaration//GEN-END:variables
}