/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CellMissyFrame.java
 *
 * Created on Mar 27, 2012, 4:45:13 PM
 */
package be.ugent.maf.cellmissy.gui;

import be.ugent.maf.cellmissy.gui.controller.CellMissyStarter;
import java.awt.Color;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author Paola
 */
public class CellMissyFrame extends javax.swing.JFrame {

    /**
     * Creates new form CellMissyFrame
     */
    public CellMissyFrame() {
        initComponents();
        // set background to white
        this.getContentPane().setBackground(new Color(255, 255, 255));
        UIManager.put("nimbusBase", Color.lightGray);      // Base color
        UIManager.put("nimbusBlueGrey", Color.lightGray);  // BlueGrey
        UIManager.put("control", Color.white);         // Control
        UIManager.put("OptionPane.background", Color.white); // Background for option pane
        UIManager.put("info", Color.white); // Background for tooltip texts (info class)
    }

    public JPanel getBackgroundPanel() {
        return backgroundPanel;
    }

    public JMenuItem getCellMiaMenuItem() {
        return cellMiaMenuItem;
    }

    public JMenuItem getDataAnalysisMenuItem() {
        return dataAnalysisMenuItem;
    }

    public JMenuItem getProjectMenuItem() {
        return newProjectMenuItem;
    }

    public JMenuItem getAllProjectsMenuItem() {
        return allProjectsMenuItem;
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public JMenuItem getNewExperimentMenuItem() {
        return newExperimentMenuItem;
    }

    public JMenuItem getNewProjectMenuItem() {
        return newProjectMenuItem;
    }

    public JMenuItem getUserMenuItem() {
        return userMenuItem;
    }

    public JMenuItem getGenericInputMenuItem() {
        return genericInputMenuItem;
    }

    public JPanel getAnalysisExperimentParentPanel() {
        return analysisExperimentParentPanel;
    }

    public JPanel getHomePanel() {
        return homePanel;
    }

    public JPanel getLoadFromCellMiaParentPanel() {
        return loadFromCellMiaParentPanel;
    }

    public JPanel getLoadFromGenericInputParentPanel() {
        return loadFromGenericInputParentPanel;
    }

    public JPanel getSetupExperimentParentPanel() {
        return setupExperimentParentPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        homePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        setupExperimentParentPanel = new javax.swing.JPanel();
        loadFromCellMiaParentPanel = new javax.swing.JPanel();
        loadFromGenericInputParentPanel = new javax.swing.JPanel();
        analysisExperimentParentPanel = new javax.swing.JPanel();
        cellMissyMenuBar = new javax.swing.JMenuBar();
        projectMenu = new javax.swing.JMenu();
        newProjectMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        allProjectsMenuItem = new javax.swing.JMenuItem();
        experimentMenu = new javax.swing.JMenu();
        newExperimentMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        loadDataMenu = new javax.swing.JMenu();
        genericInputMenuItem = new javax.swing.JMenuItem();
        cellMiaMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        dataAnalysisMenuItem = new javax.swing.JMenuItem();
        miscMenu = new javax.swing.JMenu();
        userMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(30, 5, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(120, 300));

        backgroundPanel.setBackground(new java.awt.Color(255, 255, 255));
        backgroundPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        backgroundPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        backgroundPanel.setLayout(new java.awt.CardLayout());

        homePanel.setMinimumSize(new java.awt.Dimension(20, 20));
        homePanel.setName("homePanel"); // NOI18N
        homePanel.setOpaque(false);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        jLabel4.setText("CellMissy");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel5.setText("Cell Migration Invasion");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel6.setText("Storage System");

        javax.swing.GroupLayout homePanelLayout = new javax.swing.GroupLayout(homePanel);
        homePanel.setLayout(homePanelLayout);
        homePanelLayout.setHorizontalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(homePanelLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabel6))
                    .addGroup(homePanelLayout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jLabel4)))
                .addContainerGap())
        );
        homePanelLayout.setVerticalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        backgroundPanel.add(homePanel, "homePanel");
        homePanel.getAccessibleContext().setAccessibleName("homePanel");

        setupExperimentParentPanel.setBackground(new java.awt.Color(255, 255, 255));
        setupExperimentParentPanel.setName("setupExperimentParentPanel"); // NOI18N
        setupExperimentParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        setupExperimentParentPanel.setLayout(new java.awt.GridBagLayout());
        backgroundPanel.add(setupExperimentParentPanel, "setupExperimentParentPanel");
        setupExperimentParentPanel.getAccessibleContext().setAccessibleName("setupExperimentParentPanel");

        loadFromCellMiaParentPanel.setBackground(new java.awt.Color(255, 255, 255));
        loadFromCellMiaParentPanel.setName("loadFromCellMiaParentPanel"); // NOI18N
        loadFromCellMiaParentPanel.setLayout(new java.awt.GridBagLayout());
        backgroundPanel.add(loadFromCellMiaParentPanel, "loadFromCellMiaParentPanel");
        loadFromCellMiaParentPanel.getAccessibleContext().setAccessibleName("loadFromCellMiaParentPanel");

        loadFromGenericInputParentPanel.setBackground(new java.awt.Color(255, 255, 255));
        loadFromGenericInputParentPanel.setName("loadFromGenericInputParentPanel"); // NOI18N
        loadFromGenericInputParentPanel.setLayout(new java.awt.GridBagLayout());
        backgroundPanel.add(loadFromGenericInputParentPanel, "loadFromGenericInputParentPanel");
        loadFromGenericInputParentPanel.getAccessibleContext().setAccessibleName("loadFromGenericInputParentPanel");
        loadFromGenericInputParentPanel.getAccessibleContext().setAccessibleDescription("");

        analysisExperimentParentPanel.setBackground(new java.awt.Color(255, 255, 255));
        analysisExperimentParentPanel.setName("analysisExperimentParentPanel"); // NOI18N
        analysisExperimentParentPanel.setLayout(new java.awt.GridBagLayout());
        backgroundPanel.add(analysisExperimentParentPanel, "analysisExperimentParentPanel");
        analysisExperimentParentPanel.getAccessibleContext().setAccessibleName("analysisExperimentParentPanel");
        analysisExperimentParentPanel.getAccessibleContext().setAccessibleDescription("");

        cellMissyMenuBar.setBackground(java.awt.Color.white);
        cellMissyMenuBar.setAlignmentY(0.5F);
        cellMissyMenuBar.setMinimumSize(new java.awt.Dimension(5, 2));

        projectMenu.setText("Project");

        newProjectMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newIcon.png"))); // NOI18N
        newProjectMenuItem.setText("Create Project ...");
        newProjectMenuItem.setIconTextGap(2);
        newProjectMenuItem.setName(""); // NOI18N
        projectMenu.add(newProjectMenuItem);
        projectMenu.add(jSeparator2);

        allProjectsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/overviewIcon.png"))); // NOI18N
        allProjectsMenuItem.setText("Overview Projects ...");
        allProjectsMenuItem.setIconTextGap(2);
        projectMenu.add(allProjectsMenuItem);

        cellMissyMenuBar.add(projectMenu);

        experimentMenu.setText("Experiment");

        newExperimentMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newIcon.png"))); // NOI18N
        newExperimentMenuItem.setText("Create Experiment...");
        newExperimentMenuItem.setToolTipText("");
        newExperimentMenuItem.setIconTextGap(2);
        experimentMenu.add(newExperimentMenuItem);
        experimentMenu.add(jSeparator3);

        loadDataMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dataLoadingIcon.png"))); // NOI18N
        loadDataMenu.setText("Load Motility Data...");
        loadDataMenu.setIconTextGap(2);

        genericInputMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/genericInputIcon.png"))); // NOI18N
        genericInputMenuItem.setText("... from generic input");
        loadDataMenu.add(genericInputMenuItem);

        cellMiaMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cellmia.png"))); // NOI18N
        cellMiaMenuItem.setText("... from CELLMIA");
        loadDataMenu.add(cellMiaMenuItem);

        experimentMenu.add(loadDataMenu);
        experimentMenu.add(jSeparator4);

        dataAnalysisMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dataAnalysisIcon.png"))); // NOI18N
        dataAnalysisMenuItem.setText("Data Analysis");
        dataAnalysisMenuItem.setIconTextGap(2);
        dataAnalysisMenuItem.setInheritsPopupMenu(true);
        experimentMenu.add(dataAnalysisMenuItem);

        cellMissyMenuBar.add(experimentMenu);

        miscMenu.setText("Miscellaneous");

        userMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/userManagementIcon.png"))); // NOI18N
        userMenuItem.setText("User Management");
        userMenuItem.setIconTextGap(2);
        miscMenu.add(userMenuItem);
        miscMenu.add(jSeparator1);

        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exitApplicationIcon.png"))); // NOI18N
        exitMenuItem.setText("Quit CellMissy...");
        exitMenuItem.setIconTextGap(2);
        miscMenu.add(exitMenuItem);

        cellMissyMenuBar.add(miscMenu);

        setJMenuBar(cellMissyMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CellMissyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                CellMissyStarter cellMissyStarter = new CellMissyStarter();
                cellMissyStarter.init();
                cellMissyStarter.getApplicationContext();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem allProjectsMenuItem;
    private javax.swing.JPanel analysisExperimentParentPanel;
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JMenuItem cellMiaMenuItem;
    private javax.swing.JMenuBar cellMissyMenuBar;
    private javax.swing.JMenuItem dataAnalysisMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu experimentMenu;
    private javax.swing.JMenuItem genericInputMenuItem;
    private javax.swing.JPanel homePanel;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JMenu loadDataMenu;
    private javax.swing.JPanel loadFromCellMiaParentPanel;
    private javax.swing.JPanel loadFromGenericInputParentPanel;
    private javax.swing.JMenu miscMenu;
    private javax.swing.JMenuItem newExperimentMenuItem;
    private javax.swing.JMenuItem newProjectMenuItem;
    private javax.swing.JMenu projectMenu;
    private javax.swing.JPanel setupExperimentParentPanel;
    private javax.swing.JMenuItem userMenuItem;
    // End of variables declaration//GEN-END:variables
}
