package com.n3vers4ydie.foreigncrud.connection;

import com.n3vers4ydie.autosqlcrud.JDBCURL;
import com.n3vers4ydie.autosqlcrud.SQLDB;

public class DB {

  private static SQLDB instance;

  private DB() {}

  public static SQLDB getInstance() {
    if (instance == null) {
      throw new RuntimeException("DB User must login first");
    }
    return instance;
  }

  public static void login(String username, String password) {
    instance = new SQLDB(JDBCURL.MYSQL, "localhost:3306/foreign_crud", username, password);
  }
}
