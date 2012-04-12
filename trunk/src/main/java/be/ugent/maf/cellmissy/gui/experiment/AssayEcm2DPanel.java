/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AssayEcm2DPanel.java
 *
 * Created on Apr 3, 2012, 9:30:52 AM
 */
package be.ugent.maf.cellmissy.gui.experiment;

import javax.swing.JComboBox;

/**
 *
 * @author Paola
 */
public class AssayEcm2DPanel extends javax.swing.JPanel {

    /** Creates new form AssayEcm2DPanel */
    public AssayEcm2DPanel() {
        initComponents();
    }

    public JComboBox getAssayComboBox() {
        return assayComboBox;
    }

    public JComboBox getCompositionComboBox() {
        return compositionComboBox;
    }

    public JComboBox getCoatingComboBox() {
        return coatingComboBox;
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

        assayPanel = new javax.swing.JPanel();
        assayComboBox = new javax.swing.JComboBox();
        selectAssayLabel = new javax.swing.JLabel();
        ecmPanel = new javax.swing.JPanel();
        coatingTypeLabel = new javax.swing.JLabel();
        concentrationLabel = new javax.swing.JLabel();
        compositionLabel = new javax.swing.JLabel();
        concentraionTextField = new javax.swing.JTextField();
        volumeLabel = new javax.swing.JLabel();
        coatingTimeLabel = new javax.swing.JLabel();
        coatingTemperatureLabel = new javax.swing.JLabel();
        volumeTextField = new javax.swing.JTextField();
        coatingTimeTextField = new javax.swing.JTextField();
        coatingTemperatureTextField = new javax.swing.JTextField();
        compositionComboBox = new javax.swing.JComboBox();
        coatingComboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        assayPanel.setPreferredSize(new java.awt.Dimension(400, 10));

        selectAssayLabel.setText("Select a migration assay");

        javax.swing.GroupLayout assayPanelLayout = new javax.swing.GroupLayout(assayPanel);
        assayPanel.setLayout(assayPanelLayout);
        assayPanelLayout.setHorizontalGroup(
            assayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(assayPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(selectAssayLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(assayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(228, Short.MAX_VALUE))
        );
        assayPanelLayout.setVerticalGroup(
            assayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(assayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(assayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(assayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectAssayLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(assayPanel, gridBagConstraints);

        ecmPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("ECM parameters"));
        ecmPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        ecmPanel.setPreferredSize(new java.awt.Dimension(400, 50));

        coatingTypeLabel.setText("Coating type");

        concentrationLabel.setText("Concentration");

        compositionLabel.setText("Composition");

        volumeLabel.setText("Volume");

        coatingTimeLabel.setText("Coating time");

        coatingTemperatureLabel.setText("Coating temperature");

        compositionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        coatingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout ecmPanelLayout = new javax.swing.GroupLayout(ecmPanel);
        ecmPanel.setLayout(ecmPanelLayout);
        ecmPanelLayout.setHorizontalGroup(
            ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ecmPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(compositionLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(coatingTypeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(concentrationLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(volumeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coatingTimeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(coatingTemperatureLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(compositionComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(concentraionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                        .addComponent(volumeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                        .addComponent(coatingTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(coatingTemperatureTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(coatingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(209, 209, 209))
        );

        ecmPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {coatingComboBox, compositionComboBox});

        ecmPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {coatingTemperatureTextField, coatingTimeTextField, concentraionTextField, volumeTextField});

        ecmPanelLayout.setVerticalGroup(
            ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ecmPanelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(compositionLabel)
                    .addComponent(compositionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coatingTypeLabel)
                    .addComponent(coatingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(concentrationLabel)
                    .addComponent(concentraionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(volumeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(volumeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coatingTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coatingTimeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coatingTemperatureTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coatingTemperatureLabel))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        add(ecmPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox assayComboBox;
    private javax.swing.JPanel assayPanel;
    private javax.swing.JComboBox coatingComboBox;
    private javax.swing.JLabel coatingTemperatureLabel;
    private javax.swing.JTextField coatingTemperatureTextField;
    private javax.swing.JLabel coatingTimeLabel;
    private javax.swing.JTextField coatingTimeTextField;
    private javax.swing.JLabel coatingTypeLabel;
    private javax.swing.JComboBox compositionComboBox;
    private javax.swing.JLabel compositionLabel;
    private javax.swing.JTextField concentraionTextField;
    private javax.swing.JLabel concentrationLabel;
    private javax.swing.JPanel ecmPanel;
    private javax.swing.JLabel selectAssayLabel;
    private javax.swing.JLabel volumeLabel;
    private javax.swing.JTextField volumeTextField;
    // End of variables declaration//GEN-END:variables
}
