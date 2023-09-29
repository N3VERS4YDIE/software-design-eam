package model;

import com.n3vers4ydie.autosqlcrud.SQLModel;

public class Book extends SQLModel {

    private String title;
    private String author;
    private String year;
    private int quantity;
    private int genre;

    public Book(String id, String title, String author, String year, int quantity, int genre) {
        super(id);
        setTitle(title);
        setAuthor(author);
        setYear(year);
        setQuantity(quantity);
        setGenre(genre);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        values.put("title", title);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
        values.put("author", author);
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
        values.put("year", year);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        values.put("quantity", quantity);
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
        values.put("genre_id", genre);
    }
}
