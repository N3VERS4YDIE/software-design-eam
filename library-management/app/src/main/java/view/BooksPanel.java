package view;

import connection.DB;
import dao.BookDAO;
import dao.BookGenreDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import model.Book;
import model.BookFilter;
import util.FieldChecker;
import util.Message;

public class BooksPanel extends javax.swing.JPanel {

    private BookDAO bookDAO;
    private BookGenreDAO bookGenreDAO;
    private final ArrayList<JTextComponent> txtFields = new ArrayList<>();

    public BooksPanel() {
        bookDAO = new BookDAO();
        bookGenreDAO = new BookGenreDAO();
        txtFields.add(txtIsbn);
        txtFields.add(txtTitle);
        txtFields.add(txtAuthor);

        initComponents();
        initTblBooks();
        initCmbYear();
        initCmbFilters();
        initCmbGenre();
        initListeners();

        tglFilter.setEnabled(false);
        setVisible(true);
    }

    private void initTblBooks() {
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "isbn", "title", "author", "year", "quantity", "genre" },
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

    private void initCmbYear() {
        cmbYear.removeAllItems();
        cmbYear.addItem("Unknown");
        final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = CURRENT_YEAR; i >= 2000; i--) {
            cmbYear.addItem(Integer.toString(i));
        }
        cmbYear.setSelectedIndex(0);
    }

    private void initCmbFilters() {
        cmbFilter.removeAllItems();
        for (BookFilter filter : BookFilter.values()) {
            cmbFilter.addItem(filter);
        }
        cmbFilter.setSelectedItem(BookFilter.ALL);
    }

    private void initCmbGenre() {
        cmbGenre.removeAllItems();
        try {
            ResultSet rs = bookGenreDAO.allRS();
            while (rs.next()) {
                cmbGenre.addItem(rs.getString("name"));
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
                    int selectedIndex = cmbGenre.getSelectedIndex();
                    initCmbGenre();
                    cmbGenre.setSelectedIndex(selectedIndex);
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
        btnAdd.addActionListener(this::addBook);
        btnUpdate.addActionListener(this::updateBook);
        btnDelete.addActionListener(this::deleteBook);
        btnSearch.addActionListener(this::searchBook);
        btnClearFields.addActionListener(this::clearFields);
        cmbFilter.addItemListener(e -> {
            if (e.getStateChange() == 1) {
                toggleFilter();
            }
        });
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
        txtIsbn.setText(String.valueOf(tblBooks.getValueAt(ROW, 0)));
        txtTitle.setText(String.valueOf(tblBooks.getValueAt(ROW, 1)));
        txtAuthor.setText(String.valueOf(tblBooks.getValueAt(ROW, 2)));
        cmbYear.setSelectedItem(tblBooks.getValueAt(ROW, 3));
        spnQuantity.setValue(Integer.parseInt(String.valueOf(tblBooks.getValueAt(ROW, 4))));
        cmbGenre.setSelectedItem(tblBooks.getValueAt(ROW, 5));
    }

    private void updateFields() {
        try {
            ResultSet rs = bookDAO.findRS(getId());
            rs.next();
            txtTitle.setText(rs.getString("title"));
            txtAuthor.setText(rs.getString("author"));
            cmbYear.setSelectedItem(rs.getString("year"));
            spnQuantity.setValue(rs.getInt("quantity"));
            cmbGenre.setSelectedIndex(rs.getInt("genre_id") - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBook(ActionEvent evt) {
        try {
            checkEmptyFields();

            FieldChecker.checkExistence(bookDAO.findRS(getId()));
            bookDAO.add(getBook());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void updateBook(ActionEvent evt) {
        try {
            checkEmptyFields();

            FieldChecker.checkNonExistence(bookDAO.findRS(getId()));
            bookDAO.updateRS(getBook());
            updateTable();
            clearFields(null);
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void deleteBook(ActionEvent evt) {
        try {
            FieldChecker.checkNonExistence(bookDAO.findRS(getId()));
            bookDAO.delete(getId());
            updateTable();
            clearFields(null);
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
            txtIsbn.setText(auxIdText);
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private void clearFields(ActionEvent evt) {
        for (JTextComponent field : txtFields) {
            field.setText("");
        }
        cmbGenre.setSelectedIndex(0);
        cmbYear.setSelectedIndex(0);
        spnQuantity.setValue(0);
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

    private ResultSet filter(String condition) {
        String selectQuery = String.format(
            "SELECT B.id, B.title, B.author, B.year, B.quantity, G.name FROM %s B INNER JOIN %s G ON B.genre_id = G.id ",
            bookDAO.tableName,
            bookGenreDAO.tableName
        );
        try {
            return DB.getInstance().prepareStatement(selectQuery + condition).executeQuery();
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
            return null;
        }
    }

    private void updateTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblBooks.getModel();
        tableModel.setRowCount(0);
        try {
            if (tglFilter.isSelected()) {
                ResultSet rs = null;
                switch ((BookFilter) cmbFilter.getSelectedItem()) {
                    case ALL:
                        rs =
                            filter(
                                String.format(
                                    "WHERE CONCAT(B.title, B.author, B.year, G.name) LIKE '%%%s%%' OR B.quantity = '%s'",
                                    getFilter(),
                                    getFilter()
                                )
                            );
                        break;
                    case TITLE:
                        rs = filter(String.format("WHERE B.title LIKE '%%%s%%' ORDER BY B.title", getFilter()));
                        break;
                    case AUTHOR:
                        rs = filter(String.format("WHERE B.author LIKE '%%%s%%' ORDER BY B.author", getFilter()));
                        break;
                    case YEAR:
                        rs = filter(String.format("WHERE B.year LIKE '%%%s%%' ORDER BY B.year DESC", getFilter()));
                        break;
                    case QUANTITY:
                        rs = filter(String.format("WHERE B.quantity = '%s'", getFilter()));
                        break;
                    case GENRE:
                        rs = filter(String.format("WHERE G.name LIKE '%%%s%%' ORDER BY G.name", getFilter()));
                        break;
                }
                updateTable(rs);
            } else {
                updateTable(filter(""));
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

    private void updateTable(ResultSet user) {
        DefaultTableModel tableModel = (DefaultTableModel) tblBooks.getModel();
        tableModel.setRowCount(0);
        try {
            while (user.next()) {
                tableModel.addRow(
                    new Object[] {
                        user.getString(1),
                        user.getString(2),
                        user.getString(3),
                        user.getString(4),
                        user.getInt(5),
                        user.getString(6),
                    }
                );
            }
        } catch (Exception e) {
            Message.showErrorMessage(this, e.getMessage());
        }
    }

    private Book getBook() {
        return new Book(getId(), getTitle(), getAuthor(), getYear(), getQuantity(), getGenre());
    }

    private String getId() {
        return txtIsbn.getText().trim();
    }

    private String getTitle() {
        return txtTitle.getText().trim();
    }

    private String getAuthor() {
        return txtAuthor.getText().trim();
    }

    private int getGenre() {
        return cmbGenre.getSelectedIndex() + 1;
    }

    private String getYear() {
        return cmbYear.getSelectedItem().toString();
    }

    private int getQuantity() {
        return (int) spnQuantity.getValue();
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
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        cmbGenre = new javax.swing.JComboBox<>();
        lbl7 = new javax.swing.JLabel();
        cmbYear = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        lbl8 = new javax.swing.JLabel();
        lbl9 = new javax.swing.JLabel();
        spnQuantity = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbl6 = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox<>();

        lbl1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl1.setText("isbn");

        txtIsbn.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/search.png"))); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lbl2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl2.setText("author");

        txtAuthor.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl3.setText("genre");

        cmbGenre.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl7.setText("year");

        cmbYear.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

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

        lbl8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl8.setText("title");

        txtTitle.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl9.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl9.setText("quantity");

        javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(pnlProps);
        pnlProps.setLayout(pnlPropsLayout);
        pnlPropsLayout.setHorizontalGroup(
            pnlPropsLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    pnlPropsLayout
                        .createSequentialGroup()
                        .addGroup(
                            pnlPropsLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbl1)
                                .addComponent(txtIsbn, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch)
                )
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addGroup(pnlPropsLayout.createSequentialGroup().addComponent(lbl8).addGap(0, 0, Short.MAX_VALUE))
                .addComponent(txtAuthor)
                .addComponent(
                    cmbYear,
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    0,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE
                )
                .addComponent(txtTitle)
                .addComponent(spnQuantity)
                .addGroup(
                    pnlPropsLayout
                        .createSequentialGroup()
                        .addGroup(
                            pnlPropsLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    pnlPropsLayout
                                        .createSequentialGroup()
                                        .addGroup(
                                            pnlPropsLayout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lbl2)
                                                .addComponent(lbl3)
                                                .addComponent(lbl7)
                                                .addComponent(lbl9)
                                        )
                                        .addGap(0, 0, Short.MAX_VALUE)
                                )
                                .addComponent(cmbGenre, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addGroup(
                            pnlPropsLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(
                                    txtIsbn,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(btnSearch)
                        )
                        .addGap(18, 18, 18)
                        .addComponent(lbl8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtTitle,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18, 18, 18)
                        .addComponent(lbl2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtAuthor,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18, 18, 18)
                        .addComponent(lbl3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            cmbGenre,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18, 18, 18)
                        .addComponent(lbl7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            cmbYear,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18, 18, 18)
                        .addComponent(lbl9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            spnQuantity,
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
                        .addContainerGap(29, Short.MAX_VALUE)
                )
        );

        spp.setLeftComponent(pnlProps);

        tblBooks.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblBooks.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, new String[] {}));
        tblBooks.setShowGrid(true);
        jScrollPane1.setViewportView(tblBooks);

        txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl6.setText("filter");

        tglFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        tglFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cmbFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

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
                                        .addComponent(
                                            cmbFilter,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            113,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
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
                                        .addComponent(
                                            cmbFilter,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
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
    private final javax.swing.JButton btnSearch = new javax.swing.JButton();
    private final javax.swing.JButton btnUpdate = new javax.swing.JButton();
    private javax.swing.JComboBox<BookFilter> cmbFilter;
    private javax.swing.JComboBox<String> cmbGenre;
    private javax.swing.JComboBox<String> cmbYear;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lbl9;
    private javax.swing.JPanel pnlProps;
    private javax.swing.JSpinner spnQuantity;
    private javax.swing.JSplitPane spp;
    private final javax.swing.JTable tblBooks = new javax.swing.JTable();
    private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
    private final javax.swing.JTextField txtAuthor = new javax.swing.JTextField();
    private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
    private final javax.swing.JTextField txtIsbn = new javax.swing.JTextField();
    private final javax.swing.JTextField txtTitle = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables

}
