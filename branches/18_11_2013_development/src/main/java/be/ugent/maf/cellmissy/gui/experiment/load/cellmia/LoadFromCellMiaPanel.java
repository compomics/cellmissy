/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoadFromCellMiaPanel.java
 *
 * Created on Jun 15, 2012, 1:36:56 PM
 */
package be.ugent.maf.cellmissy.gui.experiment.load.cellmia;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadFromCellMiaPanel extends javax.swing.JPanel {

    /** Creates new form LoadFromCellMiaPanel */
    public LoadFromCellMiaPanel() {
        initComponents();
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public JLabel getInfolabel() {
        return infolabel;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getExpDataButton() {
        return parseObsepFileButton;
    }

    public JButton getForwardButton() {
        return forwardButton;
    }

    public JButton getFinishButton() {
        return finishButton;
    }

    public JProgressBar getSaveDataProgressBar() {
        return saveDataProgressBar;
    }

    public JButton getCancelButton() {
        return cancelButton;
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
        infolabel = new javax.swing.JLabel();
        forwardButton = new javax.swing.JButton();
        finishButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        parseObsepFileButton = new javax.swing.JButton();
        saveDataProgressBar = new javax.swing.JProgressBar();
        startButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        topPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.95;
        add(topPanel, gridBagConstraints);

        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        forwardButton.setText("Forward");

        finishButton.setText("Finish");

        cancelButton.setText("Cancel");

        parseObsepFileButton.setText("Exp Data");

        startButton.setText("Start");

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infolabel, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(saveDataProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(parseObsepFileButton)
                .addGap(18, 18, 18)
                .addComponent(startButton)
                .addGap(18, 18, 18)
                .addComponent(cancelButton)
                .addGap(18, 18, 18)
                .addComponent(forwardButton)
                .addGap(18, 18, 18)
                .addComponent(finishButton)
                .addContainerGap())
        );

        bottomPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, finishButton, forwardButton, parseObsepFileButton, startButton});

        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(finishButton)
                            .addComponent(forwardButton)
                            .addComponent(cancelButton)
                            .addComponent(startButton)
                            .addComponent(parseObsepFileButton))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                            .addComponent(saveDataProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(6, 6, 6)))
                    .addComponent(infolabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        bottomPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {parseObsepFileButton, saveDataProgressBar});

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.05;
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton finishButton;
    private javax.swing.JButton forwardButton;
    private javax.swing.JLabel infolabel;
    private javax.swing.JButton parseObsepFileButton;
    private javax.swing.JProgressBar saveDataProgressBar;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}