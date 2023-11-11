package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Book;

public class BookDAO extends DAO<Book> {

    private final String SELECT_QUERY;

    public BookDAO() {
        super("books");
        SELECT_QUERY =
            String.format(
                "SELECT B.id, B.title, B.author, B.year, B.quantity, G.name FROM %s B INNER JOIN %s G ON B.genre_id = G.id ",
                tableName,
                new BookGenreDAO().tableName
            );
    }

    @Override
    public ResultSet allRS() throws SQLException {
        return this.db.prepareStatement(SELECT_QUERY).executeQuery();
    }

    @Override
    public ResultSet filterRS(String condition) throws SQLException {
        final String QUERY = String.format("%s WHERE %s;", SELECT_QUERY, condition);
        return this.db.prepareStatement(QUERY).executeQuery();
    }
}
