package controller;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordController {

  private PasswordController() {}

  public static String hashPassword(String password, String salt) {
    return BCrypt.hashpw(String.valueOf(password), salt);
  }

  public static void verifyPassword(String password, String hash, String salt)
    throws IllegalArgumentException {
    if (!hashPassword(password, salt).equals(hash)) {
      throw new IllegalArgumentException("Invalid credentials.");
    }
  }
}
