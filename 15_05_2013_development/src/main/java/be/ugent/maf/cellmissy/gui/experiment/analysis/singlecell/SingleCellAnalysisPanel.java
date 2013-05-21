/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

    public JLabel getTableInfoLabel() {
        return tableInfoLabel;
    }

    public JLabel getResultsImportingLabel() {
        return resultsImportingLabel;
    }

    public JLabel getPreProcessingLabel() {
        return preProcessingLabel;
    }

    public JRadioButton getNormalizedTrackCoordinatesRadioButton() {
        return normalizedTrackCoordinatesRadioButton;
    }

    public JPanel getDataTablePanel() {
        return dataTablePanel;
    }

    public JRadioButton getAngleRadioButton() {
        return angleRadioButton;
    }

    public JRadioButton getVelocityRadioButton() {
        return velocityRadioButton;
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
        preProcessingLabel = new javax.swing.JLabel();
        bottomPanel = new javax.swing.JPanel();
        resultsImporterPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tracksScrollPane = new javax.swing.JScrollPane();
        tracksTable = new javax.swing.JTable();
        trackPointsScrollPane = new javax.swing.JScrollPane();
        trackPointsTable = new javax.swing.JTable();
        preProcessingPanel = new javax.swing.JPanel();
        radioButtonsPanel = new javax.swing.JPanel();
        tableInfoLabel = new javax.swing.JLabel();
        normalizedTrackCoordinatesRadioButton = new javax.swing.JRadioButton();
        velocityRadioButton = new javax.swing.JRadioButton();
        angleRadioButton = new javax.swing.JRadioButton();
        dataTablePanel = new javax.swing.JPanel();
        graphicsParentPanel = new javax.swing.JPanel();
        extraPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        resultsImportingLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        resultsImportingLabel.setText("1. Data Importing");
        resultsImportingLabel.setToolTipText("Data loaded from DB");

        preProcessingLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        preProcessingLabel.setText("2. Pre-processing");
        preProcessingLabel.setToolTipText("Data pre-processing: normalization and outliers detection");

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultsImportingLabel)
                .addGap(18, 18, 18)
                .addComponent(preProcessingLabel)
                .addContainerGap(685, Short.MAX_VALUE))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsImportingLabel)
                    .addComponent(preProcessingLabel))
                .addContainerGap(13, Short.MAX_VALUE))
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
                                .addGap(0, 717, Short.MAX_VALUE))
                            .addComponent(trackPointsScrollPane))
                        .addContainerGap())))
        );
        resultsImporterPanelLayout.setVerticalGroup(
            resultsImporterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsImporterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tracksScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(trackPointsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bottomPanel.add(resultsImporterPanel, "resultsImporterPanel");
        resultsImporterPanel.getAccessibleContext().setAccessibleName("resultsImporterPanel");

        preProcessingPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        preProcessingPanel.setName("preProcessingPanel"); // NOI18N
        preProcessingPanel.setOpaque(false);
        preProcessingPanel.setLayout(new java.awt.GridBagLayout());

        radioButtonsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        radioButtonsPanel.setOpaque(false);
        radioButtonsPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        tableInfoLabel.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        tableInfoLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        tableInfoLabel.setPreferredSize(new java.awt.Dimension(20, 20));

        normalizedTrackCoordinatesRadioButton.setText("Track Coordinates");
        normalizedTrackCoordinatesRadioButton.setOpaque(false);

        velocityRadioButton.setText("Velocity");
        velocityRadioButton.setOpaque(false);

        angleRadioButton.setText("Angle");
        angleRadioButton.setOpaque(false);

        javax.swing.GroupLayout radioButtonsPanelLayout = new javax.swing.GroupLayout(radioButtonsPanel);
        radioButtonsPanel.setLayout(radioButtonsPanelLayout);
        radioButtonsPanelLayout.setHorizontalGroup(
            radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(radioButtonsPanelLayout.createSequentialGroup()
                        .addComponent(normalizedTrackCoordinatesRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(velocityRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(angleRadioButton))
                    .addComponent(tableInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        radioButtonsPanelLayout.setVerticalGroup(
            radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, radioButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(normalizedTrackCoordinatesRadioButton)
                    .addComponent(velocityRadioButton)
                    .addComponent(angleRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.09;
        preProcessingPanel.add(radioButtonsPanel, gridBagConstraints);

        dataTablePanel.setMinimumSize(new java.awt.Dimension(20, 20));
        dataTablePanel.setOpaque(false);
        dataTablePanel.setPreferredSize(new java.awt.Dimension(20, 20));
        dataTablePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.3;
        preProcessingPanel.add(dataTablePanel, gridBagConstraints);

        graphicsParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        graphicsParentPanel.setOpaque(false);
        graphicsParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        graphicsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.61;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        preProcessingPanel.add(graphicsParentPanel, gridBagConstraints);

        extraPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        extraPanel.setOpaque(false);
        extraPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout extraPanelLayout = new javax.swing.GroupLayout(extraPanel);
        extraPanel.setLayout(extraPanelLayout);
        extraPanelLayout.setHorizontalGroup(
            extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
        );
        extraPanelLayout.setVerticalGroup(
            extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 198, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.3;
        preProcessingPanel.add(extraPanel, gridBagConstraints);

        bottomPanel.add(preProcessingPanel, "preProcessingPanel");
        preProcessingPanel.getAccessibleContext().setAccessibleName("preProcessingPanel");

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
    private javax.swing.JRadioButton angleRadioButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel dataTablePanel;
    private javax.swing.JPanel extraPanel;
    private javax.swing.JPanel graphicsParentPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton normalizedTrackCoordinatesRadioButton;
    private javax.swing.JLabel preProcessingLabel;
    private javax.swing.JPanel preProcessingPanel;
    private javax.swing.JPanel radioButtonsPanel;
    private javax.swing.JPanel resultsImporterPanel;
    private javax.swing.JLabel resultsImportingLabel;
    private javax.swing.JLabel tableInfoLabel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JScrollPane trackPointsScrollPane;
    private javax.swing.JTable trackPointsTable;
    private javax.swing.JScrollPane tracksScrollPane;
    private javax.swing.JTable tracksTable;
    private javax.swing.JRadioButton velocityRadioButton;
    // End of variables declaration//GEN-END:variables
}
