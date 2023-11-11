package dao;

import controller.UserController;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Borrow;
import model.TransactionType;
import util.FieldChecker;

public class BorrowDAO extends DAO<Borrow> {

    private static BorrowDAO instance;

    private final String SELECT_QUERY;

    private String tablename = "borrows";

    private BorrowDAO() {
        super("borrows");
        SELECT_QUERY =
            String.format(
                "SELECT T.id, T.startdate, T.enddate, B.id, B.title, B.author, B.year, B.quantity, G.name FROM %s T INNER JOIN %s B ON T.book_id = B.id  INNER JOIN %s G ON B.genre_id = G.id WHERE T.user_id = '%s' ",
                tablename,
                new BookDAO().tableName,
                new BookGenreDAO().tableName,
                UserController.getInstance().getLoggedUserId()
            );
    }

    public static BorrowDAO getInstance() {
        if (instance == null) {
            instance = new BorrowDAO();
        }
        return instance;
    }

    @Override
    public ResultSet allRS() throws SQLException {
        return db.prepareStatement(SELECT_QUERY).executeQuery();
    }

    @Override
    public ResultSet filterRS(String sequence) throws SQLException {
        return db
            .prepareStatement(
                SELECT_QUERY +
                String.format(
                    "AND CONCAT(T.startdate, T.enddate, B.id, B.title, B.author, B.year, B.quantity, G.name) LIKE '%%%s%%'",
                    sequence
                )
            )
            .executeQuery();
    }

    public int borrowBook(Borrow transaction) throws SQLException {
        FieldChecker.checkExistence(
            db
                .prepareStatement(
                    String.format(
                        "SELECT id FROM %s WHERE user_id = '%s' AND book_id = '%s'",
                        tablename,
                        transaction.getUserId(),
                        transaction.getBookId()
                    )
                )
                .executeQuery(),
            "Already borrowed"
        );

        Connection conn = db.getConnection();
        conn.setAutoCommit(false);
        int updates = db
            .prepareStatement(
                String.format(
                    "UPDATE %s SET quantity = quantity - 1 WHERE id = '%s'",
                    new BookDAO().tableName,
                    transaction.getBookId()
                )
            )
            .executeUpdate();
        addSecretly(transaction);
        conn.commit();
        conn.setAutoCommit(true);
        addTransaction(TransactionType.BORROW, new BookDAO().tableName, transaction.getBookId());
        return updates;
    }

    public int returnBook(Borrow transaction) throws SQLException {
        Connection conn = db.getConnection();
        conn.setAutoCommit(false);
        int updates = db
            .prepareStatement(
                String.format(
                    "UPDATE %s SET quantity = quantity + 1 WHERE id = '%s'",
                    new BookDAO().tableName,
                    transaction.getBookId()
                )
            )
            .executeUpdate();

        deleteSecretly(transaction.getId());
        conn.commit();
        conn.setAutoCommit(true);
        addTransaction(TransactionType.RETURN, new BookDAO().tableName, transaction.getBookId());
        return updates;
    }
}
