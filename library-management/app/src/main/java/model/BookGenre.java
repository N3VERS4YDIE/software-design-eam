package model;

import com.n3vers4ydie.autosqlcrud.SQLModel;

public class BookGenre extends SQLModel {
    private String name;

    public BookGenre(String id, String name) {
        super(id);
        setName(name);
    }

    public BookGenre(String name) {
        super("0");
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        values.put("name", name);
    }
}
