/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.project;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class ProjectInfoDialog extends javax.swing.JDialog {

    /**
     * Creates new form ProjectInfoDialog
     */
    public ProjectInfoDialog(java.awt.Frame parent, boolean modal) {
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

        infoScrollPane = new javax.swing.JScrollPane();
        infoEditorPane = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Info - New Project ");
        setPreferredSize(new java.awt.Dimension(400, 250));

        infoScrollPane.setFocusable(false);
        infoScrollPane.setMinimumSize(new java.awt.Dimension(350, 370));
        infoScrollPane.setPreferredSize(new java.awt.Dimension(2966, 370));

        infoEditorPane.setEditable(false);
        infoEditorPane.setContentType("text/html"); // NOI18N
        infoEditorPane.setText("<html>\n   <head>\n      <TITLE></TITLE>\n   </head>\n   <body>\n      <a name=\"#top\"/>\n      <i>Create a new project in CellMissy...</i>\n      <hr>\n      <br>\n      <p align=\"left\">\n\t\t Please specify a number and a short description for the project you want to create. \n         </br>\n         <br>\n         </br>\n         <br>\n         Using the two users lists, you can specify members for the new project: just add CellMissy users to the right list. Once the project has been created, the selected members will have access and privileges to all the experiments that belong to the project.\n         </br>\n      </p>\n   </body>\n</html>");
        infoEditorPane.setFocusable(false);
        infoEditorPane.setPreferredSize(new java.awt.Dimension(2964, 370));
        infoScrollPane.setViewportView(infoEditorPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(infoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(infoScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE)
        );

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
            java.util.logging.Logger.getLogger(ProjectInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProjectInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProjectInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProjectInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProjectInfoDialog dialog = new ProjectInfoDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JEditorPane infoEditorPane;
    private javax.swing.JScrollPane infoScrollPane;
    // End of variables declaration//GEN-END:variables
}