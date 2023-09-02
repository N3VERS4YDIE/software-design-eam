package controller;

import com.n3vers4ydie.sqlcrud.SQLController;
import com.n3vers4ydie.sqlcrud.SQLModel;

public class Controller<T extends SQLModel> extends SQLController<T> {

  public Controller(String tablename) {
    super(DB.getInstance(), tablename);
  }
}
