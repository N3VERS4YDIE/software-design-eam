package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private PasswordHasher() {}

    public static String hashPassword(String password, String salt) {
        return BCrypt.hashpw(String.valueOf(password), salt);
    }

    public static boolean verifyPassword(String password, String hashedPassword, String salt)
        throws IllegalArgumentException {
        return hashPassword(password, salt).equals(hashedPassword);
    }
}
