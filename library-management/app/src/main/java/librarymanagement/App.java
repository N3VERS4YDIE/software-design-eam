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

    public static void main(String[] args) {
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(LoginView::new);
        setAdmin();
    }

    private static void setAdmin() {
        try {
            final ResultSet rs = new UserDAO().findRS("admin");
            if (!rs.next()) {
                new UserDAO().add(new User("admin", "admin", "", "", Role.ADMIN, null));
            }
        } catch (NoSuchElementException | SQLException e) {
            e.printStackTrace();
        }
    }
}
