/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.load.cellmia;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadFromCellMiaPlatePanel extends javax.swing.JPanel {

    public JList getConditionsList() {
        return conditionsList;
    }

    public JPanel getPlateParentPanel() {
        return plateParentPanel;
    }

    public JLabel getExpNumberLabel() {
        return expNumberLabel;
    }

    public JLabel getExpPurposeLabel() {
        return expPurposeLabel;
    }

    public JLabel getProjNumberLabel() {
        return projNumberLabel;
    }

    /**
     * Creates new form LoadFromCellMiaPlatePanel
     */
    public LoadFromCellMiaPlatePanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        experimentMetadataPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        projNumberLabel = new javax.swing.JLabel();
        expNumberLabel = new javax.swing.JLabel();
        expPurposeLabel = new javax.swing.JLabel();
        conditionsOverviewPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        conditionsList = new javax.swing.JList();
        plateParentPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        experimentMetadataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Experiment metadata", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        experimentMetadataPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        experimentMetadataPanel.setOpaque(false);
        experimentMetadataPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Number:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Project:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Purpose:");

        projNumberLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        expNumberLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        expPurposeLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout experimentMetadataPanelLayout = new javax.swing.GroupLayout(experimentMetadataPanel);
        experimentMetadataPanel.setLayout(experimentMetadataPanelLayout);
        experimentMetadataPanelLayout.setHorizontalGroup(
            experimentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentMetadataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(experimentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(experimentMetadataPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(23, 23, 23)
                        .addComponent(projNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(experimentMetadataPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(expNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(experimentMetadataPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(expPurposeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        experimentMetadataPanelLayout.setVerticalGroup(
            experimentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentMetadataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(experimentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(projNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(experimentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(expNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(experimentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(expPurposeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(60, 60, 0, 0);
        add(experimentMetadataPanel, gridBagConstraints);

        conditionsOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Conditions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        conditionsOverviewPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        conditionsOverviewPanel.setName(""); // NOI18N
        conditionsOverviewPanel.setOpaque(false);
        conditionsOverviewPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jScrollPane2.setBorder(null);

        conditionsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(conditionsList);

        javax.swing.GroupLayout conditionsOverviewPanelLayout = new javax.swing.GroupLayout(conditionsOverviewPanel);
        conditionsOverviewPanel.setLayout(conditionsOverviewPanelLayout);
        conditionsOverviewPanelLayout.setHorizontalGroup(
            conditionsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conditionsOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addContainerGap())
        );
        conditionsOverviewPanelLayout.setVerticalGroup(
            conditionsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 60, 60, 0);
        add(conditionsOverviewPanel, gridBagConstraints);

        plateParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Plate", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        plateParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        plateParentPanel.setName(""); // NOI18N
        plateParentPanel.setOpaque(false);
        plateParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        plateParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(55, 60, 55, 60);
        add(plateParentPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList conditionsList;
    private javax.swing.JPanel conditionsOverviewPanel;
    private javax.swing.JLabel expNumberLabel;
    private javax.swing.JLabel expPurposeLabel;
    private javax.swing.JPanel experimentMetadataPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel plateParentPanel;
    private javax.swing.JLabel projNumberLabel;
    // End of variables declaration//GEN-END:variables
}
