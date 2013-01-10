/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoadExperimentFromGenericInputPanel.java
 *
 * Created on Dec 18, 2012, 2:23:31 PM
 */
package be.ugent.maf.cellmissy.gui.experiment.load.generic;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadExperimentFromGenericInputPanel extends javax.swing.JPanel {

    /** Creates new form LoadExperimentFromGenericInputPanel */
    public LoadExperimentFromGenericInputPanel() {
        initComponents();
    }

    public JPanel getLoadDataPlateParentPanel() {
        return loadDataPlateParentPanel;
    }

    public JPanel getExpMetadataParentPanel() {
        return expMetadataParentPanel;
    }

    public JPanel getLoadExperimentParentPanel() {
        return loadExperimentParentPanel;
    }

    public JLabel getInfolabel() {
        return infolabel;
    }

    public JButton getAddAlgoButton() {
        return addAlgoButton;
    }

    public JButton getAddImagingButton() {
        return addImagingButton;
    }

    public JList getAlgoList() {
        return algoList;
    }

    public JTextField getAlgoTextField() {
        return algoTextField;
    }

    public JList getImagingList() {
        return imagingList;
    }

    public JTextField getImagingTextField() {
        return imagingTextField;
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
        userInteractionPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        algoTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        imagingTextField = new javax.swing.JTextField();
        addAlgoButton = new javax.swing.JButton();
        algoScrollPane = new javax.swing.JScrollPane();
        algoList = new javax.swing.JList();
        addImagingButton = new javax.swing.JButton();
        imagingScrollPane = new javax.swing.JScrollPane();
        imagingList = new javax.swing.JList();
        bottomPanel = new javax.swing.JPanel();
        infolabel = new javax.swing.JLabel();

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
        gridBagConstraints.weighty = 0.65;
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.35;
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(0, 100, 0, 80);
        topPanel.add(loadDataPlateParentPanel, gridBagConstraints);

        userInteractionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Algorithm - Imaging Info"));
        userInteractionPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        userInteractionPanel.setOpaque(false);
        userInteractionPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Algorithm:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Imaging Type:");

        addAlgoButton.setText("Add Algorithm");

        algoScrollPane.setBorder(null);

        algoList.setMinimumSize(new java.awt.Dimension(20, 20));
        algoList.setPreferredSize(new java.awt.Dimension(20, 20));
        algoScrollPane.setViewportView(algoList);

        addImagingButton.setText("Add Imaging Type");

        imagingScrollPane.setBorder(null);

        imagingList.setMinimumSize(new java.awt.Dimension(20, 20));
        imagingList.setPreferredSize(new java.awt.Dimension(20, 20));
        imagingScrollPane.setViewportView(imagingList);

        javax.swing.GroupLayout userInteractionPanelLayout = new javax.swing.GroupLayout(userInteractionPanel);
        userInteractionPanel.setLayout(userInteractionPanelLayout);
        userInteractionPanelLayout.setHorizontalGroup(
            userInteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userInteractionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(userInteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(algoScrollPane, 0, 0, Short.MAX_VALUE)
                    .addGroup(userInteractionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(32, 32, 32)
                        .addComponent(algoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(addAlgoButton))
                .addGap(66, 66, 66)
                .addGroup(userInteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imagingScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .addGroup(userInteractionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(10, 10, 10)
                        .addComponent(imagingTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                    .addComponent(addImagingButton))
                .addGap(207, 207, 207))
        );

        userInteractionPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {algoTextField, imagingTextField});

        userInteractionPanelLayout.setVerticalGroup(
            userInteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userInteractionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(userInteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(algoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(imagingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(userInteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addImagingButton)
                    .addComponent(addAlgoButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(userInteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imagingScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(algoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.3;
        topPanel.add(userInteractionPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        add(topPanel, gridBagConstraints);

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));
        bottomPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(infolabel, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(562, Short.MAX_VALUE))
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infolabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
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
    private javax.swing.JButton addAlgoButton;
    private javax.swing.JButton addImagingButton;
    private javax.swing.JList algoList;
    private javax.swing.JScrollPane algoScrollPane;
    private javax.swing.JTextField algoTextField;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel expMetadataParentPanel;
    private javax.swing.JList imagingList;
    private javax.swing.JScrollPane imagingScrollPane;
    private javax.swing.JTextField imagingTextField;
    private javax.swing.JLabel infolabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel loadDataPlateParentPanel;
    private javax.swing.JPanel loadExperimentParentPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel userInteractionPanel;
    // End of variables declaration//GEN-END:variables
}
