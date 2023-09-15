package com.n3vers4ydie.foreigncrud.model;

import com.n3vers4ydie.autosqlcrud.SQLModel;

public class Category extends SQLModel {

  private String name;

  public Category(String id, String name) {
    super(id);
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
