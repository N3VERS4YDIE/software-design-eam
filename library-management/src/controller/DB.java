package controller;

import com.n3vers4ydie.sqlcrud.SQLDB;

public class DB {

  private static SQLDB instance;

  private DB() {}

  public static SQLDB getInstance() {
    if (instance == null) {
      instance = new SQLDB("localhost:3306/library", "root", "");
    }
    return instance;
  }
}
