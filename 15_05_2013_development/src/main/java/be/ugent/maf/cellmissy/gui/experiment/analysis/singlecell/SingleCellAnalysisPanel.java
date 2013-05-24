/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class SingleCellAnalysisPanel extends javax.swing.JPanel {

    public JTable getTrackPointsTable() {
        return trackPointsTable;
    }

    public JTable getTracksTable() {
        return tracksTable;
    }

    public JScrollPane getTrackPointsScrollPane() {
        return trackPointsScrollPane;
    }

    public JScrollPane getTracksScrollPane() {
        return tracksScrollPane;
    }

    public JPanel getBottomPanel() {
        return bottomPanel;
    }

    public JLabel getResultsImportingLabel() {
        return resultsImportingLabel;
    }

    public JLabel getTrackCoordinatesLabel() {
        return trackCoordinatesLabel;
    }

    public JPanel getTrackCoordinatesParentPanel() {
        return trackCoordinatesParentPanel;
    }

    /**
     * Creates new form SingleCellAnalysisPanel
     */
    public SingleCellAnalysisPanel() {
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

        topPanel = new javax.swing.JPanel();
        resultsImportingLabel = new javax.swing.JLabel();
        trackCoordinatesLabel = new javax.swing.JLabel();
        singleCellVelocitiesLabel = new javax.swing.JLabel();
        bottomPanel = new javax.swing.JPanel();
        resultsImporterPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tracksScrollPane = new javax.swing.JScrollPane();
        tracksTable = new javax.swing.JTable();
        trackPointsScrollPane = new javax.swing.JScrollPane();
        trackPointsTable = new javax.swing.JTable();
        trackCoordinatesParentPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        resultsImportingLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        resultsImportingLabel.setText("1. Data Importing");
        resultsImportingLabel.setToolTipText("Data loaded from DB");

        trackCoordinatesLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        trackCoordinatesLabel.setText("2. Track Coordinates");
        trackCoordinatesLabel.setToolTipText("Data pre-processing: normalization and outliers detection");

        singleCellVelocitiesLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        singleCellVelocitiesLabel.setText("3. Single Cell Velocities");
        singleCellVelocitiesLabel.setToolTipText("Data pre-processing: normalization and outliers detection");

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultsImportingLabel)
                .addGap(18, 18, 18)
                .addComponent(trackCoordinatesLabel)
                .addGap(18, 18, 18)
                .addComponent(singleCellVelocitiesLabel)
                .addContainerGap(667, Short.MAX_VALUE))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsImportingLabel)
                    .addComponent(trackCoordinatesLabel)
                    .addComponent(singleCellVelocitiesLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.03;
        add(topPanel, gridBagConstraints);

        bottomPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        bottomPanel.setLayout(new java.awt.CardLayout());

        resultsImporterPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        resultsImporterPanel.setName("resultsImporterPanel"); // NOI18N
        resultsImporterPanel.setOpaque(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel3.setText("Tracking results from DB");

        tracksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tracksScrollPane.setViewportView(tracksTable);

        trackPointsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        trackPointsScrollPane.setViewportView(trackPointsTable);

        javax.swing.GroupLayout resultsImporterPanelLayout = new javax.swing.GroupLayout(resultsImporterPanel);
        resultsImporterPanel.setLayout(resultsImporterPanelLayout);
        resultsImporterPanelLayout.setHorizontalGroup(
            resultsImporterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsImporterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsImporterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsImporterPanelLayout.createSequentialGroup()
                        .addComponent(tracksScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(resultsImporterPanelLayout.createSequentialGroup()
                        .addGroup(resultsImporterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(resultsImporterPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 860, Short.MAX_VALUE))
                            .addComponent(trackPointsScrollPane))
                        .addContainerGap())))
        );
        resultsImporterPanelLayout.setVerticalGroup(
            resultsImporterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsImporterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tracksScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(trackPointsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bottomPanel.add(resultsImporterPanel, "resultsImporterPanel");
        resultsImporterPanel.getAccessibleContext().setAccessibleName("resultsImporterPanel");

        trackCoordinatesParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        trackCoordinatesParentPanel.setName("trackCoordinatesParentPanel"); // NOI18N
        trackCoordinatesParentPanel.setOpaque(false);
        trackCoordinatesParentPanel.setLayout(new java.awt.GridBagLayout());
        bottomPanel.add(trackCoordinatesParentPanel, "trackCoordinatesParentPanel");
        trackCoordinatesParentPanel.getAccessibleContext().setAccessibleName("trackCoordinatesParentPanel");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.97;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel resultsImporterPanel;
    private javax.swing.JLabel resultsImportingLabel;
    private javax.swing.JLabel singleCellVelocitiesLabel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel trackCoordinatesLabel;
    private javax.swing.JPanel trackCoordinatesParentPanel;
    private javax.swing.JScrollPane trackPointsScrollPane;
    private javax.swing.JTable trackPointsTable;
    private javax.swing.JScrollPane tracksScrollPane;
    private javax.swing.JTable tracksTable;
    // End of variables declaration//GEN-END:variables
}
