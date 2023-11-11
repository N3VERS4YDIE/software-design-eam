package dao;

import com.n3vers4ydie.autosqlcrud.SQLDAO;
import com.n3vers4ydie.autosqlcrud.SQLModel;
import connection.DB;
import controller.UserController;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Transaction;
import model.TransactionType;

public class DAO<T extends SQLModel> extends SQLDAO<T> {

    public DAO(String tablename) {
        super(DB.getInstance(), tablename);
    }

    public boolean addSecretly(T t) throws SQLException {
        return super.add(t);
    }

    public boolean updateSecretly(T t) throws SQLException {
        return super.updateRS(t);
    }

    public boolean deleteSecretly(String id) throws SQLException {
        return super.delete(id);
    }

    @Override
    public boolean add(T t) throws SQLException {
        addTransaction(TransactionType.ADD, t.getId());
        return super.add(t);
    }

    @Override
    public boolean updateRS(T t) throws SQLException {
        addTransaction(TransactionType.UPDATE, t.getId());
        return super.updateRS(t);
    }

    @Override
    public boolean delete(String id) throws SQLException {
        addTransaction(TransactionType.DELETE, id);
        return super.delete(id);
    }

    @Override
    public ResultSet filterRS(String columns, String sequence) throws SQLException {
        return super.filterRS("id, " + columns, sequence);
    }

    protected void addTransaction(TransactionType type, String tableName, String otherId) throws SQLException {
        TransactionDAO
            .getInstance()
            .addSecretly(new Transaction(type, UserController.getInstance().getLoggedUserId(), tableName, otherId));
    }

    protected void addTransaction(TransactionType type, String otherId) throws SQLException {
        addTransaction(type, tableName, otherId);
    }
}
