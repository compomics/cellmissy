/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SetupExperimentPanel.java
 *
 * Created on Mar 29, 2012, 9:01:25 AM
 */
package be.ugent.maf.cellmissy.gui.experiment;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Paola
 */
public class SetupExperimentPanel extends javax.swing.JPanel {

    /** Creates new form SetupExperimentPanel */
    public SetupExperimentPanel() {
        initComponents();
    }

    public JPanel getTopPanel() {
        return topPanel;
    }
    
    public JLabel getInfolabel() {
        return infolabel;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public JButton getPreviousButton() {
        return previousButton;
    }

    public JButton getFinishButton() {
        return finishButton;
    }

    public JButton getReportButton() {
        return reportButton;
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

        topPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        infolabel = new javax.swing.JLabel();
        finishButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(20, 20));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        topPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        topPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        add(topPanel, gridBagConstraints);

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));
        bottomPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        bottomPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        previousButton.setText("Previous");

        nextButton.setText("Next");

        finishButton.setText("Finish");

        reportButton.setText("Create Report");

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infolabel, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                .addGap(69, 69, 69)
                .addComponent(reportButton)
                .addGap(66, 66, 66)
                .addComponent(previousButton)
                .addGap(18, 18, 18)
                .addComponent(nextButton)
                .addGap(18, 18, 18)
                .addComponent(finishButton)
                .addGap(26, 26, 26))
        );

        bottomPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {finishButton, nextButton, previousButton});

        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infolabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(previousButton)
                        .addComponent(nextButton)
                        .addComponent(finishButton)
                        .addComponent(reportButton)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton finishButton;
    private javax.swing.JLabel infolabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton reportButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
