package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import model.Role;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserController extends Controller<User> {

  private static UserController instance;

  public UserController() {
    super("user");
  }

  public static UserController getInstance() {
    if (instance == null) {
      instance = new UserController();
    }
    return instance;
  }

  public void login(String id, String password)
    throws NoSuchElementException, SQLException, IllegalArgumentException {
    final User user = toUser(find(id));
    System.out.println(PasswordController.hashPassword(password, user.salt));
    System.out.println(user.password);
    PasswordController.verifyPassword(password, user.password, user.salt);
  }

  public void register(
    String id,
    String password,
    String firstname,
    String lastname,
    Role role
  ) throws SQLException {
    final String SALT = BCrypt.gensalt();
    final String HASHED_PASSWORD = PasswordController.hashPassword(
      password,
      SALT
    );
    final User user = new User(
      id,
      HASHED_PASSWORD,
      SALT,
      firstname,
      lastname,
      role
    );
    add(user);
  }

  public String getSalt(String id) {
    try {
      final ResultSet rs = find(id);
      if (rs.next()) {
        return rs.getString("salt");
      }
    } catch (SQLException e) {}
    return BCrypt.gensalt();
  }

  private User toUser(ResultSet rs) throws SQLException {
    return new User(
      rs.getString("id"),
      rs.getString("password"),
      rs.getString("salt"),
      rs.getString("firstname"),
      rs.getString("lastname"),
      Role.valueOf(rs.getString("role"))
    );
  }
}
