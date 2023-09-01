package main;

import com.n3vers4ydie.sqlcrud.SQLModel;

public class Song extends SQLModel {

  private String name;
  private String artist;
  private String album;
  private String description;
  private byte rating;
  private short year;

  public Song(
    String id,
    String name,
    String artist,
    String album,
    String description,
    byte rating,
    short year
  ) {
    super(id);
    this.name = name;
    this.artist = artist;
    this.album = album;
    this.description = description;
    this.rating = rating;
    this.year = year;
  }

  @Override
  public void setValues() {
    values.put("id", id);
    values.put("name", name);
    values.put("artist", artist);
    values.put("album", album);
    values.put("description", description);
    values.put("rating", rating);
    values.put("year", year);
  }
}
