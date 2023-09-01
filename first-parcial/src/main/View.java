package main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * @author Santiago Palacio Vásquez
 */
public class View extends javax.swing.JFrame {

  private Controller controller;
  private final ArrayList<JTextField> txtFields = new ArrayList<>();

  /**
   * Creates new form View
   */
  public View() {
    controller = new Controller();
    txtFields.add(txtId);
    txtFields.add(txtName);
    txtFields.add(txtArtist);
    txtFields.add(txtAlbum);
    txtFields.add(txtDescrption);
    txtFields.add(txtRating);
    txtFields.add(txtYear);

    initComponents();
    setMinimumSize(new Dimension(1000, 720));
    setPreferredSize(new Dimension(1280, 720));
    setSize(getPreferredSize());
    setLocationRelativeTo(null);
    // setExtendedState(MAXIMIZED_BOTH);

    initTable();
    initListeners();

    tglFilter.setEnabled(false);
    setVisible(true);
  }

  private void initTable() {
    DefaultTableModel tableModel = new DefaultTableModel(
      new Object[] {
        "id",
        "name",
        "artist",
        "album",
        "description",
        "rating",
        "year",
      },
      0
    ) {
      Class<?>[] types = new Class[] {
        String.class,
        String.class,
        String.class,
        String.class,
        String.class,
        Byte.class,
        Short.class,
      };

      @Override
      public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
      }
    };

    tblSongs.setModel(tableModel);
    updateTable();
  }

  private void initListeners() {
    tblSongs.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          selectTableRow();
        }
      }
    );
    btnAdd.addActionListener(this::addSong);
    btnUpdate.addActionListener(this::updateSong);
    btnDelete.addActionListener(this::deleteSong);
    btnSearch.addActionListener(this::searchSong);
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
    final int ROW = tblSongs.getSelectedRow();
    for (int i = 0; i < txtFields.size(); i++) {
      txtFields.get(i).setText(tblSongs.getValueAt(ROW, i).toString());
    }
  }

  private void addSong(ActionEvent evt) {
    try {
      checkEmptyFields();

      controller.add(getSong());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      showErrorMessage(
        "An error occurred while adding the song.",
        e.getMessage()
      );
    }
  }

  private void updateSong(ActionEvent evt) {
    try {
      checkEmptyFields();

      controller.update(getSong());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      showErrorMessage(
        "An error occurred while updating the song.",
        e.getMessage()
      );
    }
  }

  private void deleteSong(ActionEvent evt) {
    try {
      controller.delete(getId());
      updateTable();
      clearFields(null);
    } catch (Exception e) {
      showErrorMessage(
        "An error occurred while deleting the song.",
        e.getMessage()
      );
    }
  }

  private void searchSong(ActionEvent evt) {
    try {
      final ResultSet song = controller.find(getId());
      song.next();
      for (int i = 0; i < txtFields.size(); i++) {
        txtFields.get(i).setText(song.getString(i + 1));
      }
    } catch (NoSuchElementException e) {
      String auxIdText = getId();
      clearFields(null);
      txtId.setText(auxIdText);
      showErrorMessage("Song not found", null);
    } catch (Exception e) {
      showErrorMessage(
        "An error occurred while searching the song.",
        e.getMessage()
      );
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
        showErrorMessage(
          "An error occurred while filtering the table.",
          e.getMessage()
        );
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

  public void clearFields() {
    txtId.setText("");
    txtName.setText("");
    txtArtist.setText("");
    txtAlbum.setText("");
    txtDescrption.setText("");
    txtRating.setText("");
    txtYear.setText("");
  }

  private void updateTable() {
    DefaultTableModel tableModel = (DefaultTableModel) tblSongs.getModel();
    tableModel.setRowCount(0);
    try {
      if (tglFilter.isSelected()) {
        updateTable(controller.filter("name", getFilter()));
      } else {
        updateTable(controller.all());
      }
    } catch (Exception e) {
      showErrorMessage(
        "An error occurred while updating the table.",
        e.getMessage()
      );
    }
  }

  private void checkEmptyFields() {
    ArrayList<JTextField> txtFields = new ArrayList<>(this.txtFields);
    txtFields.remove(txtDescrption);
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
    JOptionPane.showMessageDialog(
      this,
      msg,
      "Error",
      JOptionPane.ERROR_MESSAGE
    );
  }

  private void updateTable(ResultSet song) {
    DefaultTableModel tableModel = (DefaultTableModel) tblSongs.getModel();
    tableModel.setRowCount(0);
    try {
      while (song.next()) {
        tableModel.addRow(
          new Object[] {
            song.getString(1),
            song.getString(2),
            song.getString(3),
            song.getString(4),
            song.getString(5),
            song.getString(6),
            song.getString(7),
          }
        );
      }
    } catch (Exception e) {
      showErrorMessage(
        "An error occurred while updating the table.",
        e.getMessage()
      );
    }
  }

  private Song getSong() {
    return new Song(
      getId(),
      getName(),
      getArtist(),
      getAlbum(),
      getDescription(),
      getRating(),
      getYear()
    );
  }

  private String getId() {
    return txtId.getText().trim();
  }

  @Override
  public String getName() {
    return txtName.getText().trim();
  }

  private String getArtist() {
    return txtArtist.getText().trim();
  }

  private String getAlbum() {
    return txtAlbum.getText().trim();
  }

  private String getDescription() {
    return txtDescrption.getText().trim();
  }

  private byte getRating() {
    return Byte.parseByte(txtRating.getText());
  }

  private short getYear() {
    return Short.parseShort(txtYear.getText());
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
    lbl4 = new javax.swing.JLabel();
    lbl7 = new javax.swing.JLabel();
    lbl8 = new javax.swing.JLabel();
    lbl5 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    lbl6 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Product Managment");

    lbl1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl1.setText("id");

    txtId.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    btnSearch.setIcon(
      new javax.swing.ImageIcon(getClass().getResource("/resources/search.png"))
    ); // NOI18N
    btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

    lbl2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl2.setText("name");

    txtName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl3.setText("artist");

    txtArtist.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl4.setText("album");

    txtAlbum.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl7.setText("rating");

    txtRating.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl8.setText("year");

    txtYear.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl5.setText("description");

    txtDescrption.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    jPanel1.setLayout(new java.awt.GridLayout(0, 2, 10, 10));

    btnAdd.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    btnAdd.setIcon(
      new javax.swing.ImageIcon(getClass().getResource("/resources/add.png"))
    ); // NOI18N
    btnAdd.setText("Add");
    btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    jPanel1.add(btnAdd);

    btnUpdate.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    btnUpdate.setIcon(
      new javax.swing.ImageIcon(getClass().getResource("/resources/update.png"))
    ); // NOI18N
    btnUpdate.setText("Update");
    btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    jPanel1.add(btnUpdate);

    btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    btnDelete.setIcon(
      new javax.swing.ImageIcon(getClass().getResource("/resources/delete.png"))
    ); // NOI18N
    btnDelete.setText("Delete");
    btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    jPanel1.add(btnDelete);

    btnClearFields.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    btnClearFields.setIcon(
      new javax.swing.ImageIcon(getClass().getResource("/resources/clear.png"))
    ); // NOI18N
    btnClearFields.setText("Clear Fields");
    btnClearFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    btnClearFields.setHorizontalTextPosition(
      javax.swing.SwingConstants.LEADING
    );
    jPanel1.add(btnClearFields);

    javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(
      pnlProps
    );
    pnlProps.setLayout(pnlPropsLayout);
    pnlPropsLayout.setHorizontalGroup(
      pnlPropsLayout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(txtName)
        .addComponent(txtArtist)
        .addComponent(txtAlbum)
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
                .addComponent(
                  txtId,
                  javax.swing.GroupLayout.DEFAULT_SIZE,
                  338,
                  Short.MAX_VALUE
                )
            )
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnSearch)
        )
        .addComponent(txtDescrption)
        .addComponent(txtRating)
        .addComponent(txtYear)
        .addComponent(
          jPanel1,
          javax.swing.GroupLayout.DEFAULT_SIZE,
          375,
          Short.MAX_VALUE
        )
        .addGroup(
          pnlPropsLayout
            .createSequentialGroup()
            .addGroup(
              pnlPropsLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lbl5)
                .addComponent(lbl7)
                .addComponent(lbl8)
            )
            .addContainerGap(
              javax.swing.GroupLayout.DEFAULT_SIZE,
              Short.MAX_VALUE
            )
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
              txtArtist,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtAlbum,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl7)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtRating,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl8)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtYear,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(18, 18, 18)
            .addComponent(lbl5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(
              txtDescrption,
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
            .addContainerGap(
              javax.swing.GroupLayout.DEFAULT_SIZE,
              Short.MAX_VALUE
            )
        )
    );

    jSplitPane1.setLeftComponent(pnlProps);

    tblSongs.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
    tblSongs.setModel(
      new javax.swing.table.DefaultTableModel(
        new Object[][] {},
        new String[] {}
      )
    );
    tblSongs.setShowGrid(true);
    jScrollPane1.setViewportView(tblSongs);

    txtFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

    lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    lbl6.setText("filter by name");

    tglFilter.setIcon(
      new javax.swing.ImageIcon(getClass().getResource("/resources/filter.png"))
    ); // NOI18N
    tglFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
      jPanel2
    );
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
                .addComponent(
                  jScrollPane1,
                  javax.swing.GroupLayout.DEFAULT_SIZE,
                  562,
                  Short.MAX_VALUE
                )
                .addGroup(
                  jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(lbl6)
                    .addPreferredGap(
                      javax.swing.LayoutStyle.ComponentPlacement.RELATED
                    )
                    .addComponent(txtFilter)
                    .addPreferredGap(
                      javax.swing.LayoutStyle.ComponentPlacement.RELATED
                    )
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
                .addComponent(
                  tglFilter,
                  javax.swing.GroupLayout.Alignment.TRAILING
                )
                .addGroup(
                  jPanel2Layout
                    .createParallelGroup(
                      javax.swing.GroupLayout.Alignment.BASELINE
                    )
                    .addComponent(
                      txtFilter,
                      javax.swing.GroupLayout.PREFERRED_SIZE,
                      javax.swing.GroupLayout.DEFAULT_SIZE,
                      javax.swing.GroupLayout.PREFERRED_SIZE
                    )
                    .addComponent(lbl6)
                )
            )
            .addPreferredGap(
              javax.swing.LayoutStyle.ComponentPlacement.UNRELATED
            )
            .addComponent(
              jScrollPane1,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              558,
              Short.MAX_VALUE
            )
            .addContainerGap()
        )
    );

    jSplitPane1.setRightComponent(jPanel2);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
      getContentPane()
    );
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          layout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(jSplitPane1)
            .addContainerGap()
        )
    );
    layout.setVerticalGroup(
      layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          layout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(jSplitPane1)
            .addContainerGap()
        )
    );

    pack();
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
  private javax.swing.JLabel lbl5;
  private javax.swing.JLabel lbl6;
  private javax.swing.JLabel lbl7;
  private javax.swing.JLabel lbl8;
  private javax.swing.JPanel pnlProps;
  private final javax.swing.JTable tblSongs = new javax.swing.JTable();
  private final javax.swing.JToggleButton tglFilter = new javax.swing.JToggleButton();
  private final javax.swing.JTextField txtAlbum = new javax.swing.JTextField();
  private final javax.swing.JTextField txtArtist = new javax.swing.JTextField();
  private final javax.swing.JTextField txtDescrption = new javax.swing.JTextField();
  private final javax.swing.JTextField txtFilter = new javax.swing.JTextField();
  private final javax.swing.JTextField txtId = new javax.swing.JTextField();
  private final javax.swing.JTextField txtName = new javax.swing.JTextField();
  private final javax.swing.JTextField txtRating = new javax.swing.JTextField();
  private final javax.swing.JTextField txtYear = new javax.swing.JTextField();
  // End of variables declaration//GEN-END:variables
}
