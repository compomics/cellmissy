/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserPanel.java
 *
 * Created on Mar 28, 2012, 5:42:21 PM
 */
package be.ugent.maf.cellmissy.gui.user;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Paola
 */
public class UserPanel extends javax.swing.JPanel {

    /** Creates new form UserPanel */
    public UserPanel() {
        initComponents();
    }

    public JButton getCreateUserButton() {
        return createUserButton;
    }

    public JTextField getCreateUserEmailTextField() {
        return createUserEmailTextField;
    }

    public JTextField getCreateUserFirstNameTextField() {
        return createUserFirstNameTextField;
    }

    public JTextField getCreateUserLastNameTextField() {
        return createUserLastNameTextField;
    }

    public JButton getDeleteUserButton() {
        return deleteUserButton;
    }

    public JTextField getDeleteUserEmailTextField() {
        return deleteUserEmailTextField;
    }

    public JTextField getDeleteUserFirstNameTextField() {
        return deleteUserFirstNameTextField;
    }

    public JTextField getDeleteUserLastNameTextField() {
        return deleteUserLastNameTextField;
    }

    public JButton getSearchUserButton() {
        return searchUserButton;
    }

    public JTextField getSearchUserFirstNameTextField() {
        return searchUserFirstNameTextField;
    }

    public JTextField getSearchUserLastNameTextField() {
        return searchUserLastNameTextField;
    }

    public JList getUserJList() {
        return userJList;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JComboBox getRoleComboBox() {
        return roleComboBox;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        createUserPanel = new javax.swing.JPanel();
        firstNameLabel = new javax.swing.JLabel();
        lastNameLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        createUserFirstNameTextField = new javax.swing.JTextField();
        createUserLastNameTextField = new javax.swing.JTextField();
        createUserEmailTextField = new javax.swing.JTextField();
        createUserButton = new javax.swing.JButton();
        roleComboBox = new javax.swing.JComboBox();
        selectRoleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        searchUserPanel = new javax.swing.JPanel();
        searchUserButton = new javax.swing.JButton();
        searchUserFirstNameTextField = new javax.swing.JTextField();
        firstNameLabel1 = new javax.swing.JLabel();
        lastNameLabel1 = new javax.swing.JLabel();
        searchUserLastNameTextField = new javax.swing.JTextField();
        deleteUserPanel = new javax.swing.JPanel();
        deleteUserButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        userJList = new javax.swing.JList();
        firstNameLabel2 = new javax.swing.JLabel();
        deleteUserFirstNameTextField = new javax.swing.JTextField();
        lastNameLabel2 = new javax.swing.JLabel();
        deleteUserLastNameTextField = new javax.swing.JTextField();
        emailLabel1 = new javax.swing.JLabel();
        deleteUserEmailTextField = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        createUserPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Create User"));
        createUserPanel.setOpaque(false);

        firstNameLabel.setText("First Name");

        lastNameLabel.setText("Last Name");

        emailLabel.setText("Email Address");

        createUserButton.setText("Create User");

        selectRoleLabel.setText("Select a Role");

        jLabel1.setText("Password");

        javax.swing.GroupLayout createUserPanelLayout = new javax.swing.GroupLayout(createUserPanel);
        createUserPanel.setLayout(createUserPanelLayout);
        createUserPanelLayout.setHorizontalGroup(
            createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createUserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createUserPanelLayout.createSequentialGroup()
                        .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(firstNameLabel)
                            .addComponent(lastNameLabel)
                            .addComponent(emailLabel))
                        .addGap(34, 34, 34)
                        .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(createUserEmailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(createUserPanelLayout.createSequentialGroup()
                                .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(createUserFirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(createUserLastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(38, 38, 38)
                                .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(selectRoleLabel)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(createUserButton))
                .addContainerGap(217, Short.MAX_VALUE))
        );

        createUserPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {createUserEmailTextField, createUserFirstNameTextField, createUserLastNameTextField});

        createUserPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, selectRoleLabel});

        createUserPanelLayout.setVerticalGroup(
            createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createUserPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createUserPanelLayout.createSequentialGroup()
                        .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createUserFirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selectRoleLabel)
                            .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(createUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(createUserPanelLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(createUserLastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(createUserEmailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(createUserPanelLayout.createSequentialGroup()
                        .addComponent(firstNameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(lastNameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(emailLabel)))
                .addGap(18, 18, 18)
                .addComponent(createUserButton)
                .addContainerGap(105, Short.MAX_VALUE))
        );

        createUserPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {createUserEmailTextField, createUserFirstNameTextField, createUserLastNameTextField});

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(createUserPanel, gridBagConstraints);

        searchUserPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search User"));
        searchUserPanel.setOpaque(false);

        searchUserButton.setText("Search User");

        firstNameLabel1.setText("First Name");

        lastNameLabel1.setText("Last Name");

        javax.swing.GroupLayout searchUserPanelLayout = new javax.swing.GroupLayout(searchUserPanel);
        searchUserPanel.setLayout(searchUserPanelLayout);
        searchUserPanelLayout.setHorizontalGroup(
            searchUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchUserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchUserPanelLayout.createSequentialGroup()
                        .addComponent(firstNameLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(searchUserFirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchUserPanelLayout.createSequentialGroup()
                        .addComponent(lastNameLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(searchUserLastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19)
                .addComponent(searchUserButton)
                .addContainerGap(374, Short.MAX_VALUE))
        );

        searchUserPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {searchUserFirstNameTextField, searchUserLastNameTextField});

        searchUserPanelLayout.setVerticalGroup(
            searchUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchUserPanelLayout.createSequentialGroup()
                .addGroup(searchUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchUserPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(searchUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(firstNameLabel1)
                            .addComponent(searchUserFirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(searchUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lastNameLabel1)
                            .addComponent(searchUserLastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(searchUserPanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(searchUserButton)))
                .addContainerGap(105, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(searchUserPanel, gridBagConstraints);

        deleteUserPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Delete User"));
        deleteUserPanel.setOpaque(false);

        deleteUserButton.setText("Delete User");

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select a user", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP));
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(45, 100));

        userJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        userJList.setVisibleRowCount(2);
        jScrollPane1.setViewportView(userJList);

        firstNameLabel2.setText("First Name");

        lastNameLabel2.setText("Last Name");

        emailLabel1.setText("Email Address");

        javax.swing.GroupLayout deleteUserPanelLayout = new javax.swing.GroupLayout(deleteUserPanel);
        deleteUserPanel.setLayout(deleteUserPanelLayout);
        deleteUserPanelLayout.setHorizontalGroup(
            deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteUserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(deleteUserPanelLayout.createSequentialGroup()
                        .addGroup(deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(firstNameLabel2)
                            .addComponent(lastNameLabel2)
                            .addComponent(emailLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(deleteUserEmailTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(deleteUserFirstNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(deleteUserLastNameTextField)))
                    .addComponent(deleteUserButton))
                .addContainerGap(333, Short.MAX_VALUE))
        );
        deleteUserPanelLayout.setVerticalGroup(
            deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteUserPanelLayout.createSequentialGroup()
                .addGroup(deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(deleteUserPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deleteUserPanelLayout.createSequentialGroup()
                                .addComponent(deleteUserFirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addGroup(deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(deleteUserLastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lastNameLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(deleteUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(deleteUserEmailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emailLabel1)))
                            .addGroup(deleteUserPanelLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(firstNameLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(deleteUserButton)))
                .addContainerGap(105, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(deleteUserPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createUserButton;
    private javax.swing.JTextField createUserEmailTextField;
    private javax.swing.JTextField createUserFirstNameTextField;
    private javax.swing.JTextField createUserLastNameTextField;
    private javax.swing.JPanel createUserPanel;
    private javax.swing.JButton deleteUserButton;
    private javax.swing.JTextField deleteUserEmailTextField;
    private javax.swing.JTextField deleteUserFirstNameTextField;
    private javax.swing.JTextField deleteUserLastNameTextField;
    private javax.swing.JPanel deleteUserPanel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel emailLabel1;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JLabel firstNameLabel1;
    private javax.swing.JLabel firstNameLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JLabel lastNameLabel1;
    private javax.swing.JLabel lastNameLabel2;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JComboBox roleComboBox;
    private javax.swing.JButton searchUserButton;
    private javax.swing.JTextField searchUserFirstNameTextField;
    private javax.swing.JTextField searchUserLastNameTextField;
    private javax.swing.JPanel searchUserPanel;
    private javax.swing.JLabel selectRoleLabel;
    private javax.swing.JList userJList;
    // End of variables declaration//GEN-END:variables
}