package view;

import controller.ReportController;
import dao.BookDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JCheckBox;
import util.Message;

public class ReportsPanel extends javax.swing.JPanel {

    private JCheckBox[] checkBoxes;
    private JCheckBox[] specialCheckBoxes;

    public ReportsPanel() {
        initComponents();
        checkBoxes = new JCheckBox[] { chkUsers, chkBooks, chkTransactions, chkBorrows, chkReturns };
        specialCheckBoxes = new JCheckBox[] { chkBorrows, chkReturns };

        initListeners();
        initCmbDate();
        initCmbUsers();

        chkAll.setSelected(true);
        updateCheckBoxes();
    }

    private void initListeners() {
        chkAll.addActionListener(e -> updateCheckBoxes());
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.addActionListener(this::updateChkAll);
        }
        for (JCheckBox specialCheckBox : specialCheckBoxes) {
            specialCheckBox.addChangeListener(e -> updatePnlFilters());
        }
        btnGenerate.addActionListener(e -> generateHTML());
    }

    private void updateCheckBoxes() {
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setSelected(chkAll.isSelected());
        }
    }

    private void updateChkAll(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        if (!source.isSelected()) {
            chkAll.setSelected(false);
        }
    }

    private void updatePnlFilters() {
        boolean isVisible = false;
        for (JCheckBox checkBox : specialCheckBoxes) {
            if (checkBox.isSelected()) {
                isVisible = true;
                break;
            }
        }
        pnlFilters.setVisible(isVisible);
    }

    private void initCmbDate() {
        cmbDateFrom.removeAllItems();
        LocalDate currentDate = LocalDate.now();
        int max = 30;
        for (int i = 0; i <= max; i++) {
            cmbDateFrom.addItem(currentDate.minusDays(i));
            cmbDateTo.addItem(currentDate.minusDays(i));
        }
        cmbDateFrom.setSelectedIndex(max);
    }

    private void initCmbUsers() {
        try {
            ResultSet rs = new UserDAO().allRS();
            cmbUsers.removeAllItems();
            cmbUsers.addItem("all users");
            while (rs.next()) {
                cmbUsers.addItem(rs.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateHTML() {
        try {
            String additionalQuery = String.format(
                "WHERE %s datetime BETWEEN '%s' AND '%s' ",
                cmbUsers.getSelectedIndex() > 0 ? String.format("user_id = '%s' AND", cmbUsers.getSelectedItem()) : "",
                ((LocalDate) cmbDateFrom.getSelectedItem()).minusDays(1),
                ((LocalDate) cmbDateTo.getSelectedItem()).plusDays(1)
            );
            HashMap<String, Integer> rows = new HashMap<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String name = checkBox.getAccessibleContext().getAccessibleName();
                    int rowCount = 0;
                    switch (name) {
                        case "users":
                            rowCount += ReportController.generateReport(new UserDAO(), name, null);
                            break;
                        case "books":
                            rowCount += ReportController.generateReport(new BookDAO(), name, null);
                            break;
                        case "transactions":
                            rowCount +=
                                ReportController.generateReport(TransactionDAO.getInstance(), name, additionalQuery);
                            break;
                        case "borrows":
                            rowCount +=
                                ReportController.generateReport(
                                    TransactionDAO.getInstance(),
                                    name,
                                    additionalQuery + "AND type = 'BORROW'"
                                );
                            break;
                        case "returns":
                            rowCount +=
                                ReportController.generateReport(
                                    TransactionDAO.getInstance(),
                                    name,
                                    additionalQuery + "AND type = 'RETURN'"
                                );
                            break;
                    }
                    rows.put(name, rowCount);
                }
            }

            boolean notSelected = true;
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    notSelected = false;
                    break;
                }
            }
            if (notSelected) {
                Message.showWarningMessage(this, "Select at least one report");
            }

            LinkedList<String> badReports = new LinkedList<>();
            for (Map.Entry<String, Integer> entry : rows.entrySet()) {
                if (entry.getValue() == 0) {
                    badReports.add("- " + entry.getKey());
                }
            }
            if (!badReports.isEmpty()) {
                Message.showWarningMessage(
                    this,
                    "The following reports have no data:\n" + String.join("\n", badReports)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlProps = new javax.swing.JPanel();
        lbl8 = new javax.swing.JLabel();
        chkAll = new javax.swing.JCheckBox();
        chkUsers = new javax.swing.JCheckBox();
        chkBooks = new javax.swing.JCheckBox();
        chkTransactions = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        lbl9 = new javax.swing.JLabel();
        chkBorrows = new javax.swing.JCheckBox();
        chkReturns = new javax.swing.JCheckBox();
        pnlFilters = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lbl5 = new javax.swing.JLabel();
        lbl6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cmbDateFrom = new javax.swing.JComboBox<>();
        cmbDateTo = new javax.swing.JComboBox<>();
        lbl4 = new javax.swing.JLabel();
        cmbUsers = new javax.swing.JComboBox<>();

        setLayout(new java.awt.GridBagLayout());

        pnlProps.setPreferredSize(new java.awt.Dimension(800, 478));

        lbl8.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl8.setText("general reports");

        chkAll.setText("all");

        chkUsers.setText("users");

        chkBooks.setText("books");

        chkTransactions.setText("transactions");

        lbl9.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl9.setText("special reports");

        chkBorrows.setText("borrows");

        chkReturns.setText("returns");

        pnlFilters.setBorder(javax.swing.BorderFactory.createTitledBorder("filters"));

        jPanel1.setLayout(new java.awt.GridLayout(1, 2, 20, 0));

        lbl5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl5.setText("from");
        jPanel1.add(lbl5);

        lbl6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl6.setText("to");
        jPanel1.add(lbl6);

        jPanel2.setLayout(new java.awt.GridLayout(1, 2, 20, 0));

        cmbDateFrom.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jPanel2.add(cmbDateFrom);

        cmbDateTo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jPanel2.add(cmbDateTo);

        lbl4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbl4.setText("user");

        cmbUsers.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        javax.swing.GroupLayout pnlFiltersLayout = new javax.swing.GroupLayout(pnlFilters);
        pnlFilters.setLayout(pnlFiltersLayout);
        pnlFiltersLayout.setHorizontalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addComponent(lbl4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cmbUsers, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlFiltersLayout.setVerticalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnGenerate.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnGenerate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/report.png"))); // NOI18N
        btnGenerate.setText("Generate");
        btnGenerate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGenerate.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout pnlPropsLayout = new javax.swing.GroupLayout(pnlProps);
        pnlProps.setLayout(pnlPropsLayout);
        pnlPropsLayout.setHorizontalGroup(
            pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGenerate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlPropsLayout.createSequentialGroup()
                        .addGroup(pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkBorrows)
                            .addComponent(chkReturns)
                            .addComponent(chkAll)
                            .addComponent(chkUsers)
                            .addComponent(chkBooks)
                            .addComponent(chkTransactions))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lbl9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPropsLayout.setVerticalGroup(
            pnlPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPropsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUsers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBooks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTransactions)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBorrows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkReturns)
                .addGap(18, 18, 18)
                .addComponent(pnlFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(pnlProps, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton btnGenerate = new javax.swing.JButton();
    private javax.swing.JCheckBox chkAll;
    private javax.swing.JCheckBox chkBooks;
    private javax.swing.JCheckBox chkBorrows;
    private javax.swing.JCheckBox chkReturns;
    private javax.swing.JCheckBox chkTransactions;
    private javax.swing.JCheckBox chkUsers;
    private javax.swing.JComboBox<LocalDate> cmbDateFrom;
    private javax.swing.JComboBox<LocalDate> cmbDateTo;
    private javax.swing.JComboBox<String> cmbUsers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lbl9;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlProps;
    // End of variables declaration//GEN-END:variables

}
