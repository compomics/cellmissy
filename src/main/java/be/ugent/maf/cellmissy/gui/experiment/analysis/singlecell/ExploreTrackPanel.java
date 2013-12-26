/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class ExploreTrackPanel extends javax.swing.JPanel {

    public JPanel getGraphicsParentPanel() {
        return graphicsParentPanel;
    }

    public JPanel getCoordinatesParentPanel() {
        return coordinatesParentPanel;
    }

    public JSlider getTimeSlider() {
        return timeSlider;
    }

    public JList getTracksList() {
        return tracksList;
    }

    public JTextField getxTextField() {
        return xTextField;
    }

    public JPanel getxYTCoordinatesParentPanel() {
        return xYTCoordinatesParentPanel;
    }

    public JTextField getyTextField() {
        return yTextField;
    }

    public JTable getTrackDataTable() {
        return trackDataTable;
    }

    public JTextField getFarthestPairFirstPointTextField() {
        return farthestPairFirstPointTextField;
    }

    public JTextField getFarthestPairSecondPointTextField() {
        return farthestPairSecondPointTextField;
    }

    public JPanel getConvexHullParentPanel() {
        return convexHullParentPanel;
    }

    /**
     * Creates new form ExploreTrackPanel
     */
    public ExploreTrackPanel() {
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

        graphicsParentPanel = new javax.swing.JPanel();
        xYTCoordinatesParentPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();
        tablePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        trackDataTable = new javax.swing.JTable();
        timeEvolutionPanel = new javax.swing.JPanel();
        yTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        timeSlider = new javax.swing.JSlider();
        xTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        farthestPairFirstPointTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        farthestPairSecondPointTextField = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        coordinatesParentPanel = new javax.swing.JPanel();
        convexHullPanel = new javax.swing.JPanel();
        convexHullParentPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tracksList = new javax.swing.JList();

        setPreferredSize(new java.awt.Dimension(400, 600));
        setLayout(new java.awt.GridBagLayout());

        graphicsParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        graphicsParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        graphicsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.5;
        add(graphicsParentPanel, gridBagConstraints);

        xYTCoordinatesParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("x(t), y(t)"));
        xYTCoordinatesParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        xYTCoordinatesParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        xYTCoordinatesParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.5;
        add(xYTCoordinatesParentPanel, gridBagConstraints);

        rightPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        rightPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Track Data"));
        tablePanel.setMinimumSize(new java.awt.Dimension(20, 20));
        tablePanel.setPreferredSize(new java.awt.Dimension(20, 20));

        trackDataTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(trackDataTable);

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addContainerGap())
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        timeEvolutionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Temporal Evolution"));

        yTextField.setEditable(false);
        yTextField.setFocusable(false);

        jLabel5.setText("x (µm) =");

        xTextField.setEditable(false);
        xTextField.setFocusable(false);

        jLabel6.setText("y (µm) =");

        jLabel7.setText("Time Frames for Selected Track");

        farthestPairFirstPointTextField.setEditable(false);
        farthestPairFirstPointTextField.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("point A");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("point B ");

        farthestPairSecondPointTextField.setEditable(false);
        farthestPairSecondPointTextField.setFocusable(false);

        javax.swing.GroupLayout timeEvolutionPanelLayout = new javax.swing.GroupLayout(timeEvolutionPanel);
        timeEvolutionPanel.setLayout(timeEvolutionPanelLayout);
        timeEvolutionPanelLayout.setHorizontalGroup(
            timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeEvolutionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(timeEvolutionPanelLayout.createSequentialGroup()
                            .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addComponent(jLabel5))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(timeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, timeEvolutionPanelLayout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addGap(24, 24, 24)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, timeEvolutionPanelLayout.createSequentialGroup()
                        .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(farthestPairSecondPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(farthestPairFirstPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        timeEvolutionPanelLayout.setVerticalGroup(
            timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeEvolutionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(farthestPairFirstPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timeEvolutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(farthestPairSecondPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(149, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(timeEvolutionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timeEvolutionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.5;
        add(rightPanel, gridBagConstraints);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        coordinatesParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        coordinatesParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        coordinatesParentPanel.setLayout(new java.awt.GridBagLayout());
        jTabbedPane1.addTab("shifted (x, y)", coordinatesParentPanel);

        convexHullPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        convexHullPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        convexHullPanel.setLayout(new java.awt.GridBagLayout());

        convexHullParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        convexHullParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        convexHullParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        convexHullPanel.add(convexHullParentPanel, gridBagConstraints);

        jTabbedPane1.addTab("convex hull", convexHullPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.5;
        add(jTabbedPane1, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(20, 20));
        jPanel1.setPreferredSize(new java.awt.Dimension(20, 20));

        jScrollPane1.setBorder(null);

        tracksList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tracksList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.5;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel convexHullPanel;
    private javax.swing.JPanel convexHullParentPanel;
    private javax.swing.JPanel coordinatesParentPanel;
    private javax.swing.JTextField farthestPairFirstPointTextField;
    private javax.swing.JTextField farthestPairSecondPointTextField;
    private javax.swing.JPanel graphicsParentPanel;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JPanel timeEvolutionPanel;
    private javax.swing.JSlider timeSlider;
    private javax.swing.JTable trackDataTable;
    private javax.swing.JList tracksList;
    private javax.swing.JTextField xTextField;
    private javax.swing.JPanel xYTCoordinatesParentPanel;
    private javax.swing.JTextField yTextField;
    // End of variables declaration//GEN-END:variables
}
