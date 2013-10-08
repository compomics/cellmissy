/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.area;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author paola
 */
public class TransformedAreaPanel extends javax.swing.JPanel {

    public JCheckBox getPlotLinesCheckBox() {
        return plotLinesCheckBox;
    }

    public JCheckBox getPlotPointsCheckBox() {
        return plotPointsCheckBox;
    }

    public JPanel getReplicatesAreaChartParentPanel() {
        return replicatesAreaChartParentPanel;
    }

    /**
     * Creates new form TransformedAreaPanel
     */
    public TransformedAreaPanel() {
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

        leftPanel = new javax.swing.JPanel();
        plotPointsCheckBox = new javax.swing.JCheckBox();
        plotLinesCheckBox = new javax.swing.JCheckBox();
        replicatesAreaChartParentPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        leftPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        plotPointsCheckBox.setText("plot points");
        plotPointsCheckBox.setOpaque(false);

        plotLinesCheckBox.setText("plot lines");
        plotLinesCheckBox.setOpaque(false);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(plotPointsCheckBox)
                    .addComponent(plotLinesCheckBox))
                .addContainerGap())
        );

        leftPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {plotLinesCheckBox, plotPointsCheckBox});

        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                .addContainerGap(116, Short.MAX_VALUE)
                .addComponent(plotPointsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(plotLinesCheckBox)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        add(leftPanel, gridBagConstraints);

        replicatesAreaChartParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        replicatesAreaChartParentPanel.setOpaque(false);
        replicatesAreaChartParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        replicatesAreaChartParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        add(replicatesAreaChartParentPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel leftPanel;
    private javax.swing.JCheckBox plotLinesCheckBox;
    private javax.swing.JCheckBox plotPointsCheckBox;
    private javax.swing.JPanel replicatesAreaChartParentPanel;
    // End of variables declaration//GEN-END:variables
}