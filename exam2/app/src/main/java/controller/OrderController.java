package controller;

import com.n3vers4ydie.autosqlcrud.SQLDB;
import connection.DB;
import dao.ClientDAO;
import dao.OrderDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderController {

    private final String selectQuery = String.format(
        "SELECT O.id, O.date, O.total, C.id, C.name FROM %s C INNER JOIN %s O ON C.id = O.client_id ",
        ClientDAO.getInstance().tableName,
        OrderDAO.getInstance().tableName
    );
    private static OrderController instance;
    private SQLDB db;

    private OrderController() {
        db = DB.getInstance();
    }

    public static OrderController getInstance() {
        if (instance == null) {
            instance = new OrderController();
        }
        return instance;
    }

    public ResultSet all() throws SQLException {
        return db.prepareStatement(selectQuery).executeQuery();
    }

    public ResultSet find(String id) throws SQLException {
        return db.prepareStatement(selectQuery + " WHERE O.id = " + id).executeQuery();
    }

    public ResultSet filter(String sequence) throws SQLException {
        return db
            .prepareStatement(selectQuery + "WHERE CONCAT(O.date, O.total, C.name) LIKE '%" + sequence + "%'")
            .executeQuery();
    }
}
