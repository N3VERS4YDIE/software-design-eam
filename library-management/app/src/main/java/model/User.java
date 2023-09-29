package model;

import com.n3vers4ydie.autosqlcrud.SQLModel;

public class User extends SQLModel {

    private String password;
    private String firstname;
    private String lastname;
    private Role role;
    private String salt;

    public User(String id, String password, String firstname, String lastname, Role role, String salt) {
        super(id);
        setPassword(password);
        setFirstname(firstname);
        setLastname(lastname);
        setRole(role);
        setSalt(salt);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        values.put("password", password);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
        values.put("salt", salt);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        values.put("firstname", firstname);
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        values.put("lastname", lastname);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        values.put("role", role.toString());
    }
}
