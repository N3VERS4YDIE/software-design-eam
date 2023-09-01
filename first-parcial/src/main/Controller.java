package main;

import com.n3vers4ydie.sqlcrud.SQLController;
import com.n3vers4ydie.sqlcrud.SQLDB;

public class Controller extends SQLController<Song> {

  public Controller() {
    super(new SQLDB("localhost:3306/songs", "root", ""), "song");
  }
}
