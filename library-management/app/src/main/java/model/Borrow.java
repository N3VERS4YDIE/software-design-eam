package model;

import com.n3vers4ydie.autosqlcrud.SQLModel;
import java.time.LocalDate;

public class Borrow extends SQLModel {

    // db object attributes
    private LocalDate startDate;
    private LocalDate endDate;
    private String userId;
    private String bookId;

    // foreign attributes
    private String bookName;

    public Borrow(String id, LocalDate startDate, LocalDate endDate, String userId, String bookId) {
        super(id);
        setStartDate(startDate);
        setEndDate(endDate);
        setUserId(userId);
        setBookId(bookId);
    }

    public Borrow(LocalDate startDate, LocalDate endDate, String userId, String bookId) {
        super("0");
        setStartDate(startDate);
        setEndDate(endDate);
        setUserId(userId);
        setBookId(bookId);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        values.put("startdate", startDate);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        values.put("enddate", endDate);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        values.put("user_id", userId);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
        values.put("book_id", bookId);
    }

    // foreign getters
    public String getBookName() {
        return bookName;
    }
}
