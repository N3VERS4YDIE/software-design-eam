package dao;

import model.Book;

public class BookDAO extends DAO<Book> {

    public BookDAO() {
        super("books");
    }
}
