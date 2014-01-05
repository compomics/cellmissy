/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.user;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class UserInfoDialog extends javax.swing.JDialog {

    /**
     * Creates new form UserInfoDialog
     */
    public UserInfoDialog(java.awt.Frame parent, boolean modal) {
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
        setTitle("Info - User Management");

        infoScrollPane.setFocusable(false);
        infoScrollPane.setMinimumSize(new java.awt.Dimension(350, 370));
        infoScrollPane.setPreferredSize(new java.awt.Dimension(2966, 370));

        infoEditorPane.setEditable(false);
        infoEditorPane.setContentType("text/html"); // NOI18N
        infoEditorPane.setText("<html>\n   <head>\n      <TITLE></TITLE>\n   </head>\n   <body>\n      <a name=\"#top\"/>\n      <i>Create a new project in CellMissy...</i>\n      <hr>\n      <br>\n      <p align=\"left\">\n\t\t Selecting an user in the list will show the current projects associated with this user, and its details in the below panel. \n         </br>\n         <br>\n\t\t </br>\n\t\t <br>\n\t\t Add a new CellMissy user using the \"Add User\" button: the new user will first add to the list, and you can then edit the data of this user and finally save it to the DB (using the \"Save/Update User\" button). If a certain user is already present in the DB, and you modify some of its field, the user will be updated as well (through the same \"Save/Update User\" button).\n         </br>\n\t\t <br>\n\t\t </br>\n         <br>\n         Note that only ADMIN users can delete users from a CellMissy DB.\n         </br>\n      </p>\n   </body>\n</html>");
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
            .addComponent(infoScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            java.util.logging.Logger.getLogger(UserInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserInfoDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserInfoDialog dialog = new UserInfoDialog(new javax.swing.JFrame(), true);
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