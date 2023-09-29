package controller;

import dao.UserDAO;
import util.PasswordHasher;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class UserController {

    private static UserController instance;
    private String loggedUserId;

    UserDAO userDAO;

    private UserController() {
        userDAO = new UserDAO();
    }

    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    public void login(String id, String password) {
        ResultSet rs;
        try {
            rs = userDAO.findRS(id);
            rs.next();
            String dbUserPassword = rs.getString("password");
            String salt = rs.getString("salt");
            if (!PasswordHasher.verifyPassword(password, dbUserPassword, salt)) {
                throw new IllegalArgumentException("Invalid credentials");
            }
            loggedUserId = id;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Email not found");
        }
    }

    public String getSalt(String id) {
        try {
            ResultSet rs = userDAO.findRS(id);
            if (rs.next()) {
                return rs.getString("salt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BCrypt.gensalt();
    }

    public String getLoggedUserId() {
        return loggedUserId;
    }
}
