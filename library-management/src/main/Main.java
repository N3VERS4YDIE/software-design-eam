package main;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import controller.UserController;
import java.awt.EventQueue;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import model.Role;
import view.LoginView;

/**
 *
 * @author Santiago Palacio Vásquez
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    FlatMacDarkLaf.setup();
    EventQueue.invokeLater(LoginView::new);

    try {
      addAdmin();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  private static void addAdmin() throws SQLException {
    try {
      UserController.getInstance().find("admin");
    } catch (NoSuchElementException e) {
      UserController
        .getInstance()
        .register("admin", "admin", "", "", Role.ADMIN);
    }
  }
}
