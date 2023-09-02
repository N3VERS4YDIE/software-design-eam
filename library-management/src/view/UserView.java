package view;

import controller.DB;
import controller.UserController;
import java.awt.Dimension;
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
import model.Role;
import model.User;
import util.Message;

/**
 *
 * @author Santiago Palacio Vásquez
 */
public class UserView extends javax.swing.JFrame {

  private UserController userController;
  private final ArrayList<JTextComponent> txtFields = new ArrayList<>();

  public UserView() {
    userController = new UserController();
    txtFields.add(txtEmail);
    txtFields.add(txtFirstname);
    txtFields.add(txtLastname);

    initComponents();
    setTitle("User Management");
    setMinimumSize(new Dimension(1111, 666));
    setPreferredSize(new Dimension(1280, 666));
    setSize(getPreferredSize());
    setLocationRelativeTo(null);
    // setExtendedState(MAXIMIZED_BOTH);

    initTblUsers();
    initCmbRole();
    initListeners();

    tglFilter.setEnabled(false);
    setVisible(true);
  }

  private void initTblUsers() {
    DefaultTableModel tableModel = new DefaultTableModel(
      new Object[] { "email", "password", "first name", "last name", "role" },
      0
    ) {
      @Override
      public Class<?> getColumnClass(int columnIndex) {
        return String.class;
      }
    };

    tblUsers.setModel(tableModel);
    updateTable();
  }

  private void initCmbRole() {
    cmbRole.removeAllItems();
    for (Role role : Role.values()) {
      cmbRole.addItem(role);
    }
    cmbRole.setSelectedItem(Role.USER);
  }

  private void initListeners() {
    tblUsers.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          updateFieldsWithTable();
        }
      }
    );
    btnAdd.addActionListener(this::addUser);
    btnUpdate.addActionListener(this::updateUser);
    btnDelete.addActionListener(this::deleteUser);
    btnSearch.addActionListener(this::searchUser);
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
    final int ROW = tblUsers.getSelectedRow();
    if (ROW == -1) {
      return;
    }
    txtEmail.setText(String.valueOf(tblUsers.getValueAt(ROW, 0)));
    txtFirstname.setText(String.valueOf(tblUsers.getValueAt(ROW, 2)));
    txtLastname.setText(String.valueOf(tblUsers.getValueAt(ROW, 3)));
    setRole(Role.valueOf(String.valueOf(tblUsers.getValueAt(ROW, 4))));
  }

  private void updateFields() {
    try {
      ResultSet rs = userController.find(getId());
      txtFirstname.setText(rs.getString(3));
      txtLastname.setText(rs.getString(4));
      setRole(Role.valueOf(rs.getString(5)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addUser(ActionEvent evt) {
    try {
      checkEmptyFields();

      userController.add(getUser());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      Message.showErrorMessage(this, e.getMessage());
    }
  }

  private void updateUser(ActionEvent evt) {
    try {
      checkEmptyFields();

      userController.update(getUser());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      Message.showErrorMessage(this, e.getMessage());
    }
  }

  private void deleteUser(ActionEvent evt) {
    try {
      userController.delete(getId());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      Message.showErrorMessage(this, e.getMessage());
    }
  }

  private void searchUser(ActionEvent evt) {
    if (getId().isEmpty()) {
      return;
    }
    try {
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
    password.setText("");
    setRole(Role.USER);
  }

  private void toggleFilter(ChangeEvent evt) {
    if (tglFilter.isSelected()) {
      try {
        updateTable();
      } catch (Exception e) {
        Message.showErrorMessage(this, e.getMessage());
      }
      tglFilter.setIcon(
        new ImageIcon(getClass().getResource("../resources/clear_filter.png"))
      );
    } else {
      updateTable();
      tglFilter.setIcon(
        new ImageIcon(getClass().getResource("../resources/filter.png"))
      );
    }
  }

  private void checkFilterState() {
    tglFilter.setEnabled(!getFilter().isEmpty());
    tglFilter.setSelected(false);
  }

  private void updateTable() {
    DefaultTableModel tableModel = (DefaultTableModel) tblUsers.getModel();
    tableModel.setRowCount(0);
    try {
      if (tglFilter.isSelected()) {
        final String QUERY = String.format(
          "SELECT * FROM user WHERE id LIKE '%%%s%%' OR firstname LIKE '%%%s%%' OR lastname LIKE '%%%s%%' OR role LIKE '%%%s%%'",
          getFilter(),
          getFilter(),
          getFilter(),
          getFilter()
        );
        ResultSet rs = DB.getInstance().prepareStatement(QUERY).executeQuery();
        updateTable(rs);
      } else {
        updateTable(userController.all());
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

  private void updateTable(ResultSet user) {
    DefaultTableModel tableModel = (DefaultTableModel) tblUsers.getModel();
    tableModel.setRowCount(0);
    try {
      while (user.next()) {
        tableModel.addRow(
          new Object[] {
            user.getString(1),
            user.getString(2),
            user.getString(3),
            user.getString(4),
            user.getString(5),
          }
        );
      }
    } catch (Exception e) {
      Message.showErrorMessage(this, e.getMessage());
    }
  }

  private User getUser() {
    return new User(
      getId(),
      getPassword(),
      getSalt(),
      getFirstName(),
      getLastName(),
      getRole()
    );
  }

  private String getId() {
    return txtEmail.getText().trim();
  }

  private String getPassword() {
    return String.valueOf(password.getPassword());
  }

  private String getFirstName() {
    return txtFirstname.getText().trim();
  }

  private String getLastName() {
    return txtLastname.getText().trim();
  }

  private String getFilter() {
    return txtFilter.getText().trim();
  }

  private Role getRole() {
    return (Role) cmbRole.getSelectedItem();
  }

  private void setRole(Role role) {
    cmbRole.setSelectedItem(role);
  }

  private String getSalt() {
    return userController.getSalt(getId());
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
  // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlProps = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lbl7 = new javax.swing.JLabel();
        cmbRole = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        password = new javax.swing.JPasswordField();
        lbl8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbl6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lbl1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl1.setText("email");

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search.png"))); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl2.setText("first name");

        txtFirstname.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl3.setText("last name");

        txtLastname.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl7.setText("role");

        cmbRole.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        jPanel1.setLayout(new java.awt.GridLayout(0, 2, 10, 10));

        btnAdd.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add.png"))); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnAdd);

        btnUpdate.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/update.png"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnUpdate);

        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/delete.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnDelete);

        btnClearFields.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnClearFields.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/clear.png"))); // NOI18N
        btnClearFields.setText("Clear Fields");
        btnClearFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearFields.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnClearFields);

        password.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl8.setText("password");

        javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(pnlProps);
        pnlProps.setLayout(pnlPropsLayout);
        pnlPropsLayout.setHorizontalGroup(
            pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl1)
                    .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addComponent(lbl8)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(password, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(txtFirstname)
            .addComponent(txtLastname)
            .addComponent(cmbRole, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl2)
                    .addComponent(lbl3)
                    .addComponent(lbl7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPropsLayout.setVerticalGroup(
            pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addGap(18, 18, 18)
                .addComponent(lbl8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(pnlProps);

        tblUsers.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblUsers.setShowGrid(true);
        jScrollPane1.setViewportView(tblUsers);

        txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl6.setText("filter");

        tglFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/filter.png"))); // NOI18N
        tglFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton btnAdd = new javax.swing.JButton();
    private final javax.swing.JButton btnClearFields = new javax.swing.JButton();
    private final javax.swing.JButton btnDelete = new javax.swing.JButton();
    private final javax.swing.JButton btnSearch = new javax.swing.JButton();
    private final javax.swing.JButton btnUpdate = new javax.swing.JButton();
    private javax.swing.JComboBox<Role> cmbRole;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JPasswordField password;
    private javax.swing.JPanel pnlProps;
    private final javax.swing.JTable tblUsers = new javax.swing.JTable();
    private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
    private final javax.swing.JTextField txtEmail = new javax.swing.JTextField();
    private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
    private final javax.swing.JTextField txtFirstname = new javax.swing.JTextField();
    private final javax.swing.JTextField txtLastname = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables
}
