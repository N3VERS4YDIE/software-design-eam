package view;

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

import controller.UserController;
import dao.BookDAO;
import dao.BookGenreDAO;
import dao.BorrowDAO;
import model.Borrow;
import util.FieldChecker;
import util.Message;

public class BorrowedBooks extends javax.swing.JPanel {

    private BookDAO bookDAO;
    private BookGenreDAO bookGenreDAO;
    private BorrowDAO borrowDAO;
    private UserController borrowController;
    private final ArrayList<JTextComponent> txtFields = new ArrayList<>();

    public BorrowedBooks() {
        bookDAO = new BookDAO();
        bookGenreDAO = new BookGenreDAO();
        borrowDAO = BorrowDAO.getInstance();
        borrowController = UserController.getInstance();
        txtFields.add(txtId);

        initComponents();
        initTblBooks();
        initListeners();

        tblBooks.setDefaultEditor(Object.class, null);
        tglFilter.setEnabled(false);
        setVisible(true);
    }

    private void initTblBooks() {
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "id", "borrow date", "return date", "isbn", "title", "author", "year", "quantity", "genre" },
            0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        tblBooks.setModel(tableModel);
        updateTable();
    }

    private void initListeners() {
        addComponentListener(
            new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent evt) {
                    updateTable();
                }
            }
        );
        tblBooks.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateFieldsWithTable();
                }
            }
        );
        btnReturn.addActionListener(this::returnBook);
        btnSearch.addActionListener(this::searchBook);
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
        final int ROW = tblBooks.getSelectedRow();
        if (ROW == -1) {
            return;
        }
        txtId.setText(String.valueOf(tblBooks.getValueAt(ROW, 0)));
    }

    private void updateFields() {
        try {
            ResultSet rs = bookDAO.findRS(getId());
            rs.next();
            txtId.setText(rs.getString("id"));
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void searchBook(ActionEvent evt) {
        if (getId().isEmpty()) {
            return;
        }
        try {
            FieldChecker.checkNonExistence(bookDAO.findRS(getId()));
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

    private void returnBook(ActionEvent evt) {
        try {
            ResultSet rs = BorrowDAO.getInstance().findRS(getId());
            rs.next();
            LocalDate endDate = LocalDate.parse(rs.getString("enddate"));

            Borrow borrow = new Borrow(
                getId(),
                LocalDate.now(),
                endDate,
                borrowController.getLoggedUserId(),
                tblBooks.getValueAt(tblBooks.getSelectedRow(), 3).toString()
            );
            borrowDAO.returnBook(borrow);
            updateTable();
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
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
        DefaultTableModel tableModel = (DefaultTableModel) tblBooks.getModel();
        tableModel.setRowCount(0);
        try {
            if (tglFilter.isSelected()) {
                updateTable(borrowDAO.filterRS(getFilter()));
            } else {
                updateTable(borrowDAO.allRS());
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void updateTable(ResultSet borrow) {
        DefaultTableModel tableModel = (DefaultTableModel) tblBooks.getModel();
        tableModel.setRowCount(0);
        try {
            while (borrow.next()) {
                tableModel.addRow(
                    new Object[] {
                        borrow.getString(1),
                        borrow.getString(2),
                        borrow.getString(3),
                        borrow.getString(4),
                        borrow.getString(5),
                        borrow.getString(6),
                        borrow.getString(7),
                        borrow.getString(8),
                        borrow.getString(9),
                    }
                );
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private String getId() {
        return txtId.getText().trim();
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
        lbl1.setText("id");

        txtId.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/search.png"))); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jPanel1.setLayout(new java.awt.GridLayout(0, 2, 10, 10));

        btnReturn.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/return.png"))); // NOI18N
        btnReturn.setText("Return");
        btnReturn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReturn.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnReturn);

        btnClearFields.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnClearFields.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clear.png"))); // NOI18N
        btnClearFields.setText("Clear Fields");
        btnClearFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearFields.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(btnClearFields);

        javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(pnlProps);
        pnlProps.setLayout(pnlPropsLayout);
        pnlPropsLayout.setHorizontalGroup(
            pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl1)
                    .addComponent(txtId, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch))
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(564, Short.MAX_VALUE))
        );

        spp.setLeftComponent(pnlProps);

        tblBooks.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblBooks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblBooks.setShowGrid(true);
        jScrollPane1.setViewportView(tblBooks);

        txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl6.setText("filter");

        tglFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        tglFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );

        spp.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spp)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spp)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton btnClearFields = new javax.swing.JButton();
    private final javax.swing.JButton btnReturn = new javax.swing.JButton();
    private final javax.swing.JButton btnSearch = new javax.swing.JButton();
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl6;
    private javax.swing.JPanel pnlProps;
    private javax.swing.JSplitPane spp;
    private final javax.swing.JTable tblBooks = new javax.swing.JTable();
    private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
    private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
    private final javax.swing.JTextField txtId = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables

}
