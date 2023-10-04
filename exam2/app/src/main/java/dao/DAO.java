package dao;

import com.n3vers4ydie.autosqlcrud.SQLDAO;
import com.n3vers4ydie.autosqlcrud.SQLModel;
import connection.DB;

public class DAO<T extends SQLModel> extends SQLDAO<T> {

    public DAO(String tablename) {
        super(DB.getInstance(), tablename);
    }
}
