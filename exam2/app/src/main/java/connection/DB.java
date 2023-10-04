package connection;

import com.n3vers4ydie.autosqlcrud.JDBCURL;
import com.n3vers4ydie.autosqlcrud.SQLDB;

public class DB {

    private static SQLDB instance;

    private DB() {}

    public static SQLDB getInstance() {
        if (instance == null) {
            instance = new SQLDB(JDBCURL.MARIADB, "localhost:3306/clients_orders", "root", "4152");
        }
        return instance;
    }
}
