/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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

    public JPanel getConvexHullGraphicsParentPanel() {
        return convexHullGraphicsParentPanel;
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
        rightPanel = new javax.swing.JPanel();
        tablePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        trackDataTable = new javax.swing.JTable();
        tracksListPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tracksList = new javax.swing.JList();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        shiftedCoordinatesPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        coordinatesParentPanel = new javax.swing.JPanel();
        temporalEvolutionPanel = new javax.swing.JPanel();
        xYTCoordinatesParentPanel = new javax.swing.JPanel();
        timeSliderPanel = new javax.swing.JPanel();
        timeSlider = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        xTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        yTextField = new javax.swing.JTextField();
        convexHullPanel = new javax.swing.JPanel();
        convexHullGraphicsParentPanel = new javax.swing.JPanel();
        convexHullInfoPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        farthestPairFirstPointTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        farthestPairSecondPointTextField = new javax.swing.JTextField();

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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addContainerGap())
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.5;
        add(rightPanel, gridBagConstraints);

        tracksListPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        tracksListPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Plotted Tracks"));

        tracksList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tracksList);

        javax.swing.GroupLayout tracksListPanelLayout = new javax.swing.GroupLayout(tracksListPanel);
        tracksListPanel.setLayout(tracksListPanelLayout);
        tracksListPanelLayout.setHorizontalGroup(
            tracksListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tracksListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );
        tracksListPanelLayout.setVerticalGroup(
            tracksListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tracksListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.5;
        add(tracksListPanel, gridBagConstraints);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.RIGHT);

        shiftedCoordinatesPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        shiftedCoordinatesPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        shiftedCoordinatesPanel.setLayout(new java.awt.GridBagLayout());

        leftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Plot Settings"));
        leftPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        leftPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 197, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 297, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        shiftedCoordinatesPanel.add(leftPanel, gridBagConstraints);

        coordinatesParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        coordinatesParentPanel.setName(""); // NOI18N
        coordinatesParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        coordinatesParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1.0;
        shiftedCoordinatesPanel.add(coordinatesParentPanel, gridBagConstraints);

        jTabbedPane1.addTab("shifted (x, y)", shiftedCoordinatesPanel);

        temporalEvolutionPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        temporalEvolutionPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        temporalEvolutionPanel.setLayout(new java.awt.GridBagLayout());

        xYTCoordinatesParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        xYTCoordinatesParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        xYTCoordinatesParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 1.0;
        temporalEvolutionPanel.add(xYTCoordinatesParentPanel, gridBagConstraints);

        timeSliderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Time Frames"));
        timeSliderPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        timeSliderPanel.setName(""); // NOI18N
        timeSliderPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel5.setText("x (µm) =");

        xTextField.setEditable(false);
        xTextField.setFocusable(false);

        jLabel6.setText("y (µm) =");

        yTextField.setEditable(false);
        yTextField.setFocusable(false);

        javax.swing.GroupLayout timeSliderPanelLayout = new javax.swing.GroupLayout(timeSliderPanel);
        timeSliderPanel.setLayout(timeSliderPanelLayout);
        timeSliderPanelLayout.setHorizontalGroup(
            timeSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeSliderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timeSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timeSliderPanelLayout.createSequentialGroup()
                        .addGroup(timeSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(timeSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(timeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        timeSliderPanelLayout.setVerticalGroup(
            timeSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeSliderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(timeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(timeSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timeSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        temporalEvolutionPanel.add(timeSliderPanel, gridBagConstraints);

        jTabbedPane1.addTab("temporal evolution", temporalEvolutionPanel);

        convexHullPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        convexHullPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        convexHullPanel.setLayout(new java.awt.GridBagLayout());

        convexHullGraphicsParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        convexHullGraphicsParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        convexHullGraphicsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 1.0;
        convexHullPanel.add(convexHullGraphicsParentPanel, gridBagConstraints);

        convexHullInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Convex Hull Data"));
        convexHullInfoPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        convexHullInfoPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("point A");

        farthestPairFirstPointTextField.setEditable(false);
        farthestPairFirstPointTextField.setFocusable(false);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("point B ");

        farthestPairSecondPointTextField.setEditable(false);
        farthestPairSecondPointTextField.setFocusable(false);

        javax.swing.GroupLayout convexHullInfoPanelLayout = new javax.swing.GroupLayout(convexHullInfoPanel);
        convexHullInfoPanel.setLayout(convexHullInfoPanelLayout);
        convexHullInfoPanelLayout.setHorizontalGroup(
            convexHullInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(convexHullInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(convexHullInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(convexHullInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(farthestPairSecondPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(farthestPairFirstPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        convexHullInfoPanelLayout.setVerticalGroup(
            convexHullInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(convexHullInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(convexHullInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(farthestPairFirstPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(convexHullInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(farthestPairSecondPointTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(240, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        convexHullPanel.add(convexHullInfoPanel, gridBagConstraints);

        jTabbedPane1.addTab("convex hull", convexHullPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.5;
        add(jTabbedPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel convexHullGraphicsParentPanel;
    private javax.swing.JPanel convexHullInfoPanel;
    private javax.swing.JPanel convexHullPanel;
    private javax.swing.JPanel coordinatesParentPanel;
    private javax.swing.JTextField farthestPairFirstPointTextField;
    private javax.swing.JTextField farthestPairSecondPointTextField;
    private javax.swing.JPanel graphicsParentPanel;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel shiftedCoordinatesPanel;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JPanel temporalEvolutionPanel;
    private javax.swing.JSlider timeSlider;
    private javax.swing.JPanel timeSliderPanel;
    private javax.swing.JTable trackDataTable;
    private javax.swing.JList tracksList;
    private javax.swing.JPanel tracksListPanel;
    private javax.swing.JTextField xTextField;
    private javax.swing.JPanel xYTCoordinatesParentPanel;
    private javax.swing.JTextField yTextField;
    // End of variables declaration//GEN-END:variables
}