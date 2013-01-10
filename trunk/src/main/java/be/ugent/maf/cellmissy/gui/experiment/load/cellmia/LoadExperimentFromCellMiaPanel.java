/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoadExperimentFromCellMiaPanel.java
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
public class LoadExperimentFromCellMiaPanel extends javax.swing.JPanel {

    /** Creates new form LoadExperimentFromCellMiaPanel */
    public LoadExperimentFromCellMiaPanel() {
        initComponents();
    }

    public JPanel getLoadDataPlateParentPanel() {
        return loadDataPlateParentPanel;
    }

    public JPanel getLoadExperimentParentPanel() {
        return loadExperimentParentPanel;
    }

    public JPanel getExpMetadataParentPanel() {
        return expMetadataParentPanel;
    }

    public JLabel getInfolabel() {
        return infolabel;
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

    public JProgressBar getjProgressBar1() {
        return jProgressBar1;
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
        leftPanel = new javax.swing.JPanel();
        loadExperimentParentPanel = new javax.swing.JPanel();
        expMetadataParentPanel = new javax.swing.JPanel();
        loadDataPlateParentPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        infolabel = new javax.swing.JLabel();
        forwardButton = new javax.swing.JButton();
        finishButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        parseObsepFileButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        topPanel.setLayout(new java.awt.GridBagLayout());

        leftPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        leftPanel.setLayout(new java.awt.GridBagLayout());

        loadExperimentParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        loadExperimentParentPanel.setOpaque(false);
        loadExperimentParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        loadExperimentParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        leftPanel.add(loadExperimentParentPanel, gridBagConstraints);

        expMetadataParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        expMetadataParentPanel.setOpaque(false);
        expMetadataParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        expMetadataParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 0);
        leftPanel.add(expMetadataParentPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        topPanel.add(leftPanel, gridBagConstraints);

        loadDataPlateParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Plate view"));
        loadDataPlateParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        loadDataPlateParentPanel.setOpaque(false);
        loadDataPlateParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        loadDataPlateParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(100, 100, 100, 100);
        topPanel.add(loadDataPlateParentPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        add(topPanel, gridBagConstraints);

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        forwardButton.setText("Forward");

        finishButton.setText("Finish");

        cancelButton.setText("Cancel");

        parseObsepFileButton.setText("Exp Data");

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(infolabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(357, 357, 357)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(parseObsepFileButton)
                .addGap(18, 18, 18)
                .addComponent(cancelButton)
                .addGap(18, 18, 18)
                .addComponent(forwardButton)
                .addGap(18, 18, 18)
                .addComponent(finishButton)
                .addGap(539, 539, 539))
        );

        bottomPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, finishButton, forwardButton, parseObsepFileButton});

        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infolabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(finishButton)
                            .addComponent(forwardButton)
                            .addComponent(cancelButton)
                            .addComponent(parseObsepFileButton))
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel expMetadataParentPanel;
    private javax.swing.JButton finishButton;
    private javax.swing.JButton forwardButton;
    private javax.swing.JLabel infolabel;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel loadDataPlateParentPanel;
    private javax.swing.JPanel loadExperimentParentPanel;
    private javax.swing.JButton parseObsepFileButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
