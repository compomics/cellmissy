/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author CompOmics Gwen
 */
public class DRDataLoadingPanel extends javax.swing.JPanel {

    /**
     * Creates new form DRDataLoadingPanel
     */
    public DRDataLoadingPanel() {
        initComponents();
    }

    public JTextField getAssayTextField() {
        return assayTextField;
    }

    public JTextField getCellLineTextField() {
        return cellLineTextField;
    }

    public JButton getChooseFileButton() {
        return chooseFileButton;
    }

    public JTextField getDurationTextField() {
        return durationTextField;
    }

    public JTextField getEcmCompositionTextField() {
        return ecmCompositionTextField;
    }

    public JTextField getEcmDensityTextField() {
        return ecmDensityTextField;
    }

    public JTextField getExpNumberTextField() {
        return expNumberTextField;
    }

    public JTextField getExpTitleTextField() {
        return expTitleTextField;
    }

    public JTextField getDatasetTextField() {
        return datasetTextField;
    }

    public JTextField getInstrumentTextField() {
        return instrumentTextField;
    }

    public JTextField getIntervalTextField() {
        return intervalTextField;
    }

    public JScrollPane getjScrollPane5() {
        return jScrollPane5;
    }

    public JTextField getMatrixTextField() {
        return matrixTextField;
    }

    public JTextField getPlateFormatTextField() {
        return plateFormatTextField;
    }

   public JTextArea getPurposeTextArea() {
        return purposeTextArea;
    }

    public JTextField getTreatmentTextField() {
        return treatmentTextField;
    }

    public JCheckBox getLogTransformCheckBox() {
        return logTransformCheckBox;
    }

    public JLabel getFileLabel() {
        return fileLabel;
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

        chooseFilePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        fileLabel = new javax.swing.JLabel();
        chooseFileButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        generalInfoPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        infoLabel1 = new javax.swing.JLabel();
        logTransformCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        expNumberTextField = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        purposeTextArea = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        expTitleTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        datasetTextField = new javax.swing.JTextField();
        experimentDetailsPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        infoLabel2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        instrumentTextField = new javax.swing.JTextField();
        plateFormatTextField = new javax.swing.JTextField();
        durationTextField = new javax.swing.JTextField();
        intervalTextField = new javax.swing.JTextField();
        ecmCompositionTextField = new javax.swing.JTextField();
        ecmDensityTextField = new javax.swing.JTextField();
        treatmentTextField = new javax.swing.JTextField();
        cellLineTextField = new javax.swing.JTextField();
        assayTextField = new javax.swing.JTextField();
        matrixTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        chooseFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Choose a File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        chooseFilePanel.setMinimumSize(new java.awt.Dimension(20, 20));

        jLabel1.setText("File to import dose-response data from");

        fileLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        chooseFileButton.setText("choose file");

        infoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        infoLabel.setText("Choose a file that CellMissy can use for the import of dose-response data (CSV, TSV, XLS, XLSX)");

        jLabel5.setText("No data will be saved to the database. Please make sure to create an analysis report before closing CellMissy.");

        javax.swing.GroupLayout chooseFilePanelLayout = new javax.swing.GroupLayout(chooseFilePanel);
        chooseFilePanel.setLayout(chooseFilePanelLayout);
        chooseFilePanelLayout.setHorizontalGroup(
            chooseFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chooseFilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chooseFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(chooseFilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chooseFileButton))
                    .addComponent(infoLabel)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        chooseFilePanelLayout.setVerticalGroup(
            chooseFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chooseFilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chooseFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(fileLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chooseFileButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(infoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        jLabel5.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.15;
        add(chooseFilePanel, gridBagConstraints);

        generalInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "General Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        generalInfoPanel.setMinimumSize(new java.awt.Dimension(20, 20));

        jLabel4.setText("Experiment Title");

        infoLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        infoLabel1.setText("Provide a general overview for the experiment.");

        logTransformCheckBox.setSelected(true);
        logTransformCheckBox.setText("Log-transform doses on fitting for better visual spreading");
        logTransformCheckBox.setToolTipText("");

        jLabel2.setText("Project/Experiment number");

        jScrollPane5.setOpaque(false);

        purposeTextArea.setColumns(20);
        purposeTextArea.setLineWrap(true);
        purposeTextArea.setRows(5);
        purposeTextArea.setToolTipText("Write a summary of the experiment's purpose.");
        purposeTextArea.setWrapStyleWord(true);
        purposeTextArea.setBorder(null);
        purposeTextArea.setPreferredSize(new java.awt.Dimension(170, 94));
        jScrollPane5.setViewportView(purposeTextArea);

        jLabel12.setText("Purpose");

        jLabel3.setText("Dataset");

        javax.swing.GroupLayout generalInfoPanelLayout = new javax.swing.GroupLayout(generalInfoPanel);
        generalInfoPanel.setLayout(generalInfoPanelLayout);
        generalInfoPanelLayout.setHorizontalGroup(
            generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoLabel1)
                    .addGroup(generalInfoPanelLayout.createSequentialGroup()
                        .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(logTransformCheckBox)
                            .addGroup(generalInfoPanelLayout.createSequentialGroup()
                                .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(expTitleTextField)
                                    .addComponent(expNumberTextField)
                                    .addComponent(datasetTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))))
                        .addGap(155, 155, 155)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(120, 120, 120))
        );
        generalInfoPanelLayout.setVerticalGroup(
            generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalInfoPanelLayout.createSequentialGroup()
                        .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(expNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(expTitleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(datasetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(logTransformCheckBox))
                    .addGroup(generalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(generalInfoPanelLayout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addGap(72, 72, 72))
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.95;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(generalInfoPanel, gridBagConstraints);

        experimentDetailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Experiment Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        experimentDetailsPanel.setMinimumSize(new java.awt.Dimension(20, 20));

        jLabel14.setText("interval");

        jLabel10.setText("duration");

        jLabel6.setText("plate format");

        jLabel11.setText("instrument");

        infoLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        infoLabel2.setText("Please provide some metadata about the experiment.");

        jLabel15.setText("cell line");

        jLabel16.setText("assay type");

        jLabel17.setText("bottom matrix");

        jLabel18.setText("ecm composition");

        jLabel19.setText("ecm density");

        jLabel22.setText("treatment");

        javax.swing.GroupLayout experimentDetailsPanelLayout = new javax.swing.GroupLayout(experimentDetailsPanel);
        experimentDetailsPanel.setLayout(experimentDetailsPanelLayout);
        experimentDetailsPanelLayout.setHorizontalGroup(
            experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                        .addComponent(infoLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(instrumentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                                .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(12, 12, 12)
                                        .addComponent(plateFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15)
                                            .addComponent(jLabel16))
                                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(assayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, experimentDetailsPanelLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(cellLineTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                                .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel14))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(durationTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(intervalTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(treatmentTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ecmCompositionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(ecmDensityTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(matrixTextField))
                        .addContainerGap(44, Short.MAX_VALUE))))
        );
        experimentDetailsPanelLayout.setVerticalGroup(
            experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(infoLabel2)
                .addGap(18, 18, 18)
                .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel22)
                            .addComponent(treatmentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cellLineTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(assayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel10)
                            .addComponent(durationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(plateFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel14)
                            .addComponent(intervalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(experimentDetailsPanelLayout.createSequentialGroup()
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(matrixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(ecmCompositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ecmDensityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))))
                .addGap(40, 40, 40)
                .addGroup(experimentDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(instrumentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.95;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(experimentDetailsPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField assayTextField;
    private javax.swing.JTextField cellLineTextField;
    private javax.swing.JButton chooseFileButton;
    private javax.swing.JPanel chooseFilePanel;
    private javax.swing.JTextField datasetTextField;
    private javax.swing.JTextField durationTextField;
    private javax.swing.JTextField ecmCompositionTextField;
    private javax.swing.JTextField ecmDensityTextField;
    private javax.swing.JTextField expNumberTextField;
    private javax.swing.JTextField expTitleTextField;
    private javax.swing.JPanel experimentDetailsPanel;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JPanel generalInfoPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel infoLabel1;
    private javax.swing.JLabel infoLabel2;
    private javax.swing.JTextField instrumentTextField;
    private javax.swing.JTextField intervalTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JCheckBox logTransformCheckBox;
    private javax.swing.JTextField matrixTextField;
    private javax.swing.JTextField plateFormatTextField;
    private javax.swing.JTextArea purposeTextArea;
    private javax.swing.JTextField treatmentTextField;
    // End of variables declaration//GEN-END:variables
}
