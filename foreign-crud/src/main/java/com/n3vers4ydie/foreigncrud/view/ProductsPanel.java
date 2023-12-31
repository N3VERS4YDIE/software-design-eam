/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.n3vers4ydie.foreigncrud.view;

import com.n3vers4ydie.foreigncrud.dao.ProductDAO;
import com.n3vers4ydie.foreigncrud.model.Product;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author n3vers4ydie
 */
public class ProductsPanel extends javax.swing.JPanel {

  private final ArrayList<JTextField> txtFields = new ArrayList<>();
  private ProductDAO productDAO;

  /**
   * Creates new form ProductsPanel
   */
  public ProductsPanel() {
    productDAO = new ProductDAO();
    txtFields.add(txtId);
    txtFields.add(txtName);
    txtFields.add(txtPrice);
    txtFields.add(txtProductId);

    initComponents();
    initTable();
    initListeners();

    tglFilter.setEnabled(false);
    setVisible(true);
  }

  private void initTable() {
    DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "id", "name", "price", "product_id" }, 0) {
      Class<?>[] types = new Class[] { String.class, String.class, BigDecimal.class, String.class };

      @Override
      public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
      }
    };

    tblProducts.setModel(tableModel);
    updateTable();
  }

  private void initListeners() {
    tblProducts.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          selectTableRow();
        }
      }
    );
    btnAdd.addActionListener(this::addProduct);
    btnUpdate.addActionListener(this::updateProduct);
    btnDelete.addActionListener(this::deleteProduct);
    btnSearch.addActionListener(this::searchProduct);
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

  private void selectTableRow() {
    final int ROW = tblProducts.getSelectedRow();
    for (int i = 0; i < txtFields.size(); i++) {
      txtFields.get(i).setText(tblProducts.getValueAt(ROW, i).toString());
    }
  }

  private void addProduct(ActionEvent evt) {
    try {
      checkEmptyFields();

      productDAO.add(getProduct());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      showErrorMessage("An error occurred while adding the product.", e.getMessage());
    }
  }

  private void updateProduct(ActionEvent evt) {
    try {
      checkEmptyFields();

      ResultSet rs = productDAO.findRS(getId());
      if (!rs.next()) {
        throw new NoSuchElementException("Product not found");
      }
      productDAO.updateRS(getProduct());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      showErrorMessage("An error occurred while updating the product.", e.getMessage());
    }
  }

  private void deleteProduct(ActionEvent evt) {
    try {
      ResultSet rs = productDAO.findRS(getId());
      if (!rs.next()) {
        throw new NoSuchElementException("Product not found");
      }
      productDAO.delete(getId());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      showErrorMessage("An error occurred while deleting the product.", e.getMessage());
    }
  }

  private void searchProduct(ActionEvent evt) {
    try {
      final ResultSet product = productDAO.findRS(getId());
      product.next();
      for (int i = 0; i < txtFields.size(); i++) {
        txtFields.get(i).setText(product.getString(i + 1));
      }
    } catch (NoSuchElementException e) {
      String auxIdText = getId();
      clearFields(null);
      txtId.setText(auxIdText);
      showErrorMessage("Product not found", null);
    } catch (Exception e) {
      showErrorMessage("An error occurred while searching the product.", e.getMessage());
    }
  }

  private void clearFields(ActionEvent evt) {
    for (JTextField field : txtFields) {
      field.setText("");
    }
  }

  private void toggleFilter(ChangeEvent evt) {
    if (tglFilter.isSelected()) {
      try {
        updateTable();
      } catch (Exception e) {
        showErrorMessage("An error occurred while filtering the table.", e.getMessage());
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

  public void clearFields() {
    txtId.setText("");
    txtName.setText("");
    txtPrice.setText("");
    txtProductId.setText("");
  }

  private void updateTable() {
    DefaultTableModel tableModel = (DefaultTableModel) tblProducts.getModel();
    tableModel.setRowCount(0);
    try {
      if (tglFilter.isSelected()) {
        updateTable(productDAO.filterRS("name", getFilter()));
      } else {
        updateTable(productDAO.allRS());
      }
    } catch (Exception e) {
      showErrorMessage("An error occurred while updating the table.", e.getMessage());
    }
  }

  private void checkEmptyFields() {
    for (JTextField field : txtFields) {
      if (field.getText().isEmpty()) {
        throw new IllegalArgumentException("Empty fields are not allowed");
      }
    }
  }

  private void showErrorMessage(String msg, String errorMsg) {
    if (errorMsg != null) {
      msg += "\n\n" + errorMsg;
    }
    JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void updateTable(ResultSet product) {
    DefaultTableModel tableModel = (DefaultTableModel) tblProducts.getModel();
    tableModel.setRowCount(0);
    try {
      while (product.next()) {
        tableModel.addRow(
          new Object[] {
            product.getString(1),
            product.getString(2),
            product.getBigDecimal(3),
            product.getString(4),
          }
        );
      }
    } catch (Exception e) {
      showErrorMessage("An error occurred while updating the table.", e.getMessage());
    }
  }

  private Product getProduct() {
    return new Product(getId(), getName(), new BigDecimal(getPrice()), getProductId());
  }

  private String getId() {
    return txtId.getText().trim();
  }

  @Override
  public String getName() {
    return txtName.getText().trim();
  }

  private String getPrice() {
    return txtPrice.getText().trim();
  }

  private String getProductId() {
    return txtProductId.getText().trim();
  }

  public String getFilter() {
    return txtFilter.getText().trim();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    jSplitPane1 = new javax.swing.JSplitPane();
    pnlProps = new javax.swing.JPanel();
    lbl1 = new javax.swing.JLabel();
    lbl2 = new javax.swing.JLabel();
    lbl3 = new javax.swing.JLabel();
    lbl4 = new javax.swing.JLabel();
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
    lbl3.setText("price");

    txtPrice.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl4.setText("product id");

    txtProductId.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

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
        .addComponent(txtPrice)
        .addComponent(txtProductId)
        .addGroup(
          pnlPropsLayout
            .createSequentialGroup()
            .addGroup(
              pnlPropsLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lbl1)
                .addComponent(lbl2)
                .addComponent(lbl3)
                .addComponent(lbl4)
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
              txtPrice,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtProductId,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(210, Short.MAX_VALUE)
        )
    );

    jSplitPane1.setLeftComponent(pnlProps);

    tblProducts.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
    tblProducts.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, new String[] {}));
    tblProducts.setShowGrid(true);
    jScrollPane1.setViewportView(tblProducts);

    txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl6.setText("filter by name");

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
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
  private javax.swing.JLabel lbl4;
  private javax.swing.JLabel lbl6;
  private javax.swing.JPanel pnlProps;
  private final javax.swing.JTable tblProducts = new javax.swing.JTable();
  private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
  private final javax.swing.JTextField txtProductId = new javax.swing.JTextField();
  private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
  private final javax.swing.JTextField txtId = new javax.swing.JTextField();
  private final javax.swing.JTextField txtName = new javax.swing.JTextField();
  private final javax.swing.JTextField txtPrice = new javax.swing.JTextField();
  // End of variables declaration//GEN-END:variables
}
