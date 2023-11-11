package util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FieldChecker {

    private FieldChecker() {}

    public static void checkEmail(String email) {
        if (!email.toLowerCase().matches("^[a-z0-9.]+@[a-z0-9.]+.[a-z]+$")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public static void checkName(String name) {
        if (!name.toLowerCase().matches("^[a-z]+$")) {
            throw new IllegalArgumentException("Invalid name");
        }
    }

    public static void checkExistence(ResultSet rs) throws SQLException {
        if (rs.next()) {
            throw new IllegalArgumentException("Already exists");
        }
    }

        public static void checkExistence(ResultSet rs, String msg) throws SQLException {
        if (rs.next()) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void checkNonExistence(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            throw new IllegalArgumentException("Does not exist");
        }
    }
}
