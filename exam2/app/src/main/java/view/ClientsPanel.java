package view;

import dao.ClientDAO;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import model.Client;
import util.FieldChecker;
import util.Message;

public class ClientsPanel extends javax.swing.JPanel {

    private ClientDAO clientDAO;
    private final ArrayList<JTextComponent> txtFields = new ArrayList<>();

    public ClientsPanel() {
        clientDAO = ClientDAO.getInstance();
        txtFields.add(txtId);
        txtFields.add(txtName);
        txtFields.add(txtEmail);

        initComponents();
        initTblClients();
        initListeners();

        tglFilter.setEnabled(false);
        setVisible(true);
    }

    private void initTblClients() {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "id", "name", "email" }, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        tblClients.setModel(tableModel);
        updateTable();
    }

    private void initListeners() {
        tblClients.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateFieldsWithTable();
                }
            }
        );
        btnAdd.addActionListener(this::addClient);
        btnUpdate.addActionListener(this::updateClient);
        btnDelete.addActionListener(this::deleteClient);
        btnSearch.addActionListener(this::searchClient);
        btnClearFields.addActionListener(this::clearFields);
        tglFilter.addChangeListener(this::toggleFilter);
        txtFilter.addKeyListener(
            new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent evt) {
                    checkFilterState();
                }
            }
        );
    }

    private void updateFieldsWithTable() {
        final int ROW = tblClients.getSelectedRow();
        if (ROW == -1) {
            return;
        }
        txtId.setText(String.valueOf(tblClients.getValueAt(ROW, 0)));
        txtName.setText(String.valueOf(tblClients.getValueAt(ROW, 1)));
        txtEmail.setText(String.valueOf(tblClients.getValueAt(ROW, 2)));
    }

    private void updateFields() {
        try {
            ResultSet rs = clientDAO.findRS(getId());
            rs.next();
            txtId.setText(rs.getString(1));
            txtName.setText(rs.getString(2));
            txtEmail.setText(rs.getString(3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addClient(ActionEvent evt) {
        try {
            checkEmptyFields();

            FieldChecker.checkExistence(clientDAO.findRS(getId()));
            clientDAO.add(getClient());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void deleteClient(ActionEvent evt) {
        try {
            FieldChecker.checkNonExistence(clientDAO.findRS(getId()));
            clientDAO.delete(getId());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void updateClient(ActionEvent evt) {
        try {
            checkEmptyFields();

            FieldChecker.checkNonExistence(clientDAO.findRS(getId()));
            clientDAO.updateRS(getClient());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }



    private void searchClient(ActionEvent evt) {
        if (getId().isEmpty()) {
            return;
        }
        try {
            FieldChecker.checkNonExistence(clientDAO.findRS(getId()));
            updateFields();
        } catch (Exception e) {
            String auxIdText = getId();
            clearFields(null);
            txtEmail.setText(auxIdText);
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void clearFields(ActionEvent evt) {
        for (JTextComponent field : txtFields) {
            field.setText("");
        }
    }

    private void toggleFilter(ChangeEvent evt) {
        if (tglFilter.isSelected()) {
            try {
                updateTable();
            } catch (Exception e) {
                Message.showErrorMessage(this, e.getMessage());
            }
            tglFilter.setIcon(new ImageIcon(getClass().getResource("/clear_filter.png")));
        } else {
            updateTable();
            tglFilter.setIcon(new ImageIcon(getClass().getResource("/filter.png")));
        }
    }

    private void checkFilterState() {
        tglFilter.setEnabled(!getFilter().isEmpty());
        tglFilter.setSelected(false);
    }

    private void updateTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblClients.getModel();
        tableModel.setRowCount(0);
        try {
            if (tglFilter.isSelected()) {
                ResultSet rs = clientDAO.filterRS("name, email", getFilter());
                updateTable(rs);
            } else {
                updateTable(clientDAO.allRS());
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void checkEmptyFields() {
        for (JTextComponent field : txtFields) {
            if (field.getText().isEmpty()) {
                throw new IllegalArgumentException("Empty fields are not allowed");
            }
        }
    }

    private void updateTable(ResultSet client) {
        DefaultTableModel tableModel = (DefaultTableModel) tblClients.getModel();
        tableModel.setRowCount(0);
        try {
            while (client.next()) {
                tableModel.addRow(new Object[] { client.getString(1), client.getString(2), client.getString(3) });
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private Client getClient() {
        return new Client(getId(), getClientName(), getEmail());
    }

    private String getId() {
        return txtId.getText().trim();
    }

    private String getClientName() {
        return txtName.getText().trim();
    }

    private String getEmail() {
        return txtEmail.getText().trim();
    }

    private String getFilter() {
        return txtFilter.getText().trim();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlProps = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbl6 = new javax.swing.JLabel();

        lbl1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl1.setText("id");

        txtId.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/search.png"))); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl2.setText("name");

        txtName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl3.setText("email");

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        jPanel1.setLayout(new java.awt.GridLayout(0, 2, 10, 10));

        btnAdd.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add.png"))); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnAdd);

        btnUpdate.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/update.png"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnUpdate);

        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnDelete);

        btnClearFields.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnClearFields.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clear.png"))); // NOI18N
        btnClearFields.setText("Clear Fields");
        btnClearFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearFields.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnClearFields);

        javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(pnlProps);
        pnlProps.setLayout(pnlPropsLayout);
        pnlPropsLayout.setHorizontalGroup(
            pnlPropsLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txtName)
                .addComponent(txtEmail)
                .addGroup(
                    pnlPropsLayout
                        .createSequentialGroup()
                        .addGroup(
                            pnlPropsLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbl1)
                                .addComponent(lbl2)
                                .addComponent(lbl3)
                                .addComponent(txtId)
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch)
                )
                .addComponent(
                    jPanel1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE
                )
        );
        pnlPropsLayout.setVerticalGroup(
            pnlPropsLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    pnlPropsLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            pnlPropsLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(
                                    txtId,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(btnSearch)
                        )
                        .addGap(18, 18, 18)
                        .addComponent(lbl2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtName,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18, 18, 18)
                        .addComponent(lbl3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtEmail,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18, 18, 18)
                        .addComponent(
                            jPanel1,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            80,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addContainerGap(232, Short.MAX_VALUE)
                )
        );

        jSplitPane1.setLeftComponent(pnlProps);

        tblClients.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblClients.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, new String[] {}));
        tblClients.setShowGrid(true);
        jScrollPane1.setViewportView(tblClients);

        txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl6.setText("filter by name or email");

        tglFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        tglFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel2Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel2Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                                .addGroup(
                                    jPanel2Layout
                                        .createSequentialGroup()
                                        .addComponent(lbl6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtFilter)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tglFilter)
                                )
                        )
                )
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    jPanel2Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel2Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tglFilter, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(
                                    jPanel2Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(
                                            txtFilter,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(lbl6)
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                        .addContainerGap()
                )
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jSplitPane1).addContainerGap())
        );
        layout.setVerticalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jSplitPane1).addContainerGap())
        );
    } // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton btnAdd = new javax.swing.JButton();
    private final javax.swing.JButton btnClearFields = new javax.swing.JButton();
    private final javax.swing.JButton btnDelete = new javax.swing.JButton();
    private final javax.swing.JButton btnSearch = new javax.swing.JButton();
    private final javax.swing.JButton btnUpdate = new javax.swing.JButton();
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl6;
    private javax.swing.JPanel pnlProps;
    private final javax.swing.JTable tblClients = new javax.swing.JTable();
    private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
    private final javax.swing.JTextField txtEmail = new javax.swing.JTextField();
    private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
    private final javax.swing.JTextField txtId = new javax.swing.JTextField();
    private final javax.swing.JTextField txtName = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables

}
