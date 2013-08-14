/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import javax.swing.JButton;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class StartupDialog extends javax.swing.JDialog {

    public JButton getAboutButton() {
        return aboutButton;
    }

    public JButton getCellMiaButton() {
        return cellMiaButton;
    }

    public JButton getCreateExpButton() {
        return createExpButton;
    }

    public JButton getDataAnalysisButton() {
        return dataAnalysisButton;
    }

    public JButton getGenericInputButton() {
        return genericInputButton;
    }

    public JButton getOverviewButton() {
        return overviewButton;
    }

    /**
     * Creates new form StartupDialog
     */
    public StartupDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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

        jLabel4 = new javax.swing.JLabel();
        createExpButton = new javax.swing.JButton();
        dataAnalysisButton = new javax.swing.JButton();
        genericInputButton = new javax.swing.JButton();
        cellMiaButton = new javax.swing.JButton();
        overviewButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();

        jLabel4.setText("Load Data");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Getting started - CellMissy");
        setModal(true);
        setResizable(false);

        createExpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newIcon.png"))); // NOI18N
        createExpButton.setText("Create Experiment");

        dataAnalysisButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dataAnalysisIcon.png"))); // NOI18N
        dataAnalysisButton.setText("Data Analysis");

        genericInputButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/genericInputIcon.png"))); // NOI18N
        genericInputButton.setText("Generic Input");

        cellMiaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cellmia.png"))); // NOI18N
        cellMiaButton.setText("CELLMIA");

        overviewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/overviewIcon.png"))); // NOI18N
        overviewButton.setText("Overview Projects");

        aboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/informationIcon.png"))); // NOI18N
        aboutButton.setText("About CellMissy");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dataAnalysisButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(createExpButton, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(genericInputButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(overviewButton, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(cellMiaButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(aboutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {createExpButton, overviewButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createExpButton)
                    .addComponent(overviewButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genericInputButton)
                    .addComponent(cellMiaButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataAnalysisButton)
                    .addComponent(aboutButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {aboutButton, cellMiaButton, createExpButton, dataAnalysisButton, genericInputButton, overviewButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                StartupDialog dialog = new StartupDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JButton cellMiaButton;
    private javax.swing.JButton createExpButton;
    private javax.swing.JButton dataAnalysisButton;
    private javax.swing.JButton genericInputButton;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton overviewButton;
    // End of variables declaration//GEN-END:variables
}
