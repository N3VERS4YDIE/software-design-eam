package com.n3vers4ydie.foreigncrud;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.n3vers4ydie.foreigncrud.view.LoginView;
import java.awt.EventQueue;

/**
 *
 * @author n3vers4ydie
 */
public class Main {

  public static void main(String[] args) {
    FlatMacLightLaf.setup();
    EventQueue.invokeLater(LoginView::new);
  }
}
