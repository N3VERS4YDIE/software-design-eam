package model;

import com.n3vers4ydie.sqlcrud.SQLModel;

public class User extends SQLModel {

  public String password;
  public String salt;
  public String firstname;
  public String lastname;
  public Role role;

  public User(
    String id,
    String password,
    String salt,
    String firstname,
    String lastname,
    Role role
  ) {
    super(id);
    this.password = password;
    this.salt = salt;
    this.firstname = firstname;
    this.lastname = lastname;
    this.role = role;
  }

  @Override
  public void setValues() {
    values.put("password", password);
    values.put("salt", salt);
    values.put("firstname", firstname);
    values.put("lastname", lastname);
    values.put("role", role);
  }
}
