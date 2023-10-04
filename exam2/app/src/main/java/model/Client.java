package model;

import com.n3vers4ydie.autosqlcrud.SQLModel;

public class Client extends SQLModel {

  private String name;
  private String email;

  public Client(String id, String name, String email) {
    super(id);
    setName(name);
    setEmail(email);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    values.put("name", name);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
    values.put("email", email);
  }
}
