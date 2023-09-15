/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.n3vers4ydie.foreigncrud.view;

import com.n3vers4ydie.autosqlcrud.SQLDB;
import com.n3vers4ydie.foreigncrud.connection.DB;
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
public class ForeignsPanel extends javax.swing.JPanel {

  private static final String ALL_QUERY = "SELECT * FROM products P INNER JOIN categories C ON C.id = P.category_id ";
  private final SQLDB db;
  private final ArrayList<JTextField> txtFields = new ArrayList<>();

  /**
   * Creates new form ForeignsPanel
   */
  public ForeignsPanel() {
    db = DB.getInstance();
    txtFields.add(txtId);
    txtFields.add(txtName);
    txtFields.add(txtPrice);
    txtFields.add(txtId2);
    txtFields.add(txtName2);
    for (JTextField field : txtFields) {
      field.setEditable(false);
      // field.setEnabled(false);
    }

    initComponents();
    initTable();
    initListeners();

    tglFilter.setEnabled(false);
    setVisible(true);
  }

  private void initTable() {
    DefaultTableModel tableModel = new DefaultTableModel(
      new Object[] { "product id", "product name", "product price", "category id", "category name" },
      0
    ) {
      Class<?>[] types = new Class[] { String.class, String.class, BigDecimal.class, String.class, String.class };

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
    txtId2.setText("");
  }

  private void updateTable() {
    DefaultTableModel tableModel = (DefaultTableModel) tblProducts.getModel();
    tableModel.setRowCount(0);
    try {
      if (tglFilter.isSelected()) {
        updateTable(db.prepareStatement(ALL_QUERY + "WHERE C.name LIKE '%" + getFilter() + "%'").executeQuery());
      } else {
        updateTable(db.prepareStatement(ALL_QUERY).executeQuery());
      }
    } catch (Exception e) {
      showErrorMessage("An error occurred while updating the table.", e.getMessage());
    }
  }

  private void showErrorMessage(String msg, String errorMsg) {
    if (errorMsg != null) {
      msg += "\n\n" + errorMsg;
    }
    JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void updateTable(ResultSet productcategory) {
    DefaultTableModel tableModel = (DefaultTableModel) tblProducts.getModel();
    tableModel.setRowCount(0);
    try {
      while (productcategory.next()) {
        tableModel.addRow(
          new Object[] {
            productcategory.getString(1),
            productcategory.getString(2),
            productcategory.getBigDecimal(3),
            productcategory.getString(4),
            productcategory.getString(6),
          }
        );
      }
    } catch (Exception e) {
      showErrorMessage("An error occurred while updating the table.", e.getMessage());
    }
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
    jPanel3 = new javax.swing.JPanel();
    lbl5 = new javax.swing.JLabel();
    lbl8 = new javax.swing.JLabel();
    lbl9 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    lbl1 = new javax.swing.JLabel();
    lbl2 = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    lbl6 = new javax.swing.JLabel();

    btnClearFields.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    btnClearFields.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clear.png"))); // NOI18N
    btnClearFields.setText("Clear Fields");
    btnClearFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnClearFields.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("product"));

    lbl5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl5.setText("id");

    txtId.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl8.setText("name");

    lbl9.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl9.setText("price");

    txtName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    txtPrice.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          jPanel3Layout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
              jPanel3Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lbl5)
                .addComponent(lbl8)
                .addComponent(lbl9)
                .addComponent(txtId)
                .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addComponent(txtPrice)
            )
            .addContainerGap()
        )
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          jPanel3Layout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(lbl5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtId,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl8)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtName,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl9)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtPrice,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        )
    );

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("category"));

    lbl1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl1.setText("id");

    txtId2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl2.setText("name");

    txtName2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          jPanel1Layout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
              jPanel1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                  jPanel1Layout
                    .createSequentialGroup()
                    .addGroup(
                      jPanel1Layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbl1)
                        .addComponent(lbl2)
                    )
                    .addGap(0, 0, Short.MAX_VALUE)
                )
                .addComponent(txtId2)
                .addComponent(
                  txtName2,
                  javax.swing.GroupLayout.Alignment.TRAILING,
                  javax.swing.GroupLayout.DEFAULT_SIZE,
                  280,
                  Short.MAX_VALUE
                )
            )
            .addContainerGap()
        )
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          jPanel1Layout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(lbl1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtId2,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtName2,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addContainerGap()
        )
    );

    javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(pnlProps);
    pnlProps.setLayout(pnlPropsLayout);
    pnlPropsLayout.setHorizontalGroup(
      pnlPropsLayout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(
          jPanel1,
          javax.swing.GroupLayout.DEFAULT_SIZE,
          javax.swing.GroupLayout.DEFAULT_SIZE,
          Short.MAX_VALUE
        )
        .addComponent(
          jPanel3,
          javax.swing.GroupLayout.Alignment.TRAILING,
          javax.swing.GroupLayout.DEFAULT_SIZE,
          javax.swing.GroupLayout.DEFAULT_SIZE,
          Short.MAX_VALUE
        )
        .addGroup(
          javax.swing.GroupLayout.Alignment.TRAILING,
          pnlPropsLayout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(
              btnClearFields,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              Short.MAX_VALUE
            )
            .addContainerGap()
        )
    );
    pnlPropsLayout.setVerticalGroup(
      pnlPropsLayout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          pnlPropsLayout
            .createSequentialGroup()
            .addGap(9, 9, 9)
            .addComponent(
              jPanel3,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              jPanel1,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(
              btnClearFields,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              35,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        )
    );

    jSplitPane1.setLeftComponent(pnlProps);

    tblProducts.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
    tblProducts.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, new String[] {}));
    tblProducts.setShowGrid(true);
    jScrollPane1.setViewportView(tblProducts);

    txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl6.setText("filter by category name");

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
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
  private final javax.swing.JButton btnClearFields = new javax.swing.JButton();
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JLabel lbl1;
  private javax.swing.JLabel lbl2;
  private javax.swing.JLabel lbl5;
  private javax.swing.JLabel lbl6;
  private javax.swing.JLabel lbl8;
  private javax.swing.JLabel lbl9;
  private javax.swing.JPanel pnlProps;
  private final javax.swing.JTable tblProducts = new javax.swing.JTable();
  private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
  private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
  private final javax.swing.JTextField txtId = new javax.swing.JTextField();
  private final javax.swing.JTextField txtId2 = new javax.swing.JTextField();
  private final javax.swing.JTextField txtName = new javax.swing.JTextField();
  private final javax.swing.JTextField txtName2 = new javax.swing.JTextField();
  private final javax.swing.JTextField txtPrice = new javax.swing.JTextField();
  // End of variables declaration//GEN-END:variables
}
