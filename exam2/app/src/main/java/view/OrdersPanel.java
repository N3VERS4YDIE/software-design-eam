package view;

import controller.OrderController;
import dao.ClientDAO;
import dao.OrderDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import model.Order;
import util.FieldChecker;
import util.Message;

public class OrdersPanel extends javax.swing.JPanel {

    private OrderDAO orderDAO;
    private ClientDAO clientDAO;
    private OrderController orderController;
    private final ArrayList<JTextComponent> txtFields = new ArrayList<>();

    public OrdersPanel() {
        orderDAO = OrderDAO.getInstance();
        clientDAO = ClientDAO.getInstance();
        orderController = OrderController.getInstance();

        initComponents();
        initCmbClients();
        initTblOrders();
        initListeners();

        tglFilter.setEnabled(false);
        setVisible(true);
    }

    private void initTblOrders() {
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "id", "date", "total", "client id", "client name" },
            0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        tblOrders.setModel(tableModel);
        updateTable();
    }

    private void initCmbClients() {
        cmbClients.removeAllItems();
        try {
            ResultSet rs = clientDAO.allRS();
            while (rs.next()) {
                cmbClients.addItem(rs.getString("id") + " -> " + rs.getString("name"));
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void initListeners() {
        addComponentListener(
            new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent evt) {
                    initCmbClients();
                    updateTable();
                }
            }
        );
        tblOrders.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateFieldsWithTable();
                }
            }
        );
        btnAdd.addActionListener(this::addOrder);
        btnUpdate.addActionListener(this::updateOrder);
        btnDelete.addActionListener(this::deleteOrder);
        btnSearch.addActionListener(this::searchOrder);
        btnClearFields.addActionListener(this::clearFields);
        tglFilter.addChangeListener(e -> toggleFilter());
        txtFilter.addKeyListener(
            new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    checkFilterState();
                }
            }
        );
    }

    private void updateFieldsWithTable() {
        final int ROW = tblOrders.getSelectedRow();
        if (ROW == -1) {
            return;
        }
        txtId.setText(String.valueOf(tblOrders.getValueAt(ROW, 0)));
        spnTotal.setValue(Double.parseDouble(tblOrders.getValueAt(ROW, 2).toString()));
        cmbClients.setSelectedItem(tblOrders.getValueAt(ROW, 3) + " -> " + tblOrders.getValueAt(ROW, 4));
    }

    private void updateFields() {
        try {
            ResultSet rs = orderDAO.findRS(getId());
            rs.next();
            txtId.setText(rs.getString("id"));
            spnTotal.setValue(rs.getInt("total"));
            cmbClients.setSelectedItem(rs.getString("client_id") + " -> " + rs.getString("name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOrder(ActionEvent evt) {
        try {
            checkEmptyFields();

            FieldChecker.checkExistence(orderDAO.findRS(getId()));
            orderDAO.add(getOrder());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void updateOrder(ActionEvent evt) {
        try {
            checkEmptyFields();

            FieldChecker.checkNonExistence(orderDAO.findRS(getId()));
            ResultSet rs = orderDAO.findRS(getId());
            rs.next();
            orderDAO.updateRS(getOrder(LocalDate.parse(rs.getDate("date").toString())));
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void deleteOrder(ActionEvent evt) {
        try {
            FieldChecker.checkNonExistence(orderDAO.findRS(getId()));
            orderDAO.delete(getId());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void searchOrder(ActionEvent evt) {
        if (getId().isEmpty()) {
            return;
        }
        try {
            FieldChecker.checkNonExistence(orderDAO.findRS(getId()));
            updateFields();
        } catch (Exception e) {
            String auxIdText = getId();
            clearFields(null);
            txtId.setText(auxIdText);
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void clearFields(ActionEvent evt) {
        for (JTextComponent field : txtFields) {
            field.setText("");
        }
    }

    private void toggleFilter() {
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
        DefaultTableModel tableModel = (DefaultTableModel) tblOrders.getModel();
        tableModel.setRowCount(0);
        try {
            if (tglFilter.isSelected()) {
                updateTable(orderController.filter(getFilter()));
            } else {
                updateTable(orderController.filter(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkEmptyFields() {
        for (JTextComponent field : txtFields) {
            if (field.getText().isEmpty()) {
                throw new IllegalArgumentException("Empty fields are not allowed");
            }
        }
    }

    private void updateTable(ResultSet order) {
        DefaultTableModel tableModel = (DefaultTableModel) tblOrders.getModel();
        tableModel.setRowCount(0);
        try {
            while (order.next()) {
                tableModel.addRow(
                    new Object[] {
                        order.getString(1),
                        order.getString(2),
                        order.getString(3),
                        order.getString(4),
                        order.getString(5)
                    }
                );
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private Order getOrder() {
        return new Order(getId(), getDate(), getTotal(), getClientId());
    }

    private Order getOrder(LocalDate date) {
        return new Order(getId(), date, getTotal(), getClientId());
    }

    private String getId() {
        return txtId.getText().trim();
    }

    private LocalDate getDate() {
        return LocalDate.now();
    }

    private double getTotal() {
        return Double.parseDouble(spnTotal.getValue().toString());
    }

    private String getClientId() {
        return cmbClients.getSelectedItem().toString().split(" -> ")[0];
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
        lbl3 = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cmbClients = new javax.swing.JComboBox<>();
        spnTotal = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbl6 = new javax.swing.JLabel();

        lbl1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl1.setText("id");

        txtId.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/search.png"))); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl3.setText("total");

        lbl4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl4.setText("client");

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

        cmbClients.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        spnTotal.setFont(new java.awt.Font("Liberation Sans", 0, 16)); // NOI18N

        javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(pnlProps);
        pnlProps.setLayout(pnlPropsLayout);
        pnlPropsLayout.setHorizontalGroup(
            pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl1)
                    .addComponent(txtId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl3)
                    .addComponent(lbl4))
                .addGap(37, 37, 37))
            .addComponent(cmbClients, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(spnTotal)
        );
        pnlPropsLayout.setVerticalGroup(
            pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addGap(18, 18, 18)
                .addComponent(lbl3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(lbl4)
                .addGap(7, 7, 7)
                .addComponent(cmbClients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(pnlProps);

        tblOrders.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblOrders.setShowGrid(true);
        jScrollPane1.setViewportView(tblOrders);

        txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl6.setText("filter by date, total or client name");

        tglFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        tglFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tglFilter))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tglFilter, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton btnAdd = new javax.swing.JButton();
    private final javax.swing.JButton btnClearFields = new javax.swing.JButton();
    private final javax.swing.JButton btnDelete = new javax.swing.JButton();
    private final javax.swing.JButton btnSearch = new javax.swing.JButton();
    private final javax.swing.JButton btnUpdate = new javax.swing.JButton();
    private javax.swing.JComboBox<String> cmbClients;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl6;
    private javax.swing.JPanel pnlProps;
    private javax.swing.JSpinner spnTotal;
    private final javax.swing.JTable tblOrders = new javax.swing.JTable();
    private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
    private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
    private final javax.swing.JTextField txtId = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables

}
