package dao;

import model.BookGenre;

public class BookGenreDAO extends DAO<BookGenre> {

    public BookGenreDAO() {
        super("genres");
    }
}
