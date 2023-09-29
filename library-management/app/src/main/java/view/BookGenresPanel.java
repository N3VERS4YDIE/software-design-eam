package view;

import connection.DB;
import dao.BookDAO;
import dao.BookGenreDAO;
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
import model.BookGenre;
import util.Message;

public class BookGenresPanel extends javax.swing.JPanel {

    private BookGenreDAO bookGenreDAO;
    private final ArrayList<JTextComponent> txtFields = new ArrayList<>();

    public BookGenresPanel() {
        bookGenreDAO = new BookGenreDAO();
        txtFields.add(txtEmail);

        initComponents();
        initTblBookGenres();
        initListeners();

        tglFilter.setEnabled(false);
        setVisible(true);
    }

    private void initTblBookGenres() {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "id", "name" }, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        tblBookGenres.setModel(tableModel);
        updateTable();
    }

    private void initListeners() {
        tblBookGenres.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateFieldsWithTable();
                }
            }
        );
        btnAdd.addActionListener(this::addBookGenre);
        btnUpdate.addActionListener(this::updateBookGenre);
        btnDelete.addActionListener(this::deleteBookGenre);
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
        final int ROW = tblBookGenres.getSelectedRow();
        if (ROW == -1) {
            return;
        }
        txtEmail.setText(String.valueOf(tblBookGenres.getValueAt(ROW, 1)));
    }

    private void addBookGenre(ActionEvent evt) {
        try {
            checkEmptyFields();

            bookGenreDAO.add(new BookGenre(getGenreName()));
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void updateBookGenre(ActionEvent evt) {
        try {
            checkEmptyFields();

            bookGenreDAO.updateRS(new BookGenre(getId(), getGenreName()));
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void deleteBookGenre(ActionEvent evt) {
        if (getId().equals("1")) {
            Message.showWarningMessage(this, "This is the default genre and cannot be deleted");
            return;
        }
        try {
            DB
                .getInstance()
                .prepareStatement(
                    String.format("UPDATE %s SET genre_id = 1 WHERE genre_id = %s", new BookDAO().tableName, getId())
                )
                .executeUpdate();
            bookGenreDAO.delete(getId());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
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
        DefaultTableModel tableModel = (DefaultTableModel) tblBookGenres.getModel();
        tableModel.setRowCount(0);
        try {
            if (tglFilter.isSelected()) {
                ResultSet rs = bookGenreDAO.filterRS("id, name", getFilter());
                updateTable(rs);
            } else {
                updateTable(bookGenreDAO.allRS());
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
        DefaultTableModel tableModel = (DefaultTableModel) tblBookGenres.getModel();
        tableModel.setRowCount(0);
        try {
            while (user.next()) {
                tableModel.addRow(new Object[] { user.getString(1), user.getString(2) });
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private String getId() {
        return tblBookGenres.getValueAt(tblBookGenres.getSelectedRow(), 0).toString();
    }

    private String getGenreName() {
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
        spp = new javax.swing.JSplitPane();
        pnlProps = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbl6 = new javax.swing.JLabel();

        lbl1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl1.setText("name");

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
                .addComponent(txtEmail)
                .addGroup(
                    pnlPropsLayout
                        .createSequentialGroup()
                        .addGroup(
                            pnlPropsLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    pnlPropsLayout
                                        .createSequentialGroup()
                                        .addComponent(lbl1)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                )
                                .addComponent(
                                    jPanel1,
                                    javax.swing.GroupLayout.Alignment.TRAILING,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    410,
                                    Short.MAX_VALUE
                                )
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
                        .addContainerGap()
                        .addComponent(lbl1)
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
                        .addContainerGap(374, Short.MAX_VALUE)
                )
        );

        spp.setLeftComponent(pnlProps);

        tblBookGenres.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblBookGenres.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, new String[] {}));
        tblBookGenres.setShowGrid(true);
        jScrollPane1.setViewportView(tblBookGenres);

        txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl6.setText("filter");

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
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
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

        spp.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(spp).addContainerGap())
        );
        layout.setVerticalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(spp).addContainerGap())
        );
    } // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton btnAdd = new javax.swing.JButton();
    private final javax.swing.JButton btnClearFields = new javax.swing.JButton();
    private final javax.swing.JButton btnDelete = new javax.swing.JButton();
    private final javax.swing.JButton btnUpdate = new javax.swing.JButton();
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl6;
    private javax.swing.JPanel pnlProps;
    private javax.swing.JSplitPane spp;
    private final javax.swing.JTable tblBookGenres = new javax.swing.JTable();
    private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
    private final javax.swing.JTextField txtEmail = new javax.swing.JTextField();
    private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables

}
