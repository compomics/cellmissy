/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author Paola
 */
public class AnalysisPanel extends javax.swing.JPanel {

    /**
     * Creates new form ConditionsPanel
     */
    public AnalysisPanel() {
        initComponents();
    }

    public JRadioButton getCellSpeedRadioButton() {
        return cellSpeedRadioButton;
    }

    public JRadioButton getCellTracksRadioButton() {
        return cellTracksRadioButton;
    }

    public JRadioButton getStatisticsRadioButton() {
        return statisticsRadioButton;
    }

    public JPanel getTrackPlotParentPanel() {
        return trackPlotParentPanel;
    }

    public JPanel getPlotOptionsParentPanel() {
        return plotOptionsParentPanel;
    }

    public JTable getDataTable() {
        return dataTable;
    }

    public JLabel getCurrentGroupName() {
        return currentGroupName;
    }

    public JPanel getBottomPanel() {
        return bottomPanel;
    }

    public JPanel getCellSpeedsPanel() {
        return cellSpeedsPanel;
    }

    public JPanel getCellTracksPanel() {
        return cellTracksPanel;
    }

    public JPanel getSpeedBoxPlotPlotParentPanel() {
        return speedBoxPlotPlotParentPanel;
    }

    public JPanel getSpeedKDEParentPanel() {
        return speedKDEParentPanel;
    }

    public JPanel getDirectPlotParentPanel() {
        return directPlotParentPanel;
    }

    public JPanel getRosePlotParentPanel() {
        return rosePlotParentPanel;
    }

    public JButton getAddGroupButton() {
        return addGroupButton;
    }

    public JList getAnalysisGroupList() {
        return analysisGroupList;
    }

    public JTable getComparisonTable() {
        return comparisonTable;
    }

    public JList getConditionList() {
        return conditionList;
    }

    public JComboBox getCorrectionComboBox() {
        return correctionComboBox;
    }

    public JComboBox getParameterComboBox() {
        return parameterComboBox;
    }

    public JButton getPerformStatButton() {
        return performStatButton;
    }

    public JButton getRemoveGroupButton() {
        return removeGroupButton;
    }

    public JComboBox getSignLevelComboBox() {
        return signLevelComboBox;
    }

    public JTable getStatTable() {
        return statTable;
    }

    public JComboBox getStatTestComboBox() {
        return statTestComboBox;
    }

    public JTextField getGroupNameTextField() {
        return groupNameTextField;
    }

    public JPanel getStatisticsParentPanel() {
        return statisticsParentPanel;
    }

    public JRadioButton getNormalityTestsRadioButton() {
        return normalityTestsRadioButton;
    }

    public JTextField getConditionTextField() {
        return accumulatedConditionTextField;
    }

    public JPanel getConditionListPanel() {
        return conditionListPanel;
    }

    public JPanel getDataPanel() {
        return dataPanel;
    }

    public JPanel getGraphicParentPanel() {
        return graphicParentPanel;
    }

    public JPanel getInputPanel() {
        return inputPanel;
    }

    public JPanel getOtherInputPanel() {
        return otherInputPanel;
    }

    public JPanel getAccumulatedDistancePanel() {
        return AccumulatedDistancePanel;
    }

    public JPanel getEuclidianDistancePanel() {
        return EuclidianDistancePanel;
    }

    public JPanel getSpeedPanel() {
        return SpeedPanel;
    }

    public JPanel getDirectionalityPanel() {
        return DirectionalityPanel;
    }

    public JTextField getAccumulatedConditionTextField() {
        return accumulatedConditionTextField;
    }

    public JPanel getAccumulatedQQPlotPanel() {
        return accumulatedQQPlotPanel;
    }

    public JPanel getAccumulatedTestPanel() {
        return accumulatedTestPanel;
    }

    public JTextField getAndersonOutcomeTextField() {
        return andersonOutcomeADTextField;
    }

    public JTextField getAndersonOutcomeTextField1() {
        return andersonOutcomeTextField1;
    }

    public JTextField getAndersonOutcomeTextField2() {
        return andersonOutcomeTextField2;
    }

    public JTextField getAndersonOutcomeTextField3() {
        return andersonOutcomeTextField3;
    }

    public JTextField getAndersonPTextField() {
        return andersonPADTextField;
    }

    public JTextField getAndersonPTextField1() {
        return andersonPTextField1;
    }

    public JTextField getAndersonPTextField2() {
        return andersonPTextField2;
    }

    public JTextField getAndersonPTextField3() {
        return andersonPTextField3;
    }

    public JTextField getDirectionalityConditionTextField() {
        return directionalityConditionTextField;
    }

    public JPanel getDirectionalityQQPlotpanel() {
        return directionalityQQPlotpanel;
    }


    public JPanel getDirectionalityTestPanel() {
        return directionalityTestPanel;
    }

    public JTextField getEuclidianConditionTextField() {
        return euclidianConditionTextField;
    }

    public JPanel getEuclidianQQPlotPanel() {
        return euclidianQQPlotPanel;
    }

    public JPanel getEuclidianTestPanel() {
        return euclidianTestPanel;
    }

    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }

    public JScrollPane getjScrollPane2() {
        return jScrollPane2;
    }

    public JScrollPane getjScrollPane3() {
        return jScrollPane3;
    }

    public JScrollPane getjScrollPane4() {
        return jScrollPane4;
    }

    public JScrollPane getjScrollPane5() {
        return jScrollPane5;
    }

    public JTextField getKurtosisOutcomeTextField() {
        return kurtosisOutcomeADTextField;
    }

    public JTextField getKurtosisOutcomeTextField1() {
        return kurtosisOutcomeTextField1;
    }

    public JTextField getKurtosisOutcomeTextField2() {
        return kurtosisOutcomeTextField2;
    }

    public JTextField getKurtosisOutcomeTextField3() {
        return kurtosisOutcomeTextField3;
    }

    public JTextField getKurtosisPTextField() {
        return kurtosisValueADTextField;
    }

    public JTextField getKurtosisPTextField1() {
        return kurtosisPTextField1;
    }

    public JTextField getKurtosisPTextField2() {
        return kurtosisPTextField2;
    }

    public JTextField getKurtosisPTextField3() {
        return kurtosisPTextField3;
    }

    public JTabbedPane getNormalityTestParentPanel() {
        return normalityTestParentPanel;
    }

    public JTextField getShapiroOutcomeTextField() {
        return shapiroOutcomeADTextField;
    }

    public JTextField getShapiroOutcomeTextField1() {
        return shapiroOutcomeTextField1;
    }

    public JTextField getShapiroOutcomeTextField2() {
        return shapiroOutcomeTextField2;
    }

    public JTextField getShapiroOutcomeTextField3() {
        return shapiroOutcomeTextField3;
    }

    public JTextField getShapiroPTextField() {
        return shapiroPADTextField;
    }

    public JTextField getShapiroPTextField1() {
        return shapiroPTextField1;
    }

    public JTextField getShapiroPTextField2() {
        return shapiroPTextField2;
    }

    public JTextField getShapiroPTextField3() {
        return shapiroPTextField3;
    }

    public JTextField getSkewnessOutcomeTextField() {
        return skewnessOutcomeADTextField;
    }

    public JTextField getSkewnessOutcomeTextField1() {
        return skewnessOutcomeTextField1;
    }

    public JTextField getSkewnessOutcomeTextField2() {
        return skewnessOutcomeTextField2;
    }

    public JTextField getSkewnessOutcomeTextField3() {
        return skewnessOutcomeTextField3;
    }

    public JTextField getSkewnessPTextField() {
        return skewnessValueADTextField;
    }

    public JTextField getSkewnessPTextField1() {
        return skewnessPTextField1;
    }

    public JTextField getSkewnessPTextField2() {
        return skewnessPTextField2;
    }

    public JTextField getSkewnessPTextField3() {
        return skewnessPTextField3;
    }

    public JTextField getSpeedConditionTextField() {
        return speedConditionTextField;
    }

    public JPanel getSpeedQQPlotPanel() {
        return speedQQPlotPanel;
    }

    public JPanel getSpeedTestPanel() {
        return speedTestPanel;
    }

    public JPanel getStatisticsPanel() {
        return statisticsPanel;
    }

    public JTextField getAndersonPADTextField() {
        return andersonPADTextField;
    }

    public JTextField getShapiroPADTextField() {
        return shapiroPADTextField;
    }

    public JTextField getAndersonOutcomeADTextField() {
        return andersonOutcomeADTextField;
    }

    public JTextField getKurtosisOutcomeADTextField() {
        return kurtosisOutcomeADTextField;
    }

    public JTextField getShapiroOutcomeADTextField() {
        return shapiroOutcomeADTextField;
    }

    public JTextField getSkewnessOutcomeADTextField() {
        return skewnessOutcomeADTextField;
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

        radioButtonsPanel = new javax.swing.JPanel();
        cellTracksRadioButton = new javax.swing.JRadioButton();
        cellSpeedRadioButton = new javax.swing.JRadioButton();
        statisticsRadioButton = new javax.swing.JRadioButton();
        normalityTestsRadioButton = new javax.swing.JRadioButton();
        bottomPanel = new javax.swing.JPanel();
        cellTracksPanel = new javax.swing.JPanel();
        plotOptionsParentPanel = new javax.swing.JPanel();
        trackPlotParentPanel = new javax.swing.JPanel();
        cellSpeedsPanel = new javax.swing.JPanel();
        dataPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        graphicParentPanel = new javax.swing.JPanel();
        speedBoxPlotPlotParentPanel = new javax.swing.JPanel();
        speedKDEParentPanel = new javax.swing.JPanel();
        directPlotParentPanel = new javax.swing.JPanel();
        rosePlotParentPanel = new javax.swing.JPanel();
        statisticsParentPanel = new javax.swing.JPanel();
        inputPanel = new javax.swing.JPanel();
        conditionListPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        conditionList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        analysisGroupList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        addGroupButton = new javax.swing.JButton();
        removeGroupButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        groupNameTextField = new javax.swing.JTextField();
        otherInputPanel = new javax.swing.JPanel();
        statTestComboBox = new javax.swing.JComboBox();
        signLevelComboBox = new javax.swing.JComboBox();
        correctionComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        parameterComboBox = new javax.swing.JComboBox();
        performStatButton = new javax.swing.JButton();
        statisticsPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        statTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        comparisonTable = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        currentGroupName = new javax.swing.JLabel();
        normalityTestParentPanel = new javax.swing.JTabbedPane();
        AccumulatedDistancePanel = new javax.swing.JPanel();
        accumulatedTestPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        accumulatedConditionTextField = new javax.swing.JTextField();
        shapiroWilkLabel = new javax.swing.JLabel();
        andersonDarlingLabel = new javax.swing.JLabel();
        skewnessLabel = new javax.swing.JLabel();
        kurtosisLabel = new javax.swing.JLabel();
        shapiroPLabel = new javax.swing.JLabel();
        AndersonPLabel = new javax.swing.JLabel();
        skewnessValueLabel = new javax.swing.JLabel();
        KurtosisValueLabel = new javax.swing.JLabel();
        shapiroPADTextField = new javax.swing.JTextField();
        andersonPADTextField = new javax.swing.JTextField();
        skewnessValueADTextField = new javax.swing.JTextField();
        kurtosisValueADTextField = new javax.swing.JTextField();
        kurtosisOutcomeLabel = new javax.swing.JLabel();
        shapiroOutcomeLabel = new javax.swing.JLabel();
        andersonOutcomeLabel = new javax.swing.JLabel();
        skewnessOutcomeLabel = new javax.swing.JLabel();
        shapiroOutcomeADTextField = new javax.swing.JTextField();
        andersonOutcomeADTextField = new javax.swing.JTextField();
        skewnessOutcomeADTextField = new javax.swing.JTextField();
        kurtosisOutcomeADTextField = new javax.swing.JTextField();
        accumulatedQQPlotPanel = new javax.swing.JPanel();
        EuclidianDistancePanel = new javax.swing.JPanel();
        euclidianTestPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        euclidianConditionTextField = new javax.swing.JTextField();
        shapiroWilkLabel1 = new javax.swing.JLabel();
        andersonDarlingLabel1 = new javax.swing.JLabel();
        skewnessLabel1 = new javax.swing.JLabel();
        kurtosisLabel1 = new javax.swing.JLabel();
        shapiroPLabel1 = new javax.swing.JLabel();
        AndersonPLabel1 = new javax.swing.JLabel();
        skewnessValueLabel1 = new javax.swing.JLabel();
        KurtosisValueLabel1 = new javax.swing.JLabel();
        shapiroPTextField1 = new javax.swing.JTextField();
        andersonPTextField1 = new javax.swing.JTextField();
        skewnessPTextField1 = new javax.swing.JTextField();
        kurtosisPTextField1 = new javax.swing.JTextField();
        kurtosisOutcomeLabel1 = new javax.swing.JLabel();
        shapiroOutcomeLabel1 = new javax.swing.JLabel();
        andersonOutcomeLabel1 = new javax.swing.JLabel();
        skewnessOutcomeLabel1 = new javax.swing.JLabel();
        shapiroOutcomeTextField1 = new javax.swing.JTextField();
        andersonOutcomeTextField1 = new javax.swing.JTextField();
        skewnessOutcomeTextField1 = new javax.swing.JTextField();
        kurtosisOutcomeTextField1 = new javax.swing.JTextField();
        euclidianQQPlotPanel = new javax.swing.JPanel();
        DirectionalityPanel = new javax.swing.JPanel();
        directionalityTestPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        directionalityConditionTextField = new javax.swing.JTextField();
        shapiroWilkLabel3 = new javax.swing.JLabel();
        andersonDarlingLabel3 = new javax.swing.JLabel();
        skewnessLabel3 = new javax.swing.JLabel();
        kurtosisLabel3 = new javax.swing.JLabel();
        shapiroPLabel3 = new javax.swing.JLabel();
        AndersonPLabel3 = new javax.swing.JLabel();
        skewnessValueLabel3 = new javax.swing.JLabel();
        KurtosisValueLabel3 = new javax.swing.JLabel();
        shapiroPTextField3 = new javax.swing.JTextField();
        andersonPTextField3 = new javax.swing.JTextField();
        skewnessPTextField3 = new javax.swing.JTextField();
        kurtosisPTextField3 = new javax.swing.JTextField();
        kurtosisOutcomeLabel3 = new javax.swing.JLabel();
        shapiroOutcomeLabel3 = new javax.swing.JLabel();
        andersonOutcomeLabel3 = new javax.swing.JLabel();
        skewnessOutcomeLabel3 = new javax.swing.JLabel();
        shapiroOutcomeTextField3 = new javax.swing.JTextField();
        andersonOutcomeTextField3 = new javax.swing.JTextField();
        skewnessOutcomeTextField3 = new javax.swing.JTextField();
        kurtosisOutcomeTextField3 = new javax.swing.JTextField();
        directionalityQQPlotpanel = new javax.swing.JPanel();
        SpeedPanel = new javax.swing.JPanel();
        speedTestPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        speedConditionTextField = new javax.swing.JTextField();
        shapiroWilkLabel2 = new javax.swing.JLabel();
        andersonDarlingLabel2 = new javax.swing.JLabel();
        skewnessLabel2 = new javax.swing.JLabel();
        kurtosisLabel2 = new javax.swing.JLabel();
        shapiroPLabel2 = new javax.swing.JLabel();
        AndersonPLabel2 = new javax.swing.JLabel();
        skewnessValueLabel2 = new javax.swing.JLabel();
        KurtosisValueLabel2 = new javax.swing.JLabel();
        shapiroPTextField2 = new javax.swing.JTextField();
        andersonPTextField2 = new javax.swing.JTextField();
        skewnessPTextField2 = new javax.swing.JTextField();
        kurtosisPTextField2 = new javax.swing.JTextField();
        kurtosisOutcomeLabel2 = new javax.swing.JLabel();
        shapiroOutcomeLabel2 = new javax.swing.JLabel();
        andersonOutcomeLabel2 = new javax.swing.JLabel();
        skewnessOutcomeLabel2 = new javax.swing.JLabel();
        shapiroOutcomeTextField2 = new javax.swing.JTextField();
        andersonOutcomeTextField2 = new javax.swing.JTextField();
        skewnessOutcomeTextField2 = new javax.swing.JTextField();
        kurtosisOutcomeTextField2 = new javax.swing.JTextField();
        speedQQPlotPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        radioButtonsPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        cellTracksRadioButton.setText("Cell Tracks");

        cellSpeedRadioButton.setText("Cell Speeds/Angle/Directionality");

        statisticsRadioButton.setText("Statistics");

        normalityTestsRadioButton.setText("Normality Tests");
        normalityTestsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalityTestsRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout radioButtonsPanelLayout = new javax.swing.GroupLayout(radioButtonsPanel);
        radioButtonsPanel.setLayout(radioButtonsPanelLayout);
        radioButtonsPanelLayout.setHorizontalGroup(
            radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cellTracksRadioButton)
                .addGap(18, 18, 18)
                .addComponent(cellSpeedRadioButton)
                .addGap(18, 18, 18)
                .addComponent(statisticsRadioButton)
                .addGap(18, 18, 18)
                .addComponent(normalityTestsRadioButton)
                .addContainerGap(628, Short.MAX_VALUE))
        );
        radioButtonsPanelLayout.setVerticalGroup(
            radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonsPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(radioButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellTracksRadioButton)
                    .addComponent(cellSpeedRadioButton)
                    .addComponent(statisticsRadioButton)
                    .addComponent(normalityTestsRadioButton))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.06;
        add(radioButtonsPanel, gridBagConstraints);

        bottomPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        bottomPanel.setLayout(new java.awt.CardLayout());

        cellTracksPanel.setName("cellTracksPanel"); // NOI18N
        cellTracksPanel.setLayout(new java.awt.GridBagLayout());

        plotOptionsParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        plotOptionsParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        plotOptionsParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        cellTracksPanel.add(plotOptionsParentPanel, gridBagConstraints);

        trackPlotParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        trackPlotParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        trackPlotParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        cellTracksPanel.add(trackPlotParentPanel, gridBagConstraints);

        bottomPanel.add(cellTracksPanel, "cellTracksPanel");
        cellTracksPanel.getAccessibleContext().setAccessibleName("cellTracksPanel");

        cellSpeedsPanel.setName("cellSpeedsPanel"); // NOI18N
        cellSpeedsPanel.setLayout(new java.awt.GridBagLayout());

        dataPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        dataPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(dataTable);

        javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);
        dataPanelLayout.setHorizontalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1164, Short.MAX_VALUE)
                .addContainerGap())
        );
        dataPanelLayout.setVerticalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        cellSpeedsPanel.add(dataPanel, gridBagConstraints);

        graphicParentPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        graphicParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        graphicParentPanel.setLayout(new java.awt.GridBagLayout());

        speedBoxPlotPlotParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Speed BoxPlot"));
        speedBoxPlotPlotParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        speedBoxPlotPlotParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        graphicParentPanel.add(speedBoxPlotPlotParentPanel, gridBagConstraints);

        speedKDEParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Speed KDE"));
        speedKDEParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        speedKDEParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        graphicParentPanel.add(speedKDEParentPanel, gridBagConstraints);

        directPlotParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Directionality"));
        directPlotParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        directPlotParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        graphicParentPanel.add(directPlotParentPanel, gridBagConstraints);

        rosePlotParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Rose Plot"));
        rosePlotParentPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        rosePlotParentPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        graphicParentPanel.add(rosePlotParentPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        cellSpeedsPanel.add(graphicParentPanel, gridBagConstraints);

        bottomPanel.add(cellSpeedsPanel, "cellSpeedsPanel");
        cellSpeedsPanel.getAccessibleContext().setAccessibleName("cellSpeedsPanel");
        cellSpeedsPanel.getAccessibleContext().setAccessibleDescription("cellSpeedsPanel");

        statisticsParentPanel.setName("statisticsParentPanel"); // NOI18N
        statisticsParentPanel.setLayout(new java.awt.GridBagLayout());

        inputPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        inputPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        inputPanel.setLayout(new java.awt.GridBagLayout());

        conditionListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Input"));
        conditionListPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        conditionListPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jScrollPane1.setViewportView(conditionList);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Choose conditions to add to group:");

        jScrollPane3.setViewportView(analysisGroupList);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Current Analysis Groups:");

        addGroupButton.setText("Add Group >>");

        removeGroupButton.setText("<< Remove Group");

        jLabel7.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel7.setText("name for the group");

        javax.swing.GroupLayout conditionListPanelLayout = new javax.swing.GroupLayout(conditionListPanel);
        conditionListPanel.setLayout(conditionListPanelLayout);
        conditionListPanelLayout.setHorizontalGroup(
            conditionListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conditionListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conditionListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(conditionListPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(conditionListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(groupNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeGroupButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addGroupButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7)))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(conditionListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        conditionListPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addGroupButton, groupNameTextField, removeGroupButton});

        conditionListPanelLayout.setVerticalGroup(
            conditionListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, conditionListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conditionListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(conditionListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(conditionListPanelLayout.createSequentialGroup()
                        .addComponent(addGroupButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(removeGroupButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(groupNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        inputPanel.add(conditionListPanel, gridBagConstraints);

        otherInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics Option"));
        otherInputPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        otherInputPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel2.setText("Statistical test");

        jLabel3.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel3.setText("Significance level");

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel4.setText("Multiple comparisons correction");

        jLabel6.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel6.setText("Parameter to test for");

        performStatButton.setText("Perform Statistics");

        javax.swing.GroupLayout otherInputPanelLayout = new javax.swing.GroupLayout(otherInputPanel);
        otherInputPanel.setLayout(otherInputPanelLayout);
        otherInputPanelLayout.setHorizontalGroup(
            otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherInputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(correctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(signLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statTestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parameterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(performStatButton))
                .addContainerGap())
        );

        otherInputPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {correctionComboBox, parameterComboBox, performStatButton, signLevelComboBox, statTestComboBox});

        otherInputPanelLayout.setVerticalGroup(
            otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, otherInputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parameterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statTestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(signLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(otherInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(correctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(performStatButton)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        inputPanel.add(otherInputPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        statisticsParentPanel.add(inputPanel, gridBagConstraints);

        statisticsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        statisticsPanel.setName("statisticsPanel"); // NOI18N
        statisticsPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));

        statTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(statTable);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Pairwise Comparisons"));

        comparisonTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(comparisonTable);

        jLabel8.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel8.setText("Current Group Analyzed");

        javax.swing.GroupLayout statisticsPanelLayout = new javax.swing.GroupLayout(statisticsPanel);
        statisticsPanel.setLayout(statisticsPanelLayout);
        statisticsPanelLayout.setHorizontalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        statisticsPanelLayout.setVerticalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(currentGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 124, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        statisticsParentPanel.add(statisticsPanel, gridBagConstraints);
        statisticsPanel.getAccessibleContext().setAccessibleName("statisticsPanel");

        bottomPanel.add(statisticsParentPanel, "statisticsParentPanel");
        statisticsParentPanel.getAccessibleContext().setAccessibleName("statisticsParentPanel");
        statisticsParentPanel.getAccessibleContext().setAccessibleDescription("statisticsParentPanel");

        normalityTestParentPanel.setName("normalityTestParentPanel"); // NOI18N

        AccumulatedDistancePanel.setName("accumulatedDistancePanel"); // NOI18N
        AccumulatedDistancePanel.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("Condition:");

        accumulatedConditionTextField.setEditable(false);
        accumulatedConditionTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accumulatedConditionTextFieldActionPerformed(evt);
            }
        });

        shapiroWilkLabel.setText("Shapiro Wilk:");

        andersonDarlingLabel.setText("Anderson Darling:");

        skewnessLabel.setText("Skewness:");

        kurtosisLabel.setText("Kurtosis:");

        shapiroPLabel.setText("p-value:");

        AndersonPLabel.setText("p-value:");

        skewnessValueLabel.setText("value:");

        KurtosisValueLabel.setText("value:");

        shapiroPADTextField.setEditable(false);
        shapiroPADTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shapiroPADTextFieldActionPerformed(evt);
            }
        });

        andersonPADTextField.setEditable(false);
        andersonPADTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonPADTextFieldActionPerformed(evt);
            }
        });

        skewnessValueADTextField.setEditable(false);

        kurtosisValueADTextField.setEditable(false);

        kurtosisOutcomeLabel.setText("outcome:");

        shapiroOutcomeLabel.setText("outcome:");

        andersonOutcomeLabel.setText("outcome:");

        skewnessOutcomeLabel.setText("outcome:");

        shapiroOutcomeADTextField.setEditable(false);

        andersonOutcomeADTextField.setEditable(false);
        andersonOutcomeADTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonOutcomeADTextFieldActionPerformed(evt);
            }
        });

        skewnessOutcomeADTextField.setEditable(false);

        kurtosisOutcomeADTextField.setEditable(false);

        accumulatedQQPlotPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("QQPlot"));
        accumulatedQQPlotPanel.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout accumulatedTestPanelLayout = new javax.swing.GroupLayout(accumulatedTestPanel);
        accumulatedTestPanel.setLayout(accumulatedTestPanelLayout);
        accumulatedTestPanelLayout.setHorizontalGroup(
            accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accumulatedTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(accumulatedTestPanelLayout.createSequentialGroup()
                        .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(accumulatedTestPanelLayout.createSequentialGroup()
                                .addComponent(shapiroWilkLabel)
                                .addGap(43, 43, 43)
                                .addComponent(shapiroPLabel))
                            .addGroup(accumulatedTestPanelLayout.createSequentialGroup()
                                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(andersonDarlingLabel)
                                    .addComponent(skewnessLabel)
                                    .addComponent(kurtosisLabel))
                                .addGap(18, 18, 18)
                                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(KurtosisValueLabel)
                                    .addComponent(skewnessValueLabel)
                                    .addComponent(AndersonPLabel))))
                        .addGap(26, 26, 26)
                        .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(skewnessValueADTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                            .addComponent(andersonPADTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shapiroPADTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kurtosisValueADTextField))
                        .addGap(53, 53, 53)
                        .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(andersonOutcomeLabel)
                            .addComponent(shapiroOutcomeLabel)
                            .addComponent(skewnessOutcomeLabel)
                            .addComponent(kurtosisOutcomeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(skewnessOutcomeADTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .addComponent(andersonOutcomeADTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shapiroOutcomeADTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kurtosisOutcomeADTextField))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(accumulatedQQPlotPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(accumulatedTestPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(accumulatedConditionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 515, Short.MAX_VALUE))))
        );
        accumulatedTestPanelLayout.setVerticalGroup(
            accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accumulatedTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(accumulatedConditionTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shapiroWilkLabel)
                    .addComponent(shapiroPLabel)
                    .addComponent(shapiroPADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shapiroOutcomeLabel)
                    .addComponent(shapiroOutcomeADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(andersonDarlingLabel)
                    .addComponent(AndersonPLabel)
                    .addComponent(andersonPADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(andersonOutcomeLabel)
                    .addComponent(andersonOutcomeADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skewnessLabel)
                    .addComponent(skewnessValueLabel)
                    .addComponent(skewnessValueADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(skewnessOutcomeLabel)
                    .addComponent(skewnessOutcomeADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accumulatedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kurtosisLabel)
                    .addComponent(KurtosisValueLabel)
                    .addComponent(kurtosisValueADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kurtosisOutcomeLabel)
                    .addComponent(kurtosisOutcomeADTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(accumulatedQQPlotPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 136;
        gridBagConstraints.ipady = 446;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 0, 12);
        AccumulatedDistancePanel.add(accumulatedTestPanel, gridBagConstraints);

        normalityTestParentPanel.addTab("Accumulated Distance", AccumulatedDistancePanel);

        EuclidianDistancePanel.setName("euclidianDIstancePanel"); // NOI18N
        EuclidianDistancePanel.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("Condition:");

        euclidianConditionTextField.setEditable(false);
        euclidianConditionTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                euclidianConditionTextFieldActionPerformed(evt);
            }
        });

        shapiroWilkLabel1.setText("Shapiro Wilk:");

        andersonDarlingLabel1.setText("Anderson Darling:");

        skewnessLabel1.setText("Skewness:");

        kurtosisLabel1.setText("Kurtosis:");

        shapiroPLabel1.setText("p-value:");

        AndersonPLabel1.setText("p-value:");

        skewnessValueLabel1.setText("value:");

        KurtosisValueLabel1.setText("value:");

        shapiroPTextField1.setEditable(false);
        shapiroPTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shapiroPTextField1ActionPerformed(evt);
            }
        });

        andersonPTextField1.setEditable(false);
        andersonPTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonPTextField1ActionPerformed(evt);
            }
        });

        skewnessPTextField1.setEditable(false);

        kurtosisPTextField1.setEditable(false);

        kurtosisOutcomeLabel1.setText("outcome:");

        shapiroOutcomeLabel1.setText("outcome:");

        andersonOutcomeLabel1.setText("outcome:");

        skewnessOutcomeLabel1.setText("outcome:");

        shapiroOutcomeTextField1.setEditable(false);

        andersonOutcomeTextField1.setEditable(false);
        andersonOutcomeTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonOutcomeTextField1ActionPerformed(evt);
            }
        });

        skewnessOutcomeTextField1.setEditable(false);

        kurtosisOutcomeTextField1.setEditable(false);

        euclidianQQPlotPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("QQPlot"));
        euclidianQQPlotPanel.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout euclidianTestPanelLayout = new javax.swing.GroupLayout(euclidianTestPanel);
        euclidianTestPanel.setLayout(euclidianTestPanelLayout);
        euclidianTestPanelLayout.setHorizontalGroup(
            euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(euclidianTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(euclidianTestPanelLayout.createSequentialGroup()
                        .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(euclidianTestPanelLayout.createSequentialGroup()
                                .addComponent(shapiroWilkLabel1)
                                .addGap(43, 43, 43)
                                .addComponent(shapiroPLabel1))
                            .addGroup(euclidianTestPanelLayout.createSequentialGroup()
                                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(andersonDarlingLabel1)
                                    .addComponent(skewnessLabel1)
                                    .addComponent(kurtosisLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(KurtosisValueLabel1)
                                    .addComponent(skewnessValueLabel1)
                                    .addComponent(AndersonPLabel1))))
                        .addGap(26, 26, 26)
                        .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(andersonPTextField1)
                            .addComponent(skewnessPTextField1)
                            .addComponent(kurtosisPTextField1)
                            .addComponent(shapiroPTextField1))
                        .addGap(57, 57, 57)
                        .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shapiroOutcomeLabel1)
                            .addComponent(kurtosisOutcomeLabel1)
                            .addComponent(skewnessOutcomeLabel1)
                            .addComponent(andersonOutcomeLabel1))
                        .addGap(31, 31, 31)
                        .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(andersonOutcomeTextField1)
                            .addComponent(shapiroOutcomeTextField1)
                            .addComponent(kurtosisOutcomeTextField1)
                            .addComponent(skewnessOutcomeTextField1))
                        .addGap(285, 285, 285))
                    .addGroup(euclidianTestPanelLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(euclidianConditionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(euclidianTestPanelLayout.createSequentialGroup()
                .addComponent(euclidianQQPlotPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 895, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 199, Short.MAX_VALUE))
        );
        euclidianTestPanelLayout.setVerticalGroup(
            euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(euclidianTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(euclidianConditionTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shapiroWilkLabel1)
                    .addComponent(shapiroPLabel1)
                    .addComponent(shapiroPTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shapiroOutcomeLabel1)
                    .addComponent(shapiroOutcomeTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(andersonDarlingLabel1)
                    .addComponent(AndersonPLabel1)
                    .addComponent(andersonPTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(andersonOutcomeLabel1)
                    .addComponent(andersonOutcomeTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skewnessLabel1)
                    .addComponent(skewnessValueLabel1)
                    .addComponent(skewnessPTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(skewnessOutcomeLabel1)
                    .addComponent(skewnessOutcomeTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(euclidianTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kurtosisLabel1)
                    .addComponent(KurtosisValueLabel1)
                    .addComponent(kurtosisPTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kurtosisOutcomeLabel1)
                    .addComponent(kurtosisOutcomeTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(euclidianQQPlotPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 199;
        gridBagConstraints.ipady = 286;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 173, 48);
        EuclidianDistancePanel.add(euclidianTestPanel, gridBagConstraints);

        normalityTestParentPanel.addTab("Euclidian Distance", EuclidianDistancePanel);

        DirectionalityPanel.setName("directionalityPanel"); // NOI18N
        DirectionalityPanel.setLayout(new java.awt.GridBagLayout());

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel12.setText("Condition:");

        directionalityConditionTextField.setEditable(false);
        directionalityConditionTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directionalityConditionTextFieldActionPerformed(evt);
            }
        });

        shapiroWilkLabel3.setText("Shapiro Wilk:");

        andersonDarlingLabel3.setText("Anderson Darling:");

        skewnessLabel3.setText("Skewness:");

        kurtosisLabel3.setText("Kurtosis:");

        shapiroPLabel3.setText("p-value:");

        AndersonPLabel3.setText("p-value:");

        skewnessValueLabel3.setText("value:");

        KurtosisValueLabel3.setText("value:");

        shapiroPTextField3.setEditable(false);
        shapiroPTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shapiroPTextField3ActionPerformed(evt);
            }
        });

        andersonPTextField3.setEditable(false);
        andersonPTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonPTextField3ActionPerformed(evt);
            }
        });

        skewnessPTextField3.setEditable(false);

        kurtosisPTextField3.setEditable(false);

        kurtosisOutcomeLabel3.setText("outcome:");

        shapiroOutcomeLabel3.setText("outcome:");

        andersonOutcomeLabel3.setText("outcome:");

        skewnessOutcomeLabel3.setText("outcome:");

        shapiroOutcomeTextField3.setEditable(false);

        andersonOutcomeTextField3.setEditable(false);
        andersonOutcomeTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonOutcomeTextField3ActionPerformed(evt);
            }
        });

        skewnessOutcomeTextField3.setEditable(false);

        kurtosisOutcomeTextField3.setEditable(false);

        directionalityQQPlotpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("QQPlot"));
        directionalityQQPlotpanel.setName("directionalityQQPlotpanel"); // NOI18N
        directionalityQQPlotpanel.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout directionalityTestPanelLayout = new javax.swing.GroupLayout(directionalityTestPanel);
        directionalityTestPanel.setLayout(directionalityTestPanelLayout);
        directionalityTestPanelLayout.setHorizontalGroup(
            directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(directionalityTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(directionalityTestPanelLayout.createSequentialGroup()
                        .addComponent(directionalityQQPlotpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(directionalityTestPanelLayout.createSequentialGroup()
                        .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(directionalityTestPanelLayout.createSequentialGroup()
                                .addComponent(shapiroWilkLabel3)
                                .addGap(43, 43, 43)
                                .addComponent(shapiroPLabel3))
                            .addGroup(directionalityTestPanelLayout.createSequentialGroup()
                                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(andersonDarlingLabel3)
                                    .addComponent(skewnessLabel3)
                                    .addComponent(kurtosisLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(KurtosisValueLabel3)
                                    .addComponent(skewnessValueLabel3)
                                    .addComponent(AndersonPLabel3))))
                        .addGap(26, 26, 26)
                        .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(andersonPTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                            .addComponent(skewnessPTextField3)
                            .addComponent(kurtosisPTextField3)
                            .addComponent(shapiroPTextField3))
                        .addGap(57, 57, 57)
                        .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shapiroOutcomeLabel3)
                            .addComponent(kurtosisOutcomeLabel3)
                            .addComponent(skewnessOutcomeLabel3)
                            .addComponent(andersonOutcomeLabel3))
                        .addGap(31, 31, 31)
                        .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(andersonOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(skewnessOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(kurtosisOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(shapiroOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(285, 285, 285))
                    .addGroup(directionalityTestPanelLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(directionalityConditionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        directionalityTestPanelLayout.setVerticalGroup(
            directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(directionalityTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(directionalityConditionTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shapiroWilkLabel3)
                    .addComponent(shapiroPLabel3)
                    .addComponent(shapiroPTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shapiroOutcomeLabel3)
                    .addComponent(shapiroOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(andersonDarlingLabel3)
                    .addComponent(AndersonPLabel3)
                    .addComponent(andersonPTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(andersonOutcomeLabel3)
                    .addComponent(andersonOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skewnessLabel3)
                    .addComponent(skewnessValueLabel3)
                    .addComponent(skewnessPTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(skewnessOutcomeLabel3)
                    .addComponent(skewnessOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(directionalityTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kurtosisLabel3)
                    .addComponent(KurtosisValueLabel3)
                    .addComponent(kurtosisPTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kurtosisOutcomeLabel3)
                    .addComponent(kurtosisOutcomeTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(directionalityQQPlotpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap(282, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 451;
        gridBagConstraints.ipady = 538;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 0, 12);
        DirectionalityPanel.add(directionalityTestPanel, gridBagConstraints);

        normalityTestParentPanel.addTab("Directionality", DirectionalityPanel);

        SpeedPanel.setName("speedPanel"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setText("Condition:");

        speedConditionTextField.setEditable(false);
        speedConditionTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speedConditionTextFieldActionPerformed(evt);
            }
        });

        shapiroWilkLabel2.setText("Shapiro Wilk:");

        andersonDarlingLabel2.setText("Anderson Darling:");

        skewnessLabel2.setText("Skewness:");

        kurtosisLabel2.setText("Kurtosis:");

        shapiroPLabel2.setText("p-value:");

        AndersonPLabel2.setText("p-value:");

        skewnessValueLabel2.setText("value:");

        KurtosisValueLabel2.setText("value:");

        shapiroPTextField2.setEditable(false);
        shapiroPTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shapiroPTextField2ActionPerformed(evt);
            }
        });

        andersonPTextField2.setEditable(false);
        andersonPTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonPTextField2ActionPerformed(evt);
            }
        });

        skewnessPTextField2.setEditable(false);

        kurtosisPTextField2.setEditable(false);

        kurtosisOutcomeLabel2.setText("outcome:");

        shapiroOutcomeLabel2.setText("outcome:");

        andersonOutcomeLabel2.setText("outcome:");

        skewnessOutcomeLabel2.setText("outcome:");

        shapiroOutcomeTextField2.setEditable(false);

        andersonOutcomeTextField2.setEditable(false);
        andersonOutcomeTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andersonOutcomeTextField2ActionPerformed(evt);
            }
        });

        skewnessOutcomeTextField2.setEditable(false);

        kurtosisOutcomeTextField2.setEditable(false);

        speedQQPlotPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("QQPlot"));
        speedQQPlotPanel.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout speedTestPanelLayout = new javax.swing.GroupLayout(speedTestPanel);
        speedTestPanel.setLayout(speedTestPanelLayout);
        speedTestPanelLayout.setHorizontalGroup(
            speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(speedTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(speedTestPanelLayout.createSequentialGroup()
                        .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(speedTestPanelLayout.createSequentialGroup()
                                .addComponent(shapiroWilkLabel2)
                                .addGap(43, 43, 43)
                                .addComponent(shapiroPLabel2))
                            .addGroup(speedTestPanelLayout.createSequentialGroup()
                                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(andersonDarlingLabel2)
                                    .addComponent(skewnessLabel2)
                                    .addComponent(kurtosisLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(KurtosisValueLabel2)
                                    .addComponent(skewnessValueLabel2)
                                    .addComponent(AndersonPLabel2))))
                        .addGap(26, 26, 26)
                        .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(andersonPTextField2)
                            .addComponent(skewnessPTextField2)
                            .addComponent(kurtosisPTextField2)
                            .addComponent(shapiroPTextField2))
                        .addGap(57, 57, 57)
                        .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shapiroOutcomeLabel2)
                            .addComponent(kurtosisOutcomeLabel2)
                            .addComponent(skewnessOutcomeLabel2)
                            .addComponent(andersonOutcomeLabel2))
                        .addGap(31, 31, 31)
                        .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(skewnessOutcomeTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addComponent(andersonOutcomeTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shapiroOutcomeTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kurtosisOutcomeTextField2))
                        .addContainerGap(598, Short.MAX_VALUE))
                    .addGroup(speedTestPanelLayout.createSequentialGroup()
                        .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(speedQQPlotPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(speedTestPanelLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(speedConditionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(29, Short.MAX_VALUE))))
        );
        speedTestPanelLayout.setVerticalGroup(
            speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(speedTestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(speedConditionTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shapiroWilkLabel2)
                    .addComponent(shapiroPLabel2)
                    .addComponent(shapiroPTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shapiroOutcomeLabel2)
                    .addComponent(shapiroOutcomeTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(andersonDarlingLabel2)
                    .addComponent(AndersonPLabel2)
                    .addComponent(andersonPTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(andersonOutcomeLabel2)
                    .addComponent(andersonOutcomeTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skewnessLabel2)
                    .addComponent(skewnessValueLabel2)
                    .addComponent(skewnessPTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(skewnessOutcomeLabel2)
                    .addComponent(skewnessOutcomeTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(speedTestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kurtosisLabel2)
                    .addComponent(KurtosisValueLabel2)
                    .addComponent(kurtosisPTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kurtosisOutcomeLabel2)
                    .addComponent(kurtosisOutcomeTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(speedQQPlotPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout SpeedPanelLayout = new javax.swing.GroupLayout(SpeedPanel);
        SpeedPanel.setLayout(SpeedPanelLayout);
        SpeedPanelLayout.setHorizontalGroup(
            SpeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SpeedPanelLayout.createSequentialGroup()
                .addComponent(speedTestPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        SpeedPanelLayout.setVerticalGroup(
            SpeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SpeedPanelLayout.createSequentialGroup()
                .addComponent(speedTestPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        normalityTestParentPanel.addTab("Speed", SpeedPanel);

        bottomPanel.add(normalityTestParentPanel, "normalityTestParentPanel");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.94;
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void normalityTestsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalityTestsRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_normalityTestsRadioButtonActionPerformed

    private void accumulatedConditionTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accumulatedConditionTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_accumulatedConditionTextFieldActionPerformed

    private void shapiroPADTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shapiroPADTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_shapiroPADTextFieldActionPerformed

    private void andersonPADTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonPADTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonPADTextFieldActionPerformed

    private void andersonOutcomeADTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonOutcomeADTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonOutcomeADTextFieldActionPerformed

    private void euclidianConditionTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_euclidianConditionTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_euclidianConditionTextFieldActionPerformed

    private void shapiroPTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shapiroPTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_shapiroPTextField1ActionPerformed

    private void andersonPTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonPTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonPTextField1ActionPerformed

    private void andersonOutcomeTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonOutcomeTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonOutcomeTextField1ActionPerformed

    private void speedConditionTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speedConditionTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_speedConditionTextFieldActionPerformed

    private void shapiroPTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shapiroPTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_shapiroPTextField2ActionPerformed

    private void andersonPTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonPTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonPTextField2ActionPerformed

    private void andersonOutcomeTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonOutcomeTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonOutcomeTextField2ActionPerformed

    private void andersonOutcomeTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonOutcomeTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonOutcomeTextField3ActionPerformed

    private void andersonPTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andersonPTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_andersonPTextField3ActionPerformed

    private void shapiroPTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shapiroPTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_shapiroPTextField3ActionPerformed

    private void directionalityConditionTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directionalityConditionTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_directionalityConditionTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccumulatedDistancePanel;
    private javax.swing.JLabel AndersonPLabel;
    private javax.swing.JLabel AndersonPLabel1;
    private javax.swing.JLabel AndersonPLabel2;
    private javax.swing.JLabel AndersonPLabel3;
    private javax.swing.JPanel DirectionalityPanel;
    private javax.swing.JPanel EuclidianDistancePanel;
    private javax.swing.JLabel KurtosisValueLabel;
    private javax.swing.JLabel KurtosisValueLabel1;
    private javax.swing.JLabel KurtosisValueLabel2;
    private javax.swing.JLabel KurtosisValueLabel3;
    private javax.swing.JPanel SpeedPanel;
    private javax.swing.JTextField accumulatedConditionTextField;
    private javax.swing.JPanel accumulatedQQPlotPanel;
    private javax.swing.JPanel accumulatedTestPanel;
    private javax.swing.JButton addGroupButton;
    private javax.swing.JList analysisGroupList;
    private javax.swing.JLabel andersonDarlingLabel;
    private javax.swing.JLabel andersonDarlingLabel1;
    private javax.swing.JLabel andersonDarlingLabel2;
    private javax.swing.JLabel andersonDarlingLabel3;
    private javax.swing.JTextField andersonOutcomeADTextField;
    private javax.swing.JLabel andersonOutcomeLabel;
    private javax.swing.JLabel andersonOutcomeLabel1;
    private javax.swing.JLabel andersonOutcomeLabel2;
    private javax.swing.JLabel andersonOutcomeLabel3;
    private javax.swing.JTextField andersonOutcomeTextField1;
    private javax.swing.JTextField andersonOutcomeTextField2;
    private javax.swing.JTextField andersonOutcomeTextField3;
    private javax.swing.JTextField andersonPADTextField;
    private javax.swing.JTextField andersonPTextField1;
    private javax.swing.JTextField andersonPTextField2;
    private javax.swing.JTextField andersonPTextField3;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JRadioButton cellSpeedRadioButton;
    private javax.swing.JPanel cellSpeedsPanel;
    private javax.swing.JPanel cellTracksPanel;
    private javax.swing.JRadioButton cellTracksRadioButton;
    private javax.swing.JTable comparisonTable;
    private javax.swing.JList conditionList;
    private javax.swing.JPanel conditionListPanel;
    private javax.swing.JComboBox correctionComboBox;
    private javax.swing.JLabel currentGroupName;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JPanel directPlotParentPanel;
    private javax.swing.JTextField directionalityConditionTextField;
    private javax.swing.JPanel directionalityQQPlotpanel;
    private javax.swing.JPanel directionalityTestPanel;
    private javax.swing.JTextField euclidianConditionTextField;
    private javax.swing.JPanel euclidianQQPlotPanel;
    private javax.swing.JPanel euclidianTestPanel;
    private javax.swing.JPanel graphicParentPanel;
    private javax.swing.JTextField groupNameTextField;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel kurtosisLabel;
    private javax.swing.JLabel kurtosisLabel1;
    private javax.swing.JLabel kurtosisLabel2;
    private javax.swing.JLabel kurtosisLabel3;
    private javax.swing.JTextField kurtosisOutcomeADTextField;
    private javax.swing.JLabel kurtosisOutcomeLabel;
    private javax.swing.JLabel kurtosisOutcomeLabel1;
    private javax.swing.JLabel kurtosisOutcomeLabel2;
    private javax.swing.JLabel kurtosisOutcomeLabel3;
    private javax.swing.JTextField kurtosisOutcomeTextField1;
    private javax.swing.JTextField kurtosisOutcomeTextField2;
    private javax.swing.JTextField kurtosisOutcomeTextField3;
    private javax.swing.JTextField kurtosisPTextField1;
    private javax.swing.JTextField kurtosisPTextField2;
    private javax.swing.JTextField kurtosisPTextField3;
    private javax.swing.JTextField kurtosisValueADTextField;
    private javax.swing.JTabbedPane normalityTestParentPanel;
    private javax.swing.JRadioButton normalityTestsRadioButton;
    private javax.swing.JPanel otherInputPanel;
    private javax.swing.JComboBox parameterComboBox;
    private javax.swing.JButton performStatButton;
    private javax.swing.JPanel plotOptionsParentPanel;
    private javax.swing.JPanel radioButtonsPanel;
    private javax.swing.JButton removeGroupButton;
    private javax.swing.JPanel rosePlotParentPanel;
    private javax.swing.JTextField shapiroOutcomeADTextField;
    private javax.swing.JLabel shapiroOutcomeLabel;
    private javax.swing.JLabel shapiroOutcomeLabel1;
    private javax.swing.JLabel shapiroOutcomeLabel2;
    private javax.swing.JLabel shapiroOutcomeLabel3;
    private javax.swing.JTextField shapiroOutcomeTextField1;
    private javax.swing.JTextField shapiroOutcomeTextField2;
    private javax.swing.JTextField shapiroOutcomeTextField3;
    private javax.swing.JTextField shapiroPADTextField;
    private javax.swing.JLabel shapiroPLabel;
    private javax.swing.JLabel shapiroPLabel1;
    private javax.swing.JLabel shapiroPLabel2;
    private javax.swing.JLabel shapiroPLabel3;
    private javax.swing.JTextField shapiroPTextField1;
    private javax.swing.JTextField shapiroPTextField2;
    private javax.swing.JTextField shapiroPTextField3;
    private javax.swing.JLabel shapiroWilkLabel;
    private javax.swing.JLabel shapiroWilkLabel1;
    private javax.swing.JLabel shapiroWilkLabel2;
    private javax.swing.JLabel shapiroWilkLabel3;
    private javax.swing.JComboBox signLevelComboBox;
    private javax.swing.JLabel skewnessLabel;
    private javax.swing.JLabel skewnessLabel1;
    private javax.swing.JLabel skewnessLabel2;
    private javax.swing.JLabel skewnessLabel3;
    private javax.swing.JTextField skewnessOutcomeADTextField;
    private javax.swing.JLabel skewnessOutcomeLabel;
    private javax.swing.JLabel skewnessOutcomeLabel1;
    private javax.swing.JLabel skewnessOutcomeLabel2;
    private javax.swing.JLabel skewnessOutcomeLabel3;
    private javax.swing.JTextField skewnessOutcomeTextField1;
    private javax.swing.JTextField skewnessOutcomeTextField2;
    private javax.swing.JTextField skewnessOutcomeTextField3;
    private javax.swing.JTextField skewnessPTextField1;
    private javax.swing.JTextField skewnessPTextField2;
    private javax.swing.JTextField skewnessPTextField3;
    private javax.swing.JTextField skewnessValueADTextField;
    private javax.swing.JLabel skewnessValueLabel;
    private javax.swing.JLabel skewnessValueLabel1;
    private javax.swing.JLabel skewnessValueLabel2;
    private javax.swing.JLabel skewnessValueLabel3;
    private javax.swing.JPanel speedBoxPlotPlotParentPanel;
    private javax.swing.JTextField speedConditionTextField;
    private javax.swing.JPanel speedKDEParentPanel;
    private javax.swing.JPanel speedQQPlotPanel;
    private javax.swing.JPanel speedTestPanel;
    private javax.swing.JTable statTable;
    private javax.swing.JComboBox statTestComboBox;
    private javax.swing.JPanel statisticsPanel;
    private javax.swing.JPanel statisticsParentPanel;
    private javax.swing.JRadioButton statisticsRadioButton;
    private javax.swing.JPanel trackPlotParentPanel;
    // End of variables declaration//GEN-END:variables

    
}
