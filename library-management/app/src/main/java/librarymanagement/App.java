package librarymanagement;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import dao.UserDAO;
import java.awt.EventQueue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import model.Role;
import model.User;
import view.LoginView;

/**
 *
 * @author Santiago Palacio Vásquez
 */
public class App {

    public static void main(String[] args) throws SQLException {
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(LoginView::new);

        try {
            App app = new App();
            app.setAdmin();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // String salt = BCrypt.gensalt();
        // String password = PasswordController.hashPassword("san", salt);
        // new UserDAO().add(new User("san", password, "", "", Role.ADMIN, salt));

        // ResultSet rs = new UserDAO().allRS();
        // ResultSetMetaData rsmd = rs.getMetaData();
        // int columnsNumber = rsmd.getColumnCount();
        // while (rs.next()) {
        //     for (int i = 1; i <= columnsNumber; i++) {
        //         if (i > 1) System.out.print(" | ");
        //         System.out.print(rs.getString(i));
        //     }
        //     System.out.println("");
        // }
    }

    private void setAdmin() throws SQLException {
        try {
            final ResultSet rs = new UserDAO().findRS("admin");
            if (!rs.next()) {
                new UserDAO().add(new User("admin", "admin", "", "", Role.ADMIN, null));
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
