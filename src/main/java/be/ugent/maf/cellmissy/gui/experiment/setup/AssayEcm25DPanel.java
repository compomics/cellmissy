/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AssayEcm3DPanel.java
 *
 * Created on Apr 3, 2012, 3:04:23 PM
 */
package be.ugent.maf.cellmissy.gui.experiment.setup;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Paola
 */
public class AssayEcm25DPanel extends javax.swing.JPanel {

    /** Creates new form AssayEcm3DPanel */
    public AssayEcm25DPanel() {
        initComponents();
    }

    public JComboBox getAssayComboBox() {
        return assayComboBox;
    }

    public JComboBox getCompositionComboBox() {
        return compositionComboBox;
    }

    public JComboBox getDensityComboBox() {
        return densityComboBox;
    }

    public JTextField getPolymerizationTemperatureTextField() {
        return polymerizationTemperatureTextField;
    }

    public JTextField getPolymerizationTimeTextField() {
        return polymerizationTimeTextField;
    }

    public JButton getAddCompositionButton() {
        return addCompositionButton;
    }

    public JTextField getCompositionTextField() {
        return compositionTextField;
    }

    public JLabel getTopVolumeUnitLabel() {
        return topVolumeUnitLabel;
    }

    public JLabel getBottomVolumeUnitlabel() {
        return bottomVolumeUnitlabel;
    }

    public JComboBox getBottomMatrixTypeComboBox() {
        return bottomMatrixTypeComboBox;
    }

    public JComboBox getPolymerizationPhComboBox() {
        return polymerizationPhComboBox;
    }

    public JTextField getBottomMatrixVolumeTextField() {
        return bottomMatrixVolumeTextField;
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
        densityLabel = new javax.swing.JLabel();
        compositionLabel = new javax.swing.JLabel();
        polymerizationTimeLabel = new javax.swing.JLabel();
        polymerizationTemperatureLabel = new javax.swing.JLabel();
        polymerizationTimeTextField = new javax.swing.JTextField();
        polymerizationTemperatureTextField = new javax.swing.JTextField();
        compositionComboBox = new javax.swing.JComboBox();
        densityComboBox = new javax.swing.JComboBox();
        addCompositionButton = new javax.swing.JButton();
        compositionTextField = new javax.swing.JTextField();
        topVolumeUnitLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        bottomMatrixTypeComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        bottomVolumeUnitlabel = new javax.swing.JLabel();
        bottomMatrixVolumeTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        polymerizationPhComboBox = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        assayPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        assayPanel.setOpaque(false);
        assayPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        assayComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        selectAssayLabel.setText("Select an invasion assay");

        javax.swing.GroupLayout assayPanelLayout = new javax.swing.GroupLayout(assayPanel);
        assayPanel.setLayout(assayPanelLayout);
        assayPanelLayout.setHorizontalGroup(
            assayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(assayPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(selectAssayLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(assayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(319, Short.MAX_VALUE))
        );
        assayPanelLayout.setVerticalGroup(
            assayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(assayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(assayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(assayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectAssayLabel))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(assayPanel, gridBagConstraints);

        ecmPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Extra Cellular Matrix", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        ecmPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        ecmPanel.setOpaque(false);
        ecmPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        densityLabel.setText("Density");

        compositionLabel.setText("Composition");

        polymerizationTimeLabel.setText("Polymerization time (min)");

        polymerizationTemperatureLabel.setText("Polymerization temperature");

        compositionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        densityComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        addCompositionButton.setText("Add new composition");

        topVolumeUnitLabel.setMinimumSize(new java.awt.Dimension(10, 10));

        jLabel1.setText("Bottom matrix type");

        bottomMatrixTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel2.setText("Bottom matrix volume");

        bottomVolumeUnitlabel.setMinimumSize(new java.awt.Dimension(10, 10));

        bottomMatrixVolumeTextField.setText("0");

        jLabel3.setText("Polymerization PH");

        polymerizationPhComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout ecmPanelLayout = new javax.swing.GroupLayout(ecmPanel);
        ecmPanel.setLayout(ecmPanelLayout);
        ecmPanelLayout.setHorizontalGroup(
            ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ecmPanelLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ecmPanelLayout.createSequentialGroup()
                        .addComponent(compositionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addCompositionButton))
                    .addGroup(ecmPanelLayout.createSequentialGroup()
                        .addComponent(compositionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(compositionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(229, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(ecmPanelLayout.createSequentialGroup()
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ecmPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(polymerizationTimeLabel)))
                    .addGroup(ecmPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(polymerizationTemperatureLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ecmPanelLayout.createSequentialGroup()
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(polymerizationTimeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                            .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(bottomMatrixVolumeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ecmPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(bottomMatrixTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ecmPanelLayout.createSequentialGroup()
                                    .addComponent(densityLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(densityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(polymerizationTemperatureTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(topVolumeUnitLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bottomVolumeUnitlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(polymerizationPhComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(141, 141, 141))
        );
        ecmPanelLayout.setVerticalGroup(
            ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ecmPanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(compositionLabel)
                    .addComponent(compositionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addCompositionButton)
                    .addComponent(compositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addGroup(ecmPanelLayout.createSequentialGroup()
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(densityLabel)
                            .addComponent(densityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(bottomMatrixTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bottomVolumeUnitlabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bottomMatrixVolumeTextField, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(11, 11, 11)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ecmPanelLayout.createSequentialGroup()
                        .addComponent(topVolumeUnitLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                        .addGap(40, 40, 40))
                    .addGroup(ecmPanelLayout.createSequentialGroup()
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(polymerizationTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(polymerizationTimeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(polymerizationTemperatureTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(polymerizationTemperatureLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ecmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(polymerizationPhComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(52, 52, 52))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        add(ecmPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCompositionButton;
    private javax.swing.JComboBox assayComboBox;
    private javax.swing.JPanel assayPanel;
    private javax.swing.JComboBox bottomMatrixTypeComboBox;
    private javax.swing.JTextField bottomMatrixVolumeTextField;
    private javax.swing.JLabel bottomVolumeUnitlabel;
    private javax.swing.JComboBox compositionComboBox;
    private javax.swing.JLabel compositionLabel;
    private javax.swing.JTextField compositionTextField;
    private javax.swing.JComboBox densityComboBox;
    private javax.swing.JLabel densityLabel;
    private javax.swing.JPanel ecmPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox polymerizationPhComboBox;
    private javax.swing.JLabel polymerizationTemperatureLabel;
    private javax.swing.JTextField polymerizationTemperatureTextField;
    private javax.swing.JLabel polymerizationTimeLabel;
    private javax.swing.JTextField polymerizationTimeTextField;
    private javax.swing.JLabel selectAssayLabel;
    private javax.swing.JLabel topVolumeUnitLabel;
    // End of variables declaration//GEN-END:variables
}
