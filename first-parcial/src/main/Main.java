package main;

import java.awt.EventQueue;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class Main {

  public static void main(String[] args) {
    FlatMacDarkLaf.setup();
    EventQueue.invokeLater(View::new);
  }
}
