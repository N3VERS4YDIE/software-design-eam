package util;

import java.awt.Component;
import javax.swing.JOptionPane;

public class Message {

  private Message() {}

  public static void showErrorMessage(Component parent, String msg) {
    JOptionPane.showMessageDialog(
      parent,
      msg,
      "Error",
      JOptionPane.ERROR_MESSAGE
    );
  }

  public static void showWarningMessage(Component parent, String msg) {
    JOptionPane.showMessageDialog(
      parent,
      msg,
      "Warning",
      JOptionPane.WARNING_MESSAGE
    );
  }
}
